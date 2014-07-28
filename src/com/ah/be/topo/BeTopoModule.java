/**
 *@filename		BeTopoModule.java
 *@version
 *@author		Steven
 *@createtime	2007-9-3  01:53:49
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.topo;

import java.io.File;
import java.util.List;

import com.ah.be.cli.cliwindow.CommandExecutor;
import com.ah.be.topo.idp.IdpEventListener;
import com.ah.be.topo.idp.IdpScheduledExecutor;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public interface BeTopoModule {

	List<String> getBackgroundImages(String domainName);

	boolean deleteBackgroundImage(String imageName, String domainName);

	void addBackgroundImage(String imageName, File imageFile, String domainName)
			throws Exception;

	IdpEventListener getIdpEventListener();

	IdpScheduledExecutor getIdpScheduledExecutor();
	
	public MapLinkProcessorViaCapwap getMapLinkProcessor();

	public MapLinkPolling getMapLinkPolling();
	
	public BeTopoModuleListener getTopoModuleListener();
	
	public CommandExecutor getCommandExecutor();
	
	public PollingController getPollingController();
}