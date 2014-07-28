package com.ah.be.debug.tcpserver;

import java.nio.channels.SocketChannel;

/**
 * 
 *@filename		CLIParserInterface.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2008-1-8 02:38:41
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
public interface CLIParserInterface
{
	/**
	 * parse cli and respond to client
	 * 
	 * @param cli:
	 *            cli string array, like "debug topo no"
	 * @param clientChannel:
	 *            all clients if null.
	 * @return
	 */
	public void parseCli(String cli, SocketChannel clientChannel);
}
