package com.dqp.executor.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The interface Sql log.
 */
public interface SqlLog {
    /**
     * 存储sql执行日志,key:jobId,  value:日志信息
     */
     Map<String, List<List<String>>> logs = new HashMap<>();
}
