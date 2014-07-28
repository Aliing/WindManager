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
 * <p>Java class for wlan-idp-sta-report complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="wlan-idp-sta-report">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="cr" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="duration" type="{http://www.aerohive.com/configuration/security}wlan-idp-sta-report-duration" minOccurs="0"/>
 *         &lt;element name="age-time" type="{http://www.aerohive.com/configuration/general}ah-int-act" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "wlan-idp-sta-report", namespace = "http://www.aerohive.com/configuration/security", propOrder = {
    "ahdeltaassistant",
    "cr",
    "duration",
    "ageTime"
})
public class WlanIdpStaReport {

    @XmlElement(name = "AH-DELTA-ASSISTANT")
    protected AhOnlyAct ahdeltaassistant;
    protected AhOnlyAct cr;
    protected WlanIdpStaReportDuration duration;
    @XmlElement(name = "age-time")
    protected AhIntAct ageTime;

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
     * Gets the value of the cr property.
     * 
     * @return
     *     possible object is
     *     {@link AhOnlyAct }
     *     
     */
    public AhOnlyAct getCr() {
        return cr;
    }

    /**
     * Sets the value of the cr property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhOnlyAct }
     *     
     */
    public void setCr(AhOnlyAct value) {
        this.cr = value;
    }

    /**
     * Gets the value of the duration property.
     * 
     * @return
     *     possible object is
     *     {@link WlanIdpStaReportDuration }
     *     
     */
    public WlanIdpStaReportDuration getDuration() {
        return duration;
    }

    /**
     * Sets the value of the duration property.
     * 
     * @param value
     *     allowed object is
     *     {@link WlanIdpStaReportDuration }
     *     
     */
    public void setDuration(WlanIdpStaReportDuration value) {
        this.duration = value;
    }

    /**
     * Gets the value of the ageTime property.
     * 
     * @return
     *     possible object is
     *     {@link AhIntAct }
     *     
     */
    public AhIntAct getAgeTime() {
        return ageTime;
    }

    /**
     * Sets the value of the ageTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhIntAct }
     *     
     */
    public void setAgeTime(AhIntAct value) {
        this.ageTime = value;
    }

}
