package com.dlion.shop.mq;

import com.alibaba.fastjson.JSON;
import com.dlion.shop.constant.ShopCode;
import com.dlion.shop.entity.MQEntity;
import com.dlion.shop.mapper.ShopCouponMapper;
import com.dlion.shop.pojo.ShopCoupon;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 订单确认失败监听器
 *
 * @author lzy
 * @date 2020/2/3
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "${mq.order.topic}",
        consumerGroup = "${mq.order.consumer.group.name}",
        messageModel = MessageModel.BROADCASTING)
public class OrderCancelListener implements RocketMQListener<MessageExt> {

    @Value("${mq.order.consumer.group.name}")
    private String groupName;

    @Value("${mq.order.topic}")
    private String topic;

    @Autowired
    private ShopCouponMapper couponMapper;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            String body = new String(messageExt.getBody(), StandardCharsets.UTF_8);
            MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
            log.info("接收到消息：{}", mqEntity);

            if(Objects.nonNull(mqEntity.getCouponId())){
                ShopCoupon coupon = couponMapper.selectByPrimaryKey(mqEntity.getCouponId());
                coupon.setUsedTime(null);
                coupon.setIsUsed(ShopCode.SHOP_COUPON_UNUSED.getCode());
                coupon.setOrderId(null);
                couponMapper.updateByPrimaryKey(coupon);

                log.info("回退优惠卷成功");
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.info("回退优惠卷失败");
        }
    }
}
