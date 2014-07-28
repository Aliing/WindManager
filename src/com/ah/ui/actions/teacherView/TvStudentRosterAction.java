package com.ah.ui.actions.teacherView;

/*
 * @author Chris Scheers
 */

import java.util.ArrayList;

import java.util.List;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.teacherView.TvClass;
import com.ah.bo.teacherView.TvStudentRoster;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.MgrUtil;

public class TvStudentRosterAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	public static final int MAX_NUMBER_TV_STUDENT=4096;
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_STUDENTNAME = 2;
	
	public static final int COLUMN_DESCRIPTION = 3;

	public static final int COLUMN_ID = 4;

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
		case COLUMN_ID:
			code = "config.tv.studentId";
			break;
		case COLUMN_NAME:
			code = "config.tv.className";
			break;
		case COLUMN_STUDENTNAME:
			code = "config.tv.studentName";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.hp.description";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}
	
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		columns.add(new HmTableColumn(COLUMN_ID));
		columns.add(new HmTableColumn(COLUMN_STUDENTNAME));
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}
	
	public String execute() throws Exception {
		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.tv.studentRoster"))) {
					return getLstForward();
				}
				setSessionDataSource(new TvStudentRoster());
				prepareDependData();
				return INPUT;
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				saveGuiData();
				if (checkStudentNameExists()) {
					prepareDependData();
					return INPUT;
				}
				List<?> lstStuId = QueryUtil.executeQuery("select distinct studentId from " + TvStudentRoster.class.getSimpleName(),
						null,new FilterParams("owner.id",getDomainId()));
				if (!lstStuId.contains(getDataSource().getStudentId())){
					if (lstStuId.size()>=MAX_NUMBER_TV_STUDENT){
						addActionError(MgrUtil.getUserMessage("error.maxSupportNumber",
								new String[]{"students",String.valueOf(MAX_NUMBER_TV_STUDENT)}));
						prepareDependData();
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
				prepareDependData();
				if (dataSource == null) {
					return prepareBoList();
				} else {
					addLstTitle(getText("config.title.tv.studentRoster.edit") + " '"
							+ getChangedName() + "'");
					return returnWord;
				}
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				saveGuiData();
				if ("update".equals(operation)) {
					return updateBo();
				} else {
					updateBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				TvStudentRoster profile = (TvStudentRoster) findBoById(boClass, cloneId);
				profile.setId(null);
				profile.setOwner(null);
				profile.setVersion(null);
				setSessionDataSource(profile);
				prepareDependData();
				return INPUT;
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if ("import".equals(operation)) {
				addLstForward("tvStudentRoster");
				clearErrorsAndMessages();
				return operation;
			} else if ("newClass".equals(operation)||
					"editClass".equals(operation)){
				saveGuiData();
				addLstForward("tvStudentRoster");
				clearErrorsAndMessages();
				return operation;
			} else if ("continue".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				} else {
					saveGuiData();
					setId(dataSource.getId());
					if (getUpdateContext()) {
						removeLstTitle();
						removeLstForward();
						setUpdateContext(false);
					}
					prepareDependData();
					return INPUT;
				}
			} else if ("search".equals(operation)) {
				Object lstCondition[];
				if (getFilterClassId()!=-1){
					lstCondition=new Object[3];
				} else {
					lstCondition=new Object[2];
				}
				lstCondition[0] = "%" + getFilterStudentId().trim().toLowerCase() + "%";
				lstCondition[1] = "%" + getFilterStudentName().trim().toLowerCase() + "%";
				if (getFilterClassId()!=-1){
					lstCondition[2] = getFilterClassId();
					filterParams = new FilterParams(
							"lower(studentId) like :s1 and lower(studentName) like :s2 and tvClass.id = :s3",
							lstCondition);
				} else {
					filterParams = new FilterParams(
							"lower(studentId) like :s1 and lower(studentName) like :s2",
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
		setSelectedL2Feature(L2_FEATURE_TV_STUDENTROSTER);
		setDataSource(TvStudentRoster.class);
		keyColumnId = COLUMN_STUDENTNAME;
		this.tableId = HmTableColumn.TABLE_TV_STUDENTROSTER;
	}
	
	public TvStudentRoster getDataSource() {
		return (TvStudentRoster) dataSource;
	}
	
	public String getChangedName() {
		return getDataSource().getLabel().replace("\\", "\\\\").replace("'", "\\'");
	}
	
	protected void prepareDependData(){
		if (getDataSource().getTvClass()!=null) {
			classId=getDataSource().getTvClass().getId();
		}
		prepareClassList();
	}
	
	protected void saveGuiData() throws Exception{
		if (classId != null) {
			TvClass tvClass = findBoById(TvClass.class,
					classId);
			getDataSource().setTvClass(tvClass);
		}
	}
	
	protected void prepareClassList(){
		lstClass = getBoCheckItems("className", TvClass.class, new FilterParams("rosterType",TvClass.TV_ROSTER_TYPE_STUDENT));
	}
	
	/*
	 * Check if BO with attribute 'name' and value 'value' already exists.
	 */
	public boolean checkStudentNameExists() {
		List<?> boIds = QueryUtil.executeQuery("select id from "
				+ TvStudentRoster.class.getSimpleName(), null, 
				new FilterParams("studentId=:s1 and tvClass.id=:s2",
						new Object[]{getDataSource().getStudentId(),
						getDataSource().getTvClass().getId()}),
				getDomain().getId());
		if (!boIds.isEmpty()) {
			addActionError(MgrUtil.getUserMessage("error.objectExists",
					"Student ID: " + getDataSource().getStudentId() + "; Class Name: " + getDataSource().getTvClass().getClassName()));
			return true;
		} else {
			return false;
		}
	}
	
	private List<CheckItem> lstClass;
	private Long classId;
	
	private String filterStudentId="";
	private String filterStudentName="";
	private long filterClassId=-1;
	
	/**
	 * @return the filterStudentId
	 */
	public String getFilterStudentId() {
		return filterStudentId;
	}

	/**
	 * @param filterStudentId the filterStudentId to set
	 */
	public void setFilterStudentId(String filterStudentId) {
		this.filterStudentId = filterStudentId;
	}

	/**
	 * @return the filterStudentName
	 */
	public String getFilterStudentName() {
		return filterStudentName;
	}

	/**
	 * @param filterStudentName the filterStudentName to set
	 */
	public void setFilterStudentName(String filterStudentName) {
		this.filterStudentName = filterStudentName;
	}

	/**
	 * @return the filterClassId
	 */
	public long getFilterClassId() {
		return filterClassId;
	}

	/**
	 * @param filterClassId the filterClassId to set
	 */
	public void setFilterClassId(long filterClassId) {
		this.filterClassId = filterClassId;
	}

	/**
	 * @return the lstClass
	 */
	public List<CheckItem> getLstClass() {
		return lstClass;
	}
	
	public List<CheckItem> getFilterClassList(){
		return getBoCheckItems("className", TvClass.class, new FilterParams("rosterType",TvClass.TV_ROSTER_TYPE_STUDENT));
	}

	/**
	 * @return the classId
	 */
	public Long getClassId() {
		return classId;
	}

	/**
	 * @param classId the classId to set
	 */
	public void setClassId(Long classId) {
		this.classId = classId;
	}

}

