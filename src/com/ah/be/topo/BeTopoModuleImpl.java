/**
 *@filename		BeTopoModuleImpl.java
 *@version
 *@author		Steven
 *@createtime	2007-9-3 PM 01:54:33
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.topo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ah.be.app.BaseModule;
import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.cli.cliwindow.CommandExecutor;
import com.ah.be.cli.cliwindow.CommandExecutorImpl;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeCapwapClientEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.os.BeOsLayerModule;
import com.ah.be.topo.idp.IdpEventListener;
import com.ah.be.topo.idp.IdpScheduledExecutor;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public class BeTopoModuleImpl extends BaseModule implements BeTopoModule {

	private MapLinkProcessorViaCapwap linkProcessor;
	private MapLinkPolling linkPolling;
	private IdpEventListener idpListener;
	private BeTopoModuleListener topoModuleListener;
	private IdpScheduledExecutor idpExecutor;
	private CommandExecutor cmdExecutor;
	private PollingController pollingController;
	
	@Override
	public boolean init() {
		linkProcessor = new MapLinkProcessorViaCapwap();
		linkPolling = new MapLinkPolling();
		idpListener = new IdpEventListener();
		topoModuleListener = new BeTopoModuleListener();
		idpExecutor = new IdpScheduledExecutor();
		cmdExecutor = new CommandExecutorImpl();
		return true;
	}

	@Override
	public boolean run() {
		linkProcessor.start();
		linkPolling.start();
		idpListener.start();
		topoModuleListener.start();
		idpExecutor.startTasks();
		cmdExecutor.run();
		return true;
	}

	@Override
	public boolean shutdown() {
		if (null != linkProcessor) {
			linkProcessor.stopProcessor();
		}
		if (null != linkPolling) {
			linkPolling.stopScheduler();
		}
		if (null != idpListener) {
			idpListener.stop();
		}
		if (null != topoModuleListener) {
			topoModuleListener.stop();
		}
		if (null != idpExecutor) {
			idpExecutor.stopTasks();
		}

		if (null != cmdExecutor) {
			cmdExecutor.stop();
		}

		return true;
	}

	/**
	 * Constructor
	 */
	public BeTopoModuleImpl() {
		setModuleId(15);
		setModuleName("BeTopoModule");
	}

	public List<String> getBackgroundImages(String domainName) {
		List<String> imageNames = null;
		try {
			imageNames = HmBeOsUtil.getFileAndSubdirectoryNames(
					BeTopoModuleUtil.getRealTopoBgImagePath(domainName),
					BeOsLayerModule.ONLYFILE, false);

		} catch (Exception e) {
			e.printStackTrace();
		}

		List<String> images = new ArrayList<String>();
		if (imageNames != null && imageNames.size() > 0) {
			for (String image : imageNames) {
				if (image.toLowerCase().endsWith(".png")
						|| image.toLowerCase().endsWith(".jpg")) {
					images.add(image);
				}
			}
		}
		Collections.sort(images, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareToIgnoreCase(o2);
			}
		});
		return images;
	}

	public boolean deleteBackgroundImage(String imageName, String domainName) {
		if (null == imageName || "".equals(imageName.trim())) {
			return false;
		}
		String fileName = BeTopoModuleUtil.getRealTopoBgImagePath(domainName)
				+ File.separator + imageName;

		try {
			return HmBeOsUtil.deletefile(fileName);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void addBackgroundImage(String imageName, File imageFile,
			String domainName) throws Exception {
		if (null == imageName || null == imageFile) {
			return;
		}
		String imagesPath = BeTopoModuleUtil.getRealTopoBgImagePath(domainName);
		File imageDirectory = new File(imagesPath);
		if (!imageDirectory.exists()) {
			imageDirectory.mkdirs();
		}

		String fileName = BeTopoModuleUtil.getRealTopoBgImagePath(domainName)
				+ File.separator + imageName;

		long oldLength = imageFile.length();
		if (oldLength > BeTopoModuleUtil.IMAGE_MAX_SIZE) {
			BeTopoModuleUtil.compressImage(imageFile, 0.9f);
			long newLength = imageFile.length();
			DebugUtil.topoDebugWarn("Map image: " + imageName + " compressed."
					+ "old length:" + oldLength + ", new length:" + newLength);
		}

		FileInputStream fis = new FileInputStream(imageFile);
		FileOutputStream fos = new FileOutputStream(fileName);
		try {
			byte[] buffer = new byte[1024];
			int len;
			while ((len = fis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
		} finally {
			fos.close();
			fis.close();
		}
	}

	@Override
	public void eventDispatched(BeBaseEvent arg_Event) {
		super.eventDispatched(arg_Event);

		topoModuleListener.addEvent(arg_Event);
		cmdExecutor.handle(arg_Event);

		if (null != arg_Event) {
			int type = arg_Event.getEventType();

			switch (type) {
			case BeEventConst.COMMUNICATIONEVENTTYPE:
				BeCommunicationEvent c_event = (BeCommunicationEvent) arg_Event;
				int msgType = c_event.getMsgType();
				if (BeCommunicationConstant.MESSAGEELEMENTTYPE_IDPSTATISTICS == msgType) {
					idpListener.addIdpEvent(arg_Event);
				} else if (BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT == msgType) {
					idpListener.addIdpEvent(arg_Event);
				} else if (BeCommunicationConstant.MESSAGETYPE_IDPQUERYRSP == msgType) {
					DebugUtil
							.topoDebugInfo("HiveAP:"
									+ c_event.getApMac()
									+ " receive IDP query response successfully. Result:"
									+ c_event.getResult() + ", Sequence Num("
									+ c_event.getSequenceNum() + ")");
				} else if (BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTEVENTRSP == msgType) {
					BeCapwapClientEvent clientEvent = (BeCapwapClientEvent) c_event;
					if (clientEvent.getQueryType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_IDPMITIGATION) {
						DebugUtil
								.topoDebugInfo("HiveAP:"
										+ c_event.getApMac()
										+ " receive IDP mitigation query response successfully. Result:"
										+ c_event.getResult()
										+ ", Sequence Num("
										+ c_event.getSequenceNum() + ")");
					}
				}
				break;
			}
		}
	}

	@Override
	public IdpEventListener getIdpEventListener() {
		return idpListener;
	}

	@Override
	public IdpScheduledExecutor getIdpScheduledExecutor() {
		return idpExecutor;
	}

	@Override
	public MapLinkProcessorViaCapwap getMapLinkProcessor() {
		return this.linkProcessor;
	}

	@Override
	public MapLinkPolling getMapLinkPolling() {
		return this.linkPolling;
	}

	@Override
	public BeTopoModuleListener getTopoModuleListener() {
		return this.topoModuleListener;
	}

	@Override
	public CommandExecutor getCommandExecutor() {
		return this.cmdExecutor;
	}

	@Override
	public PollingController getPollingController() {
		
		if(this.pollingController == null) {
			this.pollingController = new PollingController();
		}
		
		return this.pollingController;
	}

}