package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;

public class RealDataset {

	private List<Sample> samples;

	public RealDataset(String filename) {
		samples = new LinkedList<Sample>();
		try {
			ingestDataset(filename);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	public RealDataset(List<Sample> samples){
		this.samples = samples;
	}

	private void ingestDataset(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
		Gson gson = new Gson();
		String line = reader.readLine();
		while (line != null) {
			Review review = gson.fromJson(line, Review.class);
			Sample entry = new Sample(review.getUser_id(),
					review.getBusiness_id(), review.getStars());
			samples.add(entry);
			line = reader.readLine();
		}
		reader.close();
	}
	
	public Sample getSampleAtIndex(int index) {
		return samples.get(index);
	}
	
	public int getSize() {
		return samples.size();
	}
	
	/**
	 * Returns a list of range. The first range will be the specified range. The rest is the range(s) of the remaining data.
	 * @param from inclusive
	 * @param to exclusive
	 * @return
	 */
	public List<Range> split(int from, int to) {
		List<Range> result = new ArrayList<Range>();
		result.add(new Range(from, to));
		if (from != 0) {
			result.add(new Range(0, from));
		}
		if (to != samples.size()) {
			result.add(new Range(to, samples.size()));
		}
		return result;
	}

}
