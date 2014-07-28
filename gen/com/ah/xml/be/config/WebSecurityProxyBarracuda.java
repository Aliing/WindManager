//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.07.01 at 11:29:17 AM CST 
//


package com.ah.xml.be.config;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for web-security-proxy-barracuda complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="web-security-proxy-barracuda">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="http-proxy-host" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *         &lt;element name="http-proxy-port" type="{http://www.aerohive.com/configuration/admin}web-security-http-proxy-port" minOccurs="0"/>
 *         &lt;element name="https-proxy-host" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *         &lt;element name="https-proxy-port" type="{http://www.aerohive.com/configuration/admin}web-security-https-proxy-port" minOccurs="0"/>
 *         &lt;element name="subnet" type="{http://www.aerohive.com/configuration/admin}web-security-proxy-subnet" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="account-id" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *         &lt;element name="default-username" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *         &lt;element name="default-domain" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *         &lt;element name="whitelist" type="{http://www.aerohive.com/configuration/general}ah-name-act-value" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="enable" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "web-security-proxy-barracuda", namespace = "http://www.aerohive.com/configuration/admin", propOrder = {
    "ahdeltaassistant",
    "httpProxyHost",
    "httpProxyPort",
    "httpsProxyHost",
    "httpsProxyPort",
    "subnet",
    "accountId",
    "defaultUsername",
    "defaultDomain",
    "whitelist",
    "enable"
})
public class WebSecurityProxyBarracuda {

    @XmlElement(name = "AH-DELTA-ASSISTANT")
    protected AhOnlyAct ahdeltaassistant;
    @XmlElement(name = "http-proxy-host")
    protected AhStringAct httpProxyHost;
    @XmlElement(name = "http-proxy-port")
    protected WebSecurityHttpProxyPort httpProxyPort;
    @XmlElement(name = "https-proxy-host")
    protected AhStringAct httpsProxyHost;
    @XmlElement(name = "https-proxy-port")
    protected WebSecurityHttpsProxyPort httpsProxyPort;
    protected List<WebSecurityProxySubnet> subnet;
    @XmlElement(name = "account-id")
    protected AhStringAct accountId;
    @XmlElement(name = "default-username")
    protected AhStringAct defaultUsername;
    @XmlElement(name = "default-domain")
    protected AhStringAct defaultDomain;
    protected List<AhNameActValue> whitelist;
    protected AhOnlyAct enable;

    /**
     * Gets the value of the ahdeltaassistant property.
     * 
     * @return
     *     possible object is
     *     {@link AhOnlyAct }
     *     
     */
    public AhOnlyAct getAHDELTAASSISTANT() {
        return ahdeltaassistant;
    }

    /**
     * Sets the value of the ahdeltaassistant property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhOnlyAct }
     *     
     */
    public void setAHDELTAASSISTANT(AhOnlyAct value) {
        this.ahdeltaassistant = value;
    }

    /**
     * Gets the value of the httpProxyHost property.
     * 
     * @return
     *     possible object is
     *     {@link AhStringAct }
     *     
     */
    public AhStringAct getHttpProxyHost() {
        return httpProxyHost;
    }

    /**
     * Sets the value of the httpProxyHost property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhStringAct }
     *     
     */
    public void setHttpProxyHost(AhStringAct value) {
        this.httpProxyHost = value;
    }

    /**
     * Gets the value of the httpProxyPort property.
     * 
     * @return
     *     possible object is
     *     {@link WebSecurityHttpProxyPort }
     *     
     */
    public WebSecurityHttpProxyPort getHttpProxyPort() {
        return httpProxyPort;
    }

    /**
     * Sets the value of the httpProxyPort property.
     * 
     * @param value
     *     allowed object is
     *     {@link WebSecurityHttpProxyPort }
     *     
     */
    public void setHttpProxyPort(WebSecurityHttpProxyPort value) {
        this.httpProxyPort = value;
    }

    /**
     * Gets the value of the httpsProxyHost property.
     * 
     * @return
     *     possible object is
     *     {@link AhStringAct }
     *     
     */
    public AhStringAct getHttpsProxyHost() {
        return httpsProxyHost;
    }

    /**
     * Sets the value of the httpsProxyHost property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhStringAct }
     *     
     */
    public void setHttpsProxyHost(AhStringAct value) {
        this.httpsProxyHost = value;
    }

    /**
     * Gets the value of the httpsProxyPort property.
     * 
     * @return
     *     possible object is
     *     {@link WebSecurityHttpsProxyPort }
     *     
     */
    public WebSecurityHttpsProxyPort getHttpsProxyPort() {
        return httpsProxyPort;
    }

    /**
     * Sets the value of the httpsProxyPort property.
     * 
     * @param value
     *     allowed object is
     *     {@link WebSecurityHttpsProxyPort }
     *     
     */
    public void setHttpsProxyPort(WebSecurityHttpsProxyPort value) {
        this.httpsProxyPort = value;
    }

    /**
     * Gets the value of the subnet property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subnet property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubnet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WebSecurityProxySubnet }
     * 
     * 
     */
    public List<WebSecurityProxySubnet> getSubnet() {
        if (subnet == null) {
            subnet = new ArrayList<WebSecurityProxySubnet>();
        }
        return this.subnet;
    }

    /**
     * Gets the value of the accountId property.
     * 
     * @return
     *     possible object is
     *     {@link AhStringAct }
     *     
     */
    public AhStringAct getAccountId() {
        return accountId;
    }

    /**
     * Sets the value of the accountId property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhStringAct }
     *     
     */
    public void setAccountId(AhStringAct value) {
        this.accountId = value;
    }

    /**
     * Gets the value of the defaultUsername property.
     * 
     * @return
     *     possible object is
     *     {@link AhStringAct }
     *     
     */
    public AhStringAct getDefaultUsername() {
        return defaultUsername;
    }

    /**
     * Sets the value of the defaultUsername property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhStringAct }
     *     
     */
    public void setDefaultUsername(AhStringAct value) {
        this.defaultUsername = value;
    }

    /**
     * Gets the value of the defaultDomain property.
     * 
     * @return
     *     possible object is
     *     {@link AhStringAct }
     *     
     */
    public AhStringAct getDefaultDomain() {
        return defaultDomain;
    }

    /**
     * Sets the value of the defaultDomain property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhStringAct }
     *     
     */
    public void setDefaultDomain(AhStringAct value) {
        this.defaultDomain = value;
    }

    /**
     * Gets the value of the whitelist property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the whitelist property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWhitelist().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AhNameActValue }
     * 
     * 
     */
    public List<AhNameActValue> getWhitelist() {
        if (whitelist == null) {
            whitelist = new ArrayList<AhNameActValue>();
        }
        return this.whitelist;
    }

    /**
     * Gets the value of the enable property.
     * 
     * @return
     *     possible object is
     *     {@link AhOnlyAct }
     *     
     */
    public AhOnlyAct getEnable() {
        return enable;
    }

    /**
     * Sets the value of the enable property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhOnlyAct }
     *     
     */
    public void setEnable(AhOnlyAct value) {
        this.enable = value;
    }

}
