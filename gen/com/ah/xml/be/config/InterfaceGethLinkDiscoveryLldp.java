//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.07.01 at 11:29:17 AM CST 
//


package com.ah.xml.be.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for interface-geth-link-discovery-lldp complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="interface-geth-link-discovery-lldp">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="transmit" type="{http://www.aerohive.com/configuration/interface}geth-link-discovery-lldp-mode" minOccurs="0"/>
 *         &lt;element name="receive" type="{http://www.aerohive.com/configuration/interface}geth-link-discovery-lldp-mode" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "interface-geth-link-discovery-lldp", namespace = "http://www.aerohive.com/configuration/interface", propOrder = {
    "transmit",
    "receive"
})
public class InterfaceGethLinkDiscoveryLldp {

    protected GethLinkDiscoveryLldpMode transmit;
    protected GethLinkDiscoveryLldpMode receive;

    /**
     * Gets the value of the transmit property.
     * 
     * @return
     *     possible object is
     *     {@link GethLinkDiscoveryLldpMode }
     *     
     */
    public GethLinkDiscoveryLldpMode getTransmit() {
        return transmit;
    }

    /**
     * Sets the value of the transmit property.
     * 
     * @param value
     *     allowed object is
     *     {@link GethLinkDiscoveryLldpMode }
     *     
     */
    public void setTransmit(GethLinkDiscoveryLldpMode value) {
        this.transmit = value;
    }

    /**
     * Gets the value of the receive property.
     * 
     * @return
     *     possible object is
     *     {@link GethLinkDiscoveryLldpMode }
     *     
     */
    public GethLinkDiscoveryLldpMode getReceive() {
        return receive;
    }

    /**
     * Sets the value of the receive property.
     * 
     * @param value
     *     allowed object is
     *     {@link GethLinkDiscoveryLldpMode }
     *     
     */
    public void setReceive(GethLinkDiscoveryLldpMode value) {
        this.receive = value;
    }

}