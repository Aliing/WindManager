/**
 * @filename			CwpPageCustomizationAction.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.2
 * 
 * Copyright (c) 2006-2008 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.ui.actions.config;

//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.LinkedHashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import com.ah.be.app.HmBeOsUtil;
//import com.ah.be.common.AhDirTools;
//import com.ah.be.os.BeOsLayerModule;
//import com.ah.bo.admin.HmDomain;
//import com.ah.bo.wlan.CwpPageCustomization;
//import com.ah.bo.wlan.CwpPageField;
//import com.ah.bo.wlan.CwpPageFieldComparator;
import com.ah.ui.actions.BaseAction;
//import com.ah.ui.actions.hiveap.HiveApFileAction;
//import com.ah.util.EnumItem;
//import com.ah.util.MgrUtil;
//import com.ah.util.Tracer;

/**
 * Action for CWP web page customization
 */
public class CwpPageCustomizationAction extends BaseAction {

	private static final long serialVersionUID = 1L;
//
//	private String domainName = "";
//	
//	private final static short RESOURCE_TYPE_HEAD_IMAGE = 1;
//	
//	private final static short RESOURCE_TYPE_FOOT_IMAGE = 2;
//	
//	private final static short RESOURCE_TYPE_USER_POLICY = 3;
//	
//	private final static short RESOURCE_TYPE_SUCCESS_HEAD_IMAGE = 4;
//	
//	private final static short RESOURCE_TYPE_SUCCESS_FOOT_IMAGE = 5;
//	
//	private final static short STATE_ORIGINAL = 1;
//	
//	private final static short STATE_CURRENT = 2;
//	
//	private final static short PAGE_INDEX = 1;
//	
//	private final static short PAGE_SUCCESS = 2;
//	
//	private final static String DEFAULT_RESORUCE_PREFIX = "aerohive";
//	
//	private final static String DEFAULT_IMAGE_DIRECTORY = "images";
//	
//	private final static String DEFAULT_RESORUCE_DIRECTORY = "resources";
//	
//	private final static String DEFAULT_RESORUCE_CWP_DIRECTORY = "cwp";
//	
//	private final static String DEFAULT_DOMAINS_DIRECTORY = "domains";
//	
//	private final static String DEFAULT_RESOURCE_PATH = AhDirTools.getHmRoot() + 
//															DEFAULT_RESORUCE_DIRECTORY + File.separator + 
//															DEFAULT_RESORUCE_CWP_DIRECTORY + File.separator;
//	
//	private final static String DEFAULT_IMAGE_PATH = AhDirTools.getHmRoot() + 
//															DEFAULT_IMAGE_DIRECTORY + File.separator + 
//															DEFAULT_RESORUCE_CWP_DIRECTORY + File.separator;
//
//	private final static String HTML_TEMPLATE_AUTHENTICATION = "authentication.html";
//	
//	private final static String HTML_TEMPLATE_REGISTRATION = "registration.html";
//	
//	private final static String HTML_TEMPLATE_SUCCESS = "success.html";
//	
//	private final static String PAGE_ELEMENT_HEAD_IMAGE = "headImage";
//	
//	private final static String PAGE_ELEMENT_FOOT_IMAGE = "footImage";
//	
//	private final static String PAGE_ELEMENT_USER_POLICY = "userPolicy";
//	
//	private final static String PAGE_ELEMENT_NOTICE = "notice";
//	
//	private final static String PAGE_ELEMENT_REGISTRATION_FIELDS = "registrationFields";
//	
//	
//	private static final Tracer log = new Tracer(CwpPageCustomizationAction.class
//			.getSimpleName());
//	
//	public String execute() throws Exception {
//		String fw = globalForward();
//		
//		if (fw != null) {
//			return fw;
//		}
//		
//		try {
//			// get the domain name for select
//			if ("".equals(domainName)) {
//				domainName = ((HmDomain)findBoById(HmDomain.class, domainId)).getDomainName();
//			}
//			
//			if ("new".equals(operation)) {
//				if (!setTitleAndCheckAccess(getText("config.title.cwpPageCustomization"))) {
//					setUpdateContext(true);
//					return getLstForward();
//				}
//				
//				setSessionDataSource(new CwpPageCustomization());
//				preparePageFields();
//				setTabId(0);
//				return INPUT;
//			} else if ("create".equals(operation)) {
//				if (checkNameExists("name", getDataSource().getName())) {
//					return INPUT;
//				}
//				
//				if (isNameImported(getDataSource().getName())) {
//					addActionError(MgrUtil.getUserMessage("error.config.cwp.page.customization.nameImported",
//							getDataSource().getName()));
//					return INPUT;
//				}
//				
//				if (!checkNotice()) {
//					return INPUT;
//				}
//				
//				updatePageFields();
//				createCwpPage(getDataSource());
//				setTabId(0);
//				return createBo();
//			} else if ("update".equals(operation)) {
//				if (!checkNotice()) {
//					return INPUT;
//				}
//				
//				updatePageFields();
//				createCwpPage(getDataSource());
//				setTabId(0);
//				return updateBo();
//			} else if ("edit".equals(operation)) {
//				setSessionDataSource(findBoById(boClass, id));
//				addLstTitle(getText("config.title.cwpPageCustomization.edit") + " '"
//						+ getDisplayName() + "'");
//				if (dataSource == null) {
//					return prepareBoList();
//				} else {
//					preparePageFields();
//					setTabId(0);
//					return INPUT;
//				}
//			} else if ("clone".equals(operation)) {
//				long cloneId = getSelectedIds().get(0);
//				CwpPageCustomization profile = (CwpPageCustomization) findBoById(boClass, cloneId);
//				profile.setName("");
//				profile.setId(null);
//				profile.setOwner(null);
//				profile.setVersion(null);
//				setSessionDataSource(profile);
//				preparePageFields();
//				setTabId(0);
//				return INPUT;
//			} else if (("create" + getLstForward()).equals(operation)) {
//				if (checkNameExists("cwpName", getDataSource().getName())) {
//					return INPUT;
//				}
//				
//				if (isNameImported(getDataSource().getName())) {
//					addActionError(MgrUtil.getUserMessage("error.config.cwp.page.customization.nameImported",
//							getDataSource().getName()));
//					return INPUT;
//				}
//				
//				if (!checkNotice()) {
//					return INPUT;
//				}
//				
//				updatePageFields();
//				id = createBo(dataSource);
//				createCwpPage(getDataSource());
//				setUpdateContext(true);
//				setTabId(0);
//				return getLstForward();
//			} else if (("cancel" + getLstForward()).equals(operation)) {
//				if (!getLstForward().equals("")) {
//					setUpdateContext(true);
//					return getLstForward();
//				} else {
//					baseOperation();
//					return prepareBoList();
//				}
//			} else if ("addHeadImage".equals(operation)
//					|| "addFootImage".equals(operation)
//					|| "addUserPolicy".equals(operation)) {
//				clearErrorsAndMessages();
//				addLstForward("cwpPageCustom");
//				setTabId(0);
//				addLstTabId(0);
//				return operation;
//			} else if ("addSuccessHeadImage".equals(operation)
//					|| "addSuccessFootImage".equals(operation)) {
//				clearErrorsAndMessages();
//				addLstForward("cwpPageCustom");
//				setTabId(1);
//				addLstTabId(1);
//				String returnOperation = "addSuccessHeadImage".equals(operation) ? "addHeadImage" : "addFootImage";
//				return returnOperation;
//			} else if ("continue".equals(operation)) {
//				if (dataSource == null) {
//					return prepareBoList();
//				} else {
//					setId(dataSource.getId());
//					if (getUpdateContext()) {
//						removeLstTitle();
//						removeLstForward();
//						setUpdateContext(false);
//					}
//					
//					setTabId(this.getLstTabId());
//					return INPUT;
//				}
//			} else if ("previewIndexPage".equals(operation)
//					|| "previewSuccessPage".equals(operation)) {
//				clearErrorsAndMessages();
//				setUpdateContext(true);
//				updatePageFields();
//				setTabId("previewIndexPage".equals(operation) ? 0 : 1);
//				return operation;
//			} else {
//				baseOperation();
//				return prepareBoList();
//			}
//		} catch (Exception e) {
//			return prepareActionError(e);
//		}
//	}
//	
//	public void prepare() throws Exception {
//		super.prepare();
//		setSelectedL2Feature(L2_FEATURE_CWP_PAGE_CUSTOMIZATION);
//		setDataSource(CwpPageCustomization.class);
//	}
//	
//	public boolean baseOperation() throws Exception {
//		/* first, call method in super class
//		 * and then, remove created directory
//		 */
//		
//		/*
//		 * save the names of objects
//		 * they will be deleted in super.baseOperation()
//		 */
//		Set<Long> selectedIds = null;
//		List<String> names = null;
//		
//		if("remove".equals(operation)) {
//			selectedIds = new HashSet<Long>();
//			selectedIds.addAll(getAllSelectedIds());
//			
//			names = new ArrayList<String>();
//			CwpPageCustomization item = null;
//			
//			// get name from database
//			for(Long id : selectedIds) {
//				item = (CwpPageCustomization) findBoById(boClass, id);
//				
//				if(item != null) {
//					names.add(item.getName());
//				}
//			}
//		}
//		
//		boolean result = super.baseOperation();
//		
//		if(result && "remove".equals(operation)) {
//			HiveApFileAction fileAction = new HiveApFileAction();
//			fileAction.setDomainName(domainName);
//			
//			for(String name : names) {
//				fileAction.deleteCwpDirectory(name);
//			}
//		}
//		
//		return result;
//	}
//	
//	public CwpPageCustomization getDataSource() {
//		return (CwpPageCustomization) dataSource;
//	}
//	
//	public String getDisplayName() {
//		return ((CwpPageCustomization)getDataSource()).getName().replace("\\", "\\\\").replace(
//				"'", "\\'");
//	}
//	
//	public int getNameLength() {
//		return getAttributeLength("name");
//	}
//	
//	public int getDescriptionLength() {
//		return getAttributeLength("description");
//	}
//	
//	public int getNoticeLength() {
//		return getAttributeLength("notice");
//	}
//	
//	public EnumItem[] getEnumPageType() {
//		return CwpPageCustomization.PAGE_TYPE;
//	}
//	
//	public List<String> getAvailableHeadImages() {
//		List<String> headImages = new ArrayList<String>();
//		List<String> uploadedResources = getAvailablePageResources();
//		
//		/*
//		 * add uploaded page resources
//		 * 
//		 * pageResources will never be null.
//		 * even if there is no page resources, an item "Not available" will exist in the list
//		 */
//		if(!uploadedResources.get(0).equals(MgrUtil.getUserMessage("config.optionsTransfer.none"))) {
//			headImages.addAll(uploadedResources);
//		}
//		
//		// add default page resources
//		headImages.add(CwpPageCustomization.DEFAULT_HEAD_IMAGE);
//		
//		return headImages; 
//	}
//	
//	public List<String> getAvailableFootImages() {
//		List<String> footImages = new ArrayList<String>();
//		List<String> uploadedResources = getAvailablePageResources();
//		
//		/*
//		 * add uploaded page resources
//		 * 
//		 * pageResources will never be null.
//		 * even if there is no page resources, an item "Not available" will exist in the list
//		 */
//		if(!uploadedResources.get(0).equals(MgrUtil.getUserMessage("config.optionsTransfer.none"))) {
//			footImages.addAll(uploadedResources);
//		}
//		
//		// add default page resources
//		footImages.add(CwpPageCustomization.DEFAULT_FOOT_IMAGE);
//		
//		return footImages; 
//	}
//	
//	public List<String> getAvailableUserPolicies() {
//		List<String> userPolicies = new ArrayList<String>();
//		List<String> uploadedPolicies = getAvailablePageResources();
//		
//		/*
//		 * add uploaded page resources
//		 * 
//		 * pageResources will never be null.
//		 * even if there is no page resources, an item "Not available" will exist in the list
//		 */
//		if(!uploadedPolicies.get(0).equals(MgrUtil.getUserMessage("config.optionsTransfer.none"))) {
//			userPolicies.addAll(uploadedPolicies);
//		}
//		
//		// add default user policies
//		userPolicies.add(CwpPageCustomization.DEFAULT_USER_POLICY);
//		
//		return userPolicies;
//	}
//
//	public String getOriginalHeadImage() {
//		return getPageResource(STATE_ORIGINAL, RESOURCE_TYPE_HEAD_IMAGE, true);
//	}
//	
//	public String getCurrentHeadImage() {
//		return getPageResource(STATE_CURRENT, RESOURCE_TYPE_HEAD_IMAGE, true);
//	}
//	
//	public String getOriginalFootImage() {
//		return getPageResource(STATE_ORIGINAL, RESOURCE_TYPE_FOOT_IMAGE, true);
//	}
//	
//	public String getCurrentFootImage() {
//		return getPageResource(STATE_CURRENT, RESOURCE_TYPE_FOOT_IMAGE, true);
//	}
//	
//	public String getOriginalSuccessHeadImage() {
//		return getPageResource(STATE_ORIGINAL, RESOURCE_TYPE_SUCCESS_HEAD_IMAGE, true);
//	}
//	
//	public String getCurrentSuccessHeadImage() {
//		return getPageResource(STATE_CURRENT, RESOURCE_TYPE_SUCCESS_HEAD_IMAGE, true);
//	}
//
//	public String getOriginalSuccessFootImage() {
//		return getPageResource(STATE_ORIGINAL, RESOURCE_TYPE_SUCCESS_FOOT_IMAGE, true);
//	}
//
//	public String getCurrentSuccessFootImage() {
//		return getPageResource(STATE_CURRENT, RESOURCE_TYPE_SUCCESS_FOOT_IMAGE, true);
//	}
//
//	public String getOriginalUserPolicy() {
//		return getUserPolicy(STATE_ORIGINAL);
//	}
//	
//	public String getCurrentUserPolicy() {
//		return getUserPolicy(STATE_CURRENT);
//	}
//	
//	private String getUserPolicy(short state) {
//		String filePath = getPageResource(state, RESOURCE_TYPE_USER_POLICY, false);
//		
//		if(filePath == null) {
//			return null;
//		}
//		
//		// get the content of user policy from file
//		BufferedReader bufferReader = null;
//		
//		try {
//			bufferReader = new BufferedReader(new FileReader(new File(filePath)));
//		} catch (FileNotFoundException e) {
//			bufferReader = null;
//		}
//		
//		if(bufferReader == null) {
//			return null;
//		}
//		
//		String textLine = null;
//		StringBuffer userPolicy = new StringBuffer();
//		
//		try {
//			while((textLine = bufferReader.readLine()) != null) {
//				userPolicy.append(textLine).append("\n");
//			}
//		} catch (IOException e) {
//			
//		}
//		
//		return userPolicy.toString();
//	}
//	
//	public String getOriginalNotice() {
//		return getNotice(STATE_ORIGINAL);
//	}
//	
//	
//	public String getCurrentNotice() {
//		return getNotice(STATE_CURRENT);
//	}
//
//	private String getNotice(short state) {
//		if(getDataSource() == null) {
//			return null;
//		}
//		
//		CwpPageCustomization bo = null;
//		
//		try {
//			if(state == STATE_ORIGINAL) {
//				// get original BO from database
//				if(getDataSource().getId() != null)
//					bo = (CwpPageCustomization) findBoById(boClass, getDataSource().getId());
//			} else {
//				bo = getDataSource();
//			}
//		} catch(Exception e) {
//			
//		}
//		
//		String notice = null;
//		notice = bo != null ? bo.getNotice() : null;
//		
//		// change the '\n' into '<br>'
//		if(notice != null) {
//			notice = notice.replace("\r\n", "<br>");
//		}
//		
//		return notice;
//	}
//	
//	public String getOriginalSuccessNotice() {
//		if(getDataSource() == null) {
//			return null;
//		}
//		
//		// get original BO from database
//		CwpPageCustomization originalBo = null;
//		
//		try {
//			originalBo = (CwpPageCustomization) findBoById(boClass, getDataSource().getId());
//		} catch(Exception e) {
//			
//		}
//		
//		String successNotice = originalBo != null ? originalBo.getSuccessNotice() : null;
//		
//		// change the '\n' into '<br>'
//		if(successNotice != null) {
//			successNotice = successNotice.replace("\r\n", "<br>");
//		}
//		
//		return successNotice;
//	}
//	
//	public String getCurrentSuccessNotice() {
//		String successNotice = getDataSource() != null ? getDataSource().getSuccessNotice() : null;
//
//		// change the '\n' into '<br>'
//		if(successNotice != null) {
//			successNotice = successNotice.replace("\r\n", "<br>");
//		}
//
//		return successNotice;
//	}
//	
//	public String getShowAuthenticated() {
//		return getDataSource().getType() == CwpPageCustomization.TYPE_AUTHENTICATION ? "" : "none";
//	}
//	
//	public String getShowRegistrated() {
//		return getDataSource().getType() == CwpPageCustomization.TYPE_REGISTRATION ? "" : "none";
//	}
//	
//	public String getHideOriginalPage() {
//		return getDataSource().getId() == null ? "none" : "";
//	}
//	
//	private String getPageResource(CwpPageCustomization bo, int type, boolean isRelative) {
//		/*
//		 * the page resources may be images which will be used in preview page with HTML tag "img".
//		 * in tag "img", if the url is null or empty(""), something wrong will happen.
//		 * For example, in creating a CWP page, after previewing the creating page, HM will navigate 
//		 * to list view(cwpPageCustomizationList.jsp), not to INPUT page(cwpPageCustomization.jsp).
//		 * 
//		 * joseph chen , 07/29/2008
//		 */
//		String nullResource = "/";
//		
//		if(bo == null) {
//			return nullResource;
//		}
//		
//		// get absolute path in HM
//		String path = null;
//		
//
//		String fileName = null;
//		
//		switch(type) {
//		case RESOURCE_TYPE_HEAD_IMAGE:
//			fileName = bo.getHeadImage();
//			break;
//		case RESOURCE_TYPE_FOOT_IMAGE:
//			fileName = bo.getFootImage();
//			break;
//		case RESOURCE_TYPE_USER_POLICY:
//			fileName = bo.getUserPolicy();
//			break;
//		case RESOURCE_TYPE_SUCCESS_HEAD_IMAGE:
//			fileName = bo.getSuccessHeadImage();
//			break;
//		case RESOURCE_TYPE_SUCCESS_FOOT_IMAGE:
//			fileName = bo.getSuccessFootImage();
//			break;
//		default:
//			break;
//		}
//		
//		if(fileName.startsWith(DEFAULT_RESORUCE_PREFIX)) { // get from default ones
//			if(type == RESOURCE_TYPE_USER_POLICY) {
//				path = DEFAULT_RESOURCE_PATH + fileName;					
//			} else {
//				path = DEFAULT_IMAGE_PATH + fileName;
//			}
//			
//			if(isRelative) {
//				if(type == RESOURCE_TYPE_USER_POLICY) {
//					path = path.substring(path.indexOf(DEFAULT_RESORUCE_DIRECTORY) - 1);
//				} else {
//					path = path.substring(path.indexOf(DEFAULT_IMAGE_DIRECTORY) - 1);
//				}
//			}
//		} else { // get from uploaded ones
//			path = AhDirTools.getPageResourcesDir(domainName) + fileName;
//			
//			if(isRelative) {
//				path = path.substring(path.indexOf(DEFAULT_DOMAINS_DIRECTORY) - 1);
//			}
//		}
//		
//		path = path.replace("\\", "/");
//		return path;
//	}
//	
//	private String getPageResource(short state, int type, boolean isRelative) {
//		// get original BO from database
//		CwpPageCustomization bo = null;
//		
//		try {
//			if(state == STATE_ORIGINAL) {
//				if(getDataSource().getId() != null)
//					bo = (CwpPageCustomization) findBoById(boClass, getDataSource().getId());
//			}
//			else {
//				bo = getDataSource();
//			}
//		} catch(Exception e) {
//			
//		}
//		
//		return getPageResource(bo, type, isRelative);
//	}
//	
//	private List<String> getAvailablePageResources()
//	{
//		return HiveApFileAction.getAllPageResources(domainName);
//	}
//	
//	private void createCwpPage(CwpPageCustomization dataSource) {
//		if(dataSource == null) {
//			return ;
//		}
//		
//		// remove the original directory if exists
//		String newDirPath = getCreatedDirectory(dataSource);
//		
//		try {
//			if(HmBeOsUtil.isFileExist(newDirPath)) {
//				HmBeOsUtil.deleteDirectory(newDirPath);
//			}
//			
//			HmBeOsUtil.createDirectory(newDirPath);
//		} catch (Exception exception) {
//			log.error("createPage", "failed to create the path - " + newDirPath, exception);
//			addActionError(MgrUtil.getUserMessage("error.config.cwp.page.customization.creationFailed"));
//			return ;
//		}
//		
//		if(createPage(dataSource, PAGE_INDEX)) {
//			createPage(dataSource, PAGE_SUCCESS);
//		}
//		
//		return ;
//	}
//	
//	private boolean createPage(CwpPageCustomization dataSource, short pageType) {
//		if(dataSource == null) {
//			return false;
//		}
//		
//		// get path of HTML template
//		String templatePath = null;
//		
//		if(pageType == PAGE_INDEX) {
//			if(dataSource.getType() == CwpPageCustomization.TYPE_AUTHENTICATION) {
//				templatePath = DEFAULT_RESOURCE_PATH + HTML_TEMPLATE_AUTHENTICATION;
//			} else {
//				templatePath = DEFAULT_RESOURCE_PATH + HTML_TEMPLATE_REGISTRATION;
//			}
//		} else if (pageType == PAGE_SUCCESS) {
//			templatePath = DEFAULT_RESOURCE_PATH + HTML_TEMPLATE_SUCCESS;
//		}
//		
//		// read template into String lines
//		String[] htmlLines = null;
//		
//		try {
//			htmlLines = HmBeOsUtil.readFile(templatePath);
//		} catch (Exception exception) {
//			log.error("createPage", "failed to read the template file - " + templatePath, exception);
//		}
//		
//		if(htmlLines == null) {
//			addActionError(MgrUtil.getUserMessage("error.config.cwp.page.customization.creationFailed"));
//			return false;
//		}
//		
//		// modify value of fields in String lines
//		if(pageType == PAGE_INDEX) {
//			// head image
//			modifyField(htmlLines, PAGE_ELEMENT_HEAD_IMAGE, dataSource.getHeadImage());
//			// foot image
//			modifyField(htmlLines, PAGE_ELEMENT_FOOT_IMAGE, dataSource.getFootImage());
//			// user policy
//			modifyField(htmlLines, PAGE_ELEMENT_USER_POLICY, dataSource.getUserPolicy());
//			// notice
//			modifyField(htmlLines, PAGE_ELEMENT_NOTICE, 
//					getDataSource().getNotice().replace("\r\n", "<br>"));
//			
//			// registration fields
//			if(dataSource.getType() == CwpPageCustomization.TYPE_REGISTRATION) {
//				htmlLines = customizeRegistrationFields(htmlLines, dataSource);
//			}
//		} else if (pageType == PAGE_SUCCESS) {
//			// head image
//			modifyField(htmlLines, PAGE_ELEMENT_HEAD_IMAGE, dataSource.getSuccessHeadImage());
//			// foot image
//			modifyField(htmlLines, PAGE_ELEMENT_FOOT_IMAGE, dataSource.getSuccessFootImage());
//			// notice
//			modifyField(htmlLines, PAGE_ELEMENT_NOTICE, 
//					getDataSource().getSuccessNotice().replace("\r\n", "<br>"));
//		}
//		
//		// write lines into destination file
//		String directoryPath = getCreatedDirectory(dataSource);
//		String newFilePath = directoryPath;
//		
//		if(pageType == PAGE_INDEX) {
//			newFilePath += "index.html";
//		} else {
//			newFilePath += "success.html";
//		}
//		
//		// create file
//		try {
//			HmBeOsUtil.createFile(newFilePath, htmlLines);
//		} catch (Exception exception) {
//			log.error("createPage", "failed to write the HTML file - " + newFilePath, exception);
//			addActionError(MgrUtil.getUserMessage("error.config.cwp.page.customization.creationFailed"));
//			return false;
//		}
//		
//		// copy resources files
//		copyPageResources(dataSource, pageType, getCreatedDirectory(dataSource));
//		
//		if(isFilesTooBig(directoryPath)) {
//			addActionError(MgrUtil.getUserMessage("error.config.cwp.page.customization.filesTooBig", HiveApFileAction.CWP_RESOURCE_MAX_SIZE+ "K Bytes"));
//			
//			try {
//				HmBeOsUtil.deleteDirectory(directoryPath);
//			} catch (Exception exception) {
//				
//			} 
//			
//			return false;
//		}
//		
//		return true;
//	}
//	
//	private void modifyField(String[] lines, String id, String value) {
//		if(lines == null || lines.length == 0) {
//			return ;
//		}
//		
//		if(id == null || value == null) {
//			return ;
//		}
//		
//		// search the Array for the element
//		int lineNum = -1;
//		
//		for(int i=0; i<lines.length; i++) {
//			if(lines[i] != null && lines[i].contains(id)) {
//				lineNum = i;
//				break;
//			}
//		}
//		
//		if(lineNum == -1) { // the element is not found
//			return ;
//		}
//		
//		int position = lines[lineNum].indexOf(id);
//		StringBuffer line = new StringBuffer(lines[lineNum]);
//		
//		if(PAGE_ELEMENT_HEAD_IMAGE.equals(id)) {
//			/*
//			 * <td><img id="headImage" src="" width="405" height="56" alt=""></td>
//			 */
//			position += 16; // headImage" src="
//			line.insert(position, value);
//			
//		} else if(PAGE_ELEMENT_FOOT_IMAGE.equals(id)) {
//			/*
//			 * <img id="footImage" src="" width="450" height="50" alt="">
//			 */
//			position += 16; // footImage" src="
//			line.insert(position, value);
//		} else if(PAGE_ELEMENT_USER_POLICY.equals(id)) {
//			/*
//			 * <textarea name="textfield" cols="45" rows="8" id="userPolicy"></textarea>
//			 */
//			position += 12; // userPolicy">
//			line.insert(position, getUserPolicy(STATE_CURRENT));
//		} else if(PAGE_ELEMENT_NOTICE.equals(id)) {
//			/*
//			 * <p class="style5" align=left id="notice"></p>
//			 */
//			position += 8; // notice">
//			line.insert(position, value);
//		}
//		
//		
//		lines[lineNum] = line.toString();
//		
//		return ;
//	}
//	
//	private void copyPageResources(CwpPageCustomization dataSource, short pageType, String destinationPath) {
//		if(destinationPath == null) {
//			return;
//		}
//		
//		String headImagePath = null;
//		String footImagePath = null;
//		String headImageName = null;
//		String footImageName = null;
//		
//		if(pageType == PAGE_INDEX) {
//			headImagePath = getPageResource(dataSource, RESOURCE_TYPE_HEAD_IMAGE, false);
//			footImagePath = getPageResource(dataSource, RESOURCE_TYPE_FOOT_IMAGE, false);
//			headImageName = dataSource.getHeadImage();
//			footImageName = dataSource.getFootImage();
//		} else if (pageType == PAGE_SUCCESS) {
//			headImagePath = getPageResource(dataSource, RESOURCE_TYPE_SUCCESS_HEAD_IMAGE, false);
//			footImagePath = getPageResource(dataSource, RESOURCE_TYPE_SUCCESS_FOOT_IMAGE, false);
//			headImageName = dataSource.getSuccessHeadImage();
//			footImageName = dataSource.getSuccessFootImage();
//		}
//		
//		try {
//			HmBeOsUtil.copyFile(headImagePath, destinationPath + headImageName);
//			HmBeOsUtil.copyFile(footImagePath, destinationPath + footImageName);
//		} catch (Exception exception) {
//			log.error("createPage", "failed to copy resource file to " + destinationPath, exception);
//			addActionError(MgrUtil.getUserMessage("error.config.cwp.page.customization.creationFailed"));
//		}
//		
//		return ;
//	}
//	
//	private String[] customizeRegistrationFields(String[] lines, CwpPageCustomization dataSource) {
//		if(lines == null || dataSource == null 
//						|| dataSource.getType() != CwpPageCustomization.TYPE_REGISTRATION) {
//			return null;
//		}
//		
//		// store source String lines into a LinkedList
//		LinkedList<String> lineList = new LinkedList<String>();
//		
//		for(String line : lines) {
//			if(line != null && line.contains(PAGE_ELEMENT_REGISTRATION_FIELDS)) {
//				lineList.add(line);
//				
//				// insert the lines for registration fields
//				List<CwpPageField> enabledFields = getCurrentEnabledPageFields();
//				
//				if(enabledFields.size() > 0) {
//					lineList.add("<table width=\"100%\"  border=\"0\" cellspacing=\"0\" cellpadding=\"0\" >");
//					lineList.add("<tr><td></td>");
//					lineList.add("<td><div align=\"left\"><font size=\"1\">");
//					lineList.add("Fields marked with an asterisk <span class=\"yregasterisk\">*</span> are required.");
//					lineList.add("</font></div>");
//					lineList.add("<spacer type=\"block\" width=\"1\" height=\"10\"></td></tr>");
//					
//					int countField = 0;
//					int countOptionalField = 0;
//					String fieldName = null;
//					StringBuffer inputLine = new StringBuffer(80);
//					
//					for(CwpPageField field : enabledFields) {
//						lineList.add("<tr><td width=\"100\"><div align=\"left\"><span class=\"style5\">");
//						
//						if(field.getRequired()) {
//							lineList.add("<font color=\"#FF0000\">*</font>" + field.getLabel() + ": </span></div></td>");
//							countField++;
//							fieldName = "field" + countField;
//						} else {
//							lineList.add("&nbsp;" + field.getLabel() + ": </span></div></td>");
//							countOptionalField++;
//							fieldName = "opt_field" + countOptionalField;
//						}
//						
//						inputLine.delete(0, inputLine.length());
//						inputLine.append("<td><input name=\"");
//						inputLine.append(fieldName);
//						inputLine.append("\" type=\"text\" id=\"");
//						inputLine.append(fieldName);
//						inputLine.append("\" size=\"32\" maxlength=\"32\"></td></tr>");
//						
//						lineList.add(inputLine.toString());
//					}
//					
//					lineList.add("</table>");
//				}
//			} else {
//				lineList.add(line);
//			}
//		}
//		
//		// turn the String list into String array and return
//		String[] newLines = new String[lineList.size()];
//		int lineNum = 0;
//		
//		for(String line : lineList) {
//			newLines[lineNum++] = line;
//		}
//		
//		return newLines;
//	}
//	
//	private String getCreatedDirectory(CwpPageCustomization dataSource) {
//		if(dataSource == null) {
//			return null;
//		} else {
//			return AhDirTools.getCwpWebDir(domainName)
//							+ dataSource.getName()
//							+ File.separator;
//		}
//		
//	}
//	
//	/**
//	 * check if the name has been used by imported directory in CwpAction
//	 * @param name
//	 * @return
//	 * @author Joseph Chen
//	 */
//	private boolean isNameImported(String name) {
//		if(name == null) {
//			return false;
//		}
//		
//		List<String> existedNames = null;
//		
//		try {
//			existedNames = HmBeOsUtil.getFileAndSubdirectoryNames(AhDirTools.getCwpWebDir(domainName),
//					BeOsLayerModule.ONLYDIRECTORY, false);
//		} catch (Exception exception) {
//			log.error("isNameImported", "failed to get the existed names", exception);
//		} 
//		
//		if(existedNames == null) {
//			return false;
//		}
//		
//		/*
//		 * the existed name could contain the name of VHM, like aName(home)
//		 */
//		for(String existedName : existedNames) {
//			if(name.equals(existedName)) {
//				return true;
//			}
//		}
//		
//		return false;
//	}
//	
//	/**
//	 * check if the total size of files in a directory exceeds the limit 
//	 * @param path the given path
//	 * @return
//	 * @author Joseph Chen
//	 */
//	private boolean isFilesTooBig(String path) {
//		if(path == null) {
//			return false;
//		}
//		
//		File target = new File(path);
//		long totalSize = 0;
//		
//		if(target.isDirectory()) {
//			File[] files = target.listFiles();
//			
//			if(files != null) {
//				for(File file : files) {
//					totalSize += file.length();
//				}
//			}
//		} else {
//			totalSize = target.length();
//		}
//		
//		if(totalSize > HiveApFileAction.CWP_RESOURCE_MAX_SIZE * 1024) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//	
//	private void preparePageFields() {
//		CwpPageCustomization pageCustomization = getDataSource();
//		Map<String, CwpPageField> fields = new LinkedHashMap<String, CwpPageField>();
//		byte order = 1;
//		
//		for(String field : CwpPageField.FIELDS) {
//			CwpPageField newField = (CwpPageField)pageCustomization.getPageField(field);
//			
//			if(newField == null) {
//				newField = new CwpPageField();
//				newField.setEnabled(true);
//				
//				if(field.equals("Phone") || field.equals("Comment")) {
//					newField.setRequired(false);
//				} else {
//					newField.setRequired(true);
//				}
//				
//				newField.setLabel(field);
//				newField.setPlace(order++);
//			}
//			
//			newField.setField(field);
//			fields.put(field, newField);
//		}
//		
//		pageCustomization.setFields(fields);
//	}
//	
//	private void updatePageFields() {
//		
//		if(getDataSource().getType() != CwpPageCustomization.TYPE_REGISTRATION) {
//			return ;
//		}
//		
//		int index = 0;
//		
//		for(CwpPageField field : getDataSource().getFields().values()) {
//			boolean rawEnabled = false;
//			
//			/*
//			 * strangely, if none of the fields are selected in GUI, the String array
//			 * enableds will contain a item "false"
//			 */
//			if(enableds != null && !enableds[0].equals("false")) {
//				for(int j=0; j<enableds.length; j++) {
//					if(index == Integer.parseInt(enableds[j])) { // the row is enabled
//						rawEnabled = true;
//						
//						field.setLabel(labels[j]);
//						field.setPlace(orders[j]);
//						
//						// get the value of required
//						boolean fieldRequired = false;
//						
//						if(requireds != null && !requireds[0].equals("false")) {
//							for(int k=0; k<requireds.length; k++) {
//								if(index == Integer.parseInt(requireds[k])) { // the field is required
//									fieldRequired = true;
//									break;
//								}
//							}							
//						}
//						
//						field.setRequired(fieldRequired);
//						
//						break;
//					}
//				}
//			}
//			
//			field.setEnabled(rawEnabled);
//			index++;
//		}
//	}
//	
//	private String[] labels;
//	
//	private String[] enableds;
//	
//	private String[] requireds;
//	
//	private byte[] orders;
//
//	/**
//	 * getter of labels
//	 * @return the labels
//	 */
//	public String[] getLabels() {
//		return labels;
//	}
//
//	/**
//	 * setter of labels
//	 * @param labels the labels to set
//	 */
//	public void setLabels(String[] labels) {
//		this.labels = labels;
//	}
//
//	
//	
//	/**
//	 * getter of enableds
//	 * @return the enableds
//	 */
//	public String[] getEnableds() {
//		return enableds;
//	}
//
//	/**
//	 * setter of enableds
//	 * @param enableds the enableds to set
//	 */
//	public void setEnableds(String[] enableds) {
//		this.enableds = enableds;
//	}
//
//	/**
//	 * getter of requireds
//	 * @return the requireds
//	 */
//	public String[] getRequireds() {
//		return requireds;
//	}
//
//	/**
//	 * setter of requireds
//	 * @param requireds the requireds to set
//	 */
//	public void setRequireds(String[] requireds) {
//		this.requireds = requireds;
//	}
//
//	/**
//	 * getter of orders
//	 * @return the orders
//	 */
//	public byte[] getOrders() {
//		return orders;
//	}
//
//	/**
//	 * setter of orders
//	 * @param orders the orders to set
//	 */
//	public void setOrders(byte[] orders) {
//		this.orders = orders;
//	}
//
//	public EnumItem[] getFieldOrders() {
//		if(getDataSource() == null
//				|| getDataSource().getFields() == null) {
//			return null;
//		}
//		
//		int size = getDataSource().getFields().size();
//		EnumItem[] items = new EnumItem[size];
//		
//		for(int i=0; i<size; i++) {
//			items[i] = new EnumItem(i+1, String.valueOf(i+1));
//		}
//		
//		return items;
//	}
//	
//	private List<CwpPageField> getEnabledPageFields(short type) {
//		CwpPageCustomization dataSource = null;
//		
//		if(type == STATE_ORIGINAL) {
//			if(getDataSource().getId() != null)
//				try {
//					dataSource = (CwpPageCustomization) findBoById(boClass, getDataSource().getId());
//				} catch (Exception e) {
//					
//				}
//		} else {
//			dataSource = getDataSource();
//		}
//		
//		if(dataSource == null || dataSource.getFields() == null) {
//			return null;
//		}
//		
//		List<CwpPageField> enabledFields = new ArrayList<CwpPageField>();
//		
//		for(CwpPageField field : dataSource.getFields().values()) {
//			if(field.getEnabled()) {
//				enabledFields.add(field);
//			}
//		}
//		
//		Collections.sort(enabledFields, new CwpPageFieldComparator());
//		
//		return enabledFields;
//	}
//	
//	public List<CwpPageField> getOriginalEnabledPageFields() {
//		return getEnabledPageFields(STATE_ORIGINAL);
//	}
//	
//	public List<CwpPageField> getCurrentEnabledPageFields() {
//		return getEnabledPageFields(STATE_CURRENT);
//	}
//	
//	private boolean checkNotice() {
//		CwpPageCustomization dataSource = getDataSource();
//	
//		if(dataSource == null) {
//			return true;
//		}
//		
//		String notice = dataSource.getNotice();
//		
//		if(notice != null && notice.length() > getNoticeLength()) {
//			addActionError(MgrUtil.getUserMessage("error.config.cwp.page.customization.noticeTooLong"));
//			setTabId(0);
//			return false;
//		}
//		
//		String successNotice = dataSource.getSuccessNotice();
//		
//		if(successNotice != null && successNotice.length() > getNoticeLength()) {
//			addActionError(MgrUtil.getUserMessage("error.config.cwp.page.customization.noticeTooLong"));
//			setTabId(1);
//			return false;
//		}
//		
//		return true;
//	}
	
}