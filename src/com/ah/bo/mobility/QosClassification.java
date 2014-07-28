/**
 *@filename		IpAddress.java
 *@version
 *@author		Fiona
 *@createtime	2007-8-29 PM 03:16:58
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.mobility;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

/**
 * @author Chris Scheers
 */
@Entity
@Table(name = "QOS_CLASSIFICATION")
@org.hibernate.annotations.Table(appliesTo = "QOS_CLASSIFICATION", indexes = {
		@Index(name = "QOS_CLASSIFICATION_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class QosClassification implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Version
	private Timestamp version;

	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name="mapkey")
	@CollectionTable(name = "QOS_CLASSIFICATION_SERVICE", joinColumns = @JoinColumn(name = "QOS_CLASSIFICATION_ID", nullable = true))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Map<Long, QosNetworkService> networkServices = new HashMap<Long, QosNetworkService>();
	
	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name="mapkey")
	@CollectionTable(name = "QOS_CLASSIFICATION_CUSTOMSERVICE", joinColumns = @JoinColumn(name = "QOS_CLASSIFICATION_ID", nullable = true))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Map<Long, QosCustomService> customServices = new HashMap<Long, QosCustomService>();

	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name="mapkey")
	@CollectionTable(name = "QOS_CLASSIFICATION_MAC", joinColumns = @JoinColumn(name = "QOS_CLASSIFICATION_ID", nullable = true))
	// @CollectionId(columns = @Column(name = "QOS_MAC_OUI_ID"), type =
	// @Type(type = "long"), generator = "sequence")
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Map<Long, QosMacOui> qosMacOuis = new HashMap<Long, QosMacOui>();

	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name="mapkey")
	@CollectionTable(name = "QOS_CLASSIFICATION_SSID", joinColumns = @JoinColumn(name = "QOS_CLASSIFICATION_ID", nullable = true))
	// @CollectionId(columns = @Column(name = "QOS_SSID_ID"), type = @Type(type
	// = "long"), generator = "sequence")
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Map<Long, QosSsid> qosSsids = new HashMap<Long, QosSsid>();
	
	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String classificationName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	private boolean networkServicesEnabled;

	private boolean macOuisEnabled;

	private boolean marksEnabled;

	private boolean ssidEnabled;
	
	private boolean generalEnabled;

	@Column(length = 20)
	private String prtclE;

	@Column(length = 20)
	private String prtclP;

	@Column(length = 20)
	private String prtclD;

	// private short ssidIndex=EnumConstUtil.QOS_CLASS_BEST_EFFORT_1;

	@Transient
	public String[] getFieldValues() {
		return new String[] { "id", "classificationName", "description", "networkServicesEnabled",
				"macOuisEnabled", "ssidEnabled", "prtclE", "prtclP", "prtclD",
				"marksEnabled", "generalEnabled", "owner" };
	}

	// public short getSsidIndex() {
	// return ssidIndex;
	// }
	//
	// public void setSsidIndex(short ssidIndex) {
	// this.ssidIndex = ssidIndex;
	// }

	public String getPrtclD() {
		return prtclD;
	}

	public void setPrtclD(String prtclD) {
		this.prtclD = prtclD;
	}

	public String getPrtclE() {
		return prtclE;
	}

	public void setPrtclE(String prtclE) {
		this.prtclE = prtclE;
	}

	public String getPrtclP() {
		return prtclP;
	}

	public void setPrtclP(String prtclP) {
		this.prtclP = prtclP;
	}

	public Map<Long, QosNetworkService> getNetworkServices() {
		return networkServices;
	}

	public void setNetworkServices(Map<Long, QosNetworkService> networkServices) {
		this.networkServices = networkServices;
	}

	public Map<Long, QosCustomService> getCustomServices() {
		return customServices;
	}

	public void setCustomServices(Map<Long, QosCustomService> customServices) {
		this.customServices = customServices;
	}

	public List<QosNetworkService> getOrderedNetworkServices() {
		List<QosNetworkService> orderedNetworkServices = new ArrayList<QosNetworkService>(
				networkServices.values());
		/*if(null != orderedNetworkServices && orderedNetworkServices.size() > 0){
			for(QosNetworkService qns : orderedNetworkServices){
				if(null != qns.getNetworkService()){
					if(qns.getNetworkService().getServiceType() == NetworkService.SERVICE_TYPE_L7){
						qns.setNetworkServiceDisabled(true);
					}else{
						qns.setNetworkServiceDisabled(false);
					}
				}else{
					qns.setNetworkServiceDisabled(true);
				}
			}
		}*/
		Collections.sort(orderedNetworkServices,
				new Comparator<QosNetworkService>() {
					@Override
					public int compare(QosNetworkService s1,
							QosNetworkService s2) {
						String id1 = s1.getNetworkService().getServiceName();
						String id2 = s2.getNetworkService().getServiceName();
						return id1.compareTo(id2);
					}
				});
		return orderedNetworkServices;
	}

	public List<QosCustomService> getOrderedCustomServices() {
		List<QosCustomService> orderedCustomServices = new ArrayList<QosCustomService>(
				customServices.values());
		Collections.sort(orderedCustomServices,
				new Comparator<QosCustomService>() {
					@Override
					public int compare(QosCustomService s1,
							QosCustomService s2) {
						String id1 = s1.getCustomAppService().getCustomAppName();
						String id2 = s2.getCustomAppService().getCustomAppName();
						return id1.compareTo(id2);
					}
				});
		return orderedCustomServices;
	}
	
	public List<QosMacOui> getOrderedQosMacOuis() {
		List<QosMacOui> orderedQosMacOuis = new ArrayList<QosMacOui>(qosMacOuis
				.values());
		Collections.sort(orderedQosMacOuis, new Comparator<QosMacOui>() {
			@Override
			public int compare(QosMacOui s1, QosMacOui s2) {
				String id1 = s1.getMacOui().getMacOrOuiName();
				String id2 = s2.getMacOui().getMacOrOuiName();
				return id1.compareTo(id2);
			}
		});
		return orderedQosMacOuis;
	}

	public List<QosSsid> getOrderedQosSsids() {
		List<QosSsid> orderedQosSsids = new ArrayList<QosSsid>(qosSsids
				.values());
		Collections.sort(orderedQosSsids, new Comparator<QosSsid>() {
			@Override
			public int compare(QosSsid s1, QosSsid s2) {
				String id1 = s1.getSsid().getSsidName();
				String id2 = s2.getSsid().getSsidName();
				return id1.compareTo(id2);
			}
		});
		return orderedQosSsids;
	}

	public boolean getMacOuisEnabled() {
		return macOuisEnabled;
	}

	public void setMacOuisEnabled(boolean macOuisEnabled) {
		this.macOuisEnabled = macOuisEnabled;
	}

	public boolean getNetworkServicesEnabled() {
		return networkServicesEnabled;
	}

	public void setNetworkServicesEnabled(boolean networkServicesEnabled) {
		this.networkServicesEnabled = networkServicesEnabled;
	}

	public String getClassificationName() {
		return classificationName;
	}

	public void setClassificationName(String classificationName) {
		this.classificationName = classificationName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Transient
	private boolean selected;

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String getLabel() {
		return classificationName;
	}

	public Map<Long, QosMacOui> getQosMacOuis() {
		return qosMacOuis;
	}

	public void setQosMacOuis(Map<Long, QosMacOui> qosMacOuis) {
		this.qosMacOuis = qosMacOuis;
	}

	public boolean getSsidEnabled() {
		return ssidEnabled;
	}

	public void setSsidEnabled(boolean ssidEnabled) {
		this.ssidEnabled = ssidEnabled;
	}
	
	public boolean getGeneralEnabled() {
		return this.generalEnabled;
	}
	
	public void setGeneralEnabled(boolean generalEnabled) {
		this.generalEnabled = generalEnabled;
	}

	@Transient
	public String getEnableNetworkServicesValue() {
		return networkServicesEnabled ? "Enabled" : "Disabled";
	}

	@Transient
	public String getDisplayNetworkServices() {
		return networkServicesEnabled ? "" : "none";
	}
	
	@Transient
	public String getEnableMacOuiValue() {
		return macOuisEnabled ? "Enabled" : "Disabled";
	}
	
	@Transient
	public String getDisplayMacOui() {
		return macOuisEnabled ? "" : "none";
	}
	
	@Transient
	public String getEnableGeneralValue() {
		return generalEnabled ? "Enabled" : "Disabled";
	}
	
	@Transient
	public String getDisplayGeneral() {
		return generalEnabled ? "" : "none";
	}

	@Transient
	public String getChboxDValue() {
		if (prtclD != null && !prtclD.trim().equals(""))
			return "Enabled";
		return "Disabled";
	}

	@Transient
	public String getChboxPValue() {
		if (prtclP != null && !prtclP.trim().equals(""))
			return "Enabled";
		return "Disabled";
	}

	@Transient
	public String getChboxEValue() {
		if (prtclE != null && !prtclE.trim().equals(""))
			return "Enabled";
		return "Disabled";
	}

	@Transient
	public String getSsidValue() {
		if (ssidEnabled) {
			return "Enabled";
		}
		return "Disabled";
	}
	
	@Transient
	public String getDisplaySsid() {
		return ssidEnabled ? "" : "none";
	}

	@Transient
	public String getMarkValue() {
		if (marksEnabled)
			return "Enabled";
		return "Disabled";
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public boolean getMarksEnabled() {
		return marksEnabled;
	}

	public void setMarksEnabled(boolean marksEnabled) {
		this.marksEnabled = marksEnabled;
	}

	@Transient
	private String operationFlag;

	public String getOperationFlag() {
		return operationFlag;
	}

	public void setOperationFlag(String operationFlag) {
		this.operationFlag = operationFlag;
	}

	public Map<Long, QosSsid> getQosSsids() {
		return qosSsids;
	}

	public void setQosSsids(Map<Long, QosSsid> qosSsids) {
		this.qosSsids = qosSsids;
	}
	
	@Override
	public QosClassification clone() {
		try {
			return (QosClassification) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	@Transient
	private String[] ssidNames;
	@Transient
	private String[] ssidQosClasses;
	@Transient
	private String[] ouiNames;
	@Transient
	private String[] ouiQosClasses;
	@Transient
	private String[] ouiLoggings;
	@Transient
	private String[] ouiFilterActions;
	@Transient
	private String[] ouiComments;
	@Transient
	private String editOuiInfo;
	@Transient
	private String[] serviceNames;
	@Transient
	private String[] serviceQosClasses;
	@Transient
	private String[] serviceLoggings;
	@Transient
	private String[] serviceFilterActions;
	@Transient
	private String editServiceInfo;
	@Transient
	private String editSSIDInfo;
	
	
	public String getEditSSIDInfo() {
		return editSSIDInfo;
	}

	public void setEditSSIDInfo(String editSSIDInfo) {
		this.editSSIDInfo = editSSIDInfo;
	}

	public String getEditServiceInfo() {
		return editServiceInfo;
	}

	public void setEditServiceInfo(String editServiceInfo) {
		this.editServiceInfo = editServiceInfo;
	}

	public String[] getServiceNames() {
		return serviceNames;
	}

	public void setServiceNames(String[] serviceNames) {
		this.serviceNames = serviceNames;
	}

	public String[] getServiceQosClasses() {
		return serviceQosClasses;
	}

	public void setServiceQosClasses(String[] serviceQosClasses) {
		this.serviceQosClasses = serviceQosClasses;
	}

	public String[] getServiceLoggings() {
		return serviceLoggings;
	}

	public void setServiceLoggings(String[] serviceLoggings) {
		this.serviceLoggings = serviceLoggings;
	}

	public String[] getServiceFilterActions() {
		return serviceFilterActions;
	}

	public void setServiceFilterActions(String[] serviceFilterActions) {
		this.serviceFilterActions = serviceFilterActions;
	}

	public String getEditOuiInfo() {
		return editOuiInfo;
	}

	public void setEditOuiInfo(String editOuiInfo) {
		this.editOuiInfo = editOuiInfo;
	}

	public String[] getSsidNames() {
		return ssidNames;
	}

	public void setSsidNames(String[] ssidNames) {
		this.ssidNames = ssidNames;
	}

	public String[] getSsidQosClasses() {
		return ssidQosClasses;
	}

	public void setSsidQosClasses(String[] ssidQosClasses) {
		this.ssidQosClasses = ssidQosClasses;
	}

	public String[] getOuiNames() {
		return ouiNames;
	}

	public void setOuiNames(String[] ouiNames) {
		this.ouiNames = ouiNames;
	}

	public String[] getOuiQosClasses() {
		return ouiQosClasses;
	}

	public void setOuiQosClasses(String[] ouiQosClasses) {
		this.ouiQosClasses = ouiQosClasses;
	}

	public String[] getOuiLoggings() {
		return ouiLoggings;
	}

	public void setOuiLoggings(String[] ouiLoggings) {
		this.ouiLoggings = ouiLoggings;
	}

	public String[] getOuiFilterActions() {
		return ouiFilterActions;
	}

	public void setOuiFilterActions(String[] ouiFilterActions) {
		this.ouiFilterActions = ouiFilterActions;
	}

	public String[] getOuiComments() {
		return ouiComments;
	}

	public void setOuiComments(String[] ouiComments) {
		this.ouiComments = ouiComments;
	}
	@Transient
	private String parentDomID = "";
	public String getParentDomID() {
		return parentDomID;
	}

	public void setParentDomID(String parentDomID) {
		this.parentDomID = parentDomID;
	}
}