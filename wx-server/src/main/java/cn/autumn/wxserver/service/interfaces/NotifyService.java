package cn.autumn.wxserver.service.interfaces;

import cn.autumn.wxserver.model.MessageModel;

/**
 * @author: autumn
 * created in 2022/9/7 Class NotifyService
 */
public interface NotifyService {

    void processOrder(MessageModel message);
}
