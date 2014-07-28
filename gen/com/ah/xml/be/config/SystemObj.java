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
 * <p>Java class for system-obj complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="system-obj">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="temperature" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *                   &lt;element name="high-threshold" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="value" default="55">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                                 &lt;minInclusive value="45"/>
 *                                 &lt;maxInclusive value="64"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
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
 *         &lt;element name="smart-poe" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *                   &lt;element name="enable" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="led" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *                   &lt;element name="brightness" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;choice minOccurs="0">
 *                             &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *                             &lt;element name="soft" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="dim" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="off" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
 *         &lt;element name="icmp-redirect" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *                   &lt;element name="enable" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="web-server" type="{http://www.aerohive.com/configuration/others}system-web-server" minOccurs="0"/>
 *         &lt;element name="fans" type="{http://www.aerohive.com/configuration/others}system-fans" minOccurs="0"/>
 *         &lt;element name="power-mode" type="{http://www.aerohive.com/configuration/others}system-power-mode" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "system-obj", propOrder = {
    "temperature",
    "smartPoe",
    "led",
    "icmpRedirect",
    "webServer",
    "fans",
    "powerMode"
})
public class SystemObj {

    protected SystemObj.Temperature temperature;
    @XmlElement(name = "smart-poe")
    protected SystemObj.SmartPoe smartPoe;
    protected SystemObj.Led led;
    @XmlElement(name = "icmp-redirect")
    protected SystemObj.IcmpRedirect icmpRedirect;
    @XmlElement(name = "web-server")
    protected SystemWebServer webServer;
    protected SystemFans fans;
    @XmlElement(name = "power-mode")
    protected SystemPowerMode powerMode;

    /**
     * Gets the value of the temperature property.
     * 
     * @return
     *     possible object is
     *     {@link SystemObj.Temperature }
     *     
     */
    public SystemObj.Temperature getTemperature() {
        return temperature;
    }

    /**
     * Sets the value of the temperature property.
     * 
     * @param value
     *     allowed object is
     *     {@link SystemObj.Temperature }
     *     
     */
    public void setTemperature(SystemObj.Temperature value) {
        this.temperature = value;
    }

    /**
     * Gets the value of the smartPoe property.
     * 
     * @return
     *     possible object is
     *     {@link SystemObj.SmartPoe }
     *     
     */
    public SystemObj.SmartPoe getSmartPoe() {
        return smartPoe;
    }

    /**
     * Sets the value of the smartPoe property.
     * 
     * @param value
     *     allowed object is
     *     {@link SystemObj.SmartPoe }
     *     
     */
    public void setSmartPoe(SystemObj.SmartPoe value) {
        this.smartPoe = value;
    }

    /**
     * Gets the value of the led property.
     * 
     * @return
     *     possible object is
     *     {@link SystemObj.Led }
     *     
     */
    public SystemObj.Led getLed() {
        return led;
    }

    /**
     * Sets the value of the led property.
     * 
     * @param value
     *     allowed object is
     *     {@link SystemObj.Led }
     *     
     */
    public void setLed(SystemObj.Led value) {
        this.led = value;
    }

    /**
     * Gets the value of the icmpRedirect property.
     * 
     * @return
     *     possible object is
     *     {@link SystemObj.IcmpRedirect }
     *     
     */
    public SystemObj.IcmpRedirect getIcmpRedirect() {
        return icmpRedirect;
    }

    /**
     * Sets the value of the icmpRedirect property.
     * 
     * @param value
     *     allowed object is
     *     {@link SystemObj.IcmpRedirect }
     *     
     */
    public void setIcmpRedirect(SystemObj.IcmpRedirect value) {
        this.icmpRedirect = value;
    }

    /**
     * Gets the value of the webServer property.
     * 
     * @return
     *     possible object is
     *     {@link SystemWebServer }
     *     
     */
    public SystemWebServer getWebServer() {
        return webServer;
    }

    /**
     * Sets the value of the webServer property.
     * 
     * @param value
     *     allowed object is
     *     {@link SystemWebServer }
     *     
     */
    public void setWebServer(SystemWebServer value) {
        this.webServer = value;
    }

    /**
     * Gets the value of the fans property.
     * 
     * @return
     *     possible object is
     *     {@link SystemFans }
     *     
     */
    public SystemFans getFans() {
        return fans;
    }

    /**
     * Sets the value of the fans property.
     * 
     * @param value
     *     allowed object is
     *     {@link SystemFans }
     *     
     */
    public void setFans(SystemFans value) {
        this.fans = value;
    }

    /**
     * Gets the value of the powerMode property.
     * 
     * @return
     *     possible object is
     *     {@link SystemPowerMode }
     *     
     */
    public SystemPowerMode getPowerMode() {
        return powerMode;
    }

