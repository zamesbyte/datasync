package com.jamebyte.datasync.service;

import com.jamebyte.datasync.model.SyncActionEnum;
import com.jamebyte.datasync.model.SyncConfig;

/**
 * @author: zhanlifeng
 * @description:
 * @date: 2024/8/13 10:42
 * @version: 1.0
 */
public interface SyncAction {

  boolean validateSyncConfig(SyncConfig syncConfig);

  void syncByConfig(SyncConfig syncConfig);

  void existHook();

  SyncActionEnum syncActionEnum();

}
