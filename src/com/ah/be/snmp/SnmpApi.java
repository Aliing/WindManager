/**
 *@filename		SnmpApi.java
 *@version
 *@author		Frank
 *@createtime	2007-9-3 13:54:32
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.snmp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

// import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.SMIConstants;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.PDUFactory;
import org.snmp4j.util.RetrievalEvent;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

//import com.ah.be.app.DebugUtil;
//import com.ah.test.util.RunTest;
import com.ah.util.coder.AhDecoder;

/**
 * @author Frank
 * @version V1.0.0.0
 */
public class SnmpApi implements PDUFactory {

	// private static final Logger logger = Logger
	// .getLogger(SnmpApi.class);

	private String			m_string_HostName				= null;
	private int				m_int_Port						= 161;

	private Address			m_address						= null;
	private CommunityTarget	m_target						= null;
	private Snmp			m_snmp							= null;

	// use for get table
	private int				m_int_MaxNumRowsPerPDU			= 20;
	private final int		m_int_MaxNumColumnsPerPDU		= 1;

	/**
	 * time out
	 */
	public static final int	SNMP_ERROR_TIMEOUT				= -1;
	/**
	 * io error
	 */
	public static final int	SNMP_ERROR_IO					= -99;
	/**
	 * index type wrong
	 */
	public static final int	SNM_INDEX_TYPE_WRONG			= -100;
	/**
	 * Operation success (no error).
	 */
	public static final int	SNMP_ERROR_SUCCESS				= 0;
	/**
	 * PDU encoding is too big for the transport used.
	 */
	public static final int	SNMP_ERROR_TOO_BIG				= 1;
	/**
	 * No such variable binding name.
	 */
	public static final int	SNMP_ERROR_NO_SUCH_NAME			= 2;
	/**
	 * Bad value in variable binding.
	 */
	public static final int	SNMP_ERROR_BAD_VALUE			= 3;
	/**
	 * The variable binding is read-only.
	 */
	public static final int	SNMP_ERROR_READ_ONLY			= 4;
	/**
	 * An unspecific error caused by a variable binding.
	 */
	public static final int	SNMP_ERROR_GENERAL_ERROR		= 5;
	/**
	 * The variable binding is not accessible by the current MIB view.
	 */
	public static final int	SNMP_ERROR_NO_ACCESS			= 6;
	/**
	 * The variable binding's value has the wrong type.
	 */
	public static final int	SNMP_ERROR_WRONG_TYPE			= 7;
	/**
	 * The variable binding's value has the wrong length.
	 */
	public static final int	SNMP_ERROR_WRONG_LENGTH			= 8;
	/**
	 * The variable binding's value has a value that could under no
	 * circumstances be assigned.
	 */
	public static final int	SNMP_ERROR_WRONG_ENCODING		= 9;
	/**
	 * The variable binding's value has the wrong encoding.
	 */
	public static final int	SNMP_ERROR_WRONG_VALUE			= 10;
	/**
	 * The specified object does not exists and cannot be created, see error
	 * index.
	 */
	public static final int	SNMP_ERROR_NO_CREATION			= 11;
	/**
	 * The variable binding's value is presently inconsistent with the current
	 * state of the target object.
	 */
	public static final int	SNMP_ERROR_INCONSISTENT_VALUE	= 12;
	/**
	 * The resource needed to assign a variable binding's value is presently
	 * unavailable.
	 */
	public static final int	SNMP_ERROR_RESOURCE_UNAVAILABLE	= 13;
	/**
	 * Unable to commit a value, see error index.
	 */
	public static final int	SNMP_ERROR_COMMIT_FAILED		= 14;
	/**
	 * Unable to undo a committed value, see error index.
	 */
	public static final int	SNMP_ERROR_UNDO_FAILED			= 15;
	/**
	 * Unauthorized access.
	 */
	public static final int	SNMP_ERROR_AUTHORIZATION_ERROR	= 16;
	/**
	 * The variable's value cannot be modified.
	 */
	public static final int	SNMP_ERROR_NOT_WRITEABLE		= 17;
	/**
	 * The specified object does not exists and presently it cannot be created.
	 */
	public static final int	SNMP_ERROR_INCONSISTENT_NAME	= 18;

	/**
	 * Construct method
	 * 
	 * @throws IOException -
	 */
	public SnmpApi() throws IOException {
		init();
	}

