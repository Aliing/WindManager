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
 * <p>Java class for time-zone-offset complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="time-zone-offset">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cr" type="{http://www.aerohive.com/configuration/others}time-zone-extra" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="value" type="{http://www.aerohive.com/configuration/others}time-zone-offset-value" default="0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "time-zone-offset", propOrder = {
    "cr"
})
public class TimeZoneOffset {

    protected TimeZoneExtra cr;
    @XmlAttribute(name = "value")
    protected Integer value;

    /**
     * Gets the value of the cr property.
     * 
     * @return
     *     possible object is
     *     {@link TimeZoneExtra }
     *     
     */
    public TimeZoneExtra getCr() {
        return cr;
    }

    /**
     * Sets the value of the cr property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeZoneExtra }
     *     
     */
    public void setCr(TimeZoneExtra value) {
        this.cr = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getValue() {
        if (value == null) {
            return  0;
        } else {
            return value;
        }
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setValue(Integer value) {
        this.value = value;
    }

}
