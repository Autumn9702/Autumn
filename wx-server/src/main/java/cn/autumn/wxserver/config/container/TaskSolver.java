package cn.autumn.wxserver.config.container;

import cn.autumn.wxserver.service.interfaces.TaskService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * author: autumn
 * created in 2022/9/9 Class TaskSolver
 */
@Component
public class TaskSolver implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final Map<String, TaskService> taskClassSolver = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    private void registerTaskToSolver() {
        applicationContext.getBeansOfType(TaskService.class).values().forEach(
                clazz -> this.taskClassSolver.put(clazz.getClassName(), clazz));
    }

    public TaskService getTaskClass(String className) {
        return this.taskClassSolver.get(className);
    }
}
