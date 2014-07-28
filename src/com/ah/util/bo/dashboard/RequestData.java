package com.ah.util.bo.dashboard;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ah.bo.mgmt.impl.MapMgmtImpl;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.common.async.Worker;
import com.ah.common.async.impl.Messages;
import com.ah.nms.service.report.Data;
import com.ah.nms.service.report.OrderBy;
import com.ah.nms.service.report.RequestConstants;

public class RequestData {

	public RequestData() {
		data = new Data();
		metrics = new HashMap<>();
		maping = new HashMap<String,Object>();
	}
	
	static private final String REVISION = Worker.REVISION,
            WORKER = Messages.WORKER;
	static protected final Collection<String> PARAMETERs = new HashSet<>();
	static
	{
		PARAMETERs.add(REVISION);
		PARAMETERs.add(WORKER);
	}
	
	private Data data;
	private Map<String,Byte> metrics;
	Map<String, Object> maping;
	public static final byte NO_DATA_SET = -99;
	private boolean example;
	private String axis;
	private int row;
	private int rows = 10;
	private long startTime=0;
	private long endTime=0;
	private int sample = 60;
	private int samples = -1;
	private int application=NO_DATA_SET;
	private String userName=null;
	private String clientDeviceMAC=null;
	private String port=null;
	
	private long domain = NO_DATA_SET;
	private String userProfile=null;
	private String ssid=null;
	private long topology=NO_DATA_SET;
	private String deviceMac=null;
	
	private long networkPolicy=NO_DATA_SET;
	private String tag1=null;
	private String tag2=null;
	private String tag3=null;
    private String deviceType=null;
    private String deviceModel=null;
    
    private int groupBy=0;
    
    private int filterDeviceType=0;
    private int metricDeviceType=0;
    
    private OrderBy orderBy=null;
    
    private String requestId;
    
    private int apNumber=0;

	public boolean check() {
		if (domain < 0) {
			return false;
		}
		return true;
	}
	
	private String checkFilterParams(){
		if (metricDeviceType==1 && filterDeviceType==2) {
			return "This widget don't support switch. No data can be display.";
		} else if (metricDeviceType==2 && filterDeviceType==1) {
			return "This widget only support switch. No data can be display.";
		}
		return null;
	}
	
	/**
	 * only null stand for there is no error in this request data, "" is not allowed here
	 * @return
	 */
	public String checkErrors() {
		String result = null;
		result = checkFilterParams();
		
		if (result == null) {
			if (StringUtils.isNotBlank(this.getErrMsg())) {
				result = this.getErrMsg();
			}
		}
		
		return result;
	}
	
	public RequestData formatData() throws Exception {
		if(this.isExample()) {
			maping.put(RequestConstants.EXAMPLE, this.example);
		}
		data.setAxis(this.axis);
		maping.put(RequestConstants.ROW, this.row);
		maping.put(RequestConstants.ROWS, this.rows);
		maping.put(RequestConstants.START_TIME, this.startTime);
		maping.put(RequestConstants.END_TIME, this.endTime);
		maping.put(RequestConstants.SAMPLE, this.sample);
		if(this.application!=NO_DATA_SET) {
			maping.put(RequestConstants.APPLICATION, this.application);
		}
		if(this.userName!=null) {
			maping.put(RequestConstants.USER, this.userName);
		}
		if(this.clientDeviceMAC!=null) {
			maping.put(RequestConstants.CLIENT_DEVICE_MAC, this.clientDeviceMAC);
		}
		if(this.port!=null) {
			maping.put(RequestConstants.PORT, this.port);
		}
		
		if(this.domain!=NO_DATA_SET) {
			maping.put(RequestConstants.DOMAIN, this.domain);
		}
		
		if (deviceMac!=null) {
			maping.put(RequestConstants.MAC, this.deviceMac);
		}
		if (ssid!=null) {
			maping.put(RequestConstants.SSID, this.ssid);
		}
		if (userProfile!=null) {
			maping.put(RequestConstants.USER_PROFILE, this.userProfile);
		}
		if (topology!=NO_DATA_SET) {
			maping.put(RequestConstants.TOPOLOGY, this.topology);
		} else {
			MapContainerNode node =  MapMgmtImpl.getInstance().getVHMRootMap(domain);
			if (node!=null) {
				topology = node.getId();
				maping.put(RequestConstants.TOPOLOGY, this.topology);
			}
		}
		if (networkPolicy!=NO_DATA_SET) {
			maping.put(RequestConstants.NETWORK_POLICY, this.networkPolicy);
		}
		if (tag1!=null) {
			maping.put(RequestConstants.TAG1, this.tag1);
		}
		if (tag2!=null) {
			maping.put(RequestConstants.TAG2, this.tag2);
		}
		if (tag3!=null) {
			maping.put(RequestConstants.TAG3, this.tag3);
		}
		if (deviceType!=null) {
			maping.put(RequestConstants.DEVICE_TYPE, this.deviceType);
		}
		if (deviceModel!=null) {
			maping.put(RequestConstants.DEVICE_MODEL, this.deviceModel);
		}
		
		if (groupBy!=0) {
			maping.put(RequestConstants.GROUP_BY, this.groupBy);
		}
		
		data.setRequestId(this.requestId);
		
		maping.put(RequestConstants.APNUMBER, this.apNumber);
		
		if (orderBy!=null) {
			data.setOrderby(orderBy);
		}
		data.setMetrics(this.metrics);

		data.setRequest(maping);
		return this;
	}
	
