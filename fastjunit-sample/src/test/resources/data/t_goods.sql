SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_goods
-- ----------------------------
DROP TABLE IF EXISTS `t_goods`;
-- 新建表
DROP TABLE IF EXISTS `t_goods`;
CREATE TABLE `t_goods`  (
  `id` varchar(128) NOT NULL COMMENT 'id',
  `name` varchar(128) NOT NULL COMMENT '商品名称',
  `sku` varchar(128) NOT NULL COMMENT '商品规格',
  `inventory` int(11) NOT NULL COMMENT '库存数量',
  PRIMARY KEY (`id`)
) COMMENT = '商品';

-- ----------------------------
-- Records of t_goods
-- ----------------------------
INSERT INTO `t_goods` VALUES ('1', 'apple', 'apple-10001', 100);

SET FOREIGN_KEY_CHECKS = 1;
