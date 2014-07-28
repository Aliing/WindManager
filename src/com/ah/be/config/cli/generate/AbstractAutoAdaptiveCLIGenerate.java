package com.ah.be.config.cli.generate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ah.be.config.cli.generate.CLIGenerateManager.CLIGenAnnotation;
import com.ah.be.config.cli.generate.CLIGenerateManager.CliGenParameter;
import com.ah.be.config.cli.util.CLIConfigFileLoader;
import com.ah.be.config.cli.util.CLIGenUtil;
import com.ah.be.config.cli.util.ConstraintCheckUtil;
import com.ah.be.config.cli.xsdbean.CliGenType;
import com.ah.be.config.cli.xsdbean.ConstraintType;
import com.ah.be.config.cli.xsdbean.DefaultValueType;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.Tracer;

public abstract class AbstractAutoAdaptiveCLIGenerate implements CLIGenerateAutoAdaptive, CLIKeyParam {
	
	private static final Tracer log = new Tracer(AbstractAutoAdaptiveCLIGenerate.class.getSimpleName());

	private List<String> allCliList = new ArrayList<String>();
	private ConstraintType constraintObj = new ConstraintType();
	protected HiveAp hiveAp;
	
	@Override
	public boolean isValid(){
		return true;
	}

	@Override
	public void setPlatform(short platform) {
		constraintObj.setPlatform(String.valueOf(platform));
	}
	
	@Override
	public void setVersion(String version) {
		constraintObj.setVersion(version);
	}
	
	@Override
	public void setType(short type) {
		constraintObj.setType(String.valueOf(type));
	}
	
	public void setHiveAp(HiveAp hiveAp){
		this.hiveAp = hiveAp;
		setPlatform(hiveAp.getHiveApModel());
		setVersion(hiveAp.getSoftVer());
		setType(hiveAp.getDeviceType());
	}

	protected String getCLI(String cliKey, Object[] params)throws CLIGenerateException {
		//get CLI Default value from configuration file.
		Object[] defaultArgs = this.getDefaultParams(getCliParams(cliKey), this.constraintObj, params);
		
		String resCli = CLIGenerateManager.getInstance().getCLI(cliKey, params, defaultArgs);
		
		return resCli;
	}
	
	private CliGenType getCliParams(String cliKey) {
		CliGenParameter cliParam = CLIGenerateManager.getInstance().getCliParams().get(cliKey);
		if(cliParam != null){
			return cliParam.getGenParam();
		}else{
			return null;
		}
	}
	
	private void addCliToList(String cliStr){
		if(!StringUtils.isEmpty(cliStr)){
			this.allCliList.add(cliStr);
		}
	}

	@Override
	public List<String> generateCLIs() throws CLIGenerateException {
		Class<? extends CLIGenerate> clazz = this.getClass();
		Object resObj = null;
		String cliKey = null, cliStr = null, errorMessage = null;
		CliGenType cliParam;
		
		allCliList.clear();
		List<CLIGenAnnotation> methodList = CLIGenerateManager.getInstance().getClassAnnotation(clazz);
		for (CLIGenAnnotation genAnno : methodList) {
			CLIConfig cliCfg = genAnno.getCliCfg();
			Method method = genAnno.getMethod();
			if(cliCfg == null || !whetherExecuteMethod(cliCfg) ){
				continue;
			}
			
			//get CLI define.
			cliKey = cliCfg.value();
			cliParam = getCliParams(cliKey);

			//invoke method
			try {
				resObj = method.invoke(this);
			} catch (Exception e) {
				errorMessage = "";
				if(cliParam != null){
					errorMessage += "Generate CLI "+cliParam.getCmd()+" failed.\n";
				}
				errorMessage += "Execute method failed, with class \""+clazz.getSimpleName()+"\", method \""+method.getName()+"\".";
				log.error(errorMessage, e);
				
				throw new CLIGenerateException(errorMessage);
			}
			
			//if method return null or return instance not CLIGenResult continue
			if(resObj == null || !(resObj instanceof CLIGenResult)){
				continue;
			}
			
			CLIGenResult cliGenRes = (CLIGenResult)resObj;
			//generate CLI from Object array.
			if(cliGenRes.getParams() != null && !StringUtils.isEmpty(cliKey)){
				for(Object parmObj : cliGenRes.getParams()){
					cliStr = getCLI(cliKey, (Object[])parmObj);
					this.addCliToList(cliStr);
				}
			}
			//generate CLI from key and Object array mapping.
			if(cliGenRes.getKeyParams() != null){
				String cKey;
				for(int index=0; index<cliGenRes.getKeyParams().length; index++){
					cKey = String.valueOf(cliGenRes.getKeyParams()[index][0]);
					if(this.isCLIValidForXml(cKey)){
						cliStr = getCLI(cKey, (Object[])cliGenRes.getKeyParams()[index][1] );
						this.addCliToList(cliStr);
					}
				}
			}
			//generate CLI from CLI text.
			if(cliGenRes.getClis() != null){
				for(String cli : cliGenRes.getClis()){
					this.addCliToList(cli);
				}
			}
		}
		return allCliList;
	}
	
