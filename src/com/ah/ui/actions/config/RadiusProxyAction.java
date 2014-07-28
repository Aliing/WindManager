/**
 *@filename		RadiusProxyAction.java
 *@version
 *@author		Fiona
 *@createtime	2010-5-20 PM 02:27:20
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Range;
import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.RadiusHiveapAuth;
import com.ah.bo.useraccess.RadiusProxy;
import com.ah.bo.useraccess.RadiusProxyRealm;
import com.ah.bo.useraccess.RadiusServer;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.CreateObjectAuto;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.NetTool;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
public class RadiusProxyAction extends BaseAction implements QueryBo
{
	
	private static final long serialVersionUID = 1L;

	// for Router Radius Proxy, hide the NAS settings
    private boolean enable4Router;

	@Override
	public String execute() throws Exception {
		try {
			//prepare some fields for jsonMode
			if (isJsonMode() && 
					("continue".equals(operation)
					|| "continue1".equals(operation))) {
				restoreJsonContext();
			}
			
			String fw;
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.radius.proxy.new"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				// for Router Radius Proxy, hide the NAS settings
				RadiusProxy obj = new RadiusProxy();
				obj.setProxy4Router(enable4Router);
				
                setSessionDataSource(obj);
				
				// add default realm and null realm
				List<RadiusProxyRealm> radiusRealm = new ArrayList<>();
				RadiusProxyRealm defRealm = new RadiusProxyRealm();
				defRealm.setServerName(RadiusProxyRealm.DEFAULT_REALM_NAME);
				radiusRealm.add(defRealm);
				defRealm = new RadiusProxyRealm();
				defRealm.setServerName(RadiusProxyRealm.NULL_REALM_NAME);
				radiusRealm.add(defRealm);
				getDataSource().setRadiusRealm(radiusRealm);
				
				resetDisplayStyle(false);

				if (isJsonMode()) {
					storeJsonContext();
				}
				return getReturnPathWithJsonMode(INPUT, "radiusProxyJson");
			} else if ("create".equals(operation)
					|| ("create" + getLstForward()).equals(operation)) {
				if (isJsonMode()) {
					jsonObject = new JSONObject();
					if (checkNameExists("proxyName", getDataSource().getProxyName())) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource().getProxyName()));
						return "json";
					}
					updateNas();
					String errorMsg = updateRealm(true);
					if (!"".equals(errorMsg)) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", errorMsg);
						return "json";
					}
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("newObjName", getDataSource().getProxyName());
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
				} else {
					if (checkNameExists("proxyName", getDataSource().getProxyName())) {
						return INPUT;
					}
					updateNas();
					String errorMsg = updateRealm(true);
					if (!"".equals(errorMsg)) {
					    resetDisplayStyle(true);
						return INPUT;
					}
					if ("create".equals(operation)) {
						return createBo();
					} else {
						id = createBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("edit".equals(operation)) {
				String returnValue = editBo(this);
				if (dataSource != null) {
					addLstTitle(getText("config.title.radius.proxy.edit")
							+ " '" + this.getChangedName() + "'");
	                   // for Router AAA server, hide the NAS settings
                    ((RadiusProxy)(dataSource)).setProxy4Router(enable4Router);
                    if (((RadiusProxy) (dataSource)).isEnabledIDM()) {
                        RadiusProxyRealm defRealm = new RadiusProxyRealm();
                        defRealm.setServerName(RadiusProxyRealm.NULL_REALM_NAME);
                        ((RadiusProxy) (dataSource)).getRadiusRealm().add(defRealm);
                    }
				}
				if (isJsonMode()) {
					storeJsonContext();
				}
				return getReturnPathWithJsonMode(returnValue, "radiusProxyJson");
			} else if ("update".equals(operation)
					|| ("update" + getLstForward()).equals(operation)) {
				if (isJsonMode()) {
					jsonObject = new JSONObject();
					if (null != dataSource) {
						updateNas();
						String errorMsg = updateRealm(true);
						if (!"".equals(errorMsg)) {
							jsonObject.put("resultStatus", false);
							jsonObject.put("errMsg", errorMsg);
							return "json";
						}
					}
					try {
						updateBo(dataSource);
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					jsonObject.put("resultStatus", true);
					return "json";
				} else {
					if (null != dataSource) {
						updateNas();
						String errorMsg = updateRealm(true);
						if (!"".equals(errorMsg)) {
						    resetDisplayStyle(true);
							return INPUT;
						}
					}
					if ("update".equals(operation)) {
						return updateBo();
					} else {
						updateBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				RadiusProxy profile = (RadiusProxy) findBoById(boClass,
						cloneId, this);
				profile.setId(null);
				profile.setProxyName("");
				profile.setOwner(null);
				profile.setVersion(null);
				setCloneValues(profile, profile);
				setSessionDataSource(profile);
				return INPUT;
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if ("addIpAddress".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				} else {
					updateRealm(false);
					updateNas();
					addSelectedNas();
					return getReturnPathWithJsonMode(INPUT, "radiusProxyJson");
				}
			} else if ("removeIpAddress".equals(operation)
					|| "removeIpAddressNone".equals(operation)) {
				hideCreateItem = "removeIpAddressNone".equals(operation) ? ""
						: "none";
				hideNewButton = "removeIpAddressNone".equals(operation) ? "none"
						: "";
				if (dataSource == null) {
					return prepareBoList();
				} else {
					updateRealm(false);
					updateNas();
					removeSelectedNas();
					return getReturnPathWithJsonMode(INPUT, "radiusProxyJson");
				}
			} else if ("newIpAddress".equals(operation) || "newRadiusServer".equals(operation)
				|| "editIpAddress".equals(operation) || "editRadiusServer".equals(operation)) {
				updateRealm(false);
				updateNas();
				clearErrorsAndMessages();
				
				// save the flag
				getDataSource().setEnabledIDMSession(realmEnableIDM);
				
				addLstForward("radiusProxy");
				return operation;
			} else if ("addRealm".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				} else {
					updateNas();
					updateRealm(false);
					addSelectedRealm();
					return getReturnPathWithJsonMode(INPUT, "radiusProxyJson");
				}
			} else if ("removeRealm".equals(operation)
					|| "removeRealmNone".equals(operation)) {
				hideRmCreateItem = "removeRealmNone".equals(operation) ? ""
						: "none";
				hideRmNewButton = "removeRealmNone".equals(operation) ? "none"
						: "";
				if (dataSource == null) {
					return prepareBoList();
				} else {
					updateNas();
					updateRealm(false);
					removeSelectedRealm();
					return getReturnPathWithJsonMode(INPUT, "radiusProxyJson");
				}
			} else if ("continue".equals(operation)) {
				resetDisplayStyle(false);
				
				// set the flag
				realmEnableIDM = getDataSource().isEnabledIDMSession();
				
				fw = setContinueValue();
				return getReturnPathWithJsonMode(fw, "radiusProxyJson"); 
			} else if ("continue1".equals(operation)) {
				// back from radiusAssignment
				resetDisplayStyle(false);
				
				// set the flag
				realmEnableIDM = getDataSource().isEnabledIDMSession();
				
				fw = setContinueValue();
				return getReturnPathWithJsonMode(fw, "radiusProxyJson"); 
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
		setSelectedL2Feature(L2_FEATURE_RADIUS_SERVER_PROXY);
		setDataSource(RadiusProxy.class);
		keyColumnId = COLUMN_NAME;
		tableId = HmTableColumn.TABLE_CONFIGURATION_RADIUS_PROXY;
	}

	@Override
	public RadiusProxy getDataSource() {
		return (RadiusProxy) dataSource;
	}
	
	private void setCloneValues(RadiusProxy source, RadiusProxy dest) {
		List<RadiusProxyRealm> radiusRealm = new ArrayList<>();
		radiusRealm.addAll(source.getRadiusRealm());
		dest.setRadiusRealm(radiusRealm);
		List<RadiusHiveapAuth> radiusNas = new ArrayList<>();
		radiusNas.addAll(source.getRadiusNas());
		dest.setRadiusNas(radiusNas);
	}

	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		RadiusProxy source = QueryUtil.findBoById(RadiusProxy.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<RadiusProxy> list = QueryUtil.executeQuery(
			RadiusProxy.class, null, new FilterParams("id",
						destinationIds), domainId);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<>(list.size());
		for (RadiusProxy profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			RadiusProxy bo = source.clone();
			if (null == bo) {
				continue;
			}
			bo.setId(profile.getId());
			bo.setVersion(profile.getVersion());
			bo.setProxyName(profile.getProxyName());
			bo.setOwner(profile.getOwner());
			setCloneValues(source, bo);
			hmBos.add(bo);
		}
		return hmBos;
	}

	public Range getDelayRange() {
		return getAttributeRange("retryDelay");
	}
	
	public Range getCountRange() {
		return getAttributeRange("retryCount");
	}
	
	public Range getDeadRange() {
		return getAttributeRange("deadTime");
	}

	public int getNameLength() {
		return getAttributeLength("proxyName");
	}

	public int getDescriptionLength() {
		return getAttributeLength("description");
	}

	public String getChangedName() {
		return getDataSource().getProxyName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}

	public List<CheckItem> getAvailableRadiusServer() {
		return getBoCheckItems("radiusName", RadiusAssignment.class, null, CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}
	
	public EnumItem[] getEnumFormat() {
		return RadiusProxy.ENUM_RADIUS_PROXY_FORMAT;
	}
	
	public int getGridCount() {
		return getDataSource().getRadiusNas().isEmpty() ? 3 : 0;
	}
	
	private String setContinueValue() throws Exception {
		if (getUpdateContext()) {
			removeLstTitle();
			removeLstForward();
			setUpdateContext(false);
		}
		if (dataSource == null) {
			return prepareBoList();
		} else {
			setId(dataSource.getId());
			return INPUT;
		}
	}
	
	/**
	 * For realm
	 */
	private String realmName;
	
	private Long radiusSer;
	
	private boolean realmStrip = true;
	
	private Long[] radiusSers;
	
	private Collection<String> realmStrips;
	
	private Collection<String> realmIndices;
	
	private boolean realmEnableIDM;
	private int realmTlsPort;
	private Collection<String> realmCloudAuths;
	
	protected void addSelectedRealm() throws Exception {
		RadiusProxyRealm realm = new RadiusProxyRealm();
		// check realm name
		for (RadiusProxyRealm existSer : getDataSource().getRadiusRealm()) {
			if (existSer.getServerName().equalsIgnoreCase(realmName)) {
				addActionError(MgrUtil.getUserMessage("error.sameObjectExists", "Realm"));
				resetDisplayStyle(false);
				return;
			}
		}
		realm.setServerName(realmName.toLowerCase());
		
		// set radius server
		if (radiusSer != null && radiusSer > 0) {
			realm.setRadiusServer(QueryUtil.findBoById(RadiusAssignment.class, radiusSer));
		} 
		realm.setStrip(realmStrip);
		
		realm.setUseIDM(realmEnableIDM);
		realm.setTlsPort(realmTlsPort);
		
		getDataSource().getRadiusRealm().add(realm);
		
		realmName = "";
		realmStrip = true;
		realmEnableIDM = false;
		radiusSer = null;
	}
	
	private void resetDisplayStyle(boolean showNewSection) {
	    if(showNewSection) {
	        hideRmCreateItem = "none";
	        hideRmNewButton = "";
	        hideCreateItem = "none";
	        hideNewButton = "";
	    } else {
	        hideRmCreateItem = "";
	        hideRmNewButton = "none";
            hideCreateItem = "";
            hideNewButton = "none";
	    }
	}
	
	protected void removeSelectedRealm() {
		if (realmIndices != null) {
			Collection<RadiusProxyRealm> removeList = new Vector<>();
			for (String serviceIndex : realmIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < getDataSource().getRadiusRealm().size()) {
						RadiusProxyRealm singleRealm = getDataSource()
								.getRadiusRealm().get(index);
						if (singleRealm.getServerName().equalsIgnoreCase(RadiusProxyRealm.NULL_REALM_NAME)
							|| singleRealm.getServerName().equalsIgnoreCase(RadiusProxyRealm.DEFAULT_REALM_NAME)) {
							addActionError(MgrUtil.getUserMessage("error.config.auth.radius.proxy.remove.realm", singleRealm.getServerName()));
						} else {
							removeList.add(singleRealm);
						}
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getRadiusRealm().removeAll(removeList);
		}
	}
	
	protected String updateRealm(boolean ifCheck) {
		String errorMsg = "";
		if (radiusSers != null) {
		    
		    boolean enabledIDM = getDataSource().isEnabledIDM();
		    RadiusProxyRealm nullRealm = null;
		    
			for (int i = 0; i < radiusSers.length
					&& i < getDataSource().getRadiusRealm().size(); i++) {
				RadiusProxyRealm realm = getDataSource().getRadiusRealm().get(i);
				// set radius server
				RadiusAssignment radius = null;
				if (radiusSers[i] != null && radiusSers[i] > 0) {
					radius = QueryUtil.findBoById(RadiusAssignment.class, radiusSers[i], this);
				}
				realm.setRadiusServer(radius);
				
				realm.setUseIDM(false);
				if(null != realmCloudAuths) {
				    Object[] enabledIMDs = realmCloudAuths.toArray();
				    for (Object o : enabledIMDs) {
				        if (!o.toString().equals("false")
				                && i == Integer.valueOf(o.toString())) {
				            realm.setUseIDM(true);
				            enabledIDM = true;
				            break;
				        }
				    }
				}
				
				if (ifCheck) {
					boolean result = true;
					final String serverName = realm.getServerName();
                    if (null == radius) {
                        if(realm.isUseIDM()) {
                            // the server is optional for IDM
                            result = true;
                        } else if(enabledIDM && serverName.equalsIgnoreCase(RadiusProxyRealm.NULL_REALM_NAME)) {
                            // skip to check the NULL realm if the IDM is enabled
                            nullRealm = realm;
                            result = true;
                        } else {
                            errorMsg = MgrUtil.getUserMessage("error.config.auth.radius.proxy.realm.server.required");
                            addActionError(errorMsg);
                            resetDisplayStyle(false);
                            result = false;
                        }
					} 
                    //fix bug 28238
                    else {
				        result = false;
				        // check radius server
				        for (RadiusServer server : radius.getServices()) {
				        	//fix bug 28481
				            if (StringUtils.isNotBlank(server.getSharedSecret())) {
				                result = true;
				                break;
				            }
				        }
				        if (!result) {
				            errorMsg = MgrUtil.getUserMessage("error.config.auth.radius.proxy.realm.server", serverName);
				            addActionError(errorMsg);
				            resetDisplayStyle(false);
				        }
					}
					if (!result) {
						return errorMsg;
					}
				}
				realm.setStrip(false);
				if (realmStrips != null) {
					Object[] obj = realmStrips.toArray();
					for (Object o : obj) {
						if (!o.toString().equals("false")
								&& i == Integer.valueOf(o.toString())) {
							realm.setStrip(true);
							break;
						}
					}
				}
			}
			if(null != nullRealm) {
			    // remove the Null Realm if IDM is enabled.
			    getDataSource().getRadiusRealm().remove(nullRealm);
			}
		}
		return errorMsg;
	}
	
	/*
	 * For NAS
	 */
	public List<CheckItem> getAvailableIpAddress() {
		List<CheckItem> availableIpAddress = getIpObjectsByIpNameAndNet();
		for (RadiusHiveapAuth oneIp : getDataSource().getRadiusNas()) {
			availableIpAddress.remove(new CheckItem(oneIp
				.getIpAddress().getId(), oneIp.getIpAddress()
					.getAddressName()));
		}
		if (availableIpAddress.isEmpty()) {
			availableIpAddress.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
		return availableIpAddress;
	}
	
	private Long ipAddress;
	
	private String inputIpValue = "";

	private String sharekey;

	private String description;
	
	private String[] sharedSecrets;

	private String[] descriptions;

	private Collection<String> ipIndices;
	
	private String hideCreateItem = "none";

	public String getHideCreateItem() {
		return hideCreateItem;
	}

	private String hideNewButton = "";

	public String getHideNewButton() {
		return hideNewButton;
	}
	
	private String hideRmCreateItem = "none";

	public String getHideRmCreateItem() {
		return hideRmCreateItem;
	}

	private String hideRmNewButton = "";

	public String getHideRmNewButton() {
		return hideRmNewButton;
	}
	
	protected void addSelectedNas() throws Exception {
		RadiusHiveapAuth ipAuth = new RadiusHiveapAuth();
		IpAddress ipClass = null;
		// select the exist ip object
		if (ipAddress != null && ipAddress != -1) {
			ipClass = findBoById(IpAddress.class, ipAddress);
		// create new ip object by input value
		} else {
			for (RadiusHiveapAuth existSer : getDataSource().getRadiusNas()) {
				if (existSer.getIpAddress().getAddressName().equalsIgnoreCase(inputIpValue)) {
					addActionError(MgrUtil.getUserMessage("error.sameObjectExists", "IP Address / Host Name"));
					hideCreateItem = "";
					hideNewButton = "none";
					return;
				}
			}
			SingleTableItem ipObj = NetTool.getIpObjectByInput(inputIpValue, true);
			if (null != ipObj) {
				ipClass = CreateObjectAuto.createNewIP(ipObj.getIpAddress(), ipObj.getType(), getDomain(), "For NAS of RADIUS Proxy", ipObj.getNetmask());
			}
		}
		ipAuth.setIpAddress(ipClass);
	
		ipAuth.setSharedKey(sharekey);
		ipAuth.setDescription(description);
		getDataSource().getRadiusNas().add(ipAuth);
		inputIpValue = "";
		sharekey = "";
		description = "";
	}
	
	protected void removeSelectedNas() {
		if (ipIndices != null) {
			Collection<RadiusHiveapAuth> removeList = new Vector<>();
			for (String serviceIndex : ipIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < getDataSource().getRadiusNas().size()) {
						RadiusHiveapAuth singleAuth = getDataSource()
								.getRadiusNas().get(index);
						removeList.add(singleAuth);
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getRadiusNas().removeAll(removeList);
		}
	}
	
	protected void updateNas() {
		if (sharedSecrets != null) {
			for (int i = 0; i < sharedSecrets.length
					&& i < getDataSource().getRadiusNas().size(); i++) {
				getDataSource().getRadiusNas().get(i).setSharedKey(
					sharedSecrets[i]);
			}
		}
		if (descriptions != null) {
			for (int i = 0; i < descriptions.length
					&& i < getDataSource().getRadiusNas().size(); i++) {
				getDataSource().getRadiusNas().get(i).setDescription(
						descriptions[i]);
			}
		}
	}

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;

	public static final int COLUMN_FORMAT = 2;
	
	public static final int COLUMN_RETRY_DELAY = 3;
	
	public static final int COLUMN_RETRY_COUNT = 4;
	
	public static final int COLUMN_DEAD_TIME = 5;

	public static final int COLUMN_DESCRIPTION = 6;

	/**
	 * get the description of column by id
	 * 
	 * @param id -
	 * @return String -
	 */
	@Override
	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
		case COLUMN_NAME:
			code = "config.radiusProxy.name";
			break;
		case COLUMN_FORMAT:
			code = "config.radiusProxy.format";
			break;
		case COLUMN_RETRY_DELAY:
			code = "config.radiusProxy.retry.delay";
			break;
		case COLUMN_RETRY_COUNT:
			code = "config.radiusProxy.retry.count";
			break;
		case COLUMN_DEAD_TIME:
			code = "config.radiusProxy.dead.time";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.ns.description";
			break;
		}

		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<>(6);

		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_FORMAT));
		columns.add(new HmTableColumn(COLUMN_RETRY_DELAY));
		columns.add(new HmTableColumn(COLUMN_RETRY_COUNT));
		columns.add(new HmTableColumn(COLUMN_DEAD_TIME));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}

	public Long getIpAddress()
	{
		return ipAddress;
	}

	public void setIpAddress(Long ipAddress)
	{
		this.ipAddress = ipAddress;
	}

	public String getInputIpValue()
	{
		if (null != ipAddress) {
			for (CheckItem item : getAvailableIpAddress()) {
				if (item.getId().longValue() == ipAddress.longValue()) {
					inputIpValue = item.getValue();
					break;
				}
			}
		}
		return inputIpValue;
	}

	public void setInputIpValue(String inputIpValue)
	{
		this.inputIpValue = inputIpValue;
	}

	public String getSharekey()
	{
		return sharekey;
	}

	public void setSharekey(String sharekey)
	{
		this.sharekey = sharekey;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public void setSharedSecrets(String[] sharedSecrets)
	{
		this.sharedSecrets = sharedSecrets;
	}

	public void setDescriptions(String[] descriptions)
	{
		this.descriptions = descriptions;
	}

	public void setIpIndices(Collection<String> ipIndices)
	{
		this.ipIndices = ipIndices;
	}

	public String getRealmName()
	{
		return realmName;
	}

	public void setRealmName(String realmName)
	{
		this.realmName = realmName;
	}

	public Long getRadiusSer()
	{
		return radiusSer;
	}

	public void setRadiusSer(Long radiusSer)
	{
		this.radiusSer = radiusSer;
	}

	public void setRadiusSers(Long[] radiusSers)
	{
		this.radiusSers = radiusSers;
	}

	public boolean isRealmStrip()
	{
		return realmStrip;
	}

	public void setRealmStrip(boolean realmStrip)
	{
		this.realmStrip = realmStrip;
	}

	public void setRealmStrips(Collection<String> realmStrips)
	{
		this.realmStrips = realmStrips;
	}

	public void setRealmIndices(Collection<String> realmIndices)
	{
		this.realmIndices = realmIndices;
	}

	public boolean isParentIframeOpenFlag4Child() {
		return isContentShownInDlg();
	}
	
	public boolean isEnable4Router() {
        return enable4Router;
    }

    public void setEnable4Router(boolean enable4Router) {
        this.enable4Router = enable4Router;
    }
    
    public boolean isRealmEnableIDM() {
        return realmEnableIDM;
    }

    public int getRealmTlsPort() {
        return realmTlsPort;
    }

    public void setRealmEnableIDM(boolean realmEnableIDM) {
        this.realmEnableIDM = realmEnableIDM;
    }

    public void setRealmTlsPort(int realmTlsPort) {
        this.realmTlsPort = realmTlsPort;
    }
    
    public Collection<String> getRealmCloudAuths() {
        return realmCloudAuths;
    }

    public void setRealmCloudAuths(Collection<String> realmCloudAuths) {
        this.realmCloudAuths = realmCloudAuths;
    }

    private void storeJsonContext() {
		getDataSource().setParentDomID(getParentDomID());
		getDataSource().setParentIframeOpenFlg(isParentIframeOpenFlg());
		getDataSource().setContentShowType(getContentShowType());
	}
	
	private void restoreJsonContext() {
		setParentDomID(getDataSource().getParentDomID());
		setParentIframeOpenFlg(getDataSource().isParentIframeOpenFlg());
		setContentShowType(getDataSource().getContentShowType());
	}
	
	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof RadiusProxy) {
			RadiusProxy radius = (RadiusProxy) bo;
			if (radius.getRadiusRealm() != null)
				radius.getRadiusRealm().size();
			if (radius.getRadiusNas() != null)
				radius.getRadiusNas().size();
		} else if (bo instanceof RadiusAssignment) {
			RadiusAssignment radius = (RadiusAssignment) bo;
			if (radius.getServices() != null)
				radius.getServices().size();
		}
		return null;
	}

}