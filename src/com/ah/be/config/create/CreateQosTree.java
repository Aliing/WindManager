package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.QosProfileInt;
import com.ah.xml.be.config.QosClassification;
import com.ah.xml.be.config.QosL3Police;
import com.ah.xml.be.config.QosL3PoliceInterface;
import com.ah.xml.be.config.QosL3PoliceMaxBw;
import com.ah.xml.be.config.QosL3PoliceSpecificInterface;
import com.ah.xml.be.config.QosMapOui;
import com.ah.xml.be.config.QosMapService;
import com.ah.xml.be.config.QosMapSsid;
import com.ah.xml.be.config.QosObj;
import com.ah.xml.be.config.UserDefinedMarkerMap;

/**
 * 
 * @author zhang
 *
 */
public class CreateQosTree {
	
	private QosProfileInt qosImp;
	private QosObj qosObj;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> qosChildLevel_1 = new ArrayList<Object>();
	
	private List<Object> qosChildLevel_2 = new ArrayList<Object>();
	
	public CreateQosTree(QosProfileInt qosImpl, GenerateXMLDebug oDebug) throws Exception {
		this.qosImp = qosImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		
		qosObj = new QosObj();
		generateQosLevel_1();
	}
	
	public QosObj getQosObj(){
		return this.qosObj;
	}
	
	private void generateQosLevel_1() throws Exception {
		/**
		 * <qos>		QosObj
		 */
		
		/** element: <qos>.<classifier-profile>.<enable> */
		qosObj.setEnable(CLICommonFunc.getAhOnlyAct(qosImp.isQosEnable()));
		
		if(qosImp.isQosEnable()){
			
			/** element: <qos>.<classifier-profile> */
			for(int i=0; i<qosImp.getQosClassifierSize(); i++ ){
				QosObj.ClassifierProfile classifiObj = new QosObj.ClassifierProfile();
				setClassifierProfile(classifiObj, i);
				qosObj.getClassifierProfile().add(classifiObj);
			}
			
			/** element: <qos>.<classifier-map> */
			QosObj.ClassifierMap classifiMapObj = new QosObj.ClassifierMap();
			qosChildLevel_1.add(classifiMapObj);
			qosObj.setClassifierMap(classifiMapObj);
			
			/** element: <qos>.<marker-profile> */
			for(int i=0; i<qosImp.getQosMarkerSize(); i++ ){
				QosObj.MarkerProfile markerProfileObj = new  QosObj.MarkerProfile();
				setMarkerProfile(markerProfileObj, i);
				qosObj.getMarkerProfile().add(markerProfileObj);
			}
			
			/** element: <qos>.<marker-map> */
			if(qosImp.isConfigureMarkerMap()){
				QosObj.MarkerMap markerMapObj = new QosObj.MarkerMap();
				qosChildLevel_1.add(markerMapObj);
				qosObj.setMarkerMap(markerMapObj);
			}
			
			/** element: <qos>.<policy> */
			for(int i=0; i<qosImp.getQosPolicySize(); i++ ){
				QosObj.Policy policyObj = new QosObj.Policy();
				setQosPolicy(policyObj, i);
				qosObj.getPolicy().add(policyObj);
			}
			
			/** element: <qos>.<airtime> */
			QosObj.Airtime airTimeObj = new QosObj.Airtime();
			qosChildLevel_1.add(airTimeObj);
			qosObj.setAirtime(airTimeObj);
			
			/** element: <qos>.<l3-police> */
			QosL3Police l3PoliceObj= new QosL3Police();
			qosChildLevel_1.add(l3PoliceObj);
			qosObj.setL3Police(l3PoliceObj);
			
		}
		
		generateQosLevel_2();
	}
	
