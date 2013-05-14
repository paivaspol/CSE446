package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.Dataset;
import data.Sample;

/**
 * Calculates distance between two users by doing sum|pi-qi| / number of co-ratings
 * if the two users did not co-rate anything, distance is set to 2.5
 * @author sjonany
 */
public class ScaledManhattanDistanceFunction implements UserDistanceFunction{
	//default distance between two users if there is no way to compare them
	private static double DEFAULT_DISTANCE = 1.0;
	
	//key = user Id, value = the ratings he gave for each restaurant
	private Map<String, Map<String, Double>> userMap;
	public ScaledManhattanDistanceFunction(){
		userMap = new HashMap<String,Map<String, Double>> ();
	}
	
	@Override
	public void init(Dataset dataset, Parameters params) {
		dataset.resetIterator();
		while(dataset.hasNext()){
			Sample s = dataset.next();
			String userId = s.getFeatureValues().getUserId();
			String restId = s.getFeatureValues().getRestaurantId();
			double rating = s.getLabel().getRating();
			
			Map<String, Double> singleUserRatings = userMap.get(userId);
			if(singleUserRatings == null){
				singleUserRatings = new HashMap<String, Double>();
				userMap.put(userId, singleUserRatings);
			}
			
			singleUserRatings.put(restId, rating);
		}
	}

	/**
	 * distance is defined as avg|r1i - r1j|, discounting all non-intersecting ratings
	 * if users have not corated anything, we just return a default value
	 */
	@Override
	public double getDistance(String user1, String user2) {
		Map<String, Double> ratings1 = userMap.get(user1);
		Map<String, Double> ratings2 = userMap.get(user2);
		if(ratings1 == null){
			ratings1 = new HashMap<String,Double>();
		}
		if(ratings2 == null){
			ratings2 = new HashMap<String,Double>();
		}
		
		double totalManhattan = 0.0;
		int countIntersect = 0;
		for(String rest1 : ratings1.keySet()){
			if(ratings2.containsKey(rest1)){
				countIntersect++;
				totalManhattan += Math.abs(ratings1.get(rest1) - ratings2.get(rest1));
			}
		}
		
		if(countIntersect == 0){
			return DEFAULT_DISTANCE;
		}else{
			return totalManhattan / countIntersect;
		}
	}

}
