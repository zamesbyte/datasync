package com.jamebyte.datasync.schedule;

import com.jamebyte.datasync.service.impl.SyncServiceFacade;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScheduledTasks {

  private AtomicInteger status = new AtomicInteger();

  @Resource
  private SyncServiceFacade syncServiceFacade;

  // 使用 cron 表达式定义任务
  @Scheduled(cron = "${myapp.scheduled.cron}") // 每小时执行一次
  public void cronTask() {
    log.info("start sync ...");
    if(status.compareAndSet(0,1)){
      try {
        syncServiceFacade.sync();
      } catch (Exception e) {
        log.error("",e);
      } finally {
        status.set(0);
      }
    }else {
      log.info("start sync, alread sync ,  skip ");
    }
  }

}