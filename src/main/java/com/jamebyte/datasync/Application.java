package com.jamebyte.datasync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author: zhanlifeng
 * @description:
 * @date: 2024/8/9 13:12
 * @version: 1.0
 */

@EnableScheduling
@EnableConfigurationProperties
@SpringBootApplication
public class Application {


  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }


}
