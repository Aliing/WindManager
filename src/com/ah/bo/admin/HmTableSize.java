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
@Table(name = "hm_table_size_new", uniqueConstraints = { @UniqueConstraint(columnNames = {"useremail", "tableId", "position" })})
@org.hibernate.annotations.Table(appliesTo = "hm_table_size_new", indexes = {
		@Index(name = "hm_table_size_new_useremail_tableid", columnNames = { "useremail", "tableid" })
		})
public class HmTableSize implements HmBo {

	private static final long serialVersionUID = 1L;

	public HmTableSize(int tableId, int tableSize) {
		this.tableId = tableId;
		this.tableSize = tableSize;
	}

	public HmTableSize(){
		// default constructor
	}

	@Id
	@GeneratedValue
	private Long id;

	@Version
	private Timestamp version;

	/**
	 * Re-use the ID definition in HmTableColumn.java
	 */
	private int tableId;

	private int tableSize;

	private int	position;

	@Column(length = 128, nullable = false)
	private String useremail;

	public int getTableId() {
		return tableId;
	}

	public void setTableId(int tableId) {
		this.tableId = tableId;
	}

	public int getTableSize() {
		return tableSize;
	}

	public void setTableSize(int tableSize) {
		this.tableSize = tableSize;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof HmTableSize
				&& tableId == ((HmTableSize) other).getTableId();
	}

	@Override
	public int hashCode() {
		return tableSize;
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
