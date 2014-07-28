package com.ah.be.config.hiveap;

import com.ah.be.config.result.ap.AhApConfigGenerationResult;

public class ScriptConfigObject extends UpdateObject {

	private short scriptType;

	private boolean saveServerFiles;

	private AhApConfigGenerationResult resultObject;

	public short getScriptType() {
		return scriptType;
	}

	public void setScriptType(short scriptType) {
		this.scriptType = scriptType;
	}

	public boolean isSaveServerFiles() {
		return saveServerFiles;
	}

	public void setSaveServerFiles(boolean saveServerFiles) {
		this.saveServerFiles = saveServerFiles;
	}

	public AhApConfigGenerationResult getResultObject() {
		return resultObject;
	}

	public void setResultObject(AhApConfigGenerationResult resultObject) {
		this.resultObject = resultObject;
	}
}
