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
 * <p>Java class for mgt-dhcp-server complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mgt-dhcp-server">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="authoritative-flag" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="arp-check" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="ip-pool" type="{http://www.aerohive.com/configuration/general}ah-name-act-value-quote-prohibited" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="options" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *                   &lt;element name="default-gateway" type="{http://www.aerohive.com/configuration/interface}dhcp-server-options-default-gateway" minOccurs="0"/>
 *                   &lt;element name="dns1" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *                   &lt;element name="dns2" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *                   &lt;element name="dns3" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *                   &lt;element name="domain-name" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *                   &lt;element name="lease-time" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="value" default="86400">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                                 &lt;minInclusive value="60"/>
 *                                 &lt;maxInclusive value="86400000"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                           &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="netmask" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *                   &lt;element name="pop3" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *                   &lt;element name="smtp" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *                   &lt;element name="wins" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *                   &lt;element name="wins1" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *                   &lt;element name="wins2" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *                   &lt;element name="ntp1" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *                   &lt;element name="ntp2" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *                   &lt;element name="logsrv" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *                   &lt;element name="mtu" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="value">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                                 &lt;minInclusive value="68"/>
 *                                 &lt;maxInclusive value="8192"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                           &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="hivemanager" type="{http://www.aerohive.com/configuration/general}ah-name-act-value" maxOccurs="2" minOccurs="0"/>
 *                   &lt;element name="custom" type="{http://www.aerohive.com/configuration/interface}dhcp-server-options-custom" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="enable" type="{http://www.aerohive.com/configuration/interface}dhcp-server-enable" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mgt-dhcp-server", namespace = "http://www.aerohive.com/configuration/interface", propOrder = {
    "authoritativeFlag",
    "arpCheck",
    "ipPool",
    "options",
    "enable"
})
public class MgtDhcpServer {

    @XmlElement(name = "authoritative-flag")
    protected AhOnlyAct authoritativeFlag;
    @XmlElement(name = "arp-check")
    protected AhOnlyAct arpCheck;
    @XmlElement(name = "ip-pool")
    protected List<AhNameActValueQuoteProhibited> ipPool;
    protected MgtDhcpServer.Options options;
    protected DhcpServerEnable enable;

    /**
     * Gets the value of the authoritativeFlag property.
     * 
     * @return
     *     possible object is
     *     {@link AhOnlyAct }
     *     
     */
    public AhOnlyAct getAuthoritativeFlag() {
        return authoritativeFlag;
    }

    /**
     * Sets the value of the authoritativeFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhOnlyAct }
     *     
     */
    public void setAuthoritativeFlag(AhOnlyAct value) {
        this.authoritativeFlag = value;
    }

    /**
     * Gets the value of the arpCheck property.
     * 
     * @return
     *     possible object is
     *     {@link AhOnlyAct }
     *     
     */
    public AhOnlyAct getArpCheck() {
        return arpCheck;
    }

    /**
     * Sets the value of the arpCheck property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhOnlyAct }
     *     
     */
    public void setArpCheck(AhOnlyAct value) {
        this.arpCheck = value;
    }

    /**
     * Gets the value of the ipPool property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ipPool property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIpPool().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AhNameActValueQuoteProhibited }
     * 
     * 
     */
    public List<AhNameActValueQuoteProhibited> getIpPool() {
        if (ipPool == null) {
            ipPool = new ArrayList<AhNameActValueQuoteProhibited>();
        }
        return this.ipPool;
    }

    /**
     * Gets the value of the options property.
     * 
     * @return
     *     possible object is
     *     {@link MgtDhcpServer.Options }
     *     
     */
    public MgtDhcpServer.Options getOptions() {
        return options;
    }

    /**
     * Sets the value of the options property.
     * 
     * @param value
     *     allowed object is
     *     {@link MgtDhcpServer.Options }
     *     
     */
    public void setOptions(MgtDhcpServer.Options value) {
        this.options = value;
    }

