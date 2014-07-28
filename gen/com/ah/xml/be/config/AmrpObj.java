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
 * <p>Java class for amrp-obj complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="amrp-obj">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="metric" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *                   &lt;element name="poll-interval" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="value" default="60">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                                 &lt;minInclusive value="10"/>
 *                                 &lt;maxInclusive value="300"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                           &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="type" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="value" type="{http://www.aerohive.com/configuration/others}amrp-metric-type-value" default="normal" />
 *                           &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="neighbor" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="metric" type="{http://www.aerohive.com/configuration/others}amrp-neighbor-metric" minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act-value" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="vpn-tunnel" type="{http://www.aerohive.com/configuration/others}amrp-vpn-tunnel" minOccurs="0"/>
 *         &lt;element name="interface" type="{http://www.aerohive.com/configuration/others}amrp-interface" minOccurs="0"/>
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
@XmlType(name = "amrp-obj", propOrder = {
    "metric",
    "neighbor",
    "vpnTunnel",
    "_interface"
})
public class AmrpObj {

    protected AmrpObj.Metric metric;
    protected List<AmrpObj.Neighbor> neighbor;
    @XmlElement(name = "vpn-tunnel")
    protected AmrpVpnTunnel vpnTunnel;
    @XmlElement(name = "interface")
    protected AmrpInterface _interface;
    @XmlAttribute(name = "updateTime")
    protected String updateTime;

    /**
     * Gets the value of the metric property.
     * 
     * @return
     *     possible object is
     *     {@link AmrpObj.Metric }
     *     
     */
    public AmrpObj.Metric getMetric() {
        return metric;
    }

    /**
     * Sets the value of the metric property.
     * 
     * @param value
     *     allowed object is
     *     {@link AmrpObj.Metric }
     *     
     */
    public void setMetric(AmrpObj.Metric value) {
        this.metric = value;
    }

    /**
     * Gets the value of the neighbor property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the neighbor property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNeighbor().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AmrpObj.Neighbor }
     * 
     * 
     */
    public List<AmrpObj.Neighbor> getNeighbor() {
        if (neighbor == null) {
            neighbor = new ArrayList<AmrpObj.Neighbor>();
        }
        return this.neighbor;
    }

    /**
     * Gets the value of the vpnTunnel property.
     * 
     * @return
     *     possible object is
     *     {@link AmrpVpnTunnel }
     *     
     */
    public AmrpVpnTunnel getVpnTunnel() {
        return vpnTunnel;
    }

    /**
     * Sets the value of the vpnTunnel property.
     * 
     * @param value
     *     allowed object is
     *     {@link AmrpVpnTunnel }
     *     
     */
    public void setVpnTunnel(AmrpVpnTunnel value) {
        this.vpnTunnel = value;
    }

    /**
     * Gets the value of the interface property.
     * 
     * @return
     *     possible object is
     *     {@link AmrpInterface }
     *     
     */
    public AmrpInterface getInterface() {
        return _interface;
    }

    /**
     * Sets the value of the interface property.
     * 
     * @param value
     *     allowed object is
     *     {@link AmrpInterface }
     *     
     */
    public void setInterface(AmrpInterface value) {
        this._interface = value;
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
     *         &lt;element name="poll-interval" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="value" default="60">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *                       &lt;minInclusive value="10"/>
     *                       &lt;maxInclusive value="300"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *                 &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="type" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="value" type="{http://www.aerohive.com/configuration/others}amrp-metric-type-value" default="normal" />
     *                 &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act" />
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
        "ahdeltaassistant",
        "pollInterval",
        "type"
    })
    public static class Metric {

        @XmlElement(name = "AH-DELTA-ASSISTANT")
        protected AhOnlyAct ahdeltaassistant;
        @XmlElement(name = "poll-interval")
        protected AmrpObj.Metric.PollInterval pollInterval;
        protected AmrpObj.Metric.Type type;

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
         * Gets the value of the pollInterval property.
         * 
         * @return
         *     possible object is
         *     {@link AmrpObj.Metric.PollInterval }
         *     
         */
        public AmrpObj.Metric.PollInterval getPollInterval() {
            return pollInterval;
        }

        /**
         * Sets the value of the pollInterval property.
         * 
         * @param value
         *     allowed object is
         *     {@link AmrpObj.Metric.PollInterval }
         *     
         */
        public void setPollInterval(AmrpObj.Metric.PollInterval value) {
            this.pollInterval = value;
        }

        /**
         * Gets the value of the type property.
         * 
         * @return
         *     possible object is
         *     {@link AmrpObj.Metric.Type }
         *     
         */
        public AmrpObj.Metric.Type getType() {
            return type;
        }

        /**
         * Sets the value of the type property.
         * 
         * @param value
         *     allowed object is
         *     {@link AmrpObj.Metric.Type }
         *     
         */
        public void setType(AmrpObj.Metric.Type value) {
            this.type = value;
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
         *       &lt;attribute name="value" default="60">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
         *             &lt;minInclusive value="10"/>
         *             &lt;maxInclusive value="300"/>
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
        public static class PollInterval {

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
                    return  60;
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
         *       &lt;attribute name="value" type="{http://www.aerohive.com/configuration/others}amrp-metric-type-value" default="normal" />
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
        public static class Type {

            @XmlAttribute(name = "value")
            protected AmrpMetricTypeValue value;
            @XmlAttribute(name = "operation", required = true)
            protected AhEnumAct operation;

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link AmrpMetricTypeValue }
             *     
             */
            public AmrpMetricTypeValue getValue() {
                if (value == null) {
                    return AmrpMetricTypeValue.NORMAL;
                } else {
                    return value;
                }
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link AmrpMetricTypeValue }
             *     
             */
            public void setValue(AmrpMetricTypeValue value) {
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
     *         &lt;element name="metric" type="{http://www.aerohive.com/configuration/others}amrp-neighbor-metric" minOccurs="0"/>
     *       &lt;/sequence>
     *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act-value" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "metric"
    })
    public static class Neighbor {

        protected AmrpNeighborMetric metric;
        @XmlAttribute(name = "name", required = true)
        protected String name;
        @XmlAttribute(name = "operation", required = true)
        protected AhEnumActValue operation;

        /**
         * Gets the value of the metric property.
         * 
         * @return
         *     possible object is
         *     {@link AmrpNeighborMetric }
         *     
         */
        public AmrpNeighborMetric getMetric() {
            return metric;
        }

        /**
         * Sets the value of the metric property.
         * 
         * @param value
         *     allowed object is
         *     {@link AmrpNeighborMetric }
         *     
         */
        public void setMetric(AmrpNeighborMetric value) {
            this.metric = value;
        }

        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setName(String value) {
            this.name = value;
        }

        /**
         * Gets the value of the operation property.
         * 
         * @return
         *     possible object is
         *     {@link AhEnumActValue }
         *     
         */
        public AhEnumActValue getOperation() {
            return operation;
        }

        /**
         * Sets the value of the operation property.
         * 
         * @param value
         *     allowed object is
         *     {@link AhEnumActValue }
         *     
         */
        public void setOperation(AhEnumActValue value) {
            this.operation = value;
        }

    }

}