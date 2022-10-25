package cn.autumn.wxserver.config;

import cn.autumn.entity.business.ExeSeq;
import cn.autumn.entity.business.TaskClazz;
import cn.autumn.entity.business.TaskMethod;
import cn.autumn.util.DateUtil;
import cn.autumn.util.Util;
import cn.autumn.wxserver.config.container.TaskSolver;
import cn.autumn.wxserver.service.interfaces.TaskService;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;

/**
 * author: autumn
 * created in 2022/9/9 Class DynamicTask
 */
@Service
public class DynamicTask implements SchedulingConfigurer {

    private volatile ScheduledTaskRegistrar registrar;

    private final Map<String, ScheduledFuture<?>> taskFuture = new ConcurrentHashMap<>();

    private final Map<String, CronTask> executedTask = new ConcurrentHashMap<>();

    public static final Map<String, List<ExeSeq<String>>> taskExecutorQueue = new ConcurrentHashMap<>();

    @Resource
    private TaskSolver taskSolver;


    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        registrar.setScheduler(Executors.newScheduledThreadPool(2));
        this.registrar = registrar;
    }

    public void addQueue(String className, String methodName, List<ExeSeq<String>> taskQueue) {

        if (taskExecutorQueue.containsKey(methodName)) {
            taskExecutorQueue.remove(methodName);
            taskStop(methodName);
        }

        taskExecutorQueue.put(methodName, taskQueue);

        addTask(new TaskClazz(className, methodName, DateUtil.getCron(taskQueue.get(0).getExecutorTime())));

    }

    public void addQueue(String className, String methodName, ExeSeq<String> value){

        List<ExeSeq<String>> executorSequence = taskExecutorQueue.get(methodName);
        boolean methodListIsNull = false;
        if(executorSequence == null) {
            methodListIsNull = true;
            executorSequence = new ArrayList<>();
        }
        boolean reloadTask = false;
        int size = executorSequence.size();
        if (size == 0){
            executorSequence.add(value);
            reloadTask = true;
        }else {
            boolean notAdded = true;
            for (int index = 0; index < size; index++) {
                if (executorSequence.get(index).getExecutorTime().equals(value.getExecutorTime())) {
                    List<String> executeValue = executorSequence.get(index).getValues();
                    executeValue.removeIf(v -> value.getValues().contains(v));
                    executeValue.addAll(value.getValues());
                    notAdded = false;
                    break;
                }

                if (value.getExecutorTime() < executorSequence.get(index).getExecutorTime()) {
                    executorSequence.add(index, value);
                    notAdded = false;
                    /* If the caret position is in the first */
                    if (index == 0) {
                        reloadTask = true;
                    }
                    break;
                }
            }

            if (notAdded){
                executorSequence.add(value);
            }
        }

        if (methodListIsNull) {
            taskExecutorQueue.put(methodName, executorSequence);
        }

        if (reloadTask){
            addTask(new TaskClazz(className, methodName, DateUtil.getCron(value.getExecutorTime())));
        }

    }

    public void deleteTaskToQueue(String className, String methodName, ExeSeq<String> value){

        boolean closeTask = false;
        boolean reloadTask = false;
        List<ExeSeq<String>> executorSequence = taskExecutorQueue.get(methodName);
        if (CollectionUtils.isEmpty(executorSequence)) {
            executorSequence = new ArrayList<>();
        }
        int size = executorSequence.size();
        for (int index = 0; index < size; index++) {
            if(executorSequence.get(index).getExecutorTime().equals(value.getExecutorTime())){
                if(executorSequence.get(index).getValues().size() == value.getValues().size()){
                    if(executorSequence.get(index).getValues().equals(value.getValues())){
                        executorSequence.remove(index);
                        if(index == 0){
                            if(executorSequence.size() == 0){
                                closeTask = true;
                                break;
                            }
                            reloadTask = true;
                        }
                    }
                }else {
                    executorSequence.get(index).getValues().removeAll(value.getValues());
                }
                break;
            }

        }

        if (closeTask){
            taskExecutorQueue.remove(methodName);
            taskStop(methodName);
        }

        if(reloadTask){
            addTask(new TaskClazz(className, methodName, DateUtil.getCron(executorSequence.get(0).getExecutorTime())));
        }

    }

    public void nextTask(String className, String methodName) {
        if (!taskExecutorQueue.containsKey(methodName)) {
            Util.log().info("Task not exist.");
            return;
        }
        taskExecutorQueue.get(methodName).remove(0);
        if(taskExecutorQueue.get(methodName).isEmpty()) {
            Util.log().info("All task completed.");
            taskStop(methodName);
            taskExecutorQueue.remove(methodName);
            return;
        }
        addTask(new TaskClazz(className, methodName, DateUtil.getCron(taskExecutorQueue.get(methodName).get(0).getExecutorTime())));
    }

    public void addTask(TaskClazz... taskClass) {
        for (TaskClazz tc : taskClass) {
            TaskService taskService = taskSolver.getTaskClass(tc.getClassName());
            for (TaskMethod taskMethod : tc.getMethods()) {
                if (!StringUtils.hasLength(tc.getClassName())) {
                    return;
                }
                if (taskFuture.containsKey(taskMethod.getMethodName()) &&
                        executedTask.get(taskMethod.getMethodName()).getExpression().equals(taskMethod.getMethodCron())) {
                    return;
                }
                if (taskFuture.containsKey(taskMethod.getMethodName())) {
                    taskFuture.get(taskMethod.getMethodName()).cancel(false);
                    taskFuture.remove(taskMethod.getMethodName());
                    executedTask.remove(taskMethod.getMethodName());
                }
                CronTask cronTask = createTask(taskMethod, taskService);
                ScheduledFuture<?> future = Objects.requireNonNull(registrar.getScheduler()).schedule(cronTask.getRunnable(), cronTask.getTrigger());
                executedTask.put(taskMethod.getMethodName(), cronTask);
                taskFuture.put(taskMethod.getMethodName(), future);
            }
        }
    }

    public void taskStop(String... taskNames) {
        for (String taskName : taskNames) {
            if (taskFuture.containsKey(taskName)) {
                taskFuture.get(taskName).cancel(false);
                taskFuture.remove(taskName);
                executedTask.remove(taskName);
            }
        }
    }

    public Map<String, CronTask> getTasks() {
        return executedTask;
    }

    public List<ExeSeq<String>> getTaskQueue(String methodName) {
        return taskExecutorQueue.get(methodName);
    }

    private CronTask createTask(TaskMethod taskMethod, TaskService taskService) {
        return new CronTask(() -> {
            try {
                Method declaredMethod = taskService.getClass().getDeclaredMethod(taskMethod.getMethodName());
                declaredMethod.invoke(taskService.getClass().getDeclaredConstructor().newInstance());
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }, taskMethod.getMethodCron());
    }
}
