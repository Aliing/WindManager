package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;

/*
 * @author Chris Scheers
 */

@Entity
@Table(name = "hm_table_column_new", uniqueConstraints = { @UniqueConstraint(columnNames = {"useremail", "tableId", "position" })})
@org.hibernate.annotations.Table(appliesTo = "hm_table_column_new", indexes = {
		@Index(name = "hm_table_column_new_useremail_tableid", columnNames = { "useremail", "tableid" })
		})
public class HmTableColumn implements HmBo {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Version
	private Timestamp version;

	private int				columnId;

	private int				tableId;

	private int				position;

	@Column(length = 128, nullable = false)
	private String			useremail;

	/*
	 * Table IDs
	 */
	/*
	 * 1000 - 1100 hive ap
	 */
	public static final int	TABLE_MANAGED_APS_MONITOR						= 1000;

	public static final int	TABLE_NEW_APS									= 1001;

	public static final int	TABLE_AUTO_DISCOVER_APS							= 1002;

	public static final int	TABLE_MANU_PROVISION_APS						= 1003;

	public static final int	TABLE_ACTIVE_CLIENTS							= 1004;

	public static final int TABLE_MANAGED_APS_CONFIG						= 1005;

	public static final int TABLE_IDP_FRIENDLY_AP							= 1006;

	public static final int TABLE_IDP_ROGUE_AP								= 1007;

	public static final int TABLE_UPDATE_RESULT								= 1008;

	public static final int TABLE_AUTO_PROVISION							= 1009;

	public static final int TABLE_IDP_FRIENDLY_AP_PLAIN						= 1010;

	public static final int TABLE_IDP_ROGUE_AP_PLAIN						= 1011;

	public static final int TABLE_MANAGED_APS_CONFIG_GUID					= 1012;

	public static final int TABLE_MANAGED_VPN_GATEWAYS_CONFIG				= 1013;

	public static final int TABLE_MANAGED_VPN_GATEWAYS_MONITOR				= 1014;

	public static final int TABLE_MANAGED_ROUTERS_CONFIG					= 1015;

	public static final int TABLE_MANAGED_ROUTERS_MONITOR					= 1016;

	public static final int TABLE_MANAGED_DEVICEAPS_CONFIG					= 1017;

	public static final int TABLE_MANAGED_DEVICEAPS_MONITOR					= 1018;

	public static final int	TABLE_WIRED_CLIENTS							= 1019;

	public static final int	TABLE_WIRELESS_CLIENTS							= 1020;

	public static final int TABLE_MANAGED_SWITCHES_CONFIG					= 1021;

	public static final int TABLE_MANAGED_SWITCHES_MONITOR					= 1022;

	public static final int	TABLE_DEVICE_INVENTORY		                    = 1023;
	
	public static final int TABLE_DI_CONFIG_HIVE_APS 						= 1030;
	public static final int TABLE_DI_CONFIG_DEVICE_HIVEAPS					= 1031;
	public static final int TABLE_DI_CONFIG_BRANCH_ROUTERS					= 1032;
	public static final int TABLE_DI_CONFIG_SWITCHES						= 1033;
	public static final int TABLE_DI_CONFIG_VPN_GATEWAYS					= 1034;
	public static final int TABLE_DI_MANAGED_HIVE_APS						= 1035;
	public static final int TABLE_DI_DEVICE_HIVEAPS							= 1036;
	public static final int TABLE_DI_BRANCH_ROUTERS							= 1037;
	public static final int TABLE_DI_SWITCHES								= 1038;
	public static final int TABLE_DI_VPN_GATEWAYS							= 1039;

	/*
	 * 1100 - 1200 fault
	 */
	public static final int	TABLE_ALARM										= 1101;

	public static final int	TABLE_EVENT										= 1102;

	public static final int	TABLE_SUBNETWORK_ALLOCATION						= 1103;

	public static final int TABLE_ONETIMEPASSWORD                           = 1104;

	/*
	 * 2000 - 2099 HM Admin
	 */
	public static final int	TABLE_VHMMANAGEMENT								= 2000;

	public static final int	TABLE_ADMINISTRATORS							= 2001;

	public static final int	TABLE_ADMINGROUP								= 2002;

	public static final int	TABLE_SYSTEMLOG									= 2003;

	public static final int	TABLE_AUDITLOG									= 2004;

	public static final int	TABLE_MAILLIST									= 2005;

	public static final int	TABLE_CLIENTPROPERTY							= 2006;

	public static final int	TABLE_ROGUECLIENT								= 2007;

	public static final int	TABLE_LOCATIONCLIENTWATCH						= 2008;

	public static final int	TABLE_ROGUECLIENT_PLAIN							= 2009;

	public static final int	TABLE_L3FIREWALLLOG							    = 2010;

	public static final int	TABLE_KDDRLLOG								    = 2012;
	
	public static final int TABLE_ADMIN_AUXILIARY_CID_CLIENTS               = 2020;
	
	public static final int	TABL_UPGRADELOG									= 2021;


	/*
	 * 2011 for enrolled client
	 */
	public static final int TABLE_ENROLLED_MDM_CLIENT						= 2011;


