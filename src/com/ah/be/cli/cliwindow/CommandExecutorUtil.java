/**
 * @filename			CommandExecutorUtil.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.4
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.cli.cliwindow;

import java.util.Collection;

import com.ah.be.app.HmBeTopoUtil;

/**
 * Utilities of command executor
 */
public class CommandExecutorUtil {

	public static void addCommands(String source, 
			Collection<CommandStatus> commands) {
		HmBeTopoUtil.getCommandExecutor().getCommandPool().addCommands(source, commands);
	}
	
	public static void executeCommand(Command command) {
		HmBeTopoUtil.getCommandExecutor().execute(command);
	}
	
	public static Collection<CommandStatus> getCommands(String source) {
		return HmBeTopoUtil.getCommandExecutor().getCommandPool().getCommands(source);
	}
	
	public static void cancelCommand(String source) {
		HmBeTopoUtil.getCommandExecutor().cancel(source);
	}
	
	public static void clearCommand(String source) {
		Collection<CommandStatus> commands = HmBeTopoUtil.getCommandExecutor().getCommandPool().getCommands(source);
		
		if(commands == null || commands.size() == 0) {
			return ;
		}
		
		for(CommandStatus cmd : commands) {
			if(cmd == null) {
				continue;
			}
			
			cmd.setSequence(-1);
		}
	}
}
