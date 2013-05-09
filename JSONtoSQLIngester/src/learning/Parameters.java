package learning;

import java.util.HashMap;
import java.util.Map;


/**
 * the params in learning model that should be obtained thru cross validation
 * @author sjonany
 */
public class Parameters {
	private Map<String, Double> params;
	public Parameters(){
		params = new HashMap<String, Double>();
	}
	
	public void setParam(String key, Double val){
		params.put(key,  val);
	}
	
	public double getParam(String key){
		return params.get(key);
	}
}
