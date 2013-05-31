package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data.Dataset;
import data.FeatureValues;
import data.Label;
import data.Sample;

/**
 * Singular value decomposition using stochastic gradient descent for optimization
 * based on http://www2.research.att.com/~volinsky/papers/ieeecomputer.pdf page 45
 * @author sjonany
 */
public class SVDModel implements LearningModel{
	//the learning rate used for netflix prize is = 0.001, http://sifter.org/~simon/journal/20061211.html
	//LAMBDA is the regularizing term
	public enum ParameterKeys{LEARNING_RATE, LAMBDA, NUM_FEATURES};

	//num epochs to optimize a single feature
	//seems to be a large number from s
	//http://www.netflixprize.com/community/viewtopic.php?pid=5638
	private static final double TRAIN_EPOCH = 100;

	private double lambda;
	private double learningRate;
	private int numFeatures;

	private double globalAvgRating;

	//key = id, value = the vector corresponding to the id, size = NUM_FEATURES
	//from the att paper, q_i = restVecs.get(i)
	private double[][] userVecs;
	private double[][] restVecs;
	private Map<String, Integer> restIndexMap;
	private Map<String, Integer> userIndexMap;
	// map indices back to their original string ids
	private String[] restInvIndex;
	private String[] userInvIndex;
	
	public SVDModel(Parameters p){
		this.lambda = Double.parseDouble(p.getParam(ParameterKeys.LAMBDA.name()));
		this.learningRate = Double.parseDouble(p.getParam(ParameterKeys.LEARNING_RATE.name()));
		this.numFeatures = Integer.parseInt(p.getParam(ParameterKeys.NUM_FEATURES.name()));
		reset();
	}

	@Override
	public void train(Dataset data) {
		data.resetIterator();
		//first collect all the restaurants and users
		Set<String> userSet = new HashSet<String>();
		Set<String> restSet = new HashSet<String>();
		double totalRating = 0.0;
		int countRating = 0;
		while(data.hasNext()){
			Sample s = data.next();
			String userId = s.getFeatureValues().getUserId();
			String restId = s.getFeatureValues().getRestaurantId();
			double rating = s.getLabel().getRating();
			userSet.add(userId);
			restSet.add(restId);

			totalRating += rating;
			countRating++;
		}
		
		//key = rest ID, val = rest index, created on the fly so I can address restaurants with numbers
		this.restIndexMap = new HashMap<String, Integer>();
		this.userIndexMap = new HashMap<String, Integer>();
		// map indices back to their original string ids
		this.restInvIndex = new String[restSet.size()];
		this.userInvIndex = new String[userSet.size()];
				
		Iterator<String> restIt = restSet.iterator();
		for(int i=0; i<restSet.size(); i++){
			String rest = restIt.next();
			restIndexMap.put(rest, i);
			restInvIndex[i] = rest;
		}

		Iterator<String> userIt = userSet.iterator();
		for(int i=0; i<userSet.size(); i++){
			String user = userIt.next();
			userInvIndex[i] = user;
			userIndexMap.put(user, i);
		}
		
		this.globalAvgRating = totalRating / countRating;

		//initialize the vectors for each user and restaurant
		double[] initVector = getInitialVector();
		this.userVecs = new double[userSet.size()][this.numFeatures];
		this.restVecs = new double[restSet.size()][this.numFeatures];
		
		for(int user = 0; user < userSet.size(); user++){
			userVecs[user] = initVector.clone();
		}
		
		for(int rest = 0; rest < restSet.size(); rest++){
			restVecs[rest] = initVector.clone();
		}

		//SGD, until convergence, when the vectors did not change by much 
		data.resetIterator();

		
		//caches the predictions, which are just dot products
		//since we are optimizing one feature at a time in an ordered fashion, there is no need to compute
		//the entire dot product from scratch
		//key = iteration number, or the dataset index
		//value = dot product using the first f-1 features, which will remain unchanged once those features are optimized
		double[] prevPartialDotProduct = new double[data.getSize()];
		
		//optimize a feature at a time
		for(int feature = 0; feature < this.numFeatures; feature++){
			for(int epoch = 0; epoch < TRAIN_EPOCH; epoch++){
				data.resetIterator();
				int datasetIndex = 0;
				while(data.hasNext()){
					Sample s = data.next();
					String userId = s.getFeatureValues().getUserId();
					String restId = s.getFeatureValues().getRestaurantId();
					double rating = s.getLabel().getRating();
					
					double[] p_u = userVecs[userIndexMap.get(userId)];
					double[] q_i = restVecs[restIndexMap.get(restId)];
					
					//prediction is actualy just p_u dot q_i, but we cache some previous dot products,
					//since the contributions from the previous feature remain unchanged, and so do
					//the contributions from the future features
					double prediction = prevPartialDotProduct[datasetIndex];
					prediction += p_u[feature] * q_i[feature];
					prediction += (this.numFeatures - feature - 1) * initVector[feature] * initVector[feature];
					double error = rating - prediction;
					
					double prevPu = p_u[feature];
					double prevQi = q_i[feature];
					p_u[feature] += this.learningRate * (error * prevQi - this.lambda * prevPu);
					q_i[feature] += this.learningRate * (error * prevPu - this.lambda * prevQi);
				
					//update dot product cache
					//prevDotProduct[datasetIndex] = prediction;
					datasetIndex++;
				}
				if(epoch % 10 == 0)
					System.out.println("Optimizing feature " + feature + ", epoch = " + epoch);
			}//end epoch

			//the feature has been optimized, time to update the partial dot product cache
			data.resetIterator();
			int datasetIndex = 0;
			while(data.hasNext()){
				Sample s = data.next();
				String userId = s.getFeatureValues().getUserId();
				String restId = s.getFeatureValues().getRestaurantId();
				prevPartialDotProduct[datasetIndex] += userVecs[userIndexMap.get(userId)][feature] * 
						restVecs[restIndexMap.get(restId)][feature];
				datasetIndex++;
			}
		}
	}
	
