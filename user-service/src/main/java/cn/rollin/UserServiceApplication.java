package cn.rollin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * User Application
 *
 * @author rollin
 * @since 2022-09-24 17:32:03
 */
@SpringBootApplication
@MapperScan("cn.rollin.mapper")
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
