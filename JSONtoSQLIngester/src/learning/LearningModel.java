package learning;

import data.Dataset;

/**
 * 
 * @author sjonany
 */
public interface LearningModel {
	/**
	 * trains learning model based on the data set
	 * @param data
	 */
	public void train(Dataset data);
	
	/**
	 * should be called after train
	 * Get the predicted resuls from the given dataset, without
	 * using the labels in data
	 * @param data Dataset with same inputs, but predicted outputs replaced the original output
	 * @return
	 */
	public Dataset test(Dataset data);
}
