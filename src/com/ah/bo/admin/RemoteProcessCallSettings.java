package com.ah.bo.admin;

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

import org.hibernate.validator.constraints.Range;

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;

/**
 * @author Yunzhi Lin
 *
 */
@Entity
@Table(name = "RPC_SETTINGS")
public class RemoteProcessCallSettings implements HmBo {
	
	private static final long	serialVersionUID	= 1L;
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Version
	private Timestamp version;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false, unique = true)
	private HmDomain	owner;
	
	@Column(length = DEFAULT_STRING_LENGTH)
	private String userName;
	
	@Column(length = DEFAULT_STRING_LENGTH)
	private String password;
	
	public static final int DEFAULT_OVERTIME = 30; // minute
	
	public final static int MIN_OVERTIME = 15;
	public final static int MAX_OVERTIME = 120;
	
	@Range(min = MIN_OVERTIME, max = MAX_OVERTIME)
	private int timeout = DEFAULT_OVERTIME;
	
	private boolean enabled;

	/**
	 * @return the id
	 */
	@Override
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the version
	 */
	@Override
	public Timestamp getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	/**
	 * @return the owner
	 */
	@Override
	public HmDomain getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the timeout
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@Override
	public String getLabel() {
		return NmsUtil.getOEMCustomer().getNmsNameAbbreviation() + 
		" RPC Settings";
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

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled 
	 *		the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}