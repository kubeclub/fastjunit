package com.lucky.ut.effective.h2.model;

import java.util.List;

/**
 * @Author xiuyin.cui@luckincoffee.com
 * @Date 2020/9/29 11:14
 * @Description SQL 查询
 */
public class Query {

    private String sql;

    private List<Parameter> params;

    public Query() {
    }

    public Query(String sql, List<Parameter> params) {
        this.sql = sql;
        this.params = params;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Parameter> getParams() {
        return params;
    }

    public void setParams(List<Parameter> params) {
        this.params = params;
    }
}
