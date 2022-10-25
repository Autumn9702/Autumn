package cn.autumn.wxserver.config.exp;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: autumn
 * created in 2022/9/8 Class OrderException
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OrderException extends RuntimeException{

    private String toUser;

    private String msgText;

    public OrderException(String toUser, String msgText) {
        this.toUser = toUser;
        this.msgText = msgText;
    }
}