    /**
     * Sets the value of the powerMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link SystemPowerMode }
     *     
     */
    public void setPowerMode(SystemPowerMode value) {
        this.powerMode = value;
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
     *         &lt;element name="enable" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
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
        "enable"
    })
    public static class IcmpRedirect {

        @XmlElement(name = "AH-DELTA-ASSISTANT")
        protected AhOnlyAct ahdeltaassistant;
        protected AhOnlyAct enable;

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
         * Gets the value of the enable property.
         * 
         * @return
         *     possible object is
         *     {@link AhOnlyAct }
         *     
         */
        public AhOnlyAct getEnable() {
            return enable;
        }

        /**
         * Sets the value of the enable property.
         * 
         * @param value
         *     allowed object is
         *     {@link AhOnlyAct }
         *     
         */
        public void setEnable(AhOnlyAct value) {
            this.enable = value;
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
     *         &lt;element name="brightness" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;choice minOccurs="0">
     *                   &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
     *                   &lt;element name="soft" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="dim" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="off" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "brightness"
    })
    public static class Led {

        @XmlElement(name = "AH-DELTA-ASSISTANT")
        protected AhOnlyAct ahdeltaassistant;
        protected SystemObj.Led.Brightness brightness;

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
         * Gets the value of the brightness property.
         * 
         * @return
         *     possible object is
         *     {@link SystemObj.Led.Brightness }
         *     
         */
        public SystemObj.Led.Brightness getBrightness() {
            return brightness;
        }

        /**
         * Sets the value of the brightness property.
         * 
         * @param value
         *     allowed object is
         *     {@link SystemObj.Led.Brightness }
         *     
         */
        public void setBrightness(SystemObj.Led.Brightness value) {
            this.brightness = value;
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
         *         &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
         *         &lt;element name="soft" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="dim" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="off" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
            "ahdeltaassistant",
            "soft",
            "dim",
            "off"
        })
        public static class Brightness {

            @XmlElement(name = "AH-DELTA-ASSISTANT")
            protected AhOnlyAct ahdeltaassistant;
            protected String soft;
            protected String dim;
            protected String off;
            @XmlAttribute(name = "operation", required = true)
            protected AhEnumAct operation;

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
             * Gets the value of the soft property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getSoft() {
                return soft;
            }

            /**
             * Sets the value of the soft property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setSoft(String value) {
                this.soft = value;
            }

            /**
             * Gets the value of the dim property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getDim() {
                return dim;
            }

            /**
             * Sets the value of the dim property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDim(String value) {
                this.dim = value;
            }

            /**
             * Gets the value of the off property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getOff() {
                return off;
            }

            /**
             * Sets the value of the off property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setOff(String value) {
                this.off = value;
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
     *         &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
     *         &lt;element name="enable" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
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
        "enable"
    })
    public static class SmartPoe {

        @XmlElement(name = "AH-DELTA-ASSISTANT")
        protected AhOnlyAct ahdeltaassistant;
        protected AhOnlyAct enable;

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
         * Gets the value of the enable property.
         * 
         * @return
         *     possible object is
         *     {@link AhOnlyAct }
         *     
         */
        public AhOnlyAct getEnable() {
            return enable;
        }

        /**
         * Sets the value of the enable property.
         * 
         * @param value
         *     allowed object is
         *     {@link AhOnlyAct }
         *     
         */
        public void setEnable(AhOnlyAct value) {
            this.enable = value;
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
     *         &lt;element name="high-threshold" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="value" default="55">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *                       &lt;minInclusive value="45"/>
     *                       &lt;maxInclusive value="64"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
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
        "highThreshold"
    })
    public static class Temperature {

        @XmlElement(name = "AH-DELTA-ASSISTANT")
        protected AhOnlyAct ahdeltaassistant;
        @XmlElement(name = "high-threshold")
        protected SystemObj.Temperature.HighThreshold highThreshold;

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
         * Gets the value of the highThreshold property.
         * 
         * @return
         *     possible object is
         *     {@link SystemObj.Temperature.HighThreshold }
         *     
         */
        public SystemObj.Temperature.HighThreshold getHighThreshold() {
            return highThreshold;
        }

        /**
         * Sets the value of the highThreshold property.
         * 
         * @param value
         *     allowed object is
         *     {@link SystemObj.Temperature.HighThreshold }
         *     
         */
        public void setHighThreshold(SystemObj.Temperature.HighThreshold value) {
            this.highThreshold = value;
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
         *       &lt;attribute name="value" default="55">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
         *             &lt;minInclusive value="45"/>
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
        public static class HighThreshold {

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
                    return  55;
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

}