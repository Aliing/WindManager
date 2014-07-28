/**
 * @filename			CommandPoolImpl.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.4
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.cli.cliwindow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An implementation of CommandPool
 * 
 * The basic data structure is HashMap.
 */
public class CommandPoolImpl implements CommandPool {

	/*
	 * a pool of Map to store commands
	 */
	final Map<String, List<CommandStatus>> pool;
	
	public CommandPoolImpl() {
		pool = new ConcurrentHashMap<String, List<CommandStatus>>();
	}
	
	/* (non-Javadoc)
	 * @see com.ah.be.cli.cliwindow.CommandPool#addCommand(long, com.ah.be.cli.cliwindow.CommandStatus)
	 */
	@Override
	public void addCommand(String source, CommandStatus command) {
		List<CommandStatus> commands = pool.get(source);
		
		if(commands == null) {
			commands = new ArrayList<CommandStatus>();
			commands.add(command);
			pool.put(source, commands);
		} else {
			commands.add(command);
		}
	}

	/* (non-Javadoc)
	 * @see com.ah.be.cli.cliwindow.CommandPool#addCommands(long, java.util.Collection)
	 */
	@Override
	public void addCommands(String source, Collection<CommandStatus> commands) {
		if(source == null) {
			return ;
		}
		
		if(commands == null || commands.size() == 0) {
			return ;
		}
		
		List<CommandStatus> existing = pool.get(source);
		
		if(existing == null) {
			List<CommandStatus> comming = new ArrayList<CommandStatus>();
			comming.addAll(commands);
			pool.put(source, comming);
		} else {
			existing.addAll(commands);
		}
	}

	/* (non-Javadoc)
	 * @see com.ah.be.cli.cliwindow.CommandPool#getCommands(long)
	 */
	@Override
	public List<CommandStatus> getCommands(String source) {
		synchronized(this) {
			return pool.get(source);
		}
	}

	/* (non-Javadoc)
	 * @see com.ah.be.cli.cliwindow.CommandPool#removeCommands(java.lang.Long)
	 */
	@Override
	public void removeCommands(String source) {
		pool.remove(source);
	}

	/* (non-Javadoc)
	 * @see com.ah.be.cli.cliwindow.CommandPool#updateCommand(long, java.lang.String)
	 */
	@Override
	public void updateCommand(long sequence, String result) {
		synchronized(this) {
			for (String source : pool.keySet()) {
				List<CommandStatus> commands = pool.get(source);

				if (commands == null) {
					continue;
				}

				for (CommandStatus command : commands) {
					if (command == null) {
						continue;
					}

					if (command.getSequence() == sequence) {
						command.setResult(result);
						return;
					}
				}
			}
		}
	}

	@Override
	public void updateCommand(String source, CommandStatus status) {
		if(source == null || status == null) {
			return ;
		}
		
		List<CommandStatus> commands = pool.get(source);
		
		if(commands == null) {
			return ;
		}
		
		for(CommandStatus command : commands) {
			if(command == null) {
				continue;
			}
			
			if(command.getDestination().equals(status.getDestination())) {
				command.setCommand(status.getCommand());
				command.setResult(status.getResult());
				command.setSequence(status.getSequence());
				return ;
			}
		}
	}

}