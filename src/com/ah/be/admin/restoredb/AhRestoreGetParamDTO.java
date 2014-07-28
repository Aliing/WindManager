/**
 *@filename	GetDataParamDTO.java
 *@version
 *@author		Fisher
 *@createtime	2007-05-11 10:35:27
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.admin.restoredb;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * @author Fisher
 * @version V1.0.0.0
 */
public class AhRestoreGetParamDTO implements Serializable
{

	private static final long				serialVersionUID	= 1L;

	private final Vector<Map<String, String>>	m_vct_param			= new Vector<Map<String, String>>();

	public void addParam(String strFieldName, String strFieldValue)
	{
		Map<String, String> mapParam = new HashMap<String, String>(2);
		mapParam.put("FIELDNAME", strFieldName);
		mapParam.put("FIELDVALUE", strFieldValue);
		m_vct_param.add(mapParam);
	}

	public String getFiledName(int index)
	{
		return m_vct_param.get(index).get("FIELDNAME");
	}

	public String getFiledValue(int index)
	{
		return m_vct_param.get(index).get("FIELDVALUE");
	}

	public int getSize()
	{
		if (m_vct_param == null)
		{
			return 0;
		}

		return m_vct_param.size();
	}

}