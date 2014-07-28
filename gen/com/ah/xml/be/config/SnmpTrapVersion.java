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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for snmp-trap-version complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="snmp-trap-version">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="port" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" default="162">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                       &lt;minInclusive value="1"/>
 *                       &lt;maxInclusive value="65535"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act-value" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="community" type="{http://www.aerohive.com/configuration/general}ah-string" minOccurs="0"/>
 *         &lt;element name="via-vpn-tunnel" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act-value" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "snmp-trap-version", namespace = "http://www.aerohive.com/configuration/snmp", propOrder = {
    "port",
    "community",
    "viaVpnTunnel"
})
public class SnmpTrapVersion {

    protected SnmpTrapVersion.Port port;
    protected AhString community;
    @XmlElement(name = "via-vpn-tunnel")
    protected AhOnlyAct viaVpnTunnel;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "operation", required = true)
    protected AhEnumActValue operation;

    /**
     * Gets the value of the port property.
     * 
     * @return
     *     possible object is
     *     {@link SnmpTrapVersion.Port }
     *     
     */
    public SnmpTrapVersion.Port getPort() {
        return port;
    }

    /**
     * Sets the value of the port property.
     * 
     * @param value
     *     allowed object is
     *     {@link SnmpTrapVersion.Port }
     *     
     */
    public void setPort(SnmpTrapVersion.Port value) {
        this.port = value;
    }

    /**
     * Gets the value of the community property.
     * 
     * @return
     *     possible object is
     *     {@link AhString }
     *     
     */
    public AhString getCommunity() {
        return community;
    }

    /**
     * Sets the value of the community property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhString }
     *     
     */
    public void setCommunity(AhString value) {
        this.community = value;
    }

    /**
     * Gets the value of the viaVpnTunnel property.
     * 
     * @return
     *     possible object is
     *     {@link AhOnlyAct }
     *     
     */
    public AhOnlyAct getViaVpnTunnel() {
        return viaVpnTunnel;
    }

    /**
     * Sets the value of the viaVpnTunnel property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhOnlyAct }
     *     
     */
    public void setViaVpnTunnel(AhOnlyAct value) {
        this.viaVpnTunnel = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the operation property.
     * 
     * @return
     *     possible object is
     *     {@link AhEnumActValue }
     *     
     */
    public AhEnumActValue getOperation() {
        return operation;
    }

    /**
     * Sets the value of the operation property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhEnumActValue }
     *     
     */
    public void setOperation(AhEnumActValue value) {
        this.operation = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="value" default="162">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *             &lt;minInclusive value="1"/>
     *             &lt;maxInclusive value="65535"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act-value" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Port {

        @XmlAttribute(name = "value")
        protected Integer value;
        @XmlAttribute(name = "operation", required = true)
        protected AhEnumActValue operation;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public int getValue() {
            if (value == null) {
                return  162;
            } else {
                return value;
            }
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setValue(Integer value) {
            this.value = value;
        }

        /**
         * Gets the value of the operation property.
         * 
         * @return
         *     possible object is
         *     {@link AhEnumActValue }
         *     
         */
        public AhEnumActValue getOperation() {
            return operation;
        }

        /**
         * Sets the value of the operation property.
         * 
         * @param value
         *     allowed object is
         *     {@link AhEnumActValue }
         *     
         */
        public void setOperation(AhEnumActValue value) {
            this.operation = value;
        }

    }

}