	private void generateQosLevel_2() throws Exception {
		/**
		 * <qos>.<classifier-map>			QosObj.ClassifierMap
		 * <qos>.<marker-map>				QosObj.MarkerMap
		 * <qos>.<airtime>					QosObj.Airtime
		 * <qos>.<l3-police>				QosL3Police
		 */
		for(Object childObj : qosChildLevel_1 ){
			
			/** <qos>.<classifier-map> */
			if(childObj instanceof QosObj.ClassifierMap ){
				QosObj.ClassifierMap classMapObj = (QosObj.ClassifierMap)childObj;
				
				/** attribute: update */
//				classMapObj.setUpdateTime(qosImp.getQosClassUpdateTime());
				
				/** <qos>.<classifier-map>.<80211e> */
				for(int i=0; i<qosImp.getClassMap80211eSize(); i++ ){
					
					oDebug.debug("/configuration/qos/classifier-map",
							"_80211e", GenerateXMLDebug.SET_NAME,
							qosImp.getQosClassMapGuiName(), qosImp.getQosClassMapName());
					classMapObj.get80211E().add(
							CLICommonFunc.createAhNameActValueQuoteProhibited(qosImp.getClassMap80211eName(i), 
									CLICommonFunc.getYesDefault(), CLICommonFunc.getYesDefault())
					);
				}
				
				/** <qos>.<classifier-map>.<8021p> */
				for(int i=0; i<qosImp.getClassMap8021pSize(); i++ ){
					
					oDebug.debug("/configuration/qos/classifier-map",
							"_8021p", GenerateXMLDebug.SET_NAME,
							qosImp.getQosClassMapGuiName(), qosImp.getQosClassMapName());
					classMapObj.get8021P().add(
							CLICommonFunc.createAhNameActValueQuoteProhibited(qosImp.getClassMap8021pName(i), 
									CLICommonFunc.getYesDefault(), CLICommonFunc.getYesDefault())
					);
				}
				
				/** <qos>.<classifier-map>.<diffserv> */
				for(int i=0; i<qosImp.getClassMapDiffServSize(); i++){
					
					oDebug.debug("/configuration/qos/classifier-map",
							"diffserv", GenerateXMLDebug.SET_NAME,
							qosImp.getQosClassMapGuiName(), qosImp.getQosClassMapName());
					classMapObj.getDiffserv().add(
							CLICommonFunc.createAhNameActValueQuoteProhibited(qosImp.getClassMapDiffServName(i), 
									CLICommonFunc.getYesDefault(), CLICommonFunc.getYesDefault())
					);
				}
				
				/** <qos>.<classifier-map>.<oui> */
				for(int i=0; i<qosImp.getClassMapOuiSize(); i++ ){
					QosMapOui macOui = new QosMapOui();
					setOUIQosMapType(macOui, i);
					classMapObj.getOui().add(macOui);
				}
				
				/** <qos>.<classifier-map>.<ssid> */
				for(int i=0; i<qosImp.getClassMapSsidSize(); i++){
					classMapObj.getSsid().add(
							createQosMapSsid(qosImp.getClassMapSsidName(i), qosImp.getClassMapSsidLevel(i))
					);
				}
				/* move to new CLI generate implement.
				*//** <qos>.<classifier-map>.<service> *//*
				for(int i=0; i<qosImp.getClassMapServiceSize(); i++){
					QosMapService serviceObj = new QosMapService();
					setServiceQosMapType(serviceObj, i);
					classMapObj.getService().add(serviceObj);
				}
				*/
				/** <qos>.<classifier-map>.<interface> */
				for(int index=0; index<qosImp.getClassifierMapInterfaceSize(); index++){
					classMapObj.getInterface().add(this.createClassifierInterface(index));
				}
			}
			
			/** element: <qos>.<marker-map> */
			if(childObj instanceof QosObj.MarkerMap){
				QosObj.MarkerMap markerMapObj = (QosObj.MarkerMap)childObj;
				
				/** element: <qos>.<marker-map>.<8021p> */
				for(int i=0; i<qosImp.getMarkerMap8021pSize(); i++){
					
					oDebug.debug("/configuration/qos/marker-map",
							"_8021p", GenerateXMLDebug.SET_NAME,
							qosImp.getQosMarkerMapGuiName(), qosImp.getQosMarkerMapName());
					markerMapObj.get8021P().add(
							this.createQosClassification(qosImp.getMarkerMap8021pClass(i), qosImp.getMarkerMap8021pPriority(i))
					);
				}
				
				/** element: <qos>.<marker-map>.<diffserv> */
				for(int i=0; i<qosImp.getMarkerMapDiffServSize(); i++){
					
					oDebug.debug("/configuration/qos/marker-map",
							"diffserv", GenerateXMLDebug.SET_NAME,
							qosImp.getQosMarkerMapGuiName(), qosImp.getQosMarkerMapName());
					markerMapObj.getDiffserv().add(
							this.createQosClassification(qosImp.getMarkerMapDiffServClass(i), qosImp.getMarkerMapDiffServPriority(i))
					);
				}
				
				/** element: <qos>.<marker-map>.<_user-defined-8021p> */
				for(int i=0; i<qosImp.getUD8021pSize(); i++){
					markerMapObj.getUserDefined8021P().add(this.create8021pMarkerMap(i));
				}
				
				/** element: <qos>.<marker-map>.<_user-defined-diffserv> */
				for(int i=0; i<qosImp.getUDDiffservSize(); i++){
					markerMapObj.getUserDefinedDiffserv().add(this.createDiffservMarkerMap(i));
				}
				
			}
			
			/** element: <qos>.<airtime> */
			if(childObj instanceof QosObj.Airtime){
				QosObj.Airtime airTimeObj = (QosObj.Airtime)childObj;
				
				/** element: <qos>.<airtime>.<enable> */
				oDebug.debug("/configuration/qos/airtime",
						"enable", GenerateXMLDebug.SET_OPERATION,
						null, null);
				airTimeObj.setEnable(CLICommonFunc.getAhOnlyAct(qosImp.isEnableQosAirTime()));
			}
			
			/** element: <qos>.<l3-police> */
			if(childObj instanceof QosL3Police){
				QosL3Police l3PoliceObj = (QosL3Police)childObj;				
				QosL3PoliceInterface l3InterfaceObj=new QosL3PoliceInterface();
				qosChildLevel_2.add(l3InterfaceObj);
				l3PoliceObj.setInterface(l3InterfaceObj);
			}
		}
		qosChildLevel_1.clear();
		generateQosLevel_3();
	}
	
