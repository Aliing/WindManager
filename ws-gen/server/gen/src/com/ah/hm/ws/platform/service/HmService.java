
/**
 * HmService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.1  Built on : Aug 31, 2011 (12:22:40 CEST)
 */
    package com.ah.hm.ws.platform.service;
    /**
     *  HmService java skeleton interface for the axisService
     */
    public interface HmService {
     
         
        /**
         * Auto generated method signature
         * 
                                        * @param macOuis
             * @throws SoapFault : 
         */

        
                public java.lang.String[] getVendorNamesByMacOuis
                (
                  java.lang.String[] macOuis
                 )
            throws SoapFault;
        
         
        /**
         * Auto generated method signature
         * 
                                        * @param macOui
             * @throws SoapFault : 
         */

        
                public java.lang.String getVendorNameByMacOui
                (
                  java.lang.String macOui
                 )
            throws SoapFault;
        
         
        /**
         * Auto generated method signature
         * 
                                        * @param clientNames
                                        * @param caseSensitive
             * @throws SoapFault : 
         */

        
                public com.ah.hm.ws.platform.bo.ap.HiveAp[] fetchHiveApsByClientNamesWithCase
                (
                  java.lang.String[] clientNames,boolean caseSensitive
                 )
            throws SoapFault;
        
         
        /**
         * Auto generated method signature
         * 
             * @throws SoapFault : 
         */

        
                public void keepSessionAlive
                (
                  
                 )
            throws SoapFault;
        
         
        /**
         * Auto generated method signature
         * 
                                        * @param clientMac
             * @throws SoapFault : 
         */

        
                public com.ah.hm.ws.platform.bo.ap.HiveAp fetchHiveApByClientMac
                (
                  java.lang.String clientMac
                 )
            throws SoapFault;
        
         
        /**
         * Auto generated method signature
         * 
             * @throws SoapFault : 
         */

        
                public com.ah.hm.ws.platform.bo.ap.HiveAp[] fetchAllHiveAps
                (
                  
                 )
            throws SoapFault;
        
         
        /**
         * Auto generated method signature
         * 
                                        * @param passport
             * @throws SoapFault : 
         */

        
                public com.ah.hm.ws.platform.core.Session login
                (
                  com.ah.hm.ws.platform.core.Passport passport
                 )
            throws SoapFault;
        
         
        /**
         * Auto generated method signature
         * 
                                        * @param nodeId
             * @throws SoapFault : 
         */

        
                public com.ah.hm.ws.platform.bo.ap.HiveAp fetchHiveApByNodeId
                (
                  java.lang.String nodeId
                 )
            throws SoapFault;
        
         
        /**
         * Auto generated method signature
         * 
                                        * @param clientName
             * @throws SoapFault : 
         */

        
                public com.ah.hm.ws.platform.bo.ap.HiveAp fetchHiveApByClientName
                (
                  java.lang.String clientName
                 )
            throws SoapFault;
        
         
        /**
         * Auto generated method signature
         * 
             * @throws SoapFault : 
         */

        
                public void logout
                (
                  
                 )
            throws SoapFault;
        
         
        /**
         * Auto generated method signature
         * 
                                        * @param clientMacs
             * @throws SoapFault : 
         */

        
                public com.ah.hm.ws.platform.bo.ap.HiveAp[] fetchHiveApsByClientMacs
                (
                  java.lang.String[] clientMacs
                 )
            throws SoapFault;
        
         
        /**
         * Auto generated method signature
         * 
                                        * @param clientNames6
             * @throws SoapFault : 
         */

        
                public com.ah.hm.ws.platform.bo.ap.HiveAp[] fetchHiveApsByClientNames
                (
                  java.lang.String[] clientNames6
                 )
            throws SoapFault;
        
         
        /**
         * Auto generated method signature
         * 
                                        * @param nodeIds
             * @throws SoapFault : 
         */

        
                public com.ah.hm.ws.platform.bo.ap.HiveAp[] fetchHiveApsByNodeIds
                (
                  java.lang.String[] nodeIds
                 )
            throws SoapFault;
        
         }
    