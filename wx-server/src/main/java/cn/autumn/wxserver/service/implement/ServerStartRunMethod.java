package cn.autumn.wxserver.service.implement;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

/**
 * @author: autumn
 * created in 2022/9/11 Class ServerStartRunMethod
 */
@Component
public class ServerStartRunMethod implements ApplicationRunner {

    @Resource
    private NotifyServiceImpl notifyService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        notifyService.everyDayInit();
    }
}
