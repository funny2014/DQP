package com.dqp.core.rpc;


import com.dqp.core.vo.ResponseVO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ndb-executor服务的rpc请求client
 */
@FeignClient("executor")
public interface ExecutorFeignClient {
    /**
     * 执行sql
     *
     * @param dbEngine     数据库引擎
     * @param header       请求头，客户端如果是用spring cloud编写的kafka则需要此字段，原生kafka不需要此字段,直接传空串或者null
     * @param requestTime  the request time
     * @param username     the username
     * @param password     the password
     * @param jobId        the job id
     * @param requestType  the request type
     * @param sql          the sql
     * @param kafkaTopic   the kafka topic
     * @param responseType the response type
     * @param printLog     the print log
     * @return the response vo
     */
    @RequestMapping(value = "/executor", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseVO executeQuery(@RequestParam(value = "dbEngine") String dbEngine
            , @RequestParam(value = "header") String header
            , @RequestParam(value = "requestTime") long requestTime
            , @RequestParam(value = "username") String username
            , @RequestParam(value = "password") String password
            , @RequestParam(value = "jobId") String jobId
            , @RequestParam(value = "requestType") String requestType
            , @RequestBody String sql
            , @RequestParam(value = "kafkaTopic") String kafkaTopic
            , @RequestParam(value = "responseType") String responseType
            , @RequestParam(value = "printLog") boolean printLog);
}
