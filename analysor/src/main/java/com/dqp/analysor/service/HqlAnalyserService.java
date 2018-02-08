package com.dqp.analysor.service;

import com.dqp.analysor.domain.HqlOperationEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hive.ql.parse.*;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 *
 * 解析sql/hql语句，提取关键字信息，建立抽象语法树, 深度优先遍历语法树
 * This class is mainly to analysis sql/hql
 * using hive-exec.jar parse function to get an AST of the provided sql/hql
 * then recursion {ergodicStrategyDFS} 'deep first strategy' the tree
 *
 * Note: here may get some NULL POINT EXCEPTION it is because
 * in your current runtime environment's cannot find native function or .exe
 * that should be used when parsing a sql
 * to settle this exception you should give a explicit HADOOP_HOME/bin in your path
 */
@Getter
@Setter
@Slf4j
@Service
@NoArgsConstructor
public class HqlAnalyserService {
    /**
     * input hql String
     */
    private String hql;
    /**
     * the currentNode Operation type
     */
    private HqlOperationEnum currentNodeOperation;
    /**
     * the currentNode Operation table info
     */
    private String currentNodeTableTokenInfo;

    /**
     * is in a join block statements
     */
    private boolean isJoinClause = false;
    /**
     * life circle of a sql/hql  table info operation stack
     */
    private Stack<String> tableTokenInfo = new Stack<String>();
    /**
     * life circle of a sql/hql Operation  operation stack
     */
    private Stack<HqlOperationEnum> operationTokenInfo = new Stack<HqlOperationEnum>();

    /**
     * tmp mem to hold some tmp results
     */
    private Set<String> tables = new HashSet<String>();

    /**
     * alias
     */
    private Map<String, String> alias = new HashMap<String, String>();
    private Map<String, String> colAlias = new TreeMap<String, String>();
    private Map<String, String> cols = new TreeMap<String, String>();
    private static final String UNKNOWN = "UNKNOWN";
    /**
     * alias mapping <alisName,sourceName>
     */
    private Map<String, String> colsAlisMappingTables = new HashMap<String, String>();


    /**
     * ASTNode DEEP FIRST ERGODIC AST TREE
     */
    public Set<String> ergodicStrategyDFS(ASTNode astNode) {
        log.debug("ergodicStrategyDFS：：ASTNode  is {} ", astNode.toStringTree());
        Set<String> currentSet = new HashSet<String>();//current tableInfo&operationInfo set
        setCurrentOperationalTokenType(astNode);
        currentSet.addAll(recursiveChildNodeProcedure(astNode));
        currentSet.addAll(currentNodeParser(astNode, currentSet));
        endParseCurrentNode(astNode);
        return currentSet;
    }

    /**
     * ending  operations
     *
     * @param ast
     */
    private void endParseCurrentNode(ASTNode ast) {
        log.debug("endParseCurrentNode：：ASTNode  is {} ", ast.toStringTree());

        if (ast.getToken() != null) {
            switch (ast.getToken().getType()) {
                /**
                 * out form join block if it's ended
                 */
                case HiveParser.TOK_RIGHTOUTERJOIN:
                case HiveParser.TOK_LEFTOUTERJOIN:
                case HiveParser.TOK_JOIN:
                    isJoinClause = false;
                    break;
                case HiveParser.TOK_QUERY:
                    break;
                case HiveParser.TOK_LIKETABLE:
                    break;
                case HiveParser.TOK_INSERT:
                case HiveParser.TOK_SELECT:
                    currentNodeTableTokenInfo = tableTokenInfo.pop();
                    currentNodeOperation = operationTokenInfo.pop();
                    break;
            }
        }
    }

