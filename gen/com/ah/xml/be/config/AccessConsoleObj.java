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
 * <p>Java class for access-console-obj complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="access-console-obj">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="custom-ssid" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *         &lt;element name="mode" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{http://www.aerohive.com/configuration/others}ac-mode-value" default="auto" />
 *                 &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="security" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *                   &lt;element name="mac-filter" type="{http://www.aerohive.com/configuration/general}ah-name-act" minOccurs="0"/>
 *                   &lt;element name="protocol-suite" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;choice minOccurs="0">
 *                             &lt;element name="open" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="wpa-auto-psk" type="{http://www.aerohive.com/configuration/others}ac-protocol-suite-wpa"/>
 *                             &lt;element name="wpa-tkip-psk" type="{http://www.aerohive.com/configuration/others}ac-protocol-suite-wpa"/>
 *                             &lt;element name="wpa-aes-psk" type="{http://www.aerohive.com/configuration/others}ac-protocol-suite-wpa"/>
 *                             &lt;element name="wpa2-tkip-psk" type="{http://www.aerohive.com/configuration/others}ac-protocol-suite-wpa"/>
 *                             &lt;element name="wpa2-aes-psk" type="{http://www.aerohive.com/configuration/others}ac-protocol-suite-wpa"/>
 *                           &lt;/choice>
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
 *         &lt;element name="max-client" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" default="2">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                       &lt;minInclusive value="1"/>
 *                       &lt;maxInclusive value="64"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="hide-ssid" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="telnet" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
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
@XmlType(name = "access-console-obj", propOrder = {
    "customSsid",
    "mode",
    "security",
    "maxClient",
    "hideSsid",
    "telnet"
})
public class AccessConsoleObj {

    @XmlElement(name = "custom-ssid")
    protected AhStringAct customSsid;
    protected AccessConsoleObj.Mode mode;
    protected AccessConsoleObj.Security security;
    @XmlElement(name = "max-client")
    protected AccessConsoleObj.MaxClient maxClient;
    @XmlElement(name = "hide-ssid")
    protected AhOnlyAct hideSsid;
    protected AhOnlyAct telnet;
    @XmlAttribute(name = "updateTime")
    protected String updateTime;

    /**
     * Gets the value of the customSsid property.
     * 
     * @return
     *     possible object is
     *     {@link AhStringAct }
     *     
     */
    public AhStringAct getCustomSsid() {
        return customSsid;
    }

    /**
     * Sets the value of the customSsid property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhStringAct }
     *     
     */
    public void setCustomSsid(AhStringAct value) {
        this.customSsid = value;
    }

    /**
     * Gets the value of the mode property.
     * 
     * @return
     *     possible object is
     *     {@link AccessConsoleObj.Mode }
     *     
     */
    public AccessConsoleObj.Mode getMode() {
        return mode;
    }

    /**
     * Sets the value of the mode property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccessConsoleObj.Mode }
     *     
     */
    public void setMode(AccessConsoleObj.Mode value) {
        this.mode = value;
    }

    /**
     * Gets the value of the security property.
     * 
     * @return
     *     possible object is
     *     {@link AccessConsoleObj.Security }
     *     
     */
    public AccessConsoleObj.Security getSecurity() {
        return security;
    }

    /**
     * Sets the value of the security property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccessConsoleObj.Security }
     *     
     */
    public void setSecurity(AccessConsoleObj.Security value) {
        this.security = value;
    }

    /**
     * Gets the value of the maxClient property.
     * 
     * @return
     *     possible object is
     *     {@link AccessConsoleObj.MaxClient }
     *     
     */
    public AccessConsoleObj.MaxClient getMaxClient() {
        return maxClient;
    }

    /**
     * Sets the value of the maxClient property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccessConsoleObj.MaxClient }
     *     
     */
    public void setMaxClient(AccessConsoleObj.MaxClient value) {
        this.maxClient = value;
    }

    /**
     * Gets the value of the hideSsid property.
     * 
     * @return
     *     possible object is
     *     {@link AhOnlyAct }
     *     
     */
    public AhOnlyAct getHideSsid() {
        return hideSsid;
    }

    /**
     * Sets the value of the hideSsid property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhOnlyAct }
     *     
     */
    public void setHideSsid(AhOnlyAct value) {
        this.hideSsid = value;
    }

    /**
     * Gets the value of the telnet property.
     * 
     * @return
     *     possible object is
     *     {@link AhOnlyAct }
     *     
     */
    public AhOnlyAct getTelnet() {
        return telnet;
    }

    /**
     * Sets the value of the telnet property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhOnlyAct }
     *     
     */
    public void setTelnet(AhOnlyAct value) {
        this.telnet = value;
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
     *       &lt;attribute name="value" default="2">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *             &lt;minInclusive value="1"/>
     *             &lt;maxInclusive value="64"/>
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
    public static class MaxClient {

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
                return  2;
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
     *       &lt;attribute name="value" type="{http://www.aerohive.com/configuration/others}ac-mode-value" default="auto" />
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
    public static class Mode {

        @XmlAttribute(name = "value")
        protected AcModeValue value;
        @XmlAttribute(name = "operation", required = true)
        protected AhEnumAct operation;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link AcModeValue }
         *     
         */
        public AcModeValue getValue() {
            if (value == null) {
                return AcModeValue.AUTO;
            } else {
                return value;
            }
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link AcModeValue }
         *     
         */
        public void setValue(AcModeValue value) {
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
     *         &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
     *         &lt;element name="mac-filter" type="{http://www.aerohive.com/configuration/general}ah-name-act" minOccurs="0"/>
     *         &lt;element name="protocol-suite" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;choice minOccurs="0">
     *                   &lt;element name="open" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="wpa-auto-psk" type="{http://www.aerohive.com/configuration/others}ac-protocol-suite-wpa"/>
     *                   &lt;element name="wpa-tkip-psk" type="{http://www.aerohive.com/configuration/others}ac-protocol-suite-wpa"/>
     *                   &lt;element name="wpa-aes-psk" type="{http://www.aerohive.com/configuration/others}ac-protocol-suite-wpa"/>
     *                   &lt;element name="wpa2-tkip-psk" type="{http://www.aerohive.com/configuration/others}ac-protocol-suite-wpa"/>
     *                   &lt;element name="wpa2-aes-psk" type="{http://www.aerohive.com/configuration/others}ac-protocol-suite-wpa"/>
     *                 &lt;/choice>
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
        "macFilter",
        "protocolSuite"
    })
    public static class Security {

        @XmlElement(name = "AH-DELTA-ASSISTANT")
        protected AhOnlyAct ahdeltaassistant;
        @XmlElement(name = "mac-filter")
        protected AhNameAct macFilter;
        @XmlElement(name = "protocol-suite")
        protected AccessConsoleObj.Security.ProtocolSuite protocolSuite;

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
         * Gets the value of the macFilter property.
         * 
         * @return
         *     possible object is
         *     {@link AhNameAct }
         *     
         */
        public AhNameAct getMacFilter() {
            return macFilter;
        }

        /**
         * Sets the value of the macFilter property.
         * 
         * @param value
         *     allowed object is
         *     {@link AhNameAct }
         *     
         */
        public void setMacFilter(AhNameAct value) {
            this.macFilter = value;
        }

        /**
         * Gets the value of the protocolSuite property.
         * 
         * @return
         *     possible object is
         *     {@link AccessConsoleObj.Security.ProtocolSuite }
         *     
         */
        public AccessConsoleObj.Security.ProtocolSuite getProtocolSuite() {
            return protocolSuite;
        }

        /**
         * Sets the value of the protocolSuite property.
         * 
         * @param value
         *     allowed object is
         *     {@link AccessConsoleObj.Security.ProtocolSuite }
         *     
         */
        public void setProtocolSuite(AccessConsoleObj.Security.ProtocolSuite value) {
            this.protocolSuite = value;
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
         *       &lt;choice minOccurs="0">
         *         &lt;element name="open" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="wpa-auto-psk" type="{http://www.aerohive.com/configuration/others}ac-protocol-suite-wpa"/>
         *         &lt;element name="wpa-tkip-psk" type="{http://www.aerohive.com/configuration/others}ac-protocol-suite-wpa"/>
         *         &lt;element name="wpa-aes-psk" type="{http://www.aerohive.com/configuration/others}ac-protocol-suite-wpa"/>
         *         &lt;element name="wpa2-tkip-psk" type="{http://www.aerohive.com/configuration/others}ac-protocol-suite-wpa"/>
         *         &lt;element name="wpa2-aes-psk" type="{http://www.aerohive.com/configuration/others}ac-protocol-suite-wpa"/>
         *       &lt;/choice>
         *       &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "open",
            "wpaAutoPsk",
            "wpaTkipPsk",
            "wpaAesPsk",
            "wpa2TkipPsk",
            "wpa2AesPsk"
        })
        public static class ProtocolSuite {

            protected String open;
            @XmlElement(name = "wpa-auto-psk")
            protected AcProtocolSuiteWpa wpaAutoPsk;
            @XmlElement(name = "wpa-tkip-psk")
            protected AcProtocolSuiteWpa wpaTkipPsk;
            @XmlElement(name = "wpa-aes-psk")
            protected AcProtocolSuiteWpa wpaAesPsk;
            @XmlElement(name = "wpa2-tkip-psk")
            protected AcProtocolSuiteWpa wpa2TkipPsk;
            @XmlElement(name = "wpa2-aes-psk")
            protected AcProtocolSuiteWpa wpa2AesPsk;
            @XmlAttribute(name = "operation", required = true)
            protected AhEnumAct operation;

            /**
             * Gets the value of the open property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getOpen() {
                return open;
            }

            /**
             * Sets the value of the open property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setOpen(String value) {
                this.open = value;
            }

            /**
             * Gets the value of the wpaAutoPsk property.
             * 
             * @return
             *     possible object is
             *     {@link AcProtocolSuiteWpa }
             *     
             */
            public AcProtocolSuiteWpa getWpaAutoPsk() {
                return wpaAutoPsk;
            }

            /**
             * Sets the value of the wpaAutoPsk property.
             * 
             * @param value
             *     allowed object is
             *     {@link AcProtocolSuiteWpa }
             *     
             */
            public void setWpaAutoPsk(AcProtocolSuiteWpa value) {
                this.wpaAutoPsk = value;
            }

            /**
             * Gets the value of the wpaTkipPsk property.
             * 
             * @return
             *     possible object is
             *     {@link AcProtocolSuiteWpa }
             *     
             */
            public AcProtocolSuiteWpa getWpaTkipPsk() {
                return wpaTkipPsk;
            }

            /**
             * Sets the value of the wpaTkipPsk property.
             * 
             * @param value
             *     allowed object is
             *     {@link AcProtocolSuiteWpa }
             *     
             */
            public void setWpaTkipPsk(AcProtocolSuiteWpa value) {
                this.wpaTkipPsk = value;
            }

            /**
             * Gets the value of the wpaAesPsk property.
             * 
             * @return
             *     possible object is
             *     {@link AcProtocolSuiteWpa }
             *     
             */
            public AcProtocolSuiteWpa getWpaAesPsk() {
                return wpaAesPsk;
            }

            /**
             * Sets the value of the wpaAesPsk property.
             * 
             * @param value
             *     allowed object is
             *     {@link AcProtocolSuiteWpa }
             *     
             */
            public void setWpaAesPsk(AcProtocolSuiteWpa value) {
                this.wpaAesPsk = value;
            }

            /**
             * Gets the value of the wpa2TkipPsk property.
             * 
             * @return
             *     possible object is
             *     {@link AcProtocolSuiteWpa }
             *     
             */
            public AcProtocolSuiteWpa getWpa2TkipPsk() {
                return wpa2TkipPsk;
            }

            /**
             * Sets the value of the wpa2TkipPsk property.
             * 
             * @param value
             *     allowed object is
             *     {@link AcProtocolSuiteWpa }
             *     
             */
            public void setWpa2TkipPsk(AcProtocolSuiteWpa value) {
                this.wpa2TkipPsk = value;
            }

            /**
             * Gets the value of the wpa2AesPsk property.
             * 
             * @return
             *     possible object is
             *     {@link AcProtocolSuiteWpa }
             *     
             */
            public AcProtocolSuiteWpa getWpa2AesPsk() {
                return wpa2AesPsk;
            }

            /**
             * Sets the value of the wpa2AesPsk property.
             * 
             * @param value
             *     allowed object is
             *     {@link AcProtocolSuiteWpa }
             *     
             */
            public void setWpa2AesPsk(AcProtocolSuiteWpa value) {
                this.wpa2AesPsk = value;
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
