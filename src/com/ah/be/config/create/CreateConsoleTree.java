package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.ConsoleProfileInt;
import com.ah.xml.be.config.ConsoleObj;

/**
 * @author zhang
 * @version 2008-4-10 10:58:22
 */

public class CreateConsoleTree {
	
	private ConsoleObj consoleObj;
	private ConsoleProfileInt consoleImpl;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> consoleChildList_1 = new ArrayList<Object>();
	private List<Object> consoleChildList_2 = new ArrayList<Object>();

	public CreateConsoleTree(ConsoleProfileInt consoleImpl, GenerateXMLDebug oDebug){
		this.consoleImpl = consoleImpl;
		this.oDebug = oDebug;
	}
	
	public void generate(){
		
		/** element: <console> */
		oDebug.debug("/configuration", 
				"console", GenerateXMLDebug.CONFIG_ELEMENT,
				consoleImpl.getMgmtServiceGuiName(), consoleImpl.getMgmtServiceName());
		if(consoleImpl.isConfigConsoleTree()){
			consoleObj = new ConsoleObj();
			consoleChildList_1.add(consoleObj);
			generateConsoleLevel_1();
		}
	}
	
	public ConsoleObj getConsoleObj(){
		return this.consoleObj;
	}
	
	private void generateConsoleLevel_1(){
		/**
		 * <console>		ConsoleObj
		 */
		for(Object childObj : consoleChildList_1){
			
			/** element: <console> */
			if(childObj instanceof ConsoleObj){
				ConsoleObj consoleObj = (ConsoleObj)childObj;
				
				/** element: <console>.<serial-port> */
				ConsoleObj.SerialPort serialPortObj = new ConsoleObj.SerialPort();
				consoleChildList_2.add(serialPortObj);
				consoleObj.setSerialPort(serialPortObj);
			}
		}
		consoleChildList_1.clear();
		generateConsoleLevel_2();
	}
	
	private void generateConsoleLevel_2(){
		/**
		 * <console>.<serial-port>			ConsoleObj.SerialPort
		 */
		for(Object childObj : consoleChildList_2){
			
			/** element: <console>.<serial-port> */
			if(childObj instanceof ConsoleObj.SerialPort){
				ConsoleObj.SerialPort serialPortObj = (ConsoleObj.SerialPort)childObj;
				
				/** element: <console>.<serial-port>.<enable> */
				oDebug.debug("/configuration/console/serial-port", 
						"enable", GenerateXMLDebug.SET_OPERATION,
						consoleImpl.getMgmtServiceGuiName(), consoleImpl.getMgmtServiceName());
				serialPortObj.setEnable(CLICommonFunc.getAhOnlyAct(consoleImpl.isEnableSerialPort()));
			}
		}
		consoleChildList_2.clear();
	}
}
