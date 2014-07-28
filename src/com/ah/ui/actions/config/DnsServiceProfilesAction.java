/**
 * $Id: DnsServiceProfilesAction.java,v 1.34.46.1.4.2.4.1.4.2.2.1.2.1 2014/06/25 08:54:27 ylin Exp $
 */
package com.ah.ui.actions.config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.util.IPAddress;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.DnsServiceProfile;
import com.ah.bo.network.DnsSpecificSettings;
import com.ah.bo.network.DomainNameItem;
import com.ah.bo.network.DomainObject;
import com.ah.bo.network.IpAddress;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.CreateObjectAuto;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * @author Yunzhi Lin
 * 
 */
public class DnsServiceProfilesAction extends BaseAction implements QueryBo {
	
	private static final int DOMAINS_LIMIT = 32;

	/**
	 * newline in Firefox 4+
	 */
	private static final String NEWLINE_REGEX_FIREFOX4 = "\n";

	/**
	 * newline in Firefox 3.6, IE 8, Chrome 11
	 */
	private static final String NEWLINE_REGEX = "\r\n";

	private static final long serialVersionUID = 635850188678231018L;

	private static final Tracer log = new Tracer(DnsServiceProfilesAction.class.getSimpleName());

	public static final int COLUMN_NAME = 1;

	public static final int COLUMN_DESCRIPTION = 2;
	
	public static final int COLUMN_SPLITDNS = 3;

	private List<CheckItem> availableDnsServers;
	
	final List<Long> removedDomainObjeList = new ArrayList<Long>();
	
	private boolean overrideDNS;
	
	private int ruleOneIndex;
	
	private String externalDnsServerIP1;
	private String externalDnsServerIP2;
	private String externalDnsServerIP3;

