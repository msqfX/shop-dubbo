package com.dlion.shop.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dlion.shop.api.IUserService;
import com.dlion.shop.constant.ShopCode;
import com.dlion.shop.entity.Result;
import com.dlion.shop.execption.CastException;
import com.dlion.shop.mapper.ShopUserMapper;
import com.dlion.shop.mapper.ShopUserMoneyLogMapper;
import com.dlion.shop.pojo.ShopUser;
import com.dlion.shop.pojo.ShopUserMoneyLog;
import com.dlion.shop.pojo.ShopUserMoneyLogExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * @author lzy
 * @date 2020/2/2
 */
@Component
@Service(interfaceClass = IUserService.class)
public class UserServiceImpl implements IUserService {

    @Autowired
    private ShopUserMapper userMapper;

    @Autowired
    private ShopUserMoneyLogMapper userMoneyLogMapper;

    @Override
    public ShopUser findOne(Long userId) {

        if (Objects.isNull(userId)) {
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMS_VALID);
        }
        return userMapper.selectByPrimaryKey(userId);
    }

    @Override
    public Result updateMoneyPaid(ShopUserMoneyLog userMoneyLog) {
        if (Objects.isNull(userMoneyLog)) {
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMS_VALID);
        }
        if (Objects.isNull(userMoneyLog.getOrderId()) ||
                Objects.isNull(userMoneyLog.getUserId()) ||
                Objects.isNull(userMoneyLog.getUseMoney()) ||
                userMoneyLog.getUseMoney().compareTo(BigDecimal.ZERO) <= 0) {
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMS_VALID);
        }

        ShopUserMoneyLogExample shopUserMoneyLogExample = new ShopUserMoneyLogExample();
        ShopUserMoneyLogExample.Criteria criteria = shopUserMoneyLogExample.createCriteria();
        criteria.andOrderIdEqualTo(userMoneyLog.getOrderId());
        criteria.andUserIdEqualTo(userMoneyLog.getUserId());
        long count = userMoneyLogMapper.countByExample(shopUserMoneyLogExample);

        ShopUser user = userMapper.selectByPrimaryKey(userMoneyLog.getUserId());
        //扣减余额
        if (userMoneyLog.getMoneyLogType().intValue() == ShopCode.SHOP_USER_MONEY_PAID.getCode()) {
            if (count > 0) {
                //已经付款
                CastException.cast(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY);
            }
            //扣减余额
            user.setUserMoney(new BigDecimal(user.getUserMoney()).subtract(userMoneyLog.getUseMoney()).longValue());
            userMapper.updateByPrimaryKey(user);
        }
        //回退余额
        if (userMoneyLog.getMoneyLogType().intValue() == ShopCode.SHOP_USER_MONEY_REFUND.getCode().intValue()) {
            if (count < 0) {
                CastException.cast(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY);
            }
            //防止多次退款
            ShopUserMoneyLogExample example = new ShopUserMoneyLogExample();
            ShopUserMoneyLogExample.Criteria criteria1 = example.createCriteria();
            criteria1.andOrderIdEqualTo(userMoneyLog.getOrderId());
            criteria1.andUserIdEqualTo(userMoneyLog.getUserId());
            criteria1.andMoneyLogTypeEqualTo(userMoneyLog.getMoneyLogType());
            long count1 = userMoneyLogMapper.countByExample(example);
            if (count1 > 0) {
                CastException.cast(ShopCode.SHOP_USER_MONEY_REFUND_ALREADY);
            }
            //退款
            user.setUserMoney(new BigDecimal(user.getUserMoney()).add(userMoneyLog.getUseMoney()).longValue());
            userMapper.updateByPrimaryKey(user);
        }

        //记录订单余额使用日志
        userMoneyLog.setCreateTime(new Date());
        userMoneyLogMapper.insertSelective(userMoneyLog);

        return new Result(ShopCode.SHOP_SUCCESS.getSuccess(), ShopCode.SHOP_SUCCESS.getMessage());
    }
}
