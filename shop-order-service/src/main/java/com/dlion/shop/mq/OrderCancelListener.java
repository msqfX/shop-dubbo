package com.dlion.shop.mq;

import com.alibaba.fastjson.JSON;
import com.dlion.shop.constant.ShopCode;
import com.dlion.shop.entity.MQEntity;
import com.dlion.shop.mapper.ShopOrderMapper;
import com.dlion.shop.pojo.ShopOrder;
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
@RocketMQMessageListener(topic = "${mq.order.topic}",
        consumerGroup = "${mq.order.consumer.group.name}",
        messageModel = MessageModel.BROADCASTING)
public class OrderCancelListener implements RocketMQListener<MessageExt> {

    @Autowired
    private ShopOrderMapper orderMapper;

    @Override
    public void onMessage(MessageExt message) {

        try {
            String body = new String(message.getBody(), "UTF-8");
            MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);

            if (Objects.nonNull(mqEntity.getOrderId())) {
                ShopOrder order = orderMapper.selectByPrimaryKey(mqEntity.getOrderId());
                order.setOrderStatus(ShopCode.SHOP_ORDER_MESSAGE_STATUS_CANCEL.getCode());

                orderMapper.updateByPrimaryKey(order);

                log.info("订单取消成功");
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.info("订单取消失败");
        }


    }
}
