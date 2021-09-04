package com.lucky.ut.effective.h2.processor;

import com.lucky.ut.effective.base.MockDatabase;
import com.lucky.ut.effective.h2.annotation.YmlDataSet;
import com.lucky.ut.effective.h2.enums.ColType;
import com.lucky.ut.effective.h2.model.Parameter;
import com.lucky.ut.effective.h2.model.Query;
import com.lucky.ut.effective.h2.model.YmlTestDate;
import com.lucky.ut.effective.h2.model.YmlTestDate.TestDataBean;
import com.lucky.ut.effective.h2.model.YmlTestDate.TestDataBean.TableBean;
import com.lucky.ut.effective.h2.model.YmlTestDate.TestDataBean.TableBean.RowsBean;
import com.lucky.ut.effective.h2.model.YmlTestDate.TestDataBean.TableBean.RowsBean.RowBean.ColumnsBean;
import com.lucky.ut.effective.h2.model.YmlTestDate.TestDataBean.TableBean.RowsBean.RowBean.ColumnsBean.ColBean;
import com.lucky.ut.effective.h2.model.YmlTestDate.TestDataBean.TableBean.TypesBean;
import com.lucky.ut.effective.h2.model.YmlTestDate.TestDataBean.TableBean.TypesBean.TypeHintBean;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
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

/**
 * @Author xiuyin.cui@luckincoffee.com
 * @Date 2020/9/28 20:02
 * @Description {@link com.lucky.ut.effective.h2.annotation.YmlDataSet} processor
 */
public class YmlDataSetProcessor {

    private Logger logger = LoggerFactory.getLogger(YmlDataSetProcessor.class);

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
     * 执行单测前把 @YmlDataSet 的数据插入到 H2 数据库
     *
     * @param context 上下文
     * @return
     */
    public void beforeAll(ExtensionContext context) throws SQLException, FileNotFoundException {
        YmlDataSet ymlDataSet = getYmlDataSet(context);
        if (ymlDataSet == null) return;
        org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
        for (String name : ymlDataSet.value()) {
            YmlTestDate ymlTestDate = yaml.loadAs(context.getRequiredTestClass().getResourceAsStream(name), YmlTestDate.class);
            for (TestDataBean testDataBean : ymlTestDate.getTestData()) {
                Map<String, ColType> colTypeMap = initial(testDataBean.getTable().getName().toLowerCase());
                // 1. 创建 insert 脚本
                List<Query> queryList = createInsertQueryOperator(testDataBean.getTable(), colTypeMap);
                // 2. 执行 insert 脚本
                queryOperator(queryList);
            }
        }
    }

    /**
     * 执行单测后把 @DataSet 的数据从 H2 数据库清理
     *
     * @param context 上下文
     */
    public void afterAll(ExtensionContext context) throws SQLException, FileNotFoundException {
        YmlDataSet ymlDataSet = getYmlDataSet(context);
        if (ymlDataSet == null) return;
        org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
        for (String name : ymlDataSet.value()) {
            YmlTestDate ymlTestDate = yaml.loadAs(context.getRequiredTestClass().getResourceAsStream(name), YmlTestDate.class);
            for (TestDataBean testDataBean : ymlTestDate.getTestData()) {
                Map<String, ColType> colTypeMap = initial(testDataBean.getTable().getName().toLowerCase());
                // 1. 创建 delete 脚本
                List<Query> queryList = createDeleteQueryOperator(testDataBean.getTable(), colTypeMap);
                // 2. 执行 delete 脚本
                queryOperator(queryList);
            }
        }
    }

    private YmlDataSet getYmlDataSet(ExtensionContext context) {
        Method requiredTestMethod = context.getRequiredTestMethod();
        YmlDataSet ymlDataSet = requiredTestMethod.getAnnotation(YmlDataSet.class);
        if (ymlDataSet == null) {
            Class<?> requiredTestClass = context.getRequiredTestClass();
            ymlDataSet = requiredTestClass.getAnnotation(YmlDataSet.class);
        }
        return ymlDataSet;
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

    private List<Query> createInsertQueryOperator(TableBean tableBean, Map<String, ColType> colTypeMap) {
        String tableName = tableBean.getName().toLowerCase();
        // key : (id,name,sku,inventory) VALUES (?,?,?,?)
        // value : Col list
        Map<String, List<ColBean>> colMap = new HashMap<>(16);
        for (RowsBean rowsBean : tableBean.getRows()) {
            List<ColumnsBean> columns = rowsBean.getRow().getColumns();
            if (columns == null || columns.size() == 0) continue;
            List<ColBean> colList = columns.stream().map(ColumnsBean::getCol).collect(Collectors.toList());
            String s1 = colList.stream().map(ColBean::getName).collect(Collectors.joining(",", "(", ")"));
            String s2 = colList.stream().map(col -> "?").collect(Collectors.joining(",", "(", ")"));
            colMap.put(s1 + " VALUES " + s2, colList);
        }
        // INSERT INTO t_goods(id,name,sku,inventory) VALUES (?,?,?,?)
        List<Query> queryList = new ArrayList<>();
        for (Entry<String, List<ColBean>> entry : colMap.entrySet()) {
            String sql = "INSERT INTO " + tableName + entry.getKey();
            List<Parameter> params = entry.getValue().stream().map(col -> {
                Optional<TypeHintBean> optional = tableBean.getTypes().stream().map(TypesBean::getTypeHint).filter(typeHint -> col.getName().equals(typeHint.getName())).findFirst();
                if (optional.isPresent()) {
                    return new Parameter(col.getValue(), optional.get().getType());
                }
                ColType colType = colTypeMap.get(col.getName());
                return new Parameter(col.getValue(), colType != null ? colType : ColType.DEFAULT);
            }).collect(Collectors.toList());
            queryList.add(new Query(sql, params));
        }
        return queryList;
    }

    private List<Query> createDeleteQueryOperator(TableBean tableBean, Map<String, ColType> colTypeMap) {
        String tableName = tableBean.getName().toLowerCase();
        // key: id = ?
        // value: Col list
        Map<String, List<ColBean>> colMap = new HashMap<>(16);
        for (RowsBean rowsBean : tableBean.getRows()) {
            List<ColumnsBean> columns = rowsBean.getRow().getColumns();
            if (columns == null || columns.size() == 0) continue;
            List<ColBean> ids = columns.stream().map(ColumnsBean::getCol).filter(ColBean::isIsId).collect(Collectors.toList());
            if (ids.isEmpty()) {
                throw new UnsupportedOperationException("Please set at least one Id Col [e.g. @Col(name = \"id\", value = \"1\", isId = true)]");
            }
            String key = ids.stream().map(col -> col.getName() + " = ?").collect(Collectors.joining(" AND "));
            colMap.put(key, ids);
        }
        // DELETE FROM t_goods WHERE id = ?
        List<Query> queryList = new ArrayList<>();
        for (Entry<String, List<ColBean>> entry : colMap.entrySet()) {
            String sql = "DELETE FROM " + tableName + " WHERE " + entry.getKey();
            List<Parameter> params = entry.getValue().stream().map(col -> {
                Optional<TypeHintBean> optional = tableBean.getTypes().stream().map(TypesBean::getTypeHint).filter(typeHint -> col.getName().equals(typeHint.getName())).findFirst();
                if (optional.isPresent()) {
                    return new Parameter(col.getValue(), optional.get().getType());
                }
                return new Parameter(col.getValue(), colTypeMap.getOrDefault(col.getName(), ColType.DEFAULT));
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
