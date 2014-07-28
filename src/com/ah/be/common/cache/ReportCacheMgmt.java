package com.ah.be.common.cache;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ah.be.common.db.BulkUpdateUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.ClientDeviceInfo;
import com.ah.bo.performance.AhClientEditValues;
import com.ah.bo.performance.AhClientSession;

public class ReportCacheMgmt {
		
	public static final int MAX_SIZE = 200000;
	
	public static final long TIMEOUT_SECOND = 24 * 3600 * 1000; //extends to 24 hours
	
	public static final long TIMEOUT_LOAD_FROM_DB = 2 * 3600 * 1000; //timeout of load from data retention is 2 hours.

	private static ReportCacheMgmt	cacheMgmt;

	private final Map<String, ClientInfoBean> clientInfoCache;
	
	private boolean enableSystemL7Switch;
	
	private ReportCacheMgmt() {
		clientInfoCache = Collections.synchronizedMap(new HashMap<String, ClientInfoBean>());
		initEnableSystemL7Switch();
	}

	public synchronized static ReportCacheMgmt getInstance() {
		if(null == cacheMgmt) {
			cacheMgmt = new ReportCacheMgmt();
		}
		return cacheMgmt;
	}
	
	public void initEnableSystemL7Switch() {
		List<HMServicesSettings> list = QueryUtil.executeQuery(HMServicesSettings.class, null, new FilterParams(
				"owner.domainName", HmDomain.HOME_DOMAIN));
		if (list == null || list.size() == 0) {
			this.enableSystemL7Switch = true;
			return;
		}
		this.enableSystemL7Switch = list.get(0).isEnableSystemL7Switch();
	}
	
	public void updateEnableSystemL7Switch(boolean enableSystemL7Switch) {
		this.enableSystemL7Switch = enableSystemL7Switch;
	}
	
	public boolean isEnableSystemL7Switch() {
		return enableSystemL7Switch;
	}
	
	public int size() {
		return clientInfoCache.size();
	}
	
	public void cleanHistoryData() {
		if (clientInfoCache.size() < 1) {
			return;
		}
		long now = System.currentTimeMillis();
		synchronized(clientInfoCache) {
			Set<String> set = clientInfoCache.keySet();
			for (Iterator<String> iter = set.iterator(); iter.hasNext();) {
				String key = iter.next();
				if (clientInfoCache.get(key) != null) {
					ClientInfoBean bean = clientInfoCache.get(key);
					if (!bean.isOnline() && bean.getTimeout() <= now) {
						iter.remove();
					}
				}
			}
		}
	}
	
	public void refreshClientInfos(List<AhClientSession> clientList) {
		if (clientList == null || clientList.size() < 1) {
			return;
		}
		for (AhClientSession client: clientList) {
			if (client.getClientMac() != null && client.getOwner()!=null && client.getOwner().getId()!=null) {
				ClientInfoBean bean = new ClientInfoBean();
				bean.setHostName(client.getClientHostname());
				bean.setAllClientOsInfo(client.getClientOsInfo(), client.getOs_option55(), CacheMgmt.getInstance().getClientOsInfoFromCacheByOption55(client.getOs_option55(), client.getOwner()));
				bean.setProfileName(client.getUserProfileName());
				bean.setSsid(client.getClientSSID());
				bean.setUserName(client.getClientUsername());
				bean.setVlan(client.getClientVLAN());
				bean.setOnline(true);
				if (client.getClientChannel()<=0) {
					bean.setRadioType(-1);
	    		} else {
	    			bean.setRadioType(client.getClientMACProtocol());
	    		}
				bean.setClientMac(client.getClientMac());
				bean.setDomainId(null != client.getOwner() ? client.getOwner().getId() : null);
//				if (client.getConnectstate() == AhClientSession.CONNECT_STATE_UP) {
//					bean.setOnline(true);
//				}
//				else {
//					bean.setOnline(false);
//					bean.setTimeout(System.currentTimeMillis() + ReportCacheMgmt.TIMEOUT_SECOND);
//				}
				saveClientInfo(client.getClientMac(), bean);
			}
		}
	}
	
