package com.ah.bo.hiveap;

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

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "HIVEAP_FILTER")
@org.hibernate.annotations.Table(appliesTo = "HIVEAP_FILTER", indexes = {
		@Index(name = "HIVE_AP_FILTER_OWNER", columnNames = { "OWNER" })
		})
public class HiveApFilter implements HmBo {

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

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String filterName;

	private String userName;

	private Long filterTemplate;

	private Long filterTopology;

	private Long filterHive;

	private String filterIp;

	private int filterProvision;

	private boolean filterProvisionFlag;

	private int filterConfiguration;

	private boolean filterVpnServer;

	private boolean filterRadiusServer;

	private boolean filterDhcpServer;
	
	private boolean filterVpnClient;
	
	private boolean filterRadiusProxy;
	
	private short hiveApType = -2;
	
	private short hiveApModel = -2;
	
	private short filterDeviceType = -2;

	private String displayVer;
	
	private String hostname;
	
	@Column(length = 64)
	private String classificationTag1;

	@Column(length = 64)
	private String classificationTag2;

	@Column(length = 64)
	private String classificationTag3;
	
	private boolean eth0Bridge;
	
	private boolean eth1Bridge;
	
	private boolean red0Bridge;
	
	private boolean agg0Bridge;
	
	@Column(length = 14)
	private String serialNumber;
	
	
	public static final short FILTER_TYPE_MANAGED_DEVICE = 1;
	public static final short FILTER_TYPE_UNMANAGED_DEVICE = 2;
	/**
	 * used to indicate what kind of filter parameters is stored here.
	 * i.e. Managed Device, or Unmanaged Device
	 */
	private short typeOfThisFilter = FILTER_TYPE_MANAGED_DEVICE;

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public String getLabel() {
		return this.filterName;
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

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	public Long getFilterTemplate() {
		return filterTemplate;
	}

	public void setFilterTemplate(Long filterTemplate) {
		this.filterTemplate = filterTemplate;
	}

	public Long getFilterTopology() {
		return filterTopology;
	}

	public void setFilterTopology(Long filterTopology) {
		this.filterTopology = filterTopology;
	}

	public Long getFilterHive() {
		return filterHive;
	}

	public void setFilterHive(Long filterHive) {
		this.filterHive = filterHive;
	}

	public String getFilterIp() {
		return filterIp;
	}

	public void setFilterIp(String filterIp) {
		this.filterIp = filterIp;
	}

	public int getFilterProvision() {
		return filterProvision;
	}

	public void setFilterProvision(int filterProvision) {
		this.filterProvision = filterProvision;
	}

	public boolean getFilterProvisionFlag() {
		return filterProvisionFlag;
	}

	public void setFilterProvisionFlag(boolean filterProvisionFlag) {
		this.filterProvisionFlag = filterProvisionFlag;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public int getFilterConfiguration() {
		return filterConfiguration;
	}

	public void setFilterConfiguration(int filterConfiguration) {
		this.filterConfiguration = filterConfiguration;
	}

	public boolean isFilterVpnServer() {
		return filterVpnServer;
	}

	public void setFilterVpnServer(boolean filterVpnServer) {
		this.filterVpnServer = filterVpnServer;
	}

	public boolean isFilterRadiusServer() {
		return filterRadiusServer;
	}

	public void setFilterRadiusServer(boolean filterRadiusServer) {
		this.filterRadiusServer = filterRadiusServer;
	}

	public boolean isFilterDhcpServer() {
		return filterDhcpServer;
	}

	public void setFilterDhcpServer(boolean filterDhcpServer) {
		this.filterDhcpServer = filterDhcpServer;
	}

	public boolean isFilterVpnClient() {
		return filterVpnClient;
	}

	public void setFilterVpnClient(boolean filterVpnClient) {
		this.filterVpnClient = filterVpnClient;
	}
	
	public short getHiveApType(){
		return this.hiveApType;
	}
	
	public void setHiveApType(short hiveApType){
		this.hiveApType = hiveApType;
	}

	public short getHiveApModel(){
		return this.hiveApModel;
	}
	
	public void setHiveApModel(short hiveApModel){
		this.hiveApModel = hiveApModel;
	}
	
	public short getFilterDeviceType() {
		return filterDeviceType;
	}

	public void setFilterDeviceType(short filterDeviceType) {
		this.filterDeviceType = filterDeviceType;
	}
	
	public String getDisplayVer(){
		return this.displayVer;
	}
	
	public void setDisplayVer(String displayVer){
		this.displayVer = displayVer;
	}
	
	public String getClassificationTag1(){
		return this.classificationTag1;
	}
	
	public void setClassificationTag1(String classificationTag1){
		this.classificationTag1 = classificationTag1;
	}
	
	public String getClassificationTag2(){
		return this.classificationTag2;
	}
	
	public void setClassificationTag2(String classificationTag2){
		this.classificationTag2 = classificationTag2;
	}
	
	public String getClassificationTag3(){
		return this.classificationTag3;
	}
	
	public void setClassificationTag3(String classificationTag3){
		this.classificationTag3 = classificationTag3;
	}
	
	public boolean isEth0Bridge(){
		return this.eth0Bridge;
	}
	
	public void setEth0Bridge(boolean eth0Bridge){
		this.eth0Bridge = eth0Bridge;
	}
	
	public boolean isEth1Bridge(){
		return this.eth1Bridge;
	}
	
	public void setEth1Bridge(boolean eth1Bridge){
		this.eth1Bridge = eth1Bridge;
	}
	
	public boolean isRed0Bridge(){
		return this.red0Bridge;
	}
	
	public void setRed0Bridge(boolean red0Bridge){
		this.red0Bridge = red0Bridge;
	}
	
	public boolean isAgg0Bridge(){
		return this.agg0Bridge;
	}
	
	public void setAgg0Bridge(boolean agg0Bridge){
		this.agg0Bridge = agg0Bridge;
	}

	public boolean isFilterRadiusProxy() {
		return filterRadiusProxy;
	}

	public void setFilterRadiusProxy(boolean filterRadiusProxy) {
		this.filterRadiusProxy = filterRadiusProxy;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public short getTypeOfThisFilter() {
		return typeOfThisFilter;
	}

	public void setTypeOfThisFilter(short typeOfThisFilter) {
		this.typeOfThisFilter = typeOfThisFilter;
	}

}