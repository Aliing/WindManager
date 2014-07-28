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
 * <p>Java class for ip-obj complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ip-obj">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="route" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *                   &lt;element name="default" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="gateway" type="{http://www.aerohive.com/configuration/others}gateway-type" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="net" type="{http://www.aerohive.com/configuration/general}ah-name-act-value-quote-prohibited" maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;element name="host" type="{http://www.aerohive.com/configuration/general}ah-name-act-value-quote-prohibited" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="path-mtu-discovery" type="{http://www.aerohive.com/configuration/others}ip-path-mtu-discovery" minOccurs="0"/>
 *         &lt;element name="tcp-mss-threshold" type="{http://www.aerohive.com/configuration/others}ip-tcp-mss-threshold" minOccurs="0"/>
 *         &lt;element name="igmp" type="{http://www.aerohive.com/configuration/others}ip-igmp" minOccurs="0"/>
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
@XmlType(name = "ip-obj", propOrder = {
    "route",
    "pathMtuDiscovery",
    "tcpMssThreshold",
    "igmp"
})
public class IpObj {

    protected IpObj.Route route;
    @XmlElement(name = "path-mtu-discovery")
    protected IpPathMtuDiscovery pathMtuDiscovery;
    @XmlElement(name = "tcp-mss-threshold")
    protected IpTcpMssThreshold tcpMssThreshold;
    protected IpIgmp igmp;
    @XmlAttribute(name = "updateTime")
    protected String updateTime;

    /**
     * Gets the value of the route property.
     * 
     * @return
     *     possible object is
     *     {@link IpObj.Route }
     *     
     */
    public IpObj.Route getRoute() {
        return route;
    }

    /**
     * Sets the value of the route property.
     * 
     * @param value
     *     allowed object is
     *     {@link IpObj.Route }
     *     
     */
    public void setRoute(IpObj.Route value) {
        this.route = value;
    }

    /**
     * Gets the value of the pathMtuDiscovery property.
     * 
     * @return
     *     possible object is
     *     {@link IpPathMtuDiscovery }
     *     
     */
    public IpPathMtuDiscovery getPathMtuDiscovery() {
        return pathMtuDiscovery;
    }

    /**
     * Sets the value of the pathMtuDiscovery property.
     * 
     * @param value
     *     allowed object is
     *     {@link IpPathMtuDiscovery }
     *     
     */
    public void setPathMtuDiscovery(IpPathMtuDiscovery value) {
        this.pathMtuDiscovery = value;
    }

    /**
     * Gets the value of the tcpMssThreshold property.
     * 
     * @return
     *     possible object is
     *     {@link IpTcpMssThreshold }
     *     
     */
    public IpTcpMssThreshold getTcpMssThreshold() {
        return tcpMssThreshold;
    }

    /**
     * Sets the value of the tcpMssThreshold property.
     * 
     * @param value
     *     allowed object is
     *     {@link IpTcpMssThreshold }
     *     
     */
    public void setTcpMssThreshold(IpTcpMssThreshold value) {
        this.tcpMssThreshold = value;
    }

    /**
     * Gets the value of the igmp property.
     * 
     * @return
     *     possible object is
     *     {@link IpIgmp }
     *     
     */
    public IpIgmp getIgmp() {
        return igmp;
    }

    /**
     * Sets the value of the igmp property.
     * 
     * @param value
     *     allowed object is
     *     {@link IpIgmp }
     *     
     */
    public void setIgmp(IpIgmp value) {
        this.igmp = value;
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
     *         &lt;element name="default" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="gateway" type="{http://www.aerohive.com/configuration/others}gateway-type" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="net" type="{http://www.aerohive.com/configuration/general}ah-name-act-value-quote-prohibited" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element name="host" type="{http://www.aerohive.com/configuration/general}ah-name-act-value-quote-prohibited" maxOccurs="unbounded" minOccurs="0"/>
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
        "_default",
        "net",
        "host"
    })
    public static class Route {

        @XmlElement(name = "AH-DELTA-ASSISTANT")
        protected AhOnlyAct ahdeltaassistant;
        @XmlElement(name = "default")
        protected IpObj.Route.Default _default;
        protected List<AhNameActValueQuoteProhibited> net;
        protected List<AhNameActValueQuoteProhibited> host;

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
         * Gets the value of the default property.
         * 
         * @return
         *     possible object is
         *     {@link IpObj.Route.Default }
         *     
         */
        public IpObj.Route.Default getDefault() {
            return _default;
        }

        /**
         * Sets the value of the default property.
         * 
         * @param value
         *     allowed object is
         *     {@link IpObj.Route.Default }
         *     
         */
        public void setDefault(IpObj.Route.Default value) {
            this._default = value;
        }

        /**
         * Gets the value of the net property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the net property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getNet().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AhNameActValueQuoteProhibited }
         * 
         * 
         */
        public List<AhNameActValueQuoteProhibited> getNet() {
            if (net == null) {
                net = new ArrayList<AhNameActValueQuoteProhibited>();
            }
            return this.net;
        }

        /**
         * Gets the value of the host property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the host property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getHost().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AhNameActValueQuoteProhibited }
         * 
         * 
         */
        public List<AhNameActValueQuoteProhibited> getHost() {
            if (host == null) {
                host = new ArrayList<AhNameActValueQuoteProhibited>();
            }
            return this.host;
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
         *         &lt;element name="gateway" type="{http://www.aerohive.com/configuration/others}gateway-type" minOccurs="0"/>
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
            "gateway"
        })
        public static class Default {

            protected GatewayType gateway;

            /**
             * Gets the value of the gateway property.
             * 
             * @return
             *     possible object is
             *     {@link GatewayType }
             *     
             */
            public GatewayType getGateway() {
                return gateway;
            }

            /**
             * Sets the value of the gateway property.
             * 
             * @param value
             *     allowed object is
             *     {@link GatewayType }
             *     
             */
            public void setGateway(GatewayType value) {
                this.gateway = value;
            }

        }

    }

}