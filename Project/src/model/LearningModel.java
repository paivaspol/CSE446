package model;

import java.util.List;

import data.Dataset;
import data.Label;

/**
 * 
 * @author sjonany
 */
public interface LearningModel {
	/**
	 * trains learning model based on the data set
	 * this is CUMULATIVE, so calling train() successively on the model causes to the model 
	 * to accumulate its experience
	 * @param data
	 */
	public void train(Dataset data);
	
	/**
	 * should be called after train
	 * Get the predicted resuls from the given dataset, without
	 * using the labels in data
	 * @param data Dataset with same inputs, but predicted outputs replaced the original output
	 * @return labels with order corresponding the the the order of input in dataset
	 */
	public List<Label> test(Dataset data);
	
	/**
	 * reset the model and forget the experience learnt from any training
	 */
	public void reset();
}
