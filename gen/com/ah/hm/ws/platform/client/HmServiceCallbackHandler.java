
/**
 * HmServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.1  Built on : Aug 31, 2011 (12:22:40 CEST)
 */

    package com.ah.hm.ws.platform.client;

    /**
     *  HmServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class HmServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public HmServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public HmServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for getVendorNamesByMacOuis method
            * override this method for handling normal response from getVendorNamesByMacOuis operation
            */
           public void receiveResultgetVendorNamesByMacOuis(
                    java.lang.String[] result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getVendorNamesByMacOuis operation
           */
            public void receiveErrorgetVendorNamesByMacOuis(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getVendorNameByMacOui method
            * override this method for handling normal response from getVendorNameByMacOui operation
            */
           public void receiveResultgetVendorNameByMacOui(
                    java.lang.String result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getVendorNameByMacOui operation
           */
            public void receiveErrorgetVendorNameByMacOui(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fetchHiveApsByClientNamesWithCase method
            * override this method for handling normal response from fetchHiveApsByClientNamesWithCase operation
            */
           public void receiveResultfetchHiveApsByClientNamesWithCase(
                    com.ah.hm.ws.platform.client.HmServiceStub.HiveAp[] result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fetchHiveApsByClientNamesWithCase operation
           */
            public void receiveErrorfetchHiveApsByClientNamesWithCase(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for keepSessionAlive method
            * override this method for handling normal response from keepSessionAlive operation
            */
           public void receiveResultkeepSessionAlive(
                    ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from keepSessionAlive operation
           */
            public void receiveErrorkeepSessionAlive(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fetchHiveApByClientMac method
            * override this method for handling normal response from fetchHiveApByClientMac operation
            */
           public void receiveResultfetchHiveApByClientMac(
                    com.ah.hm.ws.platform.client.HmServiceStub.HiveAp result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fetchHiveApByClientMac operation
           */
            public void receiveErrorfetchHiveApByClientMac(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fetchAllHiveAps method
            * override this method for handling normal response from fetchAllHiveAps operation
            */
           public void receiveResultfetchAllHiveAps(
                    com.ah.hm.ws.platform.client.HmServiceStub.HiveAp[] result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fetchAllHiveAps operation
           */
            public void receiveErrorfetchAllHiveAps(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for login method
            * override this method for handling normal response from login operation
            */
           public void receiveResultlogin(
                    com.ah.hm.ws.platform.client.HmServiceStub.Session result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from login operation
           */
            public void receiveErrorlogin(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fetchHiveApByNodeId method
            * override this method for handling normal response from fetchHiveApByNodeId operation
            */
           public void receiveResultfetchHiveApByNodeId(
                    com.ah.hm.ws.platform.client.HmServiceStub.HiveAp result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fetchHiveApByNodeId operation
           */
            public void receiveErrorfetchHiveApByNodeId(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fetchHiveApByClientName method
            * override this method for handling normal response from fetchHiveApByClientName operation
            */
           public void receiveResultfetchHiveApByClientName(
                    com.ah.hm.ws.platform.client.HmServiceStub.HiveAp result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fetchHiveApByClientName operation
           */
            public void receiveErrorfetchHiveApByClientName(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for logout method
            * override this method for handling normal response from logout operation
            */
           public void receiveResultlogout(
                    ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from logout operation
           */
            public void receiveErrorlogout(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fetchHiveApsByClientMacs method
            * override this method for handling normal response from fetchHiveApsByClientMacs operation
            */
           public void receiveResultfetchHiveApsByClientMacs(
                    com.ah.hm.ws.platform.client.HmServiceStub.HiveAp[] result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fetchHiveApsByClientMacs operation
           */
            public void receiveErrorfetchHiveApsByClientMacs(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fetchHiveApsByClientNames method
            * override this method for handling normal response from fetchHiveApsByClientNames operation
            */
           public void receiveResultfetchHiveApsByClientNames(
                    com.ah.hm.ws.platform.client.HmServiceStub.HiveAp[] result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fetchHiveApsByClientNames operation
           */
            public void receiveErrorfetchHiveApsByClientNames(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fetchHiveApsByNodeIds method
            * override this method for handling normal response from fetchHiveApsByNodeIds operation
            */
           public void receiveResultfetchHiveApsByNodeIds(
                    com.ah.hm.ws.platform.client.HmServiceStub.HiveAp[] result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fetchHiveApsByNodeIds operation
           */
            public void receiveErrorfetchHiveApsByNodeIds(java.lang.Exception e) {
            }
                


    }
    