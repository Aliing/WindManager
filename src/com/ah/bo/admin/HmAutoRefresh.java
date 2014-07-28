package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;

@Entity
@Table(name = "hm_autorefresh_settings_new", uniqueConstraints = { @UniqueConstraint(columnNames = {"useremail", "tableId", "position" })})
@org.hibernate.annotations.Table(appliesTo = "hm_autorefresh_settings_new", indexes = {
		@Index(name = "hm_autorefresh_settings_new_useremail_tableid", columnNames = { "useremail", "tableid" })
		})
public class HmAutoRefresh implements HmBo {

	private static final long serialVersionUID = 1L;

	public HmAutoRefresh() {

	}

	public HmAutoRefresh(int tableId, boolean autoRefresh,String refInterval) {
		this.tableId = tableId;
		this.autoRefresh = autoRefresh;
		this.refInterval = refInterval;
	}

	@Id
	@GeneratedValue
	private Long id;

	@Version
	private Timestamp version;

	private int tableId;

	private int	position;

	private boolean autoRefresh;

	@Column(length = 128, nullable = false)
	private String useremail;

	/*refresh interval for page*/
	private String refInterval;
	/*refresh interval default value*/
	public static final String DEFAULT_INTERVAL="30";

	public int getTableId() {
		return tableId;
	}

	public void setTableId(int tableId) {
		this.tableId = tableId;
	}

	public boolean isAutoRefresh() {
		return autoRefresh;
	}

	public void setAutoRefresh(boolean autoRefresh) {
		this.autoRefresh = autoRefresh;
	}

	public String getRefInterval() {
		return refInterval;
	}

	public void setRefInterval(String refInterval) {
		this.refInterval = refInterval;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof HmAutoRefresh
					&& ((HmAutoRefresh)other).getTableId() == tableId
					&& ((HmAutoRefresh)other).isAutoRefresh() == autoRefresh;
	}

	@Override
	public int hashCode() {
		if (autoRefresh) {
			return 1;
		}
		return 0;
	}

	public String getUseremail() {
		return useremail;
	}

	public void setUseremail(String useremail) {
		this.useremail = useremail;
	}

	@Override
	public HmDomain getOwner() {
		return null;
	}

	@Override
	public void setOwner(HmDomain owner) {
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
		return false;
	}

	@Override
	public void setSelected(boolean selected) {
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
