package utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import config.JoeConfig;
import data.Dataset;
import data.Rating;
import data.RealDataset;
import data.Sample;

/**
 * analyze datasets
 * @author sjonany
 */
public class DatasetStats {	
	public static void main(String[] args){
		Dataset dataset = new Dataset(new RealDataset(JoeConfig.TRAIN_DIR));
		Set<String> restIdSet = new HashSet<String>();
		Set<String> userIdSet = new HashSet<String>();
		Map<Rating, Integer> globalRatingTally = new HashMap<Rating, Integer>();
		//key = rest ID, value = num visitors
		Map<String, Integer> restTally = new HashMap<String, Integer>();
		//key = rest ID, value = num visitors
		Map<String, Integer> userTally = new HashMap<String, Integer>();
		while(dataset.hasNext()){
			Sample s = dataset.next();
			String userId = s.getFeatureValues().getUserId();
			String restId = s.getFeatureValues().getRestaurantId();
			double rating = s.getLabel().getRating();

			Rating keyRating = Rating.valueOf(rating);
			incrementTally(globalRatingTally, keyRating);
			incrementTally(restTally, restId);
			incrementTally(userTally, userId);
			restIdSet.add(restId);
			userIdSet.add(userId);
		}
		System.out.println("Total number of users = " +  userIdSet.size());
		System.out.println("Total number of restaurants = " +  restIdSet.size());

		System.out.println("Global rating distribution = ");
		for(Rating rating :  globalRatingTally.keySet()){
			System.out.println(rating + ", " + globalRatingTally.get(rating));
		}

		//key = # visitors, val = # rest with that #visitors
		Map<Integer, Integer> visitorTally = new HashMap<Integer, Integer>();
		for(String restId : restTally.keySet()){
			incrementTally(visitorTally, restTally.get(restId));
		}
		System.out.println("#visitors, #restaurants with that exact number of visitors");
		for(Integer viscount :  visitorTally.keySet()){
			System.out.println(viscount + ", " +  visitorTally.get(viscount));
		}

		//key = # ratings, val = # users with that number of ratings
		Map<Integer, Integer> ratingUserTally = new HashMap<Integer, Integer>();
		for(String userId : userTally.keySet()){
			incrementTally(ratingUserTally, userTally.get(userId));
		}
		System.out.println("#ratings, #users with that exact number of ratings");

		for(Integer ratingCount :  ratingUserTally.keySet()){
			System.out.println(ratingCount + ", " +  ratingUserTally.get(ratingCount));
		}
	}

	private static <K> void incrementTally (Map<K, Integer> tally, K key){
		Integer prevCount = tally.get(key);
		if(prevCount == null){
			prevCount = 0;
		}
		tally.put(key, prevCount+1);
	}

	/**
	 * convert a tally into a valid probability distribution, using the same keys
	 * @param tally - key = rating, value = how many times the rating appears
	 * @return normalized distribution of tally, where map.get(val) = Pr(value = val), and
	 * 	sum across all values = 1
	 * if tally is all 0, will return a uniform distribution
	 * note: if probability is 0, it won't be stored in the distribution
	 */
	private static Map<Rating, Double> getNormalizedDistribution(Map<Rating, Integer> tally){
		int totalCount = 0;
		for(Rating key : tally.keySet()){
			totalCount += tally.get(key);
		}

		Map<Rating, Double> distribution = new HashMap<Rating, Double>();

		if(totalCount == 0){
			double uniformProb = 1.0 / tally.keySet().size();

			for(Rating key : tally.keySet()){
				distribution.put(key, uniformProb);
			}
		}else{
			for(Rating key : tally.keySet()){
				distribution.put(key, 1.0  * tally.get(key) / totalCount);
			}
		}
		return distribution;
	}
}