    /**
     * Gets the value of the enable property.
     * 
     * @return
     *     possible object is
     *     {@link DhcpServerEnable }
     *     
     */
    public DhcpServerEnable getEnable() {
        return enable;
    }

    /**
     * Sets the value of the enable property.
     * 
     * @param value
     *     allowed object is
     *     {@link DhcpServerEnable }
     *     
     */
    public void setEnable(DhcpServerEnable value) {
        this.enable = value;
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
     *         &lt;element name="default-gateway" type="{http://www.aerohive.com/configuration/interface}dhcp-server-options-default-gateway" minOccurs="0"/>
     *         &lt;element name="dns1" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
     *         &lt;element name="dns2" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
     *         &lt;element name="dns3" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
     *         &lt;element name="domain-name" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
     *         &lt;element name="lease-time" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="value" default="86400">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *                       &lt;minInclusive value="60"/>
     *                       &lt;maxInclusive value="86400000"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *                 &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="netmask" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
     *         &lt;element name="pop3" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
     *         &lt;element name="smtp" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
     *         &lt;element name="wins" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
     *         &lt;element name="wins1" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
     *         &lt;element name="wins2" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
     *         &lt;element name="ntp1" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
     *         &lt;element name="ntp2" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
     *         &lt;element name="logsrv" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
     *         &lt;element name="mtu" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="value">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *                       &lt;minInclusive value="68"/>
     *                       &lt;maxInclusive value="8192"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *                 &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="hivemanager" type="{http://www.aerohive.com/configuration/general}ah-name-act-value" maxOccurs="2" minOccurs="0"/>
     *         &lt;element name="custom" type="{http://www.aerohive.com/configuration/interface}dhcp-server-options-custom" maxOccurs="unbounded" minOccurs="0"/>
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
        "defaultGateway",
        "dns1",
        "dns2",
        "dns3",
        "domainName",
        "leaseTime",
        "netmask",
        "pop3",
        "smtp",
        "wins",
        "wins1",
        "wins2",
        "ntp1",
        "ntp2",
        "logsrv",
        "mtu",
        "hivemanager",
        "custom"
    })
    public static class Options {

        @XmlElement(name = "AH-DELTA-ASSISTANT")
        protected AhOnlyAct ahdeltaassistant;
        @XmlElement(name = "default-gateway")
        protected DhcpServerOptionsDefaultGateway defaultGateway;
        protected AhStringAct dns1;
        protected AhStringAct dns2;
        protected AhStringAct dns3;
        @XmlElement(name = "domain-name")
        protected AhStringAct domainName;
        @XmlElement(name = "lease-time")
        protected MgtDhcpServer.Options.LeaseTime leaseTime;
        protected AhStringAct netmask;
        protected AhStringAct pop3;
        protected AhStringAct smtp;
        protected AhStringAct wins;
        protected AhStringAct wins1;
        protected AhStringAct wins2;
        protected AhStringAct ntp1;
        protected AhStringAct ntp2;
        protected AhStringAct logsrv;
        protected MgtDhcpServer.Options.Mtu mtu;
        protected List<AhNameActValue> hivemanager;
        protected List<DhcpServerOptionsCustom> custom;

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
         * Gets the value of the defaultGateway property.
         * 
         * @return
         *     possible object is
         *     {@link DhcpServerOptionsDefaultGateway }
         *     
         */
        public DhcpServerOptionsDefaultGateway getDefaultGateway() {
            return defaultGateway;
        }

        /**
         * Sets the value of the defaultGateway property.
         * 
         * @param value
         *     allowed object is
         *     {@link DhcpServerOptionsDefaultGateway }
         *     
         */
        public void setDefaultGateway(DhcpServerOptionsDefaultGateway value) {
            this.defaultGateway = value;
        }

        /**
         * Gets the value of the dns1 property.
         * 
         * @return
         *     possible object is
         *     {@link AhStringAct }
         *     
         */
        public AhStringAct getDns1() {
            return dns1;
        }

        /**
         * Sets the value of the dns1 property.
         * 
         * @param value
         *     allowed object is
         *     {@link AhStringAct }
         *     
         */
        public void setDns1(AhStringAct value) {
            this.dns1 = value;
        }

        /**
         * Gets the value of the dns2 property.
         * 
         * @return
         *     possible object is
         *     {@link AhStringAct }
         *     
         */
        public AhStringAct getDns2() {
            return dns2;
        }

        /**
         * Sets the value of the dns2 property.
         * 
         * @param value
         *     allowed object is
         *     {@link AhStringAct }
         *     
         */
        public void setDns2(AhStringAct value) {
            this.dns2 = value;
        }

        /**
         * Gets the value of the dns3 property.
         * 
         * @return
         *     possible object is
         *     {@link AhStringAct }
         *     
         */
        public AhStringAct getDns3() {
            return dns3;
        }

        /**
         * Sets the value of the dns3 property.
         * 
         * @param value
         *     allowed object is
         *     {@link AhStringAct }
         *     
         */
        public void setDns3(AhStringAct value) {
            this.dns3 = value;
        }

        /**
         * Gets the value of the domainName property.
         * 
         * @return
         *     possible object is
         *     {@link AhStringAct }
         *     
         */
        public AhStringAct getDomainName() {
            return domainName;
        }

        /**
         * Sets the value of the domainName property.
         * 
         * @param value
         *     allowed object is
         *     {@link AhStringAct }
         *     
         */
        public void setDomainName(AhStringAct value) {
            this.domainName = value;
        }

        /**
         * Gets the value of the leaseTime property.
         * 
         * @return
         *     possible object is
         *     {@link MgtDhcpServer.Options.LeaseTime }
         *     
         */
        public MgtDhcpServer.Options.LeaseTime getLeaseTime() {
            return leaseTime;
        }

        /**
         * Sets the value of the leaseTime property.
         * 
         * @param value
         *     allowed object is
         *     {@link MgtDhcpServer.Options.LeaseTime }
         *     
         */
        public void setLeaseTime(MgtDhcpServer.Options.LeaseTime value) {
            this.leaseTime = value;
        }

        /**
         * Gets the value of the netmask property.
         * 
         * @return
         *     possible object is
         *     {@link AhStringAct }
         *     
         */
        public AhStringAct getNetmask() {
            return netmask;
        }

        /**
         * Sets the value of the netmask property.
         * 
         * @param value
         *     allowed object is
         *     {@link AhStringAct }
         *     
         */
        public void setNetmask(AhStringAct value) {
            this.netmask = value;
        }

        /**
         * Gets the value of the pop3 property.
         * 
         * @return
         *     possible object is
         *     {@link AhStringAct }
         *     
         */
        public AhStringAct getPop3() {
            return pop3;
        }

        /**
         * Sets the value of the pop3 property.
         * 
         * @param value
         *     allowed object is
         *     {@link AhStringAct }
         *     
         */
        public void setPop3(AhStringAct value) {
            this.pop3 = value;
        }

        /**
         * Gets the value of the smtp property.
         * 
         * @return
         *     possible object is
         *     {@link AhStringAct }
         *     
         */
        public AhStringAct getSmtp() {
            return smtp;
        }

        /**
         * Sets the value of the smtp property.
         * 
         * @param value
         *     allowed object is
         *     {@link AhStringAct }
         *     
         */
        public void setSmtp(AhStringAct value) {
            this.smtp = value;
        }

        /**
         * Gets the value of the wins property.
         * 
         * @return
         *     possible object is
         *     {@link AhStringAct }
         *     
         */
        public AhStringAct getWins() {
            return wins;
        }

        /**
         * Sets the value of the wins property.
         * 
         * @param value
         *     allowed object is
         *     {@link AhStringAct }
         *     
         */
        public void setWins(AhStringAct value) {
            this.wins = value;
        }

        /**
         * Gets the value of the wins1 property.
         * 
         * @return
         *     possible object is
         *     {@link AhStringAct }
         *     
         */
        public AhStringAct getWins1() {
            return wins1;
        }

        /**
         * Sets the value of the wins1 property.
         * 
         * @param value
         *     allowed object is
         *     {@link AhStringAct }
         *     
         */
        public void setWins1(AhStringAct value) {
            this.wins1 = value;
        }

        /**
         * Gets the value of the wins2 property.
         * 
         * @return
         *     possible object is
         *     {@link AhStringAct }
         *     
         */
        public AhStringAct getWins2() {
            return wins2;
        }

        /**
         * Sets the value of the wins2 property.
         * 
         * @param value
         *     allowed object is
         *     {@link AhStringAct }
         *     
         */
        public void setWins2(AhStringAct value) {
            this.wins2 = value;
        }

        /**
         * Gets the value of the ntp1 property.
         * 
         * @return
         *     possible object is
         *     {@link AhStringAct }
         *     
         */
        public AhStringAct getNtp1() {
            return ntp1;
        }

        /**
         * Sets the value of the ntp1 property.
         * 
         * @param value
         *     allowed object is
         *     {@link AhStringAct }
         *     
         */
        public void setNtp1(AhStringAct value) {
            this.ntp1 = value;
        }

        /**
         * Gets the value of the ntp2 property.
         * 
         * @return
         *     possible object is
         *     {@link AhStringAct }
         *     
         */
        public AhStringAct getNtp2() {
            return ntp2;
        }

        /**
         * Sets the value of the ntp2 property.
         * 
         * @param value
         *     allowed object is
         *     {@link AhStringAct }
         *     
         */
        public void setNtp2(AhStringAct value) {
            this.ntp2 = value;
        }

        /**
         * Gets the value of the logsrv property.
         * 
         * @return
         *     possible object is
         *     {@link AhStringAct }
         *     
         */
        public AhStringAct getLogsrv() {
            return logsrv;
        }

        /**
         * Sets the value of the logsrv property.
         * 
         * @param value
         *     allowed object is
         *     {@link AhStringAct }
         *     
         */
        public void setLogsrv(AhStringAct value) {
            this.logsrv = value;
        }

        /**
         * Gets the value of the mtu property.
         * 
         * @return
         *     possible object is
         *     {@link MgtDhcpServer.Options.Mtu }
         *     
         */
        public MgtDhcpServer.Options.Mtu getMtu() {
            return mtu;
        }

        /**
         * Sets the value of the mtu property.
         * 
         * @param value
         *     allowed object is
         *     {@link MgtDhcpServer.Options.Mtu }
         *     
         */
        public void setMtu(MgtDhcpServer.Options.Mtu value) {
            this.mtu = value;
        }

        /**
         * Gets the value of the hivemanager property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the hivemanager property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getHivemanager().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AhNameActValue }
         * 
         * 
         */
        public List<AhNameActValue> getHivemanager() {
            if (hivemanager == null) {
                hivemanager = new ArrayList<AhNameActValue>();
            }
            return this.hivemanager;
        }

        /**
         * Gets the value of the custom property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the custom property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCustom().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link DhcpServerOptionsCustom }
         * 
         * 
         */
        public List<DhcpServerOptionsCustom> getCustom() {
            if (custom == null) {
                custom = new ArrayList<DhcpServerOptionsCustom>();
            }
            return this.custom;
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
         *       &lt;attribute name="value" default="86400">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
         *             &lt;minInclusive value="60"/>
         *             &lt;maxInclusive value="86400000"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *       &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class LeaseTime {

            @XmlAttribute(name = "value")
            protected Integer value;
            @XmlAttribute(name = "operation", required = true)
            protected AhEnumAct operation;

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public int getValue() {
                if (value == null) {
                    return  86400;
                } else {
                    return value;
                }
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setValue(Integer value) {
                this.value = value;
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


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="value">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
         *             &lt;minInclusive value="68"/>
         *             &lt;maxInclusive value="8192"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *       &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Mtu {

            @XmlAttribute(name = "value")
            protected Integer value;
            @XmlAttribute(name = "operation", required = true)
            protected AhEnumAct operation;

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setValue(Integer value) {
                this.value = value;
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

    }

}
