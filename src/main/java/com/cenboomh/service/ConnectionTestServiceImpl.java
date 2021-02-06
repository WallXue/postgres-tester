package com.cenboomh.service;

import com.cenboomh.dao.read.ReadMapper;
import com.cenboomh.dao.write.WriteMapper;
import org.postgresql.jdbc.PgConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Random;

@Service
public class ConnectionTestServiceImpl implements PostgresTest {

    @Autowired
    private ReadMapper readMapper;

    @Autowired
    private WriteMapper writeMapper;

    @Autowired
    @Qualifier("readDataSource")
    private DataSource readDataSource;

    @Transactional(transactionManager = "readTransactionManager", rollbackFor = Throwable.class)
    public void testRead(Long num) throws Exception {
        List list = readMapper.testRandomQuery();
//        if (list == null || list.isEmpty()) {
//            throw new Exception("没有查询到数据！！");
//        }
    }

//    public void testRead() throws Exception {
//        PgConnection connection = null;
//        try {
//            connection = (PgConnection)readDataSource.getConnection();
//            connection.setAutoCommit(false);
//            try (Statement statement = connection.createStatement()) {
////                ResultSet resultSet = statement.executeQuery("select * from emr.emr_medicalrecord where id>floor(random()*(1000000-0))\n" +
////                        "            and id < floor(random()*(1000000-0))\n" +
////                        "            limit floor(random()*(100-0))+1;\n");
//                ResultSet resultSet = statement.executeQuery("select random()");
//                boolean next = resultSet.next();
//                int row = resultSet.getRow();
//                if (!next || row < 1) {
//                    throw new Exception("没有查询到数据！！");
//                }
//                resultSet.close();
//            }
//        } catch (Exception e) {
////            if (connection != null) {
////                connection.rollback();
////            }
//            throw e;
//        } finally {
//            if (connection != null && !connection.isClosed()) {
//                connection.close();
//            }
//        }
//
//    }

    @Transactional(transactionManager = "writeTransactionManager", rollbackFor = Exception.class)
    public void testWrite(Long num) {
        writeMapper.testInsert(num);
    }

    private static String genRandomStr(int len) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < len; j++) {
            int i = random.nextInt(126 - 32) + 32;
            sb.append((char) i);
        }
        return sb.toString();
    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        try (PrintWriter writer = new PrintWriter("foo-out.txt", "UTF-8")){
            writer.write(genRandomStr(500000));
        }
    }
}
