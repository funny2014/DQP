package com.dqp.analysor.domain;


import com.dqp.analysor.service.HqlAnalyserService;
import com.dqp.analysor.util.FileUtilsPlus;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hive.ql.parse.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 解析Hql文件内容,包装成对象
 * different file implements different file sinks
 * because the specific content of file is different
 */
@Getter
@Setter
@Slf4j
public class HqlFileSink implements Cloneable {

    /**
     * error sql List
     */
    ArrayList<String> errSql = new ArrayList<String>();
    /**
     * each line together become a sql
     */
    ArrayList sql = new ArrayList();
    /**
     * temSql
     */
    ArrayList temSql = new ArrayList();
    /**
     * ordered sql list of whole hql file
     */
    ArrayList sqlList = new ArrayList();
    /**
     * the whole hql file operated tables
     */
    Set<String> tables = new HashSet<String>();

    /**
     * the whole hql file out tables
     */
    Set<String> outTables = new HashSet<String>();

    /**
     * the whole hql file in tables
     */
    Set<String> inTables = new HashSet<String>();

    String fileName;

    ArrayList<HqlFileObject> hqlObList = new ArrayList<>();


    public void doParseFile(String fileName) throws IOException {

        this.setFileName(fileName);
        FileUtilsPlus.readFileByLines(this.getFileName(), new Consumer<String>() {
            @Override
            public void accept(String s) {
                s = s.trim();
                /**
                 * 将编码格式UTF-8+BOM文件转为普通的UTF-8文件
                 */
                if (s.startsWith("\uFEFF")) {
                    s = s.replace("\uFEFF", "");
                }
                if (s.equals("")) {

                } else {
                    if (s.startsWith("--") || s.startsWith("set ") || s.startsWith("SET ")) {  //过滤注释和set关键字

                    } else {
                        sql.add(s + "\n");
                        if (s.endsWith(";")) {
                            if ((sql.size() == 1 && s.startsWith(";"))) {
                            } else {
                                if (s.split(";").length > 1) {
                                    for (String tm : s.split(";")  ) {
                                        if (tm.trim().equals(";")) {
                                        } else {
                                            temSql.add(tm);
                                            sqlList.add(temSql.clone());
                                            temSql.clear();
                                        }
                                    }
                                } else {
                                    sqlList.add(sql.clone());
                                }
                            }
                            sql.clear();
                        }
                    }
                }
            }
        });

        StringBuilder realSql = new StringBuilder();
        sqlList.forEach(
                tepSql ->
                {
                    ((Collection) tepSql).forEach(
                            temString -> {
                                realSql.append(temString);
                            }
                    );
                    try {
                        HqlFileObject hqlOb = new HqlFileObject();
                        HqlAnalyserService hqlAnalysor = new HqlAnalyserService(realSql.toString().replaceAll(";", ""));
                        hqlOb.setFileName(fileName);
                        hqlOb.setSqlDetail(realSql.toString());
                        hqlOb.setTablesAndOper(hqlAnalysor.getTables());
                        hqlOb.setCols(hqlAnalysor.getCols());
                        hqlOb.setColAlias(hqlAnalysor.getColAlias());
                        hqlOb.setColsAlisMappingTables(hqlAnalysor.getColsAlisMappingTables());

                        outTables.addAll(hqlOb.getOutputTable());
                        inTables.addAll(hqlOb.getInputTable());
                        hqlObList.add(hqlOb);
                    } catch (ParseException e) {
                        log.error("HqlFileSink->HqlAnalyserService->ParseException THE ERROR HQL is : {} -------ParseException:{}",realSql,e);
                        errSql.add(fileName + ":::::::::" + realSql.toString() + ":::::::::::" + e.getMessage());
                    } catch (Exception e) {
                        log.error("HqlFileSink->HqlAnalyserService->Exception:", e);
                        log.warn(" The reason of the exception Maybe : HERE IS A VERSION ERROR THE CURRENT Hive.exec VERSION CANNOT SUPPORT THIS KIND OF SQL YOU SHOULD UPGRADE TEH hive.exec VERSION AT LEAST TO 2.0.0");
                        errSql.add(fileName + ":::::::::" + realSql.toString() + ":::::::::::" + e.getMessage());
                    }
                    /**
                     * clean realSql
                     */
                    realSql.delete(0, realSql.length());
                }
        );
    }


    /**
     * clean method
     */
    public void clean() {
        this.errSql.clear();
        this.sql.clear();
        this.temSql.clear();
        this.sqlList.clear();
        this.tables.clear();
        this.hqlObList.clear();
        fileName = "";
    }

}
