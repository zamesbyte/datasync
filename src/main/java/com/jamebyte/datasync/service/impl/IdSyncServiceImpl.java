package com.jamebyte.datasync.service.impl;

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
 * id同步策略
 *
 * @author: zhanlifeng
 * @description:
 * @date: 2024/8/9 15:35
 * @version: 1.0
 */
@Slf4j
@Service
public class IdSyncServiceImpl extends BaseSyncServiceImpl {

  protected String startKeySql(SyncConfig syncConfig) {
    // 根据id递增
    String startSelectSql = "select %s as startKey from  %s order by %s limit 1";
    String selectFields = syncConfig.getTableIdName();
    String tableName = syncConfig.getTargetTableName();
    String orders = syncConfig.getTableIdName() + " desc ";
    return String.format(startSelectSql, selectFields, tableName, orders);
  }

  protected String selectSql(SyncConfig syncConfig, Object startKey) {
    // 根据id递增
    String startSelectSql = "select %s  from  %s where %s order by %s limit %d";
    String selectFields = syncConfig.getSelectFields();
    String tableName = syncConfig.getTableName();
    // 第一次同步，目标表没有数据
    String where = " 1=1 ";
    if(startKey != null){
      String tableIdName = syncConfig.getSelectTableIdRealName()!=null?syncConfig.getSelectTableIdRealName():syncConfig.getTableIdName();
      if(syncConfig.getTableIdType() == TableIdType.NUM){
        where = tableIdName + ">" + startKey;
      }else {
        where = tableIdName + ">" + "'" +startKey+"'";
      }
    }

    if (StringUtils.hasText(syncConfig.getWhereClause())){
      where = where +  " and "+ syncConfig.getWhereClause() + " ";
    }

    String orders = syncConfig.getTableIdName() + " asc ";
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
    if (map.containsKey(syncConfig.getTargetTableIdName())){
      return map.get(syncConfig.getTargetTableIdName());
    }
    if (map.containsKey(syncConfig.getTargetTableIdName().toUpperCase())){
      return map.get(syncConfig.getTargetTableIdName().toUpperCase());
    }
    if (map.containsKey(syncConfig.getTargetTableIdName().toLowerCase())){
      return map.get(syncConfig.getTargetTableIdName().toLowerCase());
    }
    return null;
  }



  @Override
  public boolean validateSyncConfig(SyncConfig syncConfig) {
    if (StringUtils.isEmpty(syncConfig.getSelectFields())) {
      log.info("SelectFields absent,{}",syncConfig);
      return false;
    }
    if (StringUtils.isEmpty(syncConfig.getTableIdName())) {
      log.info("TableIdName absent",syncConfig);
      return false;
    }
    if (StringUtils.isEmpty(syncConfig.getTableIdType())) {
      log.info("TableIdType absent",syncConfig);
      return false;
    }
    if (StringUtils.isEmpty(syncConfig.getTargetTableIdName())) {
      log.info("TargetTableIdName absent",syncConfig);
      return false;
    }
    if (StringUtils.isEmpty(syncConfig.getTargetTableIdType())) {
      log.info("TargetTableIdType absent",syncConfig);
      return false;
    }
    if (StringUtils.isEmpty(syncConfig.getTargetTableName())) {
      log.info("TargetTableName absent",syncConfig);
      return false;
    }
    if (StringUtils.isEmpty(syncConfig.getTableName())) {
      log.info("TableName absent",syncConfig);
      return false;
    }
    return true;
  }




  @Override
  public SyncActionEnum syncActionEnum() {
    return SyncActionEnum.BY_ID;
  }
}
