package com.ah.ui.actions.config;

import java.io.InputStream;

import org.apache.struts2.ServletActionContext;

import com.ah.bo.mgmt.LocationTracking;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.test.AdminTest;
import com.ah.test.ClientSessionsTest;
import com.ah.test.ClientsTest;
import com.ah.test.HiveAPsTest;
import com.ah.test.HiveProfilesTest;
import com.ah.test.MapsTest;
import com.ah.test.MobileUserTest;
import com.ah.test.NetObjectsTest;
import com.ah.test.PerformanceTest;
import com.ah.test.QueryStatisticsTest;
import com.ah.test.SsidProfileTest;
import com.ah.test.TrapsTest;
import com.ah.test.util.HmTest;
import com.ah.test.util.RunTest;
import com.ah.ui.actions.BaseAction;
import com.ah.util.Tracer;

/*
 * @author Chris Scheers
 */

public class TestAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(
			TestAction.class.getSimpleName());

	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		setSelectedL2Feature(L2_FEATURE_EVENTS);
		if ("download".equals(operation)) {
			return "download";
		} else if ("mgmt".equals(operation)) {
			return "mgmt";
		} else if ("mgmtLeft".equals(operation)) {
			return "mgmtLeft";
		} else if ("mgmtTop".equals(operation)) {
			return "mgmtTop";
		} else if ("trexTest".equals(operation)) {
			preparePage();
			return "trexTest";
		} else if ("testCreateSsid".equals(operation)) {
			final SsidProfileTest test = new SsidProfileTest(operation);
			startTest(threads, test);
		} else if ("testRemoveSsid".equals(operation)) {
			final SsidProfileTest test = new SsidProfileTest(operation);
			startTest(threads, test);
		} else if ("testNetObjects".equals(operation)) {
			new NetObjectsTest().run();
		} else if ("testCreateAlarms".equals(operation)
				|| "testRemoveAlarms".equals(operation)
				|| "testCreateEvents".equals(operation)
				|| "testRemoveEvents".equals(operation)) {
			final int nTraps = selectedId.intValue();
			final TrapsTest test = new TrapsTest(operation, nTraps);
			startTest(threads, test);
		} else if ("testCreate".equals(operation)
				|| "testQuery".equals(operation)
				|| "testUpdate".equals(operation)
				|| "testRemove".equals(operation)) {
			log.info("execute", "Starting test ...");
			final int nProfiles = selectedId.intValue();
			log.info("execute", "Run " + operation + " with " + threads
					+ " threads and " + nProfiles + " profiles.");
			final HiveProfilesTest test = new HiveProfilesTest(operation,
					threads, nProfiles);
			startTest(threads, test);
		} else if ("testHiveApsCreate".equals(operation)
				|| "testHiveApsUpdate".equals(operation)
				|| "testHiveApsRemove".equals(operation)
				|| "testHiveApsBulkCreate".equals(operation)
				|| "testHiveApsBulkUpdate".equals(operation)) {
			log.info("execute", "Starting HiveAPs test ...");
			final int nAPs = selectedId.intValue();
			log.info("execute", "Run " + operation + " with " + threads
					+ " threads and " + nAPs + " HiveAPs.");
			HiveAPsTest test = new HiveAPsTest(operation, nAPs);
			startTest(threads, test);
		} else if ("testCreateClients".equals(operation)
				|| "testBulkCreateClients".equals(operation)
				|| "testUpdateClients".equals(operation)
				|| "testBatchUpdateClients".equals(operation)
				|| "testQueryClientsBy1Mac".equals(operation)
				|| "testQueryClientsBy2Mac".equals(operation)
				|| "testRemoveClients".equals(operation)) {
			log.info("execute", "Starting test clients ...");
			final int nClients = selectedId.intValue();
			log.info("execute", "Run " + operation + " with " + threads
					+ " threads and " + nClients + " profiles.");
			final ClientSessionsTest test = new ClientSessionsTest(operation,
					threads, nClients, this);
			long miliTime = RunTest.runTest(threads, test);
			int totalClients = nClients * threads;
			addActionMessage(getClientTestName(operation) + " test for "
					+ totalClients + " BOs finished in: " + miliTime
					+ " miliseconds, or " + totalClients * 1000 / miliTime
					+ " clients per seconds.");
		} else if ("testPlaceAps".equals(operation)) {
			placeAps();
		} else if ("testMap".equals(operation)) {
			new MapsTest().run();
		} else if ("testAdmin".equals(operation)) {
			new AdminTest().run();
		} else if ("testClientRssi".equals(operation)) {
			new ClientsTest().run();
		} else if ("testAddPerformace".equals(operation)) {
			new PerformanceTest().run();
		} else if ("testAddMobileUser".equals(operation)) {
			new MobileUserTest(selectedId.intValue()).run();
		} else if ("testQueryStatistics".equals(operation)) {
			new QueryStatisticsTest().run();
		} else if ("testSsh".equals(operation)) {
			return "testSsh";
		}
		return "test";
	}

	public void prepare() throws Exception {
		super.prepare();
	}

	public void placeAps() throws Exception {
		double width = 2216;
		double actualWidth = 90; // feet
		double mapToMetric = actualWidth / width
				* LocationTracking.FEET_TO_METERS;
		String apIds[] = { "001977001460", "001977002960", "001977002EC0",
				"00197702EC00", "001977000AE0", "0019770009D0", "0019770023A0",
				"001977036740", "00197703ADC0", "001977036B00", "001977000010",
				"001977000020", "001977000030", "001977000040" };
		double[] xi = { 1535.75335120643, 2019.94638069705, 1630.80965147453,
				2125, 1535.75335120643, 383.195710455764, 228.729222520107,
				2029, 383, 228, 192, 563, 157, 658 };
		double[] yi = { 638.659517426273, 611.924932975871, 267.345844504021,
				1308, 929.769436997319, 540.632707774799, 1152.55764075067,
				551, 540, 1152, 127, 95, 462, 498 };
		for (int i = 0; i < apIds.length; i++) {
			MapLeafNode mapLeafNode = (MapLeafNode) QueryUtil
					.findBoByAttribute(MapLeafNode.class, "apId", apIds[i]);
			if (mapLeafNode != null) {
				mapLeafNode.setX(xi[i]);
				mapLeafNode.setY(yi[i]);
				addActionMessage(apIds[i] + ": (" + mapLeafNode.getX() + ", "
						+ mapLeafNode.getY() + "), in meters: ("
						+ mapLeafNode.getX() * mapToMetric + ", "
						+ mapLeafNode.getY() * mapToMetric + ")");
				QueryUtil.updateBo(mapLeafNode);
			}
		}
	}

	public String getClientTestName(String operation) {
		if ("testCreateClients".equals(operation)) {
			return "Create client sessions";
		} else if ("testBulkCreateClients".equals(operation)) {
			return "Bulk create client sessions";
		} else if ("testUpdateClients".equals(operation)) {
			return "Update client sessions";
		} else if ("testBatchUpdateClients".equals(operation)) {
			return "Batch update client sessions";
		} else if ("testQueryClientsBy1Mac".equals(operation)) {
			return "Query client sessions by clientMac";
		} else if ("testQueryClientsBy2Mac".equals(operation)) {
			return "Query client sessions by clientAndApMac";
		} else if ("testRemoveClients".equals(operation)) {
			return "Remove client sessions";
		} else {
			return "Unknown";
		}
	}

	public InputStream getInputStream() throws Exception {
		String remoteFilename = "/tmp/test.zip";
		return ServletActionContext.getServletContext().getResourceAsStream(
				remoteFilename);
	}

	public String getLocalFilename() {
		return "test.zip";
	}

	protected void startTest(final int nThreads, final HmTest test) {
		Thread t = new Thread() {
			public void run() {
				RunTest.runTest(nThreads, test);
			}
		};
		t.setName("Test Action");
		t.start();
	}

	private int threads;

	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}
}