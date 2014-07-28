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
 * <p>Java class for report-statistic complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="report-statistic">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="enable" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="period" type="{http://www.aerohive.com/configuration/others}report-statistic-period" minOccurs="0"/>
 *         &lt;element name="alarm-threshold" type="{http://www.aerohive.com/configuration/others}report-statistic-alarm-threshold" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "report-statistic", propOrder = {
    "enable",
    "period",
    "alarmThreshold"
})
public class ReportStatistic {

    protected AhOnlyAct enable;
    protected ReportStatisticPeriod period;
    @XmlElement(name = "alarm-threshold")
    protected ReportStatisticAlarmThreshold alarmThreshold;

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
     * Gets the value of the period property.
     * 
     * @return
     *     possible object is
     *     {@link ReportStatisticPeriod }
     *     
     */
    public ReportStatisticPeriod getPeriod() {
        return period;
    }

    /**
     * Sets the value of the period property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportStatisticPeriod }
     *     
     */
    public void setPeriod(ReportStatisticPeriod value) {
        this.period = value;
    }

    /**
     * Gets the value of the alarmThreshold property.
     * 
     * @return
     *     possible object is
     *     {@link ReportStatisticAlarmThreshold }
     *     
     */
    public ReportStatisticAlarmThreshold getAlarmThreshold() {
        return alarmThreshold;
    }

    /**
     * Sets the value of the alarmThreshold property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportStatisticAlarmThreshold }
     *     
     */
    public void setAlarmThreshold(ReportStatisticAlarmThreshold value) {
        this.alarmThreshold = value;
    }

}