package com.dlion.shop.api;

import com.dlion.shop.entity.Result;
import com.dlion.shop.pojo.ShopPay;

/**
 * 支付服务
 */
public interface IPayService {

    /**
     * 创建支付订单
     *
     * @param pay
     * @return
     */
    Result createPayment(ShopPay pay);

    /**
     * 支付回调
     *
     * @param pay
     * @return
     */
    Result payCallBack(ShopPay pay);
}
