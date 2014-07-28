package com.ah.be.config.cli.generate.impl;

import com.ah.be.config.cli.generate.CLIConfig;
import com.ah.be.config.cli.generate.CLIGenResult;
import com.ah.be.config.cli.generate.AbstractAutoAdaptiveCLIGenerate;
import com.ah.be.config.cli.generate.CLIGenerateException;
import com.ah.bo.network.NetworkService;

public class ServiceImpl extends AbstractAutoAdaptiveCLIGenerate {
	
	private NetworkService netWorkService;
	
	private String serviceName;
	
	public ServiceImpl(NetworkService netWorkService){
		this.netWorkService = netWorkService;
	}

	@Override
	public void init() throws CLIGenerateException {
		this.serviceName = netWorkService.getServiceName();
	}
	
	@Override
	public boolean isValid(){
		return !netWorkService.isCliDefaultFlag();
	}
	
	@CLIConfig
	public CLIGenResult getCreateServiceCLI(){
		if(netWorkService.getServiceType() != NetworkService.SERVICE_TYPE_NETWORK){
			return null;
		}
		
		//get protocol
		short protocolId = -1;
		int customerNum = -1;
		switch(netWorkService.getProtocolId()){
			case NetworkService.PROTOCOL_ID_CUSTOM:
				switch(netWorkService.getProtocolNumber()){
					case 6:
						protocolId = NetworkService.PROTOCOL_ID_TCP;
						break;
					case 17:
						protocolId = NetworkService.PROTOCOL_ID_UDP;
						break;
					case 119:
						protocolId = NetworkService.PROTOCOL_ID_SVP;
						break;
					default:
						customerNum = netWorkService.getProtocolNumber();
						break;
				}
				break;
			case NetworkService.PROTOCOL_ID_SVP:
			case NetworkService.PROTOCOL_ID_TCP:
			case NetworkService.PROTOCOL_ID_UDP:
				protocolId = netWorkService.getProtocolId();
				break;
		}
		
		//get network service port
		Integer port = null;
		if(netWorkService.getProtocolId() == NetworkService.PROTOCOL_ID_TCP || 
				netWorkService.getProtocolId() == NetworkService.PROTOCOL_ID_UDP){
			port = netWorkService.getPortNumber();
		}
		
		CLIGenResult cliRes = null;
		if(protocolId > 0){
			cliRes = new CLIGenResult();
			cliRes.add(SERVICE_PROTOCOL_PORT_TIMEOUT, 
					new Object[]{serviceName, protocolId, port, netWorkService.getIdleTimeout()});
		}else if(customerNum > 0){
			cliRes = new CLIGenResult();
			cliRes.add(SERVICE_PROTOCOL_NUM_PORT_TIMEOUT, 
					new Object[]{serviceName, customerNum, port, netWorkService.getIdleTimeout()});
		}
		
		return cliRes;
	}
	
	@CLIConfig(SERVICE_APP_ID)
	public CLIGenResult getServiceAppId(){
		if(netWorkService.getServiceType() != NetworkService.SERVICE_TYPE_L7){
			return null;
		}
		
		CLIGenResult cliRes = new CLIGenResult();
		cliRes.add(new Object[]{serviceName, netWorkService.getAppId(), netWorkService.getIdleTimeout()});
		return cliRes;
	}
	
	@CLIConfig
	public CLIGenResult getServiceAlg(){
		if(!isConfigAlg()){
			return null;
		}
		
		CLIGenResult cliRes = new CLIGenResult();
		Object[] resArgs = new Object[]{serviceName, netWorkService.getAlgType()};
		cliRes.add(SERVICE_ALG, resArgs);
		cliRes.add(SERVICE_ALG_OLD, resArgs);
		cliRes.add(SERVICE_ALG_TV, resArgs);
		return cliRes;
	}
	
	private boolean isConfigAlg() {
		return netWorkService.getAlgType() > 0 && 
				netWorkService.getAlgType() != NetworkService.ALG_TYPE_NONE;
	}

}
