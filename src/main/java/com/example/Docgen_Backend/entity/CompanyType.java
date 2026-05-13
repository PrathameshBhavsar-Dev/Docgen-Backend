package com.example.Docgen_Backend.entity;

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

    public String getFullName() {
        return fullName;
    }

    // 🔥 ADD THIS METHOD
    public static CompanyType fromFullName(String fullName) {
        for (CompanyType type : CompanyType.values()) {
            if (type.fullName.equalsIgnoreCase(fullName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid company: " + fullName);
    }
}