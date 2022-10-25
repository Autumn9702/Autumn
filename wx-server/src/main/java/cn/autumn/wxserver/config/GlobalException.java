package cn.autumn.wxserver.config;

import cn.autumn.util.BeanUtil;
import cn.autumn.wxserver.config.exp.OrderException;
import cn.autumn.wxserver.server.WechatMessageServe;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.annotation.Resource;

/**
 * @author: autumn
 * created in 2022/9/8 Class GolabException
 */
@ControllerAdvice
public class GlobalException {

    @Resource
    private WechatMessageServe msgService;

    @ExceptionHandler(OrderException.class)
    public void orderException(OrderException oe) {
        if (oe.getToUser() == null || oe.getMsgText() == null) {
            return;
        }
        msgService.pushMessage(BeanUtil.beanToMsgBody(oe.getToUser(), oe.getMsgText()));
    }
}
