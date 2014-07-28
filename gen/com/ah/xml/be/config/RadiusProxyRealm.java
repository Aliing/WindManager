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
 * <p>Java class for radius-proxy-realm complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="radius-proxy-realm">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="primary" type="{http://www.aerohive.com/configuration/general}ah-name-act" minOccurs="0"/>
 *         &lt;element name="backup" type="{http://www.aerohive.com/configuration/general}ah-name-act" minOccurs="0"/>
 *         &lt;element name="no-strip" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
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
@XmlType(name = "radius-proxy-realm", namespace = "http://www.aerohive.com/configuration/aaa", propOrder = {
    "primary",
    "backup",
    "noStrip"
})
public class RadiusProxyRealm {

    protected AhNameAct primary;
    protected AhNameAct backup;
    @XmlElement(name = "no-strip")
    protected AhOnlyAct noStrip;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "operation", required = true)
    protected AhEnumActValue operation;

    /**
     * Gets the value of the primary property.
     * 
     * @return
     *     possible object is
     *     {@link AhNameAct }
     *     
     */
    public AhNameAct getPrimary() {
        return primary;
    }

    /**
     * Sets the value of the primary property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhNameAct }
     *     
     */
    public void setPrimary(AhNameAct value) {
        this.primary = value;
    }

    /**
     * Gets the value of the backup property.
     * 
     * @return
     *     possible object is
     *     {@link AhNameAct }
     *     
     */
    public AhNameAct getBackup() {
        return backup;
    }

    /**
     * Sets the value of the backup property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhNameAct }
     *     
     */
    public void setBackup(AhNameAct value) {
        this.backup = value;
    }

    /**
     * Gets the value of the noStrip property.
     * 
     * @return
     *     possible object is
     *     {@link AhOnlyAct }
     *     
     */
    public AhOnlyAct getNoStrip() {
        return noStrip;
    }

    /**
     * Sets the value of the noStrip property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhOnlyAct }
     *     
     */
    public void setNoStrip(AhOnlyAct value) {
        this.noStrip = value;
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

}