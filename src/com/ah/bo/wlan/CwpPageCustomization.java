/**
 * @filename			CwpPageCustomization.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.2
 * 
 * Copyright (c) 2006-2008 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.bo.wlan;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Transient;

import com.ah.be.common.NmsUtil;
import com.ah.be.resource.BeResModule_CWPImpl;

/**
 * BO for CWP web page customization
 */
@Embeddable
public class CwpPageCustomization implements Serializable {

	private static final long	serialVersionUID	= 1L;
	
	public final static int FILE_NAME_LENGTH		= 32;
	
	public final static int TEXT_NOTICE_LENGTH		= 256;
	
	public static final int MAX_LANGUAGE_SUPPORT=9;
	
	public final static String DEFAULT_USER_POLICY	= "aerohive_use_policy.txt";
	
	@Transient
	private String userPolicy;
	
	public final static String DEFAULT_BACKGROUND_IMAGE_LIGHT = "default_hex_light.jpg";
	
	public final static String DEFAULT_BACKGROUND_IMAGE_DARK = "default_hex_dark.jpg";
	
	public final static String DEFAULT_BACKGROUND_IMAGE_3D = NmsUtil.isHMForOEM() ? "default_3d.jpg" : "default_hive.png";
	
	public final static String DEFAULT_HEAD_IMAGE	= "default_spacer.png";
	
	public final static String DEFAULT_3D_BACKGROUND_IMAGE = "default_3d_bg.png";
	
	public final static String DEFAULT_FOOT_IMAGE	= "company_logo.png";
	
	public final static String DEFAULT_FOREGROUND_COLOR	= "#FFFFFF";

	@Column(length = FILE_NAME_LENGTH)
	private String backgroundImage;
	
	@Column(length = FILE_NAME_LENGTH)
	private String headImage;
	
	@Column(length = FILE_NAME_LENGTH)
	private String footImage;
	
	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name="mapkey")
	@CollectionTable(name = "CWP_PAGE_FIELD", joinColumns = @JoinColumn(name = "CWP_PAGE_CUSTOMIZATION_ID", nullable = true))
	private Map<String, CwpPageField> fields = new HashMap<String, CwpPageField>();
	
	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name="mapkey")
	@CollectionTable(name = "CWP_PAGE_MULTI_LANGUAGE_RES", joinColumns = @JoinColumn(name = "CWP_PAGE_MULTI_LANGUAGE_RES_ID", nullable = true))
	private Map<Integer, CwpPageMultiLanguageRes> multiLanguageRes = new HashMap<Integer, CwpPageMultiLanguageRes>();
	
	
	public Map<Integer, CwpPageMultiLanguageRes> getMultiLanguageRes() {
		return multiLanguageRes;
	}

	public void setMultiLanguageRes(
			Map<Integer, CwpPageMultiLanguageRes> multiLanguageRes) {
		this.multiLanguageRes = multiLanguageRes;
	}
	
	public CwpPageMultiLanguageRes getCwpPageMultiLanguageRes(Integer language){
		return multiLanguageRes.get(language);
	}

	public final static String DEFAULT_SUCCESS_NOTICE	= "You are now connected to the wireless network.";
	
	public final static String DEFAULT_LIB_SIP_STATUS	= "Welcome, SIP_FIELD_AE. \r\n\r\nYou have SIP_FIXED_OI overdue items and SIP_FIXED_HI items on hold on the hold-shelf."; 
								
	public final static String DEFAULT_LIB_SIP_FINES	= "Welcome, SIP_FIELD_AE. \r\n\r\nYou owe SIP_FIELD_BH SIP_FIELD_BV in fines." ; 
								
	public final static String DEFAULT_LIB_SIP_BLOCK	= "Welcome, SIP_FIELD_AE. \r\n\r\nYour library card is blocked because you owe SIP_FIELD_BH SIP_FIELD_BV in fines." ; 

