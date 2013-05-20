package model;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import model.ExpectedDifferenceDistanceFunction;
import model.Parameters;
import org.junit.Before;
import org.junit.Test;
import data.Dataset;
import data.Range;
import data.Rating;
import data.RealDataset;
import data.Sample;

//Tests ScaledManhattanDistanceFunction
public class ExpectedDifferenceDistanceTest{
	private ExpectedDifferenceDistanceFunction expectedDifferenceFcn;
	
	@Before
	public void setup(){
		List<Sample> trainSamples = new ArrayList<Sample>();
		trainSamples.add(new Sample("a","r1",2.0));
		trainSamples.add(new Sample("a","r2",2.5));
		trainSamples.add(new Sample("b","r1",3.0));
		trainSamples.add(new Sample("b","r2",3.5));
		trainSamples.add(new Sample("c","r2",4.0));
		trainSamples.add(new Sample("d","r2",4.5));
		trainSamples.add(new Sample("d","r3",5.0));
		trainSamples.add(new Sample("e","r3",2.0));
		trainSamples.add(new Sample("e","r4",4.0));
		RealDataset traindataset = new RealDataset(trainSamples);
		List<Range> ranges = new ArrayList<Range>();
		ranges.add(new Range(0,trainSamples.size()));
	
		Dataset traindata = new Dataset(traindataset, ranges);

		this.expectedDifferenceFcn = new ExpectedDifferenceDistanceFunction();
		this.expectedDifferenceFcn.init(traindata, new Parameters());
	}

	@Test
	public void testCalcProbExistRating(){
		for(Rating r: Rating.values()){
			double prob = expectedDifferenceFcn.calcProbability("a", "r1", r);
			if(r.equals(Rating.TWO)){
				Assert.assertEquals(1.0, prob, 0.001);
			}else{
				Assert.assertEquals(0.0, prob, 0.001);
			}
		}
	}
	

	@Test
	public void testCalcProbExistRestaurant(){
		for(Rating r: Rating.values()){
			double prob = expectedDifferenceFcn.calcProbability("a", "r3", r);
			if(r.equals(Rating.FIVE)){
				Assert.assertEquals(0.5, prob, 0.001);
			}else if(r.equals(Rating.TWO)){
				Assert.assertEquals(0.5, prob, 0.001);
			}else{
				Assert.assertEquals(0.0, prob, 0.001);
			}
		}
	}
	
	@Test
	public void testCalcProbNotExistRestaurant(){
		for(Rating r: Rating.values()){
			double prob = expectedDifferenceFcn.calcProbability("a", "r6", r);
			if(r.equals(Rating.TWO)){
				Assert.assertEquals(2.0/9, prob, 0.001);
			}else if(r.equals(Rating.TWO_POINT_FIVE)){
				Assert.assertEquals(1.0/9, prob, 0.001);
			}else if(r.equals(Rating.THREE)){
				Assert.assertEquals(1.0/9, prob, 0.001);
			}else if(r.equals(Rating.THREE_POINT_FIVE)){
				Assert.assertEquals(1.0/9, prob, 0.001);
			}else if(r.equals(Rating.FOUR)){
				Assert.assertEquals(2.0/9, prob, 0.001);
			}else if(r.equals(Rating.FOUR_POINT_FIVE)){
				Assert.assertEquals(1.0/9, prob, 0.001);
			}else if(r.equals(Rating.FIVE)){
				Assert.assertEquals(1.0/9, prob, 0.001);
			}else{
				Assert.assertEquals(0.0, prob, 0.001);
			}
		}
	}
	
	@Test
	public void testFullOverlapUser(){
		Assert.assertEquals(3.5,this.expectedDifferenceFcn.getDistance("a", "b"), 0.00001);
	}
	
	@Test
	public void testPartialOverlapUser(){
		Assert.assertEquals(4.0,this.expectedDifferenceFcn.getDistance("a", "d"), 0.00001);
	}
	

	@Test
	public void testSubsetOverlapUser(){
		Assert.assertEquals(3.5,this.expectedDifferenceFcn.getDistance("a", "c"), 0.00001);
	}
	
	
	@Test
	public void testNoOverlapUser(){
		Assert.assertEquals(3.125,this.expectedDifferenceFcn.getDistance("a", "e"), 0.00001);
	}
	
	@Test
	public void testOneInexistentUser(){
		Assert.assertEquals(3.125,this.expectedDifferenceFcn.getDistance("a", "f"), 0.00001);
	}
	
	@Test
	public void testBothInexistentUsers(){
		Assert.assertEquals(2.8125,this.expectedDifferenceFcn.getDistance("h", "f"), 0.00001);
	}
}