	/*
	 * 2100 for wlan policy
	 */
	public static final int TABLE_CONFIGURATION_WLAN_POLICY					= 2100;

	/*
	 * 2101 - 2199 configuration -> network objects
	 */
	public static final int	TABLE_CONFIGURATION_NETWORK_IP	                = 2101;

	public static final int	TABLE_CONFIGURATION_NETWORK_MAC		            = 2102;

    public static final int	TABLE_CONFIGURATION_NETWORK_SERVICE			    = 2103;

	public static final int	TABLE_CONFIGURATION_NETWORK_VLAN		        = 2104;

	public static final int	TABLE_CONFIGURATION_NETWORK_ETHERNET			= 2105;

	public static final int	TABLE_CONFIGURATION_NETWORK_RADIO			    = 2106;

	public static final int	TABLE_CONFIGURATION_NETWORK_ALG			        = 2107;

	public static final int	TABLE_CONFIGURATION_TUNNEL_POLICY			    = 2108;

	public static final int	TABLE_CONFIGURATION_NETWORK_ACCESS			    = 2109;

	public static final int	TABLE_CONFIGURATION_NETWORK_DHCP			    = 2110;

	public static final int	TABLE_LLDPCDPPROFILE			                = 2111;

	public static final int	TABLE_CONFIGURATION_NETWORK_OS_OBJECT			= 2112;

	public static final int	TABLE_CONFIGURATION_NETWORK_DOMAIN_OBJECT		= 2113;

	public static final int	TABLE_CONFIGURATION_DNS_SERVICE			        = 2114;

	public static final int	TABLE_CONFIGURATION_USB_MODEM			        = 2115;

	public static final int	TABLE_CONFIGURATION_ROUTING_PROFILES		    = 2116;

	public static final int	TABLE_CONFIGURATION_RADIUS_OPERATOR_NAME_ATTRIBUTE		    = 2117;

	public static final int	TABLE_CONFIGURATION_NETWORK_PPPOE			    = 2118;
	public static final int	TABLE_CONFIGURATION_NETWORK_BONJOUR_GATEEWAY_SETTINGS			  = 2119;
	public static final int	TABLE_CONFIGURATION_NETWORK_VLAN_GROUP			  = 2120;
	public static final int	TABLE_CONFIGURATION_WIFICLIENT_PREFERRED_SSID	  = 2121;

	public static final int	TABLE_CONFIGURATION_NETWOR_MSTP		    = 2123;

	public static final int	TABLE_CONFIGURATION_NETWOR_PSE_PROFILES	    = 2124;

	/*
	 * 2201 - 2299 configuration -> security policies
	 */
	public static final int TABLE_CONFIGURATION_MAC_DOS						= 2201;

	public static final int TABLE_CONFIGURATION_IP_DOS						= 2202;

	public static final int TABLE_CONFIGURATION_MAC_FILTER					= 2203;

	public static final int TABLE_CONFIGURATION_IP_FILTER					= 2204;

	public static final int TABLE_CONFIGURATION_SERVICE_FILTER				= 2205;

	public static final int TABLE_CONFIGURATION_IP_POLICY				    = 2206;

	public static final int TABLE_CONFIGURATION_MAC_POLICY				    = 2207;

	public static final int TABLE_CONFIGURATION_IDS_POLICY				    = 2208;

	public static final int TABLE_CONFIGURATION_VPN_SERVICE					= 2209;

	public static final int TABLE_CONFIGURATION_AIR_SCREEN_RULE				= 2210;

	public static final int TABLE_CONFIGURATION_AIR_SCREEN_RULE_GROUP		= 2211;

	public static final int TABLE_CONFIGURATION_DEVICE_POLICY				= 2212;

	public static final int TABLE_CONFIGURATION_VPN_NETWORK					= 2213;

	public static final int TABLE_CONFIGURATION_FIREWALL_POLICY				= 2214;

	public static final int TABLE_CONFIGURATION_ROUTING_POLICY              = 2215;

	public static final int TABLE_CONFIGURATION_ROUTING_PROFILE_POLICY       = 2216;

	public static final int TABLE_CONFIGURATION_CONFIG_MDM             		 = 2217;

	public static final int TABLE_CONFIGURATION_MDM_PROFILES           		 = 2218;
	
	public static final int TABLE_CONFIGURATION_CLI_BLOB           		  	 = 2219;
	/*
	 * 2301 - 2399 configuration -> qos policy
	 */
	public static final int	TABLE_CONFIGURATION_QOS_CLISSIFIER_MARKER			= 2301;

	public static final int	TABLE_CONFIGURATION_QOS_CLISSIFIER_MAP				= 2302;

	public static final int	TABLE_CONFIGURATION_QOS_MARKER_MAP					= 2303;

	public static final int	TABLE_CONFIGURATION_QOS_RATE_CONTROL				= 2304;

	/*
	 * 2401 - 2499 configuration -> management service
	 */
	public static final int	TABLE_CONFIGURATION_MANAGEMENT_SERVICE_OPTIONS	    = 2401;

