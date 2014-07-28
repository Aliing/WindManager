package com.ah.be.config.create;
import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.MacTableProfileInt;
import com.ah.xml.be.config.Ethx802Dot1XMacTableObj;
import com.ah.xml.be.config.MacTableExpireTime;
import com.ah.xml.be.config.MacTableSuppressInterval;
/**
 * @author llchen
 * @version 2012-01-04 9:36:43 AM
 */

public class Create8021XMacTableTree {
	private MacTableProfileInt macTableImpl;
	private Ethx802Dot1XMacTableObj macTableObj;
	private GenerateXMLDebug oDebug;

	public Create8021XMacTableTree(MacTableProfileInt macTableImpl, GenerateXMLDebug oDebug) throws Exception {
		this.macTableImpl = macTableImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		
			if(this.macTableImpl != null){
				macTableObj = new Ethx802Dot1XMacTableObj();
				generatePseLevel_1();
			}
	}
	
	public Ethx802Dot1XMacTableObj getEthx802Dot1XMacTableObj(){
		return this.macTableObj;
	}
	
	private void generatePseLevel_1() throws Exception {
		/**
		 * <_802.1x-mac-table>		Ethx802Dot1XMacTableObj
		 */
		
		/** element: <_802.1x-mac-table>.<expire-time> */
		
		oDebug.debug("/configuration/_802.1x-mac-table", 
				"expire-time", GenerateXMLDebug.SET_VALUE,
				macTableImpl.getWlanGuiName(), macTableImpl.getWlanName());
		Object[][] expireParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, macTableImpl.getExpireTime()},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		macTableObj.setExpireTime((MacTableExpireTime)
				CLICommonFunc.createObjectWithName(MacTableExpireTime.class, expireParm)
		);
	
	
		/** element: <_802.1x-mac-table>.<suppress-interval> */
		oDebug.debug("/configuration/_802.1x-mac-table", 
				"suppress-interval", GenerateXMLDebug.SET_VALUE,
				macTableImpl.getWlanGuiName(), macTableImpl.getWlanName());
		Object[][] suppressParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, macTableImpl.getSuppressInterval()},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		macTableObj.setSuppressInterval((MacTableSuppressInterval)
				CLICommonFunc.createObjectWithName(MacTableSuppressInterval.class, suppressParm)
		);
		
	}
}
