package com.ah.bo.dashboard;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "HM_DASHBOARD_LAYOUT")
@org.hibernate.annotations.Table(appliesTo = "HM_DASHBOARD_LAYOUT", indexes = {
		@Index(name = "DASHBOARD_LAYOUT_OWNER", columnNames = { "OWNER" })
		})
public class AhDashboardLayout implements HmBo {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	
	@Transient
	private boolean selected;
	
	public static final byte SIZE_SMALL = 1;
	public static final byte SIZE_MEDIUM = 2;
	public static final byte SIZE_LARGE = 3;
	public static final byte SIZE_CUSTOM = 4;
	private byte sizeType = SIZE_MEDIUM;
	
	private double width = -1;
	
	private byte itemOrder = 0;
	
	@OneToMany(mappedBy = "daLayout", cascade = {CascadeType.PERSIST})
	private Set<AhDashboardWidget> daWidgets = new HashSet<AhDashboardWidget>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dashboard_id")
	private AhDashboard dashboard;
	
	private String tabId="";
	
	@Version
	private Timestamp version;
	
	public byte getSizeType() {
		return sizeType;
	}

	public void setSizeType(byte sizeType) {
		this.sizeType = sizeType;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public String getTabId() {
		return tabId;
	}

	public void setTabId(String tabId) {
		this.tabId = (tabId==null?"":tabId);
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
		return null;
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

	public AhDashboard getDashboard() {
		return dashboard;
	}

	public void setDashboard(AhDashboard dashboard) {
		this.dashboard = dashboard;
		if (dashboard != null
				&& dashboard.getId() != null) {
			this.setTabId(dashboard.getId().toString());
		}
	}

	public byte getItemOrder() {
		return itemOrder;
	}

	public void setItemOrder(byte itemOrder) {
		this.itemOrder = itemOrder;
	}

	public Set<AhDashboardWidget> getDaWidgets() {
		return daWidgets;
	}

	public void setDaWidgets(Set<AhDashboardWidget> daWidgets) {
		this.daWidgets = daWidgets;
	}

	@Override
	public AhDashboardLayout clone() {
		try {
			return (AhDashboardLayout) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}