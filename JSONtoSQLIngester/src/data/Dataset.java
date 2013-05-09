package data;

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
	//maybe an iteration method over all samples? so training/testing is easy?
	
	public static class Sample{
		private int restaurantId;
		private int userId;
		private double rating;
		//bla23
		//or maybe should make it explicit and just have two variables : input, and label
		//so have to make 2 more classes?
	}
}
