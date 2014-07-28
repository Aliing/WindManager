package com.ah.ui.actions.admin;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.ah.be.admin.adminOperateImpl.BeOperateHMCentOSImpl;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.os.FileManager;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.HmUser;
import com.ah.bo.cloudauth.CloudAuthCustomer;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.home.HmServicesAction;
import com.ah.ui.actions.home.clientManagement.entity.CertificateQueryResEBO;
import com.ah.ui.actions.home.clientManagement.service.CertificateGenSV;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class ClientCaMgmtAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Tracer	log	= new Tracer(HmServicesAction.class.getSimpleName());
	private static final String KEYSTORE_PASSWORD = "aerohive";
	private static final String ONBOARD_TYPE = "OnboardCA";
	private static final String NONE = "----";
	private static final String CN = "CN";
	private static final String OU = "OU";
	private static final String O = "O";
	private static final String L = "L";
	private static final String ST = "ST";
	private static final String C = "C";
	
	private String selCert = null;
	private String selKey = null;
	
	private String    genFile = null;
	private String    password = null;
	private String    passwordText = null;
	private boolean   ignore;
	private File[]    uploads;
	private String[]  uploadFileNames;
	public enum CertType{
		P12,X509
	}
	
	public String getPassword(){
		return this.password;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
	
	public String getPasswordText(){
		return this.passwordText;
	}
	
	public void setPasswordText(String passwordText){
		this.passwordText = passwordText;
	}
	
	public boolean getIgnore(){
		return this.ignore;
	}
	
	public void setIgnore(boolean ignore){
		this.ignore = ignore;
	}
	
	public File[] getUpload(){
		return this.uploads;
	}
	
	public void setUpload(File[] upload){
		this.uploads = upload;
	}
	
	public void setUploadFileName(String[] uploadFileName){
		this.uploadFileNames = uploadFileName;
	}
	
	public String[] getUploadFileName(){
		return this.uploadFileNames;
	}
	
	public List<String> getCerts(){
		List<String> listFile = HmBeAdminUtil.getCAFileList(getDomain().getDomainName());
		if (null == listFile || listFile.size() == 0) {
			listFile = new ArrayList<String>();
			listFile.add("");
		}
		listFile.add(0, "");
		return listFile;
	}
	
	public String getSelCert(){
		return this.selCert;
	}
	
	public void setSelCert(String selCert){
		this.selCert = selCert;
	}
	
	public String getSelKey(){
		return this.selKey;
	}
	
	public void setSelKey(String selKey){
		this.selKey = selKey;
	}
	
	public String getGenFile(){
		return this.genFile;
	}
	
	public void setGenFile(String genFile){
		this.genFile = genFile;
	}
	
	@Override
	public String execute() throws Exception{
		String forward = globalForward();

		if (forward != null) {
			return forward;
		}
		
		String customerId = "";
		if(NmsUtil.isHostedHMApplication()){
			customerId = getUserContext().getCustomerId();
		}else{
			List<?> cusList = QueryUtil.executeQuery("select customerId from "+CloudAuthCustomer.class.getSimpleName(), null, new FilterParams("owner.domainName",HmDomain.HOME_DOMAIN));
		    if(!cusList.isEmpty()){
		    	customerId = (String)cusList.get(0);
		    }
		}
		try{
			if("importCa".equals(operation)){
				if(!setTitleAndCheckAccess(getText("config.title.updateClientCa"))){
					
				}
				return "input";
			}else if("updateCa".equals(operation)){
				/*byte[] caFile = null;
				String caKeyFile = null;
				String caName = null;
				if(uploads != null && this.uploads.length > 0){
		    		try{
		    			FileManager fileMgr = FileManager.getInstance();
		    			caFile = CertificateGenSV.readFromFile(getUpload()[0]);
		    			caKeyFile = fileMgr.readFromFile(getUpload()[1]);
		    			caName = getUploadFileName()[0];
		    		}catch(Exception e){
		    			log.error("upload customer ca failed ");
		    		}
		    	}*/
				String password = null;
				if(this.ignore == true){
					password = this.password;
				}else{
					password = this.passwordText;
				}
				byte[] caFile = null;
				String caKeyFile = null;
				if(selCert != null && selKey != null){
					String file = AhDirTools.getCertificateDir(getDomain().getDomainName());
					try{
						caFile = CertificateGenSV.readFromFile(new File(file + selCert));
						caKeyFile = FileManager.getInstance().readFromFile(new File(file + selKey));
					}catch(Exception e){
						log.error("error happens when upload the certificate",e);
					}
				}
				String result = CertificateGenSV.importCert(customerId,getDomain().getInstanceId(),caFile,caKeyFile,ONBOARD_TYPE,password,selCert);
				if("".equals(result)){
					String fp = selCert.substring(0, selCert.indexOf('.'));
					String name = fp.length() < 24 ? fp + "_RS.crt":fp.substring(0, 24) + "_RS.crt";
					CertificateGenSV.genRadiusServerCert(customerId,getDomain().getInstanceId(),getDomain().getDomainName(),fp,"AuthServer",name);
					CertObject cerView = getCertObject(caFile,selCert,CertType.X509);
					initCert(cerView);
					addActionMessage(getText("admin.clientCa.update.ok"));
					return SUCCESS;
				}else{
					addActionError(result);
					return "input";
				}
			}else if("return".equals(operation)){
				
				CertificateQueryResEBO result = CertificateGenSV.queryCert(customerId,getDomain().getInstanceId(),ONBOARD_TYPE);
				if(result != null){
					CertObject cerView = getCertObject(Base64.decodeBase64(result.getCertPayload()),result.getCertName(),CertType.X509);
					initCert(cerView);
					return SUCCESS;
				}else{
					initCert(null);
					return SUCCESS;
				}
				
			}else if("useDefault".equals(operation)){
				try{
					CertificateGenSV.resetCA(customerId, getDomain().getInstanceId(), "OnboardCA");
					CertificateGenSV.genRadiusServerCert(customerId,getDomain().getInstanceId(),getDomain().getDomainName(),"ClientMgmt-Radius-Server","AuthServer",CertificateGenSV.SERVER_CRT);
					CertificateQueryResEBO result = CertificateGenSV.queryCert(customerId,getDomain().getInstanceId(),ONBOARD_TYPE);
					if(result != null){
						CertificateGenSV.saveCert(Base64.decodeBase64(result.getCertPayload()), result.getCertName(), getDomain().getDomainName());
						CertObject cerView = getCertObject(Base64.decodeBase64(result.getCertPayload()),result.getCertName(),CertType.X509);
						addActionMessage(getText("admin.useDefaultCa.ok"));
						initCert(cerView);
						return SUCCESS;
					}else{
						initCert(null);
						return SUCCESS;
					}
				}catch(Exception e){
					e.printStackTrace();
					addActionError(getText("admin.useDefaultCa.failure"));
					initCert(null);
					return SUCCESS;
				}
			}else if("importCert".equals(operation)){
				clearErrorsAndMessages();
				addLstForward("clientCaMgmt");
				MgrUtil.setSessionAttribute("clientCaMgmt_type","cert");
				MgrUtil.setSessionAttribute("clientCaMgmt_cert",selCert);
				MgrUtil.setSessionAttribute("clientCaMgmt_key",selKey);
				return "importFile";
			}else if("importKey".equals(operation)){
				clearErrorsAndMessages();
				addLstForward("clientCaMgmt");
				MgrUtil.setSessionAttribute("clientCaMgmt_type","key");
				MgrUtil.setSessionAttribute("clientCaMgmt_cert",selCert);
				MgrUtil.setSessionAttribute("clientCaMgmt_key",selKey);
				return "importFile";
			}else if("continue".equals(operation)){
				
				String type = (String)MgrUtil.getSessionAttribute("clientCaMgmt_type");
				if("cert".equals(type)){
					if(genFile != null && genFile.length() > 0){
						selCert = genFile;
					}else{
						selCert = (String)MgrUtil.getSessionAttribute("clientCaMgmt_cert");
					}
					selKey = (String)MgrUtil.getSessionAttribute("clientCaMgmt_key");
				}else if("key".equals(type)){
					if(genFile != null && genFile.length() > 0){
						selKey = genFile;
					}else{
						selKey = (String)MgrUtil.getSessionAttribute("clientCaMgmt_key");
					}
					selCert = (String)MgrUtil.getSessionAttribute("clientCaMgmt_cert");
				}
				MgrUtil.removeSessionAttribute("clientCaMgmt_type");
				MgrUtil.removeSessionAttribute("clientCaMgmt_cert");
				MgrUtil.removeSessionAttribute("clientCaMgmt_key");
				removeLstTitle();
				removeLstForward();
				return "input";
			}else{
				removeLstTitle();
				removeLstForward();
				CertificateQueryResEBO result = CertificateGenSV.queryCert(customerId,getDomain().getInstanceId(),ONBOARD_TYPE);
				if(result != null){
					CertObject cerView = getCertObject(new Base64().decode(result.getCertPayload().getBytes()),result.getCertName(),CertType.X509);
					initCert(cerView);
					return SUCCESS;
				}else{
					initCert(null);
					return SUCCESS;
				}
			}
		}catch(Exception e){
			return prepareActionError(e);
		}
	}
	
/*	private List<String> getCertInfo(byte[] cert){
		String pathName = AhDirTools.getCidClientsDir() + getCaTemp();
		List<String> certInfo = null;
		try{
			if(CertificateGenSV.existFile(pathName)){
				new File(pathName).delete();
			}
			CertificateGenSV.writeFile(pathName, cert);
			certInfo = BeOperateHMCentOSImpl.getCertInfo();
			if(CertificateGenSV.existFile(pathName)){
				new File(pathName).delete();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return certInfo;
	}*/
	
	private CertObject getCertObject(byte[] cert,String name,CertType certType){
		CertObject certo = new CertObject();
		certo.setCaName(name);
		X509Certificate ct = null;
		InputStream in = null;
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			in = new ByteArrayInputStream(cert);
			ct = (X509Certificate)cf.generateCertificate(in);
		}catch (CertificateException e) {
			// TODO Auto-generated catch block
			log.error("error when parse the certificate: " + name + ", getCertObject()", e);
			e.printStackTrace();
		}
		if(ct != null){
			certo.setSerialName(ct.getSerialNumber().toString());
			certo.setValidFrom(ct.getNotBefore().toString());
			certo.setValidTo(ct.getNotAfter().toString());
			getCertObject(ct.getSubjectDN().toString(),certo);
		}
		return certo;
	}
	
	private CertObject getCertObject(String owner,CertObject cert){
			String[] content = owner.split(",");
			for(String con : content){
				String[] tmp = con.trim().split("=");
				if(CN.equals(tmp[0])){
					cert.setCommonName(tmp[1] == null ? tmp[1] : tmp[1].replace("(", " ("));
				}else if(C.equals(tmp[0])){
					cert.setCountryName(tmp[1]);
				}else if(O.equals(tmp[0])){
					cert.setOrgName(tmp[1]);
				}else if(OU.equals(tmp[0])){
					cert.setOuName(tmp[1]);
				}else if(L.equals(tmp[0])){
					cert.setLocalityName(tmp[1]);
				}else if (ST.equals(tmp[0])){
					cert.setStateName(tmp[1]);
				}
			}
		return cert;
	}
	
	private void initCert(CertObject cert){
		if(cert == null){
			initCaInfo(NONE,NONE,NONE,NONE,NONE,NONE,NONE,NONE,NONE,NONE);
		}else{
			initCaInfo(cert.getCaName(),cert.getOrgName(),cert.getOuName(),cert.getCommonName(),cert.getLocalityName(),cert.getStateName()
					,cert.getCountryName(),cert.getSerialName(),cert.getValidFrom(),cert.getValidTo());
			//initCaInfo("ClientCa.crt","Aerohive","MDM","MDM","Sunyvale","CA","US","100002","2013-03-08","2014-03-07");
		}
	}
	
	private void initCaInfo(String caName,String orgName,String ouName,String commonName,String localityName,
			                String stateName,String countryName,String serialName,String validFrom,String validTo){
		
		setCaName(caName);
		setOrgName(orgName);
		setOuName(ouName);
		setCommonName(commonName);
		setLocalityName(localityName);
		setStateName(stateName);
		setCountryName(countryName);
		setSerialName(serialName);
		setValidFrom(validFrom);
		setValidTo(validTo);
	}
	
	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_CLIENT_CA);
	}
	
	private String caName;
	
	private String orgName;
	
	private String ouName;
	
	private String commonName;
	
	private String localityName;
	
	private String stateName;
	
	private String countryName;
	
	private String serialName;
	
	private String validFrom;
	
	private String validTo;
	
	public String getCaName(){
		return this.caName;
	}
	
	public void setCaName(String caName){
		if(null == caName){
			this.caName = NONE;
		}else{
			this.caName = caName;
		}
	}
	
	public String getOrgName(){
		return this.orgName;
	}
	
	public void setOrgName(String orgName){
		if(null == orgName){
			this.orgName = NONE;
		}else{
			this.orgName = orgName;
		}
	}
	
	public String getOuName(){
		return this.ouName;
	}
	
	public void setOuName(String ouName){
		if(null == ouName){
			this.ouName = NONE;
		}else{
			this.ouName = ouName;
		}
	}
	
	public String getCommonName(){
		return this.commonName;
	}
	
	public void setCommonName(String commonName){
		if(null == commonName){
			this.commonName = NONE;
		}else{
			this.commonName = commonName;
		}
	}
	
	public String getLocalityName(){
		return this.localityName;
	}
	
	public void setLocalityName(String localityName){
		if(null == localityName){
			this.localityName = NONE;
		}else{
			this.localityName = localityName;
		}
	}
	
	public String getStateName(){
		return this.stateName;
	}
	
	public void setStateName(String stateName){
		if(null == stateName){
			this.stateName = NONE;
		}else{
			this.stateName = stateName;
		}
	}
	
	public String getCountryName(){
		return this.countryName;
	}
	
	public void setCountryName(String countryName){
		if(null == countryName){
			this.countryName = NONE;
		}else{
			this.countryName = countryName;
		}
	}
	
	public String getSerialName(){
		return this.serialName;
	}
	
	public void setSerialName(String serialName){
		if(null == serialName){
			this.serialName = NONE;
		}else{
			this.serialName = serialName;
		}
	}
	
	public String getValidFrom(){
		return this.validFrom;
	}
	
	public void setValidFrom(String validFrom){
		if(null == validFrom){
			this.validFrom = NONE;
		}else{
			this.validFrom = validFrom;
		}
	}
	
	public String getValidTo(){
		return this.validTo;
	}
	
	public void setValidTo(String validTo){
		if(null == validTo){
			this.validTo = NONE;
		}else{
			this.validTo = validTo;
		}
	}
	
	public class CertObject{
		
		private String caName;
		
		private String orgName;
		
		private String ouName;
		
		private String commonName;
		
		private String localityName;
		
		private String stateName;
		
		private String countryName;
		
		private String serialName;
		
		private String validFrom;
		
		private String validTo;
		
		public CertObject(){
			
		}
		
		public String getCaName(){
			return this.caName;
		}
		
		public void setCaName(String caName){
			this.caName = caName;
		}
		
		public String getOrgName(){
			return this.orgName;
		}
		
		public void setOrgName(String orgName){
			this.orgName = orgName;
		}
		
		public String getOuName(){
			return this.ouName;
		}
		
		public void setOuName(String ouName){
			this.ouName = ouName;
		}
		
		public String getCommonName(){
			return this.commonName;
		}
		
		public void setCommonName(String commonName){
			this.commonName = commonName;
		}
		
		public String getLocalityName(){
			return this.localityName;
		}
		
		public void setLocalityName(String localityName){
			this.localityName = localityName;
		}
		
		public String getStateName(){
			return this.stateName;
		}
		
		public void setStateName(String stateName){
			this.stateName = stateName;
		}
		
		public String getCountryName(){
			return this.countryName;
		}
		
		public void setCountryName(String countryName){
			this.countryName = countryName;
		}
		
		public String getSerialName(){
			return this.serialName;
		}
		
		public void setSerialName(String serialName){
			this.serialName = serialName;
		}
		
		public String getValidFrom(){
			return this.validFrom;
		}
		
		public void setValidFrom(String validFrom){
			this.validFrom = validFrom;
		}
		
		public String getValidTo(){
			return this.validTo;
		}
		
		public void setValidTo(String validTo){
			this.validTo = validTo;
		}
	}
}
