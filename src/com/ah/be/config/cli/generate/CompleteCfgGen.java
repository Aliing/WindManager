package com.ah.be.config.cli.generate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.util.Log;

import com.ah.be.config.cli.generate.impl.RadioProfileImpl;
import com.ah.be.config.cli.generate.impl.SecurityObjectImpl_Port;
import com.ah.be.config.cli.generate.impl.SecurityObjectImpl_Ssid;
import com.ah.be.config.cli.generate.impl.ServiceImpl;
import com.ah.be.config.cli.generate.tools.classloader.ConfigBoContext;
import com.ah.be.config.cli.util.ConstraintCheckUtil;
import com.ah.be.config.cli.xsdbean.ConstraintType;
import com.ah.be.config.create.source.SecurityObjectProfileInt;
import com.ah.be.config.create.source.impl.ConfigureProfileFunction;
import com.ah.be.config.create.source.impl.SecurityObjectProfileImpl;
import com.ah.be.config.create.source.impl.ServiceProfileImpl;
import com.ah.be.config.create.source.impl.sw.SecurityObjectSwitchImpl;
import com.ah.bo.hiveap.DeviceInfo;
import com.ah.bo.hiveap.HiveAp;

public class CompleteCfgGen {
	
	private List<CLIGenerate> allGenInstance = new ArrayList<CLIGenerate>();
	private ConstraintType deviceConstraint;
	private ConfigureProfileFunction profileFunction;
	private HiveAp hiveAp;
	
	public CompleteCfgGen(String... deviceArgs){
		deviceConstraint = new ConstraintType();
		if(deviceArgs.length > 0){
			deviceConstraint.setPlatform(deviceArgs[0]);
		}
		if(deviceArgs.length > 1){
			deviceConstraint.setType(deviceArgs[1]);
		}
		if(deviceArgs.length > 2){
			deviceConstraint.setVersion(deviceArgs[2]);
		}
		
		loadAllImpl();
	}
	
	public CompleteCfgGen(ConfigureProfileFunction profileFunction){
		this.profileFunction = profileFunction;
		this.hiveAp = profileFunction.getHiveAp();
		
		deviceConstraint = new ConstraintType();
		deviceConstraint.setPlatform(String.valueOf(hiveAp.getHiveApModel()));
		deviceConstraint.setType(String.valueOf(hiveAp.getDeviceType()));
		deviceConstraint.setVersion(hiveAp.getSoftVer());
		
		loadAllImpl();
	}
	
	public List<String> generateAllCLIs() throws CLIGenerateException{
		List<String> resList = new ArrayList<>();
		if(allGenInstance == null || allGenInstance.isEmpty()){
			return resList;
		}
		
		List<String> cliRes = null;
		for(CLIGenerate cliGen : allGenInstance){
			//check class level annotation
			CLIConfig classAnn = cliGen.getClass().getAnnotation(CLIConfig.class);
			if(classAnn != null){
				ConstraintType classCons = new ConstraintType();
				classCons.setPlatform(classAnn.platform());
				classCons.setType(classAnn.type());
				classCons.setVersion(classAnn.version());
				if(!ConstraintCheckUtil.isMatch(deviceConstraint, classCons)){
					continue;
				}
			}
			
			//set device platform, type, version.
			if(cliGen instanceof AbstractAutoAdaptiveCLIGenerate){
				((AbstractAutoAdaptiveCLIGenerate) cliGen).setHiveAp(this.hiveAp);
			}else if(cliGen instanceof CLIGenerateAutoAdaptive){
				((CLIGenerateAutoAdaptive) cliGen).setPlatform(Short.valueOf(deviceConstraint.getPlatform()));
				((CLIGenerateAutoAdaptive) cliGen).setType(Short.valueOf(deviceConstraint.getType()));
				((CLIGenerateAutoAdaptive) cliGen).setVersion(deviceConstraint.getVersion());
			}
			
			//generate all CLIs.
			cliGen.init();
			if(!cliGen.isValid()){
				continue;
			}
			cliRes = cliGen.generateCLIs();
			if(cliRes != null){
				resList.addAll(cliRes);
			}
		}
		
		return resList;
	}
	
