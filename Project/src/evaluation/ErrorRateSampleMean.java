package evaluation;

import java.io.File;
import java.io.PrintStream;

import model.SampleMeanModel;
import data.RealDataset;

public class ErrorRateSampleMean {

	private static final String FILENAME = "data.train";
	private static final int kFold = 5;

	public static void main(String[] args) throws Throwable {
		RealDataset realDataset = new RealDataset(FILENAME);
		PrintStream out = new PrintStream(new File("lambda.log"));
		long startTime = System.currentTimeMillis();
		out.println("Sample Mean");
		out.print("\t");
		CrossValidation cv = new CrossValidation(new SampleMeanModel(), realDataset, kFold);
		double result = cv.crossValidate();
		out.println("error = " + result);
		long endTime = System.currentTimeMillis();
		out.close();
		System.out.println("Elapsed time = " + (endTime - startTime) + "ms.");

	}

}
