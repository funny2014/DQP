package com.dqp.executor.dao;

import com.dqp.executor.enums.ResponseType;
import com.dqp.executor.util.DBConnection;
import com.dqp.executor.util.ExecutorProperties;
import com.dqp.executor.util.StringUtil;
import com.dqp.executor.vo.RequestVO;
import com.dqp.executor.vo.ResponseVO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Base jdbc dao.
 */
@Slf4j
public abstract class BaseJdbcDao {

    @Autowired
    private ExecutorProperties executorProperties;

    /**
     * 执行sql失败，返回异常信息
     */
    @SneakyThrows
    public ResponseVO responseFailed(RequestVO requestVO, Exception e) {
        if (ResponseType.KAFKA.toString().equalsIgnoreCase(requestVO.getResponseType())) {
            //TODO 发送到kafka,以后再开发
        }
        return ResponseVO.builder()
                .success(false)
                .errorMsg(StringUtil.getTrace(e))
                .build();
    }


    public abstract ResponseVO executeQuery(RequestVO requestVO);

    @SneakyThrows
    protected ResponseVO executeQuery(RequestVO requestVO, DataSource dataSource) {
        try {
            return this.executeQuery(requestVO, dataSource.getConnection());
        } catch (Exception e) {
            log.error(StringUtil.getTrace(e));
            return this.responseFailed(requestVO, e);
        }
    }

