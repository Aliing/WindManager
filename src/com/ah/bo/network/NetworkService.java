package com.ah.bo.network;

import java.sql.Timestamp;

import javax.persistence.Column;
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
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/*
 * @author Chris Scheers
 */

@Entity
@Table(name = "NETWORK_SERVICE")
@org.hibernate.annotations.Table(appliesTo = "NETWORK_SERVICE", indexes = {
		@Index(name = "NETWORK_SERVICE_APPID", columnNames = {"APPID"}),
		@Index(name = "NETWORK_SERVICE_SERVICETYPE", columnNames = {"SERVICETYPE"}),
		@Index(name = "NETWORK_SERVICE_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class NetworkService implements HmBo {

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

	@Column(length = 32)
	private String serviceName;

	public static final short PROTOCOL_ID_TCP = 1;

	public static final short PROTOCOL_ID_UDP = 2;

	public static final short PROTOCOL_ID_SVP = 3;

	public static final short PROTOCOL_ID_CUSTOM = 4;

	public static EnumItem[] ENUM_PROTOCOL_ID = MgrUtil.enumItems(
			"enum.protocolId.", new int[] { PROTOCOL_ID_TCP, PROTOCOL_ID_UDP,
					PROTOCOL_ID_SVP, PROTOCOL_ID_CUSTOM });
	@Column( nullable=true )
	private short protocolId;

	private boolean defaultFlag;

	private boolean cliDefaultFlag;

	@Column( nullable=true )
	private Integer protocolNumber;

	@Column( nullable=true )
	private Integer portNumber;

	@Column( nullable=true )
	private Integer idleTimeout;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	public static final short ALG_TYPE_NONE = 1;

	public static final short ALG_TYPE_FTP = 2;

	public static final short ALG_TYPE_TFTP = 3;

	public static final short ALG_TYPE_SIP = 4;
	
	public static final short ALG_TYPE_DNS = 5;
	
	public static final short ALG_TYPE_HTTP = 6;

	public static EnumItem[] ENUM_ALG_TYPE = MgrUtil.enumItems(
			"enum.alg.type.", new int[] { ALG_TYPE_NONE, ALG_TYPE_DNS, ALG_TYPE_FTP,
					ALG_TYPE_HTTP, ALG_TYPE_SIP ,ALG_TYPE_TFTP });

	private short algType = ALG_TYPE_NONE;
	
	//For L7 service
	public static final String L7_SERVICE_NAME_PREFIX = "L7-";
	public static final String NETWORK_SERVICE = "Network Service: ";
	public static final String APPLICATION_SERVICE = "Application Service: ";
	public static final short SERVICE_TYPE_NETWORK = 1;
	public static final short SERVICE_TYPE_L7 = 2;
	public static EnumItem[] ENUM_SERVICE_TYPE = MgrUtil.enumItems("enum.service.type.", new int[]{
			SERVICE_TYPE_NETWORK, SERVICE_TYPE_L7});
	private short serviceType;
	@Column( nullable=true )
	private int appId;
	
	
	public short getServiceType() {
		return serviceType;
	}

	public void setServiceType(short serviceType) {
		this.serviceType = serviceType;
	}
 
	public int getAppId() {
		return appId;
	}
	
	public String getAppIdStr(){
		if (appId == 0) {
			return "";
		}
		return String.valueOf(appId);
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public String getServiceTypeStr() {
		return MgrUtil.getEnumString("enum.service.type." + serviceType);
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
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
		return serviceName;
	}
	
	public String getValue(){
		return serviceName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getIdleTimeout() {
		return idleTimeout;
	}

	public void setIdleTimeout(Integer idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	public Integer getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(Integer portNumber) {
		this.portNumber = portNumber;
	}

	public short getProtocolId() {
		return protocolId;
	}

	public void setProtocolId(short protocolId) {
		this.protocolId = protocolId;
	}

	public Integer getProtocolNumber() {
		return protocolNumber;
	}

	public void setProtocolNumber(Integer protocolNumber) {
		this.protocolNumber = protocolNumber;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public boolean isDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	public String getProtocolStr() {
		String str_ptl = "";
		switch (protocolId) {
		case PROTOCOL_ID_TCP:
		case PROTOCOL_ID_UDP:
		case PROTOCOL_ID_SVP:
			str_ptl = MgrUtil.getEnumString("enum.protocolId." + protocolId);
			break;
		case PROTOCOL_ID_CUSTOM:
			str_ptl = "Protocol (" + protocolNumber + ")";
			break;
		}
		return str_ptl;
	}

	public String getAlgTypeStr() {
		return MgrUtil.getEnumString("enum.alg.type." + algType);
	}

	public String getPortNumberString() {
		if (portNumber == 0) {
			return "N/A";
		}
		return String.valueOf(portNumber);
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public boolean isCliDefaultFlag() {
		return cliDefaultFlag;
	}

	public void setCliDefaultFlag(boolean cliDefaultFlag) {
		this.cliDefaultFlag = cliDefaultFlag;
	}

	public short getAlgType() {
		return algType;
	}

	public void setAlgType(short algType) {
		this.algType = algType;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}
	
	public NetworkService() {
		
	}
	
	public NetworkService(CustomApplication customApp) {
		this.setAppId(customApp.getAppCode());
		this.setServiceName(customApp.getCustomAppShortName());
		this.setServiceType(NetworkService.SERVICE_TYPE_L7);
		this.setIdleTimeout(customApp.getIdleTimeout());
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof NetworkService)) {
			return false;
		}
		return null == id ? super.equals(other) : id.equals(((NetworkService) other).getId());
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
	}
	@Override
	public NetworkService clone() {
		try {
			return (NetworkService) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	@Transient
	private int appType = 0;

	public int getAppType() {
		return appType;
	}

	public void setAppType(int appType) {
		this.appType = appType;
	}
}