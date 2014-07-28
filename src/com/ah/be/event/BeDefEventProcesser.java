/**
 *@filename		BeDefEventProcesser.java
 *@version
 *@author		Steven
 *@createtime	2007-9-7 02:48:24
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.event;

import java.util.Queue;
import java.util.Vector;

import com.ah.be.app.BaseModule;


/**
 * @author		Steven
 * @version		V1.0.0.0 
 */
public class BeDefEventProcesser extends Thread implements BeEventProcesser
{

	/*
	 * memeber variable define
	 */
	/**
	 * 
	 */
	BaseModule						EventModule;

	/**
	 * 
	 */
	Queue<BeBaseEvent>				EventQueue;

	/**
	 * 
	 */
	Vector<BeEventDispatchListener>	EventListenerList;

	boolean							runFlag	= true;

	/**
	 * 
	 * Construct method
	 *
	 * @param
	 *
	 * @throws
	 */
	public BeDefEventProcesser()
	{

		super();
	}

	public BeDefEventProcesser(
								BaseModule arg_Module,
								Queue<BeBaseEvent> arg_Queue,
								Vector<BeEventDispatchListener> arg_ListenerList)

	{

		super();

		EventModule = arg_Module;
		EventQueue = arg_Queue;
		EventListenerList = arg_ListenerList;
	}

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void setEventModule(BaseModule arg_Module)
	{

		EventModule = arg_Module;
	}

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void setEventQueue(Queue<BeBaseEvent> arg_Queue)
	{

		EventQueue = arg_Queue;
	}

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void setEventListener(
		Vector<BeEventDispatchListener> arg_ListenerList)
	{

		EventListenerList = arg_ListenerList;
	}

	/** 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{

		myDebug("Event processer begin to run , for module = "
			+ this.EventModule.getModuleId());
		//super.run();
		while (runFlag)
		{
			BeBaseEvent PollEvent = null;
			PollEvent = EventQueue.poll();
			//myDebug("EventQueue size = " + EventQueue.size());
			if (PollEvent != null)
			{
				myDebug("retrieve one valid event from queue");
				for (int i = 0; i < EventListenerList.size(); i++)
				{
					if (EventListenerList.get(i) != null)
					{
						myDebug("generated event is dispatched!");
						EventListenerList.get(i).eventDispatched(PollEvent);
					}
				}
			}
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

	}

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	private void myDebug(String arg_Msg)
	{

		this.EventModule.debug("BeDefEventProcesser::" + arg_Msg);
	}

	/** 
	 * @see com.ah.be.event.BeEventProcesser#testMethod()
	 */
	public void testMethod()
	{
		
	}

	/**
	 * 
	 * @see com.ah.be.event.BeEventProcesser#startProcesser()
	 */
	public void startProcesser()
	{
        this.setName("BeDefEventProcesser");
		this.start();
	}

	/**
	 * 
	 * @see com.ah.be.event.BeEventProcesser#stopProcesser()
	 */
	public void stopProcesser()
	{

		this.runFlag = false;
	}

}
