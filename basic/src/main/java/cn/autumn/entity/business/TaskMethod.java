package cn.autumn.entity.business;

import lombok.Data;

/**
 * @author: autumn
 * created in 2022/9/9 Class TaskMethod
 */
@Data
public final class TaskMethod {

    private String methodName;

    private String methodCron;

    public TaskMethod() {}

    public TaskMethod(String methodName, String methodCron) {
        this.methodName = methodName;
        this.methodCron = methodCron;
    }
}
