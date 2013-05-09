package learning;

import data.Dataset;

/**
 * Predicts by using linear regression, 
 * y_pred = average_restaurant_rating + user_bias + restaurant_bias 
 * @author sjonany
 */
public class LinearRegressor implements LearningModel{
	public enum ParameterKeys{USER_BIAS_WEIGHT, RESTAURANT_BIAS_WEIGHT};
	
	private double userBiasWeight;
	private double restBiasWeight;
	
	public LinearRegressor(Parameters p){
		this.userBiasWeight = p.getParam(ParameterKeys.USER_BIAS_WEIGHT.name());
		this.restBiasWeight = p.getParam(ParameterKeys.RESTAURANT_BIAS_WEIGHT.name());
	}
	
	@Override
	public void train(Dataset data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Dataset test(Dataset data) {
		// TODO Auto-generated method stub
		return null;
	}
}