	private void generateQosLevel_3() throws Exception{
		/**
		 * <qos>.<l3-police>.<interface>			QosL3PoliceInterface
		 */
		
		for(Object childObj : qosChildLevel_2 ){
			
			/** element: <qos>.<l3-police>.<interface> */
			if(childObj instanceof QosL3PoliceInterface){
				QosL3PoliceInterface l3InterfaceObj = (QosL3PoliceInterface)childObj;
				
				/** element: <qos>.<l3-police>.<interface><eth0> */
				oDebug.debug("/configuration/qos/l3-police/interface",
						"eth0", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				l3InterfaceObj.setEth0(CreateL3PoliceInterface(qosImp.isEnableEth0LimitBandwidth(),
						qosImp.getQosEth0DownLoadRate(),qosImp.getQosEth0UploadRate(),"eth0",true));
				
				/** element: <qos>.<l3-police>.<interface><ppp0> */
				oDebug.debug("/configuration/qos/l3-police/interface",
						"ppp0", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				l3InterfaceObj.setPpp0(CreateL3PoliceInterface(qosImp.isEnableUSBLimitBandwidth(),
						qosImp.getQosUsbDownLoadRate(),qosImp.getQosUsbUploadRate(),"ppp0",true));
				
				
				/** element: <qos>.<l3-police>.<interface><tunnel0> */
				l3InterfaceObj.setTunnel0(CreateL3PoliceInterface(qosImp.isConfigTunnel(),qosImp.getQosEth0DownLoadRate(),
						qosImp.getQosEth0UploadRate(),"tunnel0",false));
				
				/** element: <qos>.<l3-police>.<interface><tunnel1> */
				l3InterfaceObj.setTunnel1(CreateL3PoliceInterface(qosImp.isConfigTunnel(),qosImp.getQosEth0DownLoadRate(),
						qosImp.getQosEth0UploadRate(),"tunnel1",false));
				
			}
		}
		qosChildLevel_2.clear();
	}
	
	private void setQosPolicy(QosObj.Policy policyObj, int i) throws Exception {
		/**
		 *  <qos>.<policy>				QosObj.Policy
		 */
		
		/** attribute: name */
		oDebug.debug("/configuration/qos",
				"policy", GenerateXMLDebug.SET_NAME,
				qosImp.getQosPolicyGuiName(), qosImp.getQosPolicyName(i));
		policyObj.setName(qosImp.getQosPolicyName(i));
		
		/** attribute: operation */
		policyObj.setOperation(CLICommonFunc.getAhEnumActValueShow(
				CLICommonFunc.getYesDefault(), qosImp.isQosPolicyDef(i)));
		
		/** element: <qos>.<policy>.<cr> */
		policyObj.setCr("");
		
		/** element: <qos>.<policy>.<user> */
		oDebug.debug("/configuration/qos/policy[@name='"+qosImp.getQosPolicyName(i)+"']",
				"user", GenerateXMLDebug.SET_VALUE,
				qosImp.getQosPolicyGuiName(), qosImp.getQosPolicyName(i));
		Object[][] userParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, qosImp.getQosPolicyUserLimit(i)}
		};
		policyObj.setUser(
				(QosObj.Policy.User)CLICommonFunc.createObjectWithName(QosObj.Policy.User.class, userParm)
		);
		
		/** element: <qos>.<policy>.<user-profile> */
		oDebug.debug("/configuration/qos/policy[@name='"+qosImp.getQosPolicyName(i)+"']",
				"user-profile", GenerateXMLDebug.SET_VALUE,
				qosImp.getQosPolicyGuiName(), qosImp.getQosPolicyName(i));
		policyObj.setUserProfile(
				CLICommonFunc.createAhStringQuoteProhibited(qosImp.getQosPolicyUserProfileValue(i), CLICommonFunc.getYesDefault())
		);
		
		/** element: <qos>.<policy>.<qos> */
		oDebug.debug("/configuration/qos/policy[@name='"+qosImp.getQosPolicyName(i)+"']",
				"qos", GenerateXMLDebug.CONFIG_ELEMENT,
				qosImp.getQosPolicyGuiName(), qosImp.getQosPolicyName(i));
		for(int j=0; j<qosImp.getQosPolicyQosSize(i); j++){
			QosObj.Policy.Qos policyQosObj = new QosObj.Policy.Qos();
			setQosPolicyQosParm(policyQosObj, i, j);
			policyObj.getQos().add(policyQosObj);
		}
		
	}
	
