//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.07.01 at 11:29:17 AM CST 
//


package com.ah.xml.be.config;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mobile-device-manager-jss complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mobile-device-manager-jss">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="enable" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="url-root-path" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *         &lt;element name="http-auth" type="{http://www.aerohive.com/configuration/ssid}mdm-http-auth" minOccurs="0"/>
 *         &lt;element name="os-object" type="{http://www.aerohive.com/configuration/ssid}mdm-os-object" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mobile-device-manager-jss", namespace = "http://www.aerohive.com/configuration/ssid", propOrder = {
    "ahdeltaassistant",
    "enable",
    "urlRootPath",
    "httpAuth",
    "osObject"
})
public class MobileDeviceManagerJss {

    @XmlElement(name = "AH-DELTA-ASSISTANT")
    protected AhOnlyAct ahdeltaassistant;
    protected AhOnlyAct enable;
    @XmlElement(name = "url-root-path")
    protected AhStringAct urlRootPath;
    @XmlElement(name = "http-auth")
    protected MdmHttpAuth httpAuth;
    @XmlElement(name = "os-object")
    protected List<MdmOsObject> osObject;

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
     * Gets the value of the httpAuth property.
     * 
     * @return
     *     possible object is
     *     {@link MdmHttpAuth }
     *     
     */
    public MdmHttpAuth getHttpAuth() {
        return httpAuth;
    }

    /**
     * Sets the value of the httpAuth property.
     * 
     * @param value
     *     allowed object is
     *     {@link MdmHttpAuth }
     *     
     */
    public void setHttpAuth(MdmHttpAuth value) {
        this.httpAuth = value;
    }

    /**
     * Gets the value of the osObject property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the osObject property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOsObject().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MdmOsObject }
     * 
     * 
     */
    public List<MdmOsObject> getOsObject() {
        if (osObject == null) {
            osObject = new ArrayList<MdmOsObject>();
        }
        return this.osObject;
    }

}
