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
 * <p>Java class for application-identification complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="application-identification">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="shutdown" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="cdp-index" type="{http://www.aerohive.com/configuration/others}app-cdp-index" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "application-identification", propOrder = {
    "ahdeltaassistant",
    "shutdown",
    "cdpIndex"
})
public class ApplicationIdentification {

    @XmlElement(name = "AH-DELTA-ASSISTANT")
    protected AhOnlyAct ahdeltaassistant;
    protected AhOnlyAct shutdown;
    @XmlElement(name = "cdp-index")
    protected List<AppCdpIndex> cdpIndex;

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
     * Gets the value of the shutdown property.
     * 
     * @return
     *     possible object is
     *     {@link AhOnlyAct }
     *     
     */
    public AhOnlyAct getShutdown() {
        return shutdown;
    }

    /**
     * Sets the value of the shutdown property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhOnlyAct }
     *     
     */
    public void setShutdown(AhOnlyAct value) {
        this.shutdown = value;
    }

    /**
     * Gets the value of the cdpIndex property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cdpIndex property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCdpIndex().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AppCdpIndex }
     * 
     * 
     */
    public List<AppCdpIndex> getCdpIndex() {
        if (cdpIndex == null) {
            cdpIndex = new ArrayList<AppCdpIndex>();
        }
        return this.cdpIndex;
    }

}