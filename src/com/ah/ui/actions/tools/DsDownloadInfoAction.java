package com.ah.ui.actions.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;

import org.json.JSONObject;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.cloudauth.HmCloudAuthCertMgmtImpl;
import com.ah.be.common.NmsUtil;
import com.ah.be.config.hiveap.UpdateManager;
import com.ah.bo.hiveap.DownloadInfo;
import com.opensymphony.xwork2.ActionSupport;

public class DsDownloadInfoAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;

	private String operation;
	
	private Long ownerId;
	
	private DownloadInfo dInfo;
	
	private InputStream iStream;
	
	private int threadNum;
	
	protected JSONObject jsonObject;
	
	@Override
	public String execute() throws Exception {
		
		if(operation.equals("getInfo")){
			prepareDownloadInfo();
			return "downloadInfo";
		}else if(operation.equals("changeThreadNum")){
			jsonObject = new JSONObject();
			if(threadNum > 0){
				UpdateManager.UPDATE_MAX_COUNT = threadNum;
				jsonObject.put("result", "success");
			}else{
				jsonObject.put("result", "threadNum invalid");
			}
			return "json";
		}else if(operation.equals("imageDownload")){
			AhAppContainer.getBeConfigModule().getImageSynupLS().downloadImageManual();
			return "json";
		}
		return null;
	}
	
	private void prepareDownloadInfo() throws Exception {
		initDownloadInfo();
		ByteArrayOutputStream oStream = serial(dInfo);
		iStream = new ByteArrayInputStream(oStream.toByteArray());
	}
	
	private void initDownloadInfo(){
		dInfo = new DownloadInfo();
		
		dInfo.setEcwpDefault(NmsUtil.isEcwpDefault());
		dInfo.setEcwpDepaul(NmsUtil.isEcwpDepaul());
		dInfo.setEcwpNnu(NmsUtil.isEcwpNnu());
		dInfo.setHHMApp(NmsUtil.isHostedHMApplication());
		dInfo.setHmIpAddress(HmBeOsUtil.getHiveManagerIPAddr());
		dInfo.setEnableIdm(NmsUtil.isVhmEnableIdm(ownerId));
		dInfo.setIdmRadSecConfig(new HmCloudAuthCertMgmtImpl().getRadSecConfig(ownerId));
	}
	
	private ByteArrayOutputStream serial(Object obj) {
		ByteArrayOutputStream outStream = null;
		ObjectOutputStream oos = null;
		try {
			outStream = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(outStream);
			oos.writeObject(obj);
			oos.flush();
			return outStream;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(outStream != null){
				try{
					outStream.close();
				}catch(Exception e){}
				
			}
			if(oos != null){
				try{
					oos.close();
				}catch(Exception e){}
			}
		}
		return null;
	}
	
	public InputStream getInputStream() throws Exception {
		return iStream;
	}

	public String getLocalFileName() {
		return "downloadInfo";
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public int getThreadNum() {
		return threadNum;
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

}
