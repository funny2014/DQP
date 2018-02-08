package com.dqp.facade.controller;


import com.dqp.facade.rpc.ExcutorFeignClient;
import com.dqp.facade.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


/**
 * The type Web socket controller.
 */
@Slf4j
@RestController
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;



    /**
     * 发送一个Job所有的sql执行日志
     * 此接口对应前端sockjs和stomp js访问方式推送，可分布式发消息(目前服务端开放，但前端未使用)
     */
    @RequestMapping(value = "/facade/websocket/sqllog", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseVO printSqlLog(String jobId, String sqllog) {
        log.info("jobid:" + jobId);
        this.simpMessagingTemplate.convertAndSend("/topic/sqllog/" + jobId, sqllog);

        return ResponseVO.builder().success(true).build();
    }

    /**
     * 前端打印一个Job一条sqlLog
     */
    @RequestMapping(value = "/facade/websocket/printLogs", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseVO printLogs(String jobId, String sqllog) throws IOException {
        log.info("websocket printLogs jobid:" + jobId + ",and current log is " + sqllog);

        MyWebSocket myWebSocket = MyWebSocket.webSocketSet.get(jobId);
        if(myWebSocket!=null){
            myWebSocket.sendMessage(sqllog);
        }
        log.info("current log is ok");
        return ResponseVO.builder().success(true).build();
    }
}
