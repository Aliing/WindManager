/**
 *@filename		AccessConsole.java
 *@version
 *@author		LiangWenping
 *@createtime	2012-2-2 PM 01:35:37
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
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

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author		LiangWenping
 * @version		V1.0.0.0 
 */
@Entity
@Table(name = "PPPOE")
@org.hibernate.annotations.Table(appliesTo = "PPPOE", indexes = {
		@Index(name = "PPPOE_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class PPPoE implements HmBo {
	
	private static final long	serialVersionUID	= 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Version
	private Timestamp version;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String pppoeName;
	
	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;
	
	private String username;
	
	private String password;
	
	private String domain;
	
	public static final short ENCRYPTION_METHOD_CHAP = 0;
	
	public static final short ENCRYPTION_METHOD_PAP = 1;
	
	public static final short ENCRYPTION_METHOD_ANY = 2;
	
	public static EnumItem[] ENUM_ENCRYPTION_METHOD = MgrUtil.enumItems("enum.encryption.method.",
			new int[] { ENCRYPTION_METHOD_CHAP, ENCRYPTION_METHOD_PAP, 
			ENCRYPTION_METHOD_ANY });
	
	private short encryptionMethod = ENCRYPTION_METHOD_CHAP;
	
	public String getPppoeName() {
		return pppoeName;
	}

	public void setPppoeName(String pppoeName) {
		this.pppoeName = pppoeName;
	}
	
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public short getEncryptionMethod() {
		return encryptionMethod;
	}

	public void setEncryptionMethod(short encryptionMethod) {
		this.encryptionMethod = encryptionMethod;
	}

	/**************** Override Method *****************/
	@Override
	public Long getId() {
		return this.id;
	}

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
	public String getLabel() {
		return pppoeName;
	}

	@Transient
	private boolean selected;

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
		this.version = version;
	}

	@Override
	public PPPoE clone() {
		try {
			return (PPPoE) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	@Transient
	public String getStrEncryptionMethod() {
		switch (encryptionMethod) {
			case ENCRYPTION_METHOD_CHAP:
				return "CHAP";
			case ENCRYPTION_METHOD_PAP:
				return "PAP";
			case ENCRYPTION_METHOD_ANY:
				return "Any";
			default:
				return "Unknown";
		}
	}
	
	@Transient
	public String getStrPasswordString() {
			return "********";
	}

}