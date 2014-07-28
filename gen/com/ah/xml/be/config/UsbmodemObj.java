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
 * <p>Java class for usbmodem-obj complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="usbmodem-obj">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="modem-id" type="{http://www.aerohive.com/configuration/admin}modem-id" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="mode" type="{http://www.aerohive.com/configuration/admin}modem-mode" minOccurs="0"/>
 *         &lt;element name="power" type="{http://www.aerohive.com/configuration/admin}usbmodem-power" minOccurs="0"/>
 *         &lt;element name="network-mode" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *         &lt;element name="enable" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "usbmodem-obj", namespace = "http://www.aerohive.com/configuration/admin", propOrder = {
    "ahdeltaassistant",
    "modemId",
    "mode",
    "power",
    "networkMode",
    "enable"
})
public class UsbmodemObj {

    @XmlElement(name = "AH-DELTA-ASSISTANT")
    protected AhOnlyAct ahdeltaassistant;
    @XmlElement(name = "modem-id")
    protected List<ModemId> modemId;
    protected ModemMode mode;
    protected UsbmodemPower power;
    @XmlElement(name = "network-mode")
    protected AhStringAct networkMode;
    protected AhOnlyAct enable;

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
     * Gets the value of the modemId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the modemId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getModemId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ModemId }
     * 
     * 
     */
    public List<ModemId> getModemId() {
        if (modemId == null) {
            modemId = new ArrayList<ModemId>();
        }
        return this.modemId;
    }

    /**
     * Gets the value of the mode property.
     * 
     * @return
     *     possible object is
     *     {@link ModemMode }
     *     
     */
    public ModemMode getMode() {
        return mode;
    }

    /**
     * Sets the value of the mode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ModemMode }
     *     
     */
    public void setMode(ModemMode value) {
        this.mode = value;
    }

    /**
     * Gets the value of the power property.
     * 
     * @return
     *     possible object is
     *     {@link UsbmodemPower }
     *     
     */
    public UsbmodemPower getPower() {
        return power;
    }

    /**
     * Sets the value of the power property.
     * 
     * @param value
     *     allowed object is
     *     {@link UsbmodemPower }
     *     
     */
    public void setPower(UsbmodemPower value) {
        this.power = value;
    }

    /**
     * Gets the value of the networkMode property.
     * 
     * @return
     *     possible object is
     *     {@link AhStringAct }
     *     
     */
    public AhStringAct getNetworkMode() {
        return networkMode;
    }

    /**
     * Sets the value of the networkMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhStringAct }
     *     
     */
    public void setNetworkMode(AhStringAct value) {
        this.networkMode = value;
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

}