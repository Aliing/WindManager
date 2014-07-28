package com.ah.be.config.cli.generate.tools.classloader;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ah.be.config.create.source.impl.ConfigLazyQueryBo;
import com.ah.bo.HmBo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryUtil;

public class Test {

	public static void main(String[] args){
//		HmBo bo = QueryUtil.findBoById(ConfigTemplate.class, 3970L, new ConfigLazyQueryBo());
		HmBo bo = QueryUtil.findBoById(HiveAp.class, 3991L, new ConfigLazyQueryBo());
		ConfigBoContext.getInstance();
		long startTime = System.currentTimeMillis();
		System.out.println("---------------------------------------------");
		Map<Class<?>, List<Object>> resMap = ConfigBoContext.getInstance().getAllChildObj(bo);
		for(Entry<Class<?>, List<Object>> entry : resMap.entrySet()){
			System.out.println(entry.getKey().getName() + "\t\t" + entry.getValue().size());
		}
		System.out.println(System.currentTimeMillis() - startTime);
	}
}
