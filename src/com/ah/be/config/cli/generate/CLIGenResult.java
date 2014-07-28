package com.ah.be.config.cli.generate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CLIGenResult implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<Object[]> params;
	
	private Object[][] keyParams;
	
	private List<String> clis;
	
	public CLIGenResult add(Object[] argParams){
		if(params == null){
			params = new ArrayList<>();
		}
		params.add(argParams);
		
		return this;
	}
	
	public CLIGenResult add(String cliKey, Object[] argParams){
		if(keyParams == null){
			keyParams = new Object[1][2];
		}else{
			keyParams = Arrays.copyOf(keyParams, keyParams.length+1);
			keyParams[keyParams.length-1] = new Object[2];
		}
		int length = keyParams.length;
		keyParams[length-1][0] = cliKey;
		keyParams[length-1][1] = argParams;
		
		return this;
	}
	
	public CLIGenResult add(String cli){
		if(clis == null){
			clis = new ArrayList<>();
		}
		clis.add(cli);
		
		return this;
	}

	public List<Object[]> getParams() {
		return params;
	}

	public Object[][] getKeyParams() {
		return keyParams;
	}

	public List<String> getClis() {
		return clis;
	}
}
