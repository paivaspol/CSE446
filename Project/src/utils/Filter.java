package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;

import data.Business;
import data.Review;

public class Filter {

	private static final String REVIEW_FILENAME = "/Users/paivaspol/Dropbox/work/UW/Spring2013/CSE446/Project/CSE446/yelp_phoenix_academic_dataset/yelp_academic_dataset_review.json";
	private static final String BUSINESS_FILENAME = "/Users/paivaspol/Dropbox/work/UW/Spring2013/CSE446/Project/CSE446/yelp_phoenix_academic_dataset/yelp_academic_dataset_business.json";
	private static final String OUT_FILENAME = "review_pruned.out";
	
	public static void main(String[] args) throws Throwable {
		PrintStream out = new PrintStream(new File(OUT_FILENAME));
		BufferedReader reviewReader = new BufferedReader(new FileReader(new File(REVIEW_FILENAME)));
		BufferedReader businessReader = new BufferedReader(new FileReader(new File(BUSINESS_FILENAME)));
		Gson gson = new Gson();
		Set<String> businessMap = new HashSet<String>();
		String ln;
		while ((ln = businessReader.readLine()) != null) {
			Business bus = gson.fromJson(ln, Business.class);
			List<String> cat = bus.getCategories();
			for (String c : cat) {
				if (c.equalsIgnoreCase("Restaurants")) {
					businessMap.add(bus.getBusiness_id());
					break;
				}
			}
		}
		System.out.println("Total Restaurants: " + businessMap.size());
		int counter = 0;
		while ((ln = reviewReader.readLine()) != null) {
			Review review = gson.fromJson(ln, Review.class);
			String bid = review.getBusiness_id();
			if (businessMap.contains(bid)) {
				out.println(ln);
				counter++;
			}
		}
		System.out.println("Total reviews: " + counter);
	}
	
}
