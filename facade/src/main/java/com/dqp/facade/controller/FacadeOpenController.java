package com.dqp.facade.controller;


import com.dqp.facade.rpc.CoreFeignClient;
import com.dqp.facade.rpc.ExcutorFeignClient;
import com.dqp.facade.util.StringUtil;
import com.dqp.facade.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The type Facade open controller.
 */
@Controller
@Slf4j
@RequestMapping("/facade/open")
public class FacadeOpenController {

    @Autowired
    private CoreFeignClient coreFeignClient;

    @Autowired
    protected ExcutorFeignClient excutorFeignClient;


    //跳转到首页面
    @RequestMapping(value = {"/", "/index"})
    public String index(Map<String, Object> model) {
        return "myindex";
    }

    /**
     * 一个任务，批量执行Sql，返回结果
     */
    @RequestMapping(value = {"/excuteBatchQuery"})
    @ResponseBody
    public List<ResponseVO> getTable(@RequestParam(name = "sqls", required = true) final String sqls, @RequestParam(name = "jobid", required = true) final String jobID) {
        log.info("sql is " + sqls + ",jobid is " + jobID);
        List<ResponseVO> results = new ArrayList<>();
        String[] sqlArray = sqls.split(";");

        for (String sql : sqlArray) {
            try {
                sql = sql.replaceAll("\r", " ").replaceAll("\n", " ");
                results.add(coreFeignClient.executeQueryToRpc(sql, "", "", jobID, true));
            } catch (Exception e) {
                log.error(StringUtil.getTrace(e));
                ResponseVO failedExecutor =  ResponseVO.builder()
                        .success(false)
                        .errorMsg(e.getMessage())
                        .build();
                results.add(failedExecutor);
            }
        }
        return results;
    }

    /**
     * 一个任务，执行一条查询
     */
    @RequestMapping(value = {"/excuteQuery"})
    @ResponseBody
    public ResponseVO getTable(@RequestParam(name = "sql", required = true) String sql,
                               @RequestParam(name = "jobId", required = true) String jobId,
                               @RequestParam(name = "username") String username,
                               @RequestParam(name = "password") String password) {
        log.info("sql is " + sql + ",jobid is " + jobId + ",username is " + username);
        try {
            return coreFeignClient.executeQueryToRpc(sql.replaceAll("\r", " ").replaceAll("\n", " "),
                    username, password, jobId, false);
        } catch (Exception e) {
            log.error(StringUtil.getTrace(e));
            return ResponseVO.builder()
                    .success(false)
                    .errorMsg(e.getMessage())
                    .build();
        }
    }

    /**
     * Gets last querys by username
     */
    @RequestMapping(value = {"/getLatestSqls"})
    @ResponseBody
    public ResponseVO getLatestSqls(@RequestParam(name = "username") String username
                                    ,@RequestParam(name = "pageNo") int pageNo
                                    ,@RequestParam(name = "pageSize") int pageSize) {
        log.info("username is " + username  + ", pageNo is " + pageNo +", pageSize is " + pageSize);
        try {
            return excutorFeignClient.getLatestSqls(username,pageNo,pageSize);
        } catch (Exception e) {
            log.error(StringUtil.getTrace(e));
            return ResponseVO.builder()
                    .success(false)
                    .errorMsg(e.getMessage())
                    .build();
        }
    }

    /**
     * Gets sql log(the latest) by jobId
     */
    @RequestMapping(value = "/sqllog/{jobId}", method = {RequestMethod.GET, RequestMethod.POST})
    public List<String> testSqlLog(@PathVariable String jobId) {
        return this.excutorFeignClient.getSqlLog(jobId);
    }
}
