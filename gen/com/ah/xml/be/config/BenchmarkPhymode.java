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
 * <p>Java class for benchmark-phymode complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="benchmark-phymode">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="_11a" type="{http://www.aerohive.com/configuration/radio}phymode-rate" minOccurs="0"/>
 *         &lt;element name="_11b" type="{http://www.aerohive.com/configuration/radio}phymode-rate" minOccurs="0"/>
 *         &lt;element name="_11g" type="{http://www.aerohive.com/configuration/radio}phymode-rate" minOccurs="0"/>
 *         &lt;element name="_11n" type="{http://www.aerohive.com/configuration/radio}phymode-rate" minOccurs="0"/>
 *         &lt;element name="_11ac" type="{http://www.aerohive.com/configuration/radio}phymode-rate" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "benchmark-phymode", namespace = "http://www.aerohive.com/configuration/radio", propOrder = {
    "_11A",
    "_11B",
    "_11G",
    "_11N",
    "_11Ac"
})
public class BenchmarkPhymode {

    @XmlElement(name = "_11a")
    protected PhymodeRate _11A;
    @XmlElement(name = "_11b")
    protected PhymodeRate _11B;
    @XmlElement(name = "_11g")
    protected PhymodeRate _11G;
    @XmlElement(name = "_11n")
    protected PhymodeRate _11N;
    @XmlElement(name = "_11ac")
    protected PhymodeRate _11Ac;

    /**
     * Gets the value of the 11A property.
     * 
     * @return
     *     possible object is
     *     {@link PhymodeRate }
     *     
     */
    public PhymodeRate get11A() {
        return _11A;
    }

    /**
     * Sets the value of the 11A property.
     * 
     * @param value
     *     allowed object is
     *     {@link PhymodeRate }
     *     
     */
    public void set11A(PhymodeRate value) {
        this._11A = value;
    }

    /**
     * Gets the value of the 11B property.
     * 
     * @return
     *     possible object is
     *     {@link PhymodeRate }
     *     
     */
    public PhymodeRate get11B() {
        return _11B;
    }

    /**
     * Sets the value of the 11B property.
     * 
     * @param value
     *     allowed object is
     *     {@link PhymodeRate }
     *     
     */
    public void set11B(PhymodeRate value) {
        this._11B = value;
    }

    /**
     * Gets the value of the 11G property.
     * 
     * @return
     *     possible object is
     *     {@link PhymodeRate }
     *     
     */
    public PhymodeRate get11G() {
        return _11G;
    }

    /**
     * Sets the value of the 11G property.
     * 
     * @param value
     *     allowed object is
     *     {@link PhymodeRate }
     *     
     */
    public void set11G(PhymodeRate value) {
        this._11G = value;
    }

    /**
     * Gets the value of the 11N property.
     * 
     * @return
     *     possible object is
     *     {@link PhymodeRate }
     *     
     */
    public PhymodeRate get11N() {
        return _11N;
    }

    /**
     * Sets the value of the 11N property.
     * 
     * @param value
     *     allowed object is
     *     {@link PhymodeRate }
     *     
     */
    public void set11N(PhymodeRate value) {
        this._11N = value;
    }

    /**
     * Gets the value of the 11Ac property.
     * 
     * @return
     *     possible object is
     *     {@link PhymodeRate }
     *     
     */
    public PhymodeRate get11Ac() {
        return _11Ac;
    }

    /**
     * Sets the value of the 11Ac property.
     * 
     * @param value
     *     allowed object is
     *     {@link PhymodeRate }
     *     
     */
    public void set11Ac(PhymodeRate value) {
        this._11Ac = value;
    }

}
