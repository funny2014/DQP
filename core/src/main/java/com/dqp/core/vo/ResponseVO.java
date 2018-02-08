package com.dqp.core.vo;

import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * Created by 刘江平 on 2016-07-27.
 */
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseVO {
    /**
     * sql查询的结果集
     */
    private List<Map<String, Object>> data;
    /**
     * 查询的结果集的字段列表
     */
    private List<String> columns;

    /**
     * sql执行是否成功
     */
    private boolean success;
    /**
     * sql执行出错的错误信息
     */
    private String errorMsg;
}