	//users can update client info by manual on monitor page 
	public void updateClientInfoByManual(List<AhClientEditValues> list) {
		if (list == null || list.size() == 0) {
			return;
		}
		for (AhClientEditValues editValue : list) {
			if (StringUtils.isBlank(editValue.getClientMac())) {
				continue;
			}
			String clientMac = editValue.getClientMac();
			String keyCache = getCacheClientInfoKey(clientMac, editValue.getOwner().getId());
			if (clientInfoCache.get(keyCache) == null) { 
				ClientInfoBean bean = clientInfoCache.get(keyCache);
				if (bean == null) { //create
					bean = new ClientInfoBean();
					bean.setClientMac(clientMac);
					bean.setDomainId(editValue.getOwner().getId());
				}
				boolean needUpdate = false;
				if (StringUtils.isNotBlank(editValue.getClientHostname())) {
					bean.setHostName(editValue.getClientHostname());
					needUpdate = true;
				}
				if (StringUtils.isNotBlank(editValue.getClientUsername())) {
					bean.setUserName(editValue.getClientUsername());
					needUpdate = true;
				}
				if (needUpdate) {
					this.saveClientInfo(clientMac, bean);
				}
			}
		}
	}
	
	public String getCacheClientInfoKey(String clientMac, Long domainId){
		return clientMac + "_" + domainId;
	}
	
	public void setClientOsInfo(String clientMac, String trapClientOsInfo, String option55, HmDomain owner) {
		if (StringUtils.isBlank(clientMac) || owner==null || owner.getId()==null ) {
			return;
		}
		if (clientInfoCache.get(getCacheClientInfoKey(clientMac,owner.getId())) == null) {
			return ;
		}
		if ((StringUtils.isBlank(trapClientOsInfo) || trapClientOsInfo.equalsIgnoreCase("unknown")) && StringUtils.isBlank(option55)) {
			return;
		}
		ClientInfoBean bean = new ClientInfoBean();
		bean.setOnline(true);
		bean.setClientMac(clientMac);
		bean.setDomainId(owner.getId());
		bean.setAllClientOsInfo(trapClientOsInfo, option55, CacheMgmt.getInstance().getClientOsInfoFromCacheByOption55(option55, owner));
		this.saveClientInfo(clientMac, bean);
	}
		
	public void option55ToOsInfoUpdateEvent(String option55, HmDomain owner) {
		if (StringUtils.isBlank(option55) || owner == null) {
			return;
		}
		if (clientInfoCache.size() < 1) {
			return;
		}
		String newOsInfo = CacheMgmt.getInstance().getClientOsInfoFromCacheByOption55(option55, owner);
		if (StringUtils.isBlank(newOsInfo)) {
			return;
		}
		Long domainId = owner.getId();
		synchronized(clientInfoCache) {
			Set<String> set = clientInfoCache.keySet();
			for (Iterator<String> iter = set.iterator(); iter.hasNext();) {
				String key = iter.next();
				if (clientInfoCache.get(key) != null) {
					ClientInfoBean bean = clientInfoCache.get(key);
					if (!domainId.equals(bean.getDomainId())) {
						continue;
					}
					if (StringUtils.isNotBlank(bean.getOsInfo())) {
						continue;
					}
					if (StringUtils.isBlank(bean.getOrginalOsInfo()) && !newOsInfo.equals(bean.getOsInfo())) {
						bean.setOsInfo(newOsInfo);
						bean.setWriteDbFlag(true);
					}
					
				}
			}
		}
		
	}
	
	private void paramIntercept(ClientInfoBean bean) {
		if (bean == null) {
			return;
		}
		if (StringUtils.isNotBlank(bean.getHostName())) {
			if (bean.getHostName().trim().equalsIgnoreCase("N/A")) {
				bean.setHostName(null);
			}
		}
	}
   
