package com.toyshop.consultation.mapper;

import com.toyshop.consultation.entity.ToyConsultationMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConsultationMessageMapper {
    int insert(ToyConsultationMessage message);

    List<ToyConsultationMessage> selectByConsultationId(@Param("consultationId") Long consultationId);
}
