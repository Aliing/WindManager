package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.IpProfileInt;
import com.ah.xml.be.config.AhEnumActValue;
import com.ah.xml.be.config.IpNatPolicy;
import com.ah.xml.be.config.IpNatPolicyObj;
import com.ah.xml.be.config.IpObj;
import com.ah.xml.be.config.IpPathMtuDiscovery;
import com.ah.xml.be.config.IpTcpMssThreshold;
import com.ah.xml.be.config.NatPolicyMatch;
import com.ah.xml.be.config.NatPolicyMatchInside;
import com.ah.xml.be.config.NatPolicyType;
import com.ah.xml.be.config.NatPolicyVhostInsideHost;
import com.ah.xml.be.config.NatPolicyVhostInsidePort;
import com.ah.xml.be.config.NatPolicyVhostOutsidePort;
import com.ah.xml.be.config.NatPolicyVhostProtocol;
import com.ah.xml.be.config.NatPolicyVhostProtocolValue;
import com.ah.xml.be.config.NatPolicyVirtualHost;
import com.ah.xml.be.config.TcpMssThresholdSize;

/**
 * @author zhang
 * @version 2007-12-14 10:51:18 AM
 */

public class CreateIpNatPolicyTree {

	private IpProfileInt ipProfileImpl;
	private IpNatPolicyObj ipNatPolicyObj;
	
	private GenerateXMLDebug oDebug;

	private List<Object> ipChildList_1 = new ArrayList<Object>();
	private List<Object> ipNatPolicyChildList_1 = new ArrayList<Object>();
	private List<Object> ipNatPolicyChildList_2 = new ArrayList<Object>();
	private List<Object> ipNatPolicyChildList_3 = new ArrayList<Object>();
	private List<Object> ipNatPolicyChildList_4 = new ArrayList<Object>();
	private List<Object> ipNatPolicyChildList_5 = new ArrayList<Object>();
	private List<Object> ipNatPolicyChildList_6 = new ArrayList<Object>();

	public CreateIpNatPolicyTree(IpProfileInt ipProfileImpl, GenerateXMLDebug oDebug) {
		this.ipProfileImpl = ipProfileImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		ipNatPolicyObj = new IpNatPolicyObj();
		generateChildLevel_1();
	}

	public IpNatPolicyObj getIpNatPolicyObj() {
		return this.ipNatPolicyObj;
	}

	private void generateChildLevel_1() throws Exception {
		
		/** element: <ip>.<nat-policy> */
		if(ipProfileImpl.isConfigNatPolicy()){
			for (int i = 0; i < ipProfileImpl.getNatPolicySize(); i++) {
				ipNatPolicyObj.getNatPolicy().add(createIpNatPolicy(i));
			}
		}
		//generateChildLevel_2();
	}
	
	private IpNatPolicy createIpNatPolicy(int index) throws Exception {
		/** element: <ip>.<nat-policy>.<type> */
		IpNatPolicy policy = new IpNatPolicy();
		policy.setName(ipProfileImpl.getNatPolicyName(index));
		policy.setOperation(AhEnumActValue.YES_WITH_VALUE);
		NatPolicyType type = new NatPolicyType();
		ipNatPolicyChildList_1.add(type);
		policy.setType(type);
		generateIpNatPolicyChildLevel_1(index);
		return policy;
	}
	
