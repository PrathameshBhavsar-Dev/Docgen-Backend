package com.example.Docgen_Backend.config;

import com.example.Docgen_Backend.entity.UserProfile;   // adjust to your package
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import com.example.Docgen_Backend.entity.*;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.Docgen_Backend.vendor.repository",
        entityManagerFactoryRef = "vendorEntityManagerFactory",
        transactionManagerRef = "vendorTransactionManager")
public class VendorDbConfig {

    @Bean
    @ConfigurationProperties("vendor.datasource")
    public HikariDataSource vendorDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setPoolName("vendor-pool");
        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean vendorEntityManagerFactory(
            @Qualifier("vendorDataSource") DataSource dataSource,
            @Value("${vendor.jpa.ddl-auto:none}") String ddlAuto,
            @Value("${spring.jpa.database-platform:org.hibernate.dialect.MySQLDialect}") String dialect,
            @Value("${spring.jpa.show-sql:false}") boolean showSql) {

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        // Only the entities vendor profiles need, so the vendor DB doesn't get users, counters, etc.
        emf.setManagedTypes(PersistenceManagedTypes.of(
                BaseDocument.class.getName(),
                UserProfile.class.getName(),
                OfferLetter.class.getName(),
                AppointmentLetter.class.getName(),
                ConfirmationLetter.class.getName(),
                IncrementLetter.class.getName(),
                InternshipLetter.class.getName(),
                CompletionLetter.class.getName(),
                ExperienceLetter.class.getName(),
                RelievingLetter.class.getName(),
                FullAndFinalLetter.class.getName(),
                SalarySlip.class.getName(),
                GenericDocument.class.getName()));
        // , add every entity UserProfile references (documents, etc.)
        emf.setPersistenceUnitName("vendor");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emf.setJpaPropertyMap(JpaProps.build(ddlAuto, dialect, showSql));
        return emf;
    }

    @Bean
    public PlatformTransactionManager vendorTransactionManager(
            @Qualifier("vendorEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}