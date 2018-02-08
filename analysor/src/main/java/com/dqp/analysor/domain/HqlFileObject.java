package com.dqp.analysor.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Hql文件的对象类
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HqlFileObject {

    String fileName;
    Time createTime;
    Time updateTime;
    Set<String> tablesAndOper;
    Set<String> inputTable;
    Set<String> outputTable;
    Map<String, String> cols;
    Map<String, String> colAlias;
    Map<String, String> colsAlisMappingTables;
    String sqlDetail;


    public Set<String> getInputTable() {
        Set<String> t = new HashSet<String>();
        for (String s : this.getTablesAndOper()
                ) {
            if (s.split("\t")[1].equals(HqlOperationEnum.CREATETABLE.name())
                    || s.split("\t")[1].equals(HqlOperationEnum.INSERT.name())) {
            } else {
                t.add(s.split("\t")[0]);
            }
        }

        return t;
    }


    public Set<String> getOutputTable() {
        Set<String> t = new HashSet<String>();
        for (String s : this.getTablesAndOper()
                ) {
            if (s.split("\t")[1].equals(HqlOperationEnum.CREATETABLE.name())
                    || s.split("\t")[1].equals(HqlOperationEnum.INSERT.name())) {
                t.add(s.split("\t")[0]);
            }
        }

        return t;
    }

}
