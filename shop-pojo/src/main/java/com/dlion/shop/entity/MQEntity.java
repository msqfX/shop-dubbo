package com.dlion.shop.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author lzy
 * @date 2020/2/3
 */
@Data
public class MQEntity implements Serializable {

    private Long orderId;

    private Long couponId;

    private Long userId;

    private BigDecimal userMoney;

    private Long goodsId;

    private Integer goodsNum;

}
