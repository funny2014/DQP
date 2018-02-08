package com.dqp.executor.enums;

/**
 * 查询结果集返回类型
 */
public enum ResponseType {

    /**
     * json直接返回
     */
    RPC, /**
     * 发送kafka
     */
    KAFKA, /**
     * 存redis
     */
    REDIS
}
