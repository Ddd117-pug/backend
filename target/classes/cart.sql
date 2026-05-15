CREATE TABLE `cart` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '加入购物车时单价',
  `num` INT NOT NULL DEFAULT 1 COMMENT '购买数量',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_cart_user_id` (`user_id`),
  KEY `idx_cart_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';
