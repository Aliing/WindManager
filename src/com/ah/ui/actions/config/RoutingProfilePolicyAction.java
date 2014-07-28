package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.DomainObject;
import com.ah.bo.network.RoutingProfilePolicy;
import com.ah.bo.network.RoutingProfilePolicyRule;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class RoutingProfilePolicyAction extends BaseAction implements QueryBo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Tracer log = new Tracer(
			RoutingProfilePolicyAction.class.getSimpleName());

	public static final int COLUMN_NAME = 1;

	public static final int COLUMN_DESCRIPTION = 2;
	public static final int DESTINATION_ANY = 0;
	public static final int SPLIT_THIRD_RULE = 2;
	
	private List<RoutingProfilePolicyRule> ruleList = new ArrayList<RoutingProfilePolicyRule>();
	private String[] sourcetypearr;
	private String[] sourcevaluearr;
	private String[] destinationtypearr;
	private String[] destinationvaluearr;
	private String[] out1arr;
	private String[] out2arr;
	private String[] out3arr;
	private String[] out4arr;
	private List<CheckItem> userprofileList = new ArrayList<CheckItem>();
	private String splitTunnelPrimarykey;
	private String splitTunnelBackupkey;

	public String getSplitTunnelPrimarykey() {
		return splitTunnelPrimarykey;
	}

	public void setSplitTunnelPrimarykey(String splitTunnelPrimarykey) {
		this.splitTunnelPrimarykey = splitTunnelPrimarykey;
	}

	public String getSplitTunnelBackupkey() {
		return splitTunnelBackupkey;
	}

	public void setSplitTunnelBackupkey(String splitTunnelBackupkey) {
		this.splitTunnelBackupkey = splitTunnelBackupkey;
	}

	public EnumItem[] getSplitTunnel() {
		return new EnumItem[] { new EnumItem(
				RoutingProfilePolicy.POLICYRULE_SPLIT,
				getText("config.routing.policy.rules.type.split")) };
	}

	public EnumItem[] getTunnelAll() {
		return new EnumItem[] { new EnumItem(
				RoutingProfilePolicy.POLICYRULE_ALL,
				getText("config.routing.policy.rules.type.all")) };
	}

	public EnumItem[] getCustom() {
		return new EnumItem[] { new EnumItem(
				RoutingProfilePolicy.POLICYRULE_CUSTOM,
				getText("config.routing.policy.rules.type.custom")) };
	}

	public List<CheckItem> getUserprofileList() {
		return userprofileList;
	}

	public void setUserprofileList(List<CheckItem> userprofileList) {
		this.userprofileList = userprofileList;
	}

	public String[] getSourcevaluearr() {
		return sourcevaluearr;
	}

	public void setSourcevaluearr(String[] sourcevaluearr) {
		this.sourcevaluearr = sourcevaluearr;
	}

	public String[] getDestinationtypearr() {
		return destinationtypearr;
	}

	public void setDestinationtypearr(String[] destinationtypearr) {
		this.destinationtypearr = destinationtypearr;
	}

	public String[] getDestinationvaluearr() {
		return destinationvaluearr;
	}

	public void setDestinationvaluearr(String[] destinationvaluearr) {
		this.destinationvaluearr = destinationvaluearr;
	}

	public String[] getOut1arr() {
		return out1arr;
	}

	public void setOut1arr(String[] out1arr) {
//		changeOutAttribute(out1arr);
		this.out1arr = out1arr;
	}

	public String[] getOut2arr() {
		return out2arr;
	}

	public void setOut2arr(String[] out2arr) {
//		changeOutAttribute(out2arr);
		this.out2arr = out2arr;
	}

	public String[] getOut3arr() {
		return out3arr;
	}

	public void setOut3arr(String[] out3arr) {
		this.out3arr = out3arr;
	}

	public String[] getOut4arr() {
		return out4arr;
	}

	public void setOut4arr(String[] out4arr) {
		this.out4arr = out4arr;
	}

	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
		case COLUMN_NAME:
			code = "config.routing.pbr.name";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.routing.policy.description";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}

	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));

		return columns;
	}

	public List<String> getPbrSelects() {
		List<String> list = new ArrayList<>();
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.pbr.role.wan"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.pbr.role.backup1"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.pbr.role.backup2"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.pbr.role.vpn"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.pbr.role.drop"));
		/*list.add("-------------------------");
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.usb"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.wifi0"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth0"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth1"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth2"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth3"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth4"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth5"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth6"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth7"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth8"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth9"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth10"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth11"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth12"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth13"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth14"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth15"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth16"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth17"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth18"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth19"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth20"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth21"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth22"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth23"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth24"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth25"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth26"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth27"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth28"));*/
		return list;
	}

	public List<String> getPbrForInterfaceSelectsed() {
		List<String> list = new ArrayList<>();
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth1"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth2"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth3"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth4"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth5"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth6"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth7"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth8"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth9"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth10"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth11"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth12"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth13"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth14"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth15"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth16"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth17"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth18"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth19"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth20"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth21"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth22"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth23"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth24"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth25"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth26"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth27"));
		list.add(MgrUtil
				.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth28"));
		return list;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof RoutingProfilePolicy) {
			RoutingProfilePolicy rp = (RoutingProfilePolicy) bo;

			if (null != rp.getRoutingProfilePolicyRuleList()) {
				rp.getRoutingProfilePolicyRuleList().size();

			}
		}
		return null;
	}

	@Override
	public String execute() throws Exception {
		prepareUserProfile();
		try {
			if ("new".equals(operation)) {
				log.info("execute", "operation:" + operation);
				if (!setTitleAndCheckAccess(getText("config.title.pbr.routingProfile"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new RoutingProfilePolicy());
				return getReturnPathWithJsonMode(INPUT,
						"routingProfilePolicyJson");
			} else if ("create".equals(operation)
					|| ("create" + getLstForward()).equals(operation)) {
				if (checkNameExists("profileName", getDataSource()
						.getProfileName())) {
					if (isJsonMode() && !isParentIframeOpenFlg()) {
						jsonObject = new JSONObject();
						jsonObject.put("t", false);
						if (getActionErrors().size() > 0) {
							Object[] errs = getActionErrors().toArray();
							jsonObject.put("m", errs[0].toString());
						}
						return "json";
					} else {

						return getReturnPathWithJsonMode(INPUT,
								"routingProfilePolicyJson");
					}

				}
				createProfilePolicyRules();
				if ("create".equals(operation)) {
					id = createBo(dataSource);
					if (isJsonMode()) {
						jsonObject = new JSONObject();
						jsonObject.put("t", true);
						jsonObject.put("n", true);
						jsonObject.put("pId", getParentDomID());
						jsonObject.put("nId", id);
						jsonObject.put("nName", getDataSource()
								.getProfileName());
						return "json";
					} else {
						return prepareBoList();
					}

				} else {
					id = createBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("edit".equals(operation)) {
				setSessionDataSource(findBoById(boClass, id, this));
				if (getDataSource().getProfileType() == RoutingProfilePolicy.POLICYRULE_SPLIT) {
					List<RoutingProfilePolicyRule> list = getDataSource()
							.getRoutingProfilePolicyRuleList();
                    for (RoutingProfilePolicyRule aList : list) {
                        RoutingProfilePolicyRule rl = list
                                .get(SPLIT_THIRD_RULE);

                        splitTunnelPrimarykey = rl.getOut1();
                        splitTunnelBackupkey = rl.getOut2();
                    }
				}
				addLstTitle(getText("config.title.pbr.routingProfile.edit")
						+ "'" + getChangedName() + "'");
				preparePolicyRules();
				String strForward = editBo(this);
				return getReturnPathWithJsonMode(strForward,
						"routingProfilePolicyJson");
			} else if ("update".equals(operation)
					|| ("update" + getLstForward()).equals(operation)) {

				updatePolicyRules();
				updateBo(dataSource);
				if ("update".equals(operation)) {
					if (isJsonMode()) {
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
				RoutingProfilePolicy profile = (RoutingProfilePolicy) findBoById(
						boClass, cloneId, this);
				profile.setId(null);
				profile.setProfileName("");
				profile.setDescription(null);
				profile.setVersion(null);
				profile.setOwner(null);
				setSessionDataSource(profile);
				addLstTitle(getText("config.title.pbr.routingProfile"));
				preparePolicyRules();
				return getReturnPathWithJsonMode(INPUT,
						"routingProfilePolicyJson");
			} else if (("cancel" + getLstForward()).equals(operation)) {
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForward();
				} else {
					baseOperation();
					return prepareBoList();
				}
			} else if ("editDomObj".equals(operation)
					|| "newDomObj".equals(operation)
					|| "editDomObjForAll".equals(operation)
					|| "newDomObjForAll".equals(operation)) {

				return operation;
			} else if ("continue".equals(operation)) {

				return getReturnPathWithJsonMode(INPUT,
						"routingProfilePolicyJson");
			} else {
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	private List<CheckItem> availableDomainObjects;

	public List<CheckItem> getAvailableDomainObjects() {
		return availableDomainObjects;
	}

	public void setAvailableDomainObjects(List<CheckItem> availableDomainObjects) {
		this.availableDomainObjects = availableDomainObjects;
	}

	private void prepareAvailableDomainObjects() {
		availableDomainObjects = getBoCheckItems("objName", DomainObject.class,
				new FilterParams("objType", DomainObject.VPN_TUNNEL),
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}

	private void prepareDependObjects() {
		prepareAvailableDomainObjects();
	}

	@Override
	public RoutingProfilePolicy getDataSource() {
		return (RoutingProfilePolicy) dataSource;
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_ROUTING_PROFILE_POLICY);
		setDataSource(RoutingProfilePolicy.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_ROUTING_PROFILE_POLICY;
	}

	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		RoutingProfilePolicy source = QueryUtil.findBoById(
				RoutingProfilePolicy.class, paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<RoutingProfilePolicy> list = QueryUtil.executeQuery(
				RoutingProfilePolicy.class, null, new FilterParams("id",
						destinationIds), domainId, this);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<>(list.size());
		for (RoutingProfilePolicy profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			RoutingProfilePolicy rp = source.clone();
			if (null == rp) {
				continue;
			}
			setCloneFields(source, rp);
			rp.setId(profile.getId());
			rp.setProfileName(profile.getProfileName());
			rp.setVersion(profile.getVersion());
			rp.setOwner(profile.getOwner());
			hmBos.add(rp);
		}
		return hmBos;
	}

	private void setCloneFields(RoutingProfilePolicy source,
			RoutingProfilePolicy destination) {
		List<RoutingProfilePolicyRule> rpRules = new ArrayList<>();

		for (RoutingProfilePolicyRule rpRule : source
				.getRoutingProfilePolicyRuleList()) {
			rpRules.add(rpRule);
		}
		destination.setRoutingProfilePolicyRuleList(rpRules);
	}

	@SuppressWarnings("unused")
	private void updatePolicyRules() throws Exception {
		if (null != getDataSource().getRoutingProfilePolicyRuleList()) {
			getDataSource().getRoutingProfilePolicyRuleList().clear();
			getDataSource().setRoutingProfilePolicyRuleList(null);
			createProfilePolicyRules();
		}
	}

	public void createProfilePolicyRules() throws Exception {
		List<RoutingProfilePolicyRule> list = new ArrayList<RoutingProfilePolicyRule>();
		if (sourcetypearr != null) {
			for (int i = 0; i < sourcetypearr.length; i++) {
				RoutingProfilePolicyRule rppr = new RoutingProfilePolicyRule();
//				UUID sourcename = UUID.randomUUID();
//				UUID destinationname = UUID.randomUUID();
//				rppr.setSourcename(sourcename.toString().replaceAll("-", ""));
//				rppr.setDestinationname(destinationname.toString().replaceAll(
//						"-", ""));

/*				switch (sourcetypearr[i]) {
				case "IP Range":
				case RoutingProfilePolicyRule.MATCHMAP_SOURCE_IPRANGE + "":
					rppr.setSourcetype(RoutingProfilePolicyRule.MATCHMAP_SOURCE_IPRANGE);
					break;
				case "Network":
				case RoutingProfilePolicyRule.MATCHMAP_SOURCE_NETWORK + "":
					rppr.setSourcetype(RoutingProfilePolicyRule.MATCHMAP_SOURCE_NETWORK);
					break;
				case "Interface":
				case RoutingProfilePolicyRule.MATCHMAP_SOURCE_INTERFACE + "":
					rppr.setSourcetype(RoutingProfilePolicyRule.MATCHMAP_SOURCE_INTERFACE);
					break;
				case "User Profile":
				case RoutingProfilePolicyRule.MATCHMAP_SOURCE_USERPROFILE + "":
					rppr.setSourcetype(RoutingProfilePolicyRule.MATCHMAP_SOURCE_USERPROFILE);
					break;
				default:
					rppr.setSourcetype(RoutingProfilePolicyRule.MATCHMAP_SOURCE_ANY);
					break;
				}*/
				switch (sourcetypearr[i]) {
				case RoutingProfilePolicyRule.MATCHMAP_SOURCE_IPRANGE + "":
					rppr.setSourcetype(RoutingProfilePolicyRule.MATCHMAP_SOURCE_IPRANGE);
					break;
				case RoutingProfilePolicyRule.MATCHMAP_SOURCE_NETWORK + "":
					rppr.setSourcetype(RoutingProfilePolicyRule.MATCHMAP_SOURCE_NETWORK);
					break;
				case RoutingProfilePolicyRule.MATCHMAP_SOURCE_INTERFACE + "":
					rppr.setSourcetype(RoutingProfilePolicyRule.MATCHMAP_SOURCE_INTERFACE);
					break;
				case RoutingProfilePolicyRule.MATCHMAP_SOURCE_USERPROFILE + "":
					rppr.setSourcetype(RoutingProfilePolicyRule.MATCHMAP_SOURCE_USERPROFILE);
					break;
				default:
					rppr.setSourcetype(RoutingProfilePolicyRule.MATCHMAP_SOURCE_ANY);
					break;
				}
				
				rppr.setSourcevalue(sourcevaluearr[i]);
/*
				switch (destinationtypearr[i]) {
				case "IP Range":
				case RoutingProfilePolicyRule.MATCHMAP_DESTINTION_IPRANGE + "":
					rppr.setDestinationtype(RoutingProfilePolicyRule.MATCHMAP_DESTINTION_IPRANGE);
					break;
				case "Network":
				case RoutingProfilePolicyRule.MATCHMAP_DESTINTION_NETWORK + "":
					rppr.setDestinationtype(RoutingProfilePolicyRule.MATCHMAP_DESTINTION_NETWORK);
					break;
				case "Hostname":
				case RoutingProfilePolicyRule.MATCHMAP_DESTINTION_HOSTNAME + "":
					rppr.setDestinationtype(RoutingProfilePolicyRule.MATCHMAP_DESTINTION_HOSTNAME);
					break;
				case "Private":
				case RoutingProfilePolicyRule.MATCHMAP_DESTINTION_PRIVATE + "":
					rppr.setDestinationtype(RoutingProfilePolicyRule.MATCHMAP_DESTINTION_PRIVATE);
					break;
				case "Any":
				case RoutingProfilePolicyRule.MATCHMAP_DESTINTION_ANY + "":
					rppr.setDestinationtype(RoutingProfilePolicyRule.MATCHMAP_DESTINTION_ANY);
					break;
				default:
					rppr.setSourcetype(RoutingProfilePolicyRule.MATCHMAP_DESTINTION_ANY);
					break;
				}*/
				
				switch (destinationtypearr[i]) {
				case RoutingProfilePolicyRule.MATCHMAP_DESTINTION_IPRANGE + "":
					rppr.setDestinationtype(RoutingProfilePolicyRule.MATCHMAP_DESTINTION_IPRANGE);
					break;
				case RoutingProfilePolicyRule.MATCHMAP_DESTINTION_NETWORK + "":
					rppr.setDestinationtype(RoutingProfilePolicyRule.MATCHMAP_DESTINTION_NETWORK);
					break;
				case RoutingProfilePolicyRule.MATCHMAP_DESTINTION_HOSTNAME + "":
					rppr.setDestinationtype(RoutingProfilePolicyRule.MATCHMAP_DESTINTION_HOSTNAME);
					break;
				case RoutingProfilePolicyRule.MATCHMAP_DESTINTION_PRIVATE + "":
					rppr.setDestinationtype(RoutingProfilePolicyRule.MATCHMAP_DESTINTION_PRIVATE);
					break;
				case RoutingProfilePolicyRule.MATCHMAP_DESTINTION_ANY + "":
					rppr.setDestinationtype(RoutingProfilePolicyRule.MATCHMAP_DESTINTION_ANY);
					break;
				default:
					rppr.setSourcetype(RoutingProfilePolicyRule.MATCHMAP_DESTINTION_ANY);
					break;
				}
				
				rppr.setDestinationvalue(destinationvaluearr[i]);
					
				String tempOut1 = changeOut(out1arr[i]);
				rppr.setOut1(tempOut1);
				String tempOut2 = changeOut(out2arr[i]);
				rppr.setOut2(tempOut2);

				rppr.setPriority(i + 1);
				list.add(rppr);
			}
		}
		getDataSource().setRoutingProfilePolicyRuleList(list);
	}

	public String getChangedName() {
		return getDataSource().getLabel().replace("\\", "\\\\")
				.replace("'", "\\'");
	}

	public List<RoutingProfilePolicyRule> getRuleList() {
		return ruleList;
	}

	public void splitTunelAdapter() {
		if (getDataSource().getProfileType() == RoutingProfilePolicy.POLICYRULE_SPLIT) {
			List<RoutingProfilePolicyRule> list = this.getRuleList();
			for (RoutingProfilePolicyRule routingpolicy : list) {
				if (routingpolicy.getDestinationtype() == DESTINATION_ANY) {
					if (routingpolicy.getOut1() == null) {
						splitTunnelPrimarykey = "";
					} else {
						String out1FromDataBase = routingpolicy.getOut1();
                        splitTunnelPrimarykey = changeOutToString(out1FromDataBase);
					}
					if (routingpolicy.getOut2() == null) {
						splitTunnelBackupkey = "";
					} else {
						String out2FromDataBase = routingpolicy.getOut2();
                        splitTunnelBackupkey = changeOutToString(out2FromDataBase);
					}

				}
			}

		}
	}

	public void setRuleList(List<RoutingProfilePolicyRule> ruleList) {
		this.ruleList = ruleList;
	}

	public String[] getSourcetypearr() {
		return sourcetypearr;
	}

	public void setSourcetypearr(String[] sourcetypearr) {
		this.sourcetypearr = sourcetypearr;
	}

	public void prepareUserProfile() {
		String sql = "select id, userprofilename from user_profile where owner = " + getDomain().getId();
		List list = QueryUtil.executeNativeQuery(sql);

        for (Object aList : list) {
            Object[] es = (Object[]) aList;
            CheckItem ii = new CheckItem(Long.valueOf(es[0].toString()),
                    es[1].toString());
            userprofileList.add(ii);
        }
	}

	public void customAdapter() {
		if (getDataSource().getProfileType() == RoutingProfilePolicy.POLICYRULE_CUSTOM) {
			List<RoutingProfilePolicyRule> list = this.getRuleList();
			for (RoutingProfilePolicyRule routingPolicy : list) {
				routingPolicy
						.setOut1(changeOutToString(routingPolicy.getOut1()));
				routingPolicy
						.setOut2(changeOutToString(routingPolicy.getOut2()));
				if(routingPolicy.getSourcetype() == 3){
					routingPolicy.setSourcevalue(changeOutToString(routingPolicy
							.getSourcevalue()));
				}
			}
		}
	}

	public void preparePolicyRules() {
		if (null != dataSource) {
			if (null != getDataSource().getRoutingProfilePolicyRuleList()) {
				ruleList = getDataSource().getRoutingProfilePolicyRuleList();

			}
			splitTunelAdapter();
			customAdapter();
		}
	}

	public String changeOut(String out) {
		if (out.equals("-")) {
			out = RoutingProfilePolicyRule.DEVICE_TYPE_BLANK_VALUE;
		} else if (out.equals(MgrUtil.getUserMessage("hiveAp.autoProvisioning.device.if.port.wifi0"))) {
			out = String.valueOf(AhInterface.DEVICE_IF_TYPE_WIFI0);
		}else if (out.equals(MgrUtil.getUserMessage("hiveAp.autoProvisioning.device.if.port.wifi1"))) {
			out = String.valueOf(AhInterface.DEVICE_IF_TYPE_WIFI1);
		} else if (out.equals(MgrUtil.getUserMessage("hiveAp.autoProvisioning.device.if.port.usb"))) {
			out = String.valueOf(AhInterface.DEVICE_IF_TYPE_USB);
		} else if (out.equals(MgrUtil.getUserMessage("hiveAp.autoProvisioning.device.if.pbr.role.drop"))) {
			out = RoutingProfilePolicyRule.DEVICE_TYPE_DROP_VALUE;
		} else if (out.equals(MgrUtil.getUserMessage("hiveAp.autoProvisioning.device.if.pbr.role.vpn"))) {
			out = RoutingProfilePolicyRule.DEVICE_TYPE_CORPORATE_NETWORK_VPN_VALUE;
		} 
		else if (out.equals(MgrUtil.getUserMessage("hiveAp.autoProvisioning.device.if.pbr.role.wan"))) {
			out = RoutingProfilePolicyRule.DEVICE_TYPE_PRIMARY_WAN_VALUE;
		} else if (out.equals(MgrUtil.getUserMessage("hiveAp.autoProvisioning.device.if.pbr.role.backup1"))) {
			out = RoutingProfilePolicyRule.DEVICE_TYPE_BACKUP_WAN_1_VALUE;
		} else if (out.equals(MgrUtil.getUserMessage("hiveAp.autoProvisioning.device.if.pbr.role.backup2"))) {
			out = RoutingProfilePolicyRule.DEVICE_TYPE_BACKUP_WAN_2_VALUE;
		} else {
			for(int tempNum = AhInterface.DEVICE_IF_TYPE_ETH0;tempNum <= AhInterface.DEVICE_IF_TYPE_ETH28; tempNum++){
				int num = tempNum - AhInterface.DEVICE_IF_TYPE_ETH0;
				String mes = "hiveAp.autoProvisioning.device.if.port.eth" + num;
				String tempStr = MgrUtil.getUserMessage(mes);
				if(out.equals(tempStr)){
					out = String.valueOf(tempNum);
					break;
				}
			}
		}
		
		return out;
	}

	public String changeOutToString(String out) {
		if (out.equals(RoutingProfilePolicyRule.DEVICE_TYPE_BLANK_VALUE)) {
			out = RoutingProfilePolicyRule.DEVICE_TYPE_BLANK_VALUE;
		} else if (out.equals(String.valueOf(AhInterface.DEVICE_IF_TYPE_WIFI0))) {
			out = MgrUtil.getUserMessage("hiveAp.autoProvisioning.device.if.port.wifi0");
		}else if (out.equals(String.valueOf(AhInterface.DEVICE_IF_TYPE_WIFI1))) {
			out = MgrUtil.getUserMessage("hiveAp.autoProvisioning.device.if.port.wifi1");
		} else if (out.equals(String.valueOf(AhInterface.DEVICE_IF_TYPE_USB))) {
			out = MgrUtil.getUserMessage("hiveAp.autoProvisioning.device.if.port.usb");
		} else if (out.equals(RoutingProfilePolicyRule.DEVICE_TYPE_DROP_VALUE)) {
			out = MgrUtil.getUserMessage("hiveAp.autoProvisioning.device.if.pbr.role.drop");
		} else if (out.equals(RoutingProfilePolicyRule.DEVICE_TYPE_CORPORATE_NETWORK_VPN_VALUE)) {
			out =  MgrUtil.getUserMessage("hiveAp.autoProvisioning.device.if.pbr.role.vpn");
		}else if (out.equals(RoutingProfilePolicyRule.DEVICE_TYPE_PRIMARY_WAN_VALUE)) {
			out = MgrUtil.getUserMessage("hiveAp.autoProvisioning.device.if.pbr.role.wan");
		} else if (out.equals(RoutingProfilePolicyRule.DEVICE_TYPE_BACKUP_WAN_1_VALUE)) {
			out = MgrUtil.getUserMessage("hiveAp.autoProvisioning.device.if.pbr.role.backup1");;
		} else if (out.equals(RoutingProfilePolicyRule.DEVICE_TYPE_BACKUP_WAN_2_VALUE)) {
			out = MgrUtil.getUserMessage("hiveAp.autoProvisioning.device.if.pbr.role.backup2");;
		} else {
			int number;
			try {
				number = Integer.parseInt(out);
				if (number >= AhInterface.DEVICE_IF_TYPE_ETH0 && number <= AhInterface.DEVICE_IF_TYPE_ETH28) {
					out = MgrUtil.getUserMessage(
							"hiveAp.autoProvisioning.device.if.port.eth" + (number - AhInterface.DEVICE_IF_TYPE_ETH0));
				}
			} catch (Exception e) {
				System.err.print(e.getMessage());
			}
		}
		return out;
	}
}
