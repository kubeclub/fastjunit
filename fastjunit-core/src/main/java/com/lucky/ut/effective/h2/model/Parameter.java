package com.lucky.ut.effective.h2.model;

import com.lucky.ut.effective.h2.enums.ColType;

/**
 * @Author xiuyin.cui@luckincoffee.com
 * @Date 2020/9/29 11:13
 * @Description SQL参数
 */
public class Parameter {

    private String value;

    private ColType type;

    public Parameter() {

    }

    public Parameter(String value, ColType type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ColType getType() {
        return type;
    }

    public void setType(ColType type) {
        this.type = type;
    }
}
