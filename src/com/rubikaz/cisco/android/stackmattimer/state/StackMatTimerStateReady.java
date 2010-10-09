package com.rubikaz.cisco.android.stackmattimer.state;

import com.rubikaz.cisco.android.stackmattimer.StackMatTimer;

public class StackMatTimerStateReady extends StackMatTimerState {
	public StackMatTimerStateReady(StackMatTimer stackMatTimer) {
		super(stackMatTimer);
		
		stackMatTimer.setGreenLed(true);
		stackMatTimer.setRedLed(false);
		stackMatTimer.setLcd(0);
	}

	@Override
	public void onSensorDown() {
	}

	@Override
	public void onSensorUp() {
		stackMatTimer.setState(new StackMatTimerStateRunning(stackMatTimer));
	}

	@Override
	public void tick() {
	}
}
