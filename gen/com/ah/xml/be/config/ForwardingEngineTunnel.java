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
 * <p>Java class for forwarding-engine-tunnel complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="forwarding-engine-tunnel">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tcp-mss-threshold" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *                   &lt;element name="enable" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *                   &lt;element name="threshold-size" type="{http://www.aerohive.com/configuration/others}tcp-mss-threshold-size" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="selective-multicast-forward" type="{http://www.aerohive.com/configuration/others}fe-tunnel-selective-multicast-forward" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "forwarding-engine-tunnel", propOrder = {
    "tcpMssThreshold",
    "selectiveMulticastForward"
})
public class ForwardingEngineTunnel {

    @XmlElement(name = "tcp-mss-threshold")
    protected ForwardingEngineTunnel.TcpMssThreshold tcpMssThreshold;
    @XmlElement(name = "selective-multicast-forward")
    protected FeTunnelSelectiveMulticastForward selectiveMulticastForward;

    /**
     * Gets the value of the tcpMssThreshold property.
     * 
     * @return
     *     possible object is
     *     {@link ForwardingEngineTunnel.TcpMssThreshold }
     *     
     */
    public ForwardingEngineTunnel.TcpMssThreshold getTcpMssThreshold() {
        return tcpMssThreshold;
    }

    /**
     * Sets the value of the tcpMssThreshold property.
     * 
     * @param value
     *     allowed object is
     *     {@link ForwardingEngineTunnel.TcpMssThreshold }
     *     
     */
    public void setTcpMssThreshold(ForwardingEngineTunnel.TcpMssThreshold value) {
        this.tcpMssThreshold = value;
    }

    /**
     * Gets the value of the selectiveMulticastForward property.
     * 
     * @return
     *     possible object is
     *     {@link FeTunnelSelectiveMulticastForward }
     *     
     */
    public FeTunnelSelectiveMulticastForward getSelectiveMulticastForward() {
        return selectiveMulticastForward;
    }

    /**
     * Sets the value of the selectiveMulticastForward property.
     * 
     * @param value
     *     allowed object is
     *     {@link FeTunnelSelectiveMulticastForward }
     *     
     */
    public void setSelectiveMulticastForward(FeTunnelSelectiveMulticastForward value) {
        this.selectiveMulticastForward = value;
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
     *         &lt;element name="threshold-size" type="{http://www.aerohive.com/configuration/others}tcp-mss-threshold-size" minOccurs="0"/>
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
        "enable",
        "thresholdSize"
    })
    public static class TcpMssThreshold {

        @XmlElement(name = "AH-DELTA-ASSISTANT")
        protected AhOnlyAct ahdeltaassistant;
        protected AhOnlyAct enable;
        @XmlElement(name = "threshold-size")
        protected TcpMssThresholdSize thresholdSize;

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

        /**
         * Gets the value of the thresholdSize property.
         * 
         * @return
         *     possible object is
         *     {@link TcpMssThresholdSize }
         *     
         */
        public TcpMssThresholdSize getThresholdSize() {
            return thresholdSize;
        }

        /**
         * Sets the value of the thresholdSize property.
         * 
         * @param value
         *     allowed object is
         *     {@link TcpMssThresholdSize }
         *     
         */
        public void setThresholdSize(TcpMssThresholdSize value) {
            this.thresholdSize = value;
        }

    }

}
