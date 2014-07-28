package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Collection;
import java.util.Vector;
import java.util.Map.Entry;

import org.hibernate.validator.constraints.Range;

import org.json.JSONObject;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.useraccess.MgmtServiceOption;
import com.ah.bo.useraccess.MulticastForwarding;

import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.wlan.Cwp;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.CountryCode;
import com.ah.util.EnumConstUtil;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.devices.impl.Device;


/*
 * Modification History
 *
 * support VHM
 *     set owner to null when cloning
 * joseph chen 05/07/2008
 */
public class MgmtServiceOptionAction extends BaseAction implements QueryBo{

	private static final long	serialVersionUID	= 1L;

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;

	public static final int COLUMN_SYSTEMLED = 2;

	public static final int COLUMN_RESETBUTTON = 3;

	public static final int COLUMN_PROXYARP = 4;

	public static final int COLUMN_SSID = 5;

	public static final int COLUMN_CONSOLEPORT = 6;

	public static final int COLUMN_CAC = 7;

	public static final int COLUMN_SMARTPOE = 8;

	public static final int COLUMN_USERAUTH = 9;

	public static final int COLUMN_TEMPTHRESHOLD = 10;

	public static final int COLUMN_DESCRIPTION = 11;

	/**
	 * get the description of column by id
	 *
	 * @param id -
	 * @return -
	 * @author Joseph Chen
	 */
	@Override
	protected String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_NAME:
			code = "config.mgmtservice.name";
			break;
		case COLUMN_SYSTEMLED:
			code = "config.mgmtservice.option.thLed";
			break;
		case COLUMN_RESETBUTTON:
			code = "config.mgmtservice.thResetButton";
			break;
		case COLUMN_PROXYARP:
			code = "config.mgmtservice.thProxyArp";
			break;
		case COLUMN_SSID:
			code = "config.mgmtservice.thSsid";
			break;
		case COLUMN_CONSOLEPORT:
			code = "config.mgmtservice.thConsolePort";
			break;
		case COLUMN_CAC:
			code = "config.mgmtservice.thCAC";
			break;
		case COLUMN_SMARTPOE:
			code = "config.mgmtservice.thSmartPoe";
			break;
		case COLUMN_USERAUTH:
			code = "config.mgmtservice.thUserAuth";
			break;
		case COLUMN_TEMPTHRESHOLD:
			code = "config.mgmtservice.thTempThreshold";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.mgmtservice.description";
			break;
		}

		return MgrUtil.getUserMessage(code);
	}
	private boolean expanding_vlanid;
	private Collection<String>	multiplevlanIndices;

	public void setMultiplevlanIndices(Collection<String> multiplevlanIndices) {
		this.multiplevlanIndices = multiplevlanIndices;
	}

	public boolean isExpanding_vlanid() {
		return expanding_vlanid;
	}

	public void setExpanding_vlanid(boolean expanding_vlanid) {
		this.expanding_vlanid = expanding_vlanid;
	}
	//add by nxma
	private String ipInput;
	public void setIpInput(String ipInput) {
		this.ipInput =ipInput;
	}
	private String netmaskInput;
	public void setNetmaskInput(String netmaskInput) {
		this.netmaskInput =netmaskInput;
	}

	private short multicastselect;
	public void setMulticastselect(short multicastselect) {
		this.multicastselect = multicastselect;
	}

	public short getMulticastselect() {
		return multicastselect;
	}
	protected void addNewMultipleVlan() {
		if (ipInput == null || netmaskInput==null) {
			return;
		}
		if(multicastselect==1){
			getDataSource().setMulticastselect(MgmtServiceOption.MULTICAST_BLOCK);
		}
		if(multicastselect==2){
				getDataSource().setMulticastselect(MgmtServiceOption.MULTICAST_ALLOW);
		}
		String start = CLICommonFunc.countIpAndMask(ipInput,
				netmaskInput);
		List<MulticastForwarding> current = getDataSource().getMultipleVlan();
		if (null != current && !current.isEmpty()) {
			for (MulticastForwarding multiforward : current) {
				if (start.equals(multiforward.getIp())) {
					addActionError(MgrUtil.getUserMessage("error.addSameNameObjectExist","'Multicast IP'"));
					return ;

				}
			}
		}
		MulticastForwarding multiforward = new MulticastForwarding();
		multiforward.setIp(start);
		multiforward.setNetmask(netmaskInput);
		getDataSource().getMultipleVlan().add(multiforward);
	}
	protected void removeSelectedMultipleVlan() {
		if (multiplevlanIndices != null) {
			Collection<MulticastForwarding> removeList = new Vector<MulticastForwarding>();
			for (String multiplevlanIndex : multiplevlanIndices) {
				try {
					int index = Integer.parseInt(multiplevlanIndex);
					if (index < getDataSource().getMultipleVlan().size()) {
						removeList.add(getDataSource().getMultipleVlan().get(index));
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getMultipleVlan().removeAll(removeList);
		}
	}

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.mgmtServiceOption"))) {
					return getLstForward();
				}
				setSessionDataSource(new MgmtServiceOption());
				return getReturnPath(INPUT, "mgtOptionJson");
			} else if ("addMultipleVlan".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				addNewMultipleVlan();
				return getReturnPath(INPUT, "mgtOptionJson");
			} else if ("removeMultipleVlan".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				removeSelectedMultipleVlan();
				return getReturnPath(INPUT, "mgtOptionJson");
			}else if ("create".equals(operation)) {
				setSavedValues();
				jsonObject = new JSONObject();
				if (isJsonMode()) {
					if(checkNameExists("mgmtName", getDataSource().getMgmtName())){
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource().getMgmtName()));
						return "json";
					}
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("newObjName", getDataSource().getMgmtName());
					try {
						id = createBo(dataSource);
						jsonObject.put("newObjId", id);
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					jsonObject.put("resultStatus", true);
					return "json";
				}else{
					if(checkNameExists("mgmtName", getDataSource().getMgmtName())){
						return INPUT;
					}
					return createBo();
				}
			} else if ("edit".equals(operation)) {
				setTabId(0);
				if(this.getExConfigGuideFeature() != null && null != dataSource
						&& dataSource.getId() != null && dataSource.getId().equals(this.id)){
					getSessionDataSource();
				}else{
					setSessionDataSource(findBoById(boClass, id, this));
				}
				if (dataSource == null) {
					fw = prepareBoList();
				} else {
					addLstTitle(getText("config.title.mgmtServiceOption.edit")
						+ " '" + getDisplayName() + "'");
					fw = INPUT;
				}
				return getReturnPath(fw, "mgtOptionJson");
			} else if ("update".equals(operation)) {
				jsonObject = new JSONObject();
				if (isJsonMode()) {
					try {
						setSavedValues();
						updateBo();
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					jsonObject.put("resultStatus", true);
					return "json";
				} else {
					setSavedValues();
					return updateBo();
				}
			} else if (("update"+ getLstForward()).equals(operation)) {
				setSavedValues();
				updateBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
			}else if ("clone".equals(operation)) {
				//add by nxma
				long cloneId = getSelectedIds().get(0);
				MgmtServiceOption option = (MgmtServiceOption) findBoById(boClass, cloneId,this);
				option.setId(null);
				option.setMgmtName("");
				option.setOwner(null);    // joseph chen
				option.setVersion(null);  // joseph chen 06/17/2008
				List<MulticastForwarding> multicastForwardings = new ArrayList<MulticastForwarding>();
				for (MulticastForwarding multicastForwarding : option
						.getMultipleVlan()) {
					multicastForwardings.add(multicastForwarding);
				}
				option.setMultipleVlan(multicastForwardings);
				setSessionDataSource(option);
				addLstTitle(getText("config.title.mgmtServiceOption"));
				return INPUT;
			} else if (("create" + getLstForward()).equals(operation)) {
				setSavedValues();
				if(checkNameExists("mgmtName", getDataSource().getMgmtName())){
					return INPUT;
				}
				id = createBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
			} else if (("cancel" + getLstForward()).equals(operation)) {
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForward();
				} else {
					baseOperation();
					return prepareBoList();
				}
			} else if ("newRadius".equals(operation) || "editRadius".equals(operation)) {
				addLstForward("mgmtServiceOption");
				setSavedValues();
				clearErrorsAndMessages();
				return operation;
			} else if ("continue".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				setId(dataSource.getId());
				if (getUpdateContext()) {
					removeLstTitle();
					removeLstForward();
					setUpdateContext(false);
				}
				return getReturnPath(INPUT, "mgtOptionJson");
			} else if ("changeMacAuth".equals(operation)) {
				jsonObject = new JSONObject();
				jsonObject.put("id", "example");
				jsonObject.put("v", getFormatExample());
				return "json";
			} else {
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_MGMT_SERVICE_OPTION);
		setDataSource(MgmtServiceOption.class);
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_MANAGEMENT_SERVICE_OPTIONS;
		keyColumnId = COLUMN_NAME;
	}

	@Override
	public MgmtServiceOption getDataSource() {
		if (null != dataSource && dataSource instanceof MgmtServiceOption) {
			return (MgmtServiceOption) dataSource;
		}
		return null;
	}

	public boolean getDisableName() {
		return getDataSource() != null && getDataSource().getId() != null;
	}

	public String getDisplayName() {
		return getDataSource().getMgmtName().replace("\\", "\\\\").replace("'", "\\'");
	}

	public int getNameLength() {
		return getAttributeLength("mgmtName");
	}

	public EnumItem[] getUserAuthValues(){
		return EnumConstUtil.ENUM_ADMIN_USER_AUTH_TYPE;
	}

	public EnumItem[] getEnumMacDelimiter() {
		return RadiusAssignment.ENUM_RADIUS_MACAUTH_DELIMITER;
	}

	public EnumItem[] getEnumMacStyle() {
		return RadiusAssignment.ENUM_RADIUS_MACAUTH_STYLE;
	}

	public EnumItem[] getEnumMacCase() {
		return RadiusAssignment.ENUM_RADIUS_MACAUTH_CASE;
	}

	public EnumItem[] getEnumSystemLed() {
		return MgmtServiceOption.ENUM_SYSTEM_LED_BRIGHTNESS;
	}

	public EnumItem[] getAuthTypeValues() {
		return Cwp.ENUM_AUTH_METHOD;
	}

	public int getDescriptionLength() {
		return getAttributeLength("description");
	}
	public List<Entry<Integer, String>> getCountryCodeValues(){
		return CountryCode.getCountryCodeList();
	}

	public Range getTempAlarmThresholdRange() {
		return getAttributeRange("tempAlarmThreshold");
	}
	
	public Range getFansOverSpeedAlarmThresholdRange() {
		return getAttributeRange("fansOverSpeedAlarmThreshold");
	}
	
	public Range getFansUnderSpeedAlarmThresholdRange() {
		return getAttributeRange("fansUnderSpeedAlarmThreshold");
	}

	public List<CheckItem> getRadiusAssignServers() {
		return getBoCheckItems("radiusName", RadiusAssignment.class, null, CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}

	public String getDisplayRadius() {
		return EnumConstUtil.ADMIN_USER_AUTHENTICATION_LOCAL == getDataSource().getUserAuth() ? "none" : "";
	}

	private Long radiusId;

	public Long getRadiusId()
	{
		if (null == radiusId && null != getDataSource().getRadiusServer()) {
			radiusId = getDataSource().getRadiusServer().getId();
		}
		return radiusId;
	}

	public void setRadiusId(Long radiusId)
	{
		this.radiusId = radiusId;
	}

	public String getFormatExample() {
		switch (getDataSource().getMacAuthDelimiter()) {
			case RadiusAssignment.RADIUS_MACAUTHDELIMITER_COLON :
				return getMacAuthFormat(":");
			case RadiusAssignment.RADIUS_MACAUTHDELIMITER_DASH :
				return getMacAuthFormat("-");
			case RadiusAssignment.RADIUS_MACAUTHDELIMITER_DOT :
				return getMacAuthFormat(".");
			default :
				break;
		}
		return "For example : 1234567890ab";
	}

	private String getMacAuthFormat(String arg_Delimiter) {
		String strExample = "For example : 1234567890ab";
		switch (getDataSource().getMacAuthStyle()) {
			case RadiusAssignment.RADIUS_MACAUTHSTYLE_NO :
				switch (getDataSource().getMacAuthCase()) {
					case RadiusAssignment.RADIUS_MACAUTHCASE_UPPER :
						strExample = "For example : 1234567890AB";
						break;
					default :
						break;
				}
				break;
			case RadiusAssignment.RADIUS_MACAUTHSTYLE_TWO :
				switch (getDataSource().getMacAuthCase()) {
					case RadiusAssignment.RADIUS_MACAUTHCASE_LOWER :
						strExample = "For example : 1234"+arg_Delimiter+"5678"+arg_Delimiter+"90ab";
						break;
					case RadiusAssignment.RADIUS_MACAUTHCASE_UPPER :
						strExample = "For example : 1234"+arg_Delimiter+"5678"+arg_Delimiter+"90AB";
						break;
					default :
						break;
				}
				break;
			case RadiusAssignment.RADIUS_MACAUTHSTYLE_FIVE :
				switch (getDataSource().getMacAuthCase()) {
					case RadiusAssignment.RADIUS_MACAUTHCASE_LOWER :
						strExample = "For example : 12"+arg_Delimiter+"34"+arg_Delimiter+"56"
							+arg_Delimiter+"78"+arg_Delimiter+"90"+arg_Delimiter+"ab";
						break;
					case RadiusAssignment.RADIUS_MACAUTHCASE_UPPER :
						strExample = "For example : 12"+arg_Delimiter+"34"+arg_Delimiter+"56"
							+arg_Delimiter+"78"+arg_Delimiter+"90"+arg_Delimiter+"AB";
						break;
					default :
						break;
				}
				break;
			default :
				break;
		}
		return strExample;
	}
	
	// add OS detection start
	public String getHideOsDetection() {
		return getDataSource().isEnableOsdetection() ? "" : "none";
	}
	
	public EnumItem[] getOsDetectionMethodDhcp(){
		return new EnumItem[] { new EnumItem(MgmtServiceOption.OS_DETECTION_METHOD_DHCP,
				getText("config.mgmtservice.osdetectionmethod.dhcp")) };
		
	}
	
	public EnumItem[] getOsDetectionMethodHttp(){
		return new EnumItem[] { new EnumItem(MgmtServiceOption.OS_DETECTION_METHOD_HTTP,
				getText("config.mgmtservice.osdetectionmethod.http")) };
	}
	
	public EnumItem[] getOsDetectionMethodBoth(){
		return new EnumItem[] { new EnumItem(MgmtServiceOption.OS_DETECTION_METHOD_BOTH,
				getText("config.mgmtservice.osdetectionmethod.both")) };
	}
	// add OS detection end
	
	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(11);

		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_SYSTEMLED));
		columns.add(new HmTableColumn(COLUMN_RESETBUTTON));
		columns.add(new HmTableColumn(COLUMN_PROXYARP));
		columns.add(new HmTableColumn(COLUMN_SSID));
		columns.add(new HmTableColumn(COLUMN_CONSOLEPORT));
		columns.add(new HmTableColumn(COLUMN_CAC));
		columns.add(new HmTableColumn(COLUMN_SMARTPOE));
		columns.add(new HmTableColumn(COLUMN_USERAUTH));
		columns.add(new HmTableColumn(COLUMN_TEMPTHRESHOLD));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));

		return columns;
	}

	public Range getAirtimePerSecondRange() {
		return this.getAttributeRange("airtimePerSecond");
	}

	public Range getRoamingGuaranteedAirtimeRange() {
		return this.getAttributeRange("roamingGuaranteedAirtime");
	}

	private void setSavedValues() throws Exception {
		if(multicastselect==1){
			getDataSource().setMulticastselect(MgmtServiceOption.MULTICAST_BLOCK);
		}
		if(multicastselect==2){
				getDataSource().setMulticastselect(MgmtServiceOption.MULTICAST_ALLOW);
		}
		if(!getDataSource().isEnableForwardMaxMac()) {
			getDataSource().setForwardMaxMac(MgmtServiceOption.DEFAULT_FORWARD_MAX_MAC);
		}

		if(!getDataSource().isEnableForwardMaxIp()) {
			getDataSource().setForwardMaxIp(MgmtServiceOption.DEFAULT_FORWARD_MAX_IP);
		}

		if (!getDataSource().isEnableTcpMss()) {
			getDataSource().setTcpMssThreshold(0);
		}

		if (EnumConstUtil.ADMIN_USER_AUTHENTICATION_LOCAL != getDataSource().getUserAuth() && null != radiusId
			&& radiusId > -1) {
			RadiusAssignment radius = findBoById(
				RadiusAssignment.class, radiusId);
			getDataSource().setRadiusServer(radius);
		} else  {
			getDataSource().setRadiusServer(null);
		}
	}

	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		MgmtServiceOption source = QueryUtil.findBoById(MgmtServiceOption.class,
				paintbrushSource);
		if (null == source) {
			return null;
		}
		List<MgmtServiceOption> list = QueryUtil.executeQuery(MgmtServiceOption.class,
				null, new FilterParams("id", destinationIds), domainId);

		if (list.isEmpty()) {
			return null;
		}

		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());

		for (MgmtServiceOption profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			MgmtServiceOption option = source.clone();

			if (null == option) {
				continue;
			}

			option.setId(profile.getId());
			option.setVersion(profile.getVersion());
			option.setMgmtName(profile.getMgmtName());
			option.setOwner(profile.getOwner());
			hmBos.add(option);
		}

		return hmBos;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo == null) {
			return null;
		}else if (bo instanceof MgmtServiceOption) {
			MgmtServiceOption mgmtServiceOption = (MgmtServiceOption) bo;

			// Just calling the get method will fetch the LAZY attributes
			// Call additional LAZY methods
			if (mgmtServiceOption.getMultipleVlan() != null) {
				for (int i = 0; i < mgmtServiceOption.getMultipleVlan().size(); i++) {
					mgmtServiceOption.getMultipleVlan().get(i).getIp();
					mgmtServiceOption.getMultipleVlan().get(i).getNetmask();				}
			}
			if (mgmtServiceOption.getMultipleVlan() != null) {
				mgmtServiceOption.getMultipleVlan().size();
			}

		}
		return null;
	}

	private String getReturnPath(String normalPath, String jsonModePath) {
		return isJsonMode() ? jsonModePath : normalPath;
	}

}