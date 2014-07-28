package com.ah.be.config.create.common;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ah.be.config.cli.xsdbean.ConstraintType;
import com.ah.bo.hiveap.HiveAp;

public class CLICommonUtils {

	public static ConstraintType getConstraintType(HiveAp hiveAp){
		if(hiveAp == null){
			return null;
		}
		
		return getConstraintType(hiveAp.getHiveApModel(), hiveAp.getDeviceType(), hiveAp.getSoftVer());
	}
	
	public static ConstraintType getConstraintType(short hiveApModel, short deviceType, String softVer){
		ConstraintType resType = new ConstraintType();
		resType.setPlatform(String.valueOf(hiveApModel));
		resType.setType(String.valueOf(deviceType));
		resType.setVersion(softVer);
		return resType;
	}
	
	public static List<String> readCLIList(String filePath){
		//read CLIs that cannot parse with C parse model.
		List<String> cParseThrowClis = new ArrayList<String>();
		FileReader reader = null;
		BufferedReader bReader = null;
		try{
			reader = new FileReader(filePath);
			bReader = new BufferedReader(reader);
			String lineStr = null;
			while((lineStr = bReader.readLine()) != null){
				if(!StringUtils.isEmpty(lineStr)){
					cParseThrowClis.add(lineStr);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(bReader != null){
				try {
					bReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return cParseThrowClis;
	}
}
