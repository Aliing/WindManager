package com.ah.be.performance.messagehandle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ah.be.event.BeBaseEvent;

class MessageHandleInfo {
	
	//max concurrent in the same time
	private int		maxConcurrentNum = 1;
	//the number of concurrent in the same time
	private int		concurrentCount = 0;
	//list of message handle interface
	private List<MessageHandleInterface> handleList = null;
	//event queue
	private BlockingQueue<BeBaseEvent> 	eventQueue = null;
	
	public MessageHandleInfo() {
		handleList = new ArrayList<MessageHandleInterface>();
		eventQueue = new LinkedBlockingQueue<BeBaseEvent>(1000);
	}

	public int getMaxConcurrentNum() {
		return maxConcurrentNum;
	}

	public void setMaxConcurrentNum(int maxConcurrentNum) {
		this.maxConcurrentNum = maxConcurrentNum;
	}

	public int getConcurrentCount() {
		return concurrentCount;
	}

	public void setConcurrentCount(int concurrentCount) {
		this.concurrentCount = concurrentCount;
	}

	public List<MessageHandleInterface> getHandleList() {
		return handleList;
	}

	public BlockingQueue<BeBaseEvent> getEventQueue() {
		return eventQueue;
	}
	
}