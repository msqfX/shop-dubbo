package com.dlion.shop.api;

import com.dlion.shop.entity.Result;
import com.dlion.shop.pojo.ShopOrder;
import com.dlion.shop.pojo.ShopUser;
import com.dlion.shop.pojo.ShopUserMoneyLog;

public interface IUserService {

    ShopUser findOne(Long userId);

    /**
     * 更新余额
     * @param userMoneyLog
     * @return
     */
    Result updateMoneyPaid(ShopUserMoneyLog userMoneyLog);
}
