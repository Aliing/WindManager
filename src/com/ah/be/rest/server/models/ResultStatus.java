package com.ah.be.rest.server.models;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ResultStatus")
public class ResultStatus extends BaseModel{
	@XStreamAlias("Operation")
	private String operation;
	@XStreamAlias("EffectRows")
	private int effectRows;
	@XStreamAlias("ResultFlag")
	private String resultFlag;
	@XStreamAlias("ResultDetail")
	private String resultDetail;

	public ResultStatus(String operation){
		this.operation = operation;
	}

	public ResultStatus(String operation, String resultFlag,
			String resultDetail, int effectRows) {
		super();
		this.operation = operation;
		this.resultFlag = resultFlag;
		this.resultDetail = resultDetail;
		this.effectRows = effectRows;
	}

	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getResultFlag() {
		return resultFlag;
	}
	public void setResultFlag(String resultFlag) {
		this.resultFlag = resultFlag;
	}
	public String getResultDetail() {
		return resultDetail;
	}
	public void setResultDetail(String resultDetail) {
		this.resultDetail = resultDetail;
	}
	public int getEffectRows() {
		return effectRows;
	}
	public void setEffectRows(int effectRows) {
		this.effectRows = effectRows;
	}


}
