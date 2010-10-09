package com.rubikaz.cisco.android.stackmattimer.state;

import com.rubikaz.cisco.android.stackmattimer.StackMatTimer;

public class StackMatTimerStateOn extends StackMatTimerState {
	public StackMatTimerStateOn(StackMatTimer stackMatTimer) {
		super(stackMatTimer);
		
		stackMatTimer.setGreenLed(false);
		stackMatTimer.setRedLed(false);
		stackMatTimer.setLcd(0);
		
		stackMatTimer.hideTimes();
	}

	@Override
	public void onSensorDown() {
		stackMatTimer.setState(new StackMatTimerStateWaitingForGreenLed(stackMatTimer));
	}

	@Override
	public void onSensorUp() {
	}

	@Override
	public void tick() {
	}
}
