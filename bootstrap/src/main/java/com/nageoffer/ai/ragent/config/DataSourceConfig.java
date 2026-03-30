/**
 * 数据源自动配置类
 * 对应设计文档章节：11.3 数据源自动配置
 * 创建时间：2026-03-30
 */
package com.nageoffer.ai.ragent.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * 数据源自动配置类
 * 根据 spring.datasource.type 配置自动选择数据源
 * 支持 H2 内存数据库和 PostgreSQL 数据库
 */
@Configuration
public class DataSourceConfig {

    /**
     * H2 内存数据库配置属性类
     */
    public static class H2DataSourceProperties {
        private String url;
        private String driverClassName;
        private String username;
        private String password;
        private HikariConfig hikari;

        // getter 和 setter 方法
        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public HikariConfig getHikari() {
            return hikari;
        }

        public void setHikari(HikariConfig hikari) {
            this.hikari = hikari;
        }
    }

    /**
     * PostgreSQL 数据库配置属性类
     */
    public static class PostgresDataSourceProperties {
        private String url;
        private String driverClassName;
        private String username;
        private String password;
        private HikariConfig hikari;

        // getter 和 setter 方法
        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public HikariConfig getHikari() {
            return hikari;
        }

        public void setHikari(HikariConfig hikari) {
            this.hikari = hikari;
        }
    }

    /**
     * H2 内存数据库数据源配置
     * 当配置 spring.datasource.type=H2 或未配置时生效
     */
    @Configuration
    @ConditionalOnProperty(name = "spring.datasource.type", havingValue = "H2", matchIfMissing = true)
    public static class H2DataSourceConfig {

        @Bean
        @Primary
        @ConfigurationProperties(prefix = "spring.datasource.h2")
        public H2DataSourceProperties h2DataSourceProperties() {
            return new H2DataSourceProperties();
        }

        @Bean
        @Primary
        public DataSource dataSource(H2DataSourceProperties properties) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(properties.getUrl());
            config.setDriverClassName(properties.getDriverClassName());
            config.setUsername(properties.getUsername());
            if (properties.getPassword() != null) {
                config.setPassword(properties.getPassword());
            }
            if (properties.getHikari() != null) {
                HikariConfig hikariConfig = properties.getHikari();
                if (hikariConfig.getPoolName() != null) {
                    config.setPoolName(hikariConfig.getPoolName());
                }
                if (hikariConfig.getMaximumPoolSize() > 0) {
                    config.setMaximumPoolSize(hikariConfig.getMaximumPoolSize());
                }
                if (hikariConfig.getMinimumIdle() > 0) {
                    config.setMinimumIdle(hikariConfig.getMinimumIdle());
                }
                if (hikariConfig.getConnectionTimeout() > 0) {
                    config.setConnectionTimeout(hikariConfig.getConnectionTimeout());
                }
                if (hikariConfig.getIdleTimeout() > 0) {
                    config.setIdleTimeout(hikariConfig.getIdleTimeout());
                }
                if (hikariConfig.getMaxLifetime() > 0) {
                    config.setMaxLifetime(hikariConfig.getMaxLifetime());
                }
            }
            return new HikariDataSource(config);
        }

        @Bean
        public DataSourceInitializer h2DataSourceInitializer(DataSource dataSource) {
            DataSourceInitializer initializer = new DataSourceInitializer();
            initializer.setDataSource(dataSource);

            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("database/h2/schema_h2.sql"));
            populator.addScript(new ClassPathResource("database/h2/data_h2.sql"));
            populator.setContinueOnError(false);
            populator.setIgnoreFailedDrops(false);

            initializer.setDatabasePopulator(populator);
            initializer.setEnabled(true);
            return initializer;
        }
    }

    /**
     * PostgreSQL 数据库数据源配置
     * 当配置 spring.datasource.type=POSTGRESQL 时生效
     */
    @Configuration
    @ConditionalOnProperty(name = "spring.datasource.type", havingValue = "POSTGRESQL")
    public static class PostgresDataSourceConfig {

        @Bean
        @Primary
        @ConfigurationProperties(prefix = "spring.datasource.postgresql")
        public PostgresDataSourceProperties postgresDataSourceProperties() {
            return new PostgresDataSourceProperties();
        }

        @Bean
        @Primary
        public DataSource dataSource(PostgresDataSourceProperties properties) {
            HikariConfig config = properties.getHikari() != null
                    ? properties.getHikari()
                    : new HikariConfig();
            config.setJdbcUrl(properties.getUrl());
            config.setDriverClassName(properties.getDriverClassName());
            config.setUsername(properties.getUsername());
            if (properties.getPassword() != null) {
                config.setPassword(properties.getPassword());
            }
            if (config.getPoolName() == null) {
                config.setPoolName("RagentPostgresPool");
            }
            return new HikariDataSource(config);
        }
    }
}