//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.07.01 at 11:29:17 AM CST 
//


package com.ah.xml.be.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for aaa-attribute complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="aaa-attribute">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="nas-identifier" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *         &lt;element name="operator-name" type="{http://www.aerohive.com/configuration/aaa}aaa-attr-operator-name" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "aaa-attribute", namespace = "http://www.aerohive.com/configuration/aaa", propOrder = {
    "nasIdentifier",
    "operatorName"
})
public class AaaAttribute {

    @XmlElement(name = "nas-identifier")
    protected AhStringAct nasIdentifier;
    @XmlElement(name = "operator-name")
    protected AaaAttrOperatorName operatorName;

    /**
     * Gets the value of the nasIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link AhStringAct }
     *     
     */
    public AhStringAct getNasIdentifier() {
        return nasIdentifier;
    }

    /**
     * Sets the value of the nasIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhStringAct }
     *     
     */
    public void setNasIdentifier(AhStringAct value) {
        this.nasIdentifier = value;
    }

    /**
     * Gets the value of the operatorName property.
     * 
     * @return
     *     possible object is
     *     {@link AaaAttrOperatorName }
     *     
     */
    public AaaAttrOperatorName getOperatorName() {
        return operatorName;
    }

    /**
     * Sets the value of the operatorName property.
     * 
     * @param value
     *     allowed object is
     *     {@link AaaAttrOperatorName }
     *     
     */
    public void setOperatorName(AaaAttrOperatorName value) {
        this.operatorName = value;
    }

}