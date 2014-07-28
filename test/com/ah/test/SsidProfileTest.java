package com.ah.test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.DosParams;
import com.ah.bo.network.DosPrevention;
import com.ah.bo.network.DosParams.FrameType;
import com.ah.bo.network.DosParams.ScreeningType;
import com.ah.bo.network.DosPrevention.DosType;
import com.ah.bo.wlan.Scheduler;
import com.ah.bo.wlan.SsidProfile;
import com.ah.bo.wlan.SsidSecurity;
import com.ah.test.util.HmTest;
import com.ah.util.Tracer;

public class SsidProfileTest extends HmTest {
	private static final Tracer log = new Tracer(SsidProfileTest.class
			.getSimpleName());

	private Set<Scheduler> set_schduler = null;

	private Set<SsidProfile> set_ssid = null;

	private final String operation;

	public SsidProfileTest() {
		this.operation = "";
	}

	public SsidProfileTest(String operation) {
		super();
		this.operation = operation;
	}

	public void run() {
		String user = "u" + getId();
		try {
			if ("testRemoveSsid".equals(operation)) {
				BoMgmt.removeAllBos(SsidProfile.class, null, null, null, null);
				BoMgmt.removeAllBos(Scheduler.class, null, null, null, null);
				BoMgmt
						.removeAllBos(DosPrevention.class, null, null, null,
								null);
			} else if ("testCreateSsid".equals(operation)) {

				createDosPrevention(user);
				createScheduler(user);
				createSsidProfile(user);
				createSsidSecurity(user);
			}
		} catch (Exception ex) {
			log.error(this.getClass().getName() + ".run()", ex.getMessage());
		}
	}

	private void createSsidSecurity(String user) throws Exception {
		// SsidSecurity sec=new SsidSecurity();
		// sec.set
	}

	private void createScheduler(String user) throws Exception {
		set_schduler = new HashSet<Scheduler>();
		Scheduler scheduler = new Scheduler();
		scheduler.setDescription("test");
		scheduler.setBeginTime("10:43");
		scheduler.setEndTime("23:59");
		scheduler.setSchedulerName(user + "_p1");
		scheduler.setType(0);
		set_schduler.add(scheduler);
		QueryUtil.createBo(scheduler);
	}

	private void createSsidProfile(String user) throws Exception {
		SsidProfile profile = new SsidProfile();
		profile.setAuthentication(0);
		profile.setBroadcase(true);
		profile.setComment(user + "_p1");
		profile.setDtimSetting(8);
		profile.setEncryption(0);
		profile.setFragThreshold(560);
		profile.setHide(false);
		profile.setMacAuthEnabled(true);
		profile.setPreauthenticationEnabled(true);
		profile.setRtsThreshold(50);
		profile.setSchedulers(set_schduler);
		profile.setSsidName(user + "_p1");

		SsidSecurity sec = new SsidSecurity();
		sec.setFirstKeyValue("111111111111111111");
		sec.setKeyType(0);
		sec.setDefaultKeyIndex(1);
		sec.setFourthValue("111111111111111111");
		sec.setProactiveEnabled(true);
		sec.setRekeyPeriod(22);
		sec.setRekeyPeriodGMK(2222);
		// sec.setSecName(user + "_p1");
		sec.setSecondKeyValue("111111111111111111");
		// sec.setSecType(1);
		// sec.setSelected(true);
		// sec.setSsidProfile(set_ssid);
		sec.setStrict(true);
		sec.setThirdKeyValue("111111111111111111");
		// QueryUtil.createBo(sec);
		profile.setSsidSecurity(sec);
		QueryUtil.createBo(profile);

		if (set_ssid == null)
			set_ssid = new HashSet<SsidProfile>();
		set_ssid.add(profile);

	}

	public void createDosPrevention(String user) throws Exception {
		// MAC DoS
		DosPrevention dosPrevention = new DosPrevention();
		dosPrevention.setDosPreventionName(user + "_p1");
		dosPrevention.setDosType(DosType.MAC);

		DosParams dosParams = new DosParams();
		dosParams.setFrameType(FrameType.DEAUTH);
		dosParams.setAlarmThreshold(55);

		Map dosParamsMap = dosPrevention.getDosParamsMap();
		dosParamsMap.put(dosParams.getkey(), dosParams);
		QueryUtil.createBo(dosPrevention);

		// IP DoS
		dosPrevention = new DosPrevention();
		dosPrevention.setDosPreventionName(user + "_p2");
		dosPrevention.setDosType(DosType.IP);

		dosParams = new DosParams();
		dosParams.setScreeningType(ScreeningType.IP_SPOOF);
		dosParams.setAlarmThreshold(33);

		dosParamsMap = dosPrevention.getDosParamsMap();
		dosParamsMap.put(dosParams.getkey(), dosParams);
		QueryUtil.createBo(dosPrevention);
	}

	public static void main(String[] args) {
		SsidProfileTest test = new SsidProfileTest();
		test.run();
	}
}
