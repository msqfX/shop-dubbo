package com.dlion.shop.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.dlion.shop.api.IPayService;
import com.dlion.shop.constant.ShopCode;
import com.dlion.shop.entity.Result;
import com.dlion.shop.execption.CastException;
import com.dlion.shop.mapper.ShopMqProducerLogMapper;
import com.dlion.shop.mapper.ShopPayMapper;
import com.dlion.shop.pojo.ShopMqProducerLog;
import com.dlion.shop.pojo.ShopPay;
import com.dlion.shop.pojo.ShopPayExample;
import com.dlion.shop.utils.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * @author lzy
 * @date 2020/2/3
 */
@Slf4j
@Component
@Service(interfaceClass = IPayService.class)
public class PayServiceImpl implements IPayService {

    @Autowired
    private ShopPayMapper payMapper;

    @Autowired
    private ShopMqProducerLogMapper mqProducerLogMapper;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Value("${rocketmq.producer.group}")
    private String group;

    @Value("${mq.topic}")
    private String topic;

    @Value("${mq.pay.tag}")
    private String tag;

    @Override
    public Result createPayment(ShopPay pay) {

        if (Objects.isNull(pay) || Objects.isNull(pay.getOrderId())) {
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMS_VALID);
        }

        ShopPayExample payExample = new ShopPayExample();
        ShopPayExample.Criteria criteria = payExample.createCriteria();
        criteria.andOrderIdEqualTo(pay.getOrderId());
        criteria.andIsPaidEqualTo(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode());
        long r = payMapper.countByExample(payExample);
        if (r > 0) {
            CastException.cast(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY);
        }

        pay.setPayId(idWorker.nextId());
        pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode());

        payMapper.insert(pay);

        return new Result(ShopCode.SHOP_SUCCESS.getSuccess(), ShopCode.SHOP_SUCCESS.getMessage());
    }

    @Override
    public Result payCallBack(ShopPay pay) {

        if (pay.getIsPaid().intValue() == ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode().intValue()) {
            ShopPay shopPay = payMapper.selectByPrimaryKey(pay.getPayId());
            if (Objects.isNull(shopPay)) {
                CastException.cast(ShopCode.SHOP_PAYMENT_NOT_FOUND);
            }

            pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
            payMapper.updateByPrimaryKey(pay);

            ShopMqProducerLog mqProducerLog = new ShopMqProducerLog();
            mqProducerLog.setId(String.valueOf(idWorker.nextId()));
            mqProducerLog.setGroupName(group);
            mqProducerLog.setMsgTag(tag);
            mqProducerLog.setMsgKey(pay.getPayId().toString());
            mqProducerLog.setCreateTime(new Date());
            mqProducerLog.setMsgTopic(topic);
            mqProducerLog.setMsgBody(JSON.toJSONString(pay));

            mqProducerLogMapper.insertSelective(mqProducerLog);

            threadPoolTaskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    //发送支付消息
                    try {
                        SendResult sendResult = sendPayMessage(topic, tag, pay.getPayId(), JSON.toJSONString(pay));
                        if (sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
                            mqProducerLogMapper.deleteByPrimaryKey(mqProducerLog.getId());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            return new Result(ShopCode.SHOP_SUCCESS.getSuccess(), ShopCode.SHOP_SUCCESS.getMessage());
        } else {
            CastException.cast(ShopCode.SHOP_ORDER_PAY_STATUS_PAY_ERROR);
            return new Result(ShopCode.SHOP_FAIL.getSuccess(), ShopCode.SHOP_FAIL.getMessage());
        }
    }

    /**
     * 发送支付成功消息
     *
     * @param topic
     * @param tag
     * @param key
     * @param body
     */
    private SendResult sendPayMessage(String topic, String tag, Long key, String body) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        Message message = new Message();
        message.setTopic(topic);
        message.setTags(tag);
        message.setKeys(key.toString());
        message.setBody(body.getBytes());

        SendResult result = rocketMQTemplate.getProducer().send(message);

        return result;
    }
}
