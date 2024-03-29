//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.07.01 at 11:29:17 AM CST 
//


package com.ah.xml.be.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for web-security-proxy-obj complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="web-security-proxy-obj">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="websense-v1" type="{http://www.aerohive.com/configuration/admin}web-security-proxy-websense" minOccurs="0"/>
 *         &lt;element name="barracuda-v1" type="{http://www.aerohive.com/configuration/admin}web-security-proxy-barracuda" minOccurs="0"/>
 *         &lt;element name="opendns-v1" type="{http://www.aerohive.com/configuration/admin}web-security-proxy-opendns" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "web-security-proxy-obj", namespace = "http://www.aerohive.com/configuration/admin", propOrder = {
    "websenseV1",
    "barracudaV1",
    "opendnsV1"
})
public class WebSecurityProxyObj {

    @XmlElement(name = "websense-v1")
    protected WebSecurityProxyWebsense websenseV1;
    @XmlElement(name = "barracuda-v1")
    protected WebSecurityProxyBarracuda barracudaV1;
    @XmlElement(name = "opendns-v1")
    protected WebSecurityProxyOpendns opendnsV1;

    /**
     * Gets the value of the websenseV1 property.
     * 
     * @return
     *     possible object is
     *     {@link WebSecurityProxyWebsense }
     *     
     */
    public WebSecurityProxyWebsense getWebsenseV1() {
        return websenseV1;
    }

    /**
     * Sets the value of the websenseV1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link WebSecurityProxyWebsense }
     *     
     */
    public void setWebsenseV1(WebSecurityProxyWebsense value) {
        this.websenseV1 = value;
    }

    /**
     * Gets the value of the barracudaV1 property.
     * 
     * @return
     *     possible object is
     *     {@link WebSecurityProxyBarracuda }
     *     
     */
    public WebSecurityProxyBarracuda getBarracudaV1() {
        return barracudaV1;
    }

    /**
     * Sets the value of the barracudaV1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link WebSecurityProxyBarracuda }
     *     
     */
    public void setBarracudaV1(WebSecurityProxyBarracuda value) {
        this.barracudaV1 = value;
    }

    /**
     * Gets the value of the opendnsV1 property.
     * 
     * @return
     *     possible object is
     *     {@link WebSecurityProxyOpendns }
     *     
     */
    public WebSecurityProxyOpendns getOpendnsV1() {
        return opendnsV1;
    }

    /**
     * Sets the value of the opendnsV1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link WebSecurityProxyOpendns }
     *     
     */
    public void setOpendnsV1(WebSecurityProxyOpendns value) {
        this.opendnsV1 = value;
    }

}
