package com.ah.be.topo.snmp;

public interface SnmpAttributes {

	/**
	 * OID of interface table
	 */
	String AH_TABLE_OID_INTERFACE = ".1.3.6.1.4.1.26928.1.1.1.2.1.1";

	/**
	 * OID of mrp table
	 */
	String AH_TABLE_OID_MRP = ".1.3.6.1.4.1.26928.1.1.1.3.1.1";

	/**
	 * OID of association table
	 */
	String AH_TABLE_OID_ASSOCIATION = ".1.3.6.1.4.1.26928.1.1.1.2.1.2";

	/**
	 * Interface Physical Address OID.
	 */
	String IFPHYSADDRESS_OID = ".1.3.6.1.2.1.2.2.1.6";
}
