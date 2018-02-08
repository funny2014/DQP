package com.dqp.core.vo;

import lombok.*;

@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AnalysorResponseVO {

    /**
     * sql执行是否成功
     */
    private boolean success;
    /**
     * sql执行出错的错误信息
     */
    private String errorMsg;
}
