package cn.autumn.entity.business;

import lombok.Data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author: autumn
 * created in 2022/9/9 Class TaskClazz
 */
@Data
public final class TaskClazz {

    private String className;

    private List<TaskMethod> methods;

    public TaskClazz() {}

    public TaskClazz(String className, String methodName, String methodCron) {
        this.className = className;
        this.methods = Collections.singletonList(new TaskMethod(methodName, methodCron));
    }

    public TaskClazz(String className, TaskMethod... methods) {
        this.className = className;
        this.methods = Arrays.asList(methods);
    }
}
