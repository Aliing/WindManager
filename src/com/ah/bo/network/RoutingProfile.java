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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "ROUTING_PROFILE")
@org.hibernate.annotations.Table(appliesTo = "ROUTING_PROFILE", indexes = {
		@Index(name = "ROUTING_PROFILE_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class RoutingProfile implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	
	// common constant value
	public static final short ENABLE_DRP_RIPV2 = 1;

	public static final short ENABLE_DRP_OSPF = 2;
	
	public static final short ENABLE_DRP_BGP = 3;
	
	public static final short ENABLE_DRP_NONE = 4;
	
	private boolean enableDynamicRouting;
	
	private short typeFlag = ENABLE_DRP_OSPF;
	
	private boolean enableRouteLan = true;
	
	private boolean enableRouteWan;
	
	private boolean useMD5;
	
	private String password;
	
	@Column(length = DEFAULT_STRING_LENGTH)
	private String area;
	
	@Column(length = DEFAULT_STRING_LENGTH)
	private String routerId;
	
	private Integer autonmousSysNm;
	
	private Integer keepalive;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String bgpRouterId;
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "NEIGHBORS_NAME_ITEM", joinColumns = @JoinColumn(name = "NEIGHBORS_OBJECT_ID", nullable = true))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<NeighborsNameItem> items = new ArrayList<NeighborsNameItem>();

	// ================================ Getter/Setter ================================
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
	public boolean isEnableDynamicRouting() {
		return enableDynamicRouting;
	}

	public void setEnableDynamicRouting(boolean enableDynamicRouting) {
		this.enableDynamicRouting = enableDynamicRouting;
	}
	
	public boolean isEnableRouteLan() {
		return enableRouteLan;
	}

	public void setEnableRouteLan(boolean enableRouteLan) {
		this.enableRouteLan = enableRouteLan;
	}

	public boolean isEnableRouteWan() {
		return enableRouteWan;
	}

	public void setEnableRouteWan(boolean enableRouteWan) {
		this.enableRouteWan = enableRouteWan;
	}

	public short getTypeFlag() {
		return typeFlag;
	}

	public void setTypeFlag(short typeFlag) {
		this.typeFlag = typeFlag;
	}
	
	public boolean isUseMD5() {
		return useMD5;
	}

	public void setUseMD5(boolean useMD5) {
		this.useMD5 = useMD5;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getRouterId() {
		return routerId;
	}

	public void setRouterId(String routerId) {
		this.routerId = routerId;
	}

	public Integer getAutonmousSysNm() {
		return autonmousSysNm;
	}

	public void setAutonmousSysNm(Integer autonmousSysNm) {
		this.autonmousSysNm = autonmousSysNm;
	}

	public Integer getKeepalive() {
		return keepalive;
	}

	public void setKeepalive(Integer keepalive) {
		this.keepalive = keepalive;
	}

	public String getBgpRouterId() {
		return bgpRouterId;
	}

	public void setBgpRouterId(String bgpRouterId) {
		this.bgpRouterId = bgpRouterId;
	}

	public List<NeighborsNameItem> getItems() {
		return items;
	}

	public void setItems(List<NeighborsNameItem> items) {
		this.items = items;
	}


	// =================== Transient Method =========================
	@Transient
	public String getStringType() {
		switch (typeFlag) {
			case ENABLE_DRP_RIPV2:
				return "RIPv2";
			case ENABLE_DRP_OSPF:
				return "OSPF";
			case ENABLE_DRP_BGP:
				return "BGP";
			case ENABLE_DRP_NONE:
				return "None";
			default:
				return "UnKnown";
		}
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
	
	/*-----------Override Object methods--------------*/
	@Version
	private Timestamp version;
	
	@Override
	public Timestamp getVersion() {
		return version;
	}
	
	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}
	
	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder().append(this.id, ((RoutingProfile) obj).id).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
	}
	
	@Override
	public RoutingProfile clone() {
		try {
			return (RoutingProfile) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
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

	@Override
	public String getLabel() {
		return null;
	}

}