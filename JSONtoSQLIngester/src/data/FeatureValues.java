package data;

public class FeatureValues {

	private String restaurantId;
	private String userId;

	public FeatureValues(String rId, String uId) {
		this.restaurantId = rId;
		this.userId = uId;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public String getUserId() {
		return userId;
	}
}