package com.ah.bo.igmp;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
@Entity
@Table(name = "MULTICAST_GROUP")
@org.hibernate.annotations.Table(appliesTo = "MULTICAST_GROUP", indexes = {
		@Index(name = "MULTICAST_GROUP_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class MulticastGroup implements HmBo,Comparable<Object> {

	private static final long serialVersionUID = 9143090140009746766L;

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
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "HIVE_AP_ID")
	private HiveAp hiveAp;
	
	
	private Integer vlanId;
	@Column(length = DEFAULT_DESCRIPTION_LENGTH, nullable = false)
	private String ipAddress;
	@OneToMany(mappedBy = "multicastGroup", cascade={CascadeType.ALL})
	private Set<MulticastGroupInterface> interfaces = new HashSet<MulticastGroupInterface>();
	
	public static final Short INTERFACE_TYPE_ETH = 1;
	
	public static final Short INTERFACE_TYPE_SFP = 2;
	
	public static final Short INTERFACE_TYPE_PORTCHANNEL = 3;
	
	public Set<MulticastGroupInterface> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(Set<MulticastGroupInterface> interfaces) {
		this.interfaces = interfaces;
	}
	
	public Integer getVlanId() {
		return vlanId;
	}

	public void setVlanId(Integer vlanId) {
		this.vlanId = vlanId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	
	public HiveAp getHiveAp() {
		return hiveAp;
	}

	public void setHiveAp(HiveAp hiveAp) {
		this.hiveAp = hiveAp;
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
		// TODO Auto-generated method stub
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

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MulticastGroup)) {
			return false;
		}
		return null == id ? super.equals(other) : id.equals(((MulticastGroup) other).getId());
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
	}
	@Override
	public MulticastGroup clone() {
		try {
			return (MulticastGroup) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	public int compareTo(Object o) {
		if(o instanceof MulticastGroup){
			if(vlanId > ((MulticastGroup)o).vlanId){
				return 1;
			}else if(vlanId < ((MulticastGroup)o).vlanId){
				return -1;
			}else{
				return 0;
			}
		}else{
			return 0;
		}
	}

}
