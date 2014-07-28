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
 * <p>Java class for cwp-cloud-cwp complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cwp-cloud-cwp">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="enable" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="service-id" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *         &lt;element name="customer-id" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *         &lt;element name="url-root-path" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *         &lt;element name="api-key" type="{http://www.aerohive.com/configuration/ssid}cloud-cwp-api-key" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cwp-cloud-cwp", namespace = "http://www.aerohive.com/configuration/ssid", propOrder = {
    "ahdeltaassistant",
    "enable",
    "serviceId",
    "customerId",
    "urlRootPath",
    "apiKey"
})
public class CwpCloudCwp {

    @XmlElement(name = "AH-DELTA-ASSISTANT")
    protected AhOnlyAct ahdeltaassistant;
    protected AhOnlyAct enable;
    @XmlElement(name = "service-id")
    protected AhStringAct serviceId;
    @XmlElement(name = "customer-id")
    protected AhStringAct customerId;
    @XmlElement(name = "url-root-path")
    protected AhStringAct urlRootPath;
    @XmlElement(name = "api-key")
    protected CloudCwpApiKey apiKey;

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
     * Gets the value of the serviceId property.
     * 
     * @return
     *     possible object is
     *     {@link AhStringAct }
     *     
     */
    public AhStringAct getServiceId() {
        return serviceId;
    }

    /**
     * Sets the value of the serviceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhStringAct }
     *     
     */
    public void setServiceId(AhStringAct value) {
        this.serviceId = value;
    }

    /**
     * Gets the value of the customerId property.
     * 
     * @return
     *     possible object is
     *     {@link AhStringAct }
     *     
     */
    public AhStringAct getCustomerId() {
        return customerId;
    }

    /**
     * Sets the value of the customerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhStringAct }
     *     
     */
    public void setCustomerId(AhStringAct value) {
        this.customerId = value;
    }

    /**
     * Gets the value of the urlRootPath property.
     * 
     * @return
     *     possible object is
     *     {@link AhStringAct }
     *     
     */
    public AhStringAct getUrlRootPath() {
        return urlRootPath;
    }

    /**
     * Sets the value of the urlRootPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhStringAct }
     *     
     */
    public void setUrlRootPath(AhStringAct value) {
        this.urlRootPath = value;
    }

    /**
     * Gets the value of the apiKey property.
     * 
     * @return
     *     possible object is
     *     {@link CloudCwpApiKey }
     *     
     */
    public CloudCwpApiKey getApiKey() {
        return apiKey;
    }

    /**
     * Sets the value of the apiKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link CloudCwpApiKey }
     *     
     */
    public void setApiKey(CloudCwpApiKey value) {
        this.apiKey = value;
    }

}