//	@Column(length = 256)
//	private String successNotice;
//	
//	@Column(length = 256)
//	private String successLibrarySIPStatus;
//	
//	@Column(length = 256)
//	private String successLibrarySIPFines;
	
	@Column(length = FILE_NAME_LENGTH)
	private String successBackgroundImage;
	
	@Column(length = FILE_NAME_LENGTH)
	private String successHeadImage;
	
	@Column(length = FILE_NAME_LENGTH)
	private String successFootImage;
	
	@Column(length = FILE_NAME_LENGTH)
	private String foregroundColor;
	
	@Column(length = FILE_NAME_LENGTH)
	private String successForegroundColor;
	
	@Column(length = FILE_NAME_LENGTH)
	private String failureBackgroundImage;
	
	@Column(length = FILE_NAME_LENGTH)
	private String failureHeadImage;
	
	@Column(length = FILE_NAME_LENGTH)
	private String failureFootImage;
	
	@Column(length = FILE_NAME_LENGTH)
	private String failureForegroundColor;
	
//	@Column(length = 256)
//	private String failureLibrarySIPFines;
	
	
	public final static Object[][] FOREGROUND_COLOR = {{"black", Color.black},
														{"white", Color.white},
														{"gray", Color.gray},
														{"yellow", Color.yellow},
														{"green", Color.green},
														{"blue", Color.blue},
														{"red", Color.red},
														{"pink", Color.pink},
														{"cyan", Color.cyan},
														{"orange", Color.orange},
														{"magenta", Color.magenta}};
	
	/*
	 * to tile background image or not
	 */
	public static boolean DEFAULT_BACKGROUND_TILES = false;
	
	private boolean tileBackgroundImage = DEFAULT_BACKGROUND_TILES;
	
	private boolean tileSuccessBackgroundImage = DEFAULT_BACKGROUND_TILES;
	
	private boolean tileFailureBackgroundImage = DEFAULT_BACKGROUND_TILES;
	
	private boolean successLibrarySIP;
	
	private boolean failureLibrarySIP;
	
	public CwpPageCustomization(){
		
	}
	
	public CwpPageCustomization(boolean idmSelfRegOpen, boolean idmSelfRegPpsk) {
		setBackgroundImage(DEFAULT_BACKGROUND_IMAGE_3D);
		setHeadImage(DEFAULT_HEAD_IMAGE);
		setFootImage(DEFAULT_FOOT_IMAGE);
//		setUserPolicy(DEFAULT_USER_POLICY);
		setSuccessBackgroundImage(DEFAULT_BACKGROUND_IMAGE_3D);
		setSuccessHeadImage(DEFAULT_HEAD_IMAGE);
		setSuccessFootImage(DEFAULT_FOOT_IMAGE);
//		setSuccessNotice(DEFAULT_SUCCESS_NOTICE);
//		setSuccessLibrarySIPStatus(DEFAULT_LIB_SIP_STATUS);
//		setSuccessLibrarySIPFines(DEFAULT_LIB_SIP_FINES);
		setForegroundColor(DEFAULT_FOREGROUND_COLOR);
		setSuccessForegroundColor(DEFAULT_FOREGROUND_COLOR);
		setFailureBackgroundImage(DEFAULT_BACKGROUND_IMAGE_3D);
		setFailureForegroundColor(DEFAULT_FOREGROUND_COLOR);
		setFailureHeadImage(DEFAULT_HEAD_IMAGE);
		setFailureFootImage(DEFAULT_FOOT_IMAGE);
//		setFailureLibrarySIPFines(DEFAULT_LIB_SIP_BLOCK);
		initMultiLanguageRes();
		initMultiLanguageFields(idmSelfRegOpen, idmSelfRegPpsk);
	}
//
	/**
	 * getter of userPolicy
	 * @return the userPolicy
	 */
	public String getUserPolicy() {
		return userPolicy;
	}

//	/**
//	 * setter of userPolicy
//	 * @param userPolicy the userPolicy to set
//	 */
	public void setUserPolicy(String userPolicy,int language) {
		getCwpPageMultiLanguageRes(language).setUserPolicy(userPolicy);
	}

	/**
	 * getter of headImage
	 * @return the headImage
	 */
	public String getHeadImage() {
		return headImage;
	}

	/**
	 * setter of headImage
	 * @param headImage the headImage to set
	 */
	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}

	/**
	 * getter of footImage
	 * @return the footImage
	 */
	public String getFootImage() {
		return footImage;
	}

	/**
	 * setter of footImage
	 * @param footImage the footImage to set
	 */
	public void setFootImage(String footImage) {
		this.footImage = footImage;
	}

