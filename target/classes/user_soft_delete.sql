ALTER TABLE `sys_user`
ADD COLUMN `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已注销' AFTER `role`;
