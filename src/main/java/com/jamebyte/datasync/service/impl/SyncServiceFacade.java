package com.jamebyte.datasync.service.impl;

import com.alibaba.fastjson.JSON;
import com.jamebyte.datasync.model.SyncActionEnum;
import com.jamebyte.datasync.model.SyncConfig;
import com.jamebyte.datasync.service.SyncAction;
import com.jamebyte.datasync.service.SyncService;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author: zhanlifeng
 * @description:
 * @date: 2024/8/13 10:39
 * @version: 1.0
 */
@Service
@Slf4j
public class SyncServiceFacade implements SyncService, InitializingBean {

  @Value("${syn.config.file}")
  private String sqlConfigFile;

  private List<SyncConfig> syncConfigList;


  @Resource
  ThreadPoolTaskExecutor threadPoolTaskExecutor;

  @Resource
  private List<SyncAction> list;


  Map<SyncActionEnum, SyncAction> map = new HashMap<>();


  @SneakyThrows
  public void refreshConfigFile() {
    String config = IOUtils.toString(new FileInputStream(sqlConfigFile), Charset.defaultCharset());
    List<SyncConfig> tempSyncConfigs = JSON.parseArray(config, SyncConfig.class);
    syncConfigList = tempSyncConfigs;
  }

  @Override
  public void sync() {

    refreshConfigFile();

    if (CollectionUtils.isEmpty(syncConfigList)) {
      log.error("no synConfigList , stop ...");
      return;
    }

    for (SyncConfig syncConfig : syncConfigList) {
      SyncAction syncAction = getSyncAction(syncConfig);
      if (syncAction == null) {
        log.error("syncAction not found,syncConfig:{}", syncConfig);
        return;
      }
      if (!syncAction.validateSyncConfig(syncConfig)) {
        log.error("syncConfig validate fail , syncConfig:{}", syncConfig);
        return;
      }
      dispacherSyncTask(syncAction, syncConfig);
    }
  }

  private SyncAction getSyncAction(SyncConfig syncConfig) {
    if (syncConfig.getTableIdName() != null) {
      return map.get(SyncActionEnum.BY_ID);
    }
    if (syncConfig.getTableKeyName() != null) {
      return map.get(SyncActionEnum.BY_KEY);
    }
    return null;
  }


  /**
   * 可以在子类覆盖
   *
   * @param syncConfig
   */
  protected void dispacherSyncTask(SyncAction syncAction, SyncConfig syncConfig) {
    threadPoolTaskExecutor.submit(new Runnable() {
      @Override
      public void run() {
        try {
          syncAction.syncByConfig(syncConfig);
        } catch (Exception e) {
          log.error("",e);
        } finally {
          syncAction.existHook();
        }
      }
    });
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    for (SyncAction syncAction : list) {
      map.put(syncAction.syncActionEnum(), syncAction);
    }

  }
}
