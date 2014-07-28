package com.ah.be.config.cli.generate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.config.cli.brackets.BracketCLIGenObj;
import com.ah.be.config.cli.brackets.CLIBracketUtil;
import com.ah.be.config.cli.generate.tools.classloader.AhClassFilterCallBack;
import com.ah.be.config.cli.generate.tools.classloader.AhClassUtils;
import com.ah.be.config.cli.util.CLIConfigFileLoader;
import com.ah.be.config.cli.util.CLIGenUtil;
import com.ah.be.config.cli.xsdbean.CliGenType;
import com.ah.be.config.cli.xsdbean.Clis;

public class CLIGenerateManager {

	private static CLIGenerateManager instance;

	private Map<String, CliGenParameter> cliParams = new HashMap<>();
	private Map<Class<? extends CLIGenerate>, List<CLIGenAnnotation>> cliAnnoMap = new HashMap<>();
	private List<Class<?>> allClassesWithoutParam;
	private Map<Constructor<?>, Class<?>> constructorMap;

	private CLIGenerateManager() {}

	public static CLIGenerateManager getInstance() {
		synchronized(CLIGenerateManager.class){
			if (instance == null) {
				instance = new CLIGenerateManager();
				instance.init();
			}
			return instance;
		}
	}
	
	private void init() {
		//load xml
		Clis clisObj = CLIConfigFileLoader.getInstance().getCLIConfig();
		if(clisObj == null || clisObj.getCliGen() == null){
			return;
		}
		
		//load all child node
		for(CliGenType cliGen : clisObj.getCliGen()){
			cliParams.put(cliGen.getKey(), new CliGenParameter(cliGen));
//			printTest(cliGen);
		}
		
		allClassesWithoutParam = AhClassUtils.getClassesByPackageName("com.ah.be.config.cli.generate.impl", 
				new AhClassFilterCallBack(){

					@Override
					public boolean isValid(Class<?> clazz) {
						//class extends from CLIGenerateAutoAdaptive
						if(!AbstractAutoAdaptiveCLIGenerate.class.isAssignableFrom(clazz)){
							return false;
						}else if(clazz.getAnnotation(Deprecated.class) != null){
							return false;
						}
						
						//get constructor without parameters;
						Constructor<?> constructorNoParams = null;
						try{
							constructorNoParams = clazz.getDeclaredConstructor(new Class<?>[]{});
						}catch(Exception e){
							constructorNoParams = null;
						}
						
						return constructorNoParams != null;
					}
				});
		
		List<Class<?>> allClassesWithOneParam = AhClassUtils.getClassesByPackageName("com.ah.be.config.cli.generate.impl", 
				new AhClassFilterCallBack(){

			@Override
			public boolean isValid(Class<?> clazz) {
				//class extends from CLIGenerateAutoAdaptive
				if(!AbstractAutoAdaptiveCLIGenerate.class.isAssignableFrom(clazz)){
					return false;
				}else if(clazz.getAnnotation(Deprecated.class) != null){
					return false;
				}
				
				//get constructor without parameters;
				Constructor<?>[] constructors = null;
				try{
					constructors = clazz.getConstructors();
					for(Constructor<?> cst : constructors){
						if(cst.getParameterTypes().length == 1){
							return true;
						}
					}
				}catch(Exception e){
					
				}
				
				return false;
			}
		});
		if(allClassesWithOneParam != null && !allClassesWithOneParam.isEmpty()){
			constructorMap = new HashMap<>();
			for(Class<?> clazz : allClassesWithOneParam){
				Constructor<?>[] constructors = clazz.getConstructors();
				for(Constructor<?> cst : constructors){
					if(cst.getParameterTypes().length == 1){
						constructorMap.put(cst, cst.getParameterTypes()[0]);
						break;
					}
				}
			}
		}
	}
	
	public String getCLI(String key, Object[] paramArg, Object... defValueArg) throws CLIGenerateException {
		String message = null;
		// key is null.
		if (key == null) {
			message = "Parameter key cannot null.";
			throw new CLIGenerateException(message);
		}
		
		CliGenParameter cliParam = cliParams.get(key);
		if (cliParam == null) {
			message = "Cannot find CLI template by key \"" + key + "\"";
			throw new CLIGenerateException(message);
		}
		// cannot find cli from key.
		String cliTemplate = cliParam.getGenParam().getCmd();
		
		//generate cli
		BracketCLIGenObj bracketObj = cliParam.getBracketObj();
		bracketObj = bracketObj.clone();
		bracketObj.init();
		bracketObj.fillParams(CLIGenUtil.getCLIParamsQueue(paramArg), false);
		if(defValueArg != null && defValueArg.length > 0){
			bracketObj.fillParams(CLIGenUtil.getCLIParamsQueue(defValueArg), true);
		}
		
		String cliRes = bracketObj.getCLI();
		if(cliRes != null){
			cliRes = CLIGenUtil.mergeCLIBlanks(cliRes.trim());
		}else{
			message = "CLI params not matcher, template:\"" + cliTemplate
					+ "\" " + "params: "
					+ CLIGenUtil.arrayToString(paramArg);
			throw new CLIGenerateException(message);
		}
		
		return cliRes;
	}
	
	public List<CLIGenAnnotation> getClassAnnotation(Class<? extends CLIGenerate> genClass){
		if(!cliAnnoMap.containsKey(genClass)){
			List<CLIGenAnnotation> resList = new ArrayList<>();
			for (Method method : genClass.getMethods()){
				CLIConfig cliCfg = method.getAnnotation(CLIConfig.class);
				if(cliCfg != null){
					resList.add(new CLIGenAnnotation(method, cliCfg));
				}
			}
			cliAnnoMap.put(genClass, resList);
		}
		return cliAnnoMap.get(genClass);
	}
	
	public List<Class<?>> getAllClassesWithoutParam(){
		return allClassesWithoutParam;
	}
	
	public Map<Constructor<?>, Class<?>> getConstructorMap(){
		return constructorMap;
	}

	public String getCLIFormat(String cliKey) {
		CliGenParameter paramObj = cliParams.get(cliKey);
		return paramObj != null ? paramObj.getGenParam().getCmd() : null;
	}

	public Map<String, CliGenParameter> getCliParams() {
		return cliParams;
	}
	
	public BracketCLIGenObj getBracketCLIGenObj(String cliKey){
		return cliParams.get(cliKey).getBracketObj();
	}
	
	public static class CliGenParameter {
		
		private CliGenType genParam;
		private BracketCLIGenObj bracketObj;
		
		public CliGenParameter(CliGenType genParam){
			this.genParam = genParam;
			init();
		}
		
		private void init(){
			String cmdStr = genParam.getCmd();
			cmdStr = "[" + cmdStr + "]";
			bracketObj = CLIBracketUtil.getFirstBracket(BracketCLIGenObj.class, cmdStr);
			bracketObj.init();
		}
		
		public CliGenType getGenParam() {
			return genParam;
		}
		public void setGenParam(CliGenType genParam) {
			this.genParam = genParam;
		}
		
		public BracketCLIGenObj getBracketObj() {
			return bracketObj;
		}
		public void setBracketObj(BracketCLIGenObj bracketObj) {
			this.bracketObj = bracketObj;
		}
	}
	
	public static class CLIGenAnnotation {
		private Method method;
		private CLIConfig cliCfg;
		
		public CLIGenAnnotation(Method method, CLIConfig cliCfg){
			this.method = method;
			this.cliCfg = cliCfg;
		}
		
		public Method getMethod() {
			return method;
		}
		public CLIConfig getCliCfg() {
			return cliCfg;
		}
	}
}
