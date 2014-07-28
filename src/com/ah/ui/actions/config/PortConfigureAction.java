package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.app.HmBeEventUtil;
import com.ah.be.app.HmBeParaUtil;
import com.ah.be.db.configuration.ConfigurationChangedEvent;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.AhInterface.DeviceInfType;
import com.ah.bo.hiveap.AhInterface.DeviceInfUnionType;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.DeviceInfo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.network.PseProfile;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.port.PortMonitorProfile;
import com.ah.bo.port.PortPseProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.hiveap.NetworkPolicyAction;
import com.ah.ui.actions.hiveap.NetworkPolicyAction.NetworkPolicyType;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.HmException;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class PortConfigureAction extends BaseAction {

    private static final long serialVersionUID = -3059385559480285840L;
    // fields for the JSON format
    private static final String ETH = "ETH";
    private static final String USB = "USB";
    private static final String SFP = "SFP";
    private static final Tracer LOG = new Tracer(PortConfigureAction.class.getSimpleName());
    
    @Override
    public String execute() throws Exception {
        try {
            jsonObject = new JSONObject();
            if("new".equals(operation)) {
                if (!setTitleAndCheckAccess(getText("config.title.lanProfile"))) {
                    setUpdateContext(true);
                    return getLstForward();
                }
                PortGroupProfile data = new PortGroupProfile();
                if(parentNpId > 0) {
                    data.setParentNPId(parentNpId);
                }
                
                NetworkPolicyType type = NetworkPolicyType.get(limitType);
                if(type == NetworkPolicyType.SUPPORT_AP) {
                    // initialize the device dialog for the BR100 if wireless only
                    data.setPortNum((short) 5);
                    data.setDeviceModels(""+HiveAp.HIVEAP_MODEL_BR100);
                    data.setDeviceType(HiveAp.Device_TYPE_HIVEAP);
                }
                
                setSessionDataSource(data);
                return getINPUTType();
            } else if("edit".equals(operation)) {
            	PortGroupProfile portProfile =(PortGroupProfile) findBoById(boClass, id, getQueryBoByMode(LazyMode.EDIT));
                setSessionDataSource(portProfile);
//            	if(null != portProfile.getItems() && !portProfile.getItems().isEmpty()){
//            		haveNonDefaultTmps = true;
//            	}
                initPortNum();
                if(getDataSource().getLoadBalanceMode() == -1
                        || getDataSource().getLoadBalanceMode() == PortGroupProfile.LOADBLANCE_MODE_AUTO) {
                    getDataSource().setLoadBalanceMode(PortGroupProfile.LOADBLANCE_MODE_SRC_DET_MAC_IP_PORT);
                } 
                if (dataSource == null) {
                    return prepareBoList();
                } else {
                	List<PortMonitorProfile> list = getDataSource().getMonitorProfiles();
                	for(PortMonitorProfile mirror : list){
                		enableSourcePort = mirror.isEnablePorts();
                		if(mirror.isEnableVlans() && mirror.getIngressVlan() != null && !mirror.getIngressVlan().isEmpty()){
                			destinationInterface = mirror.getDestinationPort();
                			enableSourceVlan = mirror.isEnableVlans();
                			ingressVlan = mirror.getIngressVlan();
                			break;
                		}
                	}
                    addLstTitle(getText("config.title.lanProfile.edit") + " '" + getChangedName()
                            + "'");
                    return getINPUTType();
                }
            } else if("editMonitor".equals(operation)) {
                List<PortMonitorProfile> list = getDataSource().getMonitorProfiles();
            	for(PortMonitorProfile mirror : list){
            		if(mirror.isEnablePorts()){
            			enableSourcePort = mirror.isEnablePorts();
            			break;
            		}
            		
            		if(mirror.isEnableVlans() && mirror.getIngressVlan() != null && !mirror.getIngressVlan().isEmpty()){
            			destinationInterface = mirror.getDestinationPort();
            			enableSourceVlan = mirror.isEnableVlans();
            			ingressVlan = mirror.getIngressVlan();
            			break;
            		}
            	}
            	if(isContainMirror(getDataSource())){
            		initPortNum();
            	}
                
                if(null == dataSource) {
                    jsonObject.put("errMsg", "Error to load the port mirror configuration page.");
                    return "json";
                } else {
                    return "monitorDlgJson";
                }
            } else if("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
                return createPortTemplate();
            } else if ("update".equals(operation) || ("update" + getLstForward()).equals(operation)) {
                return updatePortTemplate();
            } else if("configure".equals(operation)) {
                return configAccess4Ports();
            } else if ("retrieveStatus".equals(operation)) {
                try {
                    PortGroupProfile editBo = findBoById(PortGroupProfile.class, id, getQueryBoByMode(LazyMode.EDIT));
                    String statusData = editBo.getPortsBasicData();
                    if(!StringUtils.isEmpty(statusData)) {
                        jsonObject.put("succ", true);
                        jsonObject.put("status", new JSONArray(statusData));
                        jsonObject.put("desc", editBo.getPortsDesc());
                        
                        buildPoEIcons(editBo);
                    }
                } catch (Exception e) {
                    LOG.error("Error when retrive port template status. id="+id, e);
                    jsonObject.put("errMsg", "Error to get the port template status.");
                }
                return "json";
            } else if("newPseProfile".equals(operation) || "editPseProfile".equals(operation)){
            	addLstForward("portConfigure");
				addLstTabId(tabId);
				return operation;
            } else if("remove".equals(operation)) {
                boolean succ = false;
                try {
                    //==============================================
                	List<ConfigTemplate> allNetWorkPolicy =  QueryUtil.executeQuery(ConfigTemplate.class,new SortParams("id"),null,domainId);
                	Collection<Long> toRemoveIds = new ArrayList<Long>(this.getAllSelectedIds());
                	Long selectedId = toRemoveIds.iterator().next();
//                    List<PortGroupProfile> ownerList = QueryUtil.executeQuery(PortGroupProfile.class, null, new FilterParams("owner.id",domainId));
//                    for(int i=0;i<ownerList.size();i++){
//                    	if(ownerList.get(i).getItems() != null){
//                    		for(int j=0;j<ownerList.get(i).getItems().size();j++){
//                    			if(ownerList.get(i).getItems().get(j).getNonGlobalId() == selectedId){
//                    				PortGroupProfile profile = this.findBoById(PortGroupProfile.class, selectedId);
//                    				String label = "";
//                    				if(profile != null){
//                    					label = profile.getName();
//                    				}
//                    				throw new HmException("Remove object " + selectedId
//                    						+ " failed, stale object state.", null, HmMessageCodes.OBJECT_IN_USE,
//                    						new String[] { label });
//                    			}
//                    		}
//                    	}
//                    }
                    List<String> networkTemplates = new ArrayList<String>();
                    for(ConfigTemplate newWorkPolicy : allNetWorkPolicy){
                    	//check if the new template conflicts with any other templates in current network policy, if yes the change cannot be saved
                    	Set<PortGroupProfile> portProfiles = newWorkPolicy.getPortProfiles();
                		if(portProfiles.size() > 0){
                    		for(PortGroupProfile portProfile : portProfiles){
                        		if(null != portProfile && portProfile.getId().equals(selectedId)){
                        			networkTemplates.add(newWorkPolicy.getConfigName());
                        			break;
                        		}
                        		if(null != portProfile.getItems() && !portProfile.getItems().isEmpty() ){
                        			for(SingleTableItem item : portProfile.getItems()){
                        				if(null != item ){
                        					if(item.getNonGlobalId() == selectedId){
                        						networkTemplates.add(newWorkPolicy.getConfigName());
                                    			break;
                        					}
                        				}
                        			}
                        		}
                        	}
                    	}
                    }
                    if(!networkTemplates.isEmpty()){
                    	if(networkTemplates.size() < 5){
                    		String wt = "";
                    		for(int i = 0;i < networkTemplates.size();i++){
                    			wt = wt.concat(" " + networkTemplates.get(i) + "," );
                    		}
                    		wt = wt.substring(0,wt.length()-1);
                    		throw new HmException("Remove object " + selectedId
            						+ " failed, stale object state.", null, "config.networkpolicy.other.port.template.change.desc",
            						new String[] { wt });
                    		//return MgrUtil.getUserMessage("config.networkpolicy.other.port.template.change.desc",new String[]{wt});
                    	}else{
                    		String wt = "";
                    		for(int i = 0;i < networkTemplates.size();i++){
                    			wt = wt.concat(" " + networkTemplates.get(i) + "," );
                    		}
                    		wt = wt.substring(0,wt.length()-1);
                    		throw new HmException("Remove object " + selectedId
            						+ " failed, stale object state.", null, "config.networkpolicy.other.port.template.change.desc",
            						new String[] { wt + " and so on"});
                    		//return MgrUtil.getUserMessage("config.networkpolicy.other.port.template.change.desc",new String[]{wt + " and so on"});
                    	}
                    }
                    //==============================================
                    succ = removeOperation();
                } catch (Exception e) {
                    LOG.error("Error when try to remove the port template. id="+id, e);
                    if(isJsonMode()) {
                        jsonObject.put("errMsg", MgrUtil.getUserMessage(e));
                    } else {
                        addActionError(MgrUtil.getUserMessage(e));
                    }
                }
                if (isJsonMode()) {
                    if (succ) {
                        jsonObject.put("succ", true);
                    }
                    return "json";
                } else {
                    return prepareBoList();
                }
            } else if("clone".equals(operation)) {
                return clonePortTemplate();
            } else if("updateMirrorSettings".equals(operation)){
            	updateMirrorSettings();
            	QueryUtil.updateBo(dataSource);
            	return prepareBoList();
            } else {
                baseOperation();
                return prepareBoList();
            }
        } catch (Exception e) {
            return isJsonMode() ? prepareActionJSONError(e) : prepareActionError(e);
        }
    }
    
    protected String prepareActionJSONError(Exception e) throws Exception {
        LOG.error("prepareActionError", MgrUtil.getUserMessage(e), e);
        addActionErrorMsg(MgrUtil.getUserMessage(e));
        generateAuditLog(HmAuditLog.STATUS_FAILURE, boClass.getSimpleName()
                + " " + MgrUtil.getUserMessage(e));
        return getReturnPathWithJsonMode(INPUT, "jsoninput", "json");
    }

    private void buildPoEIcons(PortGroupProfile editBo) throws JSONException {
        List<PortPseProfile> pseList = editBo.getPortPseProfiles();
        if(null != pseList && !pseList.isEmpty()) {
            
            StringBuilder builder = new StringBuilder();
            builder.append("{className: 'poe', ports: { ETH: [");
            
            StringBuilder subBuilder = new StringBuilder();
            for (PortPseProfile pseProfile : pseList) {
                if(pseProfile.isEnabelIfPse()) {
                    DeviceInfUnionType type = DeviceInfType.getInstance(pseProfile.getInterfaceNum(), editBo.getFirstHiveApModel());
                    subBuilder.append(type.getIndex());
                    subBuilder.append(PortBasicProfile.PORTS_SPERATOR);
                }
            }
            if(subBuilder.length() > 0) {
                subBuilder.deleteCharAt(subBuilder.length()-1);
            }
            builder.append(subBuilder.toString());
            builder.append("]}}");
            
            jsonObject.put("poe", new JSONObject(builder.toString()));
        }
    }

    private String updatePortTemplate() throws JSONException, Exception {
        if (StringUtils.trimToEmpty(getDataSource().getDescription()).length() > getProfileDescirptionLength()) {
            addActionErrorMsg(MgrUtil
                    .getUserMessage("error.config.lanProfile.Description.exceed"));
            return getReturnPathWithJsonMode(INPUT, "jsoninput", "json");
        }
        
        if(!isJustDescriptionChange()){
        	 String err = checkPortTemplate();
             if (StringUtils.isNotEmpty(err)) {
                 jsonObject = new JSONObject();
                 jsonObject.put("err", err);
                 return "json";
             }
        }
       
        
        // update the additional settings
        updateAdditionalPortSettings();

        if ("update".equals(operation)){
            updateBo(dataSource);
            if(isJsonMode()) {
                jsonObject = new JSONObject(); 
                jsonObject.put("succ", true);
                jsonObject.put("updateAdditional", isEditAdditionalSettings());
                jsonObject.put("updateId", dataSource.getId());
                if(isEditAdditionalSettings()) {
                    jsonObject.put("portTemplateIndex", portTemplateIndex);
                }
                return "json";
            } else {
                return prepareBoList();
            }
        } else {
            updateBo(dataSource);
            setUpdateContext(true);
            return getLstForward();
        }
    }

    private boolean isJustDescriptionChange() {
    	//get portProfile from DB
    	int whetherModelsChanged= 0;
    	PortGroupProfile portFromDB = QueryUtil.findBoById(PortGroupProfile.class, dataSource.getId(), getQueryBoByMode(LazyMode.EDIT));
    	if(portFromDB.getDeviceType() != deviceType){
    		return false;
    	}
    	for(String devicemodle : portFromDB.getDeviceModelStrs()){
    		int returnValue = Arrays.binarySearch(deviceModels.split(","), devicemodle);
    		 if(returnValue >= 0){
					++ whetherModelsChanged;
				}
    	}
    	if((whetherModelsChanged != deviceModels.split(",").length) || (whetherModelsChanged !=  portFromDB.getDeviceModels().split(",").length) ){
    		return false;
    	}
    	return true;
	}

	private String checkPortTemplate() {
        String err = "";
        List<PortGroupProfile> selectPortProfilelst = new ArrayList<PortGroupProfile>();
        PortGroupProfile curPortProfile = null;
        //check the update portTemplate is existed in others network policy!
        List<ConfigTemplate> allNetWorkPolicy =  QueryUtil.executeQuery(ConfigTemplate.class,new SortParams("id"),null,domainId);
        if(null !=selectIDs && !selectIDs.equals("")  ){
        	if(selectIDs.contains("-")){
        		String[] strArray = selectIDs.split("-");
            	for(int i =0; i< strArray.length;i++){
            		PortGroupProfile selectport = QueryUtil.findBoById(PortGroupProfile.class, Long.parseLong(strArray[i]), getQueryBoByMode(LazyMode.EDIT));
            		if(selectport.getId().equals(dataSource.getId())) curPortProfile = selectport;
            		selectPortProfilelst.add(selectport);
            	}
        	}else{
        		PortGroupProfile selectport = QueryUtil.findBoById(PortGroupProfile.class, Long.parseLong(selectIDs), getQueryBoByMode(LazyMode.EDIT));
        		if(selectport.getId().equals(dataSource.getId())) curPortProfile = selectport;
        		selectPortProfilelst.add(selectport);
        	}
        }
        ConfigTemplate currentTemplate = null;
        List<String> networkTemplates = new ArrayList<String>();
        for(ConfigTemplate newWorkPolicy : allNetWorkPolicy){
        	//check if the new template conflicts with any other templates in current network policy, if yes the change cannot be saved
        	Set<PortGroupProfile> portProfiles = newWorkPolicy.getPortProfiles();
        	if(!selectPortProfilelst.isEmpty() && currentPolicyID > 0 && newWorkPolicy.getId().longValue() == currentPolicyID){
        		currentTemplate = newWorkPolicy;
            }
    		if(currentPolicyID > 0 &&  newWorkPolicy.getId().longValue() != currentPolicyID){
        		for(PortGroupProfile portProfile : portProfiles){
            		if(null != portProfile && portProfile.getId().equals(dataSource.getId())){
            			networkTemplates.add(newWorkPolicy.getConfigName());
            			break;
            		}
            		if(null != portProfile.getItems() && !portProfile.getItems().isEmpty() ){
            			for(SingleTableItem item : portProfile.getItems()){
            				if(null != item ){
            					if(item.getNonGlobalId() == dataSource.getId().longValue()){
            						networkTemplates.add(newWorkPolicy.getConfigName());
                        			break;
            					}
            				}
            			}
            		}
            	}
        	}
        }
        if(!networkTemplates.isEmpty()){
        	if(networkTemplates.size() < 5){
        		String wt = "";
        		for(int i = 0;i < networkTemplates.size();i++){
        			wt = wt.concat(" " + networkTemplates.get(i) + "," );
        		}
        		wt = wt.substring(0,wt.length()-1);
        		return MgrUtil.getUserMessage("config.networkpolicy.other.port.template.change.desc",new String[]{wt});
        	}else{
        		String wt = "";
        		for(int i = 0;i < networkTemplates.size();i++){
        			wt = wt.concat(" " + networkTemplates.get(i) + "," );
        		}
        		wt = wt.substring(0,wt.length()-1);
        		return MgrUtil.getUserMessage("config.networkpolicy.other.port.template.change.desc",new String[]{wt + " and so on"});
        	}
        }
        if(curPortProfile != null){
        	if(curPortProfile.getItems() != null && !curPortProfile.getItems().isEmpty()){
        		String wt = "";
        		int conflict = 0;
        		List<String> oldName = new ArrayList<String>();
        		for(int i = 0; i < curPortProfile.getItems().size();i++){
        			if(curPortProfile.getItems().get(i).getConfigTemplateId() == currentPolicyID){
        				conflict++;
        				Long id = curPortProfile.getItems().get(i).getNonGlobalId();
            			PortGroupProfile devTem = null;
            			if (id != null){
            				try{
            					devTem = QueryUtil.findBoById(PortGroupProfile.class, id);
            				}catch(Exception e){
            					devTem = null;
            				}
            			}
            			oldName.add(devTem.getName());
            		    /*wt = wt.concat(" " + devTem.getName() + ",");*/	
        			}
        		}
        		wt = NetworkPolicyAction.cleanDuplicateTemplateName(oldName);
        		if(conflict > 0){
        			wt = wt.substring(0,wt.length()-1);
            		return MgrUtil.getUserMessage("error.config.networkPolicy.deviceTemplates.classification.modify",new String[]{curPortProfile.getName(),wt});
        		}
        	}
        }
        if(currentTemplate != null){
            for(PortGroupProfile portProfile : selectPortProfilelst){
           	 //check the other network policies have the same DeviceTemplate, if yes, do not change anything!
           	    if((!portProfile.getId().equals(dataSource.getId())) && portProfile.getDeviceType() == deviceType){
           		   for(String devicemodle : portProfile.getDeviceModelStrs()){
           			   int returnValue = Arrays.binarySearch(deviceModels.split(","), devicemodle);
           			       if(returnValue >= 0)
    						   return MgrUtil.getUserMessage("config.networkpolicy.current.port.template.change.desc",new String[]{" " + portProfile.getName()});
           		   }
           	    }
            }
        }
        return err;
    }

    /**
     * 
     * 
     * @author Yunzhi Lin
     * - Time: Dec 7, 2012 7:10:45 PM
     * @return
     * @throws JSONException
     * @throws Exception
     */
    private String createPortTemplate() throws JSONException, Exception {
        setPortSettings();
        String profileName = getDataSource().getName();
        if(StringUtils.isNotBlank(profileName)) {
            String trProfileName = profileName.trim();
            // check the profile name
            if (trProfileName.length() > getProfileNameLength()) {
                addActionErrorMsg(MgrUtil.getUserMessage("error.config.lanProfile.Name.exceed"));
                return getReturnPathWithJsonMode(INPUT, "jsoninput", "json");
            }
            // check is the profile name duplicate
            if(checkNameExists("name", trProfileName)) {
                if(isJsonMode()) {
                    addActionErrorMsg(MgrUtil.getUserMessage("error.objectExists",trProfileName));
                }
                return getReturnPathWithJsonMode(INPUT, "jsoninput", "json");
            }
            // check the profile description
            if (StringUtils.trimToEmpty(getDataSource().getDescription()).length() > getProfileDescirptionLength()) {
                addActionErrorMsg(MgrUtil.getUserMessage("error.config.lanProfile.Description.exceed"));
                return getReturnPathWithJsonMode(INPUT, "jsoninput", "json");
            }
            //FIXME now support to configure USB port now (auto bind the WAN to profile which is functioning as Router)
            //bindWAN2Router((PortGroupProfile) dataSource);
            
            id = createBo(dataSource);
            if ("create".equals(operation)) {
                if(isJsonMode()) {
                    jsonObject = new JSONObject(); 
                    jsonObject.put("succ", true);
                    jsonObject.put("id", id);
                    jsonObject.put("newState", true);
                    return "json";
                } else {
                    return prepareBoList();
                }
            } else {
                setUpdateContext(true);
                return getLstForward();
            }
        } else {
            addActionErrorMsg(MgrUtil.getUserMessage("error.config.lanProfile.noName"));
            return getReturnPathWithJsonMode(INPUT, "jsoninput", "json");
        }
    }

    @Deprecated
    private void bindWAN2Router(PortGroupProfile deviceTemplate) {
        if(deviceTemplate.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER) {
            //find out the match WAN
            PortAccessProfile accessProfile = createWanPortType(
                    getDomain(),
                    deviceTemplate.getPortNum() < 24);
            if(null != accessProfile) {
                PortBasicProfile basic = new PortBasicProfile();
                basic.setAccessProfile(accessProfile);
                // FIXME now the USB port is always zero, maybe need to change below code if not
                basic.setUsbPorts(PortBasicProfile.DEFAULT_USB_PORTS);
                
                deviceTemplate.getBasicProfiles().add(basic);
            }
        }
    }
    private PortAccessProfile createWanPortType(HmDomain owner, boolean isRouterAP){
        // remove BeParaModule.PRE_DEFINED_PORTTYPE_WAN_ROUTER & BeParaModule.PRE_DEFINED_PORTTYPE_WAN quick start policy, do not support it now 
    	return null;
    }
    
    private String clonePortTemplate() throws Exception {
        long cloneId = getSelectedIds().get(0);
        PortGroupProfile clonedObj = findBoById(PortGroupProfile.class, cloneId, getQueryBoByMode(LazyMode.EDIT));
        clonedObj.setId(null);
        clonedObj.setName("");
        clonedObj.setOwner(null);
        clonedObj.setVersion(null);
        
        List<PortBasicProfile> basics= new ArrayList<>();
        basics.addAll(clonedObj.getBasicProfiles());
        clonedObj.setBasicProfiles(basics);
        
        List<PortMonitorProfile> monitors = new ArrayList<>();
        monitors.addAll(clonedObj.getMonitorProfiles());
        clonedObj.setMonitorProfiles(monitors);
        
        List<PortPseProfile> pses = new ArrayList<>();
        pses.addAll(clonedObj.getPortPseProfiles());
        clonedObj.setPortPseProfiles(pses);
        
        setSessionDataSource(clonedObj);
        return getINPUTType();
    }
    
    private String configAccess4Ports() throws JSONException {
        try {
            if(null == id) {
                jsonObject.put("errMsg", "Unkown error when configure ports.");
            } else {
                PortGroupProfile editBo = findBoById(PortGroupProfile.class, id ,getQueryBoByMode(LazyMode.EDIT));
                if (null != editBo) {
                    List<PortBasicProfile> tmpList = new ArrayList<>();
                    PortBasicProfile basic;
                    for (String groupStr : jsonGroups) {
                        basic = buildBasicProfile(groupStr);
                        if (null != basic) {
                            tmpList.add(basic);
                        }
                    }
                    updateBasicPorts(editBo, tmpList);
                    for (PortBasicProfile portBasicProfile : tmpList) {
                        final PortAccessProfile access = portBasicProfile.getAccessProfile();
                        if(null != access) {
                            editBo.getBasicProfiles().add(portBasicProfile);
                        }
                    }
                    if(isValidatePortGroup(editBo.getBasicProfiles(), tmpList, editBo.getDeviceType(), editBo.isChesapeake(), editBo.getPortNum())) {
                        // configure the monitor
                    	if(editBo.isChesapeake()){
                    		List<PortMonitorProfile> portMirror= buildPortMonitorProfile(editBo);
                        	editBo.setMonitorProfiles(portMirror);
                    	}
                    	
                    	if(jsonObject.optBoolean("monitor") && validateMirrorSourceVlanEnabled(editBo)){
                    		jsonObject.put("succ", false);
                    		return "json";
                    	}
                    	
                    	if(validateAggWithMirrorSourcePortChannel(editBo)){
                    		jsonObject.put("succ", false);
                    		return "json";
                    	}
                    	
                    	if(validatePortAsMirrorSource(editBo)){
                			jsonObject.put("succ", false);
                    		return "json";
                		}
                        // PSE
                        if(isSupportPse(editBo)){
                            boolean isSupportAll = false;
                            // if SR24, only support 8 port PSE, other Switch support all port
                            if(!editBo.getDeviceModels().equals(String.valueOf(HiveAp.HIVEAP_MODEL_SR24))){
                                isSupportAll = true;
                            }
                            List<PortPseProfile> portPseProfileList = buildPseProfiles(editBo,isSupportAll);
                            editBo.getPortPseProfiles().clear();
                            editBo.getPortPseProfiles().addAll(portPseProfileList);
                        }
                        
                        if(jsonObject.optBoolean("monitor")) {
                            setSessionDataSource(editBo);
                        } else {
                        	Date oldVer = editBo.getVersion();
                            QueryUtil.updateBo(editBo);
                            PortGroupProfile editBoNew = findBoById(PortGroupProfile.class, editBo.getId());
                            HmBeEventUtil.eventGenerated(new ConfigurationChangedEvent(
                            		editBoNew, ConfigurationChangedEvent.Operation.UPDATE,
            						oldVer));
                        }
                        
                        jsonObject.put("succ", true);
                        jsonObject.put("id", id);
                    }
            }
            }
        } catch (Exception e) {
            LOG.error("Error when parse string to JSON", e);
            jsonObject.put("errMsg", "Error occurs when assgin the ports.");
        }
        return "json";
    }

    private void updateAdditionalPortSettings() throws Exception {
        if (isEditAdditionalSettings()) {
            preparePortPseProfile();
            updateMirrorSettings();
            //setPortSettings();
        } else {
            resetPortSettings();
            setPortSettings();
        }
    }

    private void resetPortSettings() {
        if(getDataSource().getPortNum() != portNum
                || getDataSource().getDeviceType() != deviceType) {
            // reset the profile
            getDataSource().setLoadBalanceMode(PortGroupProfile.LOADBLANCE_MODE_SRC_DET_MAC_IP_PORT);
            getDataSource().getBasicProfiles().clear();
            getDataSource().getMonitorProfiles().clear();
            getDataSource().getPortPseProfiles().clear();
        }
    }

    private boolean isValidatePortGroup(List<PortBasicProfile> basicList,
            List<PortBasicProfile> addBasicList, short deviceType,
            boolean isChesapeake, short portNum) throws JSONException {
        // max 3 WAN supported on SR (included USB), BR (included USB, ETH0)  
        final int WAN_MAX_SUPPORT_COUNT = 2, WAN_MAX_SUPPORT_BR_COUNT = 1, MIRROR_MAX_SUPPORT_COUNT = 4;
        int current_wan_ports = 0, current_mirror_ports = 0;
        Map<Short, Long> portChannelAceesMap = new HashMap<>();
        for (PortBasicProfile basic : basicList) {
            final PortAccessProfile access = basic.getAccessProfile();
            final String allPortDesc = basic.getAllPortDesc(isChesapeake, portNum);
            if(deviceType == HiveAp.Device_TYPE_SWITCH 
                    && access.getPortType() == PortAccessProfile.PORT_TYPE_WAN) {
                jsonObject.put("errMsg", 
                        MgrUtil.getUserMessage("warn.port.template.invalid.noWan", allPortDesc));
                return false;
            }
            if(null != basic.getUSBs()
                    && access.getPortType() != PortAccessProfile.PORT_TYPE_WAN) {
                jsonObject.put("errMsg", MgrUtil.getUserMessage("warn.port.template.invalid.onlyWan"));
                return false;
            }
            if(access.getPortType() == PortAccessProfile.PORT_TYPE_WAN) {
                if(null != basic.getETHs()) {
                    current_wan_ports += basic.getETHs().length;
                }
                if(isChesapeake && null != basic.getSFPs()) {
                    current_wan_ports += basic.getSFPs().length;
                }
                if(isChesapeake && null != basic.getUSBs()) {
                    //current_wan_ports += basic.getUSBs().length;
                }
                if(isChesapeake && WAN_MAX_SUPPORT_COUNT < current_wan_ports) {
                    jsonObject.put("errMsg", MgrUtil.getUserMessage("warn.port.template.invalid.maxWan", 
                            new String[] {trNumber2Word(WAN_MAX_SUPPORT_COUNT), getPluralSuffix(WAN_MAX_SUPPORT_COUNT), ""}));
                    return false;
                } else if (!isChesapeake && portNum == 5 && WAN_MAX_SUPPORT_BR_COUNT < current_wan_ports){
                    jsonObject.put("errMsg", MgrUtil.getUserMessage("warn.port.template.invalid.maxWan", 
                            new String[] {trNumber2Word(WAN_MAX_SUPPORT_BR_COUNT), getPluralSuffix(WAN_MAX_SUPPORT_BR_COUNT), "LAN"}));
                    return false;
                }
            } else if(access.getPortType() == PortAccessProfile.PORT_TYPE_MONITOR) {
                if(null != basic.getETHs()) {
                    current_mirror_ports += basic.getETHs().length; 
                }
                if(null != basic.getSFPs()) {
                    current_mirror_ports += basic.getSFPs().length; 
                }
                if(null != basic.getUSBs()) {
                    current_mirror_ports += basic.getUSBs().length; 
                }
                if(MIRROR_MAX_SUPPORT_COUNT < current_mirror_ports) {
                    jsonObject.put("errMsg",
                            MgrUtil.getUserMessage("warn.port.template.invalid.maxMirror", trNumber2Word(MIRROR_MAX_SUPPORT_COUNT)));
                    return false;
                }
            }
            if(!checkPortChannel(basic, addBasicList, portChannelAceesMap, isChesapeake, portNum)) {
                return false;
            }
        }
        return true;
    }

    private String getPluralSuffix(final int count) {
        return count > 1 ? "s" : "";
    }
    
    private String trNumber2Word(int number) {
        final String[] basic = {"zero" , "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
        if(number >= 0 && number < 10) {
            return basic[number];
        }
        return "";
    }

    private boolean checkPortChannel(PortBasicProfile basic, List<PortBasicProfile> addBasicList, 
            Map<Short, Long> map, boolean isChesapeake, short portNum) throws JSONException {
        final int PORTCHANNEL_MAX_SUPPORT_COUNT = 8;
        final PortAccessProfile access = basic.getAccessProfile();
        if (basic.isExistPortChannel() && null != access) {
            final String allPortDesc = basic.getAllPortDesc(isChesapeake, portNum);
            if(null == map.get(basic.getPortChannel())) {
                map.put(basic.getPortChannel(), access.getId());
            } else {
                basic.setEnabledlinkAggregation(false);
                jsonObject
                .put("errMsg",
                        MgrUtil.getUserMessage("warn.port.template.invalid.aggUsed", 
                                new String[]{allPortDesc, ""+basic.getPortChannel()}));
                return false;
            }
            if (existMultiPhyPortType(basic)) {
                basic.setEnabledlinkAggregation(false);
                jsonObject
                        .put("errMsg",
                                MgrUtil.getUserMessage("warn.port.template.invalid.aggSame", 
                                        new String[]{allPortDesc, ""+ basic.getPortChannel()}));
                return false;
            } else if(access.getPortType() == PortAccessProfile.PORT_TYPE_ACCESS
                    || access.getPortType() == PortAccessProfile.PORT_TYPE_8021Q) {
                if(access.isRadiusAuthEnable()) {
                    if(null != addBasicList && !addBasicList.isEmpty() && addBasicList.contains(basic)) {
                        /*
                        basic.setEnabledlinkAggregation(false);
                        jsonObject
                        .put("errMsg",
                                "The "+ allPortDesc
                                + "(binded with authentication enabled " 
                                + "type access profile) cannot enable the port-channel.");
                        return false;
                        */
                    }
                } else {
                }
                // unable to select more than 8 Gigabitethernet interfaces add to the same port-channel
                int count = 0;
                final String[] eths = basic.getETHs();
                final String[] sfps = basic.getSFPs();
                final String[] usbs = basic.getUSBs();
                if(null != eths) {
                    count += eths.length;
                }
                if(null != sfps) {
                    count += sfps.length;
                }
                if(PORTCHANNEL_MAX_SUPPORT_COUNT < count) {
                    basic.setEnabledlinkAggregation(false);
                    jsonObject
                    .put("errMsg",
                            MgrUtil.getUserMessage("warn.port.template.invalid.maxAgg"));
                    return false;
                }
                if(null != usbs) {
                    count += usbs.length;
                }
            } else if (!(access.getPortType() == PortAccessProfile.PORT_TYPE_ACCESS
                    || access.getPortType() == PortAccessProfile.PORT_TYPE_8021Q)) {
                basic.setEnabledlinkAggregation(false);
                jsonObject
                        .put("errMsg",
                                MgrUtil.getUserMessage("warn.port.template.invalid.enableAgg", 
                                        new String[] {allPortDesc, MgrUtil.getEnumString("enum.portConfig.port.type."+access.getPortType())}));
                return false;
            }
        }
        return true;
    }

    private boolean existMultiPhyPortType(PortBasicProfile basic) {
        return (StringUtils.isNotBlank(basic.getEthPorts()) && StringUtils.isNotBlank(basic.getSfpPorts()))
                || (StringUtils.isNotBlank(basic.getEthPorts()) && StringUtils.isNotBlank(basic.getUsbPorts()))
                || (StringUtils.isNotBlank(basic.getUsbPorts()) && StringUtils.isNotBlank(basic.getSfpPorts()));
    }

    private boolean updateBasicPorts(PortGroupProfile portTemplate, List<PortBasicProfile> tmpList)
            throws JSONException {
        boolean succ = true;
        if(null != tmpList && !tmpList.isEmpty() && null != portTemplate) {
            List<PortBasicProfile> removableList = new ArrayList<>();
            List<PortBasicProfile> removableList2 = new ArrayList<>();
            for (PortBasicProfile pbProfile : tmpList) {
                String[] eths = pbProfile.getETHs();
                String[] sfps = pbProfile.getSFPs();
                String[] usbs = pbProfile.getUSBs();
                for (PortBasicProfile basic : portTemplate.getBasicProfiles()) {
                    if(!pbProfile.isExistPortChannel() && basic.isExistPortChannel()) {
                        int oldLength = (null == basic.getETHs() ? 0 : basic.getETHs().length)
                                + (null == basic.getSFPs() ? 0 : basic.getSFPs().length)
                                + (null == basic.getUSBs() ? 0 : basic.getUSBs().length);
                        // remove ports from exist basic port profiles
                        basic.setEthPorts(getPortStr(removePorts(eths, basic.getETHs())));
                        basic.setSfpPorts(getPortStr(removePorts(sfps, basic.getSFPs())));
                        basic.setUsbPorts(getPortStr(removePorts(usbs, basic.getUSBs())));
                        
                        int newLength = (null == basic.getETHs() ? 0 : basic.getETHs().length)
                                + (null == basic.getSFPs() ? 0 : basic.getSFPs().length)
                                + (null == basic.getUSBs() ? 0 : basic.getUSBs().length);
                        if(oldLength > newLength) {
                            // comment it for because delta config works now
                            //jsonObject.put("aggWarning", MgrUtil.getUserMessage("warn.port.template.removefromAgg", ""+basic.getPortChannel()));
                        }
                    } else {
                        // remove ports from exist basic port profiles
                        basic.setEthPorts(getPortStr(removePorts(eths, basic.getETHs())));
                        basic.setSfpPorts(getPortStr(removePorts(sfps, basic.getSFPs())));
                        basic.setUsbPorts(getPortStr(removePorts(usbs, basic.getUSBs())));
                    }
                    
                    // bind ports with same port-channel
                    if(pbProfile.isExistPortChannel() 
                            && basic.isExistPortChannel()
                            && pbProfile.getAccessProfile().getId().compareTo(basic.getAccessProfile().getId()) == 0
                            && pbProfile.getPortChannel() == basic.getPortChannel()) {
                        if(StringUtils.isBlank(basic.getEthPorts())) {
                            basic.setEthPorts(getPortStr(eths));
                        } else {
                            if(null != getPortStr(eths)) {
                                basic.setEthPorts(basic.getEthPorts()+PortBasicProfile.PORTS_SPERATOR+getPortStr(eths));
                            }
                        }
                        if(StringUtils.isBlank(basic.getSfpPorts())) {
                            basic.setSfpPorts(getPortStr(sfps));
                        } else {
                            if(null != getPortStr(sfps)) {
                                basic.setSfpPorts(basic.getSfpPorts()+PortBasicProfile.PORTS_SPERATOR+getPortStr(sfps));
                            }
                        }
                        if(StringUtils.isBlank(basic.getUsbPorts())) {
                            basic.setUsbPorts(getPortStr(usbs));
                        } else {
                            if(null != getPortStr(usbs)) {
                                basic.setUsbPorts(basic.getUsbPorts()+PortBasicProfile.PORTS_SPERATOR+getPortStr(usbs));
                            }
                        }
                        removableList2.add(pbProfile);
                    }
                    
                    if(StringUtils.isBlank(basic.getEthPorts())
                            && StringUtils.isBlank(basic.getSfpPorts())
                            && StringUtils.isBlank(basic.getUsbPorts())) {
                        removableList.add(basic);
                    }
                }  
            }
            portTemplate.getBasicProfiles().removeAll(removableList);
            tmpList.removeAll(removableList2);
        }
        
        return succ;
    }

    private String[] removePorts(String[] needRemovePort, String[] tempPorts) {
        if(null != tempPorts && null != needRemovePort) {
            for (String usb : needRemovePort) {
                int index = ArrayUtils.indexOf(tempPorts, usb);
                if(index >= 0) {
                    tempPorts = ArrayUtils.remove(tempPorts, index);
                }
            }
        }
        return tempPorts;
    }

    private String getPortStr(String[] ports) {
        if(null != ports && ports.length > 0) {
            final String str = Arrays.toString(ports);
            return StringUtils.deleteWhitespace(str.substring(1, str.length()-1));
        } else {
            return null;
        }
    }
    
    private PortBasicProfile buildBasicProfile(String groupStr) throws JSONException {
        PortBasicProfile basic = new PortBasicProfile();
        
        if (null != selectedAccessIds && !selectedAccessIds.isEmpty()) {
            PortAccessProfile profile = QueryUtil.findBoByAttribute(
                    PortAccessProfile.class, "id", selectedAccessIds.get(0), getDomainId(), new QueryBo() {
                        @Override
                        public Collection<HmBo> load(HmBo bo) {
                            if (bo instanceof PortAccessProfile) {
                                PortAccessProfile access = (PortAccessProfile) bo;
                                if(null != access.getCwp()) {
                                    access.getCwp().getId();
                                }
                            }
                            return null;
                        }
                    });
            if (null != profile) {
                basic.setAccessProfile(profile);
                
                jsonObject.put("monitor", profile.getPortType() == PortAccessProfile.PORT_TYPE_MONITOR);
            }
        }
        JSONObject jsonObj = new JSONObject(groupStr);
        JSONObject jsonPorts = jsonObj.getJSONObject("ports");
        LOG.debug("jsonPorts is " + jsonPorts);
        JSONArray ethPort = jsonPorts.optJSONArray(ETH);
        JSONArray sfpPort = jsonPorts.optJSONArray(SFP);
        JSONArray usbPort = jsonPorts.optJSONArray(USB);
        
        // link aggregation
        basic.setPortChannel((short) jsonObj.optInt("portChannel", 0));
        basic.setEnabledlinkAggregation(basic.getPortChannel() > 0);
        
        if (null != ethPort) {
            final String joinStr = ethPort.join(PortBasicProfile.PORTS_SPERATOR);
            LOG.debug("ETH: " + joinStr);
            if (StringUtils.isNotEmpty(joinStr)) {
                basic.setEthPorts(joinStr);
            }
        }
        if (null != sfpPort) {
            final String joinStr = sfpPort.join(PortBasicProfile.PORTS_SPERATOR);
            LOG.debug("SFP: " + joinStr);
            if (StringUtils.isNotEmpty(joinStr)) {
                basic.setSfpPorts(joinStr);
            }
        }
        if (null != usbPort) {
            final String joinStr = usbPort.join(PortBasicProfile.PORTS_SPERATOR);
            LOG.debug("USB: " + joinStr);
            if (StringUtils.isNotEmpty(joinStr)) {
                basic.setUsbPorts(joinStr);
            }
        }
        return basic;
    }

    private void setPortSettings() {
        getDataSource().setDeviceType(deviceType);
        getDataSource().setDeviceModels(deviceModels);
        getDataSource().setPortNum(portNum);
    }

    private String getINPUTType() {
        return isJsonMode() ? "jsoninput" : INPUT;
    }
    
    public String getChangedName() {
        return getDataSource().getName().replace("\\", "\\\\").replace("'", "\\'");
    }
    public int getProfileNameLength() {
        return 32;
    }
    public int getProfileDescirptionLength() {
        return 64;
    }
    
    /**
     * if isSupportAll is false, just support ETH1-ETH8
     * 
     */
    private List<PortPseProfile> buildPseProfiles(PortGroupProfile portGroupProfile,boolean isSupportAll) {
    	List<PortPseProfile> PortPseProfiles = new ArrayList<>();
    	
    	if(isSupportAll){
    		List<String> allEths = portGroupProfile.getAllETHs();
        	if(allEths != null){
        		sortList(allEths);
        		for(String eth : allEths){
            		PortPseProfile portPseProfile = new PortPseProfile();
            		short num = DeviceInfType.Gigabit.getFinalValue(Integer.valueOf(eth), portGroupProfile.getFirstHiveApModel());
            		setPortPseProfileContent(portGroupProfile,portPseProfile,num);
            		PortPseProfiles.add(portPseProfile);
            	}
        	}
/*        	List<String> allSPFs = portGroupProfile.getAllSFPs();
        	if(allSPFs != null){
        		sortList(allSPFs);
        		for(String sfp : allSPFs){
            		PortPseProfile portPseProfile = new PortPseProfile();
            		short num = DeviceInfType.SFP.getFinalValue(Integer.valueOf(sfp));
            		setPortPseProfileContent(portGroupProfile,portPseProfile,num);
            		PortPseProfiles.add(portPseProfile);
            	}
        	}
        	List<String> allUSBs = portGroupProfile.getAllUSBs();
        	if(allUSBs != null){
        		sortList(allUSBs);
        		for(String usb : allUSBs){
            		PortPseProfile portPseProfile = new PortPseProfile();
            		short num = DeviceInfType.USB.getFinalValue(Integer.valueOf(usb));
            		setPortPseProfileContent(portGroupProfile,portPseProfile,num);
            		PortPseProfiles.add(portPseProfile);
            	}
        	}*/
        	
    	} else {
        	List<String> allEths = portGroupProfile.getAllETHs();
        	if(allEths != null){
        		sortList(allEths);
        		for(String eth : allEths){
        			if(Integer.valueOf(eth)>8){
        				continue;
        			}
            		PortPseProfile portPseProfile = new PortPseProfile();
            		short num = DeviceInfType.Gigabit.getFinalValue(Integer.valueOf(eth), portGroupProfile.getFirstHiveApModel());
            		setPortPseProfileContent(portGroupProfile,portPseProfile,num);
            		PortPseProfiles.add(portPseProfile);
            	}
        	}
    	}
    
    	return PortPseProfiles;
    }
    
    private void setPortPseProfileContent(PortGroupProfile portGroupProfile,PortPseProfile portPseProfile,short num){
    	portPseProfile.setInterfaceNum(num);
    	boolean isNewStatus = true;
    	if(portGroupProfile.getPortPseProfiles() != null){
			for(PortPseProfile pseProfile : portGroupProfile.getPortPseProfiles()){
        		if(num == pseProfile.getInterfaceNum()){
        			portPseProfile.setPseProfile(pseProfile.getPseProfile());
        			portPseProfile.setEnabelIfPse(pseProfile.isEnabelIfPse());
        			isNewStatus = false;
        			break;
        		}
        	}
    	}
    	// remove quick start policies
    	if(isNewStatus){
    		portPseProfile.setEnabelIfPse(isNewStatus);
    		PseProfile pseProfile = HmBeParaUtil.getDefaultProfile(PseProfile.class, null);;
    		portPseProfile.setPseProfile(pseProfile);
    	}
    }
    private boolean isSupportPse(PortGroupProfile portGroupProfile){
    	boolean result = false;
    	if(portGroupProfile.getPortNum() >= 24){
    		result =true;
    	}
    	return result;
    }
    
    private void sortList(List<String> list){
    	Collections.sort(list, new Comparator<Object>() {
			@Override
			public int compare(final Object o1, final Object o2) {
				 final int n1=Integer.parseInt(o1.toString());
			     final int n2=Integer.parseInt(o2.toString());
				return n1 - n2;
			}
         });
    }
    
    private void preparePortPseProfile() throws Exception{
    	for(PortPseProfile portPseProfile : getDataSource().getPortPseProfiles()){
			if (arrayInterfaceNum!=null){
				for(int i =0;i<arrayInterfaceNum.length;i++){
					if (portPseProfile.getInterfaceNum()==arrayInterfaceNum[i]){
						if(portPseProfileIds != null && i < portPseProfileIds.length){
							PseProfile pseProfile =  findBoById(PseProfile.class,
		            				portPseProfileIds[i]);
		            		portPseProfile.setPseProfile(pseProfile);
						}
						break;
					}
				}
			}
			
			boolean blnEnableIfPse = false;
			if (arrayEnabelIfPse!=null){
				for(String strEnabelIfPse :arrayEnabelIfPse){
					if (String.valueOf(portPseProfile.getInterfaceNum()).equals(strEnabelIfPse)){
						blnEnableIfPse = true;
						break;
					}
				}
			}
			portPseProfile.setEnabelIfPse(blnEnableIfPse);
    	}
    }
    
    public List<CheckItem> getAvailablePortPseProfile(){
    	return getBoCheckItemsSort("name", PseProfile.class, null, 
    			new SortParams("name"),
    			CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
    }
    
    private Long[] arrayInterfaceNum;
    private String[] arrayEnabelIfPse;
    private Long[] portPseProfileIds;
    private Long pseProfileId;
    
    public Long[] getArrayInterfaceNum() {
		return arrayInterfaceNum;
	}

	public void setArrayInterfaceNum(Long[] arrayInterfaceNum) {
		this.arrayInterfaceNum = arrayInterfaceNum;
	}

	public String[] getArrayEnabelIfPse() {
		return arrayEnabelIfPse;
	}

	public void setArrayEnabelIfPse(String[] arrayEnabelIfPse) {
		this.arrayEnabelIfPse = arrayEnabelIfPse;
	}

	public Long getPseProfileId() {
		return pseProfileId;
	}

	public void setPseProfileId(Long pseProfileId) {
		this.pseProfileId = pseProfileId;
	}

	public Long[] getPortPseProfileIds() {
		return portPseProfileIds;
	}

	public void setPortPseProfileIds(Long[] portPseProfileIds) {
		this.portPseProfileIds = portPseProfileIds;
	}
	enum LazyMode {
        EDIT;
    }
    private QueryBo getQueryBoByMode(LazyMode mode) {
        QueryBo lazyLoader = null;
        switch (mode) {
        case EDIT:
            lazyLoader =new QueryBo() {
                @Override
                public Collection<HmBo> load(HmBo bo) {
                    if(bo instanceof PortGroupProfile) {
                        PortGroupProfile profile = ((PortGroupProfile) bo);
                        if(!profile.getBasicProfiles().isEmpty()) {
                            profile.getBasicProfiles().size();
                            for (PortBasicProfile basic : profile.getBasicProfiles()) {
                                basic.getAccessProfile().getId();
                                if(null != basic.getAccessProfile().getCwp()) {
                                    basic.getAccessProfile().getCwp().getId();
                                }
                            }
                        }
                        if(!profile.getPortPseProfiles().isEmpty()) {
                            profile.getPortPseProfiles().size();
                        }
                        if(!(profile.getMonitorProfiles() == null ||profile.getMonitorProfiles().isEmpty())){
                        	profile.getMonitorProfiles().size();
                        }
                    }
                    return null;
                }
            };
            break;

        default:
            break;
        }
        return lazyLoader;
    }
    /*-----------Override---------------*/
    public static final int COLUMN_NAME = 1;
    public static final int COLUMN_DESCRIPTION = 2;
    @Override
    protected List<HmTableColumn> getDefaultSelectedColums() {
        List<HmTableColumn> tableColumns = new ArrayList<HmTableColumn>(5);
        tableColumns.add(new HmTableColumn(COLUMN_NAME));
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
        case COLUMN_DESCRIPTION:
            code = "config.dnsService.description";
            break;
        }
        return null == code ? "" : MgrUtil.getUserMessage(code);
    }
    @Override
    public PortGroupProfile getDataSource() {
        return (PortGroupProfile) dataSource;
    }
    @Override
    public void prepare() throws Exception {
        super.prepare();
        setSelectedL2Feature(L2_FEATURE_PORTTYPE);
        setDataSource(PortGroupProfile.class);
        this.tableId = HmTableColumn.TABLE_CONFIGURATION_LAN_PROFILE;
    }
    /*------------Fields---------------*/
    private String deviceModels;
    private short deviceType;
    private short portNum;
    private long parentNpId;
    private boolean haveNonDefaultTmps;
    
    // bind with access profiles
    private List<Long> selectedAccessIds;
    
    private boolean editAdditionalSettings;
    private long portTemplateIndex = -1;
    
    private String selectIDs;
    
    private long currentPolicyID;
    
    // current selected network policy
    private int limitType;
    
    // for AJAX
    private short portType;
    private long groupNum;
    private int[] ethPorts;
    private int[] sfpPorts;
    private int[] usbPorts;
    // --link aggregation
    private short portChannel;
    private short loadBlanceMode;
    private String[] jsonGroups; // for 24 ports: 90*24=2160; for 48 ports: 90*48=4320
    
    public EnumItem[] getEnumPortType() {
        return MgrUtil.enumItems("enum.portConfig.port.type.", new int[] {
                PortAccessProfile.PORT_TYPE_PHONEDATA, PortAccessProfile.PORT_TYPE_AP,
                PortAccessProfile.PORT_TYPE_MONITOR, PortAccessProfile.PORT_TYPE_ACCESS,
                PortAccessProfile.PORT_TYPE_8021Q, PortAccessProfile.PORT_TYPE_WAN });
    }

    public EnumItem[] getEnumLoadblanceMode() {
        return MgrUtil.enumItems("enum.portConfig.linkaggregation.mode.", new int[] {
                /*PortGroupProfile.LOADBLANCE_MODE_AUTO,*/
                PortGroupProfile.LOADBLANCE_MODE_SRC_DST_MAC,
                PortGroupProfile.LOADBLANCE_MODE_SRC_DET_IP,
                PortGroupProfile.LOADBLANCE_MODE_SRC_DET_IP_PORT,
                PortGroupProfile.LOADBLANCE_MODE_SRC_DET_MAC_IP_PORT });
    }
    /*-----------Getter/Setter---------------*/
    public short getPortType() {
        return portType;
    }
    public int[] getEthPorts() {
        return ethPorts;
    }
    public int[] getUsbPorts() {
        return usbPorts;
    }
    public void setPortType(short portType) {
        this.portType = portType;
    }
    public void setEthPorts(int[] ethPorts) {
        this.ethPorts = ethPorts;
    }
    public int[] getSfpPorts() {
        return sfpPorts;
    }
    public void setSfpPorts(int[] sfpPorts) {
        this.sfpPorts = sfpPorts;
    }

    public void setUsbPorts(int[] usbPorts) {
        this.usbPorts = usbPorts;
    }
    public long getGroupNum() {
        return groupNum;
    }
    public void setGroupNum(long groupNum) {
        this.groupNum = groupNum;
    }
    public short getPortChannel() {
        return portChannel;
    }
    public short getLoadBlanceMode() {
        return loadBlanceMode;
    }
    public void setPortChannel(short portChannel) {
        this.portChannel = portChannel;
    }
    public void setLoadBlanceMode(short loadBlanceMode) {
        this.loadBlanceMode = loadBlanceMode;
    }
    public String getDeviceModels() {
        return deviceModels;
    }
    public short getDeviceType() {
        return deviceType;
    }
    public short getPortNum() {
        return portNum;
    }
    public long getParentNpId() {
        return parentNpId;
    }
    public void setDeviceModels(String deviceModels) {
        this.deviceModels = deviceModels;
    }
    public void setDeviceType(short deviceType) {
        this.deviceType = deviceType;
    }
    public void setPortNum(short portNum) {
        this.portNum = portNum;
    }
    public void setParentNpId(long parentNpId) {
        this.parentNpId = parentNpId;
    }
    public String[] getJsonGroups() {
        return jsonGroups;
    }
    public void setJsonGroups(String[] jsonGroups) {
        this.jsonGroups = jsonGroups;
    }
    
    public List<CheckItem> getAllPorts(){
    	List<CheckItem> list = new ArrayList<CheckItem>();
    	HiveAp hiveAp = new HiveAp(Short.parseShort(getDataSource().getDeviceModels()));

    	int ethCounts = hiveAp.getDeviceInfo().getIntegerValue(
				DeviceInfo.SPT_ETHERNET_COUNTS);
		int sfpCounts = hiveAp.getDeviceInfo().getIntegerValue(
				DeviceInfo.SPT_SFP_COUNTS);
		int interType;
		if (ethCounts > 1) {
			interType = AhInterface.DEVICE_IF_TYPE_ETH1;
			while (ethCounts > 0) {
				list.add(new CheckItem((long) interType, MgrUtil
							.getEnumString("enum.switch.interface." + interType)));
				
				ethCounts--;
				interType++;
			}
		}
		if (sfpCounts > 0) {
			for(int i=0; i<sfpCounts; i++) {
				interType = DeviceInfType.SFP.getFinalValue(i+1, getDataSource().getFirstHiveApModel());
					list.add(new CheckItem((long) interType, MgrUtil
							.getEnumString("enum.switch.interface." + interType)));
				interType++;
			}
		}
		// add port channel
		if(getDataSource() != null && getDataSource().getBasicProfiles() != null){
			for(PortBasicProfile baseProfile : getDataSource().getBasicProfiles()){
				if(!baseProfile.isEnabledlinkAggregation()){
					continue; 
				}
				short protChannel = DeviceInfType.PortChannel.getFinalValue(baseProfile.getPortChannel(), getDataSource().getFirstHiveApModel());
				
				list.add(new CheckItem((long) protChannel, MgrUtil
						.getEnumString("enum.switch.interface." + protChannel)));

			}
		}
		
		Collections.sort(list, new Comparator<CheckItem>() {
			public int compare(CheckItem o1, CheckItem o2) {
				return (int) (o1.getId() - o2.getId());
			}
		});
		
		try {
			getPortChannelMemberPorts();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	return list;
    }
    
    private String[] ingressPorts;
    private String[] egressPorts;
    private String[] bothPorts;
    
	private Object[] enableMonitorSession;

	public Object[] getEnableMonitorSession() {
		return enableMonitorSession;
	}

	public void setEnableMonitorSession(Object[] enableMonitorSession) {
		if(enableMonitorSession.length == 1){
			if(enableMonitorSession[0].toString().equalsIgnoreCase("false")){
				this.enableMonitorSession = null;
			}else{
				this.enableMonitorSession = enableMonitorSession;
			}
		}else{
			this.enableMonitorSession = enableMonitorSession;
		}
	}

	public String[] getIngressPorts() {
		return ingressPorts;
	}

	public void setIngressPorts(String[] ingressPorts) {
		this.ingressPorts = ingressPorts;
	}

	public String[] getEgressPorts() {
		return egressPorts;
	}

	public void setEgressPorts(String[] egressPorts) {
		this.egressPorts = egressPorts;
	}

    
    public List<PortMonitorProfile> getMonitorPorts(){
    	
    	List<PortMonitorProfile> monitorProfiles = getDataSource().getMonitorProfiles();
    	if (monitorProfiles.isEmpty()){
        	List<PortBasicProfile> basicProfiles = getDataSource().getBasicProfiles();
        	for(PortBasicProfile basicProfile : basicProfiles){
        		if (basicProfile.getAccessProfile().getPortType() == PortAccessProfile.PORT_TYPE_MONITOR){
        			if (basicProfile.getETHs() != null){
        				for (String ethport : basicProfile.getETHs()){
        					monitorProfiles.add(new PortMonitorProfile(Short.valueOf(DeviceInfType.Gigabit.getFinalValue(Integer.valueOf(ethport), getDataSource().getFirstHiveApModel())), getDataSource().getFirstHiveApModel()));
        				}
        			}
        			
        			if (basicProfile.getSFPs() != null){
        				for (String sfpPort : basicProfile.getSFPs()){
        					monitorProfiles.add(new PortMonitorProfile(Short.valueOf(DeviceInfType.SFP.getFinalValue(Integer.valueOf(sfpPort), getDataSource().getFirstHiveApModel())), getDataSource().getFirstHiveApModel()));
        				}
        			}
        		}
        	}
        	
        	Collections.sort(monitorProfiles, new Comparator<PortMonitorProfile>() {
    			public int compare(PortMonitorProfile o1, PortMonitorProfile o2) {
    				return o1.getDestinationPort() - o2.getDestinationPort();
    			}
    		});
    	}
    	return monitorProfiles;
    }
    
    public void initPortNum(){
    	if (getDataSource().getDeviceType() == HiveAp.Device_TYPE_SWITCH){
    		initEthPortNum = getDataSource().getPortNum();
    		initSpfhPortNum = 4;
    	}
    }
    
    private int initSpfhPortNum = 0;
    private int initEthPortNum = 0;
    
    public int getInitEthPortNum() {
		return initEthPortNum;
	}

	public void setInitEthPortNum(int initEthPortNum) {
		this.initEthPortNum = initEthPortNum;
	}

	public int getInitSpfhPortNum() {
		return initSpfhPortNum;
	}

	public void setInitSpfhPortNum(int initSpfhPortNum) {
		this.initSpfhPortNum = initSpfhPortNum;
	}
	
	public String[] getBothPorts() {
		return bothPorts;
	}

	public void setBothPorts(String[] bothPorts) {
		this.bothPorts = bothPorts;
	}
	
	public boolean isMonitor(){
    	for(PortBasicProfile basicProfile : getDataSource().getBasicProfiles()){
    		if (basicProfile.getAccessProfile().getPortType() == PortAccessProfile.PORT_TYPE_MONITOR){
    			return true;
    		}
    	}
    	return false;
	}
	
	public List<CheckItem> getSourcePortsList(){
		List<CheckItem> list = getAllPorts();

		for (PortBasicProfile basicProfile : getDataSource().getBasicProfiles()) {
			if (basicProfile.getAccessProfile().getPortType() == PortAccessProfile.PORT_TYPE_MONITOR) {
				if (basicProfile.getETHs() != null) {
					for (String ethport : basicProfile.getETHs()) {
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getId() == DeviceInfType.Gigabit.getFinalValue(Integer.valueOf(ethport), getDataSource().getFirstHiveApModel())) {
								list.remove(i);
							}
						}
					}
				}

				if (basicProfile.getSFPs() != null) {
					for (String sfpPort : basicProfile.getSFPs()) {
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getId() == DeviceInfType.SFP.getFinalValue(Integer.valueOf(sfpPort), getDataSource().getFirstHiveApModel())) {
								list.remove(i);
							}
						}
					}
				}
			}
		}

		Collections.sort(list, new Comparator<CheckItem>() {
			public int compare(CheckItem o1, CheckItem o2) {
				return (int) (o1.getId() - o2.getId());
			}
		});

    	return list;
	}

    public boolean isEditAdditionalSettings() {
        return editAdditionalSettings;
    }

    public void setEditAdditionalSettings(boolean editAdditionalSettings) {
        this.editAdditionalSettings = editAdditionalSettings;
    }

    public List<Long> getSelectedAccessIds() {
        return selectedAccessIds;
    }

    public void setSelectedAccessIds(List<Long> selectedAccessIds) {
        this.selectedAccessIds = selectedAccessIds;
    }
    
    private String allPortChannelMemberPorts;

	public String getAllPortChannelMemberPorts() {
		return allPortChannelMemberPorts;
	}

	public void setAllPortChannelMemberPorts(String allPortChannelMemberPorts) {
		this.allPortChannelMemberPorts = allPortChannelMemberPorts;
	}
	
    private String getPortChannelMemberPorts() throws JSONException{
		JSONObject tmpObj = new JSONObject();
		
		if(getDataSource() != null && getDataSource().getBasicProfiles() != null){
			for(PortBasicProfile baseProfile : getDataSource().getBasicProfiles()){
				JSONArray tmpArray = new JSONArray();
				if(!baseProfile.isEnabledlinkAggregation()){
					continue; 
				}
				short portChannel = DeviceInfType.PortChannel.getFinalValue(baseProfile.getPortChannel(), getDataSource().getFirstHiveApModel());
				String[] eths = baseProfile.getETHs();
				String[] sfps = baseProfile.getSFPs();
				int interType;
				if(eths != null && eths.length > 0){
					for(int i=0; i<eths.length; i++){
						interType = DeviceInfType.Gigabit.getFinalValue(Integer.valueOf(eths[i]), getDataSource().getFirstHiveApModel());
						tmpArray.put(interType);
					}
				}
				if(sfps != null && sfps.length > 0){
					for(int i=0; i<sfps.length; i++){
						interType = DeviceInfType.SFP.getFinalValue(Integer.valueOf(sfps[i]), getDataSource().getFirstHiveApModel());
						tmpArray.put(interType);
					}
				}
				tmpObj.put(Short.toString(portChannel), tmpArray);
			}
		}
		allPortChannelMemberPorts = tmpObj.toString();
		return allPortChannelMemberPorts;
	}

    
    
    private void setPortMonitorProfileContent(PortGroupProfile portGroupProfile,PortMonitorProfile portMonitorProfile,short num){
    	if(portGroupProfile.getMonitorProfiles() != null){
			for(PortMonitorProfile mirrorProfile : portGroupProfile.getMonitorProfiles()){
        		if(num == mirrorProfile.getDestinationPort()){
        			portMonitorProfile.setEnableMonitorSession(mirrorProfile.isEnableMonitorSession());
        			portMonitorProfile.setIngressPort(mirrorProfile.getIngressPort());
        			portMonitorProfile.setEgressPort(mirrorProfile.getEgressPort());
        			portMonitorProfile.setBothPort(mirrorProfile.getBothPort());
        			portMonitorProfile.setIngressVlan(mirrorProfile.getIngressVlan());
        			portMonitorProfile.setEnableVlans(mirrorProfile.isEnableVlans());
        			portMonitorProfile.setEnablePorts(mirrorProfile.isEnablePorts());
        		}
        	}
    	}
    }
    
    private List<PortMonitorProfile> buildPortMonitorProfile(PortGroupProfile portGroupProfile){
    	List<PortMonitorProfile> portMonitor = new ArrayList<PortMonitorProfile>();
    	for (PortBasicProfile portBasicProfile : portGroupProfile.getBasicProfiles()) {
        	if (portBasicProfile.getAccessProfile().getPortType() == PortAccessProfile.PORT_TYPE_MONITOR){
        		if (portBasicProfile.getETHs() != null){
    				for (String ethport : portBasicProfile.getETHs()){
    					short num = DeviceInfType.Gigabit.getFinalValue(Integer.valueOf(ethport), portGroupProfile.getFirstHiveApModel());
    	        		PortMonitorProfile portMonitorProfile = new PortMonitorProfile(num, portGroupProfile.getFirstHiveApModel());
    	        		setPortMonitorProfileContent(portGroupProfile,portMonitorProfile,num);
    	            	portMonitor.add(portMonitorProfile);
    				}
    			}
    			
    			if (portBasicProfile.getSFPs() != null){
    				for (String sfpPort : portBasicProfile.getSFPs()){
    					short num = DeviceInfType.SFP.getFinalValue(Integer.valueOf(sfpPort), portGroupProfile.getFirstHiveApModel());
    	        		PortMonitorProfile portMonitorProfile = new PortMonitorProfile(num, portGroupProfile.getFirstHiveApModel());
    	        		setPortMonitorProfileContent(portGroupProfile,portMonitorProfile,num);
    	           		portMonitor.add(portMonitorProfile);
    				}
    			}
    			
    			if (portBasicProfile.getUSBs() != null) {
    				for (String usbPort : portBasicProfile.getUSBs()){
    					short num = DeviceInfType.USB.getFinalValue(Integer.valueOf(usbPort), portGroupProfile.getFirstHiveApModel());
                   		PortMonitorProfile portMonitorProfile = new PortMonitorProfile(num, portGroupProfile.getFirstHiveApModel());
                   		setPortMonitorProfileContent(portGroupProfile,portMonitorProfile,num);
                   		portMonitor.add(portMonitorProfile);
    				}
    				
    			}
        	}
        }
    	
    	Collections.sort(portMonitor, new Comparator<PortMonitorProfile>() {
			public int compare(PortMonitorProfile o1, PortMonitorProfile o2) {
				return (int) (o1.getDestinationPort() - o2.getDestinationPort());
			}
		});
    	
    	return portMonitor;
    }
    
    private void updateMirrorSettings(){
    	 List<PortMonitorProfile> monitorProfiles = getMonitorPorts();
         if (monitorProfiles != null && !monitorProfiles.isEmpty()) {
             for (int i = 0; i < monitorProfiles.size(); i++) {
            	 monitorProfiles.get(i).setEnableMonitorSession(true);
            	 monitorProfiles.get(i).setIngressVlan("");
            	 monitorProfiles.get(i).setEnableVlans(false);
            	 monitorProfiles.get(i).setEnablePorts(enableSourcePort);
            	 if(enableSourcePort){
            		 if (ingressPorts != null) {
                         monitorProfiles.get(i).setIngressPort(ingressPorts[i]);
                     }
                     if (egressPorts != null) {
                         monitorProfiles.get(i).setEgressPort(egressPorts[i]);
                     }
                     if (bothPorts != null) {
                         monitorProfiles.get(i).setBothPort(bothPorts[i]);
                     }
            	 }else{
            		 monitorProfiles.get(i).setIngressPort("");
            		 monitorProfiles.get(i).setEgressPort("");
            		 monitorProfiles.get(i).setBothPort("");
            	 }
                 
                 if(destinationInterface == monitorProfiles.get(i).getDestinationPort()
                		 && enableSourceVlan){
                	 monitorProfiles.get(i).setEnableVlans(enableSourceVlan);
                     monitorProfiles.get(i).setIngressVlan(ingressVlan);
                 }
             }

             getDataSource().setMonitorProfiles(monitorProfiles);
         }
    }

    public int getLimitType() {
        return limitType;
    }

    public void setLimitType(int limitType) {
        this.limitType = limitType;
    }
    
    public boolean validateAggWithMirrorSourcePortChannel(PortGroupProfile editBo) throws Exception{
    	List<Short> tmpMirrorObj = new ArrayList<Short>();
    	List<Short> tmpBasicObj = new ArrayList<Short>();
    	List<Integer> sourceArray = new ArrayList<Integer>();
    	List<PortBasicProfile> basicProfileList = editBo.getBasicProfiles();
    	List<PortMonitorProfile> mirrorList = editBo.getMonitorProfiles();
    	if(mirrorList.isEmpty()){
    		return false;
    	}
    	for(PortMonitorProfile mirror : mirrorList){
    		if(mirror.getIngressPort() != null && !mirror.getIngressPort().isEmpty()){
    			sourceArray.addAll(getMirrorSourcePortsToArray(mirror.getIngressPort()));
    		}

			if(mirror.getEgressPort() != null && !mirror.getEgressPort().isEmpty()){
				sourceArray.addAll(getMirrorSourcePortsToArray(mirror.getEgressPort()));
			}
    		if(mirror.getBothPort() != null && !mirror.getBothPort().isEmpty()){
    			sourceArray.addAll(getMirrorSourcePortsToArray(mirror.getBothPort()));
    		}
    	}
    	
    	for(int i = 0; i < sourceArray.size(); i ++){
    		if(DeviceInfType.getInstance(sourceArray.get(i).shortValue(), editBo.getFirstHiveApModel()).getDeviceInfType() == DeviceInfType.PortChannel){
    			tmpMirrorObj.add(sourceArray.get(i).shortValue());
    		}
    	}
    	
    	for(PortBasicProfile profile : basicProfileList){
    		if(profile.isEnabledlinkAggregation()){
    			tmpBasicObj.add(DeviceInfType.PortChannel.getFinalValue((profile.getPortChannel()), editBo.getFirstHiveApModel()));
    		}
    	}
    	
    	if(tmpMirrorObj.size() > 0){
    		for(int i = 0; i < tmpMirrorObj.size(); i++){
        		if(!tmpBasicObj.contains(tmpMirrorObj.get(i))){
        			int channelId = DeviceInfType.getInstance(tmpMirrorObj.get(i), editBo.getFirstHiveApModel()).getIndex();
        			jsonObject.put("errMsg", getText("error.port.mirroring.source.channel.remove",new String[]{Integer.toString(channelId)}));
        			return true;
        		}
        	}
    	}
    	return false;
    }
    
    public boolean validatePortAsMirrorSource(PortGroupProfile editBo) throws Exception{
    	List<Short> tmpBasicObj = new ArrayList<Short>();
    	List<Integer> sourceArray = new ArrayList<Integer>();
    	List<PortBasicProfile> basicProfileList = editBo.getBasicProfiles();
    	List<PortMonitorProfile> mirrorList = editBo.getMonitorProfiles();
    	if(mirrorList.isEmpty()){
    		return false;
    	}
    	
    	for(PortMonitorProfile mirror : mirrorList){
    		if(mirror.getIngressPort() != null && !mirror.getIngressPort().isEmpty()){
    			sourceArray.addAll(getMirrorSourcePortsToArray(mirror.getIngressPort()));
    		}

			if(mirror.getEgressPort() != null && !mirror.getEgressPort().isEmpty()){
				sourceArray.addAll(getMirrorSourcePortsToArray(mirror.getEgressPort()));
			}
    		if(mirror.getBothPort() != null && !mirror.getBothPort().isEmpty()){
    			sourceArray.addAll(getMirrorSourcePortsToArray(mirror.getBothPort()));
    		}
    	}
    	
    	for(PortBasicProfile profile : basicProfileList){
    		if(profile.getAccessProfile().getPortType() == PortAccessProfile.PORT_TYPE_MONITOR){
    			if (profile.getETHs() != null){
    				for (String ethport : profile.getETHs()){
    					short num = DeviceInfType.Gigabit.getFinalValue(Integer.valueOf(ethport), editBo.getFirstHiveApModel());
    					tmpBasicObj.add(num);
    				}
    			}
    			
    			if (profile.getSFPs() != null){
    				for (String sfpPort : profile.getSFPs()){
    					short num = DeviceInfType.SFP.getFinalValue(Integer.valueOf(sfpPort), editBo.getFirstHiveApModel());
    					tmpBasicObj.add(num);
    				}
    			}
    			
    			if (profile.getUSBs() != null) {
    				for (String usbPort : profile.getUSBs()){
    					short num = DeviceInfType.USB.getFinalValue(Integer.valueOf(usbPort), editBo.getFirstHiveApModel());
    					tmpBasicObj.add(num);
    				}
    				
    			}
    		}
    	}
    	
    	if(sourceArray.size() > 0){
    		for(int i = 0; i < sourceArray.size(); i++){
        		if(DeviceInfType.getInstance(sourceArray.get(i).shortValue(), editBo.getFirstHiveApModel()).getDeviceInfType() != DeviceInfType.PortChannel){
        			for (int j = 0; j < tmpBasicObj.size(); j++) {
        				if (tmpBasicObj.get(j) == sourceArray.get(i).shortValue()) {
        					jsonObject.put("errMsg", getText("error.port.mirroring.port.type.check",
        								new String[]{MgrUtil.getEnumString("enum.switch.interface." + sourceArray.get(i))}));
        			        return true;
        			    }
        			}
        		}
        	}
    	}
		return false;
    }
    
    public boolean validateMirrorSourceVlanEnabled(PortGroupProfile editBo) throws Exception{
    	List<String> portsArray = new ArrayList<String>(); 
    	for (String groupStr : jsonGroups) {
             JSONObject jsonObj = new JSONObject(groupStr);
             JSONObject jsonPorts = jsonObj.getJSONObject("ports");
             LOG.debug("jsonPorts is " + jsonPorts);
             JSONArray ethPort = jsonPorts.optJSONArray(ETH);
             JSONArray sfpPort = jsonPorts.optJSONArray(SFP);
             JSONArray usbPort = jsonPorts.optJSONArray(USB);
             
             if (null != ethPort) {
            	 for(int e = 0; e < ethPort.length(); e ++){
            		 portsArray.add(ethPort.get(e).toString());
            	 }
             }
             if (null != sfpPort) {
            	 for(int s = 0; s < sfpPort.length(); s ++){
            		 portsArray.add(sfpPort.get(s).toString());
            	 }
             }
             if (null != usbPort) {
            	 for(int u = 0; u < usbPort.length(); u ++){
            		 portsArray.add(usbPort.get(u).toString());
            	 }
             }
        }
    	for(PortMonitorProfile profile : editBo.getMonitorProfiles()){
    		if(profile.isEnableVlans()){
    			if(portsArray.size() == 1){
    				int portNumber = DeviceInfType.getInstance(profile.getDestinationPort(), editBo.getFirstHiveApModel()).getIndex();
    				if(portsArray.contains(String.valueOf(portNumber))){
    					continue;
    				}else{
    					jsonObject.put("errMsg", getText("error.port.mirroring.enabled.vlan.check",new String[]{profile.getPortName()}));
    	    			return true;
    				}
    			}else{
    				jsonObject.put("errMsg", getText("error.port.mirroring.enabled.vlan.check",new String[]{profile.getPortName()}));
        			return true;
    			}
    		}
    	}
    	return false;
    }
    
    private List<Integer> getMirrorSourcePortsToArray(String source){
    	String[] sourceArray = source.split(",");
    	List<Integer> tmpList = new ArrayList<Integer>();
    	for(int a = 0; a < sourceArray.length; a++){
			if(sourceArray[a].indexOf("-") > 0){
				String[] tempArray = sourceArray[a].split("-");
				int startNum = Integer.valueOf(tempArray[0]);
				int endNum = Integer.valueOf(tempArray[1]);
				tmpList.add(startNum);
				while(startNum < endNum - 1){
					startNum = startNum + 1;
					tmpList.add(startNum);
				}
				tmpList.add(endNum);
			}else{
				tmpList.add(Integer.valueOf(sourceArray[a]));
			}
		}
    	
    	return tmpList;
    }
    
	public EnumItem[] getDestinationPortsList() {
    	List<EnumItem> list = new ArrayList<EnumItem>();
    	EnumItem[] ports = null;
    	List<PortMonitorProfile> monitorProfiles = getDataSource().getMonitorProfiles();
    	for(PortMonitorProfile mirrorProfile : monitorProfiles){
    		list.add(new EnumItem(mirrorProfile.getDestinationPort(),MgrUtil
					.getEnumString("enum.switch.interface." + mirrorProfile.getDestinationPort())));
    	}
    	
    	Collections.sort(list, new Comparator<EnumItem>() {
			public int compare(EnumItem o1, EnumItem o2) {
				return (int) (o1.getKey() - o2.getKey());
			}
		});
    	
    	if(list != null && list.size() > 0){
    		ports = new EnumItem[list.size()];
    		for(int i = 0; i < list.size(); i++){
        		ports[i] = list.get(i);
        	}
    	}
    	
    	return ports;
    }
	
	public boolean isContainMirror(PortGroupProfile portGroupProfile){
		for (PortBasicProfile portBasicProfile : portGroupProfile.getBasicProfiles()) {
        	if (portBasicProfile.getAccessProfile().getPortType() == PortAccessProfile.PORT_TYPE_MONITOR){
        		if(selectedAccessIds != null && !selectedAccessIds.isEmpty()){
        			accessProfileId = selectedAccessIds.get(0);
        		}
        		return true;
        	}
		}
		return false;
	}

    /*----port mirror----------*/
    private boolean enableSourceVlan = false;
    private String ingressVlan;
    private short destinationInterface;
    public Long accessProfileId;
    private boolean enableSourcePort = false;

	public Long getAccessProfileId() {
		return accessProfileId;
	}

	public void setAccessProfileId(Long accessProfileId) {
		this.accessProfileId = accessProfileId;
	}

	public short getDestinationInterface() {
		return destinationInterface;
	}

	public void setDestinationInterface(short destinationInterface) {
		this.destinationInterface = destinationInterface;
	}

	public boolean isEnableSourceVlan() {
		return enableSourceVlan;
	}

	public void setEnableSourceVlan(boolean enableSourceVlan) {
		this.enableSourceVlan = enableSourceVlan;
	}

	public String getIngressVlan() {
		return ingressVlan;
	}

	public void setIngressVlan(String ingressVlan) {
		this.ingressVlan = ingressVlan;
	}

	public boolean isHaveNonDefaultTmps() {
		return haveNonDefaultTmps;
	}

	public void setHaveNonDefaultTmps(boolean haveNonDefaultTmps) {
		this.haveNonDefaultTmps = haveNonDefaultTmps;
	}

	public String getSelectIDs() {
		return selectIDs;
	}

	public void setSelectIDs(String selectIDs) {
		this.selectIDs = selectIDs;
	}

	public long getCurrentPolicyID() {
		return currentPolicyID;
	}

	public void setCurrentPolicyID(long currentPolicyID) {
		this.currentPolicyID = currentPolicyID;
	}

    public long getPortTemplateIndex() {
        return portTemplateIndex;
    }

    public void setPortTemplateIndex(long portTemplateIndex) {
        this.portTemplateIndex = portTemplateIndex;
    }

    public boolean isEnableSourcePort() {
		return enableSourcePort;
	}

	public void setEnableSourcePort(boolean enableSourcePort) {
		this.enableSourcePort = enableSourcePort;
	}

}