    protected ResponseVO executeQuery(RequestVO requestVO, Connection conn) {
        PreparedStatement stat = null;
        ResultSet rs = null;

        try {
            stat = conn.prepareStatement(requestVO.getSql());
            return this.executeQuery(requestVO, stat);
        } catch (Exception e) {
            log.error(StringUtil.getTrace(e));
            return this.responseFailed(requestVO, e);
        } finally {
            try {
                DBConnection.close(rs, stat, conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Hive-jdbc,Spark-jdbc，Mysql-jdbc 调用
     */
    @SneakyThrows
    protected ResponseVO executeQuery(RequestVO requestVO, PreparedStatement stat) {
        long requestTime = requestVO.getRequestTime();
        String username = requestVO.getUsername();
        String jobId = requestVO.getJobId();
        String requestType = requestVO.getRequestType();
        String sql = requestVO.getSql();
        String responseType = requestVO.getResponseType();


        long t1 = System.currentTimeMillis();
        log.info("excute sql start ...");
        log.info(sql);

        boolean res = stat.execute(sql);
        if (!res) {//不返回ResultSet 集
            log.info("不返回ResultSet 集");
            return ResponseVO.builder()
                    .columns(null)
                    .data(null)
                    .success(true)
                    .errorMsg("")
                    .build();
        }
        ResultSet rs = stat.executeQuery(sql);
        long t2 = System.currentTimeMillis();
        log.info("get ResultSet success! consume time " + (t2 - t1) / 1000 + "s");
        log.info("send ResultSet to " + responseType + " ...");
        ResultSetMetaData md = rs.getMetaData(); // 得到结果集(rs)的结构信息，比如字段数、字段名等
        int columnCount = md.getColumnCount(); // 返回此 ResultSet 对象中的列数
        List<String> columns = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
            columns.add(md.getColumnLabel(i));
        }
        log.info("columnCount:{}", columnCount);
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Map<String, Object> rowData = null;
        int batchNum = 0;
        while (rs.next()) {
            rowData = new LinkedHashMap<String, Object>(columnCount);
            for (String column : columns) {
                rowData.put(column, rs.getObject(column));
            }
            result.add(rowData);
            batchNum++;
            if (ResponseType.KAFKA.toString().equalsIgnoreCase(responseType)) {
                //TODO 发送到kafka,以后再开发
            } else if (ResponseType.RPC.toString().equalsIgnoreCase(responseType) && batchNum == this.executorProperties.getResponseSize()) {
                log.info("query batchNum is " + batchNum + " ,and query while break!");
                break;
            } else if (ResponseType.REDIS.toString().equalsIgnoreCase(responseType)) {
                //TODO 存储redis,以后再开发
            }
        }
        if (batchNum == 0) {//查询结果集为空
            if (ResponseType.KAFKA.toString().equalsIgnoreCase(responseType)) {
                log.info(" 结果集为空！！");
            } else if (ResponseType.REDIS.toString().equalsIgnoreCase(responseType)) {
                //TODO 存储redis,以后再开发
            }
        }

        log.info("ResultSet'size " + batchNum + " , and result size is " + result.size() + " ,and ResponseSize is " + this.executorProperties.getResponseSize());
        return ResponseVO.builder()
                .columns(columns)
                .data(result)
                .success(true)
                .errorMsg("")
                .build();
    }

    /**
     * Presto-jdbc调用
     */
    @SneakyThrows
    protected ResponseVO executeQuery(RequestVO requestVO, Statement stat) {
        long requestTime = requestVO.getRequestTime();
        String username = requestVO.getUsername();
        String jobId = requestVO.getJobId();
        String requestType = requestVO.getRequestType();
        String sql = requestVO.getSql();
        String responseType = requestVO.getResponseType();

        long t1 = System.currentTimeMillis();
        log.info("excute sql start ...");
        log.info(sql);

        boolean res = stat.execute(sql);
        if (!res) {//不返回ResultSet 集
            return ResponseVO.builder()
                    .columns(null)
                    .data(null)
                    .success(true)
                    .errorMsg("")
                    .build();
        }

        ResultSet rs = stat.executeQuery(sql);
        long t2 = System.currentTimeMillis();
        log.info("get ResultSet success! consume time " + (t2 - t1) / 1000 + "s");
        log.info("send ResultSet to " + responseType + " ...");
        ResultSetMetaData md = rs.getMetaData(); // 得到结果集(rs)的结构信息，比如字段数、字段名等
        int columnCount = md.getColumnCount(); // 返回此 ResultSet 对象中的列数
        List<String> columns = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
            columns.add(md.getColumnLabel(i));
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Map<String, Object> rowData = null;
        int batchNum = 0;
        while (rs.next()) {
            rowData = new LinkedHashMap<String, Object>(columnCount);
            for (String column : columns) {
                rowData.put(column, rs.getObject(column));
            }
            result.add(rowData);
            batchNum++;
            if (ResponseType.KAFKA.toString().equalsIgnoreCase(responseType)) {
                //TODO 发送到kafka,以后再开发
            } else if (ResponseType.RPC.toString().equalsIgnoreCase(responseType) && batchNum == this.executorProperties.getResponseSize()) {
                break;
            } else if (ResponseType.REDIS.toString().equalsIgnoreCase(responseType)) {
                //TODO 存储redis,以后再开发
            }
        }
        if (batchNum == 0) {//查询结果集为空
            log.info(" 结果集为空！！");
            if (ResponseType.KAFKA.toString().equalsIgnoreCase(responseType)) {
                //TODO 发送到kafka,以后再开发
            } else if (ResponseType.REDIS.toString().equalsIgnoreCase(responseType)) {
                //TODO 存储redis,以后再开发
            }
        }

        log.info("ResultSet'size " + batchNum);
        return ResponseVO.builder()
                .columns(columns)
                .data(result)
                .success(true)
                .errorMsg("")
                .build();
    }


    /**
     * Mycat-jdbc,Hbase-jdbc调用,
     */
    @SneakyThrows
    protected ResponseVO executeQuery(RequestVO requestVO, JdbcTemplate jdbcTemplate) {
        long requestTime = requestVO.getRequestTime();
        String username = requestVO.getUsername();
        String jobId = requestVO.getJobId();
        String requestType = requestVO.getRequestType();
        String sql = requestVO.getSql();
        String responseType = requestVO.getResponseType();
        log.info("requestVO is " + requestVO.toString());

        long t1 = System.currentTimeMillis();
        log.info("jdbcTemplate excute sql start ...");
        log.info(sql);
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
        long t2 = System.currentTimeMillis();
        log.info("get ResultSet success! consume time " + (t2 - t1) / 1000 + "s");
        log.info("send ResultSet to " + responseType + " ...");
        SqlRowSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount();
        List<String> columns = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
            columns.add(md.getColumnLabel(i));
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Map<String, Object> rowData = null;
        int batchNum = 0;
        while (rs.next()) {
            rowData = new LinkedHashMap<String, Object>(columnCount);
            for (String column : columns) {
                rowData.put(column, rs.getObject(column));
            }
            result.add(rowData);
            batchNum++;
            if (ResponseType.KAFKA.toString().equalsIgnoreCase(responseType)) {
                //TODO 发送kafka,以后再开发
            } else if (ResponseType.RPC.toString().equalsIgnoreCase(responseType) && batchNum == this.executorProperties.getResponseSize()) {
                break;
            } else if (ResponseType.REDIS.toString().equalsIgnoreCase(responseType)) {
                //TODO 存储redis,以后再开发
            }
        }
        if (batchNum == 0) {//查询结果集为空
            if (ResponseType.KAFKA.toString().equalsIgnoreCase(responseType)) {
                //TODO 发送kafka,以后再开发
            } else if (ResponseType.REDIS.toString().equalsIgnoreCase(responseType)) {
                //TODO 存储redis,以后再开发
            }
        }
        log.info("ResultSet'size is " + batchNum);
        return ResponseVO.builder()
                .columns(columns)
                .data(result)
                .success(true)
                .errorMsg("")
                .build();
    }


}
