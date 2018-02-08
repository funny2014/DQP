package com.dqp.executor.dao;

import com.dqp.executor.util.DBConnection;
import com.dqp.executor.util.StringUtil;
import com.dqp.executor.vo.RequestVO;
import com.dqp.executor.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class LatestSqlsDao  {

    @Qualifier("MysqlDataSourceProperties")
    @Autowired
    private DataSourceProperties mysqlDataSourceProperties;


    private Connection getConnection(){
        try {
            Class.forName(this.mysqlDataSourceProperties.getDriverClassName());
        } catch (ClassNotFoundException e) {
            log.error(StringUtil.getTrace(e));
        }
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(this.mysqlDataSourceProperties.getUrl(), this.mysqlDataSourceProperties.getUsername(), this.mysqlDataSourceProperties.getPassword());
        } catch (SQLException e) {
            log.error(StringUtil.getTrace(e));
        }  
        return conn;
    }



    //分页显示日志操作：获得指定用户所执行sql一页的历史记录
    public ResponseVO executeQuery(String username, int pageNo, int pageSize) {
        Connection conn = getConnection();
        
        PreparedStatement stat = null;
        ResultSet rs = null;
        int count = 0;
        try {
            //获得指定用户所执行的SQL条数
            conn.setAutoCommit(false);
            String sql_sum = "select count(*) from sqlstable where username = ? order by createtime desc limit 100";
            stat =  conn.prepareStatement(sql_sum);
            stat.setString(1,username);
            rs = stat.executeQuery();
            while (rs.next()) {
                count =rs.getInt(1);
            }
            List<String> count_list = new ArrayList<>();
            count_list.add(String.valueOf(count));
            conn.commit();
            rs.close();
            stat.close();

            //获得指定用户所执行的SQL记录
            int start = (pageNo-1)*pageSize;
            if(pageNo > 10){  //只显示10页
                return ResponseVO.builder()
                        .success(false)
                        .errorMsg("没有更多数据")
                        .build();
            }
            String sql = "select * from sqlstable where username = ? order by createtime desc limit ?,?";
            stat = (PreparedStatement) conn.prepareStatement(sql);
            stat.setString(1,username);
            stat.setInt(2,start);
            stat.setInt(3,pageSize);
            rs = stat.executeQuery();

            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            List<String> columns = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                columns.add(md.getColumnLabel(i));
            }
            List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
            Map<String, Object> rowData = null;
            int batchNum = 0;
            while (rs.next()) {
                rowData = new LinkedHashMap<>(columnCount);
                for (String column : columns) {
                    rowData.put(column, rs.getObject(column));
                }
                result.add(rowData);
                batchNum++;

            }
            conn.commit();

            log.info("get latestsqls ResultSet'size " + batchNum);

            return ResponseVO.builder()
                    .columns(count_list)
                    .data(result)
                    .success(true)
                    .errorMsg("")
                    .build();
        } catch (SQLException e) {
            log.error(StringUtil.getTrace(e));
            return ResponseVO.builder()
                    .success(false)
                    .errorMsg(StringUtil.getTrace(e))
                    .build();
        } finally {
                DBConnection.close(rs, stat, conn);
        }
    }


    //保存用户执行的sql记录
    public ResponseVO insert(RequestVO requestVO){
        Connection conn = getConnection();
        PreparedStatement  stat = null;
        String sql = "insert into sqlstable (username,createtime,dbEngine,sqlStr) values (?,?,?,?) ";

        try{
            java.util.Date nowdate=new java.util.Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = sdf.format(nowdate);
            Long t = sdf.parse(format).getTime();

            stat = conn.prepareStatement(sql);
            stat.setString(1,requestVO.getUsername());
            stat.setString(2,format);
            stat.setString(3,requestVO.getDbEngine());
            stat.setString(4,requestVO.getSql());

            int res = stat.executeUpdate();
            if(res == 0){
                return ResponseVO.builder()
                        .columns(null)
                        .data(null)
                        .success(false)
                        .errorMsg("insert error sql is " + requestVO.getSql())
                        .build();
            }

        } catch (Exception e) {
            log.error(StringUtil.getTrace(e));
            return ResponseVO.builder()
                    .success(false)
                    .errorMsg(StringUtil.getTrace(e))
                    .build();
        } finally {
                DBConnection.close(stat, conn);
        }
        return ResponseVO.builder()
                .columns(null)
                .data(null)
                .success(true)
                .errorMsg("")
                .build();

    }


}
