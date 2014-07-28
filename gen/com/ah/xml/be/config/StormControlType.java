//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.07.01 at 11:29:17 AM CST 
//


package com.ah.xml.be.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for storm-control-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="storm-control-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="all" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="broadcast" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="multicast" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="unknown-unicast" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="tcp-syn" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="mode" type="{http://www.aerohive.com/configuration/interface}geth-storm-control-mode" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "storm-control-type", namespace = "http://www.aerohive.com/configuration/interface", propOrder = {
    "ahdeltaassistant",
    "all",
    "broadcast",
    "multicast",
    "unknownUnicast",
    "tcpSyn",
    "mode"
})
public class StormControlType {

    @XmlElement(name = "AH-DELTA-ASSISTANT")
    protected AhOnlyAct ahdeltaassistant;
    protected AhOnlyAct all;
    protected AhOnlyAct broadcast;
    protected AhOnlyAct multicast;
    @XmlElement(name = "unknown-unicast")
    protected AhOnlyAct unknownUnicast;
    @XmlElement(name = "tcp-syn")
    protected AhOnlyAct tcpSyn;
    protected GethStormControlMode mode;
    @XmlAttribute(name = "operation", required = true)
    protected AhEnumAct operation;

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
     * Gets the value of the all property.
     * 
     * @return
     *     possible object is
     *     {@link AhOnlyAct }
     *     
     */
    public AhOnlyAct getAll() {
        return all;
    }

    /**
     * Sets the value of the all property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhOnlyAct }
     *     
     */
    public void setAll(AhOnlyAct value) {
        this.all = value;
    }

    /**
     * Gets the value of the broadcast property.
     * 
     * @return
     *     possible object is
     *     {@link AhOnlyAct }
     *     
     */
    public AhOnlyAct getBroadcast() {
        return broadcast;
    }

    /**
     * Sets the value of the broadcast property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhOnlyAct }
     *     
     */
    public void setBroadcast(AhOnlyAct value) {
        this.broadcast = value;
    }

    /**
     * Gets the value of the multicast property.
     * 
     * @return
     *     possible object is
     *     {@link AhOnlyAct }
     *     
     */
    public AhOnlyAct getMulticast() {
        return multicast;
    }

    /**
     * Sets the value of the multicast property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhOnlyAct }
     *     
     */
    public void setMulticast(AhOnlyAct value) {
        this.multicast = value;
    }

    /**
     * Gets the value of the unknownUnicast property.
     * 
     * @return
     *     possible object is
     *     {@link AhOnlyAct }
     *     
     */
    public AhOnlyAct getUnknownUnicast() {
        return unknownUnicast;
    }

    /**
     * Sets the value of the unknownUnicast property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhOnlyAct }
     *     
     */
    public void setUnknownUnicast(AhOnlyAct value) {
        this.unknownUnicast = value;
    }

    /**
     * Gets the value of the tcpSyn property.
     * 
     * @return
     *     possible object is
     *     {@link AhOnlyAct }
     *     
     */
    public AhOnlyAct getTcpSyn() {
        return tcpSyn;
    }

    /**
     * Sets the value of the tcpSyn property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhOnlyAct }
     *     
     */
    public void setTcpSyn(AhOnlyAct value) {
        this.tcpSyn = value;
    }

    /**
     * Gets the value of the mode property.
     * 
     * @return
     *     possible object is
     *     {@link GethStormControlMode }
     *     
     */
    public GethStormControlMode getMode() {
        return mode;
    }

    /**
     * Sets the value of the mode property.
     * 
     * @param value
     *     allowed object is
     *     {@link GethStormControlMode }
     *     
     */
    public void setMode(GethStormControlMode value) {
        this.mode = value;
    }

    /**
     * Gets the value of the operation property.
     * 
     * @return
     *     possible object is
     *     {@link AhEnumAct }
     *     
     */
    public AhEnumAct getOperation() {
        return operation;
    }

    /**
     * Sets the value of the operation property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhEnumAct }
     *     
     */
    public void setOperation(AhEnumAct value) {
        this.operation = value;
    }

}