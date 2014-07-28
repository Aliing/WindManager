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
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/*
 * @author Chris Scheers
 */

@Entity
@Table(name = "MAC_FILTER")
@org.hibernate.annotations.Table(appliesTo = "MAC_FILTER", indexes = {
		@Index(name = "MAC_FILTER_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class MacFilter implements HmBo {

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

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String filterName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	public static final short FILTER_ACTION_PERMIT = 1;

	public static final short FILTER_ACTION_DENY = 2;

	public static EnumItem[] ENUM_FILTER_ACTION = MgrUtil.enumItems(
			"enum.filterAction.", new int[] { FILTER_ACTION_PERMIT,
					FILTER_ACTION_DENY });

	@Version
	private Timestamp version;

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "MAC_FILTER_MAC_OR_OUI", joinColumns = @JoinColumn(name = "MAC_FILTER_ID", nullable = true))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<MacFilterInfo> filterInfo = new ArrayList<MacFilterInfo>();

	@Transient
	public String[] getFieldValues() {
		return new String[] { "id", "filterName", "description","owner" };
	}

	public List<MacFilterInfo> getFilterInfo() {
		return filterInfo;
	}

	public void setFilterInfo(List<MacFilterInfo> filterInfo) {
		this.filterInfo = filterInfo;
	}

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	@Transient
	public static String getFilterActionString(short filterAction) {
		switch (filterAction) {
		case FILTER_ACTION_PERMIT:
		case FILTER_ACTION_DENY:
			return MgrUtil.getEnumString("enum.filterAction." + filterAction);
		default:
			return "INVALID";
		}
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof MacFilter
				&& (null == id ? super.equals(other) : id.equals(((MacFilter) other).getId()));
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

	@Transient
	public String getValue() {
		return filterName;
	}
	
	@Override
	public String getLabel() {
		return filterName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}
	
//	@Transient
//	public String[] getMacAddressList() {
//		int size = filterInfo.size();
//		String[] macs;
//		if (size > 0) {
//			macs = new String[size];
//			int i = 0;
//			for (MacFilterInfo mac : filterInfo) {
//				macs[i++] = mac.getMacOrOui().getMacOrOuiName();
//			}
//		} else {
//			macs = new String[]{MgrUtil
//				.getUserMessage("config.optionsTransfer.none")};
//		}
//		return macs;
//	}
	
    @Override
    public MacFilter clone() {
       try {
           return (MacFilter) super.clone();
       } catch (CloneNotSupportedException e) {
           return null;
       }
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