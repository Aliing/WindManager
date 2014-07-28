/**
 *@filename		ImportCsvFileAction.java
 *@version
 *@author		Fiona
 *@createtime	2007-11-21 PM 03:39:50
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *at Oct 22, 2010, modified by Yunzhi Lin, Change implement for 'MAC Filter CSV Import Enhancement'
 */
package com.ah.ui.actions.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.Ostermiller.util.CSVParser;
import com.ah.be.app.HmBeEventUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.app.HmBeParaUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.db.configuration.ConfigurationChangedEvent;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.DeviceIPSubNetwork;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApAutoProvision;
import com.ah.bo.hiveap.HiveApSerialNumber;
import com.ah.bo.mgmt.AccessControl;
import com.ah.bo.mgmt.AccessControl.CrudOperation;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.MapMgmt;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.OneTimePassword;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.MacFilter;
import com.ah.bo.network.MacFilterInfo;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.teacherView.TvClass;
import com.ah.bo.teacherView.TvClassSchedule;
import com.ah.bo.teacherView.TvComputerCart;
import com.ah.bo.teacherView.TvComputerCartMacName;
import com.ah.bo.teacherView.TvResourceMap;
import com.ah.bo.teacherView.TvStudentRoster;
import com.ah.bo.useraccess.LocalUser;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.RadiusServer;
import com.ah.bo.wlan.RadioProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.hiveap.HiveApAction;
import com.ah.ui.actions.home.StartHereAction;
import com.ah.ui.actions.monitor.OneTimePasswordAction;
import com.ah.ui.actions.teacherView.TvClassAction;
import com.ah.ui.actions.teacherView.TvResourceMapAction;
import com.ah.ui.actions.teacherView.TvStudentRosterAction;
import com.ah.util.CreateObjectAuto;
import com.ah.util.HmContextListener;
import com.ah.util.HmException;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.Tracer;
import com.ah.util.classifiertag.DefaultTagOrderComparator;
import com.ah.util.coder.AhEncoder;
import com.ah.util.devices.impl.Device;
import com.ah.ws.rest.client.utils.ClientUtils;
import com.ah.ws.rest.models.CustomerUserInfo;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class ImportCsvFileAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private static final Tracer	log	= new Tracer(ImportCsvFileAction.class.getSimpleName());
	
	/* Default Config Template */
	private final ConfigTemplate defTemp = HmBeParaUtil.getDefaultTemplate();

	/* Default Radio Profile for A Mode */
	private final RadioProfile defRadioA = HmBeParaUtil.getDefaultRadioAProfile();

	/* Default Radio Profile for BG Mode */
	private final RadioProfile defRadioBG = HmBeParaUtil.getDefaultRadioBGProfile();

	/* Default Radio Profile for NG Mode */
	private final RadioProfile defRadioNG = HmBeParaUtil.getDefaultRadioNGProfile();

	/* Default Radio Profile for NA Mode */
	private final RadioProfile defRadioNA = HmBeParaUtil.getDefaultRadioNAProfile();

	public static final String[] SINGLE_STRING_LIMIT = new String[] {" ", "\"", "	", "?"};
	
	public static final String[] SINGLE_STRING_LIMIT_WITH_BLANK = new String[] {"\"", "?"};

	public static final String[] LOCATION_STRING_LIMIT = new String[] {"\"", "	", "@", "?"};

	public static final String[] LOCAL_USER_STRING_LIMIT = new String[] {"	", "'", "@", "\\"};

	public static final String[] LOCAL_USER_GROUP_STRING_LIMIT = new String[] {"	"};

	private String[][] allvalue;

	private File upload;

	private String uploadContentType;

	private String uploadFileName;

	private String resultMessage = "";

	private final StringBuffer result = new StringBuffer();

	private Map<String, List<LocalUser>> localUserMap;

	private Map<String, List<MacFilter>> macFilterMap;
	
	private Map<String, List<MacOrOui>> macAddreOrOuiMap;
	
	private Map<Long, List<HiveAp>> newHiveApMap;

	private final List<HiveAp> m_vct_hiveAP = new ArrayList<HiveAp>();
	
	private final Map<Long, MapContainerNode> topoMap = new HashMap<Long, MapContainerNode>();

	private Map<String, List<LocalUserGroup>> userGroupMap;

	private Map<String, List<IpAddress>> ipObjectMap;

	private Map<String, List<RadiusAssignment>> aaaRadiusMap;
	
	private List<HiveApSerialNumber> serialNumbers;
	
	private List<DeviceIPSubNetwork> ipSubNetworks;
	
	private Map<String, List<TvClass>> tvClassMap;
	private Map<String, List<TvStudentRoster>> tvStudentRosterMap;
	private Map<String, List<TvComputerCart>> tvComputerCartMap;
	private Map<String, List<TvResourceMap>> tvResourceMap;
	
	private Map<String, List<OneTimePassword>> oneTimePasswordMap;
	
	private HmDomain thisDomain;

	private String strListForward;
	
	private String groupTitle;

	private String downLoadFileName;

	private String inputPath;
	
	private String diMenuTypeKey;
	
	private final static String DEVICE_IMPORT_PREVIEW_MENU="DEVICE_IMPORT_PREVIEW_MENU";

	//private static final Tracer log = new Tracer(ImportCsvFileAction.class
			//.getSimpleName());
	public String returnAllValue(String defaultRet, String strEx){
		if (getLastExConfigGuide()!=null) {
			return strEx;
		}
		return defaultRet;
	}

	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			if ("importFiles".equals(operation)) {
				resetL2SelectedPage();
			} else if ("import".equals(operation) || "cancel".equals(operation)) {
				if ("unManagedDevice".equals(strListForward)) {
					String di_menu =(String) MgrUtil.getSessionAttribute(DEVICE_IMPORT_PREVIEW_MENU);
					if (di_menu!=null) {
						setSelectedL2Feature(di_menu);
					} else {
						setSelectedL2Feature(L2_FEATURE_CONFIG_HIVE_APS);
					}
					resetPermission();
				}
			}
			
			// get the domain
			thisDomain = findBoById(HmDomain.class, domainId);

			if ("importFiles".equals(operation)) {
				try {
					AccessControl.checkUserAccess(getUserContext(),
							getSelectedL2FeatureKey(), CrudOperation.CREATE);
				} catch (HmException ex) {
					MgrUtil.setSessionAttribute("errorMessage", MgrUtil
							.getUserMessage(ex));
					setUpdateContext(true);
					return strListForward;
				}
				return returnAllValue(SUCCESS,"successEx");
				//return SUCCESS;
			} else if ("cancel".equals(operation)) {
				String di_menu =(String) MgrUtil.getSessionAttribute(DEVICE_IMPORT_PREVIEW_MENU);
				setDiMenuTypeKey(di_menu);
				setUpdateContext(true);
				return strListForward;
			} else if ("import".equals(operation)) {
				saveFile();
				return returnAllValue(SUCCESS,"successEx");
			} else if ("download".equals(operation)) {
				if (getShowDomain() && thisDomain != null
						&& HmDomain.HOME_DOMAIN.equals(thisDomain.getDomainName())){
					if ("tvClass".equals(strListForward)){
						downLoadFileName = "ClassExampleVHM.csv";
					} else if ("tvComputerCart".equals(strListForward)){
						downLoadFileName = "ComputerCartExampleVHM.csv";
					} else if ("tvStudentRoster".equals(strListForward)){
						downLoadFileName = "StudentRosterExampleVHM.csv";
					} else if ("tvResourceMap".equals(strListForward)){
						downLoadFileName = "ResourceMapExampleVHM.csv";
					}
				}else{
					if ("tvClass".equals(strListForward)){
						downLoadFileName = "ClassExample.csv";
					} else if ("tvComputerCart".equals(strListForward)){
						downLoadFileName = "ComputerCartExample.csv";
					} else if ("tvStudentRoster".equals(strListForward)){
						downLoadFileName = "StudentRosterExample.csv";
					} else if ("tvResourceMap".equals(strListForward)){
						downLoadFileName = "ResourceMapExample.csv";
					}
				}

				inputPath = HmContextListener.context.getRealPath("/WEB-INF/csvsamples") + File.separator + downLoadFileName;

				// check file exist
				File file = new File(inputPath);
				if (!file.exists()) {
					addActionError(MgrUtil.getUserMessage("hm.system.log.import.csv.error.download",
							new String[]{inputPath}));
					return SUCCESS;
				}

				return "download";
			}else {
				return returnAllValue(SUCCESS,"successEx");
			}
		} catch (Exception e) {
			addActionError(e.getMessage());
			return ERROR;
		}
	}
	
	private void resetL2SelectedPage () {
		if ("unManagedDevice".equals(strListForward)) {
			if (diMenuTypeKey!=null) {
				setSelectedL2Feature(diMenuTypeKey);
				MgrUtil.setSessionAttribute(DEVICE_IMPORT_PREVIEW_MENU, diMenuTypeKey);
			} else {
				setSelectedL2Feature(L2_FEATURE_CONFIG_HIVE_APS);
				MgrUtil.setSessionAttribute(DEVICE_IMPORT_PREVIEW_MENU, L2_FEATURE_CONFIG_HIVE_APS);
			}
			resetPermission();
		} else {
			MgrUtil.setSessionAttribute(DEVICE_IMPORT_PREVIEW_MENU, null);
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		strListForward = getLstForward();
		if ("localUser".equals(strListForward)) {
			setSelectedL2Feature(L2_FEATURE_LOCAL_USER);
			groupTitle = isEasyMode() ? MgrUtil.getUserMessage("config.ssid.head.ssid")+"/HiveAP" : 
				MgrUtil.getUserMessage("config.localUser.userGroup");
		}
		if ("localUserGroup".equals(strListForward)) {
			setSelectedL2Feature(L2_FEATURE_LOCAL_USER_GROUP);
		}
		if ("hiveApNew".equals(strListForward)) {
			// setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
			String listTypeFromSession = (String) MgrUtil
					.getSessionAttribute(HiveApAction.HM_LIST_TYPE);
			String viewType = (String) MgrUtil
					.getSessionAttribute(HiveApAction.MANAGED_LIST_VIEW);
			if ("managedVPNGateways".equals(listTypeFromSession)) {
				if ("config".equals(viewType)) {
					setSelectedL2Feature(L2_FEATURE_CONFIG_VPN_GATEWAYS);
				} else {
					setSelectedL2Feature(L2_FEATURE_VPN_GATEWAYS);
				}
			} else if ("managedRouters".equals(listTypeFromSession)) {
				if ("config".equals(viewType)) {
					setSelectedL2Feature(L2_FEATURE_CONFIG_BRANCH_ROUTERS);
				} else {
					setSelectedL2Feature(L2_FEATURE_BRANCH_ROUTERS);
				}
			} else if ("managedDeviceAPs".equals(listTypeFromSession)) {
				if ("config".equals(viewType)) {
					setSelectedL2Feature(L2_FEATURE_CONFIG_DEVICE_HIVEAPS);
				} else {
					setSelectedL2Feature(L2_FEATURE_DEVICE_HIVEAPS);
				}
			} else if ("managedHiveAps".equals(listTypeFromSession)) {
				if ("config".equals(viewType)) {
					setSelectedL2Feature(L2_FEATURE_CONFIG_HIVE_APS);
				} else {
					setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
				}
			} else {
				if ("config".equals(viewType)) {
					setSelectedL2Feature(L2_FEATURE_CONFIG_HIVE_APS);
				} else {
					setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
				}
			}
		}
		if ("macFilter".equals(strListForward)) {
			setSelectedL2Feature(L2_FEATURE_MAC_FILTERS);
		}
		if ("ipAddress".equals(strListForward)) {
			setSelectedL2Feature(L2_FEATURE_IP_ADDRESS);
		}
		if ("radiusAssignment".equals(strListForward)) {
			setSelectedL2Feature(L2_FEATURE_RADIUS_SERVER_ASSIGN);
		}
		if ("autoProvisioningConfig".equals(getLstForward())){
			setSelectedL2Feature(L2_FEATURE_HIVEAP_AUTO_PROVISIONING);
		}
		if ("autoProvisioningConfigIpSubNetworks".equals(getLstForward())) {
			setSelectedL2Feature(L2_FEATURE_HIVEAP_AUTO_PROVISIONING);
		}
		if ("tvClass".equals(getLstForward())){
			setSelectedL2Feature(L2_FEATURE_TV_CLASS);
		}
		if ("tvComputerCart".equals(getLstForward())){
			setSelectedL2Feature(L2_FEATURE_TV_COMPUTERCART);
		}
		if ("tvStudentRoster".equals(getLstForward())){
			setSelectedL2Feature(L2_FEATURE_TV_STUDENTROSTER);
		}
		if ("tvResourceMap".equals(getLstForward())){
			setSelectedL2Feature(L2_FEATURE_TV_RESOURCEMAP);
		}
		if("oneTimePassword".equals(getLstForward())){
			setSelectedL2Feature(L2_FEATURE_ONETIMEPASSWORD);
		}
	}

	private void saveFile() {
		if (null != uploadFileName) {
			if (null != upload && !"".equals(uploadFileName)) {
				// the file format is csv
				if (!uploadFileName.endsWith(".csv")) {
					addActionError(MgrUtil.getUserMessage(
						"error.formatInvalid", "CSV File"));
					return;
				}
				// the file cannot be empty
				if (upload.length() == 0) {
					addActionError(MgrUtil.getUserMessage(
						"error.licenseFailed.file.invalid"));
					return;
				}
				try {
					result.append("\n====================Begin====================\n\n");

					// get the data from file
					CSVParser shredder = new CSVParser(
							new InputStreamReader(new FileInputStream(upload))
					);

					//shredder.setCommentStart("#*//");
					allvalue = shredder.getAllValues();
					if (null == allvalue || allvalue.length == 0) {
						addActionError(MgrUtil.getUserMessage("hm.system.log.import.csv.no.valid.value.import"));
					} else {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.begin.check.csvfile",uploadFileName)).append("\n");
						boolean ifMapHasValue = false;
						if ("localUser".equals(strListForward)) {
							// check the local user data in this file
							readAndCheckAAALocalUser();

							if (!localUserMap.isEmpty()) {
								ifMapHasValue = true;
							}
						}
						if ("localUserGroup".equals(strListForward)) {
							// check the local user group data in this file
							readAndCheckAAAUserGroup();

							if (!userGroupMap.isEmpty()) {
								ifMapHasValue = true;
							}
						}
						if ("unManagedDevice".equals(strListForward)) {
							// check the HiveAP data in this file
							readAndCheckHiveAPNewInfo();

							if (!newHiveApMap.isEmpty() || !m_vct_hiveAP.isEmpty()) {
								ifMapHasValue = true;
							}
						}
						if ("hiveApNew".equals(strListForward)) {
							// check the HiveAP data in this file
							readAndCheckHiveAPNewInfo();

							if (!newHiveApMap.isEmpty() || !m_vct_hiveAP.isEmpty()) {
								ifMapHasValue = true;
							}
						}
						if ("macFilter".equals(strListForward)) {
							// check the mac filter data in this file
							readAndCheckMacFilter();

							if (!macFilterMap.isEmpty()|| !mergeMacFilterList.isEmpty()) {
								ifMapHasValue = true;
							}else{
								generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.system.log.import.csv.import.mac.filter.file",uploadFileName));
							}
						}
						if ("ipAddress".equals(strListForward)) {
							// check the IpAddressOrHostName data in this file
							readAndCheckIpAddressOrHostName();

							if (!ipObjectMap.isEmpty()) {
								ifMapHasValue = true;
							}
						}
						if ("radiusAssignment".equals(strListForward)) {
							// check the AaaRadiusSetting data in this file
							readAndCheckAaaRadiusSetting();

							if (!aaaRadiusMap.isEmpty()) {
								ifMapHasValue = true;
							}
						}
						if ("autoProvisioningConfig".equals(getLstForward())) {
							// check the serial number data in this file
							readSerialNumbers();
							if (!serialNumbers.isEmpty()) {
								ifMapHasValue = true;
							}
						}
						if ("autoProvisioningConfigIpSubNetworks".equals(getLstForward())) {
							//import ip subnetwork to database
							readIpSubNetworks();
							if (ipSubNetworks != null && !ipSubNetworks.isEmpty()) {
								ifMapHasValue = true;
							}
						}
						if ("tvClass".equals(getLstForward())) {
							// import the mac filter data to database
							readTvClass();
							if (!tvClassMap.isEmpty()) {
								ifMapHasValue = true;
							}
						}
						if ("tvComputerCart".equals(getLstForward())) {
							// import the IpAddressOrHostName data to database
							readTvComputerCart();
							if (!tvComputerCartMap.isEmpty()) {
								ifMapHasValue = true;
							}
						}
						if ("tvStudentRoster".equals(getLstForward())) {
							// import the AaaRadiusSetting data to database
							readTvStudentRoster();
							if (!tvStudentRosterMap.isEmpty()) {
								ifMapHasValue = true;
							}
						}
						if ("tvResourceMap".equals(getLstForward())) {
							// import the serial number data to database
							readTvResourceMap();
							if (!tvResourceMap.isEmpty()) {
								ifMapHasValue = true;
							}
						}
						if ("oneTimePassword".equals(strListForward)) {
							// check the IpAddressOrHostName data in this file
							readAndCheckOneTimePasswords();

							if (!oneTimePasswordMap.isEmpty()) {
								ifMapHasValue = true;
							}
						}
						
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.stop.check.file",uploadFileName)).append("\n\n");
						if (ifMapHasValue) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.begin,import.records",
									uploadFileName)).append("\n");

							int successNumber = 0;
							String titleName = "";
							if ("localUser".equals(strListForward)) {
								// import the local user data to database
								successNumber = createAAALocalUser();
								titleName = MgrUtil.getUserMessage("hm.system.log.import.csv.aaa.local.user");
							}
							if ("localUserGroup".equals(strListForward)) {
								// import the local user group data to database
								successNumber = createAAAUserGroup();
								titleName = MgrUtil.getUserMessage("hm.system.log.import.csv.aaa.local.user.group");
							}
							int[] successNums = new int[]{0, 0};
							if ("hiveApNew".equals(strListForward)) {
								// import the HiveAP data to database
								successNums = createHiveAp();
								titleName = MgrUtil.getUserMessage("hm.system.log.import.csv.hiveap");
							}
							if ("unManagedDevice".equals(strListForward)) {
								// import the HiveAP data to database
								successNums = createHiveAp();
								titleName = MgrUtil.getUserMessage("glasgow_05.hm.system.log.import.csv.device");
							}
							if ("macFilter".equals(strListForward)) {
								// import the mac filter data to database
								successNumber = createMacFilter();
								titleName = MgrUtil.getUserMessage("hm.system.log.import.csv.mac.filter");
							}
							if ("ipAddress".equals(strListForward)) {
								// import the IpAddressOrHostName data to database
								successNumber = createIpAddressOrHostName();
								titleName = MgrUtil.getUserMessage("hm.system.log.import.csv.ip.object.host.name");
							}
							if ("radiusAssignment".equals(strListForward)) {
								// import the AaaRadiusSetting data to database
								successNumber = createAaaRadiusSetting();
								titleName = MgrUtil.getUserMessage("hm.system.log.import.csv.aaa.client.setting");
							}
							if ("autoProvisioningConfig".equals(strListForward)) {
								// import the serial number data to database
								successNumber = updateSerialNumbers();
								titleName = NmsUtil.getOEMCustomer().getAccessPonitName() + " " + MgrUtil.getUserMessage("hm.system.log.import.csv.serial.number");
							}
							if ("autoProvisioningConfigIpSubNetworks".equals(strListForward)) {
								// import the IP Subnetwork data to database
								successNumber = updateIPSubnetworks();
								titleName = NmsUtil.getOEMCustomer().getAccessPonitName()+ " " + MgrUtil.getUserMessage("hm.system.log.import.csv.ip.subnetwork");
							}
							
							if ("tvClass".equals(strListForward)) {
								// import the mac filter data to database
								successNumber = createTvClass();
								titleName = MgrUtil.getUserMessage("hm.system.log.import.csv.class");
							}
							if ("tvComputerCart".equals(strListForward)) {
								// import the IpAddressOrHostName data to database
								successNumber = createTvComputerCart();
								titleName = MgrUtil.getUserMessage("hm.system.log.import.csv.computer.cart");
							}
							if ("tvStudentRoster".equals(strListForward)) {
								// import the AaaRadiusSetting data to database
								successNumber = createTvStudentRoster();
								titleName = MgrUtil.getUserMessage("hm.system.log.import.csv.student.roster");
							}
							if ("tvResourceMap".equals(strListForward)) {
								// import the serial number data to database
								successNumber = createTvResourceMap();
								titleName = MgrUtil.getUserMessage("hm.system.log.import.csv.resource.map");
							}
							
							if ("oneTimePassword".equals(strListForward)){
								//import the OneTime password data to database
								successNumber = createOneTimePassword();
								titleName = MgrUtil.getUserMessage("hm.system.log.import.csv.one.time.password");
							}

							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.records.stop.import",
									uploadFileName)).append("\n\n");

							if ("hiveApNew".equals(strListForward)) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.records.import.success")).append(successNums[0])
									.append("\n");
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.records.data.update.success")).append(successNums[1])
									.append("\n");
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.import.update.data.complete",new String[]{NmsUtil.getOEMCustomer().getAccessPonitName(),uploadFileName})).append("\n\n");
							} else if ("unManagedDevice".equals(strListForward)) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.records.import.success")).append(successNums[0])
								.append("\n");
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.import.file.complete",new String[]{titleName,uploadFileName})).append("\n\n");
							} else if ("tvClass".equals(strListForward)) {
								if (successNumber!=-1) {
									result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.records.import.success")).append(successNumber)
									.append("\n");
								} else {
									result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.records.import.success")).append(0)
									.append("\n");
									result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.max.number.class")).append(" ").append(TvClassAction.MAX_NUMBER_TV_CLASS).append("!")
									.append("\n");
								}
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.import.file.complete",new String[]{titleName,uploadFileName})).append("\n\n");
							} else if ("tvStudentRoster".equals(strListForward)) {
								if (successNumber!=-1) {
									result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.records.import.success")).append(successNumber)
									.append("\n");
								} else {
									result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.records.import.success")).append(0)
									.append("\n");
									result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.max.number.students")).append(" ").append(TvStudentRosterAction.MAX_NUMBER_TV_STUDENT).append("!")
									.append("\n");
								}
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.import.file.complete",new String[]{titleName,uploadFileName})).append("\n\n");
							} else if ("tvComputerCart".equals(strListForward)) {
								if (successNumber!=-1) {
									result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.records.import.success")).append(successNumber)
									.append("\n");
								} else {
									result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.records.import.success")).append(0)
									.append("\n");
									result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.max.number.computercarts")).append(" ").append(TvStudentRosterAction.MAX_NUMBER_TV_STUDENT).append("!")
									.append("\n");
								}
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.import.file.complete",new String[]{titleName,uploadFileName})).append("\n\n");
							} else if ("tvResourceMap".equals(strListForward)) {
								if (successNumber!=-1) {
									result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.records.import.success")).append(successNumber)
									.append("\n");
								} else {
									result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.records.import.success")).append(0)
									.append("\n");
									result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.max.number.resources")).append(" ").append(TvResourceMapAction.MAX_NUMBER_TV_RESOURCE).append("!")
									.append("\n");
								}
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.import.file.complete",new String[]{titleName,uploadFileName})).append("\n\n");
							} else {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.records.import.success")).append(successNumber)
									.append("\n");
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.import.file.complete",new String[]{titleName,uploadFileName})).append("\n\n");
							}
						} else {
							addActionError(MgrUtil.getUserMessage("hm.system.log.import.csv.no.valid.value.import"));
						}
					}
					result.append("=====================End=====================\n");
					if (shredder != null)
						shredder.close();
				} catch (Exception e) {
					e.printStackTrace();
					addActionError(e.getMessage());
				}
			} else {
				addActionError(MgrUtil.getUserMessage("error.fileNotExist"));
			}
		} else {
			addActionError(MgrUtil.getUserMessage("error.fileNotExist"));
		}
	}

	private String noticeInfo = "";

	private List<MacFilter> mergeMacFilterList;

	/**
	 * The format of one line value.
	 * @return String
	 */
	public String getNoticeInfo() {
		if ("localUserGroup".equals(strListForward)) {
			noticeInfo = getText("config.csvfile.message.localusergroup");
		}
		if ("localUser".equals(strListForward)) {
			noticeInfo = getText("config.csvfile.message.localuser")+" "+groupTitle+" "+
			getText("config.csvfile.message.localuser1");
		}
		if ("autoProvisioningConfig".equals(getLstForward())) {
			noticeInfo = getText("config.csvfile.message.serialNumber");
		} else if ("autoProvisioningConfigIpSubNetworks".equals(getLstForward())) {
			noticeInfo = getText("config.csvfile.message.ipSubNetworks");
		}
		
		if (getShowDomain() && thisDomain != null
				&& HmDomain.HOME_DOMAIN.equals(thisDomain.getDomainName())) {
			if ("macFilter".equals(strListForward)) {
				noticeInfo = getText("config.csvfile.message.macFilterAndDomain");
			} else if ("ipAddress".equals(strListForward)) {
				noticeInfo = getText("config.csvfile.message.ipAddressAndHostName.domain");
			} else if ("radiusAssignment".equals(strListForward)) {
				noticeInfo = getText("config.csvfile.message.aaaRadiusService.domain");
			} else if ("hiveApNew".equals(strListForward)) {
				noticeInfo = MgrUtil.getUserMessage("config.csvfile.message.hiveAP.new.vhm",
						NmsUtil.filterHiveAPModelString(HiveAp.HIVEAP_MODEL,isEasyMode()))
						+ getText("config.csvfile.message.domain");
			} else if ("tvClass".equals(strListForward)){
				noticeInfo = getText("config.csvfile.message.tvClass.vhm");
			} else if ("tvComputerCart".equals(strListForward)){
				noticeInfo = getText("config.csvfile.message.tvComputerCart.vhm");
			} else if ("tvStudentRoster".equals(strListForward)){
				noticeInfo = getText("config.csvfile.message.tvStudentRoster.vhm");
			} else if ("tvResourceMap".equals(strListForward)){
				noticeInfo = getText("config.csvfile.message.tvResourceMap.vhm");
			} else if("oneTimePassword".equals(strListForward)){
				noticeInfo = getText("config.csvfile.message.onetime.password.domain");
			} else if("unManagedDevice".equals(strListForward)){
				noticeInfo = MgrUtil.getUserMessage("glasgow_05.config.csvfile.message.hiveAP.new.vhm",
						NmsUtil.filterHiveAPModelString(HiveAp.HIVEAP_MODEL,isEasyMode()))
						+ getText("glasgow_05.config.csvfile.message.domain");
			} else {
				noticeInfo = noticeInfo + getText("config.csvfile.message.domain");
			}
		} else if ("macFilter".equals(strListForward)) {
			noticeInfo = getText("config.csvfile.message.macFilter");
		} else if ("ipAddress".equals(strListForward)) {
			noticeInfo = getText("config.csvfile.message.ipAddressAndHostName");
		} else if ("radiusAssignment".equals(strListForward)) {
			noticeInfo = getText("config.csvfile.message.aaaRadiusService");
		} else if ("hiveApNew".equals(strListForward)) {
			if (isEasyMode()) {
				noticeInfo = MgrUtil.getUserMessage("config.csvfile.message.hiveAP.new.express.mode",
						NmsUtil.filterHiveAPModelString(HiveAp.HIVEAP_MODEL,isEasyMode()));
			} else {
				noticeInfo = MgrUtil.getUserMessage("config.csvfile.message.hiveAP.new",
						NmsUtil.filterHiveAPModelString(HiveAp.HIVEAP_MODEL,isEasyMode()));
			}
		} else if ("unManagedDevice".equals(strListForward)) {
			if (isEasyMode()) {
				noticeInfo = MgrUtil.getUserMessage("glasgow_05.config.csvfile.message.hiveAP.new.express.mode",
						NmsUtil.filterHiveAPModelString(HiveAp.HIVEAP_MODEL,isEasyMode()))
						+ getText("glasgow_05.config.csvfile.message.domain");
			} else {
				noticeInfo = MgrUtil.getUserMessage("glasgow_05.config.csvfile.message.hiveAP.new",
						NmsUtil.filterHiveAPModelString(HiveAp.HIVEAP_MODEL,isEasyMode()))
						+ getText("glasgow_05.config.csvfile.message.domain");
			}
		} else if ("tvClass".equals(strListForward)){
			noticeInfo = getText("config.csvfile.message.tvClass");
		} else if ("tvComputerCart".equals(strListForward)){
			noticeInfo = getText("config.csvfile.message.tvComputerCart");
		} else if ("tvStudentRoster".equals(strListForward)){
			noticeInfo = getText("config.csvfile.message.tvStudentRoster");
		} else if ("tvResourceMap".equals(strListForward)){
			noticeInfo = getText("config.csvfile.message.tvResourceMap");
		}else if("oneTimePassword".equals(strListForward)){
			noticeInfo = getText("config.csvfile.message.onetime.password");
		}
		
		return noticeInfo;
	}

	public boolean getIsTeacherView(){
		return "tvClass".equals(strListForward) || "tvComputerCart".equals(strListForward) || "tvStudentRoster".equals(strListForward) || "tvResourceMap".equals(strListForward);
	}
	
	public boolean getIsLocalUserFlag() {
		return "localUser".equals(strListForward);
	}

	public String getLocalFileName() {
		return downLoadFileName;
	}

	public InputStream getInputStream() throws Exception {
		return new FileInputStream(inputPath);
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getUploadContentType() {
		return uploadContentType;
	}

	public void setUploadContentType(String uploadContentType) {
		this.uploadContentType = uploadContentType;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	/**
	 * Read the local user file and check the data.
	 */
	private void readAndCheckAAALocalUser() {
		localUserMap = new HashMap<String, List<LocalUser>>();
		try {
			int intLine = 0;
			LocalUser dto;
			String where = "groupName = :s1 AND userType = :s2";
			Object[] values = new Object[2];
			lineValue:
			for (String[] value : allvalue) {
				intLine++;
				int oldLine = intLine;
				boolean lineBool = false;
				// the value contains line-breaks
				for (String lineValue : value) {
					if (lineValue.contains("\r\n")) {
						intLine = intLine + lineValue.split("\r\n").length - 1;
						lineBool = true;
					}
				}
				if (lineBool) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(oldLine).append(" - ").append(intLine).append(" ").append(
							MgrUtil.getUserMessage("hm.system.log.import.csv.field.value.linebreaks.check").toLowerCase())
						.append("\n");
					continue;
				}
				HmDomain domain = thisDomain;
				if (!checkTheLineValue(value, intLine)) {
					continue;
				}
				int maxField = 6;
				boolean ifContainDomain = false;
				if (getShowDomain()
						&& thisDomain != null
						&& HmDomain.HOME_DOMAIN.equals(thisDomain
								.getDomainName())) {
					maxField = 7;
					ifContainDomain = true;
				}
				if (value.length > maxField || value.length < 4) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" " + intLine + " ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.values.contain.fields4",String.valueOf(maxField)))
						.append("\n");
					continue;
				}

				if (value[0].length() > 32
						|| value[0].length() < 1) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" " + intLine + " ").append(
							MgrUtil.getUserMessage("hm.system.log.import.csv.user.name.length"))
						.append(value[0]).append("\n");
					continue;
				} else {
					String strResult = checkTheSpecialCharacter(value[0], LOCAL_USER_STRING_LIMIT);
					if (!"".equals(strResult)) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" " + intLine + " ").append(
								MgrUtil.getUserMessage("hm.system.log.import.csv.user.name.not.contain")).append(" ").append(strResult).append(" :: ").append(value[0]).append("\n");
						continue;
					}
				}
				dto = new LocalUser();

				// for the error line number
				dto.setId(Long.valueOf(intLine));

				/*
				 * Get the user type.
				 */
				int userType = LocalUserGroup.USERGROUP_USERTYPE_RADIUS;
				int maxLength = 63;
				int minLength = 8;
				if (value[1].equals("3")) {
					userType = LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK;
					maxLength = 63;
					minLength = 8;
				} else if (!value[1].equals("1")) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" " + intLine + " ").append(
							MgrUtil.getUserMessage("hm.system.log.import.csv.user.type.format.wrong"))
						.append(value[1]).append("\n");
					continue;
				}
				dto.setUserType(userType);

