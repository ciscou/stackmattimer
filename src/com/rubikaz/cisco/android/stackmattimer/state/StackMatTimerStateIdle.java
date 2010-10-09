package com.rubikaz.cisco.android.stackmattimer.state;

import com.rubikaz.cisco.android.stackmattimer.StackMatTimer;

public class StackMatTimerStateIdle extends StackMatTimerState {
	public StackMatTimerStateIdle(StackMatTimer stackMatTimer, long time) {
		super(stackMatTimer);
		
		stackMatTimer.setGreenLed(false);
		stackMatTimer.setRedLed(false);
		stackMatTimer.setLcd(time);
		
		stackMatTimer.saveTime(time);
		stackMatTimer.showTimes();
	}

	@Override
	public void onResetButtonClicked() {
		stackMatTimer.displayNextScramble();
		stackMatTimer.setState(new StackMatTimerStateOn(stackMatTimer));
	}
	
	@Override
	public void onSensorDown() {
		stackMatTimer.setRedLed(true);
	}

	@Override
	public void onSensorUp() {
		stackMatTimer.setRedLed(false);
	}

	@Override
	public void tick() {
	}
}
