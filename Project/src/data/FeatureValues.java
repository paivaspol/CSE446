package data;

public class FeatureValues {

	private String restaurantId;
	private String userId;

	public FeatureValues(String uId, String rId) {
		this.restaurantId = rId;
		this.userId = uId;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public String getUserId() {
		return userId;
	}
	
	@Override
	public String toString(){
		return "(userId = "  + userId + ", restaurantId = " + restaurantId +  ")";
	}
}