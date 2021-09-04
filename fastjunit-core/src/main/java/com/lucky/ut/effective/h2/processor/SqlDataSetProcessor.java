package com.lucky.ut.effective.h2.processor;

import com.lucky.ut.effective.base.MockDatabase;
import com.lucky.ut.effective.h2.annotation.SqlDateSet;
import org.h2.util.IOUtils;
import org.junit.Assert;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @Author xiuyin.cui@luckincoffee.com
 * @Date 2020/9/28 20:02
 * @Description {@link SqlDateSet} processor
 */
public class SqlDataSetProcessor {

    private Logger logger = LoggerFactory.getLogger(SqlDataSetProcessor.class);

    /**
     * 执行单测前把 @SqlDateSet 的数据插入到 H2 数据库
     *
     * @param context 上下文
     * @return
     */
    public void beforeAll(ExtensionContext context) throws SQLException, IOException {
        SqlDateSet sqlDateSet = getSqlDataSet(context);
        if (sqlDateSet == null) return;
        String[] sqls = sqlDateSet.value();
        Assert.assertTrue("execute parameter is empty，see annotation @SqlDateSet" +
                "", sqls.length > 0);
        DataSource dataSource = MockDatabase.context.dataSource();
        Connection conn = dataSource.getConnection();
        conn.setAutoCommit(false);
        try (Statement statement = conn.createStatement()) {
            for (String sql : sqls) {
                logger.debug(() -> "[execute query]" + sql);
                Reader reader = IOUtils.getReader(context.getRequiredTestClass().getResourceAsStream(sql));
                String content = IOUtils.readStringAndClose(reader, -1);
                statement.execute(content);
                conn.commit();
                logger.info(() -> "commit");
            }
        } catch (SQLException | IOException e) {
            conn.rollback();
            logger.info(() -> "rollback");
            throw e;
        } finally {
            conn.close();
        }
    }


    private SqlDateSet getSqlDataSet(ExtensionContext context) {
        Method requiredTestMethod = context.getRequiredTestMethod();
        SqlDateSet sqlDateSet = requiredTestMethod.getAnnotation(SqlDateSet.class);
        if (sqlDateSet == null) {
            Class<?> requiredTestClass = context.getRequiredTestClass();
            sqlDateSet = requiredTestClass.getAnnotation(SqlDateSet.class);
        }
        return sqlDateSet;
    }

}
