package cn.autumn.entity.business;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: autumn
 * created in 2022/9/9 Class TaskQueue
 */
@Data
public final class  ExeSeq<V> {

    private Long executorTime;

    private List<V> values;

    public ExeSeq() {
        this.values = new ArrayList<>();
    }

    public ExeSeq(Long executorTime, V... values) {
        this.executorTime = executorTime;
        this.values = new ArrayList<>();
        this.values.addAll(Arrays.asList(values));
    }

    public ExeSeq(Long executorTime, List<V> values) {
        this.executorTime = executorTime;
        this.values = values;
    }
}
