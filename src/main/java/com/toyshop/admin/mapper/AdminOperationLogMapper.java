package com.toyshop.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.toyshop.admin.entity.AdminOperationLogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminOperationLogMapper extends BaseMapper<AdminOperationLogEntity> {
}
