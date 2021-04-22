package cn.jrymos.spring.custom.injection.ccc.model;

public interface Animal extends Comparable {

    @Override
    default int compareTo(Object o) {
        if (!(o instanceof Animal)) {
            return -1;
        }
        return getClass().getSimpleName().compareTo(o.getClass().getSimpleName());
    }
}
