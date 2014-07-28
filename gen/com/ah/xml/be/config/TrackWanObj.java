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
 * <p>Java class for track-wan-obj complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="track-wan-obj">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="default-gateway" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="ip" type="{http://www.aerohive.com/configuration/general}ah-name-act-value" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="multi-dst-logic" type="{http://www.aerohive.com/configuration/others}track-multi-dst-logic" minOccurs="0"/>
 *         &lt;element name="retry" type="{http://www.aerohive.com/configuration/general}ah-int-act" minOccurs="0"/>
 *         &lt;element name="interval" type="{http://www.aerohive.com/configuration/general}ah-int-act" minOccurs="0"/>
 *         &lt;element name="timeout" type="{http://www.aerohive.com/configuration/general}ah-int-act" minOccurs="0"/>
 *         &lt;element name="interface" type="{http://www.aerohive.com/configuration/general}ah-name-act-value" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="enable" type="{http://www.aerohive.com/configuration/others}track-wan-enable" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act-value" />
 *       &lt;attribute name="updateTime" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "track-wan-obj", propOrder = {
    "cr",
    "defaultGateway",
    "ip",
    "multiDstLogic",
    "retry",
    "interval",
    "timeout",
    "_interface",
    "enable"
})
public class TrackWanObj {

    protected String cr;
    @XmlElement(name = "default-gateway")
    protected AhOnlyAct defaultGateway;
    protected List<AhNameActValue> ip;
    @XmlElement(name = "multi-dst-logic")
    protected TrackMultiDstLogic multiDstLogic;
    protected AhIntAct retry;
    protected AhIntAct interval;
    protected AhIntAct timeout;
    @XmlElement(name = "interface")
    protected List<AhNameActValue> _interface;
    protected TrackWanEnable enable;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "operation", required = true)
    protected AhEnumActValue operation;
    @XmlAttribute(name = "updateTime")
    protected String updateTime;

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
     * Gets the value of the defaultGateway property.
     * 
     * @return
     *     possible object is
     *     {@link AhOnlyAct }
     *     
     */
    public AhOnlyAct getDefaultGateway() {
        return defaultGateway;
    }

    /**
     * Sets the value of the defaultGateway property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhOnlyAct }
     *     
     */
    public void setDefaultGateway(AhOnlyAct value) {
        this.defaultGateway = value;
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

    /**
     * Gets the value of the multiDstLogic property.
     * 
     * @return
     *     possible object is
     *     {@link TrackMultiDstLogic }
     *     
     */
    public TrackMultiDstLogic getMultiDstLogic() {
        return multiDstLogic;
    }

    /**
     * Sets the value of the multiDstLogic property.
     * 
     * @param value
     *     allowed object is
     *     {@link TrackMultiDstLogic }
     *     
     */
    public void setMultiDstLogic(TrackMultiDstLogic value) {
        this.multiDstLogic = value;
    }

    /**
     * Gets the value of the retry property.
     * 
     * @return
     *     possible object is
     *     {@link AhIntAct }
     *     
     */
    public AhIntAct getRetry() {
        return retry;
    }

    /**
     * Sets the value of the retry property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhIntAct }
     *     
     */
    public void setRetry(AhIntAct value) {
        this.retry = value;
    }

    /**
     * Gets the value of the interval property.
     * 
     * @return
     *     possible object is
     *     {@link AhIntAct }
     *     
     */
    public AhIntAct getInterval() {
        return interval;
    }

    /**
     * Sets the value of the interval property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhIntAct }
     *     
     */
    public void setInterval(AhIntAct value) {
        this.interval = value;
    }

    /**
     * Gets the value of the timeout property.
     * 
     * @return
     *     possible object is
     *     {@link AhIntAct }
     *     
     */
    public AhIntAct getTimeout() {
        return timeout;
    }

    /**
     * Sets the value of the timeout property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhIntAct }
     *     
     */
    public void setTimeout(AhIntAct value) {
        this.timeout = value;
    }

    /**
     * Gets the value of the interface property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the interface property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInterface().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AhNameActValue }
     * 
     * 
     */
    public List<AhNameActValue> getInterface() {
        if (_interface == null) {
            _interface = new ArrayList<AhNameActValue>();
        }
        return this._interface;
    }

    /**
     * Gets the value of the enable property.
     * 
     * @return
     *     possible object is
     *     {@link TrackWanEnable }
     *     
     */
    public TrackWanEnable getEnable() {
        return enable;
    }

    /**
     * Sets the value of the enable property.
     * 
     * @param value
     *     allowed object is
     *     {@link TrackWanEnable }
     *     
     */
    public void setEnable(TrackWanEnable value) {
        this.enable = value;
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

}