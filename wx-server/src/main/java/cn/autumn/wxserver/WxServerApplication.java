package cn.autumn.wxserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.EnableScheduling;

@Retryable
@MapperScan(value = "cn.autumn.wxserver.mapper")
@EnableScheduling
@SpringBootApplication
public class WxServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WxServerApplication.class, args);
    }

}
