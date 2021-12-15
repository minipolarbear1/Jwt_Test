package com.example.jwttest.config.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.jwttest.config.database",
        entityManagerFactoryRef = "jwtEntityManagerFactory",
        transactionManagerRef = "jwtTransactionManager"
)
public class MariaDBConfig {
    private static final String DEFAULT_DIALECT = "org.hibernate.dialect.MariaDB53Dialect";

    private final Environment env;

    @Autowired
    public MariaDBConfig(Environment env) {
        this.env = env;
    }


    @Primary
    @Bean
    public DataSource jwtDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(env.getProperty("datasource.url"));
        config.setUsername(env.getProperty("datasource.username"));
        config.setPassword(env.getProperty("datasource.password"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setMaxLifetime(240000);
        config.setConnectionTimeout(10000);
        config.setValidationTimeout(10000);

        HikariDataSource ds = new HikariDataSource(config);
        try {
            log.info("hikari MariaDB isClosed : {}", ds.getConnection().isClosed());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    @Primary
    @Bean(name = "jwtEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean jwtEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        Properties jpaProperties = new Properties();
        jpaProperties.put(AvailableSettings.DIALECT, DEFAULT_DIALECT);    // 방언 설정

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(jwtDataSource());
        factoryBean.setPackagesToScan("com.example.jwttest.domain.entity"); // @Entity 탐색위치
        factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());   // JPA 구현체로는 하이버네이트를 사용
        factoryBean.setPersistenceUnitName("jwt-mariadb");
        factoryBean.setJpaProperties(jpaProperties);

        return factoryBean;
    }


    @Primary
    @Bean(name = "jwtTransactionManager")
    PlatformTransactionManager jwtTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(jwtEntityManagerFactory(builder).getObject());
    }


}