    /**
     * Current node operations
     *
     * @param astNode
     * @param currentSet
     *
     * @return
     */
    private Collection<? extends String> currentNodeParser(ASTNode astNode, Set<String> currentSet) {
        log.debug("currentNodeParser：：ASTNode  is : {} and currentSet is : {} ", astNode, currentSet.toArray().toString());

        if (astNode.getToken() != null) {
            int currentTokenType = astNode.getToken().getType();
            switch (currentTokenType) {
                case HiveParser.TOK_TABNAME:
                    if (astNode.getChildCount() == 2) {
                        String db = BaseSemanticAnalyzer
                                .getUnescapedName((ASTNode) astNode.getChild(0));
                        String table = BaseSemanticAnalyzer
                                .getUnescapedName((ASTNode) astNode.getChild(1));

                        if (currentNodeOperation == HqlOperationEnum.CREATETABLE
                                ) {
                            this.setCurrentNodeTableTokenInfo(db + "." + table);
                        }
                        tables.add(db + "." + table + "\t" + currentNodeOperation);
                    } else if (astNode.getChildCount() == 1) {  //NO DATABASE NAME
                        String tablename = BaseSemanticAnalyzer.getUnescapedName((ASTNode) astNode.getChild(0));
                        if (currentNodeOperation == HqlOperationEnum.CREATETABLE
                                ) {
                            this.setCurrentNodeTableTokenInfo(tablename);
                        }
                        tables.add(tablename + "\t" + currentNodeOperation);

                    }
                    break;
                case HiveParser.TOK_TABLE_PARTITION:
                    if (astNode.getChildCount() != 2) {
                        String table = BaseSemanticAnalyzer
                                .getUnescapedName((ASTNode) astNode.getChild(0));
                        if (currentNodeOperation == HqlOperationEnum.SELECT) {
                            this.setCurrentNodeTableTokenInfo(table);
                        }
                        tables.add(table + "\t" + currentNodeOperation);
                    }
                    break;
                case HiveParser.TOK_TAB:// outputTable
                    String tableTab = BaseSemanticAnalyzer
                            .getUnescapedName((ASTNode) astNode.getChild(0));
                    if (currentNodeOperation == HqlOperationEnum.SELECT) {
                        this.setCurrentNodeTableTokenInfo(tableTab);
                    }
                    tables.add(tableTab + "\t" + currentNodeOperation);
                    break;
                case HiveParser.TOK_TABREF:// inputTable
                    ASTNode tabTree = (ASTNode) astNode.getChild(0);
                    String tableName = (tabTree.getChildCount() == 1) ? BaseSemanticAnalyzer
                            .getUnescapedName((ASTNode) tabTree.getChild(0))
                            : BaseSemanticAnalyzer
                            .getUnescapedName((ASTNode) tabTree.getChild(0))
                            + "." + tabTree.getChild(1);
                    if (currentNodeOperation == HqlOperationEnum.SELECT) {
                        if (this.isJoinClause && !"".equals(this.currentNodeTableTokenInfo)) {
                            this.currentNodeTableTokenInfo += "&" + tableName;//
                        } else {
                            this.currentNodeTableTokenInfo = tableName;
                        }
                        currentSet.add(tableName);
                    }
                    tables.add(tableName + "\t" + currentNodeOperation);
                    if (astNode.getChild(1) != null) {
                        String alia = astNode.getChild(1).getText().toLowerCase();
                        alias.put(alia, tableName);
                    }
                    break;
                case HiveParser.TOK_TABLE_OR_COL:

                    /**
                     * alias operation
                     * if TOK_TABLE_OR_COL's parent node is belongs to select operation and
                     * have two child node then the second node must be alias information
                     */
                    if (astNode.getParent().getType() == HiveParser.TOK_SELEXPR
                            && null != astNode.getParent().getChild(1)) {
                        this.colsAlisMappingTables.put(
                                currentNodeTableTokenInfo + "." + astNode.getParent().getChild(1).getText().toLowerCase()
                                , currentNodeTableTokenInfo + "." + astNode.getChild(0).getText().toLowerCase());
                        break;
                    }
                    /**
                     * alis --- end
                     */
                    if (astNode.getParent().getType() != HiveParser.DOT) {
                        String col = astNode.getChild(0).getText().toLowerCase();
                        if (alias.get(col) == null
                                && colAlias.get(currentNodeTableTokenInfo + "." + col) == null) {
                            if (currentNodeTableTokenInfo.indexOf("&") > 0) {
                                cols.put(UNKNOWN + "." + col, "");
                            } else {
                                cols.put(currentNodeTableTokenInfo + "." + col, "");
                            }
                        }
                    } else {

                    }
                    break;
                case HiveParser.TOK_ALLCOLREF:
                    cols.put(currentNodeTableTokenInfo + ".*", "");
                    break;
                case HiveParser.TOK_SUBQUERY:
                    if (astNode.getChildCount() == 2) {
                        String tableAlias = unescapeIdentifier(astNode.getChild(1)
                                .getText());
                        String aliaReal = "";
                        for (String table : currentSet) {
                            aliaReal += table + "&";
                        }
                        if (aliaReal.length() != 0) {
                            aliaReal = aliaReal.substring(0, aliaReal.length() - 1);
                        }
                        alias.put(tableAlias, aliaReal);
                    }
                    break;


                case HiveParser.TOK_SELEXPR:
                    if (astNode.getChild(0).getType() == HiveParser.TOK_TABLE_OR_COL) {
                        String column = astNode.getChild(0).getChild(0).getText()
                                .toLowerCase();
                        if (currentNodeTableTokenInfo.indexOf("&") > 0) {
                            cols.put(UNKNOWN + "." + column, "");
                        } else if (colAlias.get(currentNodeTableTokenInfo + "." + column) == null) {
                            cols.put(currentNodeTableTokenInfo + "." + column, "");
                        }
                    } else if (astNode.getChild(1) != null) {
                        String columnAlia = astNode.getChild(1).getText().toLowerCase();
                        colAlias.put(currentNodeTableTokenInfo + "." + columnAlia, "");
//                        cols.remove(currentNodeTableTokenInfo + "." + columnAlia) ;
                    }
                    break;

                case HiveParser.DOT:
                    if (astNode.getType() == HiveParser.DOT) {
                        if (astNode.getChildCount() == 2) {
                            if (astNode.getChild(0).getType() == HiveParser.TOK_TABLE_OR_COL
                                    && astNode.getChild(0).getChildCount() == 1
                                    && astNode.getChild(1).getType() == HiveParser.Identifier) {
                                String alia = BaseSemanticAnalyzer
                                        .unescapeIdentifier(astNode.getChild(0)
                                                .getChild(0).getText()
                                                .toLowerCase());
                                String column = BaseSemanticAnalyzer
                                        .unescapeIdentifier(astNode.getChild(1)
                                                .getText().toLowerCase());
                                String realTable = null;
                                if (!tables.contains(alia + "\t" + currentNodeOperation)
                                        && alias.get(alia) == null) {// [b SELECT, a
                                    // SELECT]
                                    alias.put(alia, currentNodeTableTokenInfo);
                                }
                                if (tables.contains(alia + "\t" + currentNodeOperation)) {
                                    realTable = alia;
                                } else if (alias.get(alia) != null) {
                                    realTable = alias.get(alia);
                                }
                                if (realTable == null || realTable.length() == 0 || realTable.indexOf("&") > 0) {
                                    realTable = UNKNOWN;
                                }
                                /**
                                 *  filtered alias coz here we already lost alias information
                                 */
                                if (this.colsAlisMappingTables.size() > 0
                                        && null != (this.colsAlisMappingTables.get(realTable + "." + column))
                                        && /*alias and column are different*/!this.colsAlisMappingTables.get(realTable + "." + column).equals(realTable + "." + column)) {
                                    //// TODO: 03/08/2017 alis operation flow will add if necessary

                                } else {
                                    cols.put(realTable + "." + column, "");
                                }


                            }
                        }
                    }
                    break;

                case HiveParser.TOK_ALTERTABLE_ADDPARTS:
                case HiveParser.TOK_ALTERTABLE_RENAME:
                case HiveParser.TOK_ALTERTABLE_ADDCOLS:
                    ASTNode alterTableName = (ASTNode) astNode.getChild(0);
                    tables.add(alterTableName.getText() + "\t" + currentNodeOperation);
                    break;
            }
        }
        return currentSet;
    }

