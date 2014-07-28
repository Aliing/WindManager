package com.ah.be.ts.hiveap;

public interface DebugMgmt<R extends DebugRequest, N extends DebugNotification> {

	int terminatePseudoRequest(DebugNotification notification) throws DebugException;

	void terminate(DebugRequest request);

}