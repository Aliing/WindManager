package com.ah.bo.hiveap;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
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
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Range;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;


@Entity
@Table(name = "FORWARDING_DB", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"OWNER", "ID" }) })
@org.hibernate.annotations.Table(appliesTo = "FORWARDING_DB", indexes = {
		@Index(name = "FORWARDING_DB_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class ForwardingDB implements HmBo {
	
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
	
	private boolean disableMacLearnForAllVlans;
	
	private boolean disableMacLearnForPartVlans;
	
	@Range(min = 0, max = 1800)
	private int idleTimeout = 300;
	
	//not used for initial design
	private boolean enableNotification;
	
	@Range(min = 0, max = 86400)
	private int notificationInterval = 1;
	
	/*@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "FORWARDING_DB_SELECTED_VLANS", joinColumns = { @JoinColumn(name = "FORWARDING_DB_ID") }, inverseJoinColumns = { @JoinColumn(name = "SELECT_VLAN_ID") })
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<Vlan> selectVlans = new HashSet<Vlan>();*/
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "STATIC_MACADDRESS_ENTRIES", joinColumns = @JoinColumn(name = "MACADDRESS_ENTRIES_ID", nullable = true))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<MacAddressLearningEntry> macAddressEntries = new ArrayList<MacAddressLearningEntry>();
	
	private String vlans;
	
	@Transient
	private String editInfo;
	
	@Transient
	private String[] fdb_vlans;
	
	@Transient
	private String[] fdb_interfaces;
	
	@Transient
	private String[] fdb_macaddress;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public HmDomain getOwner() {
		return owner;
	}

	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public Timestamp getVersion() {
		return version;
	}

	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	@Override
	public String getLabel() {
		return null;
	}

	public int getIdleTimeout() {
		return idleTimeout;
	}


	public void setIdleTimeout(int idleTimeout) {
		this.idleTimeout = idleTimeout;
	}


	public boolean isEnableNotification() {
		return enableNotification;
	}


	public void setEnableNotification(boolean enableNotification) {
		this.enableNotification = enableNotification;
	}


	public int getNotificationInterval() {
		return notificationInterval;
	}


	public void setNotificationInterval(int notificationInterval) {
		this.notificationInterval = notificationInterval;
	}


	public List<MacAddressLearningEntry> getMacAddressEntries() {
		return macAddressEntries;
	}


	public void setMacAddressEntries(List<MacAddressLearningEntry> macAddressEntries) {
		this.macAddressEntries = macAddressEntries;
	}

	public String getEditInfo() {
		return editInfo;
	}

	public void setEditInfo(String editInfo) {
		this.editInfo = editInfo;
	}

	public String[] getFdb_vlans() {
		return fdb_vlans;
	}

	public void setFdb_vlans(String[] fdb_vlans) {
		this.fdb_vlans = fdb_vlans;
	}

	public String[] getFdb_interfaces() {
		return fdb_interfaces;
	}

	public void setFdb_interfaces(String[] fdb_interfaces) {
		this.fdb_interfaces = fdb_interfaces;
	}

	public String[] getFdb_macaddress() {
		return fdb_macaddress;
	}

	public void setFdb_macaddress(String[] fdb_macaddress) {
		this.fdb_macaddress = fdb_macaddress;
	}

	public boolean isDisableMacLearnForAllVlans() {
		return disableMacLearnForAllVlans;
	}

	public void setDisableMacLearnForAllVlans(boolean disableMacLearnForAllVlans) {
		this.disableMacLearnForAllVlans = disableMacLearnForAllVlans;
	}

	public String getVlans() {
		return vlans;
	}
	
	public List<Integer> getVlanList() {
		List<Integer> vlanList = new ArrayList<Integer>();
		if (!disableMacLearnForPartVlans || StringUtils.isBlank(vlans)) {
			return vlanList;
		}
		try {
			for (String str : vlans.split(",")) {
				int index = str.indexOf("-");
				if (index > -1) {
					int start = Integer.parseInt(str.substring(0, index));
					int end = Integer.parseInt(str.substring(index + 1));
					if (start > end) { //should not happen
						continue;
					}
					for (int j = start; j <= end; j++) {
						vlanList.add(j);
					}
				} else {
					vlanList.add(Integer.parseInt(str));
				}
			}
		} catch(Exception e) {
			return vlanList;
		}
		return vlanList;
	}

	public void setVlans(String vlans) {
		this.vlans = vlans;
	}

	public boolean isDisableMacLearnForPartVlans() {
		return disableMacLearnForPartVlans;
	}

	public void setDisableMacLearnForPartVlans(boolean disableMacLearnForPartVlans) {
		this.disableMacLearnForPartVlans = disableMacLearnForPartVlans;
	}

}
