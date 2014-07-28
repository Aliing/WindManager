/**
 *@filename		BeEventModuleImpl.java
 *@version
 *@author		Steven
 *@createtime	2007-9-4 09:59:31
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ah.be.app.BaseModule;
import com.ah.util.Tracer;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public class BeEventModuleImpl extends BaseModule
													implements
													BeEventModule,
													BeEventListener
{

	private static final Tracer log = new Tracer(BeEventModuleImpl.class.getSimpleName());

	/*
	 * Member variable define
	 */

	public static final int						MAX_MODULES		= 20;

	/**
	 * 
	 */
	private final List<BaseModule>				ModuleList;

	/**
	 * 
	 */
	// private List<BeEventProcesser> ProcesserList;
	/**
	 * 
	 */
	// private List<Vector<BeEventDispatchListener>> ListenerList;
	/**
	 * 
	 */
	// private List<Queue<BeBaseEvent>> EventQueueList;
	/**
	 * 
	 */
	private final BlockingQueue<BeBaseEvent>			EventQueue;

	/**
	 * 
	 */
	private final Collection<BeEventDispatchListener>	eventDispatcherList;

	/**
	 * 
	 */
	private static final int					eventQueueSize	= 40000;

	/**
	 * 
	 */
	private Thread								EventProcessThread;

	/**
	 * Construct method
	 */
	public BeEventModuleImpl()
	{
		super();
		setModuleId(BaseModule.ModuleID_Event);
		setModuleName("BeEventModule");

		getDebuger().setModuleId(BaseModule.ModuleID_Event);
		getDebuger().setModuleName("BeEventModule");

		ModuleList = new ArrayList<BaseModule>(MAX_MODULES);
		for (int i = 0; i < MAX_MODULES; i++)
		{
			ModuleList.add(null);
		}
		// ProcesserList = new ArrayList<BeEventProcesser>(MAX_MODULES);
		// for (int i = 0; i < MAX_MODULES; i++)
		// {
		// ProcesserList.add(null);
		// }
		// ListenerList = new ArrayList<Vector<BeEventDispatchListener>>(
		// MAX_MODULES);
		// for (int i = 0; i < MAX_MODULES; i++)
		// {
		// ListenerList.add(null);
		// ListenerList.set(i, new Vector<BeEventDispatchListener>());
		// }
		// EventQueueList = new ArrayList<Queue<BeBaseEvent>>(MAX_MODULES);
		// for (int i = 0; i < MAX_MODULES; i++)
		// {
		// EventQueueList.add(null);
		// EventQueueList.set(i, new LinkedList<BeBaseEvent>());
		// }

		EventQueue = new LinkedBlockingQueue<BeBaseEvent>(eventQueueSize);
		eventDispatcherList = new LinkedList<BeEventDispatchListener>();
	}

	/**
	 * @see com.ah.be.event.BeEventModule#testMethod()
	 */
	@Override
	public void testMethod()
	{

	}

	/**
	 * @see com.ah.be.event.BeEventListener#eventGenerated(com.ah.be.event.BeBaseEvent)
	 */
	@Override
	public void eventGenerated(BeBaseEvent arg_Event)
	{
		try
		{
//			EventQueue.add(arg_Event);
//			DebugUtil
//				.commonDebugInfo("event module receive system event , queue size = "
//					+ EventQueue.size());

			if (!EventQueue.offer(arg_Event)) {
				log.error("eventGenerated", "The global forwarding queue was full.");
			}
		}
		catch (Exception e)
		{
			log.error("eventGenerated", "Adding event to queue error.", e);
		}
	}

	/**
	 * @see com.ah.be.event.BeEventModule#registeEventDispatchListener(com.ah.be.event.BeEventDispatchListener)
	 */
	public void registeEventDispatchListener(
			BeEventDispatchListener arg_Dispatcher)
	{
		synchronized (eventDispatcherList)
		{
			eventDispatcherList.add(arg_Dispatcher);
		}

		log.info("registeEventDispatchListener", "Event dispatcher added, dispatcher size = " + eventDispatcherList.size());
	}

	public void unregisterEventDispatchListener(
		BeEventDispatchListener arg_Dispatcher)
	{
		synchronized (eventDispatcherList)
		{
			eventDispatcherList.remove(arg_Dispatcher);
		}

		log.info("unregisterEventDispatchListener", "Event dispatcher removed, dispatcher size = " + eventDispatcherList.size());
	}

	/**
	 * @see com.ah.be.event.BeEventListener#getListenerId()
	 */
	public int getListenerId()
	{
		return getModuleId();
	}

	private boolean	isContinue	= true;

	public void startEventProcesser(/* BaseModule arg_Module */)
	{
		if (EventProcessThread != null)
		{
			return;
		}

		EventProcessThread = new Thread()
		{
			@Override
			public void run()
			{
				log.info("run", "<BE Thread> Event processor is running...");

				while (isContinue)
				{
					try
					{
						// take() method blocks
						BeBaseEvent event = EventQueue.take();
//						DebugUtil
//							.commonDebugInfo("event process thread take a event:moduleId = "
//								+ event.getMouleId()
//								+ " eventType = "
//								+ event.getEventType());

						/*
						 * notify all event dispatch listener
						 */
						notifyEventDispatcher(event);
					}
					catch (Exception e)
					{
						log.error("run", "Take operation interrupted while waiting.", e);
					}
				}

				log.info("run", "<BE Thread> Event processor shutdown.");
			}
		};
		
        EventProcessThread.setName("EventProcessThread");
		EventProcessThread.start();
	}

	public void notifyEventDispatcher(BeBaseEvent arg_Event)
	{
		// if (EventDispatcherList == null)
		// {
		// debug("Event dispatcher list has no object.");
		// return;
		// }

		// i think synchronize is not necessary here, bcz EventDispatcherList
		// create when process create and cleared when process terminate.

		// synchronized (EventDispatcherList)
		// {
		for (BeEventDispatchListener listener : eventDispatcherList)
		{
			try
			{
				listener.eventDispatched(arg_Event);
			}
			catch (Exception e)
			{
				log.error("notifyEventDispatcher", "Event dispatch error.", e);
			}
		}
		// }
	}

	/**
	 * @see com.ah.be.app.BaseModule#shutdown()
	 */
	@Override
	public boolean shutdown()
	{
		/*
		 * create event process shutdown signal and put it to the event queue
		 */
		log.info("shutdown", "Event module shutdown method called.");
		isContinue = false;
		BeBaseEvent shutdown = new BeBaseEvent();
		// shutdown.setModuleId(BaseModule.ModuleID_Event);
		// shutdown.setEventType(BeEventConst.Be_Event_ShutDown);
		eventGenerated(shutdown);
		log.info("shutdown", "Event module shutdown method ended.");
		return true;
	}

}