	public ConstraintType getDeviceConstraint(){
		return this.deviceConstraint;
	}
	
	private void loadAllImpl(){
		//all instance extend from CLIGenerateAutoAdaptiveAbstract and with empty parameter constructor will be load auto.
		loadCLIGenerateInstanceAuto();
		
		//all instance extend from CLIGenerateAutoAdaptiveAbstract and with only one parameter constructor will be load auto.
		loadCLIGenerateWithConstructor();
		
//		loadSecurityObjectImpl();
//		loadNetworkService();
//		loadRadioProfile();
	}
	
	private void loadCLIGenerateInstanceAuto(){
		for(Class<?> clazzClass : CLIGenerateManager.getInstance().getAllClassesWithoutParam()){
			try{
				allGenInstance.add((AbstractAutoAdaptiveCLIGenerate)(clazzClass.newInstance()));
			}catch(Exception e){
				
			}
		}
	}
	
	private void loadCLIGenerateWithConstructor(){
		Map<Class<?>, List<Object>> resourceMap = ConfigBoContext.getInstance().getAllChildObj(this.hiveAp);
		
		Map<Constructor<?>, Class<?>> constructorMap = CLIGenerateManager.getInstance().getConstructorMap();
		Iterator<Entry<Constructor<?>, Class<?>>> entrySet = constructorMap.entrySet().iterator();
		while(entrySet.hasNext()){
			Entry<Constructor<?>, Class<?>> entry = entrySet.next();
			Constructor<?> cst = entry.getKey();
			Class<?> paramClass = entry.getValue();
			List<Object> objList = resourceMap.get(paramClass);
			if(objList == null || objList.isEmpty()){
				continue;
			}
			for(Object paramObj : objList){
				try {
					allGenInstance.add((AbstractAutoAdaptiveCLIGenerate)(cst.newInstance(paramObj)));
				} catch (InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException e) {
					Log.error("New constructor "+cst.getName()+" failed", e);
				}
			}
		}
	}
	
	private void loadSecurityObjectImpl(){
		List<SecurityObjectProfileInt> securityList = this.profileFunction.getSecurityObjList();
		if(securityList != null){
			for(SecurityObjectProfileInt secImpl : securityList){
				if(secImpl instanceof SecurityObjectProfileImpl){
					allGenInstance.add(
							new SecurityObjectImpl_Ssid(((SecurityObjectProfileImpl)secImpl).getSsidProfile())
					);
				}else if(secImpl instanceof SecurityObjectSwitchImpl){
					allGenInstance.add(
							new SecurityObjectImpl_Port(((SecurityObjectSwitchImpl)secImpl).getAccessProfile())
					);
				}
			}
		}
	}
	
	private void loadNetworkService(){
		List<ServiceProfileImpl> serviceList = this.profileFunction.getServiceProfileImplList();
		if(serviceList == null || serviceList.isEmpty()){
			return;
		}
		
		for(ServiceProfileImpl serviceImpl : serviceList){
			if(serviceImpl.getNetworkService() != null && !serviceImpl.getNetworkService().isCliDefaultFlag()){
				allGenInstance.add(new ServiceImpl(serviceImpl.getNetworkService()));
			}
		}
	}
	
	private void loadRadioProfile(){
		if(hiveAp.getDeviceInfo().getIntegerValue(DeviceInfo.SPT_RADIO_COUNTS) <= 0){
			return;
		}
		
		if(hiveAp.getWifi0RadioProfile() != null && !hiveAp.getWifi0RadioProfile().isCliDefaultFlag()){
			allGenInstance.add(new RadioProfileImpl(hiveAp.getWifi0RadioProfile()));
		}
		if(hiveAp.getWifi1RadioProfile() != null && !hiveAp.getWifi1RadioProfile().isCliDefaultFlag()){
			allGenInstance.add(new RadioProfileImpl(hiveAp.getWifi1RadioProfile()));
		}
	}
	
}
