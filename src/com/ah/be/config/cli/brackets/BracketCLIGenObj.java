package com.ah.be.config.cli.brackets;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.ah.be.config.cli.generate.CLIGenerateException;
import com.ah.be.config.cli.util.CLIGenUtil;


public class BracketCLIGenObj extends BracketObj {
	private static final long serialVersionUID = 1L;
	
	protected List<BracketCLIGenObj> childs;
	
	protected String[][] paramArgs;
	
	protected String value;
	
	protected String defaultValue;
	
	public BracketCLIGenObj() {
		super();
	}
	
	public BracketCLIGenObj(char c) {
		super(c);
	}
	
	@Override
	public void init(){
		initCLIParams();
		generateChilds();
	}
	
	@Override
	public BracketCLIGenObj clone() {
		BracketCLIGenObj cloneObj = (BracketCLIGenObj)super.clone();
		if(cloneObj.getChilds() != null){
			List<BracketCLIGenObj> cList = new ArrayList<>();
			for(BracketCLIGenObj item : cloneObj.getChilds()){
				cList.add(item.clone());
			}
			cloneObj.setChilds(cList);
		}
		return cloneObj;
	}
	
	//get HM CLI
	public String getCLI() throws CLIGenerateException{
		boolean allNull = this.isAllNull(), allDefault = this.isAllDefault();
		
		if(allNull && this.getBracketType() != MIDDLE_BRACKET){
			throw new CLIGenerateException("CLI arguments invalid.");
		}
		
		if(this.getBracketType() == MIDDLE_BRACKET && (allNull || allDefault) ){
			return "";
		}
		
		if(this.getChilds() == null || this.getChilds().isEmpty()){
			String valueStr = this.getValue();
			if(CLIGenUtil.isExistsCLISpecialChar(valueStr)){
				valueStr = "\"" + valueStr + "\"";
			}
			return valueStr;
		}
		
		String contentStr = this.getContent();
		int startIndex=0;
		StringBuffer resBuff = new StringBuffer();
		for(BracketCLIGenObj childObj : this.getChilds()){
			resBuff.append(contentStr.substring(startIndex, childObj.getOpenIndex()))
				.append(childObj.getCLI());
			startIndex = childObj.getCloseIndex() + 1;
		}
		resBuff.append(contentStr.substring(startIndex));
		
		return resBuff.toString();
	}
	
	//fill CLI arguments or CLI default arguments
	public void fillParams(Queue<String> paramQueue, boolean setDefValue){
		if(paramQueue == null || paramQueue.isEmpty()){
			return;
		}
		
		String paramStr = null;
		if(this.getBracketType() == ANGLE_BRACKET){
			paramStr = paramQueue.poll();
			if(setDefValue){
				this.setDefaultValue(paramStr);
			}else{
				this.setValue(paramStr);
			}
		}else if(this.getBracketType() == BIG_BRACKET){
			paramStr = paramQueue.poll();
			for(int i=0; i<paramArgs.length; i++){
				if(paramArgs[i][0].equalsIgnoreCase(paramStr)){
					paramStr = paramArgs[i][1];
					break;
				}
			}
			if(setDefValue){
				this.setDefaultValue(paramStr);
			}else{
				this.setValue(paramStr);
			}
		}
		
		if(this.getChilds() == null){
			return;
		}
		for(BracketCLIGenObj child : this.getChilds()){
			child.fillParams(paramQueue, setDefValue);
		}
	}
	
	public boolean isAllNull(){
		if(this.getChilds() != null){
			for(BracketCLIGenObj childObj : this.getChilds()){
				if(!childObj.isAllNull()){
					return false;
				}
			}
		}
		
		if(this.getBracketType() == ANGLE_BRACKET || this.getBracketType() == BIG_BRACKET){
			return this.getValue() == null;
		}
		
		return true;
	}
	
	public boolean isAllDefault(){
		if(this.getChilds() != null){
			for(BracketCLIGenObj childObj : this.getChilds()){
				if(!childObj.isAllDefault()){
					return false;
				}
			}
		}
		
		if(this.getBracketType() == ANGLE_BRACKET || this.getBracketType() == BIG_BRACKET){
			if(this.getDefaultValue() == null){
				return false;
			}else if("*".equals(this.getDefaultValue())){
				return true;
			}else{
				return this.getValue() != null && this.getValue().equals(this.getDefaultValue());
			}
		}
		
		return true;
	}
	
	private void initCLIParams(){
		if(this.bracketType == BIG_BRACKET){
			paramArgs = CLIBracketUtil.getCLIParamsArgs(this.getContent());
		}
	}
	
	private void generateChilds(){
		List<BracketCLIGenObj> childs = CLIBracketUtil.getBracketList(BracketCLIGenObj.class, this.getContent(), false);
		if(childs == null || childs.isEmpty()){
			return;
		}
		
		for(BracketCLIGenObj item : childs){
			item.init();
		}
		this.setChilds(childs);
	}
	
	public List<BracketCLIGenObj> getChilds() {
		return childs;
	}

	public void setChilds(List<BracketCLIGenObj> childs) {
		this.childs = childs;
	}

	public String[][] getParamArgs() {
		return paramArgs;
	}

	public void setParamArgs(String[][] paramArgs) {
		this.paramArgs = paramArgs;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
}
