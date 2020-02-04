package com.dlion.shop.api;

import com.dlion.shop.entity.Result;
import com.dlion.shop.pojo.ShopOrder;

/**
 * 订单服务
 */
public interface IOrderService {

    /**
     * 确认订单
     *
     * @param shopOrder
     * @return
     */
    Result confirmOrder(ShopOrder shopOrder);
}
