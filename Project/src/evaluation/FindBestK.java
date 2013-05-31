package evaluation;

import java.io.File;
import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;

import model.KNNModel;
import model.Parameters;
import model.RidgeRegressionModel;
import model.ScaledManhattanDistanceFunction;
import model.UserDistanceFunction;
import data.Dataset;
import data.RealDataset;

public class FindBestK {
	
	private static final String FILENAME = "data.train";
	private static final int maxLambda = 25;
	private static final int lambdaStep = 5;
	private static final int kFold = 5;
	
	public static void main(String[] args) throws Throwable {
		RealDataset realDataset = new RealDataset(FILENAME);
		Map<Integer, Double> errorRate = new TreeMap<Integer, Double>();
		PrintStream out = new PrintStream(new File("lambda.log"));
		int minLambda = -1;
		double minErrorRate = Double.MAX_VALUE;
		long startTime = System.currentTimeMillis();
		for (int i = 5; i <= maxLambda; i += lambdaStep) {
			out.println("lambda = " + i);
			out.print("\t");
			Parameters param = new Parameters();
			param.setParam(KNNModel.ParameterKeys.K.name(), String.valueOf(i));
			CrossValidation cv = new CrossValidation(new KNNModel(param, new ScaledManhattanDistanceFunction()), realDataset, kFold);
			double result = cv.crossValidate();
			out.println("error = " + result);
			errorRate.put(i, result);
			minErrorRate = Math.min(result, minErrorRate);
			if (Double.compare(minErrorRate, result) == 0) {
				minLambda = i;
			}
		}
		long endTime = System.currentTimeMillis();
		out.close();
		System.out.println(errorRate);
		System.out.println("Min Lambda = " + minLambda + ", with error rate of " + minErrorRate);
		System.out.println("Elapsed time = " + (endTime - startTime) + "ms.");
	}
}
