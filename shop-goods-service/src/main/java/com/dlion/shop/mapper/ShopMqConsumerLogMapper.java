package com.dlion.shop.mapper;

import com.dlion.shop.pojo.ShopMqConsumerLog;
import com.dlion.shop.pojo.ShopMqConsumerLogExample;
import com.dlion.shop.pojo.ShopMqConsumerLogKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShopMqConsumerLogMapper {
    long countByExample(ShopMqConsumerLogExample example);

    int deleteByExample(ShopMqConsumerLogExample example);

    int deleteByPrimaryKey(ShopMqConsumerLogKey key);

    int insert(ShopMqConsumerLog record);

    int insertSelective(ShopMqConsumerLog record);

    List<ShopMqConsumerLog> selectByExample(ShopMqConsumerLogExample example);

    ShopMqConsumerLog selectByPrimaryKey(ShopMqConsumerLogKey key);

    int updateByExampleSelective(@Param("record") ShopMqConsumerLog record, @Param("example") ShopMqConsumerLogExample example);

    int updateByExample(@Param("record") ShopMqConsumerLog record, @Param("example") ShopMqConsumerLogExample example);

    int updateByPrimaryKeySelective(ShopMqConsumerLog record);

    int updateByPrimaryKey(ShopMqConsumerLog record);
}