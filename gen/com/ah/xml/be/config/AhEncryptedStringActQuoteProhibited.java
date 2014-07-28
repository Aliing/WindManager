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
 * <p>Java class for ah-encrypted-string-act-quote-prohibited complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ah-encrypted-string-act-quote-prohibited">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act" />
 *       &lt;attribute name="quoteProhibited" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act" />
 *       &lt;attribute name="encrypted" type="{http://www.aerohive.com/configuration/general}ah-encrypted-value" default="1" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ah-encrypted-string-act-quote-prohibited", namespace = "http://www.aerohive.com/configuration/general")
public class AhEncryptedStringActQuoteProhibited {

    @XmlAttribute(name = "value", required = true)
    protected String value;
    @XmlAttribute(name = "operation", required = true)
    protected AhEnumAct operation;
    @XmlAttribute(name = "quoteProhibited", required = true)
    protected AhEnumAct quoteProhibited;
    @XmlAttribute(name = "encrypted")
    protected Integer encrypted;

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

    /**
     * Gets the value of the encrypted property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getEncrypted() {
        if (encrypted == null) {
            return  1;
        } else {
            return encrypted;
        }
    }

    /**
     * Sets the value of the encrypted property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setEncrypted(Integer value) {
        this.encrypted = value;
    }

}
