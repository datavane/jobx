package com.jobxhub.common.job;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public enum RpcType {

    SYNC("同步RPC调用"), ASYNC("异步RPC调用"), ONE_WAY("单向调用");

    private String desc;

    RpcType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
