package com.ah.ui.actions;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.mgmt.QueryBo;
import com.ah.util.Tracer;


public class AppRedirectAction  extends BaseAction implements QueryBo{
	
	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(SwitchDomainAction.class
			.getSimpleName());
	
	private String redirectUrl;

	@Override
	public String execute() throws Exception {		
		//CSRF Defence - He Hui 20140523
		injectCSRFToken();
		try {
			if (TidConstant.IDM_HM_CONFIG_SSID == tid) {				
				String errorCode = getUserInfoForSSO(vid);
				if (!StringUtils.isEmpty(vid)) {
					if(null == errorCode){
						redirectUrl = request.getScheme() + "://" + request.getServerName() + ":"
								+ request.getServerPort() + request.getContextPath() + "/";
					}else{
						// need go to master
						if ("slave".equals(errorCode)) {
							redirectUrl = getMasterURL();
						} else if ("exception".equals(errorCode)) {
							redirectUrl = NmsUtil.getMyHiveServiceURL()+"/loginError.action?loginErrorMsg=error.authentication.credentials.bad";
						} else {
							redirectUrl = NmsUtil.getMyHiveServiceURL()+"/loginError.action?loginErrorMsg="+errorCode;
						}
					}
					return SUCCESS; 
				} else {
					log.error("appRedirect", "Request parameter VHMID is empty.");
				}
			} 
			return SUCCESS;
		} catch (Exception e) {
			log.error("appRedirect", e.getMessage(), e);
			return "redirect";
		}
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof HmUser) {
			HmUser user = (HmUser) bo;
// cchen DONE			
//			if (user.getTableColumns() != null)
//				user.getTableColumns().size();
//			if (user.getTableSizes() != null)
//				user.getTableSizes().size();
//			if (user.getAutoRefreshs() != null)
//				user.getAutoRefreshs().size();
			if (user.getUserGroup().getInstancePermissions() != null)
				user.getUserGroup().getInstancePermissions().size();
			if (user.getUserGroup().getFeaturePermissions() != null)
				user.getUserGroup().getFeaturePermissions().size();
		}
		if (bo instanceof HmUserGroup) {
			HmUserGroup group = (HmUserGroup) bo;
			if (group.getInstancePermissions() != null)
				group.getInstancePermissions().size();
			if (group.getFeaturePermissions() != null)
				group.getFeaturePermissions().size();
		}
		return null;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	
	private String vid;

	public String getVid() {
		return vid;
	}

	public void setVid(String vid) {
		this.vid = vid;
	}


}
