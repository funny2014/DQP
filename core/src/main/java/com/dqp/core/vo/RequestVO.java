package com.dqp.core.vo;


import lombok.*;

/**
 * The type Request vo.
 */
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RequestVO {

    /**
     * 查询的数据库引擎
     */
    private String dbEngine;
    /**
     * 请求头，客户端如果是用spring cloud编写的kafka则需要此字段，原生kafka不需要此字段,直接传空串或者null
     */
    private String header;
    /**
     * 请求时间
     */
    private long requestTime;
    /**
     * 发起请求的用户
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 请求ID,与请求的用户组成复合唯一键
     */
    private String jobId;
    /**
     * 请求类型
     */
    private String requestType;
    /**
     * 查询sql
     */
    @NonNull
    private String sql;
    /**
     * 查询的结果集存储的kafka topic,如果传空串或者null，则结果集不往kafka发，直接返回，最多返回1万条，此功能主要用于web页面查询
     */
    private String kafkaTopic;
    /**
     * 请求返回类型,rpc直接返回数据,kafka,redis
     */
    private String responseType;
    /**
     * 是否要打印sql执行日志,比如hive的日志
     */
    private boolean printLog;

    /**
     * 数据时效性 t+1 or t
     */
    private String timeLiness;

    /**
     * 发起请求用户所在的组
     */
    private String group;

}
