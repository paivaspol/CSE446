package junit;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import model.Parameters;
import model.RidgeRegressionModel;

import org.junit.Test;

import data.Dataset;
import data.Label;
import data.Range;
import data.RealDataset;
import data.Sample;

public class RidgeRegressionModelTest {
	@Test
	public void testUnregularizedRegression() {
		Parameters param = new Parameters();
		param.setParam(RidgeRegressionModel.ParameterKeys.RIDGE_PENALTY.name(), "0");
		RidgeRegressionModel model = new RidgeRegressionModel(param);
		List<Sample> samples = new ArrayList<Sample>();
		samples.add(new Sample("u1","r1",1.0));
		samples.add(new Sample("u2","r2",3.0));
		samples.add(new Sample("u3","r3",5.0));
		RealDataset realdata = new RealDataset(samples);
		List<Range> ranges = new ArrayList<Range>();
		ranges.add(new Range(0,samples.size()));
		Dataset data = new Dataset(realdata, ranges);
		
		model.train(data);
		List<Label> predictions = model.test(data);
		System.out.println(predictions);
	}
}
