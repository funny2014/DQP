package com.dqp.analysor.domain;

import lombok.*;

/**
 * 图的边
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Links {
    /**
     * The Source.
     */
    String source;
    /**
     * The Target.
     */
    String target;
    /**
     * The Name.
     */
    String name;


     @Override
    public boolean equals(Object o) {
        if (null != o) {
            Links links = (Links) o;
            if (links.getName().equals(this.getName())
                    && links.getSource().equals(this.getSource())
                    && links.getTarget().equals(this.getTarget())) {
                return true;
            } else return false;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = source != null ? source.hashCode() : 0;
        result = 31 * result + (target != null ? target.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
