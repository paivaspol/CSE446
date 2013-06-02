package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import data.Dataset;
import data.FeatureValues;
import data.Label;
import data.Sample;

//unweighted KNN model
public class KNNModel extends UserBasedInstanceModel{
	//how many nearest neighbors to look at
	public enum ParameterKeys{K};
	
	private int numNearestNeighbor;
	private UserDistanceFunction distanceFcn;
	private Parameters params;

	
	public KNNModel(Parameters p, UserDistanceFunction distanceFcn){
		super();
		this.numNearestNeighbor = Integer.parseInt(p.getParam(ParameterKeys.K.name()));
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
		//order neighbors by distance to featvals
		PriorityQueue<Sample> neighbors = new PriorityQueue<Sample>(numNearestNeighbor,
			new Comparator<Sample>(){
				@Override
				public int compare(Sample sample1, Sample sample2) {
					return -Double.compare(
						KNNModel.this.distanceFcn.getDistance(sample1.getFeatureValues().getUserId(), featVals.getUserId()),
						KNNModel.this.distanceFcn.getDistance(sample2.getFeatureValues().getUserId(), featVals.getUserId()));
				}
		});
		
		//ratings of the same restaurant by other users
		List<Sample> coratings = super.reviewMap.get(featVals.getRestaurantId());
		
		//if no other user to find similarity to, just return global average
		if(coratings == null || coratings.size() == 0){
			return new Label(super.globalAverageRating);
		}
		
		for(Sample s : coratings){
			neighbors.add(s);
			if(neighbors.size() > this.numNearestNeighbor){
				neighbors.remove();
			}
		}
		
		double sum = 0;
		int count = 0;
		while(!neighbors.isEmpty()){
			sum += neighbors.remove().getLabel().getRating();
			count++;
		}
		
		return new Label(sum / count);
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
			if(count %1000 ==0){
				System.out.println("Completed prediction #" + count);
			}
			count++;
			Sample sample = data.next();
			predictions.add(predictLabel(sample.getFeatureValues()));
		}
		
		return predictions;
	}

}
