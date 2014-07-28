package com.ah.ui.actions.monitor;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.cli.AhCliFactory;
import com.ah.be.common.SendMailUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeCapwapCliResultEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.license.HM_License;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.MailNotification;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApAutoProvision;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.monitor.OneTimePassword;
import com.ah.bo.network.VpnGatewaySetting;
import com.ah.bo.network.VpnService;
import com.ah.bo.network.VpnServiceCredential;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.config.VpnServiceAction;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.coder.AhDecoder;

public class OneTimePasswordAction  extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;
	
	private static String vhmId;
	
	private static final Tracer log = new Tracer(VpnServiceAction.class
			.getSimpleName());
	
	public static final int COLUMN_PASSWORD = 1;

	public static final int COLUMN_USERNAME = 2;

	public static final int COLUMN_EAMINADDRESS = 3;
	
	public static final int COLUMN_SENT_DATE = 4;

	public static final int COLUMN_ACTIVATE_DATE = 5;

	public static final int COLUMN_DEVICE_MODEL=6;
	
	public static final int COLUMN_DEVICE_IDENTIFIER = 7;

	public static final int COLUMN_AUTO_PROVISION = 8;

	public static final int COLUMN_DESCRIPTION = 9;
	
	
	
	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
		case COLUMN_PASSWORD:
			code = "monitor.otp.list.password";
			break;
		case COLUMN_USERNAME:
			code = "monitor.otp.list.username";
			break;
		case COLUMN_EAMINADDRESS:
			code = "monitor.otp.list.email";
			break;
		case COLUMN_SENT_DATE:
			code = "monitor.otp.list.sent.date";
			break;
		case COLUMN_ACTIVATE_DATE:
			code = "monitor.otp.list.activate.date";
			break;
		case COLUMN_DEVICE_MODEL:
			code = "monitor.otp.list.device.model";
			break;
		case COLUMN_DEVICE_IDENTIFIER:
			code = "monitor.otp.list.device.identifier";
			break;
		case COLUMN_AUTO_PROVISION:
			code = "monitor.otp.list.autoprovision";
			break;
		case COLUMN_DESCRIPTION:
			code = "monitor.otp.list.description";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}

	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		columns.add(new HmTableColumn(COLUMN_PASSWORD));
		columns.add(new HmTableColumn(COLUMN_USERNAME));
		columns.add(new HmTableColumn(COLUMN_EAMINADDRESS));
		columns.add(new HmTableColumn(COLUMN_SENT_DATE));
		columns.add(new HmTableColumn(COLUMN_ACTIVATE_DATE));
		columns.add(new HmTableColumn(COLUMN_DEVICE_MODEL));
		columns.add(new HmTableColumn(COLUMN_DEVICE_IDENTIFIER));
		columns.add(new HmTableColumn(COLUMN_AUTO_PROVISION));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}
	
	@Override
	public String execute() throws Exception{
		try {
			if("generate".equals(operation)){
				prepareForOneTimePasswords();
				generateOTPList();
				String returnValue = prepareOTPBoList();
				getLazyInfoList();
				return returnValue;
			}else if("edit".equals(operation)) {
				addLstTitle(getText("monitor.otp.operation.assign")+ "'");
				return editBo(this);
			}else if("assign".equals(operation)){
				return updateBo();
			}else if("update".equals(operation)){
				return updateBo();
			}else if("send".equals(operation)){
				emailOTPS();
				return prepareOTPBoList();
			}else if("revoke".equals(operation)){
				revokeOTPS();
				return prepareOTPBoList();
			}else if("import".equals(operation)){
				addLstForward("oneTimePassword");
				clearErrorsAndMessages();
				return operation;
			} else if ("export".equals(operation)) {
				filterParams = getSessionFilter();
				setSessionFiltering();
				prepareBoList();
				getLazyInfoList();
				return "export";
			} else if ("search".equals(operation)) {
				saveFilter();
				setSessionFilter(filterParams);
				String returnValue = prepareBoList();
				getLazyInfoList();
				return returnValue;
			}
			else {
				baseOperation();
				String returnValue = prepareOTPBoList();
				getLazyInfoList();
				return returnValue;
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}
	
	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_ONETIMEPASSWORD);
		setDataSource(OneTimePassword.class);
		keyColumnId = COLUMN_PASSWORD;
		this.tableId = HmTableColumn.TABLE_ONETIMEPASSWORD;
	}

	@Override
	public OneTimePassword getDataSource() {
		return (OneTimePassword) dataSource;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if(null != bo){
			if(bo instanceof OneTimePassword){
				OneTimePassword otp = (OneTimePassword)bo;
				if(null != otp.getHiveApAutoProvision()){
					otp.getHiveApAutoProvision().getId();
				}
			}
		}
		return null;
	}
	
	public boolean checkEmailAddressExists(String name, Object value) {
		List<?> boIds = QueryUtil.executeQuery(
				"select id from " + boClass.getSimpleName(), null,
				new FilterParams(name, value), domainId);
		if (!boIds.isEmpty()) {
			addActionError(MgrUtil.getUserMessage("error.otp.eamilExists",
					value.toString()));
			return true;
		} else {
			return false;
		}
	}
	
	public void getLazyInfoList() throws Exception{
		List<?> lst = getPage();
		List<OneTimePassword> lstValue = new ArrayList<OneTimePassword>();
		if (lst != null && lst.size() > 0) {
			String query = "select bo.hiveApAutoProvision.id "
					+ "from " + OneTimePassword.class.getSimpleName() + " bo";
			for (Object obj : lst) {
				OneTimePassword otp = (OneTimePassword) obj;
				List<?> lst_obj = QueryUtil.executeQuery(query, null,
						new FilterParams("id", otp.getId()));
				if (!lst_obj.isEmpty()) {
					Long id;
					if (lst_obj.get(0) != null) {
						id = Long.parseLong(lst_obj.get(0).toString());
						HiveApAutoProvision apAuto = QueryUtil
								.findBoById(HiveApAutoProvision.class, id);
						if (apAuto != null)
							otp.setHiveApAutoProvision(apAuto);
					}
					
				}
				lstValue.add(otp);
			}
			super.page = lstValue;
		}
	}

	public static Tracer getLog() {
		return log;
	}
	
	private void emailOTPS() {
		int count = 0;
		
		/*
		 * get mail settings
		 */
		List<MailNotification> mailNotification = QueryUtil.executeQuery(MailNotification.class,
															null, 
															null, 
															getDomain().getId());

		if (!mailNotification.isEmpty()) {
			String serverName = mailNotification.get(0)
					.getServerName();
			String mailFrom = mailNotification.get(0)
					.getMailFrom();
			
			if (serverName == null 
					|| serverName.equals("")) { 
				addActionError(MgrUtil.getUserMessage("error.gml.email.setting.wrong",
						"SMTP server"));
				return ;
			}
			
			if(mailFrom == null
					|| mailFrom.equals("")) {
				addActionError(MgrUtil.getUserMessage("error.gml.email.setting.wrong",
					"Source mail address"));
			}

		} else {
			addActionError(MgrUtil.getUserMessage("error.gml.email.setting.wrong",
				"Email setting"));
			return;
		}

		/*
		 * set mail
		 */
		if (isAllItemsSelected()) {
			List<OneTimePassword> passwords = QueryUtil.executeQuery(
					OneTimePassword.class, 
					new SortParams("oneTimePassword"), 
					filterParams,
					getDomainId());
			
			for (OneTimePassword otp : passwords) {
				if(emailOTP(otp, mailNotification)) {
					count++;
					otp.setDateSentStamp(System.currentTimeMillis());
					otp.setDateTimeZone(getDomain() != null ? getDomain().getTimeZoneString()
							: TimeZone.getDefault().getID());
				}
			}
			try {
				QueryUtil.bulkUpdateBos(passwords);
			} catch (Exception e) {
				log.error("bulkUpdateBos failed ", e);
			}
			
		} else {
			for (Long id : getAllSelectedIds()) {
				OneTimePassword otp;
				try {
					otp = findBoById(OneTimePassword.class, id);
					
					if(otp == null) {
						continue;
					}
					
					if(emailOTP(otp, mailNotification)) {
						count++;
						otp.setDateSentStamp(System.currentTimeMillis());
						otp.setDateTimeZone(getDomain() != null ? getDomain().getTimeZoneString()
								: TimeZone.getDefault().getID());
						QueryUtil.updateBo(otp);
					}
				} catch (Exception e) {
					log.error("Cannot get user(id=" + id + ") from database", e);
				}
			}
		}
	
		if (count > 1) {
			addActionMessage(MgrUtil.getUserMessage(OBJECT_EMAIL, 
					formatNum(count) + " devices registration code have "));
		} else {
			addActionMessage(MgrUtil.getUserMessage(OBJECT_EMAIL, 
					formatNum(count) + " device registration code has "));
		}
	}
	
	//1-one,2-two...10-ten,11-11...
	private static String formatNum(int count){
		String result = "";
		String[] numStr = new String[]{
				"Zero","One","Two","Three","Four","Five","Six","Seven","Eight","Nine","Ten"
		};
		if(count < numStr.length){
			result = numStr[count];
		} else {
			result = String.valueOf(count);
		}
		
		return result;
	}
	
	private boolean emailOTP(OneTimePassword password, List<MailNotification> mailNotification) {
		if(0 == password.getDateActivateStamp()) {
			if (!"".equals(password.getUserName()) && null != password.getUserName()) {
				if(!"".equals(password.getEmailAddress()) && null != password.getEmailAddress()){
					if (sendMail(password, mailNotification)) {
						return true;
					} else {
						addActionError(MgrUtil.getUserMessage("error.otp.email.send.fail", 
								password.getOneTimePassword()));
					}
				}else{
					addActionError(MgrUtil.getUserMessage("error.otp.emailaddress.null", 
							password.getOneTimePassword()));
				}
			} else {
				addActionError(MgrUtil.getUserMessage("error.otp.username.null", 
						password.getOneTimePassword()));
			}
		} else {
			addActionError(MgrUtil.getUserMessage("error.otp.error.activated", 
					password.getOneTimePassword()));
		}
	
		return false;
	}
	
	private boolean sendMail(OneTimePassword otp, List<MailNotification> mailNotification) {
		StringBuffer text = new StringBuffer();
		text.append("Hello ").append(otp.getUserName()).append("\n");
		text.append("The device registration code for you to install a configuration on your router is: \n").append(otp.getOneTimePassword()).append("\n");
		text.append("Please enter this device registration code when prompted on the first page displayed when you attempt to reach the Internet through the router. If your router has not been preconfigured, it will automatically download a configuration. After that, you can access the Internet as usual. You do not have to enter the device registration code again until your device is reset.\n");
		text.append("\n");
		text.append("\n");
		text.append("Aerohive Networks, Inc.");
	
		if (mailNotification != null && mailNotification.size() > 0) {
			SendMailUtil mailUtil = new SendMailUtil(mailNotification.get(0));
			mailUtil.setMailTo(otp.getEmailAddress());
			mailUtil.setSubject("Device registration code");
			mailUtil.setText(text.toString());
			
			try {
				mailUtil.startSend();
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}
	
	private void revokeOTPS(){
		int count = 0;
		List<Long> removedIds= new ArrayList<Long>();
		if (isAllItemsSelected()) {
			List<OneTimePassword> passwords = QueryUtil.executeQuery(
					OneTimePassword.class, 
					new SortParams("oneTimePassword"), 
					null,
					getDomainId());
			
			for (OneTimePassword otp : passwords) {
				if(0 == otp.getDateActivateStamp()) {
					count++;
					removedIds.add(otp.getId());
				}else{
					addActionError(MgrUtil.getUserMessage("error.otp.error.revoked", 
							otp.getOneTimePassword()));
				}
			}
		} else {
			for (Long id : getAllSelectedIds()) {
				OneTimePassword otp;
				try {
					otp = findBoById(OneTimePassword.class, id);
					if(otp == null) {
						continue;
					}
					count++;
					removedIds.add(otp.getId());
					
					if(0 != otp.getDateActivateStamp()) {
						//process the activated otp
						HiveAp ap = QueryUtil.findBoByAttribute(HiveAp.class, "macAddress", otp.getMacAddress(), new ImplQueryBo());
						
						if(null != ap){
							if(ap.isConnected()){
								revokeForBr(ap);
							}else{
								revokeForCvg(ap);
							}
						}
					}	
				} catch (Exception e) {
					log.error("In revokeOTPS can not get user(id=" + id + ") from database", e);
				}
			}
		}
		
		try {
			if(!removedIds.isEmpty()){
				QueryUtil.bulkRemoveBos(OneTimePassword.class, new FilterParams("id",removedIds));
			}
		} catch (Exception e) {
			log.error("In revokeOTPS bulkRemoveBos failed ", e);
		}
		
		/*
		 * return result
		 */
		if (count > 1) {
			addActionMessage(MgrUtil.getUserMessage("info.otp.objects.revoked", 
					formatNum(count) + " devices registration code have "));
		} else {
			addActionMessage(MgrUtil.getUserMessage("info.otp.objects.revoked", 
					formatNum(count) +" device registration code has "));
		}
	}
	
	private void revokeForBr(HiveAp ap){
		BeCliEvent cliEvent = new BeCliEvent();
		cliEvent.setAp(ap);
		String cli = AhCliFactory.getOTPRevokeResetCli();
		cliEvent.setClis(new String[] { cli });
		cliEvent.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
		
		try {
			cliEvent.buildPacket();
		} catch (BeCommunicationEncodeException e) {
			DebugUtil.performanceDebugError("In revokeOTPS failed to buildPacket ", e);
		}

		BeCommunicationEvent resultEvent = HmBeCommunicationUtil.sendSyncRequest(cliEvent);
		if (resultEvent == null) {
			DebugUtil.performanceDebugWarn("In revokeOTPS failed to get response of sent cli, cli:" + cli);
			revokeForCvg(ap);
		}
		if (null != resultEvent) {
			int msgType = resultEvent.getMsgType();
			switch (msgType) {
				case BeCommunicationConstant.MESSAGEELEMENTTYPE_CLIRESULT:
					try {
						resultEvent.parsePacket();
					} catch (Exception e) {
						DebugUtil.performanceDebugWarn("In revokeOTPS failed to parsePacket");
						revokeForCvg(ap);
					}
					BeCapwapCliResultEvent cliRetEvent = (BeCapwapCliResultEvent) resultEvent;
					if (!cliRetEvent.isCliSuccessful()) {
						revokeForCvg(ap);
					}
					break;
				case BeCommunicationConstant.MESSAGETYPE_CLIRSP:
				default:
					revokeForCvg(ap);
			}
		}
	}
	
	private void revokeForCvg(HiveAp ap){
		if(null != ap.getConfigTemplate() 
				&& null != ap.getConfigTemplate().getVpnService() 
				&& null != ap.getConfigTemplate().getVpnService().getVpnCredentials()){
			//update vpn service
			List<VpnServiceCredential> vpnCredentials = ap.getConfigTemplate().getVpnService().getVpnCredentials();
			VpnServiceCredential deleted = null;
			for(VpnServiceCredential vc:vpnCredentials){
				if(ap.getMacAddress().equalsIgnoreCase(vc.getAssignedClient())){
					deleted = vc;
					break;
				}
			}
			if(null != deleted){
				VpnService vs = ap.getConfigTemplate().getVpnService();
				vpnCredentials.remove(deleted);
				vs.setVpnCredentials(vpnCredentials);
				try {
					QueryUtil.updateBo(vs);
				} catch (Exception e1) {
					DebugUtil.performanceDebugError("In revokeForCvg failed to update vpn service :", e1);
				}
				// send cli to cvg
				List<VpnGatewaySetting> vpnGateWaysSetting = vs.getVpnGateWaysSetting();
				for(VpnGatewaySetting vgs:vpnGateWaysSetting){
					HiveAp apCvg = QueryUtil.findBoById(HiveAp.class, vgs.getApId()); 
					if(null != apCvg){
						BeCliEvent cliEventCvg = new BeCliEvent();
						cliEventCvg.setAp(apCvg);
						String cli = AhCliFactory.getOTPRevokeCvgCli(apCvg.getMacAddress(), deleted.getClientName());
						cliEventCvg.setClis(new String[] { cli });
						cliEventCvg.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
				
						try {
							cliEventCvg.buildPacket();
						} catch (BeCommunicationEncodeException e) {
							DebugUtil.performanceDebugError("In revokeForCvg failed to build packet", e);
						}
						BeCommunicationEvent resultEvent = HmBeCommunicationUtil.sendSyncRequest(cliEventCvg);
						if (resultEvent == null) {
							DebugUtil.performanceDebugWarn("In revokeForCvg failed to get response of sent cli, cli:" + cli);
						}
					}
				}
			}
		}
	}
	
	/**
	 * filter fields start
	 */
	private String password;
	private String username;
	private String email;
	private String dateSent;
	private String dateActivated;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDateSent() {
		return dateSent;
	}

	public void setDateSent(String dateSent) {
		this.dateSent = dateSent;
	}

	public String getDateActivated() {
		return dateActivated;
	}

	public void setDateActivated(String dateActivated) {
		this.dateActivated = dateActivated;
	}
	/**
	 * filter fields end
	 */
	private void saveFilter() {
		String searchSQL = "";
		List<Object> lstCondition = new ArrayList<Object>();

		if (!StringUtils.isBlank(password)) {
			searchSQL = "lower(oneTimePassword) like :s" + (lstCondition.size() + 1);
			lstCondition.add("%" + password.toLowerCase() + "%");
		}
		
		if (!StringUtils.isBlank(username)) {
			searchSQL = "lower(userName) like :s" + (lstCondition.size() + 1);
			lstCondition.add("%" + username.toLowerCase() + "%");
		}
		
		if (!StringUtils.isBlank(email)) {
			searchSQL = "lower(emailAddress) like :s" + (lstCondition.size() + 1);
			lstCondition.add("%" + email.toLowerCase() + "%");
		}
		
		if(!StringUtils.isBlank(dateSent)){
			searchSQL = searchSQL + ((lstCondition.size() == 0) ? "" : " AND ") 
					+ "dateSentStamp > :s" + (lstCondition.size() + 1);
			long dateStart = this.getFormattedTime(dateSent+"/00:00:00");
			lstCondition.add(dateStart);
			searchSQL = searchSQL + ((lstCondition.size() == 0) ? "" : " AND ") 
				+ "dateSentStamp < :s" + (lstCondition.size() + 1);
			lstCondition.add(dateStart + 24*3600000);
		}
		
		if(!StringUtils.isBlank(dateActivated)){
			searchSQL = searchSQL + ((lstCondition.size() == 0) ? "" : " AND ") 
					+ "dateActivateStamp > :s" + (lstCondition.size() + 1);
			long dateStart = this.getFormattedTime(dateActivated+"/00:00:00");
			lstCondition.add(dateStart);
			searchSQL = searchSQL + ((lstCondition.size() == 0) ? "" : " AND ") 
				+ "dateActivateStamp < :s" + (lstCondition.size() + 1);
			lstCondition.add(dateStart + 24*3600000);
		}

		if (lstCondition.size() == 0) {
			filterParams = null;
		} else {
			filterParams = new FilterParams(searchSQL, lstCondition.toArray());
		}
		
		setSessionFiltering();
	}
	
	private final String OTP_CURRENT_SESSION_FILTER = "otp_current_filter";
	private void setSessionFilter(FilterParams filterParamsArg) {
		if (filterParamsArg != null) {
			MgrUtil.setSessionAttribute(OTP_CURRENT_SESSION_FILTER, filterParams);
		} else {
			if (MgrUtil.getSessionAttribute(OTP_CURRENT_SESSION_FILTER) != null) {
				MgrUtil.removeSessionAttribute(OTP_CURRENT_SESSION_FILTER);
			}
		}
	}
	
	private FilterParams getSessionFilter() {
		return (FilterParams)MgrUtil.getSessionAttribute(OTP_CURRENT_SESSION_FILTER);
	}
	
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss");
	
	private long getFormattedTime(String timeStr){
		if(StringUtils.isBlank(timeStr)){
			return -1;
		}
		try{
			return dFormat.parse(timeStr).getTime();
		}catch(ParseException ex){
			log.info("search", "Error start date format: " + timeStr);
			return -1;
		}
	}
	
	private String prepareOTPBoList() throws Exception {
		setSessionFilter(null);
		return prepareBoList();
	}
	
	public boolean getDisableAssign(){
		if(null != getDataSource() &&(null != getDataSource().getDateSent() || null != getDataSource().getDateActivate())){
			return true;
		}
		return false;
	}
	
	/**
	 * generate OTP
	 */
	private String numberOfOtp = "1";
	private String desOfOtp;
	
    private void prepareForOneTimePasswords(){
    	if(null != getDomain()){
    		vhmId = getDomain().getVhmID();
    	}
	}

	public void generateOTPList(){
		try {
			List<OneTimePassword> otpList = new ArrayList<OneTimePassword>();
			for(int i=0;i<Integer.parseInt(numberOfOtp);i++){
				OneTimePassword otp = new OneTimePassword();
				otp.setOneTimePassword(generateOneTimePassword());
				otp.setDescription(desOfOtp);
				otp.setOwner(getDomain());
				otpList.add(otp);
			}
			QueryUtil.bulkCreateBos(otpList);
		} catch (Exception e) {
			log.error("generateOTPList", "error.", e);
		}
	}
	
    public static String getCharacterAndNumber(int length)  
    {  
        String val = "";  
        Random random = new Random();  
        for(int i = 0; i < length; i++)  
        {  
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            if("char".equalsIgnoreCase(charOrNum))
            {  
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; 
                val += (char) (choice + random.nextInt(26));  
            }  
            else if("num".equalsIgnoreCase(charOrNum))
            {  
                val += String.valueOf(random.nextInt(10));  
            }  
        }  
        return val;  
    }  
    
    public static String keyShaOtp(StringBuilder bufTemp){
    	byte[] byteRet;
 		byte[] sha1Out;
 		MessageDigest digest;
 		byteRet = new byte[12];
 		
 		try {
 			sha1Out = new byte[20];
 			for (int m = 0; m < 20; m++) {
 				sha1Out[m] = 0;
 			}
 			digest = MessageDigest.getInstance("MD5");
 			digest.update(bufTemp.toString().getBytes());
 			digest.update(sha1Out);
 			sha1Out = digest.digest();
 			System.arraycopy(sha1Out, 0, byteRet, 0,12);

 		} catch (NoSuchAlgorithmException e) {
 			log.error("keyShaOtp", "error.", e);
 		}
 		return AhDecoder.bytes2hex(byteRet);
    }
	
    public static String generateOneTimePassword(){
	   StringBuilder sBuilder= new StringBuilder();
	   sBuilder.append(getCharacterAndNumber(32));
	   if(null !=HM_License.getInstance().get_system_id() &&!"".equals(HM_License.getInstance().get_system_id())){
		   sBuilder.append(HM_License.getInstance().get_system_id());
	   }else{
		   sBuilder.append(vhmId);
	   }
	   
	   return keyShaOtp(sBuilder);
    }

	public String getNumberOfOtp() {
		return numberOfOtp;
	}
	
	public void setNumberOfOtp(String numberOfOtp) {
		this.numberOfOtp = numberOfOtp;
	}
	
	
	public String getDesOfOtp() {
		return desOfOtp;
	}
	
	public void setDesOfOtp(String desOfOtp) {
		this.desOfOtp = desOfOtp;
	}
	
	static class ImplQueryBo implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
			 if (bo instanceof HiveAp) {
					HiveAp hiveAp = (HiveAp) bo;
					if (null != hiveAp.getConfigTemplate() ) {
						hiveAp.getConfigTemplate().getId();
						
						if (null != hiveAp.getConfigTemplate().getVpnService()){
							hiveAp.getConfigTemplate().getVpnService().getId();
							
							if(null != hiveAp.getConfigTemplate().getVpnService().getVpnCredentials()){
								 hiveAp.getConfigTemplate().getVpnService().getVpnCredentials().size();
							}
							if(null != hiveAp.getConfigTemplate().getVpnService().getVpnGateWaysSetting()){
								hiveAp.getConfigTemplate().getVpnService().getVpnGateWaysSetting().size();
							}
						}
					}
			}
			return null;
		}
	}

}
