package com.example.Docgen_Backend.config;

import java.util.HashMap;
import java.util.Map;

final class JpaProps {
    private JpaProps() {}

    static Map<String, Object> build(String ddlAuto, String dialect, boolean showSql) {
        Map<String, Object> p = new HashMap<>();
        p.put("hibernate.hbm2ddl.auto", ddlAuto);
        p.put("hibernate.dialect", dialect);
        p.put("hibernate.show_sql", showSql);
        // Keeps the same column names Spring Boot used before (createdByUserId -> created_by_user_id)
        p.put("hibernate.physical_naming_strategy",
              "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
        return p;
    }
}