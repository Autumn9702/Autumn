package cn.autumn.wxserver.mapper;

import cn.autumn.entity.mysql.WxUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * author: autumn
 * created in 2022/9/8 Class WxUserMapper
 */
@Repository
public interface WxUserMapper extends BaseMapper<WxUser> {

    WxUser queryWxUserByUser(String toUser);

    List<WxUser> queryWxUserByListToUser(@Param("list") List<String> toUsers);
}
