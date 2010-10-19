package com.rubikaz.cisco.android.stackmattimer;

import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

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
		avg = avg(times.size(), 0);

		ravg5 = ravg(5, 0);
		ravg12 = ravg(12, 0);

		bravg5 = Long.MAX_VALUE;
		for(int i=0; i<times.size(); i++) {
			long r = ravg(5, i);
			if(r < bravg5) bravg5 = r;
			bravg5_offset = i - 5 + 1;
		}
		if(times.isEmpty()) bravg5 = StackMatTime.NA;
		
		bravg12 = Long.MAX_VALUE;
		for(int i=0; i<times.size(); i++) {
			long r = ravg(12, i);
			if(r < bravg12) bravg12 = r;
			bravg12_offset = i - 12 + 1;
		}
		if(times.isEmpty()) bravg12 = StackMatTime.NA;

		hms.get(0).put("time", new StackMatTime(avg).toString());
		hms.get(0).put("scramble", "All " + String.valueOf(getNTimes()) + " solves average");
		hms.get(1).put("time", new StackMatTime(ravg5).toString());
		hms.get(2).put("time", new StackMatTime(ravg12).toString());
		hms.get(3).put("time", new StackMatTime(bravg5).toString());
		hms.get(4).put("time", new StackMatTime(bravg12).toString());
	}

	public long getAvg() {
		return avg;
	}
	
	public long getRAvg5() {
		return ravg5;
	}

	public long getRAvg12() {
		return ravg12;
	}

	public long getBRAvg5() {
		return bravg5;
	}

	public long getBRAvg12() {
		return bravg12;
	}
	
	public int getBRAvg5Offset() {
		return bravg5_offset;
	}

	public int getBRAvg12Offset() {
		return bravg12_offset;
	}
	
	public ArrayList<HashMap<String, String>> getTimes() {
		if (hms == null)
			hms = new ArrayList<HashMap<String, String>>();
		
		avg = StackMatTime.NA;
		ravg5 = StackMatTime.NA;
		ravg12 = StackMatTime.NA;
		bravg5 = StackMatTime.NA;
		bravg12 = StackMatTime.NA;
		
		HashMap<String, String> hm0 = new HashMap<String, String>();
		hm0.put("time", new StackMatTime(avg).toString());
		hm0.put("scramble", "Session average");
		hms.add(hm0);
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
		
		return hms;
	}

	public StackMatTime getTime(int idx) {
		StackMatTime time = times.get(times.size() - idx - 1 + 5);
		return time;
	}

	public int getNTimes() {
		return times.size();
	}

	private long avg(int n, int offset) {
		if (times.size() - offset <= 0 || times.size() - offset < n)
			return StackMatTime.NA;
		long total_time = 0;
		for (int i = times.size() - offset - n; i < times.size() - offset; i++) {
			long time = times.get(i).getTime();
			if (time == StackMatTime.DNF)
				return StackMatTime.DNF;
			total_time += time;
		}
		return total_time / n;
	}

	private long ravg(int n, int offset) {
		Log.d(getClass().getSimpleName(), "1 calculating ravg(" + String.valueOf(n) + ")...");		
		
		if (times.size() - offset <= 0 || times.size() - offset < n)
			return StackMatTime.NA;
		long total_time = 0;
		long worst_time = Long.MIN_VALUE;
		long best_time = Long.MAX_VALUE;
		boolean any_dnf = false;
		for (int i = times.size() - offset - n; i < times.size() - offset; i++) {
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
				total_time += time;
			}
		}
		
		Log.d(getClass().getSimpleName(), "2 calculating ravg(" + String.valueOf(n) + "): " + new StackMatTime(total_time / (n - 2)).toString());
		
		Log.d(getClass().getSimpleName(), "3 calculating ravg(" + String.valueOf(n) + "): " + String.valueOf(any_dnf));
		if (!any_dnf)
			total_time -= worst_time;
		
		Log.d(getClass().getSimpleName(), "4 calculating ravg(" + String.valueOf(n) + "): " + new StackMatTime(total_time / (n - 2)).toString());
		
		total_time -= best_time;
		
		Log.d(getClass().getSimpleName(), "5 calculating ravg(" + String.valueOf(n) + "): " + new StackMatTime(total_time / (n - 2)).toString());
		
		return total_time / (n - 2);
	}

	private ArrayList<StackMatTime> times = null;
	// private Date date;

	private long avg;
	private long ravg5;
	private long ravg12;
	private long bravg5;
	private long bravg12;
	
	private int bravg5_offset;
	private int bravg12_offset;
	
	private ArrayList<HashMap<String, String>> hms = null;
}
