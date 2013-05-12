package data;

/**
 * 0-based, to is exclusive
 * Range(0, 5) -> [0, 1, 2, 3, 4]
 * @author pai
 */
public class Range {

	private int from, to;

	public Range(int from, int to) {
		this.from = from;
		this.to = to;
	}
	
	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}
}
