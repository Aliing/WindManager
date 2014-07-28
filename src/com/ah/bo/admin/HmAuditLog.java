package com.ah.bo.admin;

import java.sql.Timestamp;
import java.util.TimeZone;

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

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.util.datetime.AhDateTimeUtil;

@Entity
@Table(name = "HM_AUDITLOG")
@org.hibernate.annotations.Table(appliesTo = "HM_AUDITLOG", indexes = {
		@Index(name = "HM_AUDIT_LOG_OWNER", columnNames = { "OWNER" }),
		@Index(name = "HM_AUDIT_LOG_TIME", columnNames = { "LOGTIMESTAMP" })
		})
public class HmAuditLog implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(length = 256)
	private String opeationComment;

	@Column(length = IP_ADDRESS_LENGTH)
	private String hostIP;

	public static final short STATUS_SUCCESS = 0;

	public static final short STATUS_FAILURE = 1;

	public static final short STATUS_EXECUTE = 2;

	private short status = STATUS_SUCCESS;

	private long logTimeStamp;
	
	private String logTimeZone = TimeZone.getDefault().getID();

	public String getHostIP() {
		return hostIP;
	}

	public void setHostIP(String hostIP) {
		this.hostIP = hostIP;
	}

	public String getLogTime() {
		return AhDateTimeUtil.getSpecifyDateTime(logTimeStamp, TimeZone.getTimeZone(logTimeZone), owner);
	}

	public String getOpeationComment() {
		return opeationComment;
	}

	public void setOpeationComment(String opeationComment) {
		this.opeationComment = opeationComment;
	}

	public short getStatus() {
		return status;
	}

	public void setStatus(short status) {
		this.status = status;
	}

	private String userOwner;

	public String getUserOwner() {
		return userOwner;
	}

	public void setUserOwner(String userOwner) {
		this.userOwner = userOwner;
	}

	@Override
	public Long getId() {
		return id;
	}

	// get status column show
	public String getStatusStr() {
		switch (status) {
		case STATUS_SUCCESS:
			return "SUCCESS";
		case STATUS_FAILURE:
			return "FAILURE";
		case STATUS_EXECUTE:
			return "EXECUTED";
		default:
			return "N/A";
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

	@Override
	public String getLabel() {
		return opeationComment;
	}

	/**
	 * auditlog objects have no owner or version,because it can't be updated and
	 * 'userid'&'logtime' keep track of who&when create it. <br>
	 * <br>
	 * modify mark: I remove user field,create owner field to keep track of
	 * which user create it. 'user' field incurr the problem that created new
	 * user can't be deleted.
	 */
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
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setId(Long id)
	{
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

}