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
public class KNNModel implements LearningModel{
	//how many nearest neighbors to look at
	public enum ParameterKeys{K};
	
	private int numNearestNeighbor;
	private DistanceFunction distanceFcn;
	private Parameters params;
	private Dataset trainSet;
	
	public KNNModel(Parameters p, DistanceFunction distanceFcn){
		this.numNearestNeighbor = Integer.parseInt(p.getParam(ParameterKeys.K.name()));
		this.distanceFcn = distanceFcn;
		this.params = p;
	}
	
	
	public Label calcLabel(final FeatureValues featVals){
		trainSet.resetIterator();
		//order neighbors by distance to featvals
		PriorityQueue<Sample> neighbors = new PriorityQueue<Sample>(numNearestNeighbor,
			new Comparator<Sample>(){
				@Override
				public int compare(Sample o1, Sample o2) {
					return -Double.compare(KNNModel.this.distanceFcn.getDistance(o1.getFeatureValues(), featVals),
							KNNModel.this.distanceFcn.getDistance(o2.getFeatureValues(), featVals));
				}
		});
		
		while(trainSet.hasNext()){
			Sample s = trainSet.next();
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
		
		return new Label(sum/count);
	}
	
	@Override
	public void train(Dataset data) {
		this.distanceFcn.init(data, params);
		this.trainSet = data;
	}

	@Override
	public List<Label> test(Dataset data) {
		List<Label> predictions = new ArrayList<Label>();
		data.resetIterator();
		while(data.hasNext()){
			Sample sample = data.next();
			predictions.add(calcLabel(sample.getFeatureValues()));
		}
		
		return predictions;
	}

}
