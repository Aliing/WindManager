package com.ah.ui.actions.tools;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;

import org.json.JSONObject;

import com.ah.be.app.AhAppContainer;
import com.ah.be.config.BeConfigModule.ConfigType;
import com.ah.be.config.util.AhConfigUtil;
import com.ah.be.os.FileManager;
import com.ah.bo.HmBo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.Tracer;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;

public class RunningConfigParseAction extends ActionSupport implements
		Preparable, QueryBo {

	private static final long serialVersionUID = 1L;
	
	private static final Tracer log = new Tracer(RunningConfigParseAction.class.getSimpleName());

	// these column receive from HTTPS request.
	private String operation;
	private Long deviceId;
	private String config;
	private String macAddress;

	private String cliCfgPath;
	private String runXmlCfgPath;
	private String fileName;
	private String cParseIgnoreCfgPath;

//	private boolean success = false;
	
	protected JSONObject jsonObject = null;

	public static final String PARSE_RUNNING_CONFIG = ConfigType.AP_FULL
			.toString();
	public static final String PARSE_USER_CONFIG = ConfigType.USER_FULL
			.toString();

	@Override
	public String execute() throws Exception {

		boolean success = false;
		String message = null;
		
		try {
			prepareCliParse();
			if (PARSE_RUNNING_CONFIG.equals(operation)) {
				success = AhAppContainer.getBeConfigModule().parseCli(
						new HiveAp(), ConfigType.AP_FULL, cliCfgPath,
						runXmlCfgPath, cParseIgnoreCfgPath);
			} else if (PARSE_USER_CONFIG.equals(operation)) {
				success = AhAppContainer.getBeConfigModule().parseCli(
						new HiveAp(), ConfigType.USER_FULL, cliCfgPath,
						runXmlCfgPath, cParseIgnoreCfgPath);
			}
		} catch (Exception ex) {
			log.error(ex);
			message = ex.getMessage();
		}
		
		if(success){
			return "download";
		}else{
			jsonObject = new JSONObject();
			jsonObject.put("t", false);
			jsonObject.put("m", message);
			return "json";
		}
	}

	@Override
	public void prepare() throws Exception {
		// TODO Auto-generated method stub

	}

	public void prepareCliParse() throws Exception {
		if (deviceId != null) {
			HiveAp hiveAp = QueryUtil.findBoById(HiveAp.class, deviceId, this);
			if (PARSE_RUNNING_CONFIG.equals(operation)) {
				config = AhAppContainer.getBeConfigModule().fetchRunningConfig(
						hiveAp);
			} else if (PARSE_USER_CONFIG.equals(operation)) {
				config = AhAppContainer.getBeConfigModule().fetchUserConfig(
						hiveAp);
			}
			macAddress = hiveAp.getMacAddress();
		}

		String domainName = "home";
		if (PARSE_RUNNING_CONFIG.equals(operation)) {
			cliCfgPath = AhConfigUtil.getFullRunConfigPath(domainName,
					macAddress);
			runXmlCfgPath = AhConfigUtil.getFullRunXmlConfigPath(domainName,
					macAddress);
			fileName = AhConfigUtil.getFullRunXmlConfigName(macAddress);
			cParseIgnoreCfgPath = AhConfigUtil.getFullCParseIgnoreConfigPath(domainName, macAddress);
		} else if (PARSE_USER_CONFIG.equals(operation)) {
			cliCfgPath = AhConfigUtil.getUserRunConfigPath(domainName,
					macAddress);
			runXmlCfgPath = AhConfigUtil.getUserRunXmlConfigPath(domainName,
					macAddress);
			fileName = AhConfigUtil.getUserRunXmlConfigName(macAddress);
			cParseIgnoreCfgPath = AhConfigUtil.getUserCParseIgnoreConfigPath(domainName, macAddress);
		}

		// Keep config fetched into a file.
		FileManager.getInstance().createFile(config, cliCfgPath);

	}
	
	public String getJSONString() {
		if(jsonObject != null){
			return jsonObject.toString();
		}else{
			return null;
		}
	}

	public InputStream getInputStream() throws Exception {
		return new FileInputStream(runXmlCfgPath);
	}

	public String getLocalFileName() {
		return fileName;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo == null) {
			return null;
		}
		if (bo instanceof HiveAp) {
			HiveAp hiveAp = (HiveAp) bo;
			if (hiveAp.getOwner() != null)
				hiveAp.getOwner().getId();
		}

		return null;
	}

}
