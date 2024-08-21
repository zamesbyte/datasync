package com.jamebyte.datasync.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jamebyte.datasync.mapper.SqlParam;
import com.jamebyte.datasync.mapper.primary.SourceMapper;
import com.jamebyte.datasync.mapper.secondary.TargetMapper;
import com.jamebyte.datasync.model.SyncConfig;
import com.jamebyte.datasync.service.SyncAction;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * 同步基类，控制核心流程
 *
 * @author: zhanlifeng
 * @description:
 * @date: 2024/8/13 09:21
 * @version: 1.0
 */
@Slf4j
public abstract class BaseSyncServiceImpl implements SyncAction {

  private static final Logger SQL_LOG = LoggerFactory.getLogger("sql_info");

  public static final int RETRY = 10;

  @Value("${myapp.last.hour}")
  private int lastHour;

  /**
   * 允许同步时间
   */
  public  LocalTime LAST_TIME = LocalTime.of(4, 0);


  private static final Object END_OBJECT = new Object();


  @Resource
  protected SourceMapper sourceMapper;

  @Resource
  protected TargetMapper targetMapper;


  @PostConstruct
  public void init(){
    LAST_TIME = LocalTime.of(lastHour, 0);
  }



  /**
   * 循环插入的数据
   *
   * @param syncConfig
   */
  @SneakyThrows
  public void syncByConfig(SyncConfig syncConfig) {
    Object lastKey = null;
    while (true) {
      // 根据时间决定是否继续同步
      if (!canSync(lastKey)) {
        log.info("归档时间结束");
        return;
      }

      /**
       * 暂停时间
       */
      TimeUnit.MILLISECONDS.sleep(10);

      for (int i = 0; i < RETRY; i++) {

        try {
          Object nextKey = syncOneTime(lastKey, syncConfig);
          if (nextKey == END_OBJECT) {
            log.info("sync to end ,table:{}",syncConfig.getTableName());
            return;
          }

          lastKey = nextKey;
          break;
        } catch (Exception e) {
          log.warn("sync error,lastKey:{},synConfig:{}", lastKey, syncConfig, e);
          if (i == RETRY - 1) {
            log.error("重试{}次还是失败，放弃同步,synConfig:{}", RETRY, syncConfig,e);
            return;
          }
        }
      }
    }
  }


  /**
   * @param lastKey    上次同步id
   * @param syncConfig
   * @return 当前最后一个同步的id
   */
  private Object syncOneTime(Object lastKey, SyncConfig syncConfig) {
    // 获取开始条件
    Object startKey = getStartKey(lastKey, syncConfig);

    List<Map<String, Object>> dataList = getInputData(syncConfig, startKey);
    if (CollectionUtils.isEmpty(dataList)) {
      return END_OBJECT;
    }

    boolean result = syncToTarget(syncConfig, dataList);
    if (result) {
      Object newLastKey = getLastKey(dataList, syncConfig);
      if(newLastKey!=null){
        return newLastKey;
      }
    }
    throw new RuntimeException("sync error");
  }


  /**
   * 继续同步
   *
   * @return
   */
  protected boolean canSync(Object lastKey) {
    LocalTime now = LocalTime.now();
    if (now.isAfter(LAST_TIME)) {
      return false;
    }
    return true;
  }


  /**
   * 获取第一个待同步的key和字段
   *
   * @param lastKey
   * @param syncConfig
   * @return
   */
  protected Object getStartKey(Object lastKey, SyncConfig syncConfig) {
    if (lastKey != null) {
      return lastKey;
    }
    String startKeySql = startKeySql(syncConfig);
    SQL_LOG.info("lastKey:{},startKeySql:{}", lastKey,startKeySql);

    Map<String, Object> select = targetMapper.selectOne(startKeySql);
    if (select != null) {
      return select.get("startKey");
    }

    return null;
  }

  protected abstract String startKeySql(SyncConfig syncConfig);

  /**
   * 获取源库数据
   *
   * @param syncConfig
   * @param startKey
   * @return
   */
  protected List<Map<String, Object>> getInputData(SyncConfig syncConfig, Object startKey) {

    String selectSql = selectSql(syncConfig, startKey);
    SQL_LOG.info("startKey:{}, selectSql:{}", startKey,selectSql);
    List<Map<String, Object>> select = sourceMapper.select(selectSql);

    return select;
  }

  /**
   * 获取查询sql
   *
   * @param syncConfig
   * @param startKey
   * @return
   */
  protected abstract String selectSql(SyncConfig syncConfig, Object startKey);

  /**
   * 同步到目标库
   *
   * @param syncConfig
   * @param dataList
   * @return
   */
  @Transactional
  protected boolean syncToTarget(SyncConfig syncConfig, List<Map<String, Object>> dataList) {
    List<SqlParam.FieldVo> setFieldVo = Lists.newArrayList();
    List<SqlParam.FieldVo> whereFieldVo = Lists.newArrayList();

    List<Map<String, Object>> sqlMaps = Lists.newArrayList();
    SqlParam sqlParam = SqlParam.builder().tableName(syncConfig.getTargetTableName())
        .keyColumn(syncConfig.getTargetTableIdName())
        .setFieldVo(setFieldVo).whereFieldVo(whereFieldVo).build();

    if (CollectionUtils.isEmpty(dataList)) {
      return true;
    }
    for (Map<String, Object> map : dataList) {
      boolean set = setFieldVo.size() == 0;
      Map<String, Object> sqlMap = Maps.newHashMap();
      for (Entry<String, Object> entry : map.entrySet()) {
        String fieldName = entry.getKey();
        Object fieldValue = entry.getValue();
        if (skipField(fieldName, syncConfig)) {
          continue;
        }

        if (set) {
          SqlParam.FieldVo fieldVo = SqlParam.FieldVo.builder().paramName(fieldName)
              .targetColumn(fieldName).build();
          setFieldVo.add(fieldVo);
        }

        sqlMap.put(fieldName, fieldValue);
      }
      sqlMaps.add(sqlMap);
    }

    int expectCnt = dataList.size();
    int insert = targetMapper.insertAll(sqlParam, sqlMaps, sqlParam.getKeyColumn());
    if (expectCnt != insert) {
      log.info("select and insert not exist,stop");
      return false;
    }
    log.info("sync table:{},cnt:{}",syncConfig.getTableName(),insert);

    return true;
  }

  protected boolean skipField(String fieldName, SyncConfig syncConfig) {
    return false;
  }

  ;

  /**
   * 获取最后一条记录的查询key
   *
   * @param dataList
   * @param syncConfig
   * @return
   */
  protected abstract Object getLastKey(List<Map<String, Object>> dataList, SyncConfig syncConfig);


  public void existHook(){};

}
