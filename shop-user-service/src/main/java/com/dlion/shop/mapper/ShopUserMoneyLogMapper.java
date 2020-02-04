package com.dlion.shop.mapper;

import com.dlion.shop.pojo.ShopUserMoneyLog;
import com.dlion.shop.pojo.ShopUserMoneyLogExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ShopUserMoneyLogMapper {
    long countByExample(ShopUserMoneyLogExample example);

    int deleteByExample(ShopUserMoneyLogExample example);

    int insert(ShopUserMoneyLog record);

    int insertSelective(ShopUserMoneyLog record);

    List<ShopUserMoneyLog> selectByExample(ShopUserMoneyLogExample example);

    int updateByExampleSelective(@Param("record") ShopUserMoneyLog record, @Param("example") ShopUserMoneyLogExample example);

    int updateByExample(@Param("record") ShopUserMoneyLog record, @Param("example") ShopUserMoneyLogExample example);
}