package com.lucky.ut.effective.h2.processor;

import com.lucky.ut.effective.base.MockDatabase;
import com.lucky.ut.effective.h2.annotation.Col;
import com.lucky.ut.effective.h2.annotation.DataSet;
import com.lucky.ut.effective.h2.annotation.Row;
import com.lucky.ut.effective.h2.annotation.Table;
import com.lucky.ut.effective.h2.annotation.TypeHint;
import com.lucky.ut.effective.h2.enums.ColType;
import com.lucky.ut.effective.h2.model.Parameter;
import com.lucky.ut.effective.h2.model.Query;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author xiuyin.cui@luckincoffee.com
 * @Date 2020/9/28 20:02
 * @Description {@link com.lucky.ut.effective.h2.annotation.DataSet} processor
 */
public class DataSetProcessor {

    private Logger logger = LoggerFactory.getLogger(DataSetProcessor.class);

    /**
     * key: tableName
     * value: {
     * * * key: column name
     * * * value: column type
     * }
     */
    private Map<String, Map<String, ColType>> tableColTypeMap = new HashMap<>(16);

    private DataSource dataSource = MockDatabase.context.dataSource();


    /**
     * 执行单测前把 @DataSet 的数据插入到 H2 数据库
     *
     * @param context 上下文
     * @return
     */
    public void beforeAll(ExtensionContext context) throws SQLException {
        DataSet dataSet = getDataSet(context);
        if (dataSet == null) return;
        for (Table table : dataSet.testData()) {
            Map<String, ColType> colTypeMap = initial(table.name().toLowerCase());
            // 1. 创建 insert 脚本
            List<Query> queryList = createInsertQueryOperator(table, colTypeMap);
            // 2. 执行 insert 脚本
            queryOperator(queryList);
        }
    }

    /**
     * 执行单测后把 @DataSet 的数据从 H2 数据库清理
     *
     * @param context 上下文
     */
    public void afterAll(ExtensionContext context) throws SQLException {
        DataSet dataSet = getDataSet(context);
        if (dataSet == null) return;
        for (Table table : dataSet.testData()) {
            Map<String, ColType> colTypeMap = initial(table.name().toLowerCase());
            // 1. 创建 delete 脚本
            List<Query> queryList = createDeleteQueryOperator(table, colTypeMap);
            // 2. 执行 delete 脚本
            queryOperator(queryList);
        }
    }

    private DataSet getDataSet(ExtensionContext context) {
        Method requiredTestMethod = context.getRequiredTestMethod();
        DataSet dataSet = requiredTestMethod.getAnnotation(DataSet.class);
        if (dataSet == null) {
            Class<?> requiredTestClass = context.getRequiredTestClass();
            dataSet = requiredTestClass.getAnnotation(DataSet.class);
        }
        return dataSet;
    }

    public Map<String, ColType> initial(final String tableName) throws SQLException {
        Map<String, ColType> colTypeMap = tableColTypeMap.get(tableName);
        if (colTypeMap != null) {
            return colTypeMap;
        }
        createColumnMetadataOperator(tableName);
        colTypeMap = tableColTypeMap.get(tableName);
        if (colTypeMap == null) {
            throw new UnsupportedOperationException("the table name [" + tableName + "] is not stored in columnByTable.)");
        }
        Map<String, ColType> finalColTypeMap = colTypeMap;
        logger.info(() -> "typeByColumn: " + tableName + "=" + finalColTypeMap);
        return colTypeMap;
    }

