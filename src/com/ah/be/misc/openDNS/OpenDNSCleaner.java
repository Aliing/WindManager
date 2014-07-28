package com.ah.be.misc.openDNS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ah.be.rest.client.OpenDNSService;
import com.ah.be.rest.client.models.opendns.OpenDNSModel;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.OpenDNSAccount;
import com.ah.bo.admin.OpenDNSDevice;
import com.ah.bo.admin.OpenDNSMapping;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class OpenDNSCleaner implements Runnable {
	private static final Tracer log = new Tracer(OpenDNSCleaner.class.getSimpleName());

	@Override
	public void run() {
		MgrUtil.setTimerName(this.getClass().getSimpleName());
		
		List<HMServicesSettings> settings = QueryUtil.executeQuery(HMServicesSettings.class,null,null,null, new ImplQueryBo());
		for(HMServicesSettings setting : settings){
			if(setting.isEnableOpenDNS()){
				OpenDNSAccount activeAccount = setting.getOpenDNSAccount();
				if(activeAccount == null){
					log.error("The OpenDNS Account is null, can not do the setting clean!");
				}else{
					List<OpenDNSDevice> devices = QueryUtil.executeQuery(OpenDNSDevice.class, null, new FilterParams("openDNSAccount.id", activeAccount.getId()), setting.getOwner().getId());			
					List<OpenDNSDevice> removedDevices = new ArrayList<OpenDNSDevice>();
					OpenDNSDevice defaultDevice = null;
					boolean isDefaultRemoved = false;
					for(OpenDNSDevice device : devices){
						if(device.isDefaultDevice()){
							defaultDevice = device;
						}
						OpenDNSModel model = OpenDNSService.fetchDevice(activeAccount.getToken(), device.getDeviceKey());
						if(OpenDNSModel.ERROR_CODE_DEVICE_NOT_EXISTS == model.getError_code()){	
							if(device.isDefaultDevice()){
								isDefaultRemoved = true;
							}
							removedDevices.add(device);					
						}
					}
					
					if(removedDevices.size() != 0){
						try{
							for(OpenDNSDevice removedDevice : removedDevices){
								List<OpenDNSMapping> mappings = QueryUtil.executeQuery(OpenDNSMapping.class, null, new FilterParams("openDNSDevice.id", removedDevice.getId()));
								for(OpenDNSMapping mapping : mappings){
									if(isDefaultRemoved){
										mapping.setOpenDNSDevice(null);
									}else{
										mapping.setOpenDNSDevice(defaultDevice);
									}	
									QueryUtil.updateBo(mapping);
								}
								
								QueryUtil.removeBo(OpenDNSDevice.class, removedDevice.getId());
							}	
						}catch(Exception ex){
							log.error("OpenDNSCleaner", "Got error when remove the OpenDNSDevice or update the UserProfile", ex);
						}	
		
					}
				}
			}
		}
	}
	
	class ImplQueryBo implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
			if (null == bo) {
				return null;
			}
			
			if (bo instanceof HMServicesSettings){
				HMServicesSettings settings = (HMServicesSettings)bo;
				if(settings.getOpenDNSAccount() != null){
					settings.getOpenDNSAccount().getId();
				}
			}
			
			return null;
		}
	}
}
