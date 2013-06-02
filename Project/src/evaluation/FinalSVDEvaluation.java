package evaluation;

import java.io.File;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import model.LearningModel;
import model.Parameters;
import model.RidgeRegressionModel;
import model.SVDModel;
import model.SVDModel2;
import config.JoeConfig;
import data.Dataset;
import data.Label;
import data.RealDataset;

public class FinalSVDEvaluation {
	public static void main(String[] args) throws Exception{		
		Dataset trainSet = new Dataset(new RealDataset(JoeConfig.TRAIN_DIR));
		Dataset testSet = new Dataset(new RealDataset(JoeConfig.TEST_DIR));
		
		long startTime = System.currentTimeMillis();
		Parameters p = new Parameters();
		p.setParam(SVDModel.ParameterKeys.LAMBDA.name(), "" + 0.03);
		p.setParam(SVDModel.ParameterKeys.NUM_FEATURES.name(), "" + 300);
		LearningModel model = new SVDModel2(p);
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
