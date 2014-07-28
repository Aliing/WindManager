
/**
 * ExtensionMapper.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.1  Built on : Aug 31, 2011 (12:23:23 CEST)
 */

        
            package com.ah.hm.ws.platform.service.hmservice;
        
            /**
            *  ExtensionMapper class
            */
            @SuppressWarnings({"unchecked","unused"})
        
        public  class ExtensionMapper{

          public static java.lang.Object getTypeObject(java.lang.String namespaceURI,
                                                       java.lang.String typeName,
                                                       javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{

              
                  if (
                  "http://ah.com/hm/ws/platform/bo/ap".equals(namespaceURI) &&
                  "deviceGroup_type1".equals(typeName)){
                   
                            return  com.ah.hm.ws.platform.bo.ap.DeviceGroup_type1.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://ah.com/hm/ws/platform/bo/ap".equals(namespaceURI) &&
                  "type_type1".equals(typeName)){
                   
                            return  com.ah.hm.ws.platform.bo.ap.Type_type1.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://ah.com/hm/ws/platform/core".equals(namespaceURI) &&
                  "passport".equals(typeName)){
                   
                            return  com.ah.hm.ws.platform.core.Passport.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://ah.com/hm/ws/platform/bo/ap".equals(namespaceURI) &&
                  "model_type1".equals(typeName)){
                   
                            return  com.ah.hm.ws.platform.bo.ap.Model_type1.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://ah.com/hm/ws/platform/bo/ap".equals(namespaceURI) &&
                  "hiveAp".equals(typeName)){
                   
                            return  com.ah.hm.ws.platform.bo.ap.HiveAp.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://ah.com/hm/ws/platform/core".equals(namespaceURI) &&
                  "session".equals(typeName)){
                   
                            return  com.ah.hm.ws.platform.core.Session.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://ah.com/hm/ws/platform/bo/ap".equals(namespaceURI) &&
                  "type_type1".equals(typeName)){
                   
                            return  com.ah.hm.ws.platform.bo.ap.Type_type1.Factory.parse(reader);
                        

                  }

              
             throw new org.apache.axis2.databinding.ADBException("Unsupported type " + namespaceURI + " " + typeName);
          }

        }
    