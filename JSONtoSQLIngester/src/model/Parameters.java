package model;

import java.util.HashMap;
import java.util.Map;


/**
 * the params in learning model that should be obtained thru cross validation
 * @author sjonany
 */
public class Parameters {
	private Map<String, String> params;
	public Parameters(){
		params = new HashMap<String, String>();
	}
	
	public void setParam(String key, String val){
		params.put(key,  val);
	}
	
	public String getParam(String key){
		return params.get(key);
	}
}
