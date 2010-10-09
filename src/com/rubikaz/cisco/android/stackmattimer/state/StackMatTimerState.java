package com.rubikaz.cisco.android.stackmattimer.state;

import com.rubikaz.cisco.android.stackmattimer.StackMatTimer;

public abstract class StackMatTimerState {
	public StackMatTimerState(StackMatTimer stackMatTimer) {
		this.stackMatTimer = stackMatTimer;
	}
	
	public void onPowerButtonClicked() {
		stackMatTimer.endSession();
	}

	public void onResetButtonClicked() {
		stackMatTimer.setState(new StackMatTimerStateOn(stackMatTimer));
	}
	
	public abstract void onSensorDown();
    
    public abstract void onSensorUp();
    
    public abstract void tick();
	
	protected StackMatTimer stackMatTimer;
}