    public void saveClientInfo(String clientMac, ClientInfoBean bean) {
    	if (bean == null) {
    		return;
    	}
    	paramIntercept(bean);
    	ClientInfoBean value;
    	String cacheKey = getCacheClientInfoKey(clientMac, bean.getDomainId());
    	if (clientInfoCache.get(cacheKey) == null) {
    		if (clientInfoCache.size() >= MAX_SIZE) {
    			return;
    		}
    		value = new ClientInfoBean();
    		value.setHostName(bean.getHostName());
        	value.setProfileName(bean.getProfileName());
        	value.setSsid(bean.getSsid());
        	value.setUserName(bean.getUserName());
        	value.setOsInfo(bean.getOsInfo());
        	value.setOption55(bean.getOption55());
        	value.setClientMac(clientMac);
        	value.setDomainId(bean.getDomainId());
        	value.setWriteDbFlag(true);
        	value.setDataSource(bean.getDataSource());
        	value.setOnline(bean.isOnline());
        	if (bean.isOnline()) {
        		value.setVlan(bean.getVlan());
            	value.setRadioTypeReal(bean.getRadioType());
        	}
        	if (!bean.isOnline()) {
    			value.setTimeout(System.currentTimeMillis() + ReportCacheMgmt.TIMEOUT_SECOND);
    		}
        	clientInfoCache.put(cacheKey, value);
        	
    	} else {
    		synchronized(clientInfoCache) {
    			value = clientInfoCache.get(cacheKey);
     			value.setWriteDbFlag(isNeedWriteDb(value, bean));
    			value.setDataSource(ClientInfoBean.DATA_SOURCE_DEVICE);
        		if (StringUtils.isNotBlank(bean.getHostName())) {
        			value.setHostName(bean.getHostName());
        		}
        		if (StringUtils.isNotBlank(bean.getProfileName())) {
        			value.setProfileName(bean.getProfileName());
        		}
        		if (StringUtils.isNotBlank(bean.getSsid())) {
        			value.setSsid(bean.getSsid());
        		}
        		if (StringUtils.isNotBlank(bean.getUserName())) {
        			value.setUserName(bean.getUserName());
        		}
        		if (StringUtils.isNotBlank(bean.getOsInfo())) {
        			value.setOsInfo(bean.getOsInfo());
        		}
        		if (StringUtils.isNotBlank(bean.getOption55())) {
        			value.setOption55(bean.getOption55());
        		}
        		if (bean.getDomainId() != null) {
        			value.setDomainId(bean.getDomainId());
        		}
        		value.setClientMac(clientMac);
        		value.setOnline(bean.isOnline());
            	if (bean.isOnline()) {
            		value.setVlan(bean.getVlan());
                	value.setRadioTypeReal(bean.getRadioType());
            	}
            	if (!bean.isOnline()) {
        			value.setTimeout(System.currentTimeMillis() + ReportCacheMgmt.TIMEOUT_SECOND);
        		}
            	clientInfoCache.put(cacheKey, value);
    		}
    	}   	
    }
    
    private boolean isNeedWriteDb(ClientInfoBean oldone, ClientInfoBean newone) {
    	if (oldone.getDataSource() == ClientInfoBean.DATA_SOURCE_DB) {
    		return true;
    	}
    	if (StringUtils.isNotBlank(newone.getHostName())) {
			if (!newone.getHostName().equals(oldone.getHostName())) {
				return true;
			}
		}
		if (StringUtils.isNotBlank(newone.getProfileName())) {
			if (!newone.getProfileName().equals(oldone.getProfileName())) {
				return true;
			}
		}
		if (StringUtils.isNotBlank(newone.getSsid())) {
			if (!newone.getSsid().equals(oldone.getSsid())) {
				return true;
			}
		}
		if (StringUtils.isNotBlank(newone.getUserName())) {
			if (!newone.getUserName().equals(oldone.getUserName())) {
				return true;
			}
		}
		if (StringUtils.isNotBlank(newone.getOsInfo())) {
			if (!newone.getOsInfo().equals(oldone.getOsInfo())) {
				return true;
			}
		}
		if (StringUtils.isNotBlank(newone.getOption55())) {
			if (!newone.getOption55().equals(oldone.getOption55())) {
				return true;
			}
		}
		if (newone.getDomainId() != null) {
			if (!newone.getDomainId().equals(oldone.getDomainId())) {
				return true;
			}
		}
		if(newone.getVlan() != 0){
			if(newone.getVlan() != oldone.getVlan()){
				return true;
			}
		}
		if(newone.getRadioType() != -1){
			if(newone.getRadioType() != oldone.getRadioType()){
				return true;
			}
		}
		return false;
    }
        
