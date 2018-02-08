package com.dqp.analysor.service;

import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

/**
 * 使用druid api解析SQL
 * This class can analysis SQL using druid api
 * It can explain that which tables will be involved witch which operation [select,insert,delete]
 * Moreover,it also can let you know the which attributes will be involved [query,modify] AND[column1,column2...]
 */
@Getter
@Setter
@Service
@NoArgsConstructor
public abstract class SqlParserService {

    private String inputSql;
    private MySqlStatementParser parser;

    public SqlParserService(String inputSql) {
        setInputSql(inputSql);
        setParser(new MySqlStatementParser(inputSql));// parser得到AST;
    }


}
