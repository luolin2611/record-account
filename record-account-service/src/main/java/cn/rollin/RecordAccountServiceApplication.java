package cn.rollin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * RecordAccount 启动类
 *
 * @author rollin
 * @since 2022-10-05 18:02:20
 */
@SpringBootApplication
@MapperScan("cn.rollin.mapper")
public class RecordAccountServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RecordAccountServiceApplication.class, args);
    }
}

