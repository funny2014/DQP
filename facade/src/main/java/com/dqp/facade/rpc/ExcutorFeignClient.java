package com.dqp.facade.rpc;


import com.dqp.facade.vo.ResponseVO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * ndb-executor服务的rpc请求client
 */
@FeignClient("executor")
public interface ExcutorFeignClient {
    /**
     * 获得与一个任务相关的sql执行日志
     */
    @RequestMapping(value = "/executor/sqllog", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    List<String> getSqlLog(@RequestParam(value = "jobId") String jobId);

    /**
     * 获得与一个用户相关的sql执行日志
     */
    @RequestMapping(value = "/executor/latestsqls", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseVO getLatestSqls(@RequestParam(value = "username") String username
            , @RequestParam(value = "pageNo") int pageNo
            , @RequestParam(value = "pageSize") int pageSize);
}
