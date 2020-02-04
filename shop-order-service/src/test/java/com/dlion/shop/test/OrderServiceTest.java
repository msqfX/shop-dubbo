package com.dlion.shop.test;

import com.dlion.shop.OrderServiceApplication;
import com.dlion.shop.api.IOrderService;
import com.dlion.shop.entity.Result;
import com.dlion.shop.pojo.ShopOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

/**
 * @author lzy
 * @date 2020/2/3
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderServiceApplication.class)
public class OrderServiceTest {

    @Autowired
    private IOrderService orderService;

    @Test
    public void confirmOrder(){
        Long couponId = 1223971539704221725L;
        Long userId = 1223971539704221723L;
        Long goodsId = 1223971539704221724L;

        ShopOrder order = new ShopOrder();
        order.setCouponId(couponId);
        order.setGoodsId(goodsId);
        order.setUserId(userId);
        order.setAddress("北京市丰台区");
        order.setGoodsNumber(1);
        order.setGoodsPrice(new BigDecimal(1000));
        order.setOrderAmount(new BigDecimal(1000));
        order.setMoneyPaid(new BigDecimal(100));
        order.setPayMount(new BigDecimal(1000));
        order.setShippingFee(BigDecimal.ZERO);

        Result result = orderService.confirmOrder(order);
        System.out.println("下单结果："+result);

    }

}
