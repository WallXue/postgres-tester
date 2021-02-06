package com.cenboomh.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Configuration
public class AppConfig {

    @Configuration
    @MapperScan(
            basePackages = "com.cenboomh.dao.read",
            sqlSessionFactoryRef = "readSqlSessionFactory",
            sqlSessionTemplateRef = "readSqlSessionTemplate")
    @EnableTransactionManagement
    @ConditionalOnProperty("spring.datasource.read.jdbc-url")
    static class ReadDataSource {

        @Bean
        @Primary
        @ConfigurationProperties("spring.datasource.read")
        public DataSource readDataSource() {
                return DataSourceBuilder.create().type(HikariDataSource.class).build();
        }

        @Bean
        public SqlSessionFactory readSqlSessionFactory() throws Exception {
            SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
            bean.setDataSource(readDataSource());
            return bean.getObject();
        }

        @Bean
        public SqlSessionTemplate readSqlSessionTemplate() throws Exception {
            return new SqlSessionTemplate(readSqlSessionFactory());
        }

        @Bean(name = "readTransactionManager")
        public DataSourceTransactionManager readTransactionManager() {
            return new DataSourceTransactionManager(readDataSource());
        }
    }


    @Configuration
    @MapperScan(
            basePackages = "com.cenboomh.dao.write",
            sqlSessionFactoryRef = "writeSqlSessionFactory",
            sqlSessionTemplateRef = "writeSqlSessionTemplate")
    @EnableTransactionManagement
    @ConditionalOnProperty("spring.datasource.write.jdbc-url")
    static class WriteDataSource {

        @Bean
        @ConfigurationProperties("spring.datasource.write")
        public DataSource writeDataSource() {
            return DataSourceBuilder.create().type(HikariDataSource.class).build();
        }

        @Bean
        public SqlSessionFactory writeSqlSessionFactory() throws Exception {
            SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
            bean.setDataSource(writeDataSource());
            return bean.getObject();
        }

        @Bean
        public SqlSessionTemplate writeSqlSessionTemplate() throws Exception {
            return new SqlSessionTemplate(writeSqlSessionFactory());
        }

        @Bean(name = "writeTransactionManager")
        public DataSourceTransactionManager writeTransactionManager() {
            return new DataSourceTransactionManager(writeDataSource());
        }
    }

    static class ExecutorProperty {
        private Integer corePoolSize;
        private Integer maxPoolSize;
        private Integer queueCapacity;
        private Integer keepAliveSeconds;

        public Integer getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(Integer corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public Integer getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(Integer maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public Integer getQueueCapacity() {
            return queueCapacity;
        }

        public void setQueueCapacity(Integer queueCapacity) {
            this.queueCapacity = queueCapacity;
        }

        public Integer getKeepAliveSeconds() {
            return keepAliveSeconds;
        }

        public void setKeepAliveSeconds(Integer keepAliveSeconds) {
            this.keepAliveSeconds = keepAliveSeconds;
        }
    }

    @Configuration
    @ConditionalOnProperty(name = "app.read-executor.corePoolSize")
    static class ReadExecutor {
        @Bean
        @ConfigurationProperties("app.read-executor")
        public ExecutorProperty readExecutorProperties() {
            return new ExecutorProperty();
        }

        @Bean
        @Qualifier("readExecutor")
        public ExecutorService readExecutor() {
            ExecutorProperty executorProperty = readExecutorProperties();
            return new ThreadPoolExecutor(
                    executorProperty.corePoolSize, executorProperty.maxPoolSize,
                    executorProperty.keepAliveSeconds, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(executorProperty.queueCapacity),
                    new CustomizableThreadFactory("readExecutor-"),
                    new ThreadPoolExecutor.DiscardPolicy()
            );
        }
    }

    @Configuration
    @ConditionalOnProperty(name = "app.write-executor.corePoolSize")
    static class WriteExecutor {
        @Bean
        @ConfigurationProperties("app.write-executor")
        public ExecutorProperty writeExecutorProperties() {
            return new ExecutorProperty();
        }

        @Bean
        @Qualifier("writeExecutor")
        public ExecutorService writeExecutor() {
            ExecutorProperty executorProperty = writeExecutorProperties();
            return new ThreadPoolExecutor(
                    executorProperty.corePoolSize, executorProperty.maxPoolSize,
                    executorProperty.keepAliveSeconds, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(executorProperty.queueCapacity),
                    new CustomizableThreadFactory("writeExecutor-"),
                    new ThreadPoolExecutor.DiscardPolicy()
            );
        }
    }



//    @Configuration
//    static class MyBatisConfig {
//        @Bean
//        ConfigurationCustomizer mybatisConfigurationCustomizer() {
//            return new ConfigurationCustomizer() {
//                @Override
//                public void customize(org.apache.ibatis.session.Configuration configuration) {
//                    configuration.setDefaultStatementTimeout(30);
//                }
//            };
//        }
//    }
}


