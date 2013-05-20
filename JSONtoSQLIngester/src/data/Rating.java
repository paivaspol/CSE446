package data;


/**
 * useful for comparing between different real user ratings, or when using ratings as keys
 */
public enum Rating {
	ZERO(0.0), ZERO_POINT_FIVE(0.5),
	ONE(1.0), ONE_POINT_FIVE(1.5),
	TWO(2.0), TWO_POINT_FIVE(2.5),
	THREE(3.0), THREE_POINT_FIVE(3.5),
	FOUR(4.0), FOUR_POINT_FIVE(4.5),
	FIVE(5.0);

	private double val;
	private Rating(double val){
		this.val = val;
	}

	public double getRatingValue(){
		return val;
	}
	
	@Override
	public String toString(){
		return "" + val;
	}
	
	public static Rating valueOf(double rating){
		if(rating < 0.0 || rating > 5.0){
			throw new IllegalArgumentException("Invalid rating = " + rating);
		}
		
		Rating closestRating = ZERO;
		for(Rating r : Rating.values()){
			if(Math.abs(rating-r.val) < Math.abs(rating - closestRating.val)){
				closestRating = r;
			}
		}
		return closestRating;
	}
}
