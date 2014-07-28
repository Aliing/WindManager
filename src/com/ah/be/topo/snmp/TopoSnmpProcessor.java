package com.ah.be.topo.snmp;

import java.util.Vector;

//import com.ah.be.app.DebugUtil;
//import com.ah.be.snmp.SnmpApi;
//import com.ah.be.snmp.SnmpConstance;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhNeighbor;
import com.ah.bo.performance.AhXIf;
//import com.ah.bo.performance.XIfPK;

public class TopoSnmpProcessor implements SnmpAttributes {

	public Vector<AhXIf> getApIfInfo(String a_host) throws Exception {
		return null;
//		if (null == a_host || "".equals(a_host)) {
//			return null;
//		}
//		Vector<AhXIf> ifDTOVector = null;
//		SnmpApi snmpApi = new SnmpApi(a_host);
//
//		try {
//			String[][] table = snmpApi.getTable(AH_TABLE_OID_INTERFACE,
//					new int[] { SnmpConstance.SNMP_INDEX_TYPE_INT });
//
//			if (table != null && table.length > 0) {
//
//				int rowCount = table.length;
//
//				String cellValue;
//				AhXIf ifDTO;
//				ifDTOVector = new Vector<AhXIf>(rowCount);
//
//				if (rowCount <= 0) {
//					return null;
//				}
//				for (int i = 0; i < rowCount; i++) {
//					ifDTO = new AhXIf();
//					XIfPK pk = new XIfPK();
//					for (int j = 0; j < table[0].length; j++) {
//						cellValue = table[i][j];
//						switch (j) {
//						case 0:
//							pk.setIfIndex(Integer.parseInt(cellValue));
//						case 1:
//							ifDTO.setIfName(cellValue);
//							break;
//						case 2:
//							ifDTO.setSsidName(cellValue);
//							break;
//						case 3:
//							ifDTO.setIfPromiscuous(Byte.parseByte(cellValue));
//							break;
//						case 4:
//							ifDTO.setIfType(Byte.parseByte(cellValue));
//							break;
//						case 5:
//							ifDTO.setIfMode(Byte.parseByte(cellValue));
//							break;
//						default:
//							break;
//						}
//					}
//					cellValue = snmpApi.get(IFPHYSADDRESS_OID + "."
//							+ table[i][0]);
//					cellValue = cellValue.replace(":", "");
//					// just use apName field to store physical macaddress which
//					// retrieved from snmp.
//					pk.setApName(cellValue);
//					ifDTO.setXifpk(pk);
//					ifDTOVector.addElement(ifDTO);
//				}
//			} else {
//				DebugUtil.topoDebugWarn("SNMP GET Interface table from agent["
//						+ a_host + "] no any entries.");
//			}
//		} catch (Exception e) {
//			DebugUtil.topoDebugWarn("SNMP GET Interface table from agent["
//					+ a_host + "] error." + e.getMessage());
//			throw e;
//		} finally {
//			snmpApi.close();
//		}
//
//		return ifDTOVector;
	}

	public Vector<AhNeighbor> getMrpInfo(String a_host) throws Exception {
		return null;
//		if (null == a_host || "".equals(a_host)) {
//			return null;
//		}
//		Vector<AhNeighbor> mrpDTOVector = null;
//		SnmpApi snmpApi = new SnmpApi(a_host);
//
//		try {
//			String[][] table = snmpApi.getTable(AH_TABLE_OID_MRP,
//					new int[] { SnmpConstance.SNMP_INDEX_TYPE_INT });
//
//			if (table != null && table.length > 0) {
//				int rowCount = table.length;
//				if (rowCount <= 0) {
//					return null;
//				}
//				String cellValue;
//				AhNeighbor mrpDTO;
//				mrpDTOVector = new Vector<AhNeighbor>(rowCount);
//
//				for (int i = 0; i < rowCount; i++) {
//					mrpDTO = new AhNeighbor();
//					for (int j = 0; j < table[0].length; j++) {
//						cellValue = table[i][j];
//
//						switch (j) {
//						case 0:
//							mrpDTO.setIfIndex(Integer.parseInt(cellValue));
//							break;
//						case 1:
//							if (cellValue != null) {
//								cellValue = cellValue.replace(":", "");
//								mrpDTO.setNeighborAPID(cellValue);
//							}
//							break;
//						case 2:
//							mrpDTO.setLinkCost(Long.parseLong(cellValue));
//							break;
//						case 3:
//							mrpDTO.setRssi(Integer.parseInt(cellValue));
//							break;
//						case 4:
//							mrpDTO.setLinkUpTime(Long.parseLong(cellValue));
//							break;
//						case 5:
//							mrpDTO.setLinkType(Byte.parseByte(cellValue));
//							break;
//						default:
//							break;
//						}
//					}
//					cellValue = snmpApi.get(IFPHYSADDRESS_OID + "."
//							+ table[i][0]);
//					cellValue = cellValue.replace(":", "");
//					// just use apName field to store physical macaddress which
//					// retrieved from snmp.
//					mrpDTO.setApName(cellValue);
//					mrpDTOVector.addElement(mrpDTO);
//				}
//			} else {
//				DebugUtil.topoDebugWarn("SNMP GET Mrp table from agent["
//						+ a_host + "] no any entries.");
//				// Return empty vector
//				mrpDTOVector = new Vector<AhNeighbor>();
//			}
//		} catch (Exception e) {
//			DebugUtil.topoDebugWarn("SNMP GET Mrp table from agent[" + a_host
//					+ "] error:" + e.getMessage());
//			throw e;
//		} finally {
//			snmpApi.close();
//		}
//
//		return mrpDTOVector;
	}