	/**
	 * Construct method
	 * 
	 * @param arg_HostName
	 *            name of host
	 * @throws IOException -
	 */
	public SnmpApi(String arg_HostName) throws IOException {
		m_string_HostName = arg_HostName;
		init();
	}

	/**
	 * Construct method
	 * 
	 * @param arg_HostName
	 *            name of host
	 * @param arg_Port
	 *            port of snmp
	 * @throws IOException -
	 */
	public SnmpApi(String arg_HostName, int arg_Port) throws IOException {
		m_string_HostName = arg_HostName;
		m_int_Port = arg_Port;
		init();
	}

	private void init() throws IOException {
		m_address = GenericAddress.parse("udp:" + m_string_HostName + "/"
				+ m_int_Port);
		// Community Target
		m_target = new CommunityTarget();
		m_target.setAddress(m_address);
		m_target.setVersion(SnmpConstants.version2c);
		m_target.setCommunity(new OctetString("public"));
		m_target.setTimeout(3000);
		// Sending Synchronous Message
		DefaultUdpTransportMapping udpTransportMap = new DefaultUdpTransportMapping();
		m_snmp = new Snmp(udpTransportMap);
		m_snmp.listen();
	}

	@Override
	public PDU createPDU(Target target) {
		PDU request;
		request = new PDU();
		return request;
	}

	/**
	 * set address
	 * 
	 * @param arg_HostName
	 *            name of host
	 */
	public void setAddress(String arg_HostName) {
		setAddress(arg_HostName, 161);
	}

	/**
	 * set address
	 * 
	 * @param arg_HostName
	 *            name of host
	 * @param arg_Port
	 *            port of snmp
	 */
	public void setAddress(String arg_HostName, int arg_Port) {
		m_string_HostName = arg_HostName;
		m_int_Port = arg_Port;
		m_address = GenericAddress.parse("udp:" + m_string_HostName + "/"
				+ m_int_Port);
		m_target.setAddress(m_address);
	}

	/**
	 * set community
	 * 
	 * @param arg_Community
	 *            community
	 */
	public void setCommunity(String arg_Community) {
		m_target.setCommunity(new OctetString(arg_Community));
	}

	/**
	 * set time out
	 * 
	 * @param arg_Timeout
	 *            interval of time in ms
	 */
	public void setTimeout(int arg_Timeout) {
		m_target.setTimeout(arg_Timeout);
	}

	/**
	 * set MaxNumRowsPerPDU use for get table
	 * 
	 * @param arg_MaxNumPerPDU
	 *            max num per pdu, better equal columns+2 of table
	 */
	public void setMaxNumPerPDU(int arg_MaxNumPerPDU) {
		m_int_MaxNumRowsPerPDU = arg_MaxNumPerPDU;
	}

	/**
	 * get snmp operation
	 * 
	 * @param arg_OID
	 *            id of object
	 * @return PDU
	 * @throws SnmpException -
	 */
	public PDU getSnmp(String arg_OID) throws SnmpException {
		String[] string_Array = new String[1];
		string_Array[0] = arg_OID;
		return getSnmp(string_Array);
	}

	/**
	 * get snmp operation
	 * 
	 * @param arg_OID
	 *            array of object id
	 * @return PDU
	 * @throws SnmpException -
	 */
	public PDU getSnmp(String[] arg_OID) throws SnmpException {
		PDU pdu;
		PDU pduSend = new PDU();
		pduSend.setType(PDU.GET);
		for (String stringOID : arg_OID)
			pduSend.add(new VariableBinding(new OID(stringOID)));
		try {
			ResponseEvent response = m_snmp.send(pduSend, m_target);
			if (response.getResponse() != null) {
				pdu = response.getResponse();
				if (pdu.getErrorStatus() != SnmpApi.SNMP_ERROR_SUCCESS) {
					SnmpException e = new SnmpException(pdu
							.getErrorStatusText());
					e.setErrorStatus(pdu.getErrorStatus());
					throw e;
				}
				Vector<? extends VariableBinding> vector_VB = pdu.getVariableBindings();
				VariableBinding vb;
				for (int i = 0; i < vector_VB.size(); i++) {
					vb = vector_VB.elementAt(i);
					if (vb.getVariable().getSyntax() == SMIConstants.EXCEPTION_NO_SUCH_OBJECT
							|| vb.getVariable().getSyntax() == SMIConstants.EXCEPTION_NO_SUCH_INSTANCE) {
						// no such object or no such instance
						SnmpException e = new SnmpException(vb.getOid()
								.toString()
								+ ":" + vb.getVariable().toString());
						e.setErrorStatus(SnmpApi.SNMP_ERROR_NO_SUCH_NAME);
						throw e;
					}
				}
			} else {
				SnmpException e = new SnmpException("Time out");
				e.setErrorStatus(SnmpApi.SNMP_ERROR_TIMEOUT);
				throw e;
			}
		} catch (IOException ex) {
			// logger.error("error in get pdu,error message is " +
			// ex.getMessage());
			SnmpException e = new SnmpException(ex.getMessage());
			e.setErrorStatus(SnmpApi.SNMP_ERROR_IO);
			throw e;
		}
		return pdu;
	}

