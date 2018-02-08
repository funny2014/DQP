package com.dqp.executor.dao;

import com.dqp.executor.util.DBConnection;
import com.dqp.executor.util.StringUtil;
import com.dqp.executor.vo.RequestVO;
import com.dqp.executor.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Repository;

import java.sql.*;


@Repository
@Slf4j
public class PrestoDao  extends BaseJdbcDao{

    @Autowired
    private DataSourceProperties prestoDataSourceProperties;

    @Override
    public ResponseVO executeQuery(RequestVO requestVO) {
        try {
            Class.forName(this.prestoDataSourceProperties.getDriverClassName());
        } catch (ClassNotFoundException e) {
            log.error(StringUtil.getTrace(e));
        }
        Connection con = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            String userGroup = requestVO.getPassword();
            con = DriverManager.getConnection(this.prestoDataSourceProperties.getUrl(), userGroup,null);
            stat = con.createStatement();
            return super.executeQuery(requestVO, stat);
        } catch (SQLException e) {
            log.error(StringUtil.getTrace(e));
            return super.responseFailed(requestVO, e);
        } finally {
                DBConnection.close(rs, stat, con);
        }
    }
}
