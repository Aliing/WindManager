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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for schedule-obj complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="schedule-obj">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="once" type="{http://www.aerohive.com/configuration/schedule}schedule-once" minOccurs="0"/>
 *         &lt;element name="recurrent" type="{http://www.aerohive.com/configuration/schedule}schedule-recurrent" minOccurs="0"/>
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
@XmlType(name = "schedule-obj", namespace = "http://www.aerohive.com/configuration/schedule", propOrder = {
    "once",
    "recurrent"
})
public class ScheduleObj {

    protected ScheduleOnce once;
    protected ScheduleRecurrent recurrent;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "operation", required = true)
    protected AhEnumActValue operation;
    @XmlAttribute(name = "updateTime")
    protected String updateTime;

    /**
     * Gets the value of the once property.
     * 
     * @return
     *     possible object is
     *     {@link ScheduleOnce }
     *     
     */
    public ScheduleOnce getOnce() {
        return once;
    }

    /**
     * Sets the value of the once property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScheduleOnce }
     *     
     */
    public void setOnce(ScheduleOnce value) {
        this.once = value;
    }

    /**
     * Gets the value of the recurrent property.
     * 
     * @return
     *     possible object is
     *     {@link ScheduleRecurrent }
     *     
     */
    public ScheduleRecurrent getRecurrent() {
        return recurrent;
    }

    /**
     * Sets the value of the recurrent property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScheduleRecurrent }
     *     
     */
    public void setRecurrent(ScheduleRecurrent value) {
        this.recurrent = value;
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
