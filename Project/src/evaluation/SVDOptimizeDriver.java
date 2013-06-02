package evaluation;

import java.io.File;
import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;

import model.Parameters;
import model.SVDModel;
import model.SVDModel2;
import config.JoeConfig;
import data.RealDataset;

public class SVDOptimizeDriver {
	static final int NUM_FOLD = 3;
	public static void main(String[] args) throws Exception{		
		RealDataset realDataset = new RealDataset(JoeConfig.TRAIN_DIR);
		Map<Double, Double> errorRate = new TreeMap<Double, Double>();
		PrintStream out = new PrintStream(new File("svd.log"));
		double bestLambda = -1;
		double minErrorRate = Double.MAX_VALUE;
		long startTime = System.currentTimeMillis();
		for(int numFeatures : new int[]{100,200,300,400,500}){
			for (double lambda : new double[]{0.01,0.05,0.1}) {
				out.println("lambda = " + lambda);
				out.println("num features = " + numFeatures);
				out.print("\t");
				Parameters p = new Parameters();
				p.setParam(SVDModel.ParameterKeys.LAMBDA.name(), "" + lambda);
				p.setParam(SVDModel.ParameterKeys.NUM_FEATURES.name(), "" + numFeatures);
				CrossValidation cv = new CrossValidation(new SVDModel2(p), realDataset, NUM_FOLD);
				double result = cv.crossValidate();
				out.println("error = " + result);
				errorRate.put(lambda, result);
				
				if (result < minErrorRate) {
					bestLambda = lambda;
					minErrorRate = result;
				}
				out.flush();
			}
		}
		long endTime = System.currentTimeMillis();
		out.close();
		System.out.println(errorRate);
		System.out.println("Best Lambda = " + bestLambda + ", with error rate of " + minErrorRate);
		System.out.println("Elapsed time = " + (endTime - startTime) + "ms.");
	}
}
