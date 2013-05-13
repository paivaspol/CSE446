package model;

import data.Dataset;
import data.FeatureValues;

/**
 * Used for calculating distances between two points for nearest-neighbor approaches
 * @author sjonany
 */
public interface DistanceFunction {
	//precompute some stuff, where dataset is the collection of data points we have at hand
	public void init(Dataset dataset, Parameters params);
	//higher distance means the less related the two points are
	public double getDistance(FeatureValues p1, FeatureValues p2);
}
