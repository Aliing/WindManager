package com.ah.bo.admin;

import java.sql.Timestamp;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;
import com.ah.util.datetime.AhDateTimeUtil;

/**
 * @author xiaolanbao
 */

@Entity
@Table(name = "HM_SYSTEMLOG")
@org.hibernate.annotations.Table(appliesTo = "HM_SYSTEMLOG", indexes = {
		@Index(name = "HM_SYSTEM_LOG_OWNER", columnNames = { "OWNER" }),
		@Index(name = "HM_SYSTEM_LOG_TIME", columnNames = { "LOGTIMESTAMP" })
		})
public class HmSystemLog implements HmBo {

	private static final long serialVersionUID = 1L;

	public static final short MAX_SYSLOG_LENGTH = 512;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Transient
	private boolean selected = false;

	public static final short LEVEL_CRITICAL = 1;

	public static final short LEVEL_MAJOR = 2;

	public static final short LEVEL_MINOR = 3;

	private short level;

	public static final String FEATURE_TOPOLOGY = "Topology";

	public static final String FEATURE_MONITORING = "Monitoring";

	public static final String FEATURE_HIVEAPS = NmsUtil.getOEMCustomer().getAccessPonitName() + "s";

	public static final String FEATURE_CONFIGURATION = "Configuration";

	public static final String FEATURE_ADMINISTRATION = "Administration";

	public static final String FEATURE_DISCOVERY = "Discovery";

	public static final String FEATURE_AIRTIGHT_INTEGRATION = "AirTight-Integration";

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String source;

	@Column(length = MAX_SYSLOG_LENGTH)
	private String systemComment;

	private long logTimeStamp;
	
	private String logTimeZone = TimeZone.getDefault().getID();
	
	public void setLevel(short sLevel) {
		this.level = sLevel;
	}
	
	public static final String LEVEL_CRITICAL_SHOW = "Critical";

	public static final String LEVEL_MAJOR_SHOW = "Major";

	public static final String LEVEL_MINOR_SHOW = "Minor";

	public String getLevel() {
		switch (level) {
		case LEVEL_CRITICAL:
			return LEVEL_CRITICAL_SHOW;
		case LEVEL_MAJOR:
			return LEVEL_MAJOR_SHOW;
		case LEVEL_MINOR:
			return LEVEL_MINOR_SHOW;
		default:
			return "N/A";
		}
	}

	public void setSource(String strSrc) {
		this.source = strSrc;
	}

	public String getSource() {
		return this.source;
	}

	public void setSystemComment(String strComment) {
		this.systemComment = strComment;
	}

	public String getSystemComment() {
		return this.systemComment;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return "systemlog";
	}

	/**
	 * No owner field, because system log is not always driven by user & i have
	 * no idea to get user context in be<br>
	 * <br>
	 * mark: exception when find bo and bo without owner field.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = true)
	private HmDomain owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	/**
	 * logTime keep track of when log, and system log obj can not changed, so
	 * version is unnecessary
	 */
	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Version
	private Timestamp version;

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public long getLogTimeStamp() {
		return logTimeStamp;
	}

	public void setLogTimeStamp(long logTimeStamp) {
		this.logTimeStamp = logTimeStamp;
	}

	public String getLogTimeZone() {
		return logTimeZone;
	}

	public void setLogTimeZone(String logTimeZone) {
		this.logTimeZone = logTimeZone;
	}

	public String getLogTime() {
		return AhDateTimeUtil.getSpecifyDateTime(logTimeStamp, TimeZone.getTimeZone(logTimeZone));
	}

}