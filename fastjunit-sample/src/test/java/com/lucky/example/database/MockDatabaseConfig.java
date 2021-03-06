package com.lucky.example.database;

import com.lucky.ut.effective.base.MockDatabase;
import com.ucarinc.framework.autoconfigure.datasource.UcarincDataSourceProperties;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @author zhourj
 * @date 2020/7/8 9:12
 */
@ActiveProfiles("junit-test")
@Configuration
@MapperScan(basePackages = "com.lucky.example.infrastructure.*.mapper", sqlSessionFactoryRef = "sqlSessionFactory")
public class MockDatabaseConfig {

    @Bean
    public UcarincDataSourceProperties simpleDataSourceProperties() {
        UcarincDataSourceProperties ucarincDataSourceProperties = new UcarincDataSourceProperties();
        ucarincDataSourceProperties.setLocal(true);
        ucarincDataSourceProperties.setName("test_jndi");
        return new UcarincDataSourceProperties();
    }

    @Bean(name = "sampleDataSource")
    public DataSource sampleDatasource() {
        return MockDatabase.context.dataSource();
    }


    @Resource(name = "sampleDataSource")
    private DataSource sampleDataSource;


    @Bean(name = "transactionManager")
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(sampleDataSource);
    }

    @Bean(name = "sqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory()
            throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(sampleDataSource);
        // 设置 mybatis 的配置文件
        bean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("classpath:mybatis.xml"));
        // 设置mybatis的xml所在位置
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/*.xml"));
        return bean.getObject();
    }

}
