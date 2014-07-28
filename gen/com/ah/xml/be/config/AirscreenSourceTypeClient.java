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
 * <p>Java class for airscreen-source-type-client complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="airscreen-source-type-client">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="cr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="oui" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
 *         &lt;element name="rssi" type="{http://www.aerohive.com/configuration/general}ah-string-act-quote-prohibited" minOccurs="0"/>
 *         &lt;element name="auth-mode" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element name="open" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="wep" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="wep-open" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="wep-shared" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="dynamic-wep" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="wpa" type="{http://www.aerohive.com/configuration/others}airscreen-auth-mode-wpa" minOccurs="0"/>
 *                   &lt;element name="wpa-psk" type="{http://www.aerohive.com/configuration/others}airscreen-auth-mode-wpa" minOccurs="0"/>
 *                   &lt;element name="wpa-8021x" type="{http://www.aerohive.com/configuration/others}airscreen-auth-mode-wpa" minOccurs="0"/>
 *                   &lt;element name="wpa2-psk" type="{http://www.aerohive.com/configuration/others}airscreen-auth-mode-wpa" minOccurs="0"/>
 *                   &lt;element name="wpa2-8021x" type="{http://www.aerohive.com/configuration/others}airscreen-auth-mode-wpa" minOccurs="0"/>
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
@XmlType(name = "airscreen-source-type-client", propOrder = {
    "ahdeltaassistant",
    "cr",
    "oui",
    "rssi",
    "authMode"
})
public class AirscreenSourceTypeClient {

    @XmlElement(name = "AH-DELTA-ASSISTANT")
    protected AhOnlyAct ahdeltaassistant;
    protected String cr;
    protected AhStringAct oui;
    protected AhStringActQuoteProhibited rssi;
    @XmlElement(name = "auth-mode")
    protected AirscreenSourceTypeClient.AuthMode authMode;

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
     * Gets the value of the cr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCr() {
        return cr;
    }

    /**
     * Sets the value of the cr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCr(String value) {
        this.cr = value;
    }

    /**
     * Gets the value of the oui property.
     * 
     * @return
     *     possible object is
     *     {@link AhStringAct }
     *     
     */
    public AhStringAct getOui() {
        return oui;
    }

    /**
     * Sets the value of the oui property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhStringAct }
     *     
     */
    public void setOui(AhStringAct value) {
        this.oui = value;
    }

    /**
     * Gets the value of the rssi property.
     * 
     * @return
     *     possible object is
     *     {@link AhStringActQuoteProhibited }
     *     
     */
    public AhStringActQuoteProhibited getRssi() {
        return rssi;
    }

    /**
     * Sets the value of the rssi property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhStringActQuoteProhibited }
     *     
     */
    public void setRssi(AhStringActQuoteProhibited value) {
        this.rssi = value;
    }

    /**
     * Gets the value of the authMode property.
     * 
     * @return
     *     possible object is
     *     {@link AirscreenSourceTypeClient.AuthMode }
     *     
     */
    public AirscreenSourceTypeClient.AuthMode getAuthMode() {
        return authMode;
    }

