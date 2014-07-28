/**
 * @filename			CommandExecutorImpl.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.4
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.cli.cliwindow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.event.AhShutdownEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * An implementation of CommandExecutor
 */
public class CommandExecutorImpl implements CommandExecutor {

	private static final Tracer log = new Tracer(CommandExecutorImpl.class.getSimpleName());

	/*
	 * a queue for request command
	 */
	private BlockingQueue<Command> requestQueue;
	
	/*
	 * a queue for command response
	 */
	private BlockingQueue<BeBaseEvent> responseQueue;
	
	/*
	 * a thread to send commands
	 */
	private Thread sender;
	
	/*
	 * a thread to handle command response
	 */
	private Thread handler;
	
	private CommandPool commandPool;
	
	private boolean isInitialized = false;
	
	private boolean isRunning = false;
	
	public CommandExecutorImpl() {
		init();
	}
	
	/* (non-Javadoc)
	 * @see com.ah.be.cli.cliwindow.CommandExecutor#cancel(java.lang.Long)
	 */
	@Override
	public void cancel(String source) {
		commandPool.removeCommands(source);
	}

	/* (non-Javadoc)
	 * @see com.ah.be.cli.cliwindow.CommandExecutor#execute(com.ah.be.cli.cliwindow.Command)
	 */
	@Override
	public void execute(Command command) {
		if(!requestQueue.offer(command)) {
			log.warn("Command (" + command.getCommand() + ") is lost because it cannot " +
					"be inserted into request queue");
		}
	}

	/* (non-Javadoc)
	 * @see com.ah.be.cli.cliwindow.CommandExecutor#init()
	 */
	@Override
	public void init() {
		if(this.isInitialized) {
			/*
			 * has been initialized
			 */
			return ;
		}
		
		log.info("Initializing command executor.");
		requestQueue = new LinkedBlockingQueue<Command>(10000);
		responseQueue = new LinkedBlockingQueue<BeBaseEvent>(30000);
		commandPool = new CommandPoolImpl();
		this.isInitialized = true;
		log.info("Finished initializing command executor.");
	}

	/* (non-Javadoc)
	 * @see com.ah.be.cli.cliwindow.CommandExecutor#run()
	 */
	@Override
	public void run() {
		if(this.isRunning) {
			/*
			 * has been running, return
			 */
			return ;
		}
		
		log.info("<BE Thread> Begin to start the sender of command executor.");
		startSender();
		log.info("<BE Thread> Sender of command executor is running.");
		
		log.info("<BE Thread> Begin to start the handler of command executor.");
		startHandler();
		log.info("<BE Thread> Handler of command executor is running.");
		
		this.isRunning = true;
	}

	/* (non-Javadoc)
	 * @see com.ah.be.cli.cliwindow.CommandExecutor#stop()
	 */
	@Override
	public void stop() {
		log.info("Begin to stop command executor.");
		log.info(requestQueue.size() + " commands are lost when stopping command executor.");
		requestQueue.clear();
		execute(new ShutdownCommand());
		
		log.info(responseQueue.size() + " command responses are lost when stopping command executor.");
		responseQueue.clear();
		handle(new AhShutdownEvent());
		
		this.isRunning = false;
		log.info("Finished stopping command executor.");
	}

	@Override
	public void handle(BeBaseEvent event) {
		
		if(event == null) {
			return ;
		}
		
		if (event.getEventType() != BeEventConst.COMMUNICATIONEVENTTYPE
				&& event.getEventType() != BeEventConst.AH_SHUTDOWN_EVENT) {
			return;
		}
		
		
		if(event.getEventType() == BeEventConst.COMMUNICATIONEVENTTYPE) {
			BeCommunicationEvent commEvent = (BeCommunicationEvent)event;
			
			if(commEvent.getMsgType() != BeCommunicationConstant.MESSAGETYPE_CLIRSP
					&& commEvent.getMsgType() != BeCommunicationConstant.MESSAGEELEMENTTYPE_CLIRESULT) {
				return ;
			}
		}
		
		if(!responseQueue.offer(event)) {
			log.warn("Event (type = " + event.getEventType() + ") is lost because it cannot " +
					"be inserted into response queue");
		}
	}