	private void exclusiveFieldsTypeOne() {
		this.application = NO_DATA_SET;
		this.userName = null;
		this.clientDeviceMAC = null;
		this.port=null;
	}
	
	private void exclusiveFieldsTypeTwo() {
		this.networkPolicy = NO_DATA_SET;
		this.tag1 = null;
		this.tag2 = null;
		this.tag3 = null;
		this.deviceType = null;
		this.deviceModel = null;
	}

	private void exclusiveFieldsTypeThree() {
		this.userProfile = null;
		this.ssid = null;
	}
	
	public void addMetric(String name, Byte value) {
		metrics.put(name, value);
	}
	
	public Data getData() {
		return data;
	}
	public void setData(Data data) {
		this.data = data;
	}
	
	public boolean isExample() {
		return example;
	}
	public void setExample(boolean example) {
		this.example = example;
	}
	
	public String getAxis() {
		return axis;
	}
	public void setAxis(String axis) {
		this.axis = axis;
	}
	
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	
	public int getSample() {
		return sample;
	}
	public void setSample(int sample) {
		this.sample = sample;
	}
	
	public int getApplication() {
		return application;
	}
	public void setApplication(int application) {
		exclusiveFieldsTypeOne();
		this.application = application;
	}
	
	public String getClientDeviceMAC() {
		return clientDeviceMAC;
	}
	public void setClientDeviceMAC(String clientDeviceMAC) {
		exclusiveFieldsTypeOne();
		this.clientDeviceMAC = clientDeviceMAC;
	}
	
	public long getTopology() {
		return topology;
	}
	public void setTopology(long topology) {
		this.topology = topology;
	}
	
	public long getDomain() {
		return domain;
	}
	public void setDomain(long domain) {
		this.domain = domain;
	}
	