//	/**
//	 * getter of successNotice
//	 * @return the successNotice
//	 */
//	public String getSuccessNotice() {
//		return successNotice;
//	}
//
//	/**
//	 * setter of successNotice
//	 * @param successNotice the successNotice to set
//	 */
	public void setSuccessNotice(String successNotice,int language) {
		getCwpPageMultiLanguageRes(language).setSuccessNotice(successNotice);
	}

//	/**
//	 * getter of successLibrarySIPStatus
//	 * @return the successLibrarySIPStatus
//	 */
	public String getSuccessLibrarySIPStatus() {
		return DEFAULT_LIB_SIP_STATUS;
	}
//
//	/**
//	 * setter of successLibrarySIPStatus
//	 * @param successLibrarySIPStatus the successLibrarySIPStatus to set
//	 */
	public void setSuccessLibrarySIPStatus(String successLibrarySIPStatus,int language) {
		getCwpPageMultiLanguageRes(language).setSuccessLibrarySIPStatus(successLibrarySIPStatus);
	}

//	/**
//	 * getter of successLibrarySIPFines
//	 * @return the successLibrarySIPFines
//	 */
	public String getSuccessLibrarySIPFines() {
		return DEFAULT_LIB_SIP_FINES;
	}

	/**
	 * setter of successLibrarySIPFines
	 * @param successLibrarySIPFines the successLibrarySIPFines to set
	 */
	public void setSuccessLibrarySIPFines(String successLibrarySIPFines,int language) {
		getCwpPageMultiLanguageRes(language).setSuccessLibrarySIPFines(successLibrarySIPFines);
//		this.successLibrarySIPFines = successLibrarySIPFines;
	}

	/**
	 * getter of successHeadImage
	 * @return the successHeadImage
	 */
	public String getSuccessHeadImage() {
		return successHeadImage;
	}

	/**
	 * setter of successHeadImage
	 * @param successHeadImage the successHeadImage to set
	 */
	public void setSuccessHeadImage(String successHeadImage) {
		this.successHeadImage = successHeadImage;
	}

	/**
	 * getter of successFootImage
	 * @return the successFootImage
	 */
	public String getSuccessFootImage() {
		return successFootImage;
	}

	/**
	 * setter of successFootImage
	 * @param successFootImage the successFootImage to set
	 */
	public void setSuccessFootImage(String successFootImage) {
		this.successFootImage = successFootImage;
	}
	
	/**
	 * getter of fields
	 * @return the fields
	 */
	public Map<String, CwpPageField> getFields() {
		return fields;
	}

	/**
	 * setter of fields
	 * @param fields the fields to set
	 */
	public void setFields(Map<String, CwpPageField> fields) {
		this.fields = fields;
	}
	
	public CwpPageField getPageField(String field) {
		return fields.get(field);
	}
	
	/**
	 * getter of backgroundImage
	 * @return the backgroundImage
	 */
	public String getBackgroundImage() {
		return backgroundImage;
	}

	/**
	 * setter of backgroundImage
	 * @param backgroundImage the backgroundImage to set
	 */
	public void setBackgroundImage(String backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	/**
	 * get the list of CwpPageField sorted by order
	 * @return -
	 * @author Joseph Chen
	 */
	public List<CwpPageField> getPageFields() {
		if(this.fields == null) {
			return null;
		}
		
		List<CwpPageField> listFields = new ArrayList<CwpPageField>();
		
		for(CwpPageField field : this.fields.values()) {
			listFields.add(field);
		}
		
		Collections.sort(listFields, new CwpPageFieldComparator());
		
		return listFields;
	}

	/**
	 * getter of successBackgroundImage
	 * @return the successBackgroundImage
	 */
	public String getSuccessBackgroundImage() {
		return successBackgroundImage;
	}

	/**
	 * setter of successBackgroundImage
	 * @param successBackgroundImage the successBackgroundImage to set
	 */
	public void setSuccessBackgroundImage(String successBackgroundImage) {
		this.successBackgroundImage = successBackgroundImage;
	}

	/**
	 * getter of foregroundColor
	 * @return the foregroundColor
	 */
	public String getForegroundColor() {
		return foregroundColor;
	}

	/**
	 * setter of foregroundColor
	 * @param foregroundColor the foregroundColor to set
	 */
	public void setForegroundColor(String foregroundColor) {
		this.foregroundColor = foregroundColor;
	}

	/**
	 * getter of successForegroundColor
	 * @return the successForegroundColor
	 */
	public String getSuccessForegroundColor() {
		return successForegroundColor;
	}

	/**
	 * setter of successForegroundColor
	 * @param successForegroundColor the successForegroundColor to set
	 */
	public void setSuccessForegroundColor(String successForegroundColor) {
		this.successForegroundColor = successForegroundColor;
	}
	
	/**
	 * getter of tileBackgroundImage
	 * @return the tileBackgroundImage
	 */
	public boolean getTileBackgroundImage() {
		return tileBackgroundImage;
	}

	/**
	 * setter of tileBackgroundImage
	 * @param tileBackgroundImage the tileBackgroundImage to set
	 */
	public void setTileBackgroundImage(boolean tileBackgroundImage) {
		this.tileBackgroundImage = tileBackgroundImage;
	}

	/**
	 * getter of tileSuccessBackgroundImage
	 * @return the tileSuccessBackgroundImage
	 */
	public boolean getTileSuccessBackgroundImage() {
		return tileSuccessBackgroundImage;
	}

	/**
	 * setter of tileSuccessBackgroundImage
	 * @param tileSuccessBackgroundImage the tileSuccessBackgroundImage to set
	 */
	public void setTileSuccessBackgroundImage(boolean tileSuccessBackgroundImage) {
		this.tileSuccessBackgroundImage = tileSuccessBackgroundImage;
	}
	
	
	
	/**
	 * getter of tileFailureBackgroundImage
	 * @return the tileFailureBackgroundImage
	 */
	public boolean isTileFailureBackgroundImage() {
		return tileFailureBackgroundImage;
	}

	/**
	 * setter of tileFailureBackgroundImage
	 * @param tileFailureBackgroundImage the tileFailureBackgroundImage to set
	 */
	public void setTileFailureBackgroundImage(boolean tileFailureBackgroundImage) {
		this.tileFailureBackgroundImage = tileFailureBackgroundImage;
	}

	@Transient
	public String getTileBackgroundString() {
		if(this.tileBackgroundImage) {
			return "repeat";
		} else {
			return "no-repeat";
		}
	}
	
	@Transient
	public String getTileSuccessBackgroundString() {
		if(this.tileSuccessBackgroundImage) {
			return "repeat";
		} else {
			return "no-repeat";
		}
	}
	
	@Transient
	public String getTileFailureBackgroundString() {
		if(this.tileFailureBackgroundImage) {
			return "repeat";
		} else {
			return "no-repeat";
		}
	}

	/**
	 * getter of failureBackgroundImage
	 * @return the failureBackgroundImage
	 */
	public String getFailureBackgroundImage() {
		return failureBackgroundImage;
	}

	/**
	 * setter of failureBackgroundImage
	 * @param failureBackgroundImage the failureBackgroundImage to set
	 */
	public void setFailureBackgroundImage(String failureBackgroundImage) {
		this.failureBackgroundImage = failureBackgroundImage;
	}

	/**
	 * getter of failureLibrarySIP
	 * @return the failureLibrarySIP
	 */
	public boolean isFailureLibrarySIP() {
		return failureLibrarySIP;
	}

	/**
	 * setter of failureLibrarySIP
	 * @param failureLibrarySIP the failureLibrarySIP to set
	 */
	public void setFailureLibrarySIP(boolean failureLibrarySIP) {
		this.failureLibrarySIP = failureLibrarySIP;
	}

	/**
	 * getter of failureHeadImage
	 * @return the failureHeadImage
	 */
	public String getFailureHeadImage() {
		return failureHeadImage;
	}

	/**
	 * setter of failureHeadImage
	 * @param failureHeadImage the failureHeadImage to set
	 */
	public void setFailureHeadImage(String failureHeadImage) {
		this.failureHeadImage = failureHeadImage;
	}

	/**
	 * getter of failureFootImage
	 * @return the failureFootImage
	 */
	public String getFailureFootImage() {
		return failureFootImage;
	}

	/**
	 * setter of failureFootImage
	 * @param failureFootImage the failureFootImage to set
	 */
	public void setFailureFootImage(String failureFootImage) {
		this.failureFootImage = failureFootImage;
	}

	/**
	 * getter of failureForegroundColor
	 * @return the failureForegroundColor
	 */
	public String getFailureForegroundColor() {
		return failureForegroundColor;
	}

	/**
	 * setter of failureForegroundColor
	 * @param failureForegroundColor the failureForegroundColor to set
	 */
	public void setFailureForegroundColor(String failureForegroundColor) {
		this.failureForegroundColor = failureForegroundColor;
	}

	/**
	 * getter of successLibrarySIP
	 * @return the successLibrarySIP
	 */
	public boolean isSuccessLibrarySIP() {
		return successLibrarySIP;
	}

	/**
	 * setter of successLibrarySIP
	 * @param successLibrarySIP the successLibrarySIP to set
	 */
	public void setSuccessLibrarySIP(boolean successLibrarySIP) {
		this.successLibrarySIP = successLibrarySIP;
	}

	/**
	 * reset login page settings to default
	 * 
	 * @author Joseph Chen
	 */
	public void resetLoginPage() {
		setBackgroundImage(DEFAULT_BACKGROUND_IMAGE_3D);
		setHeadImage(DEFAULT_HEAD_IMAGE);
		setFootImage(DEFAULT_FOOT_IMAGE);
	//	setUserPolicy(DEFAULT_USER_POLICY);
		setDefaultUserPolicy();
		setForegroundColor(DEFAULT_FOREGROUND_COLOR);
		setTileBackgroundImage(DEFAULT_BACKGROUND_TILES);
	}
	
	public void resetSuccessPage() {
		setSuccessBackgroundImage(DEFAULT_BACKGROUND_IMAGE_3D);
		setSuccessHeadImage(DEFAULT_HEAD_IMAGE);
		setSuccessFootImage(DEFAULT_FOOT_IMAGE);
//		setSuccessNotice(DEFAULT_SUCCESS_NOTICE);
//		setSuccessLibrarySIPStatus(DEFAULT_LIB_SIP_STATUS);
//		setSuccessLibrarySIPFines(DEFAULT_LIB_SIP_FINES);
		for(int i=1;i<=MAX_LANGUAGE_SUPPORT;i++) {
			CwpPageMultiLanguageRes multiRes=getCwpPageMultiLanguageRes(new Integer(i));
			multiRes.setSuccessLibrarySIPFines(cwpResReader.getString("cwp.preview.customize.default_lib_sip_fines", getLocaleFromPreview(i)));
			multiRes.setSuccessLibrarySIPStatus(cwpResReader.getString("cwp.preview.customize.default_lib_sip_status", getLocaleFromPreview(i)));
			multiRes.setSuccessNotice(cwpResReader.getString("cwp.preview.customize.default_success_notice", getLocaleFromPreview(i)));
		
		}
		
		setSuccessForegroundColor(DEFAULT_FOREGROUND_COLOR);
		setTileSuccessBackgroundImage(DEFAULT_BACKGROUND_TILES);
	}
	
	@Transient
	BeResModule_CWPImpl cwpResReader=new BeResModule_CWPImpl();
	
	private Locale getLocaleFromPreview(int pLanguage ){
		Locale resultLocale;
		switch(pLanguage){
		case 1:
			resultLocale=Locale.ENGLISH;
			break;
		case 2:
			resultLocale=Locale.CHINA;
			break;
		case 3:
			resultLocale=Locale.GERMAN;
			break;
		case 4:
			resultLocale=Locale.FRANCE;
			break;
		case 5:
			resultLocale=Locale.KOREA;
			break;
		case 6:
			resultLocale=new Locale("nl","DU");;
			break;
		case 7:
			resultLocale=new Locale("es","SP");
			break;
		case 8:
			resultLocale=Locale.TAIWAN;
			break;
		case 9:
			resultLocale=Locale.ITALIAN;
			break;
		
			
		default:
				resultLocale=Locale.ENGLISH;
				break;
		}
		return resultLocale;
	}
	
	public void resetFailurePage() {
		setFailureBackgroundImage(DEFAULT_BACKGROUND_IMAGE_3D);
		setFailureHeadImage(DEFAULT_HEAD_IMAGE);
		setFailureFootImage(DEFAULT_FOOT_IMAGE);
		setFailureForegroundColor(DEFAULT_FOREGROUND_COLOR);
		//setFailureLibrarySIPFines(DEFAULT_LIB_SIP_BLOCK);
		setTileFailureBackgroundImage(DEFAULT_BACKGROUND_TILES);

		for(int i=1;i<=MAX_LANGUAGE_SUPPORT;i++) {
			CwpPageMultiLanguageRes multiRes=getCwpPageMultiLanguageRes(new Integer(i));
			multiRes.setFailureLibrarySIPFines(cwpResReader.getString("cwp.preview.customize.default_lib_sip_block", getLocaleFromPreview(i)));
		}
	}
	
//	for(int i=1;i<=MAX_LANGUAGE_SUPPORT;i++) {
//		CwpPageMultiLanguageRes multiRes=pageCustomization.getCwpPageMultiLanguageRes(new Integer(i));
//		
//		if(multiRes == null) {
//			multiRes = new CwpPageMultiLanguageRes();
//			multiRes.setResLanguage(i);
//			
//			multiRes.setFailureLibrarySIPFines(cwpResReader.getString("cwp.preview.customize.default_lib_sip_block", getLocaleFromPreview(i)));
//			multiRes.setSuccessLibrarySIPFines(cwpResReader.getString("cwp.preview.customize.default_lib_sip_fines", getLocaleFromPreview(i)));
//			multiRes.setSuccessLibrarySIPStatus(cwpResReader.getString("cwp.preview.customize.default_lib_sip_status", getLocaleFromPreview(i)));
//			multiRes.setSuccessNotice(cwpResReader.getString("cwp.preview.customize.default_success_notice", getLocaleFromPreview(i)));
//			multiRes.setUserPolicy(cwpResReader.getString("cwp.preview.customize.default_use_policy", getLocaleFromPreview(i)));
//			
//		}

//	/**
//	 * getter of failureLibrarySIPFines
//	 * @return the failureLibrarySIPFines
//	 */
//	public String getFailureLibrarySIPFines() {
//		return failureLibrarySIPFines;
//	}

	/**
	 * setter of failureLibrarySIPFines
	 * @param failureLibrarySIPFines the failureLibrarySIPFines to set
	 */
	public void setFailureLibrarySIPFines(String failureLibrarySIPFines,int language) {
		getCwpPageMultiLanguageRes(language).setFailureLibrarySIPFines(failureLibrarySIPFines);
//		this.failureLibrarySIPFines = failureLibrarySIPFines;
	}
	
	public void setDefaultUserPolicy(){
		for(int i=1;i<=MAX_LANGUAGE_SUPPORT;i++) {
			CwpPageMultiLanguageRes multiRes=getCwpPageMultiLanguageRes(new Integer(i));
			if(multiRes!=null){
//			multiRes.setSuccessLibrarySIPFines(cwpResReader.getString("cwp.preview.customize.default_lib_sip_fines", getLocaleFromPreview(i)));
//			multiRes.setSuccessLibrarySIPStatus(cwpResReader.getString("cwp.preview.customize.default_lib_sip_status", getLocaleFromPreview(i)));
//			multiRes.setSuccessNotice(cwpResReader.getString("cwp.preview.customize.default_success_notice", getLocaleFromPreview(i)));
			multiRes.setUserPolicy(cwpResReader.getString("cwp.preview.customize.default_use_policy", getLocaleFromPreview(i)));
			
		}
		}
	}
	
	public void setDefaultFailureLibrarySIPFines(){
		for(int i=1;i<=MAX_LANGUAGE_SUPPORT;i++) {
			CwpPageMultiLanguageRes multiRes=getCwpPageMultiLanguageRes(new Integer(i));
			if(multiRes!=null){
		    multiRes.setFailureLibrarySIPFines(cwpResReader.getString("cwp.preview.customize.default_lib_sip_block", getLocaleFromPreview(i)));
		}
		}
	}
	public void setDefaultSuccessNotice(){
		for(int i=1;i<=MAX_LANGUAGE_SUPPORT;i++) {
			CwpPageMultiLanguageRes multiRes=getCwpPageMultiLanguageRes(new Integer(i));
			if(multiRes!=null){
				multiRes.setSuccessNotice(cwpResReader.getString("cwp.preview.customize.default_success_notice", getLocaleFromPreview(i)));
					}
		}
		
	}
	
	public void setDefaultSuccessLibrarySIPStatus(){
		for(int i=1;i<=MAX_LANGUAGE_SUPPORT;i++) {
			CwpPageMultiLanguageRes multiRes=getCwpPageMultiLanguageRes(new Integer(i));
			if(multiRes!=null){
				multiRes.setSuccessLibrarySIPStatus(cwpResReader.getString("cwp.preview.customize.default_lib_sip_status", getLocaleFromPreview(i)));
					}
		}
		
	}
	
	public void setDefaultSuccessLibrarySIPFines(){
		for(int i=1;i<=MAX_LANGUAGE_SUPPORT;i++) {
			CwpPageMultiLanguageRes multiRes=getCwpPageMultiLanguageRes(new Integer(i));
			if(multiRes!=null){
				multiRes.setSuccessLibrarySIPFines(cwpResReader.getString("cwp.preview.customize.default_lib_sip_fines", getLocaleFromPreview(i)));
						}
		}
		
	}
	
	private void initMultiLanguageRes() {
		
		Map<Integer, CwpPageMultiLanguageRes> multiLanguageRes = new LinkedHashMap<Integer, CwpPageMultiLanguageRes>();
		
		for(int i=1;i<=MAX_LANGUAGE_SUPPORT;i++) {
			CwpPageMultiLanguageRes multiRes=getCwpPageMultiLanguageRes(new Integer(i));
			
			if(multiRes == null) {
			multiRes = new CwpPageMultiLanguageRes();
				multiRes.setResLanguage(i);
				
				multiRes.setFailureLibrarySIPFines(cwpResReader.getString("cwp.preview.customize.default_lib_sip_block", getLocaleFromPreview(i)));
				
//				multiRes.setFailureLibrarySIPFines(cwpResReader.getString("cwp.preview.customize.default_lib_sip_block", getLocaleFromPreview(i)));
				multiRes.setSuccessLibrarySIPFines(cwpResReader.getString("cwp.preview.customize.default_lib_sip_fines", getLocaleFromPreview(i)));
				multiRes.setSuccessLibrarySIPStatus(cwpResReader.getString("cwp.preview.customize.default_lib_sip_status", getLocaleFromPreview(i)));
				multiRes.setSuccessNotice(cwpResReader.getString("cwp.preview.customize.default_success_notice", getLocaleFromPreview(i)));
				multiRes.setUserPolicy(cwpResReader.getString("cwp.preview.customize.default_use_policy", getLocaleFromPreview(i)));
				
			}
			
			multiLanguageRes.put(new Integer(i), multiRes);
		}
		
		this.setMultiLanguageRes(multiLanguageRes);
	}
	
	public void initMultiLanguageFields(boolean idmSelfRegOpen, boolean idmSelfRegPpsk) {
		Map<String, CwpPageField> fields = new LinkedHashMap<String, CwpPageField>();
		byte order = 1;
		
		for(String field : CwpPageField.FIELDS) {
			CwpPageField newField;
			
		
				newField = new CwpPageField();
				newField.setEnabled(true);
				
				if(idmSelfRegOpen && field.equals(CwpPageField.COMMENT)) {
				    continue;
				} else if(!idmSelfRegOpen && field.equals(CwpPageField.REPRESENTING)) {
				    continue;
				}
				
				if(field.equals(CwpPageField.PHONE) || field.equals(CwpPageField.COMMENT) || field.equals(CwpPageField.REPRESENTING)) {
					newField.setRequired(false);
				} else {
					if((idmSelfRegOpen && (field.equals(CwpPageField.VISITING))
					        || (idmSelfRegPpsk && (field.equals(CwpPageField.VISITING))))){
						newField.setRequired(false);
					}else{
						newField.setRequired(true);
					}
				}
				if(field.equals(CwpPageField.FIRSTNAME)){
					newField.setLabelName("firstName");
				}else if(field.equals(CwpPageField.LASTNAME)){
					newField.setLabelName("lastName");
				}else if(field.equals(CwpPageField.EMAIL)){
					newField.setLabelName("email");
				}else if(field.equals(CwpPageField.PHONE)){
					newField.setLabelName("phone");
				}else if(field.equals(CwpPageField.VISITING)){
					newField.setLabelName("visiting");
				}else if(field.equals(CwpPageField.REPRESENTING)){
					newField.setLabelName("representing");
				}
				
				newField.setLabel(field);
				newField.setPlace(order++);
				newField.setField(field);
				newField.setFieldMark(field);
				setMultiLanguageFields(newField, idmSelfRegOpen);
			
			
			fields.put(field, newField);
		}
		setFields(fields);
	}
	
	private void setMultiLanguageFields(CwpPageField field,boolean idmSelfReg){
		String searchWord="";
		String mark="";
		if(field.getField().equals(CwpPageField.FIRSTNAME)){
			searchWord="cwp.preview.ppsk.firstname_label";
			mark=CwpPageField.FIRSTNAMEMARK;
		}else if(field.getField().equals(CwpPageField.LASTNAME)){
			searchWord="cwp.preview.ppsk.lastname_label";
			mark=CwpPageField.LASTNAMEMARK;
		}else if(field.getField().equals(CwpPageField.EMAIL)){
		    searchWord="cwp.preview.ppsk.email_label";
			mark=CwpPageField.EMAILMARK;
		}else if(field.getField().equals(CwpPageField.PHONE)){
			searchWord="cwp.preview.ppsk.phone_label";
			mark=CwpPageField.PHONEMARK;
		}else if(field.getField().equals(CwpPageField.VISITING)){
			if(idmSelfReg){
				searchWord="cwp.preview.ppsk.idm.visiting_label";
			}else{
				searchWord="cwp.preview.ppsk.visiting_label";
			}
			mark=CwpPageField.VISITINGMARK;
		}else if(field.getField().equals(CwpPageField.COMMENT)){
			searchWord="cwp.preview.ppsk.reason_label";
			mark=CwpPageField.COMMENTMARK;
		}else if(field.getField().equals(CwpPageField.REPRESENTING)){
			searchWord="cwp.preview.ppsk.idm.reason_label";
			mark=CwpPageField.REPRESENTINGMARK;
		}
		
		field.setFieldMark(mark);
		field.setLabel(cwpResReader.getString(searchWord, getLocaleFromPreview(1)));
		field.setLabel2(cwpResReader.getString(searchWord, getLocaleFromPreview(2)));
		field.setLabel3(cwpResReader.getString(searchWord, getLocaleFromPreview(3)));
		field.setLabel4(cwpResReader.getString(searchWord, getLocaleFromPreview(4)));
		field.setLabel5(cwpResReader.getString(searchWord, getLocaleFromPreview(5)));
		field.setLabel6(cwpResReader.getString(searchWord, getLocaleFromPreview(6)));
		field.setLabel7(cwpResReader.getString(searchWord, getLocaleFromPreview(7)));
		field.setLabel8(cwpResReader.getString(searchWord, getLocaleFromPreview(8)));
		field.setLabel9(cwpResReader.getString(searchWord, getLocaleFromPreview(9)));
	
	}

//	setSuccessNotice(DEFAULT_SUCCESS_NOTICE);
//	setSuccessLibrarySIPStatus(DEFAULT_LIB_SIP_STATUS);
//	setSuccessLibrarySIPFines(DEFAULT_LIB_SIP_FINES);

}