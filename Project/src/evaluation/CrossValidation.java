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
	 * @param dataset
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
		int sizePerChunk = dataset.getSize() / k;
		int startValidationRange = 0;
		int endValidationRange = startValidationRange + sizePerChunk;
		Range range = new Range(startValidationRange, endValidationRange + 1);
		double sumError = 0.0;
		// for each of the validation set
		while (startValidationRange < sizePerChunk) {
			dataset.split(range);
			model.train(dataset);
			List<Label> predictions = model.test(dataset);
			sumError += EvaluationUtils.calcAvgSSE(predictions, dataset);
			// move to the next chunk
			startValidationRange += sizePerChunk;
			endValidationRange += sizePerChunk;
			range = new Range(startValidationRange, endValidationRange);
		}
		return sumError / k;
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
