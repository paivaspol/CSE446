package model;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import model.Parameters;
import model.RidgeRegressionModel;

import org.junit.Before;
import org.junit.Test;

import data.Dataset;
import data.Label;
import data.Range;
import data.RealDataset;
import data.Sample;

public class RidgeRegressionModelTest {
	private Dataset testdata;
	private Dataset traindata;
	
	@Before
	public void setup(){
		List<Sample> trainSamples = new ArrayList<Sample>();
		trainSamples.add(new Sample("u1","r1",3.0));
		trainSamples.add(new Sample("u2","r2",4.0));
		trainSamples.add(new Sample("u2","r3",5.0));
		trainSamples.add(new Sample("u3","r2",2.0));
		trainSamples.add(new Sample("u4","r2",2.5));
		trainSamples.add(new Sample("u5","r3",3.0));
		RealDataset traindataset = new RealDataset(trainSamples);
		List<Range> ranges = new ArrayList<Range>();
		ranges.add(new Range(0,trainSamples.size()));
		this.traindata = new Dataset(traindataset, ranges);

		List<Sample> testSamples = new ArrayList<Sample>();
		testSamples.add(new Sample("u1","r1",0));
		testSamples.add(new Sample("u2","r3",0));
		testSamples.add(new Sample("u3","r6",0));
		testSamples.add(new Sample("u6","r2",0));
		testSamples.add(new Sample("u7","r7",0));
		RealDataset testDataset = new RealDataset(testSamples);
		ranges = new ArrayList<Range>();
		ranges.add(new Range(0,testSamples.size()));
		this.testdata = new Dataset(testDataset, ranges);
	}
	
	
	@Test
	public void testUnregularizedRegression() {
		Parameters param = new Parameters();
		param.setParam(RidgeRegressionModel.ParameterKeys.RIDGE_PENALTY.name(), "0");
		RidgeRegressionModel model = new RidgeRegressionModel(param);
		
		model.train(this.traindata);
		List<Label> predictions = model.test(this.testdata);
		double[] answer = new double[]{2.921711,4.693422, 2.11462,2.855702, 3.031725};
		
		for(int i=0;i<answer.length;i++){
			Assert.assertEquals(answer[i], predictions.get(i).getRating(), 0.001);
		}
	}
	
	@Test
	public void testRegularizedRegression() {
		Parameters param = new Parameters();
		param.setParam(RidgeRegressionModel.ParameterKeys.RIDGE_PENALTY.name(), "10");
		RidgeRegressionModel model = new RidgeRegressionModel(param);
		
		model.train(this.traindata);
		List<Label> predictions = model.test(this.testdata);
		double[] answer = new double[]{3.136393,3.76148,2.826496,3.117541, 3.167813};
		
		for(int i=0;i<answer.length;i++){
			Assert.assertEquals(answer[i], predictions.get(i).getRating(), 0.001);
		}
	}
}
