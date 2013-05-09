package data;

import java.util.List;

/**
 * 
 * @author pie
 * :D
 * should just be rows of (x,y), where y is the label corresponding to x, the input
 * in our case, x = (restaurant, user), y = rating of user to that restaurant
 * because of how we define the features, it's enough to just use restaurant id and user id
 * so x= (thaiTom.id=1234,pai.id = 1234 ), y = 3.5
 * But if we have time and want to do cooler features, I guess we have to expand x
 */
public class Dataset {
	private List<Sample> samples;
	//maybe an iteration method over all samples? so training/testing is easy?
	//lol, too lazy. might have to replace this?
	//also, I think we might need to keep a sample id? so it's easy to map back predictions
	//to a particular sample, idk, might not be necessary
	public List<Sample> getSamples(){
		return samples;
	}
	
	//idk, change this to whatever, this is just a stub
	public static class Sample{
		//TODO: idk, maybe we need this
		//and make data set a map , key = sampleid, val = sample
		private int sampleId;
		
		//in (x,y) notation, x = featureVals, Label = label
		private FeatureValues featureVals;
		private Label label;
		
		public Sample(FeatureValues fVals, Label label){
			this.featureVals = fVals;
			this.label = label;
		}
		
		public FeatureValues getFeatureValues(){
			return featureVals;
		}
		
		public Label getLabel(){
			return label;
		}
		
	}
	
	public static class FeatureValues{
		private int restaurantId;
		private int userId;
		
		public FeatureValues(int rId, int uId){
			this.restaurantId = rId;
			this.userId = uId;
		}
		
		public int getRestaurantId(){
			return restaurantId;
		}
		
		public int getUserId(){
			return userId;
		}
	}
	
	public static class Label{
		private double rating;
		public Label(double rating){
			this.rating = rating;
		}
		
		public double getRating(){
			return rating;
		}
	}
}
