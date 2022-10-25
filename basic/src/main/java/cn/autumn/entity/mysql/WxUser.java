package cn.autumn.entity.mysql;

import cn.autumn.entity.mybatis.DataBase;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * author: autumn
 * created in 2022/9/8 Class WxUser
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "t_wx_user_server", autoResultMap = true)
public final class WxUser extends DataBase {

    private String toUser;

    private String name;

    private String birthday;

    private Integer cityCode;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> notifyTime;
}
