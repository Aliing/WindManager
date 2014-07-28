package com.ah.ui.actions.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.LocationClientWatch;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.network.SingleTableItem;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * 
 *@filename		LocationClientWatchAction.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-4-1 06:34:45
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class LocationClientWatchAction extends BaseAction implements QueryBo{

	private static final Tracer	log	= new Tracer(LocationClientWatchAction.class.getSimpleName());

	private String				macEntry;

	private short				useType;

	private Long				mapID;

	private String				apName;

	private String				tag1;

	private String				tag2;

	private String				tag3;

	private String[]			macEntries;

	private Collection<String>	macIndices;

	private String				watchName;

	private String				clientMacs;

	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}

		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("monitor.locationClientWatch.title.new"))) {
					setUpdateContext(true);
					return getLstForward();
				}

				setSessionDataSource(new LocationClientWatch());
				return getReturnPathWithJsonMode(INPUT, "locationClientWatchJson");
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				if (isJsonMode()) {
					jsonObject = new JSONObject();
					if (checkNameExists("lower(name)", getDataSource().getName()
							.toLowerCase())) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource().getName()));
						return "json";
					}
					updateMacItems();
					String msg = checkMacOUINumber();
					if (!"".equals(msg)) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", msg);
						return "json";
					}
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("newObjName", getDataSource().getName());
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
					if (checkNameExists("lower(name)", getDataSource().getName()
							.toLowerCase())) {
						return INPUT;
					}
					updateMacItems();
					String msg = checkMacOUINumber();
					if (!"".equals(msg)) {
						addActionError(msg);
						return INPUT;
					}
					
					if ("create".equals(operation)) {
						setTableColumns();
						return createBo();
					} else {
						id = createBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("edit".equals(operation)) {
				forward = editBo(this);
				if (dataSource != null) {
					log.info("execute", "Edit client watch name is: " + getDataSource().getName());
				}
				return getReturnPathWithJsonMode(forward, "locationClientWatchJson");
			} else if ("update".equals(operation) || ("update" + getLstForward()).equals(operation)) {
				updateMacItems();
				
				if (isJsonMode()) {
					jsonObject = new JSONObject();
					try {
						String msg = checkMacOUINumber();
						if (!"".equals(msg)) {
							jsonObject.put("resultStatus", false);
							jsonObject.put("errMsg", msg);
							return "json";
						}
						updateBo();
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					jsonObject.put("resultStatus", true);
					return "json";
				} else {
					String msg = checkMacOUINumber();
					if (!"".equals(msg)) {
						addActionError(msg);
						return INPUT;
					}
					
					if ("update".equals(operation)) {
						setTableColumns();
						return updateBo();
					} else {
						id = updateBo(dataSource).getId();
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				LocationClientWatch clone = (LocationClientWatch) findBoById(boClass, cloneId, this);
				clone.setId(null);
				clone.setName("");
				clone.setVersion(null);
				clone.setOwner(null);
				List<SingleTableItem> items = new ArrayList<SingleTableItem>();
				for (SingleTableItem single : clone.getItems()) {
					items.add(single);
				}
				clone.setItems(items);
				setSessionDataSource(clone);
				return INPUT;
			} else if ("addEntry".equals(operation)) {
				if (dataSource == null) {
					return prepareWatchList();
				} else {
					updateMacItems();
					if (!addSingleMac()) {
						addActionError(MgrUtil.getUserMessage("error.addObjectTypeExists"));
					}

					return getReturnPathWithJsonMode(INPUT, "locationClientWatchJson");
				}
			} else if ("removeEntry".equals(operation)) {
				if (dataSource == null) {
					return prepareWatchList();
				} else {
					updateMacItems();
					removeSelectedMacs();
					return getReturnPathWithJsonMode(INPUT, "locationClientWatchJson");
				}
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if ("addClients".equals(operation)){
				LocationClientWatch clientWatch = QueryUtil
						.findBoByAttribute(LocationClientWatch.class, "name", watchName,
								getDomainId());
				if (clientWatch == null) {
					addActionError(MgrUtil.getUserMessage("action.error.location.client.watch.fail"));
					return SUCCESS;
				}
				id = clientWatch.getId();
				setSessionDataSource(findBoById(boClass, id,this));
				
				if (dataSource == null) {
					addActionError(MgrUtil.getUserMessage("action.error.location.client.watch.fail"));
					return getLstForward();
				} else {
					addLstTitle(getText("monitor.locationClientWatch.title.edit") + " '"
							+ getDisplayName() + "'");
					
					//
					String[] macs = clientMacs.split(",");
					for (String mac : macs) {
						SingleTableItem item = new SingleTableItem();
						item.setMacEntry(mac);
						item.setType(SingleTableItem.TYPE_GLOBAL);
						if (!getDataSource().getItems().contains(item)) {
							getDataSource().getItems().add(item);
						}
					}
					
					return INPUT;
				}
			} else {
				baseOperation();
				return prepareWatchList();
			}
		} catch (Exception e) {
			
			return prepareActionError(e);
		}
	}

    private void loadLazyData() {

        if (page.isEmpty())
            return;

        Map<Long, LocationClientWatch> lcWatchMap = new HashMap<Long, LocationClientWatch>();
        StringBuffer buf = new StringBuffer();
        for (Object element : page) {

            LocationClientWatch locationClientWatch = (LocationClientWatch) element;
            lcWatchMap.put(locationClientWatch.getId(), locationClientWatch);
            buf.append(locationClientWatch.getId());
            buf.append(",");

            locationClientWatch.setItems(new ArrayList<SingleTableItem>());
        }
        buf.deleteCharAt(buf.length()-1);

        String sql = "select a.id,b.locationclientwatch_id,b.attributeValue,b.type"
                + " from locationclientwatch a "
                + " inner join locationclient_item b on b.locationclientwatch_id = a.id "
                + " where a.id in(" + buf.toString() + ")";
        
        List<?> templates = QueryUtil.executeNativeQuery(sql);

        for (Object obj : templates) {
            Object[] template = (Object[]) obj;
            Long id = Long.valueOf(template[0].toString());

            LocationClientWatch templateElment = lcWatchMap.get(id);
            if (templateElment != null) {
                if (StringUtils.isNotBlank(template[1].toString())) {
                    SingleTableItem tempClass = new SingleTableItem();
                    tempClass.setAttributeValue((String) template[2]);
                    tempClass.setType(Short.parseShort(template[3].toString()));
                    templateElment.getItems().add(tempClass);
                }
            }
        }
    }

    public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_LOCATIONCLIENTWATCH);
		setDataSource(LocationClientWatch.class);
		keyColumnId = COLUMN_NAME;
		tableId = HmTableColumn.TABLE_LOCATIONCLIENTWATCH;
	}

	public LocationClientWatch getDataSource() {
		return (LocationClientWatch) dataSource;
	}

	private String prepareWatchList() throws Exception {

		setTableColumns();
		String str = prepareBoList();
		loadLazyData();
		return str;
	}

	private String checkMacOUINumber()
	{
		int macCount = 0;
		int ouiCount = 0;
		
		for (SingleTableItem item : getDataSource().getItems()) {
			String entry = item.getMacEntry();
			if (entry != null) {
				if (entry.length() == 6) {
					ouiCount++;
				} else if (entry.length() == 12) {
					macCount++;
				}
			}
		}
		
		if (ouiCount > LocationClientWatch.MAXCOUNT_OUI) {
			return "OUI entries number exceed limit(16).";
		}
		
		if (macCount > LocationClientWatch.MAXCOUNT_STATION) {
			return "MAC entries number exceed limit(256).";
		}
		
		return "";
	}
	
	private void updateMacItems() throws Exception {
		SingleTableItem oneItem;
		for (int i = 0; i < getDataSource().getItems().size(); i++) {
			oneItem = getDataSource().getItems().get(i);
			oneItem.setMacEntry(macEntries[i]);
		}
	}

	private boolean addSingleMac() throws Exception {
		SingleTableItem newItem = new SingleTableItem();
		newItem.setMacEntry(macEntry);
		newItem.setType(useType);
		switch (useType) {
		case SingleTableItem.TYPE_MAP:
			if (mapID != null && mapID > -1) {
				MapContainerNode location = findBoById(MapContainerNode.class,
						mapID);
				newItem.setLocation(location);
			}
			newItem.setTypeName("");
			newItem.setTag1("");
			newItem.setTag2("");
			newItem.setTag3("");
			break;
		case SingleTableItem.TYPE_HIVEAPNAME:
			newItem.setTypeName(apName);
			newItem.setTag1("");
			newItem.setTag2("");
			newItem.setTag3("");
			break;
		case SingleTableItem.TYPE_CLASSIFIER:
			newItem.setTypeName("");
			newItem.setTag1(tag1);
			newItem.setTag2(tag2);
			newItem.setTag3(tag3);
			break;
		default:
			newItem.setLocation(null);
			newItem.setTypeName("");
			newItem.setTag1("");
			newItem.setTag2("");
			newItem.setTag3("");
			break;
		}
		for (SingleTableItem macItem : getDataSource().getItems()) {
			if (macItem.getMacEntry().equals(macEntry) && macItem.getType() == useType) {
				if (null == newItem.getLocation()
						&& null == macItem.getLocation()
						|| (null != newItem.getLocation() && newItem.getLocation().equals(
								macItem.getLocation()))) {
					if (newItem.getTypeName().equals(macItem.getTypeName())
							&& newItem.getTag1().equals(macItem.getTag1())
							&& newItem.getTag2().equals(macItem.getTag2())
							&& newItem.getTag3().equals(macItem.getTag3())) {
						return false;
					}
				}
			}
		}
		getDataSource().getItems().add(newItem);
		return true;
	}

	private void removeSelectedMacs() {
		if (macIndices != null) {
			Collection<SingleTableItem> removeList = new Vector<SingleTableItem>();
			for (String serviceIndex : macIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < getDataSource().getItems().size()) {
						removeList.add(getDataSource().getItems().get(index));
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getItems().removeAll(removeList);
		}
	}

	/*
	 * ID of table columns in list view
	 */
	public static final int	COLUMN_NAME			= 1;

	public static final int	COLUMN_CLIENTCOUNT	= 2;

	public static final int	COLUMN_DESCRIPTION	= 3;

	/**
	 * get the description of column by id
	 * 
	 * @param id -
	 * @return -
	 */
	public final String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_NAME:
			code = "monitor.locationClientWatch.name";
			break;
		case COLUMN_CLIENTCOUNT:
			code = "monitor.locationClientWatch.clientCount";
			break;
		case COLUMN_DESCRIPTION:
			code = "monitor.locationClientWatch.description";
			break;
		}

		return MgrUtil.getUserMessage(code);
	}

	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();

		columns.add(new HmTableColumn(COLUMN_NAME));
		if (!NmsUtil.isHostedHMApplication()) {
			columns.add(new HmTableColumn(COLUMN_CLIENTCOUNT));
		}
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));

		return columns;
	}

	public boolean getDisableName() {
		return getDataSource() != null && getDataSource().getId() != null;
	}

	public EnumItem[] getEnumUseType() {
		return SingleTableItem.ENUM_ADDRESS_USE_TYPE;
	}

	public List<CheckItem> getMapList() {
		List<CheckItem> mapList = getMapListView();
		if (mapList.size() == 0) {
			mapList.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
		return mapList;
	}

	public String getDisplayName() {
		return getDataSource().getName().replace("\\", "\\\\").replace("'", "\\'");
	}

	public int getGridCount() {
		return getDataSource().getItems().size() == 0 ? 3 : 0;
	}

	public String getApName() {
		return apName;
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

	public String[] getMacEntries() {
		return macEntries;
	}

	public void setMacEntries(String[] macEntries) {
		this.macEntries = macEntries;
	}

	public String getMacEntry() {
		return macEntry;
	}

	public void setMacEntry(String macEntry) {
		this.macEntry = macEntry;
	}

	// comment it, otherwise we need set macIndices=null when return to page
	// public Collection<String> getMacIndices() {
	// return macIndices;
	// }

	public void setMacIndices(Collection<String> macIndices) {
		this.macIndices = macIndices;
	}

	public Long getMapID() {
		return mapID;
	}

	public void setMapID(Long mapID) {
		this.mapID = mapID;
	}

	public String getTag1() {
		return tag1;
	}

	public void setTag1(String tag1) {
		this.tag1 = tag1;
	}

	public String getTag2() {
		return tag2;
	}

	public void setTag2(String tag2) {
		this.tag2 = tag2;
	}

	public String getTag3() {
		return tag3;
	}

	public void setTag3(String tag3) {
		this.tag3 = tag3;
	}

	// comment it, otherwise we need set useType=global when return to page
	// public short getUseType() {
	// return useType;
	// }

	public void setUseType(short useType) {
		this.useType = useType;
	}

	public String getClientMacs() {
		return clientMacs;
	}

	public void setClientMacs(String clientIDList) {
		this.clientMacs = clientIDList;
	}

	public String getWatchName() {
		return watchName;
	}

	public void setWatchName(String watchName) {
		this.watchName = watchName;
	}
	
	public String getAddEntryDisabled() {
		
		if (getWriteDisabled().trim().length() > 0) {
			return "disabled";
		}
		
		if (NmsUtil.isHostedHMApplication()) {
			if (getDataSource().getItems().size() > 0)
			{
				return "disabled";
			}
		}
		
		return "";
	}

    @Override
    public Collection<HmBo> load(HmBo bo) {
        if (bo instanceof LocationClientWatch) {
            LocationClientWatch locationClientWatch = (LocationClientWatch) bo;
            if (null != locationClientWatch.getItems())
                locationClientWatch.getItems().size();
        }
        
        return null;
    }

}