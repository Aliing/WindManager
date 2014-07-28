package com.ah.bo.monitor;


import java.sql.Timestamp;

import javax.persistence.*;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;


@Entity
@Table(name = "NetworkDeviceReport")
public class NetworkDeviceReport implements HmBo{
	
	private static final long serialVersionUID = 4468876969963019577L;

	@EmbeddedId
	private NetworkDeviceReportPK  pk;

	private boolean topCandidate ;

	@Column( columnDefinition = "bigint[]")
	private Long[]  extensionBigInt ;
	@Column( columnDefinition = "TimeStamp with Time Zone[]")
	private Timestamp[]  extensionTimeStamp ;
	@Column( columnDefinition = "text[]")
	private String[]  extensionText ;
	@Column( columnDefinition = "byteA[]")
	private Byte[][]  extensionByteArray ;

	public NetworkDeviceReportPK getPk()
	{
		return pk;
	}

	public void setPk(NetworkDeviceReportPK k)
	{
		pk = k;
	}

	public boolean getTopCandidate() {
		return topCandidate;
	}

	public void setTopCandidate(boolean topCandidate) {
		this.topCandidate = topCandidate;
	}

	public Long[] getExtensionBigInt() {
		return extensionBigInt;
	}

	public void setExtensionBigInt(Long[] extensionBigInt) {
		this.extensionBigInt = extensionBigInt;
	}

	public Timestamp[] getExtensionTimeStamp() {
		return extensionTimeStamp;
	}

	public void setExtensionTimeStamp(Timestamp[] extensionTimeStamp) {
		this.extensionTimeStamp = extensionTimeStamp;
	}

	public String[] getExtensionText() {
		return extensionText;
	}

	public void setExtensionText(String[] extensionText) {
		this.extensionText = extensionText;
	}

	public Byte[][] getExtensionByteArray() {
		return extensionByteArray;
	}

	public void setExtensionByteArray(Byte[][] extensionByteArray) {
		this.extensionByteArray = extensionByteArray;
	}

	@Override
	public HmDomain getOwner() {
		return null;
	}

	@Override
	public void setOwner(HmDomain owner) {
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public Long getId() {
		return null;
	}

	@Override
	public void setId(Long l) {
	}

	@Override
	public Timestamp getVersion() {
		return null;
	}

	@Override
	public void setVersion(Timestamp version) {
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected(boolean selected) {
	}
}