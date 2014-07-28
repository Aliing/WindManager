package com.ah.be.config.cli.brackets;

import java.util.List;

import com.ah.be.config.cli.generate.CLIGenerateException;

@SuppressWarnings("unused")
public class Test {

	public static void main(String[] args) throws CLIGenerateException{
		String testStr = "11111${4444}fgfsd1${hhhh}faadsf ${fd dafa} da ";
		List<DollarPlaceHolder> list = DollarPlaceHolder.getInstances(testStr);
		for(DollarPlaceHolder holder : list){
			System.out.println(holder);
		}
	}
	
	private static int getMacNumber(BracketCLIGenObj bObj, int index, List<Integer> resList){
		if(bObj == null){
			return index;
		}
		
		index++;
		
		System.out.println(index + "\t" + bObj.getContent());
		if(bObj.getBracketType() == BracketObj.ANGLE_BRACKET && 
				("mac_addr".equals(bObj.getContent()) || "oui".equals(bObj.getContent())) ){
			resList.add(index);
		}
		
		if(bObj.getChilds() != null){
			for(BracketCLIGenObj cldObj : bObj.getChilds()){
				index = getMacNumber(cldObj, index, resList);
			}
		}
		
		return index;
	}
	
}
