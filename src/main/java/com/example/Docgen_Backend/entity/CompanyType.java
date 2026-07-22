package com.example.Docgen_Backend.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CompanyType {

    SMDS("SmartMatrix Digital Services Pvt. Ltd.", "SMDS"),
    DSS("Devcons Software Solutions Pvt. Ltd.", "DSS"),
    PSCS("Penta Software Consultancy Services (I) Pvt Ltd", "PSCS"),
    CTS("Cubeage Technologies Services Pvt. Ltd.", "CTS"),
    QMS("Quick Management Services", "QMS"),
    NCSS("Neweage Cloud Solution Pvt. Ltd.", "NCSS"),
    RPBS("RP Business Solutions LLP", "RPBS"),
    JDIT("JDIT Software Solutions Pvt. Ltd.", "JDIT"),
    NSS("NIMBJA SECURITY SOLUTIONS Pvt. Ltd.", "NSS"),
    SSS("Smart Software Services (I) Pvt. Ltd.", "SSS");

    private final String fullName;
    private final String empIdPrefix;

    CompanyType(String fullName, String empIdPrefix) {
        this.fullName = fullName;
        this.empIdPrefix = empIdPrefix;
    }

    @JsonValue
    public String getFullName() {
        return fullName;
    }

    public String getEmpIdPrefix() {
        return empIdPrefix;
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