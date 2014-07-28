package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.app.HmBeParaUtil;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApAutoProvision;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.wlan.RadioProfile;

public class RestoreProvisionConfig {

	/**
	 * This function is used to restore Auto provision configuration lower than
	 * 3.4.0.0, with the higher version, please use
	 * <b>RestoreHiveApAutoProvision.restoreHiveApAutoProvision()</b> instead.
	 * 
	 * @return -
	 */
	public static boolean restoreProvisionConfig() {
		try {
			long start = System.currentTimeMillis();
			List<HiveApAutoProvision> list = getAllProvisionConfig();
			if (null != list && !list.isEmpty()) {
				QueryUtil.bulkCreateBos(list);
			}
			long end = System.currentTimeMillis();
			AhRestoreDBTools
					.logRestoreMsg("Restore HiveAP autoprovisio completely(Data old than 3.4.0.0), count:"
							+ (list == null ? 0 : list.size())
							+ ". cost:"
							+ (end - start) + " ms.");
			return true;
		} catch (Exception e) {
			AhRestoreDBTools
					.logRestoreMsg(
							"Restore HiveAP autoprovision error(Data old than 3.4.0.0).",
							e);
			return false;
		}
	}

	private static Map<String, List<String>> getAllProvisionConfigHiveAps()
			throws AhRestoreException, AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of auto_provisioning_config_maces.xml
		 */
		boolean restoreRet = xmlParser
				.readXMLFile("auto_provisioning_config_maces");
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, List<String>> macAddressInfo = new HashMap<String, List<String>>();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			/**
			 * Set auto_provisioning_config_id
			 */
			colName = "auto_provisioning_config_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"auto_provisioning_config_maces", colName);
			if (!isColPresent) {
				/**
				 * The auto_provisioning_config_id column must be exist in the
				 * table of auto_provisioning_config_maces
				 */
				continue;
			}

			String auto_provisioning_config_id = xmlParser
					.getColVal(i, colName);
			if (auto_provisioning_config_id == null
					|| auto_provisioning_config_id.trim().equals("")
					|| auto_provisioning_config_id.trim().equalsIgnoreCase(
							"null")) {
				continue;
			}

