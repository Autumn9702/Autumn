package cn.autumn.util;

import cn.autumn.entity.mysql.WxUser;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;

import java.util.Objects;

/**
 * author: autumn
 * created in 2022/9/7 Class BeanUtil
 */
public class BeanUtil {

    public static <T> T getBean(T source, Class<T> clazz){
        if (Objects.isNull(source)) {
            source = SpringUtil.getBean(clazz);
        }
        return source;
    }

    public static String beanToMsgBody(String toUser, String content) {
        JSONObject obj = new JSONObject();
        obj.put("touser", toUser);
        obj.put("msgtype", "text");
        JSONObject text = new JSONObject();
        text.put("content",content);
        obj.put("text",text);
        return obj.toJSONString();
    }

    public static String generateTemplate(){
        StringBuffer template = new StringBuffer();
        template.append("命令面板\n");
        template.append("姓名-xxxx\n");
        template.append("生日-xxxx-xx-xx\n");
        template.append("地址-xx市xx区/县\n");
        template.append("开启通知-上午/中午/下午(ps:9点/12点/18点)\n");
        template.append("开启通知-HH:mm:ss\n");
        template.append("关闭通知-HH:mm:ss\n");
        template.append("查看通知\n");
        template.append("查看个人信息\n");
        template.append("关闭所有通知\n");
        return template.toString();
    }

    public static String beanToUserData(WxUser wxUser, String cityName) {
        if (Objects.isNull(wxUser)) {
            return "查询失败";
        }

        StringBuffer rst = new StringBuffer();
        rst.append("姓名: ").append(wxUser.getName()).append("\n");
        rst.append("生日: ").append(wxUser.getBirthday()).append("\n");
        rst.append("地址: ").append(Objects.isNull(cityName) ? "未设置" : cityName).append("\n");
        rst.append("通知时间: ").append(wxUser.getNotifyTime().isEmpty() ? "未设置" : "");
        wxUser.getNotifyTime().forEach(ny -> rst.append("\n  ").append(ny));
        return rst.toString();
    }
}
