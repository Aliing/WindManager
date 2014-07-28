//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.06.26 at 04:00:43 PM CST 
//


package com.ah.xml.hiveprofile;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for xmlHiveProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="xmlHiveProfile">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="hiveWlanDosParams" type="{}xmlDosParams" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="stationWlanDosParams" type="{}xmlDosParams" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="hiveName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="nativeVlan" type="{http://www.w3.org/2001/XMLSchema}short" />
 *       &lt;attribute name="fragThreshold" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="rtsThreshold" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xmlHiveProfile", propOrder = {
    "description",
    "hiveWlanDosParams",
    "stationWlanDosParams"
})
public class XmlHiveProfile {

    @XmlElement(required = true)
    protected String description;
    protected List<XmlDosParams> hiveWlanDosParams;
    protected List<XmlDosParams> stationWlanDosParams;
    @XmlAttribute(name = "hiveName")
    protected String hiveName;
    @XmlAttribute(name = "nativeVlan")
    protected Short nativeVlan;
    @XmlAttribute(name = "fragThreshold")
    protected Integer fragThreshold;
    @XmlAttribute(name = "rtsThreshold")
    protected Integer rtsThreshold;

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the hiveWlanDosParams property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the hiveWlanDosParams property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHiveWlanDosParams().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XmlDosParams }
     * 
     * 
     */
    public List<XmlDosParams> getHiveWlanDosParams() {
        if (hiveWlanDosParams == null) {
            hiveWlanDosParams = new ArrayList<XmlDosParams>();
        }
        return this.hiveWlanDosParams;
    }

    /**
     * Gets the value of the stationWlanDosParams property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the stationWlanDosParams property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStationWlanDosParams().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XmlDosParams }
     * 
     * 
     */
    public List<XmlDosParams> getStationWlanDosParams() {
        if (stationWlanDosParams == null) {
            stationWlanDosParams = new ArrayList<XmlDosParams>();
        }
        return this.stationWlanDosParams;
    }

    /**
     * Gets the value of the hiveName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHiveName() {
        return hiveName;
    }

    /**
     * Sets the value of the hiveName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHiveName(String value) {
        this.hiveName = value;
    }

    /**
     * Gets the value of the nativeVlan property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getNativeVlan() {
        return nativeVlan;
    }

    /**
     * Sets the value of the nativeVlan property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setNativeVlan(Short value) {
        this.nativeVlan = value;
    }

    /**
     * Gets the value of the fragThreshold property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFragThreshold() {
        return fragThreshold;
    }

    /**
     * Sets the value of the fragThreshold property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFragThreshold(Integer value) {
        this.fragThreshold = value;
    }

    /**
     * Gets the value of the rtsThreshold property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRtsThreshold() {
        return rtsThreshold;
    }

    /**
     * Sets the value of the rtsThreshold property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRtsThreshold(Integer value) {
        this.rtsThreshold = value;
    }

}
