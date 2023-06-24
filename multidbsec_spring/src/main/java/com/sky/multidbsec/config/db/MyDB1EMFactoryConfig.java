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
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.sky.multidbsec.persistence.db1.DB1;

import jakarta.persistence.EntityManagerFactory;

@Configuration(proxyBeanMethods = false)
@EnableAutoConfiguration
@EntityScan(/* defined by application.properties! omitted - basePackageClasses = DB1.class */)
@EnableTransactionManagement
// basePackageClasses:Repository package root, entityManagerFactoryRef: entityManagerFactory bean name, transactionManagerRef: transactionManager bean name
@EnableJpaRepositories(basePackageClasses = DB1.class, entityManagerFactoryRef = "firstEntityManagerFactory", transactionManagerRef = "firstTransactionManager")
public class MyDB1EMFactoryConfig {

    @Primary
    @Bean(name = "firstJpaProperties")
    @ConfigurationProperties("db1.jpa")
    public JpaProperties firstJpaProperties() {
        return new JpaProperties();
    }

    @Primary
    @Bean(name="firstEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean firstEntityManagerFactory(@Qualifier("firstDataSource")DataSource firstDataSource,
            @Qualifier("firstJpaProperties")JpaProperties firstJpaProperties) {
        EntityManagerFactoryBuilder builder = createEntityManagerFactoryBuilder(firstJpaProperties);
        // Entity package root
        return builder.dataSource(firstDataSource).packages(DB1.class).persistenceUnit("DB1").build();
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
    @Primary
    @Bean(name = "firstTransactionManager")
    public PlatformTransactionManager firstTransactionManager(@Qualifier("firstEntityManagerFactory") EntityManagerFactory firstEntityManagerFactory) {
        return new JpaTransactionManager(firstEntityManagerFactory);
    }

}