	private boolean whetherExecuteMethod(CLIConfig cliCfg){
		//check constraint define in method annotation
		ConstraintType methodConstraint = new ConstraintType();
		methodConstraint.setPlatform(cliCfg.platform());
		methodConstraint.setType(cliCfg.type());
		methodConstraint.setVersion(cliCfg.version());
		methodConstraint.setExpression(CLIConfigFileLoader.getInstance().getConstraintExpression(cliCfg.expression()));
		if(!ConstraintCheckUtil.isMatch(this.constraintObj, methodConstraint)){
			return false;
		}
		
		//check constraint define in CLI configuration xml.
		return isCLIValidForXml(cliCfg.value());
	}
	
	private boolean isCLIValidForXml(String cliKey){
		if(StringUtils.isEmpty(cliKey)){
			return true;
		}
		
		//check constraint define in CLI configuration xml.
		CliGenType cliParam = getCliParams(cliKey);
		ConstraintType cliFileconstraint = cliParam != null ? cliParam.getConstraints() : null;
		return ConstraintCheckUtil.isMatch(this.constraintObj, cliFileconstraint);
	}
	
	private Object[] getDefaultParams(CliGenType cliParam, ConstraintType deviceCst, Object[] valueArgs){
		if(cliParam == null || cliParam.getCliDefault() == null || cliParam.getCliDefault().isEmpty()){
			return null;
		}
		
		//get all match default value arrays.
		List<DefaultValueType> constraintsMatchList = new ArrayList<>();
		for(DefaultValueType defObj : cliParam.getCliDefault()) {
			if(!ConstraintCheckUtil.isMatch(deviceCst, defObj.getConstraints())){
				continue;
			}
			constraintsMatchList.add(defObj);
		}
		if(constraintsMatchList.isEmpty()){
			//no match constraint
			return null;
		}
		
		String[] valueStrArgs = new String[valueArgs != null? valueArgs.length: 0];
		for(int i=0; i<valueStrArgs.length; i++){
			valueStrArgs[i] = valueArgs[i]==null? null : valueArgs[i].toString();
		}
		
		String[] premiseArgs = null;
		for(DefaultValueType defParam : constraintsMatchList){
			if(StringUtils.isEmpty(defParam.getDependence())){
				return CLIGenUtil.stringToArray(defParam.getValue());
			}else{
				premiseArgs = CLIGenUtil.stringToArray(defParam.getDependence());
			}
			
			if(this.isPremiseMatch(premiseArgs, valueStrArgs)){
				return CLIGenUtil.stringToArray(defParam.getValue());
			}
		}
		
		return null;
	}
	
	private boolean isPremiseMatch(String[] premiseArgs, String[] defArgs){
		if(premiseArgs == null || defArgs == null || premiseArgs.length != defArgs.length){
			return false;
		}
		
		for(int index=0; index<premiseArgs.length; index++){
			if("*".equals(premiseArgs[index])){
				continue;
			}
			if(premiseArgs[index] == defArgs[index] || 
					(premiseArgs[index] != null && premiseArgs[index].equalsIgnoreCase(defArgs[index])) ){
				continue;
			}
			return false;
		}
		
		return true;
	}
	
}