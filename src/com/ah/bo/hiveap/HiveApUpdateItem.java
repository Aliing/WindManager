package com.ah.bo.hiveap;

import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
public class HiveApUpdateItem {

	@Column(length=409600)
	private String clis;

	private short updateType;

	private short scriptType;

	private int fileSize;

	private short result;

	private boolean actived;

	private boolean continued;

	@Column(length=1024)
	private String description;

	public String getClis() {
		return clis;
	}

	public void setClis(String clis) {
		this.clis = clis;
	}

	public short getUpdateType() {
		return updateType;
	}

	public void setUpdateType(short updateType) {
		this.updateType = updateType;
	}

	public short getScriptType() {
		return scriptType;
	}

	public void setScriptType(short scriptType) {
		this.scriptType = scriptType;
	}

	public short getResult() {
		return result;
	}

	public void setResult(short result) {
		this.result = result;
	}

	public boolean isActived() {
		return actived;
	}

	public void setActived(boolean actived) {
		this.actived = actived;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isContinued() {
		return continued;
	}

	public void setContinued(boolean continued) {
		this.continued = continued;
	}

}
