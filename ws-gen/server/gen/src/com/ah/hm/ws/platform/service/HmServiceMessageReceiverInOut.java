
/**
 * HmServiceMessageReceiverInOut.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.1  Built on : Aug 31, 2011 (12:22:40 CEST)
 */
        package com.ah.hm.ws.platform.service;

        /**
        *  HmServiceMessageReceiverInOut message receiver
        */

        public class HmServiceMessageReceiverInOut extends org.apache.axis2.receivers.AbstractInOutMessageReceiver{


        public void invokeBusinessLogic(org.apache.axis2.context.MessageContext msgContext, org.apache.axis2.context.MessageContext newMsgContext)
        throws org.apache.axis2.AxisFault{

        try {

        // get the implementation class for the Web Service
        Object obj = getTheImplementationObject(msgContext);

        HmService skel = (HmService)obj;
        //Out Envelop
        org.apache.axiom.soap.SOAPEnvelope envelope = null;
        //Find the axisOperation that has been set by the Dispatch phase.
        org.apache.axis2.description.AxisOperation op = msgContext.getOperationContext().getAxisOperation();
        if (op == null) {
        throw new org.apache.axis2.AxisFault("Operation is not located, if this is doclit style the SOAP-ACTION should specified via the SOAP Action to use the RawXMLProvider");
        }

        java.lang.String methodName;
        if((op.getName() != null) && ((methodName = org.apache.axis2.util.JavaUtils.xmlNameToJavaIdentifier(op.getName().getLocalPart())) != null)){


        

            if("getVendorNamesByMacOuis".equals(methodName)){
                
                com.ah.hm.ws.platform.service.hmservice.GetVendorNamesByMacOuisResponse getVendorNamesByMacOuisResponse59 = null;
	                        com.ah.hm.ws.platform.service.hmservice.GetVendorNamesByMacOuis wrappedParam =
                                                             (com.ah.hm.ws.platform.service.hmservice.GetVendorNamesByMacOuis)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.ah.hm.ws.platform.service.hmservice.GetVendorNamesByMacOuis.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               getVendorNamesByMacOuisResponse59 =
                                                   
                                                   
                                                           wrapGetVendorNamesByMacOuisResponseString(
                                                       
                                                        

                                                        
                                                       skel.getVendorNamesByMacOuis(
                                                            
                                                                getMacOuis(wrappedParam)
                                                            )
                                                    
                                                         )
                                                     ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), getVendorNamesByMacOuisResponse59, false, new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/service/HmService",
                                                    "getVendorNamesByMacOuis"));
                                    } else 

            if("getVendorNameByMacOui".equals(methodName)){
                
                com.ah.hm.ws.platform.service.hmservice.GetVendorNameByMacOuiResponse getVendorNameByMacOuiResponse63 = null;
	                        com.ah.hm.ws.platform.service.hmservice.GetVendorNameByMacOui wrappedParam =
                                                             (com.ah.hm.ws.platform.service.hmservice.GetVendorNameByMacOui)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.ah.hm.ws.platform.service.hmservice.GetVendorNameByMacOui.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               getVendorNameByMacOuiResponse63 =
                                                   
                                                   
                                                           wrapGetVendorNameByMacOuiResponseString(
                                                       
                                                        

                                                        
                                                       skel.getVendorNameByMacOui(
                                                            
                                                                getMacOui(wrappedParam)
                                                            )
                                                    
                                                         )
                                                     ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), getVendorNameByMacOuiResponse63, false, new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/service/HmService",
                                                    "getVendorNameByMacOui"));
                                    } else 

            if("fetchHiveApsByClientNamesWithCase".equals(methodName)){
                
                com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesWithCaseResponse fetchHiveApsByClientNamesWithCaseResponse68 = null;
	                        com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesWithCase wrappedParam =
                                                             (com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesWithCase)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesWithCase.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               fetchHiveApsByClientNamesWithCaseResponse68 =
                                                   
                                                   
                                                           wrapFetchHiveApsByClientNamesWithCaseResponseHiveAp(
                                                       
                                                        

                                                        
                                                       skel.fetchHiveApsByClientNamesWithCase(
                                                            
                                                                getClientNames(wrappedParam)
                                                            ,
                                                                getCaseSensitive(wrappedParam)
                                                            )
                                                    
                                                         )
                                                     ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), fetchHiveApsByClientNamesWithCaseResponse68, false, new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/service/HmService",
                                                    "fetchHiveApsByClientNamesWithCase"));
                                    } else 

            if("keepSessionAlive".equals(methodName)){
                
                com.ah.hm.ws.platform.service.hmservice.KeepSessionAliveResponse keepSessionAliveResponse71 = null;
	                        com.ah.hm.ws.platform.service.hmservice.KeepSessionAlive wrappedParam =
                                                             (com.ah.hm.ws.platform.service.hmservice.KeepSessionAlive)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.ah.hm.ws.platform.service.hmservice.KeepSessionAlive.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               keepSessionAliveResponse71 =
                                                   
                                                   
                                                           wrapkeepSessionAlive();
                                                       
                                                        

                                                        
                                                       skel.keepSessionAlive(
                                                            )
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), keepSessionAliveResponse71, false, new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/service/HmService",
                                                    "keepSessionAlive"));
                                    } else 

            if("fetchHiveApByClientMac".equals(methodName)){
                
                com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientMacResponse fetchHiveApByClientMacResponse74 = null;
	                        com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientMac wrappedParam =
                                                             (com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientMac)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientMac.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               fetchHiveApByClientMacResponse74 =
                                                   
                                                   
                                                           wrapFetchHiveApByClientMacResponseHiveAp(
                                                       
                                                        

                                                        
                                                       skel.fetchHiveApByClientMac(
                                                            
                                                                getClientMac(wrappedParam)
                                                            )
                                                    
                                                         )
                                                     ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), fetchHiveApByClientMacResponse74, false, new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/service/HmService",
                                                    "fetchHiveApByClientMac"));
                                    } else 

            if("fetchAllHiveAps".equals(methodName)){
                
                com.ah.hm.ws.platform.service.hmservice.FetchAllHiveApsResponse fetchAllHiveApsResponse77 = null;
	                        com.ah.hm.ws.platform.service.hmservice.FetchAllHiveAps wrappedParam =
                                                             (com.ah.hm.ws.platform.service.hmservice.FetchAllHiveAps)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.ah.hm.ws.platform.service.hmservice.FetchAllHiveAps.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               fetchAllHiveApsResponse77 =
                                                   
                                                   
                                                           wrapFetchAllHiveApsResponseHiveAp(
                                                       
                                                        

                                                        
                                                       skel.fetchAllHiveAps(
                                                            )
                                                    
                                                         )
                                                     ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), fetchAllHiveApsResponse77, false, new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/service/HmService",
                                                    "fetchAllHiveAps"));
                                    } else 

            if("login".equals(methodName)){
                
                com.ah.hm.ws.platform.service.hmservice.LoginResponse loginResponse81 = null;
	                        com.ah.hm.ws.platform.service.hmservice.Login wrappedParam =
                                                             (com.ah.hm.ws.platform.service.hmservice.Login)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.ah.hm.ws.platform.service.hmservice.Login.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               loginResponse81 =
                                                   
                                                   
                                                           wrapLoginResponseSession(
                                                       
                                                        

                                                        
                                                       skel.login(
                                                            
                                                                getPassport(wrappedParam)
                                                            )
                                                    
                                                         )
                                                     ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), loginResponse81, false, new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/service/HmService",
                                                    "login"));
                                    } else 

            if("fetchHiveApByNodeId".equals(methodName)){
                
                com.ah.hm.ws.platform.service.hmservice.FetchHiveApByNodeIdResponse fetchHiveApByNodeIdResponse85 = null;
	                        com.ah.hm.ws.platform.service.hmservice.FetchHiveApByNodeId wrappedParam =
                                                             (com.ah.hm.ws.platform.service.hmservice.FetchHiveApByNodeId)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.ah.hm.ws.platform.service.hmservice.FetchHiveApByNodeId.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               fetchHiveApByNodeIdResponse85 =
                                                   
                                                   
                                                           wrapFetchHiveApByNodeIdResponseHiveAp(
                                                       
                                                        

                                                        
                                                       skel.fetchHiveApByNodeId(
                                                            
                                                                getNodeId(wrappedParam)
                                                            )
                                                    
                                                         )
                                                     ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), fetchHiveApByNodeIdResponse85, false, new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/service/HmService",
                                                    "fetchHiveApByNodeId"));
                                    } else 

            if("fetchHiveApByClientName".equals(methodName)){
                
                com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientNameResponse fetchHiveApByClientNameResponse89 = null;
	                        com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientName wrappedParam =
                                                             (com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientName)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientName.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               fetchHiveApByClientNameResponse89 =
                                                   
                                                   
                                                           wrapFetchHiveApByClientNameResponseHiveAp(
                                                       
                                                        

                                                        
                                                       skel.fetchHiveApByClientName(
                                                            
                                                                getClientName(wrappedParam)
                                                            )
                                                    
                                                         )
                                                     ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), fetchHiveApByClientNameResponse89, false, new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/service/HmService",
                                                    "fetchHiveApByClientName"));
                                    } else 

            if("logout".equals(methodName)){
                
                com.ah.hm.ws.platform.service.hmservice.LogoutResponse logoutResponse92 = null;
	                        com.ah.hm.ws.platform.service.hmservice.Logout wrappedParam =
                                                             (com.ah.hm.ws.platform.service.hmservice.Logout)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.ah.hm.ws.platform.service.hmservice.Logout.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               logoutResponse92 =
                                                   
                                                   
                                                           wraplogout();
                                                       
                                                        

                                                        
                                                       skel.logout(
                                                            )
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), logoutResponse92, false, new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/service/HmService",
                                                    "logout"));
                                    } else 

            if("fetchHiveApsByClientMacs".equals(methodName)){
                
                com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientMacsResponse fetchHiveApsByClientMacsResponse95 = null;
	                        com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientMacs wrappedParam =
                                                             (com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientMacs)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientMacs.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               fetchHiveApsByClientMacsResponse95 =
                                                   
                                                   
                                                           wrapFetchHiveApsByClientMacsResponseHiveAp(
                                                       
                                                        

                                                        
                                                       skel.fetchHiveApsByClientMacs(
                                                            
                                                                getClientMacs(wrappedParam)
                                                            )
                                                    
                                                         )
                                                     ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), fetchHiveApsByClientMacsResponse95, false, new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/service/HmService",
                                                    "fetchHiveApsByClientMacs"));
                                    } else 

            if("fetchHiveApsByClientNames".equals(methodName)){
                
                com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesResponse fetchHiveApsByClientNamesResponse99 = null;
	                        com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNames wrappedParam =
                                                             (com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNames)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNames.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               fetchHiveApsByClientNamesResponse99 =
                                                   
                                                   
                                                           wrapFetchHiveApsByClientNamesResponseHiveAp(
                                                       
                                                        

                                                        
                                                       skel.fetchHiveApsByClientNames(
                                                            
                                                                getClientNames(wrappedParam)
                                                            )
                                                    
                                                         )
                                                     ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), fetchHiveApsByClientNamesResponse99, false, new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/service/HmService",
                                                    "fetchHiveApsByClientNames"));
                                    } else 

            if("fetchHiveApsByNodeIds".equals(methodName)){
                
                com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByNodeIdsResponse fetchHiveApsByNodeIdsResponse103 = null;
	                        com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByNodeIds wrappedParam =
                                                             (com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByNodeIds)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByNodeIds.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               fetchHiveApsByNodeIdsResponse103 =
                                                   
                                                   
                                                           wrapFetchHiveApsByNodeIdsResponseHiveAp(
                                                       
                                                        

                                                        
                                                       skel.fetchHiveApsByNodeIds(
                                                            
                                                                getNodeIds(wrappedParam)
                                                            )
                                                    
                                                         )
                                                     ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), fetchHiveApsByNodeIdsResponse103, false, new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/service/HmService",
                                                    "fetchHiveApsByNodeIds"));
                                    
            } else {
              throw new java.lang.RuntimeException("method not found");
            }
        

        newMsgContext.setEnvelope(envelope);
        }
        } catch (SoapFault e) {

            msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME,"soap");
            org.apache.axis2.AxisFault f = createAxisFault(e);
            if (e.getFaultMessage() != null){
                f.setDetail(toOM(e.getFaultMessage(),false));
            }
            throw f;
            }
        
        catch (java.lang.Exception e) {
        throw org.apache.axis2.AxisFault.makeFault(e);
        }
        }
        
        //
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.GetVendorNamesByMacOuis param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.GetVendorNamesByMacOuis.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.GetVendorNamesByMacOuisResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.GetVendorNamesByMacOuisResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.Soap param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.Soap.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.GetVendorNameByMacOui param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.GetVendorNameByMacOui.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.GetVendorNameByMacOuiResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.GetVendorNameByMacOuiResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesWithCase param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesWithCase.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesWithCaseResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesWithCaseResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.KeepSessionAlive param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.KeepSessionAlive.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.KeepSessionAliveResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.KeepSessionAliveResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientMac param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientMac.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientMacResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientMacResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.FetchAllHiveAps param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.FetchAllHiveAps.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.FetchAllHiveApsResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.FetchAllHiveApsResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.Login param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.Login.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.LoginResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.LoginResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.FetchHiveApByNodeId param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.FetchHiveApByNodeId.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.FetchHiveApByNodeIdResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.FetchHiveApByNodeIdResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientName param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientName.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientNameResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientNameResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.Logout param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.Logout.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.LogoutResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.LogoutResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientMacs param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientMacs.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientMacsResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientMacsResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNames param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNames.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByNodeIds param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByNodeIds.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByNodeIdsResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByNodeIdsResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.ah.hm.ws.platform.service.hmservice.GetVendorNamesByMacOuisResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.ah.hm.ws.platform.service.hmservice.GetVendorNamesByMacOuisResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    

                        private java.lang.String[] getMacOuis(
                        com.ah.hm.ws.platform.service.hmservice.GetVendorNamesByMacOuis wrappedType){
                        
                                return wrappedType.getMacOuis();
                            
                        }
                     

                        
                        private com.ah.hm.ws.platform.service.hmservice.GetVendorNamesByMacOuisResponse wrapGetVendorNamesByMacOuisResponseString(
                        java.lang.String[] param){
                        com.ah.hm.ws.platform.service.hmservice.GetVendorNamesByMacOuisResponse wrappedElement = new com.ah.hm.ws.platform.service.hmservice.GetVendorNamesByMacOuisResponse();
                        
                                wrappedElement.setString(param);
                            
                            return wrappedElement;
                        }
                     
                         private com.ah.hm.ws.platform.service.hmservice.GetVendorNamesByMacOuisResponse wrapgetVendorNamesByMacOuis(){
                                com.ah.hm.ws.platform.service.hmservice.GetVendorNamesByMacOuisResponse wrappedElement = new com.ah.hm.ws.platform.service.hmservice.GetVendorNamesByMacOuisResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.ah.hm.ws.platform.service.hmservice.GetVendorNameByMacOuiResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.ah.hm.ws.platform.service.hmservice.GetVendorNameByMacOuiResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    

                        private java.lang.String getMacOui(
                        com.ah.hm.ws.platform.service.hmservice.GetVendorNameByMacOui wrappedType){
                        
                                return wrappedType.getMacOui();
                            
                        }
                     

                        
                        private com.ah.hm.ws.platform.service.hmservice.GetVendorNameByMacOuiResponse wrapGetVendorNameByMacOuiResponseString(
                        java.lang.String param){
                        com.ah.hm.ws.platform.service.hmservice.GetVendorNameByMacOuiResponse wrappedElement = new com.ah.hm.ws.platform.service.hmservice.GetVendorNameByMacOuiResponse();
                        
                                wrappedElement.setString(param);
                            
                            return wrappedElement;
                        }
                     
                         private com.ah.hm.ws.platform.service.hmservice.GetVendorNameByMacOuiResponse wrapgetVendorNameByMacOui(){
                                com.ah.hm.ws.platform.service.hmservice.GetVendorNameByMacOuiResponse wrappedElement = new com.ah.hm.ws.platform.service.hmservice.GetVendorNameByMacOuiResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesWithCaseResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesWithCaseResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    

                        private java.lang.String[] getClientNames(
                        com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesWithCase wrappedType){
                        
                                return wrappedType.getClientNames();
                            
                        }
                     

                        private boolean getCaseSensitive(
                        com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesWithCase wrappedType){
                        
                                return wrappedType.getCaseSensitive();
                            
                        }
                     

                        
                        private com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesWithCaseResponse wrapFetchHiveApsByClientNamesWithCaseResponseHiveAp(
                        com.ah.hm.ws.platform.bo.ap.HiveAp[] param){
                        com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesWithCaseResponse wrappedElement = new com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesWithCaseResponse();
                        
                                wrappedElement.setHiveAp(param);
                            
                            return wrappedElement;
                        }
                     
                         private com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesWithCaseResponse wrapfetchHiveApsByClientNamesWithCase(){
                                com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesWithCaseResponse wrappedElement = new com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesWithCaseResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.ah.hm.ws.platform.service.hmservice.KeepSessionAliveResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.ah.hm.ws.platform.service.hmservice.KeepSessionAliveResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.ah.hm.ws.platform.service.hmservice.KeepSessionAliveResponse wrapkeepSessionAlive(){
                                com.ah.hm.ws.platform.service.hmservice.KeepSessionAliveResponse wrappedElement = new com.ah.hm.ws.platform.service.hmservice.KeepSessionAliveResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientMacResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientMacResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    

                        private java.lang.String getClientMac(
                        com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientMac wrappedType){
                        
                                return wrappedType.getClientMac();
                            
                        }
                     

                        
                        private com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientMacResponse wrapFetchHiveApByClientMacResponseHiveAp(
                        com.ah.hm.ws.platform.bo.ap.HiveAp param){
                        com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientMacResponse wrappedElement = new com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientMacResponse();
                        
                                wrappedElement.setHiveAp(param);
                            
                            return wrappedElement;
                        }
                     
                         private com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientMacResponse wrapfetchHiveApByClientMac(){
                                com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientMacResponse wrappedElement = new com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientMacResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.ah.hm.ws.platform.service.hmservice.FetchAllHiveApsResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.ah.hm.ws.platform.service.hmservice.FetchAllHiveApsResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    

                        
                        private com.ah.hm.ws.platform.service.hmservice.FetchAllHiveApsResponse wrapFetchAllHiveApsResponseHiveAp(
                        com.ah.hm.ws.platform.bo.ap.HiveAp[] param){
                        com.ah.hm.ws.platform.service.hmservice.FetchAllHiveApsResponse wrappedElement = new com.ah.hm.ws.platform.service.hmservice.FetchAllHiveApsResponse();
                        
                                wrappedElement.setHiveAp(param);
                            
                            return wrappedElement;
                        }
                     
                         private com.ah.hm.ws.platform.service.hmservice.FetchAllHiveApsResponse wrapfetchAllHiveAps(){
                                com.ah.hm.ws.platform.service.hmservice.FetchAllHiveApsResponse wrappedElement = new com.ah.hm.ws.platform.service.hmservice.FetchAllHiveApsResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.ah.hm.ws.platform.service.hmservice.LoginResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.ah.hm.ws.platform.service.hmservice.LoginResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    

                        private com.ah.hm.ws.platform.core.Passport getPassport(
                        com.ah.hm.ws.platform.service.hmservice.Login wrappedType){
                        
                                return wrappedType.getPassport();
                            
                        }
                     

                        
                        private com.ah.hm.ws.platform.service.hmservice.LoginResponse wrapLoginResponseSession(
                        com.ah.hm.ws.platform.core.Session param){
                        com.ah.hm.ws.platform.service.hmservice.LoginResponse wrappedElement = new com.ah.hm.ws.platform.service.hmservice.LoginResponse();
                        
                                wrappedElement.setSession(param);
                            
                            return wrappedElement;
                        }
                     
                         private com.ah.hm.ws.platform.service.hmservice.LoginResponse wraplogin(){
                                com.ah.hm.ws.platform.service.hmservice.LoginResponse wrappedElement = new com.ah.hm.ws.platform.service.hmservice.LoginResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.ah.hm.ws.platform.service.hmservice.FetchHiveApByNodeIdResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.ah.hm.ws.platform.service.hmservice.FetchHiveApByNodeIdResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    

                        private java.lang.String getNodeId(
                        com.ah.hm.ws.platform.service.hmservice.FetchHiveApByNodeId wrappedType){
                        
                                return wrappedType.getNodeId();
                            
                        }
                     

                        
                        private com.ah.hm.ws.platform.service.hmservice.FetchHiveApByNodeIdResponse wrapFetchHiveApByNodeIdResponseHiveAp(
                        com.ah.hm.ws.platform.bo.ap.HiveAp param){
                        com.ah.hm.ws.platform.service.hmservice.FetchHiveApByNodeIdResponse wrappedElement = new com.ah.hm.ws.platform.service.hmservice.FetchHiveApByNodeIdResponse();
                        
                                wrappedElement.setHiveAp(param);
                            
                            return wrappedElement;
                        }
                     
                         private com.ah.hm.ws.platform.service.hmservice.FetchHiveApByNodeIdResponse wrapfetchHiveApByNodeId(){
                                com.ah.hm.ws.platform.service.hmservice.FetchHiveApByNodeIdResponse wrappedElement = new com.ah.hm.ws.platform.service.hmservice.FetchHiveApByNodeIdResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientNameResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientNameResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    

                        private java.lang.String getClientName(
                        com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientName wrappedType){
                        
                                return wrappedType.getClientName();
                            
                        }
                     

                        
                        private com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientNameResponse wrapFetchHiveApByClientNameResponseHiveAp(
                        com.ah.hm.ws.platform.bo.ap.HiveAp param){
                        com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientNameResponse wrappedElement = new com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientNameResponse();
                        
                                wrappedElement.setHiveAp(param);
                            
                            return wrappedElement;
                        }
                     
                         private com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientNameResponse wrapfetchHiveApByClientName(){
                                com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientNameResponse wrappedElement = new com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientNameResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.ah.hm.ws.platform.service.hmservice.LogoutResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.ah.hm.ws.platform.service.hmservice.LogoutResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.ah.hm.ws.platform.service.hmservice.LogoutResponse wraplogout(){
                                com.ah.hm.ws.platform.service.hmservice.LogoutResponse wrappedElement = new com.ah.hm.ws.platform.service.hmservice.LogoutResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientMacsResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientMacsResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    

                        private java.lang.String[] getClientMacs(
                        com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientMacs wrappedType){
                        
                                return wrappedType.getClientMacs();
                            
                        }
                     

                        
                        private com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientMacsResponse wrapFetchHiveApsByClientMacsResponseHiveAp(
                        com.ah.hm.ws.platform.bo.ap.HiveAp[] param){
                        com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientMacsResponse wrappedElement = new com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientMacsResponse();
                        
                                wrappedElement.setHiveAp(param);
                            
                            return wrappedElement;
                        }
                     
                         private com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientMacsResponse wrapfetchHiveApsByClientMacs(){
                                com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientMacsResponse wrappedElement = new com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientMacsResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    

                        private java.lang.String[] getClientNames(
                        com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNames wrappedType){
                        
                                return wrappedType.getClientNames();
                            
                        }
                     

                        
                        private com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesResponse wrapFetchHiveApsByClientNamesResponseHiveAp(
                        com.ah.hm.ws.platform.bo.ap.HiveAp[] param){
                        com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesResponse wrappedElement = new com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesResponse();
                        
                                wrappedElement.setHiveAp(param);
                            
                            return wrappedElement;
                        }
                     
                         private com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesResponse wrapfetchHiveApsByClientNames(){
                                com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesResponse wrappedElement = new com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByNodeIdsResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByNodeIdsResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    

                        private java.lang.String[] getNodeIds(
                        com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByNodeIds wrappedType){
                        
                                return wrappedType.getNodeIds();
                            
                        }
                     

                        
                        private com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByNodeIdsResponse wrapFetchHiveApsByNodeIdsResponseHiveAp(
                        com.ah.hm.ws.platform.bo.ap.HiveAp[] param){
                        com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByNodeIdsResponse wrappedElement = new com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByNodeIdsResponse();
                        
                                wrappedElement.setHiveAp(param);
                            
                            return wrappedElement;
                        }
                     
                         private com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByNodeIdsResponse wrapfetchHiveApsByNodeIds(){
                                com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByNodeIdsResponse wrappedElement = new com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByNodeIdsResponse();
                                return wrappedElement;
                         }
                    


        /**
        *  get the default envelope
        */
        private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory){
        return factory.getDefaultEnvelope();
        }


        private  java.lang.Object fromOM(
        org.apache.axiom.om.OMElement param,
        java.lang.Class type,
        java.util.Map extraNamespaces) throws org.apache.axis2.AxisFault{

        try {
        
                if (com.ah.hm.ws.platform.service.hmservice.GetVendorNamesByMacOuis.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.GetVendorNamesByMacOuis.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.GetVendorNamesByMacOuisResponse.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.GetVendorNamesByMacOuisResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.Soap.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.Soap.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.GetVendorNameByMacOui.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.GetVendorNameByMacOui.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.GetVendorNameByMacOuiResponse.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.GetVendorNameByMacOuiResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.Soap.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.Soap.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesWithCase.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesWithCase.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesWithCaseResponse.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesWithCaseResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.Soap.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.Soap.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.KeepSessionAlive.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.KeepSessionAlive.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.KeepSessionAliveResponse.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.KeepSessionAliveResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.Soap.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.Soap.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientMac.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientMac.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientMacResponse.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientMacResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.Soap.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.Soap.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.FetchAllHiveAps.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.FetchAllHiveAps.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.FetchAllHiveApsResponse.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.FetchAllHiveApsResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.Soap.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.Soap.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.Login.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.Login.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.LoginResponse.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.LoginResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.Soap.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.Soap.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.FetchHiveApByNodeId.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.FetchHiveApByNodeId.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.FetchHiveApByNodeIdResponse.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.FetchHiveApByNodeIdResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.Soap.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.Soap.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientName.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientName.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientNameResponse.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.FetchHiveApByClientNameResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.Soap.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.Soap.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.Logout.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.Logout.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.LogoutResponse.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.LogoutResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.Soap.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.Soap.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientMacs.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientMacs.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientMacsResponse.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientMacsResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.Soap.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.Soap.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNames.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNames.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesResponse.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByClientNamesResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.Soap.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.Soap.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByNodeIds.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByNodeIds.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByNodeIdsResponse.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.FetchHiveApsByNodeIdsResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ah.hm.ws.platform.service.hmservice.Soap.class.equals(type)){
                
                           return com.ah.hm.ws.platform.service.hmservice.Soap.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
        } catch (java.lang.Exception e) {
        throw org.apache.axis2.AxisFault.makeFault(e);
        }
           return null;
        }



    

        /**
        *  A utility method that copies the namepaces from the SOAPEnvelope
        */
        private java.util.Map getEnvelopeNamespaces(org.apache.axiom.soap.SOAPEnvelope env){
        java.util.Map returnMap = new java.util.HashMap();
        java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
        while (namespaceIterator.hasNext()) {
        org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
        returnMap.put(ns.getPrefix(),ns.getNamespaceURI());
        }
        return returnMap;
        }

        private org.apache.axis2.AxisFault createAxisFault(java.lang.Exception e) {
        org.apache.axis2.AxisFault f;
        Throwable cause = e.getCause();
        if (cause != null) {
            f = new org.apache.axis2.AxisFault(e.getMessage(), cause);
        } else {
            f = new org.apache.axis2.AxisFault(e.getMessage());
        }

        return f;
    }

        }//end of class
    