    private void createColumnMetadataOperator(String tableName) throws SQLException {
        Map<String, ColType> colTypeMap = tableColTypeMap.get(tableName);
        if (colTypeMap != null && !colTypeMap.isEmpty()) {
            return;
        }
        // {name=VARCHAR, id=VARCHAR, sku=VARCHAR, inventory=INTEGER}
        colTypeMap = new HashMap<>(16);
        Connection conn = dataSource.getConnection();
        conn.setAutoCommit(false);
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE 1 = 2");
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String s = metaData.getColumnName(i).toLowerCase();
                ColType colType = ColType.getColType(metaData.getColumnType(i));
                colTypeMap.put(s, colType);
            }
            tableColTypeMap.put(tableName, colTypeMap);
        } catch (Exception ex) {
            conn.rollback();
            logger.info(() -> "rollback");
            throw ex;
        }
    }

    private List<Query> createInsertQueryOperator(Table table, Map<String, ColType> colTypeMap) {
        String tableName = table.name().toLowerCase();
        // key : (id,name,sku,inventory) VALUES (?,?,?,?)
        // value : Col list
        Map<String, List<Col>> colMap = new HashMap<>(16);
        for (Row row : table.rows()) {
            Col[] columns = row.columns();
            if (columns.length == 0) continue;
            List<Col> colList = Stream.of(columns).collect(Collectors.toList());
            String s1 = colList.stream().map(Col::name).collect(Collectors.joining(",", "(", ")"));
            String s2 = colList.stream().map(col -> "?").collect(Collectors.joining(",", "(", ")"));
            colMap.put(s1 + " VALUES " + s2, colList);
        }
        // INSERT INTO t_goods(id,name,sku,inventory) VALUES (?,?,?,?)
        List<Query> queryList = new ArrayList<>();
        for (Entry<String, List<Col>> entry : colMap.entrySet()) {
            String sql = "INSERT INTO " + tableName + entry.getKey();
            List<Parameter> params = entry.getValue().stream().map(col -> {
                Optional<TypeHint> optional = Stream.of(table.types()).filter(typeHint -> col.name().equals(typeHint.name())).findFirst();
                if (optional.isPresent()) {
                    return new Parameter(col.value(), optional.get().type());
                }
                ColType colType = colTypeMap.get(col.name());
                return new Parameter(col.value(), colType != null ? colType : ColType.DEFAULT);
            }).collect(Collectors.toList());
            queryList.add(new Query(sql, params));
        }
        return queryList;
    }

    private List<Query> createDeleteQueryOperator(Table table, Map<String, ColType> colTypeMap) {
        String tableName = table.name().toLowerCase();
        // key: id = ?
        // value: Col list
        Map<String, List<Col>> colMap = new HashMap<>(16);
        for (Row row : table.rows()) {
            Col[] columns = row.columns();
            if (columns.length == 0) continue;
            List<Col> ids = Stream.of(columns).filter(Col::isId).collect(Collectors.toList());
            if (ids.isEmpty()) {
                throw new UnsupportedOperationException("Please set at least one Id Col [e.g. @Col(name = \"id\", value = \"1\", isId = true)]");
            }
            String key = ids.stream().map(col -> col.name() + " = ?").collect(Collectors.joining(" AND "));
            colMap.put(key, ids);
        }
        // DELETE FROM t_goods WHERE id = ?
        List<Query> queryList = new ArrayList<>();
        for (Entry<String, List<Col>> entry : colMap.entrySet()) {
            String sql = "DELETE FROM " + tableName + " WHERE " + entry.getKey();
            List<Parameter> params = entry.getValue().stream().map(col -> {
                Optional<TypeHint> optional = Stream.of(table.types()).filter(typeHint -> col.name().equals(typeHint.name())).findFirst();
                if (optional.isPresent()) {
                    return new Parameter(col.value(), optional.get().type());
                }
                return new Parameter(col.value(), colTypeMap.getOrDefault(col.name(), ColType.DEFAULT));
            }).collect(Collectors.toList());
            queryList.add(new Query(sql, params));
        }
        return queryList;
    }

    private void queryOperator(List<Query> queryList) throws SQLException {
        Connection conn = dataSource.getConnection();
        conn.setAutoCommit(false);
        for (Query query : queryList) {
            try {
                PreparedStatement pstmt = conn.prepareStatement(query.getSql());
                for (int i = 0; i < query.getParams().size(); i++) {
                    Parameter param = query.getParams().get(i);
                    ColType type = param.getType();
                    Object convertedValue = param.getValue();
                    if (convertedValue != null) {
                        convertedValue = type.convert(param.getValue());
                    }
                    pstmt.setObject(i + 1, convertedValue, type.getSqlType());
                }
                logger.info(() -> "[created query]" + pstmt);
                pstmt.executeUpdate();
                conn.commit();
                logger.info(() -> "commit");
            } catch (SQLException e) {
                conn.rollback();
                logger.info(() -> "rollback");
                throw e;
            }
        }
    }

}
