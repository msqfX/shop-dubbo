package com.dlion.shop.mq;

import com.alibaba.fastjson.JSON;
import com.dlion.shop.api.IUserService;
import com.dlion.shop.constant.ShopCode;
import com.dlion.shop.entity.MQEntity;
import com.dlion.shop.pojo.ShopUserMoneyLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
    private IUserService userService;

    @Override
    public void onMessage(MessageExt message) {

        try {
            String body = new String(message.getBody(), "UtF-8");
            MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
            log.info("接收到消息：{}", mqEntity);

            if (Objects.nonNull(mqEntity.getUserMoney()) && mqEntity.getUserMoney().compareTo(BigDecimal.ZERO) > 0) {
                ShopUserMoneyLog userMoneyLog = new ShopUserMoneyLog();
                userMoneyLog.setUseMoney(mqEntity.getUserMoney());
                userMoneyLog.setUserId(mqEntity.getUserId());
                userMoneyLog.setOrderId(mqEntity.getOrderId());
                userMoneyLog.setMoneyLogType(ShopCode.SHOP_USER_MONEY_REFUND.getCode());

                userService.updateMoneyPaid(userMoneyLog);

                log.info("回退余额成功");
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.info("回退余额失败");
        }


    }
}
