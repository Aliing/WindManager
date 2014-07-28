package com.ah.be.config.create;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.AAAProfileInt;
import com.ah.be.config.util.AhConfigUtil;
import com.ah.xml.be.config.AaaAcctPort;
import com.ah.xml.be.config.AaaAttrOperatorName;
import com.ah.xml.be.config.AaaAttribute;
import com.ah.xml.be.config.AaaAuthPort;
import com.ah.xml.be.config.AaaLdapServer;
import com.ah.xml.be.config.AaaObj;
import com.ah.xml.be.config.AaaPpskServer;
import com.ah.xml.be.config.ActiveDirectoryDomain;
import com.ah.xml.be.config.ActiveDirectoryLogin;
import com.ah.xml.be.config.ActiveDirectoryLoginAdmin;
import com.ah.xml.be.config.ActiveDirectoryPrimary;
import com.ah.xml.be.config.ActiveDirectoryServer;
import com.ah.xml.be.config.ActiveDirectoryUser;
import com.ah.xml.be.config.DbServerBinddn;
import com.ah.xml.be.config.LDAPAuthCaCert;
import com.ah.xml.be.config.LDAPAuthClientCert;
import com.ah.xml.be.config.LDAPAuthPrimary;
import com.ah.xml.be.config.LDAPAuthPrivateKey;
import com.ah.xml.be.config.LDAPAuthPrivateKeyPassword;
import com.ah.xml.be.config.LdapServerIdentity;
import com.ah.xml.be.config.LdapServerName;
import com.ah.xml.be.config.LdapServerPort;
import com.ah.xml.be.config.LdapServerProtocol;
import com.ah.xml.be.config.LibrarySipServerLoginUser;
import com.ah.xml.be.config.LibrarySipServerPort;
import com.ah.xml.be.config.LocalRadiusServer;
import com.ah.xml.be.config.LocalRetryInterval;
import com.ah.xml.be.config.OpenDirectoryDomain;
import com.ah.xml.be.config.OpenDirectoryServer;
import com.ah.xml.be.config.OpenDirectoryUser;
import com.ah.xml.be.config.PpskRadiusServer;
import com.ah.xml.be.config.PpskRadiusServerConcrete;
import com.ah.xml.be.config.PpskServerAutoSaveInterval;
import com.ah.xml.be.config.RadiusAccountInterimInterval;
import com.ah.xml.be.config.RadiusAccountingAll;
import com.ah.xml.be.config.RadiusAccountingServer;
import com.ah.xml.be.config.RadiusKeepalive;
import com.ah.xml.be.config.RadiusKeepaliveInterval;
import com.ah.xml.be.config.RadiusKeepaliveRetry;
import com.ah.xml.be.config.RadiusKeepaliveRetryInterval;
import com.ah.xml.be.config.RadiusKeepaliveUsername;
import com.ah.xml.be.config.RadiusLibrarySipServer;
import com.ah.xml.be.config.RadiusProxy;
import com.ah.xml.be.config.RadiusProxyDeadTime;
import com.ah.xml.be.config.RadiusProxyInject;
import com.ah.xml.be.config.RadiusProxyRealm;
import com.ah.xml.be.config.RadiusProxyRealmAll;
import com.ah.xml.be.config.RadiusProxyRetryCount;
import com.ah.xml.be.config.RadiusProxyRetryDelay;
import com.ah.xml.be.config.RadiusProxyServer;
import com.ah.xml.be.config.RadiusRealmFormat;
import com.ah.xml.be.config.RadiusRetryInterval;
import com.ah.xml.be.config.RadiusServerType;
import com.ah.xml.be.config.RadiusTlsPort;
import com.ah.xml.be.config.STAAuthCaCert;
import com.ah.xml.be.config.STAAuthChecks;
import com.ah.xml.be.config.STAAuthPositiveType;
import com.ah.xml.be.config.STAAuthPrivateKey;
import com.ah.xml.be.config.STAAuthServerCert;
import com.ah.xml.be.config.STAAuthTls;
import com.ah.xml.be.config.StaAuthBasicType;

/**
 * 
 * @author zhang
 * 
 */
public class CreateAAATree {

//	private static final Tracer logger = new Tracer(CreateAAATree.class
//			.getSimpleName());

	private AAAProfileInt aaaProfileImpl;
	private AaaObj aaaObj;
	
	private GenerateXMLDebug oDebug;

	private List<Object> aaaChildList_0 = new ArrayList<Object>();
	private List<Object> aaaChildList_1 = new ArrayList<Object>();
	private List<Object> aaaChildList_2 = new ArrayList<Object>();
	private List<Object> aaaChildList_3 = new ArrayList<Object>();
	private List<Object> aaaChildList_4 = new ArrayList<Object>();
	private List<Object> aaaChildList_5 = new ArrayList<Object>();
	
	private List<Object> priorityList_1 = new ArrayList<Object>();
	private List<Object> priorityList_2 = new ArrayList<Object>();
	private List<Object> priorityList_3 = new ArrayList<Object>();
	private List<Object> priorityList_4 = new ArrayList<Object>();
	private List<Object> priorityList_5 = new ArrayList<Object>();
	private List<Object> priorityList_6 = new ArrayList<Object>();
	private List<Object> priorityList_7 = new ArrayList<Object>();
	
	private List<Object> adDomainList_1 = new ArrayList<Object>();

