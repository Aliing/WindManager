package com.ah.bo.monitor;

import java.sql.Timestamp;
import java.util.TimeZone;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApAutoProvision;
import com.ah.util.EnumItem;
import com.ah.util.datetime.AhDateTimeUtil;

@Entity
@Table(name = "ONETIME_PASSWORD")
@org.hibernate.annotations.Table(appliesTo = "ONETIME_PASSWORD", indexes = {
		@Index(name = "ONE_TIME_PASSWORD_OWNER", columnNames = { "OWNER" }),
		@Index(name = "ONE_TIME_PASSWORD_PASS", columnNames = { "ONETIMEPASSWORD" }),
		@Index(name = "ONE_TIME_PASSWORD_MAC", columnNames = { "MACADDRESS" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class OneTimePassword implements HmBo {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	
	@Version
	private Timestamp version;

	private String oneTimePassword;
	
	private String userName;
	
	private String emailAddress;
	
	private long dateSentStamp;
	
	private long dateActivateStamp;
	
	private String dateTimeZone = TimeZone.getDefault().getID();
	
	private short deviceModel = -1;

	private String macAddress;
	
	private String description;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "HIVEAPAUTOPROVISION")
	private HiveApAutoProvision hiveApAutoProvision;
	
	@Transient
	private boolean selected;

	@Override
	public String getLabel() {
		return this.oneTimePassword;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getDateSent() {
		if(0 != dateSentStamp){
			return AhDateTimeUtil.getDateStrFromLong(dateSentStamp, AhDateTimeUtil.CUSTOMER_FORMATTER, dateTimeZone);
		}
		return null;
	}

	public String getDateActivate() {
		if(0 != dateActivateStamp){
			return AhDateTimeUtil.getDateStrFromLong(dateActivateStamp, AhDateTimeUtil.CUSTOMER_FORMATTER, dateTimeZone);
		}
		return null;
	}

	public short getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(short deviceModel) {
		this.deviceModel = deviceModel;
	}
	
	public String getDeviceModelString(){
		if(-1 != deviceModel){
			for (EnumItem model : HiveAp.HIVEAP_MODEL) {
				if(model.getKey() == deviceModel){
					return model.getValue();
				}
			}
		}
		return null;
	}
	
	public String getMacAddressFormat(){
		if(macAddress == null || "".equals(macAddress)){
			return "";
		}else{
			String resStr = "";
			for(int i=0; i<macAddress.length(); i++){
				if(i+2 >= macAddress.length()){
					resStr += macAddress.substring(i);
				}else{
					resStr += macAddress.substring(i, i+2);
					resStr += ":";
				}
				i++;
			}
			return resStr;
		}
	}
	
	public String getHiveApAutoProvisionName(){
		if(null != hiveApAutoProvision){
			return hiveApAutoProvision.getName();
		}
	
		return null;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getOneTimePassword() {
		return oneTimePassword;
	}

	public void setOneTimePassword(String oneTimePassword) {
		this.oneTimePassword = oneTimePassword;
	}

	public long getDateSentStamp() {
		return dateSentStamp;
	}

	public void setDateSentStamp(long dateSentStamp) {
		this.dateSentStamp = dateSentStamp;
	}

	public long getDateActivateStamp() {
		return dateActivateStamp;
	}

	public void setDateActivateStamp(long dateActivateStamp) {
		this.dateActivateStamp = dateActivateStamp;
	}

	public String getDateTimeZone() {
		return dateTimeZone;
	}

	public void setDateTimeZone(String dateTimeZone) {
		this.dateTimeZone = dateTimeZone;
	}

	public HiveApAutoProvision getHiveApAutoProvision() {
		return hiveApAutoProvision;
	}

	public void setHiveApAutoProvision(HiveApAutoProvision hiveApAutoProvision) {
		this.hiveApAutoProvision = hiveApAutoProvision;
	}

}