package com.ah.be.debug.tcpserver;

import java.nio.channels.SocketChannel;

import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.be.common.NmsUtil;
import com.ah.be.debug.DebugConstant;

/**
 * 
 *@filename		DefaultCLIParser.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2008-1-8 02:38:49
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class DefaultCLIParser implements CLIParserInterface
{
	private ITcpChannelProcess	tcpChannelProcess;

	private long				serverStartTime	= System.currentTimeMillis();

	public DefaultCLIParser()
	{
	}

	public DefaultCLIParser(ITcpChannelProcess tcpChannelProcess)
	{
		this.tcpChannelProcess = tcpChannelProcess;
	}

	/**
	 * parse cli and respond to client
	 * 
	 * @param cli:
	 *            cli string array, like "debug topo no"
	 * @param clientChannel:
	 *            all clients if null.
	 * @return
	 */
	public void parseCli(String cli, SocketChannel clientChannel)
	{
		if (cli.length() == 0)
		{
			// "\r\n"
			shellPrompt(clientChannel);
			return;
		}

		// split cli with blank
		String[] clis = cli.split("\\s+");
		if (clis.length == 0)
		{
			shellPrompt(clientChannel);
			return;
		}

		// if contain control key, return
		if (isContainControlKey(cli))
		{
			responseMessage(clientChannel,
				DebugConstant.RSP_INVALIDKEYCONTAINED);
			shellPrompt(clientChannel);
			return;
		}

		// parse
		String cliHead = clis[0];

		if (cliHead.equalsIgnoreCase(DebugConstant.CLI_DEBUG)) {
			parseCli_debug(clis, clientChannel);
		} else if (cliHead.equalsIgnoreCase(DebugConstant.CLI_SHOW)) {
			parseCli_Show(clis, clientChannel);
		} else if (cliHead.equalsIgnoreCase(DebugConstant.CLI_QUIT)) {
			parseCli_Quit(clis, clientChannel);
		} else if (cliHead.equalsIgnoreCase(DebugConstant.CLI_HELP)) {
			showHelpMessage(clientChannel);
		} else {
			reportCliInvalid2Client(clientChannel);
		}

		shellPrompt(clientChannel);
	}

	/**
	 * if contain control key, return true
	 * 
	 * @param
	 * @return
	 */
	private boolean isContainControlKey(String inputStr)
	{
		byte[] byteArray = inputStr.getBytes();
		for (int i = 0; i < byteArray.length; i++)
		{
			if (byteArray[i] > 126 || byteArray[i] < 32)
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * report msg "Cli format is invalid" to client
	 * 
	 * @param
	 * @return
	 */
	private void reportCliInvalid2Client(SocketChannel clientChannel)
	{
		responseMessage(clientChannel, DebugConstant.RSP_INVALIDCLI);
	}

	/**
	 * parse cli which format is "debug ***"
	 * 
	 * @param
	 * @return
	 */
	private void parseCli_debug(String[] clis, SocketChannel clientChannel)
	{
		if (clis.length == 1)
		{
			responseMessage(clientChannel, DebugConstant.RSP_INCOMPLETECLI);
			return;
		}

		if (clis[1].equalsIgnoreCase(DebugConstant.CLI_CONSOLE))
		{
			if (clis.length == 3)
			{
				String thirdCli = clis[2];

				if (thirdCli.equalsIgnoreCase(DebugConstant.CLI_NO))
				{
					// debug console no
					tcpChannelProcess.removeDebugConsole(clientChannel);
					responseMessage(clientChannel, "Debug console is closed");
				}
				else
				{
					reportCliInvalid2Client(clientChannel);
				}
			}
			else
				if (clis.length == 2)
				{
					// debug console
					tcpChannelProcess.addDebugConsole(clientChannel);
					responseMessage(clientChannel, "Debug console is openning");
					return;
				}
				else
				{
					reportCliInvalid2Client(clientChannel);
				}

			shellPrompt(clientChannel);
			return;
		}

		if (clis.length < 3)
		{
			responseMessage(clientChannel, DebugConstant.RSP_INVALIDCLI);
			return;
		}

		String cli_module = clis[1];
		String cli_debugLevel = clis[2];
		int debugLevel = DebugConstant.debugLevelStr2Int(cli_debugLevel);
		if (debugLevel == DebugConstant.DEBUGLEVEL_INVALID
			|| !(cli_module.equalsIgnoreCase(DebugConstant.CLI_MODULE_ADMIN)
				|| cli_module.equalsIgnoreCase(DebugConstant.CLI_MODULE_CONFIG)
				|| cli_module.equalsIgnoreCase(DebugConstant.CLI_MODULE_ALL)
				|| cli_module.equalsIgnoreCase(DebugConstant.CLI_MODULE_TOPO)
				|| cli_module
					.equalsIgnoreCase(DebugConstant.CLI_MODULE_PERFORMANCE)
				|| cli_module.equalsIgnoreCase(DebugConstant.CLI_MODULE_FAULT)
				|| cli_module
					.equalsIgnoreCase(DebugConstant.CLI_MODULE_LICENSE)
				|| cli_module
					.equalsIgnoreCase(DebugConstant.CLI_MODULE_PARAMETER) || cli_module
				.equalsIgnoreCase(DebugConstant.CLI_MODULE_COMMON)))
		{
			reportCliInvalid2Client(clientChannel);
			return;
		}

		if (cli_module.equalsIgnoreCase(DebugConstant.CLI_MODULE_ALL))
		{
			tcpChannelProcess.getDebugModuleImpl()
				.setDebugLevel_All(debugLevel);
		}
		else
			if (cli_module.equalsIgnoreCase(DebugConstant.CLI_MODULE_ADMIN))
			{
				tcpChannelProcess.getDebugModuleImpl().setDebugLevel_admin(
					debugLevel);
			}
			else
				if (cli_module
					.equalsIgnoreCase(DebugConstant.CLI_MODULE_CONFIG))
				{
					tcpChannelProcess.getDebugModuleImpl()
						.setDebugLevel_config(debugLevel);
				}
				else
					if (cli_module
						.equalsIgnoreCase(DebugConstant.CLI_MODULE_TOPO))
					{
						tcpChannelProcess.getDebugModuleImpl()
							.setDebugLevel_topo(debugLevel);
					}
					else
						if (cli_module
							.equalsIgnoreCase(DebugConstant.CLI_MODULE_PERFORMANCE))
						{
							tcpChannelProcess.getDebugModuleImpl()
								.setDebugLevel_performance(debugLevel);
						}
						else
							if (cli_module
								.equalsIgnoreCase(DebugConstant.CLI_MODULE_FAULT))
							{
								tcpChannelProcess.getDebugModuleImpl()
									.setDebugLevel_fault(debugLevel);
							}
							else
								if (cli_module
									.equalsIgnoreCase(DebugConstant.CLI_MODULE_LICENSE))
								{
									tcpChannelProcess.getDebugModuleImpl()
										.setDebugLevel_license(debugLevel);
								}
								else
									if (cli_module
										.equalsIgnoreCase(DebugConstant.CLI_MODULE_PARAMETER))
									{
										tcpChannelProcess
											.getDebugModuleImpl()
											.setDebugLevel_parameter(debugLevel);
									}
									else
										if (cli_module
											.equalsIgnoreCase(DebugConstant.CLI_MODULE_COMMON))
										{
											tcpChannelProcess
												.getDebugModuleImpl()
												.setDebugLevel_common(
													debugLevel);
										}

		// send response msg to all client
		broadCastMessage(createRsp4DebugLevel(cli_module, cli_debugLevel));
	}

	/**
	 * parse cli which format is "show ***"
	 * 
	 * @param
	 * @return
	 */
	private void parseCli_Show(String[] clis, SocketChannel clientChannel)
	{
		if (clis.length == 1)
		{
			responseMessage(clientChannel, DebugConstant.RSP_INCOMPLETECLI);
			return;
		}

		String secondCli = clis[1];

		if (secondCli.equalsIgnoreCase(DebugConstant.CLI_DEBUG))
		{
			// show debug
			showDebugSettings(clientChannel);
		}
		else
			if (secondCli.equalsIgnoreCase(DebugConstant.CLI_VERSION))
			{
				// show version
				showVersion(clientChannel);
			}
			else
			{
				reportCliInvalid2Client(clientChannel);
			}
	}

	/**
	 * parse cli which format is "quit"
	 * 
	 * @param
	 * @return
	 */
	private void parseCli_Quit(String[] clis, SocketChannel clientChannel)
	{
		tcpChannelProcess.removeConnection(clientChannel);
		showQuitMsg(clientChannel);
	}
	
	/**
	 * show usage
	 * 
	 * @param
	 * @return
	 */
	private void showHelpMessage(SocketChannel clientChannel)
	{
		responseMessage(clientChannel, "Usage:");
		responseMessage(clientChannel, "show debug[version]");
		responseMessage(clientChannel, "debug console[all,topo,config,admin,performance,parameter,common,fault,license] [no]");
		responseMessage(clientChannel, "quit");
	}
	/**
	 * show debug settings
	 * 
	 * @param
	 * @return
	 */
	private void showDebugSettings(SocketChannel clientChannel)
	{
		responseMessage(clientChannel, "System(common) debug level:        "
			+ tcpChannelProcess.getDebugModuleImpl().getCommonDebugLevelStr());
		responseMessage(clientChannel, "System(admin) debug level:         "
			+ tcpChannelProcess.getDebugModuleImpl().getAdminDebugLevelStr());
		responseMessage(clientChannel, "System(topo) debug level:          "
			+ tcpChannelProcess.getDebugModuleImpl().getTopoDebugLevelStr());
		responseMessage(clientChannel, "System(config) debug level:        "
			+ tcpChannelProcess.getDebugModuleImpl().getConfigDebugLevelStr());
		responseMessage(clientChannel, "System(fault) debug level:         "
			+ tcpChannelProcess.getDebugModuleImpl().getFaultDebugLevelStr());
		responseMessage(clientChannel, "System(performance) debug level:   "
			+ tcpChannelProcess.getDebugModuleImpl()
				.getPerformanceDebugLevelStr());
		responseMessage(clientChannel, "System(parameter) debug level:     "
			+ tcpChannelProcess.getDebugModuleImpl()
				.getParameterDebugLevelStr());
		responseMessage(clientChannel, "System(license) debug level:       "
			+ tcpChannelProcess.getDebugModuleImpl().getLicenseDebugLevelStr());
	}

	/**
	 * show welcome message
	 * 
	 * @param
	 * @return
	 */
	private void showVersion(SocketChannel clientChannel)
	{
		BeVersionInfo versionInfo = NmsUtil.getVersionInfo();
		String version = versionInfo.getMainVersion() + "r"
			+ versionInfo.getSubVersion();
		long currentTime = System.currentTimeMillis();
		long totalSeconds = (currentTime - serverStartTime) / 1000;
		long seconds = totalSeconds % 60;
		long minutes = (totalSeconds / 60) % 60;
		long hours = (totalSeconds / 60 / 60) % 24;
		long days = totalSeconds / 60 / 60 / 24;

		responseMessage(clientChannel, "Aerohive Networks Inc.");
		responseMessage(clientChannel, "Copyright (C) 2006-2007");
		responseMessage(clientChannel, "\n");
		responseMessage(clientChannel, "Version:       " + "HiveManager v"
			+ version);
		responseMessage(clientChannel, "Build time:    "
			+ versionInfo.getBuildTime());
		responseMessage(clientChannel, "Uptime:        " + days + " days  "
			+ hours + " hours  " + minutes + " minutes  " + seconds
			+ " seconds");
	}

	/**
	 * show quit message
	 * 
	 * @param
	 * @return
	 */
	private void showQuitMsg(SocketChannel clientChannel)
	{
		//		
	}

	/**
	 * send message to all client
	 * 
	 * @param
	 * @return
	 */
	private void broadCastMessage(String message)
	{
		tcpChannelProcess.sendToAllClient(message);
		tcpChannelProcess.sendToAllClient("\r\n");
	}

	/**
	 * send message to given clientchannel
	 * 
	 * @param
	 * @return
	 */
	private void responseMessage(SocketChannel clientChannel, String message)
	{
		tcpChannelProcess.sendToClient(clientChannel, message);
		tcpChannelProcess.sendToClient(clientChannel, "\r\n");
	}

	/**
	 * create response msg of set debug level operation
	 * 
	 * @param
	 * @return
	 */
	private String createRsp4DebugLevel(String module, String level)
	{
		String rsp;
		if (level.equalsIgnoreCase(DebugConstant.CLI_DEBUGLEVEL_NO))
		{
			rsp = "System (" + module + ") debug feature has been closed";
		}
		else
		{
			rsp = "System (" + module + ") debug level has been changed to "
				+ level;
		}

		return rsp;
	}

	private final String	prompt	= "\r\n>";

	/**
	 * prompt to client shell
	 * 
	 * @param
	 * @return
	 */
	private void shellPrompt(SocketChannel clientChannel)
	{
		if (clientChannel == null)
		{
			tcpChannelProcess.sendToAllClient(prompt);
		}
		else
		{
			tcpChannelProcess.sendToClient(clientChannel, prompt);
		}
	}

	public ITcpChannelProcess getTcpChannelProcess()
	{
		return tcpChannelProcess;
	}

	public void setTcpChannelProcess(ITcpChannelProcess tcpChannelProcess)
	{
		this.tcpChannelProcess = tcpChannelProcess;
	}

}
