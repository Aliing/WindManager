package com.ah.ui.actions.teacherView;

/*
 * @author Chris Scheers
 */

import java.util.ArrayList;

import java.util.List;
import com.ah.bo.admin.HmTableColumn;

import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.teacherView.TvResourceMap;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;

public class TvResourceMapAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	public static final int MAX_NUMBER_TV_RESOURCE=64;
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_RESOURCE = 1;
	
	public static final int COLUMN_NAME = 2;
	
	public static final int COLUMN_PORT = 3;
	
	public static final int COLUMN_DESCRIPTION = 4;

	/**
	 * get the description of column by id
	 *
	 * @param id -
	 * @return
	 * @author Joseph Chen
	 */
	protected String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_RESOURCE:
			code = "config.tv.resource";
			break;
		case COLUMN_NAME:
			code = "config.tv.alias";
			break;
		case COLUMN_PORT:
			code = "config.tv.resource.port";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.hp.description";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}
	
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		columns.add(new HmTableColumn(COLUMN_RESOURCE));
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_PORT));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}
	
	public String execute() throws Exception {
		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.tv.resourceMap"))) {
					return getLstForward();
				}
				setSessionDataSource(new TvResourceMap());
				return INPUT;
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				if (checkResourceNameExists()) {
					return INPUT;
				}
				
				List<?> lstStuId = QueryUtil.executeQuery("select distinct resource from " + TvResourceMap.class.getSimpleName(),
						null,new FilterParams("owner.id",getDomainId()));
				if (!lstStuId.contains(getDataSource().getResource())){
					if (lstStuId.size()>=MAX_NUMBER_TV_RESOURCE){
						addActionError(MgrUtil.getUserMessage("error.maxSupportNumber",
								new String[]{"resources",String.valueOf(MAX_NUMBER_TV_RESOURCE)}));
						return INPUT;
					}
				}
				
				if ("create".equals(operation)) {
					return createBo();
				} else {
					id = createBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("edit".equals(operation)) {				
				String returnWord = editBo();
				if (dataSource == null) {
					return prepareBoList();
				} else {
					addLstTitle(getText("config.title.tv.resourceMap.edit") + " '"
							+ getChangedName() + "'");
					return returnWord;
				}
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				};
				
				if (checkResourceNameExists()) {
					return INPUT;
				}
				
				if ("update".equals(operation)) {
					return updateBo();
				} else {
					updateBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				TvResourceMap profile = (TvResourceMap) findBoById(boClass, cloneId);
				profile.setId(null);
				profile.setResource("");
				profile.setOwner(null);
				profile.setVersion(null);
				setSessionDataSource(profile);
				return INPUT;
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if ("import".equals(operation)) {
				addLstForward("tvResourceMap");
				clearErrorsAndMessages();
				return operation;
			} else if ("search".equals(operation)) {
				Object lstCondition[];
				if (!getFilterPort().equals("")){
					lstCondition=new Object[3];
				} else {
					lstCondition=new Object[2];
				}
				lstCondition[0] = "%" + getFilterResource().trim().toLowerCase() + "%";
				lstCondition[1] = "%" + getFilterAlias().trim().toLowerCase() + "%";
				if (!getFilterPort().equals("")){
					lstCondition[2] = Integer.parseInt(getFilterPort());
					filterParams = new FilterParams(
							"lower(resource) like :s1 and lower(alias) like :s2 and port = :s3",
							lstCondition);
				} else {
					filterParams = new FilterParams(
							"lower(resource) like :s1 and lower(alias) like :s2",
							lstCondition);
				}
				setSessionFiltering();
				baseOperation();
				return prepareBoList();
			} else {
				if (baseOperation()){
					this.getSessionFiltering();
				}
				setSessionFiltering();
				return prepareBoList();
			}
		} catch (Exception e) {
			this.getSessionFiltering();
			setSessionFiltering();
			return prepareActionError(e);
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_TV_RESOURCEMAP);
		setDataSource(TvResourceMap.class);
		keyColumnId = COLUMN_RESOURCE;
		this.tableId = HmTableColumn.TABLE_TV_RESOURCEMAP;
	}

	public TvResourceMap getDataSource() {
		return (TvResourceMap) dataSource;
	}

	public String getChangedName() {
		return getDataSource().getLabel().replace("\\", "\\\\").replace("'", "\\'");
	}
	
	/*
	 * Check if BO with attribute 'name' and value 'value' already exists.
	 */
	public boolean checkResourceNameExists() {
		boolean flag = false;
		List<?> boIds = QueryUtil.executeQuery("select id from "
				+ TvResourceMap.class.getSimpleName(), null, 
				new FilterParams("(resource=:s1 or (port=:s2 and alias=:s3))",
						new Object[]{getDataSource().getResource(),
						getDataSource().getPort(),getDataSource().getAlias()}),
				getDomain().getId());
		if (!boIds.isEmpty()) {
			if(dataSource == null){
				flag = true;
			}else{
				boIds.remove(getDataSource().getId());
				flag = boIds.isEmpty() == true ? false : true;
			}
		} else {
			flag = false;
		}
		
		if(flag){
			addActionError(MgrUtil.getUserMessage("error.objectExists",
					"Resource Name: " + getDataSource().getResource() + " or Port: " + getDataSource().getPort()+ ", Ip Address: "+ getDataSource().getAlias()));
		}
		
		return flag;
	}
	
	private String filterResource="";
	private String filterAlias="";
	private String filterPort="";

	/**
	 * @return the filterResource
	 */
	public String getFilterResource() {
		return filterResource;
	}

	/**
	 * @param filterResource the filterResource to set
	 */
	public void setFilterResource(String filterResource) {
		this.filterResource = filterResource;
	}

	/**
	 * @return the filterAlias
	 */
	public String getFilterAlias() {
		return filterAlias;
	}

	/**
	 * @param filterAlias the filterAlias to set
	 */
	public void setFilterAlias(String filterAlias) {
		this.filterAlias = filterAlias;
	}

	/**
	 * @return the filterPort
	 */
	public String getFilterPort() {
		return filterPort;
	}

	/**
	 * @param filterPort the filterPort to set
	 */
	public void setFilterPort(String filterPort) {
		this.filterPort = filterPort;
	}
}
