package com.dlion.shop.pojo;

import java.io.Serializable;

public class ShopMqConsumerLogKey implements Serializable {
    private String groupName;

    private String msgTopic;

    private String msgTag;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName == null ? null : groupName.trim();
    }

    public String getMsgTopic() {
        return msgTopic;
    }

    public void setMsgTopic(String msgTopic) {
        this.msgTopic = msgTopic == null ? null : msgTopic.trim();
    }

    public String getMsgTag() {
        return msgTag;
    }

    public void setMsgTag(String msgTag) {
        this.msgTag = msgTag == null ? null : msgTag.trim();
    }
}