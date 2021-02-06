package com.cenboomh;

import com.cenboomh.service.ConnectionTestServiceImpl;
import com.cenboomh.service.PostgresTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * Hello world!
 */
@Configuration
@SpringBootApplication
public class App implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    private static final LongAdder queryCount = new LongAdder();
    private static final LongAdder insertCount = new LongAdder();

    @Autowired(required = false)
    @Qualifier("readExecutor")
    private ExecutorService readExecutor;

    @Autowired(required = false)
    @Qualifier("writeExecutor")
    private ExecutorService writeExecutor;

    @Autowired
    @Qualifier("bigFieldTestServiceImpl")
    private PostgresTest bigFieldTestService;

    @Autowired
    @Qualifier("connectionTestServiceImpl")
    private PostgresTest connectionTestService;


    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    @ConfigurationProperties("app.pg-test")
    public TestConfig testConfig() {
        return new TestConfig();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("info test");
        logger.error("error test");
        final TestConfig testConfig = testConfig();

        PostgresTest postgresTest = "bigFieldTestServiceImpl".equalsIgnoreCase(testConfig.getService()) ? bigFieldTestService : connectionTestService;
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
        if (Arrays.stream(testConfig.getModel()).anyMatch("read"::equalsIgnoreCase)) {
            scheduledExecutorService.scheduleAtFixedRate(() -> {
                for (int i = 0; i < testConfig.readOneSecondCount; i++) {
                    readExecutor.submit(() -> {
                        queryCount.increment();
                        long l = System.currentTimeMillis();
                        long num = queryCount.longValue();
                        logger.warn("开始查询：{}次", num);
                        try {
                            postgresTest.testRead(num);
                        } catch (Exception e) {
                            logger.error("查询异常", e);
                        }
                        logger.warn("查询耗时，{}", (System.currentTimeMillis() - l));
                    });
                }
            }, 0, 1, TimeUnit.SECONDS);
        }
        if (Arrays.stream(testConfig.getModel()).anyMatch("write"::equalsIgnoreCase)) {
            scheduledExecutorService.scheduleAtFixedRate(() -> {
                for (int i = 0; i < testConfig.writeOneSecondCount; i++) {
                    writeExecutor.submit(() -> {
                        insertCount.increment();
                        long l = System.currentTimeMillis();
                        long num = insertCount.longValue();
                        logger.warn("开始插入,{}次", num);
                        try {
                            postgresTest.testWrite(num);
                        } catch (Exception e) {
                            logger.error("插入异常", e);
                        }
                        logger.warn("插入耗时，{}", (System.currentTimeMillis() - l));
                    });
                }
            }, 0, 1, TimeUnit.SECONDS);
        }
        scheduledExecutorService.awaitTermination(5, TimeUnit.DAYS);
    }


    static class TestConfig {
        String service = "connectionTestServiceImpl";
        String[] model;
        Integer writeOneSecondCount;
        Integer readOneSecondCount;

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }

        public String[] getModel() {
            return model;
        }

        public void setModel(String[] model) {
            this.model = model;
        }

        public Integer getWriteOneSecondCount() {
            return writeOneSecondCount;
        }

        public void setWriteOneSecondCount(Integer writeOneSecondCount) {
            this.writeOneSecondCount = writeOneSecondCount;
        }

        public Integer getReadOneSecondCount() {
            return readOneSecondCount;
        }

        public void setReadOneSecondCount(Integer readOneSecondCount) {
            this.readOneSecondCount = readOneSecondCount;
        }
    }
}
