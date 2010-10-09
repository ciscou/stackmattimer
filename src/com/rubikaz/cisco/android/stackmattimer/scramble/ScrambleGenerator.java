package com.rubikaz.cisco.android.stackmattimer.scramble;

public class ScrambleGenerator {
	public static String nextScramble(String puzzle_type) {
		if ("3x3x3".equals(puzzle_type))
			return ScrambleGenerator3x3x3.getInstance().nextScramble();
		else if ("2x2x2".equals(puzzle_type))
			return ScrambleGenerator2x2x2.getInstance().nextScramble();
		else
			return "";
	}
}
