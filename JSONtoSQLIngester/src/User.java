
public class User {

	private String name;
	private int review_count;
	private double average_stars;
	
	private String user_id;
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getReview_count() {
		return review_count;
	}
	public void setReview_count(int review_count) {
		this.review_count = review_count;
	}
	public double getAverage_stars() {
		return average_stars;
	}
	public void setAverage_stars(double average_stars) {
		this.average_stars = average_stars;
	}

}
