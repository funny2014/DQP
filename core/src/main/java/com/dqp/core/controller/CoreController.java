package com.dqp.core.controller;

import com.dqp.core.rpc.AnalysorFeignClient;
import com.dqp.core.rpc.ExecutorFeignClient;
import com.dqp.core.vo.RequestVO;
import com.dqp.core.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


/**
 * The type Core controller.
 */
@Slf4j
@RestController
@RequestMapping("/core")
public class CoreController {

    @Autowired
    private ExecutorFeignClient executorFeignClient;


    @Autowired
    private AnalysorFeignClient analysorFeignClient;

    /**
     * Execute query to rpc response vo.
     */
    @RequestMapping(value = "/rpc", produces = {MediaType.APPLICATION_JSON_VALUE}, method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseVO executeQueryToRPC(RequestVO requestVO) {
        log.info("调用executor服务 # {}", requestVO.toString());
        requestVO.setHeader("");
        requestVO.setRequestType("");
        requestVO.setKafkaTopic("");

        requestVO.setRequestType("rpc");
        requestVO.setResponseType("rpc");
        requestVO.setRequestTime(100);
        requestVO.setDbEngine("mysql");
        requestVO.setUsername("root");
        requestVO.setPassword("123456");
        requestVO.setPrintLog(false);
        requestVO.setJobId("111");
        requestVO.setSql("select 1");

        String header = requestVO.getHeader();
        long requestTime = requestVO.getRequestTime();
        String kafkaTopic = requestVO.getKafkaTopic();
        String username = requestVO.getUsername();
        String password = requestVO.getPassword();
        String sql = requestVO.getSql();
        String requestType = requestVO.getRequestType();
        String responseType = requestVO.getResponseType();
        String jobId = requestVO.getJobId();
        boolean printLog = requestVO.isPrintLog();
        return this.executorFeignClient.executeQuery(requestVO.getDbEngine()
                , header
                , requestTime
                , username, password
                , jobId, requestType
                , sql,  kafkaTopic
                , responseType, printLog);
    }



    /**
     * Execute query response vo.
     *
     * @param header      the header
     * @param requestTime the request time
     * @param username    the username
     * @param password    the password
     * @param jobId       the job id
     * @param requestType the request type
     * @param sql         the sql
     * @param kafkaTopic  the kafka topic
     * @return the response vo
     */
    @RequestMapping(value = "/kafka", produces = {MediaType.APPLICATION_JSON_VALUE}, method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseVO executeQuery(@RequestParam(value = "header") String header
            , @RequestParam(value = "requestTime") long requestTime
            , @RequestParam(value = "username") String username
            , @RequestParam(value = "password") String password
            , @RequestParam(value = "jobId") String jobId
            , @RequestParam(value = "requestType") String requestType
            , @RequestBody String sql
            , @RequestParam(value = "kafkaTopic") String kafkaTopic
            , @RequestParam(value = "dbEngine") String dbEngine
    ) {
        log.info("调用executor服务...");
        return this.executorFeignClient.executeQuery(dbEngine
                , header, requestTime
                , username, password
                , jobId, requestType
                , sql, kafkaTopic
                , "KAFKA", false);
    }


}
