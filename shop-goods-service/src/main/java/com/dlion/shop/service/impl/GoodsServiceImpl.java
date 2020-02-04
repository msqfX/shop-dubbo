package com.dlion.shop.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dlion.shop.api.IGoodsService;
import com.dlion.shop.constant.ShopCode;
import com.dlion.shop.entity.Result;
import com.dlion.shop.execption.CastException;
import com.dlion.shop.mapper.ShopGoodsMapper;
import com.dlion.shop.mapper.ShopGoodsNumberLogMapper;
import com.dlion.shop.pojo.ShopGoods;
import com.dlion.shop.pojo.ShopGoodsNumberLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * @author lzy
 * @date 2020/2/2
 */
@Component
@Service(interfaceClass = IGoodsService.class)
public class GoodsServiceImpl implements IGoodsService {

    @Autowired
    private ShopGoodsMapper goodsMapper;

    @Autowired
    private ShopGoodsNumberLogMapper shopGoodsNumberLogMapper;

    @Override
    public ShopGoods findOne(Long goodsId) {
        if (Objects.isNull(goodsId)) {
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMS_VALID);
        }
        return goodsMapper.selectByPrimaryKey(goodsId);
    }

    @Override
    public Result reduceGoodsNum(ShopGoodsNumberLog goodsNumberLog) {

        if (Objects.isNull(goodsNumberLog) || Objects.isNull(goodsNumberLog.getGoodsId()) || Objects.isNull(goodsNumberLog.getGoodsNumber())
                || Objects.isNull(goodsNumberLog.getOrderId()) || goodsNumberLog.getGoodsNumber().intValue() <= 0) {
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMS_VALID);
        }

        ShopGoods goods = goodsMapper.selectByPrimaryKey(goodsNumberLog.getGoodsId());
        if(goods.getGoodsNumber() < goodsNumberLog.getGoodsNumber()){
            CastException.cast(ShopCode.SHOP_GOODS_NUM_NOT_ENOUGH);
        }

        goods.setGoodsNumber(goods.getGoodsNumber()-goodsNumberLog.getGoodsNumber());

        goodsMapper.updateByPrimaryKey(goods);

        //记录库存日志
        goodsNumberLog.setGoodsNumber(-goodsNumberLog.getGoodsNumber());
        goodsNumberLog.setLogTime(new Date());
        shopGoodsNumberLogMapper.insertSelective(goodsNumberLog);

        return new Result(ShopCode.SHOP_SUCCESS.getSuccess(), ShopCode.SHOP_SUCCESS.getMessage());
    }
}
