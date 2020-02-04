package com.dlion.shop.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dlion.shop.api.ICouponService;
import com.dlion.shop.constant.ShopCode;
import com.dlion.shop.entity.Result;
import com.dlion.shop.execption.CastException;
import com.dlion.shop.mapper.ShopCouponMapper;
import com.dlion.shop.pojo.ShopCoupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 优惠卷服务
 *
 * @author lzy
 * @date 2020/2/2
 */
@Component
@Service(interfaceClass = ICouponService.class)
public class CouponServiceImpl implements ICouponService {

    @Autowired
    private ShopCouponMapper couponMapper;

    @Override
    public ShopCoupon findOne(Long couponId) {
        if(Objects.isNull(couponId)){
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMS_VALID);
        }
        return couponMapper.selectByPrimaryKey(couponId);
    }

    @Override
    public Result updateCouponStatus(ShopCoupon coupon) {
        if(Objects.isNull(coupon)){
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMS_VALID);
        }

        couponMapper.updateByPrimaryKey(coupon);

        return new Result(ShopCode.SHOP_SUCCESS.getSuccess(), ShopCode.SHOP_SUCCESS.getMessage());
    }
}
