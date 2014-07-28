/**
 *@filename		UserRegInfoProcessor.java
 *@version
 *@author		Fiona
 *@createtime	2011-4-9 PM 05:54:50
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.ls.processor;

import com.ah.be.ls.action.PacketUserRegInfo;
import com.ah.be.ls.util.CommConst;
import com.ah.bo.admin.UserRegInfoForLs;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
public class UserRegInfoProcessor implements DataProcessor
{
	private UserRegInfoForLs userInfo = new UserRegInfoForLs();

	@Override
	public int do_build_packet(byte[] out)
	{
		return PacketUserRegInfo.buildUserRegInfo(out, 0, userInfo);
	}

	@Override
	public int do_parse_packet(byte[] input)
	{
		return 0;
	}

	@Override
	public byte get_packet_type()
	{
		return CommConst.User_Reg_Info_Packet_Type;
	}

	@Override
	public Object get_response()
	{
		return null;
	}

	@Override
	public byte get_response_type()
	{
		return 0;
	}

	@Override
	public void init_send_data(Object obj)
	{
		userInfo = (UserRegInfoForLs)obj;
	}

	@Override
	public boolean is_need_response()
	{
		return false;
	}

}
