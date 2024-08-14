package com.jamebyte.datasync.config;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 线程池配置
 *
 * @author ruoyi
 **/
@Slf4j
@Configuration
public class ThreadPoolConfig {

  // 核心线程池大小
  private int corePoolSize = 3;

  // 最大可创建的线程数
  private int maxPoolSize = 200;

  // 队列最大长度
  private int queueCapacity = 1000;

  // 线程池维护线程所允许的空闲时间
  private int keepAliveSeconds = 300;

  @Bean(name = "threadPoolTaskExecutor")
  public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setThreadNamePrefix("sync-");
    executor.setMaxPoolSize(maxPoolSize);
    executor.setCorePoolSize(corePoolSize);
    executor.setQueueCapacity(queueCapacity);
    executor.setKeepAliveSeconds(keepAliveSeconds);
    // 线程池对拒绝任务(无线程可用)的处理策略
    executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
      @Override
      public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        log.info("reject {}", r);
      }
    });
    return executor;
  }


}
