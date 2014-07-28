package com.ah.ui.actions.monitor;

/*
 * @author Fisher
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.json.JSONObject;

import com.Ostermiller.util.CSVPrinter;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.be.performance.AhPerformanceScheduleModule;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.monitor.AhEvent;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhBandWidthSentinelHistory;
import com.ah.bo.performance.AhClientSessionHistory;
import com.ah.bo.performance.AhCustomReport;
import com.ah.bo.performance.AhCustomReportField;
import com.ah.bo.performance.AhReport;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.datetime.AhDateTimeUtil;


public class CustomReportListAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;
	private static final int DATA_TYPE_DOUBLE = 1;
	private static final int DATA_TYPE_STRING = 2;
	private static final int DATA_TYPE_DATE = 3;
	
	public static final int CLIENT_DE_AUTH_CODE = 117440512;
	private static final SimpleDateFormat l_sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	public static final int COLUMN_REPORT = 2;
	public static final int COLUMN_TYPE = 3;
	public static final int COLUMN_ORDERTYPE=4;
	public static final int COLUMN_SORTINGKEY=5;
	public static final int COLUMN_DESCRIPTION=6;

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
		case COLUMN_NAME:
			code = "report.reportList.name";
			break;
		case COLUMN_REPORT:
			code = "report.reportList.title.type";
			break;
		case COLUMN_TYPE:
			code = "report.reportList.excuteType";
			break;
		case COLUMN_ORDERTYPE:
			code = "report.customReport.orderType";
			break;
		case COLUMN_SORTINGKEY:
			code = "report.customReport.sortingKey";
			break;
		case COLUMN_DESCRIPTION:
			code = "report.customReport.description";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}

	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();

		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_REPORT));
		columns.add(new HmTableColumn(COLUMN_TYPE));
		columns.add(new HmTableColumn(COLUMN_ORDERTYPE));
		columns.add(new HmTableColumn(COLUMN_SORTINGKEY));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}
	public static TimeZone tz;
	
	public String execute() throws Exception {
		tz = getUserTimeZone();
		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess("Custom Report" + " > New")) {
					return INPUT;
				}
				setSessionDataSource(new AhCustomReport());
				initGuiValue();
				return INPUT;
			} else if ("create".equals(operation)) {
				if (checkNameExists("name", getDataSource().getName())) {
					return INPUT;
				}
				saveGuiValue();
				return createBo();
			} else if ("edit".equals(operation)) {
				editBo(this);
				initGuiValue();
				return INPUT;
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				AhCustomReport ahReport = (AhCustomReport) findBoById(boClass, cloneId,this);
				ahReport.setId(null);
				ahReport.setOwner(getDomain());
				ahReport.setDefaultFlag(false);
				ahReport.setName("");
				List<AhCustomReportField> cloneCustomFields = new ArrayList<AhCustomReportField>();
				for (AhCustomReportField tempClass : ahReport.getCustomFields()) {
					cloneCustomFields.add(tempClass);
				}
				ahReport.setCustomFields(cloneCustomFields);
				
				setSessionDataSource(ahReport);
				addLstTitle("Custom Report" + " > New");
				initGuiValue();
				return INPUT;
			} else if ("update".equals(operation)) {
				if (id == null)
					setId(getDataSource().getId());
				saveGuiValue();
				return updateBo();
			} else if ("run".equals(operation) 
					|| "changePageSize".equals(operation)
					|| "changeNextPage".equals(operation)
					|| "changePreviousPage".equals(operation)
					|| "changeGotoPage".equals(operation)){
				if (getDataSource() == null) {
					long cloneId = getSelectedIds().get(0);
					AhCustomReport ahReport = (AhCustomReport) findBoById(boClass, cloneId,this);
					setId(cloneId);
					setSessionDataSource(ahReport);
				} else {
					saveGuiValue();
				}
				initGuiValue();
				showReportTab = true;
				
				long beginTimeRun = System.currentTimeMillis();
				searchReportData();
				if ("changePreviousPage".equals(operation)){
					getDataSource().setCuPageIndex(getDataSource().getCuPageIndex()-1);
				} else if ("changeNextPage".equals(operation)){
					getDataSource().setCuPageIndex(getDataSource().getCuPageIndex()+1);
				} else if ("changeGotoPage".equals(operation)){
					if (!cuSearchGotoPage.equals("")){
						try {
							getDataSource().setCuPageIndex(Integer.parseInt(cuSearchGotoPage));
							cuSearchGotoPage="";
						} catch (Exception ex){
							cuSearchGotoPage="";
							ex.printStackTrace();
						}
					}
				}
				resetPageInfo();
				if ("run".equals(operation)){
					generateAuditReportLog(beginTimeRun);
				}
				return INPUT;
//			} else if (Navigation.OPERATION_PREVIOUS_PAGE.equals(operation)){
//				
//				
//				
			} else if ("createDownloadData".equals(operation)) {
				setId(getDataSource().getId());
				if (!getUpdateDisabled().equals("")) {
					jsonObject = new JSONObject();
					jsonObject.put("success", false);
					
					jsonObject.put("eword","User '" + getUserContext().getUserName()
							+ "' does not have WRITE access to object '"
							+ getDataSource().getLabel() + "'.");
					return "json";
				}
				
				long beginTimeRun = System.currentTimeMillis();
				searchReportData();
				boolean isSucc;
				if (getDataSource().getReportType() == AhCustomReport.REPORT_TYPE_CLIENT && 
						getDataSource().getReportDetailType() == AhCustomReport.REPORT_DETAILTYPE_COUNT &&
						getDataSource().getLongSortBy().intValue() == 2299) {
					isSucc = generalVendorInfoCurrentCvsFile();
				} else {
					isSucc = generalCurrentCvsFile();
				}
				generateAuditReportLog(beginTimeRun);
				jsonObject = new JSONObject();
				jsonObject.put("success", isSucc);
				return "json";
			} else if ("download".equals(operation)) {
				setId(getDataSource().getId());
				if (!getUpdateDisabled().equals("")) {
					addActionError("User '" + getUserContext().getUserName()
							+ "' does not have WRITE access to object '"
							+ getDataSource().getLabel() + "'.");
					return INPUT;
				}
				File file = new File(getInputPath());
				if (!file.exists()) {
					// commonly, logic should not come here
					addActionError(MgrUtil.getUserMessage("action.error.cannot.find.file"));
					// generateAuditLog(HmAuditLog.STATUS_FAILURE,
					// "Save support bundle");
					return INPUT;
				}
				return "download";
			} else {
				resetMapKeyValue();
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}
	
	public void generateAuditReportLog(long beginTimeRun) {
		long diffTimer = System.currentTimeMillis() - beginTimeRun;
		String diffTimerStr = diffTimer > 1000 ? diffTimer/1000 + "s" : diffTimer + "ms";
		generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.run.report") 
				+ MgrUtil.getUserMessage("report.reportList.name") + " (" + getDataSource().getName() + "),"
				+ MgrUtil.getUserMessage("report.reportList.title.type") + " (" + getDataSource().getReportTypeString() + "),"
				+ MgrUtil.getUserMessage("report.reportList.reportPeriod") + " (" + getDataSource().getReportPeriodString() + "),"
				+ ".  "+MgrUtil.getUserMessage("hm.audit.log.time.used") + diffTimerStr);
	}
	
	private Long locationId;

	private boolean showReportTab;
	public static final Map<Long,String> mapKeyFieldString = new HashMap<Long,String>();
	public static final Map<Long,String> mapKeyTableField = new HashMap<Long,String>();
	private final List<EnumItem> listSortByFieldHiveApU = new ArrayList<EnumItem>();
	private final List<EnumItem> listSortByFieldHiveApC = new ArrayList<EnumItem>();
	private final List<EnumItem> listSortByFieldHiveApV = new ArrayList<EnumItem>();
	private final List<EnumItem> listSortByFieldClientU = new ArrayList<EnumItem>();
	private final List<EnumItem> listSortByFieldClientC = new ArrayList<EnumItem>();
	private final List<EnumItem> listSortByFieldClientV = new ArrayList<EnumItem>();
	private final List<EnumItem> listSortByFieldClientA = new ArrayList<EnumItem>();
	private final List<EnumItem> listSortByFieldSsidC = new ArrayList<EnumItem>();
	private final List<EnumItem> listSortByFieldSsidV = new ArrayList<EnumItem>();
	
	private List<AhCustomReportField> lstCustomField;
	
	private List<Object[]> reportResult;

	private Long longSortHiveApU;
	private Long longSortHiveApC;
	private Long longSortHiveApV;
	private Long longSortClientU;
	private Long longSortClientC;
	private Long longSortClientV;
	private Long longSortClientA;
	private Long longSortSsidC;
	private Long longSortSsidV;
	
	protected List<Long> selectHiveApUIds;
	protected List<Long> selectHiveApCIds;
	protected List<Long> selectHiveApVIds;
	protected List<Long> selectClientUIds;
	protected List<Long> selectClientCIds;
	protected List<Long> selectClientVIds;
	protected List<Long> selectSsidCIds;
	protected List<Long> selectSsidVIds;
	
	private String cuSearchGotoPage="";
	
	public void prepare() throws Exception {
		super.prepare();
		setDataSource(AhCustomReport.class);
		setSelectedL2Feature(L2_FEATURE_CUSTOMREPORT);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_CUSTOMREPORT;
	}
	public AhCustomReport getDataSource() {
		return (AhCustomReport) dataSource;
	}
	
	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().getDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}
	
	private final String mailFileName = "currentReportData.csv";
	public String getInputPath() {
		return AhPerformanceScheduleModule.fileDirPathCurrent + File.separator
				+ getDomain().getDomainName() + File.separator + mailFileName;
	}
	public InputStream getInputStream() throws Exception {
		return new FileInputStream(getInputPath());
	}
	public String getLocalFileName() {
		return mailFileName;
	}
	
	public void resetMapKeyValue(){
		if (mapKeyFieldString.size()<1){
			List<AhCustomReportField> lstCustomField = QueryUtil.executeQuery(AhCustomReportField.class, null, null);
			for(AhCustomReportField tmpCustomField:lstCustomField){
				mapKeyFieldString.put(tmpCustomField.getId(), tmpCustomField.getFieldString());
				mapKeyTableField.put(tmpCustomField.getId(), tmpCustomField.getTableField());
			}
		}
	}
	
	public void resetPageInfo(){
		if (reportResult!= null && reportResult.size()>0) {
			if (getDataSource().getCuPageIndex() ==0){
				getDataSource().setCuPageIndex(1);
			}
			if (reportResult.size()%getDataSource().getCuPageSize()!=0){
				getDataSource().setCuPageCount(reportResult.size()/getDataSource().getCuPageSize()+1);
			} else {
				getDataSource().setCuPageCount(reportResult.size()/getDataSource().getCuPageSize());
			}
			if (getDataSource().getCuPageIndex()>getDataSource().getCuPageCount()){
				getDataSource().setCuPageIndex(getDataSource().getCuPageCount());
			}
		} else {
			getDataSource().setCuPageIndex(0);
			getDataSource().setCuPageCount(0);
		}
	}
	
	public void initGuiValue(){
		if (getDataSource().getLocation()!=null) {
			locationId=getDataSource().getLocation().getId();
		}
		
		lstCustomField = QueryUtil.executeQuery(AhCustomReportField.class, null, null);
		for(AhCustomReportField tmpCustomField:lstCustomField){
			for(AhCustomReportField objSaved: getDataSource().getCustomFields()){
				if (tmpCustomField.getId().equals(objSaved.getId())){
					tmpCustomField.setSelected(true);
				}
			}
			
//			AhCustomReportField tmpCustomField = (AhCustomReportField)obj;
			if (tmpCustomField.getType()==AhCustomReport.REPORT_TYPE_HIVEAP){
				if (tmpCustomField.getDetailType()==AhCustomReport.REPORT_DETAILTYPE_UNIQUE){
					listSortByFieldHiveApU.add(new EnumItem(tmpCustomField.getId().intValue(),
							tmpCustomField.getFieldString()));
				} else if (tmpCustomField.getDetailType()==AhCustomReport.REPORT_DETAILTYPE_COUNT){
					listSortByFieldHiveApC.add(new EnumItem(tmpCustomField.getId().intValue(),
							tmpCustomField.getFieldString()));
				} else if (tmpCustomField.getDetailType()==AhCustomReport.REPORT_DETAILTYPE_VALUE){
					listSortByFieldHiveApV.add(new EnumItem(tmpCustomField.getId().intValue(),
							tmpCustomField.getFieldString()));
				}
			} else if (tmpCustomField.getType()==AhCustomReport.REPORT_TYPE_CLIENT){
				if (tmpCustomField.getDetailType()==AhCustomReport.REPORT_DETAILTYPE_UNIQUE){
					listSortByFieldClientU.add(new EnumItem(tmpCustomField.getId().intValue(),
							tmpCustomField.getFieldString()));
				} else if (tmpCustomField.getDetailType()==AhCustomReport.REPORT_DETAILTYPE_COUNT){
					listSortByFieldClientC.add(new EnumItem(tmpCustomField.getId().intValue(),
							tmpCustomField.getFieldString()));
					listSortByFieldClientA.add(new EnumItem(tmpCustomField.getId().intValue(),
							tmpCustomField.getFieldString()));
				} else if (tmpCustomField.getDetailType()==AhCustomReport.REPORT_DETAILTYPE_VALUE){
					listSortByFieldClientV.add(new EnumItem(tmpCustomField.getId().intValue(),
							tmpCustomField.getFieldString()));
				}
			} else {
				if (tmpCustomField.getDetailType()==AhCustomReport.REPORT_DETAILTYPE_COUNT){
					listSortByFieldSsidC.add(new EnumItem(tmpCustomField.getId().intValue(),
							tmpCustomField.getFieldString()));
				} else if (tmpCustomField.getDetailType()==AhCustomReport.REPORT_DETAILTYPE_VALUE){
					listSortByFieldSsidV.add(new EnumItem(tmpCustomField.getId().intValue(),
							tmpCustomField.getFieldString()));
				}
			}
			
			if (mapKeyFieldString.size()<1){
				mapKeyFieldString.put(tmpCustomField.getId(), tmpCustomField.getFieldString());
				mapKeyTableField.put(tmpCustomField.getId(), tmpCustomField.getTableField());
			}
		}

		if (getDataSource().getReportType()==AhCustomReport.REPORT_TYPE_HIVEAP){
			if (getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_UNIQUE){
				longSortHiveApU = getDataSource().getLongSortBy();
			} else if (getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_COUNT){
				longSortHiveApC = getDataSource().getLongSortBy();
			} else if (getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_AVERAGE){
				longSortHiveApC = getDataSource().getLongSortBy();
			} else if (getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_VALUE){
				longSortHiveApV = getDataSource().getLongSortBy();
			}
		} else if (getDataSource().getReportType()==AhCustomReport.REPORT_TYPE_CLIENT){
			if (getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_UNIQUE){
				longSortClientU = getDataSource().getLongSortBy();
			} else if (getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_COUNT){
				longSortClientC = getDataSource().getLongSortBy();
			} else if (getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_AVERAGE){
				longSortClientA = getDataSource().getLongSortBy();
			} else if (getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_VALUE){
				longSortClientV = getDataSource().getLongSortBy();
			}
		} else {
			if (getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_COUNT){
				longSortSsidC = getDataSource().getLongSortBy();
			} else if (getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_AVERAGE){
					longSortSsidC = getDataSource().getLongSortBy();
			} else if (getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_VALUE){
				longSortSsidV = getDataSource().getLongSortBy();
			}
		}
	}
	
	public void saveGuiValue() throws Exception{
		if (locationId != null && locationId > -1) {
			MapContainerNode location = findBoById(MapContainerNode.class,
					locationId);
			getDataSource().setLocation(location);
		} else {
			getDataSource().setLocation(null);
		}
		
		if (getDataSource().getReportType()==AhCustomReport.REPORT_TYPE_HIVEAP){
			if (getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_UNIQUE){
				getDataSource().setLongSortBy(longSortHiveApU);
				List<AhCustomReportField> lstSelectFields = QueryUtil.executeQuery(AhCustomReportField.class, new SortParams("id"), new FilterParams("id",selectHiveApUIds));
				getDataSource().getCustomFields().clear();
				for(AhCustomReportField ahField:lstSelectFields){
					getDataSource().getCustomFields().add(ahField);
				}
			} else if (getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_COUNT || 
					getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_AVERAGE){
				getDataSource().setLongSortBy(longSortHiveApC);
				List<AhCustomReportField> lstSelectFields = QueryUtil.executeQuery(AhCustomReportField.class, new SortParams("id"), new FilterParams("id",selectHiveApCIds));
				getDataSource().getCustomFields().clear();
				for(AhCustomReportField ahField:lstSelectFields){
					getDataSource().getCustomFields().add(ahField);
				}
			} else if (getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_VALUE){
				getDataSource().setLongSortBy(longSortHiveApV);
				List<AhCustomReportField> lstSelectFields = QueryUtil.executeQuery(AhCustomReportField.class, new SortParams("id"), new FilterParams("id",selectHiveApVIds));
				getDataSource().getCustomFields().clear();
				for(AhCustomReportField ahField:lstSelectFields){
					getDataSource().getCustomFields().add(ahField);
				}
			}
		} else if (getDataSource().getReportType()==AhCustomReport.REPORT_TYPE_CLIENT){
			if (getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_UNIQUE){
				getDataSource().setLongSortBy(longSortClientU);
				List<AhCustomReportField> lstSelectFields = QueryUtil.executeQuery(AhCustomReportField.class, new SortParams("id"), new FilterParams("id",selectClientUIds));
				getDataSource().getCustomFields().clear();
				for(AhCustomReportField ahField:lstSelectFields){
					getDataSource().getCustomFields().add(ahField);
				}
			} else if (getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_COUNT ||
					getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_AVERAGE){
				if (getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_COUNT){
					getDataSource().setLongSortBy(longSortClientC);
				} else {
					getDataSource().setLongSortBy(longSortClientA);
				}
				List<AhCustomReportField> lstSelectFields = QueryUtil.executeQuery(AhCustomReportField.class, new SortParams("id"), new FilterParams("id",selectClientCIds));
				getDataSource().getCustomFields().clear();
				for(AhCustomReportField ahField:lstSelectFields){
					getDataSource().getCustomFields().add(ahField);
				}
			} else if (getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_VALUE){
				getDataSource().setLongSortBy(longSortClientV);
				List<AhCustomReportField> lstSelectFields = QueryUtil.executeQuery(AhCustomReportField.class, new SortParams("id"), new FilterParams("id",selectClientVIds));
				getDataSource().getCustomFields().clear();
				for(AhCustomReportField ahField:lstSelectFields){
					getDataSource().getCustomFields().add(ahField);
				}
			}
		} else {
			if (getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_COUNT ||
					getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_AVERAGE){
				getDataSource().setLongSortBy(longSortSsidC);
				List<AhCustomReportField> lstSelectFields = QueryUtil.executeQuery(AhCustomReportField.class, new SortParams("id"), new FilterParams("id",selectSsidCIds));
				getDataSource().getCustomFields().clear();
				for(AhCustomReportField ahField:lstSelectFields){
					getDataSource().getCustomFields().add(ahField);
				}
			} else if (getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_VALUE){
				getDataSource().setLongSortBy(longSortSsidV);
				List<AhCustomReportField> lstSelectFields = QueryUtil.executeQuery(AhCustomReportField.class, new SortParams("id"), new FilterParams("id",selectSsidVIds));
				getDataSource().getCustomFields().clear();
				for(AhCustomReportField ahField:lstSelectFields){
					getDataSource().getCustomFields().add(ahField);
				}
			} else {
				getDataSource().getCustomFields().clear();
			}
		}
	}
	
	public void searchReportData(){
		if (getDataSource().getReportType() == AhCustomReport.REPORT_TYPE_HIVEAP){
			if (getDataSource().getReportDetailType() == AhCustomReport.REPORT_DETAILTYPE_UNIQUE){
				searchReportHiveApUniqueData();
			} else if (getDataSource().getReportDetailType() == AhCustomReport.REPORT_DETAILTYPE_COUNT){
				searchReportHiveApCountData(AhCustomReport.REPORT_DETAILTYPE_COUNT);
			} else if (getDataSource().getReportDetailType() == AhCustomReport.REPORT_DETAILTYPE_AVERAGE){
				searchReportHiveApCountData(AhCustomReport.REPORT_DETAILTYPE_AVERAGE);
			} else if (getDataSource().getReportDetailType() == AhCustomReport.REPORT_DETAILTYPE_VALUE){
				searchReportHiveApValueData();
			}
		} else if (getDataSource().getReportType() == AhCustomReport.REPORT_TYPE_CLIENT){
			if (getDataSource().getReportDetailType() == AhCustomReport.REPORT_DETAILTYPE_UNIQUE){
				searchReportClientUniqueData();
			} else if (getDataSource().getReportDetailType() == AhCustomReport.REPORT_DETAILTYPE_COUNT){
				if (getDataSource().getLongSortBy().intValue()!=2299) {
					searchReportClientCountData(AhCustomReport.REPORT_DETAILTYPE_COUNT);
				} else {
					searchReportClientCountDataForVendor();
				}
			} else if (getDataSource().getReportDetailType() == AhCustomReport.REPORT_DETAILTYPE_AVERAGE){
				searchReportClientCountData(AhCustomReport.REPORT_DETAILTYPE_AVERAGE);
			} else if (getDataSource().getReportDetailType() == AhCustomReport.REPORT_DETAILTYPE_VALUE){
				searchReportClientValueData();
			}
		} else if (getDataSource().getReportType() == AhCustomReport.REPORT_TYPE_SSID){
			if (getDataSource().getReportDetailType() == AhCustomReport.REPORT_DETAILTYPE_COUNT){
				searchReportSsidCountData(AhCustomReport.REPORT_DETAILTYPE_COUNT);
			} else if (getDataSource().getReportDetailType() == AhCustomReport.REPORT_DETAILTYPE_AVERAGE){
				searchReportSsidCountData(AhCustomReport.REPORT_DETAILTYPE_AVERAGE);
			} else if (getDataSource().getReportDetailType() == AhCustomReport.REPORT_DETAILTYPE_VALUE){
				searchReportSsidValueData();
			}
		}
	}
	
	public void searchReportHiveApUniqueData(){
		StringBuffer tmpSql = new StringBuffer();
		tmpSql.append("select ");
		for(int i=0;i<getDataSource().getCustomFields().size();i++){
			tmpSql.append(getDataSource().getCustomFields().get(i).getTableField());
			if (i != getDataSource().getCustomFields().size()-1){
				tmpSql.append(", ");
			} 
		}
		tmpSql.append(" from HIVE_AP where owner=").append(getDomain().getId());
		tmpSql.append(" and manageStatus =" +  HiveAp.STATUS_MANAGED);
		if (getDataSource().getLocation() != null) {
			tmpSql.append(" and map_container_id=").append(getDataSource().getLocation().getId());
		}
		if (getDataSource().getApName()!= null && !getDataSource().getApName().trim().equals("")){
			tmpSql.append(" and lower(hostName) like '%").append(getDataSource().getApNameForSQL()).append("%'");
		}
		tmpSql.append(" order by ").append(getDataSource().getLongSortByTableField());
		if (getDataSource().getSortByType() == AhCustomReport.REPORT_SORTBY_TYPE_DESC) {
			tmpSql.append(" desc");
		} 
		
		reportResult = (List<Object[]>) QueryUtil.executeNativeQuery(tmpSql.toString(),100000);
		
		for(int i=0;i<getDataSource().getCustomFields().size();i++){
			for (Object[] values : reportResult) {
				if (getDataSource().getCustomFields().get(i).getTableField().equalsIgnoreCase("upTime")){
					values[i] = getUpTimeString(Long.parseLong(values[i].toString()));
				} else if (getDataSource().getCustomFields().get(i).getTableField().equalsIgnoreCase("hiveApType")){
					values[i] = getHiveApTypeString(Short.parseShort(values[i].toString()));
				}
			}
		}
	}
	public String getUpTimeString(long time ) {
		if (time > 0) {
			long upDuration = System.currentTimeMillis() - time;
			return NmsUtil.transformTime((int) (upDuration / 1000));
		} else {
			return "Unknown";
		}
	}
	
	public String getHiveApTypeString(short hiveApType) {
		switch (hiveApType) {
		case HiveAp.HIVEAP_TYPE_MP:
		case HiveAp.HIVEAP_TYPE_PORTAL:
			return MgrUtil.getEnumString("enum.hiveAp.type." + hiveApType);
		default:
			return "Unknown";
		}
	}
	
	public Calendar getReportDateTime() {
		int reportDay = 0;
		int reportMonth = 0;
		switch (getDataSource().getReportPeriod()) {
		case AhReport.REPORT_PERIOD_LASTONEDAY:
			reportDay = 1;
			break;
		case AhReport.REPORT_PERIOD_LASTTWODAYS:
			reportDay = 2;
			break;
		case AhReport.REPORT_PERIOD_LASTTHREEDAYS:
			reportDay = 3;
			break;
		case AhReport.REPORT_PERIOD_LASTONEWEEK:
			reportDay = 7;
			break;
		case AhReport.REPORT_PERIOD_LASTTWOWEEKS:
			reportDay = 14;
			break;
		case AhReport.REPORT_PERIOD_LASTTHREEWEEKS:
			reportDay = 21;
			break;
		case AhReport.REPORT_PERIOD_LASTONEMONTH:
			reportMonth = 1;
			break;
		case AhReport.REPORT_PERIOD_LASTTWOMONTHs:
			reportMonth = 2;
			break;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, reportDay * -1);
		calendar.add(Calendar.MONTH, reportMonth * -1);
		return calendar;
	}
	
	public void searchReportHiveApCountData(int detailType){
		DecimalFormat df = new DecimalFormat("0");
		long reportTimeMill = getReportDateTime().getTimeInMillis();
		boolean bandWidthFlg = false;
		StringBuffer tmpSql = new StringBuffer();
		tmpSql.append("select ");
		for(int i=0;i<getDataSource().getCustomFields().size();i++){
			if (!getDataSource().getCustomFields().get(i).getTableField().equalsIgnoreCase("bandWidthSentinelStatus")){
				tmpSql.append("bo.").append(getDataSource().getCustomFields().get(i).getTableField()).append(", ");
			} else {
				tmpSql.append("0,");
				bandWidthFlg = true;
			}
		}
		tmpSql.append(" xif.ifname");
		tmpSql.append(" from HM_RADIOSTATS bo, hm_xif xif where bo.owner=").append(getDomain().getId());
		tmpSql.append(" and xif.owner=").append(getDomain().getId());
		
		tmpSql.append(" and xif.apname=bo.apname and xif.ifindex=bo.ifindex and xif.stattimestamp=bo.stattimestamp");

		if (getDataSource().getLocation() != null) {
			String findApNameSql = "select hostName from HIVE_AP where owner=" + getDomain().getId() + 
						" and map_container_id=" +  getDataSource().getLocation().getId();
			if (getDataSource().getApName()!= null && !getDataSource().getApName().trim().equals("")){
				findApNameSql = findApNameSql + " and lower(hostName) like '%" + getDataSource().getApNameForSQL() + "%'";
			}
			List<?> apNameList = QueryUtil.executeNativeQuery(findApNameSql);
			if (apNameList.size()<1) {
				reportResult = null;
				return;
			} else {
				String strApNameSql = "";
				int k=0;
				for(Object tmpApName:apNameList){
					strApNameSql = strApNameSql + "'" + NmsUtil.convertSqlStr(tmpApName.toString()) + "'";
					k++;
					if (k!=apNameList.size()){
						strApNameSql = strApNameSql + ",";
					}
				}
				tmpSql.append(" and bo.apName in(").append(strApNameSql).append(")");
			}
		} else {
			if (!getDataSource().getApName().trim().equals("")){
				tmpSql.append(" and lower(bo.apName) like '%").append(getDataSource().getApNameForSQL()).append("%'");
			}
		}
		
		if (getDataSource().getInterfaceRole()== AhCustomReport.REPORT_INTERFACE_WIFI0){
			tmpSql.append(" and lower(xif.ifname) = 'wifi0'");
		}
		if (getDataSource().getInterfaceRole()== AhCustomReport.REPORT_INTERFACE_WIFI1){
			tmpSql.append(" and lower(xif.ifname) = 'wifi1'");
		}

		tmpSql.append(" and bo.stattimestamp >=").append(reportTimeMill);
		tmpSql.append(" order by bo.apName,bo.stattimestamp");
		List<?> initReportResult = QueryUtil.executeNativeQuery(tmpSql.toString(),100000);
		
		Map<String,Integer> mapSlaApNameCount = new HashMap<String,Integer>();
		if (bandWidthFlg){
			String sqlSla = "select apName,count(apname) from hm_bandwidthsentinel_history where owner=" + getDomain().getId(); 
			sqlSla = sqlSla + " and lower(apname) like '%" + getDataSource().getApNameForSQL() + "%'";
			sqlSla = sqlSla + " and time >=" + reportTimeMill;
			sqlSla = sqlSla + " and (bandwidthsentinelstatus =" + AhBandWidthSentinelHistory.STATUS_BAD;
			sqlSla = sqlSla + " or bandwidthsentinelstatus =" + AhBandWidthSentinelHistory.STATUS_ALERT + ")";
			sqlSla = sqlSla + " group by apname";
			List<?> initReportResultSLA = QueryUtil.executeNativeQuery(sqlSla);
			if (initReportResultSLA!=null && initReportResultSLA.size()>0){
				for(Object obj:initReportResultSLA){
					Object[] oneSlaItem = (Object[])obj;
					mapSlaApNameCount.put(oneSlaItem[0].toString(), Integer.parseInt(oneSlaItem[1].toString()));
				}
			}
		}
		
		int colCount = getDataSource().getCustomFields().size() + 1;
		Object[] oneRecord = new Object[colCount-1];
		Object[] temWifi0Report = new Object[colCount];
		Object[] temWifi1Report = new Object[colCount];
		
		int initSize=0;
		int devideWifi0Count = 0;
		int devideWifi1Count = 0;
		for(Object tmpObj:initReportResult){
			initSize++;
			Object[] oneRow = (Object[])tmpObj;
			
			if (temWifi0Report[0]==null && oneRow[colCount-1].toString().equalsIgnoreCase("wifi0")) {
				temWifi0Report = oneRow;
				if (oneRecord[0] == null) {
					oneRecord[0] = oneRow[0].toString();
					oneRecord[1] = oneRow[1].toString();
					for(int i=2;i<colCount-1; i++ ){
						oneRecord[i] = 0;
					}
				}
			}
			if (temWifi1Report[0]==null && oneRow[colCount-1].toString().equalsIgnoreCase("wifi1")) {
				temWifi1Report = oneRow;
				if (oneRecord[0] == null) {
					oneRecord[0] = oneRow[0].toString();
					oneRecord[1] = oneRow[1].toString();
					for(int i=2;i<colCount-1; i++ ){
						oneRecord[i] = 0;
					}
				}
			}
			if ((temWifi0Report[0] == null && temWifi1Report[0] != null 
					&& temWifi1Report[0].toString().equalsIgnoreCase(oneRow[0].toString())) 
				|| (temWifi1Report[0] == null && temWifi0Report[0] != null
					&& temWifi0Report[0].toString().equalsIgnoreCase(oneRow[0].toString())) 
			    || (temWifi0Report[0]!= null && temWifi1Report[0] != null 
			    		&& temWifi0Report[0].toString().equalsIgnoreCase(temWifi1Report[0].toString())
			    		&& temWifi0Report[0].toString().equalsIgnoreCase(oneRow[0].toString()))){
				if (oneRow[colCount-1].toString().equalsIgnoreCase("wifi0") &&
						oneRow[0].toString().equalsIgnoreCase(temWifi0Report[0].toString())){
					for(int i=2;i<colCount-1; i++ ){
						oneRecord[i] = Double.parseDouble(oneRecord[i].toString()) + compareValue(oneRow[i],temWifi0Report[i]);
					}
					devideWifi0Count++;
					temWifi0Report = oneRow;
				} else if (oneRow[colCount-1].toString().equalsIgnoreCase("wifi1") &&
					oneRow[0].toString().equalsIgnoreCase(temWifi1Report[0].toString())){
					for(int i=2;i<colCount-1; i++ ){
						oneRecord[i] = Double.parseDouble(oneRecord[i].toString()) + compareValue(oneRow[i],temWifi1Report[i]);
					}
					devideWifi1Count++;
					temWifi1Report = oneRow;
				}
			} else {
				if (reportResult==null){
					reportResult = new ArrayList<Object[]>(); 
				}
				if (bandWidthFlg){
					oneRecord[colCount-2] = 
						mapSlaApNameCount.get(oneRecord[0].toString()) == null
						?0:mapSlaApNameCount.get(oneRecord[0].toString());
				}
				
				if (detailType == AhCustomReport.REPORT_DETAILTYPE_AVERAGE){
					int deviceCount = devideWifi0Count>devideWifi1Count ? devideWifi0Count: devideWifi1Count;
					if (deviceCount ==0) {
						deviceCount = 1;
					}
					
					for(int k=2; k < oneRecord.length; k++) {
						oneRecord[k] = df.format(Double.parseDouble(oneRecord[k].toString())/deviceCount);
					}
					reportResult.add(oneRecord);
				} else {
					for(int k=2; k < oneRecord.length; k++) {
						oneRecord[k] = df.format(Double.parseDouble(oneRecord[k].toString()));
					}
					reportResult.add(oneRecord);
				}
				
				if (oneRow[colCount-1].toString().equalsIgnoreCase("wifi0")){
					temWifi0Report = oneRow;
					temWifi1Report =  new Object[colCount];
				} else {
					temWifi0Report = new Object[colCount];
					temWifi1Report = oneRow;
				}
				
				oneRecord = new Object[colCount-1];
				oneRecord[0] = oneRow[0].toString();
				oneRecord[1] = oneRow[1].toString();
				for(int i=2;i<colCount-1; i++ ){
					oneRecord[i] = 0;
				}
			}
			if (initSize == initReportResult.size()){
				if (reportResult==null){
					reportResult = new ArrayList<Object[]>(); 
				}
				if (bandWidthFlg){
					oneRecord[colCount-2] =  mapSlaApNameCount.get(oneRecord[0].toString()) == null
						?0:mapSlaApNameCount.get(oneRecord[0].toString());
				}
				if (detailType == AhCustomReport.REPORT_DETAILTYPE_AVERAGE){
					int deviceCount = devideWifi0Count>devideWifi1Count ? devideWifi0Count: devideWifi1Count;
					if (deviceCount ==0) {
						deviceCount = 1;
					}
					
					for(int k=2; k < oneRecord.length; k++) {
						oneRecord[k] = df.format(Double.parseDouble(oneRecord[k].toString())/deviceCount);
					}
					reportResult.add(oneRecord);
				} else {
					for(int k=2; k < oneRecord.length; k++) {
						oneRecord[k] = df.format(Double.parseDouble(oneRecord[k].toString()));
					}
					reportResult.add(oneRecord);
				}
			}
		}
		
		if (reportResult!=null && getDataSource().getLongSortBy()>0){
			boolean orderByDesc = false;
			if (getDataSource().getSortByType()==AhCustomReport.REPORT_SORTBY_TYPE_DESC) {
				orderByDesc = true;
			}
			int sortIndex = 0;
			int dataType=DATA_TYPE_DOUBLE;
			for(AhCustomReportField fieldRow: getDataSource().getCustomFields()){
				if (getDataSource().getLongSortBy().equals(fieldRow.getId())) {
					if (fieldRow.getTableField().trim().equalsIgnoreCase("apName") || 
							fieldRow.getTableField().trim().equalsIgnoreCase("apMac")){
						dataType = DATA_TYPE_STRING;
					}
					break;
				}
				sortIndex++;
			}
			Collections.sort(reportResult, new SortReportComparator(sortIndex,orderByDesc,dataType));
		}
	}
	
	class SortReportComparator implements Comparator<Object> {
		private final int index;
		private final boolean desc;
		// 1:double 2:string 3 date
		private final int valueType;
		public SortReportComparator(int index, boolean desc, int valueType){
			this.index= index;
			this.desc= desc;
			this.valueType= valueType;
		}
		
		@Override
		public int compare(Object o1, Object o2) {
			try {
				Object[] oo1 = (Object[]) o1;
				Object[] oo2 = (Object[]) o2;
				double ret;
				if (oo1.length < index + 1 || oo1[index] == null)
					return 0;
				if (oo2.length < index + 1 || oo2[index] == null)
					return 0;
				if (valueType == DATA_TYPE_DOUBLE) {
					ret = Double.parseDouble(oo1[index].toString())
							- Double.parseDouble(oo2[index].toString());
				} else if (valueType == DATA_TYPE_STRING) {
					ret = oo1[index].toString().compareToIgnoreCase(oo2[index].toString());
				} else {
					String cpoo1 = oo1[index].toString();
					String cpoo2 = oo2[index].toString();
					String oo1Date[] = cpoo1.split("\\|");
					if (oo1Date.length==2){
						cpoo1 = oo1Date[1];
					}
					String oo2Date[] = cpoo2.split("\\|");
					if (oo2Date.length==2){
						cpoo2 = oo2Date[1];
					}
					ret = l_sdf.parse(cpoo1).compareTo(l_sdf.parse(cpoo2));
				}
				if (desc) {
					ret = ret * -1;
				}
				if (ret > 0) {
					return 1;
				} else if (ret < 0) {
					return -1;
				}
				return 0;
			} catch (Exception e) {
				return 0;
			}
		}
	}

	public double compareValue(Object value1, Object value2){
		if (Double.parseDouble(value1.toString()) >= Double.parseDouble(value2.toString())) {
			return Double.parseDouble(value1.toString()) - Double.parseDouble(value2.toString());
		} else {
			return Double.parseDouble(value1.toString());
		}
	}
	
	public void searchReportHiveApValueData(){
		DecimalFormat df = new DecimalFormat("0");
		long reportTimeMill = getReportDateTime().getTimeInMillis();

		StringBuffer tmpSql = new StringBuffer();
		tmpSql.append("select ");
		for(int i=0;i<getDataSource().getCustomFields().size();i++){
			tmpSql.append("bo.").append(getDataSource().getCustomFields().get(i).getTableField()).append(", ");
		}
		tmpSql.append(" xif.ifname");
		tmpSql.append(" from HM_RADIOSTATS bo, hm_xif xif where bo.owner=").append(getDomain().getId());
		tmpSql.append(" and xif.owner=").append(getDomain().getId());
		
		tmpSql.append(" and xif.apname=bo.apname and xif.ifindex=bo.ifindex and xif.stattimestamp=bo.stattimestamp");

		if (getDataSource().getLocation() != null) {
			String findApNameSql = "select hostName from HIVE_AP where owner=" + getDomain().getId() + 
						" and map_container_id=" +  getDataSource().getLocation().getId();
			if (getDataSource().getApName()!= null && !getDataSource().getApName().trim().equals("")){
				findApNameSql = findApNameSql + " and lower(hostName) like '%" + getDataSource().getApNameForSQL() + "%'";
			}
			List<?> apNameList = QueryUtil.executeNativeQuery(findApNameSql);
			if (apNameList.size()<1) {
				reportResult = null;
				return;
			} else {
				String strApNameSql = "";
				int k=0;
				for(Object tmpApName:apNameList){
					strApNameSql = strApNameSql + "'" + NmsUtil.convertSqlStr(tmpApName.toString()) + "'";
					k++;
					if (k!=apNameList.size()){
						strApNameSql = strApNameSql + ",";
					}
				}
				tmpSql.append(" and bo.apName in(").append(strApNameSql).append(")");
			}
		} else {
			tmpSql.append(" and lower(bo.apName) like '%").append(getDataSource().getApNameForSQL()).append("%'");
		}
		
		if (getDataSource().getInterfaceRole()== AhCustomReport.REPORT_INTERFACE_WIFI0){
			tmpSql.append(" and lower(xif.ifname) = 'wifi0'");
		}
		if (getDataSource().getInterfaceRole()== AhCustomReport.REPORT_INTERFACE_WIFI1){
			tmpSql.append(" and lower(xif.ifname) = 'wifi1'");
		}

		tmpSql.append(" and bo.stattimestamp >=").append(reportTimeMill);
		tmpSql.append(" order by bo.apName,bo.stattimestamp");
		List<?> initReportResult = QueryUtil.executeNativeQuery(tmpSql.toString(), 100000);

		int colCount = getDataSource().getCustomFields().size()+1;
		Object[] oneRecord = new Object[colCount-1];
		Object[] temWifi0Report = new Object[colCount];
		Object[] temWifi1Report = new Object[colCount];
		
		
		int initSize=0;
		for(Object tmpObj:initReportResult){
			initSize++;
			Object[] oneRow = (Object[])tmpObj;
			
			if (temWifi0Report[0]==null && oneRow[colCount-1].toString().equalsIgnoreCase("wifi0")) {
				temWifi0Report = oneRow;
				continue;
			}
			if (temWifi1Report[0]==null && oneRow[colCount-1].toString().equalsIgnoreCase("wifi1")) {
				temWifi1Report = oneRow;
				continue;
			}
			if (oneRow[colCount-1].toString().equalsIgnoreCase("wifi0") &&
					oneRow[0].toString().equalsIgnoreCase(temWifi0Report[0].toString())){
				for(int i=0;i<colCount-1; i++ ){
					if (i==0 || i==1){
						oneRecord[i] = oneRow[i].toString();
					} else if (i==2){
						oneRecord[i] = AhDateTimeUtil.getSpecifyDateTimeReport(
								Long.parseLong(temWifi0Report[i].toString()),tz) + "|" 
								+ AhDateTimeUtil.getSpecifyDateTimeReport(
										Long.parseLong(oneRow[i].toString()),tz);
					} else {
						oneRecord[i] = compareValue(oneRow[i],temWifi0Report[i]);
					}
				}
				if (reportResult==null){
					reportResult = new ArrayList<Object[]>(); 
					for(int i=3;i<oneRecord.length; i++ ){
						oneRecord[i] = df.format(Double.parseDouble(oneRecord[i].toString()));
					}
					reportResult.add(oneRecord);
				} else if (reportResult.size()<1){
					for(int i=3;i<oneRecord.length; i++ ){
						oneRecord[i] = df.format(Double.parseDouble(oneRecord[i].toString()));
					}
					reportResult.add(oneRecord);
				} else {
					Object[] lastAddRow = reportResult.get(reportResult.size()-1);
					if (lastAddRow[0].toString().equalsIgnoreCase(oneRecord[0].toString())&&
							lastAddRow[1].toString().equalsIgnoreCase(oneRecord[1].toString())&&
							lastAddRow[2].toString().equalsIgnoreCase(oneRecord[2].toString())){
						for(int i=3;i<colCount-1; i++ ){
							lastAddRow[i] = df.format(Double.parseDouble(lastAddRow[i].toString()) 
							+ Double.parseDouble(oneRecord[i].toString()));
						}
					} else {
						for(int i=3;i<oneRecord.length; i++ ){
							oneRecord[i] = df.format(Double.parseDouble(oneRecord[i].toString()));
						}
						reportResult.add(oneRecord);
					}
				}

				oneRecord = new Object[colCount-1];
				temWifi0Report = oneRow;
			} else if (oneRow[colCount-1].toString().equalsIgnoreCase("wifi1") &&
				oneRow[0].toString().equalsIgnoreCase(temWifi1Report[0].toString())){
				for(int i=0;i<colCount-1; i++ ){
					if (i==0 || i==1){
						oneRecord[i] = (oneRow[i].toString());
					} else if (i==2){
						oneRecord[i] = AhDateTimeUtil.getSpecifyDateTimeReport(
								Long.parseLong(temWifi1Report[i].toString()),tz) + "|" 
								+ AhDateTimeUtil.getSpecifyDateTimeReport(
										Long.parseLong(oneRow[i].toString()),tz);
					} else {
						oneRecord[i] = compareValue(oneRow[i],temWifi1Report[i]);
					}
				}
				if (reportResult==null){
					reportResult = new ArrayList<Object[]>(); 
					for(int i=3;i<oneRecord.length; i++ ){
						oneRecord[i] = df.format(Double.parseDouble(oneRecord[i].toString()));
					}
					reportResult.add(oneRecord);
				} else if (reportResult.size()<1){
					for(int i=3;i<oneRecord.length; i++ ){
						oneRecord[i] = df.format(Double.parseDouble(oneRecord[i].toString()));
					}
					reportResult.add(oneRecord);
				} else {
					Object[] lastAddRow = reportResult.get(reportResult.size()-1);
					if (lastAddRow[0].toString().equalsIgnoreCase(oneRecord[0].toString())&&
							lastAddRow[1].toString().equalsIgnoreCase(oneRecord[1].toString())&&
							lastAddRow[2].toString().equalsIgnoreCase(oneRecord[2].toString())){
						for(int i=3;i<colCount-1; i++ ){
							lastAddRow[i] = df.format(Double.parseDouble(lastAddRow[i].toString()) 
							+ Double.parseDouble(oneRecord[i].toString()));
						}
					} else {
						for(int i=3;i<oneRecord.length; i++ ){
							oneRecord[i] = df.format(Double.parseDouble(oneRecord[i].toString()));
						}
						reportResult.add(oneRecord);
					}
				}
				oneRecord = new Object[colCount-1];
				temWifi1Report = oneRow;
			} else {
				if (oneRow[colCount-1].toString().equalsIgnoreCase("wifi0")){
					temWifi0Report = oneRow;
					temWifi1Report =  new Object[colCount];
				} else {
					temWifi0Report = new Object[colCount];
					temWifi1Report = oneRow;
				}
			}
		}

		if (reportResult!=null && getDataSource().getLongSortBy()>0){
			boolean orderByDesc = false;
			int dataType = DATA_TYPE_DOUBLE;
			if (getDataSource().getSortByType()==AhCustomReport.REPORT_SORTBY_TYPE_DESC) {
				orderByDesc = true;
			}
			int sortIndex = 0;
			for(AhCustomReportField fieldRow: getDataSource().getCustomFields()){
				if (getDataSource().getLongSortBy().equals(fieldRow.getId())) {
					if (fieldRow.getTableField().equalsIgnoreCase("apName")|| 
							fieldRow.getTableField().equalsIgnoreCase("apMac")){
						dataType = DATA_TYPE_STRING;
					} else if (fieldRow.getTableField().equalsIgnoreCase("statTimeStamp")){
						dataType = DATA_TYPE_DATE;
					} else {
						dataType = DATA_TYPE_DOUBLE;
					}
					break;
				}
				sortIndex++;
			}
			Collections.sort(reportResult, new SortReportComparator(sortIndex,orderByDesc,dataType));
		}
	}
	public void searchReportClientUniqueData(){
		
		StringBuffer tmpSqlSelect = new StringBuffer();
		long reportTimeMill = getReportDateTime().getTimeInMillis();
		
		tmpSqlSelect.append("select DISTINCT ON (clientmac) ");
		for(int i=0;i<getDataSource().getCustomFields().size();i++){
			tmpSqlSelect.append(getDataSource().getCustomFields().get(i).getTableField()).append(", ");
		}
		tmpSqlSelect.append("startTimeStamp");
		
		StringBuffer tmpSqlCondition = new StringBuffer();
		tmpSqlCondition.append(" where owner=").append(getDomain().getId());
		if (getDataSource().getLocation() != null) {
			String findApNameSql = "select hostName from HIVE_AP where owner=" + getDomain().getId() + 
						" and map_container_id=" +  getDataSource().getLocation().getId();
			if (getDataSource().getApName()!= null && !getDataSource().getApName().trim().equals("")){
				findApNameSql = findApNameSql + " and lower(hostName) like '%" + getDataSource().getApNameForSQL() + "%'";
			}
			List<?> apNameList = QueryUtil.executeNativeQuery(findApNameSql);
			if (apNameList.size()<1) {
				reportResult = null;
				return;
			} else {
				String strApNameSql = "";
				int k=0;
				for(Object tmpApName:apNameList){
					strApNameSql = strApNameSql + "'" + NmsUtil.convertSqlStr(tmpApName.toString()) + "'";
					k++;
					if (k!=apNameList.size()){
						strApNameSql = strApNameSql + ",";
					}
				}
				tmpSqlCondition.append(" and apName in(").append(strApNameSql).append(")");
			}
		} else {
			if (!getDataSource().getApName().trim().equals("")){
				tmpSqlCondition.append(" and lower(apName) like '%").append(getDataSource().getApNameForSQL()).append("%'");
			}
		}
		if (!getDataSource().getAuthMac().trim().equals("")){
			tmpSqlCondition.append(" and lower(clientMac) like '%").append(getDataSource().getClientMacForSQL()).append("%'");
		}

		if (!getDataSource().getAuthHostName().trim().equals("")){
			tmpSqlCondition.append(" and lower(clientHostname) like '%").append(getDataSource().getHostNameForSQL()).append("%'");
		}
		if (!getDataSource().getAuthUserName().trim().equals("")){
			tmpSqlCondition.append(" and lower(clientUsername) like '%").append(getDataSource().getUserNameForSQL()).append("%'");
		}
		if (!getDataSource().getAuthIp().trim().equals("")){
			tmpSqlCondition.append(" and lower(clientIP) like '%").append(getDataSource().getAuthIpForSQL()).append("%'");
		}
		
		StringBuffer tmpSql = new StringBuffer();
		tmpSql.append(tmpSqlSelect.toString());
		tmpSql.append(" from AH_CLIENTSESSION_HISTORY ");
		tmpSql.append(tmpSqlCondition.toString());
		tmpSql.append(" and endTimeStamp >=").append(reportTimeMill);
		tmpSql.append(" order by clientmac, startTimeStamp desc");

		List<?> initReportResult = QueryUtil.executeNativeQuery(tmpSql.toString(), 100000);
		
		if (initReportResult!=null && !initReportResult.isEmpty()){
			reportResult = new ArrayList<Object[]>(); 
			for(Object tmpResult:initReportResult){
				Object[] oneResult = (Object[])tmpResult;
				Object[] saveResult = new Object[oneResult.length-1];
				System.arraycopy(oneResult, 0, saveResult, 0, oneResult.length - 1);
				reportResult.add(saveResult);
			}
		}
		
		if (reportResult!=null && getDataSource().getLongSortBy()>0){
			boolean orderByDesc = false;
			int dataType;
			if (getDataSource().getSortByType()==AhCustomReport.REPORT_SORTBY_TYPE_DESC) {
				orderByDesc = true;
			}
			int sortIndex = 0;
			for(AhCustomReportField fieldRow: getDataSource().getCustomFields()){
				if (getDataSource().getLongSortBy().equals(fieldRow.getId())) {
					break;
				}
				sortIndex++;
			}
			if (sortIndex==0 || sortIndex ==1 || sortIndex ==2){
				dataType = DATA_TYPE_STRING;
			} else {
				dataType = DATA_TYPE_DOUBLE;
			}
			Collections.sort(reportResult, new SortReportComparator(sortIndex,orderByDesc,dataType));
		}
		
		for(int i=0;i<getDataSource().getCustomFields().size();i++){
			if (reportResult!=null){
				for(Object arrays:reportResult){
					Object[] values = (Object[])arrays;
					if (getDataSource().getCustomFields().get(i).getTableField().equalsIgnoreCase("clientMACProtocol")){
						values[i] = getRadioModeString(Integer.parseInt(values[i].toString()));
					} else if (getDataSource().getCustomFields().get(i).getTableField().equalsIgnoreCase("clientAuthMethod")){
						values[i] = getAuthMethodString(Integer.parseInt(values[i].toString()));
					} else if (getDataSource().getCustomFields().get(i).getTableField().equalsIgnoreCase("clientEncryptionMethod")){
						values[i] = getEncryptionString(Integer.parseInt(values[i].toString()));
					} else if (getDataSource().getCustomFields().get(i).getTableField().equalsIgnoreCase("clientCWPUsed")){
						values[i] = getCwpUsedString(Integer.parseInt(values[i].toString()));
					} else if (getDataSource().getCustomFields().get(i).getTableField().equalsIgnoreCase("clientVLAN")){
						values[i] = getVlanString(Integer.parseInt(values[i].toString()));
					}
				}
			}
		}
	}
	
	public String getVlanString(int value){
		if (value==0) {
			return "-";
		}
		return String.valueOf(value);
	}
	
	public String getRadioModeString(int value){
		switch (value) {
		case AhAssociation.CLIENTMACPROTOCOL_AMODE:
		case AhAssociation.CLIENTMACPROTOCOL_BMODE:
		case AhAssociation.CLIENTMACPROTOCOL_GMODE:
		case AhAssociation.CLIENTMACPROTOCOL_NAMODE:
		case AhAssociation.CLIENTMACPROTOCOL_NGMODE:
		case AhAssociation.CLIENTMACPROTOCOL_ACMODE:
		case AhAssociation.CLIENT_MAC_PROTOCOL_8023MODE:
			return MgrUtil.getEnumString("enum.snmp.association.macProtocol." + value);
		default:
			return "Unknown";
		}
	}
	public String getAuthMethodString(int value){
		switch (value) {
		case AhAssociation.CLIENTAUTHMETHOD_OPEN:
		case AhAssociation.CLIENTAUTHMETHOD_WEPOPEN:
		case AhAssociation.CLIENTAUTHMETHOD_WEPSHARED:
		case AhAssociation.CLIENTAUTHMETHOD_WPAPSK:
		case AhAssociation.CLIENTAUTHMETHOD_WPA2PSK:
		case AhAssociation.CLIENTAUTHMETHOD_WPA8021X:
		case AhAssociation.CLIENTAUTHMETHOD_WPA28021X:
		case AhAssociation.CLIENTAUTHMETHOD_WPAAUTOPSK:
		case AhAssociation.CLIENTAUTHMETHOD_WPAAUTO8021X:
		case AhAssociation.CLIENTAUTHMETHOD_DYNAMICWEP:
		case AhAssociation.CLIENTAUTHMETHOD_8021X:
			return MgrUtil
					.getEnumString("enum.snmp.association.authentication." + value);
		default:
			return "Unknown";
		}
	}
	public String getEncryptionString(int value){
		switch (value) {
		case AhAssociation.CLIENTENCRYMETHOD_AES:
		case AhAssociation.CLIENTENCRYMETHOD_TKIP:
		case AhAssociation.CLIENTENCRYMETHOD_WEP:
		case AhAssociation.CLIENTENCRYMETHOD_NON:
			return MgrUtil.getEnumString("enum.snmp.association.encryption."
					+ value);
		default:
			return "Unknown";
		}
	}
	public String getCwpUsedString(int value){
		if (value == 0) {
			return "";
		} else {
			return MgrUtil.getEnumString("enum.snmp.association.cwp." + value);
		}
	}
	
	public String getLinkUpTimeString(int value) {
		return NmsUtil.transformTime(value);
	}
	
	public void searchReportClientCountDataForVendor(){
		long reportTimeMill = getReportDateTime().getTimeInMillis();
		int paramCount = 1;
		List<Object> lstCondition = new ArrayList<Object>();
		
		StringBuffer filterSql = new StringBuffer();
		filterSql.append("bo.endTimeStamp>=:s").append(paramCount);
		lstCondition.add(reportTimeMill);
		if (!getDataSource().getApName().trim().equals("")){
			paramCount++;
			filterSql.append(" and lower(bo.apName) like :s").append(paramCount);
			lstCondition.add(getDataSource().getApName().replace("\\", "\\\\"));
		}
		if (getDataSource().getLocation() != null) {
			paramCount++;
			filterSql.append(" and bo.mapId=:s").append(paramCount);
			lstCondition.add(getDataSource().getLocation().getId());
		}
		if (!getDataSource().getAuthMac().trim().equals("")){
			paramCount++;
			filterSql.append(" and lower(bo.clientMac) like :s").append(paramCount);
			lstCondition.add(getDataSource().getAuthMac().replace("\\", "\\\\"));
		}
		if (!getDataSource().getAuthHostName().trim().equals("")){
			paramCount++;
			filterSql.append(" and lower(bo.clientHostname) like :s").append(+paramCount);
			lstCondition.add(getDataSource().getAuthHostName().replace("\\", "\\\\"));
		}
		if (!getDataSource().getAuthUserName().trim().equals("")){
			paramCount++;
			filterSql.append(" and lower(bo.clientUsername) like :s").append(+paramCount);
			lstCondition.add(getDataSource().getAuthUserName().replace("\\", "\\\\"));
		}
		if (!getDataSource().getAuthIp().trim().equals("")){
			paramCount++;
			filterSql.append(" and lower(bo.clientIP) like :s").append(+paramCount);
			lstCondition.add(getDataSource().getAuthIp().replace("\\", "\\\\"));
		}
		
		Object[] filterObj= new Object[lstCondition.size()];
		for(int i=0; i< lstCondition.size(); i++){
			filterObj[i] = lstCondition.get(i);
		}
		
		FilterParams myFilterParams = new FilterParams(filterSql.toString(),filterObj);
		List<?> profiles = QueryUtil.executeQuery("select DISTINCT bo.clientMac from "
				+ AhClientSessionHistory.class.getSimpleName() + " bo", null, myFilterParams,
				getDomain().getId());
		
//		paramCount = 1;
//		lstCondition.clear();
//		filterSql = new StringBuffer();
//		filterSql.append(" lower(bo.apName) like :s").append(paramCount);
//		lstCondition.add(getDataSource().getApName().replace("\\", "\\\\"));
//
//		if (getDataSource().getLocation() != null) {
//			paramCount++;
//			filterSql.append(" and bo.mapId=:s").append(paramCount);
//			lstCondition.add(getDataSource().getLocation().getId());
//		}
//		if (!getDataSource().getAuthMac().trim().equals("")){
//			paramCount++;
//			filterSql.append(" and lower(bo.clientMac) like :s").append(paramCount);
//			lstCondition.add(getDataSource().getAuthMac().replace("\\", "\\\\"));
//		}
//		if (!getDataSource().getAuthHostName().trim().equals("")){
//			paramCount++;
//			filterSql.append(" and lower(bo.clientHostname) like :s").append(+paramCount);
//			lstCondition.add(getDataSource().getAuthHostName().replace("\\", "\\\\"));
//		}
//		if (!getDataSource().getAuthUserName().trim().equals("")){
//			paramCount++;
//			filterSql.append(" and lower(bo.clientUsername) like :s").append(+paramCount);
//			lstCondition.add(getDataSource().getAuthUserName().replace("\\", "\\\\"));
//		}
//		if (!getDataSource().getAuthIp().trim().equals("")){
//			paramCount++;
//			filterSql.append(" and lower(bo.clientIP) like :s").append(+paramCount);
//			lstCondition.add(getDataSource().getAuthIp().replace("\\", "\\\\"));
//		}
//		
//		filterObj= new Object[lstCondition.size()];
//		for(int i=0; i< lstCondition.size(); i++){
//			filterObj[i] = lstCondition.get(i);
//		}
//		List<?> profilesActiveClient = QueryUtil.executeQuery("select DISTINCT bo.clientMac from "
//				+ AhClientSession.class.getSimpleName() + " bo", null, myFilterParams,
//				getDomain().getId());
		
		paramCount = 1;
		lstCondition.clear();
		filterSql = new StringBuffer();
		filterSql.append(" lower(apName) like ?");
		lstCondition.add(getDataSource().getApName().replace("\\", "\\\\"));

		if (getDataSource().getLocation() != null) {
			paramCount++;
			filterSql.append(" and mapId=?");
			lstCondition.add(getDataSource().getLocation().getId());
		}
		if (!getDataSource().getAuthMac().trim().equals("")){
			paramCount++;
			filterSql.append(" and lower(clientMac) like ?");
			lstCondition.add(getDataSource().getAuthMac().replace("\\", "\\\\"));
		}
		if (!getDataSource().getAuthHostName().trim().equals("")){
			paramCount++;
			filterSql.append(" and lower(clientHostname) like ?");
			lstCondition.add(getDataSource().getAuthHostName().replace("\\", "\\\\"));
		}
		if (!getDataSource().getAuthUserName().trim().equals("")){
			paramCount++;
			filterSql.append(" and lower(clientUsername) like ?");
			lstCondition.add(getDataSource().getAuthUserName().replace("\\", "\\\\"));
		}
		if (!getDataSource().getAuthIp().trim().equals("")){
			paramCount++;
			filterSql.append(" and lower(clientIP) like ?");
			lstCondition.add(getDataSource().getAuthIp().replace("\\", "\\\\"));
		}
		
		filterObj= new Object[lstCondition.size()];
		for(int i=0; i< lstCondition.size(); i++){
			filterObj[i] = lstCondition.get(i);
		}
		List<?> profilesActiveClient = DBOperationUtil.executeQuery("select DISTINCT clientMac from ah_clientsession"
				, null, new FilterParams(filterSql.toString(),filterObj),
				getDomain().getId());

		Map<String, Integer> clientVendorCount = new HashMap<String, Integer>();

		Set<String> clientMacSet = new HashSet<String>();
		for (Object profile : profiles) {
			clientMacSet.add(profile.toString());
		}
		for (Object activeClient : profilesActiveClient) {
			clientMacSet.add(activeClient.toString());
		}

		for (String tempMacAll: clientMacSet) {
			String tempMac = tempMacAll.substring(0, 6).toUpperCase();
			String macVendor = AhConstantUtil.getMacOuiComName(tempMac) == null ? tempMac : 
				AhConstantUtil.getMacOuiComName(tempMac);
			if (clientVendorCount.get(macVendor) == null) {
				clientVendorCount.put(macVendor, 1);
			} else {
				clientVendorCount.put(macVendor, clientVendorCount.get(macVendor) + 1);
			}
		}
		reportResult = new ArrayList<Object[]>(); 
		for (String clientMac : clientVendorCount.keySet()) {
			Object[] oneRecord = new Object[2];
			oneRecord[0] = clientMac;
			oneRecord[1] = clientVendorCount.get(clientMac).longValue();
			reportResult.add(oneRecord);
		}

		boolean orderByDesc = false;
		if (getDataSource().getSortByType()==AhCustomReport.REPORT_SORTBY_TYPE_DESC) {
			orderByDesc = true;
		}
		Collections.sort(reportResult, new SortReportComparator(1,orderByDesc,DATA_TYPE_DOUBLE));
	}
	
	public void searchReportClientCountData(int detailType){
		DecimalFormat df = new DecimalFormat("0");
		long reportTimeMill = getReportDateTime().getTimeInMillis();
		boolean clientFailure = false;
		boolean bandWidthFlg = false;
		StringBuffer tmpSql = new StringBuffer();
		tmpSql.append("select ");
		for(int i=0;i<getDataSource().getCustomFields().size();i++){
			if (!getDataSource().getCustomFields().get(i).getTableField().equalsIgnoreCase("bandWidthSentinelStatus") &&
					!getDataSource().getCustomFields().get(i).getTableField().equalsIgnoreCase("code")){
				tmpSql.append(getDataSource().getCustomFields().get(i).getTableField());
			} else if (getDataSource().getCustomFields().get(i).getTableField().equalsIgnoreCase("bandWidthSentinelStatus")){
				tmpSql.append("0");
				bandWidthFlg = true;
			} else {
				tmpSql.append("0");
				clientFailure = true;
			}
			if (i!= getDataSource().getCustomFields().size()-1){
				tmpSql.append(",");
			}
		}

		tmpSql.append(" from HM_ASSOCIATION where owner=").append(getDomain().getId());
		
		if (getDataSource().getLocation() != null) {
			String findApNameSql = "select hostName from HIVE_AP where owner=" + getDomain().getId() + 
						" and map_container_id=" +  getDataSource().getLocation().getId();
			if (getDataSource().getApName()!= null && !getDataSource().getApName().trim().equals("")){
				findApNameSql = findApNameSql + " and lower(hostName) like '%" + getDataSource().getApNameForSQL() + "%'";
			}
			List<?> apNameList = QueryUtil.executeNativeQuery(findApNameSql);
			if (apNameList.size()<1) {
				reportResult = null;
				return;
			} else {
				String strApNameSql = "";
				int k=0;
				for(Object tmpApName:apNameList){
					strApNameSql = strApNameSql + "'" + NmsUtil.convertSqlStr(tmpApName.toString()) + "'";
					k++;
					if (k!=apNameList.size()){
						strApNameSql = strApNameSql + ",";
					}
				}
				tmpSql.append(" and apName in(").append(strApNameSql).append(")");
			}
		} else {
			if (!getDataSource().getApName().trim().equals("")){
				tmpSql.append(" and lower(apName) like '%").append(getDataSource().getApNameForSQL()).append("%'");
			}
		}
		if (!getDataSource().getAuthMac().trim().equals("")){
			tmpSql.append(" and lower(clientMac) like '%").append(getDataSource().getClientMacForSQL()).append("%'");
		}
		tmpSql.append(" and time >=").append(reportTimeMill);
		
		if (!getDataSource().getAuthHostName().trim().equals("")){
			tmpSql.append(" and lower(clientHostname) like '%").append(getDataSource().getHostNameForSQL()).append("%'");
		}
		if (!getDataSource().getAuthUserName().trim().equals("")){
			tmpSql.append(" and lower(clientUsername) like '%").append(getDataSource().getUserNameForSQL()).append("%'");
		}
		if (!getDataSource().getAuthIp().trim().equals("")){
			tmpSql.append(" and lower(clientIP) like '%").append(getDataSource().getAuthIpForSQL()).append("%'");
		}
		
		tmpSql.append(" order by clientmac, time");
		List<?> initReportResult = QueryUtil.executeNativeQuery(tmpSql.toString(),100000);
		
		Map<String,Integer> mapSlaApNameCount = new HashMap<String,Integer>();
		if (bandWidthFlg){
			String sqlSla = "select clientMac,count(clientMac) from hm_bandwidthsentinel_history where owner=" + getDomain().getId(); 
			sqlSla = sqlSla + " and lower(clientMac) like '%" + getDataSource().getClientMacForSQL() + "%'";
			sqlSla = sqlSla + " and time >=" + reportTimeMill;
			sqlSla = sqlSla + " and (bandwidthsentinelstatus =" + AhBandWidthSentinelHistory.STATUS_BAD;
			sqlSla = sqlSla + " or bandwidthsentinelstatus =" + AhBandWidthSentinelHistory.STATUS_ALERT + ")";
			sqlSla = sqlSla + " group by clientMac";
			List<?> initReportResultSLA = QueryUtil.executeNativeQuery(sqlSla);
			if (initReportResultSLA!=null && initReportResultSLA.size()>0){
				for(Object obj:initReportResultSLA){
					Object[] oneSlaItem = (Object[])obj;
					mapSlaApNameCount.put(oneSlaItem[0].toString(), Integer.parseInt(oneSlaItem[1].toString()));
				}
			}
		}
		
		Map<String,Integer> mapClientFailureCount = new HashMap<String,Integer>();
		if (clientFailure){
			String sqlFailures = "select remoteId, count(remoteId) from AH_EVENT";
			sqlFailures = sqlFailures + " where time >=" + reportTimeMill;
			sqlFailures = sqlFailures + " and lower(remoteId) like '%" + getDataSource().getClientMacForSQL() + "%'";
			sqlFailures = sqlFailures + " and eventType =" + AhEvent.AH_EVENT_TYPE_CONNECTION_CHANGE;
			sqlFailures = sqlFailures + " and objectType =" + AhEvent.AH_OBJECT_TYPE_CLIENT_LINK;
			sqlFailures = sqlFailures + " and currentState =" + AhEvent.AH_STATE_DOWN;
			sqlFailures = sqlFailures + " and code =" + CLIENT_DE_AUTH_CODE;
			sqlFailures = sqlFailures + " and owner =" + getDomain().getId(); 
			sqlFailures = sqlFailures + " group by remoteId";
			
			List<?> initReportFailure = QueryUtil.executeNativeQuery(sqlFailures);
			if (initReportFailure!=null && initReportFailure.size()>0){
				for(Object obj:initReportFailure){
					Object[] oneSlaItem = (Object[])obj;
					mapClientFailureCount.put(oneSlaItem[0].toString(), Integer.parseInt(oneSlaItem[1].toString()));
				}
			}
		}
		int colCount = getDataSource().getCustomFields().size();
		Object[] oneRecord = new Object[colCount];
		Object[] tempOneRecord = new Object[colCount];
		
		int initSize=0;
		int devideCount = 1;
		for(Object tmpObj:initReportResult){
			initSize++;
			Object[] oneRow = (Object[])tmpObj;
			if (tempOneRecord[0]==null) {
				tempOneRecord = oneRow;
				if (oneRecord[0] == null) {
					oneRecord[0] = oneRow[0].toString();
					oneRecord[1] = oneRow[1].toString();
					oneRecord[2] = oneRow[2].toString();
					for(int i=3;i<colCount; i++ ){
						oneRecord[i] = 0;
					}
				}
				continue;
			}

			if (oneRow[0].toString().equalsIgnoreCase(tempOneRecord[0].toString())){
				if ((oneRecord[1].toString().trim().equals("") && !oneRow[1].toString().trim().equals(""))||
						(!oneRow[1].toString().trim().equals("") && 
						!oneRecord[1].toString().trim().equals(oneRow[1].toString().trim()))){
					oneRecord[1]=oneRow[1].toString();
				}
				if ((oneRecord[2].toString().trim().equals("") && !oneRow[2].toString().trim().equals(""))||
						(!oneRow[2].toString().trim().equals("") && 
						!oneRecord[2].toString().trim().equals(oneRow[2].toString().trim()))){
					oneRecord[2]=oneRow[2].toString();
				}
				for(int i=3;i<colCount; i++ ){
					oneRecord[i] = Double.parseDouble(oneRecord[i].toString()) + compareValue(oneRow[i],tempOneRecord[i]);
				}
				devideCount++;
				tempOneRecord = oneRow;
			} else {
				if (reportResult==null){
					reportResult = new ArrayList<Object[]>(); 
				}
				int slaIndex =3;
				if (clientFailure) {
					slaIndex = 4;
					oneRecord[3] = mapClientFailureCount.get(oneRecord[0].toString()) == null
						?0:mapClientFailureCount.get(oneRecord[0].toString());
				}
				if (bandWidthFlg){
					oneRecord[slaIndex] = mapSlaApNameCount.get(oneRecord[0].toString()) == null
						?0:mapSlaApNameCount.get(oneRecord[0].toString());
				}
				
				if (detailType == AhCustomReport.REPORT_DETAILTYPE_AVERAGE){
					if (devideCount >1) {
						devideCount = devideCount-1;
					}
					for(int k=3; k < oneRecord.length; k++) {
						oneRecord[k] = df.format(Double.parseDouble(oneRecord[k].toString())/devideCount);
					}
					reportResult.add(oneRecord);
				} else {
					for(int k=3; k < oneRecord.length; k++) {
						oneRecord[k] = df.format(Double.parseDouble(oneRecord[k].toString()));
					}
					reportResult.add(oneRecord);
				}
				
				tempOneRecord = oneRow;
				devideCount = 1;
				oneRecord = new Object[colCount];
				oneRecord[0] = oneRow[0].toString();
				oneRecord[1] = oneRow[1].toString();
				oneRecord[2] = oneRow[2].toString();
				for(int i=3;i<colCount; i++ ){
					oneRecord[i] = 0;
				}
			}
			if (initSize == initReportResult.size()){
				if (reportResult==null){
					reportResult = new ArrayList<Object[]>(); 
				}
				int slaIndex =3;
				if (clientFailure) {
					slaIndex = 4;
					oneRecord[3] = mapClientFailureCount.get(oneRecord[0].toString()) == null
						?0:mapClientFailureCount.get(oneRecord[0].toString());
				}
				if (bandWidthFlg){
					oneRecord[slaIndex] = mapSlaApNameCount.get(oneRecord[0].toString()) == null
						?0:mapSlaApNameCount.get(oneRecord[0].toString());
				}
				
				if (detailType == AhCustomReport.REPORT_DETAILTYPE_AVERAGE){
					if (devideCount >1) {
						devideCount = devideCount-1;
					}
					for(int k=3; k < oneRecord.length; k++) {
						oneRecord[k] = df.format(Double.parseDouble(oneRecord[k].toString())/devideCount);
					}
					reportResult.add(oneRecord);
				} else {
					for(int k=3; k < oneRecord.length; k++) {
						oneRecord[k] = df.format(Double.parseDouble(oneRecord[k].toString()));
					}
					reportResult.add(oneRecord);
				}
			}
		}
		
		if (reportResult!=null && getDataSource().getLongSortBy()>0){
			boolean orderByDesc = false;
			if (getDataSource().getSortByType()==AhCustomReport.REPORT_SORTBY_TYPE_DESC) {
				orderByDesc = true;
			}
			int sortIndex = 0;
			for(AhCustomReportField fieldRow: getDataSource().getCustomFields()){
				if (getDataSource().getLongSortBy().equals(fieldRow.getId())) {
					break;
				}
				sortIndex++;
			}
			Collections.sort(reportResult, new SortReportComparator(sortIndex,orderByDesc,DATA_TYPE_DOUBLE));
		}
	
	}
	public void searchReportClientValueData(){
		DecimalFormat df = new DecimalFormat("0");
		long reportTimeMill = getReportDateTime().getTimeInMillis();
		StringBuffer tmpSql = new StringBuffer();
		tmpSql.append("select ");
		for(int i=0;i<getDataSource().getCustomFields().size();i++){
			tmpSql.append(getDataSource().getCustomFields().get(i).getTableField());
			if (i!= getDataSource().getCustomFields().size()-1){
				tmpSql.append(",");
			}
		}

		tmpSql.append(" from HM_ASSOCIATION where owner=").append(getDomain().getId());
		
		if (getDataSource().getLocation() != null) {
			String findApNameSql = "select hostName from HIVE_AP where owner=" + getDomain().getId() + 
						" and map_container_id=" +  getDataSource().getLocation().getId();
			if (getDataSource().getApName()!= null && !getDataSource().getApName().trim().equals("")){
				findApNameSql = findApNameSql + " and lower(hostName) like '%" + getDataSource().getApNameForSQL() + "%'";
			}
			List<?> apNameList = QueryUtil.executeNativeQuery(findApNameSql);
			if (apNameList.size()<1) {
				reportResult = null;
				return;
			} else {
				String strApNameSql = "";
				int k=0;
				for(Object tmpApName:apNameList){
					strApNameSql = strApNameSql + "'" + NmsUtil.convertSqlStr(tmpApName.toString()) + "'";
					k++;
					if (k!=apNameList.size()){
						strApNameSql = strApNameSql + ",";
					}
				}
				tmpSql.append(" and apName in(").append(strApNameSql).append(")");
			}
		} else {
			if (!getDataSource().getApName().trim().equals("")){
				tmpSql.append(" and lower(apName) like '%").append(getDataSource().getApNameForSQL()).append("%'");
			}
		}
		if (!getDataSource().getAuthMac().trim().equals("")){
			tmpSql.append(" and lower(clientMac) like '%").append(getDataSource().getClientMacForSQL()).append("%'");
		}
		tmpSql.append(" and time >=").append(reportTimeMill);
		
		if (!getDataSource().getAuthHostName().trim().equals("")){
			tmpSql.append(" and lower(clientHostname) like '%").append(getDataSource().getHostNameForSQL()).append("%'");
		}
		if (!getDataSource().getAuthUserName().trim().equals("")){
			tmpSql.append(" and lower(clientUsername) like '%").append(getDataSource().getUserNameForSQL()).append("%'");
		}
		if (!getDataSource().getAuthIp().trim().equals("")){
			tmpSql.append(" and lower(clientIP) like '%").append(getDataSource().getAuthIpForSQL()).append("%'");
		}
		
		tmpSql.append(" order by clientmac, time");
		List<?> initReportResult = QueryUtil.executeNativeQuery(tmpSql.toString(),100000);
		
		int intTransLastIndex=-1;
		int intReceiveLastIndex=-1;
		int intDataBegin=-1;
		int sortIndex=0;
		for(AhCustomReportField fieldRow: getDataSource().getCustomFields()){
			if (fieldRow.getTableField().equalsIgnoreCase("clientLastTxRate")) {
				intTransLastIndex =sortIndex;
			}
			if (fieldRow.getTableField().equalsIgnoreCase("clientLastRxRate")) {
				intReceiveLastIndex =sortIndex;
			}
			if (fieldRow.getId() >=2318 && intDataBegin ==-1){
				intDataBegin = sortIndex;
			}
			sortIndex++;
		}
		
		
		int colCount = getDataSource().getCustomFields().size();
		Object[] oneRecord = new Object[colCount];
		Object[] tempReport = new Object[colCount];
		int initSize=0;
		
		for(Object tmpObj:initReportResult){
			initSize++;
			Object[] oneRow = (Object[])tmpObj;
			if (tempReport[0]==null) {
				tempReport = oneRow;
				continue;
			}

			if (oneRow[0].toString().equalsIgnoreCase(tempReport[0].toString())){
				for(int i=0;i<colCount; i++ ){
					if (i==0 || i==1 || i==2 ){
						oneRecord[i] = (oneRow[i].toString());
					} else if (i==3){
						oneRecord[i] = AhDateTimeUtil.getSpecifyDateTimeReport(
							Long.parseLong(tempReport[i].toString()),tz) + "|" + 
							AhDateTimeUtil.getSpecifyDateTimeReport(Long.parseLong(oneRow[i].toString()),tz);
					} else {
						if (intTransLastIndex!=-1 && i == intTransLastIndex){
							oneRecord[i] = Long.parseLong(oneRow[i].toString());
							continue;
						}
						if (intReceiveLastIndex!=-1 && i == intReceiveLastIndex){
							oneRecord[i] = Long.parseLong(oneRow[i].toString());
							continue;
						}
						if (i>=intDataBegin && intDataBegin != -1){
							oneRecord[i] = df.format(compareValue(oneRow[i],tempReport[i]));
						} else {
							oneRecord[i] = oneRow[i];
						}
					}
				}
				if (reportResult==null){
					reportResult = new ArrayList<Object[]>();
					reportResult.add(oneRecord);
				} else {
					reportResult.add(oneRecord);
				}

				oneRecord = new Object[colCount];
				tempReport = oneRow;
			} else {
				tempReport = oneRow;
			}
		}
		
		if (reportResult!=null && getDataSource().getLongSortBy()>0){
			boolean orderByDesc = false;
			int dataType = DATA_TYPE_DOUBLE;
			if (getDataSource().getSortByType()==AhCustomReport.REPORT_SORTBY_TYPE_DESC) {
				orderByDesc = true;
			}
			sortIndex = 0;
			
			for(AhCustomReportField fieldRow: getDataSource().getCustomFields()){
				if (getDataSource().getLongSortBy().equals(fieldRow.getId())) {
					if (fieldRow.getTableField().equalsIgnoreCase("clientMac")||
							fieldRow.getTableField().equalsIgnoreCase("clientHostname")||
							fieldRow.getTableField().equalsIgnoreCase("clientUsername")||
							fieldRow.getTableField().equalsIgnoreCase("clientIP")||
							fieldRow.getTableField().equalsIgnoreCase("apName")||
							fieldRow.getTableField().equalsIgnoreCase("clientSSID")||
							fieldRow.getTableField().equalsIgnoreCase("clientBSSID")){
						dataType = DATA_TYPE_STRING;
					} else if (fieldRow.getTableField().equalsIgnoreCase("time")){
							dataType = DATA_TYPE_DATE;
					} else {
						dataType = DATA_TYPE_DOUBLE;
					}
					break;
				}
				sortIndex++;
			}
			Collections.sort(reportResult, new SortReportComparator(sortIndex,orderByDesc,dataType));
		}
		
		for(int i=0;i<getDataSource().getCustomFields().size();i++){
			if (reportResult!=null){
				for(Object arrays:reportResult){
					Object[] values = (Object[])arrays;
					if (getDataSource().getCustomFields().get(i).getTableField().equalsIgnoreCase("clientMACProtocol")){
						values[i] = getRadioModeString(Integer.parseInt(values[i].toString()));
					} else if (getDataSource().getCustomFields().get(i).getTableField().equalsIgnoreCase("clientAuthMethod")){
						values[i] = getAuthMethodString(Integer.parseInt(values[i].toString()));
					} else if (getDataSource().getCustomFields().get(i).getTableField().equalsIgnoreCase("clientEncryptionMethod")){
						values[i] = getEncryptionString(Integer.parseInt(values[i].toString()));
					} else if (getDataSource().getCustomFields().get(i).getTableField().equalsIgnoreCase("clientCWPUsed")){
						values[i] = getCwpUsedString(Integer.parseInt(values[i].toString()));
					} else if (getDataSource().getCustomFields().get(i).getTableField().equalsIgnoreCase("clientLinkUptime")){
						values[i] = getLinkUpTimeString(Integer.parseInt(values[i].toString()));
					} else if (getDataSource().getCustomFields().get(i).getTableField().equalsIgnoreCase("clientVLAN")){
						values[i] = getVlanString(Integer.parseInt(values[i].toString()));
					}
				}
			}
		}
	}
	public void searchReportSsidCountData(int detailType){
		DecimalFormat df = new DecimalFormat("0");
		long reportTimeMill = getReportDateTime().getTimeInMillis();
		StringBuffer tmpSql = new StringBuffer();
		tmpSql.append("select ");
		for(int i=0;i<getDataSource().getCustomFields().size();i++){
			if (getDataSource().getCustomFields().get(i).getTableField().equalsIgnoreCase("ssidname")){
				tmpSql.append("xif.ssidname, ");
			} else {
				tmpSql.append("bo.").append(getDataSource().getCustomFields().get(i).getTableField()).append(", ");
			}
		}
		tmpSql.append(" xif.ifname ");

		tmpSql.append(" from HM_VIFSTATS bo, hm_xif xif where bo.owner=").append(getDomain().getId());
		tmpSql.append(" and xif.owner=").append(getDomain().getId());
		tmpSql.append(" and xif.ssidname!='N/A'");
		
		tmpSql.append(" and xif.apname=bo.apname and xif.ifindex=bo.ifindex and xif.stattimestamp=bo.stattimestamp");

		if (getDataSource().getLocation() != null) {
			String findApNameSql = "select hostName from HIVE_AP where owner=" + getDomain().getId() + 
						" and map_container_id=" +  getDataSource().getLocation().getId();
			if (getDataSource().getApName()!= null && !getDataSource().getApName().trim().equals("")){
				findApNameSql = findApNameSql + " and lower(hostName) like '%" + getDataSource().getApNameForSQL() + "%'";
			}
			List<?> apNameList = QueryUtil.executeNativeQuery(findApNameSql);
			if (apNameList.size()<1) {
				reportResult = null;
				return;
			} else {
				String strApNameSql = "";
				int k=0;
				for(Object tmpApName:apNameList){
					strApNameSql = strApNameSql + "'" + NmsUtil.convertSqlStr(tmpApName.toString()) + "'";
					k++;
					if (k!=apNameList.size()){
						strApNameSql = strApNameSql + ",";
					}
				}
				tmpSql.append(" and bo.apName in(").append(strApNameSql).append(")");
			}
		} else {
			if (!getDataSource().getApName().trim().equals("")){
				tmpSql.append(" and lower(bo.apName) like '%").append(getDataSource().getApNameForSQL()).append("%'");
			}
		}
		
		if (!getDataSource().getSsidName().trim().equals("")){
			tmpSql.append(" and lower(xif.ssidname) like '%").append(getDataSource().getSsidNameForSQL()).append("%'");
		}

		tmpSql.append(" and bo.stattimestamp >=").append(reportTimeMill);
		tmpSql.append(" order by bo.apName,xif.ssidname,bo.stattimestamp");
		List<?> initReportResult = QueryUtil.executeNativeQuery(tmpSql.toString(),100000);

		int colCount = getDataSource().getCustomFields().size()+1;
		Object[] oneRecord = new Object[colCount-1];
		Object[] temWifi0Report = new Object[colCount];
		Object[] temWifi1Report = new Object[colCount];
		
		int initSize=0;
		int devideWifi0Count = 0;
		int devideWifi1Count = 0;
		for(Object tmpObj:initReportResult){
			initSize++;
			Object[] oneRow = (Object[])tmpObj;
			
			if (temWifi0Report[0]==null && oneRow[colCount-1].toString().startsWith("wifi0")) {
				temWifi0Report = oneRow;
				if (oneRecord[0] == null) {
					oneRecord[0] = oneRow[0].toString();
					oneRecord[1] = oneRow[1].toString();
					for(int i=2;i<colCount-1; i++ ){
						oneRecord[i] = 0;
					}
				}
			}
			if (temWifi1Report[0]==null && oneRow[colCount-1].toString().startsWith("wifi1")) {
				temWifi1Report = oneRow;
				if (oneRecord[0] == null) {
					oneRecord[0] = oneRow[0].toString();
					oneRecord[1] = oneRow[1].toString();
					for(int i=2;i<colCount-1; i++ ){
						oneRecord[i] = 0;
					}
				}
			}
			if ((temWifi0Report[0]==null && temWifi0Report[1]==null && temWifi1Report[0]!=null && temWifi1Report[1]!=null
						&& temWifi1Report[0].toString().equalsIgnoreCase(oneRow[0].toString())
						&& temWifi1Report[1].toString().equalsIgnoreCase(oneRow[1].toString())) 
					|| (temWifi1Report[0]==null && temWifi1Report[1]==null && temWifi0Report[0]!=null && temWifi0Report[1]!=null
							&& temWifi0Report[0].toString().equalsIgnoreCase(oneRow[0].toString())
							&& temWifi0Report[1].toString().equalsIgnoreCase(oneRow[1].toString()))
					|| (temWifi0Report[0]!=null && temWifi0Report[1]!=null && temWifi1Report[0]!=null && temWifi1Report[1]!=null 
							&& temWifi0Report[0].toString().equalsIgnoreCase(temWifi1Report[0].toString())
							&& temWifi0Report[1].toString().equalsIgnoreCase(temWifi1Report[1].toString())
							&& temWifi0Report[0].toString().equalsIgnoreCase(oneRow[0].toString())
							&& temWifi0Report[1].toString().equalsIgnoreCase(oneRow[1].toString()))){
				if (oneRow[colCount-1].toString().startsWith("wifi0") &&
						oneRow[0].toString().equalsIgnoreCase(temWifi0Report[0].toString()) && 
						oneRow[1].toString().equalsIgnoreCase(temWifi0Report[1].toString())){
					for(int i=2;i<colCount-1; i++ ){
						oneRecord[i] = Double.parseDouble(oneRecord[i].toString()) + compareValue(oneRow[i],temWifi0Report[i]);
					}
					devideWifi0Count++;
					temWifi0Report = oneRow;
				} else if (oneRow[colCount-1].toString().startsWith("wifi1") &&
					oneRow[0].toString().equalsIgnoreCase(temWifi1Report[0].toString())&&
					oneRow[1].toString().equalsIgnoreCase(temWifi1Report[1].toString())){
					for(int i=2;i<colCount-1; i++ ){
						oneRecord[i] = Double.parseDouble(oneRecord[i].toString()) + compareValue(oneRow[i],temWifi1Report[i]);
					}
					devideWifi1Count++;
					temWifi1Report = oneRow;
				} 
			} else {
				if (reportResult==null){
					reportResult = new ArrayList<Object[]>(); 
				}
				
				if (detailType == AhCustomReport.REPORT_DETAILTYPE_AVERAGE){
					int deviceCount = devideWifi0Count>devideWifi1Count ? devideWifi0Count: devideWifi1Count;
					if (deviceCount ==0) {
						deviceCount = 1;
					}
					
					for(int k=2; k < oneRecord.length; k++) {
						oneRecord[k] = df.format(Double.parseDouble(oneRecord[k].toString())/deviceCount);
					}
					reportResult.add(oneRecord);
				} else {
					for(int k=2; k < oneRecord.length; k++) {
						oneRecord[k] = df.format(Double.parseDouble(oneRecord[k].toString()));
					}
					reportResult.add(oneRecord);
				}
				
				if (oneRow[colCount-1].toString().startsWith("wifi0")){
					temWifi0Report = oneRow;
					temWifi1Report =  new Object[colCount];
				} else {
					temWifi0Report = new Object[colCount];
					temWifi1Report = oneRow;
				}
				
				oneRecord = new Object[colCount-1];
				oneRecord[0] = oneRow[0].toString();
				oneRecord[1] = oneRow[1].toString();
				for(int i=2;i<colCount-1; i++ ){
					oneRecord[i] = 0;
				}
			}
			if (initSize == initReportResult.size()){
				if (reportResult==null){
					reportResult = new ArrayList<Object[]>(); 
				}
				if (detailType == AhCustomReport.REPORT_DETAILTYPE_AVERAGE){
					int deviceCount = devideWifi0Count>devideWifi1Count ? devideWifi0Count: devideWifi1Count;
					if (deviceCount ==0) {
						deviceCount = 1;
					}
					
					for(int k=2; k < oneRecord.length; k++) {
						oneRecord[k] = df.format(Double.parseDouble(oneRecord[k].toString())/deviceCount);
					}
					reportResult.add(oneRecord);
				} else {
					for(int k=2; k < oneRecord.length; k++) {
						oneRecord[k] = df.format(Double.parseDouble(oneRecord[k].toString()));
					}
					reportResult.add(oneRecord);
				}
			}
		}
		
		if (reportResult!=null && getDataSource().getLongSortBy()>0){
			boolean orderByDesc = false;
			if (getDataSource().getSortByType()==AhCustomReport.REPORT_SORTBY_TYPE_DESC) {
				orderByDesc = true;
			}
			int sortIndex = 0;
			int dataType=DATA_TYPE_DOUBLE;
			for(AhCustomReportField fieldRow: getDataSource().getCustomFields()){
				if (getDataSource().getLongSortBy().equals(fieldRow.getId())) {
					if (fieldRow.getTableField().trim().equalsIgnoreCase("ssidName") || 
							fieldRow.getTableField().trim().equalsIgnoreCase("apName")){
						dataType = DATA_TYPE_STRING;
					}
					break;
				}
				sortIndex++;
			}
			Collections.sort(reportResult, new SortReportComparator(sortIndex,orderByDesc,dataType));
		}
	
	}
	public void searchReportSsidValueData(){
		DecimalFormat df = new DecimalFormat("0");
		long reportTimeMill = getReportDateTime().getTimeInMillis();
		StringBuffer tmpSql = new StringBuffer();
		tmpSql.append("select ");
		for(int i=0;i<getDataSource().getCustomFields().size();i++){
			if (getDataSource().getCustomFields().get(i).getTableField().equalsIgnoreCase("ssidname")){
				tmpSql.append("xif.ssidname, ");
			} else {
				tmpSql.append("bo.").append(getDataSource().getCustomFields().get(i).getTableField()).append(", ");
			}
		}
		tmpSql.append(" xif.ifname ");

		tmpSql.append(" from HM_VIFSTATS bo, hm_xif xif where bo.owner=").append(getDomain().getId());
		tmpSql.append(" and xif.owner=").append(getDomain().getId());
		tmpSql.append(" and xif.ssidname!='N/A'");
		tmpSql.append(" and xif.apname=bo.apname and xif.ifindex=bo.ifindex and xif.stattimestamp=bo.stattimestamp");

		if (getDataSource().getLocation() != null) {
			String findApNameSql = "select hostName from HIVE_AP where owner=" + getDomain().getId() + 
						" and map_container_id=" +  getDataSource().getLocation().getId();
			if (getDataSource().getApName()!= null && !getDataSource().getApName().trim().equals("")){
				findApNameSql = findApNameSql + " and lower(hostName) like '%" + getDataSource().getApNameForSQL() + "%'";
			}
			List<?> apNameList = QueryUtil.executeNativeQuery(findApNameSql);
			if (apNameList.size()<1) {
				reportResult = null;
				return;
			} else {
				String strApNameSql = "";
				int k=0;
				for(Object tmpApName:apNameList){
					strApNameSql = strApNameSql + "'" + NmsUtil.convertSqlStr(tmpApName.toString()) + "'";
					k++;
					if (k!=apNameList.size()){
						strApNameSql = strApNameSql + ",";
					}
				}
				tmpSql.append(" and bo.apName in(").append(strApNameSql).append(")");
			}
		} else {
			if (!getDataSource().getApName().trim().equals("")){
				tmpSql.append(" and lower(bo.apName) like '%").append(getDataSource().getApNameForSQL()).append("%'");
			}
		}
		
		if (!getDataSource().getSsidName().trim().equals("")){
			tmpSql.append(" and lower(xif.ssidname) like '%").append(getDataSource().getSsidNameForSQL()).append("%'");
		}

		tmpSql.append(" and bo.stattimestamp >=").append(reportTimeMill);
		tmpSql.append(" order by bo.apName,xif.ssidname,bo.stattimestamp");
		List<?> initReportResult = QueryUtil.executeNativeQuery(tmpSql.toString(),100000);

		int colCount = getDataSource().getCustomFields().size()+1;
		Object[] oneRecord = new Object[colCount-1];
		Object[] temWifi0Report = new Object[colCount];
		Object[] temWifi1Report = new Object[colCount];
		
		int initSize=0;
		for(Object tmpObj:initReportResult){
			initSize++;
			Object[] oneRow = (Object[])tmpObj;
			
			if (temWifi0Report[0]==null && oneRow[colCount-1].toString().startsWith("wifi0")) {
				temWifi0Report = oneRow;
				continue;
			}
			if (temWifi1Report[0]==null && oneRow[colCount-1].toString().startsWith("wifi1")) {
				temWifi1Report = oneRow;
				continue;
			}
			if (oneRow[colCount-1].toString().startsWith("wifi0") &&
					oneRow[0].toString().equalsIgnoreCase(temWifi0Report[0].toString()) && 
					oneRow[1].toString().equalsIgnoreCase(temWifi0Report[1].toString())){
				for(int i=0;i<colCount-1; i++ ){
					if (i==0 || i==1){
						oneRecord[i] = oneRow[i].toString();
					} else if (i==2){
						oneRecord[i] = AhDateTimeUtil.getSpecifyDateTimeReport(
								Long.parseLong(temWifi0Report[i].toString()),tz) + "|" + 
								AhDateTimeUtil.getSpecifyDateTimeReport(Long.parseLong(oneRow[i].toString()),tz);
					} else {
						oneRecord[i] = compareValue(oneRow[i],temWifi0Report[i]);
					}
				}
				if (reportResult==null){
					reportResult = new ArrayList<Object[]>(); 
					for(int i=3;i<oneRecord.length; i++ ){
						oneRecord[i] = df.format(Double.parseDouble(oneRecord[i].toString()));
					}
					reportResult.add(oneRecord);
				} else if (reportResult.size()<1){
					for(int i=3;i<oneRecord.length; i++ ){
						oneRecord[i] = df.format(Double.parseDouble(oneRecord[i].toString()));
					}
					reportResult.add(oneRecord);
				} else {
					Object[] lastAddRow = reportResult.get(reportResult.size()-1);
					if (lastAddRow[0].toString().equalsIgnoreCase(oneRecord[0].toString())&&
							lastAddRow[1].toString().equalsIgnoreCase(oneRecord[1].toString())&&
							lastAddRow[2].toString().equalsIgnoreCase(oneRecord[2].toString())){
						for(int i=3;i<colCount-1; i++ ){
							lastAddRow[i] = df.format(Double.parseDouble(lastAddRow[i].toString()) 
							+ Double.parseDouble(oneRecord[i].toString()));
						}
					} else {
						for(int i=3;i<oneRecord.length; i++ ){
							oneRecord[i] = df.format(Double.parseDouble(oneRecord[i].toString()));
						}
						reportResult.add(oneRecord);
					}
				}

				oneRecord = new Object[colCount-1];
				temWifi0Report = oneRow;
			} else if (oneRow[colCount-1].toString().startsWith("wifi1") &&
				oneRow[0].toString().equalsIgnoreCase(temWifi1Report[0].toString())&&
				oneRow[1].toString().equalsIgnoreCase(temWifi1Report[1].toString())){
				for(int i=0;i<colCount-1; i++ ){
					if (i==0 || i==1){
						oneRecord[i] = oneRow[i].toString();
					} else if (i==2){
						oneRecord[i] = AhDateTimeUtil.getSpecifyDateTimeReport(
								Long.parseLong(temWifi1Report[i].toString()),tz) + "|" + 
								AhDateTimeUtil.getSpecifyDateTimeReport(Long.parseLong(oneRow[i].toString()),tz);
					} else {
						oneRecord[i] = compareValue(oneRow[i],temWifi1Report[i]);
					}
				}
				if (reportResult==null){
					reportResult = new ArrayList<Object[]>(); 
					for(int i=3;i<oneRecord.length; i++ ){
						oneRecord[i] = df.format(Double.parseDouble(oneRecord[i].toString()));
					}
					reportResult.add(oneRecord);
				} else if (reportResult.size()<1){
					for(int i=3;i<oneRecord.length; i++ ){
						oneRecord[i] = df.format(Double.parseDouble(oneRecord[i].toString()));
					}
					reportResult.add(oneRecord);
				} else {
					Object[] lastAddRow = reportResult.get(reportResult.size()-1);
					if (lastAddRow[0].toString().equalsIgnoreCase(oneRecord[0].toString())&&
							lastAddRow[1].toString().equalsIgnoreCase(oneRecord[1].toString())&&
							lastAddRow[2].toString().equalsIgnoreCase(oneRecord[2].toString())){
						for(int i=3;i<colCount-1; i++ ){
							lastAddRow[i] = df.format(Double.parseDouble(lastAddRow[i].toString()) 
							+ Double.parseDouble(oneRecord[i].toString()));
						}
					} else {
						for(int i=3;i<oneRecord.length; i++ ){
							oneRecord[i] = df.format(Double.parseDouble(oneRecord[i].toString()));
						}
						reportResult.add(oneRecord);
					}
				}
				oneRecord = new Object[colCount-1];
				temWifi1Report = oneRow;
			} else {
				if (oneRow[colCount-1].toString().startsWith("wifi0")){
					temWifi0Report = oneRow;
					temWifi1Report =  new Object[colCount];
				} else {
					temWifi0Report = new Object[colCount];
					temWifi1Report = oneRow;
				}
			}
		}

		if (reportResult!=null && getDataSource().getLongSortBy()>0){
			boolean orderByDesc = false;
			int dataType = DATA_TYPE_DOUBLE;
			if (getDataSource().getSortByType()==AhCustomReport.REPORT_SORTBY_TYPE_DESC) {
				orderByDesc = true;
			}
			int sortIndex = 0;
			for(AhCustomReportField fieldRow: getDataSource().getCustomFields()){
				if (getDataSource().getLongSortBy().equals(fieldRow.getId())) {
					if (fieldRow.getTableField().equalsIgnoreCase("ssidName")|| 
							fieldRow.getTableField().equalsIgnoreCase("apName")){
						dataType = DATA_TYPE_STRING;
					} else if (fieldRow.getTableField().equalsIgnoreCase("statTimeStamp")){
						dataType = DATA_TYPE_DATE;
					} else {
						dataType = DATA_TYPE_DOUBLE;
					}
					break;
				}
				sortIndex++;
			}
			Collections.sort(reportResult, new SortReportComparator(sortIndex,orderByDesc,dataType));
		}
	}
	
	public synchronized boolean generalVendorInfoCurrentCvsFile() {
		try {
			String currentFileDir = AhPerformanceScheduleModule.fileDirPathCurrent + File.separator
					+ getDomain().getDomainName();
			File tmpFileDir = new File(currentFileDir);
			if (!tmpFileDir.exists()) {
				tmpFileDir.mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(getInputPath());
			CSVPrinter printer = new CSVPrinter(fos);
			
			String[][] allValues;
			int totalRecordSize;
			if (reportResult==null){
				totalRecordSize = 1;
			} else {
				totalRecordSize=reportResult.size() + 1;
			}
			allValues = new String[totalRecordSize][2];
			// the title
			int i = -1;

			String[] title = new String[2];
			title[0] = getText("report.reportList.title.vendor");
			title[1] = getText("report.reportList.title.vendorCount");
			allValues[++i] = title;
			
			if (reportResult!= null && reportResult.size()>0){
				for (Object obj : reportResult) {
					Object[] objValue = (Object[])obj;
					title = new String[2];
					title[0] = objValue[0].toString();
					title[1] = objValue[1].toString();
					allValues[++i] = title;
				}
			}
			printer.writeln(allValues);
			fos.close();
			printer.close();
			return true;
		} catch (Exception ex) {
			addActionError(MgrUtil.getUserMessage(ex));
			generateAuditLog(HmAuditLog.STATUS_FAILURE, boClass.getSimpleName()
					+ " " + MgrUtil.getUserMessage(ex));
			return false;
		}
	}
	
	public synchronized boolean generalCurrentCvsFile() {
		try {
			String currentFileDir = AhPerformanceScheduleModule.fileDirPathCurrent + File.separator
					+ getDomain().getDomainName();
			File tmpFileDir = new File(currentFileDir);
			if (!tmpFileDir.exists()) {
				tmpFileDir.mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(getInputPath());
			CSVPrinter printer = new CSVPrinter(fos);
			
			String[][] allValues;
			int totalRecordSize;
			if (reportResult==null){
				totalRecordSize = 1;
			} else {
				totalRecordSize=reportResult.size() + 1;
			}
			allValues = new String[totalRecordSize][getDataSource().getCustomFields().size()];
			// the title
			int i = -1;
			int colCount=0;
			String[] title = new String[getDataSource().getCustomFields().size()];
			for(AhCustomReportField tmpItem: getDataSource().getCustomFields()){
				title[colCount] = tmpItem.getFieldString();
				colCount++;
			}
			allValues[++i] = title;
			if (reportResult!= null && reportResult.size()>0){
				for (Object obj : reportResult) {
					Object[] objValue = (Object[])obj;
					title = new String[getDataSource().getCustomFields().size()];
					for(int index=0; index <objValue.length; index++){
						title[index] = objValue[index].toString();
					}
					allValues[++i] = title;
				}
			}
			printer.writeln(allValues);
			fos.close();
			printer.close();
			return true;
		} catch (Exception ex) {
			addActionError(MgrUtil.getUserMessage(ex));
			generateAuditLog(HmAuditLog.STATUS_FAILURE, boClass.getSimpleName()
					+ " " + MgrUtil.getUserMessage(ex));
			return false;
		}
	}	
	
	public EnumItem[] getEnumReportType() {
		return AhCustomReport.REPORT_TYPE_ENUM;
	}
	
	public EnumItem[] getEnumReportDetailType() {
		if (getDataSource().getReportType() == AhCustomReport.REPORT_TYPE_SSID){
			return AhCustomReport.REPORT_DETAILTYPE_ENUM_SSID;
		}
		return AhCustomReport.REPORT_DETAILTYPE_ENUM;
	}
	
	public EnumItem[] getEnumSortByType() {
		return AhCustomReport.REPORT_SORTBY_TYPE;
	}
	
	public EnumItem[] getEnumReportPeriodType() {
		return AhCustomReport.REPORT_PERIOD_TYPE;
	}
	
	public EnumItem[] getEnumInterfaceType() {
		return AhCustomReport.REPORT_INTERFACE_TYPE_ENUM;
	}

	public List<CheckItem> getLocation() {
		List<CheckItem> listLocation = getMapListView();
		listLocation.add(0, new CheckItem((long) -1, ""));
		return listLocation;
	}
	
	public String getChangedReportName() {
		return getDataSource().getName().replace("\\", "\\\\").replace("'", "\\'");
	}

	public String getShowSsidName(){
		if (getDataSource().getReportType()==AhCustomReport.REPORT_TYPE_SSID){
			return "";
		}
		return "none";
	}
	
	public String getShowInterfaceRole(){
		if (getDataSource().getReportType()==AhCustomReport.REPORT_TYPE_HIVEAP &&
				getDataSource().getReportDetailType()!= AhCustomReport.REPORT_DETAILTYPE_UNIQUE){
			return "";
		}
		return "none";
	}
	
	public String getShowReportPeriod(){
		if (getDataSource().getReportType()==AhCustomReport.REPORT_TYPE_HIVEAP &&
				getDataSource().getReportDetailType()== AhCustomReport.REPORT_DETAILTYPE_UNIQUE){
			return "none";
		}
		return "";
	}
	
	public String getShowClientCondition(){
		if (getDataSource().getReportType()==AhCustomReport.REPORT_TYPE_CLIENT){
			return "";
		}
		return "none";
	}
	
	public String getShowClientIpAddress(){
		if (getDataSource().getReportType()==AhCustomReport.REPORT_TYPE_CLIENT){
			if (getCheckIp()){
				return "";
			}
		}
		return "none";
	}
	
	public String getShowClientHostName(){
		if (getDataSource().getReportType()==AhCustomReport.REPORT_TYPE_CLIENT){
			if (getCheckHostName()){
				return "";
			}
		}
		return "none";
	}
	
	public String getShowClientUserName(){
		if (getDataSource().getReportType()==AhCustomReport.REPORT_TYPE_CLIENT){
			if (getCheckUserName()){
				return "";
			}
		}
		return "none";
	}
	
	public String getShowSortKeyHiveApU(){
		if (getDataSource().getReportType()==AhCustomReport.REPORT_TYPE_HIVEAP && 
				getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_UNIQUE){
			return "";
		}
		return "none";
	}
	
	public String getShowSortKeyHiveApC(){
		if (getDataSource().getReportType()==AhCustomReport.REPORT_TYPE_HIVEAP && 
				(getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_COUNT ||
						getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_AVERAGE)){
			return "";
		}
		return "none";
	}
	
	public String getShowSortKeyHiveApV(){
		if (getDataSource().getReportType()==AhCustomReport.REPORT_TYPE_HIVEAP && 
				getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_VALUE){
			return "";
		}
		return "none";
	}
	
	public String getShowSortKeyClientU(){
		if (getDataSource().getReportType()==AhCustomReport.REPORT_TYPE_CLIENT && 
				getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_UNIQUE){
			return "";
		}
		return "none";
	}
	
	public String getShowSortKeyClientC(){
		if (getDataSource().getReportType()==AhCustomReport.REPORT_TYPE_CLIENT && 
				getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_COUNT){
			return "";
		}
		return "none";
	}
	public String getShowSortKeyClientCField(){
		if (getDataSource().getReportType()==AhCustomReport.REPORT_TYPE_CLIENT) {
			if (getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_COUNT &&
				getDataSource().getLongSortBy().intValue()!=2299){
				return "";
			} 
			if (getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_AVERAGE){
				return "";
			}
		}
		return "none";
	}
	
	public String getShowSortKeyClientV(){
		if (getDataSource().getReportType()==AhCustomReport.REPORT_TYPE_CLIENT && 
				getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_VALUE){
			return "";
		}
		return "none";
	}
	
	public String getShowSortKeyClientA(){
		if (getDataSource().getReportType()==AhCustomReport.REPORT_TYPE_CLIENT && 
				getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_AVERAGE){
			return "";
		}
		return "none";
	}
	
	public String getShowSortKeySsidC(){
		if (getDataSource().getReportType()==AhCustomReport.REPORT_TYPE_SSID && 
				(getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_COUNT ||
				getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_AVERAGE)){
			return "";
		}
		return "none";
	}
	
	public String getShowSortKeySsidV(){
		if (getDataSource().getReportType()==AhCustomReport.REPORT_TYPE_SSID && 
				getDataSource().getReportDetailType()==AhCustomReport.REPORT_DETAILTYPE_VALUE){
			return "";
		}
		return "none";
	}
	
	public int getCuStartItem(){
		return (getDataSource().getCuPageIndex()-1) * getDataSource().getCuPageSize();
	}
	public int getCuEndItem(){
		return getDataSource().getCuPageIndex() * getDataSource().getCuPageSize();
	}
	
	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public boolean isShowReportTab() {
		return showReportTab;
	}

	public void setShowReportTab(boolean showReportTab) {
		this.showReportTab = showReportTab;
	}

	public List<EnumItem> getListSortByFieldHiveApU() {
		return listSortByFieldHiveApU;
	}

	public List<EnumItem> getListSortByFieldHiveApC() {
		return listSortByFieldHiveApC;
	}

	public List<EnumItem> getListSortByFieldHiveApV() {
		return listSortByFieldHiveApV;
	}

	public List<EnumItem> getListSortByFieldClientU() {
		return listSortByFieldClientU;
	}

	public List<EnumItem> getListSortByFieldClientC() {
		if (listSortByFieldClientC!=null){
			listSortByFieldClientC.add(new EnumItem(2299, "Client Vendor Information"));
		}
		return listSortByFieldClientC;
	}

	public List<EnumItem> getListSortByFieldClientV() {
		return listSortByFieldClientV;
	}

	public List<EnumItem> getListSortByFieldSsidC() {
		return listSortByFieldSsidC;
	}

	public List<EnumItem> getListSortByFieldSsidV() {
		return listSortByFieldSsidV;
	}

	public Long getLongSortHiveApU() {
		return longSortHiveApU;
	}

	public void setLongSortHiveApU(Long longSortHiveApU) {
		this.longSortHiveApU = longSortHiveApU;
	}

	public Long getLongSortHiveApC() {
		return longSortHiveApC;
	}

	public void setLongSortHiveApC(Long longSortHiveApC) {
		this.longSortHiveApC = longSortHiveApC;
	}

	public Long getLongSortHiveApV() {
		return longSortHiveApV;
	}

	public void setLongSortHiveApV(Long longSortHiveApV) {
		this.longSortHiveApV = longSortHiveApV;
	}

	public Long getLongSortClientU() {
		return longSortClientU;
	}

	public void setLongSortClientU(Long longSortClientU) {
		this.longSortClientU = longSortClientU;
	}

	public Long getLongSortClientC() {
		return longSortClientC;
	}

	public void setLongSortClientC(Long longSortClientC) {
		this.longSortClientC = longSortClientC;
	}

	public Long getLongSortClientV() {
		return longSortClientV;
	}

	public void setLongSortClientV(Long longSortClientV) {
		this.longSortClientV = longSortClientV;
	}

	public Long getLongSortSsidC() {
		return longSortSsidC;
	}

	public void setLongSortSsidC(Long longSortSsidC) {
		this.longSortSsidC = longSortSsidC;
	}

	public Long getLongSortSsidV() {
		return longSortSsidV;
	}

	public void setLongSortSsidV(Long longSortSsidV) {
		this.longSortSsidV = longSortSsidV;
	}

	public List<AhCustomReportField> getLstCustomField() {
		return lstCustomField;
	}

	public List<Long> getSelectHiveApUIds() {
		return selectHiveApUIds;
	}

	public void setSelectHiveApUIds(List<Long> selectHiveApUIds) {
		this.selectHiveApUIds = selectHiveApUIds;
	}

	public List<Long> getSelectHiveApCIds() {
		return selectHiveApCIds;
	}

	public void setSelectHiveApCIds(List<Long> selectHiveApCIds) {
		this.selectHiveApCIds = selectHiveApCIds;
	}

	public List<Long> getSelectHiveApVIds() {
		return selectHiveApVIds;
	}

	public void setSelectHiveApVIds(List<Long> selectHiveApVIds) {
		this.selectHiveApVIds = selectHiveApVIds;
	}

	public List<Long> getSelectClientUIds() {
		return selectClientUIds;
	}

	public void setSelectClientUIds(List<Long> selectClientUIds) {
		this.selectClientUIds = selectClientUIds;
	}

	public List<Long> getSelectClientCIds() {
		return selectClientCIds;
	}

	public void setSelectClientCIds(List<Long> selectClientCIds) {
		this.selectClientCIds = selectClientCIds;
	}

	public List<Long> getSelectClientVIds() {
		return selectClientVIds;
	}

	public void setSelectClientVIds(List<Long> selectClientVIds) {
		this.selectClientVIds = selectClientVIds;
	}

	public List<Long> getSelectSsidCIds() {
		return selectSsidCIds;
	}

	public void setSelectSsidCIds(List<Long> selectSsidCIds) {
		this.selectSsidCIds = selectSsidCIds;
	}

	public List<Long> getSelectSsidVIds() {
		return selectSsidVIds;
	}

	public void setSelectSsidVIds(List<Long> selectSsidVIds) {
		this.selectSsidVIds = selectSsidVIds;
	}

	public List<Object[]> getReportResult() {
		return reportResult;
	}

	public boolean getCheckIp() {
		return getDataSource().getAuthIp() != null && !getDataSource().getAuthIp().equals("");
	}

	public boolean getCheckHostName() {
		return getDataSource().getAuthHostName() != null && !getDataSource().getAuthHostName().equals("");
	}

	public boolean getCheckUserName() {
		return getDataSource().getAuthUserName() != null && !getDataSource().getAuthUserName().equals("");
	}

	public String getCuSearchGotoPage() {
		return cuSearchGotoPage;
	}

	public void setCuSearchGotoPage(String cuSearchGotoPage) {
		this.cuSearchGotoPage = cuSearchGotoPage;
	}

	public Long getLongSortClientA() {
		return longSortClientA;
	}

	public void setLongSortClientA(Long longSortClientA) {
		this.longSortClientA = longSortClientA;
	}

	public List<EnumItem> getListSortByFieldClientA() {
		return listSortByFieldClientA;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		dataSource = bo;
		if (bo == null) {
			return null;
		}
		if (getDataSource().getCustomFields() != null)
			getDataSource().getCustomFields().size();
		return null;
	}

}