	public String getUserProfile() {
		return userProfile;
	}
	public void setUserProfile(String userProfile) {
		exclusiveFieldsTypeThree();
		this.userProfile=userProfile;
	}
	
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		exclusiveFieldsTypeThree();
		this.ssid=ssid;
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		exclusiveFieldsTypeOne();
		this.userName = userName;
	}

	public Map<String, Byte> getMetrics() {
		return metrics;
	}

	public void setMetrics(Map<String, Byte> metrics) {
		this.metrics = metrics;
	}
	
	public void mergeRequestData(RequestData aData) {
		if (aData == null) {
			return;
		}
		
		if (aData.getStartTime() > 0) {
			this.setStartTime(aData.getStartTime());
		}
		if (aData.getEndTime() > 0) {
			this.setEndTime(aData.getEndTime());
		}

		if (aData.getTopology() != NO_DATA_SET) {
			this.setTopology(aData.getTopology());
		}
		
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	//assistant fields, but not for request data, start
	private String renderType;
	private String htmlContent;
	private String specifyName;
	private int specifyType;
	private boolean blnOvertime;
	private boolean blnAlwaysLastHour;
	private int currentDeviceCount;
	private int componentKey;
	private String errMsg;
	private Long daId;
	
	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public String getRenderType() {
		return renderType;
	}

	public void setRenderType(String renderType) {
		this.renderType = renderType;
	}
	
	public String getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}
	
	public String getSpecifyName() {
		return specifyName;
	}

	public void setSpecifyName(String specifyName) {
		this.specifyName = specifyName;
	}
	
	public int getSpecifyType() {
		return specifyType;
	}

	public void setSpecifyType(int specifyType) {
		this.specifyType = specifyType;
	}
	
	public boolean isBlnOvertime() {
		return blnOvertime;
	}

	public void setBlnOvertime(boolean blnOvertime) {
		this.blnOvertime = blnOvertime;
	}
	
	public boolean isBlnAlwaysLastHour() {
		return blnAlwaysLastHour;
	}

	public void setBlnAlwaysLastHour(boolean blnAlwaysLastHour) {
		this.blnAlwaysLastHour = blnAlwaysLastHour;
	}
	
	public int getCurrentDeviceCount() {
		return currentDeviceCount;
	}

	public void setCurrentDeviceCount(int currentDeviceCount) {
		this.currentDeviceCount = currentDeviceCount;
	}
	
	public int getComponentKey() {
		return componentKey;
	}

	public void setComponentKey(int componentKey) {
		this.componentKey = componentKey;
	}
	//assistant fields, but not for request data, end
	
	public String getDeviceMac() {
		return deviceMac;
	}

	public void setDeviceMac(String deviceMac) {
		this.deviceMac = deviceMac;
	}

	public int getSamples() {
		return samples;
	}

	public void setSamples(int samples) {
		this.samples = samples;
	}

	public long getNetworkPolicy() {
		return networkPolicy;
	}

	public void setNetworkPolicy(long networkPolicy) {
		exclusiveFieldsTypeTwo();
		this.networkPolicy = networkPolicy;
	}

	public String getTag1() {
		return tag1;
	}

	public void setTag1(String tag1) {
		exclusiveFieldsTypeTwo();
		this.tag1 = tag1;
	}

	public String getTag2() {
		return tag2;
	}

	public void setTag2(String tag2) {
		exclusiveFieldsTypeTwo();
		this.tag2 = tag2;
	}

	public String getTag3() {
		return tag3;
	}

	public void setTag3(String tag3) {
		exclusiveFieldsTypeTwo();
		this.tag3 = tag3;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		exclusiveFieldsTypeTwo();
		this.deviceType = deviceType;
	}

	public String getDeviceModel() {
		return deviceModel;
	}
	public void setDeviceModel(String deviceModel) {
		exclusiveFieldsTypeTwo();
		 this.deviceModel=deviceModel;
	}

	public OrderBy getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String metric, boolean desc) {
		if (orderBy==null) {
			orderBy = new OrderBy();
			orderBy.setName(metric);
			orderBy.setSortorder(OrderBy.DESC);
		} else {
			orderBy.setName(metric);
			orderBy.setSortorder(OrderBy.DESC);
		}
	}

	public Map<String, Object> getMaping() {
		return maping;
	}

	public void setMaping(Map<String, Object> maping) {
		this.maping = maping;
	}

	public int getFilterDeviceType() {
		return filterDeviceType;
	}

	public void setFilterDeviceType(int filterDeviceType) {
		this.filterDeviceType = filterDeviceType;
	}

	public int getMetricDeviceType() {
		return metricDeviceType;
	}

	public void setMetricDeviceType(int metricDeviceType) {
		this.metricDeviceType = metricDeviceType;
	}

	public int getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(int groupBy) {
		this.groupBy = groupBy;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Long getDaId() {
		return daId;
	}

	public void setDaId(Long daId) {
		this.daId = daId;
	}

	public int getApNumber() {
		return apNumber;
	}

	public void setApNumber(int apNumber) {
		this.apNumber = apNumber;
	}

}
