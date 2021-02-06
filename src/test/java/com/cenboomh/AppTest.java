package com.cenboomh;

import com.cenboomh.service.BigFieldTestServiceImpl;
import com.cenboomh.service.PostgresTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, properties = {
        "command.line.runner.enabled=false",
        "application.runner.enabled=false",
        "1=1"})
public class AppTest {


    @Autowired
    @Qualifier("bigFieldTestServiceImpl")
    private PostgresTest bigFieldTest;

    @Test
    public void testBigFieldInsert() throws Exception {
        bigFieldTest.testWrite(10L);
    }

}
