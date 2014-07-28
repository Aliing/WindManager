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
 * <p>Java class for tunnel-policy-client complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tunnel-policy-client">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ipsec-tunnel" type="{http://www.aerohive.com/configuration/others}client-ipsec-tunnel" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tunnel-policy-client", propOrder = {
    "ipsecTunnel"
})
public class TunnelPolicyClient {

    @XmlElement(name = "ipsec-tunnel")
    protected List<ClientIpsecTunnel> ipsecTunnel;

    /**
     * Gets the value of the ipsecTunnel property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ipsecTunnel property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIpsecTunnel().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClientIpsecTunnel }
     * 
     * 
     */
    public List<ClientIpsecTunnel> getIpsecTunnel() {
        if (ipsecTunnel == null) {
            ipsecTunnel = new ArrayList<ClientIpsecTunnel>();
        }
        return this.ipsecTunnel;
    }

}
