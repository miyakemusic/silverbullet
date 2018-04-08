package jp.silverbullet.dependency.speceditor3;

public class RangeChecker {

	private boolean underRange;
	private boolean overRange;

	public RangeChecker(String min, String max, String v) {
		Double dmin = Double.valueOf(min);
		Double dmax = Double.valueOf(max);
		try {
			Double dv = Double.valueOf(v);
			underRange = dv < dmin; 
			overRange =  dv > dmax;
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	public boolean isSatisfied() {
		return !overRange && !underRange;
	}

	public boolean isUnderRange() {
		return underRange;
	}

	public boolean isOverRange() {
		return overRange;
	}

}
