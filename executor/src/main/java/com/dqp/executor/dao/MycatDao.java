package com.dqp.executor.dao;


import com.dqp.executor.vo.RequestVO;
import com.dqp.executor.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MycatDao extends BaseJdbcDao {


    @Autowired
    @Qualifier("mycatJdbcTemplate")
    private JdbcTemplate mycatJdbcTemplate;

    @Override
    public ResponseVO executeQuery(RequestVO requestVO) {
        return super.executeQuery(requestVO, this.mycatJdbcTemplate);
    }
}
