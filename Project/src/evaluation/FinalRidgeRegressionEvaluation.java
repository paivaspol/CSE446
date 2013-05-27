package evaluation;

import java.util.ArrayList;
import java.util.List;

import model.LearningModel;
import model.Parameters;
import model.RidgeRegressionModel;
import config.JoeConfig;
import data.Dataset;
import data.Label;
import data.RealDataset;

public class FinalRidgeRegressionEvaluation {

	//chosen by cross validating from training set
	private static final int BEST_LAMBDA = 60000;
	
	public static void main(String[] args) throws Throwable {
		Dataset trainSet = new Dataset(new RealDataset(JoeConfig.TRAIN_DIR));
		Dataset testSet = new Dataset(new RealDataset(JoeConfig.TEST_DIR));
		
		long startTime = System.currentTimeMillis();
		Parameters param = new Parameters();
		param.setParam(RidgeRegressionModel.ParameterKeys.RIDGE_PENALTY.name(), "" + BEST_LAMBDA);
		LearningModel model = new RidgeRegressionModel(param);
		System.out.println("Training...");
		model.train(trainSet);
		System.out.println("Testing...");
		List<Label> predictions = model.test(testSet);
		double MSE = EvaluationUtils.calcAvgSSE(predictions, testSet);
		long endTime = System.currentTimeMillis();
		System.out.println(MSE);
		System.out.println("Elapsed time = " + (endTime - startTime) + "ms.");
	}

}
