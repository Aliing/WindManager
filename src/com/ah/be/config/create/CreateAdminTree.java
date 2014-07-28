package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.xml.be.config.AdminAuthMethod;
import com.ah.xml.be.config.AdminObj;
import com.ah.xml.be.config.AdminUser;
import com.ah.xml.be.config.AhAuthMethod;
import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.AdminProfileInt;

/**
 * 
 * @author zhang
 *
 */
public class CreateAdminTree {

	private AdminProfileInt adminImpl;
	private AdminObj adminObj;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> adminChildList_1 = new ArrayList<Object>();
	private List<Object> adminChildList_2 = new ArrayList<Object>();
	
	public CreateAdminTree(AdminProfileInt adminImpl, GenerateXMLDebug oDebug) throws Exception {
		this.adminImpl = adminImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		
		oDebug.debug("/configuration", 
				"admin", GenerateXMLDebug.CONFIG_ELEMENT,
				null, null);
		if(adminImpl.isConfigureAdmin()){
			adminObj = new AdminObj();
			generateAdminLevel_1();
		}
	}
	
	public AdminObj getAdminObj(){
		return this.adminObj;
	}
	
	private void generateAdminLevel_1() throws Exception{
		/**
		 * <admin>		AdminObj
		 */
		
		/** attribute: updateTime */
		adminObj.setUpdateTime(adminImpl.getUpdateTime());
		
		/** element: <admin>.<root-admin> */
		oDebug.debug("/configuration/admin", 
				"root-admin", GenerateXMLDebug.CONFIG_ELEMENT,
				adminImpl.getHiveApGuiName(), adminImpl.getHiveApName());
		if(adminImpl.isConfigureRootAdmin()){
			AdminObj.RootAdmin rootAdmin = new AdminObj.RootAdmin();
			adminChildList_1.add(rootAdmin);
			adminObj.setRootAdmin(rootAdmin);
		}
		
		/** element: <admin>.<read-only> */
		oDebug.debug("/configuration/admin", 
				"read-only", GenerateXMLDebug.CONFIG_ELEMENT,
				adminImpl.getHiveApGuiName(), adminImpl.getHiveApName());
		if(adminImpl.isConfigureReaderOnly()){
			for(int i=0; i<adminImpl.getReaderOnlySize(); i++){
				adminObj.getReadOnly().add(createReaderOnlyUser(i));
			}
		}
		
		/** element: <admin>.<manager-ip> */
		oDebug.debug("/configuration/admin", 
				"manager-ip", GenerateXMLDebug.CONFIG_ELEMENT,
				adminImpl.getHiveApGuiName(), adminImpl.getHiveApName());
		if(adminImpl.isConfigureManageIp()){
			for(int i=0; i<adminImpl.getManageIpSize(); i++){
				
				oDebug.debug("/configuration/admin", 
						"manager-ip", GenerateXMLDebug.SET_NAME,
						adminImpl.getHiveApGuiName(), adminImpl.getHiveApName());
				adminObj.getManagerIp().add(
						CLICommonFunc.createAhNameActValue(adminImpl.getManageIpAndMask(i), CLICommonFunc.getYesDefault())
				);
			}
		}
		
		/** element: <admin>.<auth> */
		if(adminImpl.isConfigAdminAuth()){
			AdminObj.Auth authObj = new AdminObj.Auth();
			adminChildList_1.add(authObj);
			adminObj.setAuth(authObj);
		}
		
		generateAdminLevel_2();
	}
	
	private void generateAdminLevel_2() throws Exception {
		/**
		 * <admin>.<root-admin>		AdminObj.RootAdmin
		 * <admin>.<auth>			AdminObj.Auth
		 */

		for(Object childObj : adminChildList_1){
			
			/** element: <admin>.<root-admin> */
			if(childObj instanceof AdminObj.RootAdmin){
				AdminObj.RootAdmin rootAdminObj = (AdminObj.RootAdmin)childObj;
				
				/** attribute: name */
				oDebug.debug("/configuration/admin", 
						"root-admin", GenerateXMLDebug.SET_VALUE,
						adminImpl.getHiveApGuiName(), adminImpl.getHiveApName());
				rootAdminObj.setValue(adminImpl.getRootAdminUser());
				
				/** element: <admin>.<root-admin>.<password> */
				oDebug.debug("/configuration/admin/root-admin", 
						"password", GenerateXMLDebug.SET_VALUE,
						adminImpl.getHiveApGuiName(), adminImpl.getHiveApName());
				Object[][] passwordParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, adminImpl.getRootAdminPassword()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				rootAdminObj.setPassword(
						(AdminObj.RootAdmin.Password)CLICommonFunc.createObjectWithName(
								AdminObj.RootAdmin.Password.class, passwordParm)
				);
				rootAdminObj.getPassword().setEncrypted(rootAdminObj.getPassword().getEncrypted());
			}
			
			/** element: <admin>.<auth> */
			if(childObj instanceof AdminObj.Auth){
				AdminObj.Auth authObj = (AdminObj.Auth)childObj;
				
				/** element: <admin>.<auth>.<cr> */
				oDebug.debug("/configuration/admin/auth", 
						"cr", GenerateXMLDebug.CONFIG_ELEMENT,
						adminImpl.getHiveApGuiName(), adminImpl.getHiveApName());
				if(adminImpl.isConfigAuthTypeBoth() || adminImpl.isConfigAuthTypeLocal() || adminImpl.isConfigAuthTypeRadius()){
					AdminAuthMethod crObj = new AdminAuthMethod();
					authObj.setCr(crObj);
					adminChildList_2.add(crObj);
				}

				/** element: <admin>.<auth>.<radius-method> */
				oDebug.debug("/configuration/admin/auth", 
						"radius-method", GenerateXMLDebug.CONFIG_ELEMENT,
						adminImpl.getHiveApGuiName(), adminImpl.getHiveApName());
				if(adminImpl.isConfigAuthTypeRadius() || adminImpl.isConfigAuthTypeBoth()){
					AhAuthMethod authMethodObj = new AhAuthMethod();
					adminChildList_2.add(authMethodObj);
					authObj.setRadiusMethod(authMethodObj);
				}
			}
			
		}
		adminChildList_1.clear();
		generateAdminLevel_3();
	}
	
