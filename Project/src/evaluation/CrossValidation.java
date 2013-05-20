package evaluation;

import java.util.ArrayList;
import java.util.List;

import model.LearningModel;

import data.Dataset;
import data.Label;
import data.Range;
import data.Review;

/**
 * driver to do cross validation
 * TODO:
 */
public class CrossValidation {

	//the K in K-fold cross valdation
	private static int FOLD_COUNT = 10;
	private final int k;
	private final Dataset dataset;
	private final LearningModel model;

	/**
	 * Constructs a cross validation object
	 * @param model
	 * @param dataset the training set
	 * @param k
	 */
	public CrossValidation(LearningModel model, Dataset dataset, int k) {
		this.dataset = dataset;
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
		int iteration = 0;
		initialize(0, sizePerChunk);  // so we get the correct size
		// for each of the validation set
		for (int i = 0; i < k; i++) {
			System.out.println("iteration: " + iteration);
			int startValidationRange = i * sizePerChunk;
			int endValidationRange = startValidationRange + sizePerChunk;
			initialize(startValidationRange, endValidationRange);
			model.train(dataset);
			List<Label> predictions = model.test(dataset);
			System.out.println(predictions);
			sumError += EvaluationUtils.calcAvgSSE(predictions, dataset);
			// move to the next chunk
			iteration++;
		}
		return sumError / k;
	}
	
	public void initialize(int from, int to) {
		System.out.println("from: " + from + ", to: " + to);
		dataset.split(new Range(from, to));
	}


	public static void main(String[] args){
		//TODO: this should be the training set, which we have split before hand
		List<Review> entireSet = new ArrayList<Review>();
		
		for(int k = 1; k <= FOLD_COUNT; k++){
			//split entire set 
			//train model on one part, testo n the other
			//get error
			//avg
		}
	}
}
