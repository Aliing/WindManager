package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.DomainObject;
import com.ah.bo.network.RoutingPolicy;
import com.ah.bo.network.RoutingPolicyRule;
import com.ah.bo.useraccess.MgmtServiceIPTrack;
import com.ah.bo.useraccess.UserProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class RoutingPolicyAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;
	
	private static final Tracer log = new Tracer(RoutingPolicyAction.class
			.getSimpleName());
	
	public static final int COLUMN_NAME = 1;

	public static final int COLUMN_DESCRIPTION = 2;

	@Override
	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
		case COLUMN_NAME:
			code = "config.routing.policy.name";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.routing.policy.description";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<>(2);
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
	
		return columns;
	}
	
	@Override
	public String execute() throws Exception {
		try {
			if("new".equals(operation)){
				log.info("execute", "operation:" + operation);
				if (!setTitleAndCheckAccess(getText("config.title.routingPolicy"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new RoutingPolicy());
				getDataSource().setPolicyRuleType(RoutingPolicy.POLICYRULE_CUSTOM);
				prepareDependObjects();
				return getReturnPathWithJsonMode(INPUT, "routingPolicyJson");
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)){
				if (checkNameExists("policyName", getDataSource().getPolicyName())) {
					prepareDependObjects();
					if (isJsonMode() && !isParentIframeOpenFlg()) {
						jsonObject = new JSONObject();
						jsonObject.put("t", false);
						if (!getActionErrors().isEmpty()) {
							Object[] errs = getActionErrors().toArray();
							jsonObject.put("m", errs[0].toString());
						}
						return "json";
					} else {
						prepareDependObjects();
						return getReturnPathWithJsonMode(INPUT, "routingPolicyJson");
					}
				}
				createPolicyRules();
				if ("create".equals(operation)) {
					id=createBo(dataSource);
					if (isJsonMode()){
						jsonObject = new JSONObject();
						jsonObject.put("t", true);
						jsonObject.put("n", true);
						jsonObject.put("pId", getParentDomID());
						jsonObject.put("nId", id);
						jsonObject.put("nName", getDataSource().getPolicyName());
						return "json";
					} else {
						return prepareBoList();
					}
				} else {
					id= createBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
			} else if("edit".equals(operation)) {
				setSessionDataSource(findBoById(boClass, id, this));
				addLstTitle(getText("config.title.routingPolicy.edit")+ " '" + getChangedName() + "'");
				preparePolicyRules();
				prepareDependObjects();
				String strForward = editBo(this);
				return getReturnPathWithJsonMode(strForward, "routingPolicyJson");
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)){
				updatePolicyRules();
				updateBo(dataSource);
				if ("update".equals(operation)) {
					if (isJsonMode()){
						jsonObject = new JSONObject();
						jsonObject.put("t", true);
						jsonObject.put("n", false);
						return "json";
					} else {
						return prepareBoList();
					}
				} else {
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				RoutingPolicy profile = (RoutingPolicy) findBoById(boClass, cloneId, this);
				profile.setId(null);
				profile.setPolicyName("");
				profile.setDescription(null);
				profile.setVersion(null);
				profile.setOwner(null);
				setSessionDataSource(profile);
				addLstTitle(getText("config.title.routingPolicy"));
				preparePolicyRules();
				prepareDependObjects();
				return getReturnPathWithJsonMode(INPUT, "routingPolicyJson");
			} else if (("cancel" + getLstForward()).equals(operation)) {
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForward();
				} else {
					baseOperation();
					return prepareBoList();
				}
			} else if ("editDomObj".equals(operation) || "newDomObj".equals(operation)
					|| "editDomObjForAll".equals(operation) || "newDomObjForAll".equals(operation)) {
				createPolicyRules();
				if ("editDomObj".equals(operation) || "newDomObj".equals(operation)) {
					this.getDataSource().setEditDomType(RoutingPolicy.EDIT_DOMAIN_OBJECT_CUSTOM);
				} else {
					this.getDataSource().setEditDomType(RoutingPolicy.EDIT_DOMAIN_OBJECT_ALL);
				}
				addLstForward("routingPolicyForward");
				addLstTabId(tabId);
				return operation;
			} else if ("continue".equals(operation)) {
				removeLstTitle();
				removeLstTabId();
				removeLstForward();
				preparePolicyRules();
				prepareDependObjects();
				setId(dataSource.getId());
				if (RoutingPolicy.EDIT_DOMAIN_OBJECT_ALL.equals(this.getDataSource().getEditDomType())) {
					this.domObjIdForAll = this.domObjId;
					this.domObjId = null;
				}
				return getReturnPathWithJsonMode(INPUT, "routingPolicyJson");
			} else {
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	protected String getReturnPathWithJsonMode(String normalPath,
			String jsonModePath) {
		if (isJsonMode()) {
			return jsonModePath;
		} else {
			return normalPath;
		}
	}
	
	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_ROUTING_POLICY);
		setDataSource(RoutingPolicy.class);
		keyColumnId = COLUMN_NAME;
		tableId = HmTableColumn.TABLE_CONFIGURATION_ROUTING_POLICY;
	}

	@Override
	public RoutingPolicy getDataSource() {
		return (RoutingPolicy) dataSource;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof RoutingPolicy) {
			RoutingPolicy rp = (RoutingPolicy)bo;
			if(null != rp.getDomainObjectForDesList()){
				rp.getDomainObjectForDesList().getId();
			}
			if (null != rp.getIpTrackForCheck()) {
				rp.getIpTrackForCheck().getId();
			}
			if(null != rp.getRoutingPolicyRuleList()){
				rp.getRoutingPolicyRuleList().size();
				for(RoutingPolicyRule routingPolicyRule:rp.getRoutingPolicyRuleList()){
					if(routingPolicyRule.getSourceUserProfile()!=null){
						routingPolicyRule.getSourceUserProfile().getId();
					}
					if(routingPolicyRule.getIpTrackReachablePri() != null){
						routingPolicyRule.getIpTrackReachablePri().getId();
					}
					if(routingPolicyRule.getIpTrackReachableSec() != null){
						routingPolicyRule.getIpTrackReachableSec().getId();
					}
				}
			}
		}
		return null;
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		RoutingPolicy source = QueryUtil.findBoById(RoutingPolicy.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<RoutingPolicy> list = QueryUtil.executeQuery(RoutingPolicy.class,
				null, new FilterParams("id", destinationIds), domainId, this);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<>(list.size());
		for (RoutingPolicy profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			RoutingPolicy rp = source.clone();
			if (null == rp) {
				continue;
			}
			setCloneFields(source, rp);
			rp.setId(profile.getId());
			rp.setPolicyName(profile.getPolicyName());
			rp.setVersion(profile.getVersion());
			rp.setOwner(profile.getOwner());
			hmBos.add(rp);
		}
		return hmBos;
	}
	
	private void setCloneFields(RoutingPolicy source, RoutingPolicy destination) {
		List<RoutingPolicyRule> rpRules = new ArrayList<>();

		for (RoutingPolicyRule rpRule : source.getRoutingPolicyRuleList()) {
			rpRules.add(rpRule);
		}
		destination.setRoutingPolicyRuleList(rpRules);
	}
	
	public int getPolicyNameLength() {
		return getAttributeLength("policyName");
	}
	
	public int getDescriptionLength() {
		return getAttributeLength("description");
	}
	
	public EnumItem[] getSplitTunnel() {
		return new EnumItem[] { new EnumItem(RoutingPolicy.POLICYRULE_SPLIT,
				getText("config.routing.policy.rules.type.split")) };
	}
	
	public EnumItem[] getTunnelAll() {
		return new EnumItem[] { new EnumItem(RoutingPolicy.POLICYRULE_ALL,
				getText("config.routing.policy.rules.type.all")) };
	}
	
	public EnumItem[] getCustom() {
		return new EnumItem[] { new EnumItem(RoutingPolicy.POLICYRULE_CUSTOM,
				getText("config.routing.policy.rules.type.custom")) };
	}
	
	public List<CheckItem> getTrackIpList(){
		return  getBoCheckItems("trackName", MgmtServiceIPTrack.class, null, CHECK_ITEM_BEGIN_BLANK,
				CHECK_ITEM_END_NO);
	}
	
	public EnumItem[] getEnumInterfaceList() {
		return RoutingPolicyRule.ENUM_ROUTING_POLICY_RULE_INTERFACE;
	}
	
	public EnumItem[] getEnumInterfaceListUSB() {
		return RoutingPolicyRule.ENUM_ROUTING_POLICY_RULE_INTERFACE_USB;
	}
	
	public EnumItem[] getEnumInterfaceListPri() {
		return RoutingPolicyRule.ENUM_ROUTING_POLICY_RULE_INTERFACE_PRI;
	}
	
	public EnumItem[] getEnumForwardActionList() {
		return RoutingPolicyRule.ENUM_ROUTING_POLICY_RULE_ACTION;
	}
	
	public List<CheckItem> getSourceUserProfileList(){
		return  getBoCheckItems("userProfileName", UserProfile.class, 
				null,
				CHECK_ITEM_BEGIN_NO, CHECK_ITEM_END_NO);
	}
	
	public EnumItem[] getEnumAny() {
		return RoutingPolicyRule.ENUM_ROUTING_POLICY_RULE_UP_ANY;
	}
	
	public EnumItem[] getEnumNone() {
		return RoutingPolicyRule.ENUM_ROUTING_POLICY_RULE_TRACKIP_NONE;
	}
	
	private String[] sourceUserProfilePri;
	
	private String[] sourceUserProfileSec;
	
	private String[] ipTrackForCheckPri;
	
	private String[] ipTrackForCheckSec;
	
	private String[] interfaceTypePri;
	
	private String[] interfaceTypeSec;
	
	private String[] forwardActionTypePri;
	
	private String[] forwardActionTypeSec;
	
	private Long trackIpId = 0L;
	
	private Long domObjId;
	
	private Long domObjIdForAll;
	
	private List<CheckItem> availableDomainObjects;
	
	private List<CheckItem> ipTrackForCheckList = new ArrayList<>();
	
	private List<RoutingPolicyRule> customRuleList = new ArrayList<>();

	public String[] getSourceUserProfilePri() {
		return sourceUserProfilePri;
	}

	public void setSourceUserProfilePri(String[] sourceUserProfilePri) {
		this.sourceUserProfilePri = sourceUserProfilePri;
	}

	public String[] getIpTrackForCheckPri() {
		return ipTrackForCheckPri;
	}

	public void setIpTrackForCheckPri(String[] ipTrackForCheckPri) {
		this.ipTrackForCheckPri = ipTrackForCheckPri;
	}

	public String[] getIpTrackForCheckSec() {
		return ipTrackForCheckSec;
	}

	public void setIpTrackForCheckSec(String[] ipTrackForCheckSec) {
		this.ipTrackForCheckSec = ipTrackForCheckSec;
	}

	public String[] getInterfaceTypePri() {
		return interfaceTypePri;
	}

	public void setInterfaceTypePri(String[] interfaceTypePri) {
		this.interfaceTypePri = interfaceTypePri;
	}

	public String[] getInterfaceTypeSec() {
		return interfaceTypeSec;
	}

	public void setInterfaceTypeSec(String[] interfaceTypeSec) {
		this.interfaceTypeSec = interfaceTypeSec;
	}

	public String[] getForwardActionTypePri() {
		return forwardActionTypePri;
	}

	public void setForwardActionTypePri(String[] forwardActionTypePri) {
		this.forwardActionTypePri = forwardActionTypePri;
	}

	public String[] getForwardActionTypeSec() {
		return forwardActionTypeSec;
	}

	public void setForwardActionTypeSec(String[] forwardActionTypeSec) {
		this.forwardActionTypeSec = forwardActionTypeSec;
	}
	
	private void createPolicyRules(){
		MgmtServiceIPTrack mgmtServiceIPTrack = QueryUtil.findBoById(MgmtServiceIPTrack.class, trackIpId, this);
		getDataSource().setIpTrackForCheck(mgmtServiceIPTrack);
		
		if(RoutingPolicy.POLICYRULE_CUSTOM == getDataSource().getPolicyRuleType()){
			getDataSource().setEnableDomainObjectForDesList(false);
			DomainObject doObject = QueryUtil.findBoById(DomainObject.class, domObjId, this);
			getDataSource().setDomainObjectForDesList(doObject);
			//rule type is custom
			getDataSource().setRoutingPolicyRuleList(createRuleTypeCustom());
		}else if(RoutingPolicy.POLICYRULE_SPLIT == getDataSource().getPolicyRuleType()){
			//split
			getDataSource().setEnableDomainObjectForDesList(false);
			getDataSource().setDomainObjectForDesList(null);
			getDataSource().setRoutingPolicyRuleList(createRuleTypeSplit());
		}else{
			DomainObject doObject = QueryUtil.findBoById(DomainObject.class, domObjIdForAll, this);
			getDataSource().setDomainObjectForDesList(doObject);
			//all
			getDataSource().setRoutingPolicyRuleList(createRuleTypeAll());
		}
	}
	
	private List<RoutingPolicyRule> createRuleTypeCustom(){
		List<RoutingPolicyRule> list = new ArrayList<>();
		MgmtServiceIPTrack mgmtServiceIPTrack;
		if (this.getDataSource() != null) {
			mgmtServiceIPTrack = this.getDataSource().getIpTrackForCheck();
		} else {
			mgmtServiceIPTrack = QueryUtil.findBoById(MgmtServiceIPTrack.class, trackIpId, this);
		}
		
		if(null != sourceUserProfilePri){
			Map<Long, RoutingPolicyRule> rpRulesMap = new HashMap<>();
	    	for(int i=0;i<sourceUserProfilePri.length;i++){
				RoutingPolicyRule policyRule = new RoutingPolicyRule();
				rpRulesMap.put(Long.valueOf(sourceUserProfilePri[i]), policyRule);
				UserProfile userProfileItem = QueryUtil.findBoById(UserProfile.class, Long.valueOf(sourceUserProfilePri[i]), this);
				policyRule.setSourceUserProfile(userProfileItem);
				
				try {
					if(RoutingPolicyRule.ROUTING_POLICY_RULE_ANYGUEST == Short.valueOf(sourceUserProfilePri[i])){
						policyRule.setRuleType(RoutingPolicyRule.ROUTING_POLICY_RULE_ANYGUEST);
					}else if(RoutingPolicyRule.ROUTING_POLICY_RULE_ANY == Short.valueOf(sourceUserProfilePri[i])){
						policyRule.setRuleType(RoutingPolicyRule.ROUTING_POLICY_RULE_ANY);
					}else {
						policyRule.setRuleType(RoutingPolicyRule.ROUTING_POLICY_RULE_USERPROFILE);
					}
				} catch (NumberFormatException e) {
					policyRule.setRuleType(RoutingPolicyRule.ROUTING_POLICY_RULE_USERPROFILE);
				}
				
				if(!"0".equals(ipTrackForCheckPri[i])){
					policyRule.setIpTrackReachablePri(mgmtServiceIPTrack);
				} else {
					policyRule.setIpTrackReachablePri(null);
				}
				policyRule.setInterfaceTypePri(Short.valueOf(interfaceTypePri[i]));
				policyRule.setForwardActionTypePri(Short.valueOf(forwardActionTypePri[i]));
			}
	    	if (sourceUserProfileSec != null) {
		    	for (int i=0;i<sourceUserProfileSec.length;i++) {
		    		RoutingPolicyRule policyRule = rpRulesMap.get(Long.valueOf(sourceUserProfileSec[i]));
		    		if (policyRule != null) {
		    			if(!"0".equals(ipTrackForCheckSec[i])){
		    				policyRule.setIpTrackReachableSec(mgmtServiceIPTrack);
		    			} else {
		    				policyRule.setIpTrackReachableSec(null);
		    			}
			    		policyRule.setInterfaceTypeSec(Short.valueOf(interfaceTypeSec[i]));
			    		policyRule.setForwardActionTypeSec(Short.valueOf(forwardActionTypeSec[i]));
		    		}
		    	}
	    	}

			for (String userProfilePri : sourceUserProfilePri) {
				if (rpRulesMap.containsKey(Long.valueOf(userProfilePri))) {
					list.add(rpRulesMap.get(Long.valueOf(userProfilePri)));
				}
			}
		}

		return list;
	}
	
	private List<RoutingPolicyRule> createRuleTypeAll(){
		List<RoutingPolicyRule> list = new ArrayList<>();
		MgmtServiceIPTrack mgmtServiceIPTrack = QueryUtil.findBoById(MgmtServiceIPTrack.class, trackIpId, this);
		
		if(null != mgmtServiceIPTrack){
			//any
			RoutingPolicyRule rule1 = new RoutingPolicyRule();
			rule1.setSourceUserProfile(null);
			rule1.setRuleType(RoutingPolicyRule.ROUTING_POLICY_RULE_ANY);
			rule1.setIpTrackReachablePri(mgmtServiceIPTrack);
			rule1.setIpTrackReachableSec(mgmtServiceIPTrack);
			rule1.setInterfaceTypePri(allAnyInterfacePri);
			rule1.setInterfaceTypeSec(allAnyInterfaceSec);
			if(null != domObjIdForAll && -1 != domObjIdForAll){
				rule1.setForwardActionTypePri(RoutingPolicyRule.FORWARDACTION_EXCEPTION);
				rule1.setForwardActionTypeSec(RoutingPolicyRule.FORWARDACTION_EXCEPTION);
			}else{
				rule1.setForwardActionTypePri(RoutingPolicyRule.FORWARDACTION_ALL);
				rule1.setForwardActionTypeSec(RoutingPolicyRule.FORWARDACTION_ALL);
			}
			list.add(rule1);
			//add default rule for any guest,not show in GUI
			RoutingPolicyRule rule2 = new RoutingPolicyRule();
			rule2.setSourceUserProfile(null);
			rule2.setRuleType(RoutingPolicyRule.ROUTING_POLICY_RULE_ANYGUEST);
			rule2.setIpTrackReachablePri(mgmtServiceIPTrack);
			rule2.setIpTrackReachableSec(mgmtServiceIPTrack);
			rule2.setInterfaceTypePri(allAnyInterfacePri);
			rule2.setInterfaceTypeSec(allAnyInterfaceSec);
			rule2.setForwardActionTypePri(RoutingPolicyRule.FORWARDACTION_DROP);
			rule2.setForwardActionTypeSec(RoutingPolicyRule.FORWARDACTION_DROP);
			list.add(rule2);
		}else{
			//any
			RoutingPolicyRule rule1 = new RoutingPolicyRule();
			rule1.setSourceUserProfile(null);
			rule1.setRuleType(RoutingPolicyRule.ROUTING_POLICY_RULE_ANY);
			rule1.setIpTrackReachablePri(null);
			rule1.setInterfaceTypePri(allAnyInterfacePri);
			rule1.setInterfaceTypeSec(allAnyInterfaceSec);
			if(null != domObjIdForAll && -1 != domObjIdForAll){
				rule1.setForwardActionTypePri(RoutingPolicyRule.FORWARDACTION_EXCEPTION);
				rule1.setForwardActionTypeSec(RoutingPolicyRule.FORWARDACTION_EXCEPTION);
			}else{
				rule1.setForwardActionTypePri(RoutingPolicyRule.FORWARDACTION_ALL);
				rule1.setForwardActionTypeSec(RoutingPolicyRule.FORWARDACTION_ALL);
			}
			list.add(rule1);
			//add default rule for any guest,not show in GUI
			RoutingPolicyRule rule2 = new RoutingPolicyRule();
			rule2.setSourceUserProfile(null);
			rule2.setRuleType(RoutingPolicyRule.ROUTING_POLICY_RULE_ANYGUEST);
			rule2.setIpTrackReachablePri(null);
			rule2.setInterfaceTypePri(allAnyInterfacePri);
			rule2.setInterfaceTypeSec(allAnyInterfaceSec);
			rule2.setForwardActionTypePri(RoutingPolicyRule.FORWARDACTION_DROP);
			rule2.setForwardActionTypeSec(RoutingPolicyRule.FORWARDACTION_DROP);
			list.add(rule2);
		}
		return list;
	}
	
	private List<RoutingPolicyRule> createRuleTypeSplit(){
		List<RoutingPolicyRule> list = new ArrayList<>();
		MgmtServiceIPTrack mgmtServiceIPTrack = QueryUtil.findBoById(MgmtServiceIPTrack.class, trackIpId, this);
		
		if(null != mgmtServiceIPTrack){
			//any
			RoutingPolicyRule rule1 = new RoutingPolicyRule();
			rule1.setSourceUserProfile(null);
			rule1.setRuleType(RoutingPolicyRule.ROUTING_POLICY_RULE_ANY);
			rule1.setIpTrackReachablePri(mgmtServiceIPTrack);
			rule1.setIpTrackReachableSec(mgmtServiceIPTrack);
			rule1.setInterfaceTypePri(splitAnyInterfacePri);
			rule1.setInterfaceTypeSec(splitAnyInterfaceSec);
			rule1.setForwardActionTypePri(RoutingPolicyRule.FORWARDACTION_SPLIT);
			rule1.setForwardActionTypeSec(RoutingPolicyRule.FORWARDACTION_SPLIT);
			list.add(rule1);
			//any guest
			RoutingPolicyRule rule2 = new RoutingPolicyRule();
			rule2.setSourceUserProfile(null);
			rule2.setRuleType(RoutingPolicyRule.ROUTING_POLICY_RULE_ANYGUEST);
			rule2.setIpTrackReachablePri(mgmtServiceIPTrack);
			rule2.setIpTrackReachableSec(mgmtServiceIPTrack);
			rule2.setInterfaceTypePri(splitAnyGuestInterfacePri);
			rule2.setInterfaceTypeSec(splitAnyGuestInterfaceSec);
			rule2.setForwardActionTypePri(RoutingPolicyRule.FORWARDACTION_DROP);
			rule2.setForwardActionTypeSec(RoutingPolicyRule.FORWARDACTION_DROP);
			list.add(rule2);
		}else{
			//any
			RoutingPolicyRule rule1 = new RoutingPolicyRule();
			rule1.setSourceUserProfile(null);
			rule1.setRuleType(RoutingPolicyRule.ROUTING_POLICY_RULE_ANY);
			rule1.setIpTrackReachablePri(null);
			rule1.setInterfaceTypePri(splitAnyInterfacePri);
			rule1.setInterfaceTypeSec(splitAnyInterfaceSec);
			rule1.setForwardActionTypePri(RoutingPolicyRule.FORWARDACTION_SPLIT);
			rule1.setForwardActionTypeSec(RoutingPolicyRule.FORWARDACTION_SPLIT);
			list.add(rule1);
			//any guest
			RoutingPolicyRule rule2 = new RoutingPolicyRule();
			rule2.setSourceUserProfile(null);
			rule2.setRuleType(RoutingPolicyRule.ROUTING_POLICY_RULE_ANYGUEST);
			rule2.setIpTrackReachablePri(null);
			rule2.setIpTrackReachableSec(null);
			rule2.setInterfaceTypePri(splitAnyGuestInterfacePri);
			rule2.setInterfaceTypeSec(splitAnyGuestInterfaceSec);
			rule2.setForwardActionTypePri(RoutingPolicyRule.FORWARDACTION_DROP);
			rule2.setForwardActionTypeSec(RoutingPolicyRule.FORWARDACTION_DROP);
			list.add(rule2);
		}
		return list;
	}
	
	private void updatePolicyRules(){
		MgmtServiceIPTrack mgmtServiceIPTrack = QueryUtil.findBoById(MgmtServiceIPTrack.class, trackIpId, this);
		getDataSource().setIpTrackForCheck(mgmtServiceIPTrack);
		
		if(null != getDataSource().getRoutingPolicyRuleList()){
			getDataSource().getRoutingPolicyRuleList().clear();
			getDataSource().setRoutingPolicyRuleList(null);
			createPolicyRules();
			/*if(RoutingPolicy.POLICYRULE_SPLIT == getDataSource().getPolicyRuleType()){
				getDataSource().setRoutingPolicyRuleList(createRuleTypeSplit());
			}else if(RoutingPolicy.POLICYRULE_ALL == getDataSource().getPolicyRuleType()){
				DomainObject dobj = QueryUtil.findBoById(DomainObject.class, domObjIdForAll, this);
				getDataSource().setDomainObjectForDesList(dobj);
				getDataSource().setRoutingPolicyRuleList(createRuleTypeAll());
			}else {
				DomainObject dobj = QueryUtil.findBoById(DomainObject.class, domObjId, this);
				getDataSource().setDomainObjectForDesList(dobj);
				getDataSource().setRoutingPolicyRuleList(createRuleTypeCustom());
			}*/
		}
	}
	
	private void preparePolicyRules(){
		if(null != dataSource){
			if(null != getDataSource().getRoutingPolicyRuleList()){
				if(getDataSource().getPolicyRuleType() == RoutingPolicy.POLICYRULE_CUSTOM){
					customRuleList = getDataSource().getRoutingPolicyRuleList();
					Collections.sort(customRuleList, new Comparator<RoutingPolicyRule>(){
						@Override
						public int compare(RoutingPolicyRule rule1, RoutingPolicyRule rule2) {
							return rule1.getPosition() - rule2.getPosition();
						}
					});
				}
				if(RoutingPolicy.POLICYRULE_SPLIT == getDataSource().getPolicyRuleType()){
					if(getDataSource().getRoutingPolicyRuleList().size() > 1){
						if(getDataSource().getRoutingPolicyRuleList().get(0).getRuleType() == RoutingPolicyRule.ROUTING_POLICY_RULE_ANYGUEST && getDataSource().getRoutingPolicyRuleList().get(0).getSourceUserProfile() == null){
							splitAnyGuestInterfacePri = getDataSource().getRoutingPolicyRuleList().get(0).getInterfaceTypePri();
							splitAnyGuestInterfaceSec = getDataSource().getRoutingPolicyRuleList().get(0).getInterfaceTypeSec();
							splitAnyInterfacePri = getDataSource().getRoutingPolicyRuleList().get(1).getInterfaceTypePri();
							splitAnyInterfaceSec = getDataSource().getRoutingPolicyRuleList().get(1).getInterfaceTypeSec();
						}else{
							splitAnyInterfacePri = getDataSource().getRoutingPolicyRuleList().get(0).getInterfaceTypePri();
							splitAnyInterfaceSec = getDataSource().getRoutingPolicyRuleList().get(0).getInterfaceTypeSec();
							splitAnyGuestInterfacePri = getDataSource().getRoutingPolicyRuleList().get(1).getInterfaceTypePri();
							splitAnyGuestInterfaceSec = getDataSource().getRoutingPolicyRuleList().get(1).getInterfaceTypeSec();
						}
					}
				}
				if(RoutingPolicy.POLICYRULE_ALL == getDataSource().getPolicyRuleType()){
					if(getDataSource().getRoutingPolicyRuleList().size() > 1){
						if(getDataSource().getRoutingPolicyRuleList().get(0).getRuleType() == RoutingPolicyRule.ROUTING_POLICY_RULE_ANY && getDataSource().getRoutingPolicyRuleList().get(0).getSourceUserProfile() == null){
							allAnyInterfacePri = getDataSource().getRoutingPolicyRuleList().get(0).getInterfaceTypePri();
							allAnyInterfaceSec = getDataSource().getRoutingPolicyRuleList().get(0).getInterfaceTypeSec();
						}else{
							allAnyInterfacePri = getDataSource().getRoutingPolicyRuleList().get(1).getInterfaceTypePri();
							allAnyInterfaceSec = getDataSource().getRoutingPolicyRuleList().get(1).getInterfaceTypeSec();
						}
					}
				}
			}
			ipTrackForCheckList.add(new CheckItem(0L, "None"));
			if(getDataSource().isEnableIpTrackForCheck() 
					&& null != getDataSource().getIpTrackForCheck()){
				CheckItem item2 = new CheckItem(getDataSource().getIpTrackForCheck().getId(), getDataSource().getIpTrackForCheck().getTrackName());
				ipTrackForCheckList.add(item2);
				this.trackIpId = getDataSource().getIpTrackForCheck().getId();
			}
			if(null != getDataSource().getDomainObjectForDesList()){
				if(getDataSource().getPolicyRuleType() == RoutingPolicy.POLICYRULE_CUSTOM){
					this.domObjId = getDataSource().getDomainObjectForDesList().getId();
				}else if(getDataSource().getPolicyRuleType() == RoutingPolicy.POLICYRULE_ALL){
					this.domObjIdForAll = getDataSource().getDomainObjectForDesList().getId();
				}
				
			}
		}
	}

	public String[] getSourceUserProfileSec() {
		return sourceUserProfileSec;
	}

	public void setSourceUserProfileSec(String[] sourceUserProfileSec) {
		this.sourceUserProfileSec = sourceUserProfileSec;
	}

	public List<CheckItem> getIpTrackForCheckList() {
		return ipTrackForCheckList;
	}

	public void setIpTrackForCheckList(List<CheckItem> ipTrackForCheckList) {
		this.ipTrackForCheckList = ipTrackForCheckList;
	}

	public Long getTrackIpId() {
		if(-1 == trackIpId){
			trackIpId = 0L;
		}
		return trackIpId;
	}

	public void setTrackIpId(Long trackIpId) {
		this.trackIpId = trackIpId;
	}

	public Long getDomObjId() {
		return domObjId;
	}

	public void setDomObjId(Long domObjId) {
		this.domObjId = domObjId;
	}
	
	private void prepareAvailableDomainObjects(){
		availableDomainObjects = getBoCheckItems("objName", DomainObject.class,new FilterParams("objType",DomainObject.VPN_TUNNEL),
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}
	
	private void prepareDependObjects(){
		prepareAvailableDomainObjects();
	}

	public List<CheckItem> getAvailableDomainObjects() {
		return availableDomainObjects;
	}

	public void setAvailableDomainObjects(List<CheckItem> availableDomainObjects) {
		this.availableDomainObjects = availableDomainObjects;
	}
	
	public String getDestinationListStyle(){
		if(null != getDataSource() && getDataSource().getPolicyRuleType() == RoutingPolicy.POLICYRULE_CUSTOM
				&& null != getDataSource().getRoutingPolicyRuleList() && getDataSource().getRoutingPolicyRuleList().size() > 1){
			for(RoutingPolicyRule routingPolicyRule: getDataSource().getRoutingPolicyRuleList()){
				if(routingPolicyRule.getForwardActionTypePri() == RoutingPolicyRule.FORWARDACTION_EXCEPTION 
						|| routingPolicyRule.getForwardActionTypeSec() == RoutingPolicyRule.FORWARDACTION_EXCEPTION){
					return "";
				}
			}
		}
		return "none";
	}
	
	public String getDestinationListStyleForAll(){
		if(null != getDataSource() && getDataSource().getPolicyRuleType() == RoutingPolicy.POLICYRULE_ALL){
			return "";
		}
		return "none";
	}
	
	public String getChangedName() {
		return getDataSource().getLabel().replace("\\", "\\\\").replace("'",
				"\\'");
	}
	
	public String getCustomTypeTrStyle(){
		return (getDataSource()!=null && getDataSource().getPolicyRuleType() == RoutingPolicy.POLICYRULE_CUSTOM)? "":"none";
	}
	
	public String getAllTypeTrStyle(){
		return (getDataSource()!=null && getDataSource().getPolicyRuleType() == RoutingPolicy.POLICYRULE_ALL)? "":"none";
	}
	
	public String getSplitTypeTrStyle(){
		return (getDataSource()!=null && getDataSource().getPolicyRuleType() == RoutingPolicy.POLICYRULE_SPLIT)? "":"none";
	}
	
	public String getEnableIpTrackStyle(){
		return getDataSource()!=null && getDataSource().isEnableIpTrackForCheck()? "":"none";
	}
	
	public String getEnableDomainObjectForDesListStyle(){
		return getDataSource()!=null && getDataSource().isEnableDomainObjectForDesList()? "":"none";
	}

	public List<CheckItem> getUserProfileGuestList() {
		// TODO for remove network object in user profile
		return null;
//		return  getBoCheckItems("userProfileName", UserProfile.class,new FilterParams("networkObj.networkType",VpnNetwork.VPN_NETWORK_TYPE_GUEST),
//				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}

	public List<RoutingPolicyRule> getCustomRuleList() {
		return customRuleList;
	}

	public void setCustomRuleList(List<RoutingPolicyRule> customRuleList) {
		this.customRuleList = customRuleList;
	}
	
	private short splitAnyInterfacePri = RoutingPolicyRule.ROUTING_POLICY_RULE_ETH0;
	
	private short splitAnyInterfaceSec = RoutingPolicyRule.ROUTING_POLICY_RULE_USB;
	
	private short splitAnyGuestInterfacePri = RoutingPolicyRule.ROUTING_POLICY_RULE_ETH0;
	
	private short splitAnyGuestInterfaceSec = RoutingPolicyRule.ROUTING_POLICY_RULE_USB;

	public short getSplitAnyInterfacePri() {
		return splitAnyInterfacePri;
	}

	public void setSplitAnyInterfacePri(short splitAnyInterfacePri) {
		this.splitAnyInterfacePri = splitAnyInterfacePri;
	}

	public short getSplitAnyInterfaceSec() {
		return splitAnyInterfaceSec;
	}

	public void setSplitAnyInterfaceSec(short splitAnyInterfaceSec) {
		this.splitAnyInterfaceSec = splitAnyInterfaceSec;
	}
	
	public String getRuleSecStyle(){
		return getDataSource()!=null && getDataSource().getIpTrackForCheck()!=null ? "":"none";
	}
	
	public List<CheckItem> getTrackIpListNone(){
		List<CheckItem> returnList=new ArrayList<>();
		CheckItem item = new CheckItem(0L, "None");
		returnList.addAll(getBoCheckItems("trackName", MgmtServiceIPTrack.class, null, CHECK_ITEM_BEGIN_BLANK,
				CHECK_ITEM_END_NO));
		returnList.add(item);
		return returnList;
	}
	
	private short allAnyInterfacePri = RoutingPolicyRule.ROUTING_POLICY_RULE_ETH0;
	
	private short allAnyInterfaceSec = RoutingPolicyRule.ROUTING_POLICY_RULE_USB;
	
	private short allAnyForwardAction = RoutingPolicyRule.FORWARDACTION_ALL;

	public short getAllAnyInterfacePri() {
		return allAnyInterfacePri;
	}

	public void setAllAnyInterfacePri(short allAnyInterfacePri) {
		this.allAnyInterfacePri = allAnyInterfacePri;
	}

	public short getAllAnyInterfaceSec() {
		return allAnyInterfaceSec;
	}

	public void setAllAnyInterfaceSec(short allAnyInterfaceSec) {
		this.allAnyInterfaceSec = allAnyInterfaceSec;
	}
	
	public String getRouteGuestTrStyle(){
		return getDataSource() != null && getDataSource().getPolicyRuleType() != RoutingPolicy.POLICYRULE_ALL ? "":"none";
	}

	public Long getDomObjIdForAll() {
		return domObjIdForAll;
	}

	public void setDomObjIdForAll(Long domObjIdForAll) {
		this.domObjIdForAll = domObjIdForAll;
	}
	
	public String getSplitAnyForwardActionPriStyle() {
		return splitAnyInterfacePri == 0 ? "none":"";
	}

	public String getSplitAnyForwardActionSecStyle() {
		return splitAnyInterfaceSec == 0 ? "none":"";
	}
	
	public String getSplitAnyGuestForwardActionPriStyle() {
		return splitAnyGuestInterfacePri == 0 ? "none":"";
	}
	
	public String getSplitAnyGuestForwardActionSecStyle() {
		return splitAnyGuestInterfaceSec == 0 ? "none":"";
	}

	public String getAllAnyForwardActionPriStyle() {
		return allAnyInterfacePri == 0 ? "none":"";
	}

	public String getAllAnyForwardActionSecStyle() {
		return allAnyInterfaceSec == 0 ? "none":"";
	}

	public short getAllAnyForwardAction() {
		if(getDataSource()!=null && getDataSource().getPolicyRuleType() == RoutingPolicy.POLICYRULE_ALL && getDataSource().getDomainObjectForDesList() != null){
			return RoutingPolicyRule.FORWARDACTION_EXCEPTION;
		}else{
			return RoutingPolicyRule.FORWARDACTION_ALL;
		}
	}

	public void setAllAnyForwardAction(short allAnyForwardAction) {
		this.allAnyForwardAction = allAnyForwardAction;
	}
	
	public EnumItem[] getEnumAnyGuest() {
		return RoutingPolicyRule.ENUM_ROUTING_POLICY_RULE_UP_ANYGUEST;
	}

	public short getSplitAnyGuestInterfacePri() {
		return splitAnyGuestInterfacePri;
	}

	public void setSplitAnyGuestInterfacePri(short splitAnyGuestInterfacePri) {
		this.splitAnyGuestInterfacePri = splitAnyGuestInterfacePri;
	}

	public short getSplitAnyGuestInterfaceSec() {
		return splitAnyGuestInterfaceSec;
	}

	public void setSplitAnyGuestInterfaceSec(short splitAnyGuestInterfaceSec) {
		this.splitAnyGuestInterfaceSec = splitAnyGuestInterfaceSec;
	}

}