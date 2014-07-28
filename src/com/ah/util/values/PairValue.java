package com.ah.util.values;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PairValue<V, D> {
    private V value;
    private D desc;

    public PairValue(V v, D d) {
        this.value = v;
        this.desc = d;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public D getDesc() {
        return desc;
    }

    public void setDesc(D desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("{").append(this.value).append(", ")
                .append(this.desc).append("}").toString();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.value).append(this.desc).hashCode();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if(null == obj) {
            return false;
        }
        if(obj instanceof PairValue<?, ?>) {
            return new EqualsBuilder()
            .append(this.value, ((PairValue<V, D>)obj).value)
            .append(this.desc, ((PairValue<V, D>)obj).desc)
            .isEquals();
        } else {
            return false;
        }
    }
}
