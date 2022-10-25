package cn.autumn.model;

import lombok.Data;

/**
 * @author: autumn
 * created in 2022/9/11 Class LableValueModel
 */
@Data
public class LabelValueModel<K, V> {

    private K label;

    private V value;

    public LabelValueModel() {}

    public LabelValueModel(K label, V value) {
        this.label = label;
        this.value = value;
    }
}
