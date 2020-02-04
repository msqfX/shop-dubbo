package com.dlion.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dlion.shop.api.IOrderService;
import com.dlion.shop.entity.Result;
import com.dlion.shop.pojo.ShopOrder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单接口
 *
 * @author lzy
 * @date 2020/2/4
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private IOrderService orderService;

    /**
     * 订单确认
     *
     * @param order
     * @return
     */
    @PostMapping("/confirmOrder")
    public Object confirmOrder(@RequestBody ShopOrder order) {
        Result result = orderService.confirmOrder(order);
        return result;
    }

}
