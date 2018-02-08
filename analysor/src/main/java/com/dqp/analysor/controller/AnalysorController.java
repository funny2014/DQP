package com.dqp.analysor.controller;

import com.alibaba.fastjson.JSONArray;
import com.dqp.analysor.domain.NodeForDisplay;
import com.dqp.analysor.domain.Nodes;
import com.dqp.analysor.service.*;
import com.dqp.analysor.vo.AnalyserSuccessVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;


/**
 * The type Analysor controller.
 */
@RestController
@Slf4j
@RequestMapping("/analysor")
public class AnalysorController {

    ////返回 sql 内全部涉及的表
    //@CrossOrigin(allowCredentials = "true", allowedHeaders = "*", methods = {RequestMethod.GET,
    //        RequestMethod.POST, RequestMethod.DELETE, RequestMethod.OPTIONS,
    //        RequestMethod.HEAD, RequestMethod.PUT, RequestMethod.PATCH}, origins = "*")
    //
    @RequestMapping(value = {"/alltables"}, produces = {MediaType.APPLICATION_JSON_VALUE}, method = {RequestMethod.POST, RequestMethod.GET})
    public AnalyserSuccessVO alltables(@RequestParam(name = "sql", required = true) final String sql) {
        log.info(sql);
        SchemaStatService sqlParserService = new SchemaStatService(sql);
        return AnalyserSuccessVO.builder().success(true).msg(sqlParserService.getAllOperateTables()).build();
    }

    /**
     * 获得一条SQL语句中的AST的第一层
     */
    @RequestMapping(value = {"/ast"}, produces = {MediaType.APPLICATION_JSON_VALUE}, method = {RequestMethod.POST, RequestMethod.GET})
    public AnalyserSuccessVO ast(@RequestParam(name = "sql", required = true) final String sql) {
        log.info(sql);
        SchemaAstService sqlParserService = new SchemaAstService(sql);
        return AnalyserSuccessVO.builder().success(true).msg(sqlParserService.getAllFirstLevelAST()).build();
    }


    /**
     * 返回 sql 内全部涉及的表 与操作
     */
    @RequestMapping(value = {"/hql/parser"}, produces = {MediaType.APPLICATION_JSON_VALUE}, method = {RequestMethod.POST, RequestMethod.GET})
    public AnalyserSuccessVO hqlParser(@RequestParam(name = "sql", required = true) final String sql) {
        log.info(sql);
        Map<String, String> returnMap = new HashMap<String, String>();

        try {
            HqlAnalyserService hqlAnalyserService = new HqlAnalyserService(sql.toLowerCase());
            Set<String> currentSet = hqlAnalyserService.getTables();
            String[] tmp;
            for (String tablename :
                    currentSet) {
                tmp = tablename.split("\t");
                returnMap.put(tmp[0], tmp[1]);
            }

            return AnalyserSuccessVO.builder().success(true).msg(returnMap).build();
        } catch (ParseException e) {
            log.error("解析错误", e);
            returnMap.put("ErrorMsg", e.getMessage());
            return AnalyserSuccessVO.builder().success(false).msg(returnMap).build();
        }

    }


    /**
     * 血缘分析，遍历图
     * searching graph api
     * http://localhost:8088/analysor/lineage/search?database=112&table=1&level=job&ischild=&iscols=false&deeplevel=1
     */
    //@RequestMapping(value = {"/lineage/search"}, produces = {MediaType.APPLICATION_JSON_VALUE}, method = {RequestMethod.POST, RequestMethod.GET})
    @RequestMapping(value = {"/lineage/search"},   method = {RequestMethod.POST, RequestMethod.GET})
    public AnalyserSuccessVO searchGraph(
            @RequestParam(value = "database") final String database,
            @RequestParam(value = "table") final String table,
            @RequestParam(value = "level") final String level,
            @RequestParam(value = "ischild") final boolean ischild,
            @RequestParam(value = "iscols") final String iscols,
            @RequestParam(value = "deeplevel") final int deeplevel
    ) {

        log.info("database is :{} , table is :{} ,level is :{}, ischild is :{}, iscols is :{} ,deeplevel is {} "
                , database, table, level, ischild, ischild, deeplevel);
//        nodesList.clear();
//        linksList.clear();
        Map<String, String> returnMap = new LinkedHashMap<String, String>();

        try {
            if (AnalysorInitialService.nodesList.size() == 0) {
                AnalysorInitialService analysorInitialService = new AnalysorInitialService();
            }

            GraphSearchTaskAdapter graphSearchTaskAdapter =
                    new GraphSearchTaskAdapter(
                            AnalysorInitialService.nodesList,
                            AnalysorInitialService.linksList,
                            deeplevel,
                            ischild);
            graphSearchTaskAdapter.widthFirstSeachWithDeepLevel(database + "." + table.toLowerCase());
            returnMap.put("nodes", JSONArray.toJSONString(graphSearchTaskAdapter.getReturnNodesSet()));
            returnMap.put("links", JSONArray.toJSONString(graphSearchTaskAdapter.getReturnLinksSet()));
            return AnalyserSuccessVO.builder().success(true).msg(returnMap).build();
        } catch (Exception e) {
            log.error("解析错误", e);
            returnMap.put("ErrorMsg", e.getMessage());
            return AnalyserSuccessVO.builder().success(false).msg(returnMap).build();
        }
    }


    /***
     * 加载解析hql文件
     * parsing hql file
     * @return the analyser success vo
     * @throws IOException the io exception
     */
    @RequestMapping(value = {"/lineage/getallnodes"}, produces = {MediaType.APPLICATION_JSON_VALUE}, method = {RequestMethod.POST, RequestMethod.GET})
    public AnalyserSuccessVO getHqlFilePathAllNodesInfo() {
        Map<String, String> returnMap = new LinkedHashMap<String, String>();

        try {
            if (AnalysorInitialService.nodesList.size() == 0) {
                AnalysorInitialService analysorInitialService = new AnalysorInitialService();
            }

            ArrayList<NodeForDisplay> nodeForDisplays = new ArrayList<>(AnalysorInitialService.nodesList.size());
            for (Nodes node :
                    AnalysorInitialService.nodesList) {
                if(node.getCategory()==2){
                    nodeForDisplays.add(new NodeForDisplay(node.getName().toLowerCase()));

                }
            }

            returnMap.put("nodes", JSONArray.toJSONString(nodeForDisplays));
            return AnalyserSuccessVO.builder().success(true).msg(returnMap).build();

        } catch (Exception e) {
            log.error("解析错误", e);
            returnMap.put("ErrorMsg", e.getMessage());
            return AnalyserSuccessVO.builder().success(false).msg(returnMap).build();
        }
    }

}
