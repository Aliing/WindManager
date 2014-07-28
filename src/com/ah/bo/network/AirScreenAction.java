package com.ah.bo.network;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Range;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

@Entity
@Table(name = "AIR_SCREEN_ACTION")
@org.hibernate.annotations.Table(appliesTo = "AIR_SCREEN_ACTION", indexes = {
		@Index(name = "AIR_SCREEN_ACTION_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class AirScreenAction implements HmBo {

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

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String profileName;

	public static final short TYPE_DE_AUTH = 1;
	public static final short TYPE_LOCAL_BAN = 2;
	public static final short TYPE_REPORT_TO_HM = 3;
	public static EnumItem[] TYPE = MgrUtil.enumItems("enum.as.action.type.",
			new int[] { TYPE_DE_AUTH, TYPE_LOCAL_BAN, TYPE_REPORT_TO_HM });
	private short type = TYPE_DE_AUTH;

	@Range(min = 0, max = 360000)
	private int interval = 3600;

	@Column(length = 64)
	private String comment;

	@Override
	public Long getId() {
		return id;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public String getLabel() {
		return profileName;
	}

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof AirScreenAction
				&& (null == id ? super.equals(other) : id
						.equals(((AirScreenAction) other).getId()));
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
	}

	@Transient
	public String getValue() {
		return profileName;
	}
	
	@Transient
	public String getTypeString() {
		return MgrUtil.getEnumString("enum.as.action.type." + type);
	}
	
	@Transient
	public String getIntervalStr() {
		return TYPE_LOCAL_BAN == type ? String.valueOf(interval) : "";
	}

}