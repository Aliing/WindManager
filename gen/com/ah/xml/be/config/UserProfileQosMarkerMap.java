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
 * <p>Java class for user-profile-qos-marker-map complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="user-profile-qos-marker-map">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="_8021p" type="{http://www.aerohive.com/configuration/general}ah-name-act" minOccurs="0"/>
 *         &lt;element name="diffserv" type="{http://www.aerohive.com/configuration/general}ah-name-act" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "user-profile-qos-marker-map", namespace = "http://www.aerohive.com/configuration/userProfile", propOrder = {
    "ahdeltaassistant",
    "_8021P",
    "diffserv"
})
public class UserProfileQosMarkerMap {

    @XmlElement(name = "AH-DELTA-ASSISTANT")
    protected AhOnlyAct ahdeltaassistant;
    @XmlElement(name = "_8021p")
    protected AhNameAct _8021P;
    protected AhNameAct diffserv;

    /**
     * Gets the value of the ahdeltaassistant property.
     * 
     * @return
     *     possible object is
     *     {@link AhOnlyAct }
     *     
     */
    public AhOnlyAct getAHDELTAASSISTANT() {
        return ahdeltaassistant;
    }

    /**
     * Sets the value of the ahdeltaassistant property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhOnlyAct }
     *     
     */
    public void setAHDELTAASSISTANT(AhOnlyAct value) {
        this.ahdeltaassistant = value;
    }

    /**
     * Gets the value of the 8021P property.
     * 
     * @return
     *     possible object is
     *     {@link AhNameAct }
     *     
     */
    public AhNameAct get8021P() {
        return _8021P;
    }

    /**
     * Sets the value of the 8021P property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhNameAct }
     *     
     */
    public void set8021P(AhNameAct value) {
        this._8021P = value;
    }

    /**
     * Gets the value of the diffserv property.
     * 
     * @return
     *     possible object is
     *     {@link AhNameAct }
     *     
     */
    public AhNameAct getDiffserv() {
        return diffserv;
    }

    /**
     * Sets the value of the diffserv property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhNameAct }
     *     
     */
    public void setDiffserv(AhNameAct value) {
        this.diffserv = value;
    }

}