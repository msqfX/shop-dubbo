package com.dlion.shop.constant;

/**
 * @author lzy
 * @date 2020/2/2
 */
public enum ShopCode {

    SHOP_SUCCESS(true, 1, "正确"),

    SHOP_FAIL(false, 0, "错误"),

    SHOP_REQUEST_PARAMS_VALID(false, 4, "参数异常"),

    SHOP_GOODS_NO_EXIST(false, 5, "商品不存在"),

    SHOP_USER_NO_EXIST(false, 6, "用户不存在"),

    SHOP_ORDERAMOUNT_INVALID(false, 7, "订单金额不正确"),

    SHOP_GOODS_NUM_NOT_ENOUGH(false, 8, "商品库存不足"),

    SHOP_USER_MONEY_PAID(true, 1, "付款"),

    SHOP_USER_MONEY_REFUND(true, 2, "退款"),

    SHOP_ORDER_NO_CONFIRM(false, 0, "订单未确认"),

    SHOP_ORDER_CONFIRM(true, 0, "订单已确认"),

    SHOP_ORDER_REFUND(false, 2, "退款"),

    SHOP_ORDER_CANCEL(false, 2, "订单已取消"),

    SHOP_ORDER_INVALID(false, 3, "订单无效"),

    SHOP_ORDER_RETURNED(false, 4, "订单已退货"),

    SHOP_ORDER_PAY_STATUS_NO_PAY(true, 2, "订单未付款"),

    SHOP_ORDER_PAY_STATUS_PAYING(true, 1, "订单正在付款"),

    SHOP_ORDER_PAY_STATUS_IS_PAY(true, 2, "订单已付款"),

    SHOP_MQ_MESSAGE_STATUS_PROCESSING(true, 0, "消息正在处理"),

    SHOP_ORDER_SHIPPINGFEE_INVALID(false, 0, "运费不正确"),

    SHOP_MONEY_PAID_LESS_ZERO(false, 1, "余额小于零"),

    SHOP_MONEY_PAID_INVALID(false, 1, "余额非法"),

    SHOP_COUPON_NO_EXIST(false, 1, "优惠卷不存在"),

    SHOP_COUPON_IDUSED(false, 1, "优惠卷已经使用"),

    SHOP_COUPON_UNUSED(false, 0, "优惠卷未使用"),

    SHOP_REDUCE_GOODS_NUM_FAIL(false, 1, "扣减库存失败"),

    SHOP_COUPON_USE_FAIL(false, 1, "优惠卷状态更新失败"),

    SHOP_USER_MONEY_REDUCE_FAIL(false, 1, "扣减余额失败"),

    SHOP_USER_MONEY_REFUND_ALREADY(false, 1, "订单已经退款"),

    SHOP_ORDER_CONFIRM_FAIL(false, 2, "订单确认失败"),

    SHOP_MQ_MESSAGE_STATUS_SUCCESS(false, 1, "消息处理成功"),

    SHOP_MQ_MESSAGE_STATUS_FAIL(false, 2, "消息处理失败"),

    SHOP_ORDER_MESSAGE_STATUS_CANCEL(true, 1, "取消订单"),

    SHOP_ORDER_PAY_STATUS_PAY_ERROR(false, 1, "支付失败"),

    SHOP_PAYMENT_NOT_FOUND(false, 1, "订单未找到");


    Boolean success;
    Integer code;
    String message;

    ShopCode(Boolean success,
             Integer code,
             String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