	public CreateAAATree(AAAProfileInt aaaProfileImpl, GenerateXMLDebug oDebug) throws Exception {
		this.aaaProfileImpl = aaaProfileImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{

		if (aaaProfileImpl.isConfigureAAAProfile() || aaaProfileImpl.isConfigAAAMacFormat() 
				|| aaaProfileImpl.isConfigRadiusProxy() || aaaProfileImpl.isEnableKeepalive()
				|| aaaProfileImpl.isConfigPpskRadius()||aaaProfileImpl.isConfigAaaAttribute()) {
			aaaObj = new AaaObj();
//
//			/** attribute: updateTime */
//			aaaObj.setUpdateTime(aaaProfileImpl.getAAAUpdateTime());

			/** element: <aaa>.<radius-server> */
			oDebug.debug("/configuration/aaa", 
					"radius-server", GenerateXMLDebug.CONFIG_ELEMENT,
					null, null);
			if(aaaProfileImpl.isConfigureAAAProfile() || aaaProfileImpl.isConfigRadiusProxy() ||
					aaaProfileImpl.isEnableKeepalive()|| aaaProfileImpl.isConfigAaaAttribute() ){
				AaaObj.RadiusServer radiusServer = new AaaObj.RadiusServer();
				aaaChildList_0.add(radiusServer);
				aaaObj.setRadiusServer(radiusServer);
			}
			
			/** element: <aaa>.<mac-format> */
			oDebug.debug("/configuration/aaa", 
					"mac-format", GenerateXMLDebug.CONFIG_ELEMENT,
					null, null);
			if(aaaProfileImpl.isConfigAAAMacFormat()){
				AaaObj.MacFormat macFormat = new AaaObj.MacFormat();
				aaaChildList_0.add(macFormat);
				aaaObj.setMacFormat(macFormat);
			}
			
			/** element: <aaa>.<ppsk-server> */
			AaaPpskServer ppskServer = new AaaPpskServer();
			aaaChildList_0.add(ppskServer);
			aaaObj.setPpskServer(ppskServer);
			
			/** element: <aaa>.<attribute> */
			AaaAttribute attribute = new AaaAttribute();
			aaaChildList_0.add(attribute);
			aaaObj.setAttribute(attribute);
		}

		generateAAALevel_1();
	}

	public AaaObj getAaaObj() {
		return this.aaaObj;
	}

	private void generateAAALevel_1() throws Exception {
		/**
		 * <aaa>.<radius-server> 	AaaObj.RadiusServer
		 * <aaa>.<mac-format>		AaaObj.MacFormat
		 * <aaa>.<ppsk-server>		AaaPpskServerRadiusServer
		 * <aaa>.<attribute>		AaaAttribute
		 */
		for (Object childObj : aaaChildList_0) {
			
			/** element: <aaa>.<radius-server> */
			if (childObj instanceof AaaObj.RadiusServer) {
				AaaObj.RadiusServer radiusServerObj = (AaaObj.RadiusServer) childObj;

				/** element: <aaa>.<radius-server>.<retry-interval> */
				oDebug.debug("/configuration/aaa/radius-server", 
						"retry-interval", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				if (aaaProfileImpl.isConfigureRetryInterval()) {
					RadiusRetryInterval retryIntervalObj = new RadiusRetryInterval();
					aaaChildList_1.add(retryIntervalObj);
					radiusServerObj.setRetryInterval(retryIntervalObj);
				}
				
				/** element: <aaa>.<radius-server>.<account-interim-interval> */
				oDebug.debug("/configuration/aaa/radius-server", 
						"account-interim-interval", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				if(aaaProfileImpl.isConfigRadioAccountInterim()){
					
					oDebug.debug("/configuration/aaa/radius-server", 
							"account-interim-interval", GenerateXMLDebug.SET_VALUE,
							aaaProfileImpl.getRadiusAssGuiName(), aaaProfileImpl.getRadiusAssName());
					Object[][] accountParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, aaaProfileImpl.getAccountInterimInterval()},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					radiusServerObj.setAccountInterimInterval(
							(RadiusAccountInterimInterval)CLICommonFunc.createObjectWithName(RadiusAccountInterimInterval.class, accountParm)
					);
				}
				
				/** element: <aaa>.<radius-server>.<accounting> */
				if(aaaProfileImpl.isConfigureAAAProfile()){
					RadiusAccountingAll accountObj = new RadiusAccountingAll();
					aaaChildList_1.add(accountObj);
					radiusServerObj.setAccounting(accountObj);
				}

				/** element: <aaa>.<radius-server>.<primary> */
				oDebug.debug("/configuration/aaa/radius-server", 
						"primary", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				if (aaaProfileImpl.isConfigurePriority(AAAProfileInt.RADIUS_PRIORITY_TYPE.primary)) {
					
					oDebug.debug("/configuration/aaa/radius-server", 
							"primary", GenerateXMLDebug.NULL,
							aaaProfileImpl.getRadiusAssGuiName(), aaaProfileImpl.getRadiusAssName());
					radiusServerObj.setPrimary(createRadiusServerType(AAAProfileInt.RADIUS_PRIORITY_TYPE.primary));
				}

				/** element: <aaa>.<radius-server>.<backup1> */
				oDebug.debug("/configuration/aaa/radius-server", 
						"backup1", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				if (aaaProfileImpl.isConfigurePriority(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1)) {
					
					oDebug.debug("/configuration/aaa/radius-server", 
							"backup1", GenerateXMLDebug.NULL,
							aaaProfileImpl.getRadiusAssGuiName(), aaaProfileImpl.getRadiusAssName());
					radiusServerObj.setBackup1(createRadiusServerType(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1));
				}

				/** element: <aaa>.<radius-server>.<backup2> */
				oDebug.debug("/configuration/aaa/radius-server", 
						"backup2", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				if (aaaProfileImpl.isConfigurePriority(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2)) {
					
					oDebug.debug("/configuration/aaa/radius-server", 
							"backup2", GenerateXMLDebug.NULL,
							aaaProfileImpl.getRadiusAssGuiName(), aaaProfileImpl.getRadiusAssName());
					radiusServerObj.setBackup2(createRadiusServerType(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2));
				}

				/** element: <aaa>.<radius-server>.<backup3> */
				oDebug.debug("/configuration/aaa/radius-server", 
						"backup3", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				if (aaaProfileImpl.isConfigurePriority(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup3)) {
					
					oDebug.debug("/configuration/aaa/radius-server", 
							"backup3", GenerateXMLDebug.NULL,
							aaaProfileImpl.getRadiusAssGuiName(), aaaProfileImpl.getRadiusAssName());
					radiusServerObj.setBackup3(createRadiusServerType(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup3));
				}

				/** element: <aaa>.<radius-server>.<local> */
				oDebug.debug("/configuration/aaa/radius-server", 
						"local", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				if (aaaProfileImpl.isConfigureLocal()) {
					AaaObj.RadiusServer.Local localObj = new AaaObj.RadiusServer.Local();
					aaaChildList_1.add(localObj);
					radiusServerObj.setLocal(localObj);
				}
				
				/** element: <aaa>.<radius-server>.<dynamic-auth-extension> */
				oDebug.debug("/configuration/aaa/radius-server", 
						"dynamic-auth-extension", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				if(aaaProfileImpl.isConfigDynamicAuth()){
					radiusServerObj.setDynamicAuthExtension(CLICommonFunc.getAhOnlyAct(aaaProfileImpl.isEnableDynamicAuth()));
				}
				
				/** element: <aaa>.<radius-server>.<proxy> */
				oDebug.debug("/configuration/aaa/radius-server", 
						"proxy", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				if(aaaProfileImpl.isConfigRadiusProxy()){
					RadiusProxy proxyObj = new RadiusProxy();
					aaaChildList_1.add(proxyObj);
					radiusServerObj.setProxy(proxyObj);
				}
				
				/** element: <aaa>.<radius-server>.<name> */
				for(int index=0; index<aaaProfileImpl.getRealmSize(); index++){
					radiusServerObj.getName().add(this.createLocalRadiusServer(index));
				}
				
				/** element: <aaa>.<radius-server>.<cr> */
				radiusServerObj.getCr().addAll(radiusServerObj.getName());
				
				/** element: <aaa>.<radius-server>.<keepalive> */
				if(aaaProfileImpl.isEnableKeepalive()){
					RadiusKeepalive keepaliveObj = new RadiusKeepalive();
					aaaChildList_1.add(keepaliveObj);
					radiusServerObj.setKeepalive(keepaliveObj);
				}
			}
			
			/** element: <aaa>.<mac-format> */
			if(childObj instanceof AaaObj.MacFormat){
				AaaObj.MacFormat macFormatObj = (AaaObj.MacFormat)childObj;
				
				/** element: <aaa>.<mac-format>.<delimiter> */
				oDebug.debug("/configuration/aaa/mac-format", 
						"delimiter", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getMgmtServiceGuiName(), aaaProfileImpl.getMgmtServiceName());
				Object[][] delimiterParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, aaaProfileImpl.getDelimiterType()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				macFormatObj.setDelimiter(
						(AaaObj.MacFormat.Delimiter)CLICommonFunc.createObjectWithName(AaaObj.MacFormat.Delimiter.class, delimiterParm)
				);
				
				/** element: <aaa>.<mac-format>.<style> */
				oDebug.debug("/configuration/aaa/mac-format", 
						"style", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getMgmtServiceGuiName(), aaaProfileImpl.getMgmtServiceName());
				Object[][] styleParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, aaaProfileImpl.getStyleType()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				macFormatObj.setStyle(
						(AaaObj.MacFormat.Style)CLICommonFunc.createObjectWithName(AaaObj.MacFormat.Style.class, styleParm)
				);
				
				/** element: <aaa>.<mac-format>.<case-sensitivity> */
				oDebug.debug("/configuration/aaa/mac-format", 
						"case-sensitivity", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getMgmtServiceGuiName(), aaaProfileImpl.getMgmtServiceName());
				Object[][] caseParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, aaaProfileImpl.getCaseSensitivityType()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				macFormatObj.setCaseSensitivity(
						(AaaObj.MacFormat.CaseSensitivity)CLICommonFunc.createObjectWithName(AaaObj.MacFormat.CaseSensitivity.class, caseParm)
				);
			}
			
			/** element: <aaa>.<ppsk-server> */
			if(childObj instanceof AaaPpskServer){
				AaaPpskServer ppskServer = (AaaPpskServer)childObj;
				
				/** element: <aaa>.<ppsk-server>.<auto-save-interval> */
				if(aaaProfileImpl.isConfigAAAMacFormat()){
					Object[][] autoSave = {
							{CLICommonFunc.ATTRIBUTE_VALUE, aaaProfileImpl.getPpskAutoSaveInt()},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					ppskServer.setAutoSaveInterval(
							(PpskServerAutoSaveInterval)CLICommonFunc.createObjectWithName(PpskServerAutoSaveInterval.class, autoSave));
				}
				
				/** element: <aaa>.<ppsk-server>.<radius-server> */
				if(aaaProfileImpl.isConfigPpskRadius()){
					PpskRadiusServer radius = new PpskRadiusServer();
					aaaChildList_1.add(radius);
					ppskServer.setRadiusServer(radius);
				}
			}
			/** element: <aaa>.<attribute> */
			if(childObj instanceof AaaAttribute){
				AaaAttribute aaaAttribute=(AaaAttribute)childObj;
				/** element: <aaa>.<attribute>.<nas-identifier> */
				if(aaaProfileImpl.isConfigNasIdentifier()){
					aaaAttribute.setNasIdentifier(CLICommonFunc.createAhStringActObj(aaaProfileImpl.getNasIdentifier(),CLICommonFunc.getYesDefault()));
				}
				/** element: <aaa>.<attribute>.<operator-name> */
				if(aaaProfileImpl.isConfigOperatorName()){
					AaaAttrOperatorName operatorName=new AaaAttrOperatorName();
					aaaChildList_1.add(operatorName);
					aaaAttribute.setOperatorName(operatorName);
				}
			}
		}
		
		generateAAALevel_2();
	}

	private void generateAAALevel_2() throws Exception {
		/**
		 * <aaa>.<radius-server>.<local>						AaaObj.RadiusServer.Local
		 * <aaa>.<radius-server>.<retry-interval>				RadiusRetryInterval
		 * <aaa>.<radius-server>.<accounting>					RadiusAccountingAll
		 * <aaa>.<radius-server>.<proxy>						RadiusProxy
		 * <aaa>.<radius-server>.<keepalive>					RadiusKeepalive
		 * <aaa>.<ppsk-server>.<radius-server>					PpskRadiusServer
		 * <aaa>.<attribute>.<operator-Name>					AaaAttrOperatorName
		 */
		for (Object childObj : aaaChildList_1) {

			/** element: <aaa>.<radius-server>.<retry-interval> */
			if (childObj instanceof RadiusRetryInterval) {
				RadiusRetryInterval retryIntervalObj = (RadiusRetryInterval) childObj;

				/** attribute: value */
				oDebug.debug("/configuration/aaa/radius-server", 
						"retry-interval", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusAssGuiName(), aaaProfileImpl.getRadiusAssName());
				retryIntervalObj.setValue(aaaProfileImpl.getRetryInterval());

				/** attribute: operation */
				retryIntervalObj.setOperation(CLICommonFunc
						.getAhEnumAct(CLICommonFunc.getYesDefault()));

//				/** element: <aaa>.<radius-server>.<retry-interval>.<account-interim-interval> */
//				oDebug.debug("/configuration/aaa/radius-server/retry-interval", 
//						"account-interim-interval", GenerateXMLDebug.SET_VALUE,
//						aaaProfileImpl.getRadiusAssGuiName(), aaaProfileImpl.getRadiusAssName());
//				Object[][] interimParm = {
//						{ CLICommonFunc.ATTRIBUTE_VALUE,
//								aaaProfileImpl.getAccountInterimInterval() },
//						{ CLICommonFunc.ATTRIBUTE_OPERATION,
//								CLICommonFunc.getYesDefault() } };
//				retryIntervalObj
//						.setAccountInterimInterval((AaaObj.RadiusServer.RetryInterval.AccountInterimInterval) CLICommonFunc
//								.createObjectWithName(
//										AaaObj.RadiusServer.RetryInterval.AccountInterimInterval.class,
//										interimParm));
//				}
			}

			/** element: <aaa>.<radius-server>.<local> */
			if (childObj instanceof AaaObj.RadiusServer.Local) {
				AaaObj.RadiusServer.Local localObj = (AaaObj.RadiusServer.Local) childObj;
				
				/** element: <aaa>.<radius-server>.<local>.<STA-auth> */
				AaaObj.RadiusServer.Local.STAAuth staAuthObj = new AaaObj.RadiusServer.Local.STAAuth();
				aaaChildList_2.add(staAuthObj);
				localObj.setSTAAuth(staAuthObj);
				
//				/** attribute: updatTime */
//				localObj.setUpdateTime(aaaProfileImpl.getLocalUpdateTime());
				if(aaaProfileImpl.isConfigRadiusServer()){

					/** element: <aaa>.<radius-server>.<local>.<port> */
					oDebug.debug("/configuration/aaa/radius-server/local", 
							"port", GenerateXMLDebug.SET_VALUE,
							aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
					Object[][] localPortParm = {
							{ CLICommonFunc.ATTRIBUTE_VALUE,
									aaaProfileImpl.getLocalPort() },
							{ CLICommonFunc.ATTRIBUTE_OPERATION,
									CLICommonFunc.getYesDefault() } };
					localObj.setPort((AaaObj.RadiusServer.Local.Port) CLICommonFunc
							.createObjectWithName(
									AaaObj.RadiusServer.Local.Port.class,
									localPortParm));

					

					/** element: <aaa>.<radius-server>.<local>.<user-group> */
					oDebug.debug("/configuration/aaa/radius-server/local", 
							"user-group", GenerateXMLDebug.CONFIG_ELEMENT,
							aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
					for (int i = 0; i < aaaProfileImpl.getLocalUserGroupSize(); i++) {
						localObj.getUserGroup().add(this.createUserGroupObj(i));
					}

//					/** element: <aaa>.<radius-server>.<local>.<user-name> */
//					oDebug.debug("/configuration/aaa/radius-server/local", 
//							"user-name", GenerateXMLDebug.CONFIG_ELEMENT,
//							aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
//					if (aaaProfileImpl.isConfigureUser()) {
//						for (int i = 0; i < aaaProfileImpl.getLocalUserSize(); i++) {
//							localObj.getUserName().add(this.createUserName(i));
//						}
//					}

					/** element: <aaa>.<radius-server>.<local>.<cache> */
					oDebug.debug("/configuration/aaa/radius-server/local", 
							"cache", GenerateXMLDebug.CONFIG_ELEMENT,
							aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
					if (aaaProfileImpl.isConfigureLocalCache()) {
						AaaObj.RadiusServer.Local.Cache cacheObj = new AaaObj.RadiusServer.Local.Cache();
						aaaChildList_2.add(cacheObj);
						localObj.setCache(cacheObj);
					}

					/** element: <aaa>.<radius-server>.<local>.<attr-map> */
					oDebug.debug("/configuration/aaa/radius-server/local", 
							"attr-map", GenerateXMLDebug.CONFIG_ELEMENT,
							aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
					if (aaaProfileImpl.isConfigureAttrMap()) {
						AaaObj.RadiusServer.Local.AttrMap attrMapObj = new AaaObj.RadiusServer.Local.AttrMap();
						aaaChildList_2.add(attrMapObj);
						localObj.setAttrMap(attrMapObj);
					}

					/** element: <aaa>.<radius-server>.<local>.<db-type> */
					AaaObj.RadiusServer.Local.DbType dbTypeObj = new AaaObj.RadiusServer.Local.DbType();
					aaaChildList_2.add(dbTypeObj);
					localObj.setDbType(dbTypeObj);

					/** element: <aaa>.<radius-server>.<local>.<LDAP-auth> */
					oDebug.debug("/configuration/aaa/radius-server/local", 
							"LDAP-auth", GenerateXMLDebug.CONFIG_ELEMENT,
							aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
					if (aaaProfileImpl.isConfigureLdapTlsClientCert()) {
						AaaObj.RadiusServer.Local.LDAPAuth ldapAuthObj = new AaaObj.RadiusServer.Local.LDAPAuth();
						aaaChildList_2.add(ldapAuthObj);
						localObj.setLDAPAuth(ldapAuthObj);
					}
					
					/** element: <aaa>.<radius-server>.<local>.<local-check-period> */
					oDebug.debug("/configuration/aaa/radius-server/local", 
							"local-check-period", GenerateXMLDebug.CONFIG_ELEMENT,
							aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
					if(aaaProfileImpl.isConfigLocalCheckPeriod()){
						
						oDebug.debug("/configuration/aaa/radius-server/local", 
								"local-check-period", GenerateXMLDebug.SET_VALUE,
								aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
						Object[][] parmLocalCheck = {
								{CLICommonFunc.ATTRIBUTE_VALUE, aaaProfileImpl.getLocalCheckPeriodValue()},
								{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
						};
						localObj.setLocalCheckPeriod(
								(AaaObj.RadiusServer.Local.LocalCheckPeriod)CLICommonFunc.createObjectWithName(AaaObj.RadiusServer.Local.LocalCheckPeriod.class, parmLocalCheck)
						);
					}
					
					/** element: <aaa>.<radius-server>.<local>.<remote-check-period> */
					oDebug.debug("/configuration/aaa/radius-server/local", 
							"remote-check-period", GenerateXMLDebug.CONFIG_ELEMENT,
							aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
					if(aaaProfileImpl.isConfigRemoteCheckPeriod()){
						
						oDebug.debug("/configuration/aaa/radius-server/local", 
								"remote-check-period", GenerateXMLDebug.SET_VALUE,
								aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
						Object[][] remoteParm = {
								{CLICommonFunc.ATTRIBUTE_VALUE, aaaProfileImpl.getRemoteCheckPeriodValue()},
								{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
						};
						localObj.setRemoteCheckPeriod(
								(AaaObj.RadiusServer.Local.RemoteCheckPeriod)CLICommonFunc.createObjectWithName(AaaObj.RadiusServer.Local.RemoteCheckPeriod.class, remoteParm)
						);
					}
					
					/** element: <aaa>.<radius-server>.<local>.<retry-interval> */
					oDebug.debug("/configuration/aaa/radius-server/local", 
							"retry-interval", GenerateXMLDebug.CONFIG_ELEMENT,
							aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
					if(aaaProfileImpl.isConfigRetryInterval()){
						
						oDebug.debug("/configuration/aaa/radius-server/local", 
								"retry-interval", GenerateXMLDebug.SET_VALUE,
								aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
						Object[][] retryParm = {
								{CLICommonFunc.ATTRIBUTE_VALUE, aaaProfileImpl.getRetryIntervalValue()},
								{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
						};
						localObj.setRetryInterval(
								(LocalRetryInterval)CLICommonFunc.createObjectWithName(LocalRetryInterval.class, retryParm)
						);
					}
					
					/** element: <aaa>.<radius-server>.<local>.<shared-secret-auto-gen> */
					oDebug.debug("/configuration/aaa/radius-server/local", 
							"shared-secret-auto-gen", GenerateXMLDebug.SET_OPERATION,
							aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
					localObj.setSharedSecretAutoGen(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
				}
				
				/** element: <aaa>.<radius-server>.<local>.<enable> */
				oDebug.debug("/configuration/aaa/radius-server/local", 
						"enable", GenerateXMLDebug.SET_OPERATION,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				localObj.setEnable(CLICommonFunc.getAhOnlyAct(aaaProfileImpl
						.isRadiusServerEnable()));
				
				/** element: <aaa>.<radius-server>.<local>.<nas> */
				oDebug.debug("/configuration/aaa/radius-server/local", 
						"nas", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if (aaaProfileImpl.isConfigureNAS()) {
					for (int i = 0; i < aaaProfileImpl.getNASSize(); i++) {
						localObj.getNas().add(this.createNASObj(i));
					}
				}
				
				/** element: <aaa>.<radius-server>.<local>.<library-sip-policy> */
				oDebug.debug("/configuration/aaa/radius-server/local", 
						"library-sip-policy", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if(aaaProfileImpl.isEnableLibrarySipPolicy()){
					
					oDebug.debug("/configuration/aaa/radius-server/local", 
							"library-sip-policy", GenerateXMLDebug.SET_NAME,
							aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
					localObj.setLibrarySipPolicy(CLICommonFunc.createAhNameActObj(aaaProfileImpl.getLibrarySipPolicyName(), CLICommonFunc.getYesDefault()));
				}
			}
			
			/** element: <aaa>.<radius-server>.<accounting>	*/
			if(childObj instanceof RadiusAccountingAll){
				RadiusAccountingAll accountObj = (RadiusAccountingAll)childObj;
				
				/** element: <aaa>.<radius-server>.<accounting>.<primary> */
				oDebug.debug("/configuration/aaa/radius-server/accounting", 
						"primary", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if (aaaProfileImpl.isConfigAcctPriority(AAAProfileInt.RADIUS_PRIORITY_TYPE.primary)){
					accountObj.setPrimary(this.createRadiusAccountingServer(AAAProfileInt.RADIUS_PRIORITY_TYPE.primary));
				}
				
				/** element: <aaa>.<radius-server>.<accounting>.<backup1> */
				oDebug.debug("/configuration/aaa/radius-server/accounting", 
						"backup1", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if (aaaProfileImpl.isConfigAcctPriority(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1)){
					accountObj.setBackup1(this.createRadiusAccountingServer(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1));
				}
				
				/** element: <aaa>.<radius-server>.<accounting>.<backup2> */
				oDebug.debug("/configuration/aaa/radius-server/accounting", 
						"backup2", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if (aaaProfileImpl.isConfigAcctPriority(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2)){
					accountObj.setBackup2(this.createRadiusAccountingServer(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2));
				}
				
				/** element: <aaa>.<radius-server>.<accounting>.<backup3> */
				oDebug.debug("/configuration/aaa/radius-server/accounting", 
						"backup3", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if (aaaProfileImpl.isConfigAcctPriority(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup3)){
					accountObj.setBackup3(this.createRadiusAccountingServer(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup3));
				}
			}
			
			/** element: <aaa>.<radius-server>.<proxy> */
			if(childObj instanceof RadiusProxy){
				RadiusProxy proxyObj = (RadiusProxy)childObj;
				
				/** element: <aaa>.<radius-server>.<proxy>.<dead-time> */
				oDebug.debug("/configuration/aaa/radius-server/proxy",
						"dead-time", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				Object[][] deadTimeParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, aaaProfileImpl.getProxyDeadTime()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				proxyObj.setDeadTime(
						(RadiusProxyDeadTime)CLICommonFunc.createObjectWithName(RadiusProxyDeadTime.class, deadTimeParm));
				
				/** element: <aaa>.<radius-server>.<proxy>.<retry-delay> */
				RadiusProxyRetryDelay retryObj = new RadiusProxyRetryDelay();
				aaaChildList_2.add(retryObj);
				proxyObj.setRetryDelay(retryObj);
				
				/** element: <aaa>.<radius-server>.<proxy>.<realm> */
				RadiusProxyRealmAll realmObj = new RadiusProxyRealmAll();
				aaaChildList_2.add(realmObj);
				proxyObj.setRealm(realmObj);
				
				/** element: <aaa>.<radius-server>.<proxy>.<inject> */
				RadiusProxyInject injectObj=new RadiusProxyInject();
				aaaChildList_2.add(injectObj);
				proxyObj.setInject(injectObj);
				
//				/** element: <aaa>.<radius-server>.<proxy>.<radsec> */
//				if(aaaProfileImpl.isConfigProxyRadsec()){
//					RadiusProxyRadsec radsec = new RadiusProxyRadsec();
//					aaaChildList_2.add(radsec);
//					proxyObj.setRadsec(radsec);
//				}
			}
			
			/** element: <aaa>.<radius-server>.<keepalive> */
			if(childObj instanceof RadiusKeepalive){
				RadiusKeepalive keepAliveObj = (RadiusKeepalive)childObj;
				
				/** element: <aaa>.<radius-server>.<keepalive>.<enable> */
				keepAliveObj.setEnable(CLICommonFunc.getAhOnlyAct(aaaProfileImpl.isEnableKeepalive()));
				
				/** element: <aaa>.<radius-server>.<keepalive>.<interval> */
				Object[][] intervalParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, aaaProfileImpl.getKeepaliveInterval()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				keepAliveObj.setInterval(
						(RadiusKeepaliveInterval)CLICommonFunc.createObjectWithName(RadiusKeepaliveInterval.class, intervalParm));
				
				/** element: <aaa>.<radius-server>.<keepalive>.<retry> */
				Object[][] retryObj = {
						{CLICommonFunc.ATTRIBUTE_VALUE, aaaProfileImpl.getKeepaliveRetry()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				keepAliveObj.setRetry(
						(RadiusKeepaliveRetry)CLICommonFunc.createObjectWithName(RadiusKeepaliveRetry.class, retryObj));
				
				/** element: <aaa>.<radius-server>.<keepalive>.<retry-interval> */
				Object[][] retryIntParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, aaaProfileImpl.getKeepaliveRetryInterval()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				keepAliveObj.setRetryInterval(
						(RadiusKeepaliveRetryInterval)CLICommonFunc.createObjectWithName(RadiusKeepaliveRetryInterval.class, retryIntParm));
				
				/** element: <aaa>.<radius-server>.<keepalive>.<username> */
				if(aaaProfileImpl.isConfigKeepaliveUsername()){
					RadiusKeepaliveUsername usernameObj = new RadiusKeepaliveUsername();
					aaaChildList_2.add(usernameObj);
					keepAliveObj.setUsername(usernameObj);
				}
			}
			
			/** element: <aaa>.<ppsk-server>.<radius-server> */
			if(childObj instanceof PpskRadiusServer){
				PpskRadiusServer radiusServer = (PpskRadiusServer)childObj;
				
				/** element: <aaa>.<ppsk-server>.<radius-server>.<primary> */
				if(aaaProfileImpl.isConfigPpskPriority(AAAProfileInt.RADIUS_PRIORITY_TYPE.primary)){
					radiusServer.setPrimary(this.createPpskRadiusServerConcrete(AAAProfileInt.RADIUS_PRIORITY_TYPE.primary));
				}
				
				/** element: <aaa>.<ppsk-server>.<radius-server>.<backup1> */
				if(aaaProfileImpl.isConfigPpskPriority(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1)){
					radiusServer.setBackup1(this.createPpskRadiusServerConcrete(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1));
				}
				
				/** element: <aaa>.<ppsk-server>.<radius-server>.<backup2> */
				if(aaaProfileImpl.isConfigPpskPriority(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2)){
					radiusServer.setBackup2(this.createPpskRadiusServerConcrete(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2));
				}
				
				/** element: <aaa>.<ppsk-server>.<radius-server>.<backup3> */
				if(aaaProfileImpl.isConfigPpskPriority(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup3)){
					radiusServer.setBackup3(this.createPpskRadiusServerConcrete(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup3));
				}
				
			}
			/** element: <aaa>.<attribute>.<operator-name> */
			if(childObj instanceof AaaAttrOperatorName){
				AaaAttrOperatorName operatorName = (AaaAttrOperatorName)childObj;
				/** element: <aaa>.<attribute>.<operator-name>.<cr> */
				operatorName.setValue(CLICommonFunc.createAhStringActObj(aaaProfileImpl.getOperatorName(),CLICommonFunc.getYesDefault()));
				/** element: <aaa>.<attribute>.<operator-name>.<namespace-id> */
				operatorName.setNamespaceId(CLICommonFunc.createAhStringActObj(aaaProfileImpl.getNamespaceId(),CLICommonFunc.getYesDefault()));
				
			}
		}

		generateAAALevel_3();
	}

	private void generateAAALevel_3() throws Exception {
		/**
		 * <aaa>.<radius-server>.<local>.<STA-auth>			AaaObj.RadiusServer.Local.STAAuth 
		 * <aaa>.<radius-server>.<local>.<cache>			AaaObj.RadiusServer.Local.Cache 
		 * <aaa>.<radius-server>.<local>.<attr-map>			AaaObj.RadiusServer.Local.AttrMap 
		 * <aaa>.<radius-server>.<local>.<db-type>			AaaObj.RadiusServer.Local.DbType 
		 * <aaa>.<radius-server>.<local>.<LDAP-auth>		AaaObj.RadiusServer.Local.LDAPAuth
		 * <aaa>.<radius-server>.<proxy>.<retry-delay>		RadiusProxyRetryDelay
		 * <aaa>.<radius-server>.<proxy>.<realm>			RadiusProxyRealmAll
		 * <aaa>.<radius-server>.<proxy>.<inject>			RadiusProxyInject
		 * <aaa>.<radius-server>.<proxy>.<radsec>			RadiusProxyRadsec
		 * <aaa>.<radius-server>.<keepalive>.<username>		RadiusKeepaliveUsername
		 */
		for (Object childObj : aaaChildList_2) {

			/** element: <aaa>.<radius-server>.<local>.<STA-auth> */
			if (childObj instanceof AaaObj.RadiusServer.Local.STAAuth) {
				AaaObj.RadiusServer.Local.STAAuth staAuthObj = (AaaObj.RadiusServer.Local.STAAuth) childObj;

				/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<type> */
				AaaObj.RadiusServer.Local.STAAuth.Type staAuthTypeObj = new AaaObj.RadiusServer.Local.STAAuth.Type();
				aaaChildList_3.add(staAuthTypeObj);
				staAuthObj.setType(staAuthTypeObj);
				
				/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<default-type> */
				if(aaaProfileImpl.isConfigSTAAuthDefaultType()) {
					staAuthObj.setDefaultType(CLICommonFunc.createAhStringActObj(
							aaaProfileImpl.getSTAAuthDefaultType(), CLICommonFunc.getYesDefault()));
				}

				/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<ca-cert> */
				oDebug.debug("/configuration/aaa/radius-server/local/STA-auth", 
						"ca-cert", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if(aaaProfileImpl.isConfigCaCert()){
					STAAuthCaCert caCertObj = new STAAuthCaCert();
					aaaChildList_3.add(caCertObj);
					staAuthObj.setCaCert(caCertObj);
				}
			}

			/** element: <aaa>.<radius-server>.<local>.<cache> */
			if (childObj instanceof AaaObj.RadiusServer.Local.Cache) {
				AaaObj.RadiusServer.Local.Cache cacheObj = (AaaObj.RadiusServer.Local.Cache) childObj;

				/** attribute: operation */
				cacheObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <aaa>.<radius-server>.<local>.<cache>.<cr> */
				oDebug.debug("/configuration/aaa/radius-server/local/cache", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				cacheObj.setCr(CLICommonFunc.getAhOnlyAct(aaaProfileImpl.isEnableLocalCache()));

				/** element: <aaa>.<radius-server>.<local>.<cache>.<lifetime> */
				if(aaaProfileImpl.isEnableLocalCache()){
					oDebug.debug("/configuration/aaa/radius-server/local/cache", 
							"lifetime", GenerateXMLDebug.SET_VALUE,
							aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
					Object[][] lifeTimeParm = {
							{ CLICommonFunc.ATTRIBUTE_VALUE, aaaProfileImpl.getCacheLifeTime() },
							{ CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault() } };
					cacheObj.setLifetime((AaaObj.RadiusServer.Local.Cache.Lifetime) CLICommonFunc
									.createObjectWithName(
											AaaObj.RadiusServer.Local.Cache.Lifetime.class,
											lifeTimeParm));
				}
			}

			/** element: <aaa>.<radius-server>.<local>.<attr-map> */
			if (childObj instanceof AaaObj.RadiusServer.Local.AttrMap) {
				AaaObj.RadiusServer.Local.AttrMap attrMapObj = (AaaObj.RadiusServer.Local.AttrMap) childObj;

				/** element: <aaa>.<radius-server>.<local>.<attr-map>.<group-attr-name> */
				oDebug.debug("/configuration/aaa/radius-server/local/attr-map", 
						"group-attr-name", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if(aaaProfileImpl.isConfigGroupAttrName()){
					
					oDebug.debug("/configuration/aaa/radius-server/local/attr-map", 
							"group-attr-name", GenerateXMLDebug.SET_VALUE,
							aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
					attrMapObj.setGroupAttrName(CLICommonFunc.createAhStringActObj(aaaProfileImpl.getGroupAttrName(), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <aaa>.<radius-server>.<local>.<attr-map>.<reauth-attr-name> */
				oDebug.debug("/configuration/aaa/radius-server/local/attr-map", 
						"reauth-attr-name", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if (aaaProfileImpl.isConfigureReauthAttrName()) {
					
					oDebug.debug("/configuration/aaa/radius-server/local/attr-map", 
							"reauth-attr-name", GenerateXMLDebug.SET_VALUE,
							aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
					attrMapObj.setReauthAttrName(CLICommonFunc
							.createAhStringActObj(aaaProfileImpl
									.getReauthAttrName(), CLICommonFunc
									.getYesDefault()));
				}

				/** element: <aaa>.<radius-server>.<local>.<attr-map>.<user-profile-attr-name> */
				oDebug.debug("/configuration/aaa/radius-server/local/attr-map", 
						"user-profile-attr-name", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if (aaaProfileImpl.isConfigureUserProfileAttr()) {
					
					oDebug.debug("/configuration/aaa/radius-server/local/attr-map", 
							"user-profile-attr-name", GenerateXMLDebug.SET_VALUE,
							aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
					attrMapObj.setUserProfileAttrName(CLICommonFunc
							.createAhStringActObj(aaaProfileImpl
									.getUserProfileAttrName(), CLICommonFunc
									.getYesDefault()));
				}

				/** element: <aaa>.<radius-server>.<local>.<attr-map>.<vlan-attr-name> */
				oDebug.debug("/configuration/aaa/radius-server/local/attr-map", 
						"vlan-attr-name", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if (aaaProfileImpl.isConfigureVlanAttr()) {
					
					oDebug.debug("/configuration/aaa/radius-server/local/attr-map", 
							"vlan-attr-name", GenerateXMLDebug.SET_VALUE,
							aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
					attrMapObj.setVlanAttrName(CLICommonFunc
							.createAhStringActObj(aaaProfileImpl
									.getVlanAttrName(), CLICommonFunc
									.getYesDefault()));
				}
			}

			/** element: <aaa>.<radius-server>.<local>.<db-type> */
			if (childObj instanceof AaaObj.RadiusServer.Local.DbType) {
				AaaObj.RadiusServer.Local.DbType dbTypeObj = (AaaObj.RadiusServer.Local.DbType) childObj;

				/** element: <aaa>.<radius-server>.<local>.<db-type>.<local> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type", 
						"local", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if (aaaProfileImpl.isRadiusDBTypeLocal()) {
					dbTypeObj.setLocal(CLICommonFunc.getAhOnlyAct(CLICommonFunc
							.getYesDefault()));
				}

				/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type", 
						"active-directory", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if (aaaProfileImpl.isRadiusDBTypeActive()) {
					AaaObj.RadiusServer.Local.DbType.ActiveDirectory activeDirectoryObj = new AaaObj.RadiusServer.Local.DbType.ActiveDirectory();
					aaaChildList_3.add(activeDirectoryObj);
					dbTypeObj.setActiveDirectory(activeDirectoryObj);
				}

//				/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-ldap> */
//				oDebug.debug("/configuration/aaa/radius-server/local/db-type", 
//						"open-ldap", GenerateXMLDebug.CONFIG_ELEMENT,
//						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
//				if (aaaProfileImpl.isRadiusDBTypeOpen()) {
//					AaaObj.RadiusServer.Local.DbType.OpenLdap openLdap = new AaaObj.RadiusServer.Local.DbType.OpenLdap();
//					aaaChildList_3.add(openLdap);
//					dbTypeObj.setOpenLdap(openLdap);
//				}
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<ldap-server> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type", 
						"ldap-server", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if (aaaProfileImpl.isRadiusDBTypeOpen()){
					AaaObj.RadiusServer.Local.DbType.LdapServer ldapServerObj = new AaaObj.RadiusServer.Local.DbType.LdapServer();
					aaaChildList_3.add(ldapServerObj);
					dbTypeObj.setLdapServer(ldapServerObj);
				}
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-directory> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type", 
						"open-directory", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if(aaaProfileImpl.isRadiusDBTypeOpenDirectory()){
					AaaObj.RadiusServer.Local.DbType.OpenDirectory openDirectoryObj = new AaaObj.RadiusServer.Local.DbType.OpenDirectory();
					aaaChildList_3.add(openDirectoryObj);
					dbTypeObj.setOpenDirectory(openDirectoryObj);
				}
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<library-sip-server> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type", 
						"library-sip-server", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if(aaaProfileImpl.isEnableLibrarySipPolicy()){
					AaaObj.RadiusServer.Local.DbType.LibrarySipServer sipServer = new AaaObj.RadiusServer.Local.DbType.LibrarySipServer();
					aaaChildList_3.add(sipServer);
					dbTypeObj.setLibrarySipServer(sipServer);
				}
			}

			/** element: <aaa>.<radius-server>.<local>.<LDAP-auth> */
			if (childObj instanceof AaaObj.RadiusServer.Local.LDAPAuth) {
				AaaObj.RadiusServer.Local.LDAPAuth ldapAuthObj = (AaaObj.RadiusServer.Local.LDAPAuth) childObj;

				/** element: <aaa>.<radius-server>.<local>.<LDAP-auth>.<primary> */
				oDebug.debug("/configuration/aaa/radius-server/local/LDAP-auth", 
						"primary", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if(aaaProfileImpl.isConfigLdapAuth(AAAProfileInt.RADIUS_PRIORITY_TYPE.primary)){
					ldapAuthObj.setPrimary(this.createLDAPAuthPrimary(AAAProfileInt.RADIUS_PRIORITY_TYPE.primary));
				}

				/** element: <aaa>.<radius-server>.<local>.<LDAP-auth>.<backup1> */
				oDebug.debug("/configuration/aaa/radius-server/local/LDAP-auth", 
						"backup1", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if(aaaProfileImpl.isConfigLdapAuth(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1)){
					ldapAuthObj.setBackup1(this.createLDAPAuthPrimary(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1));
				}
				
				/** element: <aaa>.<radius-server>.<local>.<LDAP-auth>.<backup2> */
				oDebug.debug("/configuration/aaa/radius-server/local/LDAP-auth", 
						"backup2", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if(aaaProfileImpl.isConfigLdapAuth(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2)){
					ldapAuthObj.setBackup2(this.createLDAPAuthPrimary(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2));
				}
				
				/** element: <aaa>.<radius-server>.<local>.<LDAP-auth>.<backup3> */
				oDebug.debug("/configuration/aaa/radius-server/local/LDAP-auth", 
						"backup3", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if(aaaProfileImpl.isConfigLdapAuth(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup3)){
					ldapAuthObj.setBackup3(this.createLDAPAuthPrimary(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup3));
				}
			}

			/** element: <aaa>.<radius-server>.<proxy>.<retry-delay> */
			if(childObj instanceof RadiusProxyRetryDelay){
				RadiusProxyRetryDelay retryObj = (RadiusProxyRetryDelay)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/aaa/radius-server/proxy", 
						"retry-delay", GenerateXMLDebug.SET_VALUE,
						null,null);
				retryObj.setValue(aaaProfileImpl.getProxyRetryDelay());
				
				/** attribute: operation */
				retryObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <aaa>.<radius-server>.<proxy>.<retry-delay>.<retry-count> */
				oDebug.debug("/configuration/aaa/radius-server/proxy/retry-delay", 
						"retry-count", GenerateXMLDebug.SET_VALUE,
						null,null);
				Object[][] retryCountParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, aaaProfileImpl.getProxyRetryCount()}
				};
				retryObj.setRetryCount(
						(RadiusProxyRetryCount)CLICommonFunc.createObjectWithName(RadiusProxyRetryCount.class, retryCountParm)
				);
			}
			
			/** element: <aaa>.<radius-server>.<proxy>.<realm> */
			if(childObj instanceof RadiusProxyRealmAll){
				RadiusProxyRealmAll realmObj = (RadiusProxyRealmAll)childObj;
				
				/** element: <aaa>.<radius-server>.<proxy>.<realm>.<format> */
				RadiusRealmFormat formatObj = new RadiusRealmFormat();
				aaaChildList_3.add(formatObj);
				realmObj.setFormat(formatObj);
				
				/** element: <aaa>.<radius-server>.<proxy>.<realm>.<cr> */
				for(int index=0; index<aaaProfileImpl.getProxyRealmSize(); index++){
					if(aaaProfileImpl.isConfigProxyRealm(index)){
						realmObj.getCr().add(this.createRadiusProxyRealm(index));
					}
				}
			}
			
			/** element: <aaa>.<radius-server>.<proxy>.<inject> */
			if(childObj instanceof RadiusProxyInject){
				RadiusProxyInject injectObj=(RadiusProxyInject)childObj;
				injectObj.setOperatorName(CLICommonFunc.getAhOnlyAct(aaaProfileImpl.isProxyOperatorNameEnable()));
			}
			
//			/** element: <aaa>.<radius-server>.<proxy>.<radsec> */
//			if(childObj instanceof RadiusProxyRadsec){
//				RadiusProxyRadsec radsec = (RadiusProxyRadsec)childObj;
//				
//				/** element: <aaa>.<radius-server>.<proxy>.<radsec>.<enable> */
//				oDebug.debug("/configuration/aaa/radius-server/proxy/radsec", 
//						"enable", GenerateXMLDebug.SET_OPERATION,
//						null, null);
//				radsec.setEnable(CLICommonFunc.getAhOnlyAct(aaaProfileImpl.isEnableProxyRadsec()));
//				
//				/** element: <aaa>.<radius-server>.<proxy>.<radsec>.<tls-port> */
//				//fix bug 25291, no need upload tls port, OS will auto retry 80 or 443
////				if(aaaProfileImpl.isConfigRadsecTlsPort()){
////					Object[][] tlsPortParm = {
////							{CLICommonFunc.ATTRIBUTE_VALUE, aaaProfileImpl.getRadsecTlsPort()},
////							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
////					};
////					radsec.setTlsPort((RadiusTlsPort)CLICommonFunc.createObjectWithName(RadiusTlsPort.class, tlsPortParm));
////				}
//				
//				/** element: <aaa>.<radius-server>.<proxy>.<radsec>.<realm> */
//				for(int i=0; i<aaaProfileImpl.getRadsecRealmSize(); i++){
//					if(aaaProfileImpl.isAuthRealmValid(i)){
//						radsec.getRealm().add(this.createRadsecRealm(i));
//					}
//				}
//				
//				/** element: <aaa>.<radius-server>.<proxy>.<radsec>.<dynamic-auth-extension> */
//				if(aaaProfileImpl.isConfigRadsecDynamicAuthExtension()){
//					radsec.setDynamicAuthExtension(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
//				}
//			}
			
			/** element: <aaa>.<radius-server>.<keepalive>.<username> */
			if(childObj instanceof RadiusKeepaliveUsername){
				RadiusKeepaliveUsername usernameObj = (RadiusKeepaliveUsername)childObj;
				
				/** operation: value */
				usernameObj.setValue(aaaProfileImpl.getKeepaliveUsername());
				
				/** operation: operation */
				usernameObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <aaa>.<radius-server><keepalive>.<password> */
				usernameObj.setPassword(CLICommonFunc.createAhEncryptedString(
						aaaProfileImpl.getKeepalivePassword()));
			}
		}
		generateAAALevel_4();
	}

	private void generateAAALevel_4() throws Exception {
		/**
		 * <aaa>.<radius-server>.<local>.<STA-auth>.<type>				AaaObj.RadiusServer.Local.STAAuth.Type 
		 * <aaa>.<radius-server>.<local>.<STA-auth>.<ca-cert>			STAAuthCaCert
		 * <aaa>.<radius-server>.<local>.<db-type>.<active-directory>	AaaObj.RadiusServer.Local.DbType.ActiveDirectory 
//		 * <aaa>.<radius-server>.<local>.<db-type>.<open-ldap>			AaaObj.RadiusServer.Local.DbType.OpenLdap
		 * <aaa>.<radius-server>.<local>.<LDAP-auth>.<primary>			LDAPAuthPrimary
		 * <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>		AaaObj.RadiusServer.Local.DbType.LdapServer
		 * <aaa>.<radius-server>.<local>.<db-type>.<open-directory>		AaaObj.RadiusServer.Local.DbType.OpenDirectory
		 * <aaa>.<radius-server>.<proxy>.<realm>.<format>				RadiusRealmFormat
		 * <aaa>.<radius-server>.<local>.<db-type>.<library-sip-server>		AaaObj.RadiusServer.Local.DbType.LibrarySipServer
		 */
		for (Object childObj : aaaChildList_3) {

			/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<type> */
			if (childObj instanceof AaaObj.RadiusServer.Local.STAAuth.Type) {
				AaaObj.RadiusServer.Local.STAAuth.Type staAuthTypeObj = (AaaObj.RadiusServer.Local.STAAuth.Type) childObj;

				/** attribute: operation */
				staAuthTypeObj.setOperation(CLICommonFunc
						.getAhEnumAct(CLICommonFunc.getYesDefault()));

				/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<tls> */
				STAAuthTls tlsObj = new STAAuthTls();
				aaaChildList_4.add(tlsObj);
				staAuthTypeObj.setTls(tlsObj);
				
				/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<cr> */
				STAAuthPositiveType crObj = new STAAuthPositiveType();
				aaaChildList_4.add(crObj);
				staAuthTypeObj.setCr(crObj);
				
				/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<md5> */
				if (!aaaProfileImpl.isConfigureSTAauthType(AAAProfileInt.STA_AUTH_TYPE.md5)){
					StaAuthBasicType md5Obj = new StaAuthBasicType();
					aaaChildList_4.add(md5Obj);
					staAuthTypeObj.setMd5(md5Obj);
				}

				/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<peap> */
				oDebug.debug("/configuration/aaa/radius-server/local/STA-auth/type", 
						"peap", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if (!aaaProfileImpl.isConfigureSTAauthType(AAAProfileInt.STA_AUTH_TYPE.peap)) {
					AaaObj.RadiusServer.Local.STAAuth.Type.Peap peapCrObj = new AaaObj.RadiusServer.Local.STAAuth.Type.Peap();
					aaaChildList_4.add(peapCrObj);
					staAuthTypeObj.setPeap(peapCrObj);
				}

				/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<ttls> */
				oDebug.debug("/configuration/aaa/radius-server/local/STA-auth/type", 
						"ttls", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if (!aaaProfileImpl.isConfigureSTAauthType(AAAProfileInt.STA_AUTH_TYPE.ttls)) {
					AaaObj.RadiusServer.Local.STAAuth.Type.Ttls ttlsObj = new AaaObj.RadiusServer.Local.STAAuth.Type.Ttls();
					aaaChildList_4.add(ttlsObj);
					staAuthTypeObj.setTtls(ttlsObj);
				}
				
				/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<leap> */
				oDebug.debug("/configuration/aaa/radius-server/local/STA-auth/type", 
						"leap", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if (!aaaProfileImpl.isConfigureSTAauthType(AAAProfileInt.STA_AUTH_TYPE.leap)) {
					AaaObj.RadiusServer.Local.STAAuth.Type.Leap leapObj = new AaaObj.RadiusServer.Local.STAAuth.Type.Leap();
					aaaChildList_4.add(leapObj);
					staAuthTypeObj.setLeap(leapObj);
				}
			}

			/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<ca-cert> */
			if (childObj instanceof STAAuthCaCert) {
				STAAuthCaCert caCertObj = (STAAuthCaCert) childObj;
				
				/** attribute: operation */
				caCertObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));

				/** attribute: value */
				oDebug.debug("/configuration/aaa/radius-server/local/STA-auth", 
						"ca-cert", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				caCertObj.setValue(aaaProfileImpl.getSTAauthCaCertFile());

				/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<ca-cert>.<server-cert> */
				STAAuthServerCert serverCertObj = new STAAuthServerCert();
				aaaChildList_4.add(serverCertObj);
				caCertObj.setServerCert(serverCertObj);
			}

			/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory> */
			if (childObj instanceof AaaObj.RadiusServer.Local.DbType.ActiveDirectory) {
				AaaObj.RadiusServer.Local.DbType.ActiveDirectory activeDirectoryObj = (AaaObj.RadiusServer.Local.DbType.ActiveDirectory) childObj;

				/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory", 
						"primary", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if(aaaProfileImpl.isConfigActiveDirectory(AAAProfileInt.RADIUS_PRIORITY_TYPE.primary)){
					activeDirectoryObj.setPrimary(this.createActiveDirectoryPrimary(AAAProfileInt.RADIUS_PRIORITY_TYPE.primary));
				}

				/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<backup1> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory", 
						"backup1", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if(aaaProfileImpl.isConfigActiveDirectory(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1)){
					activeDirectoryObj.setBackup1(this.createActiveDirectoryPrimary(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1));
				}
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<backup2> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory", 
						"backup2", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if(aaaProfileImpl.isConfigActiveDirectory(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2)){
					activeDirectoryObj.setBackup2(this.createActiveDirectoryPrimary(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2));
				}
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<backup3> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory", 
						"backup3", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if(aaaProfileImpl.isConfigActiveDirectory(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup3)){
					activeDirectoryObj.setBackup3(this.createActiveDirectoryPrimary(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup3));
				}
			}

//			/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-ldap> */
//			if (childObj instanceof AaaObj.RadiusServer.Local.DbType.OpenLdap) {
//				AaaObj.RadiusServer.Local.DbType.OpenLdap openLdapObj = (AaaObj.RadiusServer.Local.DbType.OpenLdap) childObj;
//
//				/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-ldap>.<primary> */
//				oDebug.debug("/configuration/aaa/radius-server/local/db-type/open-ldap", 
//						"primary", GenerateXMLDebug.CONFIG_ELEMENT,
//						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
//				if(aaaProfileImpl.isConfigOpenLdap(AAAProfileInt.RADIUS_PRIORITY_TYPE.primary)){
//					openLdapObj.setPrimary(this.createOpenLdapPrimary(AAAProfileInt.RADIUS_PRIORITY_TYPE.primary));
//				}
//
//				/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-ldap>.<backup1> */
//				oDebug.debug("/configuration/aaa/radius-server/local/db-type/open-ldap", 
//						"backup1", GenerateXMLDebug.CONFIG_ELEMENT,
//						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
//				if(aaaProfileImpl.isConfigOpenLdap(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1)){
//					openLdapObj.setBackup1(this.createOpenLdapPrimary(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1));
//				}
//				
//				/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-ldap>.<backup2> */
//				oDebug.debug("/configuration/aaa/radius-server/local/db-type/open-ldap", 
//						"backup2", GenerateXMLDebug.CONFIG_ELEMENT,
//						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
//				if(aaaProfileImpl.isConfigOpenLdap(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2)){
//					openLdapObj.setBackup2(this.createOpenLdapPrimary(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2));
//				}
//				
//				/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-ldap>.<backup3> */
//				oDebug.debug("/configuration/aaa/radius-server/local/db-type/open-ldap", 
//						"backup3", GenerateXMLDebug.CONFIG_ELEMENT,
//						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
//				if(aaaProfileImpl.isConfigOpenLdap(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup3)){
//					openLdapObj.setBackup3(this.createOpenLdapPrimary(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup3));
//				}
//			}

			/** element: <aaa>.<radius-server>.<local>.<LDAP-auth>.<primary> */
			if (childObj instanceof LDAPAuthPrimary) {
				LDAPAuthPrimary primaryObj = (LDAPAuthPrimary) childObj;

				/** element: <aaa>.<radius-server>.<local>.<LDAP-auth>.<primary>.<type> */
				LDAPAuthPrimary.Type typeObj = new LDAPAuthPrimary.Type();
				aaaChildList_4.add(typeObj);
				primaryObj.setType(typeObj);
			}
			
			/** element: <aaa>.<radius-server>.<local>.<db-type>.<ldap-server> */
			if(childObj instanceof AaaObj.RadiusServer.Local.DbType.LdapServer){
				AaaObj.RadiusServer.Local.DbType.LdapServer ldapServerObj = (AaaObj.RadiusServer.Local.DbType.LdapServer)childObj;
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<primary> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/ldap-server", 
						"primary", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if(aaaProfileImpl.isConfigOpenLdap(AAAProfileInt.RADIUS_PRIORITY_TYPE.primary)){
					ldapServerObj.setPrimary(createAaaLdapServer(AAAProfileInt.RADIUS_PRIORITY_TYPE.primary));
				}
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<backup1> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/ldap-server", 
						"backup1", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if(aaaProfileImpl.isConfigOpenLdap(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1)){
					ldapServerObj.setBackup1(createAaaLdapServer(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1));
				}
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<backup2> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/ldap-server", 
						"backup2", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if(aaaProfileImpl.isConfigOpenLdap(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2)){
					ldapServerObj.setBackup2(createAaaLdapServer(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2));
				}
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<backup3> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/ldap-server", 
						"backup3", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if(aaaProfileImpl.isConfigOpenLdap(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup3)){
					ldapServerObj.setBackup3(createAaaLdapServer(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup3));
				}
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<sub-type> */
				AaaObj.RadiusServer.Local.DbType.LdapServer.SubType subTypeObj = new AaaObj.RadiusServer.Local.DbType.LdapServer.SubType();
				aaaChildList_4.add(subTypeObj);
				ldapServerObj.setSubType(subTypeObj);
			}

			/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-directory> */
			if(childObj instanceof AaaObj.RadiusServer.Local.DbType.OpenDirectory){
				AaaObj.RadiusServer.Local.DbType.OpenDirectory openDirectoryObj = (AaaObj.RadiusServer.Local.DbType.OpenDirectory)childObj;
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-directory>.<primary> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/open-directory", 
						"primary", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if(aaaProfileImpl.isConfigOpenDirectory(AAAProfileInt.RADIUS_PRIORITY_TYPE.primary)){
					openDirectoryObj.setPrimary(this.createOpenDirectoryServer(AAAProfileInt.RADIUS_PRIORITY_TYPE.primary));
				}
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-directory>.<backup1> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/open-directory", 
						"backup1", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if(aaaProfileImpl.isConfigOpenDirectory(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1)){
					openDirectoryObj.setBackup1(this.createOpenDirectoryServer(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1));
				}
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-directory>.<backup2> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/open-directory", 
						"backup2", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if(aaaProfileImpl.isConfigOpenDirectory(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2)){
					openDirectoryObj.setBackup2(this.createOpenDirectoryServer(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2));
				}
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-directory>.<backup3> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/open-directory", 
						"backup3", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if(aaaProfileImpl.isConfigOpenDirectory(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup3)){
					openDirectoryObj.setBackup3(this.createOpenDirectoryServer(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup3));
				}
			}
			
			/** element: <aaa>.<radius-server>.<proxy>.<realm>.<format> */
			if(childObj instanceof RadiusRealmFormat){
				RadiusRealmFormat formatObj = (RadiusRealmFormat)childObj;
				
				/** attribute: operation */
				formatObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** attribute: value */
				oDebug.debug("/configuration/aaa/radius-server/proxy/realm", 
						"format", GenerateXMLDebug.SET_VALUE,
						null, null);
				formatObj.setValue(aaaProfileImpl.getProxyFormat());
			}
			
			/** element: <aaa>.<radius-server>.<local>.<db-type>.<library-sip-server> */
			if(childObj instanceof AaaObj.RadiusServer.Local.DbType.LibrarySipServer){
				AaaObj.RadiusServer.Local.DbType.LibrarySipServer sipServer = (AaaObj.RadiusServer.Local.DbType.LibrarySipServer)childObj;
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<library-sip-server>.<primary> */
				RadiusLibrarySipServer pSipServer = new RadiusLibrarySipServer();
				aaaChildList_4.add(pSipServer);
				sipServer.setPrimary(pSipServer);
			}
		}
		aaaChildList_3.clear();
		generateAAALevel_5();
	}

	private void generateAAALevel_5() throws Exception {
		/**
		 * <aaa>.<radius-server>.<local>.<STA-auth>.<ca-cert>.<server-cert>			STAAuthServerCert
		 * <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<tls>					STAAuthTls
		 * <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<sub-type>			AaaObj.RadiusServer.Local.DbType.LdapServer.SubType
		 * <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<peap>					AaaObj.RadiusServer.Local.STAAuth.Type.Peap
		 * <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<ttls>					AaaObj.RadiusServer.Local.STAAuth.Type.Ttls
		 * <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<leap>					AaaObj.RadiusServer.Local.STAAuth.Type.Leap
		 * <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<cr>						STAAuthPositiveType
		 * <aaa>.<radius-server>.<local>.<db-type>.<library-sip-server>.<primary>	RadiusLibrarySipServer
		 * <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<md5>					StaAuthBasicType
		 */
		for (Object childObj : aaaChildList_4) {

			/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<ca-cert>.<server-cert> */
			if (childObj instanceof STAAuthServerCert) {
				STAAuthServerCert serverCertObj = (STAAuthServerCert) childObj;

				/** attribute: value */
				oDebug.debug("/configuration/aaa/radius-server/local/STA-auth/ca-cert", 
						"server-cert", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				serverCertObj.setValue(aaaProfileImpl.getSTAauthServerCertFile());

				/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<ca-cert>.<server-cert>.<private-key> */
				STAAuthPrivateKey privateKeyObj = new STAAuthPrivateKey();
				aaaChildList_5.add(privateKeyObj);
				serverCertObj.setPrivateKey(privateKeyObj);
			}
			
			/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<tls> */
			if(childObj instanceof STAAuthTls){
				STAAuthTls tlsObj = (STAAuthTls)childObj;
				
				/** attribute: opreation */
				oDebug.debug("/configuration/aaa/radius-server/local/STA-auth/type", 
						"tls", GenerateXMLDebug.SET_OPERATION,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				tlsObj.setOperation(CLICommonFunc.getAhEnumAct(aaaProfileImpl.isConfigureSTAauthType(AAAProfileInt.STA_AUTH_TYPE.tls)));
				
				if(aaaProfileImpl.isConfigureSTAauthType(AAAProfileInt.STA_AUTH_TYPE.tls)){
					
					/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<tls>.<cr> */
					tlsObj.setCr("");
					
					/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<tls>.<check-cert-cn> */
					oDebug.debug("/configuration/aaa/radius-server/local/STA-auth/type/tls", 
							"check-cert-cn", GenerateXMLDebug.SET_OPERATION,
							aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
					tlsObj.setCheckCertCn(CLICommonFunc.getAhOnlyAct(aaaProfileImpl.isEnableTlsCheckCertCn()));
					
					/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<tls>.<check-in-db> */
					oDebug.debug("/configuration/aaa/radius-server/local/STA-auth/type/tls", 
							"check-in-db", GenerateXMLDebug.SET_OPERATION,
							aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
					tlsObj.setCheckInDb(CLICommonFunc.getAhOnlyAct(aaaProfileImpl.isEnableTlsCheckInDb()));
				}
			}
			
			/** element: <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<sub-type> */
			if(childObj instanceof AaaObj.RadiusServer.Local.DbType.LdapServer.SubType){
				AaaObj.RadiusServer.Local.DbType.LdapServer.SubType subTypeObj = (AaaObj.RadiusServer.Local.DbType.LdapServer.SubType)childObj;
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<sub-type>.<edirectory> */
				AaaObj.RadiusServer.Local.DbType.LdapServer.SubType.Edirectory edirServerObj = new AaaObj.RadiusServer.Local.DbType.LdapServer.SubType.Edirectory();
				aaaChildList_5.add(edirServerObj);
				subTypeObj.setEdirectory(edirServerObj);
			}
			
			/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<md5> */
			if(childObj instanceof StaAuthBasicType){
				StaAuthBasicType md5Type = (StaAuthBasicType)childObj;
				
				md5Type.setCr(CLICommonFunc.getAhOnlyAct(false));
			}
			
			/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<peap> */
			if(childObj instanceof AaaObj.RadiusServer.Local.STAAuth.Type.Peap){
				AaaObj.RadiusServer.Local.STAAuth.Type.Peap peapObj = (AaaObj.RadiusServer.Local.STAAuth.Type.Peap)childObj;
				
				/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<peap>.<cr> */
				peapObj.setCr(CLICommonFunc.getAhOnlyAct(false));
			}
			
			/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<ttls>	*/
			if(childObj instanceof AaaObj.RadiusServer.Local.STAAuth.Type.Ttls){
				AaaObj.RadiusServer.Local.STAAuth.Type.Ttls ttlsObj = (AaaObj.RadiusServer.Local.STAAuth.Type.Ttls)childObj;
				
				/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<ttls>.<cr> */
				ttlsObj.setCr(CLICommonFunc.getAhOnlyAct(false));
			}
			
			/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<leap>	*/
			if(childObj instanceof AaaObj.RadiusServer.Local.STAAuth.Type.Leap){
				AaaObj.RadiusServer.Local.STAAuth.Type.Leap leapObj = (AaaObj.RadiusServer.Local.STAAuth.Type.Leap)childObj;
				
				/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<leap>.<cr> */
				leapObj.setCr(CLICommonFunc.getAhOnlyAct(false));
			}
			
			/** <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<cr> */
			if(childObj instanceof STAAuthPositiveType){
				STAAuthPositiveType crObj = (STAAuthPositiveType)childObj;
				
				/** <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<cr>.<peap> */
				oDebug.debug("/configuration/aaa/radius-server/local/STA-auth/type/cr", 
						"peap", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if (aaaProfileImpl.isConfigureSTAauthType(AAAProfileInt.STA_AUTH_TYPE.peap)) {
					crObj.setPeap(this.createSTAAuthChecks(aaaProfileImpl.isEnablePeapCheckDb()));
				}
				
				/** <aaa>.<radius-server>.<local>.<STA-auth>.<type>.<cr>.<ttls> */
				oDebug.debug("/configuration/aaa/radius-server/local/STA-auth/type/cr", 
						"ttls", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if (aaaProfileImpl.isConfigureSTAauthType(AAAProfileInt.STA_AUTH_TYPE.ttls)) {
					crObj.setTtls(this.createSTAAuthChecks(aaaProfileImpl.isEnableTtlsCheckDb()));
				}
			}
			
			/** element: <aaa>.<radius-server>.<local>.<db-type>.<library-sip-server>.<primary>	*/
			if(childObj instanceof RadiusLibrarySipServer){
				RadiusLibrarySipServer pSipServer = (RadiusLibrarySipServer)childObj;
				
				/** attribute: operation */
				pSipServer.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<library-sip-server>.<primary>.<server> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/library-sip-server/primary", 
						"server", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				pSipServer.setServer(CLICommonFunc.createAhStringActObj(aaaProfileImpl.getLibrarySipService(), CLICommonFunc.getYesDefault()));
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<library-sip-server>.<primary>.<port> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/library-sip-server/primary", 
						"port", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				Object[][] portParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, aaaProfileImpl.getLibrarySipPort()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				pSipServer.setPort((LibrarySipServerPort)CLICommonFunc.createObjectWithName(LibrarySipServerPort.class, portParm));
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<library-sip-server>.<primary>.<login-enable> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/library-sip-server/primary", 
						"login-enable", GenerateXMLDebug.SET_OPERATION,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				pSipServer.setLoginEnable(CLICommonFunc.getAhOnlyAct(aaaProfileImpl.isLibrarySipLoginEnable()));
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<library-sip-server>.<primary>.<login-user> */
				if(aaaProfileImpl.isLibrarySipLoginEnable()){
					LibrarySipServerLoginUser userObj = new LibrarySipServerLoginUser();
					aaaChildList_5.add(userObj);
					pSipServer.setLoginUser(userObj);
				}
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<library-sip-server>.<primary>.<institution-id> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/library-sip-server/primary", 
						"institution-id", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				pSipServer.setInstitutionId(CLICommonFunc.createAhStringActObj(aaaProfileImpl.getLibrarySipInstitutionId(), CLICommonFunc.getYesDefault()));
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<library-sip-server>.<primary>.<separator> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/library-sip-server/primary", 
						"separator", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				pSipServer.setSeparator(CLICommonFunc.createAhStringActObj(aaaProfileImpl.getLibrarySipSeparator(), CLICommonFunc.getYesDefault()));
			}
		}
		aaaChildList_4.clear();
		generateAAALevel_6();
	}

	private void generateAAALevel_6() throws Exception {
		/**
		 * <aaa>.<radius-server>.<local>.<STA-auth>.<ca-cert>.<server-cert>.<private-key>		STAAuthPrivateKey
		 * <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<sub-type>.<edirectory>		AaaObj.RadiusServer.Local.DbType.LdapServer.SubType.Edirectory
		 * <aaa>.<radius-server>.<local>.<db-type>.<library-sip-server>.<primary>.<login-user>	LibrarySipServerLoginUser
		 */
		for (Object childObj : aaaChildList_5) {

			/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<ca-cert>.<server-cert>.<private-key> */
			if (childObj instanceof STAAuthPrivateKey) {
				STAAuthPrivateKey privateKeyObj = (STAAuthPrivateKey) childObj;

				/** attribute: value */
				oDebug.debug("/configuration/aaa/radius-server/local/STA-auth/ca-cert/server-cert", 
						"private-key", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				privateKeyObj.setValue(aaaProfileImpl.getSTAauthPrivateKey());

				/** element: <aaa>.<radius-server>.<local>.<STA-auth>.<ca-cert>.<server-cert>.<private-key>.<private-key-password> */
				oDebug.debug("/configuration/aaa/radius-server/local/STA-auth/ca-cert/server-cert/private-key", 
						"private-key-password", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if(aaaProfileImpl.isConfigSTAauthPrivateKeyPassword()){
					
					oDebug.debug("/configuration/aaa/radius-server/local/STA-auth/ca-cert/server-cert/private-key", 
							"private-key-password", GenerateXMLDebug.SET_VALUE,
							aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
					privateKeyObj.setPrivateKeyPassword(
							CLICommonFunc.createAhEncryptedString(aaaProfileImpl
									.getSTAauthPrivateKeyPassword()));
				}
			}
			
			/** element: <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<sub-type>.<edirectory>	*/
			if(childObj instanceof AaaObj.RadiusServer.Local.DbType.LdapServer.SubType.Edirectory){
				AaaObj.RadiusServer.Local.DbType.LdapServer.SubType.Edirectory edirServerObj = (AaaObj.RadiusServer.Local.DbType.LdapServer.SubType.Edirectory)childObj;
				
				/** attribute: operation */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/ldap-server/sub-type", 
						"edirectory", GenerateXMLDebug.SET_OPERATION,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				edirServerObj.setOperation(CLICommonFunc.getAhEnumAct(aaaProfileImpl.isEnableEdirServer()));
				
				if(aaaProfileImpl.isEnableEdirServer()){
					
					/** element: <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<sub-type>.<edirectory>.<cr> */
					edirServerObj.setCr("");
					
					/** element: <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<sub-type>.<edirectory>.<acct-policy-check> */
					oDebug.debug("/configuration/aaa/radius-server/local/db-type/ldap-server/sub-type/edirectory", 
							"acct-policy-check", GenerateXMLDebug.SET_OPERATION,
							aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
					edirServerObj.setAcctPolicyCheck(CLICommonFunc.getAhOnlyAct(aaaProfileImpl.isEnablePolicyCheck()));
				}
			}
			
			/** element: <aaa>.<radius-server>.<local>.<db-type>.<library-sip-server>.<primary>.<login-user> */
			if(childObj instanceof LibrarySipServerLoginUser){
				LibrarySipServerLoginUser loginUserObj = (LibrarySipServerLoginUser)childObj;
				
				/** attribute: operation */
				loginUserObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** attribute: value */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/library-sip-server/primary", 
						"login-user", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				loginUserObj.setValue(aaaProfileImpl.getLibrarySipUserName());
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<library-sip-server>.<primary>.<login-user>.<password> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/library-sip-server/primary/login-user", 
						"password", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				loginUserObj.setPassword(CLICommonFunc.createAhEncryptedString(aaaProfileImpl.getLibrarySipPassword()));
			}
		}
	}
	
	private RadiusAccountingServer createRadiusAccountingServer(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws Exception{
		RadiusAccountingServer account = new RadiusAccountingServer();
		setRadiusAccountingServer(account, priorityType);
		return account;
	}
	
	private void setRadiusAccountingServer(RadiusAccountingServer serverType, AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws Exception{
		
		/** attribute: value */
		serverType.setValue(aaaProfileImpl.getAcctServerIp(priorityType));
		
		/** attribute: operation */
		serverType.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc
				.getYesDefault()));
		
		/** element: <shared-secret> */
		if(aaaProfileImpl.isConfigAcctSharedSecret(priorityType)){
			serverType.setSharedSecret(CLICommonFunc.createAhEncryptedStringAct(
					aaaProfileImpl.getAcctSharedSecret(priorityType), CLICommonFunc.getYesDefault()));
		}
		
		/** element: <acct-port> */
		if (aaaProfileImpl.isConfigureAcctRadAcctPort(priorityType)) {
			Object[][] acctPortParm = { 
					{CLICommonFunc.ATTRIBUTE_VALUE, aaaProfileImpl.getAcctAcctPort(priorityType)},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			serverType.setAcctPort(
					(AaaAcctPort) CLICommonFunc.createObjectWithName(AaaAcctPort.class,acctPortParm)
			);
		}
		
		/** element: <via-vpn-tunnel> */
		if(aaaProfileImpl.isEnableVpnTunnel(serverType.getValue())){
			serverType.setViaVpnTunnel(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
	}

	private RadiusServerType createRadiusServerType(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws Exception {
		RadiusServerType serverType = new RadiusServerType();
		setRadiusServerType(serverType, priorityType);
		return serverType;
	}

	private void setRadiusServerType(RadiusServerType serverType,
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws Exception {

		/** attribute: value */
		serverType.setValue(aaaProfileImpl.getServerIp(priorityType));

		/** attribute: operation */
		serverType.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc
				.getYesDefault()));

		/** element: <old-shared-secret> */
		if(aaaProfileImpl.isConfigSharedSecretOld(priorityType)){
			RadiusServerType.OldSharedSecret oldSharedSecretObj = new RadiusServerType.OldSharedSecret();
			serverType.setOldSharedSecret(oldSharedSecretObj);
			setSharedSecret(oldSharedSecretObj, priorityType);
		}
		
		/** element: <shared-secret> */
		if(aaaProfileImpl.isConfigSharedSecret(priorityType)){
			serverType.setSharedSecret(CLICommonFunc.createAhEncryptedStringAct(
					aaaProfileImpl.getSharedSecret(priorityType), CLICommonFunc.getYesDefault()));
		}
		
		/** element: <auth-port> */
		Object[][] authPortParm = { 
				{CLICommonFunc.ATTRIBUTE_VALUE, aaaProfileImpl.getAuthPort(priorityType)},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		serverType.setAuthPort(
				(AaaAuthPort) CLICommonFunc.createObjectWithName(AaaAuthPort.class,authPortParm)
		);
		
		/** element: <acct-port> */
		if (aaaProfileImpl.isConfigureAcctPort(priorityType)) {
			Object[][] acctPortParm = { 
					{CLICommonFunc.ATTRIBUTE_VALUE, aaaProfileImpl.getAcctPort(priorityType)},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			serverType.setAcctPort(
					(AaaAcctPort) CLICommonFunc.createObjectWithName(AaaAcctPort.class,acctPortParm)
			);
		}
		
		/** element: <via-vpn-tunnel> */
		if(aaaProfileImpl.isEnableVpnTunnel(serverType.getValue())){
			serverType.setViaVpnTunnel(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
	}

	private void setSharedSecret(RadiusServerType.OldSharedSecret oldSharedSecretObj,
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws Exception {

		/** attribute: value */
		oldSharedSecretObj.setValue(AhConfigUtil.hiveApCommonEncrypt(aaaProfileImpl.getSharedSecret(priorityType)));
		
		/** attribute: encrypted */
		oldSharedSecretObj.setEncrypted(oldSharedSecretObj.getEncrypted());

		/** element: <auth-port> */
		Object[][] authPortParm = { { CLICommonFunc.ATTRIBUTE_VALUE,
				aaaProfileImpl.getAuthPort(priorityType) } };
		oldSharedSecretObj
				.setAuthPort((RadiusServerType.OldSharedSecret.AuthPort) CLICommonFunc
						.createObjectWithName(
								RadiusServerType.OldSharedSecret.AuthPort.class,
								authPortParm));

		/** element: <acct-port> */
		if (aaaProfileImpl.isConfigureAcctPort(priorityType)) {
			Object[][] acctPortParm = { { CLICommonFunc.ATTRIBUTE_VALUE,
					aaaProfileImpl.getAcctPort(priorityType) } };
			oldSharedSecretObj
					.setAcctPort((RadiusServerType.OldSharedSecret.AcctPort) CLICommonFunc
							.createObjectWithName(
									RadiusServerType.OldSharedSecret.AcctPort.class,
									acctPortParm));
		}
	}

	private AaaObj.RadiusServer.Local.Nas createNASObj(int index)
			throws CreateXMLException, IOException {
		AaaObj.RadiusServer.Local.Nas localNasObj = new AaaObj.RadiusServer.Local.Nas();

		/** attribute: name */
		oDebug.debug("/configuration/aaa/radius-server/local", 
				"nas", GenerateXMLDebug.SET_NAME,
				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
		localNasObj.setName(aaaProfileImpl.getNASIpAddress(index));

		/** attribute: operation */
		localNasObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc
				.getYesDefault()));

		/** element: <shared-key> */
		if(aaaProfileImpl.isConfigNASSharedKey(index)){
			oDebug.debug("/configuration/aaa/radius-server/local/nas[@name='"+localNasObj.getName()+"']", 
					"shared-key", GenerateXMLDebug.SET_VALUE,
					aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
			localNasObj.setSharedKey(
					CLICommonFunc.createAhEncryptedString(aaaProfileImpl.getNASSharedKey(index))
			);
		}
		
		/** element: <tls> */
		if(aaaProfileImpl.isEnableNasTls(index)){
			localNasObj.setTls(CLICommonFunc.getAhOnlyAct(aaaProfileImpl.isEnableNasTls(index)));
		}
		
		if(localNasObj.getSharedKey() == null && localNasObj.getTls() == null){
			return null;
		}else{
			return localNasObj;
		}
	}

	private AaaObj.RadiusServer.Local.UserGroup createUserGroupObj(int index)
			throws Exception {

		AaaObj.RadiusServer.Local.UserGroup userGroupObj = new AaaObj.RadiusServer.Local.UserGroup();

		/** attribute: name */
		oDebug.debug("/configuration/aaa/radius-server/local", 
				"user-group", GenerateXMLDebug.SET_NAME,
				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
		userGroupObj.setName(aaaProfileImpl.getLocalUserGroupName(index));

		/** attribute: operation */
		userGroupObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <cr> */
		userGroupObj.setCr("");

		/** element: <reauth-time> */
		oDebug.debug("/configuration/aaa/radius-server/local/user-group[@name='"+userGroupObj.getName()+"']", 
				"reauth-time", GenerateXMLDebug.CONFIG_ELEMENT,
				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
		if(aaaProfileImpl.isConfigUserGroupReauthTime(index)){
			
			oDebug.debug("/configuration/aaa/radius-server/local/user-group[@name='"+userGroupObj.getName()+"']", 
					"reauth-time", GenerateXMLDebug.SET_VALUE,
					aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
			Object[][] reauthTimeParm = { 
					{CLICommonFunc.ATTRIBUTE_VALUE,aaaProfileImpl.getUserGroupReauthTime(index)},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			userGroupObj
					.setReauthTime((AaaObj.RadiusServer.Local.UserGroup.ReauthTime) CLICommonFunc
							.createObjectWithName(
									AaaObj.RadiusServer.Local.UserGroup.ReauthTime.class,
									reauthTimeParm));
		}

		/** element: <user-profile-attr> */
		oDebug.debug("/configuration/aaa/radius-server/local/user-group[@name='"+userGroupObj.getName()+"']", 
				"user-profile-attr", GenerateXMLDebug.CONFIG_ELEMENT,
				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
		if(aaaProfileImpl.isConfigUserGroupProfileAttr(index)){
			
			oDebug.debug("/configuration/aaa/radius-server/local/user-group[@name='"+userGroupObj.getName()+"']", 
					"user-profile-attr", GenerateXMLDebug.SET_VALUE,
					aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
			Object[][] userProfileAttrParm = { 
					{CLICommonFunc.ATTRIBUTE_VALUE,aaaProfileImpl.getUserGroupProfileAttr(index) },
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			userGroupObj
					.setUserProfileAttr((AaaObj.RadiusServer.Local.UserGroup.UserProfileAttr) CLICommonFunc
							.createObjectWithName(
									AaaObj.RadiusServer.Local.UserGroup.UserProfileAttr.class,
									userProfileAttrParm));
		}
		

		/** element: <vlan-id> */
		oDebug.debug("/configuration/aaa/radius-server/local/user-group[@name='"+userGroupObj.getName()+"']", 
				"vlan-id", GenerateXMLDebug.CONFIG_ELEMENT,
				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
		if(aaaProfileImpl.isConfigUserGroupVlanId(index)){
			
			oDebug.debug("/configuration/aaa/radius-server/local/user-group[@name='"+userGroupObj.getName()+"']", 
					"vlan-id", GenerateXMLDebug.SET_VALUE,
					aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
			Object[][] vlanIdParm = { 
					{CLICommonFunc.ATTRIBUTE_VALUE,aaaProfileImpl.getUserGroupVlanId(index)},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			userGroupObj
					.setVlanId((AaaObj.RadiusServer.Local.UserGroup.VlanId) CLICommonFunc
							.createObjectWithName(
									AaaObj.RadiusServer.Local.UserGroup.VlanId.class,
									vlanIdParm));
		}
		

		return userGroupObj;
	}

//	private AaaObj.RadiusServer.Local.UserName createUserName(int index) throws IOException {
//		AaaObj.RadiusServer.Local.UserName userNameObj = new AaaObj.RadiusServer.Local.UserName();
//
//		/** attribute: name */
//		oDebug.debug("/configuration/aaa/radius-server/local", 
//				"user-name", GenerateXMLDebug.SET_NAME,
//				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
//		userNameObj.setName(aaaProfileImpl.getLocalUserName(index));
//
//		/** attribute: operation */
//		userNameObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
//
//		/** element: <password> */
//		AaaObj.RadiusServer.Local.UserName.Password passwordObj = new AaaObj.RadiusServer.Local.UserName.Password();
//		userNameObj.setPassword(passwordObj);
//		// set passwordObj
//		/** attribute: value */
//		oDebug.debug("/configuration/aaa/radius-server/local/user-name[@name='"+userNameObj.getName()+"']", 
//				"password", GenerateXMLDebug.SET_VALUE,
//				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
//		passwordObj.setValue(aaaProfileImpl.getLocalUserPassword(index));
//		passwordObj.setEncrypted(passwordObj.getEncrypted());
//
//		/** element: <user-group> */
//		oDebug.debug("/configuration/aaa/radius-server/local/user-name[@name='"+userNameObj.getName()+"']", 
//				"user-group", GenerateXMLDebug.CONFIG_ELEMENT,
//				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
//		if (aaaProfileImpl.isLocalUserBindUserGroup(index)) {
//			
//			oDebug.debug("/configuration/aaa/radius-server/local/user-name[@name='"+userNameObj.getName()+"']", 
//					"user-group", GenerateXMLDebug.SET_VALUE,
//					aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
//			passwordObj.setUserGroup(CLICommonFunc.getAhString(aaaProfileImpl
//					.getLocalUserBindUserGroup(index)));
//		}
//
//		return userNameObj;
//	}
	
	private ActiveDirectoryPrimary createActiveDirectoryPrimary(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws Exception{
		ActiveDirectoryPrimary primaryObj = new ActiveDirectoryPrimary();

		/** attribute: operation */
		primaryObj.setOperation(CLICommonFunc
				.getAhEnumAct(CLICommonFunc.getYesDefault()));

		/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<server> */
		ActiveDirectoryServer serverObj = new ActiveDirectoryServer();
		priorityList_1.add(serverObj);
		primaryObj.setServer(serverObj);
		
//		/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<workgroup> */
//		ActiveDirectoryWorkgroup workgroupObj = new ActiveDirectoryWorkgroup();
//		priorityList_1.add(workgroupObj);
//		primaryObj.setWorkgroup(workgroupObj);
		
//		/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<basedn> */
//		oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name(), 
//				"basedn", GenerateXMLDebug.CONFIG_ELEMENT,
//				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
//		if(aaaProfileImpl.isConfigActiveBasedn(priorityType)){
//			
//			oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name(), 
//					"basedn", GenerateXMLDebug.SET_VALUE,
//					aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
//			primaryObj.setBasedn(CLICommonFunc.createAhStringActObj(
//					aaaProfileImpl.getActiveBasedn(priorityType), CLICommonFunc.getYesDefault()));
//		}
		
//		/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<identity> */
//		oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name(), 
//				"identity", GenerateXMLDebug.CONFIG_ELEMENT,
//				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
//		if(aaaProfileImpl.isConfigActiveIdentity(priorityType)){
//			
//			oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name(), 
//					"identity", GenerateXMLDebug.SET_VALUE,
//					aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
//			primaryObj.setIdentity(CLICommonFunc.createAhStringActObj(
//					aaaProfileImpl.getActiveIdentity(priorityType), CLICommonFunc.getYesDefault()));
//		}
		
		/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<computer-ou> */
		oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name(), 
				"computer-ou", GenerateXMLDebug.CONFIG_ELEMENT,
				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
		if(aaaProfileImpl.isConfigActiveComputerOu(priorityType)){
			
			oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name(), 
					"computer-ou", GenerateXMLDebug.SET_VALUE,
					aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
			primaryObj.setComputerOu(CLICommonFunc.createAhStringActObj(
					aaaProfileImpl.getActiveComputerOu(priorityType), CLICommonFunc.getYesDefault()));
		}
		
		/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<login> */
		oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name(), 
				"login", GenerateXMLDebug.CONFIG_ELEMENT,
				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
		if(aaaProfileImpl.isConfigADLogin(priorityType)){
			ActiveDirectoryLoginAdmin loginObj = new ActiveDirectoryLoginAdmin();
			priorityList_1.add(loginObj);
			primaryObj.setLogin(loginObj);
		}
		
		/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<tls-enable> */
		oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name(), 
				"tls-enable", GenerateXMLDebug.SET_OPERATION,
				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
		primaryObj.setTlsEnable(CLICommonFunc.getAhOnlyAct(aaaProfileImpl.isEnableTls(priorityType)));
		
		/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<domain> */
		oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name(), 
				"domain", GenerateXMLDebug.CONFIG_ELEMENT,
				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
		for(int i=0; i<aaaProfileImpl.getADDomainSize(priorityType); i++){
			primaryObj.getDomain().add(createADDomain(priorityType, i));
		}
		
		/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<global-catalog> */
		oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name(), 
				"global-catalog", GenerateXMLDebug.SET_OPERATION,
				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
		primaryObj.setGlobalCatalog(CLICommonFunc.getAhOnlyAct(aaaProfileImpl.isEnableGlobalCatalog(priorityType)));
		
		/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<sasl-wrapping> */
		String saslWrappingValue = aaaProfileImpl.getSaslWrappingValue(priorityType);
		if (saslWrappingValue != null && !saslWrappingValue.equals("")) {
			primaryObj.setSaslWrapping(CLICommonFunc.createAhStringActObj(saslWrappingValue, true));
		}

		generatePriorityLevel_1(priorityType);
		return primaryObj;
	}
	
	private LDAPAuthPrimary createLDAPAuthPrimary(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws Exception{
		LDAPAuthPrimary primaryObj = new LDAPAuthPrimary();
		
		/** attribute: operation */
		primaryObj.setOperation(CLICommonFunc
				.getAhEnumAct(CLICommonFunc.getYesDefault()));
		
		/** element: <LDAP-auth>.<type> */
		LDAPAuthPrimary.Type typeObj = new LDAPAuthPrimary.Type();
		priorityList_1.add(typeObj);
		primaryObj.setType(typeObj);
		
		generatePriorityLevel_1(priorityType);
		return primaryObj;
	}
	
	private AaaLdapServer createAaaLdapServer(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws Exception{
		AaaLdapServer ldapServerObj = new AaaLdapServer();
		
		/** attribute: operation */
		ldapServerObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
		
		/** element: <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<primary|backup1|2|3>.<server> */
		LdapServerName serverNameObj = new LdapServerName();
		priorityList_1.add(serverNameObj);
		ldapServerObj.setServer(serverNameObj);

		/** element: <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<primary|backup1|2|3>.<basedn> */
		oDebug.debug("/configuration/aaa/radius-server/local/db-type/ldap-server/"+priorityType.name(), 
				"basedn", GenerateXMLDebug.SET_VALUE,
				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
		ldapServerObj.setBasedn(CLICommonFunc.createAhStringActObj(aaaProfileImpl.getOpenLdapBasedn(priorityType), true));
		
		/** element: <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<primary|backup1|2|3>.<binddn> */
		LdapServerIdentity binddnObj = new LdapServerIdentity();
		priorityList_1.add(binddnObj);
		ldapServerObj.setBinddn(binddnObj);
		
		/** element: <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<primary|backup1|2|3>.<port> */
		oDebug.debug("/configuration/aaa/radius-server/local/db-type/ldap-server/"+priorityType.name(), 
				"port", GenerateXMLDebug.SET_VALUE,
				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
		Object[][] portParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, aaaProfileImpl.getOpenLdapPort(priorityType)},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		ldapServerObj.setPort(
				(LdapServerPort)CLICommonFunc.createObjectWithName(LdapServerPort.class, portParm)
		);
		
		/** element: <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<primary|backup1|2|3>.<filterAttr> */
		oDebug.debug("/configuration/aaa/radius-server/local/db-type/ldap-server/"+priorityType.name(), 
				"filterAttr", GenerateXMLDebug.CONFIG_ELEMENT,
				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
		if(aaaProfileImpl.isConfigLdapFilterAttr(priorityType)){
			
			oDebug.debug("/configuration/aaa/radius-server/local/db-type/ldap-server/"+priorityType.name(), 
					"filterAttr", GenerateXMLDebug.SET_VALUE,
					aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
			ldapServerObj.setFilterAttr(CLICommonFunc.createAhStringActObj(aaaProfileImpl.getLdapFilterAttr(priorityType), true));
			
			ldapServerObj.setOldFilterAttr(CLICommonFunc.createAhStringActObj(aaaProfileImpl.getLdapFilterAttr(priorityType), true));
		}
		
		/** element: <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<primary|backup1|2|3>.<protocol> */
		oDebug.debug("/configuration/aaa/radius-server/local/db-type/ldap-server/"+priorityType.name(), 
				"protocol", GenerateXMLDebug.SET_VALUE,
				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
		Object[][] protocolParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, aaaProfileImpl.getOpenLdapProtocol(priorityType)},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		ldapServerObj.setProtocol(
				(LdapServerProtocol)CLICommonFunc.createObjectWithName(LdapServerProtocol.class, protocolParm)
		);
		
		/** element: <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<primary|backup1|2|3>.<no-strip-filter> */
		oDebug.debug("/configuration/aaa/radius-server/local/db-type/ldap-server/"+priorityType.name(), 
				"no-strip-filter", GenerateXMLDebug.SET_VALUE,
				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
		
		ldapServerObj.setNoStripFilter(CLICommonFunc.getAhOnlyAct(aaaProfileImpl.isEnableStripFilter(priorityType)));
		
		generatePriorityLevel_1(priorityType);
		
		return ldapServerObj;
	}
	
	private OpenDirectoryServer createOpenDirectoryServer(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws Exception{
		OpenDirectoryServer openDirectObj = new OpenDirectoryServer();
		
		/** attribute: operation */
		openDirectObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
		
		/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-directory>.<primary|backup1|2|3>.<admin-user> */
		oDebug.debug("/configuration/aaa/radius-server/local/db-type/open-directory/"+priorityType.name(), 
				"admin-user", GenerateXMLDebug.CONFIG_ELEMENT,
				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
		if(aaaProfileImpl.isConfigOpenDirectoryUser(priorityType)){
			OpenDirectoryUser userObj = new OpenDirectoryUser();
			priorityList_1.add(userObj);
			openDirectObj.setAdminUser(userObj);
		}
		
		/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-directory>.<primary|backup1|2|3>.<tlsEnable> */
		oDebug.debug("/configuration/aaa/radius-server/local/db-type/open-directory/"+priorityType.name(), 
				"tlsEnable", GenerateXMLDebug.SET_OPERATION,
				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
		openDirectObj.setTlsEnable(CLICommonFunc.getAhOnlyAct(aaaProfileImpl.isOpenDirectoryTlsEnable(priorityType)));
		
		/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-directory>.<primary|backup1|2|3>.<domain> */
		OpenDirectoryDomain domainObj = new OpenDirectoryDomain();
		priorityList_1.add(domainObj);
		openDirectObj.setDomain(domainObj);
		
		/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-directory>.<primary|backup1|2|3>.<filter-attr> */
		oDebug.debug("/configuration/aaa/radius-server/local/db-type/open-directory/"+priorityType.name(), 
				"filter-attr", GenerateXMLDebug.CONFIG_ELEMENT,
				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
		if(aaaProfileImpl.isConfigLdapFilterAttr(priorityType)){
			
			oDebug.debug("/configuration/aaa/radius-server/local/db-type/open-directory/"+priorityType.name(), 
					"filter-attr", GenerateXMLDebug.SET_VALUE,
					aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
			openDirectObj.setFilterAttr(CLICommonFunc.createAhStringActObj(aaaProfileImpl.getLdapFilterAttr(priorityType), true));
		}
		
		/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-directory>.<primary|backup1|2|3>.<no-strip-filter> */
		oDebug.debug("/configuration/aaa/radius-server/local/db-type/open-directory/"+priorityType.name(), 
				"no-strip-filter", GenerateXMLDebug.SET_VALUE,
				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
		
		openDirectObj.setNoStripFilter(CLICommonFunc.getAhOnlyAct(aaaProfileImpl.isEnableStripFilter(priorityType)));
		
		generateOpenDirectoryServer_1(priorityType);
		return openDirectObj;
	}
	
	private void generateOpenDirectoryServer_1(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws Exception{
		/**
		 * <aaa>.<radius-server>.<local>.<db-type>.<open-directory>.<primary|backup1|2|3>.<admin-user>			OpenDirectoryUser
		 * <aaa>.<radius-server>.<local>.<db-type>.<open-directory>.<primary|backup1|2|3>.<domain>				OpenDirectoryDomain
		 */
		for(Object childObj : priorityList_1){
			
			/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-directory>.<primary|backup1|2|3>.<admin-user> */
			if(childObj instanceof OpenDirectoryUser){
				OpenDirectoryUser userObj = (OpenDirectoryUser)childObj;
				
				/** attribute: operation */
				userObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** attribute: value */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/open-directory/"+priorityType.name(), 
						"admin-user", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				userObj.setValue(aaaProfileImpl.getOpenDirectoryUser(priorityType));
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-directory>.<primary|backup1|2|3>.<admin-user>.<password> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/open-directory/"+priorityType.name()+"/admin-user", 
						"password", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				userObj.setPassword(CLICommonFunc.createAhEncryptedString(aaaProfileImpl.getOpenDirectoryPassword(priorityType)));
			}
			
			/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-directory>.<primary|backup1|2|3>.<domain> */
			if(childObj instanceof OpenDirectoryDomain){
				OpenDirectoryDomain domainObj = (OpenDirectoryDomain)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/open-directory/"+priorityType.name(), 
						"domain", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				domainObj.setValue(aaaProfileImpl.getOpenDirectoryDomain(priorityType));
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-directory>.<primary|backup1|2|3>.<domain>.<fullname> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/open-directory/"+priorityType.name()+"/domain", 
						"fullname", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				domainObj.setFullname(CLICommonFunc.createAhStringObj(aaaProfileImpl.getOpenDirectoryFullName(priorityType)));
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-directory>.<primary|backup1|2|3>.<domain>.<binddn> */
				DbServerBinddn binddnObj = new DbServerBinddn();
				priorityList_2.add(binddnObj);
				domainObj.setBinddn(binddnObj);
			}
		}
		priorityList_1.clear();
		generateOpenDirectoryServer_2(priorityType);
	}
	
	private void generateOpenDirectoryServer_2(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws Exception{
		/**
		 * <aaa>.<radius-server>.<local>.<db-type>.<open-directory>.<primary|backup1|2|3>.<domain>.<binddn>			DbServerBinddn
		 */
		for(Object childObj : priorityList_2){
			
			/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-directory>.<primary|backup1|2|3>.<domain>.<binddn> */
			if(childObj instanceof DbServerBinddn){
				DbServerBinddn binddnObj = (DbServerBinddn)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/open-directory/"+priorityType.name()+"/domain", 
						"binddn", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				binddnObj.setValue(aaaProfileImpl.getOpenDirectoryBindn(priorityType));
				
				/** attribute: operation */
				binddnObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));			
			
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-directory>.<primary|backup1|2|3>.<domain>.<binddn>.<password> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/open-directory/"+priorityType.name()+"/domain/binddn", 
						"password", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				binddnObj.setPassword(CLICommonFunc.createAhEncryptedString(aaaProfileImpl.getOpenDirectoryBindnPass(priorityType)));
			}
		}
		priorityList_2.clear();
	}
	
//	private OpenLdapPrimary createOpenLdapPrimary(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws Exception{
//		OpenLdapPrimary primaryObj = new OpenLdapPrimary();
//		
//		/** attribute: operation */
//		primaryObj.setOperation(CLICommonFunc
//				.getAhEnumAct(CLICommonFunc.getYesDefault()));
//
//		/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-ldap>.<primary|backup1|2|3><server> */
//		OpenLdapServer serverObj = new OpenLdapServer();
//		priorityList_1.add(serverObj);
//		primaryObj.setServer(serverObj);
//		
//		generatePriorityLevel_1(priorityType);
//		return primaryObj;
//	}
	
	private void generatePriorityLevel_1(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws Exception{
		/**
		 * <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<server>				ActiveDirectoryServer
//		 * <aaa>.<radius-server>.<local>.<db-type>.<open-ldap>.<primary|backup1|2|3><server>				OpenLdapServer
		 * <aaa>.<radius-server>.<local>.<LDAP-auth>.<primary|backup1|2|3>.<LDAP-auth>.<type>	LDAPAuthPrimary.Type
//		 * <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<workgroup>			ActiveDirectoryWorkgroup
		 * <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<primary|backup1|2|3>.<binddn>				LdapServerIdentity
		 * <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<login>				ActiveDirectoryLoginAdmin
		 * <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<primary|backup1|2|3>.<server>				LdapServerName
		 */
		
		for(Object childObj : priorityList_1){
			
			/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<server> */
			if (childObj instanceof ActiveDirectoryServer) {
				ActiveDirectoryServer serverObj = (ActiveDirectoryServer) childObj;

				/** attribute: value */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name()+"", 
						"server", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				serverObj.setValue(aaaProfileImpl.getActiveServer(priorityType));
				
				/** attribute: operation */
				serverObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<server>.<cr> */
				serverObj.setCr("");
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<server>.<login> */
				ActiveDirectoryLogin loginObj = new ActiveDirectoryLogin();
				priorityList_2.add(loginObj);
				serverObj.setLogin(loginObj);
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<server>.<via-vpn-tunnel> */
				if(aaaProfileImpl.isEnableVpnTunnelAd(aaaProfileImpl.getActiveServer(priorityType))){
					serverObj.setViaVpnTunnel(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
				}
			}
			
//			/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-ldap>.<primary|backup1|2|3><server> */
//			if(childObj instanceof OpenLdapServer){
//				/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-ldap>.<primary|backup1|2|3><server> */
//				if (childObj instanceof OpenLdapServer) {
//					OpenLdapServer serverObj = (OpenLdapServer) childObj;
//
//					/** attribute: value */
//					oDebug.debug("/configuration/aaa/radius-server/local/db-type/open-ldap/"+priorityType.name(), 
//							"server", GenerateXMLDebug.SET_VALUE,
//							aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
//					serverObj.setValue(aaaProfileImpl.getOpenLdapServer(priorityType));
//
//					/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-ldap>.<primary|backup1|2|3><server>.<basedn> */
//					OpenLdapBasedn basednObj = new OpenLdapBasedn();
//					priorityList_2.add(basednObj);
//					serverObj.setBasedn(basednObj);
//				}
//			}
			
			/** element: <aaa>.<radius-server>.<local>.<LDAP-auth>.<primary|backup1|2|3>.<LDAP-auth>.<type> */
			if (childObj instanceof LDAPAuthPrimary.Type) {
				LDAPAuthPrimary.Type typeObj = (LDAPAuthPrimary.Type) childObj;

				/** element: <aaa>.<radius-server>.<local>.<LDAP-auth>.<primary|backup1|2|3>.<LDAP-auth>.<tls> */
				LDAPAuthPrimary.Type.Tls tlsObj = new LDAPAuthPrimary.Type.Tls();
				priorityList_2.add(tlsObj);
				typeObj.setTls(tlsObj);
			}
			
//			/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<workgroup> */
//			if(childObj instanceof ActiveDirectoryWorkgroup){
//				ActiveDirectoryWorkgroup workGroupObj = (ActiveDirectoryWorkgroup)childObj;
//				
//				/** attribute: value */
//				oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name()+"", 
//						"workgroup", GenerateXMLDebug.SET_VALUE,
//						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
//				workGroupObj.setValue(aaaProfileImpl.getActiveDirectoryWorkgroup(priorityType));
//				
//				/** attribute: operation */
//				workGroupObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
//				
//				/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<workgroup>.<realm> */
//				oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name()+"/workgroup", 
//						"realm", GenerateXMLDebug.SET_VALUE,
//						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
//				workGroupObj.setRealm(CLICommonFunc.createAhStringObj(aaaProfileImpl.getActiveDirectoryRealm(priorityType)));
//			}
			
			/** element: <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<primary|backup1|2|3>.<binddn> */
			if(childObj instanceof LdapServerIdentity){
				LdapServerIdentity binddnObj = (LdapServerIdentity)childObj;
				
				/** attribute: operation */
				binddnObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** attribute: value */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/ldap-server/"+priorityType.name(), 
						"binddn", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				binddnObj.setValue(aaaProfileImpl.getOpenLdapIdentity(priorityType));
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<primary|backup1|2|3>.<password> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/ldap-server/"+priorityType.name(), 
						"password", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				binddnObj.setPassword(CLICommonFunc.createAhEncryptedString(aaaProfileImpl.getOpenLdapPassword(priorityType), 1));
			}
			
			/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<login> */
			if(childObj instanceof ActiveDirectoryLoginAdmin){
				ActiveDirectoryLoginAdmin loginObj = (ActiveDirectoryLoginAdmin)childObj;
				
				/** attribute: operation */
				loginObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<login>.<admin-user> */
				ActiveDirectoryUser adminUserObj = new ActiveDirectoryUser();
				priorityList_2.add(adminUserObj);
				loginObj.setAdminUser(adminUserObj);
			}
			
			/** element: <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<primary|backup1|2|3>.<server> */
			if(childObj instanceof LdapServerName){
				LdapServerName serverNameObj = (LdapServerName)childObj;
				
				/** attribute: value */
				serverNameObj.setValue(aaaProfileImpl.getOpenLdapServer(priorityType));
				
				/** attribute: operation */
				serverNameObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<ldap-server>.<primary|backup1|2|3>.<server>.<via-vpn-tunnel> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/ldap-server/"+priorityType.name()+"/server", 
						"via-vpn-tunnel", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if(aaaProfileImpl.isEnableVpnTunnelLdap(aaaProfileImpl.getOpenLdapServer(priorityType))){
					serverNameObj.setViaVpnTunnel(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
				}
			}
		}
		priorityList_1.clear();
		generatePriorityLevel_2(priorityType);
	}

	private void generatePriorityLevel_2(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws Exception{
		/**
		 * <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<server>.<login>					ActiveDirectoryLogin
		 * <aaa>.<radius-server>.<local>.<db-type>.<open-ldap>.<primary|backup1|2|3><server>.<basedn> 	OpenLdapBasedn
		 * <aaa>.<radius-server>.<local>.<LDAP-auth>.<primary|backup1|2|3>.<LDAP-auth>.<type>.<tls>			LDAPAuthPrimary.Type.Tls
		 * <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<login>.<admin-user>				ActiveDirectoryUser
		 */
		for(Object childObj : priorityList_2){
			
			/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<server>.<login> */
			if(childObj instanceof ActiveDirectoryLogin){
				ActiveDirectoryLogin loginObj = (ActiveDirectoryLogin)childObj;
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<server>.<login>.<user> */
				ActiveDirectoryUser userObj = new ActiveDirectoryUser();
				priorityList_3.add(userObj);
				loginObj.setUser(userObj);
			}
			
//			/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-ldap>.<primary|backup1|2|3><server>.<basedn> */
//			if (childObj instanceof OpenLdapBasedn) {
//				OpenLdapBasedn basednObj = (OpenLdapBasedn) childObj;
//
//				/** attribute: value */
//				oDebug.debug("/configuration/aaa/radius-server/local/db-type/open-ldap/"+priorityType.name()+"/server", 
//						"basedn", GenerateXMLDebug.SET_VALUE,
//						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
//				basednObj.setValue(aaaProfileImpl.getOpenLdapBasedn(priorityType));
//
//				/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-ldap>.<primary|backup1|2|3><server>.<basedn>.<login> */
//				OpenLdapLogin loginObj = new OpenLdapLogin();
//				priorityList_3.add(loginObj);
//				basednObj.setLogin(loginObj);
//			}
			
			/** element: <aaa>.<radius-server>.<local>.<LDAP-auth>.<primary|backup1|2|3>.<LDAP-auth>.<type>.<tls> */
			if (childObj instanceof LDAPAuthPrimary.Type.Tls) {
				LDAPAuthPrimary.Type.Tls tlsObj = (LDAPAuthPrimary.Type.Tls) childObj;

				/** element: <aaa>.<radius-server>.<local>.<LDAP-auth>.<primary|backup1|2|3>.<LDAP-auth>.<type>.<tls>.<caCert> */
				LDAPAuthCaCert caCertObj = new LDAPAuthCaCert();
				priorityList_3.add(caCertObj);
				tlsObj.setCaCert(caCertObj);
			}
			
			/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<login>.<admin-user> */
			if(childObj instanceof ActiveDirectoryUser){
				ActiveDirectoryUser userObj = (ActiveDirectoryUser)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name()+"/login", 
						"admin-user", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				userObj.setValue(aaaProfileImpl.getActiveUser(priorityType));
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<login>.<admin-user>.<password> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name()+"/login/admin-user", 
						"password", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				userObj.setPassword(CLICommonFunc.createAhEncryptedString(aaaProfileImpl.getActivePassword(priorityType), 1));
			}
		}
		priorityList_2.clear();
		generatePriorityLevel_3(priorityType);
	}
	
	private void generatePriorityLevel_3(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws Exception{
		/**
		 * <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<server>.<login>.<user>					ActiveDirectoryUser
		 * <aaa>.<radius-server>.<local>.<db-type>.<open-ldap>.<primary|backup1|2|3><server>.<basedn>.<login>	OpenLdapLogin
		 * <aaa>.<radius-server>.<local>.<LDAP-auth>.<primary|backup1|2|3>.<LDAP-auth>.<type>.<tls>.<caCert>		LDAPAuthCaCert
		 */
		for(Object childObj : priorityList_3){
			
			/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<server>.<login>.<user> */
			if(childObj instanceof ActiveDirectoryUser){
				ActiveDirectoryUser userObj = (ActiveDirectoryUser)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name()+"/server/login", 
						"user", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				userObj.setValue(aaaProfileImpl.getActiveUser(priorityType));
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<server>.<login>.<user>.<password> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name()+"/server/login/user", 
						"password", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				userObj.setPassword(CLICommonFunc.createAhEncryptedString(aaaProfileImpl.getActivePassword(priorityType), 1));
			}
			
//			/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-ldap>.<primary|backup1|2|3><server>.<basedn>.<login> */
//			if (childObj instanceof OpenLdapLogin) {
//				OpenLdapLogin loginObj = (OpenLdapLogin) childObj;
//
//				/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-ldap>.<primary|backup1|2|3><server>.<basedn>.<login>.<identity> */
//				OpenLdapIdentity identityObj = new OpenLdapIdentity();
//				priorityList_4.add(identityObj);
//				loginObj.setIdentity(identityObj);
//			}
			
			/** element: <aaa>.<radius-server>.<local>.<LDAP-auth>.<primary|backup1|2|3>.<type>.<tls>.<caCert> */
			if (childObj instanceof LDAPAuthCaCert) {
				LDAPAuthCaCert caCertObj = (LDAPAuthCaCert) childObj;

				/** attribute: value */
				oDebug.debug("/configuration/aaa/radius-server/local/LDAP-auth/"+priorityType.name()+"/type/tls", 
						"caCert", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				caCertObj.setValue(aaaProfileImpl.getOpenLdapTlsCaCert(priorityType));

				/** element: <aaa>.<radius-server>.<local>.<LDAP-auth>.<primary|backup1|2|3>.<type>.<tls>.<caCert>.<client-cert> */
				LDAPAuthClientCert clientCertObj = new LDAPAuthClientCert();
				priorityList_4.add(clientCertObj);
				caCertObj.setClientCert(clientCertObj);
			}
		}
		priorityList_3.clear();
		generatePriorityLevel_4(priorityType);
	}
	
	private void generatePriorityLevel_4(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws IOException{
		/**
		 * <aaa>.<radius-server>.<local>.<db-type>.<open-ldap>.<primary|backup1|2|3><server>.<basedn>.<login>.<identity>			OpenLdapIdentity
		 * <aaa>.<radius-server>.<local>.<LDAP-auth>.<primary|backup1|2|3>.<type>.<tls>.<caCert>.<client-cert>			LDAPAuthClientCert
		 */
		for(Object childObj : priorityList_4){
			
//			/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-ldap>.<primary|backup1|2|3><server>.<basedn>.<login>.<identity> */
//			if (childObj instanceof OpenLdapIdentity) {
//				OpenLdapIdentity identityObj = (OpenLdapIdentity) childObj;
//
//				/** attribute: value */
//				oDebug.debug("/configuration/aaa/radius-server/local/db-type/open-ldap/"+priorityType.name()+"/server/basedn/login", 
//						"identity", GenerateXMLDebug.SET_VALUE,
//						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
//				identityObj.setValue(aaaProfileImpl.getOpenLdapIdentity(priorityType));
//
//				/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-ldap>.<primary|backup1|2|3><server>.<basedn>.<login>.<identity>.<password> */
//				OpenLdapPassword passwordObj = new OpenLdapPassword();
//				priorityList_5.add(passwordObj);
//				identityObj.setPassword(passwordObj);
//			}
			
			/** element: <aaa>.<radius-server>.<local>.<LDAP-auth>.<primary|backup1|2|3>.<type>.<tls>.<caCert>.<client-cert> */
			if (childObj instanceof LDAPAuthClientCert) {
				LDAPAuthClientCert clientCertObj = (LDAPAuthClientCert) childObj;

				/** attribute: value */
				oDebug.debug("/configuration/aaa/radius-server/local/LDAP-auth/"+priorityType.name()+"/type/tls/caCert", 
						"client-cert", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				clientCertObj.setValue(aaaProfileImpl
						.getOpenLdapTlsClientCert(priorityType));

				/** element: <aaa>.<radius-server>.<local>.<LDAP-auth>.<primary|backup1|2|3>.<type>.<tls>.<caCert>.<client-cert>.<private-key> */
				LDAPAuthPrivateKey privateKeyObj = new LDAPAuthPrivateKey();
				priorityList_5.add(privateKeyObj);
				clientCertObj.setPrivateKey(privateKeyObj);
			}
		}
		priorityList_4.clear();
		generatePriorityLevel_5(priorityType);
	}
	
	private void generatePriorityLevel_5(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws IOException{
		/**
		 * <aaa>.<radius-server>.<local>.<db-type>.<open-ldap>.<primary|backup1|2|3><server>.<basedn>.<login>.<identity>.<password>		OpenLdapPassword
		 * <aaa>.<radius-server>.<local>.<LDAP-auth>.<primary|backup1|2|3>.<type>.<tls>.<caCert>.<client-cert>.<private-key>	LDAPAuthPrivateKey
		 */
		for(Object childObj : priorityList_5){
			
//			/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-ldap>.<primary|backup1|2|3><server>.<basedn>.<login>.<identity>.<password> */
//			if (childObj instanceof OpenLdapPassword) {
//				OpenLdapPassword passwordObj = (OpenLdapPassword) childObj;
//
//				/** attribute: value */
//				oDebug.debug("/configuration/aaa/radius-server/local/db-type/open-ldap/"+priorityType.name()+"/server/basedn/login/identity", 
//						"password", GenerateXMLDebug.SET_VALUE,
//						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
//				passwordObj.setValue(aaaProfileImpl.getOpenLdapPassword(priorityType));
//				
//				/** attribute: encrypted */
//				passwordObj.setEncrypted(passwordObj.getEncrypted());
//				
//				/** attribute: encrypted */
//				passwordObj.setEncrypted(passwordObj.getEncrypted());
//
//				/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-ldap>.<primary|backup1|2|3><server>.<basedn>.<login>.<identity>.<password>.<port> */
//				OpenLdapPassword.Port portObj = new OpenLdapPassword.Port();
//				priorityList_6.add(portObj);
//				passwordObj.setPort(portObj);
//			}
			
			/** element: <aaa>.<radius-server>.<local>.<LDAP-auth>.<primary|backup1|2|3>.<type>.<tls>.<caCert>.<client-cert>.<private-key> */
			if (childObj instanceof LDAPAuthPrivateKey) {
				LDAPAuthPrivateKey privateKeyObj = (LDAPAuthPrivateKey) childObj;

				/** attribute: value */
				oDebug.debug("/configuration/aaa/radius-server/local/LDAP-auth/"+priorityType.name()+"/type/tls/caCert/client-cert", 
						"private-key", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				privateKeyObj.setValue(aaaProfileImpl
						.getOpenLdapTlsPrivateKey(priorityType));

				/** element: <aaa>.<radius-server>.<local>.<LDAP-auth>.<primary|backup1|2|3>.<type>.<tls>.<caCert>.<client-cert>.<private-key>.<private-key-password> */
				LDAPAuthPrivateKeyPassword privateKeyPasswordObj = new LDAPAuthPrivateKeyPassword();
				priorityList_6.add(privateKeyPasswordObj);
				privateKeyObj.setPrivateKeyPassword(privateKeyPasswordObj);
			}
		}
		priorityList_5.clear();
		generatePriorityLevel_6(priorityType);
	}
	
	private void generatePriorityLevel_6(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws IOException{
		/**
		 * <aaa>.<radius-server>.<local>.<db-type>.<open-ldap>.<primary|backup1|2|3>.<server>.<basedn>.<login>.<identity>.<password>.<port>		OpenLdapPassword.Port
		 * <aaa>.<radius-server>.<local>.<LDAP-auth>.<primary|backup1|2|3>.<type>.<tls>.<caCert>.<client-cert>.<private-key>.<private-key-password>		LDAPAuthPrivateKeyPassword
		 */
		for(Object childObj : priorityList_6){
			
//			/** element: <aaa>.<radius-server>.<local>.<db-type>.<open-ldap>.<primary|backup1|2|3>.<server>.<basedn>.<login>.<identity>.<password>.<port> */
//			if(childObj instanceof OpenLdapPassword.Port){
//				OpenLdapPassword.Port portObj = (OpenLdapPassword.Port)childObj;
//				
//				/** attribute: value */
//				oDebug.debug("/configuration/aaa/radius-server/local/db-type/open-ldap/"+priorityType.name()+"/server/basedn/login/identity/password", 
//						"port", GenerateXMLDebug.SET_VALUE,
//						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
//				portObj.setValue(aaaProfileImpl.getOpenLdapPort(priorityType));
//			}
			
			/** element: <aaa>.<radius-server>.<local>.<LDAP-auth>.<primary|backup1|2|3>.<type>.<tls>.<caCert>.<client-cert>.<private-key>.<private-key-password> */
			if (childObj instanceof LDAPAuthPrivateKeyPassword) {
				LDAPAuthPrivateKeyPassword privateKeyPasswordObj = (LDAPAuthPrivateKeyPassword) childObj;

				/** attribute: value */
				oDebug.debug("/configuration/aaa/radius-server/local/LDAP-auth/"+priorityType.name()+"/type/tls/caCert/client-cert/private-key", 
						"private-key-password", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				privateKeyPasswordObj.setValue(aaaProfileImpl
						.getOpenLdapTlsPrivateKeyPassword(priorityType));
				
				/** attribute: encrypted */
				privateKeyPasswordObj.setEncrypted(privateKeyPasswordObj.getEncrypted());

				/** element: <aaa>.<radius-server>.<local>.<LDAP-auth>.<primary|backup1|2|3>.<type>.<tls>.<caCert>.<client-cert>.<private-key>.<private-key-password>.<verify-server> */
				LDAPAuthPrivateKeyPassword.VerifyServer verifyServerObj = new LDAPAuthPrivateKeyPassword.VerifyServer();
				priorityList_7.add(verifyServerObj);
				privateKeyPasswordObj.setVerifyServer(verifyServerObj);
			}
		}
		priorityList_6.clear();
		generatePriorityLevel_7(priorityType);
	}
	
	private void generatePriorityLevel_7(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws IOException{
		/**
		 * <aaa>.<radius-server>.<local>.<LDAP-auth>.<primary|backup1|2|3>.<type>.<tls>.<caCert>.<client-cert>.<private-key>.<private-key-password>.<verify-server> 		LDAPAuthPrivateKeyPassword.VerifyServer
		 */
		for(Object childObj : priorityList_7){
			
			/** element: <aaa>.<radius-server>.<local>.<LDAP-auth>.<primary|backup1|2|3>.<type>.<tls>.<caCert>.<client-cert>.<private-key>.<private-key-password>.<verify-server>  */
			if (childObj instanceof LDAPAuthPrivateKeyPassword.VerifyServer) {
				LDAPAuthPrivateKeyPassword.VerifyServer verifyServerObj = (LDAPAuthPrivateKeyPassword.VerifyServer) childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/aaa/radius-server/local/LDAP-auth/"+priorityType.name()+"/type/tls/caCert/client-cert/private-key/private-key-password", 
						"verify-server", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				verifyServerObj.setValue(aaaProfileImpl.getVerifyServerValue(priorityType));
			}
		}
		priorityList_7.clear();
	}
	
	private ActiveDirectoryDomain createADDomain(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType, int index) throws IOException{
		/**
		 * <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<domain>
		 */
		ActiveDirectoryDomain adDomainObj = new ActiveDirectoryDomain();
		
		/** attribute: name */
		oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name(), 
				"domain", GenerateXMLDebug.SET_NAME,
				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
		adDomainObj.setName(aaaProfileImpl.getADDomainName(priorityType, index));
		
		/** attribute: operation */
		adDomainObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
//		/** element: <default-flag> */
//		ActiveDirectoryDomain.DefaultFlag defaultObj = new ActiveDirectoryDomain.DefaultFlag();
//		adDomainList_1.add(defaultObj);
//		adDomainObj.setDefaultFlag(defaultObj);
		
		/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<domain>.<fullname> */
		oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name()+"/domain[@name='"+adDomainObj.getName()+"']", 
				"fullname", GenerateXMLDebug.CONFIG_ELEMENT,
				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
		if(aaaProfileImpl.isConfigDomainFullname(priorityType, index)){
			ActiveDirectoryDomain.Fullname fullNameObj = new ActiveDirectoryDomain.Fullname();
			adDomainList_1.add(fullNameObj);
			adDomainObj.setFullname(fullNameObj);
		}
		
//		/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<domain>.<basedn> */
//		oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name()+"/domain[@name='"+adDomainObj.getName()+"']", 
//				"basedn", GenerateXMLDebug.CONFIG_ELEMENT,
//				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
//		if(aaaProfileImpl.isConfigDomainBaseDn(priorityType, index)){
//			
//			oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name()+"/domain[@name='"+adDomainObj.getName()+"']", 
//					"basedn", GenerateXMLDebug.SET_VALUE,
//					aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
//			adDomainObj.setBasedn(CLICommonFunc.createAhStringActObj(
//					aaaProfileImpl.getADDomainBasedn(priorityType, index), CLICommonFunc.getYesDefault()
//				)
//			);
//		}
		
		/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<domain>.<server> */
		oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name()+"/domain[@name='"+adDomainObj.getName()+"']", 
				"server", GenerateXMLDebug.CONFIG_ELEMENT,
				aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
		if(aaaProfileImpl.isConfigDomainServer(priorityType, index)){
			
			oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name()+"/domain[@name='"+adDomainObj.getName()+"']", 
					"server", GenerateXMLDebug.SET_VALUE,
					aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
			adDomainObj.setServer(CLICommonFunc.createAhStringActObj(
					aaaProfileImpl.getADDomainServer(priorityType, index), CLICommonFunc.getYesDefault()
				)
			);
		}
		
		/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<domain>.<binddn> */
		ActiveDirectoryDomain.Binddn binddnObj = new ActiveDirectoryDomain.Binddn();
		adDomainList_1.add(binddnObj);
		adDomainObj.setBinddn(binddnObj);
		
		generateADDomainLevel_1(priorityType, index);
		return adDomainObj;
	}
	
	private void generateADDomainLevel_1(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType, int index) throws IOException{
		/**
		 * <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<domain>.<fullname>				ActiveDirectoryDomain.Fullname
		 * <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<domain>.<binddn>					ActiveDirectoryDomain.Binddn
		 */
		
		for(Object childObj : adDomainList_1){
			
			/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<domain>.<fullname> */
			if(childObj instanceof ActiveDirectoryDomain.Fullname){
				ActiveDirectoryDomain.Fullname fullNameObj = (ActiveDirectoryDomain.Fullname)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name()+"/domain[@name='"+aaaProfileImpl.getADDomainName(priorityType, index)+"']", 
						"fullname", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				fullNameObj.setValue(aaaProfileImpl.getADDomainFullname(priorityType, index));
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<domain>.<fullname>.<default> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name()+"/domain[@name='"+aaaProfileImpl.getADDomainName(priorityType, index)+"']/fullname", 
						"default", GenerateXMLDebug.CONFIG_ELEMENT,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				if(aaaProfileImpl.isConfigADDomainDefault(priorityType, index)){
					fullNameObj.setDefault(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
				}
			}
			
			/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<domain>.<binddn> */
			if(childObj instanceof ActiveDirectoryDomain.Binddn){
				ActiveDirectoryDomain.Binddn binddnObj = (ActiveDirectoryDomain.Binddn)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name()+"/domain[@name='"+aaaProfileImpl.getADDomainName(priorityType, index)+"']", 
						"binddn", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				binddnObj.setValue(aaaProfileImpl.getADDomainBinddn(priorityType, index));
				
				/** attribute: operation */
				binddnObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <aaa>.<radius-server>.<local>.<db-type>.<active-directory>.<primary|backup1|2|3>.<domain>.<binddn>.<password> */
				oDebug.debug("/configuration/aaa/radius-server/local/db-type/active-directory/"+priorityType.name()+"/domain[@name='"+aaaProfileImpl.getADDomainName(priorityType, index)+"']/binddn", 
						"password", GenerateXMLDebug.SET_VALUE,
						aaaProfileImpl.getRadiusGuiName(), aaaProfileImpl.getRadiusName());
				binddnObj.setPassword(CLICommonFunc.createAhEncryptedString(
						aaaProfileImpl.getADDomainBinddnPassword(priorityType, index), 1
						)
				);
			}
		}
		adDomainList_1.clear();
	}
	
	private STAAuthChecks createSTAAuthChecks(boolean isCheckDb){
		STAAuthChecks checkObj = new STAAuthChecks();
		
		/** element: <check-in-db> */
		checkObj.setCheckInDb(CLICommonFunc.getAhOnlyAct(isCheckDb));
		
		return checkObj;
	}
	
	private RadiusProxyRealm createRadiusProxyRealm(int index){
		RadiusProxyRealm realmObj = new RadiusProxyRealm();
		
		/** attribute: name */
		realmObj.setName(aaaProfileImpl.getProxyRealmName(index));
		
		/** attribute: operation */
		realmObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <no-strip> */
		if(aaaProfileImpl.isConfigProxyRealmNoStrip(index)){
			realmObj.setNoStrip(CLICommonFunc.getAhOnlyAct(aaaProfileImpl.isProxyRealmNoStrip(index)));
		}
		
		/** element: <primary> */
		if(aaaProfileImpl.isConfigProxyRealmServerPrimary(index)){
			realmObj.setPrimary(CLICommonFunc.createAhNameActObj(
					aaaProfileImpl.getProxyRealmServerPrimaryName(index), CLICommonFunc.getYesDefault()));
		}
		
		/** element: <backup> */
		if(aaaProfileImpl.isConfigProxyRealmServerBackup(index)){
			realmObj.setBackup(CLICommonFunc.createAhNameActObj(
					aaaProfileImpl.getProxyRealmServerBackupName(index), CLICommonFunc.getYesDefault()));
		}
		
		return realmObj;
	}
	
	public LocalRadiusServer createLocalRadiusServer(int index) throws Exception{
		LocalRadiusServer radServer = new LocalRadiusServer();
		
		/** attribute: name */
		radServer.setName(aaaProfileImpl.getRealmName(index));
		
		/** attribute: operation */
		radServer.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		if(!aaaProfileImpl.isProxyServerCloudAuth(index)){
			/** element: <acct-port> */
			Object[][] acctPortParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, aaaProfileImpl.getRealmAcctPort(index)},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			radServer.setAcctPort(
					(AaaAcctPort)CLICommonFunc.createObjectWithName(AaaAcctPort.class, acctPortParm));
			
			/** element: <auth-port> */
			Object[][] authPortParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, aaaProfileImpl.getRealmAuthPort(index)},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			radServer.setAuthPort(
					(AaaAuthPort)CLICommonFunc.createObjectWithName(AaaAuthPort.class, authPortParm));
		}
		
		/** element: <tls-port> */
		if(aaaProfileImpl.isProxyServerCloudAuth(index)){
			Object[][] tlsPortParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, aaaProfileImpl.getRadsecTlsPort()},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			radServer.setTlsPort((RadiusTlsPort)CLICommonFunc.createObjectWithName(RadiusTlsPort.class, tlsPortParm));
		}
		
		/** element: <server> */
		RadiusProxyServer server = new RadiusProxyServer();
		server.setValue(aaaProfileImpl.getRealmIp(index));
		radServer.setServer(server);
		
		if(!aaaProfileImpl.isProxyServerCloudAuth(index)){
			/** element: <server>.<shared-secret> */
			server.setSharedSecret(CLICommonFunc.createAhEncryptedString(aaaProfileImpl.getRealmPass(index)));
		}else {
			/** element: <server>.<tls> */
			server.setTls("");
		}
		
		return radServer;
	}
	
	private PpskRadiusServerConcrete createPpskRadiusServerConcrete(AAAProfileInt.RADIUS_PRIORITY_TYPE radiusType) throws Exception{
		PpskRadiusServerConcrete ppskRadiusServer = new PpskRadiusServerConcrete();
		
		/** attribute: operation */
		ppskRadiusServer.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
		
		/** attribute: value */
		ppskRadiusServer.setValue(aaaProfileImpl.getPpskRadiusServerIpOrHost(radiusType));
		
		/** element: <shared-secret> */
		if(aaaProfileImpl.isConfigPpskRadiusSecret(radiusType)){
			ppskRadiusServer.setSharedSecret(CLICommonFunc.createAhEncryptedStringAct(
					aaaProfileImpl.getPpskRadiusSecret(radiusType), CLICommonFunc.getYesDefault()));
		}
		
		/** element: <auth-port> */
		Object[][] authPortParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, aaaProfileImpl.getPpskRadiusAuthPort(radiusType)},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		ppskRadiusServer.setAuthPort(
				(AaaAuthPort)CLICommonFunc.createObjectWithName(AaaAuthPort.class, authPortParm));
		
		/** element: <via-vpn-tunnel> */
		if(aaaProfileImpl.isEnableVpnTunnelLdap(ppskRadiusServer.getValue())){
			ppskRadiusServer.setViaVpnTunnel(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		return ppskRadiusServer;
	}
	
//	private RadsecRealm createRadsecRealm(int index){
//		RadsecRealm realm = new RadsecRealm();
//		
//		/** attribute: operation */
//		realm.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
//		
//		/** attribute: name */
//		realm.setName(aaaProfileImpl.getRadsecRealmName(index));
//		
//		/** element: <primary> */
//		if(aaaProfileImpl.isRadsecPrimaryRealm(index)){
//			realm.setPrimary(CLICommonFunc.createAhStringActObj(
//					aaaProfileImpl.getRadsecRealmPrimaryValue(index), CLICommonFunc.getYesDefault()));
//		}
//		
//		/** element: <backup> */
//		if(aaaProfileImpl.isRadsecBackupRealm(index)){
//			realm.setBackup(CLICommonFunc.createAhStringActObj(
//					aaaProfileImpl.getRadsecRealmBackupValue(index), CLICommonFunc.getYesDefault()));
//		}
//		
//		return realm;
//	}
}