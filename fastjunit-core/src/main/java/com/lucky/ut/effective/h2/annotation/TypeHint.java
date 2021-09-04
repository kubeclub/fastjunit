package com.lucky.ut.effective.h2.annotation;

import com.lucky.ut.effective.h2.enums.ColType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author xiuyin.cui@luckincoffee.com
 * @Date 2020/9/27 10:47
 * @Description The annotation to apply a type hint to the sql.
 * <p>
 * By default, the column type is scanned before executing an insert query.
 * But sometimes, the type scanning returns unexpected column types.
 * <p>
 * In this case, you can specify the column type expressly by this annotation.
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TypeHint {
    /**
     * the column name
     */
    String name();

    /**
     * the column type to identify generic SQL types
     */
    ColType type();
}
