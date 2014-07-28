package com.ah.be.config.cli.generate.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.ah.be.config.cli.brackets.DollarPlaceHolder;
import com.ah.be.config.cli.generate.AbstractAutoAdaptiveCLIGenerate;
import com.ah.be.config.cli.generate.CLIGenerateException;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.impl.ConfigLazyQueryBo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.CLIBlob;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.Vlan;

public class SupplementalCLIImpl extends AbstractAutoAdaptiveCLIGenerate {
	
	public static final String PLACE_HOLDER_TYPE_VLAN 	= "vlan";
	public static final String PLACE_HOLDER_TYPE_IP 	= "ip";

	@Override
	public void init() throws CLIGenerateException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public List<String> generateCLIs() throws CLIGenerateException {
		CLIBlob cliBlob = getSupplementalCLI();
		if(cliBlob == null){
			return null;
		}
		
		String cliContents = cliBlob.getContentAera();
		if(StringUtils.isEmpty(cliContents)){
			return null;
		}
		
		List<DollarPlaceHolder> dollarList = DollarPlaceHolder.getInstances(cliContents);
		if(dollarList == null || dollarList.isEmpty()){
			return convertCLIToList(cliContents);
		}
		
		Map<String, String> allIpNames = new HashMap<>();
		Map<String, Integer> allVlanNames = new HashMap<>();
		String typeStr, valueStr;
		for(DollarPlaceHolder dObj : dollarList){
			if(StringUtils.isEmpty(dObj.getOriginalContent())){
				continue;
			}
			String[] contentArrays = dObj.getOriginalContent().split(":");
			typeStr = contentArrays[0].trim();
			valueStr = contentArrays[1].trim();
			if(PLACE_HOLDER_TYPE_VLAN.equalsIgnoreCase(typeStr)){
				allVlanNames.put(valueStr, null);
			}else if(PLACE_HOLDER_TYPE_IP.equalsIgnoreCase(typeStr)){
				allIpNames.put(valueStr, null);
			}
		}
		
		convertIp(allIpNames, this.hiveAp);
		convertVlan(allVlanNames, this.hiveAp);
		
		for(DollarPlaceHolder dObj : dollarList){
			if(StringUtils.isEmpty(dObj.getOriginalContent())){
				continue;
			}
			String[] contentArrays = dObj.getOriginalContent().split(":");
			typeStr = contentArrays[0].trim();
			valueStr = contentArrays[1].trim();
			if(PLACE_HOLDER_TYPE_VLAN.equalsIgnoreCase(typeStr)){
				dObj.setContent(allVlanNames.get(valueStr).toString());
			}else if(PLACE_HOLDER_TYPE_IP.equalsIgnoreCase(typeStr)){
				dObj.setContent(allIpNames.get(valueStr));
			}
		}
		
		String resClis = DollarPlaceHolder.getContent(dollarList, cliContents);
		return convertCLIToList(resClis);
	}
	
	private void convertIp(Map<String, String> allIpNames, HiveAp hiveAp) throws CLIGenerateException {
		if(allIpNames.isEmpty()){
			return;
		}
		List<IpAddress> ipObjList = QueryUtil.executeQuery(IpAddress.class, null, 
				new FilterParams("addressName in (:s1)", new Object[]{allIpNames.keySet()}), 
				hiveAp.getOwner().getId(), new ConfigLazyQueryBo() );
		
		if(ipObjList.isEmpty()){
			return;
		}
		
		try{
			for(IpAddress ipObj : ipObjList){
				String ipAddress = CLICommonFunc.getIpAddress(ipObj, hiveAp).getIpAddress();
				allIpNames.put(ipObj.getAddressName(), ipAddress);
			}
		}catch(CreateXMLException e){
			throw new CLIGenerateException(e.getMessage());
		}
		
		Iterator<Entry<String, String>> ipIterators = allIpNames.entrySet().iterator();
		while(ipIterators.hasNext()){
			Entry<String, String> ipEntry = ipIterators.next();
			if(ipEntry.getValue() == null){
				throw new CLIGenerateException("Cannot find IP address by ${ip:"+ipEntry.getKey()+"}");
			}
		}
	}
	
	private void convertVlan(Map<String, Integer> allVlanNames, HiveAp hiveAp) throws CLIGenerateException {
		if(allVlanNames.isEmpty()){
			return;
		}
		List<Vlan> vlanObjList = QueryUtil.executeQuery(Vlan.class, null, 
				new FilterParams("vlanName in (:s1)", new Object[]{allVlanNames.keySet()}), 
				hiveAp.getOwner().getId(), new ConfigLazyQueryBo() );
		
		if(vlanObjList.isEmpty()){
			return;
		}
		
		try{
			for(Vlan vlanObj : vlanObjList){
				int vlan = CLICommonFunc.getVlan(vlanObj, hiveAp).getVlanId();
				allVlanNames.put(vlanObj.getVlanName(), vlan);
			}
		}catch(CreateXMLException e){
			throw new CLIGenerateException(e.getMessage());
		}
		
		Iterator<Entry<String, Integer>> vlanIterators = allVlanNames.entrySet().iterator();
		while(vlanIterators.hasNext()){
			Entry<String, Integer> vlanEntry = vlanIterators.next();
			if(vlanEntry.getValue() == null){
				throw new CLIGenerateException("Cannot find VLAN by ${vlan:"+vlanEntry.getKey()+"}");
			}
		}
	}
	
	private List<String> convertCLIToList(String cliStr){
		if(StringUtils.isEmpty(cliStr)){
			return null;
		}
		
		String[] cliArrays = cliStr.split("\n");
		return Arrays.asList(cliArrays);
	}
	
	private CLIBlob getSupplementalCLI(){
		String sqlStr = "select enableSupplementalCLI from "+HMServicesSettings.class.getSimpleName();
		List<?> resList = QueryUtil.executeQuery(sqlStr, null, null, hiveAp.getOwner().getId());
		if(resList == null || resList.isEmpty()){
			return null;
		}else if(!Boolean.parseBoolean(resList.get(0).toString())){
			return null;
		}else if(hiveAp.getSupplementalCLI() != null){
			return hiveAp.getSupplementalCLI();
		}else{
			return hiveAp.getConfigTemplate().getSupplementalCLI();
		}
		
	}

}
