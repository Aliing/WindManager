/**
 *@filename		BeDbModuleImpl.java
 *@version
 *@author		Steven
 *@createtime	2007-9-3 01:51:41 AM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.db;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.ah.be.app.BaseModule;
import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.db.configuration.ConfigAuditScheduler;
import com.ah.be.db.configuration.ConfigurationProcessor;
import com.ah.be.db.discovery.event.impl.AhDiscoveryMgmtImpl;
import com.ah.be.db.reference.TableReferencesContainer;
import com.ah.be.event.AhEventMgmt;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.dashboard.AhDashboard;
import com.ah.bo.dashboard.AhDashboardAppAp;
import com.ah.bo.dashboard.AhDashboardLayout;
import com.ah.bo.dashboard.AhDashboardWidget;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public class BeDbModuleImpl extends BaseModule implements BeDbModule {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(BeDbModuleImpl.class.getSimpleName());

	private AhEventMgmt<BeBaseEvent> discoveryMgmt;

	private ConfigurationProcessor cp;

	private ConfigAuditScheduler scheduler;
	
	private TableReferencesContainer refContainer;

	public BeDbModuleImpl() {
		setModuleId(BaseModule.ModuleID_DB);
		setModuleName("BeDbModule");
	}

	@Override
	public boolean init() {
		resetCapwapStatus();
		removeAllIdps();
		
		//add by nianrong
		removeTempDashboard();
		
		

		discoveryMgmt = new AhDiscoveryMgmtImpl(1);
		cp = new ConfigurationProcessor();
		scheduler = new ConfigAuditScheduler();
		
		refContainer = new TableReferencesContainer();

		return true;
	}

	@Override
	public boolean run() {
		if (discoveryMgmt != null && !discoveryMgmt.isStarted()) {
			discoveryMgmt.start();
		}

		if (null != cp) {
			cp.start();
		}

		if (scheduler != null) {
			scheduler.start();
		}
		
		if (null != refContainer) {
		    refContainer.start();
		}

		return true;
	}

	@Override
	public void eventDispatched(BeBaseEvent event) {
		if (event.isShutdownRequestEvent()) {
			shutdown();

			return;
		}

		switch (event.getEventType()) {
		case BeEventConst.COMMUNICATIONEVENTTYPE:
			switch (((BeCommunicationEvent) event).getMsgType()) {
			case BeCommunicationConstant.MESSAGETYPE_APCONNECT:
			case BeCommunicationConstant.MESSAGETYPE_APDISCONNECT:
				discoveryMgmt.add(event);
				break;
			default:
				break;
			}
			break;
		case BeEventConst.AH_CONFIGURATION_CHANGE_EVENT:
			cp.addConfigurationEvent(event);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean shutdown() {
		if (discoveryMgmt != null) {
			discoveryMgmt.stop();
		}

		if (null != cp) {
			cp.stop();
		}

		if (null != scheduler) {
			scheduler.stop();
		}

		if (null != refContainer) {
		    refContainer.stop();
		}
		
		return true;
	}
	
	@Override
	public Map<String, String> getTableReferences(String tableName) {
	    if(null == refContainer) {
	        return null;
	    } else {
	        return refContainer.getTableReferences(tableName);
	    }
	}

	@Override
	public AhEventMgmt<BeBaseEvent> getDiscoveryMgmt() {
		return discoveryMgmt;
	}

	private void resetCapwapStatus() {
		try {
			log.info("resetCapwapStatus", "Resetting CAPWAP status for overall VHMs' HiveAPs.");
			BoMgmt.getHiveApMgmt().resetCapwapStatus(null);
			log.info("resetCapwapStatus",
					"The CAPWAP status for overall VHMs' HiveAPs were set to 'disconnected'.");
		} catch (Exception e) {
			String errorMsg = MgrUtil.getUserMessage("hm.system.log.be.db.module.reset.capwap.fail",NmsUtil.getOEMCustomer().getAccessPonitName());
			log.error("resetCapwapStatus", errorMsg, e);
			HmBeLogUtil
					.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_HIVEAPS, errorMsg);
		}
	}

	private void removeAllIdps() {
		try {
			QueryUtil.bulkRemoveBos(Idp.class, null);
		} catch (Exception e) {
			DebugUtil.topoDebugError("Remove All Idp object error.", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void removeTempDashboard(){
		try {
			List<Long> ids = (List<Long>)QueryUtil.executeQuery("select id from "  + AhDashboard.class.getSimpleName()
					, null, new FilterParams("daType!=:s1 and daType!=:s2 and defaultFlag=:s3",
							new Object[]{AhDashboard.DASHBOARD_TYPE_DASH,
							AhDashboard.DASHBOARD_TYPE_REPORT, false}));
			if(ids.isEmpty()) {
				return;
			}
			List<Long> layoutIds = (List<Long>)QueryUtil.executeQuery("select id from "  + AhDashboardLayout.class.getSimpleName()
					, null, new FilterParams("dashboard.id",ids));
			if(!layoutIds.isEmpty()) {
				QueryUtil.removeBos(AhDashboardWidget.class, new FilterParams("daLayout.id",layoutIds));
				QueryUtil.removeBos(AhDashboardLayout.class, layoutIds);
			}
			QueryUtil.removeBos(AhDashboard.class, ids);
			
			// remove application ap
			Set<String> removeIntervalList = new HashSet<String>();
			List<String> appAplist = (List<String>)QueryUtil.executeQuery("select distinct apMac from " 
					+ AhDashboardAppAp.class.getSimpleName(), null, new FilterParams("dashId",ids));
			removeIntervalList.addAll(appAplist);
			List<?> remlist = QueryUtil.executeQuery("select distinct apMac from " + AhDashboardAppAp.class.getSimpleName(), null, 
					new FilterParams("dashId not in (:s1)",new Object[]{ids}));
			removeIntervalList.removeAll(remlist);
			
			QueryUtil.bulkRemoveBos(AhDashboardAppAp.class, new FilterParams("dashId",ids));

		} catch (Exception e) {
			DebugUtil.topoDebugError("Remove Temp Dashboard error.", e);
		}
	}

	private static final String	hibernateCfgFile			= System.getenv("HM_ROOT")
																+ File.separator
																+ "WEB-INF"
																+ File.separator
																+ "classes"
																+ File.separator
																+ "hibernate.cfg.xml";

	private static final String	hmConfigFile				= System.getenv("HM_ROOT")
																+ File.separator
																+ "WEB-INF"
																+ File.separator
																+ "classes"
																+ File.separator
																+ "resources"
																+ File.separator
																+ "hmConfig.properties";

	private static final String	capwapConfigFile			= "/HiveManager"
																+ File.separator
																+ "capwap"
																+ File.separator
																+ "capwap.conf";

	static boolean isValidIp(String arg_IP) {
		return Pattern.matches(
				"((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)", arg_IP);
	}

	static boolean isValidHostName(String hostname) {
		if(hostname.length() > 255)
			return false;
		String regex = "^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])" +
				"(\\.([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9]))*$";
		return Pattern.matches(regex, hostname);
	}

	static boolean isValidPort(String port) {
		boolean isInt = Pattern.matches("\\d+", port);
		if (!isInt) {
			return false;
		}

		int intValue = Integer.valueOf(port);

		return intValue > 0 && intValue <= 65535;
	}

	static boolean isValidDbName(String db) {
		String regex = "[a-zA-Z_][a-zA-Z0-9_\\-]{0,62}";
		return Pattern.matches(regex, db);
	}

	static boolean isValidDbUserName(String name) {
		String regex = "[a-zA-Z0-9_]{1,32}";
		return Pattern.matches(regex, name);
	}

	static boolean isValidDbPassword(String passwd) {
		String regex = "\\S{1,32}";
		return Pattern.matches(regex, passwd);
	}

	public static int changeDBSettings(String host, int port, String db, String name, String passwd) throws IllegalArgumentException,InterruptedException,IOException {
		// check arguments
		if (host == null || (!isValidIp(host) && !isValidHostName(host))) {
			throw new IllegalArgumentException("Input '" + host
					+ "' is not a valid hostname or ip.");
		}

		if (!isValidPort(String.valueOf(port))) {
			throw new IllegalArgumentException("Input '" + port
						+ "' is not a valid port.");
		}

		if (db == null || !isValidDbName(db)) {
			throw new IllegalArgumentException("Input '" + db
					+ "' is not a valid database name.");
		}

		if (name == null || !isValidDbUserName(name)) {
			throw new IllegalArgumentException("Input '" + name
						+ "' is not a valid user name.");
		}

		if (passwd == null || !isValidDbPassword(passwd)) {
			throw new IllegalArgumentException("Input '" + passwd
					+ "' is not a valid password.");
		}

		String jdbc = "jdbc:postgresql:\\/\\/" + host + ":" + port + "\\/" + db;
		String[] dbXml = { "bash", "-c", "sed -i 's/jdbc:postgresql:\\/\\/.*/" + jdbc + "/' " + hibernateCfgFile };
		Runtime.getRuntime().exec(dbXml).waitFor();

		String[] username = { "bash", "-c", "sed -i 's/hm.connection.username=.*/hm.connection.username=" + name + "/' " + hmConfigFile };
		Runtime.getRuntime().exec(username).waitFor();

		String[] password = { "bash", "-c", "sed -i 's/hm.connection.password=.*/hm.connection.password=" + passwd + "/' " + hmConfigFile };
		Runtime.getRuntime().exec(password).waitFor();

		// change capwap settings
		String[] hostExec = { "bash", "-c", "sed -i 's/DB_HOST=.*/DB_HOST=" + host + "/' " + capwapConfigFile };
		Runtime.getRuntime().exec(hostExec).waitFor();
		String[] nameExec = { "bash", "-c", "sed -i 's/DB_NAME=.*/DB_NAME=" + db + "/' " + capwapConfigFile };
		Runtime.getRuntime().exec(nameExec).waitFor();
		String[] passwdExec = { "bash", "-c", "sed -i 's/DB_PASSWORD=.*/DB_PASSWORD=" + passwd + "/' " + capwapConfigFile };
		Runtime.getRuntime().exec(passwdExec).waitFor();
		String[] unameExec = { "bash", "-c", "sed -i 's/DB_USERNAME=.*/DB_USERNAME=" + name + "/' " + capwapConfigFile };
		Runtime.getRuntime().exec(unameExec).waitFor();
		String[] portExec = { "bash", "-c", "sed -i 's/DB_PORT=.*/DB_PORT=" + port + "/' " + capwapConfigFile };
		Runtime.getRuntime().exec(portExec).waitFor();

		return 0;
	}

}