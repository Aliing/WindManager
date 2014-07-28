package com.ah.ui.actions.home;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;


import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.admin.adminOperateImpl.BeOperateException;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeResUtil;
import com.ah.bo.admin.HmAuditLog;
import com.ah.ui.actions.BaseAction;
import com.ah.util.ImageTool;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class OEMSettingsAction extends BaseAction{
	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(OEMSettingsAction.class.getSimpleName());
	
	private boolean showOemOption;
	private boolean showConfigOption;
	private boolean showEULAOption;
	private String locatePosition;
	
	private String newEula;
	private String eulaContent;
	
	private String companyName;
	private String productName;
	private String homePage;
	private String supportEmail;
	private String copyright;
	private String helpURL;
	private String defaultHelpURL;
	
	private static String eulaFile = System.getenv("HM_ROOT") + File.separator +"tiles" + File.separator + "companyEula.htm";
	private static String uploadPath = System.getenv("HM_ROOT") + File.separator + "oemfiles";
	private static String settingsFile = System.getenv("HM_ROOT") + File.separator +"resources" + File.separator +"oem-resource.txt";
	private static String defaultSettingsFile = System.getenv("HM_ROOT") + File.separator +"resources" + File.separator +"default_oem-resource.txt";
	private static final String IMAGE_FILE_PATH = System.getenv("HM_ROOT") + File.separator +"images" + File.separator;
	
	private static final String BACKGROUND_IMAGE_FILE_PATH = System.getenv("HM_ROOT") + File.separator + "images" + File.separator + "hm" + File.separator;
	
	private static final String DEFAULT_LEFT_TOP_LOGO_IMAGE = "company_logo_reverse.png";
	
	private static final String DEFAULT_ICON_IMAGE = "favicon.ico";
	
	private static final String DEFAULT_BACKGROUND_IMAGE = "bkg.gif";
	
	private static final String DEFAULT_ABOUT_SCREEN_IMAGE = "company_logo.png";
	
	private static final String DEFAULT_CONFIG_FOOTER_LOGO_IMAGE = "HM-config-footer.png";
	
	private static final String CONFIG_FOOTER_IMAGE_FILE_PATH = System.getenv("HM_ROOT") + File.separator +"images/hm_v2" + File.separator;
	
	private static final String PREFIX_IMAGE_NAME = "default_";
	
	private File leftTopLogoLocalFile;

	private File iconLocalFile;

	private File backgroundLocalFile;

	private File aboutScreenLocalFile;
	
	private File configFooterLogoFile;

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}
		
		try{
			if ("refresh".equals(operation)) {
				toDefaultPage();
				return SUCCESS;
			}else if("updateSettings".equals(operation)){
				backupSettings();
				
				boolean boo = replaceResStr();
				if(!boo){
					addActionError(MgrUtil.getUserMessage("geneva_11.oemSettings.settings.save.error"));
					log.error("execute", "replace oem resource String catch exception");
					toDefaultPage();
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "oem set product details");
					return SUCCESS;
				}
				
				boo = replaceResourceSettings();
				if(!boo){
					addActionError(MgrUtil.getUserMessage("geneva_11.oemSettings.settings.save.error"));
					log.error("execute", "replace oem resource settings catch exception");
					toDefaultPage();
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "oem set product details");
					return SUCCESS;
				}
				
				generateAuditLog(HmAuditLog.STATUS_SUCCESS, "oem set product details");
				
				//picture
				if(null != getLeftTopLogoLocalFile() && !"".equals(getLeftTopLogoLocalFile())){
					boolean result = checkImage(getLeftTopLogoLocalFile(),new int[]{128,55});
					if(!result){
						addActionError(MgrUtil.getUserMessage("geneva_11.error.oemsettings.topleft.image"));
						toDefaultPage();
						generateAuditLog(HmAuditLog.STATUS_FAILURE, "oem replace left top logo picture");
						return SUCCESS;
					}
					
					uploadImage(IMAGE_FILE_PATH, getLeftTopLogoLocalFile(), DEFAULT_LEFT_TOP_LOGO_IMAGE);
					String casLogoFilePath = System.getenv("CATALINA_HOME") + File.separator + "webapps" + File.separator + "cas" +File.separator
							+ "images" + File.separator + "ah" + File.separator;
					uploadImage(casLogoFilePath, getLeftTopLogoLocalFile(), DEFAULT_LEFT_TOP_LOGO_IMAGE);
					
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, "oem replace left top logo picture");
				}
				if(null != getIconLocalFile() && !"".equals(getIconLocalFile())){
					boolean result = checkIconImage(getIconLocalFile(),new int[]{16,16});
					if(!result){
						addActionError(MgrUtil.getUserMessage("geneva_11.error.oemsettings.icon.image"));
						toDefaultPage();
						generateAuditLog(HmAuditLog.STATUS_FAILURE, "oem replace Icon picture");
						return SUCCESS;
					}
					uploadImage(IMAGE_FILE_PATH, getIconLocalFile(), DEFAULT_ICON_IMAGE);
					String casIconFilePath = System.getenv("CATALINA_HOME") + File.separator + "webapps" + File.separator + "cas" +File.separator;
					uploadImage(casIconFilePath, getIconLocalFile(), DEFAULT_ICON_IMAGE);
					
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, "oem replace Icon picture");
				}
				if(null != getBackgroundLocalFile() && !"".equals(getBackgroundLocalFile())){
					boolean result = checkImage(getBackgroundLocalFile(),new int[]{900,750});
					if(!result){
						addActionError(MgrUtil.getUserMessage("geneva_11.error.oemsettings.background.image"));
						toDefaultPage();
						generateAuditLog(HmAuditLog.STATUS_FAILURE, "oem replace background picture");
						return SUCCESS;
					}
					
					uploadImage(BACKGROUND_IMAGE_FILE_PATH, getBackgroundLocalFile(), DEFAULT_BACKGROUND_IMAGE);
					String casBgFilePath = System.getenv("CATALINA_HOME") + File.separator + "webapps" + File.separator + "cas" +File.separator
							+ "images" + File.separator + "ah" + File.separator;
					uploadImage(casBgFilePath, getBackgroundLocalFile(), DEFAULT_BACKGROUND_IMAGE);
					
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, "oem replace background picture");
				}
				if(null != getAboutScreenLocalFile() && !"".equals(getAboutScreenLocalFile())){
					boolean result = checkImage(getAboutScreenLocalFile(),new int[]{111,48});
					if(!result){
						addActionError(MgrUtil.getUserMessage("geneva_11.error.oemsettings.aboutscreen.image"));
						toDefaultPage();
						generateAuditLog(HmAuditLog.STATUS_FAILURE, "oem replace about screen picture");
						return SUCCESS;
					}
					uploadImage(IMAGE_FILE_PATH, getAboutScreenLocalFile(), DEFAULT_ABOUT_SCREEN_IMAGE);
					
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, "oem replace about screen picture");
				}
				
				if(null != getConfigFooterLogoFile() && !"".equals(getConfigFooterLogoFile())){
					boolean result = checkImage(getConfigFooterLogoFile(),new int[]{850,56});
					if(!result){
						addActionError(MgrUtil.getUserMessage("geneva_11.error.oemsettings.config.footer.image.size"));
						toDefaultPage();
						generateAuditLog(HmAuditLog.STATUS_FAILURE, "oem replace config footer logo picture");
						return SUCCESS;
					}
					uploadImage(CONFIG_FOOTER_IMAGE_FILE_PATH, getConfigFooterLogoFile(), DEFAULT_CONFIG_FOOTER_LOGO_IMAGE);
					
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, "oem replace config footer logo picture");
				}
				
				//EULA
				try{
					writeNewEula(newEula);
				}catch(Exception e){
					addActionError(MgrUtil.getUserMessage("geneva_11.oemSettings.eula.save.error"));
					log.error("execute", "save EULA catch exception");
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "oem replace EULA");
					toDefaultPage();
					
					return SUCCESS;
				}
				generateAuditLog(HmAuditLog.STATUS_SUCCESS, "oem replace EULA");
				
				// add settings tag
				File eulaFile = new File(uploadPath + File.separator +"config");
				if(!eulaFile.exists()){
					try {
						eulaFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				toDefaultPage();
				
				addActionMessage(MgrUtil.getUserMessage("geneva_11.oemSettings.settings.save.success"));
				
				return SUCCESS;
				
			}else if("revertLeftTopImage".equals(operation)){
				boolean result = revertImage("company_logo_reverse");
				toDefaultPage();
				if(!result){
					addActionError(MgrUtil.getUserMessage("geneva_11.error.oemsettings.topleft.image.revert"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "oem revert left top picture");
					return SUCCESS;
				}
				addActionMessage(MgrUtil.getUserMessage("geneva_11.success.oemsettings.topleft.image.revert"));
				generateAuditLog(HmAuditLog.STATUS_SUCCESS, "oem revert left top picture");
				return SUCCESS;
				
			}else if("revertIcoImage".equals(operation)){
				boolean result = revertImage("favicon");
				toDefaultPage();
				if(!result){
					addActionError(MgrUtil.getUserMessage("geneva_11.error.oemsettings.icon.image.revert"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "oem revert Icon picture");
					return SUCCESS;
				}
				addActionMessage(MgrUtil.getUserMessage("geneva_11.success.oemsettings.icon.image.revert"));
				generateAuditLog(HmAuditLog.STATUS_SUCCESS, "oem revert Icon picture");
				return SUCCESS;
				
			}else if("revertBackgroundImage".equals(operation)){
				boolean result = revertImage("bkg");
				toDefaultPage();
				if(!result){
					addActionError(MgrUtil.getUserMessage("geneva_11.error.oemsettings.background.image.revert"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "oem revert background picture");
					return SUCCESS;
				}
				addActionMessage(MgrUtil.getUserMessage("geneva_11.success.oemsettings.background.image.revert"));
				generateAuditLog(HmAuditLog.STATUS_SUCCESS, "oem revert background picture");
				return SUCCESS;
				
			}else if("revertAboutScreenImage".equals(operation)){
				boolean result = revertImage("company_logo");
				toDefaultPage();
				if(!result){
					addActionError(MgrUtil.getUserMessage("geneva_11.error.oemsettings.aboutscreen.image.revert"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "oem revert about screen picture");
					return SUCCESS;
				}
				addActionMessage(MgrUtil.getUserMessage("geneva_11.success.oemsettings.aboutscreen.image.revert"));
				generateAuditLog(HmAuditLog.STATUS_SUCCESS, "oem revert about screen picture");
				return SUCCESS;
				
			}else if("revertConfigFooterImage".equals(operation)){
				boolean result = revertImage("config_footer");
				toDefaultPage();
				if(!result){
					addActionError(MgrUtil.getUserMessage("geneva_11.error.oemsettings.config.footer.image.revert"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "oem revert config footer picture");
					return SUCCESS;
				}
				addActionMessage(MgrUtil.getUserMessage("geneva_11.success.oemsettings.config.footer.image.revert"));
				generateAuditLog(HmAuditLog.STATUS_SUCCESS, "oem revert config footer picture");
				return SUCCESS;				
			}else if("revertEULA".equals(operation)){
				boolean boo = revertImage("eula");
				toDefaultPage();
				if(!boo){
					addActionError(MgrUtil.getUserMessage("geneva_11.error.oemsettings.config.eula.revert"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "oem revert EULA picture");
					return SUCCESS;
				}
				addActionMessage(MgrUtil.getUserMessage("geneva_11.success.oemsettings.config.eula.revert"));
				toDefaultPage();
				generateAuditLog(HmAuditLog.STATUS_SUCCESS, "oem revert EULA picture");
				return SUCCESS;
			}else if("revertSetings".equals(operation)){
				boolean boo = setSettingsValue(defaultSettingsFile);
				if(!boo){
					addActionError(MgrUtil.getUserMessage("geneva_11.error.oemsettings.config.eula.replaceresource.revert"));
					log.error("execute", "get default settings catch exception");
					toDefaultPage();
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "oem revert product details");
					return SUCCESS;
				}
				boo = replaceResStr();
				if(!boo){
					addActionError(MgrUtil.getUserMessage("geneva_11.error.oemsettings.config.eula.replaceresource.revert"));
					log.error("execute", "replace  resource string catch exception");
					toDefaultPage();
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "oem revert product details");
					return SUCCESS;
				}
				
				boo = replaceResourceSettings();
				if(!boo){
					addActionError(MgrUtil.getUserMessage("geneva_11.error.oemsettings.config.eula.replaceoem.revert"));
					log.error("execute", "replace oem resource settings catch exception");
					toDefaultPage();
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "oem revert product details");
					return SUCCESS;
				}
				addActionMessage(MgrUtil.getUserMessage("geneva_11.success.oemsettings.config.settings.revert"));
				toDefaultPage();
				generateAuditLog(HmAuditLog.STATUS_SUCCESS, "oem revert product details");
				return SUCCESS;
			}
		}catch(Exception e){
			addActionError(MgrUtil.getUserMessage(e));
			log.error("execute", "catch exception", e);
			return SUCCESS;
		}
		
		toDefaultPage();
		return SUCCESS;
	}
	
	private boolean checkImage(File file, int[] picAttr){
		boolean result = true;
		int width = ImageTool.getImageWidth(file);
		int height = ImageTool.getImageHeight(file);
		if(width != picAttr[0] || height != picAttr[1]){
			result = false;
		}
		return result;
	}
	
	private boolean checkIconImage(File file, int[] picAttr){
		boolean result = true;
		int width = ImageTool.getIconImageWidth(file);
		int height = ImageTool.getIconImageHeight(file);
		if(width != picAttr[0] || height != picAttr[1]){
			result = false;
		}
		return result;
	}
	
	private void backupSettings(){
		String strErrMsg = "update_in_progress";
		String[] strCmds  = {"sh",BeAdminCentOSTools.ahShellRoot + "/ahBackupOEMImage.sh"};
		BeAdminCentOSTools.execCmdIncludeErr(strCmds, strErrMsg);
	}
	
	private void uploadImage(String path, File file, String imageName){
		FileOutputStream fos=null;
		FileInputStream fis=null;
		
		try{
			fos = new FileOutputStream(path + imageName);
			fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = fis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			//set flag
			/*File pictureFile = new File(uploadPath + File.separator +"picture");
			if(!pictureFile.exists()){
				try {
					pictureFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}*/
		}catch(Exception e)
		{
			log.error("upload image failed", e);
		}
		finally{
			try{
				if(null!=fos)
				{
					fos.close();
				}
			}catch(IOException e)
			{
				log.error("OEMSettingsAction", "FileOutputStream close failed", e);
			}
			try{
				if(null!=fis)
				{
					fis.close();
				}
			}catch(IOException e)
			{
				log.error("OEMSettingsAction", "FileInputStream close failed", e);
			}
		}
	}
	
	private boolean revertImage(String param){
		String strErrMsg = "update_in_progress";
		String[] strCmds  = {"sh",BeAdminCentOSTools.ahShellRoot + "/ahRevertOEMImage.sh", param};
		String result = BeAdminCentOSTools.execCmdIncludeErr(strCmds, strErrMsg);
		if(result.equals(strErrMsg)){
			return false;
		}else{
			//set flag
			/*File pictureFile = new File(uploadPath + File.separator +"picture");
			if(!pictureFile.exists()){
				try {
					pictureFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}*/
			return true;
		}
	}
	
	private static void writeNewEula(String content) throws FileNotFoundException{
		StringBuffer sb = new StringBuffer();
		sb.append("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
		sb.append(content.replaceAll("\n", "<br>"));
		FileOutputStream out = new FileOutputStream(eulaFile);
		try {
			out.write(sb.toString().getBytes());
		} catch (IOException e) {
			log.error("execute", "catch exception", e);
			e.printStackTrace();
		}finally{
			try {
				out.close();
			} catch (IOException e) {
				log.error("execute", "catch exception", e);
				e.printStackTrace();
			}
		}
		
		//set flag
		/*File eulaFile = new File(uploadPath + File.separator +"eula");
		if(!eulaFile.exists()){
			try {
				eulaFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
	}
	
	private void toDefaultPage() throws Exception{
		//eula
	    String content = readTextFile(eulaFile, "UTF-8");
	    setHtmlContent(content);
	    
	    //settings
	    setSettingsValue(settingsFile);
	}
	
	private boolean replaceResStr(){
		String strErrMsg = "replace_ssl_err";

		String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot + "/oemReplaceStr.sh",companyName,
				productName,supportEmail , homePage,"Copyright &copy; " + copyright , "1"};

		try {
			String strReturnMsg = BeAdminCentOSTools.execCmdWithErr(strCmds,
					strErrMsg);

			if (strReturnMsg.equals(strErrMsg)) {

				//String strAdminInstallCertErr = "adminInstallCertErr";

				//throw new BeOperateException(HmBeResUtil
				//		.getString(strAdminInstallCertErr));
				
				return false;
			}
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
	
	private boolean replaceResourceSettings(){
		FileReader read = null;
		BufferedReader br = null;
		StringBuilder content = new StringBuilder();
		FileOutputStream fs = null;
		try {   
				read = new FileReader(settingsFile);
				br = new BufferedReader(read);
			    while(br.ready() != false){
				   String strLine = br.readLine();
				   if(strLine.indexOf("company_name=") >= 0){
					   strLine = strLine.replace(strLine.split("=")[1], companyName);
					}
				   if(strLine.indexOf("company_full_name=") >= 0){
					   strLine = strLine.replace(strLine.split("=")[1], companyName);
					}
				   if(strLine.indexOf("company_name_abbreviation=") >= 0){
					   strLine = strLine.replace(strLine.split("=")[1], companyName);
					}
					if(strLine.indexOf("nms_name=") >= 0){
						strLine = strLine.replace(strLine.split("=")[1], productName);
					}
					if(strLine.indexOf("home_page=") >= 0){
						strLine = strLine.replace(strLine.split("=")[1], homePage);
					}
					/*if(strLine.indexOf("help_link=") >=0){
						strLine = strLine.replace(strLine.split("=")[1], helpURL);
					}*/
					if(strLine.indexOf("support_mail_address=") >= 0){
						strLine = strLine.replace(strLine.split("=")[1], supportEmail);
					}
					if(strLine.indexOf("nms_copyright=") >= 0){
						strLine = strLine.replace(strLine.split("=")[1].split(" ")[2], copyright.trim());
					}
				   content.append(strLine);
				   content.append("\n");
			   }
			     fs = new FileOutputStream(settingsFile);
			     fs.write(content.toString().getBytes());
			     return true;
			  }catch (IOException e){
				  e.printStackTrace();
				  return false;
			  }finally{
				  try {
					  if(null != br)
						  br.close();
					  if(null != read)
						  read.close();
					  if(null != fs)
						  fs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				  
			  }
	}
	
	private boolean setSettingsValue(String file){
		String strLine;
		FileReader fr = null;
		BufferedReader br = null;

		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			while ((strLine = br.readLine()) != null) {
				strLine = strLine.trim();
				if(!strLine.equals("") && strLine.indexOf("=") > 0 && strLine.indexOf("#") < 0){
					if(strLine.indexOf("company_name=") >= 0){
						setCompanyName(strLine.split("=")[1]);
						continue;
					}
					if(strLine.indexOf("nms_name=") >= 0){
						setProductName(strLine.split("=")[1]);
						continue;
					}
					if(strLine.indexOf("home_page=") >= 0){
						setHomePage(strLine.split("=")[1]);
						continue;
					}
					if(strLine.indexOf("help_link=") >= 0){
						setHelpURL(strLine.split("=")[1]);
						continue;
					}
					if(strLine.indexOf("support_mail_address=") >= 0){
						setSupportEmail(strLine.split("=")[1]);
						continue;
					}
					if(strLine.indexOf("nms_copyright=") >= 0){
						setCopyright(strLine.split("=")[1].split(" ")[2]);
					}
				}
			}
		}catch(Exception e){
			log.error("getProfileString", "IO Close Error.", e);
			return false;
		}finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					log.error("getProfileString", "IO Close Error.", e);
				}
			}

			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					log.error("getProfileString", "IO Close Error.", e);
				}
			}
		}
		return true;
}
	
	/**
	 * get contain of EULA and set it to variable
	 * @param resource
	 * @throws Exception
	 */
	public void setHtmlContent(String resource) throws Exception
    {
		Parser myParser;
        NodeList nodeList = null;

        myParser = Parser.createParser(resource, "UTF-8");

        NodeFilter textFilter = new NodeClassFilter(TextNode.class);
        //NodeFilter linkFilter = new NodeClassFilter(LinkTag.class);

        OrFilter lastFilter = new OrFilter();
        lastFilter.setPredicates(new NodeFilter[] { textFilter });

        nodeList = myParser.parse(lastFilter);

        Node[] nodes = nodeList.toNodeArray();

        StringBuffer sb = new StringBuffer();
        
        for (int i = 0; i < nodes.length; i++)
        {
            Node anode = (Node) nodes[i];

            String line = "";
            if (anode instanceof TextNode)
            {
                TextNode textnode = (TextNode) anode;
                line = textnode.getText();
            }
            else if (anode instanceof LinkTag)
            {
                LinkTag linknode = (LinkTag) anode;

                line = linknode.getLink();
            }

            if (line.indexOf("<!--") >= 0)
                continue;
            
            sb.append(line);
            sb.append("\n");
        }
        
        setEulaContent(sb.toString().replaceAll("&nbsp;", " ").replaceAll("<br>", "\n"));
    }

	public static boolean isTrimEmpty(String astr)
    {
        if ((null == astr) || (astr.length() == 0))
        {
            return true;
        }
        if (isBlank(astr.trim()))
        {
            return true;
        }
        return false;
    }

    public static boolean isBlank(String astr)
    {
        if ((null == astr) || (astr.length() == 0))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
	public static String readTextFile(String sFileName, String sEncode) throws ParserException
    {
		StringBuffer sbStr = new StringBuffer(); 
        try
        {
            File ff = new File(sFileName);
            InputStreamReader read = new InputStreamReader(new FileInputStream(ff),
                    sEncode);
            BufferedReader ins = new BufferedReader(read);

            String dataLine = "";
            while (null != (dataLine = ins.readLine()))
            {
                sbStr.append(dataLine);
            }

            ins.close();
        }
        catch (Exception e)
        {
        	log.error("execute", "catch exception", e);
        }

        return sbStr.toString();
    }
	
	
	public List<String> getAvailableCaFile() {
		List<String> listFile = HmBeAdminUtil.getCAFileList(getDomain().getDomainName());
		if (null == listFile || listFile.size() == 0) {
			listFile = new ArrayList<String>();
			listFile.add("");
		}
		return listFile;
	}
	
	public List<String> getAvailableKeyFile() {
		List<String> listFile = HmBeAdminUtil.getCAFileList(getDomain().getDomainName());
		if (null == listFile)
			listFile = new ArrayList<String>();
		listFile.add("");
		return listFile;
	}
	
	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_OEM_SETTINGS);
	}

	public boolean isShowOemOption() {
		return showOemOption;
	}

	public void setShowOemOption(boolean showOemOption) {
		this.showOemOption = showOemOption;
	}

	public boolean isShowConfigOption() {
		return showConfigOption;
	}

	public void setShowConfigOption(boolean showConfigOption) {
		this.showConfigOption = showConfigOption;
	}

	public boolean isShowEULAOption() {
		return showEULAOption;
	}

	public void setShowEULAOption(boolean showEULAOption) {
		this.showEULAOption = showEULAOption;
	}

	public String getLocatePosition() {
		return locatePosition;
	}

	public void setLocatePosition(String locatePosition) {
		this.locatePosition = locatePosition;
	}

	public String getEulaContent() {
		return eulaContent;
	}

	public void setEulaContent(String eulaContent) {
		this.eulaContent = eulaContent;
	}

	public String getNewEula() {
		return newEula;
	}

	public void setNewEula(String newEula) {
		this.newEula = newEula;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getHomePage() {
		return homePage;
	}

	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}

	public String getSupportEmail() {
		return supportEmail;
	}

	public void setSupportEmail(String supportEmail) {
		this.supportEmail = supportEmail;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public File getLeftTopLogoLocalFile() {
		return leftTopLogoLocalFile;
	}

	public void setLeftTopLogoLocalFile(File leftTopLogoLocalFile) {
		this.leftTopLogoLocalFile = leftTopLogoLocalFile;
	}

	public File getIconLocalFile() {
		return iconLocalFile;
	}

	public void setIconLocalFile(File iconLocalFile) {
		this.iconLocalFile = iconLocalFile;
	}

	public File getBackgroundLocalFile() {
		return backgroundLocalFile;
	}

	public void setBackgroundLocalFile(File backgroundLocalFile) {
		this.backgroundLocalFile = backgroundLocalFile;
	}

	public File getAboutScreenLocalFile() {
		return aboutScreenLocalFile;
	}

	public void setAboutScreenLocalFile(File aboutScreenLocalFile) {
		this.aboutScreenLocalFile = aboutScreenLocalFile;
	}

	public File getConfigFooterLogoFile() {
		return configFooterLogoFile;
	}

	public void setConfigFooterLogoFile(File configFooterLogoFile) {
		this.configFooterLogoFile = configFooterLogoFile;
	}

	public String getHelpURL() {
		return helpURL;
	}

	public void setHelpURL(String helpURL) {
		this.helpURL = helpURL;
	}

	public String getDefaultHelpURL() {
		return defaultHelpURL;
	}

	public void setDefaultHelpURL(String defaultHelpURL) {
		this.defaultHelpURL = defaultHelpURL;
	}

	public static void main(String[] args) throws Exception{
        System.out.println(Calendar.getInstance().get(Calendar.YEAR));
	}
}