package com.ah.test;

/*
 * @author Chris Scheers
 */

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.ah.bo.HmBo;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.HiveProfilesMgmt;
import com.ah.bo.mgmt.MapMgmt;
import com.ah.bo.mgmt.Paging;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.impl.HiveProfilesMgmtImpl;
import com.ah.bo.mgmt.impl.PagingImpl;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.network.MacFilter;
import com.ah.test.util.HmTest;
import com.ah.util.HibernateUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.xml.hiveprofile.XmlHiveProfile;
import com.ah.xml.hiveprofile.XmlHiveProfiles;

public class HiveProfilesTest extends HmTest {
	private static final Tracer log = new Tracer(HiveProfilesTest.class
			.getSimpleName());

	protected static HiveProfilesMgmt profilesMgmt = HiveProfilesMgmtImpl
			.getInstance();

	private final CountDownLatch midGate;

	private final String operation;

	private final int nProfiles;

	public HiveProfilesTest(String operation, int nThreads, int nProfiles) {
		super();
		this.operation = operation;
		midGate = new CountDownLatch(nThreads);
		this.nProfiles = nProfiles;
	}

	public void run() {
		String user = "u" + getId();
		try {
			if ("testCreate".equals(operation)) {
				createFilter(user);
				for (int i = 1; i <= nProfiles; i++) {
					createProfile(user + "_hp" + i);
				}
			} else if ("testQuery".equals(operation)) {
				for (int i = 1; i <= nProfiles; i++) {
					HmBo hmBo = BoMgmt.findBoById(MapNode.class,
							(long) getId() + 61, null, null);
					// HmBo hmBo = BoMgmt.findBoById(HiveProfile.class, new
					// Long(getId() + 25), null, null);
					log.info("run", "u" + getId() + ", bo: " + hmBo);
					List<?> bos = BoMgmt.findBos(MapNode.class, null,
							new FilterParams("mapName", MapMgmt.ROOT_MAP_NAME),
							null, null);
					log.info("run", "u" + getId() + ", # map bos: "
							+ bos.size());
				}
			} else if ("testUpdate".equals(operation)) {
				updateProfiles(user);
			} else if ("testRemove".equals(operation)) {
				removeProfiles();
				removeFilters();
			} else {
				log.info("run", "Unknown test operation: " + operation);
			}
		} catch (Exception e) {
			log.error("run", "Test failed: ", e);
		}
	}

	public static void main(String[] args) {
		HiveProfilesTest hiveProfilesTest = new HiveProfilesTest(null, 0, 0);
		try {
			log.info("main", "# arguments: " + args.length);
			MgrUtil.log(log, "Args: ", args);
			if ("store".equals(args[0])) {
				createProfile("hive1");
			} else if ("update".equals(args[0])) {
				hiveProfilesTest.updateProfiles(null);
			} else if ("remove".equals(args[0])) {
				removeProfiles();
			} else if ("list".equals(args[0])) {
				listProfiles();
			} else if ("all".equals(args[0])) {
				createProfile("hive1");
				listProfiles();
				removeProfiles();
				createProfile("hive2");
				hiveProfilesTest.updateProfiles(null);
				createProfile("hive1");
				createProfile("hive3");
				listProfiles();
			} else if ("xml".equals(args[0])) {
				exportProfiles();
			} else {
				listProfiles();
			}
		} catch (Exception e) {
			log.error("main", "Exception: ", e);
		}

		HibernateUtil.close();
	}

	public static void listProfiles() throws Exception {
		List<?> profiles = BoMgmt.findBos(HiveProfile.class, null, null, null);
		// log.info("# hive profiles: " + profiles.size());
		for (Object obj : profiles) {
			HiveProfile profile = (HiveProfile) profiles;
			// log.info("ID: " + profile.getId() + " - Hive: "
			// + profile.getHiveName());
		}
	}

	protected static int fragThreshold = 256;

	protected static short nativeVlan = 1;

	public static void createProfile(String hiveName) throws Exception {
		HiveProfile hiveProfile = new HiveProfile();
		hiveProfile.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
		hiveProfile.setHiveName(hiveName);
		// hiveProfile.setNativeVlan(nativeVlan++);
		if (nativeVlan > 16) {
			nativeVlan = 1;
		}
		hiveProfile.setFragThreshold(fragThreshold++);
		hiveProfile.setRtsThreshold(fragThreshold + 120);
		hiveProfile.setL3TrafficPort(5472);
		QueryUtil.createBo(hiveProfile);
	}

	public static void createFilter(String filterName) throws Exception {
		MacFilter macFilter = new MacFilter();
		macFilter.setFilterName(filterName + "_f1");
		// macFilter.setComment("first");
		// macFilter.setFilterAction(MacFilter.FILTER_ACTION_DENY);
		QueryUtil.createBo(macFilter);
		macFilter = new MacFilter();
		macFilter.setFilterName(filterName + "_f2");
		// macFilter.setComment("second");
		// macFilter.setFilterAction(MacFilter.FILTER_ACTION_PERMIT);
		QueryUtil.createBo(macFilter);
	}

	public void updateProfiles(String prefix) throws Exception {
		Paging paging = new PagingImpl(HiveProfile.class);
		for (paging.setPageSize(100); paging.hasNext();) {
			List<?> profiles = BoMgmt.findBos(paging.next(), null, null, null,
					null);
			log.info("updateProfiles", "Updating another " + profiles.size()
					+ " hive profiles: ");
			for (Object obj : profiles) {
				HiveProfile profile = (HiveProfile) obj;
				if (prefix != null && !profile.getHiveName().startsWith(prefix)) {
					continue;
				}
				profile.setFragThreshold(profile.getFragThreshold() + getId());
				profile.setRtsThreshold(profile.getRtsThreshold() + getId());

				BoMgmt.updateBo(profile, null, null);
			}
		}
	}

	public static void removeProfiles() throws Exception {
	}

	public static void removeFilters() throws Exception {
		List<?> macFilters = BoMgmt.findBos(MacFilter.class, null, null, null);
		// log.info("# hives: " + hiveProfiles.size());
		List<Long> ids = new ArrayList<Long>();
		for (Object obj : macFilters) {
			MacFilter macFilter = (MacFilter) obj;
			ids.add(macFilter.getId());
		}
		BoMgmt.removeBos(MacFilter.class, ids, null, null);
	}

	public static void exportProfiles() throws Exception {
		JAXBContext jc = JAXBContext.newInstance("com.ah.xml.hiveprofile");
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		List<?> hiveProfiles = BoMgmt.findBos(HiveProfile.class, null, null,
				null);
		XmlHiveProfiles xmlHiveProfiles = new XmlHiveProfiles();
		for (Object obj : hiveProfiles) {
			HiveProfile profile = (HiveProfile) obj;
			XmlHiveProfile xmlHiveProfile = profilesMgmt.marshal(profile);
			xmlHiveProfiles.getProfile().add(xmlHiveProfile);
		}
		log.info("exportProfiles", "# profiles: "
				+ xmlHiveProfiles.getProfile().size());
		m.marshal(xmlHiveProfiles, System.out);

		XmlHiveProfile xmlHiveProfile = new XmlHiveProfile();
		xmlHiveProfile.setHiveName("test1");
		xmlHiveProfile.setDescription("it");
		xmlHiveProfile.setNativeVlan((short) 5);
	}

}