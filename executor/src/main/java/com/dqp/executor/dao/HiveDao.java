package com.dqp.executor.dao;

import com.dqp.executor.rpc.FacadeFeignClient;
import com.dqp.executor.util.DBConnection;
import com.dqp.executor.util.StringUtil;
import com.dqp.executor.vo.RequestVO;
import com.dqp.executor.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.hive.jdbc.HivePreparedStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class HiveDao extends BaseJdbcDao {

    @Autowired
    private DataSourceProperties hiveDataSourceProperties;

    @Autowired
    private FacadeFeignClient facadeFeignClient;


    /**
     * executeQuery
     *
     * @param requestVO the request vo
     * @return
     */
    @Override
    public ResponseVO executeQuery(RequestVO requestVO) {
        try {
            Class.forName(this.hiveDataSourceProperties.getDriverClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection con = null;
        HivePreparedStatement stat = null;
        ResultSet rs = null;
        try {
            String userGroup = requestVO.getPassword();
            con = DriverManager.getConnection(this.hiveDataSourceProperties.getUrl(), userGroup, this.hiveDataSourceProperties.getPassword());
            stat = (HivePreparedStatement) con.prepareStatement(requestVO.getSql());
            if (requestVO.isPrintLog()) { //打印日志
                new Thread(new HiveLog(stat, requestVO, this.facadeFeignClient)).start();
            }
            return super.executeQuery(requestVO, stat);
        } catch (SQLException e) {
            log.error(StringUtil.getTrace(e));
            return super.responseFailed(requestVO, e);
        } finally {
            try {
                DBConnection.close(rs, stat, con);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * The type Hive log.
 */
@Slf4j
class HiveLog implements Runnable {

    private HivePreparedStatement stat;
    private RequestVO requestVO;
    private FacadeFeignClient facadeFeignClient;

    /**
     * Instantiates a new Hive log.
     */
    public HiveLog(HivePreparedStatement stat, RequestVO requestVO, FacadeFeignClient facadeFeignClient) {
        this.stat = stat;
        this.requestVO = requestVO;
        this.facadeFeignClient = facadeFeignClient;
    }


    @Override
    public void run() {
        String jobId = this.requestVO.getJobId();
        if (!StringUtil.isNotEmpty(jobId)) {
            log.warn("fetch hive log 的 jobId不能为空！");
            return;
        }
        log.info("fetch hive log start....");
        try{
            while (!stat.isClosed() && stat.hasMoreLogs()) {
                try {
                    List<String> logs = stat.getQueryLog();
                    log.info("fetch hive log ,size is " + logs.size());
                    if (logs != null && logs.size() > 0) {
                        //log data save in SqlLog.log
                        if (!SqlLog.logs.containsKey(jobId)) {
                            SqlLog.logs.put(jobId, new ArrayList<>());
                        }
                        SqlLog.logs.get(jobId).add(logs);

                        StringBuffer sqllog = new StringBuffer();
                        for (String log : logs) {
                            this.log.info("fetch hive log is " + log);
                            this.facadeFeignClient.printLogs(jobId, log.toString());
                            sqllog.append(log + "\n");
                        }
                        this.facadeFeignClient.sendSqlLog(jobId, sqllog.toString());
                    }
                } catch (SQLException e) { //防止while里面报错，导致一直退不出循环
                    e.printStackTrace();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("fetch hive log over!!!!");
                    break;
                }
            }
        }catch (SQLException e) {
            log.info("sql exception");
            e.printStackTrace();
        }
        //sql执行日志只在内存里保存10分钟
//		try {
//			Thread.sleep(600000);
//			SqlLog.logs.remove(jobId);
//		} catch (InterruptedException e) {
//			log.error(StringUtil.getTrace(e));
//		}
//		if(!SqlLog.logs.containsKey(jobId)){
//			log.info("jobId:"+jobId+",sqllog removed!!!");
//		}else{
//			log.warn("jobId:"+jobId+",sqllog not removed,logs size:"+SqlLog.logs.get(jobId).size());
//		}
    }

}
