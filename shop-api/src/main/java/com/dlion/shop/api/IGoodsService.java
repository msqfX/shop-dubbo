package com.dlion.shop.api;

import com.dlion.shop.entity.Result;
import com.dlion.shop.pojo.ShopGoods;
import com.dlion.shop.pojo.ShopGoodsNumberLog;

/**
 * 商品服务
 */
public interface IGoodsService {

    /**
     * 根据ID查询商品
     * @param goodsId
     * @return
     */
    ShopGoods findOne(Long goodsId);

    /**
     * 扣减库存
     *
     * @param goodsNumberLog
     * @return
     */
    Result reduceGoodsNum(ShopGoodsNumberLog goodsNumberLog);
}
