package cn.autumn.wxserver.model;

import lombok.Data;

import java.util.List;

/**
 * @author: autumn
 * created in 2022/9/10 Class RespModel
 */
@Data
public class RespModel<V> {

    private int code;

    private String msg;

    private Data<V> data;

    @lombok.Data
    public static class Data<V> {
        private List<V> coords;
    }
}
