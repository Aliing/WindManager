package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;

/*
 * @author Chris Scheers
 */

@Entity
@Table(name = "HM_EXPRESSMODE_ENABLE")
public class HmExpressModeEnable implements HmBo {

	private static final long	serialVersionUID		= 1L;

	@Id
	@GeneratedValue
	private Long				id;
	
	@Version
	private Timestamp version;
	
	@Transient
	private boolean	selected;
	
	private boolean expressModeEnable = NmsUtil.getOEMCustomer().getExpressModeEnable();

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
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

	/**
	 * @return the expressModeEnable
	 */
	public boolean isExpressModeEnable() {
		return expressModeEnable;
	}

	/**
	 * @param expressModeEnable the expressModeEnable to set
	 */
	public void setExpressModeEnable(boolean expressModeEnable) {
		this.expressModeEnable = expressModeEnable;
	}

	/**
	 * @return the selected
	 */
	@Override
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}