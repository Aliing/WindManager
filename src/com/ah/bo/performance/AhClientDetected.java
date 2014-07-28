package com.ah.bo.performance;

import java.util.Date;

public class AhClientDetected {
	private String clientMac;

	private Date detectedTime;

	private int rssi;

	private short channel;

	private double c;

	double xm, ym;

	private double plf;

	private double attenuation;

	private double distance;

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getAttenuation() {
		return attenuation;
	}

	public void setAttenuation(double attenuation) {
		this.attenuation = attenuation;
	}

	public double getPlf() {
		return plf;
	}

	public void setPlf(double plf) {
		this.plf = plf;
	}

	public double getXm() {
		return xm;
	}

	public void setXm(double xm) {
		this.xm = xm;
	}

	public double getYm() {
		return ym;
	}

	public void setYm(double ym) {
		this.ym = ym;
	}

	public String getClientMac() {
		return clientMac;
	}

	public void setClientMac(String clientMac) {
		this.clientMac = clientMac;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	public short getChannel() {
		return channel;
	}

	public void setChannel(short channel) {
		this.channel = channel;
	}

	public double getC() {
		return c;
	}

	public void setC(double c) {
		this.c = c;
	}

	public Date getDetectedTime() {
		return detectedTime;
	}

	public void setDetectedTime(Date detectedTime) {
		this.detectedTime = detectedTime;
	}
}
