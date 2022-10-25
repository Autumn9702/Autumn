package cn.autumn.wxserver.config.exp;

import lombok.Data;

/**
 * @author: autumn
 * created in 2022/9/10 Class DynamicTaskException
 */
@Data
public final class DynamicTaskException extends RuntimeException{

    private String msg;

    public DynamicTaskException() {}

    public DynamicTaskException(String msg) {
        this.msg = msg;
    }
}
