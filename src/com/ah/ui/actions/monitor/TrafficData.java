package com.ah.ui.actions.monitor;

public class TrafficData {
	private String name;
	private String location;
	private long txdata;
	private long rxdata;
	
	private long crcError;
	private long txRetry;
	private long airtime;
	private long slaCount;
	
	private long score;
	public long getCrcError() {
		return crcError;
	}
	public long getTxRetry() {
		return txRetry;
	}
	public long getAirtime() {
		return airtime;
	}
	public void setCrcError(long crcError) {
		this.crcError = crcError;
	}
	public void addCrcError(long tmpCrcError) {
		setCrcError(crcError + tmpCrcError);
	}
	
	public void setTxRetry(long txRetry) {
		this.txRetry = txRetry;
	}
	
	public void addTxRetry(long tmpTxRetry) {
		setTxRetry(txRetry + tmpTxRetry);
	}
	public void setAirtime(long airtime) {
		this.airtime = airtime;
	}
	
	public void addAirtime(long tmpAirtime){
		setAirtime(airtime + tmpAirtime);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getTxdata() {
		return txdata;
	}
	public void setTxdata(long txdata) {
		this.txdata = txdata;
	}
	public long getRxdata() {
		return rxdata;
	}
	public void setRxdata(long rxdata) {
		this.rxdata = rxdata;
	}
	
	public long getTotalData(){
		return txdata + rxdata;
	}
	
	public void addTxdata(long tmpTxdata){
		setTxdata(txdata + tmpTxdata);
	}
	public void addRxdata(long tmpRxdata){
		setRxdata(rxdata + tmpRxdata);
	}
	public long getSlaCount() {
		return slaCount;
	}
	public void setSlaCount(long slaCount) {
		this.slaCount = slaCount;
	}
	public void addSlaCount(long tmpSlaCount) {
		setSlaCount(slaCount + tmpSlaCount);
	}
	public void addScore(long tmpScore) {
		setScore(score + tmpScore);
	}
	public boolean getShowLinkSla(){
		if (slaCount==0) return false;
		return true;
	}
	public boolean getShowLinkCrc() {
		if (crcError==0) return false;
		return true;
	}
	public boolean getShowLinkTxData(){
		if (txdata ==0) return false;
		return true;
	}
	public boolean getShowLinkRxData(){
		if (rxdata ==0) return false;
		return true;
	}
	public boolean getShowLinkTxRetry() {
		if (txRetry==0) return false;
		return true;
	}
	public boolean getShowLinkAirtime() {
		if (airtime==0) return false;
		return true;
	}
	public boolean getShowLinkScore() {
		if (score==0) return false;
		return true;
	}
	/**
	 * @return the score
	 */
	public long getScore() {
		return score;
	}
	/**
	 * @param score the score to set
	 */
	public void setScore(long score) {
		this.score = score;
	}
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
}
