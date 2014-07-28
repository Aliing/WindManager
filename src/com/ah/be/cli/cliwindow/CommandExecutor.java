/**
 * @filename			CommandExecutor.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.4
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.cli.cliwindow;

import com.ah.be.event.BeBaseEvent;

/**
 * A CommandExecutor can execute commands and maintain the result of execution
 */
public interface CommandExecutor {

	/**
	 * initialize command executor 
	 * 
	 * @author Joseph Chen
	 */
	void init();
	
	/**
	 * run command executor
	 * 
	 * @author Joseph Chen
	 */
	void run();
	
	/**
	 * stop command executor
	 * 
	 * @author Joseph Chen
	 */
	void stop();
	
	/**
	 * execute command
	 * 
	 * @param command	the Command object to be executed
	 * 
	 * @author Joseph Chen
	 */
	void execute(Command command);
	
	/**
	 * handle event of command response
	 * 
	 * @param event		the event of command respose
	 * 
	 * @author Joseph Chen
	 */
	void handle(BeBaseEvent event);
	
	/**
	 * cancel commands related to a source
	 * 
	 * @param source	the ID of given source
	 * 
	 * @author Joseph Chen
	 */
	void cancel(String source);
	
	/**
	 * send a command to execution destination
	 * 
	 * @param command	the command to be sent
	 * 
	 * @author Joseph Chen
	 */
	void send(Command command) throws Exception;
	
	/**
	 * check if the command line is valid or not
	 * 
	 * @param command	the given command 
	 * 
	 * @return	true: if command is valid; 
	 * 			false: if command is not valid;
	 *  
	 * @author Joseph Chen
	 */
	boolean isValidCommand(Command command);
	
	/**
	 * get command pool of the executor
	 * 
	 * @return
	 * @author Joseph Chen
	 */
	CommandPool getCommandPool();
}
