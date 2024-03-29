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
 * <p>Java class for library-sip-policy-seq complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="library-sip-policy-seq">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.aerohive.com/configuration/general}ah-int" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-show" />
 *       &lt;attribute name="quoteProhibited" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "library-sip-policy-seq", namespace = "http://www.aerohive.com/configuration/policy", propOrder = {
    "id"
})
public class LibrarySipPolicySeq {

    protected AhInt id;
    @XmlAttribute(name = "value", required = true)
    protected String value;
    @XmlAttribute(name = "operation", required = true)
    protected AhEnumShow operation;
    @XmlAttribute(name = "quoteProhibited", required = true)
    protected AhEnumAct quoteProhibited;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link AhInt }
     *     
     */
    public AhInt getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhInt }
     *     
     */
    public void setId(AhInt value) {
        this.id = value;
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

    /**
     * Gets the value of the operation property.
     * 
     * @return
     *     possible object is
     *     {@link AhEnumShow }
     *     
     */
    public AhEnumShow getOperation() {
        return operation;
    }

    /**
     * Sets the value of the operation property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhEnumShow }
     *     
     */
    public void setOperation(AhEnumShow value) {
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
