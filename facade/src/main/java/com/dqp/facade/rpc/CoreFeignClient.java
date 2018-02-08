package com.dqp.facade.rpc;


import com.dqp.facade.vo.ResponseVO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * ndb-core服务的rpc请求client
 */
@FeignClient("core")
public interface CoreFeignClient {


    /**
     * 执行sql,查询的结果集发送到kafka里
     *
     * @param header      请求头，客户端如果是用spring cloud编写的kafka则需要此字段，原生kafka不需要此字段,直接传空串或者null
     * @param requestTime the request time
     * @param username    the username
     * @param password    the password
     * @param jobId       the job id
     * @param requestType the request type
     * @param sql         the sql
     * @param kafkaTopic  存储结果集的kafka topic,如果传空串或者null，则结果集不往kafka发
     * @return the response vo
     */
    @RequestMapping(value = "/core?responseType=kafka", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseVO executeQueryToKafka(@RequestParam(value = "header") String header
            , @RequestParam(value = "requestTime") long requestTime
            , @RequestParam(value = "username") String username
            , @RequestParam(value = "password") String password
            , @RequestParam(value = "jobId") String jobId
            , @RequestParam(value = "requestType") String requestType
            , @RequestBody String sql
            , @RequestParam(value = "kafkaTopic") String kafkaTopic
            , @RequestParam(value = "dbEngine") String dbEngine
    );

    /**
     * 执行sql,查询的结果集直接rpc请求返回
     *
     * @param sql      the sql
     * @param username the username
     * @param password the password
     * @param jobId    the job id
     * @param printLog the print log
     * @return 最多返回1万条 response vo
     */
    @RequestMapping(value = "/core/rpc?responseType=rpc", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseVO executeQueryToRpc(@RequestParam(value = "sql") String sql
            , @RequestParam(value = "username") String username
            , @RequestParam(value = "password") String password
            , @RequestParam(value = "jobId") String jobId
            , @RequestParam(value = "printLog") boolean printLog);

}
