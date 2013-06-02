package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.SVDModel.ParameterKeys;

import data.Dataset;
import data.FeatureValues;
import data.Label;
import data.Sample;

/**
 * translated pretty much verbatim from http://www.timelydevelopment.com/demos/NetflixPrize.aspx
 * to make sure results are right
 */
public class SVDModel2 implements LearningModel{
	private static final int MIN_EPOCHS = 64;
	private static final int MAX_EPOCHS = 200;
	private static final double MIN_IMPROVEMENT = 0.0001;
	private static final double INIT = 0.1;          
	private static final double LRATE = 0.001;

	private double[][] m_aMovieFeatures;
	private double[][] m_aCustFeatures;
	private Map<String, Integer> restIndexMap;
	private Map<String, Integer> userIndexMap;
	// map indices back to their original string ids
	private String[] restInvIndex;
	private String[] userInvIndex;
	private double globalAvg;
	private double lambda;
	private int MAX_FEATURES;


	public SVDModel2(Parameters p){
		this.lambda = Double.parseDouble(p.getParam(ParameterKeys.LAMBDA.name()));
		this.MAX_FEATURES = Integer.parseInt(p.getParam(ParameterKeys.NUM_FEATURES.name()));
		reset();
	}

	@Override
	public void train(Dataset data){
		data.resetIterator();
		Set<String> userSet = new HashSet<String>();
		Set<String> restSet = new HashSet<String>();
		int count = 0;
		double totRating = 0;
		while(data.hasNext()){
			Sample s = data.next();
			String userId = s.getFeatureValues().getUserId();
			String restId = s.getFeatureValues().getRestaurantId();
			double rating = s.getLabel().getRating();
	
			userSet.add(userId);
			restSet.add(restId);
			totRating += rating;

			count++;
		}
		
		this.globalAvg = totRating / count;
		//index the ratings
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

		IndexedRating[] m_aRatings = new IndexedRating[count];
		data.resetIterator();
		int m_nRatingCount = 0;
		while(data.hasNext()){
			Sample s = data.next();
			String userId = s.getFeatureValues().getUserId();
			String restId = s.getFeatureValues().getRestaurantId();
			double rating = s.getLabel().getRating();

			m_aRatings[m_nRatingCount] = new IndexedRating();
			m_aRatings[m_nRatingCount].RestId = restIndexMap.get(restId);
			m_aRatings[m_nRatingCount].CustId = userIndexMap.get(userId);
			m_aRatings[m_nRatingCount].Rating = rating;
			m_aRatings[m_nRatingCount].Cache = 0;

			m_nRatingCount++;
		}

		m_aMovieFeatures = new double[MAX_FEATURES][restSet.size()];
		m_aCustFeatures = new double[MAX_FEATURES][userSet.size()];
		for (int f=0; f<MAX_FEATURES; f++)
		{
			for (int i=0; i<restSet.size(); i++) m_aMovieFeatures[f][i] = INIT;
			for (int i=0; i<userSet.size(); i++) m_aCustFeatures[f][i] = INIT;
		}

		
		int f, e, i, custId, cnt = 0;
		double err, p, sq, rmse_last, rmse = 2.0;
		rmse_last = -1;
		int movieId;
		double cf, mf;

		for (f=0; f<MAX_FEATURES; f++)
		{
			System.out.printf("\n--- Calculating feature: %d ---\n",  f);

			// Keep looping until you have passed a minimum number 
			// of epochs or have stopped making significant progress 
			for (e=0; (e < MIN_EPOCHS) || (rmse <= rmse_last - MIN_IMPROVEMENT); e++)
			{
				cnt++;
				sq = 0;
				rmse_last = rmse;

				for (i=0; i<m_nRatingCount; i++)
				{
					IndexedRating rating = m_aRatings[i];
					movieId = rating.RestId;
					custId = rating.CustId;

					// Predict rating and calc error
					p = PredictRating(movieId, custId, f, rating.Cache, true);
					err = (1.0 * rating.Rating - p);
					sq += err*err;

					// Cache off old feature values
					cf = m_aCustFeatures[f][custId];
					mf = m_aMovieFeatures[f][movieId];

					// Cross-train the features
					m_aCustFeatures[f][custId] += (float)(LRATE * (err * mf - this.lambda * cf));
					m_aMovieFeatures[f][movieId] += (float)(LRATE * (err * cf - this.lambda * mf));
				}

				rmse = Math.sqrt(sq/m_nRatingCount);

				//System.out.printf("     <set epoch='%d' rmse='%f' />\n",cnt,rmse);
			}

			// Cache off old predictions
			for (i=0; i<m_nRatingCount; i++)
			{
				IndexedRating rating = m_aRatings[i];
				rating.Cache = PredictRating(rating.RestId, rating.CustId, f, rating.Cache, false);
			}            
		}
	}

	@Override
	public void reset(){
		this.restIndexMap = new HashMap<String, Integer>();
		this.userIndexMap = new HashMap<String, Integer>();
	}


	//
	// PredictRating
	// - During training there is no need to loop through all of the features
	// - Use a cache for the leading features and do a quick calculation for the trailing
	// - The trailing can be optionally removed when calculating a new cache value
	//
	double PredictRating(int movieId, int custId, int feature, double cache, boolean bTrailing)
	{
		// Get cached value for old features or default to an average
		double sum = (cache > 0) ? cache : 1; //m_aMovies[movieId].PseudoAvg; 

		// Add contribution of current feature
		sum += m_aMovieFeatures[feature][movieId] * m_aCustFeatures[feature][custId];
		if (sum > 5) sum = 5;
		if (sum < 1) sum = 1;

		// Add up trailing defaults values
		if (bTrailing)
		{
			sum += (MAX_FEATURES-feature-1) * (INIT * INIT);
			if (sum > 5) sum = 5;
			if (sum < 1) sum = 1;
		}

		return sum;
	}


	double PredictRating(int movieId, int custId)
	{
		double sum = 1; //m_aMovies[movieId].PseudoAvg;

		for (int f=0; f<MAX_FEATURES; f++) 
		{
			sum += m_aMovieFeatures[f][movieId] * m_aCustFeatures[f][custId];
			if (sum > 5) sum = 5;
			if (sum < 1) sum = 1;
		}

		return sum;
	}

	private static class IndexedRating{
		public int RestId;
		public int CustId;
		public double Rating;
		public double Cache;
	}

	@Override
	public List<Label> test(Dataset data) {
		List<Label> predictions = new ArrayList<Label>();
		data.resetIterator();

		while(data.hasNext()){
			Sample s = data.next();
			String userId = s.getFeatureValues().getUserId();
			String restId = s.getFeatureValues().getRestaurantId();
			
			if(!userIndexMap.containsKey(userId) || !restIndexMap.containsKey(restId)){
				predictions.add(new Label(this.globalAvg));
			}else{
				 double rating = PredictRating(restIndexMap.get(restId), userIndexMap.get(userId));
				 predictions.add(new Label(rating));
			}
		}
		
		return predictions;
	}
}


