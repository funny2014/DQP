package com.dqp.sso.feign;

import com.dqp.sso.model.HqlParserResultModel;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * hql 解析服务
 * Created on 2017/8/4.
 * Desc:
 */
@FeignClient(url = "${dataplatform.feign.hqlparser.url}", name = "${dataplatform.feign.hqlparser.name}")
public interface HqlParserServiceFeign {

  @RequestMapping(value = "/analysor/hql/parser",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  HqlParserResultModel hqlParser(@RequestParam("sql") String sql);





}
