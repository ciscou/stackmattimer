package com.rubikaz.cisco.android.stackmattimer;

import java.util.ArrayList;
import java.util.HashMap;

public class StackMatSessionTimes {
	public StackMatSessionTimes() {
		// this.date = new Date();
		times = new ArrayList<StackMatTime>();
	}

	public void add(StackMatTime time) {
		times.add(time);
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("time", time.toString());
		hm.put("scramble", time.getScramble());
		hms.add(5, hm);
	}

	public void recalculateTime(int idx) {
		StackMatTime time = times.get(times.size() - idx - 1 + 5);
		HashMap<String, String> hm = hms.get(idx);
		hm.put("time", time.toString());
		hm.put("scramble", time.getScramble());
	}

	public void deleteTime(int idx) {
		times.remove(times.size() - idx - 1 + 5);
		hms.remove(idx);
	}

	public void calculateAvgs() {
		avg = avg(times.size());
		// avg5 = avg(5);
		// avg12 = avg(12);
		ravg5 = ravg(5);
		ravg12 = ravg(12);
		//if (avg5 < bavg5)
		//	bavg5 = avg5;
		//if (avg12 < bavg12)
		//	bavg12 = avg12;
		if (ravg5 < bravg5)
			bravg5 = ravg5;
		if (ravg12 < bravg12)
			bravg12 = ravg12;

		hms.get(0).put("time", new StackMatTime(ravg5).toString());
		hms.get(1).put("time", new StackMatTime(ravg12).toString());
		hms.get(2).put("time", new StackMatTime(bravg5).toString());
		hms.get(3).put("time", new StackMatTime(bravg12).toString());
		hms.get(4).put("time", new StackMatTime(avg).toString());
	}

	// public long getAvg5() {
	//	return avg5;
	// }

	//public long getAvg12() {
	//	return avg12;
	//}

	public long getRAvg5() {
		return ravg5;
	}

	public long getRAvg12() {
		return ravg12;
	}

	public ArrayList<HashMap<String, String>> getTimes() {
		if (hms == null)
			hms = new ArrayList<HashMap<String, String>>();
		
		avg = StackMatTime.NA;
		ravg5 = StackMatTime.NA;
		ravg12 = StackMatTime.NA;
		bravg5 = StackMatTime.NA;
		bravg12 = StackMatTime.NA;
		
		HashMap<String, String> hm1 = new HashMap<String, String>();
		hm1.put("time", new StackMatTime(ravg5).toString());
		hm1.put("scramble", "Rolling average 5");
		hms.add(hm1);
		HashMap<String, String> hm2 = new HashMap<String, String>();
		hm2.put("time", new StackMatTime(ravg12).toString());
		hm2.put("scramble", "Rolling average 12");
		hms.add(hm2);
		HashMap<String, String> hm3 = new HashMap<String, String>();
		hm3.put("time", new StackMatTime(bravg5).toString());
		hm3.put("scramble", "Best rolling average 5");
		hms.add(hm3);
		HashMap<String, String> hm4 = new HashMap<String, String>();
		hm4.put("time", new StackMatTime(bravg12).toString());
		hm4.put("scramble", "Best rolling average 12");
		hms.add(hm4);
		HashMap<String, String> hm5 = new HashMap<String, String>();
		hm5.put("time", new StackMatTime(avg).toString());
		hm5.put("scramble", "Session average");
		hms.add(hm5);
		
		return hms;
	}

	public StackMatTime getTime(int idx) {
		StackMatTime time = times.get(times.size() - idx - 1 + 5);
		return time;
	}

	public int getNTimes() {
		return times.size();
	}

	private long avg(int n) {
		if (times.size() < n)
			return StackMatTime.NA;
		long total_time = 0;
		for (int i = times.size() - n; i < times.size(); i++) {
			long time = times.get(i).getTime();
			if (time == StackMatTime.DNF)
				return StackMatTime.DNF;
			total_time += time;
		}
		return total_time / n;
	}

	private long ravg(int n) {
		if (times.size() < n)
			return StackMatTime.NA;
		long total_time = 0;
		long worst_time = Long.MIN_VALUE;
		long best_time = Long.MAX_VALUE;
		boolean any_dnf = false;
		for (int i = times.size() - n; i < times.size(); i++) {
			long time = times.get(i).getTime();
			if (time == StackMatTime.DNF) {
				if (any_dnf) {
					return StackMatTime.DNF;
				} else {
					any_dnf = true;
				}
			} else {
				if (time > worst_time)
					worst_time = time;
				if (time < best_time)
					best_time = time;
			}
			total_time += time;
		}
		if (!any_dnf)
			total_time -= worst_time;
		total_time -= best_time;
		return total_time / (n - 2);
	}

	private ArrayList<StackMatTime> times = null;
	// private Date date;

	private long avg;
	// private long avg5;
	// private long avg12;
	private long ravg5;
	private long ravg12;
	// private long bavg5;
	// private long bavg12;
	private long bravg5;
	private long bravg12;

	private ArrayList<HashMap<String, String>> hms = null;
}
