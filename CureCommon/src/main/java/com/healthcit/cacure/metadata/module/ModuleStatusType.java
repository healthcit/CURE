//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.05.06 at 03:59:27 PM EDT 
//


package com.healthcit.cacure.metadata.module;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;


/**
 * <p>Java class for ModuleStatusType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ModuleStatusType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="new"/>
 *     &lt;enumeration value="in-progress"/>
 *     &lt;enumeration value="submitted"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlEnum
public enum ModuleStatusType {

    @XmlEnumValue("new")
    NEW("new"),
    @XmlEnumValue("in-progress")
    IN_PROGRESS("in-progress"),
    @XmlEnumValue("submitted")
    SUBMITTED("submitted");
    private final String value;

    ModuleStatusType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ModuleStatusType fromValue(String v) {
        for (ModuleStatusType c: ModuleStatusType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}