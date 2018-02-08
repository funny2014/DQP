package com.dqp.executor.controller;

import com.dqp.executor.dao.SqlLog;
import com.dqp.executor.enums.ResponseType;
import com.dqp.executor.service.ExecutorService;
import com.dqp.executor.vo.RequestVO;
import com.dqp.executor.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * The type Executor controller.
 */
@RestController
@RequestMapping("/executor")
@Slf4j
public class ExecutorController {

    @Autowired
    private ExecutorService executorService;


    /**
     * Execute query response vo.
     *
     * @param dbEngine     the db engine
     * @param header       the header
     * @param requestTime  the request time
     * @param username     the username
     * @param password     the password
     * @param jobId        the job id
     * @param requestType  the request type
     * @param sql          the sql
     * @param kafkaTopic   the kafka topic
     * @param responseType the response type 数据管道类型
     * @param printLog     the print log
     * @return the response vo
     */
    @RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE}, method = {
            RequestMethod.GET, RequestMethod.POST})
    public ResponseVO executeQuery(
            @RequestParam(value = "dbEngine") String dbEngine,
            @RequestParam(value = "header") String header,
            @RequestParam(value = "requestTime") long requestTime,
            @RequestParam(value = "username") String username,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "jobId") String jobId,
            @RequestParam(value = "requestType") String requestType,
            @RequestBody String sql,
            @RequestParam(value = "kafkaTopic") String kafkaTopic,
            @RequestParam(value = "responseType") String responseType,
            @RequestParam(value = "printLog") boolean printLog) {

        RequestVO requestVO = RequestVO.builder().dbEngine(dbEngine)
                .header(header).jobId(jobId).kafkaTopic(kafkaTopic)
                .password(password).username(username).sql(sql)
                .printLog(printLog).requestTime(requestTime)
                .requestType(requestType).responseType(responseType).build();


        ResponseType[] rts = ResponseType.values();
        boolean flag = false;
        for (ResponseType rt : rts) {
            if (rt.toString().equalsIgnoreCase(responseType)) {
                flag = true;
            }
        }
        if (!flag) {
            return ResponseVO.builder().success(false)
                    .errorMsg("unknow responseType:" + responseType).build();
        }

        //保存查询的历史记录
        log.info("insert sql start");
        this.executorService.executeInsertLatestSqls(requestVO);


        log.info("采用的数据引擎：{}" , dbEngine);
        /**
         * 根据请求参数选择执行引擎
         */
        if ("hive".equalsIgnoreCase(dbEngine)) {
            return this.executorService.executeQueryHive(requestVO);
        } else if ("hbase".equalsIgnoreCase(dbEngine)) {
            return this.executorService.executeQueryHbase(requestVO);
        } else if ("mycat".equalsIgnoreCase(dbEngine)) {
            return this.executorService.executeQueryMycat(requestVO);//BaseTO->MycatTO
        } else if ("presto".equalsIgnoreCase(dbEngine)) {
            return this.executorService.executeQueryPresto(requestVO);
        } else if ("sparksql".equalsIgnoreCase(dbEngine)) {
            return this.executorService.executeQuerySparkSql(requestVO);
        } else if ("mysql".equalsIgnoreCase(dbEngine)) {
            return this.executorService.executeQueryMysql(requestVO);
        } else {
            String errorMsg = "unknow dbEngine:" + dbEngine;
            log.warn(errorMsg);
            if (ResponseType.KAFKA.toString().equalsIgnoreCase(responseType)) {
                //  todo  发送kafka,以后开发
            }
            return ResponseVO.builder().success(false).errorMsg(errorMsg)
                    .build();
        }
    }

    /**
     * Gets sql log(the latest) by jobId , 从内存中取
     */
    @RequestMapping(path = "/sqllog", produces = {MediaType.APPLICATION_JSON_VALUE}, method = {
            RequestMethod.GET, RequestMethod.POST})
    public List<String> getSqlLog(@RequestParam(name = "jobId") String jobId) {
        log.info("查询日志");
        List<List<String>> logs = SqlLog.logs.get(jobId);
        if (logs == null || logs.size() == 0) {
            log.info("jobID=" + jobId + ",没有此任务日志。。。");
            return new ArrayList<String>();
        }
        log.info("日志大小:{}", logs.size());

        List<String> sqlLog = logs.get(logs.size() - 1);
        return sqlLog;
    }


    /**
     * Gets last querys by username, 从数据库中取
     */
    @RequestMapping(path = "/latestsqls", produces = {MediaType.APPLICATION_JSON_VALUE}, method = {
            RequestMethod.GET, RequestMethod.POST})
    public ResponseVO getLatestSqls(@RequestParam(name = "username") String username
            , @RequestParam(name = "pageNo") int pageNo
            , @RequestParam(name = "pageSize") int pageSize) {
        return this.executorService.executeQueryLatestSqls(username, pageNo, pageSize);
    }

}