//				if (LocalUserGroup.USERGROUP_USERTYPE_RADIUS == userType) {
//					maxField = maxField - 1;
//					if (value.length > maxField) {
//						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
//						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.radius.user.values.check",String.valueOf(maxField)))
//							.append("\n");
//						continue;
//					}
//				}

				/*
				 * Get User Group
				 */
				String errorTitle = isEasyMode() ? (LocalUserGroup.USERGROUP_USERTYPE_RADIUS == userType ? "HiveAP" : "SSID") : groupTitle;
				if (value[2].length() > 32
					|| value[2].length() < 1) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" " + intLine + " ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.hiveap.name.length.check",errorTitle))
						.append(value[2]).append("\n");
					continue;
				} else {
					values[0] = value[2];
					values[1] = userType;
					List<LocalUserGroup> groupList = QueryUtil
							.executeQuery(LocalUserGroup.class, null, new FilterParams(where, values), domain.getId());
					if (groupList.size() != 1) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" " + intLine + " ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.user.type.exist.db",new String[]{errorTitle,value[2]}))
							.append("\n");
						continue;
					}
					dto.setLocalUserGroup(groupList.get(0));
				}

				/*
				 * Get Password
				 */
				if (value[3].length()==0){
					String savePassword = getAutoGeneratePassword(dto.getLocalUserGroup(),value[0].length());
					if (userType == LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK){
						if (!checkPasswordCorrect(dto.getLocalUserGroup(), intLine, savePassword, value[0])) {
							continue;
						}
					}
					dto.setLocalUserPassword(savePassword);
				} else {
					if (value[3].length() > maxLength
							|| value[3].length() < minLength) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" " + intLine + " ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.password.length.scope")).append(" ").append(minLength).append("-").append(maxLength).append(" :: ")
							.append(value[3]).append("\n");
						continue;
					} else {
						if (userType == LocalUserGroup.USERGROUP_USERTYPE_RADIUS) {
							String strResult = checkTheSpecialCharacter(value[3], SINGLE_STRING_LIMIT);
							if (!"".equals(strResult)) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" " + intLine + " ").append(
										MgrUtil.getUserMessage("hm.system.log.import.csv.password.not.contain.check")).append(" ").append(strResult).append(" :: ").append(value[3]).append("\n");
								continue;
							}
						} else {
							if (!checkPasswordCorrect(dto.getLocalUserGroup(), intLine, value[3], value[0])) {
								continue;
							}
						}
					}
					dto.setLocalUserPassword(value[3]);
				}

				int manuallyIndex = 4;

				if (LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK == userType) {
					/*
					 * Get Email Address
					 */
					if (value.length > 4 && !value[4].equals("")) {
						if (value[4].length() > 128) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" " + intLine + " ").append(
									MgrUtil.getUserMessage("hm.system.log.import.csv.email.address.length.check"))
								.append(value[4]).append("\n");
							continue;
						} else if (!checkMailAddress(value[4], intLine, MgrUtil.getUserMessage("hm.system.log.import.csv.email.invalid"))) {
							continue;						
						}
						dto.setMailAddress(value[4]);
					}
					manuallyIndex++;
				}

				/*
				 * Get Description
				 */
				if (value.length > manuallyIndex && value[manuallyIndex].length() > 64) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" " + intLine + " ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.descri.length.check"))
						.append(value[manuallyIndex]).append("\n");
					continue;
				}
				dto.setDescription(value.length > manuallyIndex ? value[manuallyIndex] : "");
				manuallyIndex++;

				if (value.length == manuallyIndex+1 && ifContainDomain) {
					if (value[manuallyIndex].length() > 32) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" " + intLine + " ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.virtual.hm.length"))
							.append(value[manuallyIndex]).append("\n");
						continue;
					} else {
						domain = QueryUtil.findBoByAttribute(
								HmDomain.class, "domainName", value[manuallyIndex]);
						if (null == domain) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" " + intLine + " ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.virtual.db.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
								.append(value[manuallyIndex]).append("\n");
							continue;
						}
					}
				}

				// get the domain id
				String key = String.valueOf(domain.getId());

				if (QueryUtil.findBoByAttribute(LocalUser.class,
					"userName", value[0], domain.getId()) != null) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" " + intLine + " ").append(
							MgrUtil.getUserMessage("hm.system.log.import.csv.user.name.exist.db",value[0])).append("\n");
					continue;
				}
				// check if the name exist in this file
				if (null != localUserMap.get(key)) {
					for (LocalUser oneUser : localUserMap.get(key)) {
						if (value[0].equals(oneUser.getUserName())) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" " + intLine + " ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.user.name.exist.file",value[0])).append("\n");
							continue lineValue;
						}
					}
				}
				dto.setUserName(value[0]);
				dto.setOwner(domain);

				if (null == localUserMap.get(key)) {
					List<LocalUser> vct_aaa = new ArrayList<LocalUser>();
					vct_aaa.add(dto);
					localUserMap.put(key, vct_aaa);
				} else {
					localUserMap.get(key).add(dto);
				}
			}
		} catch (Exception ex) {
			addActionError(ex.getMessage());
		}
	}
	
	private String getAutoGeneratePassword(LocalUserGroup userGroup, int userNameLen) {
		if (userGroup.getUserType() == LocalUserGroup.USERGROUP_USERTYPE_RADIUS) {
			return MgrUtil.getRandomString(63, 7);
		} else {
			int charLimit = 0;
			int passwordLength=63;
			if (userGroup.getBlnCharLetters()) {
				charLimit = charLimit + 1;
			}
			if (userGroup.getBlnCharDigits()) {
				charLimit = charLimit + 2;
			}
			if (userGroup.getBlnCharSpecial()) {
				charLimit = charLimit + 4;
			}
			if (userGroup.getPersonPskCombo() == LocalUserGroup.PSKFORMAT_COMBO_NOCOMBO) {
				if (userGroup.getBlnCharLetters()) {
					charLimit = 1;
				} else if (userGroup.getBlnCharDigits()) {
					charLimit = 2;
				} else {
					charLimit = 4;
				}
			}
			if (userGroup.getPskGenerateMethod()==LocalUserGroup.PSK_METHOD_PASSWORD_USERNAME
				&& userGroup.getUserType() == LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK) {
				int existPassLength = userGroup.getConcatenateString().length() + userNameLen;
				passwordLength = 63 - existPassLength;
			}
			return MgrUtil.getRandomString(passwordLength, charLimit);
		}
	}

	/**
	 * Read the local user group file and check the data.
	 */
	private void readAndCheckAAAUserGroup() {
		userGroupMap = new HashMap<String, List<LocalUserGroup>>();
		try {
			int intLine = 0;
			LocalUserGroup dto;
			lineValue:
			for (String[] value : allvalue) {
				intLine++;
				int oldLine = intLine;
				boolean lineBool = false;
				// the value contains line-breaks
				for (String lineValue : value) {
					if (lineValue.contains("\r\n")) {
						intLine = intLine + lineValue.split("\r\n").length - 1;
						lineBool = true;
					}
				}
				if (lineBool) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(oldLine).append(" - ").append(intLine).append(" ").append(
							MgrUtil.getUserMessage("hm.system.log.import.csv.field.value.linebreaks.check").toLowerCase())
						.append("\n");
					continue;
				}
				if (!checkTheLineValue(value, intLine)) {
					continue;
				}
				HmDomain domain = thisDomain;
				int maxField = 9;
				boolean ifContainDomain = false;
				if (getShowDomain()
						&& thisDomain != null
						&& HmDomain.HOME_DOMAIN.equals(thisDomain
								.getDomainName())) {
					maxField++;
					ifContainDomain = true;
				}
				if (value.length > maxField || value.length < 6) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.values.contain.fields6",String.valueOf(maxField)))
						.append("\n");
					continue;
				}

				/*
				 * check the length of name
				 */
				if (value[0].length() < 1 || value[0].length() > 32) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.user.group.length"))
						.append(value[0]).append("\n");
					continue;
				} else {
					String strResult = checkTheSpecialCharacter(value[0], LOCAL_USER_GROUP_STRING_LIMIT);
					if (!"".equals(strResult)) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
								MgrUtil.getUserMessage("hm.system.log.import.csv.user.group.not.contain")).append(" ").append(strResult).append(" :: ").append(value[0]).append("\n");
						continue;
					}
				}
				dto = new LocalUserGroup();
				/*
				 * Get the user type.
				 */
				int userType = LocalUserGroup.USERGROUP_USERTYPE_RADIUS;
				String errorType = "RADIUS user group";
				if (value[1].equals("2")) {
					userType = LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK;
					errorType = "Private PSK-Auto user group";
				} else if (value[1].equals("3")) {
					userType = LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK;
					errorType = "Private PSK-Manual user group";
				} else if (!value[1].equals("1")) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
							MgrUtil.getUserMessage("hm.system.log.import.csv.user.type.format.wrong"))
						.append(value[1]).append("\n");
					continue;
				}
				dto.setUserType(userType);
				if (LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK != userType) {
					if (value.length > maxField-1) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.values.contain.more.fields",new String[]{errorType,String.valueOf(maxField - 1)}))
							.append("\n");
						continue;
					}
				} else {
					if (value.length < 8) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.values.errortype",errorType))
							.append("\n");
						continue;
					}
				}

				/*
				 * Get the user attribute.
				 */
				if (value[2].equals("")){
					dto.setUserProfileId(-1);
				} else {
					if (!checkValue(value[2], 0, 4095)) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
								MgrUtil.getUserMessage("hm.system.log.import.csv.user.attribute.length"))
						.append(value[2]).append("\n");
						continue;
					} else {
						dto.setUserProfileId(Integer.parseInt(value[2]));
					}
				}

				/*
				 * Get the vlan id.
				 */
				if (value[3].equals("")){
					dto.setVlanId(-1);	
				} else {
					if (!checkValue(value[3], 1, 4094)) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
								MgrUtil.getUserMessage("hm.system.log.import.csv.vlan.id")).append(
							value[3]).append("\n");
						continue;
					} else {
						dto.setVlanId(Integer.parseInt(value[3]));
					}
				}

				/*
				 * Get the re-auth time.
				 */
				if (value[4].equals("") || !checkValue(value[4], 600, 86400)) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
							MgrUtil.getUserMessage("hm.system.log.import.csv.re.auth.time"))
						.append(value[4]).append("\n");
					continue;
				} else {
					dto.setReauthTime(Integer.parseInt(value[4]));
				}

				/*
				 * Get the credential persistency.
				 */
				int credentialType = LocalUserGroup.USERGROUP_CREDENTIAL_FLASH;
				if (value[5].equals("2")) {
					credentialType = LocalUserGroup.USERGROUP_CREDENTIAL_DRAM;
				} else if (!value[5].equals("1")) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
							MgrUtil.getUserMessage("hm.system.log.import.csv.credential.persistency.wrong"))
						.append(value[5]).append("\n");
					continue;
				}
				if (LocalUserGroup.USERGROUP_USERTYPE_RADIUS != userType) {
					if (credentialType == LocalUserGroup.USERGROUP_CREDENTIAL_DRAM) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.credential.persistency.check",errorType))
							.append(value[5]).append("\n");
						continue;
					}
				}
				dto.setCredentialType(credentialType);

				int manuallyIndex = 6;
//				if (LocalUserGroup.USERGROUP_USERTYPE_RADIUS != userType) {
					/*
					 * Automatically generated private PSK
					 */
					if (LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK == userType) {

						// get user name prefix
						if (value[6].length() < 1 || value[6].length() > 28) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.user.name.prefix.length.check"))
								.append(value[6]).append("\n");
							continue;
						} else {
							String strResult = checkTheSpecialCharacter(value[6], LOCAL_USER_STRING_LIMIT);
							if (!"".equals(strResult)) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
										MgrUtil.getUserMessage("hm.system.log.import.csv.user.name.prefix.not.contain")).append(" ").append(strResult).append(" :: ").append(value[6]).append("\n");
								continue;
							}
						}
						dto.setUserNamePrefix(value[6]);

						// get secret
						if (value[7].length() < 1 || value[7].length() > 64) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.secret.length.check"))
								.append(value[7]).append("\n");
							continue;
						} else {
							String strResult = checkTheSpecialCharacter(value[7], SINGLE_STRING_LIMIT);
							if (!"".equals(strResult)) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
										MgrUtil.getUserMessage("hm.system.log.import.csv.secret.not.contain")).append(" ").append(strResult).append(" :: ").append(value[7]).append("\n");
								continue;
							}
						}
						dto.setPskSecret(value[7]);

						// get location variable
//						if (value[8].length() > 32) {
//							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
//								.append(" failed : the length of Location Variable cannot be more than 32 :: ")
//								.append(value[8]).append("\n");
//							continue;
//						}
//						dto.setPskLocation(value[8]);

						// get psk length
//						if (!checkValue(value[9], 8, 63)) {
//							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
//								" failed : PSK Length must be 8-63 :: ").append(
//								value[9]).append("\n");
//							continue;
//						}
//						dto.setPskLength(Integer.valueOf(value[9]));
						manuallyIndex = 8;
					}
					/*
					 * Get Generation Method
					 */
//					int pskGenerateMethod = LocalUserGroup.PSK_METHOD_PASSWORD_ONLY;
//					if (value[manuallyIndex].equals("2")) {
//						pskGenerateMethod = LocalUserGroup.PSK_METHOD_PASSWORD_USERNAME;
//					} else if (!value[manuallyIndex].equals("1")) {
//						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
//							" failed : the format of Generation Method is wrong :: ")
//							.append(value[manuallyIndex]).append("\n");
//						continue;
//					}
//					dto.setPskGenerateMethod(pskGenerateMethod);
//
//					manuallyIndex += 1;
//					if (pskGenerateMethod == LocalUserGroup.PSK_METHOD_PASSWORD_USERNAME) {
//						// get Concatenating String
//						if (value[manuallyIndex].length() > 8) {
//							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
//								.append(" failed : the length of Concatenating String cannot be more than 8 :: ")
//								.append(value[manuallyIndex]).append("\n");
//							continue;
//						}
//						dto.setConcatenateString(value[manuallyIndex]);
//						manuallyIndex += 1;
//					}

					/*
					 * Get Validity Period
					 */
