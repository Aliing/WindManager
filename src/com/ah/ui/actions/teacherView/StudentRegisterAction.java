/**
 *@filename		StudentRegisterAction.java
 *@version
 *@author		Fiona
 *@createtime	Jun 3, 2010 2:07:16 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.ui.actions.teacherView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ah.be.common.NmsUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.teacherView.TvClass;
import com.ah.bo.teacherView.TvStudentRoster;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.tags.OptionsTransfer;
import com.ah.util.CheckItem;
import com.ah.util.HmProxyUtil;
import com.ah.util.MgrUtil;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
public class StudentRegisterAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	@Override
	public String execute() throws Exception {
		try {
			if ("registration".equals(operation)) {
				userContext = null;
				setSessionDataSource(new TvStudentRoster());
				prepareVhmList();
				if (lstVhm.size() == 1 && classVhmId == (long)CHECK_ITEM_ID_NONE) {
					addActionError(MgrUtil.getUserMessage("error.teacherView.student.register.no.class"));
				}
				return operation;
			} else if ("submit".equals(operation)) {
				userContext = null;
				prepareVhmList();
				if (null == getDataSource()) {
					return "registration";
				}
				
				if (null != classVhmId && -1l != classVhmId) {
					HmDomain owner = new HmDomain();
					owner.setId(classVhmId);
					owner.setVersion(new Timestamp(System.currentTimeMillis()));
					getDataSource().setOwner(owner);
				} else {
					return "registration";
				}
				
				String arandom=(String)(MgrUtil.getSessionAttribute("VALIDATE_IMAGE_CODE")); 
				if(!arandom.equalsIgnoreCase(this.getRand())) {
					addActionError(MgrUtil.getUserMessage("error.config.validateCode"));
					return "registration";
				} 
				
				List<TvStudentRoster> createOnes = new ArrayList<TvStudentRoster>();
				TvStudentRoster studentRoster;
				for (Long classId : selectClass) {
					List<TvStudentRoster> student = QueryUtil.executeQuery(TvStudentRoster.class, null, 
							new FilterParams("studentName=:s1 and tvClass.id=:s2",
									new Object[]{getDataSource().getStudentName(),classId}),classVhmId);
					if (student.isEmpty()) {
						TvClass tvClass = new TvClass();
						tvClass.setId(classId);
						tvClass.setVersion(new Timestamp(System.currentTimeMillis()));
						studentRoster = new TvStudentRoster();
						studentRoster.setStudentName(getDataSource().getStudentName());
						studentRoster.setDescription(getDataSource().getDescription());
						studentRoster.setTvClass(tvClass);
						studentRoster.setOwner(getDataSource().getOwner());
						createOnes.add(studentRoster);
					} 
				}
				
				QueryUtil.bulkCreateBos(createOnes);
				confirmMessage = MgrUtil.getUserMessage("infor.teacherView.student.register.success");
				generateRegistAuditLog(HmAuditLog.STATUS_SUCCESS, "Register Student (" + getDataSource().getStudentName() + ")");
				
				return "registSuccess";
			} else if ("changeVhm".equals(operation)) {
				List<CheckItem> allClass = prepareAvailableClass();
				jsonArray = new JSONArray();
				JSONObject jsObj;
				for (CheckItem item : allClass) {
					jsObj = new JSONObject();
					jsObj.put("id", item.getId());
					jsObj.put("value", item.getValue());
					jsonArray.put(jsObj);
				}
				return "json";
			} else {
				return "login";
			}
		} catch (Exception e) {
			addActionError(e.getMessage());
			return "registration";
		}
	}

	@Override
	public void prepare() throws Exception {
		setSelectedL2Feature(L2_FEATURE_TV_STUDENTROSTER);
		setDataSource(TvStudentRoster.class);
		versionInfo = NmsUtil.getVersionInfo();
	}

	@Override
	public TvStudentRoster getDataSource() {
		return (TvStudentRoster) dataSource;
	}
	
	/**
	 * For student register
	 */
	protected OptionsTransfer classOptions;
	
	private String rand;
	
	public String getRand() {
		return rand;
	}

	public void setRand(String rand) {
		this.rand = rand;
	}
	
	private List<CheckItem> lstVhm = new ArrayList<CheckItem>();
	
	private Long classVhmId;

	public void prepareVhmList() {
		// get list of id and name from database
		String sql = "SELECT bo.id, bo.domainName FROM " + HmDomain.class.getSimpleName() + " bo";
		List<?> bos = QueryUtil.executeQuery(sql, new SortParams("id"),
				new FilterParams("domainName != :s1", new Object[]{HmDomain.GLOBAL_DOMAIN}));

		List<CheckItem> items = new ArrayList<CheckItem>();
		for (Object obj : bos) {
			Object[] item = (Object[]) obj;
			
			// check if teacher view allowed
			List<?> viewFlag = QueryUtil.executeQuery("SELECT enableTeacher FROM " + HMServicesSettings.class.getSimpleName(), null,
					new FilterParams("owner.id", item[0]));
			if ((Boolean)(viewFlag.get(0))) {
				String profileName = (String) item[1];
				CheckItem checkItem = new CheckItem((Long) item[0], profileName);
				if (HmDomain.HOME_DOMAIN.equals(profileName)) {
					items.add(0, checkItem);
				} else {
					items.add(checkItem);
				}
			}
		}
		if (items.size() == 0) {
			items.add(new CheckItem((long) CHECK_ITEM_ID_NONE, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
		lstVhm = items;
		if (null == classVhmId) {
			classVhmId = items.get(0).getId();
		}
		prepareAvailableClass();
	}

	public List<CheckItem> getLstVhm() {
		return lstVhm;
	}

	public void setLstVhm(List<CheckItem> lstVhm) {
		this.lstVhm = lstVhm;
	}

	public Long getClassVhmId() {
		return classVhmId;
	}

	public void setClassVhmId(Long classVhmId) {
		this.classVhmId = classVhmId;
	}

	public OptionsTransfer getClassOptions() {
		return classOptions;
	}

	public void setClassOptions(OptionsTransfer classOptions) {
		this.classOptions = classOptions;
	}
	
	protected List<Long> selectClass;

	public List<Long> getSelectClass() {
		return selectClass;
	}

	public void setSelectClass(List<Long> selectClass) {
		this.selectClass = selectClass;
	}
	
	public List<CheckItem> prepareAvailableClass() {
		// get list of id and name from database
		String sql = "SELECT bo.id, bo.className FROM " + TvClass.class.getSimpleName() + " bo";
		List<?> bos = QueryUtil.executeQuery(sql, new SortParams("id"), null, classVhmId);

		List<CheckItem> items = new ArrayList<CheckItem>();
		for (Object obj : bos) {
			Object[] item = (Object[]) obj;
			String profileName = (String) item[1];
			CheckItem checkItem = new CheckItem((Long) item[0], profileName);
			
			if (null != selectClass && selectClass.contains(item[0])) {
				getDataSource().getAllClasses().add(checkItem);
			} else {
				items.add(checkItem);
			}
		}
		if (items.size() == 0) {
			items.add(new CheckItem((long) CHECK_ITEM_ID_NONE, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}

		classOptions = new OptionsTransfer(MgrUtil.getUserMessage("config.teacherView.student.register.class.available"), 
				MgrUtil.getUserMessage("config.teacherView.student.register.class.selected"),
				items, getDataSource().getAllClasses(), "id", "value", "selectClass", 0, "180px", "10", true);
		return items;
	}
	
	public String getShowVhmList() {
		return lstVhm.size() > 1 ? "" : "none";
	}
	
	// check if can register
	public String getDisableRegister() {
		return lstVhm.size() == 1 && classVhmId == (long)CHECK_ITEM_ID_NONE ? "disabled" : "";
	}
	
	/**
	 * @author fxr
	 * @param arg_Status :
	 *            HmAuditLog.STATUS_SUCCESS;HmAuditLog.STATUS_FAILURE
	 * @param arg_Comment :
	 *            the comment of this operation
	 */
	public void generateRegistAuditLog(short arg_Status, String arg_Comment) {
		HmAuditLog log = new HmAuditLog();
		log.setStatus(arg_Status);
		log.setOpeationComment(arg_Comment);
		log.setHostIP(null == request ? "127.0.0.1" : HmProxyUtil
				.getClientIp(request));
		try {
			log.setUserOwner(getDataSource().getStudentName());
			HmDomain domain = QueryUtil.findBoById(HmDomain.class, classVhmId);
			log.setOwner(domain);
			log.setLogTimeStamp(System.currentTimeMillis());
			log.setLogTimeZone(domain != null ? domain.getTimeZoneString()
					: TimeZone.getDefault().getID());

			BeLogTools.info(HmLogConst.M_GUIAUDIT, "[" + log.getHostIP() + " "
					+ log.getOwner() + "." + log.getUserOwner() + "]" + " "
					+ arg_Comment + ":" + arg_Status);

			QueryUtil.createBo(log);
		} catch (Exception e) {
			addActionError(e.getMessage());
		}
	}
	
	private String confirmMessage = "";

	public String getConfirmMessage() {
		return confirmMessage;
	}

	public void setConfirmMessage(String confirmMessage) {
		this.confirmMessage = confirmMessage;
	}
	
	@Override
	public boolean isEasyMode() {
		return false;
	}

	@Override
	public boolean isFullMode() {
		return true;
	}
	
	public String licTile = MgrUtil.getUserMessage("config.teacher.view.registration.title");

	public String getLicTile() {
		return licTile;
	}

	public void setLicTile(String licTile) {
		this.licTile = licTile;
	}

}