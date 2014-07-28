package com.ah.ui.actions.monitor.enrolledclient.tools;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;

import com.ah.be.common.ConfigUtil;
import com.ah.be.rest.client.models.ResponseModel;
import com.ah.be.rest.client.utils.HttpToolkit;
import com.ah.bo.mgmt.SortParams;
import com.ah.util.Tracer;

public class ResponseModelServiceImpl implements ResponseModelService{
	
	private static final Tracer log = new Tracer(ResponseModelServiceImpl.class.getSimpleName());
	
	public static final String ENROLLED_CLIENT_SORT_ASC = "asc";
	
	public static final String 	ENROLLED_CLIENT_SORT_DESC = "desc";
	
	private HttpClient client = new HttpClient();
	
	private ResponseModel res;
	
	public ResponseModel getDeviceListAll(String customId,String pageNum, String pageSize,
			String status,String ownerType, String osType, String active,SortParams sortParam){
		String direction;
		try{
			if(sortParam.isAscending()){
				direction = ENROLLED_CLIENT_SORT_ASC;
			}else{
				direction = ENROLLED_CLIENT_SORT_DESC;
			}
			client = new HttpClient();
			res= HttpToolkit.doPostXML(ConfigUtil.getACMConfigServerUrl() + URLUtils.REST_URL + URLUtils.DEVICE_CLIENTS_LIST_APPENDER, 
					new TransObjectToXMLImpl().
					getDeviceListPostStr(customId,pageNum, pageSize,status,ownerType, osType, active,getSortParamList(direction,sortParam.getOrderBy().split(","))),
					client);
			if(HttpStatus.SC_OK == res.getResponseCode()){
				log.info(ResponseModelServiceImpl.class.getSimpleName()+":getDeviceList", "Succeed in getting device list");
				return res;
			}
			if(HttpStatus.SC_SERVICE_UNAVAILABLE == res.getResponseCode()){
				log.error("Can't find the server");
				return null;
			}
			if(400 == res.getResponseCode()){
				log.error("No authentication to do this operation,please get the permission first");
				return res;
			}
			else{
				log.error(ResponseModelServiceImpl.class.getSimpleName()+":getDeviceList()", "Failed in getting device list");
				return null;
			}
		}catch(Exception e){
			log.error(ResponseModelServiceImpl.class.getSimpleName() + ":getDeviceListAll()", "Connect serve exception", e);
			return null;
		}
	}