//					int validTimeType = LocalUserGroup.VALIDTYME_TYPE_ALWAYS;
//					if (value[manuallyIndex].equals("2")) {
//						validTimeType = LocalUserGroup.VALIDTYME_TYPE_ONCE;
//					} else if (value[manuallyIndex].equals("3")) {
//						if (LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK != userType) {
//							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
//								" failed : the format of Validity Period is wrong :: ")
//								.append(value[manuallyIndex]).append("\n");
//							continue;
//						}
//						validTimeType = LocalUserGroup.VALIDTYME_TYPE_SCHEDULE;
//					} else if (!value[manuallyIndex].equals("1")) {
//						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
//							" failed : the format of Validity Period is wrong :: ")
//							.append(value[manuallyIndex]).append("\n");
//						continue;
//					}
//					dto.setValidTimeType(validTimeType);
//					manuallyIndex += 1;
//
//					if (validTimeType == LocalUserGroup.VALIDTYME_TYPE_ONCE) {
//						/*
//						 * Get start time.
//						 */
//						if (!value[manuallyIndex].equals("")){
//							if (!checkTimeValue(value[manuallyIndex], intLine, "Start Time")){
//								continue;
//							}
//							try{
//								Calendar startTime = Calendar.getInstance();
//								startTime.set(Integer.valueOf(value[manuallyIndex].substring(0, 4)), Integer.valueOf(value[manuallyIndex].substring(4, 6))-1,
//										Integer.valueOf(value[manuallyIndex].substring(6, 8)), Integer.valueOf(value[manuallyIndex].substring(8, 10)), Integer.valueOf(value[manuallyIndex].substring(10)),0);
//								dto.setStartTime(startTime.getTime());
//							} catch (Exception e) {
//								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
//										" failed: the format of Start Time is wrong :: ")
//										.append(value[manuallyIndex]).append("\n");
//								continue;
//							}
//						} else {
//							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
//								" failed: the Start Time is required :: ")
//								.append(value[manuallyIndex]).append("\n");
//							continue;
//						}
//						manuallyIndex += 1;
//						if (value.length > manuallyIndex) {
//							/*
//							 * Get end time
//							 */
//							if (!value[manuallyIndex].equals("")){
//								if (!checkTimeValue(value[manuallyIndex], intLine, "End Time")){
//									continue;
//								}
//								try{
//									Calendar endTime = Calendar.getInstance();
//									endTime.set(Integer.valueOf(value[manuallyIndex].substring(0, 4)), Integer.valueOf(value[manuallyIndex].substring(4, 6))-1,
//											Integer.valueOf(value[manuallyIndex].substring(6, 8)), Integer.valueOf(value[manuallyIndex].substring(8, 10)), Integer.valueOf(value[manuallyIndex].substring(10)),0);
//									dto.setExpiredTime(endTime.getTime());
//								} catch (Exception e) {
//									result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
//											" failed: the format of End Time is wrong :: ")
//											.append(value[manuallyIndex]).append("\n");
//									continue;
//								}
//							}
//						} else {
//							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
//								" failed: the fields are not integrated.").append("\n");
//							continue;
//						}
//						manuallyIndex += 1;
//					} else if (validTimeType == LocalUserGroup.VALIDTYME_TYPE_SCHEDULE) {
//						/*
//						 * Get schedule
//						 */
//						if (value[manuallyIndex].length() < 1 || value[manuallyIndex].length() > 32) {
//							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
//								" failed : the length of Schedule must be 1-32 :: ")
//								.append(value[manuallyIndex]).append("\n");
//							continue;
//						} else {
//							Scheduler scheduler = QueryUtil.findBoByAttribute(Scheduler.class,
//									"schedulerName", value[manuallyIndex], domain.getId());
//							if (scheduler == null) {
//									result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
//										" failed : the Schedule (").append(value[manuallyIndex])
//										.append(") does not exist in database.").append("\n");
//									continue;
//							} else {
//								dto.setSchedule(scheduler);
//							}
//						}
//						manuallyIndex += 1;
//					}
//					if (value.length > manuallyIndex) {
//						/*
//						 * Get Character Types Permitted
//						 */
//						if (value[manuallyIndex].length() != 3) {
//							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
//								" failed : the format of Character Types Permitted is wrong :: ")
//								.append(value[manuallyIndex]).append("\n");
//							continue;
//						}
//						for (int j = 0; j < value[manuallyIndex].length(); j ++) {
//							char index = value[manuallyIndex].charAt(j);
//							if (index == '1' || index == '0') {
//								switch (j) {
//									case 0:
//										dto.setBlnCharLetters(index == '1');
//										break;
//									case 1:
//										dto.setBlnCharDigits(index == '1');
//										break;
//									case 2:
//										dto.setBlnCharSpecial(index == '1');
//										break;
//									default:
//										break;
//								}
//							} else {
//								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
//									" failed : the format of Character Types Permitted is wrong :: ")
//									.append(value[manuallyIndex]).append("\n");
//								continue lineValue;
//							}
//						}
//						manuallyIndex += 1;
//
//						if (value.length > manuallyIndex) {
//							/*
//							 * Get Character Types Combined
//							 */
//							int personPskCombo = LocalUserGroup.PSKFORMAT_COMBO_AND;
//							if (value[manuallyIndex].equals("1")) {
//								personPskCombo = LocalUserGroup.PSKFORMAT_COMBO_OR;
//							} else if (value[manuallyIndex].equals("3")) {
//								personPskCombo = LocalUserGroup.PSKFORMAT_COMBO_NOCOMBO;
//							} else if (!value[manuallyIndex].equals("2")) {
//								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
//									" failed : the format of Character Types Combined is wrong :: ")
//									.append(value[manuallyIndex]).append("\n");
//								continue;
//							}
//							dto.setPersonPskCombo(personPskCombo);
//							manuallyIndex += 1;
//						} else {
//							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
//								" failed: the fields are not integrated.").append("\n");
//							continue;
//						}
//					} else {
//						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
//							" failed: the fields are not integrated.").append("\n");
//						continue;
//					}
//				}

				/*
				 * Get Use for VoIP phone device authentication   	
				 */
				if (LocalUserGroup.USERGROUP_USERTYPE_RADIUS == userType){
					if(value[manuallyIndex].length() > 1){
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.user.group.voice.device.length"))
						.append(value[manuallyIndex]).append("\n");
						continue;
					}
					
					if(!value[manuallyIndex].equals("1") && !value[manuallyIndex].equals("2")){
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.user.group.voice.device.length"))
						.append(value[manuallyIndex]).append("\n");
						continue;
					}
					
					String strResult = checkTheSpecialCharacter(value[manuallyIndex], LOCAL_USER_STRING_LIMIT);
					if (!"".equals(strResult)) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
								MgrUtil.getUserMessage("hm.system.log.import.csv.user.group.voice.device.not.contain")).append(" ").append(strResult).append(" :: ").append(value[6]).append("\n");
						continue;
					}
					
					if(value[manuallyIndex].equals("1")){
						dto.setVoiceDevice(Boolean.TRUE);
					}else{
						dto.setVoiceDevice(Boolean.FALSE);
					}
					
					manuallyIndex++;
				}
				
				/*
				 * Get description
				 */
				if (value.length > manuallyIndex) {
					if (value[manuallyIndex].length() > 64) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.descri.length.check"))
							.append(value[manuallyIndex]).append("\n");
						continue;
					}
					dto.setDescription(value[manuallyIndex]);
				}

				/*
				 * Get domain name
				 */
				manuallyIndex++;
				if (value.length > manuallyIndex+1) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
							MgrUtil.getUserMessage("hm.system.log.import.csv.fields.not.integrate")).append("\n");
					continue;
				}
				if (value.length == manuallyIndex+1 && ifContainDomain) {
					if (value[manuallyIndex].length() > 32) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.virtual.length.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
							.append(value[manuallyIndex]).append("\n");
						continue;
					} else {
						domain = QueryUtil.findBoByAttribute(
								HmDomain.class, "domainName", value[manuallyIndex]);
						if (null == domain) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.virtual.db.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
								.append(value[manuallyIndex]).append("\n");
							continue;
						}
					}
				}

				// get the domain id
				String key = String.valueOf(domain.getId());

				// check if the name exist in database
				if (QueryUtil.findBoByAttribute(LocalUserGroup.class,
					"groupName", value[0], domain.getId()) != null) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.user.group.exist.db",value[0])).append("\n");
					continue;
				}
				// check if the name exist in this file
				if (null != userGroupMap.get(key)) {
					for (LocalUserGroup oneGroup : userGroupMap.get(key)) {
						if (value[0].equals(oneGroup.getGroupName())) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.user.group.exist.file",value[0])).append("\n");
							continue lineValue;
						}
					}
				}

				// for the error line number
				dto.setGroupName(value[0]);
				dto.setOwner(domain);

				if (null == userGroupMap.get(key)) {
					List<LocalUserGroup> vct_aaa = new ArrayList<LocalUserGroup>();
					vct_aaa.add(dto);
					userGroupMap.put(key, vct_aaa);
				} else {
					userGroupMap.get(key).add(dto);
				}
			}
		} catch (Exception ex) {
			addActionError(ex.getMessage());
		}
	}

	/**
	 * Read the mac filter file and check the data.
	 */
	private void readAndCheckMacFilter() {
		Map<String, List<MacFilter>> macFilterInFileMap = new HashMap<String, List<MacFilter>>();
		macFilterMap = new HashMap<String, List<MacFilter>>();
		macAddreOrOuiMap = new HashMap<String, List<MacOrOui>>();
		mergeMacFilterList =new ArrayList<MacFilter>();
		try {
			int intLine = 0;
			MacFilter dto;
			boolean isSingleLine=true;
			
			//lidan edit,it is used for if mac has description in the format 1
			boolean isSingleLine_des=false;
			
			// distinguish which type of the format
			if(allvalue[0].length>5){
				result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.resource.file.type.format1"))
				.append("\n");
			}else{
				result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.resource.file.type.format2"))
				.append("\n");
				isSingleLine=false;
			}
			lineValue:
			for (String[] value : allvalue) {
				intLine++;
				int oldLine = intLine;
				boolean lineBool = false;
				// the value contains line-breaks
				for (String lineValue : value) {
					if (lineValue.contains("\r\n")) {
						intLine = intLine + lineValue.split("\r\n").length - 1;
						lineBool = true;
					}
				}
				// add SystemLog
				StringBuilder systemLogMsg = new StringBuilder();
				systemLogMsg.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.failure", new String[]{uploadFileName,String.valueOf(intLine)})).append(" : ");
				StringBuffer errorCauseMsg;
				
				if (lineBool) {
					errorCauseMsg =new StringBuffer(MgrUtil.getUserMessage("hm.system.log.import.csv.error.cause"));
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(oldLine).append(" - ").append(intLine).append(" ").
					append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(" : ").append(errorCauseMsg)
						.append("\n");
					
					systemLogMsg.append(errorCauseMsg);
					HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_CONFIGURATION,
							systemLogMsg.toString());
					continue;
				}
				if (!checkTheLineValue(value, intLine)) {
					continue;
				}
				HmDomain domain = thisDomain;
				
				int valueArrayLength = value.length;
				int macLength=valueArrayLength - 3;
				int modMACLength=macLength % 2;
				// test other VHM not 'home'
				boolean isHasVHMName=true;
				if (getShowDomain()
						&& thisDomain != null
						&& HmDomain.HOME_DOMAIN.equals(thisDomain
								.getDomainName())) {
					if (valueArrayLength < 5) {
						errorCauseMsg =new StringBuffer(MgrUtil.getUserMessage("hm.system.log.import.csv.file.fields","5"));
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").
						append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(" : ").append(errorCauseMsg)
							.append("\n");
						
						systemLogMsg.append(errorCauseMsg);
						HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_CONFIGURATION,
								systemLogMsg.toString());
						continue;
					}
					
					//lidan edit
					if (modMACLength != 0 && macLength %3 != 0) {
						errorCauseMsg =new StringBuffer(MgrUtil.getUserMessage("hm.system.log.import.csv.file.mac.length"));
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").
						append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(" : ").append(errorCauseMsg)
							.append("\n");
						
						systemLogMsg.append(errorCauseMsg);
						HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_CONFIGURATION,
								systemLogMsg.toString());
						continue;
					}
					
					//lidan edit
					if(macLength % 3 ==0){
						if("P".equals(value[5]) || "D".equals(value[5])){
							isSingleLine_des = true;
						}				
					}
				} else {
					System.out.println("domain length>>4");
					 macLength=valueArrayLength - 2;
					 modMACLength=macLength % 2;
					if (valueArrayLength < 4) {
						errorCauseMsg =new StringBuffer(MgrUtil.getUserMessage("hm.system.log.import.csv.file.fields","4"));
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").
						append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(" : ").append(errorCauseMsg)
							.append("\n");
						
						systemLogMsg.append(errorCauseMsg);
						HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_CONFIGURATION,
								systemLogMsg.toString());
						continue;
					}

					//lidan edit
					if (modMACLength != 0 && macLength %3 != 0) {
						errorCauseMsg =new StringBuffer(MgrUtil.getUserMessage("hm.system.log.import.csv.file.mac.length"));
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").
						append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(" : ").append(errorCauseMsg)
							.append("\n");
						
						systemLogMsg.append(errorCauseMsg);
						HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_CONFIGURATION,
								systemLogMsg.toString());
						continue;
					}
					
					//lidan edit
					if(macLength % 3 ==0){
						//distinguish whether has the mac address description in the first format
						if("P".equals(value[4]) || "D".equals(value[4])){
							isSingleLine_des = true;
						}
					}
					
					isHasVHMName=false;
				}
				// check VHM name
				if (isHasVHMName) {
					if (value[2].length() > 32) {
						errorCauseMsg =new StringBuffer(MgrUtil.getUserMessage("hm.system.log.import.csv.vhm.name.check", new String[]{NmsUtil.getOEMCustomer().getNmsNameAbbreviation()})).append(value[2]);
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(" : ").append(errorCauseMsg)
							.append("\n");
						
						systemLogMsg.append(errorCauseMsg);
						HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_CONFIGURATION,
								systemLogMsg.toString());
						continue;
					} else if (!"".equals(value[2])) {
						domain = QueryUtil.findBoByAttribute(
								HmDomain.class, "domainName", value[2]);
						if (null == domain) {
							errorCauseMsg =new StringBuffer(MgrUtil.getUserMessage("hm.system.log.import.csv.vhm.name.check", new String[]{NmsUtil.getOEMCustomer().getNmsNameAbbreviation()})).append(value[2]);
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(" : ").append(errorCauseMsg)
								.append("\n");
							
							systemLogMsg.append(errorCauseMsg);
							HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_CONFIGURATION,
									systemLogMsg.toString());
							continue;
						}
					}
				}
				// get the domain id
				String key = String.valueOf(domain.getId());

				if (value[0].length() < 1 || value[0].length() > 32) {
					errorCauseMsg =new StringBuffer(MgrUtil.getUserMessage("hm.system.log.import.csv.mac.filter")).append(value[0]);
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(" : ").append("errorCauseMsg")
						.append("\n");
					
					systemLogMsg.append(errorCauseMsg);
					HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_CONFIGURATION,
							systemLogMsg.toString());
					continue;
				} else {
					String strResult = checkTheSpecialCharacter(value[0], SINGLE_STRING_LIMIT);
					if (!"".equals(strResult)) {
						errorCauseMsg =new StringBuffer(MgrUtil.getUserMessage("hm.system.log.import.csv.mac.filter.name.not.contain")).append(" ").append(strResult).append(" :: ").append(value[0]);
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").
						append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(" : ").append(errorCauseMsg).append("\n");
						
						systemLogMsg.append(errorCauseMsg);
						HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_CONFIGURATION,
								systemLogMsg.toString());
						continue;
					} 
					// check if the name exist in this file
					if (null != macFilterInFileMap.get(key)&&isSingleLine) {
						for (MacFilter oneFilter : macFilterInFileMap.get(key)) {
							if (value[0].equals(oneFilter.getFilterName())) {
								// if type is Format-2, it allows duplicate MAC Filter Name
								errorCauseMsg =new StringBuffer(MgrUtil.getUserMessage("hm.system.log.import.csv.mac.filter.name.exist.list",value[0]));
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").
								append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(" : ")
								.append(errorCauseMsg).append("\n");
								
								systemLogMsg.append(errorCauseMsg);
								HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_CONFIGURATION,
										systemLogMsg.toString());
								continue lineValue;
							}
						}
					}
				}

				if (value[1].length() > 64) {
					errorCauseMsg =new StringBuffer(MgrUtil.getUserMessage("hm.system.log.import.csv.descri.length.check")).append(value[1]);
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").
					append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(" : ")
						.append(errorCauseMsg).append("\n");
					
					systemLogMsg.append(errorCauseMsg);
					HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_CONFIGURATION,
							systemLogMsg.toString());
					continue;
				}
				if (macLength / 2 > 256){
					errorCauseMsg =new StringBuffer(MgrUtil.getUserMessage("hm.system.log.import.csv.mac.address.max.length")).append(value[1]);
					
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").
					append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(" : ")
					.append(errorCauseMsg).append("\n");
					
					systemLogMsg.append(errorCauseMsg);
					HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_CONFIGURATION,
							systemLogMsg.toString());
					continue;
				}
					
				List<MacFilterInfo> filterInfo = new ArrayList<MacFilterInfo>();
				//check if exist the same filter in database 
				MacFilter dbMACFilter = QueryUtil.findBoByAttribute(MacFilter.class, "filterName", value[0], domain.getId(), this);
				List<MacFilterInfo> existsInDBMACList=new ArrayList<MacFilterInfo>();
				
				//lidan edit,format 1 has no mac des
				if(!isSingleLine_des){
					for (int i = isHasVHMName ? 3 : 2; i < valueArrayLength-1; i=i+2) {
						MacFilterInfo singleInfo = new MacFilterInfo();
						boolean isMACExistDB=false;
						MacOrOui singleMac;
						
						//skip empty pair
						if(value[i].trim().length()==0 && value[i+1].trim().length()==0)
							continue;
						
						String macOrOui = value[i].trim();
						String format2MACOUIName = format2MACOUI(macOrOui);
						
						// mac address or oui format check
						if (null==format2MACOUIName) {
							errorCauseMsg =new StringBuffer(MgrUtil.getUserMessage("hm.system.log.import.csv.mac.address.format.error")).append(macOrOui);
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").
							append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(" : ")
									.append(errorCauseMsg).append("\n");
							
							systemLogMsg.append(errorCauseMsg);
							HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_CONFIGURATION,
									systemLogMsg.toString());
							continue lineValue;
						} else {
							// get the mac which has the same mac name as this mac entry
							singleMac = QueryUtil.findBoByAttribute(MacOrOui.class,
									"macOrOuiName", format2MACOUIName, domain.getId(),this);
							if (null == singleMac) {
								boolean macBool = true;
								if (null != macAddreOrOuiMap.get(key)) {
									// get the same mac from the map
									for (MacOrOui oneMac : macAddreOrOuiMap.get(key)) {
										if (format2MACOUIName.equals(oneMac.getMacOrOuiName())) {
											macBool = false;
											singleMac = oneMac;
											break;
										}
									}
								}
								if (macBool) {
									// create the new mac
									singleMac = new MacOrOui();
									singleMac.setMacOrOuiName(format2MACOUIName);// set MAC value to name
									singleMac.setOwner(domain);
									List<SingleTableItem> items = new ArrayList<SingleTableItem>();
									SingleTableItem single = new SingleTableItem();
									single.setDescription(isSingleLine?"":value[1]);
									single.setMacEntry(format2MACOUIName);
									single.setType(SingleTableItem.TYPE_GLOBAL);
									items.add(single);
									singleMac.setItems(items);
									singleMac
											.setTypeFlag(format2MACOUIName.length() == 6 ? MacOrOui.TYPE_MAC_OUI
													: MacOrOui.TYPE_MAC_ADDRESS);
									singleMac.setDefaultFlag(false);
									if (null == macAddreOrOuiMap.get(key)) {
										List<MacOrOui> vct_mac = new ArrayList<MacOrOui>();
										vct_mac.add(singleMac);
										macAddreOrOuiMap.put(key, vct_mac);
									} else {
										macAddreOrOuiMap.get(key).add(singleMac);
									}
								} 
							} else {
								isMACExistDB=true;
							}
						}

						for (MacFilterInfo macInfo : filterInfo) {
							if (singleMac.getMacOrOuiName().equals(
									macInfo.getMacOrOui().getMacOrOuiName())) {
								errorCauseMsg =new StringBuffer(MgrUtil.getUserMessage("hm.system.log.import.csv.mac.address.exist.list")).append(value[i]);
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").
								append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(" : ")
										.append(errorCauseMsg).append("\n");
								
								systemLogMsg.append(errorCauseMsg);
								HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_CONFIGURATION,
										systemLogMsg.toString());
								continue lineValue;
							}
						}
						singleInfo.setMacOrOui(singleMac);
						if (!value[i+1].equals("P") && !value[i+1].equals("D")) {
							errorCauseMsg =new StringBuffer(MgrUtil.getUserMessage("hm.system.log.import.csv.action.format.error")).append(value[i+1]);
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").
							append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(" : ")
								.append(errorCauseMsg).append("\n");
							
							systemLogMsg.append(errorCauseMsg);
							HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_CONFIGURATION,
									systemLogMsg.toString());
							continue lineValue;
						} else {
							singleInfo.setFilterAction(value[i+1].equals("P") ? MacFilter.FILTER_ACTION_PERMIT : MacFilter.FILTER_ACTION_DENY);
						}
						filterInfo.add(singleInfo);
						if(isMACExistDB){
							existsInDBMACList.add(singleInfo);
						}
					}
				}else{
					for (int i = isHasVHMName ? 3 : 2; i < valueArrayLength-1; i=i+3) {
						MacFilterInfo singleInfo = new MacFilterInfo();
						boolean isMACExistDB=false;
						MacOrOui singleMac;
						
						//skip empty pair
						if(value[i].trim().length()==0 && value[i+1].trim().length()==0 && value[i+2].trim().length()==0)
							continue;
						
						String macOrOui = value[i].trim();
						String format2MACOUIName = format2MACOUI(macOrOui);
						
						// mac address or oui format check
						if (null==format2MACOUIName) {
							errorCauseMsg =new StringBuffer(MgrUtil.getUserMessage("hm.system.log.import.csv.mac.address.format.error")).append(macOrOui);
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").
							append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(" : ")
									.append(errorCauseMsg).append("\n");
							
							systemLogMsg.append(errorCauseMsg);
							HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_CONFIGURATION,
									systemLogMsg.toString());
							continue lineValue;
						} else {
							// get the mac which has the same mac name as this mac entry
							singleMac = QueryUtil.findBoByAttribute(MacOrOui.class,
									"macOrOuiName", format2MACOUIName, domain.getId(),this);
							if (null == singleMac) {
								boolean macBool = true;
								if (null != macAddreOrOuiMap.get(key)) {
									// get the same mac from the map
									for (MacOrOui oneMac : macAddreOrOuiMap.get(key)) {
										if (format2MACOUIName.equals(oneMac.getMacOrOuiName())) {
											macBool = false;
											singleMac = oneMac;
											break;
										}
									}
								}
								if (macBool) {
									// create the new mac
									singleMac = new MacOrOui();
									singleMac.setMacOrOuiName(format2MACOUIName);// set MAC value to name
									singleMac.setOwner(domain);
									List<SingleTableItem> items = new ArrayList<SingleTableItem>();
									SingleTableItem single = new SingleTableItem();
									single.setDescription(isSingleLine?(value[i+1].trim()):value[1]);
									single.setMacEntry(format2MACOUIName);
									single.setType(SingleTableItem.TYPE_GLOBAL);
									items.add(single);
									singleMac.setItems(items);
									singleMac
											.setTypeFlag(format2MACOUIName.length() == 6 ? MacOrOui.TYPE_MAC_OUI
													: MacOrOui.TYPE_MAC_ADDRESS);
									singleMac.setDefaultFlag(false);
									if (null == macAddreOrOuiMap.get(key)) {
										List<MacOrOui> vct_mac = new ArrayList<MacOrOui>();
										vct_mac.add(singleMac);
										macAddreOrOuiMap.put(key, vct_mac);
									} else {
										macAddreOrOuiMap.get(key).add(singleMac);
									}
								} 
							} else {
								isMACExistDB=true;
							}
						}

						for (MacFilterInfo macInfo : filterInfo) {
							if (singleMac.getMacOrOuiName().equals(
									macInfo.getMacOrOui().getMacOrOuiName())) {
								errorCauseMsg =new StringBuffer(MgrUtil.getUserMessage("hm.system.log.import.csv.mac.address.exist.list")).append(value[i]);
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").
								append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(" : ")
										.append(errorCauseMsg).append("\n");
								
								systemLogMsg.append(errorCauseMsg);
								HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_CONFIGURATION,
										systemLogMsg.toString());
								continue lineValue;
							}
						}
						singleInfo.setMacOrOui(singleMac);
						if (!value[i+2].toUpperCase().equals("P") && !value[i+2].toUpperCase().equals("D")) {
							errorCauseMsg =new StringBuffer(MgrUtil.getUserMessage("hm.system.log.import.csv.action.format.error")).append(value[i+1]);
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").
							append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(" : ")
								.append(errorCauseMsg).append("\n");
							
							systemLogMsg.append(errorCauseMsg);
							HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_CONFIGURATION,
									systemLogMsg.toString());
							continue lineValue;
						} else {
							singleInfo.setFilterAction(value[i+2].toUpperCase().equals("P") ? MacFilter.FILTER_ACTION_PERMIT : MacFilter.FILTER_ACTION_DENY);
						}
						filterInfo.add(singleInfo);
						if(isMACExistDB){
							existsInDBMACList.add(singleInfo);
						}
					}
				}

				if(null!=dbMACFilter){
					// check MAC Filter in file 
					if(null==macFilterInFileMap.get(key)){
						List<MacFilter> vct_mac = new ArrayList<MacFilter>();
						vct_mac.add(dbMACFilter);
						macFilterInFileMap.put(key, vct_mac);
					}else{
						macFilterInFileMap.get(key).add(dbMACFilter);
					}
					
					if (existsInDBMACList.isEmpty()) {
						// if exists MACFilter but not any MAC/OUI entry, add all entries in file
						dbMACFilter.getFilterInfo().addAll(filterInfo);
						updateMacFilter(mergeMacFilterList,dbMACFilter);
					} else {
						// if exists MACFilter in DataBase, but not the MAC/OUI
						if(filterInfo.size()>existsInDBMACList.size()){
							for (MacFilterInfo macFilterInfo : filterInfo) {
								if(!existsInDBMACList.contains(macFilterInfo)){
									dbMACFilter.getFilterInfo().add(macFilterInfo);
									updateMacFilter(mergeMacFilterList,dbMACFilter);
								}
							}
						}
						
						// if exists MAC and MACFilter in DataBase, associate with MACFilter
						List<MacFilterInfo> notDuplicateFilterList=new ArrayList<MacFilterInfo>();
				Lable: for (MacFilterInfo element : existsInDBMACList) {
							for (MacFilterInfo macFilterInfo : dbMACFilter.getFilterInfo()) {
								if (macFilterInfo.getMacOrOui().getMacOrOuiName()
										.equals(element.getMacOrOui().getMacOrOuiName()))
									continue Lable;
								}
							if(!notDuplicateFilterList.contains(element)){
								notDuplicateFilterList.add(element);
							}
						}
						if(!notDuplicateFilterList.isEmpty()){
							dbMACFilter.getFilterInfo().addAll(notDuplicateFilterList);
							updateMacFilter(mergeMacFilterList,dbMACFilter);
						}
					}
					continue lineValue;
				} 
				//if MACFilter doesn't exist in DataBase, insert a new filter
				dto = new MacFilter();
				dto.setFilterName(value[0]);
				if (isSingleLine)
					dto.setDescription(value[1]);
				dto.setOwner(domain);
				dto.setFilterInfo(filterInfo);
				
				if (null == macFilterMap.get(key)) {
					List<MacFilter> vct_mac = new ArrayList<MacFilter>();
					vct_mac.add(dto);
					macFilterMap.put(key, vct_mac);
				} else {
					boolean isNotExist=true;
					for (MacFilter filter : macFilterMap.get(key)) {
						String filterName = filter.getFilterName();
						if(filterName.equals(value[0])){
							List<MacFilterInfo> notDuplicateFilterList=new ArrayList<MacFilterInfo>();
							isNotExist=false;
							for (MacFilterInfo macFilterInfo : filterInfo) {
								if(diffMacFilterInfo(macFilterInfo,filter)
										&&!notDuplicateFilterList.contains(macFilterInfo)){
									notDuplicateFilterList.add(macFilterInfo);
								}
							}
							filter.getFilterInfo().addAll(notDuplicateFilterList);
							break;
						}
					}
					if(isNotExist)
						macFilterMap.get(key).add(dto);
				}
				if(null==macFilterInFileMap.get(key)){
					List<MacFilter> vct_mac = new ArrayList<MacFilter>();
					vct_mac.add(dto);
					macFilterInFileMap.put(key, vct_mac);
				}else{
					macFilterInFileMap.get(key).add(dto);
				}
			}
		} catch (Exception ex) {
			addActionError(ex.getMessage());
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_CONFIGURATION,MgrUtil.getUserMessage("hm.system.log.import.csv.uploadfiel.read.error",uploadFileName));
		}
	}
	
	/**
	 * update MACFilter Info. with MacFilterInfo in the MACFilter
	 * @param mergeMacFilterList -
	 * @param newMACFilter -
	 */
	private void updateMacFilter(List<MacFilter> mergeMacFilterList, MacFilter newMACFilter) {
		String filterName = newMACFilter.getFilterName();
		
		boolean isNotExist=true;
		for (MacFilter macFilter : mergeMacFilterList) {
			if(macFilter.getFilterName().equals(filterName)){
				isNotExist=false;
				List<MacFilterInfo> tempMacFilterInfoList=new ArrayList<MacFilterInfo>();
				for (MacFilterInfo macFilterInfo : newMACFilter.getFilterInfo()) {
					if(diffMacFilterInfo(macFilterInfo,macFilter)
							&&!tempMacFilterInfoList.contains(macFilterInfo)){
						tempMacFilterInfoList.add(macFilterInfo);
					}
				}
				macFilter.getFilterInfo().addAll(tempMacFilterInfoList);
				break;
			}
		}
		if(isNotExist){
			mergeMacFilterList.add(newMACFilter);
		}
	}

	/**
	 * Determine if have the same MAC/OUI in MACFilter
	 * @param macFilterInfo -
	 * @param macFilter -
	 * @return if contains the MAC/OUI return <b>true</b>; else <b>false</b>
	 */
	private boolean diffMacFilterInfo(MacFilterInfo macFilterInfo, MacFilter macFilter) {
		for (MacFilterInfo element : macFilter.getFilterInfo()){
			if(element.getMacOrOui().getMacOrOuiName().equals(macFilterInfo.getMacOrOui().getMacOrOuiName())){
				return false;
			}
		}		
		return true;
	}

	/**
	 * Read the serial number file and get the data.
	 */
	private void readSerialNumbers(){
		serialNumbers = new ArrayList<HiveApSerialNumber>();
		try {
			int intLine = 0;
			HiveApSerialNumber dto;
			lineValue:
			for (String[] value : allvalue) {
				intLine++;
				int oldLine = intLine;
				boolean lineBool = false;
				// the value contains line-breaks
				for (String lineValue : value) {
					if (lineValue.contains("\r\n")) {
						intLine = intLine + lineValue.split("\r\n").length - 1;
						lineBool = true;
					}
				}
				if (lineBool) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(oldLine).append(" - ").append(intLine).append(" ").append(
							MgrUtil.getUserMessage("hm.system.log.import.csv.field.value.linebreaks.check").toLowerCase())
						.append("\n");
					continue;
				}
				HmDomain domain = thisDomain;
				if (!checkTheLineValue(value, intLine)) {
					continue;
				}
				// there are one or two fields
				if (getShowDomain()
						&& thisDomain != null
						&& HmDomain.HOME_DOMAIN.equals(thisDomain
								.getDomainName())) {
					if (value.length > 2 || value.length < 1) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
								MgrUtil.getUserMessage("hm.system.log.import.csv.values.contain.two.fields"))
							.append("\n");
						continue;
					}
				} else {
					if (value.length != 1) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
								MgrUtil.getUserMessage("hm.system.log.import.csv.values.contain.one.field"))
							.append("\n");
						continue;
					}
				}
				/*
				 * Check Serial Number
				 */
				if (value[0].length() != 14) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
							MgrUtil.getUserMessage("hm.system.log.import.csv.serial.number.length.check"))
						.append(value[0]).append("\n");
					continue;
				} else {
					// it cannot exist in database
					if (QueryUtil.findBoByAttribute(HiveApSerialNumber.class, "lower(serialNumber)", value[0].toLowerCase()) != null) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.serial.number.exist.db"))
							.append(value[0]).append("\n");
						continue;
					}
					// it cannot exist in this file
					for (HiveApSerialNumber serial : serialNumbers) {
						if (value[0].equalsIgnoreCase(serial.getSerialNumber())) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.serial.number.exist.file"))
								.append(value[0]).append("\n");
							continue lineValue;
						}
					}
				}
				/*
				 * Check Virtual HM Name
				 */
				if (value.length == 2) {
					if (value[1].length() > 32) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.virtual.length.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
							.append(value[1]).append("\n");
						continue;
					} else {
						domain = QueryUtil.findBoByAttribute(
								HmDomain.class, "domainName", value[1]);
						if (null == domain) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.virtual.db.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
								.append(value[1]).append("\n");
							continue;
						}
					}
				}
				dto = new HiveApSerialNumber();
				dto.setSerialNumber(value[0]);
				dto.setOwner(domain);

				serialNumbers.add(dto);
			}
		} catch (Exception ex) {
			addActionError(ex.getMessage());
		}
	}
	
	/**
	 * Read the ip subnetwork file and get the data.
	 */
	private void readIpSubNetworks(){
		ipSubNetworks = new ArrayList<DeviceIPSubNetwork>();
		try {
			int intLine = 0;
			DeviceIPSubNetwork dto;
			lineValue:
			for (String[] value : allvalue) {
				intLine++;
				int oldLine = intLine;
				boolean lineBool = false;
				// the value contains line-breaks
				for (String lineValue : value) {
					if (lineValue.contains("\r\n")) {
						intLine = intLine + lineValue.split("\r\n").length - 1;
						lineBool = true;
					}
				}
				if (lineBool) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(oldLine).append(" - ").append(intLine).append(" ").append(
							MgrUtil.getUserMessage("hm.system.log.import.csv.field.value.linebreaks.check").toLowerCase())
						.append("\n");
					continue;
				}
				HmDomain domain = thisDomain;
				if (!checkTheLineValue(value, intLine)) {
					continue;
				}
				// there are one or two fields
				if (getShowDomain()
						&& thisDomain != null
						&& HmDomain.HOME_DOMAIN.equals(thisDomain
								.getDomainName())) {
					if (value.length > 2 || value.length < 1) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
								MgrUtil.getUserMessage("hm.system.log.import.csv.values.contain.two.fields"))
							.append("\n");
						continue;
					}
				} else {
					if (value.length != 1) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
								MgrUtil.getUserMessage("hm.system.log.import.csv.values.contain.one.field"))
							.append("\n");
						continue;
					}
				}
				/*
				 * Check Serial Number
				 */
				String[] entry = value[0].split("/");
				boolean onlyIpAddress = false;
				
				if (entry.length == 1) {
					if (getIpAddressWrongFlag(entry[0])) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ip.address.check"))
							.append(value[0]).append("\n");
						continue;
					} else {
						onlyIpAddress = true;
					}
				}
				
				if (!onlyIpAddress) {
					if (entry.length < 1 || entry.length > 2 || getIpAddressWrongFlag(entry[0])) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ip.entry.netmask.format.check"))
							.append(value[0]).append("\n");
						continue;
					}
					
					//check mask
					try {
						int maskValue = Integer.parseInt(entry[1]);
						if (maskValue >= 32 || maskValue <= 0) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ip.entry.netmask.format.check"))
								.append(value[0]).append("\n");
							continue;
						}
					} catch (Exception e1) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ip.entry.netmask.format.check"))
							.append(value[0]).append("\n");
						continue;
					}
				}
					
				if (QueryUtil.findBoByAttribute(DeviceIPSubNetwork.class, "lower(ipSubNetwork)", value[0].toLowerCase(), getDomainId()) != null) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ip.subnet.exist.db"))
						.append(value[0]).append("\n");
					continue;
				}
				// it cannot exist in this file
				for (DeviceIPSubNetwork ipAddress : ipSubNetworks) {
					if (value[0].equalsIgnoreCase(ipAddress.getIpSubNetwork())) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ip.subnet.exist.file"))
							.append(value[0]).append("\n");
						continue lineValue;
					}
				}
				
				/*
				 * Check Virtual HM Name
				 */
				if (value.length == 2) {
					if (value[1].length() > 32) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.virtual.length.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
							.append(value[1]).append("\n");
						continue;
					} else {
						domain = QueryUtil.findBoByAttribute(
								HmDomain.class, "domainName", value[1]);
						if (null == domain) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.virtual.db.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
								.append(value[1]).append("\n");
							continue;
						}
					}
				}
				dto = new DeviceIPSubNetwork();
				dto.setIpSubNetwork(value[0]);
				dto.setOwner(domain);

				ipSubNetworks.add(dto);
			}
		} catch (Exception ex) {
			addActionError(ex.getMessage());
		}
	}

	/**
	 * Read the ip address or host name file and check the data.
	 */
	private void readAndCheckIpAddressOrHostName() {
		ipObjectMap = new HashMap<String, List<IpAddress>>();
		try {
			int intLine = 0;
			IpAddress dto;
			lineValue:
			for (String[] value : allvalue) {
				intLine++;
				int oldLine = intLine;
				boolean lineBool = false;
				// the value contains line-breaks
				for (String lineValue : value) {
					if (lineValue.contains("\r\n")) {
						intLine = intLine + lineValue.split("\r\n").length - 1;
						lineBool = true;
					}
				}
				if (lineBool) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(oldLine).append(" - ").append(intLine).append(" ").append(
							MgrUtil.getUserMessage("hm.system.log.import.csv.field.value.linebreaks.check").toLowerCase())
						.append("\n");
					continue;
				}
				if (!checkTheLineValue(value, intLine)) {
					continue;
				}
				HmDomain domain = thisDomain;
				boolean isDomain = false;
				boolean ifValidLength;
				if (getShowDomain()
						&& thisDomain != null
						&& HmDomain.HOME_DOMAIN.equals(thisDomain
								.getDomainName())) {
					ifValidLength = value.length > 4
						&& (value.length%4 == 1 || value.length%4 == 2 || value.length%4 == 3);
					isDomain = true;
				} else {
					ifValidLength = value.length > 3
						&& (value.length%4 == 1 || value.length%4 == 2 || value.length%4 == 0);
				}
				if (!ifValidLength) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
							MgrUtil.getUserMessage("hm.system.log.import.csv.fields.num.check"))
						.append(value.length).append("\n");
					continue;
				}

				if (isDomain) {
					if (value[2].length() > 32) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.virtual.length.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
							.append(value[2]).append("\n");
						continue;
					} else if (!"".equals(value[2])) {
						domain = QueryUtil.findBoByAttribute(
								HmDomain.class, "domainName", value[2]);
						if (null == domain) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.virtual.db.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
								.append(value[2]).append("\n");
							continue;
						}
					}
				}
				// get the domain id
				String key = String.valueOf(domain.getId());

				/*
				 * check object name
				 */
				if (value[0].length() < 1 || value[0].length() > 32) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.object.name.length.check"))
						.append(value[0]).append("\n");
					continue;
				} else {
					String strResult = checkTheSpecialCharacter(value[0], SINGLE_STRING_LIMIT);
					if (!"".equals(strResult)) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.object.name.not.contain"))
						.append(" '").append(strResult).append("' :: ").append(value[0]).append("\n");
						continue;
					} else if (QueryUtil.findBoByAttribute(IpAddress.class,
						"addressName", value[0], domain.getId()) != null) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.object.name.exist.db.check",value[0]))
							.append("\n");
						continue;
					}
					// check if the name exist in this file
					if (null != ipObjectMap.get(key)) {
						for (IpAddress oneIp : ipObjectMap.get(key)) {
							if (value[0].equals(oneIp.getAddressName())) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.object.name.repeat.check",value[0])).append("\n");
								continue lineValue;
							}
						}
					}
				}
				/*
				 *  get the category of this object
				 */
				short typeFlag;
				if (!"1".equals(value[1]) && !"2".equals(value[1]) && !"3".equals(value[1]) && !"4".equals(value[1])) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.format.category.check"))
						.append(value[1]).append("\n");
					continue;
				} else {
					typeFlag = (short)Integer.parseInt(value[1]);
				}

				/*
				 * get the ip entries
				 */
				List<SingleTableItem> ipInfo = new ArrayList<SingleTableItem>();
				for (int i = isDomain ? 3 : 2; i < value.length-1; i=i+4) {
					// there are four ip entries already
//					if (ipInfo.size() == 4) {
//						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
//							.append(" failed : One IP Object/Host Name at most contains four different type IP entries.")
//							.append("\n");
//						continue lineValue;
//					}
					SingleTableItem singleInfo = new SingleTableItem();
					/*
					 * ip entry format check
					 */
					if ("".equals(value[i])) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ip.entry.empty.check"))
							.append(value[i]).append("\n");
						continue lineValue;
					}
					// it is an ip address
					if (typeFlag == IpAddress.TYPE_IP_ADDRESS) {
						if (getIpAddressWrongFlag(value[i].trim())) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ip.entry.ip.address.check"))
								.append(value[i]).append("\n");
							continue lineValue;
						}
						singleInfo.setIpAddress(value[i].trim());
						singleInfo.setNetmask(IpAddress.NETMASK_OF_SINGLE_IP);
						
					} else if (typeFlag == IpAddress.TYPE_HOST_NAME) {
						// it is a host name
						if (value[i].length() > 64) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ip.entry.length.check"))
								.append(value[i]).append("\n");
							continue lineValue;
						}
						String strResult = checkTheSpecialCharacter(value[i], SINGLE_STRING_LIMIT);
						if (!"".equals(strResult)) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ip.entry.not.contain"))
							.append(" '").append(strResult).append("' :: ").append(value[i]).append("\n");
							continue lineValue;
						}
						singleInfo.setIpAddress(value[i]);
						singleInfo.setNetmask("");
						
					// it is an ip address/netmask
					} else if (typeFlag == IpAddress.TYPE_IP_NETWORK) {
						String[] entry = value[i].split("/");
						if (entry.length != 2 || getIpAddressWrongFlag(entry[0]) || getNetmaskWrongFlag(entry[1])) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ip.entry.netmask.format.check"))
								.append(value[i]).append("\n");
							continue lineValue;
						}
						singleInfo.setIpAddress(entry[0]);
						singleInfo.setNetmask(entry[1]);
						
					// it is an ip address/wildcard
					} else {
						String[] entry = value[i].split("/");
						if (entry.length != 2 || getIpAddressWrongFlag(entry[0]) || getIpAddressWrongFlag(entry[1])) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ip.entry.format.check"))
								.append(value[i]).append("\n");
							continue lineValue;
						}
						singleInfo.setIpAddress(entry[0]);
						singleInfo.setNetmask(entry[1]);
					}
					/*
					 * get the type of ip entry
					 */
					short type;
					if (!value[i+1].equals("1") && !value[i+1].equals("2") && !value[i+1].equals("3") && !value[i+1].equals("4")) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.type.format.check"))
							.append(value[i+1]).append("\n");
						continue lineValue;
					} else {
						type = (short)Integer.parseInt(value[i+1]);
						singleInfo.setType(type);
					}
					
					/*
					 * there are no type value and description
					 */
					if (value.length > i+2) {
						/*
						 * check the type value base on type
						 */
						if (type == SingleTableItem.TYPE_GLOBAL) {
							if (!"".equals(value[i+2])) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
									.append(MgrUtil.getUserMessage("hm.system.log.import.csv.global.value.empty.check"))
									.append(value[i+2]).append("\n");
								continue lineValue;
							}
						} else if ("".equals(value[i+2])) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.type.value.empty.check"))
								.append(value[i+2]).append("\n");
							continue lineValue;
						}
						switch (type) {
						case SingleTableItem.TYPE_MAP:
							/*
							 * check the map name
							 */
							if (MapMgmt.ROOT_MAP_NAME.equals(value[i+2]) || MapMgmt.VHM_ROOT_MAP_NAME.equals(value[i+2])) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
									.append(MgrUtil.getUserMessage("hm.system.log.import.csv.topology.name.check"))
									.append(value[i+2]).append("\n");
								continue lineValue;
							}

							MapContainerNode topology = QueryUtil.findBoByAttribute(MapContainerNode.class,
									"mapName", value[i+2], domain.getId());
							if (null == topology) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
									.append(MgrUtil.getUserMessage("hm.system.log.import.csv.toplogy.map.db.exist.check"))
									.append(value[i+2]).append("\n");
								continue lineValue;
							}
							if (MapContainerNode.MAP_TYPE_BUILDING == topology.getMapType()) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
									.append(MgrUtil.getUserMessage("hm.system.log.import.csv.topology.type.check"))
									.append(value[i+2]).append("\n");
								continue lineValue;
							}
							singleInfo.setLocation(topology);
							break;
						case SingleTableItem.TYPE_HIVEAPNAME:
							/*
							 * check the hiveap name
							 */
							if (value[i+2].length() > 32) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.hiveap.name.length.check",NmsUtil.getOEMCustomer().getAccessPonitName()))
									.append(value[i + 2]).append("\n");
								continue lineValue;
							}
							String strResult = checkTheSpecialCharacter(value[i+2], SINGLE_STRING_LIMIT);
							if (!"".equals(strResult)) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.name.not.contain.check",NmsUtil.getOEMCustomer().getAccessPonitName())).append(strResult).append(" :: ").append(value[i + 2]).append("\n");
								continue lineValue;
							}
							singleInfo.setTypeName(value[i+2]);
							break;
						case SingleTableItem.TYPE_CLASSIFIER:
							//fix bug 27474
							i += 2;
							if(i + 2> value.length -1 ){
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
										MgrUtil.getUserMessage("hm.system.log.import.csv.fields.num.check"))
									.append(value.length).append("\n");
								continue lineValue;
							}
							
							if(value[i].length() == 0 && value[i+1].length() == 0 && value[i+2].length() == 0){
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.classifier.value.length.check"))
								.append(value[i]).append("\n");
								
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.classifier.value.length.check"))
								.append(value[i + 1]).append("\n");
								
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.classifier.value.length.check"))
								.append(value[i + 2]).append("\n");
								continue lineValue;
							}
							
							// check the tag1 of classifier type
							if (value[i].length() > 64) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
									.append(MgrUtil.getUserMessage("hm.system.log.import.csv.classifier.value.length.check"))
									.append(value[i]).append("\n");
								continue lineValue;
							}
							singleInfo.setTag1(value[i]);
							singleInfo.setTag1Checked((value[i] != null && !value[i].isEmpty()) ? true : false);
							
							// check the tag2 of classifier type
							if (value[i + 1].length() > 64) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
									.append(MgrUtil.getUserMessage("hm.system.log.import.csv.classifier.value.length.check"))
									.append(value[i + 1]).append("\n");
								continue lineValue;
							}
							singleInfo.setTag2(value[i + 1]);
							singleInfo.setTag2Checked((value[i + 1] != null && !value[i + 1].isEmpty()) ? true : false);
							
							// check the tag3 of classifier type
							if (value[i + 2].length() > 64) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
									.append(MgrUtil.getUserMessage("hm.system.log.import.csv.classifier.value.length.check"))
									.append(value[i + 2]).append("\n");
								continue lineValue;
							}
							singleInfo.setTag3(value[i + 2]);
							singleInfo.setTag3Checked((value[i + 2] != null && !value[i + 2].isEmpty()) ? true : false);
							break;
						default:
							break;
						}
						if (value.length > i+3) {
							// get the description of ip entry
							if (!value[i+3].equals("")) {
								if (value[i+3].length() > 64) {
									result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
										.append(MgrUtil.getUserMessage("hm.system.log.import.csv.entry.desc.length.check"))
										.append(value[i+3]).append("\n");
									continue lineValue;
								} else {
									singleInfo.setDescription(value[i+3]);
								}
							}
						}
					}
					// check the type in all ip entries
					for (SingleTableItem item : ipInfo) {
						boolean typeValueRrpeat = false;
						switch (type) {
							case SingleTableItem.TYPE_MAP:
								if (item.getType() == SingleTableItem.TYPE_MAP) {
									if (item.getLocation().equals(singleInfo.getLocation())) {
										typeValueRrpeat = true;
									}
								}
								break;
							case SingleTableItem.TYPE_HIVEAPNAME:
								if (item.getType() == SingleTableItem.TYPE_HIVEAPNAME) {
									if (item.getTypeName().equals(singleInfo.getTypeName())) {
										typeValueRrpeat = true;
									}
								}
								break;
							case SingleTableItem.TYPE_GLOBAL:
								if (item.getType() == SingleTableItem.TYPE_GLOBAL) {
									typeValueRrpeat = true;
								}
								break;
							case SingleTableItem.TYPE_CLASSIFIER:
								if (item.getType() == SingleTableItem.TYPE_CLASSIFIER) {
									if (item.getTag1().equals(singleInfo.getTag1())
											&& item.getTag2().equals(singleInfo.getTag2())
											&& item.getTag3().equals(singleInfo.getTag3())) {
										typeValueRrpeat = true;
									}
								}
								break;
							default:
								break;
						}
						if (typeValueRrpeat) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ip.host.name.type.value.repeat"))
								.append(value[i+1]).append("\n");
							continue lineValue;
						}
					}
					if (singleInfo.getType() == SingleTableItem.TYPE_GLOBAL) {
						ipInfo.add(0, singleInfo);
					} else {
						ipInfo.add(singleInfo);
					}
				}
				// there is no ip entry
				if (ipInfo.isEmpty()) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ip.host.name.contain.global.ip"))
						.append("\n");
					continue lineValue;
				}
				boolean isContainGlobal = false;
				for (SingleTableItem item : ipInfo) {
					if (item.getType() == SingleTableItem.TYPE_GLOBAL) {
						isContainGlobal = true;
						break;
					}
				}
				// there is no global ip entry
				if (!isContainGlobal) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.global.ip.check"))
						.append("\n");
					continue lineValue;
				}

				dto = new IpAddress();
				dto.setAddressName(value[0]);
				dto.setTypeFlag(typeFlag);
				dto.setOwner(domain);
				
				//fix bug 27469
				Collections.sort(ipInfo, new DefaultTagOrderComparator());
				
				dto.setItems(ipInfo);

				if (null == ipObjectMap.get(key)) {
					List<IpAddress> vct_ip = new ArrayList<IpAddress>();
					vct_ip.add(dto);
					ipObjectMap.put(key, vct_ip);
				} else {
					ipObjectMap.get(key).add(dto);
				}
			}
		} catch (Exception ex) {
			addActionError(ex.getMessage());
		}
	}

	/**
	 * Read the aaa radius setting file and check the data.
	 */
	private void readAndCheckAaaRadiusSetting() {
		aaaRadiusMap = new HashMap<String, List<RadiusAssignment>>();
		ipObjectMap = new HashMap<String, List<IpAddress>>();
		try {
			int intLine = 0;
			RadiusAssignment dto;
			lineValue:
			for (String[] value : allvalue) {
				intLine++;
				int oldLine = intLine;
				boolean lineBool = false;
				// the value contains line-breaks
				for (String lineValue : value) {
					if (lineValue.contains("\r\n")) {
						intLine = intLine + lineValue.split("\r\n").length - 1;
						lineBool = true;
					}
				}
				if (lineBool) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(oldLine).append(" - ").append(intLine).append(" ").append(
							MgrUtil.getUserMessage("hm.system.log.import.csv.field.value.linebreaks.check").toLowerCase())
						.append("\n");
					continue;
				}
				if (!checkTheLineValue(value, intLine)) {
					continue;
				}
				HmDomain domain = thisDomain;
				boolean isDomain = false;
				if (value.length < 4) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
							MgrUtil.getUserMessage("hm.system.log.import.csv.values.contain.fields"))
						.append("\n");
					continue;
				}
				boolean ifValidLength;
				if (getShowDomain()
						&& thisDomain != null
						&& HmDomain.HOME_DOMAIN.equals(thisDomain
								.getDomainName())) {
					ifValidLength = value.length == 4 || value.length%5 == 1;
					isDomain = true;
				} else {
					ifValidLength = value.length == 4 || value.length%5 == 0;
				}
				if (!ifValidLength) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
							MgrUtil.getUserMessage("hm.system.log.import.csv.fields.num.check"))
						.append(value.length).append("\n");
					continue;
				}

				/*
				 * check vhm name
				 */
				if (isDomain) {
					if (value.length > 5) {
						if (value[5].length() > 32) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.virtual.length.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
								.append(value[5]).append("\n");
							continue;
						} else if (!"".equals(value[5])) {
							domain = QueryUtil.findBoByAttribute(
									HmDomain.class, "domainName", value[5]);
							if (null == domain) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.virtual.db.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
									.append(value[5]).append("\n");
								continue;
							}
						}
					}
				}
				// get the domain id
				String key = String.valueOf(domain.getId());

				/*
				 * check radius name
				 */
				if (value[0].length() < 1 || value[0].length() > 32) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.radius.name.length.check"))
						.append(value[0]).append("\n");
					continue;
				} else {
					String strResult = checkTheSpecialCharacter(value[0], SINGLE_STRING_LIMIT);
					if (!"".equals(strResult)) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.radius.name.not.contain"))
						.append(" '").append(strResult).append("' :: ").append(value[0]).append("\n");
						continue;
					} else if (QueryUtil.findBoByAttribute(RadiusAssignment.class,
						"radiusName", value[0], domain.getId()) != null) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					    .append(MgrUtil.getUserMessage("hm.system.log.import.csv.radius.name.exist.db",value[0]))
						.append("\n");
						continue;
					}
					// check if the name exist in this file
					if (null != aaaRadiusMap.get(key)) {
						for (RadiusAssignment oneRadius : aaaRadiusMap.get(key)) {
							if (value[0].equals(oneRadius.getRadiusName())) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.radius.name.exist.file",value[0])).append("\n");
								continue lineValue;
							}
						}
					}
				}
				// check Retry Interval
				if (!checkValue(value[1], 60, 100000000)) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
							MgrUtil.getUserMessage("hm.system.log.import.csv.retry.interval"))
						.append(value[1]).append("\n");
					continue;
				}
				// check Accounting Interim Update Interval
				if (!checkValue(value[2], 10, 100000000)) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
							MgrUtil.getUserMessage("hm.system.log.import.csv.account.interim.update.interval"))
						.append(value[2]).append("\n");
					continue;
				}
				/*
				 *  get Permit Dynamic Change of Authorization Messages
				 */
				boolean enableExt;
				if (!"true".equals(value[3]) && !"false".equals(value[3])) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.permit.format.check"))
						.append(value[3]).append("\n");
					continue;
				} else {
					enableExt = "true".equals(value[3]);
				}
				/*
				 * check description
				 */
				if (value.length > 4) {
					if (value[4].length() > 64) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.descri.length.check"))
							.append(value[4]).append("\n");
						continue;
					}
				}

				/*
				 * get the radius servers
				 */
				List<RadiusServer> servers = new ArrayList<RadiusServer>();
				for (int i = isDomain ? 6 : 5; i < value.length-4; i=i+5) {
					// there are four radius servers already
//					if (servers.size() == 4) {
//						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
//							.append(" failed : One AAA Client Setting at most contains four different priority RADIUS servers")
//							.append("\n");
//						continue lineValue;
//					}
					RadiusServer singleInfo = new RadiusServer();
					/*
					 * check server ip or name
					 */
					if (value[i].length() < 1 || value[i].length() > 32) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.server.ip.name.length.check"))
							.append(value[i]).append("\n");
						continue lineValue;
					}
					
					/*
					 * check shared secret
					 */
					if (value[i+1].length() > 32) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.shared.secret.length.check"))
							.append(value[i+1]).append("\n");
						continue lineValue;
					} else if (value[i+1].length() > 1) {
						String strResult = checkTheSpecialCharacter(value[i+1], SINGLE_STRING_LIMIT);
						if (!"".equals(strResult)) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.shared.secret.check"))
							.append(" '").append(strResult).append("' :: ").append(value[i + 1]).append("\n");
							continue lineValue;
						}
						singleInfo.setSharedSecret(value[i+1]);
					}
					
					/*
					 * get the priority of radius server
					 */
					short priority;
					if (!value[i+2].equals("1") && !value[i+2].equals("2") && !value[i+2].equals("3") && !value[i+2].equals("4")) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.server.role.check"))
							.append(value[i+2]).append("\n");
						continue lineValue;
					} else {
						priority = (short)Integer.parseInt(value[i+2]);
						singleInfo.setServerPriority(priority);
					}

					// check Authentication Port
					if (!checkValue(value[i+3], 0, 65535)) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.auth.port.length.check"))
							.append(value[i+3]).append("\n");
						continue lineValue;
					}
					int authPort = Integer.parseInt(value[i+3]);
					if (authPort > 0) {
						singleInfo.setAuthPort(authPort);
					}
					
					// check Accounting Port
					if (!checkValue(value[i+4], 0, 65535)) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.account.port.length.check"))
							.append(value[i+4]).append("\n");
						continue lineValue;
					}
					int acctPort = Integer.parseInt(value[i+4]);
					if (acctPort > 0) {
						singleInfo.setAcctPort(acctPort);
						if (authPort > 0) {
							singleInfo.setServerType(RadiusServer.RADIUS_SERVER_TYPE_BOTH);
						} else {
							singleInfo.setServerType(RadiusServer.RADIUS_SERVER_TYPE_ACCT);
						}
					} else {
						if (authPort > 0) {
							singleInfo.setServerType(RadiusServer.RADIUS_SERVER_TYPE_AUTH);
						} else {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.account.port.use.check"))
							.append(value[i]).append("\n");
							continue lineValue;
						}
					}

					// check the priority in all radius servers
					for (RadiusServer server : servers) {
						if (value[i].equals(server.getIpAddress().getAddressName())) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.aaa.server.ip.repeat.check"))
								.append(value[i]).append("\n");
							continue lineValue;
						}
						if (RadiusServer.RADIUS_SERVER_TYPE_BOTH == singleInfo.getServerType() || (singleInfo.getServerType() == server.getServerType()
							|| RadiusServer.RADIUS_SERVER_TYPE_BOTH == server.getServerType())) {
							if (server.getServerPriority() == priority) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
									.append(MgrUtil.getUserMessage("hm.system.log.import.csv.aaa.client.repeat.check"))
									.append(value[i+2]).append("\n");
								continue lineValue;
							}
						}
					}
					IpAddress ipAddress = QueryUtil.findBoByAttribute(IpAddress.class,
						"addressName", value[i], domain.getId());
					if (null == ipAddress) {
						short ipType = IpAddress.TYPE_IP_ADDRESS;
						if (getIpAddressWrongFlag(value[i])) {
							ipType = IpAddress.TYPE_HOST_NAME;
							String strResult = checkTheSpecialCharacter(value[i], SINGLE_STRING_LIMIT);
							if (!"".equals(strResult)) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.host.name.server.ip.contain.check")).append(" ").append(strResult).append(" :: ").append(value[i]).append("\n");
								continue lineValue;
							}
						}
						boolean ipBool = true;
						if (null != ipObjectMap.get(key)) {
							// get the same IP from the map
							for (IpAddress oneIp : ipObjectMap.get(key)) {
								if (value[i].equals(oneIp.getAddressName())) {
									ipAddress = oneIp;
									ipBool = false;
									break;
								}
							}
						}
						if (ipBool) {
							ipAddress = CreateObjectAuto.getNewIpObject(value[i], value[i], null, ipType, domain, MgrUtil.getUserMessage("hm.system.log.import.csv.aaa.client.desc.message") + value[0]);
							if (null == ipObjectMap.get(key)) {
								List<IpAddress> vct_ip = new ArrayList<IpAddress>();
								vct_ip.add(ipAddress);
								ipObjectMap.put(key, vct_ip);
							} else {
								ipObjectMap.get(key).add(ipAddress);
							}
						}
					}
					singleInfo.setIpAddress(ipAddress);
					servers.add(singleInfo);
				}
				
				dto = new RadiusAssignment();
				dto.setRadiusName(value[0]);
				dto.setRetryInterval(Integer.parseInt(value[1]));
				dto.setUpdateInterval(Integer.parseInt(value[2]));
				dto.setEnableExtensionRadius(enableExt);
				dto.setDescription(value.length > 4 ? value[4] : "");
				dto.setOwner(domain);
				dto.setServices(!servers.isEmpty() ? servers : null);

				if (null == aaaRadiusMap.get(key)) {
					List<RadiusAssignment> listRadius = new ArrayList<RadiusAssignment>();
					listRadius.add(dto);
					aaaRadiusMap.put(key, listRadius);
				} else {
					aaaRadiusMap.get(key).add(dto);
				}
			}
		} catch (Exception ex) {
			addActionError(ex.getMessage());
		}
	}

	/**
	 * Save the local user file data in database.
	 * @return int : the total number of create successfully
	 */
	private int createAAALocalUser() {
		int successNumber = 0;
		try {
			List<LocalUser> m_vct_aaa_user;
			for (String key : localUserMap.keySet()) {
				m_vct_aaa_user = localUserMap.get(key);
				if (null != m_vct_aaa_user) {
					/*
					 * get the local users together which belong to the same user group
					 */
					Map<String, List<LocalUser>> singleMap = new HashMap<String, List<LocalUser>>();
					for (LocalUser dtoUser : m_vct_aaa_user) {
						String groupKey = dtoUser.getUserGroupName();
						if (null == singleMap.get(groupKey)) {
							List<LocalUser> vector_single = new ArrayList<LocalUser>();
							vector_single.add(dtoUser);
							singleMap.put(groupKey, vector_single);
						} else {
							singleMap.get(groupKey).add(dtoUser);
						}
					}

					/*
					 * the local user count limit is 1000 for one user group
					 */
					List<LocalUser> userList;
					for (String userkey : singleMap.keySet()) {
						userList = singleMap.get(userkey);
						long count = QueryUtil.findRowCount(LocalUser.class,
								new FilterParams("localUserGroup", userList.get(0).getLocalUserGroup()));
						LocalUser lastUser = null;
						for (int i = 0; i < userList.size(); i++) {
							LocalUser oneUser = userList.get(i);
							if (count + i < LocalUser.MAX_COUNT_AP30_LOCALUSER) {
								QueryUtil.createBo(oneUser);
								successNumber++;
								lastUser = oneUser;
							} else {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.import.file.message",oneUser.getId().toString())).append(" ").append(groupTitle).append(" (")
									.append(oneUser.getUserGroupName())
									.append(").\n");
							}
						}
						// For configuration indication, specially for LocalUser
						if(null != lastUser){
							HmBeEventUtil.eventGenerated(new ConfigurationChangedEvent(
								lastUser, ConfigurationChangedEvent.Operation.CREATE, null));
						}
					}
				}
			}
		} catch (Exception ex) {
			addActionError(ex.getMessage());
		}
		return successNumber;
	}

	/**
	 * Save the local user group file data in database.
	 * @return int : the total number of create successfully
	 */
	private int createAAAUserGroup() {
		int successNumber = 0;
		try {
			List<LocalUserGroup> m_vct_aaa;
			for (String key : userGroupMap.keySet()) {
				m_vct_aaa = userGroupMap.get(key);
				if (null != m_vct_aaa) {
					QueryUtil.bulkCreateBos(m_vct_aaa);
					successNumber += m_vct_aaa.size();
				}
			}
		} catch (Exception ex) {
			addActionError(ex.getMessage());
		}
		return successNumber;
	}

	/**
	 * Save the mac filter file data in database.
	 * @return int : the total number of create successfully
	 */
	private int createMacFilter() {
		int successNumber = 0;
		try {
			List<MacOrOui> m_vct_mac;
			// insert all the new mac address or oui
			for (String key : macAddreOrOuiMap.keySet()) {
				m_vct_mac = macAddreOrOuiMap.get(key);
				if (null != m_vct_mac) {
					QueryUtil.bulkCreateBos(m_vct_mac);
				}
			}
			List<MacFilter> m_vct_mac_filter;
			// insert all the mac filter
			for (String key : macFilterMap.keySet()) {
				m_vct_mac_filter = macFilterMap.get(key);
				if (null != m_vct_mac_filter) {
					QueryUtil.bulkCreateBos(m_vct_mac_filter);
					successNumber += m_vct_mac_filter.size();
				}
			}
			// merge mac filter
			if(!mergeMacFilterList.isEmpty()){
				QueryUtil.bulkUpdateBos(mergeMacFilterList);
				successNumber += mergeMacFilterList.size();
			}
			generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.system.log.import.csv.upload.csvfile.message",uploadFileName));
		} catch (Exception ex) {
			addActionError(ex.getMessage());
			generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.system.log.import.csv.upload.csvfile.message",uploadFileName));
		}
		return successNumber;
	}
	
	/**
	 * Check the serial number file data and save them in database.
	 * @return int : the total number of create successfully
	 */
	private int updateSerialNumbers(){
		int successNumber = 0;
		try {
			QueryUtil.bulkCreateBos(serialNumbers);
			successNumber = serialNumbers.size();
		} catch (Exception ex) {
			addActionError(ex.getMessage());
		}
		return successNumber;
	}
	
	/**
	 * Check the IP Subnetwork file data and save them in database.
	 * @return int : the total number of create successfully
	 */
	private int updateIPSubnetworks(){
		int successNumber = 0;
		try {
			QueryUtil.bulkCreateBos(ipSubNetworks);
			successNumber = ipSubNetworks.size();
		} catch (Exception ex) {
			addActionError(ex.getMessage());
		}
		return successNumber;
	}

	/**
	 * Save the IpAddressOrHostName file data in database.
	 * @return int : the total number of create successfully
	 */
	private int createIpAddressOrHostName() {
		int successNumber = 0;
		try {
			List<IpAddress> m_vct_ip;
			for (String key : ipObjectMap.keySet()) {
				m_vct_ip = ipObjectMap.get(key);
				if (null != m_vct_ip) {
					QueryUtil.bulkCreateBos(m_vct_ip);
					successNumber += m_vct_ip.size();
				}
			}
		} catch (Exception ex) {
			addActionError(ex.getMessage());
		}
		return successNumber;
	}

	/**
	 * Save the aaa radius setting data in database.
	 * @return int : the total number of create successfully
	 */
	private int createAaaRadiusSetting() {
		int successNumber = 0;
		try {
			List<IpAddress> m_vct_ip;
			for (String key : ipObjectMap.keySet()) {
				m_vct_ip = ipObjectMap.get(key);
				if (null != m_vct_ip) {
					QueryUtil.bulkCreateBos(m_vct_ip);
				}
			}
			List<RadiusAssignment> listRadius;
			for (String key : aaaRadiusMap.keySet()) {
				listRadius = aaaRadiusMap.get(key);
				if (null != listRadius) {
					QueryUtil.bulkCreateBos(listRadius);
					successNumber += listRadius.size();
				}
			}
		} catch (Exception ex) {
			addActionError(ex.getMessage());
		}
		return successNumber;
	}

	/**
	 * Read the HiveAP file and check the data for new HiveAPs.
	 */
	private void readAndCheckHiveAPNewInfo() {
		newHiveApMap = new HashMap<Long, List<HiveAp>>();
		HiveAp singleAP;
		int intLine = 0;
		List<String> names = new ArrayList<String>();
		List<String> nodeIds = new ArrayList<String>();
		try {
			for (String[] colValue : allvalue)  {
				intLine++;
				int oldLine = intLine;
				boolean lineBool = false;
				// the value contains line-breaks
				for (String lineValue : colValue) {
					if (lineValue.contains("\r\n")) {
						intLine = intLine + lineValue.split("\r\n").length - 1;
						lineBool = true;
					}
				}
				if (lineBool) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(oldLine).append(" - ").append(intLine)
					.append(" ").append(MgrUtil.getUserMessage("hm.system.log.import.csv.field.value.linebreaks.check").toLowerCase())
						.append("\n");
					continue;
				}
				if (!checkTheLineValue(colValue, intLine)) {
					continue;
				}

				HmDomain domain = thisDomain;
				boolean isAllDomain = false;
				int maxLength=12;
				if (getShowDomain()
						&& thisDomain != null
						&& HmDomain.HOME_DOMAIN.equals(thisDomain
								.getDomainName())) {
					if (colValue.length != 12) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.node.id.value.contain.check","12"))
							.append("\n");
						continue;
					}
					isAllDomain = true;
				} else {
					maxLength = isFullMode() ? 12 : 11;
					if (colValue.length != maxLength) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.node.id.value.contain.check",String.valueOf(maxLength)))
							.append("\n");
						continue;
					}
				}
				// serial number
				if (colValue[0].length() != 14 && colValue[0].length() != 0) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("glasgow_05.hm.system.log.import.csv.serialNum.length.check"))
						.append(colValue[0]).append("\n");
					continue;
				}
				if (colValue[0].length() != 0 && !isNumber(colValue[0])) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("glasgow_05.hm.system.log.import.csv.serialNum.valid.check")).append(
						colValue[0]).append("\n");
					continue;
				}

				//mac address
				if (colValue[1].length() != 12) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.node.id.length.check"))
						.append(colValue[1]).append("\n");
					continue;
				}
				if (!isHex(colValue[1])) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.node.id.valid.check")).append(
						colValue[1]).append("\n");
					continue;
				}

				if (colValue.length == maxLength) {
					if (!colValue[maxLength-1].equals("")) {
						if (colValue[maxLength-1].length() > 32) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.virtual.length.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
								.append(colValue[maxLength-1]).append("\n");
							continue;
						} else {
							if (!isAllDomain && !thisDomain.getDomainName().equals(colValue[maxLength-1])){
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("glasgow_05_hm.system.log.import.csv.virtual.db.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
									.append(colValue[maxLength-1]).append("\n");
								continue;
							}
							
							domain = QueryUtil.findBoByAttribute(
									HmDomain.class, "domainName", colValue[maxLength-1]
											);
							if (null == domain) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.virtual.db.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
									.append(colValue[maxLength-1]).append("\n");
								continue;
							}
							if (!StartHereAction.isStartHereConfigured(domain.getId())) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.virtual.initial.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
									.append(colValue[maxLength-1]).append("\n");
								continue;
							}
						}
					} else {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("glasgow_05_hm.system.log.import.csv.virtual.length.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
							.append(colValue[maxLength-1]).append("\n");
						continue;
					}
				}

				// mac address of hiveap
				singleAP = QueryUtil.findBoByAttribute(HiveAp.class, "macAddress", colValue[1].toUpperCase());

				// create new hiveap
				if (null == singleAP) {
					singleAP = new HiveAp();
					singleAP.setManageStatus(HiveAp.STATUS_PRECONFIG);
					// for the error line number
					singleAP.setId(Long.valueOf(intLine));
					singleAP.setMacAddress(colValue[1].toUpperCase());
					singleAP.setSerialNumber(colValue[0]);
				// update the exist hiveap
				} else {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.node.id.exist.v.check",colValue[1])).append("\n");
					continue;
				}
				
				// default host name
				String hostName = "AH-" + colValue[1].substring(6).toLowerCase();
				if (colValue[2].length() > 32) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.host.name.length.check"))
						.append(colValue[2]).append("\n");
					continue;
				} else if (colValue[2].length() > 0) {
					String strResult = checkTheSpecialCharacter(colValue[2], SINGLE_STRING_LIMIT);
					if (!"".equals(strResult)) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.host.name.contain.check"))
						.append(" '").append(strResult).append("' :: ").append(colValue[2]).append("\n");
						continue;
					}
					hostName = colValue[2];
				}

				if (HiveApAction.isHiveApHostNameExist(domain.getId(),
						hostName, null)) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.host.name.db.check",hostName))
					.append("\n");
					continue;
				}
				singleAP.setHostName(hostName);
		
				// ap mode
				if (colValue[3].length() > 0) {
					short apModel;
					boolean isMatched = false;
					if (colValue[3] != null) {
						try {
							Device hiveap= Device.valueOf(colValue[3].toUpperCase().replace('-', '_'));
							List<Device> deviceObjects = AhConstantUtil.getDeviceObjects(Device.ALL);
							if (deviceObjects != null && deviceObjects.contains(hiveap)) {
								apModel = AhConstantUtil.getModelByDevice(hiveap);
								Boolean isTrue = AhConstantUtil.isTrueAll(Device.SUPPORTED_IMPORT_CSV, apModel);
								if (isTrue == null ? false : isTrue) {
									singleAP.setHiveApModel(apModel);
									singleAP.setDeviceType(AhConstantUtil.getDeviceTypeByDevice(hiveap));
									isMatched = true;
								}
							}
						} catch (Exception e) {
							log.error("readAndCheckHiveAPNewInfo", "Reading/Checking AP information error.", e);
						}
					}

					if (!isMatched) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ap.model.check")).append(
							colValue[3]).append("\n");
						continue;
					} 
				} else {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ap.model.check")).append(
							colValue[3]).append("\n");
					continue;
				}
				
				// Device function
				if (!colValue[4].equalsIgnoreCase("AP") && !colValue[4].equalsIgnoreCase("Router") 
						&& !colValue[4].equalsIgnoreCase("Switch")) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("glasgow_05.hm.system.log.import.csv.ap.function.check"))
						.append(colValue[4]).append("\n");
					continue;
				}
				
				if (isEasyMode() && !colValue[3].toUpperCase().startsWith("AP")) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ap.model.check"))
						.append(colValue[3]).append("\n");
					continue;
				}
				if (isEasyMode() && !colValue[4].equalsIgnoreCase("AP")) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("glasgow_05.hm.system.log.import.csv.ap.function.check"))
						.append(colValue[4]).append("\n");
					continue;
				}
				if (colValue[4].equalsIgnoreCase("AP")) {
					singleAP.setDeviceType(HiveAp.Device_TYPE_HIVEAP);
				} else if (colValue[4].equalsIgnoreCase("Router")) {
					singleAP.setDeviceType(HiveAp.Device_TYPE_BRANCH_ROUTER);
				} else if (colValue[4].equalsIgnoreCase("L2 VPN Gateway")) {
					singleAP.setDeviceType(HiveAp.Device_TYPE_HIVEAP);
				} else if (colValue[4].equalsIgnoreCase("L3 VPN Gateway")) {
					singleAP.setDeviceType(HiveAp.Device_TYPE_VPN_GATEWAY);
				} else if (colValue[4].equalsIgnoreCase("Switch")) {
					singleAP.setDeviceType(HiveAp.Device_TYPE_SWITCH);
				}
				
				if (!AhConstantUtil.getHiveApModelSupportType(singleAP.getHiveApModel(), singleAP.getDeviceType())) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("glasgow_05.hm.system.log.import.csv.ap.function.notmatch"))
						.append(colValue[4]).append("\n");
					continue;
				}

				
				// wlan policy
				// full mode, wlan policy can be selected
				int k = 5;
				if (isFullMode() || isAllDomain) {
					boolean easyMode = false;
					if (isAllDomain) {
						List<HmStartConfig> hmMode = QueryUtil.executeQuery(
							HmStartConfig.class, null, null, domain.getId());
						if (!hmMode.isEmpty()) {
							easyMode = HmStartConfig.HM_MODE_EASY == hmMode.get(0).getModeType();
						}
					}
					if (easyMode) {
						singleAP.setConfigTemplate(HmBeParaUtil.getEasyModeDefaultTemplate(domain.getId()));
					} else {
						if (colValue[k].length() > 0) {
							ConfigTemplate config = QueryUtil
								.findBoByAttribute(ConfigTemplate.class, "configName",
										colValue[k], domain.getId());
							if (null == config) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.network.policy.check")).append(
									colValue[k]).append("\n");
								continue;
							}
							singleAP.setConfigTemplate(config);
						} else {
//							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
//							.append(MgrUtil.getUserMessage("glasgow_05.hm.system.log.import.csv.network.policy.length.check")).append(
//								colValue[k]).append("\n");
//							continue;
							singleAP.setConfigTemplate(HmBeParaUtil.getDefaultTemplate());
						}

					}
					k++;
				// express mode use the default wlan policy
				} else {
					singleAP.setConfigTemplate(HmBeParaUtil.getEasyModeDefaultTemplate(domain.getId()));
				}
				
				// set version information
				String softVer = NmsUtil.getHiveOSVersion(versionInfo);
				singleAP.setSoftVer(softVer);
				singleAP.setDisplayVer(getText("monitor.hiveAp.DisplayVer", 
						new String[]{singleAP.getSoftVerString()}));
				singleAP.setPriority(HiveAp.getDefaultBonjourPriority(singleAP.getHiveApModel()));
			
				if (!colValue[k].equals("")) {
					if (colValue[k].length() > 32) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.location.length.check"))
							.append(colValue[k]).append("\n");
						continue;
					}
					String strResultLocation = checkTheSpecialCharacter(colValue[k], LOCATION_STRING_LIMIT);
					if (!"".equals(strResultLocation)) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.location.contain.check"))
						.append(" '").append(strResultLocation).append("' :: ").append(colValue[k]).append("\n");
						continue;
					}
					singleAP.setLocation(colValue[k]);
				}

				k++;
				
				if (!colValue[k].equals("")) {
					if (getIpAddressWrongFlag(colValue[k])) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ip.valid.check"))
							.append(colValue[k]).append("\n");
						continue;
					}
					singleAP.setCfgIpAddress(colValue[k]);
					singleAP.setIpAddress(colValue[k]);

					if (!colValue[k+1].equals("")) {
						if (getNetmaskWrongFlag(colValue[k+1])) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.subnet.mask.check"))
								.append(colValue[k+1]).append("\n");
							continue;
						}
						singleAP.setCfgNetmask(colValue[k+1]);
						singleAP.setNetmask(colValue[k+1]);

						if (!colValue[k+2].equals("")) {
							if (getIpAddressWrongFlag(colValue[k+2]
									)) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
									.append(MgrUtil.getUserMessage("hm.system.log.import.csv.gateway.address.check"))
									.append(colValue[k+2]).append("\n");
								continue;
							}
							if (!HmBeOsUtil.isInSameSubnet(singleAP.getCfgIpAddress(),
								colValue[k+2], singleAP.getCfgNetmask())) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
									.append(MgrUtil.getUserMessage("hm.system.log.import.csv.gateway.subnet.mask.same.check"))
									.append("\n");
								continue;
							}
							singleAP.setCfgGateway(colValue[k+2]);
							singleAP.setGateway(colValue[k+2]);
						}

					} else {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.subnet.mask.empty.check"))
							.append("\n");
						continue;
					}
					singleAP.setDhcp(false);
				} else {
					if (colValue.length > k+1) {
						if (!colValue[k+1].equals("")) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ip.address.length.check"))
								.append("\n");
							continue;
						}
						if (colValue.length > k+2) {
							if (!colValue[k+2].equals("")) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
									.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ip.address.subnet.mask.check"))
									.append("\n");
								continue;
							}
						}
					}
				}

				k+=3;
				if (colValue.length > k) {
					if (!colValue[k].equals("")) {
						if (MapMgmt.ROOT_MAP_NAME.equals(colValue[k]) || MapMgmt.VHM_ROOT_MAP_NAME.equals(colValue[k])) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.topology.name.check"))
								.append(colValue[k]).append("\n");
							continue;
						}

						MapContainerNode topology = QueryUtil
								.findBoByAttribute(MapContainerNode.class,
										"mapName", colValue[k], domain
												.getId());
						if (null == topology) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.topology.type.exist.check"))
								.append(colValue[k]).append("\n");
							continue;
						}
						if (MapContainerNode.MAP_TYPE_BUILDING == topology.getMapType()) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.topology.type.check"))
								.append(colValue[k]).append("\n");
							continue;
						}

						singleAP.setMapContainer(topology);

					}
				}

				singleAP.setOwner(domain);

				// Node ID cannot repeat in this file
				boolean boolMac = false;
				for (String nodeId : nodeIds) {
					if (nodeId.equalsIgnoreCase(colValue[1])) {
						boolMac = true;
						break;
					}
				}
				if (boolMac) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(". ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.node.id.check",colValue[1]))
					.append("\n");
					continue;
				}

				// Host Name cannot repeat in this file
				boolean boolName = false;
				for (String name : names) {
					if (name.equals(hostName)) {
						boolName = true;
						break;
					}
				}
				if (boolName) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.host.name.check",hostName))
					.append("\n");
					continue;
				}
				if (!boolMac && !boolName) {
					nodeIds.add(colValue[1]);
					names.add(hostName);
				}
				
				// update hiveap
