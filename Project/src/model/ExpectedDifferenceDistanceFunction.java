package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import data.Dataset;
import data.Rating;
import data.Sample;

/**
 * Distance function to compare different user's profiles using probabilistic model.
 * Source : EMD-GD from the paper "Alleviating the Sparsity Problem in Collaborative Filtering"
 * 
 * Given two users profiles, u1 and u2, which is just a vector of ratings for different restaurants
 * We calculate the distance between the two users by calculating E[|u1-u2|] 
 * Assuming the ratings between two different users are independent, this evaluates to
 * sum for each restaurant r'{ sum across all r1, r2{ P(u1, r', r1) P(u2,r',r2) |r1-r2|}}
 * P(u,r,rating) is described in depth below
 * @author sjonany
 *
 */
public class ExpectedDifferenceDistanceFunction implements UserDistanceFunction{
	//key = user Id, value = the ratings he gave for each restaurant
	private Map<String, Map<String, Rating>> userMap;
	//probability that restaurant gets a certain rating, as calculated from the data distribution
	//key = restId, value = {key = rating, value = probability}
	private Map<String, Map<Rating,Double>> restaurantProbDistribution;
	//global probability of any rating, as calculated from the data distribution
	//key = rating, value = probability
	private Map<Rating,Double> globalProbDistribution;
	
	//all known restaurant ids
	private Set<String> restIdSet;
	
	/////////////////
	//caches

	//key = restId, value = expected |r1-r2| for the restaurant for two unknown user ratings
	private Map<String, Double> restExpectation;
	//key user pair, value = distance between two users
	private Map<UserPair, Double> userPairDistanceCache;
	
	public ExpectedDifferenceDistanceFunction(){
		userMap = new HashMap<String,Map<String, Rating>>();
		restaurantProbDistribution = new HashMap<String, Map<Rating,Double>>();
		restIdSet = new HashSet<String>();
		globalProbDistribution = new HashMap<Rating,Double>();
		restExpectation = new HashMap<String, Double>();
		userPairDistanceCache = new HashMap<UserPair, Double>();
	}
	
	@Override
	public void init(Dataset dataset, Parameters params) {
		//key = restId, value = {key = rating, value = #users given this rating}
		Map<String, Map<Rating, Integer>> restaurantTally = new HashMap<String, Map<Rating, Integer>>();

		//key = rating, value = #users given this rating
		Map<Rating, Integer> globalTally = new HashMap<Rating, Integer>();
		dataset.resetIterator();
		while(dataset.hasNext()){
			Sample s = dataset.next();
			String userId = s.getFeatureValues().getUserId();
			String restId = s.getFeatureValues().getRestaurantId();
			double rating = s.getLabel().getRating();
			
			Map<String, Rating> singleUserRatings = userMap.get(userId);
			if(singleUserRatings == null){
				singleUserRatings = new HashMap<String, Rating>();
				userMap.put(userId, singleUserRatings);
			}
			
			Map<Rating, Integer> restTally = restaurantTally.get(restId);
			if(restTally == null){
				restTally = new HashMap<Rating, Integer>();
				restaurantTally.put(restId, restTally);
			}
			Rating keyRating = Rating.valueOf(rating);
			incrementTally(restTally, keyRating);
			
			incrementTally(globalTally, keyRating);
			singleUserRatings.put(restId, Rating.valueOf(rating));
			restIdSet.add(restId);
		}
		for(String restId : restIdSet){
			this.restaurantProbDistribution.put(restId, getNormalizedDistribution(restaurantTally.get(restId)));
		}
		
		for(String restId : restIdSet){
			double expectation = 0.0;
			for(Rating r1 : Rating.values()){
				for(Rating r2 : Rating.values()){
					expectation += Math.abs(r1.getRatingValue() - r2.getRatingValue()) * 
							 getProbability(this.restaurantProbDistribution.get(restId), r1) * 
							 getProbability(this.restaurantProbDistribution.get(restId), r2);
				}
			}
			restExpectation.put(restId, expectation);
		}
		
		this.globalProbDistribution = getNormalizedDistribution(globalTally);
	}

	@Override
	public double getDistance(String user1, String user2) {
		UserPair pairKey = new UserPair(user1, user2);
		if(userPairDistanceCache.containsKey(pairKey)){
			return userPairDistanceCache.get(pairKey);
		}
		
		double expectation = 0.0;
		for(String restId : restIdSet){
			//do we have exact ratings for this <user, rest> pair?
			boolean isUser1Visit = userMap.containsKey(user1) && userMap.get(user1).containsKey(restId);
			boolean isUser2Visit = userMap.containsKey(user2) && userMap.get(user2).containsKey(restId);
			if(!isUser1Visit && !isUser2Visit){
				expectation += restExpectation.get(restId);
			}else if(isUser1Visit && isUser2Visit){
				Rating r1 = userMap.get(user1).get(restId);
				Rating r2 = userMap.get(user2).get(restId);
				expectation += Math.abs(r1.getRatingValue()-r2.getRatingValue());
			}else if(isUser1Visit){
				Rating r1 = userMap.get(user1).get(restId);
				for(Rating r2 : Rating.values()){
					expectation += Math.abs(r1.getRatingValue() - r2.getRatingValue()) * 
							calcProbability(user1, restId, r1) * 
							calcProbability(user2, restId, r2);
				}
			}else{
				Rating r2 = userMap.get(user2).get(restId);
				for(Rating r1 : Rating.values()){
					expectation += Math.abs(r1.getRatingValue() - r2.getRatingValue()) * 
							calcProbability(user1, restId, r1) * 
							calcProbability(user2, restId, r2);
				}
			}
		}
		userPairDistanceCache.put(pairKey, expectation);
		return expectation;
	}
	
	/**
	 * @param rating - must be between 0 and 5.0, with increments of 0.5
	 * return probability that userId will rate restId with the given rating
	 * if restaurant is known
	 * 	if user has rated on the restaurant, we set prob = 1 for that exact stored rating, and 0 otherwise
	 * 	else
	 * 	  return probability based on that specific restaurant's probability distribution
	 * else
	 * 	we return probability based on global rating distribution
	 * note: hmm.. actually in this case, we always use restaurants that exist in our training data.
	 * oh well... might be useful to handle this case someday
	 */
	public double calcProbability(String userId, String restId, Rating rating){
		if(restIdSet.contains(restId)){
			if(userMap.containsKey(userId) && userMap.get(userId).containsKey(restId)){
				if(rating.equals(userMap.get(userId).get(restId))){
					return 1.0;
				}else{
					return 0.0;
				}
			}else{
				return getProbability(this.restaurantProbDistribution.get(restId), rating);
			}
		}else{
			return getProbability(this.globalProbDistribution, rating);
		}
	}
	
	///////////////////////////////////////////
	//tallying utility functions
	
	private <K> double getProbability(Map<K, Double> distribution, K key){
		if(!distribution.containsKey(key)){
			return 0.0;
		}else{
			return distribution.get(key);
		}
	}
	
	private <K> void incrementTally (Map<K, Integer> tally, K key){
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
