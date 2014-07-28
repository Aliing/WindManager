package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.hibernate.validator.constraints.Range;
import org.json.JSONObject;

import com.ah.be.app.HmBeParaUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.parameter.BeParaModule;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.IdsPolicy;
import com.ah.bo.network.IdsPolicySsidProfile;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.Vlan;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.tags.OptionsTransfer;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class IdsPolicyAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(IdsPolicyAction.class
			.getSimpleName());

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}
		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.ids"))) {
					return getLstForward();
				}
				setSessionDataSource(new IdsPolicy());

				// enable BSSID detection
				Map<String, Object> para = new HashMap<String, Object>();
				for (String mac : NmsUtil.getHiveApMacOui()) {
					para.put("macOrOuiName", BeParaModule.DEFAULT_MAC_OUI_NAME + "-" + mac);
					getDataSource().getMacOrOuis().add(HmBeParaUtil.getDefaultProfile(MacOrOui.class, para));
				}
				getDataSource().getVlans().add(HmBeParaUtil.getDefaultProfile(Vlan.class, null));

				prepareDependentObjects();
//				return INPUT;
				return getReturnPath(INPUT, "idsPolicyJson");
			} else if ("create".equals(operation)
					|| ("create" + getLstForward()).equals(operation)) {
				setSelectedObjects();
				updateIdsSsidProfiles();
				jsonObject = new JSONObject();
				if (isJsonMode()) {
					if (checkNameExists("policyName", getDataSource()
							.getPolicyName())) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource().getPolicyName()));
						return "json";
					}
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("newObjName", getDataSource().getPolicyName());
					if ("create".equals(operation)) {
						try {
							id = createBo(dataSource);
							jsonObject.put("newObjId", id);
						} catch (Exception e) {
							jsonObject.put("resultStatus", false);
							jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
							return "json";
						}
					}
					jsonObject.put("resultStatus", true);
					return "json";
				}else{
					if (checkNameExists("policyName", getDataSource()
							.getPolicyName())) {
						prepareDependentObjects();
						return INPUT;
					}
					if(hasBoInEasyMode()) {
					    return prepareBoList();
					}
					updateUnselectedObjects();

					String result;
					Long newId;
					if ("create".equals(operation)) {
						newId = createBo(dataSource);
						result = prepareBoList();
					} else {
						newId = id = createBo(dataSource);
						setUpdateContext(true);
						result = getLstForward();
					}
					if (isEasyMode()) {
						IdsPolicy ids = QueryUtil
								.findBoById(IdsPolicy.class, newId);
						ConfigTemplate defaultTemplate = HmBeParaUtil
								.getEasyModeDefaultTemplate(domainId);
						defaultTemplate.setIdsPolicy(ids);
						QueryUtil.updateBo(defaultTemplate);
					}
					return result;
				}
			} else if ("edit".equals(operation)) {
				String strForward = editBo(this);
				if (dataSource != null) {
					prepareDependentObjects();
					setSelectedId(getDataSource().getId());
				}
				addLstTitle(getText("config.title.ids.edit") + " '"
						+ getChangedIdsName() + "'");
			//	return strForward;
				return getReturnPath(strForward, "idsPolicyJson");
			} else if ("update".equals(operation)
					|| ("update" + getLstForward()).equals(operation)) {
				jsonObject = new JSONObject();
				if (isJsonMode()) {
					try {
						if (dataSource != null) {
							if (dataSource != null) {
								setSelectedObjects();
								updateIdsSsidProfiles();
								updateUnselectedObjects();
							}
						}
						if ("update".equals(operation)) {
							updateBo();
						} else {
							updateBo(dataSource);
							setUpdateContext(true);
						}
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					jsonObject.put("resultStatus", true);
					return "json";
				}else{
					if (dataSource != null) {
						setSelectedObjects();
						updateIdsSsidProfiles();
						updateUnselectedObjects();
					}
					if ("update".equals(operation)) {
						return updateBo();
					} else {
						updateBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("newMacOrOui".equals(operation)
					|| "newVlan".equals(operation)
					|| "newSsid".equals(operation)
					|| "editMacOrOui".equals(operation)
					|| "editVlan".equals(operation)
					|| "editSsid".equals(operation)) {
				setSelectedObjects();
				clearErrorsAndMessages();
				addLstForward("idsPolicy");
				addLstTabId(tabId);
				return operation;
			} else if ("continue".equals(operation)
					|| "continueFromSsid".equals(operation)) {
				if (null == dataSource) {
					return prepareBoList();
				} else {
					setId(dataSource.getId());
					prepareDependentObjects();
					if (getUpdateContext()) {
						removeLstTitle();
						MgrUtil
								.setSessionAttribute("CURRENT_TABID",
										getTabId());
						removeLstForward();
						setUpdateContext(false);
					}
					if ("continueFromSsid".equals(operation)) {
						expanding = true;
					}
//					return INPUT;
					return getReturnPath(INPUT, "idsPolicyJson");
				}
			} else if ("addSsidProfiles".equals(operation)) {
				if (dataSource == null) {
					String returnString = prepareBoList();
					return getReturnPath(returnString, "idsPolicyJson");
				} else {
					updateIdsSsidProfiles();
					addSelectedSsidProfiles();
					setSelectedObjects();
					prepareDependentObjects();
					return getReturnPath(INPUT, "idsPolicyJson");
				}
			} else if ("removeSsidProfiles".equals(operation)) {
				if (dataSource == null) {
					String returnString = prepareBoList();
					return getReturnPath(returnString, "idsPolicyJson");
				} else {
					updateIdsSsidProfiles();
					removeSelectedSsidProfiles();
					setSelectedObjects();
					prepareDependentObjects();
					return getReturnPath(INPUT, "idsPolicyJson");
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				IdsPolicy profile = (IdsPolicy) findBoById(boClass, cloneId,
						this);
				profile.setId(null);
				profile.setPolicyName("");
				profile.setOwner(null);
				profile.setVersion(null);
				profile.setDefaultFlag(false);
				setCloneValues(profile, profile);
				setSessionDataSource(profile);
				prepareDependentObjects();
				addLstTitle(getText("config.title.ids"));
				return INPUT;
			} else if (("cancel" + getLstForward()).equals(operation)) {
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForward();
				} else {
					baseOperation();
					return prepareBoList();
				}
			} else {
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			setL3Features(null);
			return prepareActionError(e);
		}
	}

	public String getChangedIdsName() {
		return getDataSource().getPolicyName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_IDS_POLICY);
		setDataSource(IdsPolicy.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_IDS_POLICY;
	}

	private void setCloneValues(IdsPolicy source, IdsPolicy dest) {
		List<IdsPolicySsidProfile> cloned_idsSsids = new ArrayList<IdsPolicySsidProfile>();
		cloned_idsSsids.addAll(source.getIdsSsids());
		dest.setIdsSsids(cloned_idsSsids);
		Set<MacOrOui> cloned_macOui = new HashSet<MacOrOui>();
		cloned_macOui.addAll(source.getMacOrOuis());
		dest.setMacOrOuis(cloned_macOui);
		Set<Vlan> cloned_vlan = new HashSet<Vlan>();
		cloned_vlan.addAll(source.getVlans());
		dest.setVlans(cloned_vlan);
	}

	@Override
	protected void updateConfigTemplate() throws Exception {
	    ConfigTemplate defaultTemplate = HmBeParaUtil
	        .getEasyModeDefaultTemplate(domainId);
	    defaultTemplate.setIdsPolicy(null);
	    QueryUtil.updateBo(defaultTemplate);
	}

	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		IdsPolicy source = QueryUtil.findBoById(IdsPolicy.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<IdsPolicy> list = QueryUtil.executeQuery(IdsPolicy.class,
				null, new FilterParams("id", destinationIds), domainId);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (IdsPolicy profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			IdsPolicy up = source.clone();
			if (null == up) {
				continue;
			}
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setPolicyName(profile.getPolicyName());
			up.setOwner(profile.getOwner());
			setCloneValues(source, up);
			hmBos.add(up);
		}
		return hmBos;
	}

	public int getPolicyNameLength() {
		return getAttributeLength("policyName");
	}

	public int getDescriptionLength() {
		return getAttributeLength("description");
	}

	public Range getMitigatePeriodRange() {
		return getAttributeRange("mitigatePeriod");
	}

	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().isDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}

	protected OptionsTransfer macOrOuiOptions;

	protected OptionsTransfer vlanOptions;

	protected List<CheckItem> availableSsidProfiles;

	protected List<Long> macOrOuis;

	protected List<Long> vlans;

	private List<Long> ssidIds;

	private boolean encryptionCheck;

	private short encryptionType;

	private Collection<String> ssidIndices;

	private short[] encryptionTypes;

	private Collection<String> encryptionIndices;

	private Collection<String> rowIndices;

	private boolean expanding;

	private long macOrOui;

	private long vlan;

	private long ssid;

	public void setMacOrOui(long macOrOui) {
		this.macOrOui = macOrOui;
	}

	public void setVlan(long vlan) {
		this.vlan = vlan;
	}

	public void setSsid(long ssid) {
		this.ssid = ssid;
	}

	public long getMacOrOui() {
		return macOrOui;
	}

	public long getVlan() {
		return vlan;
	}

	public long getSsid() {
		return ssid;
	}

	public void setRowIndices(Collection<String> rowIndices) {
		this.rowIndices = rowIndices;
	}

	public void setEncryptionCheck(boolean encryptionCheck) {
		this.encryptionCheck = encryptionCheck;
	}

	public void setSsidIndices(Collection<String> ssidIndices) {
		this.ssidIndices = ssidIndices;
	}

	public void setEncryptionType(short encryptionType) {
		this.encryptionType = encryptionType;
	}

	public void setEncryptionTypes(short[] encryptionTypes) {
		this.encryptionTypes = encryptionTypes;
	}

	public void setEncryptionIndices(Collection<String> encryptionIndices) {
		this.encryptionIndices = encryptionIndices;
	}

	public void setSsidIds(List<Long> ssidIds) {
		this.ssidIds = ssidIds;
	}

	public void setMacOrOuis(List<Long> macOrOuis) {
		this.macOrOuis = macOrOuis;
	}

	public void setVlans(List<Long> vlans) {
		this.vlans = vlans;
	}

	public boolean isExpanding() {
		return expanding;
	}

	public void setExpanding(boolean expanding) {
		this.expanding = expanding;
	}

	public OptionsTransfer getMacOrOuiOptions() {
		return macOrOuiOptions;
	}

	public void setMacOrOuiOptions(OptionsTransfer macOrOuiOptions) {
		this.macOrOuiOptions = macOrOuiOptions;
	}

	public OptionsTransfer getVlanOptions() {
		return vlanOptions;
	}

	public void setVlanOptions(OptionsTransfer vlanOptions) {
		this.vlanOptions = vlanOptions;
	}

	public List<CheckItem> getAvailableSsidProfiles() {
		return availableSsidProfiles;
	}

	public EnumItem[] getEnumEncryptionType() {
		return IdsPolicySsidProfile.ENCRYPTION_TYPE;
	}

	public int getGridCount() {
		return getDataSource().getIdsSsids().size() == 0 ? 3 : 0;
	}

	protected void prepareDependentObjects() throws Exception {
		prepareAvailableMacOrOuis();
		prepareAvailableSsids();
		prepareAvailableVlans();
	}

	private void prepareAvailableMacOrOuis() throws Exception {
		List<MacOrOui> availableMacOrOuis = QueryUtil.executeQuery(MacOrOui.class,
				null, null, domainId);
		List<MacOrOui> macOuis = new ArrayList<MacOrOui>();

		for (MacOrOui macOrOui : availableMacOrOuis) {
			if (macOrOui.getTypeFlag() == MacOrOui.TYPE_MAC_OUI) {
				boolean exist = false;
				for (MacOrOui exitOui : macOuis) {
					if (macOrOui.getMacOrOuiName().equals(
							exitOui.getMacOrOuiName())) {
						exist = true;
						break;
					}
				}
				if (!exist) {
					macOuis.add(macOrOui);
					for (Object obj : getDataSource().getMacOrOuis()) {
						MacOrOui tmp_macOrOui = (MacOrOui) obj;
						if (tmp_macOrOui.getTypeFlag() == MacOrOui.TYPE_MAC_OUI) {
							if (tmp_macOrOui.getMacOrOuiName().equals(
									macOrOui.getMacOrOuiName())) {
								macOuis.remove(macOrOui);
							}
						}
					}
				}
			}
		}
		// For the OptionsTransfer component
		macOrOuiOptions = new OptionsTransfer(MgrUtil
				.getUserMessage("config.ids.availableMacOrOuis"), MgrUtil
				.getUserMessage("config.ids.selectedMacOrOuis"), macOuis,
				getDataSource().getMacOrOuis(), "id", "macOrOuiName",
				"macOrOuis", "MacOrOui", SIMPLE_OBJECT_MAC, MAC_SUB_OBJECT_OUI,
				"", domainId);
	}

	private void prepareAvailableVlans() throws Exception {
		List<Vlan> availableVlans = QueryUtil.executeQuery(Vlan.class, null, null,
				domainId);
		List<Vlan> vlans = new ArrayList<Vlan>();
		// Collection<Vlan> removeList = new Vector<Vlan>();

		for (Vlan vlan : availableVlans) {
			boolean exist = false;
			for (Vlan existVlan : vlans) {
				if (existVlan.getVlanName().equals(vlan.getVlanName())) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				vlans.add(vlan);
				for (Object obj : getDataSource().getVlans()) {
					Vlan tmp_vlan = (Vlan) obj;
					if (tmp_vlan.getVlanName().equals(vlan.getVlanName())) {
						// removeList.add(vlan);
						vlans.remove(vlan);
					}
				}
			}
		}
		// For the OptionsTransfer component
		vlanOptions = new OptionsTransfer(MgrUtil
				.getUserMessage("config.ids.availableVlans"), MgrUtil
				.getUserMessage("config.ids.selectedVlans"), vlans,
				getDataSource().getVlans(), "id", "vlanName", "vlans", 16,
				"Vlan", SIMPLE_OBJECT_VLAN, null, "", domainId);
	}

	protected void prepareAvailableSsids() {
		List<CheckItem> all = isEasyMode() ? getBoCheckItems("ssidName",
				SsidProfile.class, new FilterParams("defaultFlag", false))
				: getBoCheckItems("ssidName", SsidProfile.class, null);
		availableSsidProfiles = new ArrayList<CheckItem>();
		for (CheckItem profile : all) {
			if (!hasSsidProfile(profile.getId())) {
				availableSsidProfiles.add(profile);
			}
		}
		if (availableSsidProfiles.size() == 0) {
			availableSsidProfiles.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
	}

	private boolean hasSsidProfile(Long ssidId) {
		for (IdsPolicySsidProfile ssidProfiles : getDataSource().getIdsSsids()) {
			if (ssidId.equals(ssidProfiles.getSsidProfile().getId())) {
				return true;
			}
		}
		return false;
	}

	protected void updateUnselectedObjects() throws Exception {
		IdsPolicy idsPolicy = getDataSource();
		if (null != idsPolicy) {
			if (!(idsPolicy.isOuiEnable())) {
				idsPolicy.setMacOrOuis(null);
			}
			if (!(idsPolicy.isInNetworkEnable())) {
				idsPolicy.setVlans(null);
			}
			if (!(idsPolicy.isSsidEnable())) {
				idsPolicy.setIdsSsids(null);
			}
		}
	}

	protected void setSelectedObjects() throws Exception {
		setSelectedMacOrOuis();
		setSelectedVlans();
		setMitigationModePara();
	}

	private void setMitigationModePara() {
		if (null != radioMitigationMode) {
			if (radioMitigationMode.equals("manual")) {
				getDataSource().setMitigationMode(IdsPolicy.MITIGATION_MODE_MANUAL);
				getDataSource().setInSameNetwork(true);
			} else if (radioMitigationMode.equals("auto")) {
				getDataSource().setMitigationMode(IdsPolicy.MITIGATION_MODE_AUTO);
			} else {
				getDataSource().setMitigationMode(IdsPolicy.MITIGATION_MODE_SEMIAUTO);
				getDataSource().setInSameNetwork(true);
			}
		}
	}

	private void setSelectedMacOrOuis() throws Exception {
		Set<MacOrOui> idsMacOrOui = getDataSource().getMacOrOuis();
		idsMacOrOui.clear();
		if (macOrOuis != null) {

			for (Long ouiId : macOrOuis) {
				MacOrOui macOrOui = findBoById(MacOrOui.class, ouiId);
				if (macOrOui != null) {
					idsMacOrOui.add(macOrOui);
				}
			}
			if (idsMacOrOui.size() != macOrOuis.size()) {
				String tempStr[] = { getText("config.ids.selectedMacOrOuis") };
				addActionError(getText("info.ssid.warning.deleteRecord",
						tempStr));
			}
		}
		getDataSource().setMacOrOuis(idsMacOrOui);
		log.info("setSelectedMacOrOuis", "Ids Policy "
				+ getDataSource().getPolicyName() + " has "
				+ idsMacOrOui.size() + " MacOrOuis.");
	}

	private void setSelectedVlans() throws Exception {
		Set<Vlan> idsVlans = getDataSource().getVlans();
		idsVlans.clear();
		if (vlans != null) {

			for (Long vlanId : vlans) {
				Vlan vlan = findBoById(Vlan.class, vlanId);
				if (vlan != null) {
					idsVlans.add(vlan);
				}
			}
			if (idsVlans.size() != vlans.size()) {
				String tempStr[] = { getText("config.ids.selectedVlans") };
				addActionError(getText("info.ssid.warning.deleteRecord",
						tempStr));
			}
		}
		getDataSource().setVlans(idsVlans);
		log.info("setSelectedVlans", "Ids Policy "
				+ getDataSource().getPolicyName() + " has " + idsVlans.size()
				+ " vlans.");
	}

	protected void updateIdsSsidProfiles() {
		if (rowIndices != null) {
			for (int i = 0; i < rowIndices.size()
					&& i < getDataSource().getIdsSsids().size(); i++) {
				getDataSource().getIdsSsids().get(i).setEncryptionEnable(false);
			}
		}
		if (encryptionIndices != null && encryptionTypes != null
				&& encryptionIndices.size() == encryptionTypes.length) {
			Object[] arr_encryptionIndices = encryptionIndices.toArray();
			for (int i = 0; i < arr_encryptionIndices.length; i++) {
				String encryptionIndex = (String) arr_encryptionIndices[i];
				log.info("updateSsidProfiles", "encryption enabled: "
						+ encryptionIndex);

				try {
					int index = Integer.parseInt(encryptionIndex);
					if (index < getDataSource().getIdsSsids().size()) {
						getDataSource().getIdsSsids().get(index)
								.setEncryptionEnable(true);
						getDataSource().getIdsSsids().get(index)
								.setEncryptionType(encryptionTypes[i]);

					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void addSelectedSsidProfiles() {
		if (ssidIds == null) {
			return;
		}
		for (Long ssidId : ssidIds) {
			SsidProfile ssidProfile = QueryUtil.findBoById(
					SsidProfile.class, ssidId);
			if (ssidProfile == null) {
				continue;
			}
			IdsPolicySsidProfile idsSsidProfile = new IdsPolicySsidProfile();
			idsSsidProfile.setSsidProfile(ssidProfile);
			log.info("addSelectedSsidProfiles", "SsidProfile: " + ssidId);
			idsSsidProfile.setEncryptionEnable(encryptionCheck);
			idsSsidProfile.setEncryptionType(encryptionType);

			getDataSource().getIdsSsids().add(idsSsidProfile);
		}
	}

	protected void removeSelectedSsidProfiles() {
		if (ssidIndices != null) {
			Collection<IdsPolicySsidProfile> removeList = new Vector<IdsPolicySsidProfile>();
			for (String ssidIndex : ssidIndices) {
				try {
					int index = Integer.parseInt(ssidIndex);
					if (index < getDataSource().getIdsSsids().size()) {
						removeList
								.add(getDataSource().getIdsSsids().get(index));
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getIdsSsids().removeAll(removeList);
		}
	}

	public IdsPolicy getDataSource() {
		return (IdsPolicy) dataSource;
	}

	public String getMacOrOuiOptionsRowStatus() {
		if (null != getDataSource()) {
			if (getDataSource().isOuiEnable()) {
				return "";
			}
		}
		return "none";
	}

	public String getSsidOptionsRowStatus() {
		if (null != getDataSource()) {
			if (getDataSource().isSsidEnable()) {
				return "";
			}
		}
		return "none";
	}
	
	public String getAgeTimeRowStatus() {
		if (null != getDataSource()) {
			if (getDataSource().isStaReportEnabled()) {
				return "";
			}
		}
		return "none";
	}

	public String getVlanOptionsRowStatus() {
		if (null != getDataSource()) {
			if (getDataSource().isInNetworkEnable()) {
				return "";
			}
		}
		return "none";
	}

	public Collection<HmBo> load(HmBo bo) {
		if (null != bo && bo instanceof IdsPolicy) {
			IdsPolicy ids = (IdsPolicy) bo;
			if (null != ids.getIdsSsids()) {
				ids.getIdsSsids().size();
			}
			if (null != ids.getVlans()) {
				ids.getVlans().size();
			}
			if (null != ids.getMacOrOuis()) {
				ids.getMacOrOuis().size();
			}
		}
		return null;
	}

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;

	public static final int COLUMN_DETECTION = 2;

	public static final int COLUMN_PERIOD = 3;

	public static final int COLUMN_DURATION = 4;

	public static final int COLUMN_QUIET = 5;

	public static final int COLUMN_DESCRIPTION = 6;

	/**
	 * get the description of column by id
	 *
	 * @param id -
	 * @return String -
	 */
	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
		case COLUMN_NAME:
			code = "config.ids.name";
			break;
		case COLUMN_DETECTION:
			code = "config.ids.rogueDetection";
			break;
		case COLUMN_PERIOD:
			code = "config.ids.head.mitigation.period";
			break;
		case COLUMN_DURATION:
			code = "config.ids.head.mitigation.duration";
			break;
		case COLUMN_QUIET:
			code = "config.ids.head.mitigation.quiet";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.ids.description";
			break;
		}

		return MgrUtil.getUserMessage(code);
	}

	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();

		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_DETECTION));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}

	private String radioMitigationMode;

	public String getRadioMitigationMode()
	{
		if (null == radioMitigationMode) {
			switch (getDataSource().getMitigationMode()) {
				case IdsPolicy.MITIGATION_MODE_AUTO:
					radioMitigationMode = "auto";
					break;
				case IdsPolicy.MITIGATION_MODE_MANUAL:
					radioMitigationMode = "manual";
					break;
				case IdsPolicy.MITIGATION_MODE_SEMIAUTO:
					radioMitigationMode = "semiAuto";
					break;
			}
		}
		return radioMitigationMode;
	}

	public void setRadioMitigationMode(String radioMitigationMode)
	{
		this.radioMitigationMode = radioMitigationMode;
	}

	private String getReturnPath(String normalPath, String jsonModePath) {
		return isJsonMode() ? jsonModePath : normalPath;
	}

}