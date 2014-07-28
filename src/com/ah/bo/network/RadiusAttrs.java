package com.ah.bo.network;

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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.MgrUtil;

@Entity
@Table(name = "RADIUS_OPERATOR_ATTRIBUTE")
@org.hibernate.annotations.Table(appliesTo = "RADIUS_OPERATOR_ATTRIBUTE", indexes = {
		@Index(name = "RADIUS_OPERATOR_ATTRIBUTE_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class RadiusAttrs implements HmBo {

	/**
	 * @author Jian Sun
	 * @version V1.0.0.0
	 */
	private static final long serialVersionUID = 1L;

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
	
	@Column(length = DEFAULT_DESCRIPTION_LENGTH, nullable = false)
	private String  objectName;
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "RADIUS_ATTRIBUTE_ITEM", joinColumns = @JoinColumn(name = "RADIUS_ATTRIBUTE_ID", nullable = false))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<SingleTableItem> items = new ArrayList<SingleTableItem>();
	
	private boolean defaultFlag;

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public List<SingleTableItem> getItems() {
		return items;
	}

	public void setItems(List<SingleTableItem> items) {
		this.items = items;
	}

	public boolean isDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Override
	public String getLabel() {
		return objectName;
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

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof RadiusAttrs)) {
			return false;
		}
		return null == id ? super.equals(other) : id.equals(((RadiusAttrs) other).getId());
	}
	
	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
	}
	@Override
	public RadiusAttrs clone() {
		try {
			return (RadiusAttrs) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	@Transient
	public int getRadiusAttrsCount() {
		return items.size();
	}
	
	@Transient
	public String[] getRadiusAttrsList() {
		String[] radiusAttrs = new String[items.size()];
		int i = 0;
		for (SingleTableItem item : items) {
			radiusAttrs[i++] = item.getOperatorName() + "/"
					+ MgrUtil.getEnumString("enum.radiusAttrs." + item.getNameSpaceId());
		}
		return radiusAttrs;
	}
	
	@Transient
	private boolean parentIframeOpenFlg;
	@Transient
	private String parentDomID = "";
	@Transient
	private String contentShowType = "subdrawer";
	
	public String getContentShowType() {
		return contentShowType;
	}

	public void setContentShowType(String contentShowType) {
		this.contentShowType = contentShowType;
	}
	
	public boolean isParentIframeOpenFlg() {
		return parentIframeOpenFlg;
	}

	public void setParentIframeOpenFlg(boolean parentIframeOpenFlg) {
		this.parentIframeOpenFlg = parentIframeOpenFlg;
	}

	public String getParentDomID() {
		return parentDomID;
	}

	public void setParentDomID(String parentDomID) {
		this.parentDomID = parentDomID;
	}

}