package com.lucky.ut.effective.h2.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author xiuyin.cui@luckincoffee.com
 * @Date 2020/9/27 13:47
 * @Description The annotation to specify the table to insert the test data.
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
    /**
     * the table name
     */
    String name();

    /**
     * rows
     */
    Row[] rows();

    /**
     * the column type hints (optional)
     */
    TypeHint[] types() default {};
}