    /**
     * recursion the astNode
     *
     * @param astNode
     *
     * @return
     */
    private Collection<? extends String> recursiveChildNodeProcedure(ASTNode astNode) {
        Set<String> currentSet = new HashSet<String>();//current tableInfo&operationInfo set
        if (astNode.getChildCount() > 0) {
            for (int count = 0; count < astNode.getChildCount(); count++) {
                currentSet.addAll(ergodicStrategyDFS((ASTNode) astNode.getChild(count)));
            }
        }
        return currentSet;
    }

    /**
     * push the current node into stack
     * until all the information have done then pop stack
     *
     * @param astNode
     */
    private void setCurrentOperationalTokenType(ASTNode astNode) {
        if (null != astNode.getToken()) {
            int currentTokenType = astNode.getToken().getType();
            /**
             * TOK_ALTERDATABASE_PROPERTIES = 592;
             * TOK_ALTERVIEW_RENAME = 628;
             * in this range must be ALTER operation
             */
            if (currentTokenType >= HiveParser.TOK_ALTERDATABASE_PROPERTIES
                    && currentTokenType <= HiveParser.TOK_ALTERVIEW_RENAME) {
                this.setCurrentNodeOperation(HqlOperationEnum.ALTER);//是alter操作
            }
            /**
             * in this range must be JOIN operation
             */
            if (HiveParser.TOK_CROSSJOIN == currentTokenType ||
                    HiveParser.TOK_FULLOUTERJOIN == currentTokenType ||
                    HiveParser.TOK_JOIN == currentTokenType ||
                    HiveParser.TOK_LEFTOUTERJOIN == currentTokenType ||
                    HiveParser.TOK_LEFTSEMIJOIN == currentTokenType ||
                    HiveParser.TOK_MAPJOIN == currentTokenType ||
                    HiveParser.TOK_RIGHTOUTERJOIN == currentTokenType ||
                    HiveParser.TOK_UNIQUEJOIN == currentTokenType
                    ) {
                this.setJoinClause(true);
            }
            switch (currentTokenType) {
                case HiveParser.TOK_QUERY://THIS IS A QUERY BLOCK
                    this.tableTokenInfoStackPusher(true);//current operation/table name-->push stack
                    this.operationTokenInfoStackPusher();
                    setCurrentNodeOperation(HqlOperationEnum.SELECT);//set current stack operation type
                    break;
                case HiveParser.TOK_SELECT://THIS IS A SELECT BLOCK
                    this.tableTokenInfoStackPusher(false);//current operation/table name-->push stack
                    this.operationTokenInfoStackPusher();
                    setCurrentNodeOperation(HqlOperationEnum.SELECT);//set current stack operation type
                    break;
                case HiveParser.TOK_INSERT://THIS IS A INSERT BLOCK
                    this.tableTokenInfoStackPusher(false);//current operation/table name-->push stack
                    this.operationTokenInfoStackPusher();
                    setCurrentNodeOperation(HqlOperationEnum.INSERT);//set current stack operation type
                    break;
                case HiveParser.TOK_DROPTABLE:
                    setCurrentNodeOperation(HqlOperationEnum.DROP);// drop operation
                    break;
                case HiveParser.TOK_TRUNCATETABLE:
                    setCurrentNodeOperation(HqlOperationEnum.TRUNCATE);//truncate operation
                    break;
                case HiveParser.TOK_LOAD:
                    setCurrentNodeOperation(HqlOperationEnum.LOAD);//load operation
                    break;
                case HiveParser.TOK_CREATETABLE:
                    setCurrentNodeOperation(HqlOperationEnum.CREATETABLE);//create operation
                    break;
            }
        }
        astNode.getToken();
    }


