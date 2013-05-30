package data;
import java.util.Arrays;

/**
 * wrapper over matrix. just a 1 d vector with a nice toString
 */
public class Vector{
	private double[] arr;
	private double l2NormSq;
	public Vector(double[] arr){
		this.arr = arr;
		this.l2NormSq = -1;
	}
	
	public double dotProduct(Vector other){
		double res = 0;
		for(int i=0;i<arr.length;i++){
			res += arr[i] * other.arr[i];
		}
		return res;
	}

	public Vector times(double scale){
		double[] res = new double[arr.length];
		for(int i=0;i<res.length;i++){
			res[i] = arr[i] * scale;
		}
		return new Vector(res);
	}

	public Vector plus(Vector other){
		double[] res = new double[arr.length];
		for(int i=0;i<res.length;i++){
			res[i] = arr[i] + other.arr[i];
		}
		return new Vector(res);
	}

	public Vector minus(Vector other){
		double[] res = new double[arr.length];
		for(int i=0;i<res.length;i++){
			res[i] = arr[i] - other.arr[i];
		}
		return new Vector(res);
	}

	public static double getMaxAbsDifference(Vector v1, Vector v2){
		double result = Double.MIN_VALUE;
		for(int col=0; col<v1.arr.length; col++){
			result = Math.max(Math.abs(v1.arr[col] - v2.arr[col]), result);
		}
		return result;
	}

	public double[] toArray(){
		return arr.clone();
	}
	
	public Vector abs(){
		double[] res = new double[arr.length];
		for(int i=0;i<res.length;i++){
			res[i] = Math.abs(arr[i]);
		}
		return new Vector(res);
	}
	
	public boolean containsNan(){
		for(double d : arr)
			if(Double.isNaN(d) || Double.isInfinite(d))
				return true;
	
		return false;
	}
	
	@Override
	public String toString(){
		return Arrays.toString(toArray());
	}
	
	public double getL2NormSq(){
		if(l2NormSq <0){
			l2NormSq = 0;
			for(int i=0;i<arr.length;i++){
				l2NormSq += arr[i] * arr[i];
			};
		}
		return l2NormSq;
	}
}