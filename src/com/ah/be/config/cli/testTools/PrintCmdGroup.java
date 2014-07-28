package com.ah.be.config.cli.testTools;

import com.ah.be.config.cli.parse.CLIParseInstance;
import com.ah.be.config.cli.parse.CLIParseResult;

public class PrintCmdGroup {
	
	public static final String SPLIT_STRING = "	---------------------------------------------------------------------------";
	
	public static void getCliGroup(String[][] group){
		if(group == null){
			return;
		}
		for(int i=0; i<group.length; i++){
			System.out.println("{"+group[i][0]+"}:"+group[i][1]);
		}
	}

	public static void main(String[] args){
//		String cmd = "security-object <string> security protocol-suite wpa2-aes-psk {hex-key|ascii-key} <string> [ rekey-period <number> ] [ {non-strict|strict} ] [ gmk-rekey-period <number> ] [ ptk-timeout <number> ] [ ptk-retry <number> ] [ gtk-timeout <number> ] [ gtk-retry <number> ] [ ptk-rekey-period <number> ] ";
//		String cli = "security-object zhang security protocol-suite wpa2-aes-psk ascii-key 1111111 rekey-period 100 non-strict gmk-rekey-period 99 ptk-timeout 200 ptk-retry 300 gtk-timeout 400  gtk-retry 500 ptk-rekey-period 600";
		
		String cmd = "forwarding-engine static-rule <string> action pass in-if <ethx|aggx|redx> src-oui <oui> dst-mac <mac_addr> out-if <wifix.y> rx-mac <mac_addr>";
		String cli = "forwarding-engine static-rule test_rule action pass in-if eth1 src-oui 3388FF dst-mac aaBBdd:55ff99 out-if wifi0.1 rx-mac 1122-44ff-aa76";
		
		CLIParseInstance instance = new CLIParseInstance(cmd, null);
		instance.init();
		CLIParseResult parseRes = instance.parse(cli);
		
		System.out.println("Regex:"+SPLIT_STRING);
		System.out.println(parseRes.getRegex());
		
		System.out.println("Params Group:"+SPLIT_STRING);
		getCliGroup(parseRes.getGroup());
	}
}
