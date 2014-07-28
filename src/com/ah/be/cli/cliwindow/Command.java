/**
 * @filename			Command.java
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

/**
 * Command object is used for request sender to send a request from 
 * a source to one or more destinations.
 */
public class Command {
	/*
	 * the command to be executed
	 */
	private String command;
	
	/*
	 * ID of the source station which sends a command 
	 */
	private String source;
	
	/*
	 * the destination stations a command is sent to
	 */
	private List<String> destinations;
	
	public static final String RESULT_PREPARE				= "Ready";
	
	public static final String RESULT_SENDED 				= "The command was sent.";
	
	public static final String RESULT_SENDED_FAILED 		= "Failed to send the command.";
	
	public static final String RESULT_EXECUTED 				= "The command was executed. No returned result.";
	
	public Command() {
		destinations = new ArrayList<String>();
	}

	public Command(String source, String command) {
		this.source = source;
		this.command = command;
		destinations = new ArrayList<String>();
	}
	
	/**
	 * getter of command
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * setter of command
	 * @param command the command to set
	 */
	public void setCommand(String command) {
		this.command = command;
	}

	/**
	 * getter of source
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * setter of source
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * getter of destinations
	 * @return the destinations
	 */
	public List<String> getDestinations() {
		return destinations;
	}

	/**
	 * setter of destinations
	 * @param destinations the destinations to set
	 */
	public void setDestinations(List<String> destinations) {
		this.destinations = destinations;
	}
	
	/**
	 * add a destination to the command
	 * 
	 * @param destination	the destination to be added
	 * 
	 * @author Joseph Chen
	 */
	public void addDestination(String destination) {
		if(this.destinations == null) {
			this.destinations = new ArrayList<String>();
		}
		
		this.destinations.add(destination);
	}
	
	/**
	 * add a collection of destinations to the command
	 * 
	 * @param destination	the destination to be added
	 * 
	 * @author Joseph Chen
	 */
	public void addDestinations(Collection<String> destinations) {
		if(this.destinations == null) {
			this.destinations = new ArrayList<String>();
		}
		
		this.destinations.addAll(destinations);
	}
	
}
