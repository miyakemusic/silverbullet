package obsolute.register;

public class RangeGetter {
	private Integer start;
	private Integer stop;

	public RangeGetter(String bits) {
		bits = bits.replace("]", "").replace("[", "");
		if (bits.contains(":")) {
			start = Integer.valueOf(bits.split(":")[1]);
			stop = Integer.valueOf(bits.split(":")[0]);
		}
		else {
			start = Integer.valueOf(bits.replace("[", "").replace("]", ""));
			stop = -1;
		}
	}

	public Integer getStart() {
		return start;
	}

	public Integer getStop() {
		return stop;
	}
	
	public boolean isRange() {
		return stop != -1;
	}
}