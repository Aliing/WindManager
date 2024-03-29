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
 * <p>Java class for policy-action-inter-station-traffic-drop complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="policy-action-inter-station-traffic-drop">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="log" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="cr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="initiate-session" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="terminate-session" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="packet-drop" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "policy-action-inter-station-traffic-drop", namespace = "http://www.aerohive.com/configuration/policy", propOrder = {
    "log"
})
public class PolicyActionInterStationTrafficDrop {

    protected PolicyActionInterStationTrafficDrop.Log log;

    /**
     * Gets the value of the log property.
     * 
     * @return
     *     possible object is
     *     {@link PolicyActionInterStationTrafficDrop.Log }
     *     
     */
    public PolicyActionInterStationTrafficDrop.Log getLog() {
        return log;
    }

    /**
     * Sets the value of the log property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyActionInterStationTrafficDrop.Log }
     *     
     */
    public void setLog(PolicyActionInterStationTrafficDrop.Log value) {
        this.log = value;
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
     *         &lt;element name="cr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="initiate-session" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="terminate-session" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="packet-drop" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
        "cr",
        "initiateSession",
        "terminateSession",
        "packetDrop"
    })
    public static class Log {

        protected String cr;
        @XmlElement(name = "initiate-session")
        protected String initiateSession;
        @XmlElement(name = "terminate-session")
        protected String terminateSession;
        @XmlElement(name = "packet-drop")
        protected String packetDrop;

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
         * Gets the value of the initiateSession property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getInitiateSession() {
            return initiateSession;
        }

        /**
         * Sets the value of the initiateSession property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setInitiateSession(String value) {
            this.initiateSession = value;
        }

        /**
         * Gets the value of the terminateSession property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTerminateSession() {
            return terminateSession;
        }

        /**
         * Sets the value of the terminateSession property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTerminateSession(String value) {
            this.terminateSession = value;
        }

        /**
         * Gets the value of the packetDrop property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPacketDrop() {
            return packetDrop;
        }

        /**
         * Sets the value of the packetDrop property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPacketDrop(String value) {
            this.packetDrop = value;
        }

    }

}
