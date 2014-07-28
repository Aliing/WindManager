package com.ah.be.ts.hiveap.impl;

public class ResponseEventProcessor extends EventProcessor {

	private static final long serialVersionUID = 1L;

	public ResponseEventProcessor(HiveApDebugMgmtImpl hiveApDebug) {
		super.hiveApDebug = hiveApDebug;
	}

	@Override
	public String getShortName() {
		return "Response";
	}

}