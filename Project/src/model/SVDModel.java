package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import data.Dataset;
import data.FeatureValues;
import data.Label;
import data.Sample;
import data.Vector;

/**
 * Singular value decomposition using stochastic gradient descent for optimization
 * based on http://www2.research.att.com/~volinsky/papers/ieeecomputer.pdf page 45
 * @author sjonany
 */
public class SVDModel implements LearningModel{
	//the learning rate used for netflix prize is = 0.001, http://sifter.org/~simon/journal/20061211.html
	//LAMBDA is the regularizing term
	public enum ParameterKeys{LEARNING_RATE, LAMBDA, NUM_FEATURES};

	//define SGD convergence if none of vector weights change by this much
	private static final double CONVERGENCE_LIMIT = 0.0001;

	private double lambda;
	private double learningRate;
	private int numFeatures;

	private double globalAvgRating;

	//key = id, value = the vector corresponding to the id, size = NUM_FEATURES
	//from the att paper, q_i = restVecs.get(i)
	private Map<String, Vector> userVecs;
	private Map<String, Vector> restVecs;

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
		this.globalAvgRating = totalRating / countRating;

		//initialize the vectors for each user and restaurant
		//we just set to 0
		for(String user : userSet){
			userVecs.put(user, getInitialVector());
		}

		for(String rest : restSet){
			restVecs.put(rest, getInitialVector());
		}

		//SGD, until convergence, when the vectors did not change by much 
		int numIteration = 0;
		data.resetIterator();
		
		double prevMSEReg = -1;
		//boolean onePassIsComplete = false;
		while(true){
			Sample s = data.next();
			String userId = s.getFeatureValues().getUserId();
			String restId = s.getFeatureValues().getRestaurantId();
			double rating = s.getLabel().getRating();

			double error = rating - predictRating(userId, restId) ;
			Vector p_u = userVecs.get(userId);
			Vector q_i = restVecs.get(restId);

			Vector newUserVec = p_u.plus(q_i.times(error).minus(p_u.times(this.lambda)).times(this.learningRate));
			p_u = newUserVec;
			Vector newRestVec = q_i.plus(p_u.times(error).minus(q_i.times(this.lambda)).times(this.learningRate));

			if(newUserVec.containsNan() ||
					newRestVec.containsNan() ||
					p_u.containsNan() ||
					q_i.containsNan()){
				System.out.println("NAN!!!");
				System.out.println(p_u);
				System.out.println(q_i);
				System.out.println(newUserVec);
				System.out.println(newRestVec);
				new Scanner(System.in).nextLine();
			}
			//check for convergence, but we can only quit only once we have made a complete pass
			/*
			double maxDiff = 0.0;
			if(onePassIsComplete){
				boolean isConverge = true;
				for(int factor=0; factor<this.numFeatures; factor++){
					double diff1 = Vector.getMaxAbsDifference(userVecs.get(userId), newUserVec);
					double diff2 = Vector.getMaxAbsDifference(restVecs.get(restId), newRestVec);
					maxDiff = Math.max(diff1,  diff2);
					if(diff1 > CONVERGENCE_LIMIT){
						isConverge = false;
						break;
					}
	
					if(diff2 > CONVERGENCE_LIMIT){
						isConverge = false;
						break;
					}
				}
				if(isConverge){
					break;
				}
			}*/

			userVecs.put(userId, newUserVec);
			restVecs.put(restId, newRestVec);
			
			if(!data.hasNext()){
				//onePassIsComplete = true;
				data.resetIterator();
				
				//calculate the total regularized SSE
				double totalRegSSE = 0.0;
				int count = 0;
				while(data.hasNext()){
					Sample s1 = data.next();

					String userId1 = s1.getFeatureValues().getUserId();
					String restId1 = s1.getFeatureValues().getRestaurantId();
					double rating1 = s1.getLabel().getRating();
					
					totalRegSSE += Math.pow(rating1-predictRating(userId1,restId1), 2) + 
							this.lambda * (userVecs.get(userId1).getL2NormSq() +
									restVecs.get(restId1).getL2NormSq());
					count++;
				}
				double MSEReg = totalRegSSE / count;

				System.out.println("current mse_reg = " + MSEReg);
				if(MSEReg < prevMSEReg && Math.abs(MSEReg - prevMSEReg) < CONVERGENCE_LIMIT){
					break;
				}
				
				prevMSEReg = MSEReg;
				data.resetIterator();
				
			}

			numIteration++;
			if(numIteration % 100000 == 0){
				System.out.println("SGD iteration " + numIteration );
			}
		}
		System.out.println("Converged at iteration = " + numIteration);
	}
	
	/**
	 all user and restaurant vectors get initialized to the return value of this fcn

	 from http://sifter.org/~simon/journal/20061211.html, he just used 0.1 's 
	 " I initialize both vectors to 0.1, 0.1, 0.1, 0.1, .... Profound, no? 
	 (How it's initialized actually does matter a bit later, but not yet...)"

	 */
	private Vector getInitialVector(){
		double[] doub = new double[this.numFeatures];
		for(int i=0; i<doub.length; i++){
			doub[i] = 0.1;
		}
		return new Vector(doub);
	}


	private double predictRating(String userId, String restId){
		Vector userVec = userVecs.get(userId);
		Vector restVec = restVecs.get(restId);

		if(userVec == null || restVec == null){
			return this.globalAvgRating;
		}

		double result = userVec.dotProduct(restVec);
		if(Double.isNaN(result)){
			System.out.println("NAN!!!");
			System.out.println(userVec);
			System.out.println(restVec);
			new Scanner(System.in).nextLine();
		}
		return userVec.dotProduct(restVec);
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
		userVecs = new HashMap<String, Vector>();
		restVecs = new HashMap<String, Vector>();
	}
}
