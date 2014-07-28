package com.ah.be.communication;


public interface ResponseListener
{
	/**
	   * Process a response.
	   */
	  void onResponse(BeCommunicationEvent event);
}
