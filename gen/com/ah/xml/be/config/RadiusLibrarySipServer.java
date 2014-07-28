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
 * <p>Java class for radius-library-sip-server complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="radius-library-sip-server">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="server" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *         &lt;element name="port" type="{http://www.aerohive.com/configuration/aaa}library-sip-server-port" minOccurs="0"/>
 *         &lt;element name="login-enable" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="login-user" type="{http://www.aerohive.com/configuration/aaa}library-sip-server-login-user" minOccurs="0"/>
 *         &lt;element name="institution-id" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *         &lt;element name="separator" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "radius-library-sip-server", namespace = "http://www.aerohive.com/configuration/aaa", propOrder = {
    "server",
    "port",
    "loginEnable",
    "loginUser",
    "institutionId",
    "separator"
})
public class RadiusLibrarySipServer {

    protected AhStringAct server;
    protected LibrarySipServerPort port;
    @XmlElement(name = "login-enable")
    protected AhOnlyAct loginEnable;
    @XmlElement(name = "login-user")
    protected LibrarySipServerLoginUser loginUser;
    @XmlElement(name = "institution-id")
    protected AhStringAct institutionId;
    protected AhStringAct separator;
    @XmlAttribute(name = "operation", required = true)
    protected AhEnumAct operation;

    /**
     * Gets the value of the server property.
     * 
     * @return
     *     possible object is
     *     {@link AhStringAct }
     *     
     */
    public AhStringAct getServer() {
        return server;
    }

    /**
     * Sets the value of the server property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhStringAct }
     *     
     */
    public void setServer(AhStringAct value) {
        this.server = value;
    }

    /**
     * Gets the value of the port property.
     * 
     * @return
     *     possible object is
     *     {@link LibrarySipServerPort }
     *     
     */
    public LibrarySipServerPort getPort() {
        return port;
    }

    /**
     * Sets the value of the port property.
     * 
     * @param value
     *     allowed object is
     *     {@link LibrarySipServerPort }
     *     
     */
    public void setPort(LibrarySipServerPort value) {
        this.port = value;
    }

    /**
     * Gets the value of the loginEnable property.
     * 
     * @return
     *     possible object is
     *     {@link AhOnlyAct }
     *     
     */
    public AhOnlyAct getLoginEnable() {
        return loginEnable;
    }

    /**
     * Sets the value of the loginEnable property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhOnlyAct }
     *     
     */
    public void setLoginEnable(AhOnlyAct value) {
        this.loginEnable = value;
    }

    /**
     * Gets the value of the loginUser property.
     * 
     * @return
     *     possible object is
     *     {@link LibrarySipServerLoginUser }
     *     
     */
    public LibrarySipServerLoginUser getLoginUser() {
        return loginUser;
    }

    /**
     * Sets the value of the loginUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link LibrarySipServerLoginUser }
     *     
     */
    public void setLoginUser(LibrarySipServerLoginUser value) {
        this.loginUser = value;
    }

    /**
     * Gets the value of the institutionId property.
     * 
     * @return
     *     possible object is
     *     {@link AhStringAct }
     *     
     */
    public AhStringAct getInstitutionId() {
        return institutionId;
    }

    /**
     * Sets the value of the institutionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhStringAct }
     *     
     */
    public void setInstitutionId(AhStringAct value) {
        this.institutionId = value;
    }

    /**
     * Gets the value of the separator property.
     * 
     * @return
     *     possible object is
     *     {@link AhStringAct }
     *     
     */
    public AhStringAct getSeparator() {
        return separator;
    }

    /**
     * Sets the value of the separator property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhStringAct }
     *     
     */
    public void setSeparator(AhStringAct value) {
        this.separator = value;
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
