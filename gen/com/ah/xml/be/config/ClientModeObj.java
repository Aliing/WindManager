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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for client-mode-obj complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="client-mode-obj">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="ssid" type="{http://www.aerohive.com/configuration/ssid}client-mode-ssid" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="band-mode" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *         &lt;element name="connect" type="{http://www.aerohive.com/configuration/ssid}client-mode-ssid-connect" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="updateTime" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "client-mode-obj", namespace = "http://www.aerohive.com/configuration/ssid", propOrder = {
    "ahdeltaassistant",
    "ssid",
    "bandMode",
    "connect"
})
public class ClientModeObj {

    @XmlElement(name = "AH-DELTA-ASSISTANT")
    protected AhOnlyAct ahdeltaassistant;
    protected List<ClientModeSsid> ssid;
    @XmlElement(name = "band-mode")
    protected AhStringAct bandMode;
    protected ClientModeSsidConnect connect;
    @XmlAttribute(name = "updateTime")
    protected String updateTime;

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
     * Gets the value of the ssid property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ssid property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSsid().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClientModeSsid }
     * 
     * 
     */
    public List<ClientModeSsid> getSsid() {
        if (ssid == null) {
            ssid = new ArrayList<ClientModeSsid>();
        }
        return this.ssid;
    }

    /**
     * Gets the value of the bandMode property.
     * 
     * @return
     *     possible object is
     *     {@link AhStringAct }
     *     
     */
    public AhStringAct getBandMode() {
        return bandMode;
    }

    /**
     * Sets the value of the bandMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhStringAct }
     *     
     */
    public void setBandMode(AhStringAct value) {
        this.bandMode = value;
    }

    /**
     * Gets the value of the connect property.
     * 
     * @return
     *     possible object is
     *     {@link ClientModeSsidConnect }
     *     
     */
    public ClientModeSsidConnect getConnect() {
        return connect;
    }

    /**
     * Sets the value of the connect property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClientModeSsidConnect }
     *     
     */
    public void setConnect(ClientModeSsidConnect value) {
        this.connect = value;
    }

    /**
     * Gets the value of the updateTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpdateTime() {
        return updateTime;
    }

    /**
     * Sets the value of the updateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdateTime(String value) {
        this.updateTime = value;
    }

}