    public ClientInfoBean getClientInfoBean(String clientMac, Long domainId) {
    	if (clientMac == null || domainId == null) {
    		return null;
    	}
    	return clientInfoCache.get(getCacheClientInfoKey(clientMac, domainId));
    }
    
    //just used for debug.
    public List<ClientInfoBean> getClientInfoListForDebug(int limitedNumber) {
    	if (clientInfoCache.size() < 1) {
    		return null;
    	}
    	if (limitedNumber > 200) {
    		return null;
    	}
    	List<ClientInfoBean> list = new ArrayList<ClientInfoBean>();
    	synchronized(clientInfoCache) {
			Set<String> set = clientInfoCache.keySet();
			for (Iterator<String> iter = set.iterator(); iter.hasNext();) {
				String key = iter.next();
				if (clientInfoCache.get(key) != null) {
					ClientInfoBean bean = clientInfoCache.get(key);
					list.add(bean);
				}
				if (list.size() >= limitedNumber) {
					break;
				}
			}
		}
    	return list;
    }
    
    public void loadFromDb() {
    	BeLogTools.info(HmLogConst.M_PERFORMANCE, "[ReportCacheMgmt] start execute loading client cache data from db.");
    	if (clientInfoCache != null && clientInfoCache.size() > 0) {
    		BeLogTools.error(HmLogConst.M_PERFORMANCE, "[ReportCacheMgmt] ClientInfo cache data is not empty, ignore current loading.");
    		return;
    	}
    	Calendar c = Calendar.getInstance();
		long endTime = c.getTimeInMillis();
		c.add(Calendar.DATE, -30);    
		long startTime = c.getTimeInMillis();
		//only load last month's data.
    	String sql = "select mac,owner,hostname,os_type,option55,username,profilename,ssid,vlan,radiotype from client_device_info " +
    			     " where update_at >= " + startTime + " and update_at <= " + endTime;
    	try {
    		List<?> clientList = QueryUtil.executeNativeQuery(sql);
        	if (clientList == null || clientList.size() == 0) {
        		BeLogTools.error(HmLogConst.M_PERFORMANCE, "[ReportCacheMgmt] ClientInfo data that from DB is empty.");
        		return;
        	}
        	for (int i = 0; i < clientList.size(); i++) {
    			Object[] obj = (Object[]) clientList.get(i);
    			ClientInfoBean clientInfoBean = new ClientInfoBean();
    			int index = 0;
    			String clientMac = (String) obj[index++];
    			clientInfoBean.setClientMac(clientMac);
    			clientInfoBean.setDomainId(Long.valueOf(obj[index++].toString()));
    			clientInfoBean.setHostName((String) obj[index++]);
    			clientInfoBean.setOsInfo((String) obj[index++]);
    			clientInfoBean.setOption55((String) obj[index++]);
    			clientInfoBean.setUserName((String) obj[index++]);
    			clientInfoBean.setProfileName((String) obj[index++]);
    			clientInfoBean.setSsid((String) obj[index++]);
    			clientInfoBean.setVlan((int) obj[index++]);
    			clientInfoBean.setRadioTypeReal((int) obj[index++]);
    			clientInfoBean.setDataSource(ClientInfoBean.DATA_SOURCE_DB);
    			clientInfoBean.setOnline(false);
    			clientInfoBean.setTimeout(TIMEOUT_LOAD_FROM_DB);
    			String cacheKey = getCacheClientInfoKey(clientMac, clientInfoBean.getDomainId());
    			clientInfoCache.put(cacheKey, clientInfoBean);
    		}
    	} catch (Exception e) {
    		BeLogTools.error(HmLogConst.M_PERFORMANCE, "[ReportCacheMgmt] execute loading client cache data error." , e);
    	}
    	BeLogTools.info(HmLogConst.M_PERFORMANCE, "[ReportCacheMgmt] finish execute loading client cache data from db. client size is :" + clientInfoCache.size());
    }
    