	@Override
	public String execute() throws Exception {
		String gForward = globalForward();
		if (null != gForward) {
			return gForward;
		}

		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.dnsService"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				prepareDNSServers();
				setSessionDataSource(new DnsServiceProfile());
				
				return getInputFormValue();
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				// create a DNS service
				return createDnsService();
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				// update a DNS service
				return updateDnsService();
			} else if ("edit".equals(operation)) {
				setSessionDataSource(findBoById(boClass, id, this));
				if (dataSource == null) {
					return prepareBoList();
				} else {
					// prepare the initial values
					prepareDNSServers();
					prepareInitDNSServerValues();

					addLstTitle(getText("config.title.dnsService.edit") + " '" + getChangedName()
							+ "'");
					return getInputFormValue();
				}
			} else if(StringUtils.isNotBlank(operation) && operation.contains("IpAddress")){
				// redirect to IP Address, to save the DomainNames&Advance values
				addLstForward(L2_FEATURE_DNS_SERVICE);
				
				// fixed Bug 15298: store the 'Domain Names' & 'Specific Domains' in session
				if(StringUtils.isNotBlank(domainNames)) {
					setDomainNamesSession(domainNames.replaceAll(NEWLINE_REGEX, NEWLINE_REGEX_FIREFOX4));
				}
				setSpecificDNSSession(specificDomain, specificDNS, -1);
				
				if(getDataSource().getServiceType() == DnsServiceProfile.EXTERNAL_DNS){
					if(!externalDnsServerIP1.isEmpty()){
						getDataSource().setExternalIP1(externalDnsServerIP1);
					}
					
					if(!externalDnsServerIP2.isEmpty()){
						getDataSource().setExternalIP2(externalDnsServerIP2);
					}
					
					if(!externalDnsServerIP3.isEmpty()){
						getDataSource().setExternalIP3(externalDnsServerIP3);
					}
				}
				
				if(operation.contains("new")){
					setNewDNSSession(operation);
					// correct the DNS servers
					correctDNSServers();
					
					return "newIpAddress";
				}else{
					setEditDNSSession(operation);
					
					if(null == tmpDNSServerId){
						addActionErrorMsg(MgrUtil
								.getUserMessage("error.config.dnsService.dnsServer.notdata"));
						prepareDNSServers();
						prepareInitDNSServerValues();
						return getErrorInputFormValue();
					}
					// correct the DNS servers
					correctDNSServers();
					
					return "editIpAddress";
				}
			} else if(StringUtils.isNotBlank(operation) && operation.contains("SpecificDNS")) {
				addLstForward(L2_FEATURE_DNS_SERVICE);
				//log.debug("operation:"+operation +" tmpDNSServerId="+tmpDNSServerId+" dataIndex="+dataIndex);
				//log.debug("specificDomain:"+Arrays.toString(specificDomain) + " specificDNS:"+Arrays.toString(specificDNS));

				// fixed Bug 15298: store the 'Domain Names' & 'Specific Domains' in session
				if(StringUtils.isNotBlank(domainNames)) {
					setDomainNamesSession(domainNames.replaceAll(NEWLINE_REGEX, NEWLINE_REGEX_FIREFOX4));
				}
				setSpecificDNSSession(specificDomain, specificDNS, dataIndex);
				
				if(operation.contains("new")) {
					return "newIpAddress";
				} else {
					if(null == tmpDNSServerId){
						addActionErrorMsg(MgrUtil
								.getUserMessage("error.config.dnsService.dnsServer.notdata"));
						prepareDNSServers();
						prepareInitDNSServerValues();
						return getErrorInputFormValue();
					}
					return "editIpAddress";
				}
			}else if("continue".equals(operation)) {
				String updateDNSSession = getDNSServerUpdateSession();
				if(null == updateDNSSession) {
					continueSpecificSettings();
				} else {
					// get normal DNS server session
					continueNormalDNSSettings(updateDNSSession);
				}
				
				return getInputFormValue();
			} else if("remove".equals(operation)){
				List<DnsServiceProfile> list = getRemovedDNSServices();
				baseOperation();
				removeReferenceObjs(list);
				
				return prepareBoList();
			} else if("clone".equals(operation)) {
				prepareCloneObject();
				return INPUT;
			} else if (("cancel" + getLstForward()).equals(operation)) {
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForward();
				} else {
					baseOperation();
					return prepareBoList();
				}
			}else {
				boolean flag = baseOperation();
				// remove the unused Domain Object after paint-brush operation
				if(flag && "paintbrush".equals(operation)) {
					QueryUtil.removeBos(DomainObject.class, removedDomainObjeList);
				}
				return prepareBoList();
			}
		} catch (Exception e) {
			if(isJsonMode()) {
				return isParentIframeOpenFlg() ? "jsoninput" : "json";
			} else {
				return prepareActionError(e);
			}
		}
	}

	// ----------------methods for operation ---------------------//
	private String getInputFormValue() {
		return isJsonMode() ? "jsoninput" : INPUT;
	}
	private String getErrorInputFormValue() {
		return isJsonMode() ? (isParentIframeOpenFlg()? "jsoninput" : "json") : INPUT;
	}
	
	private String createDnsService() throws Exception {
		String serviceName = getDataSource().getServiceName();
		if (StringUtils.isNotBlank(serviceName)) {
			// check service name
			String trServiceName = serviceName.trim();
			
			if (checkNameExists("serviceName", trServiceName)) {
				prepareDNSServers();
				if(isJsonMode()) {
					addActionErrorMsg(MgrUtil.getUserMessage("error.objectExists", trServiceName));
				}
				return getErrorInputFormValue();
			}
			if (isServiceNameExcced(trServiceName)) {
				prepareDNSServers();
				return getErrorInputFormValue();
			}

			getDataSource().setServiceName(trServiceName);
			
			// handle the Domain Names&&Advance section at last
			switch(getDataSource().getServiceType()){
				case DnsServiceProfile.SAME_DNS:
					// validate External(no split DNS section)
					if(!validateExternal(true)) {
						return getErrorInputFormValue();
					}
					reverseDNSServers();
					break;
				case DnsServiceProfile.SEPARATE_DNS:
			        if(!validateExternal(false)) {
			            return getErrorInputFormValue();
			        }
					// validate Workspace(split section)
					if(!validateWorkspace(false)) {
						return getErrorInputFormValue();
					}
					// update the Domain Names
					updateWorkspace();
					// update the Advance section
					updateSpecificDNSServers();
					break;
				case DnsServiceProfile.EXTERNAL_DNS:
					reverseDNSServers();
					break;
			}
			
			if ("create".equals(operation)) {
				createBo(dataSource);
				return prepareBoList();
			} else {
				id = createBo(dataSource);
				setUpdateContext(true);
				if(isJsonMode() && !isParentIframeOpenFlg()) {
					jsonObject = new JSONObject();
					jsonObject.put("succ", true);
					jsonObject.put("addedId", id);
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("addedName", ((DnsServiceProfile)(dataSource)).getServiceName());
					jsonObject.put("overrideDNS", overrideDNS);
					return "json";
				} else {
					return getLstForward();
				}
			}
		} else {
			addActionErrorMsg(MgrUtil.getUserMessage("error.config.dnsService.noServiceName"));
			return getErrorInputFormValue();
		}
	}

	private String updateDnsService() throws Exception {
		// validate Workspace(split section)
		if(!validateWorkspace(true)) {
			return getErrorInputFormValue();
		}
		// validate External(no split DNS section)
		if(!validateExternal(true)) {
			return getErrorInputFormValue();
		}
		
		// handle the Domain Names&&Advance section at last
		switch(getDataSource().getServiceType()){
			case DnsServiceProfile.SAME_DNS:
				reverseDNSServers();
				break;
			case DnsServiceProfile.SEPARATE_DNS:
				// update the Domain Names
				updateWorkspace();
				// update the Advance section
				updateSpecificDNSServers();
				break;
			case DnsServiceProfile.EXTERNAL_DNS:
				reverseDNSServers();
				break;
		}
		
		if ("update".equals(operation)){
			updateBo(dataSource);
			return prepareBoList();
		} else {
			updateBo(dataSource);
			setUpdateContext(true);
			if(isJsonMode() && !isParentIframeOpenFlg()) {
				jsonObject = new JSONObject();
				jsonObject.put("succ", true);
				jsonObject.put("overrideDNS", overrideDNS);
				return "json";
			} else {
				return getLstForward();
			}
		}
	}

	private void updateSpecificDNSServers() {
		if (null != specificDomain && null !=specificDNS 
				&&specificDomain.length == specificDNS.length) {
			List<DnsSpecificSettings> specificSettingsList = new ArrayList<DnsSpecificSettings>();
			for (int index = 0; index < specificDomain.length; index++) {
				if(StringUtils.isBlank(specificDomain[index]) && StringUtils.isBlank(specificDNS[index])){
					continue;
				}
				DnsSpecificSettings specificSettings = new DnsSpecificSettings();
				specificSettings.setDomainName(specificDomain[index].trim());
				specificSettings.setDnsServer(getDNSServerByName(specificDNS[index],
						getDataSource().getServiceName(), true, true));
				
				specificSettingsList.add(specificSettings);
			}
			getDataSource().setSpecificInfos(specificSettingsList);
		} else {
			getDataSource().setSpecificInfos(new ArrayList<DnsSpecificSettings>());
		}
	}

	private void updateWorkspace() throws Exception {
		String[] domainNameArray = getDomainNamesArray();
		String objName = null == getDataSource().getDomainObj() ? null : getDataSource()
				.getDomainObj().getObjName();
		DomainObject domainObject = getDomainNamesFromDB(objName, domainNameArray);
		if(null != domainObject) {
			getDataSource().setDomainObj(domainObject);
		}
	}

	private String[] getDomainNamesArray() {
		domainNames = domainNames.replaceAll(NEWLINE_REGEX, NEWLINE_REGEX_FIREFOX4);
		String[] domainNameArray = domainNames.split(NEWLINE_REGEX_FIREFOX4);
		log.debug(">>>the domainNames is "+Arrays.toString(domainNameArray));
		return domainNameArray;
	}
	
	private boolean validateWorkspace(boolean updateFlag) throws Exception {
		// distinguish mode
		if (getDataSource().getServiceType() == DnsServiceProfile.SEPARATE_DNS) {
			
			//
			int domainCount = 0;
			// check the Domain Names
			if (StringUtils.isBlank(domainNames)) {
				addActionErrorMsg(MgrUtil
						.getUserMessage("error.config.dnsService.required.domainNames"));
				resetDNSValues(updateFlag);
				return false;
			} else {
				for (String domainName : getDomainNamesArray()) {
					if(StringUtils.isNotBlank(domainName)) {
						domainCount++;
					}
					
					if(StringUtils.isNotBlank(domainName) && domainName.length() > HmBo.DEFAULT_STRING_LENGTH){
						addActionErrorMsg(MgrUtil
								.getUserMessage("error.config.dnsService.domainNames.exceed"));
						resetDNSValues(true);
						return false;
					}
				}
			}
			
			// check the Advance section
			if(specificDomain != null && specificDNS != null) {
				if (specificDomain.length == specificDNS.length) {
					for (int index = 0; index < specificDomain.length; index++) {
						if ((StringUtils.isBlank(specificDomain[index]) && StringUtils
								.isNotBlank(specificDNS[index]))
								|| (StringUtils.isNotBlank(specificDomain[index]) && StringUtils
										.isBlank(specificDNS[index]))) {
							addActionErrorMsg(MgrUtil
									.getUserMessage("error.config.dnsService.advanced.nomatch"));
							resetDNSValues(updateFlag);
							return false;
						} else {
							if(StringUtils.isNotBlank(specificDomain[index]) 
									&& StringUtils.isNotBlank(specificDNS[index])) {
								for(int anotherIndex = index-1; anotherIndex >= 0; anotherIndex--) {
									if(specificDomain[index].equalsIgnoreCase(specificDomain[anotherIndex])
											&& specificDNS[index].equals(specificDNS[anotherIndex])) {
										addActionErrorMsg(MgrUtil.getUserMessage("error.config.dnsService.advanced.specificpaire.same"));
										resetDNSValues(updateFlag);
										return false;
									}
								}
								domainCount++;
							}
						}
					}
				} else {
					addActionErrorMsg(MgrUtil
							.getUserMessage("error.config.dnsService.advanced.nomatch"));
					resetDNSValues(updateFlag);
					return false;
				}
			}
			
			// check the Domain limit
			if(domainCount > DOMAINS_LIMIT) {
				addActionErrorMsg(MgrUtil
						.getUserMessage("error.config.dnsService.domains.exceed"));
				resetDNSValues(updateFlag);
				return false;
			}

			// handle the internal DNS servers
			if (!setDNSServerByType(true)) {
				resetDNSValues(updateFlag);
				return false;
			}
		}
		return true;
	}

	/**
	 * Reset the DNS service values
	 * @author Yunzhi Lin
	 * - Time: Sep 21, 2011 4:17:20 PM
	 * @param updateFlag -
	 * @throws Exception -
	 */
	private void resetDNSValues(boolean updateFlag) throws Exception {
		prepareDNSServers();
		if(updateFlag) {
			// need to initial the editing Object information
			prepareInitDNSServerValues();
		} else {
			initDNSSpecificEntry();
		}
	}
	
	private void initDNSSpecificEntry() {
		if(specificDomain != null && specificDNS != null) {
			int entryength = specificDomain.length;
			if(specificDNS.length > entryength) {
				entryength = specificDNS.length;
			}
			specificDNSPair.clear();
			SpecificDNSPair element;
			for (int i = 0; i < entryength; i++) {
				element = new SpecificDNSPair();
				String domainName = "";
				String dnsName = "";
				if (i < specificDomain.length 
						&& StringUtils.isNotBlank(specificDomain[i])) {
					domainName = specificDomain[i];
				}
				if (i < specificDNS.length 
						&& StringUtils.isNotBlank(specificDNS[i])) {
					dnsName = specificDNS[i];
				}
				element.setSpecificDomain(domainName);
				element.setSpecificDNS(dnsName);
				
				specificDNSPair.add(element);
			}
			specificDomain = null;
			specificDNS = null;
		}
	}
	
	private boolean validateExternal(boolean updateFlag) throws Exception {
		// handle external DNS section
		if (getDataSource().isExternalSepcServerType()) {
			// handle the external DNS servers
			if (!setDNSServerByType(false)) {
				prepareDNSServers();
				if(updateFlag) {
					// need to initial the editing Object information
					prepareInitDNSServerValues();
				}
				return false;
			}
		} else {
			// reset the external DNS servers (when LocalDNS or OpenDNS)
			getDataSource().setExternalDns1(null);
			getDataSource().setExternalDns2(null);
			getDataSource().setExternalDns3(null);
		}
		return true;
	}

	private DomainObject getDomainNamesFromDB(String dObjName, String[] domainNameArray)
			throws Exception {
		if(StringUtils.isBlank(dObjName)) {
			dObjName = generateDomainObjectName();
		}
		DomainObject dObject = QueryUtil.findBoByAttribute(DomainObject.class, "objName", dObjName,
				getDomain().getId());
		if (null == dObject) {
			// create a DomainObject
			dObject = new DomainObject();
			dObject.setOwner(getDomain());
			dObject.setObjName(dObjName);
			List<DomainNameItem> dItems = new ArrayList<DomainNameItem>();
			for (String dItemName : domainNameArray) {
				if (StringUtils.isNotBlank(dItemName) 
						&& dItemName.length() <= HmBo.DEFAULT_STRING_LENGTH) {
					DomainNameItem dItem = new DomainNameItem();
					dItem.setDomainName(dItemName);
					dItems.add(dItem);
				}
			}
			dObject.setItems(dItems);
			
			dObject.setAutoGenerateFlag(true);

			QueryUtil.createBo(dObject);
		} else {
			List<DomainNameItem> dItems = new ArrayList<DomainNameItem>();
			for (String dItemName : domainNameArray) {
				if (StringUtils.isNotBlank(dItemName)
						&& dItemName.length() <= HmBo.DEFAULT_STRING_LENGTH) {
					DomainNameItem dItem = new DomainNameItem();
					dItem.setDomainName(dItemName);
					dItems.add(dItem);
				}
			}
			dObject.setItems(dItems);
			dObject.setAutoGenerateFlag(true);
			
			QueryUtil.updateBo(dObject);
		}
		return dObject;
	}
	
	private boolean isServiceNameExcced(String serviceName) throws JSONException {
		boolean exceed = serviceName.length() > getServiceNameLength();
		if (exceed) {
			addActionErrorMsg(MgrUtil.getUserMessage("error.config.dnsService.serviceName.exceed"));
		}
		return exceed;
	}

	/**
	 * generate a DomainNames object name by date time
	 * 
	 * @author Yunzhi Lin - Time: Jun 16, 2011 6:25:54 PM
	 * @return -
	 */
	private String generateDomainObjectName() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");
		Long currentOwnerId = getDomain().getId();
		if (null == currentOwnerId) {
			currentOwnerId = 0L;
		}
		return "DNS_" + currentOwnerId + "_" + dateFormat.format(new Date());
	}

	private void continueNormalDNSSettings(String updateDNSSession) throws Exception {
		// select the radio button if it is external DNS Server
		if(null != tmpDNSServerId &&  updateDNSSession.contains(EXTERNAL_DNS_OPERATION_SUFFIX) && getDataSource().getServiceType() != DnsServiceProfile.EXTERNAL_DNS) {
			getDataSource().setExternalServerType(DnsServiceProfile.SPECIFIC_DNS_TYPE);
		} 
		// prepare the initial values
		prepareDNSServers();
		prepareInitDNSServerValues();
		
		if (getUpdateContext()) {
			removeLstTitle();
			removeLstForward();
			setUpdateContext(false);
		}
		setId(dataSource.getId());
		setTabId(getLstTabId());
		
		log.debug("return from IpAddress : "+tmpDNSServerId);
		if(null != tmpDNSServerId) {
			setCurrentDNSServer(tmpDNSServerId, updateDNSSession);
		}
		// fixed Bug 15298
		restoreSpecificDomains();
		domainNames = getDomainNamesSession();
	}
	
	private void restoreSpecificDomains() throws Exception {
		String[] tempDomains = getSpecificDomainsSession();
		String[] tempDNSNames = getSpecificDNSNameSession();
		
		if(null != tempDomains && null != tempDNSNames) {
			specificDNSPair.clear();
			int domainSize = tempDomains.length;
			int dnsSize = tempDNSNames.length;
			int size = domainSize > dnsSize ? domainSize :dnsSize;
			// reset the values in pair
			for(int i=0; i<size; i++) {
				SpecificDNSPair pair = new SpecificDNSPair();
				if (i < domainSize) {
					pair.setSpecificDomain(tempDomains[i]);
				} else {
					pair.setSpecificDomain("");
				}
				if (i < dnsSize) {
					pair.setSpecificDNS(tempDNSNames[i]);
					pair.setSpecificDNSId(getDNSServerId(tempDNSNames[i]));
				} else {
					pair.setSpecificDNS("");
					pair.setSpecificDNSId(-1L);
				}
				specificDNSPair.add(pair);
			}
		}
	}

	private void continueSpecificSettings() throws Exception {
		// prepare the initial values
		prepareDNSServers();
		prepareInitDNSServerValues();
		
		if (getUpdateContext()) {
			removeLstTitle();
			removeLstForward();
			setUpdateContext(false);
		}
		setId(dataSource.getId());
		setTabId(getLstTabId());
		log.debug("return from IpAddress : "+tmpDNSServerId);
		// fixed Bug 15298
		restoreSpecificDomains4SpecificIPAdress();
		domainNames = getDomainNamesSession();
	}

	/**
	 * restore 'Specific Domains' from session
	 * @author Yunzhi Lin
	 * - Time: Oct 21, 2011 2:29:40 PM
	 * @throws Exception -
	 */
	private void restoreSpecificDomains4SpecificIPAdress() throws Exception {
		String[] tempDomains = getSpecificDomainsSession();
		String[] tempDNSNames = getSpecificDNSNameSession();
		int tempDataIndex = getSpecificDataIndex();
//			log.debug("temp Domains:" + Arrays.toString(tempDomains) + " temp DNS:"
//					+ Arrays.toString(tempDNSNames) + " temp index:" + tempDataIndex);
		
		if(null != tempDomains && null != tempDNSNames) {
			int size = tempDomains.length;
			if(tempDataIndex > size-1) {
				log.error("continueSpecificSettings","Get an error data index. Size:"+size+" index:"+tempDataIndex);
				return;
			}
			int dbSize = specificDNSPair.size();
			// recount the pair size
			if (size > dbSize) {
				for (int j = 0; j < size - dbSize; j++) {
					specificDNSPair.add(new SpecificDNSPair());
				}
			} else if (size < dbSize) {
				for (int j = 0; j < size - dbSize; j++) {
					specificDNSPair.remove(j);
				}
			}
			// reset the values in pair
			for(int i=0; i<size; i++) {
				SpecificDNSPair pair = specificDNSPair.get(i);
				pair.setSpecificDomain(tempDomains[i]);
				if(tempDataIndex == i && null != tmpDNSServerId) {
					String serverName = getDNSServerName(tmpDNSServerId);
					if(null != serverName) {
						pair.setSpecificDNS(serverName);
						pair.setSpecificDNSId(tmpDNSServerId);
					}
				} else {
					pair.setSpecificDNS(tempDNSNames[i]);
					pair.setSpecificDNSId(getDNSServerId(tempDNSNames[i]));
				}
			}
		}
	}
	
	/**
	 * TA610: DNS profile paintbrush operation
	 * @author Yunzhi Lin
	 */
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSourceId, Set<Long> destinationIds) {
		DnsServiceProfile sourceObj = QueryUtil.findBoById(DnsServiceProfile.class, paintbrushSourceId, this);
		if (null == sourceObj) {
			return null;
		}
		List<DnsServiceProfile> list = QueryUtil.executeQuery(DnsServiceProfile.class, null,
				new FilterParams("id", destinationIds), domainId, this);
		if (null == list || list.isEmpty()) {
			return null;
		}
		List<HmBo> paintedList = new ArrayList<HmBo>();
		// clear need to remove Domain Object list 
		removedDomainObjeList.clear();
		for (DnsServiceProfile destObj : list) {
			if (destObj.getId().equals(paintbrushSourceId)) {
				continue;
			}
			DnsServiceProfile cloneObj = sourceObj.clone();
			if(null == cloneObj) {
				continue;
			}
			cloneObj.setId(destObj.getId());
			cloneObj.setVersion(destObj.getVersion());
			cloneObj.setServiceName(destObj.getServiceName());
			cloneObj.setOwner(destObj.getOwner());
			// handle the Domain Object(which is no a drop-box list element)
			DomainObject domainObj = cloneObj.getDomainObj();
			DomainObject destDomainObj = destObj.getDomainObj();
			if(null == domainObj) {
				if(null != destDomainObj) {
					// if the source doesn't have a domain object, add the old object id to remove list
					removedDomainObjeList.add(destDomainObj.getId());
				}
			} else {
				// need to change the Domain Object value
				List<String> domainItems=new ArrayList<String>();
				for(DomainNameItem items:domainObj.getItems()) {
					domainItems.add(items.getDomainName());
				}
				String[] domainNameArray= domainItems.toArray(new String[domainItems.size()]);
				log.debug("domain object domainNameArray:"+Arrays.toString(domainNameArray));
				if(null == destDomainObj) {
					// create a new Domain Object
					try {
						DomainObject domainObject = getDomainNamesFromDB(null, domainNameArray);
						cloneObj.setDomainObj(domainObject);
					} catch (Exception e) {
						log.error("Error when create a new Domain Object.", e);
					}
				} else {
					// change the old Domain Object
					try {
						DomainObject domainObject = getDomainNamesFromDB(destDomainObj.getObjName(), domainNameArray);
						cloneObj.setDomainObj(domainObject);
					} catch (Exception e) {
						log.error("Error when update the Domain Object.", e);
					}
				}
			}
			
			List<DnsSpecificSettings> specificInfos = new ArrayList<DnsSpecificSettings>();
			specificInfos.addAll(sourceObj.getSpecificInfos());
			cloneObj.setSpecificInfos(specificInfos );
			
			paintedList.add(cloneObj);
		}
		
		return paintedList;
	}
	
	private List<DnsServiceProfile> getRemovedDNSServices() {
		if (getAllSelectedIds().size() <= 0) {
			return null;
		}
		List<DnsServiceProfile> list = new ArrayList<DnsServiceProfile>();
		DnsServiceProfile dnsProfile;
		for (Long selectedId : getAllSelectedIds()) {
			try {
				dnsProfile = QueryUtil.findBoById(DnsServiceProfile.class, selectedId, this);
				if(null != dnsProfile){
					list.add(dnsProfile);
				}
			} catch (Exception e) {
				log.error("Get the DNS Service Error.", e);
			}
		}
		return list;
	}

	private void removeReferenceObjs(List<DnsServiceProfile> dnsServicesList) {
		if(null == dnsServicesList) {
			return;
		}
		DomainObject dmObj;
		for (DnsServiceProfile dnsProfile : dnsServicesList) {
			try {
				if(null != dnsProfile){
					dmObj = dnsProfile.getDomainObj();
					if(null != dmObj) {
						QueryUtil.removeBo(DomainObject.class, dmObj.getId());
					}
				}
			} catch (Exception e) {
				log.error("Cannot remove the DomainObject object.", e);
			}
		}
	}
	
	private void prepareCloneObject() throws Exception {
		long cloneId = getSelectedIds().get(0);
		DnsServiceProfile clonedObj = findBoById(DnsServiceProfile.class, cloneId, this);
		clonedObj.setId(null);
		clonedObj.setServiceName("");
		clonedObj.setOwner(null);
		clonedObj.setVersion(null);
		DomainObject domainObj = clonedObj.getDomainObj();
		if(null != domainObj) {
			domainObj.setId(null);
			domainObj.setVersion(null);
			domainObj.setOwner(null);
			domainObj.setObjName(null);
		}
		
		setSessionDataSource(clonedObj);
		
		prepareDNSServers();
		prepareInitDNSServerValues();
	}
	
	///==========Sessions for Domain Names: start=============///
	private void setDomainNamesSession(String domainNames) {
		if(StringUtils.isNotBlank(domainNames) 
		        && !domainNames.equals(MgrUtil.getUserMessage("config.title.dnsService.domainNames.hint"))) {
			MgrUtil.setSessionAttribute(boClass.getSimpleName() + "_Domain_Names", domainNames);
		}
	}
	private String getDomainNamesSession() {
		Object obj = MgrUtil.getSessionAttribute(boClass.getSimpleName() + "_Domain_Names");
		if(null == obj) {
			return null;
		} else {
			MgrUtil.removeSessionAttribute(boClass.getSimpleName() + "_Domain_Names");
			return (String) obj;
		}
	}
	///==========Sessions for Domain Names: end=============///
	
	///==========Sessions for Specific settings: start=============///
	private void setSpecificDNSSession(String[] domains, String[] dnsNames, int index) {
		setSpecificDomainsSession(domains);
		setSpecificDNSNameSession(dnsNames);
		setSpecificDataIndex(index);
	}
	private void setSpecificDomainsSession(String[] domains) {
	    if(!(domains.length == 1 && StringUtils.isBlank(domains[0]))) {
	        MgrUtil.setSessionAttribute(boClass.getSimpleName() + "_Specific_Domains", domains);
	    }
	}
	private String[] getSpecificDomainsSession() {
		Object obj = MgrUtil.getSessionAttribute(boClass.getSimpleName() + "_Specific_Domains");
		if(null == obj) {
			return new String[0];
		} else {
			MgrUtil.removeSessionAttribute(boClass.getSimpleName() + "_Specific_Domains");
			return (String[]) obj;
		}
	}
	private void setSpecificDNSNameSession(String[] dnsNames) {
	    if(!(dnsNames.length == 1 && StringUtils.isBlank(dnsNames[0]))) {
	        MgrUtil.setSessionAttribute(boClass.getSimpleName() + "_Specific_DNSNames", dnsNames);
	    }
	}
	private String[] getSpecificDNSNameSession() {
		Object obj = MgrUtil.getSessionAttribute(boClass.getSimpleName() + "_Specific_DNSNames");
		if(null == obj) {
			return new String[0];
		} else {
			MgrUtil.removeSessionAttribute(boClass.getSimpleName() + "_Specific_DNSNames");
			return (String[]) obj;
		}
	}
	private void setSpecificDataIndex(int index) {
		if(index > -1) {
			MgrUtil.setSessionAttribute(boClass.getSimpleName() + "_Specific_Index", index);
		}
	}
	private int getSpecificDataIndex() {
		Object obj = MgrUtil.getSessionAttribute(boClass.getSimpleName() + "_Specific_Index");
		if(null == obj) {
			return -1;
		} else {
			MgrUtil.removeSessionAttribute(boClass.getSimpleName() + "_Specific_Index");
			return Integer.parseInt(obj.toString());
		}
	}
	///==========Sessions for Specific settings: end=============///
	
	///==========(internal/external)DNS servers block: start=============///
	/**
	 * Reverse the DNS server for no-split mode, change to external DNS to internal DNS
	 * 
	 * @author Yunzhi Lin
	 * - Time: Aug 2, 2011 10:27:09 AM
	 */
	private void reverseDNSServers() {
		DnsServiceProfile dateSource = getDataSource();
		IpAddress dnsServer;
		switch(getDataSource().getServiceType()){
			case DnsServiceProfile.SAME_DNS:
				//reset the internal DNS servers
				dateSource.setInternalDns1(null);
				dateSource.setInternalDns2(null);
				dateSource.setInternalDns3(null);
				
				dnsServer = dateSource.getExternalDns1();
				if(null != dnsServer 
						&& StringUtils.isNotBlank(dateSource.getExternalIP1())) {
					dateSource.setInternalDns1(dnsServer);
				}
				dateSource.setExternalDns1(null);
				
				dnsServer = dateSource.getExternalDns2();
				if(null != dnsServer
						&& StringUtils.isNotBlank(dateSource.getExternalIP2())) {
					dateSource.setInternalDns2(dnsServer);
				}
				dateSource.setExternalDns2(null);
				
				dnsServer = dateSource.getExternalDns3();
				if(null != dnsServer
						&& StringUtils.isNotBlank(dateSource.getExternalIP3())) {
					dateSource.setInternalDns3(dnsServer);
				}
				dateSource.setExternalDns3(null);
				
				break;
			case DnsServiceProfile.SEPARATE_DNS:
				dnsServer = dateSource.getInternalDns1();
				if(null != dnsServer 
						&& StringUtils.isBlank(dateSource.getInternalIP1())) {
					dateSource.setInternalDns1(null);
				}
				dnsServer = dateSource.getInternalDns2();
				if(null != dnsServer
						&& StringUtils.isBlank(dateSource.getInternalIP2())) {
					dateSource.setInternalDns2(null);
				}
				dnsServer = dateSource.getInternalDns3();
				if(null != dnsServer
						&& StringUtils.isBlank(dateSource.getInternalIP3())) {
					dateSource.setInternalDns3(null);
				}
				
				break;
			case DnsServiceProfile.EXTERNAL_DNS:
			    //reset the internal DNS servers
                dateSource.setInternalDns1(null);
                dateSource.setInternalDns2(null);
                dateSource.setInternalDns3(null);
                
				if(externalDnsServerIP1 != null && StringUtils.isNotBlank(externalDnsServerIP1)){
					IpAddress ipAddres1 = getDNSServerByName(externalDnsServerIP1,
							getDataSource().getServiceName(), true, getDataSource().getServiceType());
					dateSource.setExternalDns1(ipAddres1);
				} else {
				    dateSource.setExternalDns1(null);
				}
				if(externalDnsServerIP2 != null && StringUtils.isNotBlank(externalDnsServerIP2)){
					IpAddress ipAddres2 = getDNSServerByName(externalDnsServerIP2,
							getDataSource().getServiceName(), true, getDataSource().getServiceType());
					dateSource.setExternalDns2(ipAddres2);
				} else {
				    dateSource.setExternalDns2(null);
				}
				if(externalDnsServerIP3 != null && StringUtils.isNotBlank(externalDnsServerIP3)){
					IpAddress ipAddres3 = getDNSServerByName(externalDnsServerIP3,
							getDataSource().getServiceName(), true, getDataSource().getServiceType());
					dateSource.setExternalDns3(ipAddres3);
				} else {
				    dateSource.setExternalDns3(null);
				}
				break;
		}
	}
	
	/**
	 * Change the internal DNS servers to external DNS servers when the split mode is enable
	 *  
	 * @author Yunzhi Lin
	 * - Time: Aug 12, 2011 1:14:00 PM
	 */
	private void correctDNSServers() {
		if(getDataSource().getServiceType() == DnsServiceProfile.SEPARATE_DNS) {
			IpAddress internalDns = getDataSource().getInternalDns1();
			String externalIP = getDataSource().getExternalIP1();
			if(null != internalDns
					&& StringUtils.isNotBlank(externalIP)
					&& externalIP.equals(internalDns.getAddressName())) {
				getDataSource().setExternalDns1(internalDns);
				getDataSource().setInternalDns1(null);
			} 
			internalDns = getDataSource().getInternalDns2();
			externalIP = getDataSource().getExternalIP2();
			if (null != internalDns 
					&& StringUtils.isNotBlank(externalIP)
					&& externalIP.equals(internalDns.getAddressName())) {
				getDataSource().setExternalDns2(internalDns);
				getDataSource().setInternalDns2(null);
			} 
			internalDns = getDataSource().getInternalDns3();
			externalIP = getDataSource().getExternalIP3();
			if (null != internalDns 
					&& StringUtils.isNotBlank(externalIP)
					&& externalIP.equals(internalDns.getAddressName())) {
				getDataSource().setExternalDns3(internalDns);
				getDataSource().setInternalDns3(null);
			}
		}
	}

	/**
	 * set DNS severs by type(Internal/External)
	 * 
	 * @author Yunzhi Lin - Time: Jun 16, 2011 5:24:00 PM
	 * @param internalFlag
	 *            (boolean)identify which type of the DNS
	 *            server(Internal/External)
	 * @return return <code>false</code> if DNS server#1 is empty; else return
	 *         <code>true</code>
	 * @throws JSONException -
	 */
	private boolean setDNSServerByType(boolean internalFlag) throws JSONException {
		if (internalFlag) {
			// check is internal DNS Server#1 validate
			if (StringUtils.isBlank(getDataSource().getInternalIP1())) {
				addActionErrorMsg(MgrUtil.getUserMessage("error.config.dnsService.required.dnsServer"));
				return false;
			}

			// bind the internal DNS Server#1
			IpAddress ipAddress1 = getDNSServerByName(getDataSource().getInternalIP1(),
					getDataSource().getServiceName(), true, getDataSource().getServiceType());
			getDataSource().setInternalDns1(ipAddress1);
			// bind the internal DNS Server#2
			if (StringUtils.isNotBlank(getDataSource().getInternalIP2())) {
				IpAddress ipAddress2 = getDNSServerByName(getDataSource().getInternalIP2(),
						getDataSource().getServiceName(), true, getDataSource().getServiceType());
				getDataSource().setInternalDns2(ipAddress2);
			} else {
			    getDataSource().setInternalDns2(null);
			}
			// bind the internal DNS Server#3
			if (StringUtils.isNotBlank(getDataSource().getInternalIP3())) {
				IpAddress ipAddress3 = getDNSServerByName(getDataSource().getInternalIP3(),
						getDataSource().getServiceName(), true, getDataSource().getServiceType());
				getDataSource().setInternalDns3(ipAddress3);
			} else {
			    getDataSource().setInternalDns3(null);
			}
		} else {
			// check is external DNS Server#1 validate
			if (StringUtils.isBlank(getDataSource().getExternalIP1())) {
				addActionErrorMsg(MgrUtil.getUserMessage("error.config.dnsService.required.dnsServer"));
				return false;
			}

			// bind the external DNS Server#1
			IpAddress ipAddress1 = getDNSServerByName(getDataSource().getExternalIP1(),
					getDataSource().getServiceName(), false, getDataSource().getServiceType());
			getDataSource().setExternalDns1(ipAddress1);
			// bind the external DNS Server#2
			if (StringUtils.isNotBlank(getDataSource().getExternalIP2())) {
				IpAddress ipAddress2 = getDNSServerByName(getDataSource().getExternalIP2(),
						getDataSource().getServiceName(), false, getDataSource().getServiceType());
				getDataSource().setExternalDns2(ipAddress2);
			} else {
				getDataSource().setExternalDns2(null);
			}
			// bind the external DNS Server#3
			if (StringUtils.isNotBlank(getDataSource().getExternalIP3())) {
				IpAddress ipAddress3 = getDNSServerByName(getDataSource().getExternalIP3(),
						getDataSource().getServiceName(), false, getDataSource().getServiceType());
				getDataSource().setExternalDns3(ipAddress3);
			} else {
				getDataSource().setExternalDns3(null);
			}
		}
		return true;
	}

	/**
	 * create a new DNS server or get an exist DNS server by the specific server
	 * name
	 * 
	 * @author Yunzhi Lin - Time: Jun 16, 2011 4:50:29 PM
	 * @param ipAddressName
	 *            (String)specify the IP Address name for DNS server
	 * @param serviceName
	 *            (String)current DNS service Name(for set the description in
	 *            {@link IPAddress})
	 * @param internalFlag
	 *            (boolean)identify which type of the DNS
	 *            server(Internal/External, for set the description in
	 *            {@link IPAddress})
	 * @param splitDNS
	 *            (boolean)identify which mode of the DNS server(Split/Nosplit,
	 *            for set the description in {@link IPAddress})
	 * @return the DNS server(actually value is {@link IPAddress})
	 */
	private IpAddress getDNSServerByName(String ipAddressName, String serviceName,
			boolean internalFlag, boolean splitDNS) {
		IpAddress ipAddress1 = QueryUtil.findBoByAttribute(IpAddress.class, "addressName",
				ipAddressName, getDomain().getId());
		if (ipAddress1 == null) {
			ipAddress1 = CreateObjectAuto.createNewIP(ipAddressName, IpAddress.TYPE_IP_ADDRESS,
					getDomain(), "For " + serviceName + " 's "
							+ (splitDNS ? (internalFlag ? "Workspace" : "External") : "")
							+ " DNS Server#1");
		}
		return ipAddress1;
	}
	
	private IpAddress getDNSServerByName(String ipAddressName, String serviceName,
			boolean internalFlag, int serviceType) {
		IpAddress ipAddress1 = QueryUtil.findBoByAttribute(IpAddress.class, "addressName",
				ipAddressName, getDomain().getId());
		if (ipAddress1 == null) {
			String description = "";
			switch(serviceType){
				case DnsServiceProfile.SAME_DNS:
					description = "For " + serviceName + " 's " + " DNS Server#1";
					break;
				case DnsServiceProfile.SEPARATE_DNS:
					description = "For " + serviceName + " 's " + (internalFlag ? "Workspace" : "External") + " DNS Server#1";
					break;
			}
			ipAddress1 = CreateObjectAuto.createNewIP(ipAddressName, IpAddress.TYPE_IP_ADDRESS,
					getDomain(), description);
		}
		return ipAddress1;
	}
	
	private void setDNSServerUpdateSession(String dnsOperationSuffix) {
		MgrUtil.setSessionAttribute(boClass.getSimpleName() + "_DNS_Update",
				dnsOperationSuffix);
	}
	
	private String getDNSServerUpdateSession() {
		Object sessionObj = MgrUtil.getSessionAttribute(boClass.getSimpleName() + "_DNS_Update");
		if(null == sessionObj) {
			return null;
		} else {
			MgrUtil.removeSessionAttribute(boClass.getSimpleName() + "_DNS_Update");
			return sessionObj.toString();
		}
	}
	
	private void setEditDNSSession(String operation) throws Exception {
		if (operation.contains(INTERNAL_DNS1_OPERATION_SUFFIX)
				&& StringUtils.isNotBlank(getDataSource().getInternalIP1())) {
			tmpDNSServerId = getDNSServerId(getDataSource().getInternalIP1());
			setDNSServerUpdateSession(INTERNAL_DNS1_OPERATION_SUFFIX);
		} else if (operation.contains(INTERNAL_DNS2_OPERATION_SUFFIX)
				&& StringUtils.isNotBlank(getDataSource().getInternalIP2())) {
			tmpDNSServerId = getDNSServerId(getDataSource().getInternalIP2());
			setDNSServerUpdateSession(INTERNAL_DNS2_OPERATION_SUFFIX);
		} else if (operation.contains(INTERNAL_DNS3_OPERATION_SUFFIX)
				&& StringUtils.isNotBlank(getDataSource().getInternalIP3())) {
			tmpDNSServerId = getDNSServerId(getDataSource().getInternalIP3());
			setDNSServerUpdateSession(INTERNAL_DNS3_OPERATION_SUFFIX);
		} else if (operation.contains(EXTERNAL_DNS1_OPERATION_SUFFIX)
				&& StringUtils.isNotBlank(getDataSource().getExternalIP1())) {
			tmpDNSServerId = getDNSServerId(getDataSource().getExternalIP1());
			setDNSServerUpdateSession(EXTERNAL_DNS1_OPERATION_SUFFIX);
		} else if (operation.contains(EXTERNAL_DNS2_OPERATION_SUFFIX)
				&& StringUtils.isNotBlank(getDataSource().getExternalIP2())) {
			tmpDNSServerId = getDNSServerId(getDataSource().getExternalIP2());
			setDNSServerUpdateSession(EXTERNAL_DNS2_OPERATION_SUFFIX);
		} else if (operation.contains(EXTERNAL_DNS3_OPERATION_SUFFIX)
				&& StringUtils.isNotBlank(getDataSource().getExternalIP3())) {
			tmpDNSServerId = getDNSServerId(getDataSource().getExternalIP3());
			setDNSServerUpdateSession(EXTERNAL_DNS3_OPERATION_SUFFIX);
		}
	}

	private void setNewDNSSession(String operation) {
		if (operation.contains(INTERNAL_DNS1_OPERATION_SUFFIX)) {
			setDNSServerUpdateSession(INTERNAL_DNS1_OPERATION_SUFFIX);
		} else if (operation.contains(INTERNAL_DNS2_OPERATION_SUFFIX)) {
			setDNSServerUpdateSession(INTERNAL_DNS2_OPERATION_SUFFIX);
		} else if (operation.contains(INTERNAL_DNS3_OPERATION_SUFFIX)) {
			setDNSServerUpdateSession(INTERNAL_DNS3_OPERATION_SUFFIX);
		} else if (operation.contains(EXTERNAL_DNS1_OPERATION_SUFFIX)) {
			setDNSServerUpdateSession(EXTERNAL_DNS1_OPERATION_SUFFIX);
		} else if (operation.contains(EXTERNAL_DNS2_OPERATION_SUFFIX)) {
			setDNSServerUpdateSession(EXTERNAL_DNS2_OPERATION_SUFFIX);
		} else if (operation.contains(EXTERNAL_DNS3_OPERATION_SUFFIX)) {
			setDNSServerUpdateSession(EXTERNAL_DNS3_OPERATION_SUFFIX);
		}
	}
	
	private void setCurrentDNSServer(Long tmpDNSServerId,String updateDNSSession) throws Exception {
		// create or edit
		String tmpDNSServerName = getDNSServerName(tmpDNSServerId);
		
		IpAddress tmpDNSServer = QueryUtil.findBoByAttribute(IpAddress.class, "addressName",
				tmpDNSServerName, getDomain().getId());
		
		if(StringUtils.isBlank(tmpDNSServerName) || null == tmpDNSServer) {
			addActionErrorMsg(MgrUtil
					.getUserMessage("error.config.dnsService.dnsServer.notdata"));
		} else {
			if(updateDNSSession.equals(INTERNAL_DNS1_OPERATION_SUFFIX)) {
				getDataSource().setInternalIP1(tmpDNSServerName);
				getDataSource().setInternalDns1(tmpDNSServer);
			} else if(updateDNSSession.equals(INTERNAL_DNS2_OPERATION_SUFFIX)) {
				getDataSource().setInternalIP2(tmpDNSServerName);
				getDataSource().setInternalDns2(tmpDNSServer);
			} else if(updateDNSSession.equals(INTERNAL_DNS3_OPERATION_SUFFIX)) {
				getDataSource().setInternalIP3(tmpDNSServerName);
				getDataSource().setInternalDns3(tmpDNSServer);
			} else if(updateDNSSession.equals(EXTERNAL_DNS1_OPERATION_SUFFIX)) {
				if(getDataSource().getServiceType() == DnsServiceProfile.EXTERNAL_DNS){
					externalDnsServerIP1 = tmpDNSServerName;
				}
				getDataSource().setExternalIP1(tmpDNSServerName);
				getDataSource().setExternalDns1(tmpDNSServer);
			} else if(updateDNSSession.equals(EXTERNAL_DNS2_OPERATION_SUFFIX)) {
				if(getDataSource().getServiceType() == DnsServiceProfile.EXTERNAL_DNS){
					externalDnsServerIP2 = tmpDNSServerName;
				}
				getDataSource().setExternalIP2(tmpDNSServerName);
				getDataSource().setExternalDns2(tmpDNSServer);
			} else if(updateDNSSession.equals(EXTERNAL_DNS3_OPERATION_SUFFIX)) {
				if(getDataSource().getServiceType() == DnsServiceProfile.EXTERNAL_DNS){
					externalDnsServerIP3 = tmpDNSServerName;
				}
				getDataSource().setExternalIP3(tmpDNSServerName);
				getDataSource().setExternalDns3(tmpDNSServer);
			} else {
				addActionErrorMsg(MgrUtil
						.getUserMessage("error.config.dnsService.dnsServer.notdata"));
			}
		}
	}
	///==========(internal/external)DNS servers block: end=============///
	
	// ----------------methods for JSP ---------------------//
	public int getServiceNameLength() {
		return getAttributeLength("serviceName");
	}

	public int getDescriptionLength() {
		return getAttributeLength("description");
	}

	public String getChangedName() {
		return getDataSource().getServiceName().replace("\\", "\\\\").replace("'", "\\'");
	}

	public List<DnsModeRadio> getDnsSplitMode() {
		List<DnsModeRadio> radios = new ArrayList<DnsModeRadio>();
		radios.add(new DnsModeRadio(false, MgrUtil
				.getUserMessage("config.title.dnsService.nosplit.desc")));
		radios.add(new DnsModeRadio(true, MgrUtil
				.getUserMessage("config.title.dnsService.split.desc")));

		return radios;
	}
	
	public List<DnsModeRadio> getDnsServiceMode(){
		List<DnsModeRadio> radios = new ArrayList<DnsModeRadio>();
		radios.add(new DnsModeRadio(DnsServiceProfile.SAME_DNS, MgrUtil
				.getUserMessage("geneva_10.config.title.dnsService.nosplit.desc")));
		radios.add(new DnsModeRadio(DnsServiceProfile.SEPARATE_DNS, MgrUtil
				.getUserMessage("geneva_10.config.title.dnsService.split.desc")));
		radios.add(new DnsModeRadio(DnsServiceProfile.EXTERNAL_DNS, MgrUtil
				.getUserMessage("geneva_10.config.title.dnsService.dnsProxy.desc")));

		return radios;
	}
	
	public boolean getInternalDNSChecked(){
		if(getDataSource() != null && getDataSource().getServiceType() == DnsServiceProfile.EXTERNAL_DNS ){
			return true;
		}
		
		return false;
	}

	public List<DnsModeRadio> getExternalDnsMode() {
		List<DnsModeRadio> radios = new ArrayList<DnsModeRadio>();
		radios.add(new DnsModeRadio(DnsServiceProfile.LOCAL_DNS_TYPE, MgrUtil
				.getUserMessage("config.title.dnsService.external.local")));
		radios.add(new DnsModeRadio(DnsServiceProfile.OPEN_DNS_TYPE, MgrUtil
				.getUserMessage("config.title.dnsService.external.open")));
		radios.add(new DnsModeRadio(DnsServiceProfile.SPECIFIC_DNS_TYPE, MgrUtil
				.getUserMessage("config.title.dnsService.external.specify")));

		return radios;
	}

	public List<String> getDnsNameItems() {
		List<String> nameItems = new ArrayList<String>();
		nameItems.add(MgrUtil.getUserMessage("config.title.dnsService.dns1"));
		nameItems.add(MgrUtil.getUserMessage("config.title.dnsService.dns2"));
		nameItems.add(MgrUtil.getUserMessage("config.title.dnsService.dns3"));

		return nameItems;
	}

	private void prepareDNSServers() throws Exception {
		if(null == availableDnsServers) {
			availableDnsServers = getIpObjectsBySingleIp(CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		}
	}

	public List<CheckItem> getAvailableDnsServers() {
		return availableDnsServers;
	}

	private void prepareInitDNSServerValues() {
		if (null != dataSource) {
			DnsServiceProfile ds = getDataSource();

			prepareWorkspace(ds);

			prepareExternal(ds);
		}
	}

	private void prepareExternal(DnsServiceProfile ds) {
		if (ds.isExternalSepcServerType()) {
			if(ds.getServiceType() == DnsServiceProfile.SEPARATE_DNS) {
				if(null != ds.getExternalDns1()) {
					ds.setExternalIP1(ds.getExternalDns1().getAddressName());
				}
				if (null != ds.getExternalDns2()) {
					ds.setExternalIP2(ds.getExternalDns2().getAddressName());
				}
				if (null != ds.getExternalDns3()) {
					ds.setExternalIP3(ds.getExternalDns3().getAddressName());
				}
			} else {
				// if no-split, change the internal DNS to external DNS for GUI
				if(null != ds.getInternalDns1()) {
					ds.setExternalIP1(ds.getInternalDns1().getAddressName());
				}
				if (null != ds.getInternalDns2()) {
					ds.setExternalIP2(ds.getInternalDns2().getAddressName());
				}
				if (null != ds.getInternalDns3()) {
					ds.setExternalIP3(ds.getInternalDns3().getAddressName());
				}
			}
		}
		
		if(ds.getServiceType() == DnsServiceProfile.EXTERNAL_DNS){
			if(ds.getExternalDns1() != null){
				externalDnsServerIP1 = ds.getExternalDns1().getAddressName();
				ds.setExternalIP1(externalDnsServerIP1);
			}else{
				externalDnsServerIP1 = ds.getExternalIP1();
			}
			
			if(ds.getExternalDns2() != null){
				externalDnsServerIP2 = ds.getExternalDns2().getAddressName();
				ds.setExternalIP2(externalDnsServerIP2);
			}else{
				externalDnsServerIP2 = ds.getExternalIP2();
			}
			
			if(ds.getExternalDns3() != null){
				externalDnsServerIP3 = ds.getExternalDns3().getAddressName();
				ds.setExternalIP3(externalDnsServerIP3);
			}else{
				externalDnsServerIP3 = ds.getExternalIP3();
			}
		}
	}

	private void prepareWorkspace(DnsServiceProfile ds) {
		if (ds.getServiceType() == DnsServiceProfile.SEPARATE_DNS) {
			// set the DNS Server in Workspace
			if(null != ds.getInternalDns1()) {
				ds.setInternalIP1(ds.getInternalDns1().getAddressName());
			}
			if (null != ds.getInternalDns2()) {
				ds.setInternalIP2(ds.getInternalDns2().getAddressName());
			}
			if (null != ds.getInternalDns3()) {
				ds.setInternalIP3(ds.getInternalDns3().getAddressName());
			}
			// set the Domain Names
			StringBuilder sb = new StringBuilder();
			DomainObject domainObj = ds.getDomainObj();
			if (null != domainObj && !domainObj.getItems().isEmpty()) {
				for (DomainNameItem item : domainObj.getItems()) {
					sb.append(item.getDomainName());
					sb.append(NEWLINE_REGEX);
				}
				domainNames = sb.substring(0, sb.length() - 2);
			}
			// set the specific settings
			if(!ds.getSpecificInfos().isEmpty()) {
				specificDNSPair.clear();
				for (DnsSpecificSettings item : ds.getSpecificInfos()) {
					SpecificDNSPair pair = new SpecificDNSPair();
					pair.setSpecificDomain(item.getDomainName());
					if (null != item.getDnsServer()
							&& !item.getDnsServer().getItems().isEmpty()) {
						pair.setSpecificDNS(item.getDnsServer().getAddressName());
						pair.setSpecificDNSId(item.getDnsServer().getId());
					}
					specificDNSPair.add(pair);
				}
			}
		}
	}

	private Long getDNSServerId(String addressName) throws Exception {
		prepareDNSServers();
		for (CheckItem item : getAvailableDnsServers()) {
			if(item.getValue().equals(addressName)) {
				return item.getId();
			}
		}
		return null;
	}
	
	private String getDNSServerName(Long addressId) throws Exception {
		if(null == addressId) {
			return null;
		}
		
		prepareDNSServers();
		for (CheckItem item : getAvailableDnsServers()) {
			if(item.getId().compareTo(addressId) == 0) {
				return item.getValue();
			}
		}
		return null;
	}
	// ----------------override methods---------------------//
	@Override
	public DnsServiceProfile getDataSource() {
		return (DnsServiceProfile) dataSource;
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> tableColumns = new ArrayList<HmTableColumn>(3);
		tableColumns.add(new HmTableColumn(COLUMN_NAME));
		tableColumns.add(new HmTableColumn(COLUMN_SPLITDNS));
		tableColumns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return tableColumns;
	}

	@Override
	protected String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_NAME:
			code = "config.dnsService.name";
			break;
		case COLUMN_SPLITDNS:
			code = "config.dnsService.splitdns";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.dnsService.description";
			break;
		}
		return null == code ? "" : MgrUtil.getUserMessage(code);
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_DNS_SERVICE);
		setDataSource(DnsServiceProfile.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_DNS_SERVICE;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof DnsServiceProfile) {
			dataSource = bo;
			if (null != getDataSource().getDomainObj()) {
				getDataSource().getDomainObj().getId();
				
				if (null != getDataSource().getDomainObj().getItems()) {
					getDataSource().getDomainObj().getItems().size();
				}
			}
			if (null != getDataSource().getSpecificInfos()) {
				getDataSource().getSpecificInfos().size();
				for (DnsSpecificSettings dSpecificSettings : getDataSource().getSpecificInfos()) {
					if (null != dSpecificSettings.getDnsServer()
							&& null != dSpecificSettings.getDnsServer().getItems()) {
						dSpecificSettings.getDnsServer().getItems().size();
					}
				}
			}

			IpAddress tmpInternalDns1 = getDataSource().getInternalDns1();
			if (null != tmpInternalDns1 && null != tmpInternalDns1.getItems()) {
				tmpInternalDns1.getItems().size();
			}
			IpAddress tmpInternalDns2 = getDataSource().getInternalDns2();
			if (null != tmpInternalDns2 && null != tmpInternalDns2.getItems()) {
				tmpInternalDns2.getItems().size();
			}
			IpAddress tmpInternalDns3 = getDataSource().getInternalDns3();
			if (null != tmpInternalDns3 && null != tmpInternalDns3.getItems()) {
				tmpInternalDns3.getItems().size();
			}
			IpAddress tmpExternalDns1 = getDataSource().getExternalDns1();
			if (null != tmpExternalDns1 && null != tmpExternalDns1.getItems()) {
				tmpExternalDns1.getItems().size();
			}
			IpAddress tmpExternalDns2 = getDataSource().getExternalDns2();
			if (null != tmpExternalDns2 && null != tmpExternalDns2.getItems()) {
				tmpExternalDns2.getItems().size();
			}
			IpAddress tmpExternalDns3 = getDataSource().getExternalDns3();
			if (null != tmpExternalDns3 && null != tmpExternalDns3.getItems()) {
				tmpExternalDns3.getItems().size();
			}
		}
		return null;
	}

	// ----------------inner class(For Radio Button)---------------------//
	class DnsModeRadio {

		private boolean modeFlag;
		private String modeDesc;
		private int modeType;

		public DnsModeRadio(boolean modeFlag, String modeDesc) {
			this.modeFlag = modeFlag;
			this.modeDesc = modeDesc;
		}
		public DnsModeRadio(int modeType, String modeDesc) {
			this.modeDesc = modeDesc;
			this.modeType = modeType;
		}

		public boolean isModeFlag() {
			return modeFlag;
		}
		public void setModeFlag(boolean modeFlag) {
			this.modeFlag = modeFlag;
		}
		public String getModeDesc() {
			return modeDesc;
		}
		public void setModeDesc(String modeDesc) {
			this.modeDesc = modeDesc;
		}
		public int getModeType() {
			return modeType;
		}
		public void setModeType(int modeType) {
			this.modeType = modeType;
		}
	}

	class SpecificDNSPair {
		private String specificDomain;
		private String specificDNS;
		private Long specificDNSId;
		public String getSpecificDomain() {
			return specificDomain;
		}
		public void setSpecificDomain(String specificDomain) {
			this.specificDomain = specificDomain;
		}
		public String getSpecificDNS() {
			return specificDNS;
		}
		public void setSpecificDNS(String specificDNS) {
			this.specificDNS = specificDNS;
		}
		public Long getSpecificDNSId() {
			return specificDNSId;
		}
		public void setSpecificDNSId(Long specificDNSId) {
			this.specificDNSId = specificDNSId;
		}
	}
	
	// ----------------Constant---------------------//
	private static final String EXTERNAL_DNS_OPERATION_SUFFIX = "Ex";

	private static final String EXTERNAL_DNS3_OPERATION_SUFFIX = "Ex3";

	private static final String EXTERNAL_DNS2_OPERATION_SUFFIX = "Ex2";

	private static final String EXTERNAL_DNS1_OPERATION_SUFFIX = "Ex1";

	private static final String INTERNAL_DNS3_OPERATION_SUFFIX = "In3";

	private static final String INTERNAL_DNS2_OPERATION_SUFFIX = "In2";

	private static final String INTERNAL_DNS1_OPERATION_SUFFIX = "In1";
	
	// ----------------Field for JSP---------------------//
	private String domainNames; // textarea
	
	private String[] specificDomain; // Advance - Specific domain input
	
	private String[] specificDNS; // Advance - Specific DNS input
	
	// initial Specific Domain-DNS settings
	private List<SpecificDNSPair> specificDNSPair = new ArrayList<SpecificDNSPair>();
	
	private Long tmpDNSServerId; // get from IpAddress page
	
	private int dataIndex = -1; // to store the date index of new/edit specific DNS  
	
	// ----------------Getter/Setter---------------------//
	public String getDomainNames() {
		return domainNames;
	}

	public void setDomainNames(String domainNames) {
		this.domainNames = domainNames;
	}

	public String[] getSpecificDomain() {
		return specificDomain;
	}

	public void setSpecificDomain(String[] specificDomain) {
		this.specificDomain = specificDomain;
	}

	public String[] getSpecificDNS() {
		return specificDNS;
	}

	public void setSpecificDNS(String[] specificDNS) {
		this.specificDNS = specificDNS;
	}

	public List<SpecificDNSPair> getSpecificDNSPair() {
		return specificDNSPair;
	}

	public void setSpecificDNSPair(List<SpecificDNSPair> specificDNSPair) {
		this.specificDNSPair = specificDNSPair;
	}

	public Long getTmpDNSServerId() {
		return tmpDNSServerId;
	}

	public void setTmpDNSServerId(Long tmpDNSServerId) {
		this.tmpDNSServerId = tmpDNSServerId;
	}

	public int getDataIndex() {
		return dataIndex;
	}

	public void setDataIndex(int dataIndex) {
		this.dataIndex = dataIndex;
	}

	public boolean isOverrideDNS() {
		return overrideDNS;
	}

	public void setOverrideDNS(boolean overrideDNS) {
		this.overrideDNS = overrideDNS;
	}

	public int getRuleOneIndex() {
		return ruleOneIndex;
	}

	public void setRuleOneIndex(int ruleOneIndex) {
		this.ruleOneIndex = ruleOneIndex;
	}

	public String getExternalDnsServerIP1() {
		return externalDnsServerIP1;
	}

	public void setExternalDnsServerIP1(String externalDnsServerIP1) {
		this.externalDnsServerIP1 = externalDnsServerIP1;
	}

	public String getExternalDnsServerIP2() {
		return externalDnsServerIP2;
	}

	public void setExternalDnsServerIP2(String externalDnsServerIP2) {
		this.externalDnsServerIP2 = externalDnsServerIP2;
	}

	public String getExternalDnsServerIP3() {
		return externalDnsServerIP3;
	}

	public void setExternalDnsServerIP3(String externalDnsServerIP3) {
		this.externalDnsServerIP3 = externalDnsServerIP3;
	}
}