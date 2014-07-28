package com.ah.be.config.cli.testTools;

import com.ah.be.config.cli.util.ConstraintCheckUtil;
import com.ah.be.config.cli.xsdbean.ConstraintType;

public class ConstraintPerformanceTest {

	public static void main(String[] args){
		String version = "6.1.5.0";
		String express = ">= 6.1.3.0 || <=7.1.1.0";
		int runTimes = 1000;
		System.out.println(ConstraintCheckUtil.checkConstraint(version, express));
		
		long time_1 = System.currentTimeMillis();
		for(int i=0; i<runTimes; i++){
			ConstraintCheckUtil.checkConstraint(version, express);
		}
		System.out.println((System.currentTimeMillis() - time_1)+"ms");
		
		ConstraintType apCons = new ConstraintType();
		apCons.setVersion(version);
		ConstraintType expCons = new ConstraintType();
		expCons.setVersion(express);
		long time_2 = System.currentTimeMillis();
		for(int i=0; i<runTimes; i++){
			ConstraintCheckUtil.isMatch(apCons, expCons);
		}
		System.out.println((System.currentTimeMillis() - time_2)+"ms");
	}
}
