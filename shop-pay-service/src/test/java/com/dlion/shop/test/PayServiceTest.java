package com.dlion.shop.test;

import com.dlion.shop.PayServiceApplication;
import com.dlion.shop.api.IPayService;
import com.dlion.shop.constant.ShopCode;
import com.dlion.shop.entity.Result;
import com.dlion.shop.pojo.ShopPay;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author lzy
 * @date 2020/2/3
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PayServiceApplication.class)
public class PayServiceTest {

    @Autowired
    private IPayService payService;

    @Test
    public void createPayment() throws IOException {
        Long orderId = 1224312214995996672L;
        ShopPay pay = new ShopPay();
        pay.setOrderId(orderId);
        pay.setPayAmount(new BigDecimal(880));
        Result payment = payService.createPayment(pay);
        System.out.println("支付结果：" + payment);

        System.in.read();
    }

    @Test
    public void payCallBack() throws IOException {
        Long orderId = 1224312214995996672L;
        ShopPay pay = new ShopPay();
        pay.setOrderId(orderId);
        pay.setPayId(1224529103877705728L);
        pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
        payService.payCallBack(pay);

        System.in.read();

    }

}
