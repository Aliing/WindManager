package com.ah.be.performance;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.ah.be.app.DebugUtil;
import com.ah.bo.HmBo;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.OsObject;
import com.ah.bo.network.OsObjectVersion;

/**
 * 
 *@filename		BeSLAStatsProcessor.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2010-6-3 02:23:17
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 * 
 */
public class BeOsInfoProcessor implements QueryBo{

	// cache group
	private static BeOsInfoProcessor instance;
	private static final ConcurrentMap<Long, Map<String,String>>	osInfo_map						= new ConcurrentHashMap<Long, Map<String,String>>();
	private static final ConcurrentMap<String,String>	globalOsInfo_map				= new ConcurrentHashMap<String,String>();
	
	public BeOsInfoProcessor (){
		
	}
	
	public static BeOsInfoProcessor getInstance(){
		if(instance==null) {
			instance = new BeOsInfoProcessor();
		}
		return instance;
	}
	public synchronized String getOsName(Long domainId, String key) {
		if(null == key || key.isEmpty())
			return "unknown";
		
		if(osInfo_map == null || globalOsInfo_map == null) {
			return key;
		}
		if(osInfo_map.get(domainId)!=null && osInfo_map.get(domainId).get(key) !=null) {
			return osInfo_map.get(domainId).get(key);
		} else if(globalOsInfo_map.get(key) !=null) {
			return globalOsInfo_map.get(key);
		} else {
			return key;
		}
	}
	
	public synchronized void addOsName(Long profileId){
		try {
			if(profileId== null) {
				return;
			}
			OsObject os = QueryUtil.findBoById(OsObject.class, profileId, this);
			if(os!=null) {
				if(os.getItems()!=null) {
					for(OsObjectVersion ov: os.getItems()){
						if(osInfo_map.get(os.getOwner().getId())==null) {
							Map<String,String> tmp = new HashMap<String,String>();
							osInfo_map.put(os.getOwner().getId(), tmp);
						}
						osInfo_map.get(os.getOwner().getId()).put(ov.getOsVersion(), os.getOsName());
					}
				}
				
				if(os.getDhcpItems()!=null) {
					for(OsObjectVersion ov: os.getDhcpItems()){
						if(osInfo_map.get(os.getOwner().getId())==null) {
							Map<String,String> tmp = new HashMap<String,String>();
							osInfo_map.put(os.getOwner().getId(), tmp);
						}
						osInfo_map.get(os.getOwner().getId()).put(ov.getOsVersion(), os.getOsName());
					}
				}
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugError("BeOsInfoProcessor.addOsName() catch exception", e);
		}
		
	}
	
	public synchronized void resetOsName(Long domainId){
		try {
			if(osInfo_map.get(domainId)!=null) {
				osInfo_map.get(domainId).clear();
			}
			List<OsObject> list = QueryUtil.executeQuery(OsObject.class, null, null, domainId, this);
			if(!list.isEmpty()) {
				for(OsObject os: list) {
					if(os.getItems()!=null) {
						for(OsObjectVersion ov: os.getItems()){
							if(osInfo_map.get(os.getOwner().getId())==null) {
								Map<String,String> tmp = new HashMap<String,String>();
								osInfo_map.put(os.getOwner().getId(), tmp);
							}
							osInfo_map.get(os.getOwner().getId()).put(ov.getOsVersion(), os.getOsName());
						}
					}
					
					if(os.getDhcpItems()!=null) {
						for(OsObjectVersion ov: os.getDhcpItems()){
							if(osInfo_map.get(os.getOwner().getId())==null) {
								Map<String,String> tmp = new HashMap<String,String>();
								osInfo_map.put(os.getOwner().getId(), tmp);
							}
							osInfo_map.get(os.getOwner().getId()).put(ov.getOsVersion(), os.getOsName());
						}
					}
				}
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugError("BeOsInfoProcessor.resetOsName() catch exception", e);
		}
	}

	public synchronized void removeVhmOsName(Long domainId) {
		try {
			if(osInfo_map.get(domainId)!=null) {
				osInfo_map.remove(domainId);
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugError("BeOsInfoProcessor.removeVhmOsName() catch exception", e);
		}
	}
	
	public void initOsInfoMap(){
		try {
			List<OsObject> list = QueryUtil.executeQuery(OsObject.class, null, null, null, this);
			if(!list.isEmpty()) {
				for(OsObject os: list) {
					if(os.isDefaultFlag()) {
						if(os.getItems()!=null) {
							for(OsObjectVersion ov: os.getItems()){
								if(os.getOsName().equals("iPod/iPhone/iPad") 
										&& (ov.getOsVersion().equals("iPhone") ||  ov.getOsVersion().equals("iPad"))){
									continue;
								}
								globalOsInfo_map.put(ov.getOsVersion(), os.getOsName());
							}
						}
						
						if(os.getDhcpItems()!=null) {
							for(OsObjectVersion ov: os.getDhcpItems()){
								globalOsInfo_map.put(ov.getOsVersion(), os.getOsName());
							}
						}
					} else {
						if(os.getItems()!=null) {
							for(OsObjectVersion ov: os.getItems()){
								if(osInfo_map.get(os.getOwner().getId())==null) {
									Map<String,String> tmp = new HashMap<String,String>();
									osInfo_map.put(os.getOwner().getId(), tmp);
								}
								osInfo_map.get(os.getOwner().getId()).put(ov.getOsVersion(), os.getOsName());
							}
						}
						
						if(os.getDhcpItems()!=null) {
							for(OsObjectVersion ov: os.getDhcpItems()){
								if(osInfo_map.get(os.getOwner().getId())==null) {
									Map<String,String> tmp = new HashMap<String,String>();
									osInfo_map.put(os.getOwner().getId(), tmp);
								}
								osInfo_map.get(os.getOwner().getId()).put(ov.getOsVersion(), os.getOsName());
							}
						}
					}
				}
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugError("BeOsInfoProcessor.initOsInfoMap() catch exception", e);
		}
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		
		if(bo instanceof OsObject) {
			OsObject profile = (OsObject) bo;
			if(profile.getItems()!=null) {
				profile.getItems().size();
			}
			if(profile.getDhcpItems()!=null) {
				profile.getDhcpItems().size();
			}
			if(profile.getOwner()!=null) {
				profile.getOwner().getId();
			}
		}
		return null;
	}
}