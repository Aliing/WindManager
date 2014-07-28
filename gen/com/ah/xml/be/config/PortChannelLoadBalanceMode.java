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
 * <p>Java class for port-channel-load-balance-mode complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="port-channel-load-balance-mode">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="value" type="{http://www.aerohive.com/configuration/others}port-channel-load-balance-mode-value" default="src-dst-mac-ip-port" />
 *       &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "port-channel-load-balance-mode")
public class PortChannelLoadBalanceMode {

    @XmlAttribute(name = "value")
    protected PortChannelLoadBalanceModeValue value;
    @XmlAttribute(name = "operation", required = true)
    protected AhEnumAct operation;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link PortChannelLoadBalanceModeValue }
     *     
     */
    public PortChannelLoadBalanceModeValue getValue() {
        if (value == null) {
            return PortChannelLoadBalanceModeValue.SRC_DST_MAC_IP_PORT;
        } else {
            return value;
        }
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link PortChannelLoadBalanceModeValue }
     *     
     */
    public void setValue(PortChannelLoadBalanceModeValue value) {
        this.value = value;
    }

    /**
     * Gets the value of the operation property.
     * 
     * @return
     *     possible object is
     *     {@link AhEnumAct }
     *     
     */
    public AhEnumAct getOperation() {
        return operation;
    }

    /**
     * Sets the value of the operation property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhEnumAct }
     *     
     */
    public void setOperation(AhEnumAct value) {
        this.operation = value;
    }

}
