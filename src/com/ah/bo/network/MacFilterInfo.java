package com.ah.bo.network;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Embeddable
public class MacFilterInfo implements Serializable
{
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MAC_OR_OUI_ID", nullable = true)
	private MacOrOui macOrOui;
	
	private short filterAction;
	
	public short getFilterAction() {
		return filterAction;
	}

	public void setFilterAction(short filterAction) {
		this.filterAction = filterAction;
	}

	public MacOrOui getMacOrOui() {
		return macOrOui;
	}

	public void setMacOrOui(MacOrOui macOrOui) {
		this.macOrOui = macOrOui;
	}
	
	@Transient
	public String[] getFieldValues(){
		String[] fieldValues ={"MAC_FILTER_ID","MAC_OR_OUI_ID","filterAction","MAC_FILTER_INFO_ID"};
		return fieldValues;
	}

}
