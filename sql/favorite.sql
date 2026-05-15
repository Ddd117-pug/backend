CREATE TABLE `favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_favorite_user_product` (`user_id`, `product_id`),
  KEY `idx_favorite_user_id` (`user_id`),
  KEY `idx_favorite_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';
