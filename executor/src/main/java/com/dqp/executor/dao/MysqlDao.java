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


@Repository
@Slf4j
public class MysqlDao extends BaseJdbcDao {

    @Qualifier("MysqlDataSourceProperties")
    @Autowired
    private DataSourceProperties mysqlDataSourceProperties;

    @Override
    public ResponseVO executeQuery(RequestVO requestVO) {
        try {
            Class.forName(this.mysqlDataSourceProperties.getDriverClassName());
        } catch (ClassNotFoundException e) {
            log.error(StringUtil.getTrace(e));
        }
        Connection con = null;
        PreparedStatement stat = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection(this.mysqlDataSourceProperties.getUrl(), this.mysqlDataSourceProperties.getUsername(), this.mysqlDataSourceProperties.getPassword());
            stat = con.prepareStatement(requestVO.getSql());
            return super.executeQuery(requestVO, stat);
        } catch (SQLException e) {
            log.error(StringUtil.getTrace(e));
            return super.responseFailed(requestVO, e);
        } finally {
                DBConnection.close(rs, stat, con);
        }
    }
}
