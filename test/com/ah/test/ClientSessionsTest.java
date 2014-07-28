package com.ah.test;

import java.util.ArrayList;
import java.util.List;

import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhClientSession;
import com.ah.test.util.HmTest;
import com.ah.util.Tracer;
import com.opensymphony.xwork2.ActionSupport;

public class ClientSessionsTest extends HmTest {
	private static final Tracer log = new Tracer(ClientSessionsTest.class
			.getSimpleName());

	private String operation;

	private int nClients;

	private ActionSupport action;

	public ClientSessionsTest(String operation, int nThreads, int nClients,
			ActionSupport action) {
		super();
		this.operation = operation;
		this.nClients = nClients;
		this.action = action;
	}

	public void run() {
		log.info("run", "Thread id: " + getId());
		try {
			if ("testCreateClients".equals(operation)) {
				createClientSessions();
			} else if ("testBulkCreateClients".equals(operation)) {
				bulkCreateClientSessions();
			} else if ("testUpdateClients".equals(operation)) {
				updateClientSessions();
			} else if ("testBatchUpdateClients".equals(operation)) {
				batchUpdateClientSessions();
			} else if ("testQueryClientsBy1Mac".equals(operation)) {
				queryClientByClientMac();
			} else if ("testQueryClientsBy2Mac".equals(operation)) {
				queryClientByApAndClientMac();
			} else if ("testRemoveClients".equals(operation)) {
				removeClientSessions();
			}
		} catch (Exception e) {
			action.addActionError("Test failed: " + e);
			log.error("run", "Test failed: ", e);
		}
	}

	private String getMac(char prefix, int i) {
		return "c" + (100 + getId()) + (10000000 + i);
	}

	private void createClientSessions() throws Exception {
		for (int i = 0; i < nClients; i++) {
			AhClientSession client = createClient(i);
			Long clientId = QueryUtil.createBo(client);
		}
		action.addActionMessage(nClients
				+ " client sessions created in thread: " + getId());
		log.info("createClientSessions", "Created " + nClients
				+ " client objects.");
	}

	private void bulkCreateClientSessions() throws Exception {
		int bulkSize = 30;
		int clientCount = 0;
		for (int i = 0; i < nClients / bulkSize; i++) {
			List<AhClientSession> clients = new ArrayList<AhClientSession>();
			for (int j = 0; j < bulkSize; j++) {
				AhClientSession client = createClient(i);
				clients.add(client);
			}
			QueryUtil.bulkCreateBos(clients);
			clientCount += bulkSize;
		}
		action.addActionMessage(clientCount
				+ " client sessions bulk created in thread: " + getId());
		log.info("bulkCreateClientSessions", "Bulk created " + clientCount
				+ " client objects.");
	}

	private AhClientSession createClient(int i) {
		AhClientSession client = new AhClientSession();
		client.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
		client.setClientMac(getMac('c', i));
		client.setApMac(getMac('a', i));
		client.setClientIP("192.168.2.30");
		client.setApName("AH-" + client.getApMac());
//		client.setStartTime(new Date());
//		client.setClientRSSI(46);
		return client;
	}

	private void updateClientSessions() throws Exception {
		int clientCount = 0;
		for (int i = 0; i < nClients; i++) {
			String clientMac = getMac('c', i);
			List<Long> boIds = (List<Long>) QueryUtil.executeQuery(
					"select id from "
							+ AhClientSession.class.getSimpleName(),
					null, new FilterParams("clientMac", clientMac));
			for (Long boId : boIds) {
				AhClientSession client = (AhClientSession) QueryUtil
						.findBoById(AhClientSession.class, boId);
				client.setClientIP("172.80.4.25");
				client.setClientHostname("pda-345as");
				client.setClientUsername("jones");
				client = (AhClientSession) QueryUtil.updateBo(client);
			}
			clientCount += boIds.size();
		}
		action.addActionMessage("Updated " + clientCount
				+ " client sessions in thread: " + getId());
		log.info("updateClientSessions", "Updated " + clientCount
				+ " client sessions in thread: " + getId());
	}

	private void batchUpdateClientSessions() throws Exception {
		int clientCount = 0;
		for (int i = 0; i < nClients; i++) {
			String clientMac = getMac('c', i);
			int updates = QueryUtil
					.updateBo(
							AhClientSession.class,
							"clientIp = :s1, clientHostname = :s2, clientUsername = :s3",
							new FilterParams("clientMac = :s4",
									new Object[] { "192.140.2.60", "laptop",
											"jane", clientMac }));
			clientCount += updates;
		}
		action.addActionMessage("Updated " + clientCount
				+ " client sessions in thread: " + getId());
		log.info("batchUpdateClientSessions", "Updated " + clientCount
				+ " client sessions in thread: " + getId());
	}

	private void queryClientByClientMac() {
		int clientCount = 0;
		for (int i = 0; i < nClients; i++) {
			String clientMac = getMac('c', i);
			List<?> boIds = QueryUtil.executeQuery("select id from "
					+ AhClientSession.class.getSimpleName(), null,
					new FilterParams("clientMac", clientMac));
			clientCount += boIds.size();
		}
		action.addActionMessage("Found " + clientCount
				+ " client sessions in thread: " + getId());
		log.info("queryClientByClientMac", "Found " + clientCount
				+ " client sessions in thread: " + getId());
	}

	private void queryClientByApAndClientMac() {
		int clientCount = 0;
		for (int i = 0; i < nClients; i++) {
			String clientMac = getMac('c', i);
			String apMac = getMac('a', i);
			List<?> boIds = QueryUtil.executeQuery("select id from "
					+ AhClientSession.class.getSimpleName(), null,
					new FilterParams("clientMac = :s1 AND apMac = :s2",
							new Object[] { clientMac, apMac }));
			clientCount += boIds.size();
		}
		action.addActionMessage("Found " + clientCount
				+ " client sessions in thread: " + getId());
		log.info("queryClientByApAndClientMac", "Found " + clientCount
				+ " client sessions in thread: " + getId());
	}

	private void removeClientSessions() throws Exception {
		QueryUtil.bulkRemoveBos(AhClientSession.class, null, null, null);
	}
}
