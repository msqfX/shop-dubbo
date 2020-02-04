package com.dlion.shop.execption;

import com.dlion.shop.constant.ShopCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lzy
 * @date 2020/2/2
 */
@Slf4j
public class CastException {
    public static void cast(ShopCode shopCode){
        log.error(shopCode.toString());
        throw new CustomerExecption(shopCode);
    }
}