	public Vector<AhAssociation> getAssociationInfo(String a_host)
			throws Exception {
		return null;
//		if (null == a_host || "".equals(a_host)) {
//			return null;
//		}
//		Vector<AhAssociation> associateDTOVector = null;
//		SnmpApi snmpApi = new SnmpApi(a_host);
//
//		try {
//			String[][] table = snmpApi.getTable(AH_TABLE_OID_ASSOCIATION,
//					new int[] { SnmpConstance.SNMP_INDEX_TYPE_INT });
//
//			if (table != null && table.length > 0) {
//
//				int rowCount = table.length;
//				if (rowCount <= 0) {
//					return null;
//				}
//
//				String cellValue;
//				AhAssociation associateDTO;
//				associateDTOVector = new Vector<AhAssociation>(rowCount);
//
//				for (int i = 0; i < rowCount; i++) {
//					associateDTO = new AhAssociation();
//
//					for (int j = 0; j < table[0].length; j++) {
//						cellValue = table[i][j];
//
//						switch (j) {
//						case 0:
//							associateDTO
//									.setIfIndex(Integer.parseInt(cellValue));
//							break;
//						case 1:
//							if (cellValue != null) {
//								cellValue = cellValue.replace(":", "");
//								associateDTO.setClientMac(cellValue);
//							}
//							break;
//						case 2:
//							associateDTO.setClientIP(cellValue);
//							break;
//						case 3:
//							associateDTO.setClientHostname(cellValue);
//							break;
//						case 4:
//							associateDTO.setClientRSSI(Integer
//									.parseInt(cellValue));
//							break;
//						case 5:
//							associateDTO.setClientLinkUptime(Long
//									.parseLong(cellValue));
//							break;
//						case 6:
//							associateDTO.setClientCWPUsed(Byte
//									.parseByte(cellValue));
//							break;
//						case 7:
//							associateDTO.setClientAuthMethod(Byte
//									.parseByte(cellValue));
//							break;
//						case 8:
//							associateDTO.setClientEncryptionMethod(Byte
//									.parseByte(cellValue));
//							break;
//						case 9:
//							associateDTO.setClientMACProtocol(Byte
//									.parseByte(cellValue));
//							break;
//						case 10:
//							associateDTO.setClientSSID(cellValue);
//							break;
//						case 11:
//							associateDTO.setClientVLAN(Integer
//									.parseInt(cellValue));
//							break;
//						case 12:
//							associateDTO.setClientUserProfId(Integer
//									.parseInt(cellValue));
//							break;
//						case 13:
//							associateDTO.setClientChannel(Integer
//									.parseInt(cellValue));
//							break;
//						case 14:
//							associateDTO.setClientLastTxRate(Integer
//									.parseInt(cellValue));
//							break;
//						default:
//							break;
//						}
//					}
//
//					associateDTOVector.addElement(associateDTO);
//				}
//			} else {
//				DebugUtil
//						.topoDebugWarn("SNMP GET Association table from agent["
//								+ a_host + "] no any entries.");
//			}
//		} catch (Exception e) {
//			DebugUtil.topoDebugWarn("SNMP GET Association table from agent["
//					+ a_host + "] error:" + e.getMessage());
//			throw e;
//		} finally {
//			snmpApi.close();
//		}
//
//		return associateDTOVector;
	}

	public static void main(String args[]) throws Exception {
		// TopoSnmpProcessor sp = TopoSnmpProcessor.getInstance();

		// Vector<AhXIf> xif = sp.getApIfInfo("10.155.20.56");
		// Vector<AhNeighbor> neighbor = sp.getMrpInfo("10.155.20.64");
		// Vector<AhAssociation> association = sp
		// .getAssociationInfo("10.155.20.56");

	}

}