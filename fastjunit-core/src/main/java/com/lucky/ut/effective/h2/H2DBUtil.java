package com.lucky.ut.effective.h2;

import com.lucky.ut.effective.base.MockDatabase;
import org.h2.util.IOUtils;
import org.h2.util.StringUtils;
import org.junit.Assert;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @Author xiuyin.cui@luckincoffee.com
 * @Date 2020/9/18 11:25
 * @Description tool class for h2 database execute query
 */
public class H2DBUtil {

    private Logger logger = LoggerFactory.getLogger(H2DBUtil.class);

    private InputStream inputStream;

    public void executeQuery() throws SQLException, IOException {
        DataSource dataSource = MockDatabase.context.dataSource();
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        Assert.assertTrue("execute parameter is empty，see annotation @H2DB", inputStream != null);
        Reader reader = IOUtils.getReader(inputStream);
        String content = IOUtils.readStringAndClose(reader, -1);
        String[] sentences = StringUtils.arraySplit(content, ';', true);
        for (String sentence : sentences) {
            if (sentence != null && !sentence.isEmpty() && !sentence.startsWith("--")) {
                ResultSet resultSet = statement.executeQuery(sentence);
                resultSet.next();
                System.out.println("Succeeded: " + sentence + " = " + resultSet.getString(1));
                resultSet.close();
            }
        }
        statement.close();
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
