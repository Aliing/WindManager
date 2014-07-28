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
 * <p>Java class for device-group-obj complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="device-group-obj">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mac-object" type="{http://www.aerohive.com/configuration/general}ah-name-act-value" minOccurs="0"/>
 *         &lt;element name="domain-object" type="{http://www.aerohive.com/configuration/general}ah-name-act-value" minOccurs="0"/>
 *         &lt;element name="os-object" type="{http://www.aerohive.com/configuration/general}ah-name-act-value" minOccurs="0"/>
 *         &lt;element name="ownership" type="{http://www.aerohive.com/configuration/policy}device-group-ownership" minOccurs="0"/>
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
@XmlType(name = "device-group-obj", namespace = "http://www.aerohive.com/configuration/policy", propOrder = {
    "macObject",
    "domainObject",
    "osObject",
    "ownership"
})
public class DeviceGroupObj {

    @XmlElement(name = "mac-object")
    protected AhNameActValue macObject;
    @XmlElement(name = "domain-object")
    protected AhNameActValue domainObject;
    @XmlElement(name = "os-object")
    protected AhNameActValue osObject;
    protected DeviceGroupOwnership ownership;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "operation", required = true)
    protected AhEnumActValue operation;

    /**
     * Gets the value of the macObject property.
     * 
     * @return
     *     possible object is
     *     {@link AhNameActValue }
     *     
     */
    public AhNameActValue getMacObject() {
        return macObject;
    }

    /**
     * Sets the value of the macObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhNameActValue }
     *     
     */
    public void setMacObject(AhNameActValue value) {
        this.macObject = value;
    }

    /**
     * Gets the value of the domainObject property.
     * 
     * @return
     *     possible object is
     *     {@link AhNameActValue }
     *     
     */
    public AhNameActValue getDomainObject() {
        return domainObject;
    }

    /**
     * Sets the value of the domainObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhNameActValue }
     *     
     */
    public void setDomainObject(AhNameActValue value) {
        this.domainObject = value;
    }

    /**
     * Gets the value of the osObject property.
     * 
     * @return
     *     possible object is
     *     {@link AhNameActValue }
     *     
     */
    public AhNameActValue getOsObject() {
        return osObject;
    }

    /**
     * Sets the value of the osObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhNameActValue }
     *     
     */
    public void setOsObject(AhNameActValue value) {
        this.osObject = value;
    }

    /**
     * Gets the value of the ownership property.
     * 
     * @return
     *     possible object is
     *     {@link DeviceGroupOwnership }
     *     
     */
    public DeviceGroupOwnership getOwnership() {
        return ownership;
    }

    /**
     * Sets the value of the ownership property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeviceGroupOwnership }
     *     
     */
    public void setOwnership(DeviceGroupOwnership value) {
        this.ownership = value;
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