package com.dqp.core.rpc;


import com.dqp.core.vo.AnalysorResponseVO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("analysor")
public interface AnalysorFeignClient {
    /**
     * 调用analysor服务解析sql
     */
    @RequestMapping(value = "/analysor", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    AnalysorResponseVO analysorParser(@RequestParam(value = "sql", required = true) String sql);
}
