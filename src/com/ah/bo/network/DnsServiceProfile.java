package com.ah.bo.network;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

/**
 * @author Yunzhi Lin
 * $Id: DnsServiceProfile.java,v 1.12.24.1.4.1.12.1 2014/05/30 05:51:28 ylin Exp $
 */
@Entity
@Table(name = "DNS_SERVICE_PROFILE")
@org.hibernate.annotations.Table(appliesTo = "DNS_SERVICE_PROFILE", indexes = {
		@Index(name = "DNS_SERVICE_PROFILE_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class DnsServiceProfile implements HmBo {

	private static final long serialVersionUID = 1L;
	
	public static final String OPENDNS_SERVICE_NAME="OpenDNS";

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	
	@Id
	@GeneratedValue
	private Long id;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String serviceName;
	
	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;
	
	public static final int SAME_DNS = 0;
	public static final int SEPARATE_DNS = 1;
	public static final int EXTERNAL_DNS = 2;
	public static final int INTERNAL_DNS = 3;
	private int serviceType = SEPARATE_DNS;
	
	/*-----------Workspace--------------*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INTERNAL_DNS1_ID")
	private IpAddress internalDns1;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INTERNAL_DNS2_ID")
	private IpAddress internalDns2;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INTERNAL_DNS3_ID")
	private IpAddress internalDns3;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DOMAIN_OBJECT_ID")
	private DomainObject domainObj;
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "DNS_SPECIFIC_SETTINGS", joinColumns = @JoinColumn(name = "DNS_SERVICE_ID", nullable = true))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<DnsSpecificSettings> specificInfos = new ArrayList<DnsSpecificSettings>();
	
	/*-----------External--------------*/
	public static final int LOCAL_DNS_TYPE = 1;
	public static final int OPEN_DNS_TYPE = 2;
	public static final int SPECIFIC_DNS_TYPE = 3;
	private int externalServerType = LOCAL_DNS_TYPE;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EXTERNAL_DNS1_ID")
	private IpAddress externalDns1;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EXTERNAL_DNS2_ID")
	private IpAddress externalDns2;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EXTERNAL_DNS3_ID")
	private IpAddress externalDns3;
	
	/*-----------Transient Field--------------*/
	@Transient
	private String internalIP1;
	@Transient
	private String internalIP2;
	@Transient
	private String internalIP3;
	@Transient
	private String externalIP1;
	@Transient
	private String externalIP2;
	@Transient
	private String externalIP3;
	
	@Transient
	public boolean isExternalSepcServerType() {
		return externalServerType == SPECIFIC_DNS_TYPE;
	}
	
	@Transient
	public boolean isSplitDNS() {
        if(serviceType == DnsServiceProfile.SEPARATE_DNS 
                || externalServerType == DnsServiceProfile.LOCAL_DNS_TYPE){
            return true;
        }else{
            return false;
        }
	}
	
	/*-----------Override Object methods--------------*/
	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder().append(this.id, ((DnsServiceProfile) obj).id)
				.append(this.serviceName, ((DnsServiceProfile) obj).serviceName).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).append(this.serviceName).toHashCode();
	}
	@Override
	public DnsServiceProfile clone() {
		// shadow clone
		try {
			return (DnsServiceProfile) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	/*-----------implements from HmBo--------------*/
	@Override
	public String getLabel() {
		return this.serviceName;
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
	public Long getId() {
		return this.id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Version
	private Timestamp version;
	
	@Override
	public Timestamp getVersion() {
		return this.version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Transient
	private boolean selected;
	
	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	/*-----------Getter/Setter--------------*/
	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * @param serviceName 
	 *		the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description 
	 *		the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the internalDns1
	 */
	public IpAddress getInternalDns1() {
		return internalDns1;
	}

	/**
	 * @param internalDns1 
	 *		the internalDns1 to set
	 */
	public void setInternalDns1(IpAddress internalDns1) {
		this.internalDns1 = internalDns1;
	}

	/**
	 * @return the internalDns2
	 */
	public IpAddress getInternalDns2() {
		return internalDns2;
	}

	/**
	 * @param internalDns2 
	 *		the internalDns2 to set
	 */
	public void setInternalDns2(IpAddress internalDns2) {
		this.internalDns2 = internalDns2;
	}

	/**
	 * @return the internalDns3
	 */
	public IpAddress getInternalDns3() {
		return internalDns3;
	}

	/**
	 * @param internalDns3 
	 *		the internalDns3 to set
	 */
	public void setInternalDns3(IpAddress internalDns3) {
		this.internalDns3 = internalDns3;
	}

	/**
	 * @return the domainObj
	 */
	public DomainObject getDomainObj() {
		return domainObj;
	}

	/**
	 * @param domainObj 
	 *		the domainObj to set
	 */
	public void setDomainObj(DomainObject domainObj) {
		this.domainObj = domainObj;
	}

	/**
	 * @return the specificInfos
	 */
	public List<DnsSpecificSettings> getSpecificInfos() {
		return specificInfos;
	}

	/**
	 * @param specificInfos 
	 *		the specificInfos to set
	 */
	public void setSpecificInfos(List<DnsSpecificSettings> specificInfos) {
		this.specificInfos = specificInfos;
	}

	/**
	 * @return the externalServerType
	 */
	public int getExternalServerType() {
		return externalServerType;
	}

	/**
	 * @param externalServerType 
	 *		the externalServerType to set
	 */
	public void setExternalServerType(int externalServerType) {
		this.externalServerType = externalServerType;
	}

	/**
	 * @return the externalDns1
	 */
	public IpAddress getExternalDns1() {
		return externalDns1;
	}

	/**
	 * @param externalDns1 
	 *		the externalDns1 to set
	 */
	public void setExternalDns1(IpAddress externalDns1) {
		this.externalDns1 = externalDns1;
	}

	/**
	 * @return the externalDns2
	 */
	public IpAddress getExternalDns2() {
		return externalDns2;
	}

	/**
	 * @param externalDns2 
	 *		the externalDns2 to set
	 */
	public void setExternalDns2(IpAddress externalDns2) {
		this.externalDns2 = externalDns2;
	}

	/**
	 * @return the externalDns3
	 */
	public IpAddress getExternalDns3() {
		return externalDns3;
	}

	/**
	 * @param externalDns3 
	 *		the externalDns3 to set
	 */
	public void setExternalDns3(IpAddress externalDns3) {
		this.externalDns3 = externalDns3;
	}

	/**
	 * @return the internalIP1
	 */
	public String getInternalIP1() {
		return internalIP1;
	}

	/**
	 * @param internalIP1 
	 *		the internalIP1 to set
	 */
	public void setInternalIP1(String internalIP1) {
		this.internalIP1 = internalIP1;
	}

	/**
	 * @return the internalIP2
	 */
	public String getInternalIP2() {
		return internalIP2;
	}

	/**
	 * @param internalIP2 
	 *		the internalIP2 to set
	 */
	public void setInternalIP2(String internalIP2) {
		this.internalIP2 = internalIP2;
	}

	/**
	 * @return the internalIP3
	 */
	public String getInternalIP3() {
		return internalIP3;
	}

	/**
	 * @param internalIP3 
	 *		the internalIP3 to set
	 */
	public void setInternalIP3(String internalIP3) {
		this.internalIP3 = internalIP3;
	}

	/**
	 * @return the externalIP1
	 */
	public String getExternalIP1() {
		return externalIP1;
	}

	/**
	 * @param externalIP1 
	 *		the externalIP1 to set
	 */
	public void setExternalIP1(String externalIP1) {
		this.externalIP1 = externalIP1;
	}

	/**
	 * @return the externalIP2
	 */
	public String getExternalIP2() {
		return externalIP2;
	}

	/**
	 * @param externalIP2 
	 *		the externalIP2 to set
	 */
	public void setExternalIP2(String externalIP2) {
		this.externalIP2 = externalIP2;
	}

	/**
	 * @return the externalIP3
	 */
	public String getExternalIP3() {
		return externalIP3;
	}

	/**
	 * @param externalIP3 
	 *		the externalIP3 to set
	 */
	public void setExternalIP3(String externalIP3) {
		this.externalIP3 = externalIP3;
	}

	public int getServiceType() {
		return serviceType;
	}

	public void setServiceType(int serviceType) {
		this.serviceType = serviceType;
	}

}