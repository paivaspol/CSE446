package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class DataSplitter {
	
	private static final int numRoundRobin = 5;
	private static final String FILENAME = "review_pruned.txt";
	private static final String TEST_FILENAME = "data.test";
	private static final String TRAIN_FILENAME = "data.train";
	
	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(FILENAME)));
		String ln;
		int testCounter = 0;
		int trainCounter = 0;
		int counter = 0;
		PrintStream outTest = new PrintStream(new File(TEST_FILENAME));
		PrintStream outTrain = new PrintStream(new File(TRAIN_FILENAME));
		while ((ln = reader.readLine()) != null) {
			if (counter % numRoundRobin == 0) {
				outTest.println(ln);
				testCounter++;
			} else {
				outTrain.println(ln);
				trainCounter++;
			}
			counter++;
		}
		System.out.println("num train: " + trainCounter);
		System.out.println("num test: " + testCounter);
		reader.close();
		outTest.close();
		outTrain.close();
	}

}
