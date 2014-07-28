package com.ah.bo.network;

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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "BONJOUR_SERVICE")
@org.hibernate.annotations.Table(appliesTo = "BONJOUR_SERVICE", indexes = {
		@Index(name = "BONJOUR_SERVICE_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class BonjourService implements HmBo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String INTERNET_PRINTING_PROTOCOL_NAME="Internet Printing Protocol";
	public static final String JETDIRECT_NAME="JetDirect";
	public static final String LPR_NAME="LPR";
	public static final String AFP_NAME="AFP";
	public static final String SAMBA_NAME="Samba";
	public static final String FTP_NAME="FTP";
	public static final String ITUNES_NAME="iTunes";
	public static final String BITTORRENT_NAME="BitTorrent";
	public static final String AIRPLAY_NAME="AirPlay";
	public static final String ICHAT_NAME="iChat";
	public static final String SSH_NAME="SSH";
	public static final String TELNET_NAME="Telnet";
	public static final String SHELL_NAME="Shell";
	public static final String AEROHIVE_SERVICES_TCP_NAME="Aerohive TCP Services";
	public static final String AEROHIVE_SERVICES_UDP_NAME="Aerohive UDP Services";
	public static final String AEROHIVE_HTTP_PROXY_CONFIGURATION_NAME="Aerohive HTTP Proxy Configuration";
	public static final String ALL_TCP_SERVICES_NAME="All TCP Services";
	public static final String ALL_UDP_SERVICES_NAME="All UDP Services";
	public static final String ALL_SERVICES_NAME="All Services";
	public static final String REMOTE_AUDIO_OUTPUT_SERVICES_NAME="Remote Audio Output Services";
	public static final String APPLE_TV_SERVICES_NAME="APPLE TV Services";
	public static final String HOME_SHARING_SERVICES_NAME="Home Sharing Services";
	public static final String SLEEP_PROXY_NAME="SleepProxy";
	
	public static final String INTERNET_PRINTING_PROTOCOL_TYPE="*._ipp._tcp.";
	public static final String JETDIRECT_TYPE="*._pdl-datastream._tcp.";
	public static final String LPR_TYPE="*._printer._tcp.";
	public static final String AFP_TYPE="*._afpovertcp._tcp.";
	public static final String SAMBA_TYPE="*._smb._tcp.";
	public static final String FTP_TYPE="*._ftp._tcp.";
	public static final String ITUNES_TYPE="*._daap._tcp.";
	public static final String BITTORRENT_TYPE="*._bittorrent._tcp.";
	public static final String AIRPLAY_TYPE="*._airplay._tcp.";
	public static final String ICHAT_TYPE="*._presence._tcp.";
	public static final String SSH_TYPE="*._ssh._tcp.";
	public static final String TELNET_TYPE="*._telnet._tcp.";
	public static final String SHELL_TYPE="*._shell._tcp.";
	public static final String AEROHIVE_SERVICES_TCP_TYPE="*._aerohive*._tcp.";
	public static final String AEROHIVE_SERVICES_UDP_TYPE="*._aerohive*._udp.";
	public static final String AEROHIVE_HTTP_PROXY_CONFIGURATION_TYPE="*._aerohive-proxy._tcp.";
	public static final String ALL_TCP_SERVICES_TYPE="*._*._tcp.";
	public static final String ALL_UDP_SERVICES_TYPE="*._*._udp.";
	public static final String ALL_SERVICES_TYPE="*._*._*.";
	public static final String REMOTE_AUDIO_OUTPUT_SERVICES_TYPE="*._raop._tcp.";
	public static final String APPLE_TV_SERVICES_TYPE="*._appletv*._tcp.";
	public static final String HOME_SHARING_SERVICES_TYPE="*._home-sharing._tcp.";
	public static final String SLEEP_PROXY_TYPE="*._sleep-proxy._udp.";
	
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Version
	private Timestamp version;

	private String	serviceName;
	
	private String type;
	
	private int typeId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "BONJOUR_SERVICE_CATEGRORY_ID", nullable = true)
	private BonjourServiceCategory bonjourServiceCategory;
	
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public BonjourServiceCategory getBonjourServiceCategory() {
		return bonjourServiceCategory;
	}

	public void setBonjourServiceCategory(
			BonjourServiceCategory bonjourServiceCategory) {
		this.bonjourServiceCategory = bonjourServiceCategory;
	}

	@Transient
	public static String[] getDefaultBonjouServiceName(){
		return new String[]{
				INTERNET_PRINTING_PROTOCOL_NAME,JETDIRECT_NAME,LPR_NAME,AFP_NAME,SAMBA_NAME,FTP_NAME,ITUNES_NAME,
				ITUNES_NAME,BITTORRENT_NAME,AIRPLAY_NAME,ICHAT_NAME,SSH_NAME,TELNET_NAME,SHELL_NAME,
				AEROHIVE_SERVICES_TCP_NAME,AEROHIVE_SERVICES_UDP_NAME,AEROHIVE_HTTP_PROXY_CONFIGURATION_NAME,
				ALL_TCP_SERVICES_NAME,ALL_UDP_SERVICES_NAME,ALL_SERVICES_NAME,REMOTE_AUDIO_OUTPUT_SERVICES_NAME,
				APPLE_TV_SERVICES_NAME,HOME_SHARING_SERVICES_NAME,SLEEP_PROXY_NAME
		};
	}
	
	@Transient
	public static String[] getDefaultBonjouServiceType(){
		return new String[]{
				INTERNET_PRINTING_PROTOCOL_TYPE,JETDIRECT_TYPE,LPR_TYPE,AFP_TYPE,SAMBA_TYPE,FTP_TYPE,ITUNES_TYPE,
				ITUNES_TYPE,BITTORRENT_TYPE,AIRPLAY_TYPE,ICHAT_TYPE,SSH_TYPE,TELNET_TYPE,SHELL_TYPE,
				AEROHIVE_SERVICES_TCP_TYPE,AEROHIVE_SERVICES_UDP_TYPE,AEROHIVE_HTTP_PROXY_CONFIGURATION_TYPE,
				ALL_TCP_SERVICES_TYPE,ALL_UDP_SERVICES_TYPE,ALL_SERVICES_TYPE,REMOTE_AUDIO_OUTPUT_SERVICES_TYPE,
				APPLE_TV_SERVICES_TYPE,HOME_SHARING_SERVICES_TYPE,SLEEP_PROXY_TYPE
		};
	}
	
	@Override
	public boolean equals(Object osObject) {
		if (!(osObject instanceof BonjourService)) {
			return false;
		}
		return null == id ? super.equals(osObject) : id.equals(((BonjourService) osObject).getId());
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
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
	public String getLabel() {
		return serviceName;
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
	public BonjourService clone() {
		try {
			return (BonjourService) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
