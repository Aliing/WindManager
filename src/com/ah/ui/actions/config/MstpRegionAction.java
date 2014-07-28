package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.AhDataTableColumn;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.network.MstpRegion;
import com.ah.bo.network.MstpRegionPriority;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.HmException;
import com.ah.util.HmMessageCodes;
import com.ah.util.MgrUtil;

import edu.emory.mathcs.backport.java.util.Arrays;

/*
 * Modification History
 * 
 * support VHM
 *     set owner to null when cloning
 *     change commonIpAddressList from List<Object[]> to List<CheckItem>
 *     modify function prepareIpAddressSnmp,getCommonIpAddress
 * joseph chen 05/07/2008
 * 
 */
public class MstpRegionAction extends BaseAction implements QueryBo {

    private static final long serialVersionUID = 1L;

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_REGION_NAME = 1;
	
	public static final int COLUMN_DESCRIPTION = 2;
	
	public static final int COLUMN_REVISION = 3;
	
	public static final int COLUMN_MAX_HOPS = 4;
	
	private short[] instances;
	private short[] priorities;
	private String[] vlans;
	
	private String mstp_ahDtDatas;

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.switchSettings.mstp.title.new"))) {
					return getLstForward();
				}
				setSessionDataSource(new MstpRegion());
				prepareMstpInstanceDataTable();
				return returnResultKeyWord(INPUT, "mstpJson");
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				jsonObject = new JSONObject();
				prepareDataTableToMstpRegion();
				if (isJsonMode()) {
					if (checkNameExists("regionName", getDataSource().getRegionName())) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource().getRegionName()));
						prepareMstpRegionForDataTable();
						prepareMstpInstanceDataTable();
						return "json";
					}
					
					if(instances != null){
						Set<Short> set = new HashSet<Short>();
						for(int i = 0; i < instances.length; i ++){
							if(!set.add(instances[i])){
								jsonObject.put("resultStatus", false);
								jsonObject.put("errMsg", MgrUtil.getUserMessage("error.mstp.region.instance.unique", Integer.toString(instances[i])));
								prepareMstpRegionForDataTable();
								prepareMstpInstanceDataTable();
								return "json";
							}
						}
					}
					
					if(vlans != null){
						List<String> list = getRangeList(vlans);
						Set<String> set = new HashSet<String>();
						for (String vlan : list){
							if(vlan != null && vlan != ""){
								if(!set.add(vlan)){
									prepareMstpRegionForDataTable();
									prepareMstpInstanceDataTable();
									jsonObject.put("resultStatus", false);
									jsonObject.put("errMsg", MgrUtil.getUserMessage("error.mstp.region.vlan.reference", vlan));
									return "json";
								}
							}
						}
					}
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("newObjName", getDataSource().getRegionName());
					try {
						id = createBo(dataSource);
						jsonObject.put("newObjId", id);
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						prepareMstpRegionForDataTable();
						prepareMstpInstanceDataTable();
						return "json";
					}
					jsonObject.put("resultStatus", true);
					return "json";
				} else {
					if(checkNameExists("regionName", getDataSource().getRegionName())){
						prepareMstpRegionForDataTable();
						prepareMstpInstanceDataTable();
						return INPUT;
					}
					if (!mstpInstanceExists(instances)){
						return INPUT;
					}
					
					if (!instanceVlanExists(vlans)){
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
				fw = editBo(this);
				addLstTitle(getSelectedL2Feature().getDescription()
					+ " > Edit '" + getDisplayName() + "'");
				prepareMstpRegionForDataTable();
				prepareMstpInstanceDataTable();
//				if(getDataSource()!=null){					
//					initValues();
//					if (getDataSource().getSnmpInfo().size() == 0) {
//						buttonShowing = true;
//					}
//				}
				return returnResultKeyWord(fw, "mstpJson");
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				if (dataSource == null || dataSource.getId() == null
						|| !dataSource.getId().equals(id)) {
					generateAuditLog(HmAuditLog.STATUS_FAILURE,  MgrUtil.getUserMessage("hm.audit.log.vhm.update") + getLastTitle() + "("
							+ getDataSource().getLabel() + ")");

					throw new HmException(
							"Update object failed, session must have been shared by another browser window.",
							HmMessageCodes.STALE_SESSION_OBJECT, new String[] { "Update" });
				}
				jsonObject = new JSONObject();
				
				if (isJsonMode()) {
					try {
						if(instances != null){
							Set<Short> set = new HashSet<Short>();
							for(int i = 0; i < instances.length; i ++){
								if(!set.add(instances[i])){
									jsonObject.put("resultStatus", false);
									jsonObject.put("errMsg", MgrUtil.getUserMessage("error.mstp.region.instance.unique", Integer.toString(instances[i])));
									prepareDataTableToMstpRegion();
									prepareMstpRegionForDataTable();
									prepareMstpInstanceDataTable();
									return "json";
								}
							}
						}
						if(vlans != null){
							List<String> list = getRangeList(vlans);
							Set<String> set = new HashSet<String>();
							for (String vlan : list){
								if(vlan != null && vlan != ""){
									if(!set.add(vlan)){
										prepareDataTableToMstpRegion();
										prepareMstpRegionForDataTable();
										prepareMstpInstanceDataTable();
										jsonObject.put("resultStatus", false);
										jsonObject.put("errMsg", MgrUtil.getUserMessage("error.mstp.region.vlan.reference", vlan));
										return "json";
									}
								}
							}
						}
						prepareDataTableToMstpRegion();
						updateBo();
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						e.printStackTrace();
						prepareDataTableToMstpRegion();
						prepareMstpRegionForDataTable();
						prepareMstpInstanceDataTable();
						return "json";
					}
					jsonObject.put("resultStatus", true);
					return "json";
				}else{
					if ("update".equals(operation)) {
						if (!mstpInstanceExists(instances)){
							return INPUT;
						}
						if(!instanceVlanExists(vlans)){
							return INPUT;
						}
						prepareDataTableToMstpRegion();
						return updateBo();
					} else {
						if (!mstpInstanceExists(instances)){
							return INPUT;
						}
						if(!instanceVlanExists(vlans)){
							return INPUT;
						}
						prepareDataTableToMstpRegion();
						updateBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			}else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				MstpRegion mstpRegion = (MstpRegion) findBoById(boClass, cloneId, this);
				mstpRegion.setId(null);
				mstpRegion.setRegionName(null);
				mstpRegion.setOwner(null);
				mstpRegion.setVersion(null);
				setSessionDataSource(mstpRegion);
				addLstTitle(getText("config.switchSettings.mstp.title.new"));
				prepareMstpRegionForDataTable();
				prepareMstpInstanceDataTable();
				return INPUT;
			} else if (("cancel" + getLstForward()).equals(operation)) {
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForward();
				} else {
					baseOperation();
					return prepareBoList();
				}
			} else if("addMstp".equals(operation)){
				if (dataSource == null) {
					String returnString = prepareBoList();
					return returnResultKeyWord(returnString,"mstpJson");
				} else {
					buttonShowing = true;
					return returnResultKeyWord(INPUT,"snmpJson");
				}
				
			}else if("removeMstp".equals(operation)){
				if (dataSource == null) {
					String returnString = prepareBoList();
					return returnResultKeyWord(returnString,"mstpJson");
				} else {
					return returnResultKeyWord(INPUT,"mstpJson");
				}				
			}else {
				if (isJsonMode()) {
					prepareBoList();
					return "mstpJson";
				}
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_MSTP_REGION);
		setDataSource(MstpRegion.class);
		
		keyColumnId = COLUMN_REGION_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_NETWOR_MSTP;

	}

	protected void preparePage() throws Exception {
		enableSorting();
		enablePaging();
		page = findBos(this);
	}

	/**
	 * get the description of column by id
	 *
	 * @param id -
	 * @return -
	 * @author Joseph Chen
	 */
	protected String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_REGION_NAME:
			code = "config.switchSettings.mstp.name";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.switchSettings.mstp.description";
			break;
		case COLUMN_REVISION:
			code = "config.switchSettings.mstp.revision";
			break;
		case COLUMN_MAX_HOPS:
			code = "config.switchSettings.mstp.maxhops";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}
	
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		
		columns.add(new HmTableColumn(COLUMN_REGION_NAME));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		columns.add(new HmTableColumn(COLUMN_REVISION));
		columns.add(new HmTableColumn(COLUMN_MAX_HOPS));
		return columns;
	}


	public void prepareSaveInfo(){
		if(getDataSource()==null) {
		}
	}
	
	public MstpRegion getDataSource() {
		return (MstpRegion) dataSource;
	}
	
	public boolean getDisableName() {
		return getDataSource() != null && getDataSource().getId() != null;
	}

	public int getNameLength() {
		return getAttributeLength("regionName");
	}

	public int getDescriptionLength() {
		return getAttributeLength("description");
	}

	private boolean buttonShowing=false;

	public boolean getButtonShowing() {
		return buttonShowing;
	}

	public void setButtonShowing(boolean buttonShowing) {
		this.buttonShowing = buttonShowing;
	}	

	
	public List<MstpRegionPriority> saveMstpRegionPriority(){
		List<MstpRegionPriority> prioityList = getDataSource().getMstpRegionPriorityList();
		if (prioityList.size() > 0){
			return prioityList;
		} else{
			prioityList = new ArrayList<MstpRegionPriority>();
			if (instances.length > 0){
				for (int i = 0; i < instances.length; i ++){
					MstpRegionPriority prioity = new MstpRegionPriority();
					prioity.setInstance(instances[i]);
					prioity.setTimes(priorities[i]);
					prioity.setVlan(vlans[i]);
					prioityList.add(prioity);
				}
			}
		}
		return prioityList;
	}
	
	private String mstpi_ahDtClumnDefs;
	
	private void prepareMstpInstanceDataTable() throws JSONException {
		List<CheckItem> priorities = getPriorityList();
		List<AhDataTableColumn> ahDataTableColumns = new ArrayList<AhDataTableColumn>();
		AhDataTableColumn column = new AhDataTableColumn();
		column.setMark("priorities");
		column.setOptions(priorities);
		ahDataTableColumns.add(column);
		
		mstpi_ahDtClumnDefs = generateAhDataTableColumnJsonString(ahDataTableColumns);
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if(bo instanceof MstpRegion) {
			MstpRegion mstpRegion = (MstpRegion)bo;

			if(mstpRegion.getMstpRegionPriorityList() != null) {
				mstpRegion.getMstpRegionPriorityList().size();
			}
		}
	    
		return null;
	}
	
	protected String returnResultKeyWord(String normalkey, String expressKey){
		if(isJsonMode()) {
			return  expressKey;
		} else {
			return normalkey;
		}
	}
	
	public String getDisplayName() {
		return getDataSource().getRegionName().replace("\\", "\\\\").replace("'", "\\'");
	}
	
	public short[] getInstances() {
		return instances;
	}

	public void setInstances(short[] instances) {
		this.instances = instances;
	}

	public short[] getPriorities() {
		return priorities;
	}

	public void setPriorities(short[] priorities) {
		this.priorities = priorities;
	}

	public String[] getVlans() {
		return vlans;
	}

	public void setVlans(String[] vlans) {
		this.vlans = vlans;
	}
	
	public String getMstp_ahDtDatas() {
		return mstp_ahDtDatas;
	}

	public void setMstp_ahDtDatas(String mstp_ahDtDatas) {
		this.mstp_ahDtDatas = mstp_ahDtDatas;
	}

	public String getMstpi_ahDtClumnDefs() {
		return mstpi_ahDtClumnDefs;
	}

	public void setMstpi_ahDtClumnDefs(String mstpi_ahDtClumnDefs) {
		this.mstpi_ahDtClumnDefs = mstpi_ahDtClumnDefs;
	}


	public void prepareMstpRegionForDataTable() throws JSONException{
		if (getDataSource() == null 
				|| getDataSource().getMstpRegionPriorityList().size() == 0) {
			mstp_ahDtDatas = "";
			return ;
		}
		
		JSONArray jsonArray = new JSONArray();
		
		for(MstpRegionPriority priority :getDataSource().getMstpRegionPriorityList()){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("instances", priority.getInstance());
			jsonObject.put("priorities", priority.getTimes());
			jsonObject.put("vlans", priority.getVlan());
			jsonArray.put(jsonObject);
		}

		mstp_ahDtDatas = jsonArray.toString();
	}
	
	public void prepareDataTableToMstpRegion(){
		List<MstpRegionPriority> priorityList = new ArrayList<MstpRegionPriority>();
		if(null != getDataSource() && instances != null && instances.length != 0){
			for (int i = 0; i < instances.length; i ++){
				MstpRegionPriority priority = new MstpRegionPriority();
				priority.setInstance(instances[i]);
				priority.setTimes(priorities[i]);
				priority.setVlan(vlans[i]);
				priorityList.add(priority);
			}
		}
		getDataSource().setMstpRegionPriorityList(priorityList);
	}
	
	public boolean mstpInstanceExists(short[] instances){
		Set<Short> set = new HashSet<Short>();
		if(instances != null){
			for(int i = 0; i < instances.length; i ++){
				if(!set.add(instances[i])){
					try {
						prepareDataTableToMstpRegion();
						prepareMstpRegionForDataTable();
						prepareMstpInstanceDataTable();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					addActionError(MgrUtil.getUserMessage("error.mstp.region.instance.unique",
							Integer.toString(instances[i])));
					return false;
				}
			}		   
		}
		return true;
	}
	
	public boolean instanceVlanExists(String[] vlans){
		if(null == vlans){
			return true;
		}
		List<String> list = getRangeList(vlans);
		Set<String> set = new HashSet<String>();
		for (String vlan : list){
			if(vlan != null && vlan != ""){
				if(!set.add(vlan)){
					try {
						prepareDataTableToMstpRegion();
						prepareMstpRegionForDataTable();
						prepareMstpInstanceDataTable();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					addActionError(MgrUtil.getUserMessage("error.mstp.region.vlan.reference",
							vlan));
					return false;
				}
			}
		}
		
		return true;
	}

	private List<String> extracted(String[] vlans) {
		return Arrays.asList(vlans);
	}
	
	public String getUpdateDisabled()
	{
		if ("".equals(getWriteDisabled()))
		{
			return "";
		}
		return "disabled";
	}
	
	public List<CheckItem> getPriorityList (){
		List<CheckItem> list = new ArrayList<CheckItem>();
		short i = MstpRegionPriority.MIN_TIMES;
		while (i < MstpRegionPriority.MAX_TIMES + 1){
			list.add(new CheckItem((long)i,Integer.toString(MstpRegionPriority.BASE_PRIORITY * i)));
			i ++;
		}
		return  list;
	}
	
	public List<String> getRangeList(String[] vlans){
		List<String> list = new ArrayList<String>();
		for(String strSource : vlans) {
			if(strSource.indexOf(",") > 0){
				String[] tmp = strSource.split(",");
				for (String temp : tmp){
					if(temp.indexOf("-") > 0){
						boolean[] argMerge = new boolean[CLICommonFunc.MAX_MERGE_RANGE];
						int from, to;
						from = Integer.valueOf((temp.substring(0, temp.indexOf("-"))).trim());
						to = Integer.valueOf((temp.substring(temp.indexOf("-") +1)).trim());
						for(int i = from; i <= to; i ++){
							argMerge[i] = true;
						}
						for(int m = 0; m < argMerge.length; m ++){
							if(argMerge[m]) {
								list.add(Integer.toString(m));
							}
						}
					}else{
						list.add(temp.trim());
					}
				}
				
			}else if(strSource.indexOf("-") > 0){
				int from, to;
				from = Integer.valueOf((strSource.substring(0, strSource.indexOf("-"))).trim());
				to = Integer.valueOf((strSource.substring(strSource.indexOf("-") +1)).trim());
				boolean[] argMerge = new boolean[CLICommonFunc.MAX_MERGE_RANGE];
				for(int i = from; i <= to; i ++){
					argMerge[i] = true;
				}
				for(int m = 0; m < argMerge.length; m ++){
					if(argMerge[m]) {
						list.add(Integer.toString(m));
					}
				}
			}else{
				list.add(strSource);
			}
		}
		
		
		return list;
	}
		
}