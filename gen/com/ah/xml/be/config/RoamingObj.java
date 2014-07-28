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
 * <p>Java class for roaming-obj complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="roaming-obj">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cache" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *                   &lt;element name="query-interval" type="{http://www.aerohive.com/configuration/roaming}cache-query-interval" minOccurs="0"/>
 *                   &lt;element name="update-interval" type="{http://www.aerohive.com/configuration/roaming}cache-update-interval" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="hop" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" default="1">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                       &lt;minInclusive value="0"/>
 *                       &lt;maxInclusive value="16"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="neighbor" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="query-interval" type="{http://www.aerohive.com/configuration/roaming}neighbor-query-interval" minOccurs="0"/>
 *                   &lt;element name="include" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *                             &lt;element name="ip" type="{http://www.aerohive.com/configuration/roaming}include-ip" maxOccurs="unbounded" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="exclude" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *                             &lt;element name="ip" type="{http://www.aerohive.com/configuration/general}ah-name-act-value" maxOccurs="unbounded" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="port" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" default="3000">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                       &lt;minInclusive value="1500"/>
 *                       &lt;maxInclusive value="65000"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="cache-broadcast" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="neighbor-type" type="{http://www.aerohive.com/configuration/roaming}cache-broadcast-neighbor-type" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
@XmlType(name = "roaming-obj", namespace = "http://www.aerohive.com/configuration/roaming", propOrder = {
    "cache",
    "hop",
    "neighbor",
    "port",
    "cacheBroadcast"
})
public class RoamingObj {

    protected RoamingObj.Cache cache;
    protected RoamingObj.Hop hop;
    protected RoamingObj.Neighbor neighbor;
    protected RoamingObj.Port port;
    @XmlElement(name = "cache-broadcast")
    protected RoamingObj.CacheBroadcast cacheBroadcast;
    @XmlAttribute(name = "updateTime")
    protected String updateTime;

    /**
     * Gets the value of the cache property.
     * 
     * @return
     *     possible object is
     *     {@link RoamingObj.Cache }
     *     
     */
    public RoamingObj.Cache getCache() {
        return cache;
    }

    /**
     * Sets the value of the cache property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoamingObj.Cache }
     *     
     */
    public void setCache(RoamingObj.Cache value) {
        this.cache = value;
    }

    /**
     * Gets the value of the hop property.
     * 
     * @return
     *     possible object is
     *     {@link RoamingObj.Hop }
     *     
     */
    public RoamingObj.Hop getHop() {
        return hop;
    }

    /**
     * Sets the value of the hop property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoamingObj.Hop }
     *     
     */
    public void setHop(RoamingObj.Hop value) {
        this.hop = value;
    }

    /**
     * Gets the value of the neighbor property.
     * 
     * @return
     *     possible object is
     *     {@link RoamingObj.Neighbor }
     *     
     */
    public RoamingObj.Neighbor getNeighbor() {
        return neighbor;
    }

    /**
     * Sets the value of the neighbor property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoamingObj.Neighbor }
     *     
     */
    public void setNeighbor(RoamingObj.Neighbor value) {
        this.neighbor = value;
    }

    /**
     * Gets the value of the port property.
     * 
     * @return
     *     possible object is
     *     {@link RoamingObj.Port }
     *     
     */
    public RoamingObj.Port getPort() {
        return port;
    }

    /**
     * Sets the value of the port property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoamingObj.Port }
     *     
     */
    public void setPort(RoamingObj.Port value) {
        this.port = value;
    }

    /**
     * Gets the value of the cacheBroadcast property.
     * 
     * @return
     *     possible object is
     *     {@link RoamingObj.CacheBroadcast }
     *     
     */
    public RoamingObj.CacheBroadcast getCacheBroadcast() {
        return cacheBroadcast;
    }

    /**
     * Sets the value of the cacheBroadcast property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoamingObj.CacheBroadcast }
     *     
     */
    public void setCacheBroadcast(RoamingObj.CacheBroadcast value) {
        this.cacheBroadcast = value;
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
     *         &lt;element name="query-interval" type="{http://www.aerohive.com/configuration/roaming}cache-query-interval" minOccurs="0"/>
     *         &lt;element name="update-interval" type="{http://www.aerohive.com/configuration/roaming}cache-update-interval" minOccurs="0"/>
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
        "queryInterval",
        "updateInterval"
    })
    public static class Cache {

        @XmlElement(name = "AH-DELTA-ASSISTANT")
        protected AhOnlyAct ahdeltaassistant;
        @XmlElement(name = "query-interval")
        protected CacheQueryInterval queryInterval;
        @XmlElement(name = "update-interval")
        protected CacheUpdateInterval updateInterval;

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
         * Gets the value of the queryInterval property.
         * 
         * @return
         *     possible object is
         *     {@link CacheQueryInterval }
         *     
         */
        public CacheQueryInterval getQueryInterval() {
            return queryInterval;
        }

        /**
         * Sets the value of the queryInterval property.
         * 
         * @param value
         *     allowed object is
         *     {@link CacheQueryInterval }
         *     
         */
        public void setQueryInterval(CacheQueryInterval value) {
            this.queryInterval = value;
        }

        /**
         * Gets the value of the updateInterval property.
         * 
         * @return
         *     possible object is
         *     {@link CacheUpdateInterval }
         *     
         */
        public CacheUpdateInterval getUpdateInterval() {
            return updateInterval;
        }

        /**
         * Sets the value of the updateInterval property.
         * 
         * @param value
         *     allowed object is
         *     {@link CacheUpdateInterval }
         *     
         */
        public void setUpdateInterval(CacheUpdateInterval value) {
            this.updateInterval = value;
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
     *       &lt;sequence>
     *         &lt;element name="neighbor-type" type="{http://www.aerohive.com/configuration/roaming}cache-broadcast-neighbor-type" minOccurs="0"/>
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
        "neighborType"
    })
    public static class CacheBroadcast {

        @XmlElement(name = "neighbor-type")
        protected CacheBroadcastNeighborType neighborType;

        /**
         * Gets the value of the neighborType property.
         * 
         * @return
         *     possible object is
         *     {@link CacheBroadcastNeighborType }
         *     
         */
        public CacheBroadcastNeighborType getNeighborType() {
            return neighborType;
        }

        /**
         * Sets the value of the neighborType property.
         * 
         * @param value
         *     allowed object is
         *     {@link CacheBroadcastNeighborType }
         *     
         */
        public void setNeighborType(CacheBroadcastNeighborType value) {
            this.neighborType = value;
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
     *       &lt;attribute name="value" default="1">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *             &lt;minInclusive value="0"/>
     *             &lt;maxInclusive value="16"/>
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
    public static class Hop {

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
                return  1;
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
     *       &lt;sequence>
     *         &lt;element name="query-interval" type="{http://www.aerohive.com/configuration/roaming}neighbor-query-interval" minOccurs="0"/>
     *         &lt;element name="include" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
     *                   &lt;element name="ip" type="{http://www.aerohive.com/configuration/roaming}include-ip" maxOccurs="unbounded" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="exclude" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
     *                   &lt;element name="ip" type="{http://www.aerohive.com/configuration/general}ah-name-act-value" maxOccurs="unbounded" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
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
        "queryInterval",
        "include",
        "exclude"
    })
    public static class Neighbor {

        @XmlElement(name = "query-interval")
        protected NeighborQueryInterval queryInterval;
        protected RoamingObj.Neighbor.Include include;
        protected RoamingObj.Neighbor.Exclude exclude;

        /**
         * Gets the value of the queryInterval property.
         * 
         * @return
         *     possible object is
         *     {@link NeighborQueryInterval }
         *     
         */
        public NeighborQueryInterval getQueryInterval() {
            return queryInterval;
        }

        /**
         * Sets the value of the queryInterval property.
         * 
         * @param value
         *     allowed object is
         *     {@link NeighborQueryInterval }
         *     
         */
        public void setQueryInterval(NeighborQueryInterval value) {
            this.queryInterval = value;
        }

        /**
         * Gets the value of the include property.
         * 
         * @return
         *     possible object is
         *     {@link RoamingObj.Neighbor.Include }
         *     
         */
        public RoamingObj.Neighbor.Include getInclude() {
            return include;
        }

        /**
         * Sets the value of the include property.
         * 
         * @param value
         *     allowed object is
         *     {@link RoamingObj.Neighbor.Include }
         *     
         */
        public void setInclude(RoamingObj.Neighbor.Include value) {
            this.include = value;
        }

        /**
         * Gets the value of the exclude property.
         * 
         * @return
         *     possible object is
         *     {@link RoamingObj.Neighbor.Exclude }
         *     
         */
        public RoamingObj.Neighbor.Exclude getExclude() {
            return exclude;
        }

        /**
         * Sets the value of the exclude property.
         * 
         * @param value
         *     allowed object is
         *     {@link RoamingObj.Neighbor.Exclude }
         *     
         */
        public void setExclude(RoamingObj.Neighbor.Exclude value) {
            this.exclude = value;
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
         *         &lt;element name="ip" type="{http://www.aerohive.com/configuration/general}ah-name-act-value" maxOccurs="unbounded" minOccurs="0"/>
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
            "ip"
        })
        public static class Exclude {

            @XmlElement(name = "AH-DELTA-ASSISTANT")
            protected AhOnlyAct ahdeltaassistant;
            protected List<AhNameActValue> ip;

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
             * Gets the value of the ip property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the ip property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getIp().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link AhNameActValue }
             * 
             * 
             */
            public List<AhNameActValue> getIp() {
                if (ip == null) {
                    ip = new ArrayList<AhNameActValue>();
                }
                return this.ip;
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
         *       &lt;sequence>
         *         &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
         *         &lt;element name="ip" type="{http://www.aerohive.com/configuration/roaming}include-ip" maxOccurs="unbounded" minOccurs="0"/>
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
            "ip"
        })
        public static class Include {

            @XmlElement(name = "AH-DELTA-ASSISTANT")
            protected AhOnlyAct ahdeltaassistant;
            protected List<IncludeIp> ip;

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
             * Gets the value of the ip property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the ip property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getIp().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link IncludeIp }
             * 
             * 
             */
            public List<IncludeIp> getIp() {
                if (ip == null) {
                    ip = new ArrayList<IncludeIp>();
                }
                return this.ip;
            }

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
     *       &lt;attribute name="value" default="3000">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *             &lt;minInclusive value="1500"/>
     *             &lt;maxInclusive value="65000"/>
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
    public static class Port {

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
                return  3000;
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

}
