package com.jamebyte.datasync.service.impl;

import com.jamebyte.datasync.model.StartKey;
import com.jamebyte.datasync.model.SyncActionEnum;
import com.jamebyte.datasync.model.SyncConfig;
import com.jamebyte.datasync.model.TableIdType;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * 核心字段同步策略
 *
 * @author: zhanlifeng
 * @description:
 * @date: 2024/8/9 15:35
 * @version: 1.0
 */
@Slf4j
@Service
public class KeySyncServiceImpl extends BaseSyncServiceImpl {

  ThreadLocal<Boolean> endFlag = ThreadLocal.withInitial(() -> false);

  protected String startKeySql(SyncConfig syncConfig) {
    // 根据id递增
    String startSelectSql = "select %s as startKey from  %s order by %s limit 1";
    String selectFields = syncConfig.getTableKeyName();
    String tableName = syncConfig.getTargetTableName();
    String orders = syncConfig.getTableKeyName() + " desc ";
    return String.format(startSelectSql, selectFields, tableName, orders);
  }

  protected String selectSql(SyncConfig syncConfig, Object startKey) {
    if (startKey instanceof StartKey) {
      StartKey startKey1 = (StartKey) startKey;
      return selectSql(syncConfig, startKey1.getValue(), startKey1.getSubValue());
    }
    return selectSql(syncConfig, startKey, null);
  }


  protected String selectSql(SyncConfig syncConfig, Object startKey, Object subValue) {
    // 根据id递增
    String startSelectSql = "select %s  from  %s where %s order by %s limit %d";

    String selectFields = syncConfig.getSelectFields();
    selectFields += " , " + syncConfig.getTableSubKeyName();
    String tableName = syncConfig.getTableName();
    // 第一次同步，目标表没有数据
    String where = " 1=1 ";
    if (startKey != null) {
      if (endFlag.get()) {
        if (syncConfig.getTableKeyType() == TableIdType.NUM) {
          where = syncConfig.getTableKeyName() + "=" + startKey;
        } else {
          where = syncConfig.getTableKeyName() + "=" + "'" + startKey + "'";
        }

        if (subValue != null) {
          if (syncConfig.getTableSubKeyType() == TableIdType.NUM) {
            where = where + " and " + syncConfig.getTableSubKeyName() + " > " + "'"
                + subValue + "'";
          } else {
            where = where + " and " + syncConfig.getTableSubKeyName() + " > " + "'"
                + subValue + "'";
          }
        }
      } else {
        where = syncConfig.getTableKeyName() + ">" + "'" + startKey + "'";
      }
    }
    String orders =
        syncConfig.getTableKeyName() + " asc , " + syncConfig.getTableSubKeyName() + " asc ";
    int limit = syncConfig.getLimit();
    return String.format(startSelectSql, selectFields, tableName, where, orders,
        limit);
  }


  @Override
  protected Object getLastKey(List<Map<String, Object>> dataList, SyncConfig syncConfig) {
    if (CollectionUtils.isEmpty(dataList)) {
      return null;
    }
    Map<String, Object> map = dataList.get(dataList.size() - 1);

    if(map.get(syncConfig.getTableKeyName())==null){
      return null;
    }
    StartKey startKey = new StartKey();
    startKey.setValue(map.get(syncConfig.getTableKeyName()));
    startKey.setSubValue(map.get(syncConfig.getTableSubKeyName()));
    return startKey;
  }

  /**
   * 辅助字段不允许插入，跳过
   *
   * @param fieldName
   * @param syncConfig
   * @return
   */
  protected boolean skipField(String fieldName, SyncConfig syncConfig) {
    return fieldName.equalsIgnoreCase(syncConfig.getTableSubKeyName());
  }


  @Override
  public boolean validateSyncConfig(SyncConfig syncConfig) {
    if (StringUtils.isEmpty(syncConfig.getSelectFields())) {
      log.info("SelectFields absent", syncConfig);
      return false;
    }
    if (StringUtils.isEmpty(syncConfig.getTableKeyName())) {
      log.info("TableKeyName absent", syncConfig);
      return false;
    }
    if (StringUtils.isEmpty(syncConfig.getTableKeyType())) {
      log.info("TableKeyType absent", syncConfig);
      return false;
    }
    if (StringUtils.isEmpty(syncConfig.getTableSubKeyName())) {
      log.info("TableSubKeyName absent", syncConfig);
      return false;
    }
    if (StringUtils.isEmpty(syncConfig.getTableSubKeyType())) {
      log.info("TableSubKeyType absent", syncConfig);
      return false;
    }
    if (StringUtils.isEmpty(syncConfig.getTargetTableIdName())) {
      log.info("TargetTableIdName absent", syncConfig);
      return false;
    }
    if (StringUtils.isEmpty(syncConfig.getTargetTableIdType())) {
      log.info("TargetTableIdType absent", syncConfig);
      return false;
    }
    if (StringUtils.isEmpty(syncConfig.getTargetTableName())) {
      log.info("TargetTableName absent", syncConfig);
      return false;
    }
    if (StringUtils.isEmpty(syncConfig.getTableName())) {
      log.info("TableName absent", syncConfig);
      return false;
    }
    return true;
  }

  protected boolean canSync(Object lastKey) {
    if (!super.canSync(lastKey)) {
      endFlag.set(true);
    }
    if (lastKey == null && endFlag.get()) {
      log.info("补充同步所有key字段值结束");
      return false;
    }
    return true;
  }

  @Override
  public SyncActionEnum syncActionEnum() {
    return SyncActionEnum.BY_KEY;
  }

  @Override
  public void existHook(){
    endFlag.remove();
  }


}
