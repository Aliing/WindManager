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
 * <p>Java class for STA-auth-private-key complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="STA-auth-private-key">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="private-key-password" type="{http://www.aerohive.com/configuration/general}ah-encrypted-string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "STA-auth-private-key", namespace = "http://www.aerohive.com/configuration/aaa", propOrder = {
    "privateKeyPassword"
})
public class STAAuthPrivateKey {

    @XmlElement(name = "private-key-password")
    protected AhEncryptedString privateKeyPassword;
    @XmlAttribute(name = "value", required = true)
    protected String value;

    /**
     * Gets the value of the privateKeyPassword property.
     * 
     * @return
     *     possible object is
     *     {@link AhEncryptedString }
     *     
     */
    public AhEncryptedString getPrivateKeyPassword() {
        return privateKeyPassword;
    }

    /**
     * Sets the value of the privateKeyPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhEncryptedString }
     *     
     */
    public void setPrivateKeyPassword(AhEncryptedString value) {
        this.privateKeyPassword = value;
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

}