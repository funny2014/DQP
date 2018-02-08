package com.dqp.analysor.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  图的点
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Nodes {
    /**
     * The Category.
     */
    int category;// 0 targetTable   1 task/sql   2 table
    String name;
    int value;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Nodes nodes = (Nodes) o;
//        if (category != nodes.category) return false;
        if (value != nodes.value) return false;
        return name != null ? name.equals(nodes.name) : nodes.name == null;
    }

    @Override
    public int hashCode() {
        int result = value;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + value;
        return result;
    }
}
