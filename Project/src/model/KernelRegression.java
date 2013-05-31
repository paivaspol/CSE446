package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import data.Dataset;
import data.FeatureValues;
import data.Label;
import data.Sample;

/**
 * Kernel regression model
 * @author vaspol
 */
public class KernelRegression extends UserBasedInstanceModel{
	//how many nearest neighbors to look at
	public enum ParameterKeys{ rho };
	
	private int rho;
	private UserDistanceFunction distanceFcn;
	private Parameters params;

	
	public KernelRegression(Parameters p, UserDistanceFunction distanceFcn){
		super();
		this.rho = Integer.parseInt(p.getParam(ParameterKeys.rho.name()));
		this.distanceFcn = distanceFcn;
		this.params = p;
		reset();
	}
	
	@Override
	public void reset(){
		super.baseReset();
		this.distanceFcn.reset();
	}
	
	
	public Label predictLabel(final FeatureValues featVals){
		//ratings of the same restaurant by other users
		List<Sample> coratings = super.reviewMap.get(featVals.getRestaurantId());
		
		//if no other user to find similarity to, just return global average
		if(coratings == null || coratings.size() == 0){
			return new Label(super.globalAverageRating);
		}
		double weightSum = 0.0;
		double weightedDataSum = 0.0;
		for(Sample s : coratings){
			// compute the weight from all the restaurants
			double curWeight = computeWeight(s, featVals.getUserId());
			weightSum += curWeight;
			weightedDataSum += curWeight * s.getLabel().getRating();
		}		
		return new Label(weightedDataSum / weightSum);
	}
	
	@Override
	public void train(Dataset data) {
		super.train(data);
		this.distanceFcn.init(data, params);
	}

	@Override
	public List<Label> test(Dataset data) {
		List<Label> predictions = new ArrayList<Label>();
		data.resetIterator();
		int count = 0;
		while(data.hasNext()){
			if(count % 1000 == 0){
				System.out.println("Completed prediction #" + count);
			}
			count++;
			Sample sample = data.next();
			predictions.add(predictLabel(sample.getFeatureValues()));
		}
		return predictions;
	}
	
	/**
	 * Computes the weight
	 * @param s
	 * @param queryUserId
	 * @return
	 */
	private double computeWeight(Sample s, String queryUserId) {
		double distance = this.distanceFcn.getDistance(s.getFeatureValues().getUserId(), queryUserId);
		return Math.exp((-1 * distance * distance) / (rho * rho));
	}

}
