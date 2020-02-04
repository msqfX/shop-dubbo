package com.dlion.shop.mq;

import com.alibaba.fastjson.JSON;
import com.dlion.shop.constant.ShopCode;
import com.dlion.shop.mapper.ShopOrderMapper;
import com.dlion.shop.pojo.ShopOrder;
import com.dlion.shop.pojo.ShopPay;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author lzy
 * @date 2020/2/3
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "${mq.pay.topic}",
        consumerGroup = "${mq.pay.consumer.group.name}",
        messageModel = MessageModel.BROADCASTING)
public class PaymentListener implements RocketMQListener<MessageExt> {

    @Autowired
    private ShopOrderMapper orderMapper;

    @Override
    public void onMessage(MessageExt message) {

        try {
            String body = new String(message.getBody(), "UTF-8");
            ShopPay pay = JSON.parseObject(body, ShopPay.class);

            log.info("收到支付消息：{}", body);

            if (Objects.nonNull(pay.getOrderId())) {
                ShopOrder order = orderMapper.selectByPrimaryKey(pay.getOrderId());
                order.setOrderStatus(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());

                orderMapper.updateByPrimaryKey(order);

                log.info("订单支付成功");
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.info("订单取消失败");
        }


    }
}
