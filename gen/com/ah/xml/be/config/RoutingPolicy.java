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
 * <p>Java class for routing-policy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="routing-policy">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="from" type="{http://www.aerohive.com/configuration/admin}routing-policy-from" minOccurs="0"/>
 *         &lt;element name="primary-wan" type="{http://www.aerohive.com/configuration/admin}routing-policy-wan" minOccurs="0"/>
 *         &lt;element name="secondary-wan" type="{http://www.aerohive.com/configuration/admin}routing-policy-wan" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.aerohive.com/configuration/admin}routing-policy-id" minOccurs="0"/>
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
@XmlType(name = "routing-policy", namespace = "http://www.aerohive.com/configuration/admin", propOrder = {
    "cr",
    "from",
    "primaryWan",
    "secondaryWan",
    "id"
})
public class RoutingPolicy {

    protected String cr;
    protected RoutingPolicyFrom from;
    @XmlElement(name = "primary-wan")
    protected RoutingPolicyWan primaryWan;
    @XmlElement(name = "secondary-wan")
    protected RoutingPolicyWan secondaryWan;
    protected RoutingPolicyId id;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "operation", required = true)
    protected AhEnumActValue operation;

    /**
     * Gets the value of the cr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCr() {
        return cr;
    }

    /**
     * Sets the value of the cr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCr(String value) {
        this.cr = value;
    }

    /**
     * Gets the value of the from property.
     * 
     * @return
     *     possible object is
     *     {@link RoutingPolicyFrom }
     *     
     */
    public RoutingPolicyFrom getFrom() {
        return from;
    }

    /**
     * Sets the value of the from property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoutingPolicyFrom }
     *     
     */
    public void setFrom(RoutingPolicyFrom value) {
        this.from = value;
    }

    /**
     * Gets the value of the primaryWan property.
     * 
     * @return
     *     possible object is
     *     {@link RoutingPolicyWan }
     *     
     */
    public RoutingPolicyWan getPrimaryWan() {
        return primaryWan;
    }

    /**
     * Sets the value of the primaryWan property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoutingPolicyWan }
     *     
     */
    public void setPrimaryWan(RoutingPolicyWan value) {
        this.primaryWan = value;
    }

    /**
     * Gets the value of the secondaryWan property.
     * 
     * @return
     *     possible object is
     *     {@link RoutingPolicyWan }
     *     
     */
    public RoutingPolicyWan getSecondaryWan() {
        return secondaryWan;
    }

    /**
     * Sets the value of the secondaryWan property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoutingPolicyWan }
     *     
     */
    public void setSecondaryWan(RoutingPolicyWan value) {
        this.secondaryWan = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link RoutingPolicyId }
     *     
     */
    public RoutingPolicyId getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoutingPolicyId }
     *     
     */
    public void setId(RoutingPolicyId value) {
        this.id = value;
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