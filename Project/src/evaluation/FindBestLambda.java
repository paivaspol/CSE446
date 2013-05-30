package evaluation;

import java.io.File;
import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;

import model.ExpectedDifferenceDistanceFunction;
import model.KNNModel;
import model.Parameters;
import model.RidgeRegressionModel;
import data.RealDataset;

public class FindBestLambda {

	private static final String FILENAME = "data.train";
	private static final int maxLambda = 100000;
	private static final int lambdaStep = 1;
	private static final int k = 5;
	
	public static void main(String[] args) throws Throwable {
		RealDataset realDataset = new RealDataset(FILENAME);
		Map<Integer, Double> errorRate = new TreeMap<Integer, Double>();
		PrintStream out = new PrintStream(new File("svd.log"));
		int minLambda = -1;
		double minErrorRate = Double.MAX_VALUE;
		long startTime = System.currentTimeMillis();
		for (int kneighbor : new int[]{2,3}) {
			out.println("kneighbor = " + kneighbor);
			out.print("\t");
			Parameters param = new Parameters();
			param.setParam(KNNModel.ParameterKeys.K.name(), String.valueOf(kneighbor));
			CrossValidation cv = new CrossValidation(new KNNModel(param, new ExpectedDifferenceDistanceFunction()), realDataset, k);
			double result = cv.crossValidate();
			out.println("error = " + result);
			errorRate.put(kneighbor, result);
			minErrorRate = Math.min(result, minErrorRate);
			if (Double.compare(minErrorRate, result) == 0) {
				minLambda = kneighbor;
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
