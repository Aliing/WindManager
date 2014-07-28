package com.ah.test;

/*
 * @author Chris Scheers
 */
import java.util.Date;
import java.util.Random;

import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhNeighbor;
import com.ah.bo.performance.AhRadioStats;
import com.ah.bo.performance.AhVIfStats;
import com.ah.bo.performance.AhXIf;
import com.ah.bo.performance.XIfPK;
import com.ah.test.util.HmTest;
import com.ah.util.Tracer;

public class PerformanceTest extends HmTest {
	private static final Tracer log = new Tracer(PerformanceTest.class
			.getSimpleName());

	public void run() {
		try {
//			addNeithbor();
//			addClient();
//			addInterface();

		} catch (Exception e) {
			log.error("run", "Test failed: ", e);
			return;
		}
	}
//
//	public void addInterface() {
//		try {
//			for (int k = 0; k < 3; k++) {
//				Date mydata = new Date();
//				mydata.setTime(mydata.getTime() + 3600000 * k);
//				for (int i = 0; i < 6; i++) {
//					AhVIfStats ahVIfStats = new AhVIfStats();
//					// setSessionDataSource(new AhVIfStats());
//					XIfPK xifpk = new XIfPK();
//					xifpk.setIfIndex(i + 1);
//					xifpk.setApName("apname01");
//					xifpk.setStatTime(mydata);
//					ahVIfStats.setXifpk(xifpk);
//					ahVIfStats.setApSerialNumber("A10092211545");
//					ahVIfStats.setApMac("apmac01");
//					// ((AhVIfStats)dataSource).setRxVIfAssocWithBadWPAIE(11+i*100*k);
//					BoMgmt.createBo(ahVIfStats, null, null);
//					// createBo(dataSource);
//				}
//				for (int i = 0; i < 6; i++) {
//
//					AhXIf ahXIf = new AhXIf();
//					// setSessionDataSource(new AhXIf());
//					XIfPK xifpk = new XIfPK();
//					xifpk.setIfIndex(i + 1);
//					xifpk.setApName("apname01");
//					xifpk.setStatTime(mydata);
//					ahXIf.setXifpk(xifpk);
//					ahXIf.setApMac("apmac01");
//					ahXIf.setApSerialNumber("A10092211545");
//					ahXIf.setIfName("wifi" + i);
//					if (k == 0) {
//						ahXIf.setIfType(AhXIf.IFMODE_NOTUSED);
//					} else if (k == 1) {
//						ahXIf.setIfType(AhXIf.IFMODE_ACCESS);
//					} else {
//						ahXIf.setIfType(AhXIf.IFMODE_BACKHAUL);
//					}
//					BoMgmt.createBo(ahXIf, null, null);
//					// createBo(dataSource);
//				}
//			}
//
//			for (int k = 0; k < 3; k++) {
//				Date mydata = new Date();
//				mydata.setTime(mydata.getTime() + 3600000 * k);
//				for (int i = 0; i < 6; i++) {
//					AhVIfStats ahVIfStats = new AhVIfStats();
//					// setSessionDataSource(new AhVIfStats());
//					XIfPK xifpk = new XIfPK();
//					xifpk.setIfIndex(i + 1);
//					xifpk.setApName("apname02");
//					xifpk.setStatTime(mydata);
//					ahVIfStats.setXifpk(xifpk);
//					ahVIfStats.setApMac("apmac02");
//					ahVIfStats.setApSerialNumber("A20092211545");
//					// ((AhVIfStats)dataSource).setRxVIfAssocWithBadWPAIE(11+i*100*k);
//					BoMgmt.createBo(ahVIfStats, null, null);
//					// createBo(dataSource);
//				}
//
//				for (int i = 0; i < 6; i++) {
//					AhXIf ahXIf = new AhXIf();
//					// setSessionDataSource(new AhXIf());
//					XIfPK xifpk = new XIfPK();
//					xifpk.setIfIndex(i + 1);
//					xifpk.setApName("apname02");
//					xifpk.setStatTime(mydata);
//					ahXIf.setXifpk(xifpk);
//					ahXIf.setApMac("apmac02");
//					ahXIf.setApSerialNumber("A20092211545");
//					ahXIf.setIfName("wifi" + i);
//					if (k == 0) {
//						ahXIf.setIfType(AhXIf.IFMODE_NOTUSED);
//					} else if (k == 1) {
//						ahXIf.setIfType(AhXIf.IFMODE_ACCESS);
//					} else {
//						ahXIf.setIfType(AhXIf.IFMODE_BACKHAUL);
//					}
//					BoMgmt.createBo(ahXIf, null, null);
//					// createBo(dataSource);
//				}
//			}
//
//			for (int k = 0; k < 3; k++) {
//				Date mydata = new Date();
//				mydata.setTime(mydata.getTime() + 3600000 * k);
//				for (int i = 0; i < 2; i++) {
//					AhRadioStats ahRadioStats = new AhRadioStats();
//					// setSessionDataSource(new AhRadioStats());
//					XIfPK xifpk = new XIfPK();
//					xifpk.setIfIndex(i + 7);
//					xifpk.setApName("apname01");
//					xifpk.setStatTime(mydata);
//					ahRadioStats.setXifpk(xifpk);
//					ahRadioStats.setApMac("apmac01");
//					ahRadioStats.setApSerialNumber("A100922222222");
//					// ((AhRadioStats)dataSource).setRadioRxDropForFrameTooLarge(11+i*100*k);
//					BoMgmt.createBo(ahRadioStats, null, null);
//					// createBo(dataSource);
//				}
//				for (int i = 0; i < 2; i++) {
//					AhXIf ahXIf = new AhXIf();
//					// setSessionDataSource(new AhXIf());
//					XIfPK xifpk = new XIfPK();
//					xifpk.setIfIndex(i + 7);
//					xifpk.setApName("apname01");
//					xifpk.setStatTime(mydata);
//					ahXIf.setXifpk(xifpk);
//					ahXIf.setApMac("apmac01");
//					ahXIf.setApSerialNumber("A100922222222");
//					ahXIf.setIfName("eth" + i);
//					if (k == 0) {
//						ahXIf.setIfType(AhXIf.IFMODE_NOTUSED);
//					} else if (k == 1) {
//						ahXIf.setIfType(AhXIf.IFMODE_ACCESS);
//					} else {
//						ahXIf.setIfType(AhXIf.IFMODE_BACKHAUL);
//					}
//					BoMgmt.createBo(ahXIf, null, null);
//					// createBo(dataSource);
//				}
//			}
//
//			for (int k = 0; k < 3; k++) {
//				Date mydata = new Date();
//				mydata.setTime(mydata.getTime() + 3600000 * k);
//				for (int i = 0; i < 2; i++) {
//					AhRadioStats ahRadioStats = new AhRadioStats();
//					// setSessionDataSource(new AhRadioStats());
//					XIfPK xifpk = new XIfPK();
//					xifpk.setIfIndex(i + 1);
//					xifpk.setApName("apname02");
//					xifpk.setStatTime(mydata);
//					ahRadioStats.setXifpk(xifpk);
//					ahRadioStats.setApMac("apmac02");
//					ahRadioStats.setApSerialNumber("A200922222222");
//					// ((AhRadioStats)dataSource).setRadioRxDropForFrameTooLarge(11+i*100*k);
//					BoMgmt.createBo(ahRadioStats, null, null);
//					// createBo(dataSource);
//				}
//
//				for (int i = 0; i < 2; i++) {
//					AhXIf ahXIf = new AhXIf();
//					// setSessionDataSource(new AhXIf());
//					XIfPK xifpk = new XIfPK();
//					xifpk.setIfIndex(i + 7);
//					xifpk.setApName("apname02");
//					xifpk.setStatTime(mydata);
//					ahXIf.setXifpk(xifpk);
//					ahXIf.setApMac("apmac02");
//					ahXIf.setApSerialNumber("A200922222222");
//					ahXIf.setIfName("eth" + i);
//					if (k == 0) {
//						ahXIf.setIfType(AhXIf.IFMODE_NOTUSED);
//					} else if (k == 1) {
//						ahXIf.setIfType(AhXIf.IFMODE_ACCESS);
//					} else {
//						ahXIf.setIfType(AhXIf.IFMODE_BACKHAUL);
//					}
//					BoMgmt.createBo(ahXIf, null, null);
//					// createBo(dataSource);
//				}
//			}
//
//		} catch (Exception e) {
//			log.error("run", "Test failed: ", e);
//			return;
//		}
//	}
//
//	public void addNeithbor() {
//		try {
//			for (int i = 0; i < 3; i++) {
//				Date mydata = new Date();
//				mydata.setTime(mydata.getTime() + 3600000 * i);
//				for (int j = 0; j < 8; j++) {
//					AhNeighbor ahNeighbor = new AhNeighbor();
//
//					ahNeighbor.setIfIndex(j + 1);
//					ahNeighbor.setApName("apname1");
//					ahNeighbor.setApMac("apmac1");
//					ahNeighbor.setStatTime(mydata);
//					ahNeighbor.setNeighborAPID("NeighborAPID0" + (j + 1));
//					ahNeighbor.setLinkCost(300 + j);
//					BoMgmt.createBo(ahNeighbor, null, null);
//
//				}
//			}
//			for (int i = 0; i < 3; i++) {
//				Date mydata = new Date();
//				mydata.setTime(mydata.getTime() + 3600000 * i);
//				for (int j = 0; j < 8; j++) {
//					AhNeighbor ahNeighbor = new AhNeighbor();
//					ahNeighbor.setIfIndex(j + 1);
//					ahNeighbor.setApName("apname2");
//					ahNeighbor.setApMac("apmac2");
//					ahNeighbor.setStatTime(mydata);
//					ahNeighbor.setNeighborAPID("NeighborAPID1" + (j + 1));
//					ahNeighbor.setLinkCost(400 + j);
//					BoMgmt.createBo(ahNeighbor, null, null);
//
//				}
//			}
//
//		} catch (Exception e) {
//			log.error("run", "Test failed: ", e);
//			return;
//		}
//	}
//
//	public void addClient() {
//
//		try {
//			int k = 0;
//			for (int i = 0; i < 3; i++) {
//				Date mydata = new Date();
//				Date startTm = new Date(mydata.getTime() + 3600000 * k);
//				Date endTm = new Date(mydata.getTime() + 3600000 * (k + 50));
//
////				AhHistoryClientSession ahHistoryClientSession = new AhHistoryClientSession();
////				ahHistoryClientSession.setApName("apname1");
////				ahHistoryClientSession.setApMac("apmac1");
////				ahHistoryClientSession.setApSerialNumber("AP01092233698");
////				ahHistoryClientSession.setIfIndex(i);
////				ahHistoryClientSession.setClientMac("clientMac01");
////				ahHistoryClientSession.setClientIP("192.168.0.1");
////				ahHistoryClientSession.setClientUsername("user01");
////				ahHistoryClientSession.setStartTime(startTm);
////				ahHistoryClientSession.setEndTime(endTm);
////				ahHistoryClientSession.setMemo("memo" + i);
////				BoMgmt.createBo(ahHistoryClientSession, null, null);
//				Random radio = new Random();
//				for (int j = 0; j < 50; j++) {
//
//					AhAssociation ahAssociation = new AhAssociation();
//					ahAssociation.setApName("apname1");
//					ahAssociation.setApMac("apmac1");
//					ahAssociation.setApSerialNumber("AP01092233698");
//					ahAssociation.setIfIndex(i);
//					ahAssociation.setClientMac("clientMac01");
//					ahAssociation.setClientIP("192.168.0.1");
//					ahAssociation.setClientUsername("user01");
//					ahAssociation.setStatTime(new Date(mydata.getTime()
//							+ 3600000 * (j + k)));
//					ahAssociation.setClientHostname("clientHostname01");
//
//					ahAssociation.setClientRxDataFrames((j + 1) * 2
//							* radio.nextInt());
//					ahAssociation.setClientRxDataOctets((j + 2) * 2
//							* radio.nextInt());
//					ahAssociation.setClientRxMgtFrames((j + 3) * 2
//							* radio.nextInt());
//					ahAssociation.setClientRxUnicastFrames((j + 4) * 2
//							* radio.nextInt());
//					ahAssociation.setClientRxMulticastFrames((j + 5) * 2
//							* radio.nextInt());
//					ahAssociation.setClientRxBroadcastFrames((j + 6) * 2
//							* radio.nextInt());
//					ahAssociation.setClientRxMICFailures((j + 7) * 2
//							* radio.nextInt());
//					ahAssociation.setClientTxDataFrames((j + 8) * 2
//							* radio.nextInt());
//					ahAssociation.setClientTxMgtFrames((j + 9) * 2
//							* radio.nextInt());
//					ahAssociation.setClientTxDataOctets((j + 10) * 2
//							* radio.nextInt());
//					ahAssociation.setClientTxUnicastFrames((j + 11) * 2
//							* radio.nextInt());
//					ahAssociation.setClientTxMulticastFrames((j + 12) * 2
//							* radio.nextInt());
//					ahAssociation.setClientTxBroadcastFrames((j + 13) * 2
//							* radio.nextInt());
//					BoMgmt.createBo(ahAssociation, null, null);
//
//				}
//
//				k = k + 50;
//			}
//
//			k = 0;
//			for (int i = 0; i < 3; i++) {
//				Date mydata = new Date();
//				Date startTm = new Date(mydata.getTime() + 3600000 * k);
//				Date endTm = new Date(mydata.getTime() + 3600000 * (k + 50));
////				AhHistoryClientSession ahHistoryClientSession = new AhHistoryClientSession();
////				ahHistoryClientSession.setApName("apname2");
////				ahHistoryClientSession.setApMac("apmac2");
////				ahHistoryClientSession.setApSerialNumber("AP02092233698");
////				ahHistoryClientSession.setIfIndex(i);
////				ahHistoryClientSession.setClientMac("clientMac02");
////				ahHistoryClientSession.setClientIP("192.168.0.2");
////				ahHistoryClientSession.setClientUsername("user02");
////				ahHistoryClientSession.setStartTime(startTm);
////				ahHistoryClientSession.setEndTime(endTm);
////				ahHistoryClientSession.setMemo("memo" + i);
////				BoMgmt.createBo(ahHistoryClientSession, null, null);
//
//				Random radio = new Random();
//				for (int j = 0; j < 50; j++) {
//					AhAssociation ahAssociation = new AhAssociation();
//					ahAssociation.setApName("apname2");
//					ahAssociation.setApMac("apmac2");
//					ahAssociation.setApSerialNumber("AP02092233698");
//					ahAssociation.setIfIndex(i);
//					ahAssociation.setClientMac("clientMac02");
//					ahAssociation.setClientIP("192.168.0.2");
//					ahAssociation.setClientUsername("user02");
//					ahAssociation.setStatTime(new Date(mydata.getTime()
//							+ 3600000 * (j + k)));
//					ahAssociation.setClientHostname("clientHostname02");
//
//					ahAssociation.setClientRxDataFrames((j + 1) * 5
//							* radio.nextInt());
//					ahAssociation.setClientRxDataOctets((j + 2) * 5
//							* radio.nextInt());
//					ahAssociation.setClientRxMgtFrames((j + 3) * 5
//							* radio.nextInt());
//					ahAssociation.setClientRxUnicastFrames((j + 4) * 5
//							* radio.nextInt());
//					ahAssociation.setClientRxMulticastFrames((j + 5) * 5
//							* radio.nextInt());
//					ahAssociation.setClientRxBroadcastFrames((j + 6) * 5
//							* radio.nextInt());
//					ahAssociation.setClientRxMICFailures((j + 7) * 5
//							* radio.nextInt());
//					ahAssociation.setClientTxDataFrames((j + 8) * 5
//							* radio.nextInt());
//					ahAssociation.setClientTxMgtFrames((j + 9) * 5
//							* radio.nextInt());
//					ahAssociation.setClientTxDataOctets((j + 10) * 5
//							* radio.nextInt());
//					ahAssociation.setClientTxUnicastFrames((j + 11) * 5
//							* radio.nextInt());
//					ahAssociation.setClientTxMulticastFrames((j + 12) * 5
//							* radio.nextInt());
//					ahAssociation.setClientTxBroadcastFrames((j + 13) * 5
//							* radio.nextInt());
//					BoMgmt.createBo(ahAssociation, null, null);
//				}
//
//				k = k + 50;
//			}
//		} catch (Exception e) {
//			log.error("run", "Test failed: ", e);
//			return;
//		}
//	}
}