	private void setQosPolicyQosParm(QosObj.Policy.Qos policyQosObj, int i, int j){
		/**
		 * <qos>.<policy>.<qos>				QosObj.Policy.Qos
		 */
		
		/** attribute: name */
		oDebug.debug("/configuration/qos/policy[@name='"+qosImp.getQosPolicyName(i)+"']",
				"qos", GenerateXMLDebug.SET_NAME,
				qosImp.getQosPolicyGuiName(), qosImp.getQosPolicyName(i));
		policyQosObj.setName(qosImp.getQosPolicyQosName(i, j));
		
		/** element: <cr> */
		oDebug.debug("/configuration/qos/policy[@name='"+qosImp.getQosPolicyName(i)+"']/qos[@name='"+qosImp.getQosPolicyQosName(i, j)+"']",
				"cr", GenerateXMLDebug.SET_VALUE,
				qosImp.getQosPolicyGuiName(), qosImp.getQosPolicyName(i));
		policyQosObj.setCr(
				CLICommonFunc.createAhStringQuoteProhibited(qosImp.getQosPolicyQosCrValue(i, j), 
						CLICommonFunc.getYesDefault())
		);
		
//		/** element: <qos>.<policy>.<qos>.<strict> */
//		if(qosImp.isConfigureQosPolicyStrict(i, j) ){
//			policyQosObj.setStrict(
//					CLICommonFunc.createAhStringObj(qosImp.getQosPolicyTypeValue(i, j))
//			);
//		}
//		
//		/** element: <qos>.<policy>.<qos>.<wrr> */
//		if(qosImp.isConfigureQosPolicyWRR(i, j) ){
//			policyQosObj.setWrr(
//					CLICommonFunc.createAhStringObj(qosImp.getQosPolicyTypeValue(i, j))
//			);
//		}
	}
	
