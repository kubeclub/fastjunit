package com.lucky.ut.effective.extend;

import com.lucky.ut.effective.h2.H2DBUtil;
import com.lucky.ut.effective.h2.annotation.H2DB;
import com.lucky.ut.effective.h2.processor.DataSetProcessor;
import com.lucky.ut.effective.h2.processor.SqlDataSetProcessor;
import com.lucky.ut.effective.h2.processor.YmlDataSetProcessor;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionConfigurationException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.util.ExceptionUtils;
import org.junit.platform.commons.util.ReflectionUtils;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.function.Predicate;

import static org.junit.platform.commons.util.AnnotationUtils.findAnnotatedFields;
import static org.junit.platform.commons.util.ReflectionUtils.makeAccessible;

/**
 * @Author xiuyin.cui@luckincoffee.com
 * @Date 2020/9/18 11:12
 * @Description JUnit Jupiter extension for {@link H2DB @H2DB}.
 */
public class H2DBExtend implements BeforeAllCallback, BeforeEachCallback, ParameterResolver,
        BeforeTestExecutionCallback, AfterTestExecutionCallback {

    DataSetProcessor dataSetProcessor = new DataSetProcessor();
    YmlDataSetProcessor ymlDataSetProcessor = new YmlDataSetProcessor();
    SqlDataSetProcessor sqlDataSetProcessor = new SqlDataSetProcessor();

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        injectStaticFields(context, context.getRequiredTestClass());
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        context.getRequiredTestInstances().getAllInstances() //
                .forEach(instance -> injectInstanceFields(context, instance));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.isAnnotated(H2DB.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Class<?> parameterType = parameterContext.getParameter().getType();
        assertSupportedType("parameter", parameterType);
        boolean present = parameterContext.findAnnotation(H2DB.class).isPresent();
        if (!present) {
            throw new ExtensionConfigurationException("Can resolve @H2DB annotation");
        }
        return resolveH2DBUtil(parameterContext.findAnnotation(H2DB.class).get(), extensionContext.getRequiredTestClass());
    }

    private void injectStaticFields(ExtensionContext context, Class<?> testClass) {
        injectFields(context, null, testClass, ReflectionUtils::isStatic);
    }

    private void injectInstanceFields(ExtensionContext context, Object instance) {
        injectFields(context, instance, instance.getClass(), ReflectionUtils::isNotStatic);
    }

    private void injectFields(ExtensionContext context, Object testInstance, Class<?> testClass,
                              Predicate<Field> predicate) {
        findAnnotatedFields(testClass, H2DB.class, predicate).forEach(field -> {
            assertSupportedType("field", field.getType());
            H2DB h2DB = field.getAnnotation(H2DB.class);
            try {
                makeAccessible(field).set(testInstance, resolveH2DBUtil(h2DB, testClass));
            } catch (Throwable t) {
                ExceptionUtils.throwAsUncheckedException(t);
            }
        });
    }


    private void assertSupportedType(String target, Class<?> type) {
        if (type != H2DBUtil.class) {
            throw new ExtensionConfigurationException("Can only resolve @H2DB " + target + " of type "
                    + H2DBUtil.class.getName() + " but was: " + type.getName());
        }
    }


    private Object resolveH2DBUtil(H2DB h2DB, Class<?> requiredTestClass) {
        InputStream inputStream = requiredTestClass.getResourceAsStream(h2DB.value());
        H2DBUtil h2DBUtil = new H2DBUtil();
        h2DBUtil.setInputStream(inputStream);
        return h2DBUtil;
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        dataSetProcessor.beforeAll(context);
        ymlDataSetProcessor.beforeAll(context);
        sqlDataSetProcessor.beforeAll(context);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        dataSetProcessor.afterAll(context);
        ymlDataSetProcessor.afterAll(context);
    }
}
