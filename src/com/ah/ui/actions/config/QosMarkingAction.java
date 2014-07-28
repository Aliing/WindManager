package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.ah.be.app.HmBeParaUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mobility.QosMarking;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumConstUtil;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/*
 * Modification History
 * 
 * support VHM
 *     set owner to null when cloning
 * joseph chen 05/07/2008
 */
public class QosMarkingAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	private String qosName="";
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_PROTOCOL_P = 2;
	
	public static final int COLUMN_PROTOCOL_D = 3;
	
	public static final int COLUMN_DESCRIPTION = 4;

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.marking"))) {
					return getLstForward();
				}
				setSessionDataSource(new QosMarking());
				return returnResultKeyWord(INPUT, "qosMarkingJson");
			} else if ("create".equals(operation)
					|| ("create" + getLstForward()).equals(operation)) {
				if(hasBoInEasyMode()) {
					return prepareBoList();
				}
				
				prepareQosClsssValues();
				if (checkNameExists("qosName", getDataSource().getQosName())) {
					if(("create" + getLstForward()).equals(operation)){
						return returnResultKeyWord(INPUT, "qosMarkingJson");
					} else {
						if (isJsonMode() && !isParentIframeOpenFlg()) {
							jsonObject = new JSONObject();
							jsonObject.put("t", false);
							if (getActionErrors().size()>0) {
								Object[] errs = getActionErrors().toArray();
								jsonObject.put("m", errs[0].toString());
							}
							return "json";
						} else {
							return INPUT;
						}
					}
				}
				String result;
				Long newId;
				
				if ("create".equals(operation)) {
					newId=createBo(dataSource);
					if (isJsonMode() && !isParentIframeOpenFlg()){
						jsonObject = new JSONObject();
						jsonObject.put("t", true);
						jsonObject.put("n", true);
						jsonObject.put("pId", getParentDomID());
						jsonObject.put("nId", newId);
						jsonObject.put("nName", getDataSource().getQosName());
						return "json";
					} else {
						result = returnResultKeyWord(prepareBoList(),"qosMarkingJson");
					}
					
				} else {
					newId = id = createBo(dataSource);
					setUpdateContext(true);
					result = getLstForward();
				}
				
				if (isEasyMode()) {
					QosMarking qosMark = QueryUtil
							.findBoById(QosMarking.class, newId);
					ConfigTemplate defaultTemplate = HmBeParaUtil
							.getEasyModeDefaultTemplate(domainId);
					defaultTemplate.setMarkerMap(qosMark);
					QueryUtil.updateBo(defaultTemplate);
				}
				
				return result;
			} else if ("edit".equals(operation)) {
				String returnWord = editBo();
				if (dataSource == null) {
					return prepareBoList();
				} else {
					qosName=getDataSource().getQosName();
					initQosClassValues();
					addLstTitle(getText("config.title.marking.edit") + " '"
							+ getDisplayName() + "'");
					checkDefaultQos();
					return returnResultKeyWord(returnWord, "qosMarkingJson");
				}
			} else if ("update".equals(operation)) {
				prepareQosClsssValues();
				updateBo(dataSource);
				if (isJsonMode() && !isParentIframeOpenFlg()){
					jsonObject = new JSONObject();
					jsonObject.put("t", true);
					jsonObject.put("n", false);
					return "json";
				} else {
					return returnResultKeyWord(prepareBoList(), "qosMarkingJson");
				}
			} else if (("update"+ getLstForward()).equals(operation)) {
				prepareQosClsssValues();
				updateBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
			} else if ("clone".equals(operation)) {
				setSessionDataSource(new QosMarking());
				long cloneId = getSelectedIds().get(0);
				QosMarking clone = (QosMarking) findBoById(boClass, cloneId);
				qosName=clone.getQosName();
				clone.setId(null);
				clone.setQosName("");
				clone.setOwner(null); // joseph chen
				clone.setVersion(null);  // joseph chen 06/17/2008
				setSessionDataSource(clone);
				initQosClassValues();
				checkDefaultQos();
				addLstTitle(getText("config.title.marking"));
				return INPUT;
			} else if (("create" + getLstForward()).equals(operation)) {
				prepareQosClsssValues();
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

	public void checkDefaultQos(){
		if(qosName!=null && (qosName.equalsIgnoreCase("def-ap-mkr")
				|| qosName.equalsIgnoreCase("def-land-mkr")))
			this.chboxP=false;
		if(qosName!=null && qosName.equals("def-ap-mkr"))
			this.checkPE="pE";
	}
	
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_QOS_MARKING);
		setDataSource(QosMarking.class);
		
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_QOS_MARKER_MAP;
	}
	
	protected String returnResultKeyWord(String normalkey, String expressKey){
		if(isJsonMode()) {
			return  expressKey;
		} else {
			return normalkey;
		}
	}
	
	@Override
	protected void updateConfigTemplate() throws Exception {
		ConfigTemplate defaultTemplate = HmBeParaUtil
			.getEasyModeDefaultTemplate(domainId);
		defaultTemplate.setMarkerMap(null);
		QueryUtil.updateBo(defaultTemplate);
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
		case COLUMN_PROTOCOL_P:
			code = "config.qos.protocolP";
			break;
		case COLUMN_PROTOCOL_D:
			code = "config.qos.protocolD";
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
		columns.add(new HmTableColumn(COLUMN_PROTOCOL_P));
		columns.add(new HmTableColumn(COLUMN_PROTOCOL_D));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		
		return columns;
	}

	public void prepareQosClsssValues() {
		if (getDataSource() == null)
			return;
		getDataSource().setPrtclD("");
		getDataSource().setPrtclP("");
		getDataSource().setPrtclE("");
		if (chboxD) {
			if (this.getProtocolDValue() != null)
				getDataSource().setPrtclD(formatValue(getProtocolDValue()));
		}

		if (chboxP) {
			if (this.getProtocolPValue() != null)
				getDataSource().setPrtclP(formatValue(getProtocolPValue()));
		}
		if(checkPE.equals("pE"))
			getDataSource().setPrtclE("76543021");
	}

	private String formatValue(String[] array) {
		String value = "";
		if (array == null || array.length <= 0)
			return value;
		for (int i = 0; i < array.length; i++) {
			if (i == 0)
				value = array[i];
			else
				value = value + ":" + array[i];
		}
		return value.trim();
	}

	public void initQosClassValues() {
		if (getDataSource() == null)
			return;
		String value =null;
		
		if(getDataSource().getPrtclD()!=null){
			value = getDataSource().getPrtclD().trim();
		}
		if (value != null && !value.equals("")) {
			this.chboxD = true;
			this.setProtocolDValue(value.split(":"));
		}
		if(getDataSource().getPrtclP()!=null){
			value = getDataSource().getPrtclP().trim();
		}
		
		if (value != null && !value.equals("")) {
			this.chboxP = true;
			this.setProtocolPValue(value.split(":"));
		}		
	}

	public QosMarking getDataSource() {
		return (QosMarking) dataSource;
	}

	public boolean getDisabledName() {
		return getDataSource() != null && getDataSource().getId() != null;
	}

	public String getDisplayName() {
		return getDataSource().getQosName().replace("\\", "\\\\").replace("'",
				"\\'");
	}
	
	public EnumItem[] getEnumQosClass() {
		return EnumConstUtil.ENUM_QOS_CLASS;
	}

	public int getNameLength() {
		return super.getAttributeLength("qosName");
	}

	public int getDescriptionLength() {
		return super.getAttributeLength("description");
	}

	public static final String[] defaultQosValue = { "7", "6", "5", "4", "3",
			"0", "2", "1" };

	public static final String[] defaultDiffValue = { "56", "48", "40", "32",
			"24", "0", "16", "8" };

	private final String[] defaultQosName = { "7-Network Service", "6-Voice",
			"5-Video", "4-Controlled Load", "3-Excellent Effort",
			"2-Best Effort 1", "1-Best Effort 2", "0-Background" };
	private final String[] defaultWmmName = { "Voice", "Voice", "Video", "Video",
			"Best Effort", "Best Effort", "Background", "Background" };

	public Map<String, DefaultValue> getProtocolD() {
		Map<String, DefaultValue> map = new LinkedHashMap<String, DefaultValue>();
		for (int i = 0; i < defaultQosName.length; i++) {
			DefaultValue defaultValue = new DefaultValue();
			defaultValue.key = defaultQosName[i];
			defaultValue.value = defaultWmmName[i];
			map.put(defaultValue.key, defaultValue);
		}
		return map;
	}

	class DefaultValue {
		String key;

		String value;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	private boolean chboxP = false;
	
	private boolean chboxD = false;
	
	private String checkPE="";

	private String[] protocolPValue = defaultQosValue;

	private String[] protocolDValue = defaultDiffValue;
	
	private boolean defaultAp=false;
			
	public void setCheckPE(String checkPE){
		this.checkPE=checkPE;
	}
	public String getCheckPE(){
		return this.checkPE;
	}
	public boolean getDefaultAp(){
		if(qosName!=null && (qosName.equalsIgnoreCase("def-ap-mkr")))
			defaultAp=true;
		return defaultAp;
	}

	public String[] getProtocolDValue() {
		return protocolDValue;
	}

	public void setProtocolDValue(String[] protocolDValue) {
		this.protocolDValue = protocolDValue;
	}

	public String[] getProtocolPValue() {
		return protocolPValue;
	}

	public void setProtocolPValue(String[] protocolPValue) {
		this.protocolPValue = protocolPValue;
	}

	public boolean getChboxP() {
		return chboxP;
	}

	public void setChboxP(boolean chboxP) {
		this.chboxP = chboxP;
	}

	public boolean getDisableQosClass(){
		return qosName != null && (qosName.equalsIgnoreCase("def-ap-mkr")
				|| qosName.equalsIgnoreCase("def-land-mkr"));
	}

	public boolean getChboxD() {
		return chboxD;
	}

	public void setChboxD(boolean chboxD) {
		this.chboxD = chboxD;
	}
	
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		QosMarking source = QueryUtil.findBoById(QosMarking.class,
				paintbrushSource);
		if (null == source) {
			return null;
		}
		List<QosMarking> list = QueryUtil.executeQuery(QosMarking.class,
				null, new FilterParams("id", destinationIds), domainId);
		
		if (list.isEmpty()) {
			return null;
		}
		
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		
		for (QosMarking profile : list) {
			
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			QosMarking qos = source.clone();
			
			if (null == qos) {
				continue;
			}
			
			qos.setId(profile.getId());
			qos.setVersion(profile.getVersion());
			qos.setQosName(profile.getQosName());
			qos.setOwner(profile.getOwner());
			hmBos.add(qos);
		}
	
		return hmBos;
	}

}