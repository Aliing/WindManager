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
 * <p>Java class for designated-server-idm-proxy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="designated-server-idm-proxy">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="announce" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="dynamic" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "designated-server-idm-proxy", namespace = "http://www.aerohive.com/configuration/aaa", propOrder = {
    "ahdeltaassistant",
    "announce",
    "dynamic"
})
public class DesignatedServerIdmProxy {

    @XmlElement(name = "AH-DELTA-ASSISTANT")
    protected AhOnlyAct ahdeltaassistant;
    protected AhOnlyAct announce;
    protected AhOnlyAct dynamic;

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
     * Gets the value of the announce property.
     * 
     * @return
     *     possible object is
     *     {@link AhOnlyAct }
     *     
     */
    public AhOnlyAct getAnnounce() {
        return announce;
    }

    /**
     * Sets the value of the announce property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhOnlyAct }
     *     
     */
    public void setAnnounce(AhOnlyAct value) {
        this.announce = value;
    }

    /**
     * Gets the value of the dynamic property.
     * 
     * @return
     *     possible object is
     *     {@link AhOnlyAct }
     *     
     */
    public AhOnlyAct getDynamic() {
        return dynamic;
    }

    /**
     * Sets the value of the dynamic property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhOnlyAct }
     *     
     */
    public void setDynamic(AhOnlyAct value) {
        this.dynamic = value;
    }

}