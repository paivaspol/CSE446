package evaluation;

import java.util.ArrayList;
import java.util.List;

import model.LearningModel;
import data.Dataset;
import data.Label;
import data.Range;
import data.RealDataset;

/**
 * driver to do cross validation
 * TODO:
 */
public class CrossValidation {

	//the K in K-fold cross valdation
	private final int k;
	private final RealDataset dataset;
	private final LearningModel model;

	/**
	 * Constructs a cross validation object
	 * @param model
	 * @param realDataset the training set
	 * @param k
	 */
	public CrossValidation(LearningModel model, RealDataset realDataset, int k) {
		this.dataset = realDataset;
		this.k = k;
		this.model = model;
	}

	/**
	 * returns the average error of the given model
	 * @return
	 */
	public double crossValidate() {
		System.out.println("Cross validating...");
		int sizePerChunk = dataset.getSize() / k; // got size
		double sumError = 0.0;
		dataset.split(new Range(0, sizePerChunk));
		// for each of the validation set
		for (int i = 0; i < k; i++) {
			int startValidationRange = i * sizePerChunk;
			int endValidationRange = startValidationRange + sizePerChunk;
			List<Range> ranges = dataset.split(new Range(startValidationRange, endValidationRange));
			
			// Generate the ranges
			List<Range> validationRangeList = new ArrayList<Range>();
			validationRangeList.add(ranges.get(0));
			List<Range> trainingRangeList = new ArrayList<Range>();
			for (int j = 1; j < ranges.size(); j++) {
				trainingRangeList.add(ranges.get(j));
			}
			Dataset validationSet = new Dataset(dataset, validationRangeList);
			Dataset trainingSet = new Dataset(dataset, trainingRangeList);
			printRanges(validationRangeList, trainingRangeList);
			
			// Do cv.
			System.out.println("training...");
			model.train(trainingSet);
			System.out.println("predicting...");
			List<Label> predictions = model.test(validationSet);
			System.out.println(predictions);
			sumError += EvaluationUtils.calcAvgSSE(predictions, validationSet);
			// move to the next chunk
		}
		return sumError / k;
	}

	private void printRanges(List<Range> validationRangeList,
			List<Range> trainingRangeList) {
		System.out.println("Validation Set Range:");
		System.out.println("\t" + validationRangeList);
		System.out.println("Training Set Range:");
		System.out.println("\t" + trainingRangeList);
	}
	
}
