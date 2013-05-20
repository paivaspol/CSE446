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

	public Dataset(RealDataset realDataset) {
		this(realDataset, null);
	}
	
	//assumes that ranges are SORTED and NON-INTERSECTING, ranges are end-exclusive
	public Dataset(RealDataset realDataset, List<Range> ranges) {
		this.realDataset = realDataset;
		this.ranges = ranges;
		if (ranges != null) {
			this.size = computeSize();
		} else {
			this.size = realDataset.getSize();
		}
		resetIterator();
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
		return curRangeIndex != ranges.size();
	}

	@Override
	public Sample next() {
		//precondition: curSampleIndex point to the next element to return
		if(!hasNext()){
			throw new IllegalArgumentException("No more element to return");
		}
		
		Sample ans = realDataset.getSampleAtIndex(curSampleIndex);
		curSampleIndex++;
		Range curRange = ranges.get(curRangeIndex);
		if(curRange.getTo() == curSampleIndex){
			curRangeIndex++;
			if(curRangeIndex != ranges.size())
				curSampleIndex = ranges.get(curRangeIndex).getFrom();
		}
		return ans;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		// do nothing
	}

	public int getSize() {
		return this.size;
	}
	
	/**
	 * resets the iterator
	 */
	public void resetIterator() {
		curRangeIndex = 0;
		if (ranges != null) {
			curSampleIndex = ranges.get(0).getFrom();
		}
	}
	
}
