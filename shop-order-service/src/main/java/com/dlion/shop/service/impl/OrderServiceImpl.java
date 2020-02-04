package com.dlion.shop.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.dlion.shop.api.ICouponService;
import com.dlion.shop.api.IGoodsService;
import com.dlion.shop.api.IOrderService;
import com.dlion.shop.api.IUserService;
import com.dlion.shop.constant.ShopCode;
import com.dlion.shop.entity.MQEntity;
import com.dlion.shop.entity.Result;
import com.dlion.shop.execption.CastException;
import com.dlion.shop.mapper.ShopOrderMapper;
import com.dlion.shop.pojo.*;
import com.dlion.shop.utils.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * @author lzy
 * @date 2020/2/2
 */
@Slf4j
@Component
@Service(interfaceClass = IOrderService.class)
public class OrderServiceImpl implements IOrderService {

    @Reference
    private IGoodsService goodsService;

    @Reference
    private IUserService userService;

    @Reference
    private ICouponService couponService;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private ShopOrderMapper orderMapper;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value("${mq.order.topic}")
    private String topic;

    @Value("${mq.order.tag.cancel}")
    private String tag;

    @Override
    public Result confirmOrder(ShopOrder order) {

        //校验订单
        checkOrder(order);
        //生成预订单
        Long orderId = savePreOrder(order);

        try {
            //扣减库存
            reduceGoodsNum(order);

            //更改优惠卷状态
            updateCouponStatus(order);

            //扣减余额
            reduceMoneyPaid(order);

            CastException.cast(ShopCode.SHOP_FAIL);

            //确认订单
            updateOrderStatus(order);

            return new Result(ShopCode.SHOP_SUCCESS.getSuccess(), ShopCode.SHOP_SUCCESS.getMessage());
        } catch (Exception e) {
            //确认订单失败,发送消息
            MQEntity mqEntity = new MQEntity();
            mqEntity.setOrderId(orderId);
            mqEntity.setCouponId(order.getCouponId());
            mqEntity.setGoodsId(order.getGoodsId());
            mqEntity.setUserId(order.getUserId());
            mqEntity.setUserMoney(order.getMoneyPaid());
            mqEntity.setGoodsNum(order.getGoodsNumber());

            try {
                sendCancelOrder(topic, tag, orderId.toString(), JSON.toJSONString(mqEntity));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            System.out.println("出现了异常！"+e);
            //返回失败状态
            return new Result(ShopCode.SHOP_FAIL.getSuccess(), ShopCode.SHOP_FAIL.getMessage());
        }
    }

    /**
     * 发送订单确认失败消息
     *
     * @param topic
     * @param tag
     * @param key
     * @param body
     */
    private void sendCancelOrder(String topic, String tag, String key, String body) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        Message message = new Message(topic, tag, key, body.getBytes());
        rocketMQTemplate.getProducer().send(message);

    }

    /**
     * 确认订单
     *
     * @param order
     */
    private void updateOrderStatus(ShopOrder order) {
        order.setOrderStatus(ShopCode.SHOP_ORDER_CONFIRM.getCode());
        order.setPayStatus(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode());
        order.setConfirmTime(new Date());
        int r = orderMapper.updateByPrimaryKey(order);
        if (r <= 0) {
            CastException.cast(ShopCode.SHOP_ORDER_CONFIRM_FAIL);
        }

        log.info("订单：{}，确认成功", order.getOrderId());
    }

    /**
     * 扣减余额
     *
     * @param order
     */
    private void reduceMoneyPaid(ShopOrder order) {
        if (order.getMoneyPaid() != null && order.getMoneyPaid().compareTo(BigDecimal.ZERO) == 1) {
            ShopUserMoneyLog userMoneyLog = new ShopUserMoneyLog();
            userMoneyLog.setOrderId(order.getOrderId());
            userMoneyLog.setUserId(order.getUserId());
            userMoneyLog.setUseMoney(order.getMoneyPaid());
            userMoneyLog.setMoneyLogType(ShopCode.SHOP_USER_MONEY_PAID.getCode());

            Result result = userService.updateMoneyPaid(userMoneyLog);

            if (result.getSuccess().equals(ShopCode.SHOP_FAIL.getSuccess())) {
                CastException.cast(ShopCode.SHOP_USER_MONEY_REDUCE_FAIL);
                log.info("订单：{}，扣减余额失败", order.getOrderId());
            }
        }
        log.info("订单：{}，扣减余额成功", order.getOrderId());

    }

    /**
     * 使用优惠卷
     *
     * @param order
     */
    private void updateCouponStatus(ShopOrder order) {
        if (!Objects.isNull(order.getCouponId())) {
            ShopCoupon coupon = couponService.findOne(order.getCouponId());

            coupon.setOrderId(order.getOrderId());
            coupon.setIsUsed(ShopCode.SHOP_COUPON_IDUSED.getCode());
            coupon.setUsedTime(new Date());
            coupon.setOrderId(order.getOrderId());

            //更新优惠卷状态
            Result result = couponService.updateCouponStatus(coupon);
            if (result.getSuccess().equals(ShopCode.SHOP_FAIL.getSuccess())) {
                CastException.cast(ShopCode.SHOP_COUPON_USE_FAIL);
                log.info("订单：{}，使用优惠卷失败", order.getOrderId());
            }
        }
        log.info("订单：{}，使用优惠卷成功", order.getOrderId());

    }

