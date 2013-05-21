package evaluation;

import java.util.ArrayList;
import java.util.List;

import model.ExpectedDifferenceDistanceFunction;
import model.KNNModel;
import model.Parameters;
import model.ScaledManhattanDistanceFunction;
import config.JoeConfig;
import data.Dataset;
import data.Label;
import data.Range;
import data.RealDataset;

/**
 * toy-class to run random stuff
 */
public class SampleDriver {
	
	public static final String PAI_PATH = "/Users/paivaspol/Dropbox/work/UW/Spring2013/CSE446/Project/CSE446/yelp_phoenix_academic_dataset/yelp_academic_dataset_review.json";
	
	public static void main(String[] args){
		RealDataset overallData = new RealDataset(JoeConfig.REALDATA_DIR);
		List<Range> ranges = new ArrayList<Range>();
		//note: with 20,000 ratings, seems to take 9 seconds, 
		//~9 times for 10,000 ratings for each training
		//makes sense, since matrix inversion =  O(n^3)
		ranges.add(new Range(0,20000));
		Dataset toyData = new Dataset(overallData, ranges);
		
		for(int lambda = 5; lambda <= 100; lambda++){
			Parameters param = new Parameters();
			
			param.setParam(KNNModel.ParameterKeys.K.name(), "10");
			KNNModel model = new KNNModel(param, new ExpectedDifferenceDistanceFunction());
			
			/*
			param.setParam(RidgeRegressionModel.ParameterKeys.RIDGE_PENALTY.name(), ""+1.0*lambda);
			RidgeRegressionModel model = new RidgeRegressionModel(param);		
			*/
			model.train(toyData);
			List<Label> preds = model.test(toyData);
			System.out.println(lambda + " -> " + EvaluationUtils.calcAvgSSE(preds, toyData));
		}
	}
}
