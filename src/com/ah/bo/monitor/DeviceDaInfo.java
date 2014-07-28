package com.ah.bo.monitor;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;

@Entity
@Table(name = "DEVICE_DA_INFO")
@org.hibernate.annotations.Table(appliesTo = "DEVICE_DA_INFO", indexes = {
		@Index(name = "DA_MAC", columnNames = {"MACADDRESS"}),
		@Index(name = "DA_OWNER", columnNames = {"OWNER"})
		})
public class DeviceDaInfo implements HmBo, Comparable<DeviceDaInfo> {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	
	@Version
	private Timestamp version;
	
	private String macAddress;
	
	private int cpuUsage1;

	private int cpuUsage2;
	
	private int totalMem;
	
	private int freeMem;
	
	private int usedMem;
	
	private String DAMac;

	private String BDAMac;
	
	private String PortalMac;
	
	@Transient
	private String priority = "";
	
	@Transient
	private HiveAp hiveAp;

	public HmDomain getOwner() {
		return this.owner;
	}

	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public String getLabel() {
		return macAddress;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Timestamp getVersion() {
		return this.version;
	}
	
	public void setVersion(Timestamp version) {
		this.version = version;
	}
	
	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	public String getDAMac() {
		return DAMac;
	}

	public void setDAMac(String dAMac) {
		DAMac = dAMac;
	}

	public String getBDAMac() {
		return BDAMac;
	}

	public void setBDAMac(String bDAMac) {
		BDAMac = bDAMac;
	}

	public String getPortalMac() {
		return PortalMac;
	}

	public void setPortalMac(String portalMac) {
		PortalMac = portalMac;
	}
	
	public int getCpuUsage1() {
		return cpuUsage1;
	}

	public void setCpuUsage1(int cpuUsage1) {
		this.cpuUsage1 = cpuUsage1;
	}

	public int getCpuUsage2() {
		return cpuUsage2;
	}

	public void setCpuUsage2(int cpuUsage2) {
		this.cpuUsage2 = cpuUsage2;
	}

	public int getTotalMem() {
		return totalMem;
	}

	public void setTotalMem(int totalMem) {
		this.totalMem = totalMem;
	}

	public int getFreeMem() {
		return freeMem;
	}

	public void setFreeMem(int freeMem) {
		this.freeMem = freeMem;
	}

	public int getUsedMem() {
		return usedMem;
	}

	public void setUsedMem(int usedMem) {
		this.usedMem = usedMem;
	}

	@Transient
	private boolean	selected;
	
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	@Transient
	public String getPriority() {
		return priority;
	}

	@Transient
	public void setPriority(String priority) {
		this.priority = priority;
	}
	
	@Transient
	public HiveAp getHiveAp() {
		if(hiveAp == null){
			hiveAp = new HiveAp();
		}
		return hiveAp;
	}

	@Transient
	public void setHiveAp(HiveAp hiveAp) {
		this.hiveAp = hiveAp;
	}
	
	@Transient
	public String getDAKey(){
		return this.DAMac;
	}
	
	public boolean isPortal(){
		return PortalMac == null || "".equals(PortalMac);
	}
	
	public boolean isDA(){
		return macAddress.equals(DAMac);
	}
	
	public boolean isBDA(){
		return macAddress.equals(BDAMac);
	}
	
	public void initPriority(){
		
		if(this.getHiveAp() != null && this.getHiveAp().isIDMProxy()){
			this.setPriority(this.getPriority() + "1");
		}else{
			this.setPriority(this.getPriority() + "0");
		}
		
		if(this.getHiveAp() != null && this.getHiveAp().getHiveApType() == HiveAp.HIVEAP_TYPE_PORTAL){
			this.setPriority(this.getPriority() + "1");
		}else{
			this.setPriority(this.getPriority() + "0");
		}
		
//		if(this.getHiveAp() != null && !this.getHiveAp().isDhcp()){
//			this.setPriority(this.getPriority() + "1");
//		}else{
//			this.setPriority(this.getPriority() + "0");
//		}
//		
//		if(this.getHiveAp() != null && (this.getHiveAp().getHiveApModel() == HiveAp.HIVEAP_MODEL_330 || this.getHiveAp().getHiveApModel() == HiveAp.HIVEAP_MODEL_350 ||
//			this.getHiveAp().getHiveApModel() == HiveAp.HIVEAP_MODEL_370 || this.getHiveAp().getHiveApModel() == HiveAp.HIVEAP_MODEL_390 ||
//			this.getHiveAp().getHiveApModel() == HiveAp.HIVEAP_MODEL_320 || this.getHiveAp().getHiveApModel() == HiveAp.HIVEAP_MODEL_340 || 
//			this.getHiveAp().getHiveApModel() == HiveAp.HIVEAP_MODEL_380) ){
//			this.setPriority(this.getPriority() + "1");
//		}else{
//			this.setPriority(this.getPriority() + "0");
//		}
//		
//		if(this.isDA()){
//			this.setPriority(this.getPriority() + "1");
//		}else{
//			this.setPriority(this.getPriority() + "0");
//		}
//		
//		if(this.isBDA()){
//			this.setPriority(this.getPriority() + "1");
//		}else{
//			this.setPriority(this.getPriority() + "0");
//		}
	}

	@Override
	public int compareTo(DeviceDaInfo o) {
		int res = o.getPriority().compareTo(this.getPriority());
		
		if(res == 0){
			return o.getFreeMem() - this.getFreeMem();
		}else{
			return res;
		}
	}

}