    /**
     * Unescape identifier string.
     * examples:'abc'->abc
     *
     * @param val the val
     *
     * @return the string
     */
    public static String unescapeIdentifier(String val) {
        if (val == null) {
            return null;
        }
        if (val.charAt(0) == '`' && val.charAt(val.length() - 1) == '`') {
            val = val.substring(1, val.length() - 1);
        }
        return val;
    }

    /**
     * construct method
     *
     * @param hql
     */
    public HqlAnalyserService(String hql) throws ParseException {
        log.debug("HqlAnalyserService：：input sql is {} ", hql);
        this.setHql(hql);
        ergodicStrategyDFS(getAst());
        log.debug("HqlAnalyserService：：parser success");

    }

    /**
     * taking the advantage of hive.ParseDriver
     * then we can get the ASTNode of this hql/sql
     * You can check HiveParser.tokenNames all types
     *
     */
    public ASTNode getAst() throws ParseException {
        ParseDriver parseDriver = new ParseDriver();
        return parseDriver.parse(this.getHql());
    }


    /**
     * tableTokenInfoStackPusher
     */
    private void tableTokenInfoStackPusher(boolean isClean) {

        if (isClean) {
            this.getTableTokenInfo().push(this.getCurrentNodeTableTokenInfo());
            this.setCurrentNodeTableTokenInfo("");
        } else {
            this.getTableTokenInfo().push(this.getCurrentNodeTableTokenInfo());
        }

    }

    private void operationTokenInfoStackPusher() {
        this.getOperationTokenInfo().push(currentNodeOperation);
    }

}