//				if (updateAp) {
//					m_vct_hiveAP.add(singleAP);
//				// create new hiveap
//				} else {
				List<HiveAp> newAps = newHiveApMap.get(domain.getId());
				if (null == newAps) {
					newAps = new ArrayList<HiveAp>();
					newAps.add(singleAP);
					newHiveApMap.put(domain.getId(), newAps);
				} else {
					newAps.add(singleAP);
				}
//				}
			}
		} catch (Exception e) {
			addActionError(e.getMessage());
		}
	}
	
	/**
	 * Check the HiveAP file data and save them in database.
	 * @return int : the total number of create successfully
	 */
	private int[] createHiveAp() {
		int[] successNumber = new int[]{0, 0};
		try {
//			List<HmDomain> bos = QueryUtil.executeQuery(HmDomain.class, null,
//					new FilterParams("id", newHiveApMap.keySet()));
//			Map<Long, HmDomain> hmDomains = new HashMap<Long, HmDomain>(bos
//					.size());
//
//			for (HmDomain hmDomain : bos) {
//				hmDomain.computeManagedApNum();
//				hmDomains.put(hmDomain.getId(), hmDomain);
//			}

			/*
			 * Check the total number of HiveAPs
			 */
			for (Long domId : newHiveApMap.keySet()) {
				//HmDomain domain = hmDomains.get(domId);
				for (HiveAp ap : newHiveApMap.get(domId)) {
					HmDomain domain = ap.getOwner();
					if (domain.getRunStatus() == HmDomain.DOMAIN_DEFAULT_STATUS) {
						//Assign default value
						if(null == ap.getConfigTemplate()){
							ap.setConfigTemplate(defTemp);
						}
						if(null==ap.getWifi0RadioProfile() || null==ap.getWifi0RadioProfile().getId()){
							RadioProfile wifi0RadioProfile = HiveAp.is11nHiveAP(ap.getHiveApModel()) ? defRadioNG
									: defRadioBG;
							ap.setWifi0RadioProfile(wifi0RadioProfile);
						}
						if(null==ap.getWifi1RadioProfile() || null==ap.getWifi1RadioProfile().getId()){
							RadioProfile wifi1RadioProfile = HiveAp.is11nHiveAP(ap.getHiveApModel()) ? defRadioNA
									: defRadioA;
							ap.setWifi1RadioProfile(wifi1RadioProfile);
						}
						BoMgmt.getMapMgmt().createHiveApWithPropagation(ap,
								ap.getMapContainer());
						successNumber[0]++;
						//domain.setManagedApNum(domain.getManagedApNum() + 1);
					} else {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.haveaps.total.num.check",new String[]{ap.getId().toString(),NmsUtil.getOEMCustomer().getNmsNameAbbreviation(),domain.getDomainName()})).append("\n");
					}
				}
			}
			
			// update the HiveAP data to database
			for (HiveAp hiveAp : m_vct_hiveAP) {
				try {
					MapContainerNode newParent = topoMap
							.get(hiveAp.getId());
					BoMgmt.getMapMgmt()
							.updateHiveApWithPropagation(
									hiveAp, newParent,
									hiveAp.isConnected(),
									hiveAp.getManageStatus());
					successNumber[1]++;
				} catch (RuntimeException e) {
					result.append("Update ").append(NmsUtil.getOEMCustomer().getAccessPonitName()).append(":").append(hiveAp.getMacAddress()).append(" error.").append(e.getMessage()).append("\n");
				}
			}
		} catch (Exception ex) {
			addActionError(ex.getMessage());
		}
		return successNumber;
	}

	private static boolean isNumber(String value) {
		if (value == null || value.equals(""))
			return false;
		for (int i = 0; i < value.length(); i++)
			if (!"0123456789".contains(value.substring(i, i + 1)))
				return false;
		return true;
	}

	private boolean isHex(String value) {
		if (value == null || value.equals(""))
			return false;
		for (int i = 0; i < value.length(); i++)
			if (!"0123456789ABCDEFabcdef".contains(value.substring(i, i + 1)))
				return false;
		return true;
	}

	private boolean checkValue(String value, int min, int max) {
		if (value == null || value.equals(""))
			return false;
		if (!isNumber(value))
			return false;
		int v = Integer.parseInt(value);
		return !(v < min || v > max);
	}

	/**
	 * Check the ipAddress' format
	 *
	 * @param arg_IpAddress -
	 * @return boolean - true:the ip is wrong;false:the ip is right.
	 */
	public static boolean getIpAddressWrongFlag(String arg_IpAddress) {
		boolean bool = false;
		String[] strIp = arg_IpAddress.split("\\.");
		if (strIp.length != 4 || arg_IpAddress.length() > 15) {
			bool = true;
		} else {
			for (int i = 0; i < 4; i++) {
				if (!isNumber(strIp[i]) || Integer.parseInt(strIp[i]) > 255) {
					bool = true;
					break;
				}
			}
			if (!bool) {
				bool = Pattern.matches("^([0]{1,3}\\.){3}[0]{1,3}$", arg_IpAddress);
			}
		}
		return bool;
	}

	/**
	 * Check the netmask format
	 *
	 * @param arg_Netmask -
	 * @return boolean - true:the netmask is wrong;false:the netmask is right.
	 */
	public static boolean getNetmaskWrongFlag(String arg_Netmask) {
		if (!getIpAddressWrongFlag(arg_Netmask)) {
			long long_Net = AhEncoder.ip2Long(arg_Netmask);
			String binaryString = Long.toBinaryString(long_Net);

			return binaryString.length() != 32
					|| (binaryString.contains("0") && binaryString.indexOf("0") < binaryString
							.lastIndexOf("1"));
		} else {
			return true;
		}
	}

	/**
	 * The special characters cannot be input.
	 *
	 * @param str_Value : the checked character;
	 * @param str_Limit : the limit characters
	 * @return String : the result
	 */
	private String checkTheSpecialCharacter(String str_Value, String[] str_Limit) {
		if (null == str_Limit) {
			return "";
		}
		for (String signal : str_Limit) {
			if (str_Value.contains(signal)) {
				return signal;
			}
		}
		return "";
	}

	/**
	 * Check the values in one line.
	 *
	 *@param arg_Value : line value
	 *@param arg_Line : the line number
	 *@return boolean : true : the value is valid; false : the value is invalid
	 */
	private boolean checkTheLineValue (String[] arg_Value, int arg_Line) {
		boolean boolResult = true;
		int length = arg_Value.length;
		if (length == 0) {
			result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(arg_Line).append(" ")
			.append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(". ")
			.append(MgrUtil.getUserMessage("hm.system.log.import.csv.record.no.value"))
				.append("\n");
			boolResult = false;
		}
		if (arg_Value[0].startsWith("*") || arg_Value[0].startsWith("#")) {
			result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ignore.line")).append(" ").append(arg_Line).append(MgrUtil.getUserMessage("hm.system.log.import.csv.record.start.char"))
			.append(" '").append(arg_Value[0].charAt(0)).append("'.")
				.append("\n");
			boolResult = false;
		}
		if (arg_Value[0].startsWith("//")) {
			result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ignore.line")).append(" ").append(arg_Line)
			.append(MgrUtil.getUserMessage("hm.system.log.import.csv.record.start.char")).append(" '//'.")
				.append("\n");
			boolResult = false;
		}
		// ignore blank line
		if(StringUtils.strip(arg_Value[0]).isEmpty()&&StringUtils.strip(arg_Value[length-1]).isEmpty())
			if(StringUtils.isBlank(StringUtils.join(arg_Value))){
				result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.blank.line.check",String.valueOf(arg_Line)))
				.append("\n");
				boolResult=false;
			}
		
		return boolResult;
	}
	public String getResultMessage() {
		if (result.length() > 0)
			resultMessage = result.toString();
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}

	public String getTitleStr() {
		return getSelectedL2Feature().getDescription() + " > " + getText("config.csvfile.import");
	}

