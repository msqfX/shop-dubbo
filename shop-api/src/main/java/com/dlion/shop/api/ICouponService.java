package com.dlion.shop.api;

import com.dlion.shop.entity.Result;
import com.dlion.shop.pojo.ShopCoupon;

/**
 * 优惠卷服务
 *
 * @author lzy
 * @date 2020/2/2
 */
public interface ICouponService {

    ShopCoupon findOne(Long couponId);

    /**
     * 更新优惠卷状态
     *
     * @param coupon
     * @return
     */
    Result updateCouponStatus(ShopCoupon coupon);
}
