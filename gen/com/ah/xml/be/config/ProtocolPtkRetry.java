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
 * <p>Java class for protocol-ptk-retry complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="protocol-ptk-retry">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="value" default="3">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *             &lt;minInclusive value="1"/>
 *             &lt;maxInclusive value="10"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "protocol-ptk-retry", namespace = "http://www.aerohive.com/configuration/ssid")
public class ProtocolPtkRetry {

    @XmlAttribute(name = "value")
    protected Integer value;

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
            return  3;
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
