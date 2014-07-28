package com.ah.bo.hiveap;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
public class ConfigTemplateType {
	public boolean isWirelessEnabled() {
		return wirelessEnabled;
	}

	public boolean isSwitchEnabled() {
		return switchEnabled;
	}

	public boolean isRouterEnabled() {
		return routerEnabled;
	}

	public boolean isBonjourEnabled() {
		return bonjourEnabled;
	}

	public static final int _NONE = 0;
	public static final int WIRELESS = 01;
	public static final int SWITCH = 02;
	public static final int ROUTER = 04;
	public static final int BONJOUR = 010;
	
	private boolean wirelessEnabled;
	private boolean switchEnabled;
	private boolean routerEnabled;
	private boolean bonjourEnabled;
	
	public ConfigTemplateType setDefaultTemplateType() {
		this.setWirelessEnabled(true);
		this.setBonjourEnabled(true);
		return this;
	}
	
	public void setWirelessEnabled(boolean wirelessEnabled) {
		this.wirelessEnabled = wirelessEnabled;
	}
	public void setSwitchEnabled(boolean switchEnabled) {
		this.switchEnabled = switchEnabled;
	}
	public void setRouterEnabled(boolean routerEnabled) {
		this.routerEnabled = routerEnabled;
	}
	public void setBonjourEnabled(boolean bonjourEnabled) {
		this.bonjourEnabled = bonjourEnabled;
	}
	
	@Transient
	public int getCurrentTypeMarks() {
		int currentTypeMarks = 0;
		currentTypeMarks |= this.wirelessEnabled?WIRELESS:0;
		currentTypeMarks |= this.switchEnabled?SWITCH:0;
		currentTypeMarks |= this.routerEnabled?ROUTER:0;
		currentTypeMarks |= this.bonjourEnabled?BONJOUR:0;
		return currentTypeMarks;
	}
	
	@Override
	public boolean equals(Object another) {
		if (another instanceof ConfigTemplateType
				&& another != null) {
			return this.getCurrentTypeMarks() == ((ConfigTemplateType)another).getCurrentTypeMarks();
		}
		return false;
	}
	
	/**
	 * only check whether these types are supported, no matter other types are supported or not
	 * @param typeMark
	 * @return
	 */
	@Transient
	public boolean isTypeSupport(int typeMark) {
		int currentTypeMarks = this.getCurrentTypeMarks();
		if (currentTypeMarks == 0 || typeMark == 0) {
			return (typeMark == 0 && currentTypeMarks == 0)?true:false;
		}
		return (typeMark & currentTypeMarks) == typeMark;
	}
	
	/**
	 * only these types are supported and other types are not supported
	 * @param typeMark
	 * @return
	 */
	@Transient
	public boolean isTypeSupportStrict(int typeMark) {
		int currentTypeMarks = this.getCurrentTypeMarks();
		if (currentTypeMarks == 0 || typeMark == 0) {
			return (typeMark == 0 && currentTypeMarks == 0)?true:false;
		}
		return (typeMark ^ currentTypeMarks) == 0;
	}
	
	/**
	 * check whether at least one type is set
	 * @return
	 */
	@Transient
	public boolean isTypeSet() {
		int currentTypeMarks = this.getCurrentTypeMarks();
		return currentTypeMarks != 0;
	}
	
	/**
	 * return true only when other types are selected but won't contain those types passed in as arguments
	 * @param typeMark
	 * @return
	 */
	@Transient
	public boolean isOtherTypesSupportedExcept(int typeMark) {
		int currentTypeMarks = this.getCurrentTypeMarks();
		if (currentTypeMarks == 0) {
			return false;
		}
		if (typeMark == 0) {
			return currentTypeMarks != 0;
		}
		return (typeMark & currentTypeMarks) == 0;
	}
	
	/**
	 * return true if one or all of types passed in is supported, <b>no matter</b> whether other types are support
	 * @param typeMark
	 * @return
	 */
	@Transient
	public boolean isTypeSupportOr(int typeMark) {
		int currentTypeMarks = this.getCurrentTypeMarks();
		if (currentTypeMarks == 0 || typeMark == 0) {
			return false;
		}
		return (typeMark & currentTypeMarks) > 0;
	}
	
