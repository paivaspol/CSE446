package model;

import data.Dataset;
import data.FeatureValues;

/**
 * Used for calculating distances between two users
 * @author sjonany
 */
public interface UserDistanceFunction {
	//precompute some stuff, where dataset is the collection of data points we have at hand
	public void init(Dataset dataset, Parameters params);
	//higher distance means the less related the two users are
	public double getDistance(String user1, String user2);
}
