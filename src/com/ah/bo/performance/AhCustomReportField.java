package com.ah.bo.performance;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "HM_CUSTOM_REPORT_FIELD")
public class AhCustomReportField implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	private int type;

	private int detailType;

	private String fieldString;
	
	private String tableField;
	
	private String tableName;
	
	private String strUnit;
	
	private String description;
	
	public String getStrUnit() {
		return strUnit;
	}

	public void setStrUnit(String strUnit) {
		this.strUnit = strUnit;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Timestamp getVersion() {
		return null;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Transient
	private boolean selected;

	@Override
	public boolean isSelected() {
		return getSelected();
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public boolean getSelected() {
		switch (id.intValue()) {
			case 1101: return true;
			case 1102: return true;
			case 1201: return true;
			case 1202: return true;
			case 1301: return true;
			case 1302: return true;
			case 1303: return true;
			case 2101: return true;
			case 2102: return true;
			case 2103: return true;
			case 2201: return true;
			case 2202: return true;
			case 2203: return true;
			case 2301: return true;
			case 2302: return true;
			case 2303: return true;
			case 2304: return true;
			case 3201: return true;
			case 3202: return true;
			case 3301: return true;
			case 3302: return true;
			case 3303: return true;
			default : break;
		}
		return selected;
	}

	@Override
	public void setVersion(Timestamp version) {
		
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public HmDomain getOwner() {
		return null;
	}

	@Override
	public void setOwner(HmDomain owner) {
		
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getDetailType() {
		return detailType;
	}

	public void setDetailType(int detailType) {
		this.detailType = detailType;
	}

	public String getFieldString() {
		return fieldString;
	}

	public void setFieldString(String fieldString) {
		this.fieldString = fieldString;
	}

	public String getTableField() {
		return tableField;
	}

	public void setTableField(String tableField) {
		this.tableField = tableField;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}