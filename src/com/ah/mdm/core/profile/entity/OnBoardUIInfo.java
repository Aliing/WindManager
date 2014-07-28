package com.ah.mdm.core.profile.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@SuppressWarnings("serial")
@XStreamAlias("OnBoardUIInfo")
public class OnBoardUIInfo implements HmBo,Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private HmDomain owner;
	
	@Version
	private Timestamp version;	
	
	@Transient
	private boolean selected;	
	

	public Timestamp getVersion() {
		return version;
	}

	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Transient
	@XStreamAlias("PageName")
	private String pageName;
	
	@Transient
	@XStreamAlias("LogoImage")
	private String logoImage;
	
	@Transient
	@XStreamAlias("HorMainImage")
	private String horMainImage;
	
	@Transient
	@XStreamAlias("VerMainImage")
	private String verMainImage;
	
	@Transient
	@XStreamAlias("ClientInfoTitle")
	private String clientInfoTitle;
	
	@Transient
	@XStreamAlias("UserNameLabel")
	private String userNameLabel;
	
	@Transient
	@XStreamAlias("OwnerShipLabel")
	private String ownerShipLabel;
	
	@Transient
	@XStreamAlias("CidText")
	private String cidText;
	
	@Transient
	@XStreamAlias("ByodLabel")
	private String byodLabel;
	
	@Transient
	@XStreamAlias("CidLabel")
	private String cidLabel;
	
	@Transient
	@XStreamAlias("TargetUrl")
	private String targetUrl;
	@Transient
	@XStreamAlias("WelcomeText")
	private String welcomeText;
	
	@Transient
	@XStreamAlias("AgreementText")
	private String agreementText;
	
	@Transient
	@XStreamAlias("ByodText")
	private String byodText;



	public HmDomain getOwner() {
		return owner;
	}

	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setId(Long id) {
		// TODO Auto-generated method stub
		
	}

	public String getLogoImage() {
		return logoImage;
	}

	public void setLogoImage(String logoImage) {
		this.logoImage = logoImage;
	}

	public String getHorMainImage() {
		return horMainImage;
	}

	public void setHorMainImage(String horMainImage) {
		this.horMainImage = horMainImage;
	}

	public String getVerMainImage() {
		return verMainImage;
	}

	public void setVerMainImage(String verMainImage) {
		this.verMainImage = verMainImage;
	}

	public String getClientInfoTitle() {
		return clientInfoTitle;
	}

	public void setClientInfoTitle(String clientInfoTitle) {
		this.clientInfoTitle = clientInfoTitle;
	}


	public String getOwnerShipLabel() {
		return ownerShipLabel;
	}

	public void setOwnerShipLabel(String ownerShipLabel) {
		this.ownerShipLabel = ownerShipLabel;
	}

	public String getCidText() {
		return cidText;
	}

	public void setCidText(String cidText) {
		this.cidText = cidText;
	}

	public String getByodLabel() {
		return byodLabel;
	}

	public void setByodLabel(String byodLabel) {
		this.byodLabel = byodLabel;
	}

	public String getCidLabel() {
		return cidLabel;
	}

	public void setCidLabel(String cidLabel) {
		this.cidLabel = cidLabel;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public String getWelcomeText() {
		return welcomeText;
	}

	public void setWelcomeText(String welcomeText) {
		this.welcomeText = welcomeText;
	}

	public String getAgreementText() {
		return agreementText;
	}

	public void setAgreementText(String agreementText) {
		this.agreementText = agreementText;
	}

	public String getByodText() {
		return byodText;
	}

	public void setByodText(String byodText) {
		this.byodText = byodText;
	}

	public String getUserNameLabel() {
		return userNameLabel;
	}

	public void setUserNameLabel(String userNameLabel) {
		this.userNameLabel = userNameLabel;
	}






}