	private void generateIpNatPolicyChildLevel_1(int index) throws Exception {
		for (Object childObj : ipNatPolicyChildList_1) {
			if (childObj instanceof NatPolicyType) {
				NatPolicyType type = (NatPolicyType)childObj;
				if (ipProfileImpl.isNatPolicyConfigMatch(index)) {
					
					/** element: <ip>.<nat-policy>.<type>.<match-net> */
					NatPolicyMatch matchNet = new NatPolicyMatch();
					ipNatPolicyChildList_2.add(matchNet);
					type.setMatchNet(matchNet);
				} else if (ipProfileImpl.isNatPolicyConfigVirtualHost(index)) {
					
					/** element: <ip>.<nat-policy>.<type>.<virtual-host> */
					NatPolicyVirtualHost virtualHost = new NatPolicyVirtualHost();
					ipNatPolicyChildList_2.add(virtualHost);
				    type.setVirtualHost(virtualHost);
				}
			}
		}
		ipNatPolicyChildList_1.clear();
		generateIpNatPolicyChildLevel_2(index);
	}

	
	private void generateIpNatPolicyChildLevel_2(int index) throws Exception {
		for (Object childObj : ipNatPolicyChildList_2) {
			if (childObj instanceof NatPolicyMatch) {
				NatPolicyMatch match = (NatPolicyMatch)childObj;
				
				/** element: <ip>.<nat-policy>.<type>.<match-net>.<inside> */
				NatPolicyMatchInside matchInside = new NatPolicyMatchInside();
				ipNatPolicyChildList_3.add(matchInside);
				match.setInside(matchInside);
			}
			if (childObj instanceof NatPolicyVirtualHost) {
				NatPolicyVirtualHost virtualHost = (NatPolicyVirtualHost)childObj;
				
				/** element: <ip>.<nat-policy>.<type>.<match-net>.<inside-host> */
				NatPolicyVhostInsideHost host = new NatPolicyVhostInsideHost();
				ipNatPolicyChildList_3.add(host);
				virtualHost.setInsideHost(host);
			}
		}
		ipNatPolicyChildList_2.clear();
		generateIpNatPolicyChildLevel_3(index);
	}
	
	
	private void generateIpNatPolicyChildLevel_3(int index) throws Exception {
		for (Object childObj : ipNatPolicyChildList_3) {
			if (childObj instanceof NatPolicyMatchInside) {
				NatPolicyMatchInside matchInside = (NatPolicyMatchInside)childObj;
				matchInside.setValue(ipProfileImpl.getNatPolicyMatchInsideValue(index));
				
				/** element: <ip>.<nat-policy>.<type>.<match-net>.<inside>.<outside> */
				matchInside.setOutside(CLICommonFunc.createAhStringObj(ipProfileImpl.getNatPolicyMatchOutsideValue(index)));
			}
			if (childObj instanceof NatPolicyVhostInsideHost) {
				NatPolicyVhostInsideHost virtualHost = (NatPolicyVhostInsideHost)childObj;
				virtualHost.setValue(ipProfileImpl.getNatPolicyVhostInsideHostValue(index));
				
				/** element: <ip>.<nat-policy>.<type>.<match-net>.<inside-host>.<inside-port> */
				NatPolicyVhostInsidePort insidePort = new NatPolicyVhostInsidePort();
				ipNatPolicyChildList_4.add(insidePort);
				virtualHost.setInsidePort(insidePort);
			}
		}
		ipNatPolicyChildList_3.clear();
		generateIpNatPolicyChildLevel_4(index);
	}
	
	private void generateIpNatPolicyChildLevel_4(int index) throws Exception {
		for (Object childObj : ipNatPolicyChildList_4) {
			if (childObj instanceof NatPolicyVhostInsidePort) {
				NatPolicyVhostInsidePort insidePort = (NatPolicyVhostInsidePort)childObj;
				insidePort.setValue(ipProfileImpl.getNatPolicyVhostInsidePortValue(index));
				
				/** element: <ip>.<nat-policy>.<type>.<match-net>.<inside-host>.<inside-port>.<outside-port> */
				NatPolicyVhostOutsidePort outsidePort = new NatPolicyVhostOutsidePort();
				ipNatPolicyChildList_5.add(outsidePort);
				insidePort.setOutsidePort(outsidePort); 
			}
		}
		ipNatPolicyChildList_4.clear();
		generateIpNatPolicyChildLevel_5(index);
	}
	
	private void generateIpNatPolicyChildLevel_5(int index) throws Exception {
		for (Object childObj : ipNatPolicyChildList_5) {
			if (childObj instanceof NatPolicyVhostOutsidePort) {
				NatPolicyVhostOutsidePort outsidePort = (NatPolicyVhostOutsidePort)childObj;
				outsidePort.setValue(ipProfileImpl.getNatPolicyVhostOutsidePortValue(index));
				
				/** element: <ip>.<nat-policy>.<type>.<match-net>.<inside-host>.<inside-port>.<outside-port>.<protocol> */
				NatPolicyVhostProtocol protocol = new NatPolicyVhostProtocol();
				ipNatPolicyChildList_6.add(protocol);
				outsidePort.setProtocol(protocol);
			} 
		}
		ipNatPolicyChildList_5.clear();
		generateIpNatPolicyChildLevel_6(index);
	}
	
	private void generateIpNatPolicyChildLevel_6(int index) throws Exception {
		for (Object childObj : ipNatPolicyChildList_6) {
			if (childObj instanceof NatPolicyVhostProtocol) {
				NatPolicyVhostProtocol protocol = (NatPolicyVhostProtocol)childObj;
				protocol.setValue(NatPolicyVhostProtocolValue.fromValue(ipProfileImpl.getNatPolicyVhostProtocolValue(index)));	
			}
		}
		ipNatPolicyChildList_6.clear();
	
	}

}