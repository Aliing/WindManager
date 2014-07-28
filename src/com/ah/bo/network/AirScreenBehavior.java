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
@Table(name = "AIR_SCREEN_BEHAVIOR")
@org.hibernate.annotations.Table(appliesTo = "AIR_SCREEN_BEHAVIOR", indexes = {
		@Index(name = "AIR_SCREEN_BEHAVIOR_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class AirScreenBehavior implements HmBo {

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

	public static final short TYPE_RECONNECTION = 1;
	public static EnumItem[] TYPE = MgrUtil.enumItems("enum.as.behavior.type.",
			new int[] { TYPE_RECONNECTION });
	private short type = TYPE_RECONNECTION;

	public static final short CONNECTION_CASE_ANY = 1;
	public static final short CONNECTION_CASE_FAILURE = 2;
	public static final short CONNECTION_CASE_SUCCESS = 3;
	public static EnumItem[] CONNECTION_CASE = MgrUtil.enumItems(
			"enum.as.behavior.connection.case.", new int[] {
					CONNECTION_CASE_ANY, CONNECTION_CASE_FAILURE,
					CONNECTION_CASE_SUCCESS });
	private short connectionCase = CONNECTION_CASE_ANY;

	@Range(min = 1, max = 2147483647)
	private int interval = 60; // Note: This value should be divisible by 5

	@Range(min = 1, max = 2147483647)
	private int threshold = 10;

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

	public short getConnectionCase() {
		return connectionCase;
	}

	public void setConnectionCase(short connectionCase) {
		this.connectionCase = connectionCase;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof AirScreenBehavior
				&& (null == id ? super.equals(other) : id
						.equals(((AirScreenBehavior) other).getId()));
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
		return MgrUtil.getEnumString("enum.as.behavior.type." + type);
	}
	
	@Transient
	public String getCaseString() {
		return MgrUtil.getEnumString("enum.as.behavior.connection.case." + connectionCase);
	}

}