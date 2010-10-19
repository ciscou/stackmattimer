package com.rubikaz.cisco.android.stackmattimer;

public class StackMatTime {
	public StackMatTime(String scramble, long time) {
		this.scramble = scramble;
		this.time = time;
		// this.date = new Date();
	}
	
	public StackMatTime(long time) {
		this("", time);
	}

	public long getTime() {
		return isDNF() ? StackMatTime.DNF : time + (isPlus2() ? 2000 : 0);
	}

	public String getScramble() {
		return scramble;
	}

	public void toggleDNF() {
		is_dnf = !is_dnf;
	}

	public boolean isDNF() {
		return is_dnf;
	}
	
	public void togglePlus2() {
		is_plus_2 = !is_plus_2;
	}
	
	public boolean isPlus2() {
		return is_plus_2;
	}

	@Override
	public String toString() {
		if (time == StackMatTime.DNF || isDNF())
			return "DNF";
		if (time == StackMatTime.NA)
			return "N/A";

		long ms = time;
		long s = ms / 1000;
		long minutes = s / 60;
		long seconds = s - minutes * 60;
		long cs = ms / 10 - minutes * 60 * 100 - seconds * 100;

		String mm = String.valueOf(minutes);
		if (minutes < 10)
			mm = "0" + mm;
		String ss = String.valueOf(seconds);
		if (seconds < 10)
			ss = "0" + ss;
		String zz = String.valueOf(cs);
		if (cs < 10)
			zz = "0" + cs;

		return mm + ":" + ss + "." + zz + (isPlus2() ? " +2" : "");
	}
	
	public static final long DNF = Long.MAX_VALUE - 1;
	public static final long NA = Long.MAX_VALUE - 2;

	private String scramble;
	private long time;
	// private Date date;

	private boolean is_dnf = false;
	private boolean is_plus_2 = false;
}
