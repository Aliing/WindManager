package com.ah.ui.actions.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhClientEditValues;
import com.ah.bo.performance.AhClientSession;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * 
 *@filename		ClientModifyAction.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2008-7-31 10:14:56
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class ClientModifyAction extends BaseAction {

	private static final Tracer	log			= new Tracer(ClientModifyAction.class.getSimpleName());

	private final CacheMgmt			cacheMgmt	= CacheMgmt.getInstance();

	private String				editUserName;

	private String				editHostName;

	private String				editIP;

	private String				editComment1;

	private String				editComment2;

	private boolean				flagEditUserName;

	private boolean				flagEditHostName;

	private boolean				flagEditIP;

	private boolean				flagEditComment1;

	private boolean				flagEditComment2;

	private String				selectedIDStr;

	private String				filterClientMac;

	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			if ("add".equals(operation)) {
				try {
					boolean result = checkDuplicateEntry();
					if (!result) {
						addActionError(MgrUtil.getUserMessage("action.error.create.fail",getDataSource().getClientMac()));
						prepareClientModifyList();

						return SUCCESS;
					}

					// result = checkValidate();
					// if (!result) {
					// addActionError("Create failed. All fields empty is not permitted!");
					// prepareClientModifyList();
					//
					// return SUCCESS;
					// }

					addClientModification();

					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.add.client.property"));

					addActionMessage(MgrUtil.getUserMessage("message.save.client.modification.success"));

				} catch (Exception e) {
					log.error("execute", "save client modifications catch exception.", e);

					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.add.client.property"));

					addActionError(MgrUtil.getUserMessage("action.error.save.modification.fail"));
				}

				prepareClientModifyList();
				return SUCCESS;
			} else if ("remove".equals(operation)) {
				try {
					if (allItemsSelected) {
						baseOperation();
						cacheMgmt.initClientEditValuesCache();
					} else {
						removeClientModification();
					}

					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.remove.client.property"));
					// addActionMessage("Success clear modification for client.");
				} catch (Exception e) {
					log.error("execute", "clear client modifications catch exception.", e);
					addActionError(MgrUtil.getUserMessage("action.error.operation.fail"));

					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.remove.client.property"));
				}

				prepareFilteredClientModifyList();
				return SUCCESS;
				// } else if ("checkDuplicateEntry".equals(operation)) {
				// boolean result = checkDuplicateEntry();
				//
				// jsonObject = new JSONObject();
				// jsonObject.put("success", result);
				// return "json";
			} else if ("initEditValues".equals(operation)) {
				prepareEditValues();
				return "json";
			} else if ("saveEditResults".equals(operation)) {
				if (!(flagEditUserName || flagEditHostName || flagEditIP || flagEditComment1 || flagEditComment2)) {
					return prepareClientModifyList();
				}

				try {
					saveEditResults();

					addActionMessage(MgrUtil.getUserMessage("message.save.client.modification.success"));

					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.modify.client.property"));
				} catch (Exception e) {
					log.error("execute", "save edit results catch exception.", e);
					addActionError(MgrUtil.getUserMessage("action.error.save.modification.fail"));

					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.modify.client.property"));
				}

				return prepareFilteredClientModifyList();
			} else if ("search".equals(operation)) {
				saveFilter();
				return prepareFilteredClientModifyList();
			} else {
				baseOperation();
				prepareClientModifyList();
				return SUCCESS;
			}
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage(e));
			return ERROR;
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_CLIENTMODIFICATIONS);
		setDataSource(AhClientEditValues.class);
		dataSource = new AhClientEditValues();
		keyColumnId = COLUMN_CLIENTMAC;
		tableId = HmTableColumn.TABLE_CLIENTPROPERTY;
	}

	public AhClientEditValues getDataSource() {
		return (AhClientEditValues) dataSource;
	}

	/*
	 * ID of table columns in list view
	 */
	public static final int	COLUMN_CLIENTMAC		= 1;

	public static final int	COLUMN_CLIENTIP			= 2;

	public static final int	COLUMN_CLIENTHOSTNAME	= 3;

	public static final int	COLUMN_CLIENTUSERNAME	= 4;

	public static final int	COLUMN_CLIENTCOMMENT1	= 5;

	public static final int	COLUMN_CLIENTCOMMENT2	= 6;

	/**
	 * get the description of column by id
	 * 
	 * @param id -
	 * @return -
	 */
	public String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_CLIENTMAC:
			code = "monitor.activeClient.clientMac";
			break;
		case COLUMN_CLIENTIP:
			code = "monitor.activeClient.clientIP";
			break;
		case COLUMN_CLIENTHOSTNAME:
			code = "monitor.activeClient.clientHostName";
			break;
		case COLUMN_CLIENTUSERNAME:
			code = "monitor.activeClient.clientUserName";
			break;
		case COLUMN_CLIENTCOMMENT1:
			code = "monitor.client.comment1";
			break;
		case COLUMN_CLIENTCOMMENT2:
			code = "monitor.client.comment2";
			break;
		}

		return MgrUtil.getUserMessage(code);
	}

	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();

		columns.add(new HmTableColumn(COLUMN_CLIENTMAC));
		columns.add(new HmTableColumn(COLUMN_CLIENTIP));
		columns.add(new HmTableColumn(COLUMN_CLIENTHOSTNAME));
		columns.add(new HmTableColumn(COLUMN_CLIENTUSERNAME));
		columns.add(new HmTableColumn(COLUMN_CLIENTCOMMENT1));
		columns.add(new HmTableColumn(COLUMN_CLIENTCOMMENT2));

		return columns;
	}

	private String prepareClientModifyList() throws Exception {
		if(filterParams == null)
		{
			filterParams= new FilterParams("type",AhClientEditValues.TYPE_USER_ADD);
		}
		enableSorting();
		enablePaging();
		prepareBoList();

		return SUCCESS;
	}

	private String prepareFilteredClientModifyList() throws Exception {
		getSessionFiltering();
		restoreFilter();
		return prepareClientModifyList();
	}

	private void restoreFilter() {
		List<Object> filterParameters = (List<Object>) MgrUtil.getSessionAttribute(CURRENT_FILTER);

		if (null == filterParameters) {
			return;
		}

		filterClientMac = (String) filterParameters.get(0);
	}

	private static final String	CURRENT_FILTER	= "client_modifications_current_filter";

	protected void saveFilter() {
		String searchSQL = "";
		List<Object> lstCondition = new ArrayList<Object>();
		
		searchSQL += "type = :s1";
		lstCondition.add(AhClientEditValues.TYPE_USER_ADD);

		if (filterClientMac != null && filterClientMac.trim().length() > 0) {
			if(!searchSQL.equalsIgnoreCase(""))
				searchSQL += " and ";
			searchSQL += "lower(clientMac) like :s" + (lstCondition.size() + 1);
			lstCondition.add("%" + filterClientMac.toLowerCase() + "%");
		}

		if (lstCondition.size() == 0) {
			filterParams = null;
		} else {
			filterParams = new FilterParams(searchSQL, lstCondition.toArray());
		}

		setSessionFiltering();

		List<Object> filterParameters = new ArrayList<Object>();
		filterParameters.add(filterClientMac);
		MgrUtil.setSessionAttribute(CURRENT_FILTER, filterParameters);
	}

	private void prepareEditValues() throws JSONException {
		List<Long> ids = getSelectedModifyIds();

		if (ids == null || ids.size() == 0) {
			// addActionError("There are no items to operation.");
			return;
		}

		if (ids.size() == 1) {
			AhClientEditValues editValues = QueryUtil.findBoById(
					AhClientEditValues.class, ids.get(0));
			editUserName = editValues.getClientUsername();
			editHostName = editValues.getClientHostname();
			editIP = editValues.getClientIP();
			editComment1 = editValues.getComment1();
			editComment2 = editValues.getComment2();
		}

		jsonObject = new JSONObject();
		jsonObject.put("eUserName", editUserName);
		jsonObject.put("eHostName", editHostName);
		jsonObject.put("eIP", editIP);
		jsonObject.put("eComment1", editComment1);
		jsonObject.put("eComment2", editComment2);
	}

	private List<Long> getSelectedModifyIds() {
		String[] ids = selectedIDStr.split(",");
		List<Long> idList = new ArrayList<Long>(ids.length);
		for (String str_id : ids) {
			idList.add(Long.parseLong(str_id));
		}

		return idList;
	}

	/**
	 * check whether exist same client mac<br>
	 * return true if there are no duplicate entry
	 * 
	 * @return -
	 */
	private boolean checkDuplicateEntry() {
		AhClientEditValues clientModify = getDataSource();
		AhClientEditValues cachedbo = cacheMgmt.getClientEditValues(clientModify.getClientMac(),
				getDomain());
		return cachedbo == null;
	}

	// /**
	// * check whether all input field are empty<br>
	// * return true if some fields have value.
	// *
	// * @param
	// *
	// * @return
	// */
	// private boolean checkValidate() {
	// AhClientEditValues clientModify = getDataSource();
	// if (clientModify.getClientIP().length() == 0
	// && clientModify.getClientHostname().length() == 0
	// && clientModify.getClientUsername().length() == 0
	// && clientModify.getComment1().length() == 0
	// && clientModify.getComment2().length() == 0) {
	// return false;
	// }
	//
	// return true;
	// }

	private void addClientModification() {
		AhClientEditValues clientModify = getDataSource();
		clientModify.setOwner(getDomain());

		try {
			// update client
//			String where = "clientMac=:s1 AND connectstate=:s2";
//			Object[] values = new Object[2];
//			values[0] = clientModify.getClientMac();
//			values[1] = AhClientSession.CONNECT_STATE_UP;
//			FilterParams filterParams = new FilterParams(where, values);
//			List<AhClientSession> clientList = QueryUtil.executeQuery(AhClientSession.class, null, filterParams,
//					getDomainId());
			String where = "clientMac=? AND connectstate=?";
			Object[] values = new Object[2];
			values[0] = clientModify.getClientMac();
			values[1] = AhClientSession.CONNECT_STATE_UP;
			FilterParams filterParams = new FilterParams(where, values);
			List<AhClientSession> clientList = DBOperationUtil.executeQuery(AhClientSession.class, null, filterParams,
					getDomainId());
			
			if (!clientList.isEmpty()) {
				AhClientSession client = clientList.get(0);

				// client ip
				if (clientModify.getClientIP().trim().length() > 0) {
					if (client.getClientIP() == null || client.getClientIP().trim().length() == 0
							|| client.getClientIP().equals("0.0.0.0")) {
						client.setClientIP(clientModify.getClientIP());
					}
				}

				// client host name
				if (clientModify.getClientHostname().trim().length() > 0) {
					if (client.getClientHostname() == null
							|| client.getClientHostname().trim().length() == 0) {
						client.setClientHostname(clientModify.getClientHostname());
					}
				}

				// client user name
				if (clientModify.getClientUsername().trim().length() > 0) {
					if (client.getClientUsername() == null
							|| client.getClientUsername().trim().length() == 0) {
						client.setClientUsername(clientModify.getClientUsername());
					}
				}

				// comment1
				if (clientModify.getComment1().trim().length() > 0) {
					client.setComment1(clientModify.getComment1());
				}

				// comment2
				if (clientModify.getComment2().trim().length() > 0) {
					client.setComment2(clientModify.getComment2());
				}

				QueryUtil.updateBo(client);
			}

			// insert modify entry
			clientModify.setClientMac(clientModify.getClientMac().toUpperCase());
			QueryUtil.createBo(clientModify);

			// update cache
			cacheMgmt.addClientEditValues(clientModify);

			// clear input fields
			dataSource = new AhClientEditValues();

		} catch (Exception e) {
			log.error("addClientModification", "add client modifications", e);
		}
	}

	private void saveEditResults() {

		List<Long> selectIDS = getSelectedModifyIds();
		List<HmBo> updateList = new ArrayList<HmBo>();
		List<AhClientEditValues> editValuesList = new ArrayList<AhClientEditValues>();

		for (Long id : selectIDS) {
			AhClientEditValues clientEditValues = QueryUtil.findBoById(
					AhClientEditValues.class, id);
			if (clientEditValues == null) {
				continue;
			}

			if (flagEditUserName) {
				clientEditValues.setClientUsername(editUserName);
			}

			if (flagEditHostName) {
				clientEditValues.setClientHostname(editHostName);
			}

			if (flagEditIP) {
				clientEditValues.setClientIP(editIP);
			}

			if (flagEditComment1) {
				clientEditValues.setComment1(editComment1);
			}

			if (flagEditComment2) {
				clientEditValues.setComment2(editComment2);
			}

//			String where = "clientMac=:s1 AND connectstate=:s2";
//			Object[] values = new Object[2];
//			values[0] = clientEditValues.getClientMac();
//			values[1] = AhClientSession.CONNECT_STATE_UP;
//			FilterParams filterParams = new FilterParams(where, values);
//			List<AhClientSession> clientList = QueryUtil.executeQuery(AhClientSession.class, null, filterParams,
//					clientEditValues.getOwner().getId());
			String where = "clientMac=? AND connectstate=?";
			Object[] values = new Object[2];
			values[0] = clientEditValues.getClientMac();
			values[1] = AhClientSession.CONNECT_STATE_UP;
			FilterParams filterParams = new FilterParams(where, values);
			List<AhClientSession> clientList = DBOperationUtil.executeQuery(AhClientSession.class, null, filterParams,
					clientEditValues.getOwner().getId());
			if (clientList.size() > 0) {
				AhClientSession client = clientList.get(0);

				boolean isUpdate = false;
				if (flagEditHostName
						&& (client.getClientHostname() == null || client.getClientHostname()
						.length() == 0)) {
					client.setClientHostname(editHostName);
					isUpdate = true;
				}

				if (flagEditIP
						&& (client.getClientIP() == null || client.getClientIP().length() == 0 || client
						.getClientIP().equals("0.0.0.0"))) {
					client.setClientIP(editIP);
					isUpdate = true;
				}

				if (flagEditUserName
						&& (client.getClientUsername() == null || client.getClientUsername()
						.length() == 0)) {
					client.setClientUsername(editUserName);
					isUpdate = true;
				}

				if (flagEditComment1) {
					client.setComment1(editComment1);
					isUpdate = true;
				}

				if (flagEditComment2) {
					client.setComment2(editComment2);
					isUpdate = true;
				}

				// update AhClientSession
				if (isUpdate) {
//					updateList.add(client);
					DBOperationUtil.updateBO(client);
				}
			}

			updateList.add(clientEditValues);
			editValuesList.add(clientEditValues);
		}

		try {
			if (updateList.size() > 0) {
				QueryUtil.bulkUpdateBos(updateList);
			}

			// cache
			if (editValuesList.size() > 0) {
				cacheMgmt.addClientEditValues(editValuesList);
			}
		} catch (Exception e) {
			log.error("saveEditResults", "catch exception", e);
		}
	}

	private void removeClientModification() throws Exception {
		if (getAllSelectedIds() == null) {
			addActionMessage(MgrUtil.getUserMessage(SELECT_OBJECT));
			return;
		}

		int selectedCount = getAllSelectedIds().size();
		if (selectedCount == 0) {
			addActionMessage(MgrUtil.getUserMessage(NO_OBJECTS_REMOVED));
			return;
		}

		int removeCount = removeSelectedClientModify(getAllSelectedIds());
		if (removeCount > 0) {
			addActionMessage(MgrUtil.getUserMessage(OBJECTS_REMOVED, removeCount + ""));
		}
	}

	private int removeSelectedClientModify(Collection<Long> ids) throws Exception {
		// get clientmac list
		String where = "id in (:s1)";
		Object[] values = new Object[1];
		values[0] = ids;

		List<String> macList = (List<String>) QueryUtil.executeQuery("select clientMac from "
				+ AhClientEditValues.class.getSimpleName(), null, new FilterParams(where, values));

		int removeCount = removeBos(AhClientEditValues.class, ids);

		if (removeCount == ids.size()) {
			cacheMgmt.removeClientEditValues(macList, getDomain());
		} else {
			cacheMgmt.initClientEditValuesCache();
		}

		return removeCount;
	}

	public String getEditComment1() {
		return editComment1;
	}

	public void setEditComment1(String editComment1) {
		this.editComment1 = editComment1;
	}

	public String getEditComment2() {
		return editComment2;
	}

	public void setEditComment2(String editComment2) {
		this.editComment2 = editComment2;
	}

	public String getEditHostName() {
		return editHostName;
	}

	public void setEditHostName(String editHostName) {
		this.editHostName = editHostName;
	}

	public String getEditIP() {
		return editIP;
	}

	public void setEditIP(String editIP) {
		this.editIP = editIP;
	}

	public String getEditUserName() {
		return editUserName;
	}

	public void setEditUserName(String editUserName) {
		this.editUserName = editUserName;
	}

	public boolean isFlagEditComment1() {
		return flagEditComment1;
	}

	public void setFlagEditComment1(boolean flagEditComment1) {
		this.flagEditComment1 = flagEditComment1;
	}

	public boolean isFlagEditComment2() {
		return flagEditComment2;
	}

	public void setFlagEditComment2(boolean flagEditComment2) {
		this.flagEditComment2 = flagEditComment2;
	}

	public boolean isFlagEditHostName() {
		return flagEditHostName;
	}

	public void setFlagEditHostName(boolean flagEditHostName) {
		this.flagEditHostName = flagEditHostName;
	}

	public boolean isFlagEditIP() {
		return flagEditIP;
	}

	public void setFlagEditIP(boolean flagEditIP) {
		this.flagEditIP = flagEditIP;
	}

	public boolean isFlagEditUserName() {
		return flagEditUserName;
	}

	public void setFlagEditUserName(boolean flagEditUserName) {
		this.flagEditUserName = flagEditUserName;
	}

	public String getSelectedIDStr() {
		return selectedIDStr;
	}

	public void setSelectedIDStr(String selectedIDStr) {
		this.selectedIDStr = selectedIDStr;
	}

	public String getFilterClientMac() {
		return filterClientMac;
	}

	public void setFilterClientMac(String filterClientMac) {
		this.filterClientMac = filterClientMac;
	}
}
