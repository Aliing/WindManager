//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.07.01 at 11:29:17 AM CST 
//


package com.ah.xml.be.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for service-protocol complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="service-protocol">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="port" type="{http://www.aerohive.com/configuration/service}protocol-port" minOccurs="0"/>
 *         &lt;element name="timeout" type="{http://www.aerohive.com/configuration/service}protocol-timeout" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="value" use="required" type="{http://www.aerohive.com/configuration/service}protocol-value" />
 *       &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-show" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "service-protocol", namespace = "http://www.aerohive.com/configuration/service", propOrder = {
    "port",
    "timeout"
})
public class ServiceProtocol {

    protected ProtocolPort port;
    protected ProtocolTimeout timeout;
    @XmlAttribute(name = "value", required = true)
    protected String value;
    @XmlAttribute(name = "operation", required = true)
    protected AhEnumShow operation;

    /**
     * Gets the value of the port property.
     * 
     * @return
     *     possible object is
     *     {@link ProtocolPort }
     *     
     */
    public ProtocolPort getPort() {
        return port;
    }

    /**
     * Sets the value of the port property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProtocolPort }
     *     
     */
    public void setPort(ProtocolPort value) {
        this.port = value;
    }

    /**
     * Gets the value of the timeout property.
     * 
     * @return
     *     possible object is
     *     {@link ProtocolTimeout }
     *     
     */
    public ProtocolTimeout getTimeout() {
        return timeout;
    }

    /**
     * Sets the value of the timeout property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProtocolTimeout }
     *     
     */
    public void setTimeout(ProtocolTimeout value) {
        this.timeout = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the operation property.
     * 
     * @return
     *     possible object is
     *     {@link AhEnumShow }
     *     
     */
    public AhEnumShow getOperation() {
        return operation;
    }

    /**
     * Sets the value of the operation property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhEnumShow }
     *     
     */
    public void setOperation(AhEnumShow value) {
        this.operation = value;
    }

}