	private void generateAdminLevel_3() {
		/**
		 * <admin>.<auth>.<radius-method>				AhAuthMethod
		 * <admin>.<auth>.<cr>							AdminAuthMethod
		 */
		for(Object childObj : adminChildList_2){
			
			/** element: <admin>.<auth>.<radius-method> */
			if(childObj instanceof AhAuthMethod){
				AhAuthMethod authMethodObj = (AhAuthMethod)childObj;
				
				/** attribute: operation */
				authMethodObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <admin>.<auth>.<radius-method>.<pap> */
				oDebug.debug("/configuration/admin/auth/radius-method", 
						"pap", GenerateXMLDebug.CONFIG_ELEMENT,
						adminImpl.getHiveApGuiName(), adminImpl.getHiveApName());
				if(adminImpl.isAuthTypePap()){
					authMethodObj.setPap(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
				}
				
				/** element: <admin>.<auth>.<radius-method>.<chap> */
				oDebug.debug("/configuration/admin/auth/radius-method", 
						"chap", GenerateXMLDebug.CONFIG_ELEMENT,
						adminImpl.getHiveApGuiName(), adminImpl.getHiveApName());
				if(adminImpl.isAuthTypeChap()){
					authMethodObj.setChap(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
				}
				
				/** element: <admin>.<auth>.<radius-method>.<ms-chap-v2> */
				oDebug.debug("/configuration/admin/auth/radius-method", 
						"ms-chap-v2", GenerateXMLDebug.CONFIG_ELEMENT,
						adminImpl.getHiveApGuiName(), adminImpl.getHiveApName());
				if(adminImpl.isAuthTypeMschapv2()){
					authMethodObj.setMsChapV2(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
				}
			}
			
			/** element: <admin>.<auth>.<cr> */
			if(childObj instanceof AdminAuthMethod){
				AdminAuthMethod crObj = (AdminAuthMethod)childObj;
				
				/** attribute: operation */
				crObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <admin>.<auth>.<cr>.<both> */
				oDebug.debug("/configuration/admin/auth/cr", 
						"both", GenerateXMLDebug.CONFIG_ELEMENT,
						adminImpl.getHiveApGuiName(), adminImpl.getHiveApName());
				if(adminImpl.isConfigAuthTypeBoth()){
					crObj.setBoth("");
				}
				
				/** element: <admin>.<auth>.<cr>.<local> */
				oDebug.debug("/configuration/admin/auth/cr", 
						"local", GenerateXMLDebug.CONFIG_ELEMENT,
						adminImpl.getHiveApGuiName(), adminImpl.getHiveApName());
				if(adminImpl.isConfigAuthTypeLocal()){
					crObj.setLocal("");
				}
				
				/** element: <admin>.<auth>.<cr>.<radius> */
				oDebug.debug("/configuration/admin/auth/cr", 
						"radius", GenerateXMLDebug.CONFIG_ELEMENT,
						adminImpl.getHiveApGuiName(), adminImpl.getHiveApName());
				if(adminImpl.isConfigAuthTypeRadius()){
					crObj.setRadius("");
				}
			}
		}
		adminChildList_2.clear();
	} 
	
	private AdminUser createReaderOnlyUser(int index) throws Exception {
		AdminUser readerOnly = new AdminUser();
		
		/** attribute: name */
		oDebug.debug("/configuration/admin", 
				"read-only", GenerateXMLDebug.SET_NAME,
				adminImpl.getHiveApGuiName(), adminImpl.getHiveApName());
		readerOnly.setName(adminImpl.getReaderOnlyUser(index));
		
		/** attribute: operation */
		readerOnly.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <password> */
		oDebug.debug("/configuration/admin/read-only[@name='"+readerOnly.getName()+"']", 
				"password", GenerateXMLDebug.SET_VALUE,
				adminImpl.getHiveApGuiName(), adminImpl.getHiveApName());
		Object[][] passwordParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, adminImpl.getReaderOnlyPassword(index)}
		};
		readerOnly.setPassword(
				(AdminUser.Password)CLICommonFunc.createObjectWithName(AdminUser.Password.class, passwordParm)
		);
		readerOnly.getPassword().setEncrypted(readerOnly.getPassword().getEncrypted());
		
		return readerOnly;
	}
}
