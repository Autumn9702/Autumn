package cn.autumn.entity.business.wxserver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: autumn
 * created in 2022/9/7 Class NormalMsg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextMsg {

    private String touser;

    private String msgtype;

    private Map<String, Object> text;

    public TextMsg(String touser, String content) {
        this.touser = touser;
        this.msgtype = "text";
        this.text = new HashMap<>();
        this.text.put("content", content);
    }
}
