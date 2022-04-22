package cn.lxiaol;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

/**
 * @author lixiaolong
 * @EnableGlobalMethodSecurity(securedEnabled=true)
 * @date 2021/1/21 16:19
 */
@SpringBootApplication
@MapperScan("cn.lxiaol.mapper")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringsecuritywebApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringsecuritywebApplication.class, args);
        System.out.println("启动成功");
    }
}