			/**
			 * Set macaddress
			 */
			colName = "macaddress";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"auto_provisioning_config_maces", colName);
			if (!isColPresent) {
				/**
				 * The macaddress column must be exist in the table of
				 * auto_provisioning_config_maces
				 */
				continue;
			}

			String macaddress = xmlParser.getColVal(i, colName);
			if (macaddress == null || macaddress.trim().equals("")
					|| macaddress.trim().equalsIgnoreCase("null")) {
				continue;
			}

			if (macAddressInfo.get(auto_provisioning_config_id) == null) {
				List<String> macAddresses = new ArrayList<String>();
				macAddresses.add(macaddress);
				macAddressInfo.put(auto_provisioning_config_id, macAddresses);
			} else {
				macAddressInfo.get(auto_provisioning_config_id).add(macaddress);
			}
		}
		return macAddressInfo;
	}

	private static List<HiveApAutoProvision> getAllProvisionConfig()
			throws AhRestoreException {
		Map<String, List<String>> macAddresses = null;

		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of auto_provisioning_config.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("auto_provisioning_config");
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read auto_provisioning_config.xml file.");
			return null;
		}

		/**
		 * No one row data stored in auto_provisioning_config table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<HiveApAutoProvision> configs = new ArrayList<HiveApAutoProvision>();

		if (rowCount <= 0) {
			return configs;
		}

		// fetch the child table afte make sure the parent table has data.
		try {
			macAddresses = getAllProvisionConfigHiveAps();
		} catch (Exception e1) {
			AhRestoreDBTools.logRestoreMsg(
					"get provision config macAddresses error", e1);
		}

		boolean isColPresent;
		String colName;
		HiveApAutoProvision config_ag20;
		HiveApAutoProvision config_11n;
		for (int i = 0; i < rowCount; i++) {
			try {
				config_ag20 = new HiveApAutoProvision();
				config_11n = new HiveApAutoProvision();
				config_ag20.setModelType(HiveAp.HIVEAP_MODEL_20);
				config_11n.setModelType(HiveAp.HIVEAP_MODEL_340);

				/**
				 * Set autoprovisioning
				 */
				colName = "autoprovisioning";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"auto_provisioning_config", colName);
				String autoprovisioning = (isColPresent ? xmlParser.getColVal(
						i, colName) : "false");
				boolean autoProvision = AhRestoreCommons
						.convertStringToBoolean(autoprovisioning);
				config_ag20.setAutoProvision(autoProvision);
				config_11n.setAutoProvision(autoProvision);

				/**
				 * Set ID
				 */
				colName = "id";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"auto_provisioning_config", colName);
				if (!isColPresent) {
					/**
					 * The id column must be exist in the table of
					 * auto_provisioning_config
					 */
					continue;
				}
				String id = isColPresent ? xmlParser.getColVal(i, colName)
						: "1";

				/**
				 * Set acltype
				 */
				colName = "acltype";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"auto_provisioning_config", colName);
				int acltype = (isColPresent ? AhRestoreCommons
						.convertInt(xmlParser.getColVal(i, colName))
						: HiveApAutoProvision.ACL_MANUAL_AP);

				config_ag20.setAclType((short) acltype);
				config_11n.setAclType((short) acltype);

				if (null != macAddresses && null != macAddresses.get(id)
						&& !macAddresses.get(id).isEmpty()) {
					List<String> items = macAddresses.get(id);
					if (acltype == HiveApAutoProvision.ACL_IMPORT_SN) {
						config_ag20
								.setMacAddresses(new ArrayList<String>(items));
						config_11n
								.setMacAddresses(new ArrayList<String>(items));
						config_ag20.setAccessControled(true);
						config_11n.setAccessControled(true);
					} else {
						List<?> list = QueryUtil.executeQuery(
								"select hiveApModel, macAddress from "
										+ HiveAp.class.getSimpleName(), null,
								new FilterParams("macAddress", macAddresses
										.get(id)));
						if (!list.isEmpty()) {
							List<String> ag20 = new ArrayList<String>();
							List<String> _11n = new ArrayList<String>();
							for (Object object : list) {
								Object[] attrs = (Object[]) object;
								Short model = (Short) attrs[0];
								String mac = (String) attrs[1];
								if (model != null) {
									if (model == HiveAp.HIVEAP_MODEL_340) {
										_11n.add(mac);
									} else if (model == HiveAp.HIVEAP_MODEL_20) {
										ag20.add(mac);
									}
								}
							}
							if (!ag20.isEmpty()) {
								config_ag20.setMacAddresses(ag20);
								config_ag20.setAccessControled(true);
							} else {
								// set auto provision to false if access control
								// is roll back to false;
								config_ag20.setAutoProvision(false);
							}
							if (!_11n.isEmpty()) {
								config_11n.setMacAddresses(_11n);
								config_11n.setAccessControled(true);
							} else {
								// set auto provision to false if access control
								// is roll back to false;
								config_11n.setAutoProvision(false);
							}
						}
					}
				}

				/**
				 * Set imagename
				 */
				colName = "imagename";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"auto_provisioning_config", colName);
				String imagename = isColPresent ? AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)) : "";
				config_ag20.setImageName(imagename.trim());

				/**
				 * Set imageversion
				 */
				colName = "imageversion";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"auto_provisioning_config", colName);
				String imageversion = isColPresent ? AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)) : "";
				config_ag20.setImageVersion(imageversion.trim());

				/**
				 * Set imagename_11n
				 */
				colName = "imagename_11n";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"auto_provisioning_config", colName);
				String imagename_11n = isColPresent ? AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)) : "";
				config_11n.setImageName(imagename_11n);

				/**
				 * Set imageversion_11n
				 */
				colName = "imageversion_11n";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"auto_provisioning_config", colName);
				String imageversion_11n = isColPresent ? AhRestoreCommons
						.convertString(xmlParser.getColVal(i, colName)) : "";
				config_11n.setImageVersion(imageversion_11n.trim());

				/**
				 * Set nativevlan
				 */
				// colName = "nativevlan";
				// isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				// "auto_provisioning_config", colName);
				// int nativevlan = (isColPresent ? AhRestoreCommons
				// .convertInt(xmlParser.getColVal(i, colName)) : 1);
				// config.setNativeVlan(nativevlan > 4094 ? 4094 : nativevlan);
				/**
				 * Set provisioningname
				 */
				config_ag20
						.setProvisioningName(HiveApAutoProvision.getProvisionName(HiveAp.HIVEAP_MODEL_20, HiveAp.Device_TYPE_HIVEAP));
				config_11n
						.setProvisioningName(HiveApAutoProvision.getProvisionName(HiveAp.HIVEAP_MODEL_340, HiveAp.Device_TYPE_HIVEAP));
				// colName = "provisioningname";
				// isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				// "auto_provisioning_config", colName);
				// String provisioningname = isColPresent ? AhRestoreCommons
				// .convertString(xmlParser.getColVal(i, colName)) : "";
				// config.setProvisioningName(provisioningname.trim());

				/**
				 * Set rewritemap
				 */
				colName = "rewritemap";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"auto_provisioning_config", colName);
				String rewritemap = (isColPresent ? xmlParser.getColVal(i,
						colName) : "false");
				boolean b_rewritemap = AhRestoreCommons
						.convertStringToBoolean(rewritemap);
				config_ag20.setRewriteMap(b_rewritemap);
				config_11n.setRewriteMap(b_rewritemap);

				/**
				 * Set uploadconfig
				 */
				colName = "uploadconfig";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"auto_provisioning_config", colName);
				String uploadconfig = (isColPresent ? xmlParser.getColVal(i,
						colName) : "false");
				boolean b_uploadconfig = AhRestoreCommons
						.convertStringToBoolean(uploadconfig);
				config_ag20.setUploadConfig(b_uploadconfig);
				config_11n.setUploadConfig(b_uploadconfig);

				/**
				 * Set uploadimage
				 */
				colName = "uploadimage";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"auto_provisioning_config", colName);
				String uploadimage = (isColPresent ? xmlParser.getColVal(i,
						colName) : "false");
				boolean b_uploadimage = AhRestoreCommons
						.convertStringToBoolean(uploadimage);
				config_ag20.setUploadImage(b_uploadimage);
				config_11n.setUploadImage(b_uploadimage);

				/**
				 * Set rebooting
				 */
				colName = "rebooting";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"auto_provisioning_config", colName);
				String rebooting = (isColPresent ? xmlParser.getColVal(i,
						colName) : "false");
				boolean b_rebooting = AhRestoreCommons
						.convertStringToBoolean(rebooting);
				config_ag20.setRebooting(b_rebooting);
				config_11n.setRebooting(b_rebooting);

				/**
				 * Set configtemplateid
				 */
				colName = "configtemplateid";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"auto_provisioning_config", colName);
				String config_template_id = isColPresent ? AhRestoreCommons.convertString(xmlParser
						.getColVal(i, colName)) : "-1";
				if (!"".equals(config_template_id)) {
					Long config_template_id_new = AhRestoreNewMapTools
							.getMapConfigTemplate(AhRestoreCommons
									.convertLong(config_template_id));
					if (null != config_template_id_new) {
						config_ag20.setConfigTemplateId(config_template_id_new);
						config_11n.setConfigTemplateId(config_template_id_new);
					} else {
						AhRestoreDBTools
								.logRestoreMsg("Cound not find the new config template id mapping to old id:"
										+ config_template_id);
						ConfigTemplate template = HmBeParaUtil.getDefaultTemplate();
						if (null != template) {
							config_ag20.setConfigTemplateId(template.getId());
							config_11n.setConfigTemplateId(template.getId());
						}
					}
				} else {
					ConfigTemplate template = HmBeParaUtil.getDefaultTemplate();
					if (null != template) {
						config_ag20.setConfigTemplateId(template.getId());
						config_11n.setConfigTemplateId(template.getId());
					}
				}
				
				/**
				 * Set mapcontainerid
				 */
				colName = "mapcontainerid";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"auto_provisioning_config", colName);
				String map_container_id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(
						i, colName)) : "-1";
				if (!"".equals(map_container_id)) {
					Long map_container_id_new = AhRestoreNewMapTools
							.getMapMapContainer(AhRestoreCommons.convertLong(map_container_id));
					if (null != map_container_id) {
						config_ag20.setMapContainerId(map_container_id_new);
						config_11n.setMapContainerId(map_container_id_new);
					} else {
						AhRestoreDBTools
								.logRestoreMsg("Cound not find the new map container id mapping to old id:"
										+ map_container_id);
					}
				}

				/**
				 * Set wifiaprofileid
				 */
				colName = "wifiaprofileid";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"auto_provisioning_config", colName);
				String wifi_a_profile_id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(
						i, colName)) : "-1";
				if (!"".equals(wifi_a_profile_id)) {
					Long wifi_a_profile_id_new = AhRestoreNewMapTools
							.getMapRadioProfile(AhRestoreCommons
									.convertLong(wifi_a_profile_id));
					if (null != wifi_a_profile_id_new) {
						config_ag20.setWifi1ProfileId(wifi_a_profile_id_new);
					} else {
						AhRestoreDBTools
								.logRestoreMsg("Cound not find the new radio profile id mapping to old id:"
										+ wifi_a_profile_id);
						RadioProfile profile = HmBeParaUtil.getDefaultRadioAProfile();
						if (null != profile) {
							config_ag20.setWifi1ProfileId(profile.getId());
						}
					}
				} else {
					RadioProfile profile = HmBeParaUtil.getDefaultRadioAProfile();
					if (null != profile) {
						config_ag20.setWifi1ProfileId(profile.getId());
					}
				}

				/**
				 * Set wifibgprofileid
				 */
				colName = "wifibgprofileid";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"auto_provisioning_config", colName);
				String wifi_bg_profile_id = isColPresent ? AhRestoreCommons.convertString(xmlParser
						.getColVal(i, colName)) : "-1";
				if (!"".equals(wifi_bg_profile_id)) {
					Long wifi_bg_profile_id_new = AhRestoreNewMapTools
							.getMapRadioProfile(AhRestoreCommons
									.convertLong(wifi_bg_profile_id));
					if (null != wifi_bg_profile_id_new) {
						config_ag20.setWifi0ProfileId(wifi_bg_profile_id_new);
					} else {
						AhRestoreDBTools
								.logRestoreMsg("Cound not find the new radio profile id mapping to old id:"
										+ wifi_bg_profile_id);
						RadioProfile profile = HmBeParaUtil.getDefaultRadioBGProfile();
						if (null != profile) {
							config_ag20.setWifi0ProfileId(profile.getId());
						}
					}
				} else {
					RadioProfile profile = HmBeParaUtil.getDefaultRadioBGProfile();
					if (null != profile) {
						config_ag20.setWifi0ProfileId(profile.getId());
					}
				}

				/**
				 * Set wifinaprofileid
				 */
				colName = "wifinaprofileid";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"auto_provisioning_config", colName);
				String wifi_na_profile_id = isColPresent ? AhRestoreCommons.convertString(xmlParser
						.getColVal(i, colName)) : "-1";
				if (!"".equals(wifi_na_profile_id)) {
					Long wifi_na_profile_id_new = AhRestoreNewMapTools
							.getMapRadioProfile(AhRestoreCommons
									.convertLong(wifi_na_profile_id));
					if (null != wifi_na_profile_id_new) {
						config_11n.setWifi1ProfileId(wifi_na_profile_id_new);
					} else {
						AhRestoreDBTools
								.logRestoreMsg("Cound not find the new radio profile id mapping to old id:"
										+ wifi_na_profile_id);
						RadioProfile profile = HmBeParaUtil.getDefaultRadioNAProfile();
						if (null != profile) {
							config_11n.setWifi1ProfileId(profile.getId());
						}
					}
				} else {
					RadioProfile profile = HmBeParaUtil.getDefaultRadioNAProfile();
					if (null != profile) {
						config_11n.setWifi1ProfileId(profile.getId());
					}
				}

				/**
				 * Set wifingprofileid
				 */
				colName = "wifingprofileid";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"auto_provisioning_config", colName);
				String wifi_ng_profile_id = isColPresent ? AhRestoreCommons.convertString(xmlParser
						.getColVal(i, colName)) : "-1";
				if (!"".equals(wifi_ng_profile_id)) {
					Long wifi_ng_profile_id_new = AhRestoreNewMapTools
							.getMapRadioProfile(AhRestoreCommons
									.convertLong(wifi_ng_profile_id));
		
					if (null != wifi_ng_profile_id_new) {
						config_11n.setWifi0ProfileId(wifi_ng_profile_id_new);
					} else {
						AhRestoreDBTools
								.logRestoreMsg("Cound not find the new radio profile id mapping to old id:"
										+ wifi_ng_profile_id);
						RadioProfile profile = HmBeParaUtil.getDefaultRadioNGProfile();
						if (null != profile) {
							config_11n.setWifi0ProfileId(profile.getId());
						}
					}
				} else {
					RadioProfile profile = HmBeParaUtil.getDefaultRadioNGProfile();
					if (null != profile) {
						config_11n.setWifi0ProfileId(profile.getId());
					}
				}
				
				/**
				 * Set owner
				 */
				colName = "owner";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"auto_provisioning_config", colName);
				long ownerId = isColPresent ? AhRestoreCommons
						.convertLong(xmlParser.getColVal(i, colName)) : 1;
						
				if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
				{
					continue;
				}
				HmDomain ownerDomain = AhRestoreNewMapTools
						.getHmDomain(ownerId);
				config_ag20.setOwner(ownerDomain);
				config_11n.setOwner(ownerDomain);

				configs.add(config_ag20);
				configs.add(config_11n);
			} catch (Exception e) {
				AhRestoreDBTools.logRestoreMsg("get provision config", e);
			}
		}

		return configs.size() > 0 ? configs : null;
	}

}