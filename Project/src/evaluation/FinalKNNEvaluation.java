package evaluation;

import java.util.ArrayList;
import java.util.List;

import model.ExpectedDifferenceDistanceFunction;
import model.KNNModel;
import model.LearningModel;
import model.Parameters;
import model.ScaledManhattanDistanceFunction;
import config.JoeConfig;
import data.Dataset;
import data.Label;
import data.Range;
import data.RealDataset;

public class FinalKNNEvaluation {

	//chosen by cross validating from training set
	private static final int BEST_K = 100;
	
	public static void main(String[] args) throws Throwable {
		//List<Range> range = new ArrayList<Range>();
		//range.add(new Range(0,5));
		Dataset trainSet = new Dataset(new RealDataset(JoeConfig.TRAIN_DIR));
		Dataset testSet = new Dataset(new RealDataset(JoeConfig.TEST_DIR));
		
		long startTime = System.currentTimeMillis();
		Parameters param = new Parameters();
		param.setParam(KNNModel.ParameterKeys.K.name(), "" + BEST_K);
		LearningModel model = new KNNModel(param, new ExpectedDifferenceDistanceFunction());
		System.out.println("Training...");
		model.train(trainSet);
		System.out.println("Testing...");
		List<Label> predictions = model.test(testSet);
		double MSE = EvaluationUtils.calcAvgSSE(predictions, testSet);
		System.out.println("MSE for expected MD = " + MSE);
		long endTime = System.currentTimeMillis();
		System.out.println("Elapsed time = " + (endTime - startTime) + "ms.");
	}

}
