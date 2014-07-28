package com.ah.ha.dns;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.os.NetConfigImplInterface;
import com.ah.bo.admin.HASettings;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ha.HAStatus;
import com.ah.ha.HAUtil;
import com.ah.util.Tracer;

public class VhnSynchronizer implements Runnable {

	private static final Tracer log = new Tracer(VhnSynchronizer.class.getSimpleName());

	private static VhnSynchronizer instance;

	private ScheduledExecutorService scheduler;

	private VhnSynchronizer() {
	}

	public synchronized static VhnSynchronizer getInstance() {
		if (instance == null) {
			instance = new VhnSynchronizer();
		}

		return instance;
	}

	public synchronized void start() {
		log.info("start", "Starting VHN synchronizer...");

		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleWithFixedDelay(this, 0, 60, TimeUnit.SECONDS);
			log.info("start", "VHN synchronizer started.");
		} else {
			log.info("start", "VHN synchronizer has already been started.");
		}
	}

	public synchronized void stop() {
		log.info("stop", "Stopping VHN synchronizer...");

		if (scheduler != null && !scheduler.isShutdown()) {
			try {
				scheduler.shutdown();
				log.info("stop", "VHN synchronizer stopped.");
			} catch (Exception e) {
				log.error("stop", "Stop VHN synchronizer error.", e);
			}
		} else {
			log.info("stop", "VHN synchronizer has already been stopped.");
		}
	}

	@Override
	public void run() {
		try {
			HAStatus curStatus = HAUtil.getHAMonitor().getCurrentStatus();
			if (HAStatus.STATUS_HA_MASTER == curStatus.getStatus()) {
				List<HASettings> list = QueryUtil.executeQuery(HASettings.class, null, null);
				if (!list.isEmpty()) {
					// fetch active hostname
					NetConfigImplInterface networkService = AhAppContainer.getBeOsLayerModule().getNetworkService();
					String hostname = networkService.getHostName();
					if (!hostname.contains(NmsUtil.getOEMCustomer().getDnsSuffix()) &&
							!hostname.contains(NmsUtil.getOEMCustomer().getDnsSuffix() + ".")) {
						hostname += ("." + NmsUtil.getOEMCustomer().getDnsSuffix());
					}

					// fetch current vhn settings
					HMServicesSettings hmService = QueryUtil.findBoByAttribute(HMServicesSettings.class,
							"owner.domainName", HmDomain.HOME_DOMAIN);
					String vhn = hmService.getVirtualHostName();

					log.debug("run", "Active node hostname is " + hostname + " and VHN is " + vhn);
					if (hostname == null || hostname.length() == 0) return;
					if (vhn == null || vhn.length() == 0) return;

					// fetch current cname for vhn
					String cmdCname = HmBeOsUtil.getHAScriptsPath() + "dns_change.py get_dns_record " + vhn;
					String cname = BeAdminCentOSTools.getOutStreamExecCmd(cmdCname);
					String suffix = NmsUtil.getOEMCustomer().getDnsSuffix();

					if (!"".equals(cname) && (cname.endsWith(suffix) || cname.endsWith(suffix + "."))) {
						if (!hostname.equalsIgnoreCase(cname) && !(hostname + ".").equalsIgnoreCase(cname)) {
							String cmdChange = HmBeOsUtil.getHAScriptsPath() + "dns_change.py change_dns " + vhn + " " + hostname;
							String result = BeAdminCentOSTools.getOutStreamExecCmd(cmdChange);
							try {
								//Integer.parseInt(result);
								log.info("run", result);
							} catch (Exception e) {
								log.error("run", "Change CNAME for VHN " + vhn + " to "+hostname+" failed! Message:" + cmdChange, e);
							}
						}
					} else {
						log.error("run", "CNAME for VHN " + vhn + "invalid! Message:" + cname);
					}
				} else {
					log.error("run", "HA setting did not exist.");
				}
			}
		} catch (Exception e) {
			log.error("run", "VhnSynchronizer.run() catch exception.", e);
		}
	}

}