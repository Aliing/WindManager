/**
 * @filename			CommandPool.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.4
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.cli.cliwindow;

import java.util.Collection;
import java.util.List;

/**
 * CommandPool maintains the status of commands, to be executed or have been executed.
 */
public interface CommandPool {
	/**
	 * add a command into the pool
	 * 
	 * @param source	ID of the source associated with the command
	 * @param command	a CommandStatus object to be added
	 * 
	 * @author Joseph Chen
	 */
	void addCommand(String source, CommandStatus command);

	/**
	 * add a collection of commands into the pool
	 * 
	 * @param source	ID of the source associated with the command
	 * @param command	a collection CommandStatus objects to be added
	 * 
	 * @author Joseph Chen
	 */
	void addCommands(String source, Collection<CommandStatus> commands);
	
	/**
	 * remove a collection of commands which are associated with a source
	 * 
	 * @param source	ID of the source
	 * 
	 * @author Joseph Chen
	 */
	void removeCommands(String source);
	
	/**
	 * update the status of a command
	 * 
	 * @param sequence	sequence number of the command
	 * @param result	execution result of the command
	 * 
	 * @author Joseph Chen
	 */
	void updateCommand(long sequence, String result);
	
	/**
	 * update the whole command
	 * 
	 * @param source	source ID of the command
	 * @param status	command status
	 * 
	 * @author Joseph Chen
	 */
	void updateCommand(String source, CommandStatus status);
	/**
	 * get all commands associated with a source
	 * 
	 * @param source	ID of the source
	 * 
	 * @return	a list of commands
	 * 
	 * @author Joseph Chen
	 */
	List<CommandStatus> getCommands(String source);
}