	/**
	 * get snmp operation
	 * 
	 * @param arg_OID
	 *            id of object
	 * @return String
	 * @throws SnmpException -
	 */
	public String get(String arg_OID) throws SnmpException {
		PDU pdu = getSnmp(arg_OID);
		if (pdu == null || pdu.getVariableBindings() == null
				|| pdu.getVariableBindings().size() <= 0)
			return null;
		VariableBinding vb = pdu.getVariableBindings()
				.elementAt(0);
		return vb.getVariable().toString();
	}

	/**
	 * get operation
	 * 
	 * @param arg_OID
	 *            array of object id
	 * @return String[]
	 * @throws SnmpException -
	 */
	public String[] get(String[] arg_OID) throws SnmpException {
		PDU pdu = getSnmp(arg_OID);
		if (pdu == null || pdu.getVariableBindings() == null
				|| pdu.getVariableBindings().size() <= 0)
			return null;
		Vector<? extends VariableBinding> vector_VB = pdu.getVariableBindings();
		String[] string_Return = new String[vector_VB.size()];
		VariableBinding vb;
		for (int i = 0; i < vector_VB.size(); i++) {
			vb = vector_VB.elementAt(i);
			string_Return[i] = vb.getVariable().toString();
		}
		return string_Return;
	}

	private List<TableEvent> getTable(OID arg_TableOID) throws SnmpException {
		TableUtils tableUtils = new TableUtils(m_snmp, this);
		tableUtils.setMaxNumRowsPerPDU(m_int_MaxNumRowsPerPDU);
		tableUtils.setMaxNumColumnsPerPDU(m_int_MaxNumColumnsPerPDU);

		OID[] columns = new OID[1];
		for (int i = 0; i < columns.length; i++) {
			columns[i] = arg_TableOID;
		}

		List<TableEvent> list_Return = tableUtils
				.getTable(m_target, columns, null, null);

		if (list_Return != null && list_Return.size() > 0) {
			RetrievalEvent event = list_Return.get(list_Return
					.size() - 1);
			if (event.getStatus() != RetrievalEvent.STATUS_OK) {
				// logger.error("error in get table,error status is "+
				// event.getStatus() + " error message is "+
				// event.getErrorMessage());
				SnmpException e = new SnmpException("Time out");
				e.setErrorStatus(SnmpApi.SNMP_ERROR_TIMEOUT);
				throw e;
			}
		}
		if (list_Return == null) {
			SnmpException e = new SnmpException("Time out");
			e.setErrorStatus(SnmpApi.SNMP_ERROR_TIMEOUT);
			throw e;
		}
		return list_Return;
	}

