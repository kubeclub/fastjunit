package com.lucky.example.database;

import com.lucky.example.infrastructure.database.mapper.GoodsMapper;
import com.lucky.example.infrastructure.database.po.GoodsPo;
import com.lucky.ut.effective.h2.annotation.SqlDateSet;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author xiuyin.cui@luckincoffee.com
 * @Date 2020/9/29 13:31
 * @Description 持久层单元测试
 */
@Component
@SqlDateSet(value = "/data/t_goods.sql")
public class GoodsMapperSqlDateSetTest extends BaseMockDatabase {

    @Resource
    GoodsMapper goodsMapper;

    @Test
    @DisplayName("数据库单测用例 - 查询")
    public void select() {
        GoodsPo result = goodsMapper.selectOne("1");
        Assertions.assertThat(result)
                .hasFieldOrPropertyWithValue("id", "1")
                .hasFieldOrPropertyWithValue("name", "apple")
                .hasFieldOrPropertyWithValue("sku", "apple-10001")
                .hasFieldOrPropertyWithValue("inventory", 100);
    }
}
