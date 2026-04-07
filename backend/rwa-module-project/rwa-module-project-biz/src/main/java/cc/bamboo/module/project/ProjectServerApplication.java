package cc.bamboo.module.project;

import com.dtflys.forest.springboot.annotation.ForestScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Project Server 启动类
 */
@SpringBootApplication
@ForestScan(basePackages = "cc.bamboo.module.project.service.spiderpool.forest")
public class ProjectServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProjectServerApplication.class, args);
    }
}
