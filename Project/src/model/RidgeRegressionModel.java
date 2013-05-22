package model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Jama.Matrix;
import data.Dataset;
import data.Label;
import data.Sample;

/**
 * Predicts by using ridge regression
 * objective = minimize (SSE + lambda * (weights^2))
 * Where the weights are the w's below:
 * 
 * y_pred = w0 + w1 * average_restaurant_rating + w2 * user_bias + w3 * restaurant_bias
 * @author sjonany
 */
public class RidgeRegressionModel implements LearningModel{
	//ridge regression penalty term
	public enum ParameterKeys{RIDGE_PENALTY};
	
	//Weight indexes
	private static class Weights{
		private static final int NUM_WEIGHTS = 4;
		private static final int INTERCEPT_INDEX = 0;
		private static final int GLOBAL_AVG_INDEX = 1;
		private static final int USER_BIAS_INDEX = 2;
		private static final int REST_BIAS_INDEX = 3;
	}
	
	//fixed parameter
	//for ridge regression
	private double lambda;
	
	//model
	private double[] weights;
	
	private double globalAverageRating;
	private double globalUserAverageRating;
	private double globalRestaurantAverageRating;
	
	//key = user id, value = his average rating
	private Map<String, Double> userAvgRating;
	//key = restaurant id, value = restaurant average rating
	private Map<String, Double> restaurantAvgRating;
	
	public RidgeRegressionModel(Parameters p){
		this.lambda = Double.parseDouble(p.getParam(ParameterKeys.RIDGE_PENALTY.name()));
		reset();
	}
	
	@Override 
	public void reset(){
		this.userAvgRating = new HashMap<String, Double>();
		this.restaurantAvgRating = new HashMap<String, Double>();
		this.weights = new double[Weights.NUM_WEIGHTS];
	}
	
	@Override
	public void train(Dataset data) {
		data.resetIterator();
		double totalRating = 0.0;
		int countRating = 0;

		////////////////////////////////////////////////////
		//Convert data into useful statistics
		
		//key = user id, value = total rating of the user
		Map<String, Double> totalUserRatings = new HashMap<String, Double>();

		//key = user id, value = number of ratings the user made
		Map<String, Double> countUserRatings = new HashMap<String, Double>();
		
		//key = user id, value = total rating of the restaurant
		Map<String, Double> totalRestaurantRatings = new HashMap<String, Double>();

		//key = user id, value = number of ratings for the restaurant
		Map<String, Double> countRestaurantRatings = new HashMap<String, Double>();
		
		while(data.hasNext()){
			Sample s = data.next();
			String userId = s.getFeatureValues().getUserId();
			String restId = s.getFeatureValues().getRestaurantId();
			double rating = s.getLabel().getRating();
			
			addToMap(totalUserRatings, userId, rating);
			addToMap(totalRestaurantRatings, restId, rating);
			addToMap(countUserRatings, userId, 1.0);
			addToMap(countRestaurantRatings, restId, 1.0);
			
			totalRating += rating;
			countRating++;
		}
		
		this.globalAverageRating = 1.0 * totalRating/countRating;
		for(String userId : totalUserRatings.keySet()){
			double userAvg = totalUserRatings.get(userId)/countUserRatings.get(userId);
			this.userAvgRating.put(userId, userAvg);
			this.globalUserAverageRating += userAvg;
		}
		this.globalUserAverageRating /= totalUserRatings.keySet().size();
		
		for(String restId: totalRestaurantRatings.keySet()){
			double restAvg = totalRestaurantRatings.get(restId)/countRestaurantRatings.get(restId);
			this.restaurantAvgRating.put(restId, restAvg);
			this.globalRestaurantAverageRating += restAvg;
		}
		this.globalRestaurantAverageRating /= totalRestaurantRatings.keySet().size();
		
		//3cols. first col =1's, second = user bias, third = restaurant bias
		double[][] inputVals = new double[countRating][Weights.NUM_WEIGHTS];
		double[][] outputVals = new double[countRating][1];
		data.resetIterator();
		for(int row = 0; row < countRating; row++){
			Sample s = data.next();
			String userId = s.getFeatureValues().getUserId();
			String restId = s.getFeatureValues().getRestaurantId();
			double rating = s.getLabel().getRating();

			
			inputVals[row][Weights.INTERCEPT_INDEX] = 1;
			inputVals[row][Weights.GLOBAL_AVG_INDEX] = globalAverageRating;
			inputVals[row][Weights.USER_BIAS_INDEX] = this.userAvgRating.get(userId) - 
					globalUserAverageRating;
			inputVals[row][Weights.REST_BIAS_INDEX] = this.restaurantAvgRating.get(restId) - 
					globalRestaurantAverageRating;
					
			outputVals[row][0] = rating;
		}

		Matrix H = new Matrix(inputVals);
		Matrix T = new Matrix(outputVals);
		this.weights = calcWRidge(H, T, this.lambda);
	}
	
	@Override
	/**
	 * @return a list of predicted labels in the same order data is provided
	 */
	public List<Label> test(Dataset data) {
		List<Label> predictions = new ArrayList<Label>();
		data.resetIterator();
		while(data.hasNext()){
			Sample sample = data.next();
			Double userAvgRating = this.userAvgRating.get(sample.getFeatureValues().getUserId());
			double userBias = userAvgRating == null ? 0 : userAvgRating - globalUserAverageRating;
			Double restAvgRating = this.restaurantAvgRating.get(sample.getFeatureValues().getRestaurantId());
			double restBias = restAvgRating == null ? 0 : restAvgRating - globalRestaurantAverageRating;
			
			double predictedLabel = 
					this.weights[Weights.INTERCEPT_INDEX] + 
					this.weights[Weights.GLOBAL_AVG_INDEX] * globalAverageRating +
					this.weights[Weights.USER_BIAS_INDEX] * userBias + 
					this.weights[Weights.REST_BIAS_INDEX] * restBias;
			predictions.add(new Label(predictedLabel));
		}
		
		return predictions;
	}

	/////////////////////////////////////////
	//optimize ridge regression using formula
	//w_ridge = (HtH + lambda*(I_o+k))^-1 + Ht t
	public static double[] calcWRidge(Matrix H, Matrix T, double lambda){
		if(lambda == 0.0){
			//singular matrices are non-invertible, need a small shift to make determinant non-zero
			lambda = 0.00001;
		}
		Matrix H_trans = H.transpose();
		Matrix I = Matrix.identity(H.getColumnDimension(), H.getColumnDimension());
		I.set(0, 0, 0);
		
		Matrix wRidgeMat = H_trans.times(H).plus(I.times(lambda)).inverse().times(H_trans.times(T));
		return wRidgeMat.transpose().getArray()[0];
	}
	
	public static <K> void addToMap(Map<K,Double> map, K key, Double additionVal){
		Double val = map.get(key);
		if(val == null){
			map.put(key, Double.valueOf(0));
			val = Double.valueOf(0);
		}
		map.put(key, val + additionVal);
	}
}
