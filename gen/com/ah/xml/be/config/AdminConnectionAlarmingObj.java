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
 * <p>Java class for admin-connection-alarming-obj complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="admin-connection-alarming-obj">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="enable" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="tx_retry" type="{http://www.aerohive.com/configuration/admin}connection-alarming-tx_retry" minOccurs="0"/>
 *         &lt;element name="tx_frame_error" type="{http://www.aerohive.com/configuration/admin}connection-alarming-tx_frame_error" minOccurs="0"/>
 *         &lt;element name="prob_request" type="{http://www.aerohive.com/configuration/admin}connection-alarming-prob_request" minOccurs="0"/>
 *         &lt;element name="ingress_multicast" type="{http://www.aerohive.com/configuration/admin}connection-alarming-ingress_multicast" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "admin-connection-alarming-obj", namespace = "http://www.aerohive.com/configuration/admin", propOrder = {
    "enable",
    "txRetry",
    "txFrameError",
    "probRequest",
    "ingressMulticast"
})
public class AdminConnectionAlarmingObj {

    protected AhOnlyAct enable;
    @XmlElement(name = "tx_retry")
    protected ConnectionAlarmingTxRetry txRetry;
    @XmlElement(name = "tx_frame_error")
    protected ConnectionAlarmingTxFrameError txFrameError;
    @XmlElement(name = "prob_request")
    protected ConnectionAlarmingProbRequest probRequest;
    @XmlElement(name = "ingress_multicast")
    protected ConnectionAlarmingIngressMulticast ingressMulticast;

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
     * Gets the value of the txRetry property.
     * 
     * @return
     *     possible object is
     *     {@link ConnectionAlarmingTxRetry }
     *     
     */
    public ConnectionAlarmingTxRetry getTxRetry() {
        return txRetry;
    }

    /**
     * Sets the value of the txRetry property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConnectionAlarmingTxRetry }
     *     
     */
    public void setTxRetry(ConnectionAlarmingTxRetry value) {
        this.txRetry = value;
    }

    /**
     * Gets the value of the txFrameError property.
     * 
     * @return
     *     possible object is
     *     {@link ConnectionAlarmingTxFrameError }
     *     
     */
    public ConnectionAlarmingTxFrameError getTxFrameError() {
        return txFrameError;
    }

    /**
     * Sets the value of the txFrameError property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConnectionAlarmingTxFrameError }
     *     
     */
    public void setTxFrameError(ConnectionAlarmingTxFrameError value) {
        this.txFrameError = value;
    }

    /**
     * Gets the value of the probRequest property.
     * 
     * @return
     *     possible object is
     *     {@link ConnectionAlarmingProbRequest }
     *     
     */
    public ConnectionAlarmingProbRequest getProbRequest() {
        return probRequest;
    }

    /**
     * Sets the value of the probRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConnectionAlarmingProbRequest }
     *     
     */
    public void setProbRequest(ConnectionAlarmingProbRequest value) {
        this.probRequest = value;
    }

    /**
     * Gets the value of the ingressMulticast property.
     * 
     * @return
     *     possible object is
     *     {@link ConnectionAlarmingIngressMulticast }
     *     
     */
    public ConnectionAlarmingIngressMulticast getIngressMulticast() {
        return ingressMulticast;
    }

    /**
     * Sets the value of the ingressMulticast property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConnectionAlarmingIngressMulticast }
     *     
     */
    public void setIngressMulticast(ConnectionAlarmingIngressMulticast value) {
        this.ingressMulticast = value;
    }

}