    public void writeDbFromClientCache(){
    	if (clientInfoCache.size() < 1) {
			return;
		}
    	int exceptionNum = 0;
    	while(true){
    		if(exceptionNum > 3){
				break;
			}
    		List<ClientInfoBean> list = new ArrayList<ClientInfoBean>();
    		Set<String> set = clientInfoCache.keySet();
    		synchronized(clientInfoCache){
    			for (Iterator<String> iter = set.iterator(); iter.hasNext();) {
    				String key = iter.next();
    				ClientInfoBean bean = clientInfoCache.get(key);
    				if (bean != null && bean.isWriteDbFlag() ) {
    					if(list.size() > 500){
    						break;
    					}
    					bean.setWriteDbFlag(false);
    					list.add(bean);
    				}
    			}
    		}
    		if(null == list || list.isEmpty()){
    			break;
    		}
			String select_exist_client_sql = "select mac, owner, hostname, os_type, option55, username, profilename, ssid, vlan, radiotype from client_device_info where ";
			StringBuffer sb = new StringBuffer();
			for(int i=0 ; i< list.size(); i++){
				ClientInfoBean cifb = list.get(i);
				if(i != list.size() - 1){
					sb.append("(");
					sb.append("mac='");
					sb.append(cifb.getClientMac());
					sb.append("' and owner=");
					sb.append(cifb.getDomainId());
					sb.append(") or ");
				}else{
					sb.append("(");
					sb.append("mac='");
					sb.append(cifb.getClientMac());
					sb.append("' and owner=");
					sb.append(cifb.getDomainId());
					sb.append(") ");
				}
			}
			select_exist_client_sql = select_exist_client_sql + sb.toString();
			List<?> existClients = QueryUtil.executeNativeQuery(select_exist_client_sql);
			if (null != existClients && !existClients.isEmpty()) {
				long time = System.currentTimeMillis();
				StringBuilder clientSql = new StringBuilder();
				clientSql.append("update client_device_info set hostname=?, os_type=?, option55=?, username=?, profilename=?, ssid=?, vlan=?, radiotype=?, update_at=? " +
						        " where mac=? and owner=?");
				List<ClientDeviceInfo> createBos = new ArrayList<ClientDeviceInfo>();
				List<Object[]> paraList = new ArrayList<Object[]>();
				List<String> existMacOwners = new ArrayList<String>();
				for (Object obj : existClients) {
					Object[] oneObj = (Object[])obj;
					String str = oneObj[0].toString()+oneObj[1].toString();
					existMacOwners.add(str);
				}
				for(ClientInfoBean cifb: list){
					if(existMacOwners.contains(cifb.getClientMac()+cifb.getDomainId().toString())){
						if(null != cifb.getDomainId()){
							boolean exist = false;
							String  hostName = null;
							String  OS_type = null;
							String  option55 = null;
							String  userName = null;
							String  profileName = null;
							String  ssid = null;
							int     vlan = 0;
							int     radioType = -1;
							for(Object obj: existClients) {
								Object[] oneObj = (Object[])obj;
								if(oneObj[0].toString().equals(cifb.getClientMac()) && oneObj[1].toString().equals(cifb.getDomainId().toString())){
									exist = true;
									hostName = (String)oneObj[2];
									OS_type = (String)oneObj[3];
									option55 = (String)oneObj[4];
									userName = (String)oneObj[5];
									profileName = (String)oneObj[6];
									ssid = (String)oneObj[7];
									vlan = (int)oneObj[8];
									radioType = (int)oneObj[9];
									break;
								}
							}
							if (exist) {
								Object[] objs = new Object[11];
								int index = 0;
								objs[index++] = StringUtils.isNotBlank(cifb.getHostName()) ? cifb.getHostName() : hostName;
								objs[index++] = StringUtils.isNotBlank(cifb.getOsInfo()) ? cifb.getOsInfo() : OS_type;
								objs[index++] = StringUtils.isNotBlank(cifb.getOption55()) ? cifb.getOption55() : option55;
								objs[index++] = StringUtils.isNotBlank(cifb.getUserName()) ? cifb.getUserName() : userName;
								objs[index++] = StringUtils.isNotBlank(cifb.getProfileName()) ? cifb.getProfileName() : profileName;
								objs[index++] = StringUtils.isNotBlank(cifb.getSsid()) ? cifb.getSsid() : ssid;
								objs[index++] = cifb.getVlan() !=0 ? cifb.getVlan() : vlan;
								objs[index++] = cifb.getRadioType() !=-1 ? cifb.getRadioType() : radioType;
								objs[index++] = time;
								objs[index++] = cifb.getClientMac();
								objs[index++] = cifb.getDomainId();
								paraList.add(objs);
							}
						}
					}else{
						if(null != cifb.getDomainId()){
							ClientDeviceInfo cli = new ClientDeviceInfo();
							cli.setHostName(cifb.getHostName());
							cli.setUserName(cifb.getUserName());
							cli.setProfileName(cifb.getProfileName());
							cli.setSsid(cifb.getSsid());
							cli.setVlan(cifb.getVlan());
							cli.setRadioType(cifb.getRadioType());
							cli.setMAC(cifb.getClientMac());
							cli.setOS_type(cifb.getOsInfo());
							cli.setOption55(cifb.getOption55());
							cli.setOwner(QueryUtil.findBoById(HmDomain.class, cifb.getDomainId()));
							cli.setUpdate_at(time);
							createBos.add(cli);
						}
					}
				}
				if(null != paraList && !paraList.isEmpty()){
					try {
						QueryUtil.executeBatchUpdate(clientSql.toString(), paraList);
					} catch (Exception e) {
						exceptionNum++;
						BeLogTools.error(HmLogConst.M_PERFORMANCE, "ReportCacheMgmt batch update client_device_info exception: " + e.getMessage());
						synchronized(clientInfoCache){
							for(Object[] obj: paraList){
								if (StringUtils.isBlank(obj[9].toString()) || StringUtils.isBlank(obj[10].toString())) {
									continue;
								}
								clientInfoCache.get(getCacheClientInfoKey(obj[9].toString(), Long.valueOf(obj[10].toString()))).setWriteDbFlag(true);
							}
			    		}
					}
				}
				if(null != createBos && !createBos.isEmpty()){
					try {
						BulkUpdateUtil.bulkInsert(ClientDeviceInfo.class, createBos);
					} catch (Exception e) {
						exceptionNum++;
						BeLogTools.error(HmLogConst.M_PERFORMANCE, "ReportCacheMgmt batch insert into client_device_info exception: " + e.getMessage());
						synchronized(clientInfoCache){
		    				for(ClientDeviceInfo cdin: createBos){
		    					if (StringUtils.isBlank(cdin.getMAC()) || cdin.getOwner()==null || cdin.getOwner().getId()==null) {
									continue;
								}
		    					clientInfoCache.get(getCacheClientInfoKey(cdin.getMAC(), cdin.getOwner().getId())).setWriteDbFlag(true);
							}
						}
					}
				}
			}else{
				List<ClientDeviceInfo> cdi = new ArrayList<ClientDeviceInfo>();
				long time = System.currentTimeMillis();
				for(ClientInfoBean bean : list){
					if(null != bean.getDomainId()){
						ClientDeviceInfo cli = new ClientDeviceInfo();
						cli.setHostName(bean.getHostName());
						cli.setMAC(bean.getClientMac());
						cli.setOS_type(bean.getOsInfo());
						cli.setOption55(bean.getOption55());
						cli.setUserName(bean.getUserName());
						cli.setProfileName(bean.getProfileName());
						cli.setSsid(bean.getSsid());
						cli.setVlan(bean.getVlan());
						cli.setRadioType(bean.getRadioType());
						cli.setOwner(QueryUtil.findBoById(HmDomain.class, bean.getDomainId()));
						cli.setUpdate_at(time);
						cdi.add(cli);
					}
				}
				if(null != cdi && !cdi.isEmpty()){
					try {
						BulkUpdateUtil.bulkInsert(ClientDeviceInfo.class, cdi);
					} catch (Exception e) {
						exceptionNum++;
						BeLogTools.error(HmLogConst.M_PERFORMANCE, "ReportCacheMgmt batch insert into client_device_info exception: " + e.getMessage());
						synchronized(clientInfoCache){
							for(ClientInfoBean cifb: list){
								if (StringUtils.isBlank(cifb.getClientMac()) || cifb.getDomainId()==null) {
									continue;
								}
								
		    					clientInfoCache.get(getCacheClientInfoKey(cifb.getClientMac(), cifb.getDomainId())).setWriteDbFlag(true);
							}
						}
					}
				}
			}
			if(list.size() < 500){
    			break;
    		}
			
    	}
		
    }
    
}