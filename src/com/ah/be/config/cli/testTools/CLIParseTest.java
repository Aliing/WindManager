package com.ah.be.config.cli.testTools;

import java.io.File;
import java.io.IOException;

import com.ah.be.config.cli.merge.XmlMergeCache;
import com.ah.be.config.cli.parse.CLIParseFactory;
import com.ah.be.config.cli.util.CLIConfigFileUtil;
import com.ah.be.config.cli.xsdbean.ConstraintType;

public class CLIParseTest {

	public static final String LINE_SPLIT = "-----------------------------------------------------------------------";
	
	public static StringBuffer allXml = new StringBuffer("<root>");

	public CLIParseTest() {
		
	}

	public static void main(String args[]) throws IOException {
		long startTime = System.currentTimeMillis();
		String up_name=null, sName = null, policy_name = null;
		StringBuffer clisBuff = new StringBuffer();
		for(int index=1; index<=1; index++){
			up_name = "UP_"+index;
			sName = "SName_"+index;
			policy_name = "policy_"+index;
			clisBuff
//				.append("user-profile "+up_name+" qos-policy qos_test vlan-id 99 mobility-policy policy_test attribute 100").append("\n")
//				.append("user-profile "+up_name+" before up2 1").append("\n")
//				.append("user-profile "+up_name+" performance-sentinel enable").append("\n")
//				.append("user-profile "+up_name+" performance-sentinel action boost ").append("\n")
//				.append("user-profile "+up_name+" performance-sentinel guaranteed-bandwidth 100 ").append("\n")
//				.append("user-profile "+up_name+" l3-tunnel-action all ").append("\n")
//				.append("user-profile "+up_name+" tunnel-policy p_name ").append("\n")
//				.append("user-profile "+up_name+" security ip-policy from-access from_a  to-access to_b").append("\n")
//				.append("user-profile "+up_name+" security ip-policy from-air from_a  to-air to_b").append("\n")
//				.append("user-profile "+up_name+" ip-policy-default-action inter-station-traffic-drop ").append("\n")
//				.append("user-profile "+up_name+" security mac-policy  from-access from_mac  to-access to_mac  ").append("\n")
//				.append("user-profile "+up_name+" security mac-policy  from-air from_mac  to-air to_mac  ").append("\n")
//				.append("no user-profile "+up_name+" mac-policy-default-action deny ").append("\n")
//				.append("user-profile "+up_name+" schedule sdl_name ").append("\n")
//				.append("user-profile "+up_name+" deny-action-for-schedule  quarantine ").append("\n")
//				.append("ssid "+sName+" security wlan dos ssid-level frame-type assoc-req").append("\n")
//				.append("ssid "+sName+" security mac-filter mfName").append("\n")
//				.append("no ssid "+sName+" wmm").append("\n")
//				.append("ssid "+sName+" rts-threshold 100").append("\n")
//				.append("ssid "+sName+" user-group gName").append("\n")
//				.append("ssid "+sName+" qos-marker qmName").append("\n")
//				.append("ssid "+sName+" qos-classifier qName").append("\n")
//				.append("ssid "+sName+"").append("\n")
//				.append("ip-policy "+policy_name+"  id 100 before id 200 from 1.1.1.1 255.255.255.0  to 2.2.2.2 255.255.255.0  service ser_name  action permit log").append("\n")
//				.append("no security-object test user-profile-deny action ban 60 strict ").append("\n")
//				.append("capwap client server backup name 2.2.2.2  connect-delay 99 via-vpn-tunnel").append("\n")
//				.append("ip igmp snooping last-member-query-interval 99").append("\n")
//				.append("forwarding-engine static-rule test_rule action pass in-if eth1 src-oui 3388FF dst-mac aaBBdd:55ff99 out-if wifi0.1 rx-mac 1122-44ff-aa76").append("\n")
//				.append("interface eth1/5 spanning-tree mst-instance 5 priority 128 ").append("\n")
//				.append("interface eth1/9 spanning-tree mst-instance 6 path-cost 399 ").append("\n")
				.append("aaa radius-server name zhang server 192.168.1.1 shared-secret aerohive").append("\n")
				;
		}
		String[] clis = clisBuff.toString().split("\\n");
		System.out.println("CLI counts: " + clis.length);
		
//		try {
//			FileWriter fw = new FileWriter("D:\\zjie\\tmp\\up_clis.txt");
//			PrintWriter pw = new PrintWriter(fw);
//			pw.println(clisBuff.toString());
//			pw.flush();
//			pw.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		CLIParseFactory parseFactory = CLIParseFactory.getInstance();
		XmlMergeCache xmlCache = new XmlMergeCache();
		xmlCache.init();
		long initFinish = System.currentTimeMillis();
		
//		for (String cli : clis) {
//			long t1 = System.currentTimeMillis();
//			CLIParseResult parseRes = parseFactory.parseCli(cli);
////			parseTime += System.currentTimeMillis() - t1;
//			if(parseRes == null){
//				continue;
//			}
//			t1 = System.currentTimeMillis();
//			xmlCache.mergeElement(parseRes.getXmlNode());
////			mergeTime += System.currentTimeMillis() - t1;
//		}
		ConstraintType type = new ConstraintType();
		type.setPlatform("8");
		xmlCache = parseFactory.parseCli(clis, type);
		
//		parseFactory.parseCli(clis, xmlCache);
		
		long parseFinish = System.currentTimeMillis();

		String path = "D:\\zjie\\tmp\\test.xml";
		xmlCache.writeXml(path, true);
		
		long endTime = System.currentTimeMillis();
		System.out.println("------------------total: " + String.valueOf(endTime - startTime) + "ms");
		System.out.println("parse: " + String.valueOf(parseFinish - initFinish) + " ms");
		System.out.println("parse all: " + String.valueOf(endTime - initFinish) + " ms");
	}
}