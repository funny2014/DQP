package com.dqp.analysor.service;


import com.dqp.analysor.domain.HqlFileSink;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 获得hql文件并解析文件的各项属性
 * To do a recursion Analysis of all the hql File which is in the given path
 */
@Getter
@Setter
@Slf4j
@Service
public class HqlFileAnalyserService {
    private ArrayList<String> errSql = new ArrayList<String>();
    private String currentFile = "";
    static int errorCount = 0;
    List<HqlFileSink> hqlFileSink_list = new ArrayList<HqlFileSink>();

    /**
     * 通过递归的方式，获得指定路径下hql文件
     * to recursion the given file path
     */
    public void getFiles(String filePath) throws IOException {
        log.info("the current file path is : {}", filePath);
        int hqlFileCount = 0;
        File root = new File(filePath);
        File[] files = root.listFiles();
        for (File file : files) {
            currentFile = file.getAbsolutePath();
            if (file.isDirectory()) {
                if (currentFile.contains("备份")) {   //过滤
                    log.info("filtered key words is 备份  path is : {}", currentFile);
                } else {
                    log.info("recursion current path is : {}", currentFile);
                    getFiles(currentFile);
                }
            } else {
                if (currentFile.endsWith(".hql") && !currentFile.contains("TEST")) {
                    hqlFileCount++;
                    log.info("hqlFileCount is : {}  and the file path is  : {}", hqlFileCount, currentFile);

                    HqlFileSink hqlFileSink = new HqlFileSink();
                    hqlFileSink.doParseFile(currentFile);
                    errSql.add(hqlFileSink.getErrSql().clone().toString());
                    hqlFileSink_list.add(hqlFileSink);
                } else {
                    log.info("filtered  key words  is start with TEST and !hql path is : {}", currentFile);
                }
            }
        }

    }


    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        String filePath = "/Users/golden/Projects/testData/";
        HqlFileAnalyserService hqlFileAnalyserService = new HqlFileAnalyserService();
        hqlFileAnalyserService.getFiles(filePath);
        log.info("errSql: {} ", hqlFileAnalyserService.getErrSql());
    }
}
