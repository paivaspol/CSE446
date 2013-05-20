package data;

public class Sample {

	// in (x,y) notation, x = featureVals, Label = label
	private FeatureValues featureVals;
	private Label label;

	public Sample(String userId, String businessId, double rating) {
		this.featureVals = new FeatureValues(userId, businessId);
		this.label = new Label(rating);
	}

	public FeatureValues getFeatureValues() {
		return featureVals;
	}

	public Label getLabel() {
		return label;
	}

	@Override
	public String toString(){
		return featureVals.toString() + " -> " + label.toString();
	}
}