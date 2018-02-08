package com.dqp.executor.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * JDBC连接工具类
 */
@Slf4j
public class DBConnection {

    // 关闭有结果集数据库
    public static void close(ResultSet rs, Statement stat, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stat != null) {
                stat.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            log.error(StringUtil.getTrace(e));
        }
    }


    // 关闭没有结果集
    public static void close(Statement stat, Connection conn) {
        try {
            if (stat != null) {
                stat.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            log.error(StringUtil.getTrace(e));
        }
    }


}
