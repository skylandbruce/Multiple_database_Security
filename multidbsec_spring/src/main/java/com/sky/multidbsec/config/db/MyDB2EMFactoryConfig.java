package com.sky.multidbsec.config.db;


import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.sky.multidbsec.persistence.db2.DB2;

import jakarta.persistence.EntityManagerFactory;

@Configuration(proxyBeanMethods = false)
@EnableAutoConfiguration
@EntityScan(/* defined by application.properties! omitted - basePackageClasses = DB2.class */)
@EnableTransactionManagement
// Repository package root
@EnableJpaRepositories(basePackageClasses = DB2.class, entityManagerFactoryRef = "secondEntityManagerFactory",transactionManagerRef = "secondTransactionManager")
public class MyDB2EMFactoryConfig {

    @Bean(name = "secondJpaProperties") 
    @ConfigurationProperties("db2.jpa")
    public JpaProperties secondJpaProperties() {
        return new JpaProperties();
    }
    @Bean(name = "secondEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean secondEntityManagerFactory(@Qualifier("secondDataSource") DataSource secondDataSource,
            @Qualifier("secondJpaProperties") JpaProperties secondJpaProperties) {
        EntityManagerFactoryBuilder builder = createEntityManagerFactoryBuilder(secondJpaProperties);
        // Entity package root
        return builder.dataSource(secondDataSource).packages(DB2.class).persistenceUnit("DB2").build();
    }

    private EntityManagerFactoryBuilder createEntityManagerFactoryBuilder(JpaProperties jpaProperties) {
        JpaVendorAdapter jpaVendorAdapter = createJpaVendorAdapter(jpaProperties);
        return new EntityManagerFactoryBuilder(jpaVendorAdapter, jpaProperties.getProperties(), null);
    }

    private JpaVendorAdapter createJpaVendorAdapter(JpaProperties jpaProperties) {
        // ... map JPA properties as needed
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(jpaProperties.isGenerateDdl());
        vendorAdapter.setShowSql(jpaProperties.isShowSql());
        vendorAdapter.setDatabase(jpaProperties.getDatabase());
        vendorAdapter.setDatabasePlatform(jpaProperties.getDatabasePlatform());
     
        // return new HibernateJpaVendorAdapter();
        return vendorAdapter;
    }

    // // trasationManager  에 연결시켜주어야 동작한다.
    @Bean(name = "secondTransactionManager")
    public PlatformTransactionManager secondTransactionManager(@Qualifier("secondEntityManagerFactory") EntityManagerFactory secondEntityManagerFactory) {
        return new JpaTransactionManager(secondEntityManagerFactory);
    }


}

