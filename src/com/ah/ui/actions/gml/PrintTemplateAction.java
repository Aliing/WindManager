/**
 * @filename			PrintTemplateAction.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.5
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.ui.actions.gml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.gml.PrintTemplate;
import com.ah.bo.gml.TemplateField;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * 
 */
public class PrintTemplateAction extends BaseAction {

	private static final long	serialVersionUID	= 1L;

	private static final Tracer log = new Tracer(PrintTemplateAction.class);
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	public static final int COLUMN_DEFAULT = 2;
	public static final int COLUMN_STATUS = 3;


	public String execute() throws Exception {
		
		try {
			if("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("gml.template.new"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				
				setSessionDataSource(new PrintTemplate());
				prepareView();
				return INPUT;
			} else if("create".equals(operation)) {
				prepareSubmit();
				
				if (checkNameExists("name", getDataSource().getName())) {
					prepareView();
					return INPUT;
				}
			
				return createBo();
			} else if("edit".equals(operation)) {
				String strForward = editBo(new MyLoader());
				
				if (dataSource != null) {
					addLstTitle(getText("gml.template.edit") + " '"
							+ getChangedName() + "'");
				}
				
				prepareView();
				return strForward;				
			} else if("preview".equals(operation)) {
				Set<Long> ids = this.getAllSelectedIds();
				
				if(ids != null && ids.size() > 0) {
					/*
					 * the request is from list view
					 */
					for(Long id : ids) {
						dataSource = QueryUtil.findBoById(PrintTemplate.class, id, new MyLoader());
						break;
					}
				} else {
					prepareSubmit();
				}
				
				this.setSessionDataSource(dataSource);
				return "preview";
			} else if("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				PrintTemplate profile = (PrintTemplate) findBoById(boClass, cloneId, new MyLoader());
				profile.setName("");
				profile.setId(null);
				profile.setAsDefault(false);
				profile.setDefaultFlag(false);
				profile.setOwner(null);
				profile.setVersion(null);
				setSessionDataSource(profile);
				prepareView();
				return INPUT;
			} else if("update".equals(operation)) {
				if(dataSource != null) {
					prepareSubmit();
				}
				
				if(!isValid()) {
					return INPUT;
				}
				
				return updateBo();
			} else if("default".equals(operation)) {
				if(isTemplateEnabled()) {
					setDefaultTemplate();
				}

				return "json";
			} else {
				baseOperation();
				return prepareBoList();
			}		
		} catch(Exception e) {
			return prepareActionError(e);
		}
	}
	
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_UM_TEMP_PRINT);
		setDataSource(PrintTemplate.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_GML_TEMPLATE;

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
			code = "gml.template.name";
			break;
		case COLUMN_DEFAULT:
			code = "gml.template.default";
			break;
		case COLUMN_STATUS:
			code = "gml.template.status";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}
	
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_DEFAULT));
		columns.add(new HmTableColumn(COLUMN_STATUS));
		
		return columns;
	}
	
	public PrintTemplate getDataSource() {
		return (PrintTemplate)dataSource;
	}
	
	public String getChangedName() {
		return getDataSource().getName().replace("\\", "\\\\").replace("'",
				"\\'");
	}

	public int getNameLength() {
		return getAttributeLength("name");
	}
	
	private void prepareView() {
		this.templateId = getDataSource().getId();
		prepareFields(getDataSource());
	}
	
	private void prepareFields(PrintTemplate template) {
		List<TemplateField> oldFields = template.getTemplateFields();
		
		if(oldFields == null 
				|| oldFields.size() == 0) {
			template.setFields(TemplateField.getDefaultFields());
			return ;
		}
		
		
		Map<String, TemplateField> fields = new LinkedHashMap<String, TemplateField>();
		byte i = 1;
		
		/*
		 * add old ones
		 */
		for(TemplateField field : oldFields) {
			field.setPlace(i++);
			fields.put(field.getLabel(), field);
		}
		
		/*
		 * add left ones
		 */
		i = 1;
		
		for(String field : TemplateField.FIELDS) {
			TemplateField templateField = template.getField(field);
			
			if(templateField == null) {
				templateField = new TemplateField();
	
				templateField.setRequired(false);
				templateField.setLabel(field);
				templateField.setPlace((byte)(oldFields.size() + i));
				fields.put(field, templateField);
				i++;
			} 
		}
		
		template.setFields(fields);
	}
	
	private void prepareSubmit() {
		updateFields();
	}

	private void updateFields() {
		
		Map<String, TemplateField> fields = new LinkedHashMap<String, TemplateField>();
		
		if(requireds != null && !requireds[0].equals("false")) {
			for (String required : requireds) {
				TemplateField newField = new TemplateField();
				int index = Integer.parseInt(required);

				newField.setField(labels[index]);
				newField.setLabel(labels[index]);
				newField.setPlace(orders[index]);
				newField.setRequired(true);

				fields.put(labels[index], newField);
			}
		}
		
		getDataSource().setFields(fields);
	}
	
	private String defaultId;


	/**
	 * getter of defaultId
	 * @return the defaultId
	 */
	public String getDefaultId() {
		return defaultId;
	}

	/**
	 * setter of defaultId
	 * @param defaultId the defaultId to set
	 */
	public void setDefaultId(String defaultId) {
		this.defaultId = defaultId;
	}
	
	private String[] labels;
	
	private String[] enableds;
	
	private String[] requireds;
	
	private byte[] orders;

	/**
	 * getter of labels
	 * @return the labels
	 */
	public String[] getLabels() {
		return labels;
	}

	/**
	 * setter of labels
	 * @param labels the labels to set
	 */
	public void setLabels(String[] labels) {
		this.labels = labels;
	}

	/**
	 * getter of enableds
	 * @return the enableds
	 */
	public String[] getEnableds() {
		return enableds;
	}

	/**
	 * setter of enableds
	 * @param enableds the enableds to set
	 */
	public void setEnableds(String[] enableds) {
		this.enableds = enableds;
	}

	/**
	 * getter of requireds
	 * @return the requireds
	 */
	public String[] getRequireds() {
		return requireds;
	}

	/**
	 * setter of requireds
	 * @param requireds the requireds to set
	 */
	public void setRequireds(String[] requireds) {
		this.requireds = requireds;
	}

	/**
	 * getter of orders
	 * @return the orders
	 */
	public byte[] getOrders() {
		return orders;
	}

	/**
	 * setter of orders
	 * @param orders the orders to set
	 */
	public void setOrders(byte[] orders) {
		this.orders = orders;
	}

	public EnumItem[] getFieldOrders() {
		if(getDataSource() == null) {
			return null;
		}
		
		int size = getDataSource().getFields().size();
		EnumItem[] items = new EnumItem[size];
		
		for(int i=0; i<size; i++) {
			items[i] = new EnumItem(i+1, String.valueOf(i+1));
		}
		
		return items;
				
	}
	
	private Long templateId;


	/**
	 * getter of templateId
	 * @return the templateId
	 */
	public Long getTemplateId() {
		return templateId;
	}

	/**
	 * setter of templateId
	 * @param templateId the templateId to set
	 */
	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
	
	public List<TemplateField> getFields(){
		this.getSessionDataSource();
		
		if(dataSource == null) {
			return null;
		}
		
		return getDataSource().getTemplateFields();
	}
	
	public String getHeaderHTML() {
		this.getSessionDataSource();
		
		if(dataSource == null) {
				return null;
		}
		
		return getDataSource().getHeaderHTML();
	}
	
	public String getFooterHTML() {
		this.getSessionDataSource();
		
		if(dataSource == null) {
				return null;
		}
		
		return getDataSource().getFooterHTML();		
	}
	
	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().getDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}
	
	private void setDefaultTemplate() {
		jsonObject = new JSONObject();
		
		/*
		 * set all asDefault to false
		 */
		String sql = "UPDATE print_template SET asDefault='f'";
		
		try {
			if(QueryUtil.executeNativeUpdate(sql) < 1) {
				jsonObject.put("failed", true);
				jsonObject.put("msg", MgrUtil
						.getUserMessage("error.gml.template.default"));
				log.error("Cannot reset field 'asDefault' of all profiles");
				return ;
			}
		} catch(Exception e) {
			log.error("Failed to put attribute into JSON object", e);
		}
		
		/*
		 * set the default one
		 */
		sql = "UPDATE print_template SET asDefault='t' WHERE id=" + Long.parseLong(defaultId);

		try {
			if(QueryUtil.executeNativeUpdate(sql) < 1) {
				jsonObject.put("failed", true);
				jsonObject.put("msg", MgrUtil
						.getUserMessage("error.gml.template.default"));
				log.error("Cannot set field 'asDefault' of profile(" + defaultId + ") to true");
			}
		} catch(Exception e) {
			log.error("Failed to put attribute into JSON object", e);
		}
	}
	
	private boolean isTemplateEnabled() {
		/*
		 * get print template from database
		 */
		PrintTemplate template = QueryUtil.findBoById(PrintTemplate.class,
														Long.parseLong(defaultId),
														new MyLoader());
		
		if(template == null) {
			return false;
		}
		
		jsonObject = new JSONObject();
		
		if(!template.getEnabled()) {
			try {
				jsonObject.put("failed", true);
				jsonObject.put("msg", MgrUtil
						.getUserMessage("error.gml.template.not.enabled"));
			} catch(Exception e) {
				log.error("Failed to put attribute into JSON object", e);
			}

			return false;
		}
		
		return true;
	}
	
	private boolean isValid() {
		PrintTemplate template = getDataSource();
		
		if(template == null) {
			return false;
		}
		
		if(template.getAsDefault() && !template.getEnabled()) {
			addActionError(MgrUtil.getUserMessage("error.gml.template.default.disable"));
			return false;
		}
		
		return true;
	}

	private class MyLoader implements QueryBo{

		@Override
		public Collection<HmBo> load(HmBo bo) {
			if (bo instanceof PrintTemplate) {
				PrintTemplate template = (PrintTemplate)bo;
				
				if(template.getFields() != null) {
					template.getFields().size();
				}
			}
			return null;
		}
		
	}

}