    /**
     * Sets the value of the authMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link AirscreenSourceTypeClient.AuthMode }
     *     
     */
    public void setAuthMode(AirscreenSourceTypeClient.AuthMode value) {
        this.authMode = value;
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
     *       &lt;choice>
     *         &lt;element name="open" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="wep" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="wep-open" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="wep-shared" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="dynamic-wep" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="wpa" type="{http://www.aerohive.com/configuration/others}airscreen-auth-mode-wpa" minOccurs="0"/>
     *         &lt;element name="wpa-psk" type="{http://www.aerohive.com/configuration/others}airscreen-auth-mode-wpa" minOccurs="0"/>
     *         &lt;element name="wpa-8021x" type="{http://www.aerohive.com/configuration/others}airscreen-auth-mode-wpa" minOccurs="0"/>
     *         &lt;element name="wpa2-psk" type="{http://www.aerohive.com/configuration/others}airscreen-auth-mode-wpa" minOccurs="0"/>
     *         &lt;element name="wpa2-8021x" type="{http://www.aerohive.com/configuration/others}airscreen-auth-mode-wpa" minOccurs="0"/>
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
        "wep",
        "wepOpen",
        "wepShared",
        "dynamicWep",
        "wpa",
        "wpaPsk",
        "wpa8021X",
        "wpa2Psk",
        "wpa28021X"
    })
    public static class AuthMode {

        protected String open;
        protected String wep;
        @XmlElement(name = "wep-open")
        protected String wepOpen;
        @XmlElement(name = "wep-shared")
        protected String wepShared;
        @XmlElement(name = "dynamic-wep")
        protected String dynamicWep;
        protected AirscreenAuthModeWpa wpa;
        @XmlElement(name = "wpa-psk")
        protected AirscreenAuthModeWpa wpaPsk;
        @XmlElement(name = "wpa-8021x")
        protected AirscreenAuthModeWpa wpa8021X;
        @XmlElement(name = "wpa2-psk")
        protected AirscreenAuthModeWpa wpa2Psk;
        @XmlElement(name = "wpa2-8021x")
        protected AirscreenAuthModeWpa wpa28021X;
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
         * Gets the value of the wep property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getWep() {
            return wep;
        }

        /**
         * Sets the value of the wep property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setWep(String value) {
            this.wep = value;
        }

        /**
         * Gets the value of the wepOpen property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getWepOpen() {
            return wepOpen;
        }

        /**
         * Sets the value of the wepOpen property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setWepOpen(String value) {
            this.wepOpen = value;
        }

        /**
         * Gets the value of the wepShared property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getWepShared() {
            return wepShared;
        }

        /**
         * Sets the value of the wepShared property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setWepShared(String value) {
            this.wepShared = value;
        }

        /**
         * Gets the value of the dynamicWep property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDynamicWep() {
            return dynamicWep;
        }

        /**
         * Sets the value of the dynamicWep property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDynamicWep(String value) {
            this.dynamicWep = value;
        }

        /**
         * Gets the value of the wpa property.
         * 
         * @return
         *     possible object is
         *     {@link AirscreenAuthModeWpa }
         *     
         */
        public AirscreenAuthModeWpa getWpa() {
            return wpa;
        }

        /**
         * Sets the value of the wpa property.
         * 
         * @param value
         *     allowed object is
         *     {@link AirscreenAuthModeWpa }
         *     
         */
        public void setWpa(AirscreenAuthModeWpa value) {
            this.wpa = value;
        }

        /**
         * Gets the value of the wpaPsk property.
         * 
         * @return
         *     possible object is
         *     {@link AirscreenAuthModeWpa }
         *     
         */
        public AirscreenAuthModeWpa getWpaPsk() {
            return wpaPsk;
        }

        /**
         * Sets the value of the wpaPsk property.
         * 
         * @param value
         *     allowed object is
         *     {@link AirscreenAuthModeWpa }
         *     
         */
        public void setWpaPsk(AirscreenAuthModeWpa value) {
            this.wpaPsk = value;
        }

        /**
         * Gets the value of the wpa8021X property.
         * 
         * @return
         *     possible object is
         *     {@link AirscreenAuthModeWpa }
         *     
         */
        public AirscreenAuthModeWpa getWpa8021X() {
            return wpa8021X;
        }

        /**
         * Sets the value of the wpa8021X property.
         * 
         * @param value
         *     allowed object is
         *     {@link AirscreenAuthModeWpa }
         *     
         */
        public void setWpa8021X(AirscreenAuthModeWpa value) {
            this.wpa8021X = value;
        }

        /**
         * Gets the value of the wpa2Psk property.
         * 
         * @return
         *     possible object is
         *     {@link AirscreenAuthModeWpa }
         *     
         */
        public AirscreenAuthModeWpa getWpa2Psk() {
            return wpa2Psk;
        }

        /**
         * Sets the value of the wpa2Psk property.
         * 
         * @param value
         *     allowed object is
         *     {@link AirscreenAuthModeWpa }
         *     
         */
        public void setWpa2Psk(AirscreenAuthModeWpa value) {
            this.wpa2Psk = value;
        }

        /**
         * Gets the value of the wpa28021X property.
         * 
         * @return
         *     possible object is
         *     {@link AirscreenAuthModeWpa }
         *     
         */
        public AirscreenAuthModeWpa getWpa28021X() {
            return wpa28021X;
        }

        /**
         * Sets the value of the wpa28021X property.
         * 
         * @param value
         *     allowed object is
         *     {@link AirscreenAuthModeWpa }
         *     
         */
        public void setWpa28021X(AirscreenAuthModeWpa value) {
            this.wpa28021X = value;
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