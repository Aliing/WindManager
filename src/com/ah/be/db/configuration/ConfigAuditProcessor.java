package com.ah.be.db.configuration;

import java.io.FileNotFoundException;
import java.util.Date;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.config.AhConfigDisconnectException;
import com.ah.be.config.AhConfigParsedException;
import com.ah.be.config.AhConfigRetrievedException;
import com.ah.be.config.BeConfigModule.ConfigType;
import com.ah.be.config.cli.generate.CLIGenerateException;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.event.AhConfigGeneratedEvent;
import com.ah.be.config.event.AhConfigGeneratedEvent.ConfigGenResultType;
import com.ah.be.config.event.AhConfigGeneratedEvent.UseMode;
import com.ah.be.db.configuration.ConfigurationProcessor.ConfigurationType;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class ConfigAuditProcessor {

	private static final Tracer log = new Tracer(ConfigAuditProcessor.class
			.getSimpleName());

	public static synchronized void dealConfigAuditResult(AhConfigGeneratedEvent event) {
		try {
			ConfigGenResultType resultType = event.getConfigGenResultType();
			HiveAp hiveAp = event.getHiveAp();
			ConfigurationType type;
			if (event.getUseMode() == UseMode.RADIUS_USER_AUDIT) {
				type = ConfigurationType.UserDatabase;
			} else if (event.getUseMode() == UseMode.FULL_AUDIT) {
				type = ConfigurationType.Configuration;
			} else {
				log.error("dealConfigAuditResult", "Unknown type:"
						+ event.getUseMode()
						+ ", cannot do configuration audit.");
				return;
			}

			if (ConfigGenResultType.SUCC.equals(resultType)) {
				// difference exist;
				// update both for user mismatch or configuration mismatch
				BoMgmt.getHiveApMgmt().updateConfigurationIndication(hiveAp,
						new Date(), false, type);
			} else if (ConfigGenResultType.NO_DIFF.equals(resultType)) {
				// no difference exist;
				// update both for user match or configuration match
				BoMgmt.getHiveApMgmt().updateConfigurationIndication(hiveAp,
						new Date(), true, type);
			} else {
				// operation failed;
				log.error("dealConfigAuditResult",
						"config Audit failed. match/mismatch result ["
								+ resultType + "] for HiveAp:"
								+ hiveAp.getHostName());
				// System log
				String errMsg = event.getErrorMsg();
				if (null != errMsg && !"".equals(errMsg.trim())) {
					if (type == ConfigurationType.UserDatabase) {
						HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
								HmSystemLog.FEATURE_HIVEAPS,
								MgrUtil.getUserMessage("hm.system.log.config.audit.processor.db.audit.user.fail",new String[]{NmsUtil.getOEMCustomer().getAccessPonitName(),hiveAp.getMacAddress()}) + errMsg);
					} else {
						HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
								HmSystemLog.FEATURE_HIVEAPS,
								MgrUtil.getUserMessage("hm.system.log.config.audit.processor.configuration.audit.fail",new String[]{NmsUtil.getOEMCustomer().getAccessPonitName(),hiveAp.getMacAddress()}) + errMsg);
					}
				}
			}
		} catch (Exception e) {
			log
					.error(
							"dealConfigAuditResult",
							"deal with the delta configuration generated result failed.",
							e);
		}
	}

	/**
	 * view of the current configuration of HiveAP, with full/delta
	 * previous/delta running type. the delta running result will effect the
	 * configuration indication
	 * 
	 * @param type
	 *            -
	 * @param hiveAp
	 *            -
	 * @return -
	 */
	public static synchronized String view(ConfigType type, HiveAp hiveAp) {
		log.info("view", "View Configuration with type:" + type + " on HiveAP:"
				+ hiveAp.getMacAddress());
		String message = "";
		String result;
		try {
			result = AhAppContainer.getBeConfigModule()
					.viewConfig(hiveAp, type);
			log.info("view", "view Configs result returned.");
			// no difference after compare delta configuration
			if (null == result || "".equals(result.trim())) {
				if (type == ConfigType.AP_DELTA || type == ConfigType.AP_AUDIT) {
					message = "("
							+ MgrUtil
									.getUserMessage("error.hiveAp.update.script.generate.compare")
							+ ")";
				} else if (type == ConfigType.USER_DELTA
						|| type == ConfigType.USER_AUDIT) {
					message = "("
							+ MgrUtil
									.getUserMessage("error.hiveAp.update.psk.generate.compare")
							+ ")";
				} else {
					log.error("view",
							"No configuration details with ConfigType:" + type);
				}
			} else {
				message = result;
			}

			if (type == ConfigType.AP_AUDIT) {
				// update configuration indication
				BoMgmt.getHiveApMgmt().updateConfigurationIndication(hiveAp,
						new Date(), null == result || "".equals(result.trim()),
						ConfigurationType.Configuration);
			} else if (type == ConfigType.USER_AUDIT) {
				// update user database indication
				BoMgmt.getHiveApMgmt().updateConfigurationIndication(hiveAp,
						new Date(), null == result || "".equals(result.trim()),
						ConfigurationType.UserDatabase);
			}
		} catch (Exception e) {
			log.error("view", "Failed to view HiveAP:" + hiveAp.getMacAddress()
					+ ", viewType:" + type, e);
			message = getDetailInfo(e, type, hiveAp.getLastCfgTime() == 0);
		}
		return message;
	}

	public static String getDetailInfo(Exception e, ConfigType type,
			boolean isFirstTime) {
		String message = "";
		Throwable t = null;
		if (null != e && e instanceof FileNotFoundException
				|| e instanceof AhConfigRetrievedException
				|| e instanceof CreateXMLException
				|| e instanceof AhConfigParsedException
				|| e instanceof AhConfigDisconnectException) {
			message = e.getMessage() == null ? "Unknown Error while generate configuration file."
					: e.getMessage();
		} else if((t = getCLIGenerateException(e)) != null ){
			message = t.getMessage();
		} else if (null != e && null != e.getCause()
				&& (e.getCause() instanceof CreateXMLException || 
						e.getCause() instanceof CLIGenerateException)) {
			message = e.getCause().getMessage() == null ? "Unknown Error while generate configuration file."
					: e.getCause().getMessage();
		} else {
			if (type == ConfigType.AP_FULL) {
				message = MgrUtil
						.getUserMessage("error.hiveAp.update.script.generate.full");
			} else if (type == ConfigType.AP_DELTA
					|| type == ConfigType.AP_AUDIT) {
				message = MgrUtil
						.getUserMessage("error.hiveAp.update.script.generate.delta");
			} else if (type == ConfigType.USER_FULL) {
				message = MgrUtil
						.getUserMessage("error.hiveAp.update.psk.generate.full");
			} else if (type == ConfigType.USER_DELTA
					|| type == ConfigType.USER_AUDIT) {
				message = MgrUtil
						.getUserMessage("error.hiveAp.update.psk.generate.delta");
			} else if (type == ConfigType.BOOTSTRAP) {
				message = NmsUtil
						.getUserMessage("error.hiveAp.update.bootstrap.generate");
			}
		}
		if (isFirstTime) {
			message += "\n"
					+ NmsUtil
							.getUserMessage("error.be.config.create.generateXml.reason");
		}
		return message;
	}
	
	private static Throwable getCLIGenerateException(Exception e){
		Throwable tCause = e;
		while(tCause != null){
			if(tCause instanceof CLIGenerateException){
				return tCause;
			}else{
				tCause = tCause.getCause();
			}
		}
		return null;
	}

}