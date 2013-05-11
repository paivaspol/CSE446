package data;

import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author pie :D should just be rows of (x,y), where y is the label
 *         corresponding to x, the input in our case, x = (restaurant, user), y
 *         = rating of user to that restaurant because of how we define the
 *         features, it's enough to just use restaurant id and user id so x=
 *         (thaiTom.id=1234,pai.id = 1234 ), y = 3.5 But if we have time and
 *         want to do cooler features, I guess we have to expand x
 */
public class Dataset implements Iterator<Sample> {

	private List<Range> ranges;
	private int curRangeIndex;
	private int curSampleIndex;
	private RealDataset realDataset;
	private int size;

	public Dataset(RealDataset realDataset, List<Range> ranges) {
		resetIterator();
		this.realDataset = realDataset;
		this.ranges = ranges;
		this.size = computeSize();
	}
	
	private int computeSize() {
		int result = 0;
		for (Range range : ranges) {
			result += range.getTo() - range.getFrom();
		}
		return result;
	}

	@Override
	public boolean hasNext() {
		if (curSampleIndex >= ranges.get(curRangeIndex).getTo()
				&& curRangeIndex < ranges.size()) {
			curSampleIndex = 0;
			curRangeIndex++;
		}
		return curRangeIndex < ranges.size()
				&& curSampleIndex < ranges.get(curRangeIndex).getTo();
	}

	@Override
	public Sample next() {
		return realDataset.getSampleAtIndex(curSampleIndex);
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		// do nothing
	}

	public int getSize() {
		return this.size;
	}
	
	public void resetIterator() {
		curRangeIndex = 0;
		curSampleIndex = 0;
	}
	
}