/**
	 * Check the values in one line.
	 *
	 *@param arg_Value : line value
	 *@param arg_Line : the line number
	 *@return boolean : true : the value is valid; false : the value is invalid
	 */
//	private boolean checkTimeValue (String arg_Value, int arg_Line, String field) {
//		if (arg_Value.length() != 12) {
//			result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(arg_Line).append(
//				" failed : the length of " + field + " must be 12 :: ")
//				.append(arg_Value).append("\n");
//			return false;
//		}
//		if (!isNumber(arg_Value)) {
//			result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(arg_Line).append(
//				" failed: the format of " + field + " is wrong :: ")
//				.append(arg_Value).append("\n");
//			return false;
//		}
//		return true;
//	}

	/**
	 * Check the password of local user.
	 *
	 * @param group the user belong to;
	 * @param intLine the line number;
	 * @param manualPass the password to be checked;
	 * @param userName this user name
	 * @return boolean
	 */
	private boolean checkPasswordCorrect(LocalUserGroup group, int intLine, String manualPass, String userName) {
		if (group.getPskGenerateMethod() == LocalUserGroup.PSK_METHOD_PASSWORD_USERNAME) {
			int intPskLength = group.getConcatenateString().length() + userName.length()
					+ manualPass.length();
			if (intPskLength > 63) {
				result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" " + intLine + " ")
				.append(MgrUtil.getUserMessage("hm.system.log.import.csv.password.psk.length.check"))
					.append(manualPass).append("\n");
				return false;
			}
		}

		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String digStr = "1234567890";
		String spcStr = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
		boolean blnStr = false;
		boolean blnDig = false;
		boolean blnSpc = false;
		char[] psbyte;
		String message;
		if (group.getPskGenerateMethod() == LocalUserGroup.PSK_METHOD_PASSWORD_USERNAME){
			psbyte = (userName + group.getConcatenateString() +  manualPass).toCharArray();
			message = " " + MgrUtil.getUserMessage("hm.system.log.import.csv.username.password.message.value") + " ";
		} else {
			psbyte =  manualPass.toCharArray();
			message = " " + MgrUtil.getUserMessage("hm.system.log.import.csv.password.message.value") + " ";
		}

		for(char onebyte: psbyte){
			if (str.indexOf(onebyte)!=-1){
				blnStr = true;
				break;
			}
		}

		for(char onebyte: psbyte){
			if (digStr.indexOf(onebyte)!=-1){
				blnDig = true;
				break;
			}
		}

		for(char onebyte: psbyte){
			if (spcStr.indexOf(onebyte)!=-1){
				blnSpc = true;
				break;
			}
		}

		boolean bool = true;
		String errorMessage = "";
		if (group.getPersonPskCombo() == LocalUserGroup.PSKFORMAT_COMBO_AND){
			if (!blnStr == group.getBlnCharLetters()){
				if (blnStr){
					errorMessage = MgrUtil.getUserMessage("hm.system.log.import.csv.password.message.check.head")+ message +MgrUtil.getUserMessage("hm.system.log.import.csv.password.letters.check");
				} else {
					errorMessage = MgrUtil.getUserMessage("hm.system.log.import.csv.password.message.check.head")+ message +MgrUtil.getUserMessage("hm.system.log.import.csv.password.exist.letters.check");
				}
				bool = false;
			} else if (!blnDig == group.getBlnCharDigits()){
				if (blnDig){
					errorMessage = MgrUtil.getUserMessage("hm.system.log.import.csv.password.message.check.head")+ message +MgrUtil.getUserMessage("hm.system.log.import.csv.password.ditgits.check");
				} else {
					errorMessage = MgrUtil.getUserMessage("hm.system.log.import.csv.password.message.check.head")+ message +MgrUtil.getUserMessage("hm.system.log.import.csv.password.exist.ditgits.check");
				}
				bool = false;
			} else if (!blnSpc == group.getBlnCharSpecial()){
				if (blnSpc){
					errorMessage = MgrUtil.getUserMessage("hm.system.log.import.csv.password.message.check.head")+ message +MgrUtil.getUserMessage("hm.system.log.import.csv.password.characters.check");
				} else {
					errorMessage = MgrUtil.getUserMessage("hm.system.log.import.csv.password.message.check.head")+ message +MgrUtil.getUserMessage("hm.system.log.import.csv.password.exist.characters.check");
				}
				bool = false;
			}
		} else if (group.getPersonPskCombo() ==LocalUserGroup.PSKFORMAT_COMBO_OR){
			if (!group.getBlnCharLetters() && blnStr){
				errorMessage = MgrUtil.getUserMessage("hm.system.log.import.csv.password.message.check.head")+ message +MgrUtil.getUserMessage("hm.system.log.import.csv.password.letters.check");
				bool = false;
			} else if (!group.getBlnCharDigits() && blnDig){
				errorMessage = MgrUtil.getUserMessage("hm.system.log.import.csv.password.message.check.head")+ message +MgrUtil.getUserMessage("hm.system.log.import.csv.password.ditgits.check");
				bool = false;
			} else if (!group.getBlnCharSpecial() && blnSpc){
				errorMessage = MgrUtil.getUserMessage("hm.system.log.import.csv.password.message.check.head")+ message +MgrUtil.getUserMessage("hm.system.log.import.csv.password.characters.check");
				bool = false;
			}
		} else if (group.getPersonPskCombo() ==LocalUserGroup.PSKFORMAT_COMBO_NOCOMBO){
			if (group.getBlnCharLetters()){
				if (blnDig){
					errorMessage = MgrUtil.getUserMessage("hm.system.log.import.csv.password.message.check.head")+ message +MgrUtil.getUserMessage("hm.system.log.import.csv.password.ditgits.check");
					bool = false;
				} else if (blnSpc){
					errorMessage = MgrUtil.getUserMessage("hm.system.log.import.csv.password.message.check.head")+ message +MgrUtil.getUserMessage("hm.system.log.import.csv.password.characters.check");
					bool = false;
				}
			} else if (group.getBlnCharDigits()){
				if (blnStr){
					errorMessage = MgrUtil.getUserMessage("hm.system.log.import.csv.password.message.check.head")+ message +MgrUtil.getUserMessage("hm.system.log.import.csv.password.letters.check");
					bool = false;
				} else if (blnSpc){
					errorMessage = MgrUtil.getUserMessage("hm.system.log.import.csv.password.message.check.head")+ message +MgrUtil.getUserMessage("hm.system.log.import.csv.password.characters.check");
					bool = false;
				}
			} else if (group.getBlnCharSpecial()){
				if (blnStr){
					errorMessage = MgrUtil.getUserMessage("hm.system.log.import.csv.password.message.check.head")+ message +MgrUtil.getUserMessage("hm.system.log.import.csv.password.letters.check");
					bool = false;
				} else if (blnDig){
					errorMessage = MgrUtil.getUserMessage("hm.system.log.import.csv.password.message.check.head")+ message +MgrUtil.getUserMessage("hm.system.log.import.csv.password.ditgits.check");
					bool = false;
				}
			}
		}
		if (!bool) {
			result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" " + intLine + " ")
			.append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(" : ").append(errorMessage).append(" :: ")
				.append(manualPass).append("\n");
		}
		return bool;
	}

	/**
	 * Check the email address of local user.
	 *
	 * @param mailAddress -
	 * @param intLine the line number
	 * @return boolean
	 */
	private boolean checkMailAddress(String mailAddress, int intLine, String msg){
		if (mailAddress.equals("")){
			return false;
		}
		String regex = "^([a-z0-9A-Z]+[-|_\\.+]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

		/* The following is the list of known TLDs that an e-mail address must end with. */
		String[] knownDomsPat = new String[]{".com",".net",".org",".edu",".int",".mil",".gov",".arpa",".biz",".aero",".name",".coop",".info",".pro",".museum"};

		boolean bool = true;
		String mail[] = mailAddress.split(";");
		for(String oneItem: mail){
			if (oneItem.equals("")){
				bool = false;
				break;
			}
			if (!Pattern.matches(regex, oneItem)) {
				bool = false;
				break;
			}
			String[] total = oneItem.split("\\.");
			if (total[total.length-1].length() != 2) {
				bool = false;
				for (String endStr : knownDomsPat) {
					if (oneItem.endsWith(endStr)) {
						bool = true;
						break;
					}
				}
				if (!bool) {
					break;
				}
			}
		}
		if(!bool){
			result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" " + intLine + "::")
			.append(msg).append("::")
			.append(mailAddress).append("\n");
		}
		return bool;
	}

	/**
	 * Read the Tv Class file and check the data.
	 */
	private void readTvClass() {
		tvClassMap = new HashMap<String, List<TvClass>>();
		try {
			int intLine = 0;
			TvClass dto;
			lineValue:
			for (String[] value : allvalue) {
				intLine++;
				int oldLine = intLine;
				boolean lineBool = false;
				// the value contains line-breaks
				for (String lineValue : value) {
					if (lineValue.contains("\r\n")) {
						intLine = intLine + lineValue.split("\r\n").length - 1;
						lineBool = true;
					}
				}
				if (lineBool) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(oldLine).append(" - ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.field.value.linebreaks.check").toLowerCase())
					.append("\n");
					continue;
				}
				if (!checkTheLineValue(value, intLine)) {
					continue;
				}
				HmDomain domain = thisDomain;
				boolean isDomain = false;
				boolean ifValidLength;
				if (getShowDomain()
						&& thisDomain != null
						&& HmDomain.HOME_DOMAIN.equals(thisDomain
								.getDomainName())) {
					ifValidLength = value.length > 10 && ((value.length-7)%4 == 0);
					isDomain = true;
				} else {
					ifValidLength = value.length > 9 && ((value.length-6)%4 == 0);
				}
				if (!ifValidLength) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.fields.num.check"))
					.append(value.length).append("\n");
					continue;
				}

				if (isDomain) {
					if (value[6].length() > 32) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.virtual.length.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
						.append(value[6]).append("\n");
						continue;
					} else if (!"".equals(value[6])) {
						domain = QueryUtil.findBoByAttribute(
								HmDomain.class, "domainName", value[6]);
						if (null == domain) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.virtual.db.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
							.append(value[6]).append("\n");
							continue;
						}
					}
				}
				// get the domain id
				String key = String.valueOf(domain.getId());

				/*
				 * check object name
				 */
				if (value[0].length() < 1 || value[0].length() > 32) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.class.name.length.check"))
						.append(value[0]).append("\n");
					continue;
				} else {
					String strResult = checkTheSpecialCharacter(value[0], SINGLE_STRING_LIMIT);
					if (!"".equals(strResult)) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.class.name.check"))
						.append(" '").append(strResult).append("' :: ").append(value[0]).append("\n");
						continue;
					} else if (QueryUtil.findBoByAttribute(TvClass.class,
						"className", value[0], domain.getId()) != null) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(". ")	
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.class.name.exist.db.check",value[0]))
							.append("\n");
						continue;
					}
					// check if the name exist in this file
					if (null != tvClassMap.get(key)) {
						for (TvClass oneClass : tvClassMap.get(key)) {
							if (value[0].equals(oneClass.getClassName())) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(". ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.class.name.repeat.check",value[0]))
								.append("\n");
								continue lineValue;
							}
						}
					}
				}
				
				/*
				 * Get the Subject.
				 */
				if (value[1].length() < 1 || value[1].length() >64){
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.subject.length.check"))
						.append(value[1]).append("\n");
					continue;
				} 
				
				/*
				 * Get the Teacher.
				 */
				if (value[2].length() < 1 || value[2].length() > 128) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.teacher.name.length.check"))
					.append(value[2]).append("\n");
					continue;
				} else if(!checkMailAddress(value[2], intLine, MgrUtil.getUserMessage("hm.system.log.import.csv.teacher.name.format.check"))){
					continue;
				} else if (!checkTeacherAvailable(value[2], domain.getId())) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(". ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.teacher.name.db.check",value[2]))
					.append("\n");
					continue;
				}
				
				/*
				 * Get Roster Type.
				 */
				if (!"1".equals(value[3]) && !"2".equals(value[3])) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.roster.type.check"))
						.append(value[3]).append("\n");
					continue;
				} 
				
				/*
				 * Get Computer Cart Name.
				 */
				TvComputerCart tvCart=null;
				if (value[3].equals("2")){
					if (value[4].length() < 1 || value[4].length() > 128) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.computer.cart.name.length.check"))
						.append(value[4]).append("\n");
						continue;
					}  else {
						tvCart = QueryUtil.findBoByAttribute(TvComputerCart.class,
								"cartName", value[4], domain.getId());
						if (tvCart==null){
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(". ")		
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.computer.cart.name.db.check",value[4]))
									.append("\n");
							continue;
						}
					}
				}
				
				/*
				 * Get Description.
				 */
				if (value[5].length() >256){
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.description.length.check"))
						.append(value[5]).append("\n");
					continue;
				} 

				/*
				 * get the ip entries
				 */
				List<TvClassSchedule> scheduleInfo = new ArrayList<TvClassSchedule>();
				for (int i = isDomain ? 7 : 6; i < value.length-1; i=i+4) {
					TvClassSchedule singleInfo = new TvClassSchedule();
					/*
					 * weekDay
					 */
					if(!checkWeekDay(value[i])){
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.weekday.check"))
						.append(value[i]).append("\n");
						continue lineValue;
					}
//					if (!value[i].equals("0") && !value[i].equals("1") 
//							&& !value[i].equals("2") && !value[i].equals("3")
//							&& !value[i].equals("4") && !value[i].equals("5")
//							&& !value[i].equals("6") && !value[i].equals("7")) {
//							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
//							.append(" failed : the format of weekday is wrong :: ")
//							.append(value[i]).append("\n");
//						continue lineValue;
//					}
					
					/*
					 * Start Time
					 */
					if (!checkFormatTimeForClass(value[i+1])){
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.start.time.value.check"))
							.append(value[i+1]).append("\n");
						continue lineValue;
					}
					
					/*
					 * End Time
					 */
					if (!checkFormatTimeForClass(value[i+2])){
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.end.time.value.check"))
							.append(value[i+2]).append("\n");
						continue lineValue;
					}
					
					if (value[i+2].compareTo(value[i+1])<=0){
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.start.time.check"))
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.start.time")).append(value[i+1])
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.end.time")).append(value[i+2]).append("\n");
						continue lineValue;
					}
					
					/*
					 * Get the Room.
					 */
					if (value[i+3].length() > 256) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.room.length.check"))
						.append(value[i+3]).append("\n");
						continue lineValue;
					}
					
					for(TvClassSchedule single : scheduleInfo){
						for(int k=0;k<7;k++){
							if (single.getWeekdaySec().charAt(k)=='1' && value[i].charAt(k)=='1'){
								if ((single.getStartTime().compareTo(value[i+1])>=0 && single.getStartTime().compareTo(value[i+2])<=0)
										|| (single.getEndTime().compareTo(value[i+1])>=0 && single.getEndTime().compareTo(value[i+2])<=0)
										|| ((value[i+1]).compareTo(single.getStartTime())>=0 && (value[i+1]).compareTo(single.getEndTime())<=0)
										|| ((value[i+2]).compareTo(single.getStartTime())>=0 && (value[i+2]).compareTo(single.getEndTime())<=0)){
									result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
									.append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(". ")
									.append(MgrUtil.getUserMessage("hm.system.log.import.csv.the.schedule.time.same.check")).append("\n");
									continue lineValue;
								}
							}
						}
					}
					
					
