package com.ah.ui.actions.teacherView;

/*
 * @author Chris Scheers
 */

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.rest.client.models.UserModel;
import com.ah.be.rest.client.services.UserService;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.teacherView.TvClass;
import com.ah.bo.teacherView.TvClassSchedule;
import com.ah.bo.teacherView.TvComputerCart;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.ws.rest.client.utils.ClientUtils;
import com.ah.ws.rest.client.utils.PortalResUtils;
import com.ah.ws.rest.models.CreateUpdateUser;
import com.ah.ws.rest.models.CustomerUserInfo;
import com.ah.ws.rest.models.DeleteResetUser;

public class TvClassAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	public static final int MAX_NUMBER_TV_CLASS=1024;

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;

	public static final int COLUMN_SUBJECT = 2;

	public static final int COLUMN_TEACHER = 3;

	public static final int COLUMN_ROSTERTYPE = 4;

	public static final int COLUMN_DESCRIPTION = 5;

	public static final String MONDAY_TO_FRIDAY = "Monday - Friday";

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
			code = "config.tv.className";
			break;
		case COLUMN_SUBJECT:
			code = "config.tv.subject";
			break;
		case COLUMN_TEACHER:
			code = "config.tv.teacher";
			break;
		case COLUMN_ROSTERTYPE:
			code = "config.tv.rosterType";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.hp.description";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}

	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();

		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_SUBJECT));
		columns.add(new HmTableColumn(COLUMN_TEACHER));
		columns.add(new HmTableColumn(COLUMN_ROSTERTYPE));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}

	public String execute() throws Exception {
		try {

			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.tv.class"))) {
					return getLstForward();
				}
				setSessionDataSource(new TvClass());
				prepareDependData();
				hideCreateItem = "";
				hideNewButton = "none";
				return INPUT;
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				saveGuiData();
				if (checkNameExists("className", getDataSource().getClassName())) {
					prepareDependData();
					return INPUT;
				}
				long classCount = QueryUtil.findRowCount(TvClass.class, new FilterParams("owner.id",getDomainId()));
				if (classCount>=MAX_NUMBER_TV_CLASS){
					addActionError(MgrUtil.getUserMessage("error.maxSupportNumber",
							new String[]{"classes",String.valueOf(MAX_NUMBER_TV_CLASS)}));
					prepareDependData();
					return INPUT;
				}

				if ("create".equals(operation)) {
					return createBo();
				} else {
					id = createBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("edit".equals(operation)) {
				String returnWord = editBo(this);
				prepareDependData();
				if (dataSource == null) {
					return prepareBoList();
				} else {
					addLstTitle(getText("config.title.tv.class.edit") + " '"
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
				TvClass profile = (TvClass) findBoById(boClass, cloneId,this);
				profile.setId(null);
				profile.setClassName("");
				profile.setOwner(null);
				profile.setVersion(null);
				List<TvClassSchedule> items = new ArrayList<TvClassSchedule>();
				items.addAll(profile.getItems());
				profile.setItems(items);
				setSessionDataSource(profile);
				prepareDependData();
				return INPUT;
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if ("import".equals(operation)) {
				addLstForward("tvClass");
				clearErrorsAndMessages();
				return operation;
			} else if ("newCart".equals(operation)||
					"editCart".equals(operation)||
					"newTeacher".equals(operation)){
				saveGuiData();
				addLstForward("tvClass");
				clearErrorsAndMessages();
				return operation;
			} else if ("continue".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				} else {
					saveGuiData();
					if (retTeacherName!=null && !retTeacherName.equals("")){
						getDataSource().setTeacherId(retTeacherName);
					}
					setId(dataSource.getId());
					if (getUpdateContext()) {
						removeLstTitle();
						removeLstForward();
						setUpdateContext(false);
					}
					prepareDependData();
					return INPUT;
				}
			} else if ("addSchedule".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				} else {
					int retInt=addSingleSchedule();
					if (retInt==1) {
						addActionError(MgrUtil.getUserMessage("error.addObjectTypeExists"));
					} else if (retInt==2) {
						addActionError(MgrUtil.getUserMessage("error.addObjectTVScheduleExists"));
					}
					prepareDependData();
					return INPUT;
				}
			} else if ("removeSchedule".equals(operation)
					|| "removeScheduleNone".equals(operation)) {
					hideCreateItem = "removeScheduleNone".equals(operation) ? ""
						: "none";
					hideNewButton = "removeScheduleNone".equals(operation) ? "none"
						: "";
				if (dataSource == null) {
					return prepareBoList();
				} else {
					removeSelectedSchedule();
					prepareDependData();
					return INPUT;
				}
			} else if ("search".equals(operation)) {
				Object lstCondition[] = new Object[3];
				lstCondition[0] = "%" + getFilterClassName().trim().toLowerCase() + "%";
				lstCondition[1] = "%" + getFilterSubject().trim().toLowerCase() + "%";
				lstCondition[2] = "%" + getFilterTeacher().trim().toLowerCase() + "%";
				filterParams = new FilterParams(
						"lower(className) like :s1 and lower(subject) like :s2 and lower(teacherId) like :s3",
						lstCondition);
				setSessionFiltering();
				baseOperation();
				return prepareBoList();
			} else if ("doCheckUserEmail".equals(operation)) {
				doCheckUserEmail();
				return "json";
			} else if ("doCreateTeacher".equals(operation)) {
				doCreateTeacher();
				return "json";
			} else if ("doRemoveTeacher".equals(operation)) {
				doRemoveTeacher();
				return "json";
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
		setSelectedL2Feature(L2_FEATURE_TV_CLASS);
		setDataSource(TvClass.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_TV_CLASS;
	}
	public TvClass getDataSource() {
		return (TvClass) dataSource;
	}

	public String getChangedName() {
		return getDataSource().getLabel().replace("\\", "\\\\").replace("'", "\\'");
	}

	protected void prepareDependData(){
		if (getDataSource().getComputerCart()!=null) {
			cartId=getDataSource().getComputerCart().getId();
		}
		prepareTeacherList();
		prepareCartList();
		initTimeZone(getUserContext().getTimeZone());
	}

	protected void saveGuiData() throws Exception{
		if (getDataSource().getRosterType()==TvClass.TV_ROSTER_TYPE_COMPUTERCART) {
			if (cartId != null) {
				TvComputerCart tvComputerCartClass = findBoById(TvComputerCart.class,
						cartId);
				getDataSource().setComputerCart(tvComputerCartClass);
			}
		} else {
			getDataSource().setComputerCart(null);
		}
	}

	protected void prepareTeacherList(){
		lstTeacher = new ArrayList<TextItem>();

		if (!StringUtils.isEmpty(getUserContext().getCustomerId())) {
			// customer with CID
			try {
				// get teacher list from Portal
				HmDomain owner = getCustomerDomain();
				if (owner == null) {
					jsonObject.put("msg",
							MgrUtil.getUserMessage("error.teacherView.remove.teacher.failed") + ", can't get VHM domain.");
					return;
				}
				
				String productId = getProductId(owner);
				List<CustomerUserInfo>  findTeacher = ClientUtils.getPortalResUtils()
						.getVHMUsersByGroupName(getUserContext().getCustomerId(),
								productId,
								HmUserGroup.TEACHER);
				if (findTeacher != null && !findTeacher.isEmpty()) {
					for (CustomerUserInfo userInfo : findTeacher) {
						lstTeacher.add(new TextItem(userInfo.getUserEmail(), userInfo.getUserEmail()));
					}
				} else {
					lstTeacher.add(new TextItem(MgrUtil
							.getUserMessage("config.optionsTransfer.none"),MgrUtil
							.getUserMessage("config.optionsTransfer.none")));
				}
			} catch (Exception e) {
				addActionError(MgrUtil.getUserMessage("error.teacherView.get.teacher.list.error"));
				lstTeacher.add(new TextItem(MgrUtil
						.getUserMessage("config.optionsTransfer.none"),MgrUtil
						.getUserMessage("config.optionsTransfer.none")));
			}
		} else {
			// customer without CID
/*			List<?> findTeacher = QueryUtil.executeQuery("select userName from " + HmUser.class.getSimpleName(),
					new SortParams("userName"),
					new FilterParams("userGroup.groupName",HmUserGroup.TEACHER), getDomain().getId()); 
					fix bug 23956 in Genava, VHM  with IDM can create teacher*/
			List<?> findTeacher = QueryUtil.executeQuery("select emailAddress from " + HmUser.class.getSimpleName(),
					new SortParams("emailAddress"),
					new FilterParams("userGroup.groupName",HmUserGroup.TEACHER), getDomain().getId());
			if (findTeacher.isEmpty()){
				lstTeacher.add(new TextItem(MgrUtil
						.getUserMessage("config.optionsTransfer.none"),MgrUtil
						.getUserMessage("config.optionsTransfer.none")));
			} else {
				for(Object oneObj:findTeacher){
					lstTeacher.add(new TextItem(oneObj.toString(),oneObj.toString()));
				}
			}
		}
	}

	protected void prepareCartList(){
		lstCart = getBoCheckItems("cartName", TvComputerCart.class, null,
				BaseAction.CHECK_ITEM_BEGIN_BLANK, BaseAction.CHECK_ITEM_END_NO);
	}

	private List<TextItem> lstTeacher;
	private List<CheckItem> lstCart;

	private String filterClassName = "";
	private String filterSubject = "";
	private String filterTeacher = "";

	private Long cartId;
	private String retTeacherName;

//	private String addWeekDay="Monday";
	private String addSHour="";
	private String addSMin="";
	private String addEHour="";
	private String addEMin="";
	private String addRoom="";
	private Collection<String> scheduleIndices;

	private boolean addSun;
	private boolean addMon;
	private boolean addTue;
	private boolean addWed;
	private boolean addThu;
	private boolean addFri;
	private boolean addSat;
	private boolean addMonFri;

	private String hideNewButton = "";
	private String hideCreateItem = "none";
	public String getHideCreateItem()
	{
		return hideCreateItem;
	}

	public String getHideNewButton()
	{
		return hideNewButton;
	}

	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return "";
		}
		return "disabled";
	}

	public int getGridCount() {
		return getDataSource().getItems().size() == 0 ? 3 : 0;
	}

//	public List<TextItem> getLstWeekDay(){
//		List<TextItem> retLst = new ArrayList<TextItem>();
//		retLst.add(new TextItem("Sunday","Sunday"));
//		retLst.add(new TextItem("Monday","Monday"));
//		retLst.add(new TextItem("Tuesday","Tuesday"));
//		retLst.add(new TextItem("Wednesday","Wednesday"));
//		retLst.add(new TextItem("Thursday","Thursday"));
//		retLst.add(new TextItem("Friday","Friday"));
//		retLst.add(new TextItem("Saturday","Saturday"));
//		retLst.add(new TextItem(MONDAY_TO_FRIDAY, MONDAY_TO_FRIDAY));
//		return retLst;
//	}
	private static final DecimalFormat df = new DecimalFormat("00");
	public List<TextItem> getLstHour(){
		List<TextItem> retLst = new ArrayList<TextItem>();
		for(int i=0; i<24; i++){
			retLst.add(new TextItem(df.format(i),df.format(i)));
		}
		return retLst;
	}

	public List<TextItem> getLstMin(){
		List<TextItem> retLst = new ArrayList<TextItem>();
		for(int i=0; i<60; i=i+1){
			retLst.add(new TextItem(df.format(i),df.format(i)));
		}
		return retLst;
	}

	protected String getWeekDayString(){
		StringBuffer temBuf = new StringBuffer(7);
		temBuf.append(addSun?"1":"0");
		temBuf.append(addMon?"1":"0");
		temBuf.append(addTue?"1":"0");
		temBuf.append(addWed?"1":"0");
		temBuf.append(addThu?"1":"0");
		temBuf.append(addFri?"1":"0");
		temBuf.append(addSat?"1":"0");
		return temBuf.toString();
	}

	protected int addSingleSchedule() throws Exception {
		String weekDayString = getWeekDayString();
		for(TvClassSchedule single : getDataSource().getItems()){
			for(int i=0;i<7;i++){
				if (single.getWeekdaySec().charAt(i)=='1' && weekDayString.charAt(i)=='1'){
					if ((single.getStartTime().compareTo(addSHour + ":" + addSMin)>=0 && single.getStartTime().compareTo(addEHour + ":" + addEMin)<=0)
						|| (single.getEndTime().compareTo(addSHour + ":" + addSMin)>=0 && single.getEndTime().compareTo(addEHour + ":" + addEMin)<=0)
						|| ((addSHour + ":" + addSMin).compareTo(single.getStartTime())>=0 && (addSHour + ":" + addSMin).compareTo(single.getEndTime())<=0)
						|| ((addEHour + ":" + addEMin).compareTo(single.getStartTime())>=0 && (addEHour + ":" + addEMin).compareTo(single.getEndTime())<=0)){
						hideCreateItem = "";
						hideNewButton = "none";
						return 2;
					}
				}
			}
		}
//		for(TvClassSchedule single : getDataSource().getItems()){
//			if (single.getWeekday().equalsIgnoreCase(addWeekDay)
//					&& single.getStartTime().equalsIgnoreCase(addSHour + ":" + addSMin)
//					&& single.getEndTime().equalsIgnoreCase(addEHour + ":" + addEMin)
//					&& single.getRoom().equalsIgnoreCase(addRoom)){
//				hideCreateItem = "";
//				hideNewButton = "none";
//				return 1;
//			}
//
//			if (addWeekDay.equalsIgnoreCase(MONDAY_TO_FRIDAY)){
//				if (!single.getWeekday().equalsIgnoreCase("Saturday")
//					&& !single.getWeekday().equalsIgnoreCase("Sunday")){
//					if ((single.getStartTime().compareTo(addSHour + ":" + addSMin)>=0 && single.getStartTime().compareTo(addEHour + ":" + addEMin)<=0)
//							|| (single.getEndTime().compareTo(addSHour + ":" + addSMin)>=0 && single.getEndTime().compareTo(addEHour + ":" + addEMin)<=0)
//							|| ((addSHour + ":" + addSMin).compareTo(single.getStartTime())>=0 && (addSHour + ":" + addSMin).compareTo(single.getEndTime())<=0)
//							|| ((addEHour + ":" + addEMin).compareTo(single.getStartTime())>=0 && (addEHour + ":" + addEMin).compareTo(single.getEndTime())<=0)){
//						hideCreateItem = "";
//						hideNewButton = "none";
//						return 2;
//					}
//				}
//			} else {
//				if (single.getWeekday().equalsIgnoreCase(MONDAY_TO_FRIDAY)
//						&& !addWeekDay.equalsIgnoreCase("Saturday")
//						&& !addWeekDay.equalsIgnoreCase("Sunday")){
//					if ((single.getStartTime().compareTo(addSHour + ":" + addSMin)>=0 && single.getStartTime().compareTo(addEHour + ":" + addEMin)<=0)
//							|| (single.getEndTime().compareTo(addSHour + ":" + addSMin)>=0 && single.getEndTime().compareTo(addEHour + ":" + addEMin)<=0)
//							|| ((addSHour + ":" + addSMin).compareTo(single.getStartTime())>=0 && (addSHour + ":" + addSMin).compareTo(single.getEndTime())<=0)
//							|| ((addEHour + ":" + addEMin).compareTo(single.getStartTime())>=0 && (addEHour + ":" + addEMin).compareTo(single.getEndTime())<=0)){
//						hideCreateItem = "";
//						hideNewButton = "none";
//						return 2;
//					}
//				}
//			}
//		}
		TvClassSchedule tvSche=new TvClassSchedule();
		tvSche.setWeekdaySec(weekDayString);
		tvSche.setStartTime(addSHour + ":" + addSMin);
		tvSche.setEndTime(addEHour + ":" + addEMin);
		tvSche.setRoom(addRoom);
		getDataSource().getItems().add(tvSche);
		addSHour="";
		addSMin="";
		addEHour="";
		addEMin="";
		addRoom="";
		addSun=false;
		addMon=false;
		addTue=false;
		addWed=false;
		addThu=false;
		addFri=false;
		addSat=false;
		addMonFri=false;
		Collections.sort(getDataSource().getItems(), new Comparator<TvClassSchedule>() {
			@Override
			public int compare(TvClassSchedule o1, TvClassSchedule o2) {
				return o2.getWeekdaySec().compareTo(o1.getWeekdaySec());
//				int reportTime1 = getIntWeekDayValue(o1.getWeekday());
//				int reportTime2 = getIntWeekDayValue(o2.getWeekday());
//				int diff = reportTime1 - reportTime2;
//				if (diff == 0) {
//					diff = o1.getStartTime().compareTo(o2.getStartTime());
//				}
//				return diff;
			}
//			private int getIntWeekDayValue(String weekDay){
//				if (weekDay.equalsIgnoreCase("Monday")){
//					return 0;
//				} else if (weekDay.equalsIgnoreCase("Tuesday")){
//					return 1;
//				} else if (weekDay.equalsIgnoreCase("Wednesday")){
//					return 2;
//				} else if (weekDay.equalsIgnoreCase("Thursday")){
//					return 3;
//				} else if (weekDay.equalsIgnoreCase("Friday")){
//					return 4;
//				} else if (weekDay.equalsIgnoreCase("Saturday")){
//					return 5;
//				} else if (weekDay.equalsIgnoreCase("Sunday")){
//					return 6;
//				} else {
//					return 7;
//				}
//			}
		});
		return 0;
	}

	protected void removeSelectedSchedule() {
		if (scheduleIndices != null) {
			Collection<TvClassSchedule> removeList = new Vector<TvClassSchedule>();
			for (String serviceIndex : scheduleIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < getDataSource().getItems().size()) {
						removeList
								.add(getDataSource().getItems().get(index));
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

	public String getHideCartDiv(){
		if (getDataSource().getRosterType()==TvClass.TV_ROSTER_TYPE_STUDENT){
			return "none";
		}
		return "";
	}

	public String getShowMaxPeriodNote(){
		if (getDataSource()!=null && getDataSource().getItems()!=null && getDataSource().getItems().size()>=32){
			return "";
		}
		return "none";
	}

	public String getApplyUpdateDisabled() {
		if ("".equals(getWriteDisabled()) && getShowMaxPeriodNote().equals("none")) {
			return "";
		}
		return "disabled";
	}

	/**
	 * @return the cartId
	 */
	public Long getCartId() {
		return cartId;
	}

	/**
	 * @param cartId the cartId to set
	 */
	public void setCartId(Long cartId) {
		this.cartId = cartId;
	}

	/**
	 * @return the lstTeacher
	 */
	public List<TextItem> getLstTeacher() {
		return lstTeacher;
	}

	public List<TextItem> getFilterTeacherList(){
		List<TextItem> lstTeacher = new ArrayList<TextItem>();

		List<?> findTeacher = QueryUtil.executeQuery("select userName from " + HmUser.class.getSimpleName(),
				new SortParams("userName"),
				new FilterParams("userGroup.groupName",HmUserGroup.TEACHER), getDomain().getId());
		if (!findTeacher.isEmpty()){
			for(Object oneObj:findTeacher){
				lstTeacher.add(new TextItem(oneObj.toString(),oneObj.toString()));
			}
		}
		return lstTeacher;
	}

	/**
	 * @return the lstCart
	 */
	public List<CheckItem> getLstCart() {
		return lstCart;
	}

	public EnumItem[] getEnumRosterType() {
		return TvClass.TV_ENUM_ROSTER_TYPE;
	}

//	/**
//	 * @param addWeekDay the addWeekDay to set
//	 */
//	public void setAddWeekDay(String addWeekDay) {
//		this.addWeekDay = addWeekDay;
//	}

	/**
	 * @param addSHour the addSHour to set
	 */
	public void setAddSHour(String addSHour) {
		this.addSHour = addSHour;
	}

	/**
	 * @param addSMin the addSMin to set
	 */
	public void setAddSMin(String addSMin) {
		this.addSMin = addSMin;
	}

	/**
	 * @param addEHour the addEHour to set
	 */
	public void setAddEHour(String addEHour) {
		this.addEHour = addEHour;
	}

	/**
	 * @param addEMin the addEMin to set
	 */
	public void setAddEMin(String addEMin) {
		this.addEMin = addEMin;
	}

	/**
	 * @param addRoom the addRoom to set
	 */
	public void setAddRoom(String addRoom) {
		this.addRoom = addRoom;
	}

	/**
	 * @param scheduleIndices the scheduleIndices to set
	 */
	public void setScheduleIndices(Collection<String> scheduleIndices) {
		this.scheduleIndices = scheduleIndices;
	}

	/**
	 * @return the retTeacherName
	 */
	public String getRetTeacherName() {
		return retTeacherName;
	}

	/**
	 * @param retTeacherName the retTeacherName to set
	 */
	public void setRetTeacherName(String retTeacherName) {
		this.retTeacherName = retTeacherName;
	}

//	/**
//	 * @return the addWeekDay
//	 */
//	public String getAddWeekDay() {
//		return addWeekDay;
//	}

	/**
	 * @return the filterClassName
	 */
	public String getFilterClassName() {
		return filterClassName;
	}

	/**
	 * @param filterClassName the filterClassName to set
	 */
	public void setFilterClassName(String filterClassName) {
		this.filterClassName = filterClassName;
	}

	/**
	 * @return the filterSubject
	 */
	public String getFilterSubject() {
		return filterSubject;
	}

	/**
	 * @param filterSubject the filterSubject to set
	 */
	public void setFilterSubject(String filterSubject) {
		this.filterSubject = filterSubject;
	}

	/**
	 * @return the filterTeacher
	 */
	public String getFilterTeacher() {
		return filterTeacher;
	}

	/**
	 * @param filterTeacher the filterTeacher to set
	 */
	public void setFilterTeacher(String filterTeacher) {
		this.filterTeacher = filterTeacher;
	}

	/**
	 * @return the addSHour
	 */
	public String getAddSHour() {
		return addSHour;
	}

	/**
	 * @return the addSMin
	 */
	public String getAddSMin() {
		return addSMin;
	}

	/**
	 * @return the addEHour
	 */
	public String getAddEHour() {
		return addEHour;
	}

	/**
	 * @return the addEMin
	 */
	public String getAddEMin() {
		return addEMin;
	}

	/**
	 * @return the addRoom
	 */
	public String getAddRoom() {
		return addRoom;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof TvClass) {
			dataSource = bo;
			if (getDataSource().getItems() != null) {
				getDataSource().getItems().size();
			}
		}
		return null;
	}

	/**
	 * @return the addSun
	 */
	public boolean isAddSun() {
		return addSun;
	}

	/**
	 * @param addSun the addSun to set
	 */
	public void setAddSun(boolean addSun) {
		this.addSun = addSun;
	}

	/**
	 * @return the addMon
	 */
	public boolean isAddMon() {
		return addMon;
	}

	/**
	 * @param addMon the addMon to set
	 */
	public void setAddMon(boolean addMon) {
		this.addMon = addMon;
	}

	/**
	 * @return the addTue
	 */
	public boolean isAddTue() {
		return addTue;
	}

	/**
	 * @param addTus the addTue to set
	 */
	public void setAddTue(boolean addTue) {
		this.addTue = addTue;
	}

	/**
	 * @return the addWed
	 */
	public boolean isAddWed() {
		return addWed;
	}

	/**
	 * @param addWed the addWed to set
	 */
	public void setAddWed(boolean addWed) {
		this.addWed = addWed;
	}

	/**
	 * @return the addThu
	 */
	public boolean isAddThu() {
		return addThu;
	}

	/**
	 * @param addThu the addThu to set
	 */
	public void setAddThu(boolean addThu) {
		this.addThu = addThu;
	}

	/**
	 * @return the addFri
	 */
	public boolean isAddFri() {
		return addFri;
	}

	/**
	 * @param addFri the addFri to set
	 */
	public void setAddFri(boolean addFri) {
		this.addFri = addFri;
	}

	/**
	 * @return the addSat
	 */
	public boolean isAddSat() {
		return addSat;
	}

	/**
	 * @param addSat the addSat to set
	 */
	public void setAddSat(boolean addSat) {
		this.addSat = addSat;
	}

	/**
	 * @return the addMonFri
	 */
	public boolean isAddMonFri() {
		return addMonFri;
	}

	/**
	 * @param addMonFri the addMonFri to set
	 */
	public void setAddMonFri(boolean addMonFri) {
		this.addMonFri = addMonFri;
	}

	public boolean getCreateFromVHM(){
		if (NmsUtil.isHostedHMApplication() && null != userContext.getCustomerId() && !userContext.getCustomerId().isEmpty() && !getIsInHomeDomain()) {
			return true;
		}

		return false;
	}

	/*
	 * add for new Teacher start
	 */
	private String emailAddress;
	private String userName;
	private String description;
	private int timeZone;
	private int i18n;
	private int gmCss;
	private short dateFormat;
	private short timeFormat;
	private String selectedTeacher;

	public boolean getShowNewTeacherLinkForCustomerWithCid(){
		if (NmsUtil.isHostedHMApplication() && !StringUtils.isEmpty(userContext.getCustomerId())) {
			return true;
		}

		return false;
	}
	
	/**
	 * TechOPs switch to normal hmol customer's VHM (neither global nor home domain), can not create/remove teacher user
	 * 
	 * @return
	 */
	public boolean getSwitchToNoneHomeDomain() {
		return NmsUtil.isHostedHMApplication() && getUserContext().getSwitchDomain() != null && !getIsInHomeDomain();
	}
	
	public EnumItem[] getEnumLanguage() {
		return MgrUtil.getEnumLanguage();
	}
	
	public EnumItem[] getEnumGmCss() {
		return MgrUtil.enumItems("enum.css.",
				new int[] {HmUser.CSS_USER_DEFAULT, HmUser.CSS_USER_1, HmUser.CSS_USER_2});
	}
	
	public EnumItem[] getEnumTimeZone() {
		return HmBeOsUtil.getEnumsTimeZone();
	}

	public EnumItem[] getEnumDateFormat(){
		return MgrUtil.enumItems("enum.dateformat.",
				new int[] { HmUser.DATEFORMAT_YYYYMMDD, HmUser.DATEFORMAT_MMDDYYYY, HmUser.DATEFORMAT_DDMMYYYY});
	}

	public EnumItem[] getEnumTimeFormat(){
		return MgrUtil.enumItems("enum.timeformat.",
				new int[] { HmUser.TIMEFORMAT_12HOURS, HmUser.TIMEFORMAT_24HOURS});
	}
	
	private HmDomain getCustomerDomain() {
		return getUserContext().getSwitchDomain() != null ? getUserContext().getSwitchDomain() : getUserContext().getOwner();
	}
	
	private String getProductId(HmDomain owner) {
		String productId;
		if (owner != null && owner.isHomeDomain()) {
			// Aerohive's VHM-ID should be 'HMOL-Aerohive'
			productId = HmDomain.PRODUCT_ID_VHM;
		} else {
			productId = owner.getVhmID();
		}
		return productId;
	}
	
	// value stored in DB is name of time zone, need convert to int value. 
	private void initTimeZone(String timeZoneName) {
		timeZone = HmBeOsUtil.getServerTimeZoneIndex(timeZoneName);
	}
	
	private void doCheckUserEmail() throws JSONException {
		jsonObject = new JSONObject();
		jsonObject.put("exist", true);
		jsonObject.put("msg",
				MgrUtil.getUserMessage("error.otp.eamilExists", emailAddress));

		HmDomain owner = getCustomerDomain();
		if (owner == null) {
			jsonObject.put("exist", true);
			return;
		}
		
		String productId = getProductId(owner);
		UserService myhive = new UserService(NmsUtil.getMyHiveServiceURL());
		UserModel userModel = myhive.retrieveUserInfo(emailAddress, productId);
		if (null == userModel || userModel.getReturnCode() > 0) {
			jsonObject.put("exist", false);
		} else {
			// get user info from Portal
			if (!StringUtils.isEmpty(getUserContext().getCustomerId()) && 
					getUserContext().getCustomerId().equals(userModel.getCustomerId()) &&
					StringUtils.isEmpty(userModel.getGroupName())) {
				// user with current email exist under current user's CID, but has no permission on current Product
				jsonObject.put("exist", false);
			}
		}
	}
	
	private void doCreateTeacher() throws JSONException {
		jsonObject = new JSONObject();
		jsonObject.put("success", false);
		
		if (StringUtils.isEmpty(getUserContext().getCustomerId())) {
			// customer without CID, should not call this operation to create Teacher
			jsonObject.put("msg",
					MgrUtil.getUserMessage("error.teacherView.create.teacher.forbidden"));
			return;
		}
		
		HmDomain owner = getCustomerDomain();
		if (owner == null) {
			jsonObject.put("msg",
					MgrUtil.getUserMessage("error.teacherView.create.teacher.failed") + " can't get VHM domain.");
			return;
		}
		CreateUpdateUser users = new CreateUpdateUser();
		users.setOptType(PortalResUtils.OPT_TYPE_VHM);
		users.setCustomerId(getUserContext().getCustomerId());
		String productId = getProductId(owner);
		users.setProductId(productId);
		
		List<CustomerUserInfo> userInfos = new ArrayList<>();
		CustomerUserInfo user = new CustomerUserInfo();
		user.setUserEmail(emailAddress);
		user.setUserName(userName);
		user.setDescription(description);
		user.setI18n(i18n);
//		user.setGmCss(gmCss);
		user.setTimezone(HmBeOsUtil.getTimeZoneString(timeZone));
		user.setDateFormat(dateFormat);
		user.setTimeFormat(timeFormat);
		user.setGroupName(HmUserGroup.TEACHER);
		user.setDefaultFlag(false);
		userInfos.add(user);
		users.setUsers(userInfos);
		try {
			ClientUtils.getPortalResUtils().createUserOnPortal(users);

			jsonObject.put("success", true);
			jsonObject.put("msg",
					MgrUtil.getUserMessage("info.teacherView.create.teacher.success"));
			jsonObject.put("emailAddress", emailAddress);
		} catch (Exception e) {
			jsonObject.put("msg",
					MgrUtil.getUserMessage("error.teacherView.create.teacher.failed") + " " + e.getMessage());
		}
	}
	
	private void doRemoveTeacher() throws JSONException {
		jsonObject = new JSONObject();
		jsonObject.put("success", false);
		
		// do check if teacher be referenced by class
		List<?> classIds = QueryUtil.executeQuery(
				"select id from " + TvClass.class.getSimpleName(),
				null,
				new FilterParams("lower(teacherId)", StringUtils
						.lowerCase(selectedTeacher)));
		if (classIds != null && !classIds.isEmpty()) {
			jsonObject.put("msg",
					MgrUtil.getUserMessage("action.error.delete.refedteacheruser", selectedTeacher));
			return;
		}

		HmDomain owner = getCustomerDomain();
		if (owner == null) {
			jsonObject.put("msg",
					MgrUtil.getUserMessage("error.teacherView.remove.teacher.failed") + " can't get VHM domain.");
			return;
		}
		DeleteResetUser deleteUsers = new DeleteResetUser();
		deleteUsers.setOptType(PortalResUtils.OPT_TYPE_VHM);
		deleteUsers.setCustomerId(getUserContext().getCustomerId());
		String productId = getProductId(owner);
		deleteUsers.setProductId(productId);
		
		List<String> userEmails = new ArrayList<>();
		userEmails.add(selectedTeacher);
		deleteUsers.setUserEmails(userEmails.toArray());

		try {
			ClientUtils.getPortalResUtils().removeUserOnPortal(deleteUsers);

			jsonObject.put("success", true);
			jsonObject.put("msg",
					MgrUtil.getUserMessage("info.teacherView.remove.teacher.success"));
			jsonObject.put("emailAddress", selectedTeacher);
		} catch (Exception e) {
			jsonObject.put("msg",
					MgrUtil.getUserMessage("error.teacherView.remove.teacher.failed") + " " + e.getMessage());
		}
	}

	public String getSelectedTeacher() {
		return selectedTeacher;
	}

	public void setSelectedTeacher(String selectedTeacher) {
		this.selectedTeacher = selectedTeacher;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(int timeZone) {
		this.timeZone = timeZone;
	}

	public int getI18n() {
		return i18n;
	}

	public void setI18n(int i18n) {
		this.i18n = i18n;
	}

	public int getGmCss() {
		return gmCss;
	}

	public void setGmCss(int gmCss) {
		this.gmCss = gmCss;
	}

	public short getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(short dateFormat) {
		this.dateFormat = dateFormat;
	}

	public short getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(short timeFormat) {
		this.timeFormat = timeFormat;
	}
	/*
	 * add for new Teacher start
	 */

}