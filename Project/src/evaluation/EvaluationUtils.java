package evaluation;

import java.util.List;

import data.Dataset;
import data.Label;

/**
 * A list of utility methods for performing some evaluation metrics
 */
public class EvaluationUtils {
	/**
	 * @param predictions the predictions made on the dataset
	 * @param dataset the input (x,y) pairs on which predictions are made 
	 * @return sum squared error
	 */
	public static double calcAvgSSE(List<Label> predictions, Dataset dataset){
		dataset.resetIterator();
		double SSE = 0.0;
		int i=0;
		while(dataset.hasNext()){
			double trueR = dataset.next().getLabel().getRating();
			double predR = predictions.get(i).getRating();
			SSE += Math.pow(trueR-predR, 2);
			i++;
		}
		
		return SSE/i;
	}
}
