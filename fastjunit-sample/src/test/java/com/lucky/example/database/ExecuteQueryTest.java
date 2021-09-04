package com.lucky.example.database;

import com.lucky.ut.effective.h2.H2DBUtil;
import com.lucky.ut.effective.h2.annotation.H2DB;
import org.junit.jupiter.api.Test;

/**
 * @Author xiuyin.cui@luckincoffee.com
 * @Date 2020/10/13 9:46
 * @Description 直接执行 SQL 语句
 */

public class ExecuteQueryTest {

    @H2DB(value = "/sql/testing.sql")
    private H2DBUtil h2DBUtil;

    @Test
    public void testExecuteQuery() throws Exception {
        h2DBUtil.executeQuery();
    }
}
