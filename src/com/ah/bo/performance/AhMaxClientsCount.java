package com.ah.bo.performance;

import java.sql.Timestamp;

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

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "max_clients_count")
@org.hibernate.annotations.Table(appliesTo = "max_clients_count", indexes = {
		@Index(name = "MAX_CLIENTS_COUNT_OWNER", columnNames = { "OWNER" }),
		@Index(name = "MAX_CLIENTS_COUNT_TIME", columnNames = { "timeStamp" })
		})
public class AhMaxClientsCount implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long		id;

	private int			maxClientCount = 0;
	
	private int			currentClientCount = 0;

	private long		client24Count;

	private long		client5Count;

	private long 		clientwiredCount;
	
	private long 		totalCount;

	private boolean		globalFlg;
	
	private long		timeStamp = System.currentTimeMillis();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain	owner;

	@Version
	private Timestamp		version;

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public String getLabel() {
		return "maxclientcount";
	}

	@Transient
	private boolean	selected;

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

	@Override
	public void setVersion(Timestamp version) {

	}

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public int getMaxClientCount() {
		return maxClientCount;
	}

	public void setMaxClientCount(int maxClientCount) {
		this.maxClientCount = maxClientCount;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public int getCurrentClientCount() {
		return currentClientCount;
	}

	public void setCurrentClientCount(int currentClientCount) {
		this.currentClientCount = currentClientCount;
	}

	/**
	 * @return the globalFlg
	 */
	public boolean getGlobalFlg() {
		return globalFlg;
	}

	/**
	 * @param globalFlg the globalFlg to set
	 */
	public void setGlobalFlg(boolean globalFlg) {
		this.globalFlg = globalFlg;
	}

	public long getClient24Count() {
		return client24Count;
	}

	public void setClient24Count(long client24Count) {
		this.client24Count = client24Count;
	}

	public long getClient5Count() {
		return client5Count;
	}

	public void setClient5Count(long client5Count) {
		this.client5Count = client5Count;
	}

	public long getClientwiredCount() {
		return clientwiredCount;
	}

	public void setClientwiredCount(long clientwiredCount) {
		this.clientwiredCount = clientwiredCount;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

}