	public static final int	TABLE_CONFIGURATION_MANAGEMENT_SERVICE_SNMP		    = 2402;

    public static final int	TABLE_CONFIGURATION_MANAGEMENT_SERVICE_NTP			= 2403;

	public static final int	TABLE_CONFIGURATION_MANAGEMENT_SERVICE_SYSLOG		= 2404;

	public static final int	TABLE_CONFIGURATION_MANAGEMENT_SERVICE_DNS			= 2405;

	public static final int	TABLE_CONFIGURATION_MANAGEMENT_LOCATION			    = 2406;

	public static final int	TABLE_CONFIGURATION_MANAGEMENT_IP_TRACK			    = 2407;

	/*
	 * 2501 - 2599 configuration -> authentication
	 */
	public static final int TABLE_CONFIGURATION_AUTH_AAA_CLIENT 			    = 2501;

	public static final int TABLE_CONFIGURATION_AUTH_HIVEAP_AAA 			    = 2502;

	public static final int TABLE_CONFIGURATION_AUTH_LOCAL_USER 			    = 2503;

	public static final int TABLE_CONFIGURATION_AUTH_LOCAL_GROUP 			    = 2504;

	public static final int TABLE_CONFIGURATION_AUTH_AD_LDAP    			    = 2505;

	public static final int TABLE_CONFIGURATION_AUTH_USER_ATTRIBUTE 			= 2506;

	public static final int TABLE_CONFIGURATION_AUTHENTICATION_CWP 			    = 2507;

	public static final int TABLE_CONFIGURATION_PRIVATE_PSK		 			    = 2508;

	public static final int	TABLE_CONFIGURATION_CWP_CERTIFICATE					= 2509;

	public static final int	TABLE_CONFIGURATION_RADIUS_PROXY					= 2510;

	public static final int	TABLE_CONFIGURATION_RADIUS_LIBRARY_SIP				= 2511;

	public static final int	TABLE_CONFIGURATION_AUTH_LOCAL_USER_MACAUTH		    = 2512;

	/*
	 * 2200 for hive profile
	 */
	public static final int TABLE_CONFIGURATION_HIVE_PROFILE					= 2200;

	/*
	 * 2300 for user profile
	 */
	public static final int	TABLE_CONFIGURATION_USER_PROFILE					= 2300;

	/*
	 * 2400 for ssid profile
	 */
	public static final int	TABLE_CONFIGURATION_SSID_PROFILE					= 2400;

	/*
	 * 2500 for schedule
	 */
	public static final int	TABLE_CONFIGURATION_SCHEDULER						= 2500;

	/*
	 * 2600 for report
	 */
	public static final int TABLE_CONFIGURATION_REPORT							= 2600;
	public static final int TABLE_REPORT_NETWORKUSAGE							= 2601;
	public static final int TABLE_CONFIGURATION_REPORT_PCI						= 2602;
	/*
	 * 2700 for report
	 */
	public static final int TABLE_CONFIGURATION_CUSTOMREPORT					= 2700;

	public static final int TABLE_MONITOR_PLANNERINFO_REPORT					= 2800;

	public static final int TABLE_REPORT_RECUR									= 2900;


	/*
	 * 3000 to 3100 for GML
	 */
	public static final int TABLE_GML_TEMPORARAY								= 3000;

	public static final int TABLE_GML_PERMANENT									= 3001;

	public static final int TABLE_GML_TEMPLATE									= 3002;

	public static final int TABLE_USER_REPORT									= 3010;

	public static final int	TABLE_CLIENT_MONITOR								= 3011;


	/*
	 * 4000 to 4100 for Teacher view
	 */
	public static final int TABLE_TV_CLASS										= 4000;
	public static final int TABLE_TV_COMPUTERCART								= 4001;
	public static final int TABLE_TV_STUDENTROSTER								= 4002;
	public static final int TABLE_TV_RESOURCEMAP								= 4003;

	/*
	 * 5000 for LAN profile
	 */
	public static final int	TABLE_CONFIGURATION_LAN_PROFILE					= 500;
	public static final int	TABLE_CONFIGURATION_PORTTYPE_PROFILE				= 5001;

	@Transient
	private String			columnDescription;

	public HmTableColumn() {
		super();
	}

	public HmTableColumn(int columnId) {
		this.columnId = columnId;
	}

	public int getColumnId() {
		return columnId;
	}

	public void setColumnId(int columnId) {
		this.columnId = columnId;
	}

	public int getTableId() {
		return tableId;
	}

	public void setTableId(int tableId) {
		this.tableId = tableId;
	}

	public String getColumnDescription() {
		return columnDescription;
	}

	public void setColumnDescription(String columnDescription) {
		this.columnDescription = columnDescription;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof HmTableColumn && 
				new EqualsBuilder().append(columnId, ((HmTableColumn) other).getColumnId())
					.append(tableId, ((HmTableColumn) other).getTableId())
					.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(columnId).append(tableId).toHashCode();
	}

	public String getUseremail() {
		return useremail;
	}

	public void setUseremail(String useremail) {
		this.useremail = useremail;
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

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected(boolean selected) {
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