	private void setClassifierProfile(QosObj.ClassifierProfile classifiObj, int index){
		/***
		 * <qos>.<classifier-profile>		QosObj.ClassifierProfile
		 */
		
		/** attribute: name */
		oDebug.debug("/configuration/qos",
				"classifier-profile", GenerateXMLDebug.SET_NAME,
				qosImp.getClassAndMarkerGuiName(), qosImp.getQosClisifierProfileName(index));
		classifiObj.setName(qosImp.getQosClisifierProfileName(index));
		
		/** attribute: operation */
		classifiObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
//		/** attribute: update */
//		classifiObj.setUpdateTime(qosImp.getQosClassUpdateTime());
		
		/** element: <qos>.<classifier-profile>.<cr> */
		classifiObj.setCr("");
		
		/** element: <qos>.<classifier-profile>.<8021p> */
		oDebug.debug("/configuration/qos/classifier-profile[@name='"+qosImp.getQosClisifierProfileName(index)+"']",
				"_8021p", GenerateXMLDebug.CONFIG_ELEMENT,
				qosImp.getClassAndMarkerGuiName(), qosImp.getQosClisifierProfileName(index));
		if(qosImp.isConfigureClassifier8021p(index) ){
			classifiObj.set8021P(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		/** element: <qos>.<classifier-profile>.<80211e> */
		oDebug.debug("/configuration/qos/classifier-profile[@name='"+qosImp.getQosClisifierProfileName(index)+"']",
				"_80211e", GenerateXMLDebug.CONFIG_ELEMENT,
				qosImp.getClassAndMarkerGuiName(), qosImp.getQosClisifierProfileName(index));
		if(qosImp.isConfigureClassifier80211e(index) ){
			classifiObj.set80211E(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		/** element: <qos>.<classifier-profile>.<MAC> */
		oDebug.debug("/configuration/qos/classifier-profile[@name='"+qosImp.getQosClisifierProfileName(index)+"']",
				"MAC", GenerateXMLDebug.CONFIG_ELEMENT,
				qosImp.getClassAndMarkerGuiName(), qosImp.getQosClisifierProfileName(index));
		if(qosImp.isConfigureClassifierMac(index)){
			classifiObj.setMAC(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		/** element: <qos>.<classifier-profile>.<diffserv> */
		oDebug.debug("/configuration/qos/classifier-profile[@name='"+qosImp.getQosClisifierProfileName(index)+"']",
				"diffserv", GenerateXMLDebug.CONFIG_ELEMENT,
				qosImp.getClassAndMarkerGuiName(), qosImp.getQosClisifierProfileName(index));
		if(qosImp.isConfigureClassifierDiffserv(index)){
			classifiObj.setDiffserv(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		/** element: <qos>.<classifier-profile>.<interface-ssid> */
		oDebug.debug("/configuration/qos/classifier-profile[@name='"+qosImp.getQosClisifierProfileName(index)+"']",
				"interface-ssid", GenerateXMLDebug.CONFIG_ELEMENT,
				qosImp.getClassAndMarkerGuiName(), qosImp.getQosClisifierProfileName(index));
		if(qosImp.isConfigClassInterfaceSsid(index)){
			classifiObj.setInterfaceSsid(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		/** element: <qos>.<classifier-profile>.<service> */
		oDebug.debug("/configuration/qos/classifier-profile[@name='"+qosImp.getQosClisifierProfileName(index)+"']",
				"service", GenerateXMLDebug.CONFIG_ELEMENT,
				qosImp.getClassAndMarkerGuiName(), qosImp.getQosClisifierProfileName(index));
		if(qosImp.isConfigureClassifierService(index)){
			classifiObj.setService(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		/** element: <qos>.<classifier-profile>.<interface-ssid-only> */
		oDebug.debug("/configuration/qos/classifier-profile[@name='"+qosImp.getQosClisifierProfileName(index)+"']",
				"interface-ssid-only", GenerateXMLDebug.CONFIG_ELEMENT,
				qosImp.getClassAndMarkerGuiName(), qosImp.getQosClisifierProfileName(index));
		if(qosImp.isConfigClassInterfaceSsidOnly(index)){
			classifiObj.setInterfaceSsidOnly(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
	}
	
	private void setMarkerProfile(QosObj.MarkerProfile markerProfileObj, int index ){
		/**
		 * <qos>.<marker-profile>			QosObj.MarkerMap
		 */
		
		/** attribute: name */
		oDebug.debug("/configuration/qos",
				"marker-profile", GenerateXMLDebug.SET_NAME,
				qosImp.getClassAndMarkerGuiName(), qosImp.getQosMarkerProfileName(index));
		markerProfileObj.setName(qosImp.getQosMarkerProfileName(index));
		
		/** attribute: update */
		markerProfileObj.setUpdateTime(qosImp.getQosMarkerUpdateTime());
		
		/** attribute: operation */
		markerProfileObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <qos>.<marker-profile>.<cr> */
		markerProfileObj.setCr("");
		
		/** element: <qos>.<marker-profile>.<80211e> */
		oDebug.debug("/configuration/qos/marker-profile[@name='"+qosImp.getQosMarkerProfileName(index)+"']",
				"_80211e", GenerateXMLDebug.CONFIG_ELEMENT,
				qosImp.getClassAndMarkerGuiName(), qosImp.getQosMarkerProfileName(index));
		if(qosImp.isConfigureMarker80211e(index)){
			markerProfileObj.set80211E(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		/** element: <qos>.<marker-profile>.<8021p> */
		oDebug.debug("/configuration/qos/marker-profile[@name='"+qosImp.getQosMarkerProfileName(index)+"']",
				"_8021p", GenerateXMLDebug.CONFIG_ELEMENT,
				qosImp.getClassAndMarkerGuiName(), qosImp.getQosMarkerProfileName(index));
		if(qosImp.isConfigureMarker8021p(index) ){
			markerProfileObj.set8021P(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		/** element: <qos>.<marker-profile>.<diffserv> */
		oDebug.debug("/configuration/qos/marker-profile[@name='"+qosImp.getQosMarkerProfileName(index)+"']",
				"diffserv", GenerateXMLDebug.CONFIG_ELEMENT,
				qosImp.getClassAndMarkerGuiName(), qosImp.getQosMarkerProfileName(index));
		if(qosImp.isConfigureMarkerDiffServ(index) ){
			markerProfileObj.setDiffserv(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
	}
	
	private void setOUIQosMapType(QosMapOui macOui, int index) throws Exception {
		/**
		 * <qos>.<classifier-map>.<oui>				QosMapType
		 */
		
		/** attribute: operation */
		macOui.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** attribute: name */
		oDebug.debug("/configuration/qos/classifier-map",
				"oui", GenerateXMLDebug.SET_NAME,
				qosImp.getQosClassMapGuiName(), qosImp.getQosClassMapName());
		macOui.setName(qosImp.getClassMapOuiAddr(index));
		
		/** element: <qos>.<classifier-map>.<oui>.<qos> */
		oDebug.debug("/configuration/qos/classifier-map/oui[@name='"+qosImp.getClassMapOuiAddr(index)+"']",
				"qos", GenerateXMLDebug.SET_VALUE,
				qosImp.getQosClassMapGuiName(), qosImp.getQosClassMapName());
		Object[][] ouiQosParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, qosImp.getClassMapOuiQos(index)}
		};
		macOui.setQos(
				(QosMapOui.Qos)CLICommonFunc.createObjectWithName(QosMapOui.Qos.class, ouiQosParm)
		);
		
		/** element: <qos>.<classifier-map>.<oui>.<action> */
		QosMapOui.Action actionObj = new QosMapOui.Action();
		macOui.setAction(actionObj);
		//set QosMapType.Action
		{
			oDebug.debug("/configuration/qos/classifier-map/oui[@name='"+qosImp.getClassMapOuiAddr(index)+"']/action",
					"permit", GenerateXMLDebug.CONFIG_ELEMENT,
					qosImp.getQosClassMapGuiName(), qosImp.getQosClassMapName());
			if(qosImp.isConfigureClassOuiActionPermit(index)){
				actionObj.setPermit(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
			}
			
			oDebug.debug("/configuration/qos/classifier-map/oui[@name='"+qosImp.getClassMapOuiAddr(index)+"']/action",
					"deny", GenerateXMLDebug.CONFIG_ELEMENT,
					qosImp.getQosClassMapGuiName(), qosImp.getQosClassMapName());
			if(qosImp.isConfigureClassOuiActionDeny(index)){
				actionObj.setDeny(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
			}
			
			oDebug.debug("/configuration/qos/classifier-map/oui[@name='"+qosImp.getClassMapOuiAddr(index)+"']/action",
					"log", GenerateXMLDebug.CONFIG_ELEMENT,
					qosImp.getQosClassMapGuiName(), qosImp.getQosClassMapName());
			if(qosImp.isConfigureClassOuiActionLog(index)){
				actionObj.setLog("");
			}
		}
		
		/** element: <qos>.<classifier-map>.<oui>.<comment> */
		oDebug.debug("/configuration/qos/classifier-map/oui[@name='"+qosImp.getClassMapOuiAddr(index)+"']",
				"comment", GenerateXMLDebug.CONFIG_ELEMENT,
				qosImp.getQosClassMapGuiName(), qosImp.getQosClassMapName());
		if(qosImp.isConfigOuiComment(index)){
			
			oDebug.debug("/configuration/qos/classifier-map/oui[@name='"+qosImp.getClassMapOuiAddr(index)+"']",
					"comment", GenerateXMLDebug.SET_VALUE,
					qosImp.getQosClassMapGuiName(), qosImp.getQosClassMapName());
			macOui.setComment(CLICommonFunc.getAhString(qosImp.getOuiComment(index)));
		}
	}
	
	private void setServiceQosMapType(QosMapService serviceObj, int index) throws Exception{
		/**
		 * <qos>.<classifier-map>.<service>			QosMapType
		 */
		
		/** attribute: operation */
		serviceObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** attribute: name */
		oDebug.debug("/configuration/qos/classifier-map",
				"service", GenerateXMLDebug.SET_NAME,
				qosImp.getQosClassMapGuiName(), qosImp.getQosClassMapName());
		serviceObj.setName(qosImp.getClassMapServiceName(index));
		
		/** element: <qos>.<classifier-map>.<service>.<qos> */
		oDebug.debug("/configuration/qos/classifier-map/service[@name='"+qosImp.getClassMapServiceName(index)+"']",
				"qos", GenerateXMLDebug.SET_VALUE,
				qosImp.getQosClassMapGuiName(), qosImp.getQosClassMapName());
		Object[][] serviceQosParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, qosImp.getClassMapServiceQos(index)}
		};
		serviceObj.setQos(
				(QosMapService.Qos)CLICommonFunc.createObjectWithName(QosMapService.Qos.class, serviceQosParm)
		);
		
		/** element: <qos>.<classifier-map>.<service>.<action> */
		QosMapService.Action actionObj = new QosMapService.Action();
		serviceObj.setAction(actionObj);
		//set QosMapType.Action
		{
			/** element: <qos>.<classifier-map>.<service>.<action>.<permit> */
			oDebug.debug("/configuration/qos/classifier-map/service[@name='"+qosImp.getClassMapServiceName(index)+"']/action",
					"permit", GenerateXMLDebug.CONFIG_ELEMENT,
					qosImp.getQosClassMapGuiName(), qosImp.getQosClassMapName());
			if(qosImp.isConfigureClassMapActionPermit(index)){
				actionObj.setPermit(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
			}
			
			/** element: <qos>.<classifier-map>.<service>.<action>.<deny> */
			oDebug.debug("/configuration/qos/classifier-map/service[@name='"+qosImp.getClassMapServiceName(index)+"']/action",
					"deny", GenerateXMLDebug.CONFIG_ELEMENT,
					qosImp.getQosClassMapGuiName(), qosImp.getQosClassMapName());
			if(qosImp.isConfigureClassMapActionDeny(index)){
				actionObj.setDeny(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
			}
			
			/** element: <qos>.<classifier-map>.<service>.<action>.<log> */
			oDebug.debug("/configuration/qos/classifier-map/service[@name='"+qosImp.getClassMapServiceName(index)+"']/action",
					"log", GenerateXMLDebug.CONFIG_ELEMENT,
					qosImp.getQosClassMapGuiName(), qosImp.getQosClassMapName());
			if(qosImp.isConfigureClassMapActionLog(index)){
				actionObj.setLog("");
			}
		}
		
	}
	
	private QosMapSsid createQosMapSsid(String ssidName, int level) throws Exception{
		QosMapSsid qosSsidObj = new QosMapSsid();
		
		/** attribute: operation */
		qosSsidObj.setOperation(CLICommonFunc.getAhEnumShow(CLICommonFunc.getYesDefault()));
		
		/** attribute: name */
		oDebug.debug("/configuration/qos/classifier-map",
				"ssid", GenerateXMLDebug.SET_NAME,
				qosImp.getQosClassMapGuiName(), qosImp.getQosClassMapName());
		qosSsidObj.setName(ssidName);
		
		/** element: <cr> */
		oDebug.debug("/configuration/qos/classifier-map/ssid[@name='"+qosSsidObj.getName()+"']",
				"cr", GenerateXMLDebug.CONFIG_ELEMENT,
				qosImp.getQosClassMapGuiName(), qosImp.getQosClassMapName());
		
		if(qosImp.isConfigQosSsidLevel(level)){
			
			oDebug.debug("/configuration/qos/classifier-map/ssid[@name='"+qosSsidObj.getName()+"']",
					"cr", GenerateXMLDebug.SET_VALUE,
					qosImp.getQosClassMapGuiName(), qosImp.getQosClassMapName());
			Object[][] crParam = {
					{CLICommonFunc.ATTRIBUTE_VALUE, level},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			qosSsidObj.setCr((QosMapSsid.Cr)CLICommonFunc.createObjectWithName(QosMapSsid.Cr.class, crParam));
		}
		
		return qosSsidObj;
	}
	
	private QosL3PoliceSpecificInterface CreateL3PoliceInterface(boolean enable,int maxDownRate,
			int maxUpRate, String interfaceName,boolean isLoadBW) throws Exception{
		QosL3PoliceSpecificInterface qosinterfaceObj=new QosL3PoliceSpecificInterface();
		/** attribute: enable */
		qosinterfaceObj.setEnable(CLICommonFunc.getAhOnlyAct(enable));
		if(enable && isLoadBW){
			oDebug.debug("/configuration/qos/l3-police/interface/'"+interfaceName+"'",
					"max-download-bw", GenerateXMLDebug.SET_VALUE,
					null,null);
			Object[][] downloadParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, maxDownRate },
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			qosinterfaceObj.setMaxDownloadBw((QosL3PoliceMaxBw)
					CLICommonFunc.createObjectWithName(QosL3PoliceMaxBw.class, downloadParm)
			);
			
			oDebug.debug("/configuration/qos/l3-police/interface/'"+interfaceName+"'",
					"max-upload-bw", GenerateXMLDebug.SET_VALUE,
					null,null);
			Object[][] uploadParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, maxUpRate },
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			qosinterfaceObj.setMaxUploadBw((QosL3PoliceMaxBw)
					CLICommonFunc.createObjectWithName(QosL3PoliceMaxBw.class, uploadParm)
			);
		}
		return qosinterfaceObj;
	}
	
	private QosObj.ClassifierMap.Interface createClassifierInterface(int index){
		QosObj.ClassifierMap.Interface infObj = new QosObj.ClassifierMap.Interface();
		
		/** atttribute: operation */
		infObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** atttribute: name */
		infObj.setName(qosImp.getClassifierMapInterfaceName(index));
		
		/** element: <cr> */
		infObj.setCr(CLICommonFunc.getAhInt(qosImp.getClassifierMapInterfacePriority(index)));
		
		return infObj;
	}
	
	private UserDefinedMarkerMap create8021pMarkerMap(int i){
		UserDefinedMarkerMap definedMap = new UserDefinedMarkerMap();
		
		/** attribute: name */
		definedMap.setName(qosImp.getUD8021pName(i));
		
		/** attribute: operation */
		definedMap.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <cr> */
		for(int j=0; j<qosImp.getUD8021pContentSize(i); j++){
			definedMap.getCr().add(this.createQosClassification(
					qosImp.getUD8021pContentClass(i, j), qosImp.getUD8021pContentPriority(i, j)));
		}
		
		return definedMap;
	}
	
	private UserDefinedMarkerMap createDiffservMarkerMap(int i){
		UserDefinedMarkerMap definedMap = new UserDefinedMarkerMap();
		
		/** attribute: name */
		definedMap.setName(qosImp.getUDDiffservName(i));
		
		/** attribute: operation */
		definedMap.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <cr> */
		for(int j=0; j<qosImp.getUDDiffservContentSize(i); j++){
			definedMap.getCr().add(this.createQosClassification(
					qosImp.getUDDiffservContentClass(i, j), qosImp.getUDDiffservContentPriority(i, j)));
		}
		
		return definedMap;
	}
	
	private QosClassification createQosClassification(int qosClass, int qosPriority){
		QosClassification resQos = new QosClassification();
		
		/** attribute: name */
		resQos.setName(qosClass);
		
		/** attribute: operation */
		resQos.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <cr> */
		resQos.setCr(CLICommonFunc.getAhInt(qosPriority));
		
		return resQos;
	}
}
