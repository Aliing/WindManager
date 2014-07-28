package com.ah.be.common;

import java.util.ArrayList;
import java.util.List;

public class SyncConfigValues {
	
    private final List<List<String>> m_sync_items = new ArrayList<List<String>>();
    
    public void setSyncItems()
    {
    	//in List the value is
    	//1:section
    	//2:key
//    	List<String> item_staging = new ArrayList<String>();
//    	item_staging.add(ConfigUtil.SECTION_PORTAL);
//    	item_staging.add(ConfigUtil.KEY_STAGING_SERVER);
    	
    	List<String> item_hmType = new ArrayList<String>();
    	item_hmType.add(ConfigUtil.SECTION_APPLICATION);
    	item_hmType.add(ConfigUtil.KEY_APPLICATION_TYPE);
    	
    	//add to sync list
    	//m_sync_items.add(item_staging);
    	m_sync_items.add(item_hmType);
    }
    
    public List<List<String>> getSyncItems()
    {
    	return m_sync_items;
    }
    
    public static void main(String[] args)
    {
    	//parameter1: src file
    	//parameter2: dest file
    	if(args.length != 2)
    	{
   		    System.exit(1);
    	}
    	
    	String strSrcFile = args[0];
    	String strDestFile = args[1];
    	
    	SyncConfigValues oSyncValues = new SyncConfigValues();
    	oSyncValues.setSyncItems();
    	
    	List<List<String>> sync_items = oSyncValues.getSyncItems();

		for (List<String> oItem : sync_items) {
			//get value from src file
			String value = ConfigUtil.getConfigInfo(strSrcFile, oItem.get(0), oItem.get(1), null);

			if (null == value) {
				System.out.println("could not get the value for " + oItem.get(0) + ":" + oItem.get(1));
				continue;
			}

			//set value to dest file
			if (!ConfigUtil.setConfigInfo(strDestFile, oItem.get(0), oItem.get(1), value)) {
				System.out.println("could not sync the value for " + oItem.get(0) + ":" + oItem.get(1));
			}
		}
    }

}