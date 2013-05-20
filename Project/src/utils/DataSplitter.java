package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class DataSplitter {
	
	private static final int numRoundRobin = 5;
	private static final String FILENAME = "/Users/paivaspol/Dropbox/work/UW/Spring2013/CSE446/Project/CSE446/yelp_phoenix_academic_dataset/yelp_academic_dataset_review.json";
	private static final String TEST_FILENAME = "data.test";
	private static final String TRAIN_FILENAME = "data.train";
	
	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(FILENAME)));
		String ln;
		int counter = 0;
		PrintStream outTest = new PrintStream(new File(TEST_FILENAME));
		PrintStream outTrain = new PrintStream(new File(TRAIN_FILENAME));
		while ((ln = reader.readLine()) != null) {
			if (counter % numRoundRobin == 0) {
				outTest.println(ln);
			} else {
				outTrain.println(ln);
			}
			counter++;
		}
		reader.close();
		outTest.close();
		outTrain.close();
	}

}
