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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for walled-garden-service-protocol complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="walled-garden-service-protocol">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="port" type="{http://www.aerohive.com/configuration/ssid}walled-garden-service-port" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.aerohive.com/configuration/general}ah-protocol-value" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "walled-garden-service-protocol", namespace = "http://www.aerohive.com/configuration/ssid", propOrder = {
    "port"
})
public class WalledGardenServiceProtocol {

    protected List<WalledGardenServicePort> port;
    @XmlAttribute(name = "name", required = true)
    protected int name;

    /**
     * Gets the value of the port property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the port property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPort().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WalledGardenServicePort }
     * 
     * 
     */
    public List<WalledGardenServicePort> getPort() {
        if (port == null) {
            port = new ArrayList<WalledGardenServicePort>();
        }
        return this.port;
    }

    /**
     * Gets the value of the name property.
     * 
     */
    public int getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     */
    public void setName(int value) {
        this.name = value;
    }

}