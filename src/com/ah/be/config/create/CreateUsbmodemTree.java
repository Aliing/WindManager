package com.ah.be.config.create;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.UsbmodemInt;
import com.ah.xml.be.config.ModemId;
import com.ah.xml.be.config.ModemMode;
import com.ah.xml.be.config.UsbmodemObj;
import com.ah.xml.be.config.UsbmodemPower;

public class CreateUsbmodemTree {
	
	private UsbmodemInt usbImpl;
	private GenerateXMLDebug oDebug;
	
	private UsbmodemObj usbmodem;
	
	private List<Object> usbChildList_1 = new ArrayList<Object>();

	public CreateUsbmodemTree(UsbmodemInt usbImpl, GenerateXMLDebug oDebug){
		this.usbImpl = usbImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		
		if(usbImpl.isConfigUsb()){
			usbmodem = new UsbmodemObj();
			generateUSBLevel_1();
		}
	}
	
	public UsbmodemObj getUsbmodemObj(){
		return this.usbmodem;
	}
	
	private void generateUSBLevel_1() throws IOException{
		
		/** element: <usbmodem>.<enable> */
		usbmodem.setEnable(CLICommonFunc.getAhOnlyAct(usbImpl.isEnableUsbmodem()));
		
		boolean usbmodemPowerEnable = usbImpl.isUsbmodemPowerEnable();
		/** element: <usbmodem>.<power> */
		UsbmodemPower usbmodemPower = new UsbmodemPower();
		usbmodemPower.setEnable(CLICommonFunc.getAhOnlyAct(usbmodemPowerEnable));
		usbmodem.setPower(usbmodemPower);
		
		if (usbmodemPowerEnable) {
			/** element: <usbmodem>.<mode> */
			ModemMode modeObj = new ModemMode();
			usbChildList_1.add(modeObj);
			usbmodem.setMode(modeObj);
			
			/** element: <usbmodem>.<modem-id> */
			for(int index=0; index<usbImpl.getUSBModemIdSize(); index++){
				if(usbImpl.isConfigUSBModem(index)){
					usbmodem.getModemId().add(createModemId(index));
				}
			}
			/** element: <usbmodem>.<network-mode> */
			usbmodem.setNetworkMode(CLICommonFunc.createAhStringActObj(usbImpl.getUsbmodemNetworkMode(), true));
		}
		
		generateUSBLevel_2();
	}
	
	private void generateUSBLevel_2(){
		/**
		 * <usbmodem>.<mode>						ModemMode
		 */
		for(Object childObj : usbChildList_1){
			
			/** element: <usbmodem>.<mode> */
			if(childObj instanceof ModemMode){
				ModemMode modeObj = (ModemMode)childObj;
				
				if(usbImpl.isConfigUsbModem(UsbmodemInt.UsbMode.primarywan)){
					/** element: <usbmodem>.<mode><primary-wan>*/
					modeObj.setPrimaryWan(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
				}else if(usbImpl.isConfigUsbModem(UsbmodemInt.UsbMode.alwaysconnected)){
					/** element: <usbmodem>.<mode>.<always-connected> */
					modeObj.setAlwaysConnected(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
				}else if(usbImpl.isConfigUsbModem(UsbmodemInt.UsbMode.ondemand)){
					/** element: <usbmodem>.<mode>.<on-demand>*/
					modeObj.setOnDemand(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
				}

				/** attribute: operation */
				modeObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
			}
		}
		usbChildList_1.clear();
	}
	
	private ModemId createModemId(int index) throws IOException{
		ModemId modemId = new ModemId();
		
		/** attribute: operation */
		modemId.setOperation(CLICommonFunc.getAhEnumShow(CLICommonFunc.getYesDefault()));
		
		/** attribute: name */
		modemId.setName(usbImpl.getModemId(index));
		
		/** element: <apn> */
		if(usbImpl.isConfigApn(index)){
			modemId.setApn(CLICommonFunc.createAhStringActQuoteProhibited(
					usbImpl.getApn(index), CLICommonFunc.getYesDefault(), CLICommonFunc.getYesDefault()));
		}
		
		/** element: <dialup-number> */
		if(usbImpl.isConfigDialupNumber(index)){
			modemId.setDialupNumber(CLICommonFunc.createAhStringActQuoteProhibited(
					usbImpl.getDialupNumber(index), CLICommonFunc.getYesDefault(), CLICommonFunc.getYesDefault()));
		}
		
		/** element: <dialup-username> */
		if(usbImpl.isConfigDialupUsername(index)){
			modemId.setDialupUsername(CLICommonFunc.createAhStringActQuoteProhibited(
					usbImpl.getDialupUsername(index), CLICommonFunc.getYesDefault(), CLICommonFunc.getYesDefault()));
		}
		
		/** element: <dialup-password> */
		if(usbImpl.isConfigDialupPassword(index)){
			modemId.setDialupPassword(CLICommonFunc.createAhEncryptedStringActQuoteProhibited(
					usbImpl.getDialupPassword(index), CLICommonFunc.getYesDefault(), CLICommonFunc.getYesDefault()));
		}
		
		return modemId;
	}
}
