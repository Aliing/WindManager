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
 * <p>Java class for ssid-rrm-quiet complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ssid-rrm-quiet">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="enable" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="period" type="{http://www.aerohive.com/configuration/ssid}ssid-rrm-quiet-period" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ssid-rrm-quiet", namespace = "http://www.aerohive.com/configuration/ssid", propOrder = {
    "enable",
    "period"
})
public class SsidRrmQuiet {

    protected AhOnlyAct enable;
    protected SsidRrmQuietPeriod period;

    /**
     * Gets the value of the enable property.
     * 
     * @return
     *     possible object is
     *     {@link AhOnlyAct }
     *     
     */
    public AhOnlyAct getEnable() {
        return enable;
    }

    /**
     * Sets the value of the enable property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhOnlyAct }
     *     
     */
    public void setEnable(AhOnlyAct value) {
        this.enable = value;
    }

    /**
     * Gets the value of the period property.
     * 
     * @return
     *     possible object is
     *     {@link SsidRrmQuietPeriod }
     *     
     */
    public SsidRrmQuietPeriod getPeriod() {
        return period;
    }

    /**
     * Sets the value of the period property.
     * 
     * @param value
     *     allowed object is
     *     {@link SsidRrmQuietPeriod }
     *     
     */
    public void setPeriod(SsidRrmQuietPeriod value) {
        this.period = value;
    }

}
