package cn.lxiaol.serviceacl;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author lixiaolong
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("cn.lxiaol")
@MapperScan("cn.lxiaol.serviceacl.mapper")
public class ServiceAclApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceAclApplication.class, args);
        System.out.println("启动成功");
    }

}