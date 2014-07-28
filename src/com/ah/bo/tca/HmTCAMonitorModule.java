/**   
* @Title: HmTCAMonitorModule.java 
* @Package com.aerohive.test 
* @Description: TODO(write something)
* @author xxu   
* @date 2012-8-2 
* @version V1.0   
*/
package com.ah.bo.tca;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.ah.be.common.NmsUtil;
import com.ah.util.Tracer;

/** 
 * @ClassName: HmTCAMonitorModule 
 * @Description: TODO
 * @author xxu
 * @date 2012-8-2 
 *  
 */
public class HmTCAMonitorModule {
	
	private static final HmTCAMonitorModule m_instance = new HmTCAMonitorModule();
	
	
	private ScheduledThreadPoolExecutor scheduledExec = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(3);

	public static final String DISKUSAGE="DiskUsage";
	
	private Map<String,ScheduledFuture<?>> taskMap=new ConcurrentHashMap<String, ScheduledFuture<?>>();
	
	private static final Tracer log	= new Tracer(HmTCAMonitorModule.class.getSimpleName());
	
	
	private HmTCAMonitorModule(){
		
	}
	
	/**
	 * 
	 * @return
	 */
	public static HmTCAMonitorModule getInstance() {        
		return m_instance;        
		}     
		
	
	public boolean updateTask(TCAAlarm alarm){
		TCAMonitorRunnable r=TCAUtils.convertTCAAlarm(alarm);
		if(r==null){
			return false;
		}
		return updateTask(r);
		
	}
	
	
	public synchronized boolean updateTask(TCAMonitorRunnable tcaRunnable){
		boolean result = false;
		if(removeTask(tcaRunnable.getName())){
			result = addTask(tcaRunnable);
		}
		
		return result;
		
	}
	
	public void startMonitor(){
		//TCA alarm don't work for hmOnline
		if( NmsUtil.isHostedHMApplication()){
			return;
		}
		List<TCAMonitorRunnable> runList=TCAUtils.getTCAMonitorList();
		for(int i=0;i<runList.size();i++){
			addTask(runList.get(i));
		}
	}
	
	public void stopMonitor(){
		//TCA alarm don't work for hmOnline
		if( NmsUtil.isHostedHMApplication()){
			return;
		}
		taskMap.clear();
		try{
//		BlockingQueue<Runnable> queue=scheduledExec.getQueue();
//		while(!queue.isEmpty()){
//			queue.remove();
//		}
		scheduledExec.shutdown();
		} catch (Exception e) {
			return;
		} 
	}
	
	/**
	 * 
	 * @param tcaRunnable
	 * @param interval task run interval, it's seconds
	 * @return
	 */
	public synchronized boolean addTask(TCAMonitorRunnable tcaRunnable){
		try {
			ScheduledFuture<?> scheduledFuture = scheduledExec.scheduleWithFixedDelay(tcaRunnable, 0, tcaRunnable.getInterval(), tcaRunnable.getTimeUnit());
			taskMap.put(tcaRunnable.getName(), scheduledFuture);
			return true;
		} catch (Exception e) {
			log.error("execute", "TCA Monitor add Task Failed!", e);
			return false;
		}
	}
	
	/**
	 * 
	 * @param taskName
	 * @return true if task is moved successfully
	 */
	public synchronized boolean removeTask(String taskName){
		ScheduledFuture<?> scheduledFuture=taskMap.get(taskName);
		if(scheduledFuture == null){
			return false;
		}
		BlockingQueue<Runnable> queue=scheduledExec.getQueue();
		boolean result = queue.remove(scheduledFuture);
		if(result){
			taskMap.remove(taskName);
		}
		return result;
	}
	
	
}
