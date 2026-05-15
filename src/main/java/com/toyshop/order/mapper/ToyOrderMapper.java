package com.toyshop.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.toyshop.order.entity.ToyOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ToyOrderMapper extends BaseMapper<ToyOrder> {
}

