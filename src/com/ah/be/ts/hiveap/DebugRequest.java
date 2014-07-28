package com.ah.be.ts.hiveap;

public interface DebugRequest extends Debug {

	int getGroupId();

	String getSessionId();

	void setSessionId(String sessionId);

	DebugState getDebugState();

	/**
	 * Initiate HiveAP debug process. Returns the cookie of debug.
	 *
	 * @return A qualified integer used to indicate an unique debug.
	 * @throws DebugException If fails to send debug initiation request to HiveAP.
	 */
	int initiate() throws DebugException;

	/**
	 * Terminate HiveAP debug process. Returns the sequence number of the debug termination request.
	 *
	 * @return A qualified integer representing the sequence number of a request used for terminating a specified HiveAP debug process.
	 * @throws DebugException If fails to send this debug termination request to HiveAP.
	 */
	int terminate() throws DebugException;

	/**
	 * Replace the current debug state with the new one given as argument. In fact, whether or not needs replacing is totally driven by the debug state machine. The replacement won't be executed if it is judged to be illegal.
	 *
	 * @param newState new state to instead of the current debug state.
	 * @return the actual current debug state after replacement.
	 */
	DebugState changeState(DebugState newState);

	/**
	 * @return the name of debug.
	 */
	String getName();

}