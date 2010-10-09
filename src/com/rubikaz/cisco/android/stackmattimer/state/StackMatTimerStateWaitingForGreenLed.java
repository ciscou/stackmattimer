package com.rubikaz.cisco.android.stackmattimer.state;

import com.rubikaz.cisco.android.stackmattimer.StackMatTimer;

import android.os.SystemClock;

public class StackMatTimerStateWaitingForGreenLed extends StackMatTimerState {
	public StackMatTimerStateWaitingForGreenLed(StackMatTimer stackMatTimer) {
		super(stackMatTimer);
		
		stackMatTimer.setGreenLed(false);
		stackMatTimer.setRedLed(true);
		stackMatTimer.setLcd(0);
		
		startTime = SystemClock.uptimeMillis();
	}

	@Override
	public void onSensorDown() {
	}

	@Override
	public void onSensorUp() {
		stackMatTimer.setState(new StackMatTimerStateOn(stackMatTimer));
	}

	@Override
	public void tick() {
		long elapsed_time = SystemClock.uptimeMillis() - startTime;
		if(elapsed_time > 1000) {
			stackMatTimer.setState(new StackMatTimerStateReady(stackMatTimer));
		}
	}
	
	private long startTime;
}
