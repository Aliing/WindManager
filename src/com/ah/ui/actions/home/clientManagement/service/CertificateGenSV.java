package com.ah.ui.actions.home.clientManagement.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.admin.adminOperateImpl.BeRootCADTO;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.os.FileManager;
import com.ah.be.rest.ahmdm.client.IResponseFromMDM;
import com.ah.be.rest.ahmdm.client.ResponseFromMDMImpl;
import com.ah.be.rest.client.models.ResponseModel;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ui.actions.home.clientManagement.entity.AcmTroubleEBO;
import com.ah.ui.actions.home.clientManagement.entity.CAResetEBO;
import com.ah.ui.actions.home.clientManagement.entity.CertificateImportEBO;
import com.ah.ui.actions.home.clientManagement.entity.CertificateQueryEBO;
import com.ah.ui.actions.home.clientManagement.entity.CertificateQueryResEBO;
import com.ah.ui.actions.home.clientManagement.entity.CheckCMServiceEBO;
import com.ah.ui.actions.home.clientManagement.entity.CsrCertificateEBO;
import com.ah.ui.actions.home.clientManagement.entity.EnableClientManageEBO;
import com.ah.ui.actions.home.clientManagement.entity.Exception4ACM;
import com.ah.ui.actions.home.clientManagement.entity.OnBoardCaEBO;
import com.ah.ui.actions.home.clientManagement.entity.SignedCertificateEBO;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.values.PairValue;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class CertificateGenSV {
	private static final Tracer log = new Tracer(CertificateGenSV.class.getSimpleName());
	
	//public static final String HOST_NAME = "http://10.155.21.106:8080/aerohivemdm";    //just for test;
    //public static final String HOST_NAME = CertificateGenSV.getUrl();                  //get the uri from online
	public static final String ENABLE_URI                 = "/api/hm/clientmanagement/update";
	public static final String CERT_URI                   = "/api/hm/certs/sign";
	public static final String TROUBLESHOOTING            = "/api/common/mgmt/healthcheck";
	public static final String IMPORT_CERT                = "/api/hm/certs/import";
	public static final String QUERY_CERT                 = "/api/hm/certs/query";
	public static final String RESET_CA                   = "/api/hm/certs/reset";
	
	public static final String SERVER_CRT                 = "ClientMgmt-Radius-Server_Crt.crt";
	public static final String ONBOARD_CA                 = "ClientMgmt_CA.crt";
	public static final String SERVER_PFX                 = "ClientMgmt-Radius-Server.pfx";
	public static final String SERVER_KEY                 = "ClientMgmt-Radius-Server_key.pem";
	
	public static final String COUNTRY                    = "US";
	public static final String STATE                      = "California";
	public static final String LOCALITY_NAME              = "Sunnyvale";
	public static final String ORGANIZATION               = "Aerohive";
	public static final String ORGANIZATION_UNIT          = "Aerohive";
	public static final String COMMON_NAME                = "Authentication Server";
	
// Exception code for update client management
	public static final String UPDATE_EXCEPTION           = "enum.acm.update.";
//10000 - 10099 FRO GENERAL EXCEPTION	
	public static final int NO_THIS_CUSTOMER              = 10000;
	public static final int CUSTOMER_DISABLED             = 10001;
	public static final int NO_LICENSE                    = 10002;
	public static final int NO_OBJECT                     = 10003;
//10100 - 10199 FOR UPDATE CLIENT MANAGEMENT	
	public static final int CRT_NOT_MATCH_KEY             = 10100;
	public static final int CRT_TIME_INVALID              = 10101;
	public static final int CRT_BIT_FALSE                 = 10102;
	public static final int CRT_NOT_IN_KEY_USAGE          = 10103;
	public static final int CRT_NOT_NULL                  = 10104;
	public static final int CRT_INVALID                   = 10105;
	public static final int KEY_INVALID                   = 10106;
	public static final int PASSWORD_INVALID              = 10107;
	
	
// Exception code for troubleshooting tool
	public static final String TROUBLESHOOTING_EXCEPTION  = "enum.acm.troubleshooting.";
	
	public static final int UNKNOW                        = 0;
	public static final int OK                            = 1;
	public static final int DB_CONNECTION_FAILE           = 2;
	public static final int MQ_DOWN                       = 3;
	public static final int MEMCACHED_DOWN                = 4;
	public static final int PNS_DOWN                      = 5;
	public static final int HEARTBEAT_DOWN                = 6;
	public static final int SCHEDULE_DOWN                 = 7;
	public static final int SERVER_UNREACHABLE            = 100;
	
	public CertificateGenSV(){
		
	}
	
	public String transEnableClientManageToXML(String customId,String hmId,String clientManagementStatus){
		XStream xs = new XStream(new DomDriver());
		EnableClientManageEBO ecmEBO = new EnableClientManageEBO();
		ecmEBO.setCustomId(customId);
		ecmEBO.setHmId(hmId);
		if(clientManagementStatus != "" && clientManagementStatus != null){
			ecmEBO.setClientManagementStatus(clientManagementStatus);
		}
/*		if(enableCidPolicy != "" && enableCidPolicy != null){
			ecmEBO.setCidPolicyEnforcement(enableCidPolicy);
		}*/
		/*if(useCusCert != "" && useCusCert != null){
			ecmEBO.setUseCustomCert(useCusCert);
		}
		if(caFile != null){
			Base64 b64 = new Base64();
			ecmEBO.setOnboardCA(b64.encodeToString(caFile));
		}else{
			ecmEBO.setOnboardCA(" ");
		}
		if(caKeyFile != null && caKeyFile.trim() != ""){
			Base64 b64 = new Base64();
			ecmEBO.setOnboardCAKey(b64.encodeToString(caKeyFile.getBytes()));
		}else{
			ecmEBO.setOnboardCAKey(caKeyFile);
		}*/
		xs.processAnnotations(EnableClientManageEBO.class);
		return xs.toXML(ecmEBO);
	}
	
	private String importCertificate(String customerId,String hmId,String certType
			,String certName,byte[] certPayload,String certPrivateKey,String privateKeyPassword){
		XStream xs = new XStream(new DomDriver());
		CertificateImportEBO certImport = new CertificateImportEBO();
		certImport.setCustomId(customerId);
		certImport.setHmId(hmId);
		if(certType != "" && certType != null){
			certImport.setCertType(certType);
		}
		if(certPrivateKey != "" && certPrivateKey != null){
			certImport.setCertPrivateKey(new Base64().encodeToString(certPrivateKey.getBytes()));
		}
		if(certName != "" && certName != null){
			certImport.setCertName(certName);
		}
		if(privateKeyPassword != "" && privateKeyPassword != null){
			certImport.setPrivateKeyPassword(privateKeyPassword);
		}
		if(certPayload != null){
			Base64 b64 = new Base64();
			certImport.setCertPayload(b64.encodeToString(certPayload));
		}
		xs.processAnnotations(CertificateImportEBO.class);
		return xs.toXML(certImport);
	}
	
	private String queryCertificate(String customerId,String hmId,String certType){
		XStream xs = new XStream(new DomDriver());
		CertificateQueryEBO certQuery = new CertificateQueryEBO();
		certQuery.setCustomId(customerId);
		certQuery.setHmId(hmId);
		if(certType != "" && certType != null){
			certQuery.setCertType(certType);
		}
		xs.processAnnotations(CertificateQueryEBO.class);
		return xs.toXML(certQuery);
	}
	
	private CertificateQueryResEBO transResponseToCert(ResponseModel responseModel){
		if(responseModel == null){
			return new CertificateQueryResEBO();
		}
		try{
			String cert = responseModel.getResponseText();
			XStream xs = new XStream(new DomDriver());
			xs.processAnnotations(CertificateQueryResEBO.class);
			return (CertificateQueryResEBO)xs.fromXML(cert);
		}catch(Exception e){
			log.error(CertificateGenSV.class.getSimpleName()+":CertificateQueryResEBO()","Failed when transfer xml to object", e);
			return null;
		}
	}
	
	public String transCsrCertificateToXML(String customerId,String hmId,String certType,String csrPayload){
		XStream xs = new XStream(new DomDriver());
		CsrCertificateEBO csrEBO = new CsrCertificateEBO();
		csrEBO.setCustomerId(customerId);
		csrEBO.setHmId(hmId);
		if(certType != "" && certType != null){
			csrEBO.setCertType(certType);
		}
		if(csrPayload != "" && csrPayload != null){
			csrEBO.setCsrPayload(csrPayload);
		}
		xs.processAnnotations(CsrCertificateEBO.class);
		return xs.toXML(csrEBO);
	}
	
	public String resetCaToXML(String customerId,String hmId,String certType){
		XStream xs = new XStream(new DomDriver());
		CAResetEBO res = new CAResetEBO();
		res.setCustomId(customerId);
		res.setHmId(hmId);
		res.setCertType(certType);
		xs.processAnnotations(CAResetEBO.class);
		return xs.toXML(res);
	}
	
	public OnBoardCaEBO transResponseToOnBoardCa(ResponseModel responseModel){
		if(responseModel == null){
			return new OnBoardCaEBO();
		}
		try{
			String onBoardCaXML = responseModel.getResponseText();
			XStream xs = new XStream(new DomDriver());
			xs.processAnnotations(OnBoardCaEBO.class);
			return (OnBoardCaEBO)xs.fromXML(onBoardCaXML);
		}catch(Exception e){
			log.error(CertificateGenSV.class.getSimpleName()+":transResponseToOnBoardCa()","Failed when transfer xml to object", e);
			return null;
		}
	}
	
	public Exception4ACM transResponseToException(ResponseModel responseModel){
		if(responseModel == null){
			return new Exception4ACM();
		}
		try{
			String exception4ACM = responseModel.getResponseText();
			XStream xs = new XStream(new DomDriver());
			xs.processAnnotations(Exception4ACM.class);
			return (Exception4ACM)xs.fromXML(exception4ACM);
		}catch(Exception e){
			log.error(CertificateGenSV.class.getSimpleName()+":transResponseToException()","Failed when transfer xml to object", e);
			return null;
		}
	}
	
   private String checkACMServerRequestBody(String customerId,String hmId){
       if(StringUtils.isBlank(customerId)) return "";
       
        XStream xs = new XStream(new DomDriver());
        CheckCMServiceEBO bo = new CheckCMServiceEBO();
        bo.setCustomId(customerId);
        bo.setHmId(hmId);
        xs.processAnnotations(CheckCMServiceEBO.class);
        return xs.toXML(bo);
   }
	        
	public AcmTroubleEBO transResponseToTrouble(ResponseModel responseModel){
		if(responseModel == null){
			return new AcmTroubleEBO();
		}
		try{
			String acmTroubleEBO = responseModel.getResponseText();
			XStream xs = new XStream(new DomDriver());
			xs.processAnnotations(AcmTroubleEBO.class);
			return (AcmTroubleEBO)xs.fromXML(acmTroubleEBO);
		}catch(Exception e){
			log.error(CertificateGenSV.class.getSimpleName()+":transResponseToTrouble()","Failed when transfer xml to object", e);
			return null;
		}
	}
	
	public String checkTrouble(AcmTroubleEBO trb){
		String suc = "";
		if(!trb.getStatusCode().equals("1")){
			suc = trb.getStatusCode();
		}
		return suc;
	}
	
	public SignedCertificateEBO transResponseToSignedCertificate(ResponseModel responseModel){
		if(responseModel == null){
			return new SignedCertificateEBO();
		}
		try{
			String signedCertXML = responseModel.getResponseText();
			XStream xs = new XStream(new DomDriver());
			xs.processAnnotations(SignedCertificateEBO.class);
			return (SignedCertificateEBO)xs.fromXML(signedCertXML);
		}catch(Exception e){
			e.printStackTrace();
			log.error(CertificateGenSV.class.getSimpleName()+":transResponseToSignedCertificate()","Failed when transfer xml to object", e);
			return null;
		}
	}
	
	public boolean writeFile(String fileName,String content) throws IOException{
		File fl = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		try{
			fl = new File(fileName);
			if(CertificateGenSV.existFile(fileName)){
				fl.delete();
			}
			fw = new FileWriter(fl);
			bw = new BufferedWriter(fw);
			bw.write(content,0,content.length());
			bw.close();
			fw.close();
			return true;
			}catch(Exception e){
				bw.close();
				fw.close();
				log.error("writeFile()", "Error when write the string to the file" + fileName, e);
			}
		return false;
	}
	
	public static void writeFile(String pathName,byte[] bytes) throws IOException{
		File fl = new File(pathName);
		if(existFile(pathName)){
			fl.delete();
		}
		ByteBuffer bb = ByteBuffer.allocate(bytes.length);
		bb.put(bytes);
		bb.flip();
		FileChannel fileChannel = null;
		try {
			fileChannel = new FileOutputStream(pathName, false).getChannel();
			fileChannel.write(bb);
		} finally {
			if (fileChannel != null) {
				try {
					fileChannel.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}
	
	@SuppressWarnings("resource")
	public static byte[] readFromFile(File file) throws IOException {
		FileChannel fileChannel = new FileInputStream(file).getChannel();
		ByteBuffer bb = ByteBuffer.allocate((int) fileChannel.size());
		fileChannel.read(bb);
		fileChannel.close();
		bb.flip();
		byte[] bytes;

		if (bb.hasArray()) {
			bytes = bb.array();
		} else {
			bytes = new byte[bb.limit()];
			bb.get(bytes);
		}

		return bytes;
	}
	
	public String readFile(String fileName) throws IOException{
		StringBuffer ss = new StringBuffer();
		File file = null;
		String strt = "";
		FileReader fr = null;
		BufferedReader bur = null;
		try{
			file = new File(fileName);
			fr = new FileReader(file);
			bur = new BufferedReader(fr);
			bur.readLine();
			while((strt = bur.readLine()) != null){
				if(!strt.contains("END CERTIFICATE REQUEST")){
					ss.append(strt);
				}else{
					return ss.toString();
				}
			}
			bur.close();
			fr.close();
		}catch(Exception e){
			bur.close();
			fr.close();
			log.error("readFile()","Try to read file from " + fileName,e);
		}
		return ss.toString();
	}
	
	public static boolean existFile(String file){
		boolean isExist = false;
		try{
			File f = new File(file);
			if(f.isFile() && f.exists()){
				isExist = true;
			}else{
				isExist = false;
			}
		}catch(Exception e){
			e.printStackTrace();
			log.info(CertificateGenSV.class.getSimpleName(),": existFile()");
		}
		return isExist;
	}
	
	public static boolean existsFile(String domainName){
		
		String onboard = BeAdminCentOSTools.AH_CERTIFICAT_PFEFIX
		                 + domainName
		                 + BeAdminCentOSTools.AH_CERTIFICATE_HOME
		                 + File.separator + "ClientMgmt_CA.crt";
		String cert = BeAdminCentOSTools.AH_CERTIFICAT_PFEFIX
		              + domainName
		              + BeAdminCentOSTools.AH_CERTIFICATE_HOME
		              + File.separator + "ClientMgmt-Radius-Server_Crt.crt";
		String key = BeAdminCentOSTools.AH_CERTIFICAT_PFEFIX
		             + domainName
		             + BeAdminCentOSTools.AH_CERTIFICATE_HOME
		             + File.separator + "ClientMgmt-Radius-Server_key.pem";
		if(!CertificateGenSV.existFile(cert)){
			return false;
		}else if(!CertificateGenSV.existFile(onboard)){
			return false;
		}else if(!CertificateGenSV.existFile(key)){
			return false;
		}
		return true;
	}
	
	public void copyFiles(String home,String destDomain){
		List<String> crtFiles = new ArrayList<String>(3);
		crtFiles.add(new String(SERVER_CRT));
		crtFiles.add(new String(ONBOARD_CA));
		crtFiles.add(new String("ClientMgmt-Radius-Server_key.pem"));
		for(int i = 0;i < crtFiles.size();i++){
			copyFile(home,destDomain,crtFiles.get(i));
		}
	}
	
	public void copyFile(String home,String dest,String fileName){
		String srcFile = BeAdminCentOSTools.AH_CERTIFICAT_PFEFIX
	                     + home
	                     + BeAdminCentOSTools.AH_CERTIFICATE_HOME
	                     + File.separator + fileName;
		String dir = BeAdminCentOSTools.AH_CERTIFICAT_PFEFIX
                     + dest
                     + BeAdminCentOSTools.AH_CERTIFICATE_HOME
                     + File.separator;
		String desFile = dir + fileName;
		if(FileManager.getInstance().existsFile(dir)){
			try{
				if(FileManager.getInstance().existsFile(desFile)){
					FileManager.getInstance().deletefile(desFile);
				}
				if(FileManager.getInstance().existsFile(srcFile)){
					FileManager.getInstance().createFile("", desFile);
					FileManager.getInstance().copyFile(srcFile,desFile );
				}
			}catch(Exception e){
				log.error("Try to copy files from home domain to" + dest + ",File name is:" + fileName);
			}
		}
	}
	
	public static String getUrl(){
		return ConfigUtil.getACMConfigServerUrl();
	/*	return ConfigUtil.getConfigInfo(ConfigUtil.SECTION_AEROHIVE_MDM, 
                ConfigUtil.KEY_URL_ROOT_PATH);*/
	}
	
	public void copyCrt(String homeDomain,String destDomain){
		try{
			
			copyFiles(homeDomain,destDomain);
			
		}catch(Exception e){
			log.error("CertificateGenSV: copyCrt()", "try to copy cert files from " + homeDomain + " to " + destDomain);
		}
	}
	
	private void synOnboard(HMServicesSettings hms,HMServicesSettings hbo){
		if(hms == null || hbo == null){
			return ;
		}
		hms.setEnableCidPolicyEnforcement(hbo.isEnableCidPolicyEnforcement());
		hms.setEnableClientManagement(hbo.isEnableClientManagement());
		hms.setApiKey(hbo.getApiKey());
	}
	
	/**
	 * 
	 * @param enableClient
	 * @param domain
	 * @param customerId
	 * @return
	 */
	public static String certificateGenereate(boolean enableClient,String domain,String customerId,String hmId){
		HMServicesSettings bo = null;
		String result = "";
		try{
			bo = QueryUtil.findBoByAttribute(HMServicesSettings.class,"domainname", domain);
			result = certificateGenereate(enableClient,customerId,hmId,domain, "OnboardCA", "AuthServer",
					new CertificateGenSV().createCsrData(domain, "ClientMgmt-Radius-Server"),bo);
			if("".equals(result)){
				bo.setEnableClientManagement(enableClient);
	    		bo.setEnableCidPolicyEnforcement(false);
	    		QueryUtil.updateBo(bo);
			}else{
				return result;
			}
		}catch(Exception e){
			log.error("certificateGenerate()","Error when generate the certificate." + "\r\n" + bo + "\r\n" + result,e);
		}
		return result;
	}
	
	public static String certificateGenereate(boolean enableClient,String customerId,String hmId,String domain,
			                  String caType,String certType,BeRootCADTO data,HMServicesSettings bo) throws Exception{
		String rt = "";
		String enableOnboard = "";
		String enableCid = "";
//		String useCustomCert = "";
		String cerDic = BeAdminCentOSTools.AH_CERTIFICAT_PFEFIX
                        + domain
                        + BeAdminCentOSTools.AH_CERTIFICATE_HOME
                        + File.separator;
		String csrf = data.getFileName() + ".csr";
		if(enableClient == true){
			enableOnboard = "1";
		}else{
			enableOnboard = "0";
		}
//		if(useCusCert == true){
//			useCustomCert = "1";
//		}else{
//			useCustomCert = "0";
//		}
		try{
			IResponseFromMDM res = new ResponseFromMDMImpl();
			CertificateGenSV cerSV = new CertificateGenSV();
			String clientm = cerSV.transEnableClientManageToXML(customerId,hmId,enableOnboard);
			ResponseModel resm = res.sendInfoToMDM(getUrl() + ENABLE_URI, clientm);
			if(enableClient == true){
				OnBoardCaEBO onb = null;
				if(HttpStatus.SC_OK == resm.getResponseCode()){
					onb = cerSV.transResponseToOnBoardCa(resm);
				}else{
					return MgrUtil.getUserMessage("home.clientManagement.enable.ca", new String[]{hmId,customerId});
				}
				
				//save the apikey to the database
				bo.setApiKey(onb.getApiKey());
				Base64 b64 = new Base64();
				byte[] content = b64.decode(onb.getCertPayload());
				cerSV.writeFile(cerDic + CertificateGenSV.ONBOARD_CA,new String(content));	
				boolean isSuccess = HmBeAdminUtil.createServerCSR(data);
				if(isSuccess == true){
					String csrContent = cerSV.readFile(cerDic + csrf);
					String csrTem = cerSV.transCsrCertificateToXML(customerId,hmId, certType,csrContent);
					ResponseModel csrRes = res.sendInfoToMDM(getUrl() + CERT_URI, csrTem);
					if(HttpStatus.SC_OK != csrRes.getResponseCode()){
						return MgrUtil.getUserMessage("home.clientManagement.enable.server", new String[]{hmId,customerId});
					}
					SignedCertificateEBO sce = cerSV.transResponseToSignedCertificate(csrRes);
				    byte[] sc = b64.decode(sce.getCertPayload());
				    cerSV.writeFile(cerDic + CertificateGenSV.SERVER_CRT, new String(sc));
				    
				    boolean result = generatePfxCertificate(cerDic);
				    if(!result){
				    	return MgrUtil.getUserMessage("home.clientManagement.enable.pfx", new String[]{hmId,customerId});
				    }
				}
			}
			return rt;
		}catch(IOException e){
			throw new IOException();
		}catch(Exception e){
			log.error("certificateGenerate()","Error when generate the certificate",e);
			throw new Exception();
			//e.printStackTrace();
		}
	}
	
	public static boolean generatePfxCertificate(String cerDic){
		String strCmd = "";
		StringBuffer strCmdBuf = new StringBuffer();
		strCmdBuf.append("openssl pkcs12 -export -out ");
		strCmdBuf.append(cerDic + CertificateGenSV.SERVER_PFX);
		strCmdBuf.append(" -inkey ");
		strCmdBuf.append(cerDic + CertificateGenSV.SERVER_KEY);
		strCmdBuf.append(" -in ");
		strCmdBuf.append(cerDic + CertificateGenSV.SERVER_CRT);
		strCmdBuf.append(" -password pass:aerohive");
		strCmd = strCmdBuf.toString();
		boolean result = BeAdminCentOSTools.exeSysCmd(strCmd);
		return result;
	}
	
	public static String importCert(String customId,String hmId,byte[] ca,String key,String onboard,String password,String caName){
		CertificateGenSV certSv = new CertificateGenSV();
		IResponseFromMDM res = new ResponseFromMDMImpl();
		String certs = certSv.importCertificate(customId,hmId, onboard, caName, ca, key, password);
		try{
			ResponseModel resm = res.sendInfoToMDM(getUrl() + IMPORT_CERT,certs);
			if(HttpStatus.SC_OK != resm.getResponseCode()){
				return MgrUtil.getEnumString(UPDATE_EXCEPTION + 
					       certSv.transResponseToException(resm).getExceptionCode());
			}
		}catch(IOException e){
			log.error("certificateGenerate()","Error when importCert",e);
			return "Unknow error.";
		}catch(Exception e){
			log.error("certificateGenerate()","Error when importCert",e);
			return "Unknow error.";
		}
		return "";
	}
	
	public boolean signCsrByClientCA(String domain,String customerId,String hmId,String csrf){
		String cerDic = BeAdminCentOSTools.AH_CERTIFICAT_PFEFIX
                + domain
                + BeAdminCentOSTools.AH_CERTIFICATE_HOME
                + File.separator;
		boolean isSuccess = false;
		IResponseFromMDM res = new ResponseFromMDMImpl();
		Base64 b64 = new Base64();
		try{
			String csrContent = readFile(cerDic + csrf + ".csr");
			String csrTem = transCsrCertificateToXML(customerId,hmId, "AuthServer",csrContent);
			ResponseModel csrRes = res.sendInfoToMDM(getUrl() + CERT_URI, csrTem);
			SignedCertificateEBO sce = transResponseToSignedCertificate(csrRes);
			byte[] sc = b64.decode(sce.getCertPayload());
			writeFile(cerDic + csrf + ".crt", new String(sc));
			isSuccess = true;
		}catch(Exception e){
			log.error(e.getMessage(),"error happened when sign csr by client CA.",e);
		}
		return isSuccess;
	}
	
	public static void saveCert(byte[] cert,String name,String domain) throws IOException{
		String cerDic = BeAdminCentOSTools.AH_CERTIFICAT_PFEFIX
                + domain
                + BeAdminCentOSTools.AH_CERTIFICATE_HOME
                + File.separator;
		writeFile(cerDic + name,cert);
	}
	
	public static boolean resetCA(String customerId,String hmId,String certType){
		boolean isSuccess = false;
		IResponseFromMDM res = new ResponseFromMDMImpl();
		String reset = new CertificateGenSV().resetCaToXML(customerId,hmId,certType);
		try {
			ResponseModel resModel = res.sendInfoToMDM(getUrl() + RESET_CA, reset);
			if(resModel.getResponseCode() == HttpStatus.SC_OK){
				isSuccess = true;
			}
		} catch (Exception e) {
			log.error(e.getMessage(),"error happened when reset client CA.",e);
		}
		return isSuccess;
	}
	
	public static CertificateQueryResEBO queryCert(String customId,String hmId,String onboard){
		CertificateQueryResEBO cert = null;
		CertificateGenSV certSv = new CertificateGenSV();
		IResponseFromMDM res = new ResponseFromMDMImpl();
		String certs = certSv.queryCertificate(customId,hmId, onboard);
		try{
			ResponseModel resm = res.sendInfoToMDM(getUrl() + QUERY_CERT,certs);
			if(HttpStatus.SC_OK == resm.getResponseCode()){
				cert = certSv.transResponseToCert(resm);
			}
		}catch(IOException e){
			log.error("certificateGenerate()","Error when queryCert",e);
		}catch(Exception e){
			log.error("certificateGenerate()","Error when queryCert",e);
		}
		return cert;
	}

	public static PairValue<Boolean, PairValue<String, String>> troubleshooting(String customerId, HmDomain domain){
	    PairValue<Boolean, PairValue<String, String>> values;
		IResponseFromMDM res = new ResponseFromMDMImpl();
		CertificateGenSV cerSV = new CertificateGenSV();
		try{
            String hmId = domain.isHomeDomain() ? BeLicenseModule.HIVEMANAGER_SYSTEM_ID : domain.getVhmID();
            
            List<Header> header = new ArrayList<>();
            header.add(new Header("Accept", "application/xml"));
            header.add(new Header("Content-Type", "application/xml"));
            
            ResponseModel resm = res.sendInfoToMDM(getUrl() + TROUBLESHOOTING,
                    cerSV.checkACMServerRequestBody(customerId, hmId),
                    header);
			if(HttpStatus.SC_OK == resm.getResponseCode()){
				AcmTroubleEBO trb = cerSV.transResponseToTrouble(resm);
				
				String warningmessage = "";
				final String apiKey = trb.getApiKey();
				if(StringUtils.isNotBlank(apiKey)) {
				    HMServicesSettings hmsettings = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner", domain);
				    if(null != hmsettings && (StringUtils.isBlank(hmsettings.getApiKey())
				            || (StringUtils.isNotBlank(hmsettings.getApiKey())
				                    && !hmsettings.getApiKey().equals(apiKey)))){
				        // ApiKey doesn't exist or mismatch
				        hmsettings.setApiKey(apiKey);
				        QueryUtil.updateBo(hmsettings);
				        warningmessage = MgrUtil.getUserMessage("home.hmSettings.clientManagement.troubleshooting.warning");
				    }
				}
				
				if(trb.getStatusCode().equals("1")){
				    // empty
				    values = new PairValue<Boolean, PairValue<String, String>>(true, new PairValue<>("", warningmessage));
				} else {
				    values = new PairValue<Boolean, PairValue<String, String>>(false, new PairValue<>(trb.getStatusCode(), warningmessage));
				}
				return values;		
			}else{
				Exception4ACM excep = cerSV.transResponseToException(resm);
				return new PairValue<Boolean, PairValue<String, String>>(false, new PairValue<>(excep.getExceptionCode(), ""));
			}
		}catch(IOException e){
			log.error("certificateGenerate()","Error when troubleshooting, server unreachable",e);
			return new PairValue<Boolean, PairValue<String, String>>(false, new PairValue<>(Integer.toString(SERVER_UNREACHABLE), ""));
		}catch(Exception e){
			log.error("certificateGenerate()","Error when troubleshooting",e);
		}
		
		return new PairValue<Boolean, PairValue<String, String>>(false, new PairValue<>(Integer.toString(UNKNOW), ""));
	}
	
	public static void synCert4VHM(boolean enableClient,HMServicesSettings bo){
		try{
			if(!NmsUtil.isHostedHMApplication()){
				CertificateGenSV cerSV = new CertificateGenSV();
				String where = "domainname not in (:s1,:s2)";
				Object[] binds = new String[]{"home","global"};
				List<HmDomain> domains = (List<HmDomain>)QueryUtil.executeQuery(HmDomain.class, null, new FilterParams(where,binds));
		    	if(!domains.isEmpty()){
		    		for(int i = 0;i < domains.size();i++){
		    			HmDomain domain = (HmDomain)(domains.get(i));
		    			if(enableClient == true){
		    				cerSV.copyCrt("home",domain.getDomainName());
		    			}
		    			HMServicesSettings hmSvSettings = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner", domain);
		    			cerSV.synOnboard(hmSvSettings,bo);
		                QueryUtil.updateBo(hmSvSettings);
			    	}
		    	}
		    }
		}catch(Exception e){
			log.error("Try to copy certificate files to vhm");
		}
	}
	
	public static void copyCrt4VHM(String srcDomain,String desDomain){
		CertificateGenSV cerSV = new CertificateGenSV();
	    cerSV.copyCrt(srcDomain,desDomain);
	}
	
	public static void genRadiusServerCert(String customerId,String hmId,String domain,String fileName,String certType,String certName){
		String cerDic = BeAdminCentOSTools.AH_CERTIFICAT_PFEFIX
                + domain
                + BeAdminCentOSTools.AH_CERTIFICATE_HOME
                + File.separator;
		try{
			CertificateGenSV cerSV = new CertificateGenSV();
			IResponseFromMDM res = new ResponseFromMDMImpl();
			HmBeAdminUtil.createServerCSR(cerSV.createCsrData(domain,fileName));
			String csrContent = cerSV.readFile(cerDic + fileName + ".csr");
			String csrTem = cerSV.transCsrCertificateToXML(customerId,hmId, certType,csrContent);
			ResponseModel csrRes = res.sendInfoToMDM(getUrl() + CERT_URI, csrTem);
			SignedCertificateEBO sce = cerSV.transResponseToSignedCertificate(csrRes);
		    byte[] sc = new Base64().decode(sce.getCertPayload());
		    cerSV.writeFile(cerDic + certName, new String(sc));
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private BeRootCADTO createCsrData(String domain,String server){
		BeRootCADTO dto = new BeRootCADTO();
    	dto.setCommName(CertificateGenSV.COMMON_NAME);
    	dto.setCountryCode(CertificateGenSV.COUNTRY);
    	dto.setKeySize("1024");
    	dto.setLocalityName(CertificateGenSV.LOCALITY_NAME);
    	dto.setOrgName(CertificateGenSV.ORGANIZATION);
    	dto.setOrgUnit(CertificateGenSV.ORGANIZATION_UNIT);
    	dto.setStateName(CertificateGenSV.STATE);
    	dto.setFileName(server);
    	dto.setPassword("");
    	dto.setDomainName(domain);
        return dto;
	}
	
	public static X509Certificate ananysisP12(InputStream in, char[] keyStorePassword) throws KeyStoreException, NoSuchAlgorithmException,
	   CertificateException, IOException
	 {
	  KeyStore keyStore = KeyStore.getInstance("PKCS12");
	  keyStore.load(in, keyStorePassword);
	  in.close();
	  Enumeration<String> enums = keyStore.aliases();
	  if (enums.hasMoreElements())
	  {
	   String keyAlis = enums.nextElement();
	   X509Certificate certificate = (X509Certificate) keyStore.getCertificate(keyAlis);
	   return certificate;
	  }
	  return null;
	 }
	
}
