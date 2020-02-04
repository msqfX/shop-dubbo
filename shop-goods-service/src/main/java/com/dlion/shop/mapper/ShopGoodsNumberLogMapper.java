package com.dlion.shop.mapper;

import com.dlion.shop.pojo.ShopGoodsNumberLog;
import com.dlion.shop.pojo.ShopGoodsNumberLogExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ShopGoodsNumberLogMapper {
    long countByExample(ShopGoodsNumberLogExample example);

    int deleteByExample(ShopGoodsNumberLogExample example);

    int insert(ShopGoodsNumberLog record);

    int insertSelective(ShopGoodsNumberLog record);

    List<ShopGoodsNumberLog> selectByExample(ShopGoodsNumberLogExample example);

    int updateByExampleSelective(@Param("record") ShopGoodsNumberLog record, @Param("example") ShopGoodsNumberLogExample example);

    int updateByExample(@Param("record") ShopGoodsNumberLog record, @Param("example") ShopGoodsNumberLogExample example);
}