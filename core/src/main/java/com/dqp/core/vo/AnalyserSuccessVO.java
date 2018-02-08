package com.dqp.core.vo;


import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@ToString
@EqualsAndHashCode
@Data
public class AnalyserSuccessVO {
    /**
     * 操作是否成功!true成功；flase失败
     */
    private boolean success;
    /**
     * 成功信息或者失败信息
     */
    private MySqlSchemaStatVisitor msg;
}
