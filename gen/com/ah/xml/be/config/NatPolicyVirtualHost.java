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
 * <p>Java class for nat-policy-virtual-host complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="nat-policy-virtual-host">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="inside-host" type="{http://www.aerohive.com/configuration/others}nat-policy-vhost-inside-host" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "nat-policy-virtual-host", propOrder = {
    "insideHost"
})
public class NatPolicyVirtualHost {

    @XmlElement(name = "inside-host")
    protected NatPolicyVhostInsideHost insideHost;

    /**
     * Gets the value of the insideHost property.
     * 
     * @return
     *     possible object is
     *     {@link NatPolicyVhostInsideHost }
     *     
     */
    public NatPolicyVhostInsideHost getInsideHost() {
        return insideHost;
    }

    /**
     * Sets the value of the insideHost property.
     * 
     * @param value
     *     allowed object is
     *     {@link NatPolicyVhostInsideHost }
     *     
     */
    public void setInsideHost(NatPolicyVhostInsideHost value) {
        this.insideHost = value;
    }

}
