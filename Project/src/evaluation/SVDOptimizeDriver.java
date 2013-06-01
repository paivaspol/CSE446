package evaluation;

import java.io.File;
import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;

import model.Parameters;
import model.SVDModel;
import config.JoeConfig;
import data.RealDataset;

public class SVDOptimizeDriver {
	static final int NUM_FOLD = 2;
	public static void main(String[] args) throws Exception{		
		RealDataset realDataset = new RealDataset(JoeConfig.TRAIN_DIR);
		Map<Double, Double> errorRate = new TreeMap<Double, Double>();
		PrintStream out = new PrintStream(new File("svd.log"));
		double bestLambda = -1;
		double minErrorRate = Double.MAX_VALUE;
		long startTime = System.currentTimeMillis();
		for (double lambda : new double[]{0.01/*,0.02,0.03,0.04,0.05,0.06,0.07,0.08,0.08,0.1*/}) {
			out.println("lambda = " + lambda);
			out.print("\t");
			Parameters p = new Parameters();
			p.setParam(SVDModel.ParameterKeys.LAMBDA.name(), "" + lambda);
			p.setParam(SVDModel.ParameterKeys.NUM_FEATURES.name(), "" + 100);
			p.setParam(SVDModel.ParameterKeys.LEARNING_RATE.name(), "" + 0.1);
			CrossValidation cv = new CrossValidation(new SVDModel(p), realDataset, NUM_FOLD);
			double result = cv.crossValidate();
			out.println("error = " + result);
			errorRate.put(lambda, result);
			
			if (result < minErrorRate) {
				bestLambda = lambda;
				minErrorRate = result;
			}
			out.flush();
		}
		long endTime = System.currentTimeMillis();
		out.close();
		System.out.println(errorRate);
		System.out.println("Best Lambda = " + bestLambda + ", with error rate of " + minErrorRate);
		System.out.println("Elapsed time = " + (endTime - startTime) + "ms.");
	}
}
