package com.rubikaz.cisco.android.stackmattimer.state;

import com.rubikaz.cisco.android.stackmattimer.StackMatTimer;

public class StackMatTimerStateOff extends StackMatTimerState {
	public StackMatTimerStateOff(StackMatTimer stackMatTimer) {
		super(stackMatTimer);
		
		stackMatTimer.setGreenLed(false);
		stackMatTimer.setRedLed(false);
		stackMatTimer.setLcd(-1);
		
		stackMatTimer.finishCurrentSession();
	}

	@Override
	public void onPowerButtonClicked() {
		stackMatTimer.startNewSession();
		stackMatTimer.displayNextScramble();
		stackMatTimer.setState(new StackMatTimerStateOn(stackMatTimer));
	}

	@Override
	public void onResetButtonClicked() {
	}

	@Override
	public void onSensorDown() {
	}

	@Override
	public void onSensorUp() {
	}

	@Override
	public void tick() {
	}
}
