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

/*
 * @author Chris Scheers
 */

@Entity
@Table(name = "MAC_OR_OUI")
@org.hibernate.annotations.Table(appliesTo = "MAC_OR_OUI", indexes = {
		@Index(name = "MAC_OR_OUI_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class MacOrOui implements HmBo {

	private static final long serialVersionUID = 1L;
	
	public static final String MAC_RANGE_FROM = "000000";
	
	public static final String MAC_RANGE_TO = "FFFFFF";

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
	private String macOrOuiName;

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "MAC_OR_OUI_ITEM", joinColumns = @JoinColumn(name = "MAC_OR_OUI_ID", nullable = false))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<SingleTableItem> items = new ArrayList<SingleTableItem>();

	private boolean defaultFlag;

	private short typeFlag = TYPE_MAC_ADDRESS;

	public static final short TYPE_MAC_ADDRESS = 1;

	public static final short TYPE_MAC_OUI = 2;

	public static final short TYPE_MAC_RANGE = 3;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public boolean equals(Object macOrOui) {
		if (!(macOrOui instanceof MacOrOui)) {
			return false;
		}
		return null == id ? super.equals(macOrOui) : id.equals(((MacOrOui) macOrOui).getId());
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
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
		return macOrOuiName;
	}

	public String getMacOrOuiName() {
		return macOrOuiName;
	}

	public void setMacOrOuiName(String macOrOuiName) {
		this.macOrOuiName = macOrOuiName;
	}

	public short getTypeFlag() {
		return typeFlag;
	}

	public void setTypeFlag(short typeFlag) {
		this.typeFlag = typeFlag;
	}

	public boolean getDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public List<SingleTableItem> getItems() {
		return items;
	}

	public void setItems(List<SingleTableItem> items) {
		this.items = items;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Transient
	public String getValue() {
		return macOrOuiName;
	}

	@Transient
	public String[] getMacList() {
		String[] macs = new String[items.size()];
		int i = 0;
		for (SingleTableItem item : items) {
			if (TYPE_MAC_RANGE == typeFlag) {
				macs[i++] = item.getMacRangeFrom() + "-" + item.getMacRangeTo();
			} else {
				macs[i++] = item.getMacEntry() + "/"
					+ MgrUtil.getEnumString("enum.ipAddress." + item.getType());
			}
		}
		return macs;
	}

	@Transient
	public int getMacCount() {
		return items.size();
	}
	
	@Transient
	public String getDescription(){
		if(!items.isEmpty())
			return items.get(0).getDescription();
		return "";
	}

	@Transient
	public String getStringType() {
		String stringType = "";
		switch (typeFlag) {
		case TYPE_MAC_ADDRESS:
			stringType = "MAC Address";
			break;
		case TYPE_MAC_OUI:
			stringType = "MAC OUI";
			break;
		case TYPE_MAC_RANGE:
			stringType = "MAC Address Range";
			break;
		default:
		}
		return stringType;
	}
	
	@Override
	public MacOrOui clone() {
		try {
			return (MacOrOui) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}