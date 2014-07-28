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
 * <p>Java class for hive-wlan-idp complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="hive-wlan-idp">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="mitigator-reeval-period" type="{http://www.aerohive.com/configuration/hive}wlan-idp-mitigator-reeval-period" minOccurs="0"/>
 *         &lt;element name="max-mitigator-num" type="{http://www.aerohive.com/configuration/hive}wlan-idp-max-mitigator-num" minOccurs="0"/>
 *         &lt;element name="mitigation-mode" type="{http://www.aerohive.com/configuration/hive}wlan-idp-mitigation-mode" minOccurs="0"/>
 *         &lt;element name="in-net-ap" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "hive-wlan-idp", namespace = "http://www.aerohive.com/configuration/hive", propOrder = {
    "ahdeltaassistant",
    "mitigatorReevalPeriod",
    "maxMitigatorNum",
    "mitigationMode",
    "inNetAp"
})
public class HiveWlanIdp {

    @XmlElement(name = "AH-DELTA-ASSISTANT")
    protected AhOnlyAct ahdeltaassistant;
    @XmlElement(name = "mitigator-reeval-period")
    protected WlanIdpMitigatorReevalPeriod mitigatorReevalPeriod;
    @XmlElement(name = "max-mitigator-num")
    protected WlanIdpMaxMitigatorNum maxMitigatorNum;
    @XmlElement(name = "mitigation-mode")
    protected WlanIdpMitigationMode mitigationMode;
    @XmlElement(name = "in-net-ap")
    protected AhOnlyAct inNetAp;

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
     * Gets the value of the mitigatorReevalPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link WlanIdpMitigatorReevalPeriod }
     *     
     */
    public WlanIdpMitigatorReevalPeriod getMitigatorReevalPeriod() {
        return mitigatorReevalPeriod;
    }

    /**
     * Sets the value of the mitigatorReevalPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link WlanIdpMitigatorReevalPeriod }
     *     
     */
    public void setMitigatorReevalPeriod(WlanIdpMitigatorReevalPeriod value) {
        this.mitigatorReevalPeriod = value;
    }

    /**
     * Gets the value of the maxMitigatorNum property.
     * 
     * @return
     *     possible object is
     *     {@link WlanIdpMaxMitigatorNum }
     *     
     */
    public WlanIdpMaxMitigatorNum getMaxMitigatorNum() {
        return maxMitigatorNum;
    }

    /**
     * Sets the value of the maxMitigatorNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link WlanIdpMaxMitigatorNum }
     *     
     */
    public void setMaxMitigatorNum(WlanIdpMaxMitigatorNum value) {
        this.maxMitigatorNum = value;
    }

    /**
     * Gets the value of the mitigationMode property.
     * 
     * @return
     *     possible object is
     *     {@link WlanIdpMitigationMode }
     *     
     */
    public WlanIdpMitigationMode getMitigationMode() {
        return mitigationMode;
    }

    /**
     * Sets the value of the mitigationMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link WlanIdpMitigationMode }
     *     
     */
    public void setMitigationMode(WlanIdpMitigationMode value) {
        this.mitigationMode = value;
    }

    /**
     * Gets the value of the inNetAp property.
     * 
     * @return
     *     possible object is
     *     {@link AhOnlyAct }
     *     
     */
    public AhOnlyAct getInNetAp() {
        return inNetAp;
    }

    /**
     * Sets the value of the inNetAp property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhOnlyAct }
     *     
     */
    public void setInNetAp(AhOnlyAct value) {
        this.inNetAp = value;
    }

}
