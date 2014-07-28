package com.ah.be.ts.hiveap;

public class DebugState {

	public enum State {
		UNINITIATED,
		INITIATION_REQUESTED,
		INITIATION_RESPONSED,
		INITIATION_FAILED,
	//	TERMINATION_REQUESTED,
	//	TERMINATION_RESPONSED,
	//	TERMINATION_FAILED,
		FINISHED,
		ABORTED,
		STOPPED
	}

	private State state = State.UNINITIATED;

	private String description = "";

	public DebugState() {
		
	}

	public DebugState(State state) {
		this.state = state;
	}

	public DebugState(State state, String description) {
		this.state = state;
		this.description = description;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "State: " + state + "; Description: " + description;
	}

}