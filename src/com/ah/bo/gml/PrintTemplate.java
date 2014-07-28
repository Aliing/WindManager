/**
 * @filename			PrintTemplate.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.5
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.bo.gml;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "PRINT_TEMPLATE")
@org.hibernate.annotations.Table(appliesTo = "PRINT_TEMPLATE", indexes = {
		@Index(name = "PRINT_TEMPLATE_OWNER", columnNames = { "OWNER" })
		})
public class PrintTemplate implements HmBo {

	private static final long	serialVersionUID	= 1L;

	/*
	 * HmBo fields ======================
	 */
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Version
	private Timestamp version;

	@Transient
	private boolean selected;

	private boolean defaultFlag;
	
	/*
	 * native fields ===================
	 */
	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String name;

	private boolean asDefault;
	
	private boolean enabled;

	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name="mapkey")
	@CollectionTable(name = "TEMPLATE_FIELD", joinColumns = @JoinColumn(name = "TEMPLATE_ID", nullable = true))
	private Map<String, TemplateField> fields = new HashMap<String, TemplateField>();
	
	
	@Column(length = 2048)
	private String headerHTML;
	
	@Column(length = 2048)
	private String footerHTML;
	
	public PrintTemplate() {
		setFields(TemplateField.getDefaultFields());
	}
	
	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#getId()
	 */
	@Override
	public Long getId() {
		return this.id;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#getVersion()
	 */
	@Override
	public Timestamp getVersion() {
		return this.version;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#isSelected()
	 */
	@Override
	public boolean isSelected() {
		return this.selected;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#setSelected(boolean)
	 */
	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#setVersion(java.util.Date)
	 */
	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBoBase#getLabel()
	 */
	@Override
	public String getLabel() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBoBase#getOwner()
	 */
	@Override
	public HmDomain getOwner() {
		return this.owner;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBoBase#setOwner(com.ah.bo.admin.HmDomain)
	 */
	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner; 
	}

	public boolean getDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}
	
	/**
	 * getter of name
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * setter of name
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * getter of asDefault
	 * @return the asDefault
	 */
	public boolean getAsDefault() {
		return asDefault;
	}

	/**
	 * setter of asDefault
	 * @param asDefault the asDefault to set
	 */
	public void setAsDefault(boolean asDefault) {
		this.asDefault = asDefault;
	}

	/**
	 * getter of enabled
	 * @return the enabled
	 */
	public boolean getEnabled() {
		return enabled;
	}

	/**
	 * setter of enabled
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * get status in String
	 * @return String "Enabled" or "Disabled"
	 * @author Joseph Chen
	 */
	public String getStatusString() {
		if(this.enabled) {
			return "Enabled";
		} else {
			return "Disabled";
		}
	}
	
	/**
	 * get as default or not in String
	 * @return	String "default" or "no"
	 * @author Joseph Chen
	 */
	public String getAsDefaultString() {
		if(this.asDefault) {
			return "default";
		} else {
			return "no";
		}
	}
	
	/**
	 * getter of fields
	 * @return the fields
	 */
	public Map<String, TemplateField> getFields() {
		return fields;
	}

	/**
	 * setter of fields
	 * @param fields the fields to set
	 */
	public void setFields(Map<String, TemplateField> fields) {
		this.fields = fields;
	}
	
	public TemplateField getField(String field) {
		return fields.get(field);
	}
	
	/**
	 * get the list of TemplateField sorted by order
	 * @return -
	 * @author Joseph Chen
	 */
	public List<TemplateField> getTemplateFields() {
		if(this.fields == null) {
			return null;
		}
		
		List<TemplateField> listFields = new ArrayList<TemplateField>();
		
		for(TemplateField field : this.fields.values()) {
			listFields.add(field);
		}
		
		Collections.sort(listFields, new TemplateFieldComparator());
		
		return listFields;
	}

	/**
	 * getter of headerHTML
	 * @return the headerHTML
	 */
	public String getHeaderHTML() {
		return headerHTML;
	}

	/**
	 * setter of headerHTML
	 * @param headerHTML the headerHTML to set
	 */
	public void setHeaderHTML(String headerHTML) {
		this.headerHTML = headerHTML;
	}

	/**
	 * getter of footerHTML
	 * @return the footerHTML
	 */
	public String getFooterHTML() {
		return footerHTML;
	}

	/**
	 * setter of footerHTML
	 * @param footerHTML the footerHTML to set
	 */
	public void setFooterHTML(String footerHTML) {
		this.footerHTML = footerHTML;
	}

}