//					for(TvClassSchedule single : scheduleInfo){
//						if (single.getWeekday().equalsIgnoreCase(getTvWeekDayString(value[i]))
//								&& single.getStartTime().equalsIgnoreCase(value[i+1])
//								&& single.getEndTime().equalsIgnoreCase(value[i+2])
//								&& single.getRoom().equalsIgnoreCase(value[i+3])){
//							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
//							.append(" failed : the scheduler time is same.").append("\n");
//							continue lineValue;
//						}
//						
//						if (getTvWeekDayString(value[i]).equalsIgnoreCase(TvClassAction.MONDAY_TO_FRIDAY)){
//							if (!single.getWeekday().equalsIgnoreCase("Saturday")
//									&& !single.getWeekday().equalsIgnoreCase("Sunday")){
//								if ((single.getStartTime().compareTo(value[i+1])>=0 && single.getStartTime().compareTo(value[i+2])<=0)
//										|| (single.getEndTime().compareTo(value[i+1])>=0 && single.getEndTime().compareTo(value[i+2])<=0)
//										|| ((value[i+1]).compareTo(single.getStartTime())>=0 && (value[i+1]).compareTo(single.getEndTime())<=0)
//										|| ((value[i+2]).compareTo(single.getStartTime())>=0 && (value[i+2]).compareTo(single.getEndTime())<=0)){
//									result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
//									.append(" failed : the scheduler time is same.").append("\n");
//									continue lineValue;
//								}
//							}
//						} else {
//							if (single.getWeekday().equalsIgnoreCase(TvClassAction.MONDAY_TO_FRIDAY)
//									&& !getTvWeekDayString(value[i]).equalsIgnoreCase("Saturday")
//									&& !getTvWeekDayString(value[i]).equalsIgnoreCase("Sunday")){
//								if ((single.getStartTime().compareTo(value[i+1])>=0 && single.getStartTime().compareTo(value[i+2])<=0)
//										|| (single.getEndTime().compareTo(value[i+1])>=0 && single.getEndTime().compareTo(value[i+2])<=0)
//										|| ((value[i+1]).compareTo(single.getStartTime())>=0 && (value[i+1]).compareTo(single.getEndTime())<=0)
//										|| ((value[i+2]).compareTo(single.getStartTime())>=0 && (value[i+2]).compareTo(single.getEndTime())<=0)){
//									result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
//									.append(" failed : the scheduler time is same.").append("\n");
//									continue lineValue;
//								}
//							}
//						}
//					}
					
					
					singleInfo.setWeekdaySec(value[i].trim());
					singleInfo.setStartTime(value[i+1]);
					singleInfo.setEndTime(value[i+2]);
					singleInfo.setRoom(value[i+3]);
					scheduleInfo.add(singleInfo);
				}
				// there is no ip entry
				if (scheduleInfo.isEmpty()) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(". ")	
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.one.class.schedule.exist.check"))
						.append("\n");
					continue lineValue;
				}
				
				if (scheduleInfo.size() >32) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(". ")	
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.one.class.schedule.check"))
						.append("\n");
					continue lineValue;
				}

				dto = new TvClass();
				dto.setClassName(value[0]);
				dto.setSubject(value[1]);
				dto.setTeacherId(value[2]);
				dto.setRosterType(Integer.parseInt(value[3]));
				dto.setComputerCart(tvCart);
				dto.setDescription(value[5]);
				dto.setOwner(domain);
				dto.setItems(scheduleInfo);

				if (null == tvClassMap.get(key)) {
					List<TvClass> vct_tvClass = new ArrayList<TvClass>();
					vct_tvClass.add(dto);
					tvClassMap.put(key, vct_tvClass);
				} else {
					tvClassMap.get(key).add(dto);
				}
			}
		} catch (Exception ex) {
			addActionError(ex.getMessage());
		}
	}
	
	private boolean checkTeacherAvailable(String teacherEmail, Long domainId){
		if (!StringUtils.isEmpty(getUserContext().getCustomerId())) {
			List<String> teacherEmails = new ArrayList<String>();
			if(teacherEmails.size() == 0){
				try {
					// get teacher list from Portal
					HmDomain owner = getCustomerDomain();
					if (owner == null) {
						return false;
					}
					
					String productId = getProductId(owner);
					List<CustomerUserInfo>  findTeachers = ClientUtils.getPortalResUtils()
							.getVHMUsersByGroupName(getUserContext().getCustomerId(),
									productId,
									HmUserGroup.TEACHER);
					if (findTeachers != null && !findTeachers.isEmpty()) {
						for(CustomerUserInfo findTeacher : findTeachers){
							teacherEmails.add(findTeacher.getUserEmail());
						}					
					} 
				} catch (Exception e) {
					addActionError(MgrUtil.getUserMessage("error.teacherView.find.teacher.failed"));
					return false;
				}
			}
			
			for(int i = 0; i < teacherEmails.size(); i++){
				if(teacherEmail.equalsIgnoreCase(teacherEmails.get(i))){
					return true;
				}
			}
			
			return false;
		}else{
			return !QueryUtil.executeQuery(HmUser.class, null, 
					new FilterParams("emailAddress=:s1 and userGroup.groupName=:s2",
							new Object[]{teacherEmail,HmUserGroup.TEACHER}),domainId).isEmpty();
		}
	}
	
	private HmDomain getCustomerDomain() {
		return getUserContext().getSwitchDomain() != null ? getUserContext().getSwitchDomain() : getUserContext().getOwner();
	}
	
	private String getProductId(HmDomain owner) {
		String productId;
		if (owner != null && owner.isHomeDomain()) {
			// Aerohive's VHM-ID should be 'HMOL-Aerohive'
			productId = HmDomain.PRODUCT_ID_VHM;
		} else {
			productId = owner.getVhmID();
		}
		return productId;
	}
	
	/**
	 * Read the  TvComputerCart file and check the data.
	 */
	private void readTvComputerCart() {
		tvComputerCartMap = new HashMap<String, List<TvComputerCart>>();
		try {
			int intLine = 0;
			TvComputerCart dto;
			lineValue:
			for (String[] value : allvalue) {
				intLine++;
				int oldLine = intLine;
				boolean lineBool = false;
				// the value contains line-breaks
				for (String lineValue : value) {
					if (lineValue.contains("\r\n")) {
						intLine = intLine + lineValue.split("\r\n").length - 1;
						lineBool = true;
					}
				}
				if (lineBool) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(oldLine).append(" - ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.field.value.linebreaks.check").toLowerCase())
					.append("\n");
					continue;
				}
				if (!checkTheLineValue(value, intLine)) {
					continue;
				}
				HmDomain domain = thisDomain;
				boolean isDomain = false;
				boolean ifValidLength;
				if (getShowDomain()
						&& thisDomain != null
						&& HmDomain.HOME_DOMAIN.equals(thisDomain
								.getDomainName())) {
					ifValidLength = value.length > 3;
					isDomain = true;
				} else {
					ifValidLength = value.length > 2;
				}
				if (!ifValidLength) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.fields.num.check"))
					.append(value.length).append("\n");
					continue;
				}

				if (isDomain) {
					if (value[2].length() > 32) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.virtual.length.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
							.append(value[2]).append("\n");
						continue;
					} else if (!"".equals(value[2])) {
						domain = QueryUtil.findBoByAttribute(
								HmDomain.class, "domainName", value[2]);
						if (null == domain) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.virtual.db.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
							.append(value[2]).append("\n");
							continue;
						}
					}
				}
				// get the domain id
				String key = String.valueOf(domain.getId());

				/*
				 * check object name
				 */
				if (value[0].length() < 1 || value[0].length() > 128) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.computer.cart.name.length.check"))
						.append(value[0]).append("\n");
					continue;
				} else {
					String strResult = checkTheSpecialCharacter(value[0], SINGLE_STRING_LIMIT);
					if (!"".equals(strResult)) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.computer.cart.name.check"))
						.append(" '").append(strResult).append("' :: ").append(value[0]).append("\n");
						continue;
					} else if (QueryUtil.findBoByAttribute(TvComputerCart.class,
						"cartName", value[0], domain.getId()) != null) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(". ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.computer.cart.name.db.check",value[0]))
						.append("\n");
						continue;
					}
					// check if the name exist in this file
					if (null != tvComputerCartMap.get(key)) {
						for (TvComputerCart oneClass : tvComputerCartMap.get(key)) {
							if (value[0].equals(oneClass.getCartName())) {
								result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(". ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.computer.cart.name.repeat.check",value[0]))
								.append("\n");
								continue lineValue;
							}
						}
					}
				}
				
				/*
				 * Get Description.
				 */
				if (value[1].length() >256){
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.description.length.check"))
						.append(value[1]).append("\n");
					continue;
				} 

				/*
				 * get the ip entries
				 */
				List<TvComputerCartMacName> macInfo = new ArrayList<TvComputerCartMacName>();
				Set<String> macList = new HashSet<String>();
				//The MacAddress and Computer Name should be paired
				int macPairCounter = value.length - (isDomain ? 3 : 2);
				if(macPairCounter % 2 != 0){
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.computercart.valuepair.check"))
					.append(value[0]).append("\n");
					continue lineValue;
				}
				
				for (int i = isDomain ? 3 : 2; i < value.length; i=i+2) {
					/*
					 *  MacAddress length check
					 */
					if (value[i].length()!=12) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.mac.address.value.check"))
						.append(value[i]).append("\n");
						continue lineValue;
					} 
					for(int index=0;index<value[i].length();index++){
						if (!((value[i].charAt(index)>='0' && value[i].charAt(index)<='9')||
								(value[i].charAt(index)>='a' && value[i].charAt(index)<='f')||
								(value[i].charAt(index)>='A' && value[i].charAt(index)<='F'))){
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
								.append(MgrUtil.getUserMessage("hm.system.log.import.csv.mac.address.check"))
								.append(value[i]).append("\n");
							continue lineValue;
						}
					}
					if (macList.contains(value[i])){
						continue lineValue;
					}
					macList.add(value[i]);
					
					if (value[i+1].length() == 0 || value[i+1].length()>128) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.computer.name.length.check"))
						.append(value[i+1]).append("\n");
						continue lineValue;
					} 
					TvComputerCartMacName tmpMacName= new TvComputerCartMacName();
					tmpMacName.setStuMac(value[i]);
					tmpMacName.setStuName(value[i+1]);
					macInfo.add(tmpMacName);
				}
				// there is no ip entry
				if (macInfo.isEmpty()) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(". ")	
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ip.no.check"))
						.append("\n");
					continue;
				}

				dto = new TvComputerCart();
				dto.setCartName(value[0]);
				dto.setDescription(value[1]);
				dto.setOwner(domain);
				dto.setItems(macInfo);

				if (null == tvComputerCartMap.get(key)) {
					List<TvComputerCart> vct_tvCart = new ArrayList<TvComputerCart>();
					vct_tvCart.add(dto);
					tvComputerCartMap.put(key, vct_tvCart);
				} else {
					tvComputerCartMap.get(key).add(dto);
				}
			}
		} catch (Exception ex) {
			addActionError(ex.getMessage());
		}
	}
	
	/**
	 * Read the Tv StudentRoster file and check the data.
	 */
	private void readTvStudentRoster() {
		tvStudentRosterMap = new HashMap<String, List<TvStudentRoster>>();
		try {
			int intLine = 0;
			TvStudentRoster dto;
			lineValue:
			for (String[] value : allvalue) {
				intLine++;
				int oldLine = intLine;
				boolean lineBool = false;
				// the value contains line-breaks
				for (String lineValue : value) {
					if (lineValue.contains("\r\n")) {
						intLine = intLine + lineValue.split("\r\n").length - 1;
						lineBool = true;
					}
				}
				if (lineBool) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(oldLine).append(" - ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.field.value.linebreaks.check").toLowerCase())
					.append("\n");
					continue;
				}
				if (!checkTheLineValue(value, intLine)) {
					continue;
				}
				HmDomain domain = thisDomain;
				boolean isDomain = false;
				boolean ifValidLength;
				if (getShowDomain()
						&& thisDomain != null
						&& HmDomain.HOME_DOMAIN.equals(thisDomain
								.getDomainName())) {
					ifValidLength = value.length > 4;
					isDomain = true;
				} else {
					ifValidLength = value.length > 3;
				}
				if (!ifValidLength) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.fields.num.check"))
					.append(value.length).append("\n");
					continue;
				}

				if (isDomain) {
					if (value[4].length() > 32) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.virtual.length.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
						.append(value[4]).append("\n");
						continue;
					} else if (!"".equals(value[4])) {
						domain = QueryUtil.findBoByAttribute(
								HmDomain.class, "domainName", value[4]);
						if (null == domain) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.virtual.db.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
							.append(value[4]).append("\n");
							continue;
						}
					}
				}
				// get the domain id
				String key = String.valueOf(domain.getId());

				/*
				 * check Student ID
				 */
				if (value[0].length() < 1 || value[0].length() > 128) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.student.id.length.check"))
						.append(value[0]).append("\n");
					continue;
				} else {
					String strResult = checkTheSpecialCharacter(value[0], SINGLE_STRING_LIMIT);
					if (!"".equals(strResult)) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.student.id.check"))
						.append(" '").append(strResult).append("' :: ").append(value[0]).append("\n");
						continue;
					}
				}
				
				/*
				 * check Student Name
				 */
				if (value[1].length() < 1 || value[1].length() > 128) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.student.name.length.check"))
						.append(value[1]).append("\n");
					continue;
				} else {
					String strResult = checkTheSpecialCharacter(value[1], SINGLE_STRING_LIMIT_WITH_BLANK);
					if (!"".equals(strResult)) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.student.name.check"))
						.append("'").append(strResult).append("' :: ").append(value[1]).append("\n");
						continue;
					}
				}
				
				/*
				 * Get class Name.
				 */
				TvClass tvClass;
				if (value[2].length() < 1 || value[2].length() > 32) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.class.name.length.check"))
					.append(value[2]).append("\n");
					continue;
				}  else {
					tvClass = QueryUtil.findBoByAttribute(TvClass.class,
							"className", value[2], domain.getId());
					if (tvClass==null){
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append("::")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(". ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.class.name.not.exist.db.check", value[2])).append("\n");
						continue;
					}else if(tvClass.getRosterType() != TvClass.TV_ROSTER_TYPE_STUDENT){
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append("::")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(". ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.class.rostertype.check", value[2])).append("\n");
						continue;
					}
				}
				
				if (!(QueryUtil.executeQuery(TvStudentRoster.class, null, 
						new FilterParams("studentId=:s1 and tvClass.id=:s2",
								new Object[]{value[0],tvClass.getId()}),domain.getId()).isEmpty())) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(". ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.student.id.db.check",new String[]{value[0],value[2]}))
					.append("\n");
					continue;
				}
				
				// check if the name exist in this file
				if (null != tvStudentRosterMap.get(key)) {
					for (TvStudentRoster oneClass : tvStudentRosterMap.get(key)) {
						if (value[0].equals(oneClass.getStudentId()) && value[2].equals(oneClass.getTvClass().getClassName())) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(". ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.student.id.repeat.check",new String[]{value[0],value[2]}))
							.append("\n");
							continue lineValue;
						}
					}
				}
				
				/*
				 * Get Description.
				 */
				if (value[3].length() >256){
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.description.length.check"))
						.append(value[3]).append("\n");
					continue;
				} 

				dto = new TvStudentRoster();
				dto.setStudentId(value[0]);
				dto.setStudentName(value[1]);
				dto.setTvClass(tvClass);
				dto.setDescription(value[3]);
				dto.setOwner(domain);

				if (null == tvStudentRosterMap.get(key)) {
					List<TvStudentRoster> vct_tvStudentRoster = new ArrayList<TvStudentRoster>();
					vct_tvStudentRoster.add(dto);
					tvStudentRosterMap.put(key, vct_tvStudentRoster);
				} else {
					tvStudentRosterMap.get(key).add(dto);
				}
			}
		} catch (Exception ex) {
			addActionError(ex.getMessage());
		}
	}
	
	/**
	 * Read the Tv  ResourceMap file and check the data.
	 */
	private void readTvResourceMap() {
		tvResourceMap = new HashMap<String, List<TvResourceMap>>();
		try {
			int intLine = 0;
			TvResourceMap dto;
			lineValue:
			for (String[] value : allvalue) {
				intLine++;
				int oldLine = intLine;
				boolean lineBool = false;
				// the value contains line-breaks
				for (String lineValue : value) {
					if (lineValue.contains("\r\n")) {
						intLine = intLine + lineValue.split("\r\n").length - 1;
						lineBool = true;
					}
				}
				if (lineBool) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(oldLine).append(" - ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.field.value.linebreaks.check").toLowerCase())
					.append("\n");
					continue;
				}
				if (!checkTheLineValue(value, intLine)) {
					continue;
				}
				HmDomain domain = thisDomain;
				boolean isDomain = false;
				boolean ifValidLength;
				if (getShowDomain()
						&& thisDomain != null
						&& HmDomain.HOME_DOMAIN.equals(thisDomain
								.getDomainName())) {
					ifValidLength = value.length > 4;
					isDomain = true;
				} else {
					ifValidLength = value.length > 3;
				}
				if (!ifValidLength) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.fields.num.check"))
						.append(value.length).append("\n");
					continue;
				}

				if (isDomain) {
					if (value[4].length() > 32) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.virtual.length.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
						.append(value[4]).append("\n");
						continue;
					} else if (!"".equals(value[4])) {
						domain = QueryUtil.findBoByAttribute(
								HmDomain.class, "domainName", value[4]);
						if (null == domain) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.virtual.db.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
							.append(value[4]).append("\n");
							continue;
						}
					}
				}
				// get the domain id
				String key = String.valueOf(domain.getId());

				/*
				 * check Resource
				 */
				if (value[0].length() < 1 || value[0].length() > 32) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.resource.name.length.check"))
						.append(value[0]).append("\n");
					continue;
				} else {
					String strResult = checkTheSpecialCharacter(value[0], SINGLE_STRING_LIMIT_WITH_BLANK);
					if (!"".equals(strResult)) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.resource.name.value.check"))
						.append(" '").append(strResult).append("' :: ").append(value[0]).append("\n");
						continue;
					} 
				}

				/*
				 * Get the Alias.
				 */
				if ("".equals(value[1])) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ip.address.length.check"))
						.append(value[1]).append("\n");
					continue;
				}
				if (getIpAddressWrongFlag(value[1].trim())){
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.ip.address.check"))
					.append(value[1]).append("\n");
					continue;
				}

				/*
				 * Get Port.
				 */
				if (value[2].length() <1 || value[2].length()>5){
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.port.length.check"))
					.append(value[2]).append("\n");
					continue;
				}
				if (!checkValue(value[2], 1, 65535)) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.port.length.check"))
						.append(value[2]).append("\n");
					continue;
				}
				
				if (!(QueryUtil.executeQuery(TvResourceMap.class, null, 
						new FilterParams("(resource=:s1 or (port=:s2 and alias=:s3))",
								new Object[]{value[0],Integer.parseInt(value[2]),value[1]}),domain.getId()).isEmpty())){
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(". ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.resource.name.db.check",new String[]{value[0],value[2],value[1]}))
					.append("\n");
					continue;

				}
				// check if the name exist in this file
				if (null != tvResourceMap.get(key)) {
					for (TvResourceMap oneClass : tvResourceMap.get(key)) {
						if (value[0].equals(oneClass.getResource())|| 
								(Integer.parseInt(value[2])==oneClass.getPort() && oneClass.getAlias().equals(value[1]))) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(". ")
							.append(MgrUtil.getUserMessage("hm.system.log.import.csv.resource.name.check",new String[]{value[0],value[2],value[1]}))
							.append("\n");
							continue lineValue;
						}
					}
				}

				/*
				 * Get Description.
				 */
				if (value[3].length() >256){
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.description.length.check"))
						.append(value[3]).append("\n");
					continue;
				} 

				dto = new TvResourceMap();
				dto.setResource(value[0]);
				dto.setAlias(value[1].trim());
				dto.setPort(Integer.parseInt(value[2]));
				dto.setDescription(value[3]);
				dto.setOwner(domain);

				if (null == tvResourceMap.get(key)) {
					List<TvResourceMap> vct_tvResourceMap = new ArrayList<TvResourceMap>();
					vct_tvResourceMap.add(dto);
					tvResourceMap.put(key, vct_tvResourceMap);
				} else {
					tvResourceMap.get(key).add(dto);
				}
			}
		} catch (Exception ex) {
			addActionError(ex.getMessage());
		}
	}
	
	/**
	 * Save the createTvClass file data in database.
	 * @return int : the total number of create successfully
	 */
	@SuppressWarnings("unchecked")
	private int createTvClass() {
		int successNumber = 0;
		try {
			List<TvClass> m_vct_tvClass;
			for (String key : tvClassMap.keySet()) {
				List<String> lstStuId = (List<String>)QueryUtil.executeQuery("select distinct className from " + TvClass.class.getSimpleName(),
						null,new FilterParams("owner.id",Long.parseLong(key)));
				m_vct_tvClass = tvClassMap.get(key);
				for(TvClass tc:m_vct_tvClass){
					lstStuId.add(tc.getClassName());
				}
				if (lstStuId.size()>TvClassAction.MAX_NUMBER_TV_CLASS){
					return -1;
				}
			}
			for (String key : tvClassMap.keySet()) {
				m_vct_tvClass = tvClassMap.get(key);
				if (null != m_vct_tvClass) {
					QueryUtil.bulkCreateBos(m_vct_tvClass);
					successNumber += m_vct_tvClass.size();
				}
			}
		} catch (Exception ex) {
			addActionError(ex.getMessage());
		}
		return successNumber;
	}
	
	/**
	 * Save the createTvComputerCart file data in database.
	 * @return int : the total number of create successfully
	 */
	private int createTvComputerCart() {
		int successNumber = 0;
		try {
			List<TvComputerCart> m_vct_tvComputerCart;
			for (String key : tvComputerCartMap.keySet()) {				
				String macCountSql = "select count(stumac) from tv_computer_cart_mac aa "
					 	+ "inner join tv_computer_cart bb on aa.tv_cart_id=bb.id and bb.owner="
						+ Long.parseLong(key);
				List<?> macCount = QueryUtil.executeNativeQuery(macCountSql);
				Long stuMacCount = Long.parseLong(macCount.get(0).toString());
				
				m_vct_tvComputerCart = tvComputerCartMap.get(key);
				for(TvComputerCart cart:m_vct_tvComputerCart){
					stuMacCount += cart.getItems().size();
				}
				if (stuMacCount > TvStudentRosterAction.MAX_NUMBER_TV_STUDENT){
					return -1;
				}
			}
			
			for (String key : tvComputerCartMap.keySet()) {
				m_vct_tvComputerCart = tvComputerCartMap.get(key);
				if (null != m_vct_tvComputerCart) {
					for(TvComputerCart cart: m_vct_tvComputerCart){
						sortLst(cart);
					}
					QueryUtil.bulkCreateBos(m_vct_tvComputerCart);
					successNumber += m_vct_tvComputerCart.size();
				}
			}
		} catch (Exception ex) {
			addActionError(ex.getMessage());
		}
		return successNumber;
	}
	
	private int createOneTimePassword(){
		int successNumber = 0;
		try {
			List<OneTimePassword> m_otp;
			for (String key : oneTimePasswordMap.keySet()) {
				m_otp = oneTimePasswordMap.get(key);
				if (null != m_otp) {
					QueryUtil.bulkCreateBos(m_otp);
					successNumber += m_otp.size();
				}
			}
		} catch (Exception ex) {
			addActionError(ex.getMessage());
		}
		return successNumber;
	}
	
	private void sortLst(TvComputerCart cart){
		if (cart!=null && !cart.getItems().isEmpty()){
			Collections.sort(cart.getItems(), new Comparator<TvComputerCartMacName>() {
				@Override
				public int compare(TvComputerCartMacName o1, TvComputerCartMacName o2) {
					return o1.getStuMac().compareToIgnoreCase(o2.getStuMac());
				}
			});
		}
	}
	
	/**
	 * Save the createTvStudentRoster file data in database.
	 * @return int : the total number of create successfully
	 */
	@SuppressWarnings("unchecked")
	private int createTvStudentRoster() {
		int successNumber = 0;
		try {
			List<TvStudentRoster> m_vct_tvStudentRoster;
			for (String key : tvStudentRosterMap.keySet()) {
				List<String> lstStuId = (List<String>)QueryUtil.executeQuery("select distinct studentId from " + TvStudentRoster.class.getSimpleName(),
						null,new FilterParams("owner.id",Long.parseLong(key)));
				m_vct_tvStudentRoster = tvStudentRosterMap.get(key);
				for(TvStudentRoster st:m_vct_tvStudentRoster){
					if (!lstStuId.contains(st.getStudentId())){
						lstStuId.add(st.getStudentId());
					}
				}
				if (lstStuId.size()>TvStudentRosterAction.MAX_NUMBER_TV_STUDENT){
					return -1;
				}
			}
			
			for (String key : tvStudentRosterMap.keySet()) {
				m_vct_tvStudentRoster = tvStudentRosterMap.get(key);
				if (null != m_vct_tvStudentRoster) {
					QueryUtil.bulkCreateBos(m_vct_tvStudentRoster);
					successNumber += m_vct_tvStudentRoster.size();
				}
			}
		} catch (Exception ex) {
			addActionError(ex.getMessage());
		}
		return successNumber;
	}
	
	/**
	 * Save the TvResourceMap file data in database.
	 * @return int : the total number of create successfully
	 */
	@SuppressWarnings("unchecked")
	private int createTvResourceMap() {
		int successNumber = 0;
		try {
			List<TvResourceMap> m_vct_tvResourceMap;
			for (String key : tvResourceMap.keySet()) {
				List<String> lstResourceId = (List<String>)QueryUtil.executeQuery("select resource from " + TvResourceMap.class.getSimpleName(),
						null,new FilterParams("owner.id",Long.parseLong(key)));
				m_vct_tvResourceMap = tvResourceMap.get(key);
				for(TvResourceMap rs:m_vct_tvResourceMap){
					lstResourceId.add(rs.getResource());
				}
				if (lstResourceId.size()>TvResourceMapAction.MAX_NUMBER_TV_RESOURCE){
					return -1;
				}
			}

			for (String key : tvResourceMap.keySet()) {
				m_vct_tvResourceMap = tvResourceMap.get(key);
				if (null != m_vct_tvResourceMap) {
					QueryUtil.bulkCreateBos(m_vct_tvResourceMap);
					successNumber += m_vct_tvResourceMap.size();
				}
			}
		} catch (Exception ex) {
			addActionError(ex.getMessage());
		}
		return successNumber;
	}
	
	public boolean checkFormatTimeForClass(String time){
		if (time.length()!=5 || time.indexOf(":")!=2){
			return false;
		}
		if (time.charAt(0)<'0' || time.charAt(0)>'2'){
			return false;
		}
		if (time.charAt(1)<'0' || time.charAt(1)>'9'){
			return false;
		}
		if (time.charAt(0)=='2' && time.charAt(1)>'3') {
			return false;
		}
		if (time.charAt(3)<'0' || time.charAt(3)>'5'){
			return false;
		}
		if (time.charAt(4)<'0' && time.charAt(4)>'5'){
			return false;
		}
		return true;
	}
	
	public boolean checkWeekDay(String day){
		if (day.length()!=7) {
			return false;
		}
		for(int k=0; k<7; k++) {
			if (day.charAt(k)!='0' && day.charAt(k)!='1') {
				return false;
			}
		}
		return true;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof MacFilter) {
			bo.getOwner().getId();
			if (((MacFilter)bo).getFilterInfo()!=null) {
				((MacFilter)bo).getFilterInfo().size();
			}
		}
		if (bo instanceof MacOrOui) {
			bo.getOwner().getId();
			if (((MacOrOui)bo).getItems()!=null) {
				((MacOrOui)bo).getItems().size();
			}
		}
		return null;
	}

