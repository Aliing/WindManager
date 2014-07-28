/**
 *@filename		RadiusAssignmentAction.java
 *@version
 *@author		Fiona
 *@createtime	2007-9-27 PM 05:08:00
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

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmLoginAuthentication;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.IpAddress;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.RadiusServer;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.CreateObjectAuto;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class RadiusAssignmentAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private List<CheckItem> availableIpAddress;
	
	private boolean ifUsedByAuth;
	
	private static final String RADIUS_ASSIGNMENT_SSID_OF_NETWORKPOLICY = "RADIUS_ASSIGNMENT_SSID_OF_NETWORKPOLICY";
	
	private static final String RADIUS_ASSIGNMENT_SSID_TYPE_FLAG = "RADIUS_ASSIGNMENT_SSID_TYPE_FLAG";
	
    private static final Tracer log = new Tracer(RadiusAssignmentAction.class.getSimpleName());

	public String getSharedSecret()
	{
		return sharedSecret;
	}

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			//prepare some fields for jsonMode
			if (isJsonMode() && 
					"continue".equals(operation)) {
				restoreJsonContext();
			}
			
			if ("new".equals(operation)) {
				if (!isJsonMode() && !setTitleAndCheckAccess(getText("config.title.radius"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				RadiusAssignment raObj = new RadiusAssignment();
				// The checkbox is selected by default in routing network policy and not selected in wireless only network policy.
				if(isJsonMode()) {
				    raObj.setEnableDHCP4RadiusServer(isBindRoutingNetworkPolicy());
				}
                setSessionDataSource(raObj);
				hideCreateItem = "";
				hideNewButton = "none";
				if (isJsonMode() && (null == getParentDomID() || "".equals(getParentDomID()))) {
					MgrUtil.setSessionAttribute(RADIUS_ASSIGNMENT_SSID_OF_NETWORKPOLICY, ssidForRadius);
					MgrUtil.setSessionAttribute(RADIUS_ASSIGNMENT_SSID_TYPE_FLAG, radiusTypeFlag);
				}
				if (isJsonMode()) {
					storeJsonContext();
				}
				return isJsonMode() ? "newRs" : INPUT;
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				if (checkNameExists("radiusName", getDataSource()
						.getRadiusName()) || !updateRules(true)) {
					return isJsonMode() ? "newRs" : INPUT;
				}
				if ("create".equals(operation) && !isJsonMode()) {
					return createBo();
				} else {
					id = createBo(dataSource);
					if (!isJsonMode() || isParentIframeOpenFlg()) {
						setUpdateContext(true);
						return getLstForward();
					} else if (null == getParentDomID() || "".equals(getParentDomID())){
						RadiusAssignment radiusSer = QueryUtil.findBoById(RadiusAssignment.class, id);
						
						ssidForRadius = (Long)MgrUtil.getSessionAttribute(RADIUS_ASSIGNMENT_SSID_OF_NETWORKPOLICY);
						radiusTypeFlag = (Short)MgrUtil.getSessionAttribute(RADIUS_ASSIGNMENT_SSID_TYPE_FLAG);
						
						// lan profile
						if (3 == radiusTypeFlag) {
							PortAccessProfile accObj = QueryUtil.findBoById(PortAccessProfile.class, ssidForRadius);
							accObj.setRadiusAssignment(radiusSer);
							updateBoWithEvent(accObj);
						} else {
							SsidProfile ssidObj = QueryUtil.findBoById(SsidProfile.class, ssidForRadius);
							if (1 == radiusTypeFlag) {
								ssidObj.setRadiusAssignment(radiusSer);
							} else {
								ssidObj.setRadiusAssignmentPpsk(radiusSer);
							}
							updateBoWithEvent(ssidObj);
							
						}
						return "newRs";
					} else {
						return "newRs";
					}
				}
			} else if ("edit".equals(operation)) {
				String returnWord = editBo(this);
				if (dataSource != null && !isJsonMode()) {
					if (getDataSource().getServices().isEmpty()) {
						hideCreateItem = "";
						hideNewButton = "none";
					}
					addLstTitle(getText("config.title.radius.edit")
							+ " '" + getChangedName() + "'");
				}
				if (isJsonMode() && (null == getParentDomID() || "".equals(getParentDomID()))) {
					MgrUtil.setSessionAttribute(RADIUS_ASSIGNMENT_SSID_OF_NETWORKPOLICY, ssidForRadius);
					MgrUtil.setSessionAttribute(RADIUS_ASSIGNMENT_SSID_TYPE_FLAG, radiusTypeFlag);
				}
				if (isJsonMode()) {
					storeJsonContext();
				}
				return isJsonMode() ? "newRs" : returnWord;
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				if (null != dataSource && !updateRules(true)) {
					return isJsonMode() ? "newRs" : INPUT;
				}
				if ("update".equals(operation)) {
					if (isJsonMode()) {
					    updateBo(dataSource);
					    if (null != MgrUtil.getSessionAttribute(RADIUS_ASSIGNMENT_SSID_OF_NETWORKPOLICY)) {
					        ssidForRadius = (Long)MgrUtil.getSessionAttribute(RADIUS_ASSIGNMENT_SSID_OF_NETWORKPOLICY);
					        radiusTypeFlag = (Short)MgrUtil.getSessionAttribute(RADIUS_ASSIGNMENT_SSID_TYPE_FLAG);
					    }
						return "newRs";
					} else {
						return updateBo();
					}
				} else {
					//id = dataSource.getId();
					updateBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				RadiusAssignment profile = (RadiusAssignment) findBoById(
						boClass, cloneId, this);
				profile.setId(null);
				profile.setRadiusName("");
				List<RadiusServer> newserver = new ArrayList<RadiusServer>();
				newserver.addAll(profile.getServices());
				profile.setServices(newserver);
				profile.setOwner(null);
				profile.setVersion(null);
				setSessionDataSource(profile);
				addLstTitle(getText("config.title.radius"));
				return INPUT;
			} else if ("addRadiusServer".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				} else {
					updateRules(false);
					addSelectedRules();
					return isJsonMode() ? "newRs" : INPUT;
				}
			} else if ("removeRadiusServer".equals(operation)
					|| "removeRadiusServerNone".equals(operation)) {
				hideCreateItem = "removeRadiusServerNone".equals(operation) ? ""
						: "none";
				hideNewButton = "removeRadiusServerNone".equals(operation) ? "none"
						: "";
				if (dataSource == null) {
					return prepareBoList();
				} else {
					updateRules(false);
					removeSelectedRules();
					return isJsonMode() ? "newRs" : INPUT;
				}
			} else if ("newIpAddress".equals(operation) || "editIpAddress".equals(operation)) {
				updateRules(false);
				clearErrorsAndMessages();
				addLstForward("radiusAssignment");
				addLstTabId(tabId);
				return operation;
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if ("continue".equals(operation)) {
				hideCreateItem = "";
				hideNewButton = "none";
				return setContinueValue();
			} else if ("import".equals(operation)) {
				addLstForward("radiusAssignment");
				clearErrorsAndMessages();
				return operation;
			} else {
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

    @Override
    protected String prepareActionError(Exception e) throws Exception {
        log.error("prepareActionError", MgrUtil.getUserMessage(e), e);
        addActionError(MgrUtil.getUserMessage(e));
        generateAuditLog(HmAuditLog.STATUS_FAILURE, boClass.getSimpleName()
                + " " + MgrUtil.getUserMessage(e));
        if(isJsonMode()) {
            return "newRs"; 
        } else {
            try {
                return prepareBoList();
            } catch (Exception ne) {
                return prepareEmptyBoList();
            }
        }
    }

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_RADIUS_SERVER_ASSIGN);
		setDataSource(RadiusAssignment.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_AUTH_AAA_CLIENT;
		// avoid cancel back error when open two edit view 
		if(L2_FEATURE_RADIUS_SERVER_ASSIGN.equals(request.getParameter("operation"))
		        || "create".equals(request.getParameter("operation")) 
		        || ("create" + getLstForward()).equals(request.getParameter("operation"))
		        || "update".equals(request.getParameter("operation")) 
		        || ("update"+ getLstForward()).equals(request.getParameter("operation"))) {
		    if(null == getDataSource()) {
		        //setSessionDataSource(new RadiusAssignment());
		    }
		}
	}

	@Override
	public RadiusAssignment getDataSource() {
		return (RadiusAssignment) dataSource;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof RadiusAssignment) {
			RadiusAssignment radius = (RadiusAssignment) bo;
			if (radius.getServices() != null)
				radius.getServices().size();
		}
		return null;
	}

	public int getRadiusNameLength() {
		return getAttributeLength("radiusName");
	}

	public int getCommentLength() {
		return getAttributeLength("description");
	}

	public String getChangedName() {
		return getDataSource().getRadiusName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}

	public int getGridCount() {
		return getDataSource().getServices().isEmpty() ? 3 : 0;
	}
	
	public EnumItem[] getEnumPriority() {
		return RadiusServer.ENUM_RADIUS_PRIORITY;
	}
	
	public EnumItem[] getEnumSerType() {
		return RadiusServer.ENUM_RADIUS_TYPE;
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
			return isJsonMode() ? "newRs" : INPUT;
		}
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		RadiusAssignment source = QueryUtil.findBoById(RadiusAssignment.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<RadiusAssignment> list = QueryUtil.executeQuery(RadiusAssignment.class,
				null, new FilterParams("id", destinationIds), domainId);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (RadiusAssignment profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}
	
			RadiusAssignment up = source.clone();
			if (null == up) {
				continue;
			}
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setRadiusName(profile.getRadiusName());
			up.setOwner(profile.getOwner());
			List<RadiusServer> newserver = new ArrayList<RadiusServer>();
			newserver.addAll(source.getServices());
			up.setServices(newserver);
			hmBos.add(up);
		}
		return hmBos;
	}

    private boolean isBindRoutingNetworkPolicy() {
        Object npObj = MgrUtil.getSessionAttribute(ConfigTemplate.class.getSimpleName() + "Source");
		return null != npObj && ((ConfigTemplate) npObj).getConfigType().isRouterContained();
	}
	   
	protected void addSelectedRules() throws Exception {
		RadiusServer server = new RadiusServer();
		// check the server role cannot repeat
		for (RadiusServer existSer : getDataSource().getServices()) {
			if (RadiusServer.RADIUS_SERVER_TYPE_BOTH == serverType || (serverType == existSer.getServerType()
				|| RadiusServer.RADIUS_SERVER_TYPE_BOTH == existSer.getServerType())) {
				if (existSer.getServerPriority() == serverPriority) {
					addActionError(MgrUtil.getUserMessage("action.error.server.role.repeat"));
					hideCreateItem = "";
					hideNewButton = "none";
					return;
				}
			}
		}
		IpAddress ipClass;
		// select the exist ip object
		if (ipAddress != null && ipAddress != -1) {
			ipClass = findBoById(IpAddress.class,
					ipAddress);
		// create new ip object by input value
		} else {
			for (RadiusServer existSer : getDataSource().getServices()) {
				if (existSer.getIpAddress().getAddressName().equalsIgnoreCase(inputIpValue)) {
					addActionError(MgrUtil.getUserMessage("error.sameObjectExists", "Server IP/Name"));
					hideCreateItem = "";
					hideNewButton = "none";
					return;
				}
			}
			short ipType = ImportCsvFileAction.getIpAddressWrongFlag(inputIpValue) ? IpAddress.TYPE_HOST_NAME : IpAddress.TYPE_IP_ADDRESS;
			ipClass = CreateObjectAuto.createNewIP(inputIpValue, ipType, getDomain(), "For RADIUS Server of AAA Client Setting");
		}
		server.setIpAddress(ipClass);
		
		// refresh the ip address list
		for (CheckItem singleIp : getAvailableIpAddress()) {
			if (ipClass.getId().equals(singleIp.getId())) {
				availableIpAddress.remove(singleIp);
				break;
			}
		}
		server.setSharedSecret(sharedSecret);
		
		server.setServerType(serverType);
		switch (serverType) {
			case RadiusServer.RADIUS_SERVER_TYPE_ACCT:
				server.setAcctPort(acctPort);
				break;
			case RadiusServer.RADIUS_SERVER_TYPE_AUTH:
				server.setAuthPort(authPort);
				break;
			case RadiusServer.RADIUS_SERVER_TYPE_BOTH:
				server.setAuthPort(authPort);
				server.setAcctPort(acctPort);
				break;
		}
		server.setServerPriority(serverPriority);

		getDataSource().getServices().add(server);

		inputIpValue = "";
		sharedSecret = "";
		authPort = 1812;
		serverType = RadiusServer.RADIUS_SERVER_TYPE_BOTH;
		acctPort = 1813;
		serverPriority = RadiusServer.RADIUS_PRIORITY_PRIMARY;
	}

	protected void removeSelectedRules() {
		if (ruleIndices != null) {
			Collection<RadiusServer> removeList = new Vector<RadiusServer>();
			for (String serviceIndex : ruleIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < getDataSource().getServices().size()) {
						removeList
								.add(getDataSource().getServices().get(index));
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getServices().removeAll(removeList);
		}
	}
	
	protected boolean updateRules(boolean ifCheck) {
		boolean containBoth = false;
		if(getDataSource().isEnableDHCP4RadiusServer()) {
		    getDataSource().getServices().clear();
		} else {
		    if (priorities != null) {
		        RadiusServer rdServer;
		        int auth = 0;
		        int acct = 0;
		        for (int i = 0; i < priorities.length
		        && i < getDataSource().getServices().size(); i++) {
		            rdServer = getDataSource().getServices().get(i);
		            if (RadiusServer.RADIUS_SERVER_TYPE_ACCT != rdServer.getServerType()) {
		                rdServer.setAuthPort(authPorts[auth++]);
		            }
		            if (RadiusServer.RADIUS_SERVER_TYPE_AUTH != rdServer.getServerType()) {
		                rdServer.setAcctPort(accoutPorts[acct++]);
		            }
		            
		            if (RadiusServer.RADIUS_SERVER_TYPE_BOTH == rdServer.getServerType() && !sharedSecrets[i].trim().isEmpty()) {
		                containBoth = true;
		            }
		            
		            rdServer.setSharedSecret(sharedSecrets[i]);
		            rdServer.setServerPriority(priorities[i]);
		            //getDataSource().getServices().set(i, rdServer);
		        }
		        if (ifCheck) {
		            for (int i = 0; i < getDataSource().getServices().size()-1; i++) {
		                rdServer = getDataSource().getServices().get(i);
		                RadiusServer rdJServer;
		                for (int j = i+1; j < getDataSource().getServices().size(); j++) {
		                    rdJServer = getDataSource().getServices().get(j);
		                    if (RadiusServer.RADIUS_SERVER_TYPE_BOTH == rdServer.getServerType() || (rdServer.getServerType() == rdJServer.getServerType()
		                            || RadiusServer.RADIUS_SERVER_TYPE_BOTH == rdJServer.getServerType())) {
		                        if (rdJServer.getServerPriority() == rdServer.getServerPriority()) {
		                            addActionError(MgrUtil.getUserMessage("action.error.server.role.repeat"));
		                            return false;
		                        }
		                    }
		                }
		            }
		        }
		    }
		}
		// radius assignment edit
		if (ifCheck && null != getDataSource().getId()) {
			// used by radius proxy
			if (!QueryUtil.executeNativeQuery("SELECT servername FROM radius_proxy_realm WHERE radius_server_id = " + getDataSource().getId(), 1).isEmpty()) {
				if (!containBoth) {
					addActionError(MgrUtil.getUserMessage("error.config.auth.radius.proxy.aaa.client.server"));
					hideCreateItem = "";
					hideNewButton = "none";
					return false;
				}
			}
		}
		return true;
	}

	public List<CheckItem> getAvailableIpAddress() {
		if (null == availableIpAddress) {
			availableIpAddress = getIpObjectsByIpAndName();
			for (RadiusServer oneIp : getDataSource().getServices()) {
					availableIpAddress.remove(new CheckItem(oneIp
							.getIpAddress().getId(), oneIp.getIpAddress()
							.getAddressName()));
				}
			}
		if (availableIpAddress.isEmpty()) {
			availableIpAddress.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
		return availableIpAddress;
	}
	
	public boolean getIfUsedByAuth()
	{
		List<?> boIds = QueryUtil.executeQuery("select id from " + HmLoginAuthentication.class.getSimpleName(), null,
			new FilterParams("radiusAssignment.id", id));
		ifUsedByAuth = !boIds.isEmpty();
		return ifUsedByAuth;
	}

	public void setIfUsedByAuth(boolean ifUsedByAuth)
	{
		this.ifUsedByAuth = ifUsedByAuth;
	}
	
	private Long ipAddress;
	
	private String inputIpValue = "";

	private String sharedSecret;

	private int authPort = 1812;

	private short serverType = RadiusServer.RADIUS_SERVER_TYPE_BOTH;

	private int acctPort = 1813;

	private short serverPriority;
	
	private int[] authPorts;

	private String[] sharedSecrets;

	private int[] accoutPorts;

	private short[] priorities;

	private Collection<String> ruleIndices;
	
	private Long ssidForRadius;
	
	private short radiusTypeFlag;

	public short getRadiusTypeFlag()
	{
		return radiusTypeFlag;
	}

	public void setRadiusTypeFlag(short radiusTypeFlag)
	{
		this.radiusTypeFlag = radiusTypeFlag;
	}

	public Long getSsidForRadius()
	{
		return ssidForRadius;
	}

	public void setSsidForRadius(Long ssidForRadius)
	{
		this.ssidForRadius = ssidForRadius;
	}

	public void setSharedSecret(String sharedSecret) {
		this.sharedSecret = sharedSecret;
	}

	public void setAuthPort(int authPort) {
		this.authPort = authPort;
	}

	public void setAcctPort(int acctPort) {
		this.acctPort = acctPort;
	}

	public void setServerPriority(short serverPriority) {
		this.serverPriority = serverPriority;
	}

	public void setRuleIndices(Collection<String> ruleIndices) {
		this.ruleIndices = ruleIndices;
	}

	public short getServerType() {
		return serverType;
	}

	public void setServerType(short serverType) {
		this.serverType = serverType;
	}

	public int getAuthPort() {
		return authPort;
	}

	public int getAcctPort() {
		return acctPort;
	}

	public Long getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(Long ipAddress) {
		this.ipAddress = ipAddress;
	}

	private String hideCreateItem = "none";

	public String getHideCreateItem() {
		return hideCreateItem;
	}

	private String hideNewButton = "";

	public String getHideNewButton() {
		return hideNewButton;
	}

	public short getServerPriority()
	{
		return serverPriority;
	}
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_RETRY = 2;
	
	public static final int COLUMN_ACCOUNT = 3;
	
	public static final int COLUMN_DESCRIPTION = 4;
	
	/**
	 * get the description of column by id
	 * 
	 * @param id -
	 * @return String
	 */
	@Override
	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
		case COLUMN_NAME:
			code = "config.radiusAssign.radiusName";
			break;
		case COLUMN_RETRY:
			code = "config.radiusAssign.retryInterval";
			break;
		case COLUMN_ACCOUNT:
			code = "config.radiusAssign.updateInterval";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.radiusAssign.description";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(4);
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_RETRY));
		columns.add(new HmTableColumn(COLUMN_ACCOUNT));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
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

	public void setAuthPorts(int[] authPorts)
	{
		this.authPorts = authPorts;
	}

	public void setAccoutPorts(int[] accoutPorts)
	{
		this.accoutPorts = accoutPorts;
	}

	public void setPriorities(short[] priorities)
	{
		this.priorities = priorities;
	}

	public void setSharedSecrets(String[] sharedSecrets)
	{
		this.sharedSecrets = sharedSecrets;
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

}