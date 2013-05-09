package evaluation;

import java.util.ArrayList;
import java.util.List;

import data.Review;

/**
 * driver to do cross validation
 * TODO:
 */
public class CrossValidation {
	//the K in K-fold cross valdation
	private static int FOLD_COUNT = 10;
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
