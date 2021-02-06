package com.cenboomh.service;

import com.cenboomh.dao.read.ReadMapper;
import com.cenboomh.dao.write.WriteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class BigFieldTestServiceImpl implements PostgresTest {


    private static final List<Integer[]> file_size = new ArrayList<>();

    static {
        file_size.add(new Integer[]{1, 102400});
        file_size.add(new Integer[]{102400, 15340000});
//        file_size.add(new Integer[]{15340000, 17340000});
//        file_size.add(new Integer[]{17340000, 60240000});
//        file_size.add(new Integer[]{60240000, 102400000});
//        file_size.add(new Integer[]{302400000, 502400000});
    }


    @Autowired
    private ReadMapper readMapper;

    @Autowired
    private WriteMapper writeMapper;

    @Transactional(transactionManager = "readTransactionManager", rollbackFor = Throwable.class)
    public void testRead(Long num) throws Exception {
        List list = readMapper.testBigFieldQuery();
        if (list == null || list.isEmpty()) {
            throw new Exception("没有查询到数据！！");
        }
    }

    @Transactional(transactionManager = "writeTransactionManager", rollbackFor = Exception.class)
    public void testWrite(Long num) {
        writeMapper.testBigFieldInsert(num, genRandomStr());
    }

    private static String genRandomStr() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        Integer[] range = file_size.get(random.nextInt(file_size.size()));
        int jmax = random.nextInt(range[1] - range[0]) + range[0];
        return genRandomStr(jmax);
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


}
