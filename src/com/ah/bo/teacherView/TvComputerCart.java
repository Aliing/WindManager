/**
 *@filename		IpAddress.java
 *@version
 *@author		Fiona
 *@createtime	2007-8-29 PM 03:16:58
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.teacherView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

/**
 * @author Fisher
 * @version V1.0.0.0
 */
@Entity
@Table(name = "TV_COMPUTER_CART")
@org.hibernate.annotations.Table(appliesTo = "TV_COMPUTER_CART", indexes = {
		@Index(name = "TV_COMPUTER_CART_OWNER", columnNames = { "OWNER" })
		})
public class TvComputerCart implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Version
	private Timestamp version;
	
	@Column(length = 128)
	private String cartName;
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "TV_COMPUTER_CART_MAC", joinColumns = @JoinColumn(name = "TV_CART_ID", nullable = false))
	private List<TvComputerCartMacName> items = new ArrayList<TvComputerCartMacName>();

	@Column(length = 256)
	private String description;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Transient
	public String getValue() {
		return cartName;
	}

	@Transient
	private boolean selected;

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String getLabel() {
		return cartName;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof TvComputerCart)) {
			return false;
		}
		return null == id ? super.equals(other) : id.equals(((TvComputerCart) other).getId());
	}

	/**
	 * @return the cartName
	 */
	public String getCartName() {
		return cartName;
	}

	/**
	 * @param cartName the cartName to set
	 */
	public void setCartName(String cartName) {
		this.cartName = cartName;
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	/**
	 * @return the items
	 */
	public List<TvComputerCartMacName> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<TvComputerCartMacName> items) {
		this.items = items;
	}

}