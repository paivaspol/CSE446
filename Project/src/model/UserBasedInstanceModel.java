package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.Dataset;
import data.FeatureValues;
import data.Label;
import data.Sample;

/**
 * learning models which predicts rating by looking at other users who rate
 * the same restaurant, treat these other uses as points, and perform some
 * neighbor calculations for their contributions
 * @author sjonany
 */
public abstract class UserBasedInstanceModel implements LearningModel{
	//key = business id, value = reviews for that business
	protected Map<String, List<Sample>> reviewMap;
	//average rating across all user, restaurant pairs
	protected double globalAverageRating;
	
	public UserBasedInstanceModel(){
		baseReset();
	}
	
	public void baseReset(){
		this.reviewMap = new HashMap<String, List<Sample>>();
	}
	
	@Override
	public void train(Dataset data) {
		data.resetIterator();
		double totalRating = 0;
		int countRating = 0;
		while(data.hasNext()){
			Sample s = data.next();
			String restId = s.getFeatureValues().getRestaurantId();
			totalRating += s.getLabel().getRating();
			countRating++;
			
			List<Sample> reviewsForOneBusiness = reviewMap.get(restId);
			if(reviewsForOneBusiness == null){
				reviewsForOneBusiness = new ArrayList<Sample>();
				reviewMap.put(restId, reviewsForOneBusiness);
			}
			reviewsForOneBusiness.add(s);
		}
		this.globalAverageRating = 1.0*totalRating / countRating;
	}
	
	@Override
	public List<Label> test(Dataset data){
		List<Label> predictions = new ArrayList<Label>();
		data.resetIterator();
		while(data.hasNext()){
			Sample sample = data.next();
			predictions.add(predictLabel(sample.getFeatureValues()));
		}
		
		return predictions;
	}
	
	/**
	 * given x = featVals, return y_predicted
	 * @param featVals test input
	 * @return the predicted output
	 */
	public abstract Label predictLabel(FeatureValues featVals);
}