    /**
     * 扣减库存
     *
     * @param order
     */
    private void reduceGoodsNum(ShopOrder order) {

        ShopGoodsNumberLog goodsNumberLog = new ShopGoodsNumberLog();
        goodsNumberLog.setOrderId(order.getOrderId());
        goodsNumberLog.setGoodsId(order.getGoodsId());
        goodsNumberLog.setGoodsNumber(order.getGoodsNumber());

        Result result = goodsService.reduceGoodsNum(goodsNumberLog);
        if (result.getSuccess().equals(ShopCode.SHOP_FAIL.getSuccess())) {
            CastException.cast(ShopCode.SHOP_REDUCE_GOODS_NUM_FAIL);
            log.error("订单：{}，扣减库存失败", order.getOrderId());
        }

        log.info("订单：{}，扣减库存成功", order.getOrderId());
    }

    /**
     * 校验订单
     *
     * @param order
     */
    private void checkOrder(ShopOrder order) {
        if (Objects.isNull(order)) {
            CastException.cast(ShopCode.SHOP_ORDER_INVALID);
        }

        ShopGoods goods = goodsService.findOne(order.getGoodsId());
        if (Objects.isNull(goods)) {
            CastException.cast(ShopCode.SHOP_GOODS_NO_EXIST);
        }

        ShopUser user = userService.findOne(order.getUserId());
        if (Objects.isNull(user)) {
            CastException.cast(ShopCode.SHOP_USER_NO_EXIST);
        }

        if (order.getPayMount().compareTo(goods.getGoodsPrice().multiply(new BigDecimal(order.getGoodsNumber()))) != 0) {
            CastException.cast(ShopCode.SHOP_ORDERAMOUNT_INVALID);
        }

        if (order.getGoodsNumber() >= goods.getGoodsNumber()) {
            CastException.cast(ShopCode.SHOP_GOODS_NUM_NOT_ENOUGH);
        }

        log.info("订单校验通过");
    }

    private Long savePreOrder(ShopOrder order) {

        order.setOrderStatus(ShopCode.SHOP_ORDER_NO_CONFIRM.getCode());
        long orderId = idWorker.nextId();
        order.setOrderId(orderId);

        BigDecimal shippingFee = calculateShippingFee(order.getOrderAmount());
        if (order.getShippingFee().compareTo(shippingFee) != 0) {
            CastException.cast(ShopCode.SHOP_ORDER_SHIPPINGFEE_INVALID);
        }

        BigDecimal orderAmount = order.getGoodsPrice().multiply(new BigDecimal(order.getGoodsNumber()));
        orderAmount.add(shippingFee);
        if (order.getOrderAmount().compareTo(orderAmount) != 0) {
            CastException.cast(ShopCode.SHOP_ORDERAMOUNT_INVALID);
        }

        //检查用户余额
        BigDecimal moneyPaid = order.getMoneyPaid();
        if (moneyPaid != null) {
            int r = moneyPaid.compareTo(BigDecimal.ZERO);
            if (r == -1) {
                CastException.cast(ShopCode.SHOP_MONEY_PAID_LESS_ZERO);
            }
            if (r == 1) {
                ShopUser user = userService.findOne(order.getUserId());
                if (moneyPaid.compareTo(new BigDecimal(user.getUserMoney())) == 1) {
                    CastException.cast(ShopCode.SHOP_MONEY_PAID_INVALID);
                }
            }
        } else {
            order.setMoneyPaid(BigDecimal.ZERO);
        }

        //优惠卷
        Long couponId = order.getCouponId();
        if (!Objects.isNull(couponId)) {
            ShopCoupon coupon = couponService.findOne(couponId);
            if (Objects.isNull(coupon)) {
                CastException.cast(ShopCode.SHOP_COUPON_NO_EXIST);
            }
            if (coupon.getIsUsed().intValue() == ShopCode.SHOP_COUPON_IDUSED.getCode().intValue()) {
                CastException.cast(ShopCode.SHOP_COUPON_IDUSED);
            }
            order.setCouponPaid(coupon.getCouponPrice());

        } else {
            order.setCouponPaid(BigDecimal.ZERO);
        }

        //核算金额 订单金额-余额-优惠卷
        BigDecimal payAmount = order.getOrderAmount().subtract(order.getMoneyPaid()).subtract(order.getCouponPaid());
        order.setPayMount(payAmount);

        //设置下单时间
        order.setAddTime(new Date());

        orderMapper.insertSelective(order);

        return orderId;
    }

    private BigDecimal calculateShippingFee(BigDecimal orderAmount) {
        if (orderAmount.compareTo(new BigDecimal(100)) == 1) {
            return BigDecimal.ZERO;
        } else {
            return new BigDecimal(10);
        }
    }
}