	@Override
	public void send(Command command) throws Exception {
		if(command == null) {
			throw new NullPointerException("Command is null");
		}
		
		if(command.getSource() == null) {
			throw new Exception("Command has no source.");
		}
		
		List<String> destinations = command.getDestinations();
		
		if(destinations == null) {
			destinations = new ArrayList<String>();
		}
	
		if(destinations.size() == 0) {
			List<CommandStatus> statuses = commandPool.getCommands(command.getSource());
			
			if(statuses == null ||
					statuses.size() == 0) {
				throw new Exception("Command has no destinations.");
			}
			
			for(CommandStatus status : statuses) {
				destinations.add(status.getDestination());
			}
		}
		
		if(!isValidCommand(command)) {
			throw new Exception("The command is invalid.");
		}
		
		String cmdLine = command.getCommand();
		cmdLine += "\n";
		
		/*
		 * get AP from database
		 */
		List<HiveAp> aps = QueryUtil.executeQuery(HiveAp.class, 
				null, new FilterParams("hostname", destinations));
		
		if(aps == null || aps.size() == 0) {
			log.info("send", "Cannot find HiveAp objects for the command: " + command.getCommand());
		}
		
		for(HiveAp ap : aps) {
			if(ap == null) {
				continue;
			}
			
			/*
			 * send event to AP
			 */
			BeCliEvent cliEvent = new BeCliEvent();
			cliEvent.setAp(ap);
			cliEvent.setClis(new String[] {cmdLine});
			cliEvent.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			cliEvent.buildPacket();
			int serialNum = HmBeCommunicationUtil.sendRequest(cliEvent);
			String result;
			
			if(serialNum != BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED) {
				result = Command.RESULT_SENDED;
			} else {
				result = Command.RESULT_SENDED_FAILED;
			}
			
			/*
			 * put into command pool
			 */
			CommandStatus status = new CommandStatus(cmdLine, cliEvent.getSequenceNum(),
					(ap).getHostName());
			status.setResult(result);
			
			commandPool.addCommand(command.getSource(), status);
			
			/*
			 * wait a moment if needed
			 */
			
				try {
				Thread.sleep(20);
				} catch(Exception e) {
					log.error("Exception occurred during thread sleeping.", e);
				}
			}
		}

	@Override
	public boolean isValidCommand(Command command) {
		String cmdLine = command.getCommand();

		if(cmdLine == null
				|| cmdLine.length() == 0) {
			return false;
		}
		
		return true;
	}
	
	private void dispose(BeBaseEvent event) {
		BeCommunicationEvent commEvent = (BeCommunicationEvent)event;
		String result = null;
		
		if(commEvent.getMsgType() == BeCommunicationConstant.MESSAGETYPE_CLIRSP
				&& commEvent.getResult() == BeCommunicationConstant.CLIRESULT_SUCCESS){
			log.info("dispose", "receive cli response, result is success, skip it for result.");
			return;
		}
		
		try {
			result = BeTopoModuleUtil.parseCliRequestResult(commEvent);
		} catch (Exception e) {
			log.error("Failed to parse the CLI result for AP: " + commEvent.getApMac());
		}
		
		if(result == null) {
			return ;
		}
		
		if(result.equals("")) {
			result = Command.RESULT_EXECUTED;
		}
		
		commandPool.updateCommand(commEvent.getSequenceNum(), result);
	}
	
	private void startSender() {
		sender = new Thread(new Runnable() {
			@Override
			public void run() {
				MgrUtil.setTimerName(this.getClass().getSimpleName());
				while(true) {
					Command command = null;
					
					try {
						command = requestQueue.take();
					} catch (InterruptedException e) {
						log.error("Failed to take command from reqeust queue.", e);
					}
					
					if(command == null) {
						continue;
					}
					
					if(command instanceof ShutdownCommand) {
						log.info("<BE Thead> Sender of command executor is shut down.");
						break;
					}
					
					try {
						send(command);
					} catch(Exception e) {
						log.error("Exception occurred during sending command (" + command.getCommand() + ")", e);
					}
				}
				
			}
			
		});
		sender.setName("command sender");		
		sender.start();
	}
	
	private void startHandler() {
		handler = new Thread(new Runnable() {
			@Override
			public void run() {
				MgrUtil.setTimerName(this.getClass().getSimpleName());				
				while(true) {
					BeBaseEvent event = null;
					
					try {
						event = responseQueue.take();
					} catch (InterruptedException e) {
						log.error("Failed to take event from the response queue.", e);
					}
					
					if(event == null) {
						continue;
					}
					
					if(event.getEventType() == BeEventConst.AH_SHUTDOWN_EVENT) {
						/*
						 * stop the handling thread
						 */
						log.info("<BE Thead> Handler of command executor is shut down.");
						break;
					}
					
					try {
						dispose(event);
					} catch(Exception e) {
						log.error("Exception occurred during disposing event (type=" + event.getEventType() + ")", e);
					}
				}
				
			}
			
		});
		
		handler.setName("command handler");
		handler.start();
	}

	@Override
	public CommandPool getCommandPool() {
		return commandPool;
	}

}