	/**
	 * get table
	 * 
	 * @param arg_TableOID
	 *            object id of table
	 * @param arg_IndexType
	 *            index type of table
	 * @return String[][] if length is 0 indicate table has no data
	 * @throws SnmpException -
	 */
	public String[][] getTable(String arg_TableOID, int[] arg_IndexType)
			throws SnmpException {
		List<TableEvent> list = getTable(new OID(arg_TableOID));

		// get rows and columns
		int int_Rows = 0;
		boolean boolean_First = true;
		int int_Columns = 0;
		int int_PreColumnID = 0, int_CurColumnID;
		for (TableEvent event : list) {
			// get column number
			int_CurColumnID = event.getIndex().get(1);
			if (int_CurColumnID != int_PreColumnID) {
				int_Columns++;
				int_PreColumnID = int_CurColumnID;
				if (int_Columns > 1) {
					boolean_First = false;
				} else
					int_Rows = 1;
			} else {
				// get row number
				if (boolean_First)
					int_Rows++;
			}
		}

		int int_IndexColumns = arg_IndexType.length;
		int_Columns += int_IndexColumns;
		String[][] string_Array = new String[int_Rows][int_Columns];

		// assemble
		int int_Row, int_Column;
		int int_CurrSize = 0;
		for (TableEvent event : list) {
			int_Row = int_CurrSize % int_Rows;
			int_Column = int_CurrSize / int_Rows;
			// get index value
			if (0 == int_Column) {
				// retrieve index value by index type
				int int_Pos = 2;
				byte[] byte_Array;
				int int_Len;
				for (int k = 0; k < int_IndexColumns; k++) {
					try {
						switch (arg_IndexType[k]) {
						case SnmpConstance.SNMP_INDEX_TYPE_INT:
							string_Array[int_Row][k] = String.valueOf(event
									.getIndex().get(int_Pos));
							int_Pos++;
							break;
						case SnmpConstance.SNMP_INDEX_TYPE_IP:
							string_Array[int_Row][k] = event.getIndex().get(
									int_Pos)
									+ "."
									+ event.getIndex().get(int_Pos + 1)
									+ "."
									+ event.getIndex().get(int_Pos + 2)
									+ "." + event.getIndex().get(int_Pos + 3);
							int_Pos += 4;
							break;
						case SnmpConstance.SNMP_INDEX_TYPE_OTECT:
							int_Len = event.getIndex().get(int_Pos);
							int_Pos++;
							byte_Array = new byte[int_Len];
							for (int j = 0; j < int_Len; j++)
								byte_Array[j] = (byte) event.getIndex().get(
										int_Pos + j);
							int_Pos += int_Len;
							string_Array[int_Row][k] = new String(byte_Array);
							break;
						case SnmpConstance.SNMP_INDEX_TYPE_OTECT_HEX:
							int_Len = event.getIndex().get(int_Pos);
							int_Pos++;
							byte_Array = new byte[int_Len];
							for (int j = 0; j < int_Len; j++)
								byte_Array[j] = (byte) event.getIndex().get(
										int_Pos + j);
							int_Pos += int_Len;
							string_Array[int_Row][k] = AhDecoder
									.bytes2hex(byte_Array);
							break;
						default:
							string_Array[int_Row][k] = "";
							break;
						}
					} catch (Exception e) {
						SnmpException ex = new SnmpException(
								"Index type is wrong");
						ex.setErrorStatus(SnmpApi.SNM_INDEX_TYPE_WRONG);
						throw ex;
					}
				}
			}
			string_Array[int_Row][int_Column + int_IndexColumns] = event
					.getColumns()[0].getVariable().toString();
			int_CurrSize++;
		}
		return string_Array;
	}

	/**
	 * get table
	 * 
	 * @param arg_TableOID
	 *            object id of table
	 * @return VariableBinding[][] if length is 0 indicate table has no data
	 * @throws SnmpException -
	 */
	public VariableBinding[][] getSnmpTable(String arg_TableOID)
			throws SnmpException {
		List<TableEvent> list = getTable(new OID(arg_TableOID));

		// get rows and columns
		int int_Rows = 0;
		boolean boolean_First = true;
		int int_Columns = 0;
		int int_PreColumnID = 0, int_CurColumnID;
		for (TableEvent event : list) {
			// get column number
			int_CurColumnID = event.getIndex().get(1);
			if (int_CurColumnID != int_PreColumnID) {
				int_Columns++;
				int_PreColumnID = int_CurColumnID;
				if (int_Columns > 1) {
					boolean_First = false;
				} else
					int_Rows = 1;
			} else {
				// get row number
				if (boolean_First)
					int_Rows++;
			}
		}
		VariableBinding[][] vbs = new VariableBinding[int_Rows][int_Columns];

		// assemble
		int int_Row, int_Column;
		int int_CurrSize = 0;
		for (TableEvent event : list) {
			int_Row = int_CurrSize % int_Rows;
			int_Column = int_CurrSize / int_Rows;
			vbs[int_Row][int_Column] = event.getColumns()[0];
			int_CurrSize++;
		}
		return vbs;
	}

