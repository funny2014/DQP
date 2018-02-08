package com.dqp.analysor.service;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Map;


@Getter
@Setter
@Service
@NoArgsConstructor
public class SchemaStatService extends SqlParserService {
    private SQLStatement statement;
    private MySqlSchemaStatVisitor visitor;


    public SchemaStatService(String inputSql) {
        super(inputSql);
        this.statement = super.getParser().parseStatement();
        this.statement.accept(visitor = new MySqlSchemaStatVisitor());// 将ast通过visitor输出
    }

    /**
     * 返回所有被操作的Table names
     *
     * @return all operate tables
     */
    public Map getAllOperateTables() {
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("tables : "+this.visitor.getTables()+"\n");//tables
//        stringBuilder.append("columns : "+this.visitor.getColumns()+"\n");//getColumns
//        stringBuilder.append("conditions : "+this.visitor.getConditions()+"\n");//getConditions
//        stringBuilder.append("relationships : "+this.visitor.getRelationships()+"\n");//getRelationships
//        stringBuilder.append("groupbycolumns : "+this.visitor.getGroupByColumns()+"\n");//getGroupByColumns
//        stringBuilder.append("orderbycolumns : "+this.visitor.getOrderByColumns()+"\n");//getOrderByColumns
//        stringBuilder.append("dbtype : "+this.visitor.getDbType()+"\n");
//        stringBuilder.append("parameters : "+this.visitor.getParameters());
        return this.visitor.getTables();
    }


    /**
     * Gets all state.
     */
    public MySqlSchemaStatVisitor getAllState() {
        return visitor;
    }

}
