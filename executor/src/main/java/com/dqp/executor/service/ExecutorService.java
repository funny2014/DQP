package com.dqp.executor.service;

import com.dqp.executor.dao.*;
import com.dqp.executor.vo.RequestVO;
import com.dqp.executor.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The type Executor service.
 */
@Service
public class ExecutorService {

    @Autowired
    private MycatDao mycatDao;

    @Autowired
    private HiveDao hiveDao;

    @Autowired
    private HbaseDao hbaseDao;

    @Autowired
    private PrestoDao prestoDao;

    @Autowired
    private SparkSqlDao sparkSqlDao;

    @Autowired
    private MysqlDao mysqlDao;

    @Autowired
    private LatestSqlsDao latestSqlsDao;

    /**
     * Execute query mycat response vo.
     */
    public ResponseVO executeQueryMycat(RequestVO requestVO) {
        return mycatDao.executeQuery(requestVO);
    }

    /**
     * Execute query hive response vo.
     */
    public ResponseVO executeQueryHive(RequestVO requestVO) {
        return hiveDao.executeQuery(requestVO);
    }

    /**
     * Execute query hbase response vo.
     */
    public ResponseVO executeQueryHbase(RequestVO requestVO) {
        return hbaseDao.executeQuery(requestVO);
    }

    /**
     * Execute query presto response vo.
     */
    public ResponseVO executeQueryPresto(RequestVO requestVO) { return prestoDao.executeQuery(requestVO); }

    /**
     * Execute query sparksql response vo.
     */
    public ResponseVO executeQuerySparkSql(RequestVO requestVO) { return sparkSqlDao.executeQuery(requestVO); }


    /**
     * Execute query mysql response vo.
     */
    public ResponseVO executeQueryMysql(RequestVO requestVO){
        return mysqlDao.executeQuery(requestVO);
    }

    /**
     * Execute query latestSqls response vo.
     */
    public ResponseVO executeQueryLatestSqls(String username,int pageNo,int pageSize){
        return latestSqlsDao.executeQuery(username,pageNo,pageSize);
    }

    /**
     * Execute insert latestSqls response vo.
     */
    public ResponseVO executeInsertLatestSqls(RequestVO requestVO){
        return latestSqlsDao.insert(requestVO);
    }
}
