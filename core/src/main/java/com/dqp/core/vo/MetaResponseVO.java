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
public class MetaResponseVO {

    /**
     * 返回表所在的数据库层：<表名,<查询引擎,数据库层>>
     */
    private Map<String, Map<String, String>> databases;
    /**
     * 返回表的列名：<表名,<查询引擎,[列1,列2,列3]>>
     */
    private Map<String, Map<String, List<String>>> columns;

    /**
     * sql执行是否成功
     */
    private boolean success;
    /**
     * sql执行出错的错误信息
     */
    private String errorMsg;
}
