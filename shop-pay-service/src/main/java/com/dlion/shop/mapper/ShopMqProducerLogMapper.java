package com.dlion.shop.mapper;

import com.dlion.shop.pojo.ShopMqProducerLog;
import com.dlion.shop.pojo.ShopMqProducerLogExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ShopMqProducerLogMapper {
    long countByExample(ShopMqProducerLogExample example);

    int deleteByExample(ShopMqProducerLogExample example);

    int deleteByPrimaryKey(String id);

    int insert(ShopMqProducerLog record);

    int insertSelective(ShopMqProducerLog record);

    List<ShopMqProducerLog> selectByExample(ShopMqProducerLogExample example);

    ShopMqProducerLog selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ShopMqProducerLog record, @Param("example") ShopMqProducerLogExample example);

    int updateByExample(@Param("record") ShopMqProducerLog record, @Param("example") ShopMqProducerLogExample example);

    int updateByPrimaryKeySelective(ShopMqProducerLog record);

    int updateByPrimaryKey(ShopMqProducerLog record);
}