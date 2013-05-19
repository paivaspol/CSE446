package junit;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import model.Parameters;
import model.ScaledManhattanDistanceFunction;

import org.junit.Before;
import org.junit.Test;

import data.Dataset;
import data.Range;
import data.RealDataset;
import data.Sample;

//Tests ScaledManhattanDistanceFunction
public class ScaledMDTest {
	private ScaledManhattanDistanceFunction mdScaledFcn;
	
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

		this.mdScaledFcn = new ScaledManhattanDistanceFunction();
		this.mdScaledFcn.init(traindata, new Parameters());
	}
	
	@Test
	public void testFullOverlapUser(){
		Assert.assertEquals(1.0,this.mdScaledFcn.getDistance("a", "b"), 0.00001);
	}
	
	@Test
	public void testPartialOverlapUser(){
		Assert.assertEquals(2.0,this.mdScaledFcn.getDistance("a", "d"), 0.00001);
	}
	

	@Test
	public void testSubsetOverlapUser(){
		Assert.assertEquals(1.5,this.mdScaledFcn.getDistance("a", "c"), 0.00001);
	}
	
	
	@Test
	public void testNoOverlapUser(){
		Assert.assertEquals(ScaledManhattanDistanceFunction.DEFAULT_DISTANCE,this.mdScaledFcn.getDistance("a", "e"), 0.00001);
	}
	
	@Test
	public void testOneInexistentUser(){
		Assert.assertEquals(ScaledManhattanDistanceFunction.DEFAULT_DISTANCE,this.mdScaledFcn.getDistance("a", "f"), 0.00001);
	}
	
	@Test
	public void testBothInexistentUsers(){
		Assert.assertEquals(ScaledManhattanDistanceFunction.DEFAULT_DISTANCE,this.mdScaledFcn.getDistance("h", "f"), 0.00001);
	}
}
