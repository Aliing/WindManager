package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.validator.constraints.Range;

import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mobility.QosRateControl;
import com.ah.bo.mobility.QosRateLimit;
import com.ah.bo.useraccess.UserProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/*
 * Modification History
 * 
 * support VHM
 *     set owner to null when cloning
 * joseph chen 05/07/2008
 */

public class QosRateControlAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_RATE_LIMIT_ABG = 2;
	
	public static final int COLUMN_RATE_LIMIT_11N = 3;
	
	public static final int COLUMN_RATE_LIMIT_11AC = 4;
	
	public static final int COLUMN_DESCRIPTION = 5;

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.rateControl"))) {
					return getLstForward();
				}
				setSessionDataSource(new QosRateControl());
				return getReturnPathWithJsonMode(INPUT, "qosRateControlJsonDlg");
			} else if ("create".equals(operation)) {
				prepareRateValues();
				if (checkNameExists("qosName", getDataSource().getQosName())) {
					if (isJsonMode() && !isParentIframeOpenFlg()) {
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg",MgrUtil.getUserMessage("error.objectExists", getDataSource().getQosName()));
						return "json";
					} else {
						addActionError(MgrUtil.getUserMessage("error.config.qos.rate.usedButTooBig"));
						return INPUT;
					}
				}
				if (isJsonMode() && !isParentIframeOpenFlg()) {
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", true);
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("addedName", getDataSource().getQosName());
					try {
						id = createBo(dataSource);
						jsonObject.put("addedId", id);
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					return "json";
				} else {
					String rtnStr = createBo();
					return getReturnPathWithJsonMode(rtnStr, "qosRateControlJsonDlg");
				}
			} else if ("edit".equals(operation)) {
				setSessionDataSource(findBoById(boClass, id, new MyLoader()));
				if (dataSource == null) {
					return prepareBoList();
				} else {
					initRateValues();
					addLstTitle(getText("config.title.rateControl.edit") + " '"
							+ this.getDisplayName() + "'");

					return getReturnPathWithJsonMode(INPUT, "qosRateControlJsonDlg");
				}
			} else if ("update".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				prepareRateValues();
				if (isUsing()) {
					if (isJsonMode() && !isParentIframeOpenFlg()) {
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg",MgrUtil.getUserMessage("error.config.qos.rate.usedButTooBig"));
						return "json";
					} else {
						addActionError(MgrUtil.getUserMessage("error.config.qos.rate.usedButTooBig"));
						return INPUT;
					}
					
				}
				if (isJsonMode() && !isParentIframeOpenFlg()) {
					jsonObject = new JSONObject();
					jsonObject.put("resultStatus", true);
					jsonObject.put("parentDomID", getParentDomID());
					try {
						updateBo(dataSource);
					} catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					return "json";
				} else {
					String rtnStr = updateBo();
					return getReturnPathWithJsonMode(rtnStr, "qosRateControlJsonDlg");
				}
			} else if (("update"  + getLstForward()).equals(operation)) {
				if (dataSource != null) {
					prepareRateValues();
					if (isUsing()) {
						addActionError(MgrUtil.getUserMessage("error.config.qos.rate.usedButTooBig"));
						return getReturnPathWithJsonMode(INPUT, "qosRateControlJsonDlg");
					}
				}
								
				updateBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				QosRateControl clone = (QosRateControl) findBoById(boClass,
						cloneId, new MyLoader());
				clone.setId(null);
				clone.setDefaultFlag(false);
				clone.setQosName("");
				clone.setOwner(null); // joseph chen
				clone.setVersion(null);  // joseph chen 06/17/2008
				setSessionDataSource(clone);
				initRateValues();
				return INPUT;
			} else if (("create" + getLstForward()).equals(operation)) {
				prepareRateValues();
				if (checkNameExists("qosName", getDataSource().getQosName())) {
					return getReturnPathWithJsonMode(INPUT, "qosRateControlJsonDlg");
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

	/**
	 * Check the rate limit if the QoS rate control is used by WLAN Policy
	 * @return boolean
	 */
	public boolean isUsing() {
		boolean bln = false;
		if(getDataSource()!=null){
			int rate=getDataSource().getRateLimit();
			int rate11n=getDataSource().getRateLimit11n();
			int rate11ac=getDataSource().getRateLimit11ac();
			
			// get the user profile which is using this qos rate control
			String query="select bo.policingRate,bo.policingRate11n,bo.policingRate11ac from " + UserProfile.class.getSimpleName() + " bo";
			List<?> lst=QueryUtil.executeQuery(query, null,
					new FilterParams("bo.qosRateControl.id",getDataSource().getId()));
			if(!lst.isEmpty()){
				for (int i = 0; i < lst.size(); i ++) {
					// get the rate limit from WLAN policy
//					query="select policingRate, policingRate11n from config_template_qos where user_profile_id="+lst.get(i).toString();
//					List<?> list=QueryUtil.executeNativeQuery(query);
//					if(!list.isEmpty()){
					for (Object obj : lst) {
						Object[] value = (Object[]) obj;
						if (Integer.parseInt(value[0].toString()) < rate
								|| Integer.parseInt(value[1].toString()) < rate11n
								|| Integer.parseInt(value[2].toString()) < rate11ac) {
							bln = true;
							break;
						}
					}
//					}
					if (bln)
						break;
				}
			}
		}
		return bln;
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_QOS_RATE_CONTROL);
		setDataSource(QosRateControl.class);
		
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_QOS_RATE_CONTROL;
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
		case COLUMN_NAME:
			code = "config.qos.classification.name";
			break;
		case COLUMN_RATE_LIMIT_ABG:
			code = "config.qos.rateControl.rateLimit.abg";
			break;
		case COLUMN_RATE_LIMIT_11N:
			code = "config.qos.rateControl.rateLimit.11n";
			break;
		case COLUMN_RATE_LIMIT_11AC:
			code = "config.qos.rateControl.rateLimit.11ac";
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
		columns.add(new HmTableColumn(COLUMN_RATE_LIMIT_ABG));
		columns.add(new HmTableColumn(COLUMN_RATE_LIMIT_11N));
		columns.add(new HmTableColumn(COLUMN_RATE_LIMIT_11AC));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		
		return columns;
	}
	
	public void prepareRateValues() {
		if (getDataSource() == null)
			return;
		List<QosRateLimit> list_limit = new ArrayList<QosRateLimit>();
		for (int i = 0; i < this.className.length; i++) {
			QosRateLimit limit = new QosRateLimit();
			limit.setQosClass(className[i]);
			limit.setSchedulingType(schedulingTypeName[i]);
			limit.setSchedulingWeight(schedulingWeight[i]);
			limit.setPolicingRateLimit(policingRateLimit[i]);
			limit.setPolicing11nRateLimit(policing11nRateLimit[i]);
			limit.setPolicing11acRateLimit(policing11acRateLimit[i]);
			list_limit.add(limit);
		}
		getDataSource().setQosRateLimit(list_limit);
	}

	public void initRateValues() {
		if (getDataSource() == null)
			return;
		List<QosRateLimit> list_limit = getDataSource().getQosRateLimit();
		if (list_limit == null || list_limit.size() <= 0)
			return;
		int index = 0;
		for (int i = 0; i < 8; i++) {
			for (QosRateLimit limit : list_limit) {
				// className[index]=limit.getQosClass();
				switch (limit.getQosClass()) {
				case 7:
					index = 0;
					break;
				case 6:
					index = 1;
					break;
				case 5:
					index = 2;
					break;
				case 4:
					index = 3;
					break;
				case 3:
					index = 4;
					break;
				case 2:
					index = 5;
					break;
				case 1:
					index = 6;
					break;
				case 0:
					index = 7;
					break;
				default:
					break;
				}
				schedulingTypeName[index] = limit.getSchedulingType();
				schedulingWeight[index] = limit.getSchedulingWeight();
				policingRateLimit[index] = limit.getPolicingRateLimit();
				policing11nRateLimit[index] = limit.getPolicing11nRateLimit();
				policing11acRateLimit[index] = limit.getPolicing11acRateLimit();
			}
		}
	}

	public QosRateControl getDataSource() {
		return (QosRateControl) dataSource;
	}

	public boolean getDisabledName() {
		return getDataSource() != null && getDataSource().getId() != null;
	}

	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().isDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}

	public String getDisplayName() {
		return getDataSource().getQosName().replace("\\", "\\\\").replace("'",
				"\\'");
	}

	public Range getRateLimitRange() {
		return super.getAttributeRange("rateLimit");
	}

	public Range getRateLimit11nRange() {
		return super.getAttributeRange("rateLimit11n");
	}
	
	public Range getRateLimit11acRange() {
		return super.getAttributeRange("rateLimit11ac");
	}

	public EnumItem[] getEnumSchedulingType() {
		return QosRateLimit.ENUM_SCHEDULING_TYPE;
	}

	public int getNameLength() {
		return super.getAttributeLength("qosName");
	}

	public int getDescriptionLength() {
		return super.getAttributeLength("description");
	}

	public short[] schedulingTypeName = { 1, 1, 2, 2, 2, 2, 2, 2 };

	private final short[] className = { 7, 6, 5, 4, 3, 2, 1, 0 };

	private final String[] qosClassName = { "7 - Network Control", "6 - Voice",
			"5 - Video", "4 - Controlled", "3 - Excellent Effort",
			"2 - Best Effort 1", "1 - Best Effort 2", "0 - Background" };

	private int[] schedulingWeight = { 0, 0, 60, 50, 40, 30, 20, 10 };

	private int[] policingRateLimit = { 512, 512, 10000, 54000, 54000, 54000,
			54000, 54000 };

	private int[] policing11nRateLimit = { 20000, 20000, 1000000, 1000000, 1000000, 1000000,
		1000000, 1000000 };
	
	private int[] policing11acRateLimit = { 20000, 20000, 1000000, 1000000, 1000000, 1000000,
			1000000, 1000000 };

	public void setSchedulingWeight(int[] schedulingWeight) {
		this.schedulingWeight = schedulingWeight;
	}

	public Map<String, String> getQosClass() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (int i = 0; i < qosClassName.length; i++)
			map.put(String.valueOf(className[i]), qosClassName[i]);
		return map;
	}

	public int[] getPolicingRateLimit()
	{
		return policingRateLimit;
	}

	public void setPolicingRateLimit(int[] policingRateLimit)
	{
		this.policingRateLimit = policingRateLimit;
	}

	public int[] getPolicing11nRateLimit()
	{
		return policing11nRateLimit;
	}

	public void setPolicing11nRateLimit(int[] policing11nRateLimit)
	{
		this.policing11nRateLimit = policing11nRateLimit;
	}
	
	public int[] getPolicing11acRateLimit()
	{
		return policing11acRateLimit;
	}

	public void setPolicing11acRateLimit(int[] policing11acRateLimit)
	{
		this.policing11acRateLimit = policing11acRateLimit;
	}

	public int[] getSchedulingWeight() {
		return schedulingWeight;
	}

	public String[] getWeightPer() {
		String[] tmp = new String[8];
		int total = 0;
		for (int weight : schedulingWeight) {
			total = total + weight;
		}
		for (int i = 0; i < schedulingWeight.length; i++) {
			if (total==0) {
				tmp[i] = "0";
			} else {
				tmp[i] = String.valueOf(schedulingWeight[i] * 100 / total);
			}
		}
		return tmp;
	}

	public short[] getSchedulingTypeName() {
		return schedulingTypeName;
	}

	public void setSchedulingTypeName(short[] schedulingTypeName) {
		this.schedulingTypeName = schedulingTypeName;
	}

	public String[] getQosClassName() {
		return qosClassName;
	}

	public boolean[] getWeightDisabled() {
		boolean[] schedulingWeightDisabled = new boolean[8];
		if (getDataSource() == null
				|| getDataSource().getQosRateLimit() == null
				|| operation.equals("new")) {
			schedulingWeightDisabled[0] = true;
			schedulingWeightDisabled[1] = true;
			schedulingWeightDisabled[2] = false;
			schedulingWeightDisabled[3] = false;
			schedulingWeightDisabled[4] = false;
			schedulingWeightDisabled[5] = false;
			schedulingWeightDisabled[6] = false;
			schedulingWeightDisabled[7] = false;
		} else if (getDataSource() != null
				&& getDataSource().getQosRateLimit() != null) {
			for (int i = 0; i < getDataSource().getQosRateLimit().size(); i++)
				schedulingWeightDisabled[i] = getDataSource().getQosRateLimit()
						.get(i).getSchedulingType() == 1;
		}
		return schedulingWeightDisabled;
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		QosRateControl source = QueryUtil.findBoById(QosRateControl.class,
				paintbrushSource, new MyLoader());
		if (null == source) {
			return null;
		}
		List<QosRateControl> list = QueryUtil.executeQuery(QosRateControl.class,
				null, new FilterParams("id", destinationIds), domainId, new MyLoader());
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (QosRateControl profile : list) {
			if (profile.isDefaultFlag()) {
				continue;
			}
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}
	
			QosRateControl up = source.clone();
			if (null == up) {
				continue;
			}
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setQosName(profile.getQosName());
			up.setOwner(profile.getOwner());
			up.setDefaultFlag(false);
			hmBos.add(up);
		}
		return hmBos;
	}

	private class MyLoader implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
			if(bo instanceof QosRateControl) {
				QosRateControl qosRate = (QosRateControl)bo;
				
				if(qosRate.getQosRateLimit() != null) {
					qosRate.getQosRateLimit().size();
				}
			}
			
			return null;
		}
	}

}