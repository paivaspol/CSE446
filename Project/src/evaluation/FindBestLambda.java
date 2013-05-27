package evaluation;

import java.io.File;
import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;

import model.Parameters;
import model.RidgeRegressionModel;
import data.RealDataset;

public class FindBestLambda {

	private static final String FILENAME = "data.train";
	private static final int maxLambda = 100000;
	private static final int k = 5;
	
	public static void main(String[] args) throws Throwable {
		RealDataset realDataset = new RealDataset(FILENAME);
		Map<Integer, Double> errorRate = new TreeMap<Integer, Double>();
		PrintStream out = new PrintStream(new File("lambda.log"));
		int minLambda = -1;
		double minErrorRate = Double.MAX_VALUE;
		long startTime = System.currentTimeMillis();
		int[] lambdas = { 0, 10000, 20000, 30000, 40000, 50000, 60000, 70000, 80000, 90000, 100000, 110000, 120000, 130000, 140000, 150000 };
		for (int lambda : lambdas) {
			out.println("lambda = " + lambda);
			out.print("\t");
			Parameters param = new Parameters();
			param.setParam(RidgeRegressionModel.ParameterKeys.RIDGE_PENALTY.name(), String.valueOf(lambda));
			CrossValidation cv = new CrossValidation(new RidgeRegressionModel(param), realDataset, k);
			double result = cv.crossValidate();
			out.println("error = " + result);
			errorRate.put(lambda, result);
			minErrorRate = Math.min(result, minErrorRate);
			if (Double.compare(minErrorRate, result) == 0) {
				minLambda = lambda;
			}
			out.flush();
		}
		long endTime = System.currentTimeMillis();
		out.close();
		System.out.println(errorRate);
		System.out.println("Min Lambda = " + minLambda + ", with error rate of " + minErrorRate);
		System.out.println("Elapsed time = " + (endTime - startTime) + "ms.");
	}

}
