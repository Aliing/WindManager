
/**
 * HiveAp.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.1  Built on : Aug 31, 2011 (12:23:23 CEST)
 */

            
                package com.ah.hm.ws.platform.bo.ap;
            

            /**
            *  HiveAp bean class
            */
            @SuppressWarnings({"unchecked","unused"})
        
        public  class HiveAp
        implements org.apache.axis2.databinding.ADBBean{
        /* This type was generated from the piece of schema that had
                name = hiveAp
                Namespace URI = http://ah.com/hm/ws/platform/bo/ap
                Namespace Prefix = 
                */
            

                        /**
                        * field for NodeId
                        */

                        
                                    protected java.lang.String localNodeId ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localNodeIdTracker = false ;

                           public boolean isNodeIdSpecified(){
                               return localNodeIdTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getNodeId(){
                               return localNodeId;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param NodeId
                               */
                               public void setNodeId(java.lang.String param){
                            localNodeIdTracker = param != null;
                                   
                                            this.localNodeId=param;
                                    

                               }
                            

                        /**
                        * field for SerialNumber
                        */

                        
                                    protected java.lang.String localSerialNumber ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localSerialNumberTracker = false ;

                           public boolean isSerialNumberSpecified(){
                               return localSerialNumberTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getSerialNumber(){
                               return localSerialNumber;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param SerialNumber
                               */
                               public void setSerialNumber(java.lang.String param){
                            localSerialNumberTracker = param != null;
                                   
                                            this.localSerialNumber=param;
                                    

                               }
                            

                        /**
                        * field for HostName
                        */

                        
                                    protected java.lang.String localHostName ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localHostNameTracker = false ;

                           public boolean isHostNameSpecified(){
                               return localHostNameTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getHostName(){
                               return localHostName;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param HostName
                               */
                               public void setHostName(java.lang.String param){
                            localHostNameTracker = param != null;
                                   
                                            this.localHostName=param;
                                    

                               }
                            

                        /**
                        * field for IpAddress
                        */

                        
                                    protected java.lang.String localIpAddress ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localIpAddressTracker = false ;

                           public boolean isIpAddressSpecified(){
                               return localIpAddressTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getIpAddress(){
                               return localIpAddress;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param IpAddress
                               */
                               public void setIpAddress(java.lang.String param){
                            localIpAddressTracker = param != null;
                                   
                                            this.localIpAddress=param;
                                    

                               }
                            

                        /**
                        * field for Netmask
                        */

                        
                                    protected java.lang.String localNetmask ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localNetmaskTracker = false ;

                           public boolean isNetmaskSpecified(){
                               return localNetmaskTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getNetmask(){
                               return localNetmask;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Netmask
                               */
                               public void setNetmask(java.lang.String param){
                            localNetmaskTracker = param != null;
                                   
                                            this.localNetmask=param;
                                    

                               }
                            

                        /**
                        * field for Gateway
                        */

                        
                                    protected java.lang.String localGateway ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localGatewayTracker = false ;

                           public boolean isGatewaySpecified(){
                               return localGatewayTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getGateway(){
                               return localGateway;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Gateway
                               */
                               public void setGateway(java.lang.String param){
                            localGatewayTracker = param != null;
                                   
                                            this.localGateway=param;
                                    

                               }
                            

                        /**
                        * field for SoftwareVersion
                        */

                        
                                    protected java.lang.String localSoftwareVersion ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localSoftwareVersionTracker = false ;

                           public boolean isSoftwareVersionSpecified(){
                               return localSoftwareVersionTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getSoftwareVersion(){
                               return localSoftwareVersion;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param SoftwareVersion
                               */
                               public void setSoftwareVersion(java.lang.String param){
                            localSoftwareVersionTracker = param != null;
                                   
                                            this.localSoftwareVersion=param;
                                    

                               }
                            

                        /**
                        * field for DeviceGroup
                        */

                        
                                    protected com.ah.hm.ws.platform.bo.ap.DeviceGroup_type1 localDeviceGroup ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localDeviceGroupTracker = false ;

                           public boolean isDeviceGroupSpecified(){
                               return localDeviceGroupTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return com.ah.hm.ws.platform.bo.ap.DeviceGroup_type1
                           */
                           public  com.ah.hm.ws.platform.bo.ap.DeviceGroup_type1 getDeviceGroup(){
                               return localDeviceGroup;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param DeviceGroup
                               */
                               public void setDeviceGroup(com.ah.hm.ws.platform.bo.ap.DeviceGroup_type1 param){
                            localDeviceGroupTracker = param != null;
                                   
                                            this.localDeviceGroup=param;
                                    

                               }
                            

                        /**
                        * field for Model
                        */

                        
                                    protected com.ah.hm.ws.platform.bo.ap.Model_type1 localModel ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localModelTracker = false ;

                           public boolean isModelSpecified(){
                               return localModelTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return com.ah.hm.ws.platform.bo.ap.Model_type1
                           */
                           public  com.ah.hm.ws.platform.bo.ap.Model_type1 getModel(){
                               return localModel;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Model
                               */
                               public void setModel(com.ah.hm.ws.platform.bo.ap.Model_type1 param){
                            localModelTracker = param != null;
                                   
                                            this.localModel=param;
                                    

                               }
                            

                        /**
                        * field for Type
                        */

                        
                                    protected com.ah.hm.ws.platform.bo.ap.Type_type1 localType ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localTypeTracker = false ;

                           public boolean isTypeSpecified(){
                               return localTypeTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return com.ah.hm.ws.platform.bo.ap.Type_type1
                           */
                           public  com.ah.hm.ws.platform.bo.ap.Type_type1 getType(){
                               return localType;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Type
                               */
                               public void setType(com.ah.hm.ws.platform.bo.ap.Type_type1 param){
                            localTypeTracker = param != null;
                                   
                                            this.localType=param;
                                    

                               }
                            

                        /**
                        * field for NativeVlan
                        * This was an Attribute!
                        */

                        
                                    protected int localNativeVlan ;
                                

                           /**
                           * Auto generated getter method
                           * @return int
                           */
                           public  int getNativeVlan(){
                               return localNativeVlan;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param NativeVlan
                               */
                               public void setNativeVlan(int param){
                            
                                            this.localNativeVlan=param;
                                    

                               }
                            

                        /**
                        * field for MgtVlan
                        * This was an Attribute!
                        */

                        
                                    protected int localMgtVlan ;
                                

                           /**
                           * Auto generated getter method
                           * @return int
                           */
                           public  int getMgtVlan(){
                               return localMgtVlan;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param MgtVlan
                               */
                               public void setMgtVlan(int param){
                            
                                            this.localMgtVlan=param;
                                    

                               }
                            

                        /**
                        * field for Active
                        * This was an Attribute!
                        */

                        
                                    protected boolean localActive ;
                                

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getActive(){
                               return localActive;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Active
                               */
                               public void setActive(boolean param){
                            
                                            this.localActive=param;
                                    

                               }
                            

     
     
        /**
        *
        * @param parentQName
        * @param factory
        * @return org.apache.axiom.om.OMElement
        */
       public org.apache.axiom.om.OMElement getOMElement (
               final javax.xml.namespace.QName parentQName,
               final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException{


        
               org.apache.axiom.om.OMDataSource dataSource =
                       new org.apache.axis2.databinding.ADBDataSource(this,parentQName);
               return factory.createOMElement(dataSource,parentQName);
            
        }

         public void serialize(final javax.xml.namespace.QName parentQName,
                                       javax.xml.stream.XMLStreamWriter xmlWriter)
                                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
                           serialize(parentQName,xmlWriter,false);
         }

         public void serialize(final javax.xml.namespace.QName parentQName,
                               javax.xml.stream.XMLStreamWriter xmlWriter,
                               boolean serializeType)
            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
            
                


                java.lang.String prefix = null;
                java.lang.String namespace = null;
                

                    prefix = parentQName.getPrefix();
                    namespace = parentQName.getNamespaceURI();
                    writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);
                
                  if (serializeType){
               

                   java.lang.String namespacePrefix = registerPrefix(xmlWriter,"http://ah.com/hm/ws/platform/bo/ap");
                   if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)){
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           namespacePrefix+":hiveAp",
                           xmlWriter);
                   } else {
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           "hiveAp",
                           xmlWriter);
                   }

               
                   }
               
                                                   if (localNativeVlan!=java.lang.Integer.MIN_VALUE) {
                                               
                                                writeAttribute("",
                                                         "nativeVlan",
                                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNativeVlan), xmlWriter);

                                            
                                      }
                                    
                                      else {
                                          throw new org.apache.axis2.databinding.ADBException("required attribute localNativeVlan is null");
                                      }
                                    
                                                   if (localMgtVlan!=java.lang.Integer.MIN_VALUE) {
                                               
                                                writeAttribute("",
                                                         "mgtVlan",
                                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMgtVlan), xmlWriter);

                                            
                                      }
                                    
                                      else {
                                          throw new org.apache.axis2.databinding.ADBException("required attribute localMgtVlan is null");
                                      }
                                    
                                                   if (true) {
                                               
                                                writeAttribute("",
                                                         "active",
                                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localActive), xmlWriter);

                                            
                                      }
                                    
                                      else {
                                          throw new org.apache.axis2.databinding.ADBException("required attribute localActive is null");
                                      }
                                     if (localNodeIdTracker){
                                    namespace = "http://ah.com/hm/ws/platform/bo/ap";
                                    writeStartElement(null, namespace, "nodeId", xmlWriter);
                             

                                          if (localNodeId==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("nodeId cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localNodeId);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localSerialNumberTracker){
                                    namespace = "http://ah.com/hm/ws/platform/bo/ap";
                                    writeStartElement(null, namespace, "serialNumber", xmlWriter);
                             

                                          if (localSerialNumber==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("serialNumber cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localSerialNumber);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localHostNameTracker){
                                    namespace = "http://ah.com/hm/ws/platform/bo/ap";
                                    writeStartElement(null, namespace, "hostName", xmlWriter);
                             

                                          if (localHostName==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("hostName cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localHostName);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localIpAddressTracker){
                                    namespace = "http://ah.com/hm/ws/platform/bo/ap";
                                    writeStartElement(null, namespace, "ipAddress", xmlWriter);
                             

                                          if (localIpAddress==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("ipAddress cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localIpAddress);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localNetmaskTracker){
                                    namespace = "http://ah.com/hm/ws/platform/bo/ap";
                                    writeStartElement(null, namespace, "netmask", xmlWriter);
                             

                                          if (localNetmask==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("netmask cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localNetmask);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localGatewayTracker){
                                    namespace = "http://ah.com/hm/ws/platform/bo/ap";
                                    writeStartElement(null, namespace, "gateway", xmlWriter);
                             

                                          if (localGateway==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("gateway cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localGateway);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localSoftwareVersionTracker){
                                    namespace = "http://ah.com/hm/ws/platform/bo/ap";
                                    writeStartElement(null, namespace, "softwareVersion", xmlWriter);
                             

                                          if (localSoftwareVersion==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("softwareVersion cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localSoftwareVersion);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localDeviceGroupTracker){
                                            if (localDeviceGroup==null){
                                                 throw new org.apache.axis2.databinding.ADBException("deviceGroup cannot be null!!");
                                            }
                                           localDeviceGroup.serialize(new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/bo/ap","deviceGroup"),
                                               xmlWriter);
                                        } if (localModelTracker){
                                            if (localModel==null){
                                                 throw new org.apache.axis2.databinding.ADBException("model cannot be null!!");
                                            }
                                           localModel.serialize(new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/bo/ap","model"),
                                               xmlWriter);
                                        } if (localTypeTracker){
                                            if (localType==null){
                                                 throw new org.apache.axis2.databinding.ADBException("type cannot be null!!");
                                            }
                                           localType.serialize(new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/bo/ap","type"),
                                               xmlWriter);
                                        }
                    xmlWriter.writeEndElement();
               

        }

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if(namespace.equals("http://ah.com/hm/ws/platform/bo/ap")){
                return "";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
                                       javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }
        
        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(java.lang.String prefix,java.lang.String namespace,java.lang.String attName,
                                    java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace,attName,attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(java.lang.String namespace,java.lang.String attName,
                                    java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName,attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace,attName,attValue);
            }
        }


           /**
             * Util method to write an attribute without the ns prefix
             */
            private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                                             javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

                java.lang.String attributeNamespace = qname.getNamespaceURI();
                java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
                if (attributePrefix == null) {
                    attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
                }
                java.lang.String attributeValue;
                if (attributePrefix.trim().length() > 0) {
                    attributeValue = attributePrefix + ":" + qname.getLocalPart();
                } else {
                    attributeValue = qname.getLocalPart();
                }

                if (namespace.equals("")) {
                    xmlWriter.writeAttribute(attName, attributeValue);
                } else {
                    registerPrefix(xmlWriter, namespace);
                    xmlWriter.writeAttribute(namespace, attName, attributeValue);
                }
            }
        /**
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix,namespaceURI);
                }

                if (prefix.trim().length() > 0){
                    xmlWriter.writeCharacters(prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                                 javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix,namespaceURI);
                        }

                        if (prefix.trim().length() > 0){
                            stringToWrite.append(prefix).append(":").append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }


        /**
         * Register a namespace prefix
         */
        private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace) throws javax.xml.stream.XMLStreamException {
            java.lang.String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    java.lang.String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }


  
        /**
        * databinding method to get an XML representation of this object
        *
        */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                    throws org.apache.axis2.databinding.ADBException{


        
                 java.util.ArrayList elementList = new java.util.ArrayList();
                 java.util.ArrayList attribList = new java.util.ArrayList();

                 if (localNodeIdTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/bo/ap",
                                                                      "nodeId"));
                                 
                                        if (localNodeId != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNodeId));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("nodeId cannot be null!!");
                                        }
                                    } if (localSerialNumberTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/bo/ap",
                                                                      "serialNumber"));
                                 
                                        if (localSerialNumber != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSerialNumber));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("serialNumber cannot be null!!");
                                        }
                                    } if (localHostNameTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/bo/ap",
                                                                      "hostName"));
                                 
                                        if (localHostName != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localHostName));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("hostName cannot be null!!");
                                        }
                                    } if (localIpAddressTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/bo/ap",
                                                                      "ipAddress"));
                                 
                                        if (localIpAddress != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localIpAddress));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("ipAddress cannot be null!!");
                                        }
                                    } if (localNetmaskTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/bo/ap",
                                                                      "netmask"));
                                 
                                        if (localNetmask != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNetmask));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("netmask cannot be null!!");
                                        }
                                    } if (localGatewayTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/bo/ap",
                                                                      "gateway"));
                                 
                                        if (localGateway != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localGateway));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("gateway cannot be null!!");
                                        }
                                    } if (localSoftwareVersionTracker){
                                      elementList.add(new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/bo/ap",
                                                                      "softwareVersion"));
                                 
                                        if (localSoftwareVersion != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSoftwareVersion));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("softwareVersion cannot be null!!");
                                        }
                                    } if (localDeviceGroupTracker){
                            elementList.add(new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/bo/ap",
                                                                      "deviceGroup"));
                            
                            
                                    if (localDeviceGroup==null){
                                         throw new org.apache.axis2.databinding.ADBException("deviceGroup cannot be null!!");
                                    }
                                    elementList.add(localDeviceGroup);
                                } if (localModelTracker){
                            elementList.add(new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/bo/ap",
                                                                      "model"));
                            
                            
                                    if (localModel==null){
                                         throw new org.apache.axis2.databinding.ADBException("model cannot be null!!");
                                    }
                                    elementList.add(localModel);
                                } if (localTypeTracker){
                            elementList.add(new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/bo/ap",
                                                                      "type"));
                            
                            
                                    if (localType==null){
                                         throw new org.apache.axis2.databinding.ADBException("type cannot be null!!");
                                    }
                                    elementList.add(localType);
                                }
                            attribList.add(
                            new javax.xml.namespace.QName("","nativeVlan"));
                            
                                      attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNativeVlan));
                                
                            attribList.add(
                            new javax.xml.namespace.QName("","mgtVlan"));
                            
                                      attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMgtVlan));
                                
                            attribList.add(
                            new javax.xml.namespace.QName("","active"));
                            
                                      attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localActive));
                                

                return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(), attribList.toArray());
            
            

        }

  

     /**
      *  Factory class that keeps the parse method
      */
    public static class Factory{

        
        

        /**
        * static method to create the object
        * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
        *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
        * Postcondition: If this object is an element, the reader is positioned at its end element
        *                If this object is a complex type, the reader is positioned at the end element of its outer element
        */
        public static HiveAp parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            HiveAp object =
                new HiveAp();

            int event;
            java.lang.String nillableValue = null;
            java.lang.String prefix ="";
            java.lang.String namespaceuri ="";
            try {
                
                while (!reader.isStartElement() && !reader.isEndElement())
                    reader.next();

                
                if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","type")!=null){
                  java.lang.String fullTypeName = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                        "type");
                  if (fullTypeName!=null){
                    java.lang.String nsPrefix = null;
                    if (fullTypeName.indexOf(":") > -1){
                        nsPrefix = fullTypeName.substring(0,fullTypeName.indexOf(":"));
                    }
                    nsPrefix = nsPrefix==null?"":nsPrefix;

                    java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":")+1);
                    
                            if (!"hiveAp".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (HiveAp)com.ah.hm.ws.platform.service.hmservice.ExtensionMapper.getTypeObject(
                                     nsUri,type,reader);
                              }
                        

                  }
                

                }

                

                
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();
                

                
                    // handle attribute "nativeVlan"
                    java.lang.String tempAttribNativeVlan =
                        
                                reader.getAttributeValue(null,"nativeVlan");
                            
                   if (tempAttribNativeVlan!=null){
                         java.lang.String content = tempAttribNativeVlan;
                        
                                                 object.setNativeVlan(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(tempAttribNativeVlan));
                                            
                    } else {
                       
                               throw new org.apache.axis2.databinding.ADBException("Required attribute nativeVlan is missing");
                           
                    }
                    handledAttributes.add("nativeVlan");
                    
                    // handle attribute "mgtVlan"
                    java.lang.String tempAttribMgtVlan =
                        
                                reader.getAttributeValue(null,"mgtVlan");
                            
                   if (tempAttribMgtVlan!=null){
                         java.lang.String content = tempAttribMgtVlan;
                        
                                                 object.setMgtVlan(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(tempAttribMgtVlan));
                                            
                    } else {
                       
                               throw new org.apache.axis2.databinding.ADBException("Required attribute mgtVlan is missing");
                           
                    }
                    handledAttributes.add("mgtVlan");
                    
                    // handle attribute "active"
                    java.lang.String tempAttribActive =
                        
                                reader.getAttributeValue(null,"active");
                            
                   if (tempAttribActive!=null){
                         java.lang.String content = tempAttribActive;
                        
                                                 object.setActive(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(tempAttribActive));
                                            
                    } else {
                       
                               throw new org.apache.axis2.databinding.ADBException("Required attribute active is missing");
                           
                    }
                    handledAttributes.add("active");
                    
                    
                    reader.next();
                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/bo/ap","nodeId").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setNodeId(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/bo/ap","serialNumber").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setSerialNumber(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/bo/ap","hostName").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setHostName(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/bo/ap","ipAddress").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setIpAddress(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/bo/ap","netmask").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setNetmask(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/bo/ap","gateway").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setGateway(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/bo/ap","softwareVersion").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setSoftwareVersion(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/bo/ap","deviceGroup").equals(reader.getName())){
                                
                                                object.setDeviceGroup(com.ah.hm.ws.platform.bo.ap.DeviceGroup_type1.Factory.parse(reader));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/bo/ap","model").equals(reader.getName())){
                                
                                                object.setModel(com.ah.hm.ws.platform.bo.ap.Model_type1.Factory.parse(reader));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://ah.com/hm/ws/platform/bo/ap","type").equals(reader.getName())){
                                
                                                object.setType(com.ah.hm.ws.platform.bo.ap.Type_type1.Factory.parse(reader));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                  
                            while (!reader.isStartElement() && !reader.isEndElement())
                                reader.next();
                            
                                if (reader.isStartElement())
                                // A start element we are not expecting indicates a trailing invalid property
                                throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                            



            } catch (javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }

            return object;
        }

        }//end of factory class

        

        }
           
    