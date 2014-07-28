
/**
 * SoapFault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.1  Built on : Aug 31, 2011 (12:22:40 CEST)
 */

package com.ah.hm.ws.platform.service;

public class SoapFault extends org.apache.axis2.AxisFault{

    private static final long serialVersionUID = 1403769807958L;
    
    private com.ah.hm.ws.platform.service.hmservice.Soap faultMessage;

    
             public SoapFault(org.apache.axiom.soap.SOAPFaultCode sOAPFaultCode,org.apache.axiom.soap.SOAPFaultReason sOAPFaultReason,org.apache.axiom.soap.SOAPFaultNode sOAPFaultNode,org.apache.axiom.soap.SOAPFaultRole sOAPFaultRole,org.apache.axiom.soap.SOAPFaultDetail sOAPFaultDetail) {
                super(sOAPFaultCode,sOAPFaultReason,sOAPFaultNode,sOAPFaultRole,sOAPFaultDetail);
            }
        
             public SoapFault(org.apache.axiom.soap.SOAPFault sOAPFault) {
                super(sOAPFault);
            }
        
             public SoapFault(org.apache.axiom.soap.SOAPFault sOAPFault,org.apache.axis2.context.MessageContext messageContext) {
                super(sOAPFault,messageContext);
            }
        
             public SoapFault(java.lang.String string) {
                super(string);
            }
        
             public SoapFault(javax.xml.namespace.QName qName,java.lang.String string,java.lang.Throwable throwable) {
                super(qName,string,throwable);
            }
        
             public SoapFault(javax.xml.namespace.QName qName,java.util.List list,java.lang.String string,java.lang.Throwable throwable) {
                super(qName,list,string,throwable);
            }
        
             public SoapFault(javax.xml.namespace.QName qName,java.lang.String string,java.lang.String string2,java.lang.String string3,org.apache.axiom.om.OMElement oMElement) {
                super(qName,string,string2,string3,oMElement);
            }
        
             public SoapFault(java.lang.String string,javax.xml.namespace.QName qName,java.lang.Throwable throwable) {
                super(string,qName,throwable);
            }
        
             public SoapFault(java.lang.String string,org.apache.axis2.context.MessageContext messageContext,java.lang.Throwable throwable) {
                super(string,messageContext,throwable);
            }
        
             public SoapFault(java.lang.String string,java.lang.String string1,java.lang.Throwable throwable) {
                super(string,string1,throwable);
            }
        
             public SoapFault(java.lang.String string,org.apache.axis2.context.MessageContext messageContext) {
                super(string,messageContext);
            }
        
             public SoapFault(java.lang.String string,java.lang.String string1) {
                super(string,string1);
            }
        
             public SoapFault(java.lang.String string,javax.xml.namespace.QName qName) {
                super(string,qName);
            }
        
             public SoapFault(java.lang.String string,java.lang.Throwable throwable) {
                super(string,throwable);
            }
        

    public void setFaultMessage(com.ah.hm.ws.platform.service.hmservice.Soap msg){
       faultMessage = msg;
    }
    
    public com.ah.hm.ws.platform.service.hmservice.Soap getFaultMessage(){
       return faultMessage;
    }
}
    