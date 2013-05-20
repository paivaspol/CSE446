package evaluation;

import static org.junit.Assert.*;

import model.Parameters;
import model.RidgeRegressionModel;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import data.Dataset;
import data.RealDataset;

public class CrossValidationTest {

	private static final String FILENAME = "dummy.txt";
	
	private static RealDataset realDataset;
	private Dataset dataset;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		realDataset = new RealDataset(FILENAME);
	}
	
	@Before
	public void setup() {
		dataset = new Dataset(realDataset);
	}
	

	@Test
	public void test() {
		int k = 6;
		Parameters param = new Parameters();
		param.setParam(RidgeRegressionModel.ParameterKeys.RIDGE_PENALTY.name(), 0.5);
		CrossValidation cv = new CrossValidation(new RidgeRegressionModel(param), dataset, k);
		double result = cv.crossValidate();
	}

}
