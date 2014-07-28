package com.ah.be.debug.tcpserver;

import java.nio.channels.SocketChannel;

import com.ah.be.debug.BeDebugModuleImpl;

/**
 * 
 *@filename		ITcpChannelProcess.java
 *@version		V1.0.0.0
 *@author		xiaxiaoyin & juyizhou
 *@createtime	2008-1-8 02:39:14
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
public interface ITcpChannelProcess
{

	/**
	 * add tcp connection
	 * 
	 * @param arg_Channel
	 *            tcp channel
	 */
	public void addConnection(SocketChannel arg_Channel);

	/**
	 * remove tcp connection
	 * 
	 * @param arg_Channel
	 *            tcp channel
	 */
	public void removeConnection(SocketChannel arg_Channel);
	
	/**
	 * remove all tcp client connection
	 * 
	 * @param arg_Channel
	 *            tcp channel
	 */
	public void removeAllClientConnection();
	
	/**
	 * cache arg_Channel as a debug console
	 *
	 *@param 
	 *
	 *@return
	 */
	public void addDebugConsole(SocketChannel arg_Channel);
	
	/**
	 * remove arg_Channel from cache
	 *
	 *@param 
	 *
	 *@return
	 */
	public void removeDebugConsole(SocketChannel arg_Channel);

	/**
	 * process tcp channel when data arrival
	 * 
	 * @param arg_Channel
	 *            tcp channel
	 */
	public void process(SocketChannel arg_Channel);
	
	/**
	 * send data to all client
	 * 
	 * @param msg
	 *            data be sent
	 */
	public void sendToAllClient(String msg);
	
	/**
	 * send data to clientchannel
	 *
	 *@param 
	 *
	 *@return
	 */
	public void sendToClient(SocketChannel clientChannel,String msg);
	
	/**
	 * send message to all debug console
	 *
	 *@param 
	 *
	 *@return
	 */
	public void sendToAllDebugConsole(String msg);
	
	/**
	 * get debug module
	 *
	 *@param 
	 *
	 *@return
	 */
	public BeDebugModuleImpl getDebugModuleImpl();
	
	/**
	 * set debug module
	 *
	 *@param 
	 *
	 *@return
	 */
	public void setDebugModuleImpl(BeDebugModuleImpl debugModuleImpl);
}
