//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.07.01 at 11:29:17 AM CST 
//


package com.ah.xml.be.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for config-obj complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="config-obj">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="rollback" type="{http://www.aerohive.com/configuration/others}config-rollback" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "config-obj", propOrder = {
    "rollback"
})
public class ConfigObj {

    protected ConfigRollback rollback;

    /**
     * Gets the value of the rollback property.
     * 
     * @return
     *     possible object is
     *     {@link ConfigRollback }
     *     
     */
    public ConfigRollback getRollback() {
        return rollback;
    }

    /**
     * Sets the value of the rollback property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConfigRollback }
     *     
     */
    public void setRollback(ConfigRollback value) {
        this.rollback = value;
    }

}
