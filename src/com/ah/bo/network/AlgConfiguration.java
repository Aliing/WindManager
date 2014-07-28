/**
 *@filename		AlgConfiguration.java
 *@version
 *@author		Fiona
 *@createtime	2007-12-6 PM 06:54:08
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.network;

import java.sql.Timestamp;
import java.util.HashMap;
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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.network.AlgConfigurationInfo.GatewayType;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
@Entity
@Table(name = "ALG_CONFIGURATION")
@org.hibernate.annotations.Table(appliesTo = "ALG_CONFIGURATION", indexes = {
		@Index(name = "ALG_CONFIGURATION_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class AlgConfiguration implements HmBo {

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

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String configName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name="mapkey")
	@CollectionTable(name = "ALG_CONFIG_INFO", joinColumns = @JoinColumn(name = "ALG_CONFIGURATION_ID", nullable = true))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Map<String, AlgConfigurationInfo> items = new HashMap<String, AlgConfigurationInfo>();

	private boolean defaultFlag;

	public boolean isDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

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

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
		return configName;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public AlgConfigurationInfo getAlgInfo(GatewayType type) {
		return items.get(type.name());
	}

	public Map<String, AlgConfigurationInfo> getItems() {
		return items;
	}

	public void setItems(Map<String, AlgConfigurationInfo> items) {
		this.items = items;
	}
	
	@Override
	public AlgConfiguration clone() {
		try {
			return (AlgConfiguration) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}