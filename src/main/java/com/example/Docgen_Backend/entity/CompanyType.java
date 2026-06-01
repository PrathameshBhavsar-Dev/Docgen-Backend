package com.example.Docgen_Backend.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CompanyType {

    SMT("SmartMatrix Digital Services Pvt. Ltd."),
    DCS("Devcons Software Solutions Pvt. Ltd."),
    PSS("Penta Software Consultancy Services (I) Pvt Ltd"),
    CTS("Cubeage Technologies Services Pvt. Ltd."),
    QMS("Quick Management Services"),
    NCS("Neweage Cloud Solution Pvt. Ltd."),
    RBS("RP Business Solutions LLP"),
    JDT("JDIT Software Solutions Pvt. Ltd."),
    NSS("NIMBJA SECURITY SOLUTIONS Pvt. Ltd."),
    SSS("Smart Software Services (I) Pvt. Ltd.");

    private final String fullName;

    CompanyType(String fullName) {
        this.fullName = fullName;
    }

    @JsonValue
    public String getFullName() {
        return fullName;
    }

    @JsonCreator
    public static CompanyType fromFullName(String value) {

        for (CompanyType type : CompanyType.values()) {

            // match enum name
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }

            // match full company name
            if (type.fullName.equalsIgnoreCase(value)) {
                return type;
            }
        }

        throw new IllegalArgumentException(
                "Invalid company: " + value
        );
    }
}