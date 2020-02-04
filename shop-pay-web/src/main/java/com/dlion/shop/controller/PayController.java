package com.dlion.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dlion.shop.api.IPayService;
import com.dlion.shop.entity.Result;
import com.dlion.shop.pojo.ShopPay;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 支付接口
 *
 * @author lzy
 * @date 2020/2/4
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private IPayService payService;

    @PostMapping("/createPayment")
    public Object createPayment(@RequestBody ShopPay pay) {
        Result result = payService.createPayment(pay);
        return result;
    }

    @RequestMapping("/payCallBack")
    public Object payCallBack(@RequestBody ShopPay pay) {
        Result result = payService.payCallBack(pay);
        return result;
    }
}
