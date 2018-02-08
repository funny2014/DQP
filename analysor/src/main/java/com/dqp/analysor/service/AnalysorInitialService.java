package com.dqp.analysor.service;

import com.dqp.analysor.domain.HqlFileSink;
import com.dqp.analysor.domain.Links;
import com.dqp.analysor.domain.Nodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


/*
*  分析器初始化类，随项目启动而启动：加载解析HQL文件，获得节点和边的集合信息
*/
@Slf4j
@Service
public class AnalysorInitialService {

    //@Value("${hqlfile.path}")
    //String filePath;
    String filePath = "/Users/golden/Projects/DQP/analysor/testData";

    public static Set<Nodes> nodesList = new HashSet<Nodes>(2000);
    public static Set<Links> linksList = new HashSet<Links>(2000);

    public AnalysorInitialService() throws IOException {
        nodesList.clear();
        linksList.clear();

        HqlFileAnalyserService hqlFileAnalyserService = new HqlFileAnalyserService();
        hqlFileAnalyserService.getFiles(filePath);
        log.warn("''errSql::'''''''''''''''''''{}'''''''''''''''''''''''''''", hqlFileAnalyserService.getErrSql());

        for (HqlFileSink ql : hqlFileAnalyserService.getHqlFileSink_list()) {
            generateNodesAndLinks_hFile(ql);
        }
    }

    /**
     * 获得点和边的集合
     * To generate Nodes and Links from a given HqlFileSink object
     *
     * notice the to lowerCase
     * @param ql
     */
    private void generateNodesAndLinks_hFile(HqlFileSink ql) {
        Nodes task = new Nodes(1, ql.getFileName().replace(filePath, ""), 30);
//        Nodes task = new Nodes(1, ql.getFileName().replace("D:\\git\\gitlab\\BigData\\project\\etl_automation\\script\\", ""), 30);
        nodesList.add(task);

        for (String tmpInputTable : ql.getInTables()) {
            Nodes table = new Nodes(2, tmpInputTable.toLowerCase(), 30);
            Links link = new Links(tmpInputTable.toLowerCase(), ql.getFileName().replace(filePath, ""), "INPUT");
            nodesList.add(table);
            linksList.add(link);
        }

        for (String tmpoutTable : ql.getOutTables()) {
            Nodes table = new Nodes(2, tmpoutTable.toLowerCase(), 30);
            Links link = new Links(ql.getFileName().replace(filePath, ""), tmpoutTable.toLowerCase(), "OUTPUT");
            linksList.add(link);
            nodesList.add(table);
        }

    }
}
