package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.List;

import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mobility.QosClassfierAndMarker;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;
/*
 * Modification History
 * 
 * support VHM
 *     set owner to null when cloning
 * joseph chen 05/07/2008
 */
public class QosClassfierAndMarkerAction extends BaseAction{

	private static final long serialVersionUID = 1L;

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_ENABLE_NETWORK_SERVICE = 2;
	
	public static final int COLUMN_ENABLE_OUI = 3;
	
	public static final int COLUMN_ENABLE_MARK = 4;
	
	public static final int COLUMN_ENABLE_SSID = 5;
	
	public static final int COLUMN_DESCRIPTION = 6;

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.qosClassfierAndMarker"))) {
					return getLstForward();
				}
				setSessionDataSource(new QosClassfierAndMarker());
				return INPUT;
			} else if ("create".equals(operation)) {
				if (checkNameExists("qosName", getDataSource().getQosName())) {
					return INPUT;
				}				
				return createBo();
			} else if ("edit".equals(operation)) {
				setSessionDataSource(findBoById(boClass, id));
				if (dataSource == null) {
					return prepareBoList();
				} else {
					addLstTitle(getText("config.title.qosClassfierAndMarker.edit")
						+ " '" + getDisplayName() + "'");
					return INPUT;
				}
			} else if ("update".equals(operation)) {
				return updateBo();
			} else if (("update" + getLstForward()).equals(operation)) {
				updateBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
			} else if ("clone".equals(operation)) {
				setSessionDataSource(new QosClassfierAndMarker());
				long cloneId = getSelectedIds().get(0);
				QosClassfierAndMarker clone = (QosClassfierAndMarker) findBoById(boClass, cloneId);
				clone.setId(null);
				clone.setQosName("");
				clone.setOwner(null);    // joseph chen 06/17/2008
				clone.setVersion(null);  // joseph chen 06/17/2008
				setSessionDataSource(clone);
				addLstTitle(getText("config.title.qosClassfierAndMarker"));
				return INPUT;
			} else if (("create" + getLstForward()).equals(operation)) {
				if (checkNameExists("qosName", getDataSource().getQosName())) {
					return INPUT;
				}
				id = createBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
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
			return prepareActionError(e);
		}
	}
	public String getDisplayName() {
		return getDataSource().getQosName().replace("\\", "\\\\")
				.replace("'", "\\'");
	}
	public boolean getDisabledName() {
		return getDataSource() != null && getDataSource().getId() != null;
	}
	public int getNameLength() {
		return super.getAttributeLength("qosName");
	}
	public int getDescriptionLength() {
		return super.getAttributeLength("description");
	}
	
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_QOS_CLASSFIER_AND_MARKER);
		setDataSource(QosClassfierAndMarker.class);
		
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_QOS_CLISSIFIER_MARKER;

	}
	
	/**
	 * get the description of column by id
	 * @param id
	 * @return
	 * @author Joseph Chen
	 */
	protected String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_NAME:
			code = "config.qos.classification.name";
			break;
		case COLUMN_ENABLE_NETWORK_SERVICE:
			code = "config.qos.classification.tab.networkServices";
			break;
		case COLUMN_ENABLE_OUI:
			code = "config.qos.classification.macOuis";
			break;
		case COLUMN_ENABLE_MARK:
			code = "config.qos.classification.tab.mark";
			break;
		case COLUMN_ENABLE_SSID:
			code = "config.qos.classification.tab.ssid";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.qos.classification.description";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}

	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_ENABLE_NETWORK_SERVICE));
		columns.add(new HmTableColumn(COLUMN_ENABLE_OUI));
		columns.add(new HmTableColumn(COLUMN_ENABLE_MARK));
		columns.add(new HmTableColumn(COLUMN_ENABLE_SSID));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		
		return columns;
	}
	
	public QosClassfierAndMarker getDataSource(){
		return (QosClassfierAndMarker)dataSource;
	}
	
//	public void prepareSave(){
//		if(getDataSource()==null)
//			return;
//		if(getDataSource().getCheckD())
//			chID="checkD";
//		if(getDataSource().getCheckE())
//			chID="checkE";
//		if(getDataSource().getCheckP())
//			chID="checkP";
//	}
	
//	private String chID="chboxP";
//	private boolean checkE=false;
//	private boolean checkP=false;
//	private boolean checkD=false;
//	
//	public boolean getCheckE() {
//		return checkE;
//	}
//
//	public void setCheckE(boolean checkE) {
//		this.checkE = checkE;
//	}
//
//	public boolean getCheckP() {
//		return checkP;
//	}
//
//	public void setCheckP(boolean checkP) {
//		this.checkP = checkP;
//	}
//
//	public boolean getCheckD() {
//		return checkD;
//	}
//
//	public void setCheckD(boolean checkD) {
//		this.checkD = checkD;
//	}

//	public String getChID() {
//		return chID;
//	}
//
//	public void setChID(String chID) {
//		this.chID = chID;
//	}
}
