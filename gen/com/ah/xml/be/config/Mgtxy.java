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
 * <p>Java class for mgtxy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mgtxy">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="vlan" type="{http://www.aerohive.com/configuration/interface}interface-vlan" minOccurs="0"/>
 *         &lt;element name="ip" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *         &lt;element name="manage" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *                   &lt;element name="ping" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="dhcp-server" type="{http://www.aerohive.com/configuration/interface}mgt-dhcp-server" minOccurs="0"/>
 *         &lt;element name="ip-helper" type="{http://www.aerohive.com/configuration/interface}interface-ip-helper" minOccurs="0"/>
 *         &lt;element name="dns-server" type="{http://www.aerohive.com/configuration/interface}mgt-dns-server" minOccurs="0"/>
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
@XmlType(name = "mgtxy", namespace = "http://www.aerohive.com/configuration/interface", propOrder = {
    "vlan",
    "ip",
    "manage",
    "dhcpServer",
    "ipHelper",
    "dnsServer"
})
public class Mgtxy {

    protected InterfaceVlan vlan;
    protected AhStringAct ip;
    protected Mgtxy.Manage manage;
    @XmlElement(name = "dhcp-server")
    protected MgtDhcpServer dhcpServer;
    @XmlElement(name = "ip-helper")
    protected InterfaceIpHelper ipHelper;
    @XmlElement(name = "dns-server")
    protected MgtDnsServer dnsServer;
    @XmlAttribute(name = "updateTime")
    protected String updateTime;

    /**
     * Gets the value of the vlan property.
     * 
     * @return
     *     possible object is
     *     {@link InterfaceVlan }
     *     
     */
    public InterfaceVlan getVlan() {
        return vlan;
    }

    /**
     * Sets the value of the vlan property.
     * 
     * @param value
     *     allowed object is
     *     {@link InterfaceVlan }
     *     
     */
    public void setVlan(InterfaceVlan value) {
        this.vlan = value;
    }

    /**
     * Gets the value of the ip property.
     * 
     * @return
     *     possible object is
     *     {@link AhStringAct }
     *     
     */
    public AhStringAct getIp() {
        return ip;
    }

    /**
     * Sets the value of the ip property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhStringAct }
     *     
     */
    public void setIp(AhStringAct value) {
        this.ip = value;
    }

    /**
     * Gets the value of the manage property.
     * 
     * @return
     *     possible object is
     *     {@link Mgtxy.Manage }
     *     
     */
    public Mgtxy.Manage getManage() {
        return manage;
    }

    /**
     * Sets the value of the manage property.
     * 
     * @param value
     *     allowed object is
     *     {@link Mgtxy.Manage }
     *     
     */
    public void setManage(Mgtxy.Manage value) {
        this.manage = value;
    }

    /**
     * Gets the value of the dhcpServer property.
     * 
     * @return
     *     possible object is
     *     {@link MgtDhcpServer }
     *     
     */
    public MgtDhcpServer getDhcpServer() {
        return dhcpServer;
    }

    /**
     * Sets the value of the dhcpServer property.
     * 
     * @param value
     *     allowed object is
     *     {@link MgtDhcpServer }
     *     
     */
    public void setDhcpServer(MgtDhcpServer value) {
        this.dhcpServer = value;
    }

    /**
     * Gets the value of the ipHelper property.
     * 
     * @return
     *     possible object is
     *     {@link InterfaceIpHelper }
     *     
     */
    public InterfaceIpHelper getIpHelper() {
        return ipHelper;
    }

    /**
     * Sets the value of the ipHelper property.
     * 
     * @param value
     *     allowed object is
     *     {@link InterfaceIpHelper }
     *     
     */
    public void setIpHelper(InterfaceIpHelper value) {
        this.ipHelper = value;
    }

    /**
     * Gets the value of the dnsServer property.
     * 
     * @return
     *     possible object is
     *     {@link MgtDnsServer }
     *     
     */
    public MgtDnsServer getDnsServer() {
        return dnsServer;
    }

    /**
     * Sets the value of the dnsServer property.
     * 
     * @param value
     *     allowed object is
     *     {@link MgtDnsServer }
     *     
     */
    public void setDnsServer(MgtDnsServer value) {
        this.dnsServer = value;
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


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
     *         &lt;element name="ping" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "ahdeltaassistant",
        "ping"
    })
    public static class Manage {

        @XmlElement(name = "AH-DELTA-ASSISTANT")
        protected AhOnlyAct ahdeltaassistant;
        protected AhOnlyAct ping;

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
         * Gets the value of the ping property.
         * 
         * @return
         *     possible object is
         *     {@link AhOnlyAct }
         *     
         */
        public AhOnlyAct getPing() {
            return ping;
        }

        /**
         * Sets the value of the ping property.
         * 
         * @param value
         *     allowed object is
         *     {@link AhOnlyAct }
         *     
         */
        public void setPing(AhOnlyAct value) {
            this.ping = value;
        }

    }

}
