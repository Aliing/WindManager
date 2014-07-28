package com.ah.be.admin.restoredb;

import java.sql.Timestamp;

import com.ah.bo.HmBo;

public class AhRestoreNewTools {
	
	public static <T extends HmBo> T CreateBoWithId(Class<T> boClass, Long lId)
	{
		if (null == lId)
		{
			return null;
		}
		
		try
		{
			T bo = boClass.newInstance();
			
			bo.setId(lId);
			bo.setVersion(new Timestamp(System.currentTimeMillis()));
			return bo;
		}
		catch(Exception e)
		{
			//add log
			return null;
		}
	}

}