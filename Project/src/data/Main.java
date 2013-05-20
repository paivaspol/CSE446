package data;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import com.google.gson.Gson;

public class Main {
	
	private static final String[] FILENAME = {
		"yelp_academic_dataset_business.json",
		"yelp_academic_dataset_user.json",
		"yelp_academic_dataset_review.json"
	};
	
	private static SQLiteConnection conn;
	
	public static void main(String[] args) throws Throwable {
		conn = new SQLiteConnection(new File("data.db"));
		conn.open(true);
		/* 
		 * 1. deserialize the JSON into the target object
		 * 2. dump the serialized data into sqlite
		 */
//		List<Business> businessList = deserializeBusiness(new Scanner(new File(realPath(FILENAME[0]))));
//		dumpBusinessDataIntoSQLite(businessList);
//		dumpUserDataIntoSQLite(deserializeUser(new Scanner(new File(realPath(FILENAME[1])))));
		dumpReviewDataIntoSQLite(deserializeReview(new Scanner(new File(realPath(FILENAME[2])))));
		System.out.println("DONE!");
	}
	
	private static List<Business> deserializeBusiness(Scanner input) {
		Gson gson = new Gson();
		List<Business> result = new LinkedList<Business>();
		while (input.hasNext()) {
			String jsonStr = input.nextLine();
			Business b = gson.fromJson(jsonStr, Business.class);
			result.add(b);
		}
		return result;
	}
	
	private static void dumpBusinessDataIntoSQLite(List<Business> objects) throws SQLiteException {
		String createTableQuery = "CREATE TABLE Business(type text, business_id text, name text, full_address text, stars real, review_count int)";
		conn.exec(createTableQuery);
		for (Business business : objects) {
			SQLiteStatement statement = conn.prepare("INSERT INTO Business VALUES(?, ?, ?, ?, ?, ?)");
			statement.bind(1, business.getType())
					.bind(2, business.getBusiness_id())
					.bind(3, business.getName())
					.bind(4, business.getFull_address())
					.bind(5, business.getStars())
					.bind(6, business.getReview_count());
			statement.step();
		}
	}
	
	private static List<User> deserializeUser(Scanner input) {
		Gson gson = new Gson();
		List<User> result = new LinkedList<User>();
		while (input.hasNext()) {
			String jsonStr = input.nextLine();
			User b = gson.fromJson(jsonStr, User.class);
			result.add(b);
		}
		return result;
	}
	
	private static void dumpUserDataIntoSQLite(List<User> objects) throws SQLiteException {
		String createTableQuery = "CREATE TABLE User(user_id text, name text, average_stars real, review_count int)";
		conn.exec(createTableQuery);
		for (User user : objects) {
			SQLiteStatement statement = conn.prepare("INSERT INTO User VALUES(?, ?, ?, ?)");
			statement.bind(1, user.getUser_id())
					.bind(2, user.getName())
					.bind(3, user.getAverage_stars())
					.bind(4, user.getReview_count());
			statement.step();
		}
	}
	
	private static List<Review> deserializeReview(Scanner input) {
		Gson gson = new Gson();
		List<Review> result = new LinkedList<Review>();
		while (input.hasNext()) {
			String jsonStr = input.nextLine();
			Review b = gson.fromJson(jsonStr, Review.class);
			result.add(b);
		}
		return result;
	}
	
	private static void dumpReviewDataIntoSQLite(List<Review> objects) throws SQLiteException {
		String createTableQuery = "CREATE TABLE Review(business_id text, user_id text, stars real, review_text text, date text)";
		conn.exec(createTableQuery);
		for (Review review : objects) {
			SQLiteStatement statement = conn.prepare("INSERT INTO Review VALUES(?, ?, ?, ?, ?)");
			statement.bind(1, review.getBusiness_id())
					.bind(2, review.getUser_id())
					.bind(3, review.getStars())
					.bind(4, review.getText())
					.bind(5, review.getDate());
			statement.step();
		}
	}

	
	private static String realPath(String filename) {
		return "../yelp_phoenix_academic_dataset/" + filename;
	}

}
