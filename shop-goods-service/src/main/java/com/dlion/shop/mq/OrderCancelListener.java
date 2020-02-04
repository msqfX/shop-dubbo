package com.dlion.shop.mq;

import com.alibaba.fastjson.JSON;
import com.dlion.shop.constant.ShopCode;
import com.dlion.shop.entity.MQEntity;
import com.dlion.shop.mapper.ShopGoodsMapper;
import com.dlion.shop.mapper.ShopGoodsNumberLogMapper;
import com.dlion.shop.mapper.ShopMqConsumerLogMapper;
import com.dlion.shop.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
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
    private ShopMqConsumerLogMapper shopMqConsumerLogMapper;

    @Autowired
    private ShopGoodsMapper goodsMapper;

    @Autowired
    private ShopGoodsNumberLogMapper goodsNumberLogMapper;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            String msgId = messageExt.getMsgId();
            String tags = messageExt.getTags();
            String keys = messageExt.getKeys();
            String body = new String(messageExt.getBody(), StandardCharsets.UTF_8);
            MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
            log.info("接收到消息：{}", mqEntity);

            //查询消费记录
            ShopMqConsumerLogKey shopMqConsumerLogKey = new ShopMqConsumerLogKey();
            shopMqConsumerLogKey.setGroupName(groupName);
            shopMqConsumerLogKey.setMsgTag(tags);
            shopMqConsumerLogKey.setMsgTopic(topic);
            ShopMqConsumerLog shopMqConsumerLog = shopMqConsumerLogMapper.selectByPrimaryKey(shopMqConsumerLogKey);
            if (!Objects.isNull(shopMqConsumerLog)) {
                //已经消费过
                Integer status = shopMqConsumerLog.getConsumerStatus();
                //处理过-返回
                if (status.intValue() == ShopCode.SHOP_MQ_MESSAGE_STATUS_SUCCESS.getCode().intValue()) {
                    log.info("消息已经处理过：{}", msgId);
                    return;
                }
                //处理成功-返回
                if (status.intValue() == ShopCode.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode().intValue()) {
                    log.info("消息正在处理：{}", msgId);
                    return;
                }
                //处理失败--
                if (status.intValue() == ShopCode.SHOP_MQ_MESSAGE_STATUS_FAIL.getCode().intValue()) {
                    //获得消息处理次数
                    int times = shopMqConsumerLog.getConsumerTimes();
                    if (times > 3) {
                        log.info("消息已经处理了三次了，不能在处理了，{}", msgId);
                        return;
                    }

                    shopMqConsumerLog.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode());

                    ShopMqConsumerLogExample mqConsumerLogExample = new ShopMqConsumerLogExample();
                    ShopMqConsumerLogExample.Criteria criteria = mqConsumerLogExample.createCriteria();
                    criteria.andMsgTagEqualTo(shopMqConsumerLog.getMsgTag());
                    criteria.andMsgKeyEqualTo(shopMqConsumerLog.getMsgKey());
                    criteria.andGroupNameEqualTo(groupName);
                    criteria.andConsumerTimesEqualTo(shopMqConsumerLog.getConsumerTimes());
                    int r = shopMqConsumerLogMapper.updateByExampleSelective(shopMqConsumerLog, mqConsumerLogExample);
                    if (r <= 0) {
                        log.info("并发处理，稍后处理");
                        return;
                    }

                }
            } else {
                //没有被消费过
                shopMqConsumerLog = new ShopMqConsumerLog();
                shopMqConsumerLog.setMsgKey(keys);
                shopMqConsumerLog.setMsgTag(tags);
                shopMqConsumerLog.setMsgTopic(topic);
                shopMqConsumerLog.setGroupName(groupName);
                shopMqConsumerLog.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode());
                shopMqConsumerLog.setMsgBody(body);
                shopMqConsumerLog.setMsgId(msgId);
                shopMqConsumerLog.setConsumerTimes(0);

                shopMqConsumerLogMapper.insertSelective(shopMqConsumerLog);
            }

            //回退库存
            Long goodsId = mqEntity.getGoodsId();

            ShopGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            goods.setGoodsNumber(goods.getGoodsNumber() + mqEntity.getGoodsNum());
            goodsMapper.updateByPrimaryKey(goods);

            //记录库存日志
            ShopGoodsNumberLog goodsNumberLog = new ShopGoodsNumberLog();
            goodsNumberLog.setLogTime(new Date());
            goodsNumberLog.setGoodsNumber(+mqEntity.getGoodsNum());
            goodsNumberLog.setGoodsId(goodsId);
            goodsNumberLog.setOrderId(mqEntity.getOrderId());
            goodsNumberLogMapper.insertSelective(goodsNumberLog);

            //修改消息处理结果
            shopMqConsumerLog.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_SUCCESS.getCode());
            shopMqConsumerLog.setConsumerTimestamp(new Date());
            shopMqConsumerLogMapper.insertSelective(shopMqConsumerLog);

            log.info("回退库存成功");

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