	@Override
	public ResponseModel getDeviceDetail(String customId, String deviceId) {
		try {
			client = new HttpClient();
			ResponseModel res = HttpToolkit.doPostXML(
					ConfigUtil.getACMConfigServerUrl() + URLUtils.REST_URL + URLUtils.DEVICE_DETAILS_INFO_APPENDER + deviceId,
					new TransObjectToXMLImpl().getDeviceInfoPostStr(customId),
					client);
			if (res.getResponseCode() == HttpStatus.SC_OK) {
				log.info(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getDeviceDetail()",
						"Succeed in getting detail information");
				return res;
			} else {
				log.error(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getDeviceDetail()",
						"Failed in getting detail information");
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}
	}

	@Override
	public ResponseModel getRestrictionInfo(String customId, String deviceId) {
		try {
			client = new HttpClient();
			ResponseModel res = HttpToolkit.doPostXML(
					ConfigUtil.getACMConfigServerUrl() + URLUtils.REST_URL + URLUtils.DEVICE_RESTRICTIONS_INFO_APPENDER
							+ deviceId, new TransObjectToXMLImpl()
							.getRestrictionInfoPostStr(customId), client);
			if (res.getResponseCode() == HttpStatus.SC_OK) {
				log.info(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getRestrictionInfo() method succeed");
				return res;
			} else {
				log.error(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getRestrictionInfo() method failed");
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}

	}

	@Override
	public ResponseModel getApplicationInfo(String customId, String deviceId) {
		try {
			client = new HttpClient();
			ResponseModel res = HttpToolkit.doPostXML(
					ConfigUtil.getACMConfigServerUrl() + URLUtils.REST_URL + URLUtils.DEVICE_APP_LIST_APPENDER + deviceId,
					new TransObjectToXMLImpl().getAppListPostStr(customId, "0",
							"10000"), client);
			if (res.getResponseCode() == HttpStatus.SC_OK) {
				log.info(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getApplicationInfo() method succeed");
				return res;
			} else {
				log.error(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getApplicationInfo() method failed");
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}

	}
	
	@Override
	public ResponseModel getActiveClientListEnrolledInfo(String customId, String vhmId, List<String> clientMacList) {
		try {
			client = new HttpClient();
			ResponseModel res = HttpToolkit.doPostXML4ACMDefaultHead(
					ConfigUtil.getACMConfigServerUrl() + URLUtils.REST_CM_HM_URL + URLUtils.DEVICE_CLIENTS_ENROLLEDLIST_APPENDER,
					new TransObjectToXMLImpl().getActiveClientListEnrolledPostStr(customId, vhmId,
							clientMacList), client);
			if (res.getResponseCode() == HttpStatus.SC_OK) {
				log.info(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getActiveClientListEnrolledInfo() method succeed");
				return res;
			} else {
				log.error(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getActiveClientListEnrolledInfo() method failed");
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}
	}
	
	@Override
	public ResponseModel getActiveClientDetailInfo(String customId, String vhmId, String macAddress) {
		try {
			client = new HttpClient();
			ResponseModel res = HttpToolkit.doPostXML4ACMDefaultHead(
					ConfigUtil.getACMConfigServerUrl() + URLUtils.REST_CM_HM_URL + URLUtils.DEVICE_CLIENT_DETAIL_APPENDER,
					new TransObjectToXMLImpl().getActiveClientDetailPostStr(customId, vhmId,
							macAddress), client);
			if (res.getResponseCode() == HttpStatus.SC_OK) {
				log.info(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getActiveClientDetailInfo() method succeed");
				return res;
			} else {
				log.error(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getActiveClientDetailInfo() method failed");
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}
	}
	
	@Override
	public ResponseModel getActiveClientNetworkInfo(String customId, String vhmId, String macAddress) {
		try {
			client = new HttpClient();
			ResponseModel res = HttpToolkit.doPostXML4ACMDefaultHead(
					ConfigUtil.getACMConfigServerUrl() + URLUtils.REST_CM_HM_URL + URLUtils.DEVICE_CLIENT_NETWORK_APPENDER,
					new TransObjectToXMLImpl().getActiveClientNetworkPostStr(customId, vhmId,
							macAddress), client);
			if (res.getResponseCode() == HttpStatus.SC_OK) {
				log.info(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getActiveClientNetworkInfo() method succeed");
				return res;
			} else {
				log.error(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getActiveClientNetworkInfo() method failed");
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}
	}
	
	@Override
	public ResponseModel getActiveClientScanResultInfo(String customId, String vhmId, String macAddress, int limit) {
		try {
			client = new HttpClient();
			ResponseModel res = HttpToolkit.doPostXML4ACMDefaultHead(
					ConfigUtil.getACMConfigServerUrl() + URLUtils.REST_CM_HM_URL + URLUtils.DEVICE_CLIENT_SCANRESULT_APPENDER,
					new TransObjectToXMLImpl().getActiveClientScanResultPostStr(customId, vhmId,
							macAddress, limit), client);
			if (res.getResponseCode() == HttpStatus.SC_OK) {
				log.info(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getActiveClientScanResultInfo() method succeed");
				return res;
			} else {
				log.error(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getActiveClientScanResultInfo() method failed");
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}
	}
	@Override
	public ResponseModel getActiveClientScanResultInfo(String customId, String vhmId, String macAddress) {
		return getActiveClientScanResultInfo(customId, vhmId, macAddress, 0);
	}
	
	@Override
	public ResponseModel getActiveClientProfileInfo(String customId, String vhmId, String macAddress, int limit) {
		try {
			client = new HttpClient();
			ResponseModel res = HttpToolkit.doPostXML4ACMDefaultHead(
					ConfigUtil.getACMConfigServerUrl() + URLUtils.REST_CM_HM_URL + URLUtils.DEVICE_CLIENT_PROFILE_APPENDER,
					new TransObjectToXMLImpl().getActiveClientProfilePostStr(customId, vhmId,
							macAddress, limit), client);
			if (res.getResponseCode() == HttpStatus.SC_OK) {
				log.info(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getActiveClientProfileInfo() method succeed");
				return res;
			} else {
				log.error(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getActiveClientProfileInfo() method failed");
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}
	}
	@Override
	public ResponseModel getActiveClientProfileInfo(String customId, String vhmId, String macAddress) {
		return getActiveClientProfileInfo(customId, vhmId, macAddress, 0);
	}
	
	@Override
	public ResponseModel getActiveClientCertificateInfo(String customId, String vhmId, String macAddress, int limit) {
		try {
			client = new HttpClient();
			ResponseModel res = HttpToolkit.doPostXML4ACMDefaultHead(
					ConfigUtil.getACMConfigServerUrl() + URLUtils.REST_CM_HM_URL + URLUtils.DEVICE_CLIENT_CERT_APPENDER,
					new TransObjectToXMLImpl().getActiveClientCertificatePostStr(customId, vhmId,
							macAddress, limit), client);
			if (res.getResponseCode() == HttpStatus.SC_OK) {
				log.info(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getActiveClientCertificateInfo() method succeed");
				return res;
			} else {
				log.error(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getActiveClientCertificateInfo() method failed");
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}
	}
	
	@Override
	public ResponseModel getActiveClientCertificateInfo(String customId, String vhmId, String macAddress) {
		return getActiveClientCertificateInfo(customId, vhmId, macAddress, 0);
	}
	
	@Override
	public ResponseModel getActiveClientActivityLogInfo(String customId, String vhmId, String macAddress, int limit) {
		try {
			client = new HttpClient();
			ResponseModel res = HttpToolkit.doPostXML4ACMDefaultHead(
					ConfigUtil.getACMConfigServerUrl() + URLUtils.REST_CM_HM_URL + URLUtils.DEVICE_CLIENT_LOGS_APPENDER,
					new TransObjectToXMLImpl().getActiveClientActivityLogPostStr(customId, vhmId,
							macAddress, limit), client);
			if (res.getResponseCode() == HttpStatus.SC_OK) {
				log.info(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getActiveClientActivityLogInfo() method succeed");
				return res;
			} else {
				log.error(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getActiveClientActivityLogInfo() method failed");
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}
	}
	
	@Override
	public ResponseModel getActiveClientActivityLogInfo(String customId, String vhmId, String macAddress) {
		return getActiveClientActivityLogInfo(customId, vhmId, macAddress, 0);
	}

	@Override
	public ResponseModel getNetworkInfo(String customId, String deviceId) {
		try {
			client = new HttpClient();
			ResponseModel res = HttpToolkit.doPostXML(
					ConfigUtil.getACMConfigServerUrl() + URLUtils.REST_URL + URLUtils.DEVICE_NETWORK_INFO_APPENDER + deviceId,
					new TransObjectToXMLImpl().getNetworkInfoPostStr(customId),
					client);
			if (res.getResponseCode() == HttpStatus.SC_OK) {
				log.info(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getNetworkInfo() method succeed");
				return res;
			} else {
				log.error(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getNetworkInfo() method failed");
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}

	}
	@Override
	public ResponseModel getActiveClientEnrolledStatus(String customId,
			String macAddress) {
		try{
			client = new HttpClient();
			ResponseModel res = HttpToolkit.doPostXML(ConfigUtil.getACMConfigServerUrl()+URLUtils.REST_URL+URLUtils.ACTIVE_CLIENT_ENROLLED_APPENDER + "/"+macAddress,
					new TransObjectToXMLImpl().getActiveClientEnrolledPostStr(customId,""), client);
			if(res.getResponseCode() == HttpStatus.SC_OK){
				log.info(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getActiveClientEnrolledStatus() method succeed");
				return res;
			}
			if(res.getResponseCode() == HttpStatus.SC_NOT_FOUND){
				log.info(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getActiveClientEnrolledStatus()","No active client is enrolled client");
				return null;
			}else{
				log.error(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getActiveClientEnrolledStatus() method failed");
				return null;
			}
		}catch(Exception e){
			log.error(ResponseModelServiceImpl.class.getSimpleName() + ":getActiveClientEnrolledInfo()", "failed to connect the serve", e);
			return null;
		}
	}
	
	private static List<SortParamForClient> getSortParamList(String direction,String [] sortParam){
		List<SortParamForClient> listSort = new ArrayList<SortParamForClient>();
		for(String param : sortParam){
			SortParamForClient sort = new SortParamForClient();
			sort.setDirection(direction);
			sort.setOrderBy(param);
			listSort.add(sort);
		}
		return listSort;
	}

	@Override
	public ResponseModel getSecurityinfo(String customId, String deviceId) {
		try {
			client = new HttpClient();
			ResponseModel res = HttpToolkit.doPostXML(
					ConfigUtil.getACMConfigServerUrl() + URLUtils.REST_URL + URLUtils.DEVICE_SECURITY_INFO_APPENDER + deviceId,
					new TransObjectToXMLImpl().getNetworkInfoPostStr(customId),
					client);
			if (res.getResponseCode() == HttpStatus.SC_OK) {
				log.info(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getNetworkInfo() method succeed");
				return res;
			} else {
				log.error(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getNetworkInfo() method failed");
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}

	}

	@Override
	public ResponseModel operationOnClient(String customId, String deviceIdList,String actionName) {
		try {
			client = new HttpClient();
			ResponseModel res = HttpToolkit.doPostXML(
					ConfigUtil.getACMConfigServerUrl() + URLUtils.REST_URL + URLUtils.DEVICE_OPERATION_VALUE_STR,
					new TransObjectToXMLImpl().getOperationPostStr(customId,
							deviceIdList,actionName), client);
			if (res.getResponseCode() == HttpStatus.SC_OK) {
				log.info(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getRetrieveInfo() method succeed");
				return res;
			} else {
				log.error(ResponseModelServiceImpl.class.getSimpleName()
						+ ":getRetrieveInfo() method failed");
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}
	}
}
