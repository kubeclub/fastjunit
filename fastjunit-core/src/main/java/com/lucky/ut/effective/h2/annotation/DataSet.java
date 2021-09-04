package com.lucky.ut.effective.h2.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author xiuyin.cui@luckincoffee.com
 * @Date 2020/9/27 10:29
 * @Description The main annotation to specify the test data.
 * <p>
 * You can apply this annotation to the test method that needs the test data before execution.
 * <p>
 * Each test data is inserted in a defined order. And the inserted test data is cleaned up at the proper timing.
 * <p>
 * When this annotation is applied to both the test class and the test method,
 * the annotation applied to the method will be used
 * <p>
 * ## usage
 * ```
 * @DataSet(testData = {
 * @Table(name = "parent_table", rows = {
 * @Row(columns = {
 * @Col(name = "id", value = "2", isId = true),
 * @Col(name = "name", value = "parent_record")
 * })
 * }),
 * @Table(name = "child_table", rows = {
 * @Row(columns = {
 * @Col(name = "id", value = "2", isId = true),
 * @Col(name = "name", value = "child_record"),
 * @Col(name = "parent_table_id", value = "2"),
 * })
 * })
 * })
 * ```
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSet {

    /**
     * the test data
     */
    Table[] testData();

}
