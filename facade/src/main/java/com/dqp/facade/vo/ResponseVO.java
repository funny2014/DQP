package com.dqp.facade.vo;

import lombok.*;

import java.util.List;
import java.util.Map;

@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseVO {
    /**
     * sql查询的结果集
     */
    protected List<Map<String, Object>> data;
    /**
     * 查询的结果集的字段列表
     */
    protected List<String> columns;

    /**
     * sql执行是否成功
     */
    protected boolean success;
    /**
     * sql执行出错的错误信息
     */
    protected String errorMsg;
}
