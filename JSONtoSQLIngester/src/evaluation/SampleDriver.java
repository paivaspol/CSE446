package evaluation;

import java.util.ArrayList;
import java.util.List;
import model.Parameters;
import model.RidgeRegressionModel;
import config.JoeConfig;
import data.Dataset;
import data.Range;
import data.RealDataset;

/**
 * toy-class to run random stuff
 */
public class SampleDriver {
	public static void main(String[] args){
		RealDataset overallData = new RealDataset(JoeConfig.REALDATA_DIR);
		List<Range> ranges = new ArrayList<Range>();
		ranges.add(new Range(1,5));
		ranges.add(new Range(7,10));
		Dataset toyData = new Dataset(overallData, ranges);
		Parameters param = new Parameters();
		param.setParam(RidgeRegressionModel.ParameterKeys.RIDGE_PENALTY.name(), 10.0);
		RidgeRegressionModel model = new RidgeRegressionModel(param);		
		model.train(toyData);
		
	}
}
