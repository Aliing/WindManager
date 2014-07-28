package com.ah.ui.actions.gml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ah.be.common.DBOperationUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.performance.AhClientSession;
import com.ah.bo.useraccess.UserProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class GMLClientMonitorAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private static final Tracer	log	= new Tracer(GMLClientMonitorAction.class.getSimpleName());

	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}

		try {
			baseOperation();
			return prepareClientList();
		} catch (Exception e) {
			prepareActionError(e);
			return prepareClientList();
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_GM_CLIENTMONITOR);
		setDataSource(AhClientSession.class);
		keyColumnId = COLUMN_CLIENTMAC;
		tableId = HmTableColumn.TABLE_CLIENT_MONITOR;
	}

	@Override
	public AhClientSession getDataSource() {
		return (AhClientSession) dataSource;
	}

	public static final int	COLUMN_USERNAME			= 1;

	public static final int	COLUMN_IPADDRESS		= 2;

	public static final int	COLUMN_USERGROUP		= 3;

	public static final int	COLUMN_CLIENTMAC		= 4;

	public static final int	COLUMN_SESSIONSTARTED	= 5;

	public static final int	COLUMN_SESSIONTIME		= 6;

	/**
	 * get the description of column by id
	 * 
	 * @param id -
	 * @return -
	 */
	@Override
	public final String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_USERNAME:
			code = "gml.clientmonitor.userName";
			break;
		case COLUMN_IPADDRESS:
			code = "gml.clientmonitor.ipAddress";
			break;
		case COLUMN_USERGROUP:
			code = "gml.clientmonitor.userGroup";
			break;
		case COLUMN_CLIENTMAC:
			code = "gml.clientmonitor.clientMac";
			break;
		case COLUMN_SESSIONSTARTED:
			code = "gml.clientmonitor.sessionStarted";
			break;
		case COLUMN_SESSIONTIME:
			code = "gml.clientmonitor.sessionTime";
			break;
		}

		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(6);

		columns.add(new HmTableColumn(COLUMN_CLIENTMAC));
		columns.add(new HmTableColumn(COLUMN_USERNAME));
		columns.add(new HmTableColumn(COLUMN_IPADDRESS));
		columns.add(new HmTableColumn(COLUMN_USERGROUP));
		columns.add(new HmTableColumn(COLUMN_SESSIONSTARTED));
		columns.add(new HmTableColumn(COLUMN_SESSIONTIME));

		return columns;
	}

	private String prepareClientList() throws Exception {
		clearDataSource();
		removeSessionAttributes();
		enableSorting();
		enablePaging();
		
		queryData();
		
		setTableColumns();
		return SUCCESS;
	}
	
	private void queryData() throws Exception 
	{
		String whereSql = prepareGuessAccessMap();
		if (whereSql == null || whereSql.trim().length() == 0) {
			return;
		}
		
		log.warn("queryData", "select id from ah_clientsession where connectstate=1 and (" + whereSql + ")");
//		List<?> resultList = QueryUtil.executeNativeQuery("select id from ah_clientsession where connectstate=1 and (" + whereSql + ")");
		List<?> resultList = DBOperationUtil.executeQuery("select id from ah_clientsession where connectstate=1 and (" + whereSql + ")");
		if (resultList.isEmpty()) {
			return;
		}
		
		List<Long> idList = new ArrayList<Long>(resultList.size());
		for (Object object : resultList) {
			Long intId = Long.parseLong(object.toString());
			idList.add(intId);
		}

		StringBuilder sqlCondition = new StringBuilder();
		sqlCondition.append("id in (");
		for(int i = 0; i < idList.size(); i++) {
			if(0 != i)
				sqlCondition.append(",");
			sqlCondition.append("?");
		}
		sqlCondition.append(")");
		filterParams = new FilterParams(sqlCondition.toString(),idList.toArray());
//		filterParams = new FilterParams("id in (:s1)",new Object[]{idList});

		page = findBos();
	}

	private String prepareGuessAccessMap() {
		List<String> sqlList = new ArrayList<String>();
		try {
			String strSql = "select a.macaddress, c.ssid, d.id "
					+ " from hive_ap a, config_template_ssid b,ssid_profile c, user_profile d "
					+ " where a.template_id = b.config_template_id "
					+ " and b.ssid_profile_id = c.id "
					+ " and (c.userprofile_default_id=d.id or c.userprofile_selfreg_id = d.id "
					+ " or d.id in (select e.user_profile_id from ssid_profile_user_profile e where e.ssid_profile_id = c.id))"
					+ " and d.blnUserManager= true and a.owner="
					+ getDomain().getId() + " and c.owner=" + getDomain().getId() + " and d.owner="
					+ getDomain().getId();
			List<?> queryResult = QueryUtil.executeNativeQuery(strSql);
			if (queryResult != null && queryResult.size() > 0) {
				for (Object obj : queryResult) {
					Object[] result = (Object[]) obj;
					UserProfile up = findBoById(UserProfile.class, Long
							.parseLong(result[2].toString()), this);
					sqlList.add("(apMac='" + result[0].toString() + "' and clientssid='"
							+ result[1].toString() + "' and clientuserprofid="
							+ up.getAttributeValue() + ")");
					Set<String> setUserAttribute = new HashSet<String>();
					if (up.getUserProfileAttribute() != null) {
						for (SingleTableItem singleTable : up.getUserProfileAttribute().getItems()) {
							String[] strAttrValue = singleTable.getAttributeValue().split(",");
							for (String attrValue : strAttrValue) {
								String[] attrRange = attrValue.split("-");
								if (attrRange.length > 1) {
									for (int addCount = Integer.parseInt(attrRange[0]); addCount < Integer
											.parseInt(attrRange[1]) + 1; addCount++) {
										setUserAttribute.add(String.valueOf(addCount));
									}
								} else {
									setUserAttribute.add(String.valueOf(attrRange[0]));
								}
							}
						}
					}
					for (String value : setUserAttribute) {
							sqlList.add("(apMac='" + result[0].toString()
									+ "' and clientssid='" + result[1].toString()
									+ "' and clientuserprofid=" + value + ")");
					}
				}
			}
		} catch (Exception e) {
			log.error("prepareGuessAccessMap", "catch exception", e);
		}
		
		String resultSql = "";
		if (sqlList.size() > 0) {
			for (int i = 0; i < (sqlList.size()-1); i++) {
				resultSql = resultSql + sqlList.get(i) + " or ";
			}
			resultSql += sqlList.get(sqlList.size()-1);
		}
		
		return resultSql;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof UserProfile) {
			UserProfile userp = (UserProfile) bo;
			if (userp.getUserProfileAttribute() != null) {
				userp.getUserProfileAttribute().getId();
				if (userp.getUserProfileAttribute().getItems()!=null) {
					userp.getUserProfileAttribute().getItems().size();
				}
			}
		}
		return null;
	}

}