//	public String getTvWeekDayString(String day){
//		if ("0".equals(day)){
//			return "Sunday";
//		}else if ("1".equals(day)){
//			return "Monday";
//		}else if ("2".equals(day)){
//			return "Tuesday";
//		}else if ("3".equals(day)){
//			return "Wednesday";
//		}else if ("4".equals(day)){
//			return "Thursday";
//		}else if ("5".equals(day)){
//			return "Friday";
//		}else if ("6".equals(day)){
//			return "Saturday";
//		} else {
//			return TvClassAction.MONDAY_TO_FRIDAY;
//		}
//	}
	
	/**
	 * For MAC, we support these formats: <br>
	 * 1- 000000000000<br> 
	 * 2- 00:00:00:00:00:00<br> 
	 * 3- 00-00-00-00-00-00<br> 
	 * 4- 000000-000000 <br>
	 * For OUI, we support these formats: <br>
	 * 1- 000000 <br>
	 * 2- 00:00:00 <br>
	 * 3- 00-00-00
	 * 
	 * @param macORoui -
	 * @return value without separator or any format; <p>if the param not in supported format, return <b>null</b>
	 */
	private String format2MACOUI(String macORoui) {
		final int MAC_LENGTH=12;
		final int OUI_LENGTH=6;
		final String colonSymbols=":";
		final String dashSymbols="-";
		if (StringUtils.isEmpty(macORoui)){
			return null;
		}
		
		int length = macORoui.length();
		if (MAC_LENGTH == length || OUI_LENGTH == length){
			//MAC_1 - 000000000000
			//OUI_1 - 000000
			if (isHex(macORoui))
				return macORoui;
			//"The MAC/OUI "+macORoui+" should only contains hex value."
			return null;
		} else if (OUI_LENGTH < length && length < MAC_LENGTH){
			boolean containsColon = StringUtils.contains(macORoui, colonSymbols);
			boolean containsDash = StringUtils.contains(macORoui, dashSymbols);
			if (containsColon && containsDash){
				//"The MAC "+macORoui+" is invalid format."
				return null;
			}
			String[] arrayStr=macORoui.split(containsColon?colonSymbols:dashSymbols);
			if (3 == arrayStr.length) {
				//OUI_2 - 00:00:00
				//OUI_3 - 00-00-00
				for (String element : arrayStr) {
					//if the sub string's length exceed 2 or not a hex value return null
					if (2 != element.length() || !isHex(element)){
						//"The OUI "+macORoui+" is invalid format."
						return null;
					}
				}
				return StringUtils.join(arrayStr);
			} else {
				//"The OUI "+macORoui+" is invalid format."
				return null;
			}
		}else if (MAC_LENGTH < length){
			boolean containsColon = StringUtils.contains(macORoui, colonSymbols);
			boolean containsDash = StringUtils.contains(macORoui, dashSymbols);
			if (containsColon && containsDash){
				//"The MAC "+macORoui+" is invalid format."
				return null;
			}
			String[] arrayStr=macORoui.split(containsColon?colonSymbols:dashSymbols);
			if (6 == arrayStr.length){
				//MAC_2 - 00:00:00:00:00:00
				//MAC_3 - 00-00-00-00-00-00
				for (String element : arrayStr) {
					//if the sub string's length exceed 2 or not a hex value return null
					if (2 != element.length() || !isHex(element)){
						//"The MAC "+macORoui+" is invalid format."
						return null;
					}
				}
				return StringUtils.join(arrayStr);
			} else if (2 == arrayStr.length && containsDash){
				//MAC_4 - 000000-000000
				if (6 != arrayStr[0].length() || 6 != arrayStr[1].length()){
					//"The MAC "+macORoui+" is invalid format."
					return null;
				}
				if (isHex(arrayStr[0]) && isHex(arrayStr[1])){
					return StringUtils.join(arrayStr);
				}else{
					//"The MAC "+macORoui+" is invalid format."
					return null;
				}
			}else{
				//after split the length isn't equal 6 or 2, return null
				//"The MAC "+macORoui+" is invalid format."
			}
		}else{
			//"Unknown return...input is:"+macORoui
		}
		return null;
	}
	
	/**
	 * Read the  TvComputerCart file and check the data.
	 */
	private void readAndCheckOneTimePasswords() {
		oneTimePasswordMap = new HashMap<String, List<OneTimePassword>>();
		try {
			int intLine = 0;
			OneTimePassword otp;
			for (String[] value : allvalue) {
				intLine++;
				int oldLine = intLine;
				boolean lineBool = false;
				// the value contains line-breaks
				for (String lineValue : value) {
					if (lineValue.contains("\r\n")) {
						intLine = intLine + lineValue.split("\r\n").length - 1;
						lineBool = true;
					}
				}
				if (lineBool) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(oldLine).append(" - ").append(intLine).append(" ").
					append(MgrUtil.getUserMessage("hm.system.log.import.csv.field.value.linebreaks.check").toLowerCase())
						.append("\n");
					continue;
				}
				if (!checkTheLineValue(value, intLine)) {
					continue;
				}
				HmDomain domain = thisDomain;
				HiveApAutoProvision apAutoProvision = null;
				boolean isDomain = false;
				boolean ifValidLength;
				if (getShowDomain()
						&& thisDomain != null
						&& HmDomain.HOME_DOMAIN.equals(thisDomain
								.getDomainName())) {
					if(value.length == 5){
						ifValidLength = true;
						isDomain = true;
					}else{
						ifValidLength = false;
					}
				} else {
					if(value.length == 4){
						ifValidLength = true;
					}else{
						ifValidLength = false;
					}
				}
				if (!ifValidLength) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").append(
							MgrUtil.getUserMessage("hm.system.log.import.csv.fields.num.check"))
						.append(value.length).append("\n");
					continue;
				}

				if (isDomain) {
					if (value[4].length() > 32) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").
						append(MgrUtil.getUserMessage("hm.system.log.import.csv.virtual.length.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
							.append(value[2]).append("\n");
						continue;
					} else if (!"".equals(value[4])) {
						domain = QueryUtil.findBoByAttribute(
								HmDomain.class, "domainName", value[4]);
						if (null == domain) {
							result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").
							append(MgrUtil.getUserMessage("hm.system.log.import.csv.virtual.db.check",NmsUtil.getOEMCustomer().getNmsNameAbbreviation()))
								.append(value[4]).append("\n");
							continue;
						}
					}
				}
				
				/*
				 * Get Auto Provisioning.
				 */
				if (value[3].length() >32){
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.auto.provise.length.check"))
						.append(value[3]).append("\n");
					continue;
				}else if(!"".equals(value[3])){
					apAutoProvision =  QueryUtil.findBoByAttribute(
							HiveApAutoProvision.class, "name", value[3],domain.getId());
					if (null == apAutoProvision) {
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ").
						append(MgrUtil.getUserMessage("hm.system.log.import.csv.auto.provise.check")).append(value[3]).append("\n");
						continue;
					}
				} 
				
				// get the domain id
				String key = String.valueOf(domain.getId());

				/*
				 * check password
				 */
				if ((value[0].length() > 0 && value[0].length() != 24) || value[0].length() > 24) {
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.password.length.check"))
						.append(value[0]).append("\n");
					continue;
				}
				if(value[0].length() == 0){
					value[0] = OneTimePasswordAction.generateOneTimePassword();
				}
				
				//validate if have the same password in the database
				List<?> resList = QueryUtil.executeQuery("select oneTimePassword from "+OneTimePassword.class.getSimpleName(), 
						null, new FilterParams("oneTimePassword", value[0]));
				if(!resList.isEmpty()){
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.password.same.db.check"))
					.append(value[0]).append("\n");
					continue;
				}
				
				if(null != oneTimePasswordMap.get(key)){
					boolean exist = false;
					for(OneTimePassword temp: oneTimePasswordMap.get(key)){
						if(value[0].equals(temp.getOneTimePassword())){
							exist = true;
							break;
						}
					}
					if(exist){
						result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.password.same.check"))
						.append(value[0]).append("\n");
						continue;
					}
				}
			
				if(value[1].length() == 0 && value[2].length() == 0){
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.failure")).append(". ")
					.append(MgrUtil.getUserMessage("hm.system.log.import.csv.username.email.check"))
					.append("\n");
					continue;
				}
				
				/*
				 * Get User name.
				 */
				if (value[1].length() >32){
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.username.check"))
						.append(value[1]).append("\n");
					continue;
				} 
				
				/*
				 * Get Email Address.
				 */
				if (value[2].length() >128){
					result.append(MgrUtil.getUserMessage("hm.system.log.import.csv.file.check")).append(" ").append(intLine).append(" ")
						.append(MgrUtil.getUserMessage("hm.system.log.import.csv.email.check"))
						.append(value[2]).append("\n");
					continue;
				} 

				otp = new OneTimePassword();
				otp.setOneTimePassword(value[0]);
				otp.setUserName(value[1]);
				otp.setEmailAddress(value[2]);
				otp.setHiveApAutoProvision(apAutoProvision);
				otp.setOwner(domain);
				
				if (null == oneTimePasswordMap.get(key)) {
					List<OneTimePassword> otpTemp = new ArrayList<OneTimePassword>();
					otpTemp.add(otp);
					oneTimePasswordMap.put(key, otpTemp);
				} else {
					oneTimePasswordMap.get(key).add(otp);
				}
			}
		} catch (Exception ex) {
			addActionError(ex.getMessage());
		}
	}

	public String getDiMenuTypeKey() {
		return diMenuTypeKey;
	}

	public void setDiMenuTypeKey(String diMenuTypeKey) {
		this.diMenuTypeKey = diMenuTypeKey;
	}

}
