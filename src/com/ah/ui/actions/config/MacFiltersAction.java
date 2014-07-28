package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.network.AccessConsole;
import com.ah.bo.network.MacFilter;
import com.ah.bo.network.MacFilterInfo;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/*
 * Modification History
 *
 * setOwner(null) in cloning
 * change commonMacOrOuiList from List<?> to List<CheckItem>
 * modify function - getCommonMacOrOuiList
 */
/*
 * @author Chris Scheers
 */

public class MacFiltersAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(MacFiltersAction.class.getSimpleName());
	
	public static final int MAX_MACFILTER_ENTER=256;
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_DESCRIPTION = 2;
	
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
			code = "config.macFilter.name";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.macFilter.description";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(2);
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			//prepare some fields for jsonMode
			if (isJsonMode() && "continue".equals(operation)) {
				restoreJsonContext();
			}
			
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.macFilter"))) {
					return getLstForward();
				}
				setSessionDataSource(new MacFilter());
				initValues();
				if (isJsonMode()) {
					storeJsonContext();
				}
				return isJsonMode() ? "macFileterDlg" : INPUT;
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				updateMacOrOui();
				if (checkNameExists("filterName", getDataSource()
						.getFilterName())) {
					if (isJsonMode()) {
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus",false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource()
								.getFilterName()));
					}
					
					initValues();
					return isJsonMode() ? (isParentIframeOpenFlg()? "macFileterDlg" : "json") : INPUT;
				}
				if (!checkTotalMac()){
					initValues();
					return isJsonMode() ? (isParentIframeOpenFlg()? "macFileterDlg" : "json") : INPUT;
				}
				
				if (isJsonMode() && !isParentIframeOpenFlg()) {
					jsonObject = new JSONObject();
					try {
						id = createBo(dataSource);
						setUpdateContext(true);
						jsonObject.put("resultStatus", true);
						//jsonObject.put("parentDomID", getParentDomID());
						jsonObject.put("id", id);
						jsonObject.put("name", getDataSource().getFilterName());
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					return "json";
				} else {
					if ("create".equals(operation)) {
						return createBo();
					} else {
						id = createBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("addFilterInfo".equals(operation)) {
				if (dataSource == null) {
					String str = prepareBoList();
					return isJsonMode() ? "macFileterDlg" : str;
				} else {
					updateMacOrOui();
					if (!checkTotalMac()){
						initValues();
						buttonShowing = true;
						return isJsonMode() ? "macFileterDlg" : INPUT;
					}
					addSelectedMacOrOui();
					initValues();
					buttonShowing = false;
					return isJsonMode() ? "macFileterDlg" : INPUT;
				}
			} else if ("removeFilterInfo".equals(operation)) {
				if (dataSource == null) {
					String str = prepareBoList();
					return isJsonMode() ? "macFileterDlg" : str;
				} else {
					updateMacOrOui();
					removeSelectedMacOrOui();
					initValues();
					return isJsonMode() ? "macFileterDlg" : INPUT;
				}
			} else if ("newFilterInfo".equals(operation)||
					   "editFilterInfo".equals(operation)) {
				updateMacOrOui();
				addLstForward("macOrOuiFilter");
				return operation;
			} else if (("cancel" + getLstForward()).equals(operation)) {
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForward();
				} else {
					baseOperation();
					return prepareBoList();
				}
			} else if ("edit".equals(operation)) {
				String strForward = editBo(this);
				if (dataSource != null) {
					addLstTitle(getText("config.title.macFilter.edit")
							+ " '" + getDisplayName() + "'");
					initValues();
				}
				if (isJsonMode()) {
					storeJsonContext();
				}
				return isJsonMode()? "macFileterDlg" : strForward;
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				MacFilter filter = (MacFilter) findBoById(boClass, cloneId,this);
				filter.setId(null);
				filter.setFilterName("");
				filter.setFilterInfo(getFilterInfo(filter.getFilterInfo()));
				filter.setOwner(null);   // joseph chen 06/17/2008
				filter.setVersion(null); // joseph chen 06/17/2008
				setSessionDataSource(filter);
				initValues();
				addLstTitle(getText("config.title.macFilter"));
				return INPUT;
			} else if ("continue".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				buttonShowing = true;
				initValues();
				setId(dataSource.getId());
				if (getUpdateContext()) {
					removeLstTitle();
					removeLstForward();
					setUpdateContext(false);
				}
				return isJsonMode()? "macFileterDlg" : INPUT;
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				if (dataSource != null) {
					updateMacOrOui();
				}
				if (!checkTotalMac()){
					initValues();
					return isJsonMode() ? (isParentIframeOpenFlg()? "macFileterDlg" : "json") : INPUT;
				}
				if (!checkRelationshipMac()){
					initValues();
					return isJsonMode() ? (isParentIframeOpenFlg()? "macFileterDlg" : "json") : INPUT;
				}
				if (isJsonMode() && !isParentIframeOpenFlg()) {
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", true);
					//jsonObject.put("parentDomID", getParentDomID());
					try {
						updateBo(dataSource);
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					return "json";
				} else {
					if ("update".equals(operation)) {
						return updateBo();
					} else {
						updateBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("import".equals(operation)) {
				addLstForward("macFilter");
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

	private List<MacFilterInfo> getFilterInfo(List<MacFilterInfo> list_info) {
		List<MacFilterInfo> list = null;
		if (list_info == null || list_info.size() <= 0)
			return null;
		for (MacFilterInfo dnsInfo : list_info) {
			if (dnsInfo != null) {
				if (list == null)
					list = new ArrayList<MacFilterInfo>();
				if (dnsInfo.getMacOrOui() != null) {
					MacOrOui mac = QueryUtil.findBoById(
							MacOrOui.class, dnsInfo.getMacOrOui().getId());
					dnsInfo.setMacOrOui(mac);
				}
				list.add(dnsInfo);
			}
		}
		return list;
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_MAC_FILTERS);
		setDataSource(MacFilter.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_MAC_FILTER;
	}

	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		MacFilter source = QueryUtil.findBoById(MacFilter.class,
				paintbrushSource,this);
		if (null == source) {
			return null;
		}
		List<MacFilter> list = QueryUtil.executeQuery(MacFilter.class,
				null, new FilterParams("id", destinationIds), domainId,this);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (MacFilter profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			MacFilter up = source.clone();
			if (null == up) {
				continue;
			}
			
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setFilterName(profile.getFilterName());
			up.setOwner(profile.getOwner());
			up.setFilterInfo(getFilterInfo(source.getFilterInfo()));
			hmBos.add(up);
		}
		return hmBos;
	}

	public void initValues() throws Exception {
		prepareMacOrOui();
	}
	
	public boolean checkTotalMac() throws JSONException{
		boolean result = true;
		if (macOrOuiIds!=null) {
			if (getDataSource().getFilterInfo().size() + macOrOuiIds.size()>MAX_MACFILTER_ENTER) {
				addActionError(getText("error.config.macFilter.maxNumber",
						new String[]{String.valueOf(MAX_MACFILTER_ENTER)} ));
				result = false;
			}
		} else {
			if (getDataSource().getFilterInfo().size()>MAX_MACFILTER_ENTER) {
				addActionError(getText("error.config.macFilter.maxNumber",
						new String[]{String.valueOf(MAX_MACFILTER_ENTER)} ));
				result = false;
			}
		}
		if (!result) {
			if (isJsonMode()) {
				jsonObject = new JSONObject();
				jsonObject.put("error",true);
				jsonObject.put("msg",getText("error.config.macFilter.maxNumber",
						new String[]{String.valueOf(MAX_MACFILTER_ENTER)} ));
			}
			return false;
		}
		return true;
	}
	
	public boolean checkRelationshipMac(){
		Set<String> macSet = new HashSet<String>();
		for(MacFilterInfo macInfo:getDataSource().getFilterInfo()){
			macSet.add(macInfo.getMacOrOui().getMacOrOuiName());
		}
		if (!checkRelativedSsid(getDataSource(),macSet)){
			return false;
		}
		if (!checkRelativedHive(getDataSource(),macSet)){
			return false;
		}
		return checkRelativedAccessColsole(getDataSource(),macSet);
	}
	
	private boolean checkRelativedSsid(MacFilter currentMac, Set<String> macSet){
		boolean result = true;
		int ssidCount = 0;
		long start = System.currentTimeMillis();
		List<?> ssidIds = QueryUtil.executeNativeQuery("select distinct ssid_profile_id " +
				"from ssid_profile_mac_filter where mac_filter_id=" + currentMac.getId());

		if (!ssidIds.isEmpty()) {
			for (Object ssidId : ssidIds) {
				ssidCount++;
				List<?> macOUILst =QueryUtil.executeQuery("select distinct bo.ssidName,finalJoin.macOrOui.macOrOuiName from " 
						+ SsidProfile.class.getSimpleName() 
						+ " as bo join bo.macFilters as joined join joined.filterInfo as finalJoin",
						null, new FilterParams("bo.id=:s1 and joined.id!=:s2",
								new Object[]{Long.parseLong(ssidId.toString()),currentMac.getId()}));
				
				
				Set<String> oldMacSet = new HashSet<String>();
				String ssidName = "";
				if (!macOUILst.isEmpty()){
					for(Object oneMac:macOUILst){
						Object[] oneObj = (Object[])oneMac;
						ssidName=oneObj[0].toString();
						oldMacSet.add(oneObj[1].toString());
					}
				}
				oldMacSet.addAll(macSet);
				if (oldMacSet.size()>MAX_MACFILTER_ENTER) {
					addActionError(getText(
							"error.config.macFilter.maxNumber.inssid",
							new String[] {"SSID", ssidName,String.valueOf(MAX_MACFILTER_ENTER) }));
					if (isJsonMode()) {
						jsonObject = new JSONObject();
						try {
							jsonObject.put("error",true);
							jsonObject.put("msg",getText("error.config.macFilter.maxNumber.inssid",
									new String[] {"SSID", ssidName,String.valueOf(MAX_MACFILTER_ENTER) }));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					result = false;
					break;
				}
			}
		}
		long end = System.currentTimeMillis();
		log.info("checkRelativedSsid", "update mac filter check "+ ssidCount +" SSID cost:"+(end-start)+"ms.");
		return result;
	}
	
	private boolean checkRelativedHive(MacFilter currentMac, Set<String> macSet) {
		boolean result = true;
		int hiveCount = 0;
		long start = System.currentTimeMillis();
		List<?> hiveIds = QueryUtil.executeNativeQuery("select distinct hive_profile_id " +
				"from hive_profile_mac_filter where mac_filter_id=" + currentMac.getId());

		if (!hiveIds.isEmpty()) {
			for (Object hiveId : hiveIds) {
				hiveCount++;
				List<?> macOUILst =QueryUtil.executeQuery("select distinct bo.hiveName,finalJoin.macOrOui.macOrOuiName from " 
						+ HiveProfile.class.getSimpleName() 
						+ " as bo join bo.macFilters as joined join joined.filterInfo as finalJoin",
						null, new FilterParams("bo.id=:s1 and joined.id!=:s2",
								new Object[]{Long.parseLong(hiveId.toString()),currentMac.getId()}));
				
				
				Set<String> oldMacSet = new HashSet<String>();
				String hiveName = "";
				if (!macOUILst.isEmpty()){
					for(Object oneMac:macOUILst){
						Object[] oneObj = (Object[])oneMac;
						hiveName=oneObj[0].toString();
						oldMacSet.add(oneObj[1].toString());
					}
				}
				oldMacSet.addAll(macSet);
				if (oldMacSet.size()>MAX_MACFILTER_ENTER) {
					addActionError(getText(
							"error.config.macFilter.maxNumber.inssid",
							new String[] {"Hive", hiveName,String.valueOf(MAX_MACFILTER_ENTER) }));
					if (isJsonMode()) {
						jsonObject = new JSONObject();
						try {
							jsonObject.put("error",true);
							jsonObject.put("msg",getText("error.config.macFilter.maxNumber.inssid",
									new String[] {"Hive", hiveName,String.valueOf(MAX_MACFILTER_ENTER) }));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					result = false;
					break;
				}
			}
		}
		long end = System.currentTimeMillis();
		log.info("checkRelativedHive", "update mac filter check "+ hiveCount +" Hive cost:"+(end-start)+"ms.");
		return result;
	}
	
	private boolean checkRelativedAccessColsole(MacFilter currentMac, Set<String> macSet) {
		boolean result = true;
		int accessConsoleCount = 0;
		long start = System.currentTimeMillis();
		List<?> accessConsoleIds = QueryUtil.executeNativeQuery("select distinct access_console_id " +
				"from access_console_mac_filter where mac_filter_id=" + currentMac.getId());

		if (!accessConsoleIds.isEmpty()) {
			for (Object accessConsoleId : accessConsoleIds) {
				accessConsoleCount++;
				List<?> macOUILst =QueryUtil.executeQuery("select distinct bo.consoleName,finalJoin.macOrOui.macOrOuiName from " 
						+ AccessConsole.class.getSimpleName() 
						+ " as bo join bo.macFilters as joined join joined.filterInfo as finalJoin",
						null, new FilterParams("bo.id=:s1 and joined.id!=:s2",
								new Object[]{Long.parseLong(accessConsoleId.toString()),currentMac.getId()}));
				
				
				Set<String> oldMacSet = new HashSet<String>();
				String accessConsoleName = "";
				if (!macOUILst.isEmpty()){
					for(Object oneMac:macOUILst){
						Object[] oneObj = (Object[])oneMac;
						accessConsoleName=oneObj[0].toString();
						oldMacSet.add(oneObj[1].toString());
					}
				}
				oldMacSet.addAll(macSet);
				if (oldMacSet.size()>MAX_MACFILTER_ENTER) {
					addActionError(getText(
							"error.config.macFilter.maxNumber.inssid",
							new String[] {"Access Console", accessConsoleName,String.valueOf(MAX_MACFILTER_ENTER) }));
					if (isJsonMode()) {
						jsonObject = new JSONObject();
						try {
							jsonObject.put("error",true);
							jsonObject.put("msg",getText("error.config.macFilter.maxNumber.inssid",
									new String[] {"Access Console", accessConsoleName,String.valueOf(MAX_MACFILTER_ENTER) }));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					result = false;
					break;
				}
			}
		}
		long end = System.currentTimeMillis();
		log.info("checkRelativedAccessConsole", "update mac filter check "+ accessConsoleCount +" AccessConsole cost:"+(end-start)+"ms.");
		return result;
	}

	protected void prepareMacOrOui() throws Exception {
		availableMacOrOui = this.getBoCheckItems("macOrOuiName", MacOrOui.class,  new FilterParams("typeFlag !=:s1",new Object[]{MacOrOui.TYPE_MAC_RANGE}));

		for (MacFilterInfo macOrOuiInfo : getDataSource().getFilterInfo()) {
			if(macOrOuiInfo == null) {
				continue;
			}

			MacOrOui macOrOui = macOrOuiInfo.getMacOrOui();

			if(macOrOui == null) {
				continue;
			}

			availableMacOrOui.remove(new CheckItem(macOrOui.getId(), macOrOui.getMacOrOuiName()));
		}

		if (availableMacOrOui.size() == 0) {
			availableMacOrOui.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
	}

	public boolean getDisableName() {
		return getDataSource() != null && getDataSource().getId() != null;
	}

	public String getDisplayName() {
		return getDataSource().getFilterName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}

	public int getNameLength() {
		return getAttributeLength("filterName");
	}

	public int getDescriptionLength() {
		return getAttributeLength("description");
	}

	public int getGridCount() {
		return getDataSource().getFilterInfo().size() < 1 ? GRID_COUNT : 0;
	}

	protected void removeSelectedMacOrOui() {
		if (macOrOuiIndices == null)
			return;
		Collection<MacFilterInfo> removeList = new Vector<MacFilterInfo>();
		for (String macOrOui : macOrOuiIndices) {
			try {
				int index = Integer.parseInt(macOrOui);
				if (index < getDataSource().getFilterInfo().size()) {
					removeList.add(getDataSource().getFilterInfo().get(index));
				}
			} catch (NumberFormatException e) {
				// Bug in struts, shouldn't create a 'false' entry when no
				// check boxes checked.
				return;
			}
		}
		getDataSource().getFilterInfo().removeAll(removeList);
	}

	protected void addSelectedMacOrOui() {
		if (macOrOuiIds == null) {
			return;
		}
		for (Long id : macOrOuiIds) {
			MacFilterInfo macOrOuiInfo = new MacFilterInfo();
			MacOrOui macOrOui = QueryUtil.findBoById(MacOrOui.class,
					id, this);
			if (macOrOui == null) {
				continue;
			}
			macOrOuiInfo.setMacOrOui(macOrOui);
			macOrOuiInfo.setFilterAction(filterAction);
			getDataSource().getFilterInfo().add(macOrOuiInfo);
		}
	}

	public String[] getActionIndex() {
		return actionIndex;
	}

	public void setActionIndex(String[] actionIndex) {
		this.actionIndex = actionIndex;
	}

	protected void updateMacOrOui() {
		if (actionIndex != null)
			for (int i = 0; i < actionIndex.length
					&& i < getDataSource().getFilterInfo().size(); i++) {
				getDataSource().getFilterInfo().get(i).setFilterAction(
						Short.parseShort(actionIndex[i]));
			}
	}

	protected boolean isExistingMacOrOui(String name) {
		if (name == null || name.equals("") || getDataSource() == null)
			return false;
		for (MacFilterInfo macOrOuiInfo : getDataSource().getFilterInfo()) {
			if (macOrOuiInfo!=null && macOrOuiInfo.getMacOrOui()!=null
					&& name.equals(macOrOuiInfo.getMacOrOui().getMacOrOuiName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public MacFilter getDataSource() {
		return (MacFilter) dataSource;
	}

	public EnumItem[] getEnumActionValues() {
		return MacFilter.ENUM_FILTER_ACTION;
	}

	private List<CheckItem> availableMacOrOui;
	private Collection<String> macOrOuiIndices;
	private List<Long> macOrOuiIds;
	private Long macOrOuiId;
	private short filterAction;
	private String[] actionIndex;
	private boolean buttonShowing;

	public boolean getButtonShowing() {
		return buttonShowing;
	}

	public void setButtonShowing(boolean buttonShowing) {
		this.buttonShowing = buttonShowing;
	}

	public List<CheckItem> getAvailableMacOrOui() {
		return availableMacOrOui;
	}

	public void setFilterAction(short filterAction) {
		this.filterAction = filterAction;
	}

	public void setMacOrOuiIds(List<Long> macOrOuiIds) {
		this.macOrOuiIds = macOrOuiIds;
	}

	public void setMacOrOuiIndices(Collection<String> macOrOuiIndices) {
		this.macOrOuiIndices = macOrOuiIndices;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof MacFilter) {
			dataSource = bo;
			if (getDataSource().getFilterInfo()!=null) {
				getDataSource().getFilterInfo().size();
				for (MacFilterInfo filterInfo : getDataSource().getFilterInfo()) {
					MacOrOui macOrOui = filterInfo.getMacOrOui();
					if(null != macOrOui && null != macOrOui.getItems())
						macOrOui.getItems().size();
				}
			}
		}
		if (bo instanceof MacOrOui){
			MacOrOui macOrOui = (MacOrOui) bo;
			if(null != macOrOui.getItems())
				macOrOui.getItems().size();
		}
		return null;
	}

	public Long getMacOrOuiId() {
		return macOrOuiId;
	}

	public void setMacOrOuiId(Long macOrOuiId) {
		this.macOrOuiId = macOrOuiId;
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