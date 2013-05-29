package evaluation;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import model.ExpectedDifferenceDistanceFunction;
import model.KNNModel;
import model.LearningModel;
import model.Parameters;
import model.ScaledManhattanDistanceFunction;
import config.JoeConfig;
import data.Dataset;
import data.Range;
import data.RealDataset;

//compares the performance of two distance functions with increasing number of training examples
public class DistanceFcnComparisonEvaluation {
	static final int K = 100;
	static final int REP = 10;
	static final int TEST_SIZE = 20000;
	static final int INIT_TRAIN_SIZE = 5000;
	static final int END_TRAIN_SIZE = 50000;
	static final int INCR_TRAIN_SIZE = 5000;
	
	public static void main(String[] args) throws Throwable {
		RealDataset realDataset = new RealDataset(JoeConfig.TRAIN_DIR);
		PrintStream out = new PrintStream(new File("distanceFcn.log"));
		PrintStream outVerbose = new PrintStream(new File("distanceFcnVerbose.log"));
		Parameters p = new Parameters();
		p.setParam(KNNModel.ParameterKeys.K.name(), "" + K);
		LearningModel md = new KNNModel(p, new ScaledManhattanDistanceFunction());
		LearningModel emd = new KNNModel(p, new ExpectedDifferenceDistanceFunction());
		
		int[] numTrainArr = new int[]{100,500,1000,5000,10000,25000,50000};

		//measure time
		for (int numTrain : numTrainArr) {
			double mdTime = randomEval(md, numTrain, TEST_SIZE, realDataset).runtimeTest;
			double emdTime = randomEval(emd, numTrain, TEST_SIZE, realDataset).runtimeTest;
			outVerbose.println(numTrain + " , " + mdTime + " , " + emdTime);
			outVerbose.flush();
			System.out.println(numTrain);
		}
		
		//measure accuracy
		
		for (int numTrain : numTrainArr) {
			double mdMSE = 0.0;
			double emdMSE = 0.0;
			for(int i=1;i<=REP;i++){
				double curMdMSE = randomEval(md, numTrain, TEST_SIZE, realDataset).MSE;
				double curEmdMSE = randomEval(emd, numTrain, TEST_SIZE, realDataset).MSE;
				outVerbose.println("numTrain = " + numTrain +", REP = " + i + ", MD = " + curMdMSE + " , EMD = " + curEmdMSE);
				outVerbose.flush();
				System.out.println("numTrain = " + numTrain +", REP = " + i + ", MD = " + curMdMSE + " , EMD = " + curEmdMSE);
				mdMSE += curMdMSE;
				emdMSE += curEmdMSE;
			}
			mdMSE /= REP;
			emdMSE /= REP;
			out.print(numTrain + " , " + mdMSE + " , " + emdMSE + "\n");
			out.flush();
		}
		outVerbose.close();
		out.close();
	}

	/**
	 * take random subsets from all data for training and testing, then return the MSE
	 * @return
	 */
	private static Result randomEval(LearningModel model, int numTrain, int numTest, RealDataset dataset){
		int[] indices = new int[dataset.getSize()];
		for(int i=0;i<indices.length;i++){
			indices[i] = i;
		}
		
		shuffleArray(indices);
		int[] trainIndices = new int[numTrain];
		int[] testIndices = new int[numTest];
		for(int i=0;i<numTrain;i++){
			trainIndices[i] = indices[i];
		}
		
		for(int i=0;i<numTest;i++){
			testIndices[i] = indices[i+numTrain];
		}
		Arrays.sort(trainIndices);
		Arrays.sort(testIndices);
		
		List<Range> testRanges = new ArrayList<Range>();
		List<Range> trainRanges = new ArrayList<Range>();

		for(int i=0;i<numTrain;i++){
			trainRanges.add(new Range(trainIndices[i], trainIndices[i]+1));
		}
		
		for(int i=0;i<numTest;i++){
			testRanges.add(new Range(testIndices[i], testIndices[i]+1));
		}
		
		Dataset trainData = new Dataset(dataset, trainRanges);
		Dataset testData = new Dataset(dataset, testRanges);
		Result result = new Result();
		model.reset();
		model.train(trainData);
		
		long startTime = System.currentTimeMillis();
		result.MSE = EvaluationUtils.calcAvgSSE(model.test(testData), testData);
		long endTime = System.currentTimeMillis();

		result.runtimeTest = endTime - startTime;
		return result;
	}

	static class Result{
		public double runtimeTest; //ms
		public double MSE;
	}
	
	private static void shuffleArray(int[] a) {
		int n = a.length;
		Random random = new Random();
		random.nextInt();
		for (int i = 0; i < n; i++) {
			int change = i + random.nextInt(n - i);
			swap(a, i, change);
		}
	}

	private static void swap(int[] a, int i, int change) {
		int helper = a[i];
		a[i] = a[change];
		a[change] = helper;
	}
}
