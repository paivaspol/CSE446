package model;

import java.util.ArrayList;
import java.util.List;

import data.Dataset;
import data.Label;
import data.Sample;

public class SampleMeanModel implements LearningModel {

	private double runningSum = 0.0;
	private int dataSetSize = 0;
	
	@Override
	public void train(Dataset data) {
		reset();
		dataSetSize = data.getSize();
		while (data.hasNext()) {
			Sample s = data.next();
			Label ratingLabel = s.getLabel();
			runningSum += ratingLabel.getRating();
		}
	}

	@Override
	public List<Label> test(Dataset data) {
		List<Label> result = new ArrayList<Label>();
		for (int i = 0; i < dataSetSize; i++) {
			Label label = new Label(runningSum / dataSetSize);
			result.add(label);
		}
		return result;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		runningSum = 0.0;
		dataSetSize = 0;
	}

}
