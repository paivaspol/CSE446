package evaluation;

import model.Parameters;
import model.RidgeRegressionModel;

import org.junit.BeforeClass;
import org.junit.Test;

import data.RealDataset;

public class CrossValidationTest {

	private static final String FILENAME = "dummy.txt";
	
	private static RealDataset realDataset;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		realDataset = new RealDataset(FILENAME);
	}

	@Test
	public void test() {
		int k = 6;
		Parameters param = new Parameters();
		param.setParam(RidgeRegressionModel.ParameterKeys.RIDGE_PENALTY.name(), "0.5");
		CrossValidation cv = new CrossValidation(new RidgeRegressionModel(param), realDataset, k);
		double result = cv.crossValidate();
	}

}
