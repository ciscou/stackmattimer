package com.rubikaz.cisco.android.stackmattimer.state;

import com.rubikaz.cisco.android.stackmattimer.StackMatTimer;

import android.os.SystemClock;

public class StackMatTimerStateRunning extends StackMatTimerState {
	public StackMatTimerStateRunning(StackMatTimer stackMatTimer) {
		super(stackMatTimer);
		
		stackMatTimer.setGreenLed(false);
		stackMatTimer.setRedLed(false);
		elapsed_time = 0;
		stackMatTimer.setLcd(elapsed_time);
		stackMatTimer.hideScramble();
		
		startTime = SystemClock.uptimeMillis();
	}

	@Override
	public void onSensorDown() {
		elapsed_time = SystemClock.uptimeMillis() - startTime;
		stackMatTimer.showScramble();
		stackMatTimer.setState(new StackMatTimerStateIdle(stackMatTimer, elapsed_time));
	}

	@Override
	public void onSensorUp() {
	}

	@Override
	public void tick() {
		elapsed_time = SystemClock.uptimeMillis() - startTime;
		stackMatTimer.setLcd(elapsed_time);
		
		b = (elapsed_time % 100) < 50;
		stackMatTimer.setGreenLed(b);
		stackMatTimer.setRedLed(!b);
	}
	
	private long startTime;
	private long elapsed_time;
	boolean b;
}
