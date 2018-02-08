package com.dqp.executor.rpc;


import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * facade服务的rpc请求client
 */
@FeignClient("facade")
public interface FacadeFeignClient {
    /**
     * 发送一个Job所有的sql执行日志
     */
    @RequestMapping(value = "/facade/websocket/sqllog", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void sendSqlLog(@RequestParam(value = "jobId") String jobId, @RequestParam(value = "sqllog") String sqllog);

    /**
     * 打印一个Job一条sqlLog
     */
    @RequestMapping(value = "/facade/websocket/printLogs", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void printLogs(@RequestParam(value = "jobId") String jobId, @RequestParam(value = "sqllog") String sqllog);
}