	/**
	 * return true if one or all of types passed in is supported, and <b>can not</b> support other type(s) than types passed in
	 * @param typeMark
	 * @return
	 */
	@Transient
	public boolean isTypeSupportOrStrict(int typeMark) {
		int currentTypeMarks = this.getCurrentTypeMarks();
		if (currentTypeMarks == 0 || typeMark == 0) {
			return false;
		}
		return (typeMark | currentTypeMarks) == typeMark
				&& (typeMark & currentTypeMarks) > 0;
	}
	
	/**
	 * it means this network policy is set with other type(s), but not bonjour is set
	 * @return
	 */
	@Transient
	public boolean isOtherTypesSupportedExceptBonjour() {
		return this.isOtherTypesSupportedExcept(BONJOUR);
	}
	
	/**
	 * it means this network policy is set with other type(s), but not wireless is set
	 * @return
	 */
	@Transient
	public boolean isOtherTypesSupportedExceptWireless() {
		return this.isOtherTypesSupportedExcept(WIRELESS);
	}
	
	/**
	 * it means this network policy is set with other type(s), but not wireless or bonjour
	 * @return
	 */
	@Transient
	public boolean isOtherTypesSupportedExceptWirelessAndBonjour() {
		return this.isOtherTypesSupportedExcept(WIRELESS|BONJOUR);
	}
	
	/**
	 * it means this network policy is set with only one bonjour type
	 * @return
	 */
	@Transient
	public boolean isBonjourOnly(){
		return this.isTypeSupportStrict(BONJOUR);
	}
	
	/**
	 * it means this network policy is set with only one wireless type
	 * @return
	 */
	@Transient
	public boolean isWirelessOnly(){
		return this.isTypeSupportStrict(WIRELESS);
	}
	
	/**
	 * it means this network policy is set with only one SWITCH type
	 * @return
	 */
	@Transient
	public boolean isSwitchOnly(){
		return this.isTypeSupportStrict(SWITCH);
	}
	
	/**
	 * it means this network policy is set with only two types: wireless and router
	 * @return
	 */
	@Transient
	public boolean isWirelessAndRouterOnly() {
		return this.isTypeSupportStrict(WIRELESS|ROUTER);
	}
	
	/**
	 * it means this network policy is set with at least router, no matter whether other type(s) exists
	 * @return
	 */
	@Transient
	public boolean isRouterContained() {
		return this.isTypeSupport(ROUTER);
	}
	
	/**
	 * it means this network policy is set with at least WIRELESS, no matter whether other type(s) exists
	 * @return
	 */
	@Transient
	public boolean isWirelessContained() {
		return this.isTypeSupport(WIRELESS);
	}
	
	/**
	 * it means this network policy is set with at least Bonjour, no matter whether other type(s) exists
	 * @return
	 */
	@Transient
	public boolean isBonjourContained() {
		return this.isTypeSupport(BONJOUR);
	}
	
	/**
	 * it means this network policy is set with at least SWITCH, no matter whether other type(s) exists
	 * @return
	 */
	@Transient
	public boolean isSwitchContained() {
		return this.isTypeSupport(SWITCH);
	}
	
	/**
	 * it means this network policy is set with at least router or switch, no matter whether other type(s) exists
	 * @return
	 */
	@Transient
	public boolean isRouterOrSwitchContained() {
		return this.isTypeSupportOr(ROUTER | SWITCH);
	}
	
	/**
	 * it means this network policy is set with at least WIRELESS or BONJOUR, no matter whether other type(s) exists
	 * @return
	 */
	@Transient
	public boolean isWirelessOrBonjourContained() {
		return this.isTypeSupportOr(WIRELESS | BONJOUR);
	}
	
	/**
	 * it means this network policy is set with at least two types: wireless and router, no matter whether other type(s) exists
	 * @return
	 */
	@Transient
	public boolean isWirelessAndRouterContained() {
		return this.isTypeSupport(WIRELESS|ROUTER);
	}
	
	/**
	 * it means this network policy is set with wireless or bonjour or wireless+bonjour, but no other type(s) exists
	 * @return
	 */
	@Transient
	public boolean isWirelessOrBonjourOnly() {
		return this.isTypeSupportOrStrict(WIRELESS|BONJOUR);
	}
	
	/**
	 * please check whenever you see the method is called
	 * @return
	 */
	@Deprecated
	@Transient
	public boolean isWirelessAndRouterOnlyNeedCheck() {
		return this.isWirelessAndRouterOnly();
	}
	
	/**
	 * please check whenever you see the method is called
	 * @return
	 */
	@Deprecated
	@Transient
	public boolean isWirelessAndRouterContainedNeedCheck() {
		return this.isWirelessAndRouterContained();
	}
	
}