	/**
	 all user and restaurant vectors get initialized to the return value of this fcn

	 from http://sifter.org/~simon/journal/20061211.html, he just used 0.1 's 
	 " I initialize both vectors to 0.1, 0.1, 0.1, 0.1, .... Profound, no? 
	 (How it's initialized actually does matter a bit later, but not yet...)"

	 */
	private double[] getInitialVector(){
		double[] doub = new double[this.numFeatures];
		for(int i=0; i<doub.length; i++){
			doub[i] = 0.1;
		}
		return doub;
	}


	/**
	 * rating prediction with clipping, again from simon http://www.sifter.org/~simon/journal/20061211.html
	 * @param userId
	 * @param restId
	 * @return
	 */
	private double predictRating(String userId, String restId){
		if(!userIndexMap.containsKey(userId) || !restIndexMap.containsKey(restId)){
			return this.globalAvgRating;
		}
		double[] u = userVecs[userIndexMap.get(userId)];
		double[] r = restVecs[restIndexMap.get(restId)];
		
		double result = 1.0;
		for(int i=0;i<u.length;i++){
			result += u[i] * r[i];
			if(result > 5){
				result = 5;
			}
			
			if(result < 1){
				result = 1;
			}
		}
		return result;
	}

	@Override
	public List<Label> test(Dataset data) {		
		List<Label> predictions = new ArrayList<Label>();
		data.resetIterator();

		while(data.hasNext()){
			Sample sample = data.next();
			FeatureValues feats = sample.getFeatureValues();
			predictions.add(new Label(predictRating(feats.getUserId(), 
					feats.getRestaurantId())));
		}
		
		return predictions;
	}

	@Override
	public void reset() {		
		this.restIndexMap = new HashMap<String, Integer>();
		this.userIndexMap = new HashMap<String, Integer>();
	}
}
