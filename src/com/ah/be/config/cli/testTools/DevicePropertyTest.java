package com.ah.be.config.cli.testTools;

import java.util.List;

import com.ah.be.parameter.device.DevicePropertyManage;

import edu.emory.mathcs.backport.java.util.Collections;

public class DevicePropertyTest {

	public static void main(String[] args){
		String keyStr = "spt_L7_service";
		List<Short> resList = DevicePropertyManage.getInstance().getSupportDeviceList(keyStr);
		Collections.sort(resList);
		
		StringBuffer printBuf = new StringBuffer();
		for(Short mod : resList){
			if(printBuf.length() > 0){
				printBuf.append("|");
			}
			printBuf.append(mod);
		}
		
		System.out.println(printBuf);
	}
}
