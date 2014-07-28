package com.ah.ui.actions.tools;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.ah.be.license.LicenseOperationTool;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.home.clientManagement.service.CertificateGenSV;
import com.ah.util.MgrUtil;
import com.ah.util.values.PairValue;

public class ClientMgmtTestAction extends BaseAction {
	
	@Override
	public String execute(){
		
		try{
			if("test".equals(operation)){
				jsonObject = new JSONObject();
				
				String customerId = LicenseOperationTool.getCustomerIdFromRemote(getDomain().getInstanceId());
                PairValue<Boolean, PairValue<String, String>> rtnvalue = CertificateGenSV.troubleshooting(customerId, getDomain());
                String message = rtnvalue.getDesc().getValue();
                String warningMessage = rtnvalue.getDesc().getDesc();
                if(rtnvalue.getValue()){
                    jsonObject.put("succ", true);
                    if(StringUtils.isBlank(message)) {
                        jsonObject.put("msg", MgrUtil.getUserMessage("home.hmSettings.clientManagement.troubleshooting.success"));
                    }
                }else{
                    jsonObject.put("succ", false);
                    if(Integer.parseInt(message) > 0){
                        message = MgrUtil.getEnumString(CertificateGenSV.TROUBLESHOOTING_EXCEPTION + message);
                    }
                    jsonObject.put("msg", MgrUtil.getUserMessage("home.hmSettings.clientManagement.troubleshooting.failure.reason",message));
                }
                if(StringUtils.isNotBlank(warningMessage)) {
                    jsonObject.put("warn", warningMessage);
                }
				return "json";
			}
		}catch(Exception e){
			
		}
		return SUCCESS;
	}
	
	@Override
	public void prepare() throws Exception{
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_CLIENT_MGMT_TEST);
	}

}
