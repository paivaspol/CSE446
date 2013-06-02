package evaluation;

import java.util.List;

import model.ExpectedDifferenceDistanceFunction;
import model.KernelRegression;
import model.LearningModel;
import model.Parameters;
import model.ScaledManhattanDistanceFunction;
import data.Dataset;
import data.Label;
import data.RealDataset;

public class FinalKernelRegressionEvaluation {

	//chosen by cross validating from training set
	private static final double BEST_RHO = 3.8;

	public static void main(String[] args) throws Throwable {
		//List<Range> range = new ArrayList<Range>();
		//range.add(new Range(0,5));
		Dataset trainSet = new Dataset(new RealDataset("data.train"));
		Dataset testSet = new Dataset(new RealDataset("data.test"));

		long startTime = System.currentTimeMillis();
		
		Parameters param = new Parameters();
		param.setParam(KernelRegression.ParameterKeys.rho.name(), String.valueOf(BEST_RHO));
		LearningModel model = new KernelRegression(param, new ExpectedDifferenceDistanceFunction());
		System.out.println("Training...");
		model.train(trainSet);
		System.out.println("Testing... with ExpectedDifferenceDistanceFunction");
		List<Label> predictions = model.test(testSet);
		double MSE = EvaluationUtils.calcAvgSSE(predictions, testSet);
		System.out.println("MSE for expected MD = " + MSE);
		long endTime = System.currentTimeMillis();
		System.out.println("Elapsed time = " + (endTime - startTime) + "ms.");
		
		long startTime2 = System.currentTimeMillis();
		LearningModel model2 = new KernelRegression(param, new ScaledManhattanDistanceFunction());
		System.out.println("Training...");
		model2.train(trainSet);
		System.out.println("Testing... with ScaledManhattanDistanceFunction");
		List<Label> predictions2 = model2.test(testSet);
		double MSE2 = EvaluationUtils.calcAvgSSE(predictions2, testSet);
		System.out.println("MSE for expected MD = " + MSE2);
		long endTime2 = System.currentTimeMillis();
		System.out.println("Elapsed time = " + (endTime2 - startTime2) + "ms.");
	}

}