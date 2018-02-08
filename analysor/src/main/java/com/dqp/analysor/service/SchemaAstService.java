package com.dqp.analysor.service;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Service
@NoArgsConstructor
public class SchemaAstService extends SqlParserService {
    private List<SQLStatement> stmtList;

    private List<String> selectList;
    private StringBuffer select;
    private StringBuffer from;
    private StringBuffer where;
    private StringBuffer order;
    private StringBuffer group;
    private StringBuffer limit;


    public SchemaAstService(String inputSql) {
        super(inputSql);
        stmtList = super.getParser().parseStatementList();
        selectList = new LinkedList();
        select = new StringBuffer();
        from = new StringBuffer();
        where = new StringBuffer();
        order = new StringBuffer();
        group = new StringBuffer();
        limit = new StringBuffer();

    }

    /**
     * Gets all first level ast.
     */
    public Map getAllFirstLevelAST() {
        SQLASTOutputVisitor selectVisitor = SQLUtils.createFormatOutputVisitor(select, stmtList, JdbcUtils.MYSQL);
        SQLASTOutputVisitor fromVisitor = SQLUtils.createFormatOutputVisitor(from, stmtList, JdbcUtils.MYSQL);
        SQLASTOutputVisitor whereVisitor = SQLUtils.createFormatOutputVisitor(where, stmtList, JdbcUtils.MYSQL);
        SQLASTOutputVisitor orderVisitor = SQLUtils.createFormatOutputVisitor(order, stmtList, JdbcUtils.MYSQL);
        SQLASTOutputVisitor limitVisitor = SQLUtils.createFormatOutputVisitor(limit, stmtList, JdbcUtils.MYSQL);
        SQLASTOutputVisitor groupVisitor = SQLUtils.createFormatOutputVisitor(group, stmtList, JdbcUtils.MYSQL);

        stmtList.forEach(
                stmt -> {
                    if (stmt instanceof SQLSelectStatement) {

                        /**
                         * first level select action
                         */
                        SQLSelectStatement sstmt = (SQLSelectStatement) stmt;
                        SQLSelect sqlselect = sstmt.getSelect();
                        MySqlSelectQueryBlock query = (MySqlSelectQueryBlock) sqlselect.getQuery();
                        query.getSelectList().forEach(item -> {
                            int size = select.length();
                            item.accept(selectVisitor);
                            selectList.add(select.substring(size));
                        });

                        /**
                         * first level from action
                         */
                        query.getFrom().accept(fromVisitor);

                        /**
                         * first level where action
                         */
                        if (null != query.getWhere()) {
                            query.getWhere().accept(whereVisitor);
                        }
                        /**
                         * first level order action
                         */
                        if (null != query.getOrderBy()) {
                            query.getOrderBy().accept(orderVisitor);
                        }
                        /**
                         * first level limit action
                         */
                        if (null != query.getLimit()) {
                            query.getLimit().accept(limitVisitor);
                        }
                        /**
                         * first level group action
                         */
                        if (null != query.getGroupBy()) {
                            query.getGroupBy().accept(groupVisitor);
                        }

                    }
                });
        HashMap hashMap = new HashMap();
        hashMap.put("from", from);
        hashMap.put("select", selectList);
        hashMap.put("where", where);
        hashMap.put("order", order);
        hashMap.put("group", group);
        hashMap.put("limit", limit);
        return hashMap;
    }
}
