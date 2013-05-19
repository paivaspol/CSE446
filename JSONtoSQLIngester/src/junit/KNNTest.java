package junit;

import java.util.ArrayList;
import java.util.List;

import model.KNNModel;
import model.Parameters;
import model.UserDistanceFunction;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import data.Dataset;
import data.Label;
import data.Range;
import data.RealDataset;
import data.Sample;

public class KNNTest {
	private KNNModel oneNN;
	private KNNModel threeNN;
	
	@Before
	public void setup(){
		List<Sample> trainSamples = new ArrayList<Sample>();
		trainSamples.add(new Sample("a","r1",3.0));
		trainSamples.add(new Sample("aaaa","r1",2.0));
		trainSamples.add(new Sample("aa","r1",4.0));
		trainSamples.add(new Sample("aaa","r1",5.0));
		trainSamples.add(new Sample("a","r2",2.5));
		trainSamples.add(new Sample("aa","r3",3.0));
		RealDataset traindataset = new RealDataset(trainSamples);
		List<Range> ranges = new ArrayList<Range>();
		ranges.add(new Range(0,trainSamples.size()));
	
		Dataset traindata = new Dataset(traindataset, ranges);

		UserDistanceFunction simpleDistFcn = new DudDistanceFunction();
		Parameters param1 = new Parameters();
		param1.setParam(KNNModel.ParameterKeys.K.name(),"1");
		this.oneNN = new KNNModel(param1, simpleDistFcn);
		this.oneNN.train(traindata);
		
		Parameters param3 = new Parameters();
		param3.setParam(KNNModel.ParameterKeys.K.name(),"3");
		this.threeNN = new KNNModel(param3, simpleDistFcn);
		this.threeNN.train(traindata);
	}
	
	@Test
	public void test1NNWithCompetitor(){
		List<Sample> testSamples = new ArrayList<Sample>();
		testSamples.add(new Sample("bb","r1",4.0));
		testSamples.add(new Sample("bbbbb","r1",2.0));
		RealDataset testDataset = new RealDataset(testSamples);
		List<Range> ranges = new ArrayList<Range>();
		ranges.add(new Range(0,testSamples.size()));
		Dataset testdata = new Dataset(testDataset, ranges);
		List<Label> outputs = this.oneNN.test(testdata);
		for(int i=0;i<outputs.size();i++){
			Assert.assertEquals(testSamples.get(i).getLabel().getRating(), outputs.get(i).getRating(),0.000001);
		}	
	}
	
	@Test
	public void test1NNWithoutCompetitor(){
		List<Sample> testSamples = new ArrayList<Sample>();
		testSamples.add(new Sample("bb","r2",2.5));
		testSamples.add(new Sample("bbbbb","r3",3.0));
		RealDataset testDataset = new RealDataset(testSamples);
		List<Range> ranges = new ArrayList<Range>();
		ranges.add(new Range(0,testSamples.size()));
		Dataset testdata = new Dataset(testDataset, ranges);
		List<Label> outputs = this.oneNN.test(testdata);
		for(int i=0;i<outputs.size();i++){
			Assert.assertEquals(testSamples.get(i).getLabel().getRating(), outputs.get(i).getRating(),0.000001);
		}	
	}
	
	@Test
	public void test1NNWithoutNeighbor(){
		List<Sample> testSamples = new ArrayList<Sample>();
		testSamples.add(new Sample("bb","r5",3.25));
		RealDataset testDataset = new RealDataset(testSamples);
		List<Range> ranges = new ArrayList<Range>();
		ranges.add(new Range(0,testSamples.size()));
		Dataset testdata = new Dataset(testDataset, ranges);
		List<Label> outputs = this.oneNN.test(testdata);
		for(int i=0;i<outputs.size();i++){
			Assert.assertEquals(testSamples.get(i).getLabel().getRating(), outputs.get(i).getRating(),0.000001);
		}	
	}
	
	@Test
	public void testKNNWithCompetitor(){
		List<Sample> testSamples = new ArrayList<Sample>();
		testSamples.add(new Sample("bb","r1",4.0));
		testSamples.add(new Sample("bbb","r1",11.0/3));
		testSamples.add(new Sample("bbbbb","r1",11.0/3));
		RealDataset testDataset = new RealDataset(testSamples);
		List<Range> ranges = new ArrayList<Range>();
		ranges.add(new Range(0,testSamples.size()));
		Dataset testdata = new Dataset(testDataset, ranges);
		List<Label> outputs = this.threeNN.test(testdata);
		for(int i=0;i<outputs.size();i++){
			Assert.assertEquals(testSamples.get(i).getLabel().getRating(), outputs.get(i).getRating(),0.000001);
		}	
	}
	
	@Test
	public void testKNNWithIncompleteNeighbor(){
		List<Sample> testSamples = new ArrayList<Sample>();
		testSamples.add(new Sample("bb","r2",2.5));
		testSamples.add(new Sample("bbb","r3",3.0));
		RealDataset testDataset = new RealDataset(testSamples);
		List<Range> ranges = new ArrayList<Range>();
		ranges.add(new Range(0,testSamples.size()));
		Dataset testdata = new Dataset(testDataset, ranges);
		List<Label> outputs = this.threeNN.test(testdata);
		for(int i=0;i<outputs.size();i++){
			Assert.assertEquals(testSamples.get(i).getLabel().getRating(), outputs.get(i).getRating(),0.000001);
		}	
	}
	
	@Test
	public void testKNNWithoutNeighbor(){
		List<Sample> testSamples = new ArrayList<Sample>();
		testSamples.add(new Sample("bb","r5",3.25));
		RealDataset testDataset = new RealDataset(testSamples);
		List<Range> ranges = new ArrayList<Range>();
		ranges.add(new Range(0,testSamples.size()));
		Dataset testdata = new Dataset(testDataset, ranges);
		List<Label> outputs = this.threeNN.test(testdata);
		for(int i=0;i<outputs.size();i++){
			Assert.assertEquals(testSamples.get(i).getLabel().getRating(), outputs.get(i).getRating(),0.000001);
		}	
	}
	
	//distance between two users = abs(name1.length-name2.length)
	private static class DudDistanceFunction implements UserDistanceFunction{
		@Override
		public void init(Dataset dataset, Parameters params) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public double getDistance(String user1, String user2) {
			return Math.abs(user1.length()-user2.length());
		}
		
	}
}
