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
 * <p>Java class for open-directory-domain complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="open-directory-domain">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fullname" type="{http://www.aerohive.com/configuration/general}ah-string" minOccurs="0"/>
 *         &lt;element name="binddn" type="{http://www.aerohive.com/configuration/aaa}db-server-binddn" minOccurs="0"/>
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
@XmlType(name = "open-directory-domain", namespace = "http://www.aerohive.com/configuration/aaa", propOrder = {
    "fullname",
    "binddn"
})
public class OpenDirectoryDomain {

    protected AhString fullname;
    protected DbServerBinddn binddn;
    @XmlAttribute(name = "value", required = true)
    protected String value;

    /**
     * Gets the value of the fullname property.
     * 
     * @return
     *     possible object is
     *     {@link AhString }
     *     
     */
    public AhString getFullname() {
        return fullname;
    }

    /**
     * Sets the value of the fullname property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhString }
     *     
     */
    public void setFullname(AhString value) {
        this.fullname = value;
    }

    /**
     * Gets the value of the binddn property.
     * 
     * @return
     *     possible object is
     *     {@link DbServerBinddn }
     *     
     */
    public DbServerBinddn getBinddn() {
        return binddn;
    }

    /**
     * Sets the value of the binddn property.
     * 
     * @param value
     *     allowed object is
     *     {@link DbServerBinddn }
     *     
     */
    public void setBinddn(DbServerBinddn value) {
        this.binddn = value;
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
