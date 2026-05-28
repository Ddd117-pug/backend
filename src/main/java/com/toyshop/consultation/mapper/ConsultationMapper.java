package com.toyshop.consultation.mapper;

import com.toyshop.consultation.entity.ToyConsultation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ConsultationMapper {
    int insert(ToyConsultation consultation);

    ToyConsultation selectById(@Param("id") Long id);

    List<ToyConsultation> selectUserPage(@Param("userId") Long userId,
                                         @Param("status") Integer status,
                                         @Param("offset") Integer offset,
                                         @Param("pageSize") Integer pageSize);

    long countUserPage(@Param("userId") Long userId,
                       @Param("status") Integer status);

    List<ToyConsultation> selectAdminPage(@Param("keyword") String keyword,
                                          @Param("userId") Long userId,
                                          @Param("productId") Long productId,
                                          @Param("status") Integer status,
                                          @Param("offset") Integer offset,
                                          @Param("pageSize") Integer pageSize);

    long countAdminPage(@Param("keyword") String keyword,
                        @Param("userId") Long userId,
                        @Param("productId") Long productId,
                        @Param("status") Integer status);

    int updateStatus(@Param("id") Long id,
                     @Param("status") Integer status,
                     @Param("updatedAt") LocalDateTime updatedAt,
                     @Param("closedAt") LocalDateTime closedAt);

    int updateLastMessage(@Param("id") Long id,
                          @Param("lastMessage") String lastMessage,
                          @Param("lastSenderType") String lastSenderType,
                          @Param("unreadUserCount") Integer unreadUserCount,
                          @Param("unreadAdminCount") Integer unreadAdminCount,
                          @Param("updatedAt") LocalDateTime updatedAt,
                          @Param("status") Integer status);

    int markUserRead(@Param("id") Long id);

    int markAdminRead(@Param("id") Long id);
}
