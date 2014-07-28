package com.ah.bo.network;

import java.sql.Timestamp;

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

@Entity
@Table(name = "BONJOUR_SERVICE_CATEGORY")
@org.hibernate.annotations.Table(appliesTo = "BONJOUR_SERVICE_CATEGORY", indexes = {
		@Index(name = "BONJOUR_SERVICE_CATEGORY_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class BonjourServiceCategory implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Version
	private Timestamp version;
	
	public static final String SERVICE_CATEGORY_ALL="All";
	public static final String SERVICE_CATEGORY_PRINTING="Printing";
	public static final String SERVICE_CATEGORY_FILE_SHARING="File Sharing";
	public static final String SERVICE_CATEGORY_MEDIA="Media Services";
	public static final String SERVICE_CATEGORY_INSTANT_MESSAGEING="Instant Messaging";
	public static final String SERVICE_CATEGORY_LOGIN="Login";
	public static final String SERVICE_CATEGORY_AEROHIVE="Aerohive Services";
	public static final String SERVICE_CATEGORY_CUSTIOM="Custom Services";
	public static final String SERVICE_CATEGORY_WILDCARD_SERVICES="Wildcard Services";

	private String serviceCategoryName;
	
	public String getServiceCategoryName() {
		return serviceCategoryName;
	}

	public void setServiceCategoryName(String serviceCategoryName) {
		this.serviceCategoryName = serviceCategoryName;
	}
	
	@Transient
	public static String[] getServiceCategory(){
		return new String[]{
				SERVICE_CATEGORY_ALL,SERVICE_CATEGORY_WILDCARD_SERVICES,SERVICE_CATEGORY_PRINTING,SERVICE_CATEGORY_FILE_SHARING,
				SERVICE_CATEGORY_MEDIA,SERVICE_CATEGORY_INSTANT_MESSAGEING,SERVICE_CATEGORY_LOGIN,
				SERVICE_CATEGORY_AEROHIVE,SERVICE_CATEGORY_CUSTIOM
		};
	}

	@Override
	public boolean equals(Object osObject) {
		if (!(osObject instanceof BonjourServiceCategory)) {
			return false;
		}
		return null == id ? super.equals(osObject) : id.equals(((BonjourServiceCategory) osObject).getId());
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
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
	public String getLabel() {
		return serviceCategoryName;
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
	public BonjourServiceCategory clone() {
		try {
			return (BonjourServiceCategory) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
