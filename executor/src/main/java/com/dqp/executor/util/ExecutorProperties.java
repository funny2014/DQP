package com.dqp.executor.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * The type Executor properties.
 */
@ConfigurationProperties(prefix = ExecutorProperties.PREFIX)
@Data
public class ExecutorProperties {
    /**
     * The constant PREFIX.
     */
    public static final String PREFIX = "executor";
    /**
     * sql执行成功后返回结果集的最多记录数
     */
    private int responseSize;
}
