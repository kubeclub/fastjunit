package com.lucky.ut.effective.h2.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.PrimitiveIterator;

/**
 * @Author xiuyin.cui@luckincoffee.com
 * @Date 2020/9/27 10:30
 * @Description The annotation to specify the row to insert the test data.
 * <p>
 * At least one Id column (the Col `isId` parameter is true) requires in each row.
 * The Id column is used when the delete task is executed.
 * <p>
 * When you specify binary data, convert the data into base64 string.
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Col {
    /**
     * the column name
     */
    String name();

    /**
     * the column value
     */
    String value();

    /**
     * whether this column is id (false is default)
     */
    boolean isId() default false;
}
