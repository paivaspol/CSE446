package evaluation;

import java.io.File;
import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;

import model.KernelRegression;
import model.Parameters;
import model.ScaledManhattanDistanceFunction;
import data.RealDataset;

public class FindBestRhoForKernelRegression {
	
	private static final String FILENAME = "data.train";
	private static final double FIRST_RHO = 3.8;
	private static final double MAX_RHO = 5.0;
	private static final double RHO_STEP = 0.2;
	private static final int kFold = 5;
	
	public static void main(String[] args) throws Throwable {
		RealDataset realDataset = new RealDataset(FILENAME);
		Map<Double, Double> errorRate = new TreeMap<Double, Double>();
		PrintStream out = new PrintStream(new File("lambda.log"));
		double minLambda = -1;
		double minErrorRate = Double.MAX_VALUE;
		long startTime = System.currentTimeMillis();
		for (double i = FIRST_RHO; i <= MAX_RHO; i += RHO_STEP) {
			out.println("lambda = " + i);
			out.print("\t");
			Parameters param = new Parameters();
			param.setParam(KernelRegression.ParameterKeys.rho.name(), String.valueOf(i));
			CrossValidation cv = new CrossValidation(new KernelRegression(param, new ScaledManhattanDistanceFunction()), realDataset, kFold);
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
