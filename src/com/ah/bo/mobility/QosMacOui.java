package com.ah.bo.mobility;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.ah.bo.network.MacOrOui;
import com.ah.util.EnumConstUtil;
/*
@Entity
@Table(name="QOS_CLASSIFICATION_MAC_OUI")*/
@Embeddable
@SuppressWarnings("serial")
public class QosMacOui implements Serializable{

//	@Id
//	@GeneratedValue
//	private Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="MAC_OR_OUI_ID",nullable=false)
	private MacOrOui macOui;
	
	private String macEntry;
	
	private short qosClassMacOuis;

	private short filterActionMacOuis;

	private short loggingMacOuis;
	
	@Column(length = 32)
	private String comment;
	
	@Transient
	public String[] getFieldValues(){
		String[] fieldValues ={"QOS_CLASSIFICATION_ID","MAC_OR_OUI_ID",
				"macEntry","qosClassMacOuis","filterActionMacOuis","loggingMacOuis","comment"};
		return fieldValues;
	}
//	@Version
//	private Date version;
//	
//	@Length(max = OWNER_STRING_LENGTH)
//	private String owner;
	
//	public short getFilterAction() {
//		return filterAction;
//	}
//
//	public void setFilterAction(short filterAction) {
//		this.filterAction = filterAction;
//	}

//	public boolean isLogging() {
//		return logging;
//	}
//
//	public void setLogging(boolean logging) {
//		this.logging = logging;
//	}
//
//	public short getQosClass() {
//		return qosClass;
//	}
//
//	public void setQosClass(short qosClass) {
//		this.qosClass = qosClass;
//	}

//	public Long getId() {
//		// TODO Auto-generated method stub
//		return this.id;
//	}
//
//	public String getLabel() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public String getOwner() {
//		// TODO Auto-generated method stub
//		return this.owner;
//	}
//
//	public Date getVersion() {
//		// TODO Auto-generated method stub
//		return this.version;
//	}
//
//	@Transient
//	private boolean selected;
//	public boolean isSelected() {
//		// TODO Auto-generated method stub
//		return this.selected;
//	}
//
//	public void setOwner(String owner) {
//		// TODO Auto-generated method stub
//		this.owner=owner;
//	}
//
//	public void setSelected(boolean selected) {
//		// TODO Auto-generated method stub
//		this.selected=selected;
//	}

	public MacOrOui getMacOui() {
		return macOui;
	}

	public void setMacOui(MacOrOui macOui) {
		this.macOui = macOui;
	}

	public String getMacEntry() {
		return macEntry;
	}

	public void setMacEntry(String macEntry) {
		this.macEntry = macEntry;
	}

	public short getFilterActionMacOuis() {
		return filterActionMacOuis;
	}

	public void setFilterActionMacOuis(short filterActionMacOuis) {
		this.filterActionMacOuis = filterActionMacOuis;
	}

	public boolean isEnableLoggingMacOuis() {
		return loggingMacOuis==EnumConstUtil.ENABLE;
	}
	public short getLoggingMacOuis() {
		return loggingMacOuis;
	}

	public void setLoggingMacOuis(short loggingMacOuis) {
		this.loggingMacOuis = loggingMacOuis;
	}

	public short getQosClassMacOuis() {
		return qosClassMacOuis;
	}

	public void setQosClassMacOuis(short qosClassMacOuis) {
		this.qosClassMacOuis = qosClassMacOuis;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}