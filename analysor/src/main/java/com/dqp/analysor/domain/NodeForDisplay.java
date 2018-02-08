package com.dqp.analysor.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于返回给前端显示的节点对象
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeForDisplay {
    /**
     * The Value.
     */
    String value;
}
