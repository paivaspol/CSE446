package learning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.Dataset;
import data.Dataset.Label;
import data.Dataset.Sample;

/**
 * Predicts by using linear regression, 
 * y_pred = average_restaurant_rating + user_bias + restaurant_bias 
 * @author sjonany
 */
public class LinearRegressor implements LearningModel{
	public enum ParameterKeys{USER_BIAS_WEIGHT, RESTAURANT_BIAS_WEIGHT};
	
	//params
	private double userBiasWeight;
	private double restBiasWeight;
	
	//model
	private double globalAverageRating;
	private double globalUserAverageRating;
	private double globalRestaurantAverageRating;
	
	//key = user id, value = his average rating
	private Map<Integer, Double> userAvgRating;
	//key = restaurant id, value = restaurant average rating
	private Map<Integer, Double> restaurantAvgRating;
	
	public LinearRegressor(Parameters p){
		this.userBiasWeight = p.getParam(ParameterKeys.USER_BIAS_WEIGHT.name());
		this.restBiasWeight = p.getParam(ParameterKeys.RESTAURANT_BIAS_WEIGHT.name());
		
		this.userAvgRating = new HashMap<Integer, Double>();
		this.restaurantAvgRating = new HashMap<Integer, Double>();
	}
	
	@Override
	public void train(Dataset data) {
		double totalRating = 0.0;
		int countRating = 0;
		
		//key = user id, value = total rating of the user
		Map<Integer, Double> totalUserRatings = new HashMap<Integer, Double>();

		//key = user id, value = number of ratings the user made
		Map<Integer, Double> countUserRatings = new HashMap<Integer, Double>();
		
		//key = user id, value = total rating of the restaurant
		Map<Integer, Double> totalRestaurantRatings = new HashMap<Integer, Double>();

		//key = user id, value = number of ratings for the restaurant
		Map<Integer, Double> countRestaurantRatings = new HashMap<Integer, Double>();
		
		for(Sample s: data.getSamples()){
			int userId = s.getFeatureValues().getUserId();
			int restId = s.getFeatureValues().getRestaurantId();
			double rating = s.getLabel().getRating();
			
			addToMap(totalUserRatings, userId, rating);
			addToMap(totalRestaurantRatings, restId, rating);
			addToMap(countUserRatings, userId, 1.0);
			addToMap(countRestaurantRatings, restId, 1.0);
			
			totalRating += s.getLabel().getRating();
			countRating++;
		}
		
		this.globalAverageRating = 1.0 * totalRating/countRating;
		for(Integer userId : totalUserRatings.keySet()){
			double userAvg = totalUserRatings.get(userId)/countUserRatings.get(userId);
			this.userAvgRating.put(userId, userAvg);
			this.globalUserAverageRating += userAvg;
		}
		this.globalAverageRating /= totalUserRatings.keySet().size();
		
		for(Integer restId : totalRestaurantRatings.keySet()){
			double restAvg = totalRestaurantRatings.get(restId)/countRestaurantRatings.get(restId);
			this.restaurantAvgRating.put(restId, restAvg);
			this.globalRestaurantAverageRating += restAvg;
		}
		this.globalRestaurantAverageRating /= totalRestaurantRatings.keySet().size();
	}

	
	@Override
	/**
	 * @return a list of predicted labels in the same order data is provided
	 */
	public Dataset test(Dataset data) {
		List<Label> predictions = new ArrayList<Label>();
		for(Sample sample : data.getSamples()){
			Double userAvgRating = this.userAvgRating.get(sample.getFeatureValues().getUserId());
			double userBias = userAvgRating == null ? 0 : userAvgRating - globalUserAverageRating;
			Double restAvgRating = this.restaurantAvgRating.get(sample.getFeatureValues().getRestaurantId());
			double restBias = restAvgRating == null ? 0 : restAvgRating - globalRestaurantAverageRating;
			
			double predictedLabel = globalAverageRating +
					userBiasWeight * userBias + 
					restBiasWeight * restBias;
			predictions.add(new Label(predictedLabel));
		}
		//TODO: idk, need to finalize dataset first to map my predictions to samples
		return null;
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
