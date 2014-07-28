/**
 * @filename			CommandStatus.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.4
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.cli.cliwindow;

/**
 * CommandStatus object maintains the status of a command execution
 */
public class CommandStatus {
	/*
	 * command
	 */
	private String command;
	
	/*
	 * sequence number of the command
	 */
	private long sequence;
	
	/*
	 * destination executor of he command
	 */
	private String destination;
	
	/*
	 * temporary or final result of command execution
	 */
	private String result;
	
	public CommandStatus() {
		this.result = Command.RESULT_PREPARE;
	}
	
	public CommandStatus(String destination) {
		this.destination = destination;
		this.result = Command.RESULT_PREPARE;
	}
	
	public CommandStatus(long sequence, String destination) {
		this.sequence = sequence;
		this.destination = destination;
		this.result = Command.RESULT_PREPARE;
	}

	public CommandStatus(String command, 
			long sequence, 
			String destination) {
		this.command = command;
		this.sequence = sequence;
		this.destination = destination;
		this.result = Command.RESULT_PREPARE;
	}
	

	/**
	 * getter of sequence
	 * @return the sequence
	 */
	public long getSequence() {
		return sequence;
	}

	/**
	 * setter of sequence
	 * @param sequence the sequence to set
	 */
	public void setSequence(long sequence) {
		this.sequence = sequence;
	}

	/**
	 * getter of destination
	 * @return the destination
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * setter of destination
	 * @param destination the destination to set
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}

	/**
	 * getter of result
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * setter of result
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
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
	
	
}
