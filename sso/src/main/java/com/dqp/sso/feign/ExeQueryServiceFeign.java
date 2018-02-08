package com.dqp.sso.feign;

import com.dqp.sso.model.ExeQueryResultModel;
import com.dqp.sso.model.LatestSqlResultModel;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 执行查询接口
 */
@FeignClient(url = "${dataplatform.feign.exequery.url}", name = "${dataplatform.feign.exequery.name}",path = "/facade/open/")
public interface ExeQueryServiceFeign {

  @RequestMapping(value = "excuteQueryByDbEngine", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  ExeQueryResultModel executeQueryByDbEngine(@RequestParam("sql") String sql, @RequestParam("timeLiness") String timeLiness, @RequestParam
          ("username") String username, @RequestParam("dbEngine") String dbEngine, @RequestParam("group") String group,
                                             @RequestParam(name = "jobId", required = false) String jobId);

  @RequestMapping(value = "getLatestSqls", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  LatestSqlResultModel getLatestSqls(@RequestParam("username") String username, @RequestParam("pageNo") String pageNo, @RequestParam("pageSize") String pageSize);




}
