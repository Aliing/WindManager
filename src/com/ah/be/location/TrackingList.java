/**
 * @filename			TrackingList.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.3
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * list of clients to be tracked and related tracking parameters
 */
public class TrackingList {
	private TrackingParameter parameters;
	
	private List<Tracker> trackers;
	
	private List<TrackingClient> clients;
	
	private boolean isAllClients = false;
	
	public TrackingList() {
		this.trackers = new ArrayList<Tracker>();
		this.clients = new ArrayList<TrackingClient>();
	}
	
	public TrackingList(List<Tracker> trackers,
						List<TrackingClient> clients, 
						TrackingParameter parameters) {
		this.trackers = trackers;
		this.clients = clients;
		this.parameters = parameters;
	}

	public void addClient(TrackingClient client) {
		if(this.clients == null) {
			return ;
		}
		
		this.clients.add(client);
			
	}
	
	public void addClients(Collection<TrackingClient> client) {
		if(this.clients == null) {
			return ;
		}
		
		this.clients.addAll(client);
			
	}
	
	/**
	 * getter of parameters
	 * @return the parameters
	 */
	public TrackingParameter getParameters() {
		return parameters;
	}

	/**
	 * setter of parameters
	 * @param parameters the parameters to set
	 */
	public void setParameters(TrackingParameter parameters) {
		this.parameters = parameters;
	}

	/**
	 * getter of clients
	 * @return the clients
	 */
	public List<TrackingClient> getClients() {
		return clients;
	}

	/**
	 * setter of clients
	 * @param clients the clients to set
	 */
	public void setClients(List<TrackingClient> clients) {
		this.clients = clients;
	}

	/**
	 * getter of tracker
	 * @return the tracker
	 */
	public List<Tracker> getTrackers() {
		return trackers;
	}

	/**
	 * setter of tracker
	 * @param tracker the tracker to set
	 */
	public void setTrackers(List<Tracker> tracker) {
		this.trackers = tracker;
	}
	
	/**
	 * add tracker as the receiver of tracking parameters and client list
	 * 
	 * @param tracker
	 * @author Joseph Chen
	 */
	public void addTracker(Tracker tracker) {
		if(this.trackers == null) {
			this.trackers = new ArrayList<Tracker>();
		}
		
		this.trackers.add(tracker);
	}
	
	/**
	 * add trackers as the receiver of tracking parameters and client list
	 * 
	 * @param tracker
	 * @author Joseph Chen
	 */
	public void addTrackers(Collection<Tracker> tracker) {
		if(this.trackers == null) {
			this.trackers = new ArrayList<Tracker>();
		}
		
		this.trackers.addAll(trackers);
	}
	
	public boolean isAllClients() {
		return this.isAllClients;
	}
	
	public void setAllClients(boolean isAllClients) {
		this.isAllClients = isAllClients;
	}
}
