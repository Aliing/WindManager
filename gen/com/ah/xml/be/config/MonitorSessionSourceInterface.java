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
 * <p>Java class for monitor-session-source-interface complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="monitor-session-source-interface">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="both" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="egress" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ingress" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/choice>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act-value" />
 *       &lt;attribute name="quoteProhibited" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "monitor-session-source-interface", namespace = "http://www.aerohive.com/configuration/interface", propOrder = {
    "both",
    "egress",
    "ingress"
})
public class MonitorSessionSourceInterface {

    protected String both;
    protected String egress;
    protected String ingress;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "operation", required = true)
    protected AhEnumActValue operation;
    @XmlAttribute(name = "quoteProhibited", required = true)
    protected AhEnumAct quoteProhibited;

    /**
     * Gets the value of the both property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBoth() {
        return both;
    }

    /**
     * Sets the value of the both property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBoth(String value) {
        this.both = value;
    }

    /**
     * Gets the value of the egress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEgress() {
        return egress;
    }

    /**
     * Sets the value of the egress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEgress(String value) {
        this.egress = value;
    }

    /**
     * Gets the value of the ingress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIngress() {
        return ingress;
    }

    /**
     * Sets the value of the ingress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIngress(String value) {
        this.ingress = value;
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
     * Gets the value of the quoteProhibited property.
     * 
     * @return
     *     possible object is
     *     {@link AhEnumAct }
     *     
     */
    public AhEnumAct getQuoteProhibited() {
        return quoteProhibited;
    }

    /**
     * Sets the value of the quoteProhibited property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhEnumAct }
     *     
     */
    public void setQuoteProhibited(AhEnumAct value) {
        this.quoteProhibited = value;
    }

}
