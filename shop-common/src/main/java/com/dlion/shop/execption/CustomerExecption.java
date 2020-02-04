package com.dlion.shop.execption;

import com.dlion.shop.constant.ShopCode;

/**
 * @author lzy
 * @date 2020/2/2
 */
public class CustomerExecption extends RuntimeException {

    private ShopCode shopCode;

    public CustomerExecption(ShopCode shopCode){
        this.shopCode = shopCode;
    }

}
