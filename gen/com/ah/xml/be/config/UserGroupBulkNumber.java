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
 * <p>Java class for user-group-bulk-number complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="user-group-bulk-number">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="bulk-interval" type="{http://www.aerohive.com/configuration/general}ah-string-quote-prohibited" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="value" use="required" type="{http://www.aerohive.com/configuration/others}user-group-bulk-number-value" />
 *       &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "user-group-bulk-number", propOrder = {
    "bulkInterval"
})
public class UserGroupBulkNumber {

    @XmlElement(name = "bulk-interval")
    protected AhStringQuoteProhibited bulkInterval;
    @XmlAttribute(name = "value", required = true)
    protected int value;
    @XmlAttribute(name = "operation", required = true)
    protected AhEnumAct operation;

    /**
     * Gets the value of the bulkInterval property.
     * 
     * @return
     *     possible object is
     *     {@link AhStringQuoteProhibited }
     *     
     */
    public AhStringQuoteProhibited getBulkInterval() {
        return bulkInterval;
    }

    /**
     * Sets the value of the bulkInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhStringQuoteProhibited }
     *     
     */
    public void setBulkInterval(AhStringQuoteProhibited value) {
        this.bulkInterval = value;
    }

    /**
     * Gets the value of the value property.
     * 
     */
    public int getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     */
    public void setValue(int value) {
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

}