	/**
	 * send snmp trap
	 *
	 * @param oid -
	 * @param vbs -
	 * @throws SnmpException -
	 */
	public void sendSnmpTrap(String oid,VariableBinding[] vbs) throws SnmpException {
		PDU pduSend = new PDU();
		pduSend.setType(PDU.TRAP);
		long uptime = getUpTime();
		//.iso.org.dod.internet.mgmt.mib-2.system.sysUpTime
		pduSend.add(new VariableBinding(new OID("1.3.6.1.2.1.1.3.0"),new TimeTicks(uptime*100)));
		//.iso.org.dod.internet.snmpV2.snmpModules.snmpMIB.snmpMIBObjects.snmpTrap.snmpTrapOID
		pduSend.add(new VariableBinding(new OID("1.3.6.1.6.3.1.1.4.1.0"),new OID(oid)));
		if(vbs != null)
			pduSend.addAll(vbs);
		try {
			m_snmp.send(pduSend, m_target);
		} catch (IOException ex) {
			SnmpException e = new SnmpException(ex.getMessage());
			e.setErrorStatus(SnmpApi.SNMP_ERROR_IO);
			throw e;
		}
	}
	
	private long getUpTime() {
		long uptime = 0;

		try {
			String[] string_Path_Array = new String[3];
			string_Path_Array[0] = "bash";
			string_Path_Array[1] = "-c";
			string_Path_Array[2] = "uptime";
			Process process = Runtime.getRuntime().exec(string_Path_Array);

			BufferedReader reader = new BufferedReader(new InputStreamReader(process
					.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				if (line.indexOf("up") > 0) {
					// parse it.
					String[] splitArray = line.split("\\s+");
					List<String> list = new ArrayList<String>(splitArray.length);
					list.addAll(Arrays.asList(splitArray));

					if (list.indexOf("days,") > 0) {
						int days = Integer.valueOf(list.get(list.indexOf("days,") - 1));
						uptime += (days * 24 * 60 * 60);
					}

					if (list.indexOf("day,") > 0) {
						uptime += (24 * 60 * 60);
					}

					if (list.indexOf("min,") > 0) {
						int mins = Integer.valueOf(list.get(list.indexOf("min,") - 1));
						uptime += (60 * mins);
					} else {
						int userIndex = (list.indexOf("users,") > 0) ? list.indexOf("users,")
								: list.indexOf("user,");
						String timeString = list.get(userIndex - 2);
						int hour = Integer.valueOf(
								timeString.substring(0, timeString.indexOf(":")));
						int min = Integer.valueOf(
								timeString.substring(timeString.indexOf(":") + 1, timeString
										.indexOf(",")));

						uptime += (hour * 60 * 60);
						uptime += (min * 60);
					}
				}
			}
		} catch (Exception e) {
			uptime = 0;
		}
		return uptime;
	}
	/**
	 * close snmp,best close definitely
	 */
	public void close() {
		if (m_snmp != null) {
			try {
				m_snmp.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			m_snmp = null;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}

	public static void main(String[] args) {
		try {
			for(int m = 0; m < 10; m ++) {
			SnmpApi snmpApi = new SnmpApi();
			snmpApi.setAddress("xiaxiaoyin", 162);
			snmpApi.setCommunity("hivemanager");
			// System.out.println(snmpApi.get("1.3.6.1.4.1.26928.1.1.1.2.1"));
//			int[] indexType = new int[1];
//			indexType[0] = SnmpConstance.SNMP_INDEX_TYPE_INT;
//			// indexType[1] = SnmpConstance.SNMP_INDEX_TYPE_OTECT_HEX;
//			String[][] vb_Array = snmpApi.getTable(
//					"1.3.6.1.4.1.26928.1.1.1.2.1.2", indexType);
//			for (String[] row : vb_Array) {
//				for (String column : row)
//					System.out.print(column + "\t");
//				System.out.println();
//			}
			
			//test for trap
			VariableBinding[] vbs = new VariableBinding[0];
			for(int i = 0; i < 0; i++) {
				vbs[i] = new VariableBinding();
			}
			snmpApi.sendSnmpTrap("1.3.6.1.4.1.26928.1.1.1.1.1.8",vbs);
			snmpApi.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}