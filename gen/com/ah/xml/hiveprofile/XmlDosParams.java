//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.06.26 at 04:00:43 PM CST 
//


package com.ah.xml.hiveprofile;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for xmlDosParams complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="xmlDosParams">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="frameType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="alarmInterval" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="alarmThreshold" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="ban" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xmlDosParams")
public class XmlDosParams {

    @XmlAttribute(name = "frameType")
    protected String frameType;
    @XmlAttribute(name = "alarmInterval")
    protected Integer alarmInterval;
    @XmlAttribute(name = "alarmThreshold")
    protected Integer alarmThreshold;
    @XmlAttribute(name = "ban")
    protected Integer ban;

    /**
     * Gets the value of the frameType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFrameType() {
        return frameType;
    }

    /**
     * Sets the value of the frameType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFrameType(String value) {
        this.frameType = value;
    }

    /**
     * Gets the value of the alarmInterval property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAlarmInterval() {
        return alarmInterval;
    }

    /**
     * Sets the value of the alarmInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAlarmInterval(Integer value) {
        this.alarmInterval = value;
    }

    /**
     * Gets the value of the alarmThreshold property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAlarmThreshold() {
        return alarmThreshold;
    }

    /**
     * Sets the value of the alarmThreshold property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAlarmThreshold(Integer value) {
        this.alarmThreshold = value;
    }

    /**
     * Gets the value of the ban property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getBan() {
        return ban;
    }

    /**
     * Sets the value of the ban property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBan(Integer value) {
        this.ban = value;
    }

}