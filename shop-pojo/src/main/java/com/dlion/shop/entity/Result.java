package com.dlion.shop.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lzy
 * @date 2020/2/2
 */
@Data
public class Result implements Serializable {

    private Boolean success;

    private String message;

    public Result(Boolean success, String message){
        this.success = success;
        this.message = message;
    }

}
