package com.dlion.shop.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ShopUserMoneyLog implements Serializable {
    private Long userId;

    private Long orderId;

    private Integer moneyLogType;

    private BigDecimal useMoney;

    private Date createTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getMoneyLogType() {
        return moneyLogType;
    }

    public void setMoneyLogType(Integer moneyLogType) {
        this.moneyLogType = moneyLogType;
    }

    public BigDecimal getUseMoney() {
        return useMoney;
    }

    public void setUseMoney(BigDecimal useMoney) {
        this.useMoney = useMoney;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}