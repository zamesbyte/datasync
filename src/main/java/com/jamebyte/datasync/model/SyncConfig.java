package com.jamebyte.datasync.model;

import com.alibaba.fastjson2.JSON;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * @author: zhanlifeng
 * @description:
 * @date: 2024/8/9 15:32
 * @version: 1.0
 */
@Data
public class SyncConfig {

  private String tableName;

  private String selectFields;


  /**
   * 用id归档
   */
  private String tableIdName;

  private String selectTableIdRealName;

  private TableIdType tableIdType;

  /**
   * 用某个字段归档
   */
  private String tableKeyName;
  private TableIdType tableKeyType;

  /**
   * 通过某个字段归档时的辅助排序字段
   */
  private String tableSubKeyName;
  private TableIdType tableSubKeyType;

  private String targetTableIdName;
  private TableIdType targetTableIdType;


  private String targetTableName;



  private int limit = 100;


  public static void main(String[] args) {
    List<SyncConfig> list = new ArrayList<>();


    SyncConfig syncConfig = new SyncConfig();
    syncConfig.setTableName("house_archives");
    String selectFields = "    `id` ,\n"
        + "`pc_house_id` ,\n"
        + "`building_name` ,\n"
        + "`house_address` ,\n"
        + "`householder_name` ,\n"
        + "`householder_card_id` ,\n"
        + "`householder_phone` ,\n"
        + "`structure_type_name` ,\n"
        + "`jd_result` ,\n"
        + "`deal_result` ,\n"
        + "`house_property`,\n"
        + " `update_time`";
//    synConfig.setTableName("house_archives");
    syncConfig.setSelectFields(selectFields);
    syncConfig.setTableIdName("id");
    syncConfig.setTableIdType(TableIdType.STRING);
//    syncConfig.setTableKeyName("");
//    syncConfig.setTableKeyType(TableIdType.STRING);
//    syncConfig.setTableSubKeyName("");
//    syncConfig.setTableSubKeyType(TableIdType.STRING);
    syncConfig.setTargetTableIdName("id");
    syncConfig.setTargetTableIdType(TableIdType.STRING);
    syncConfig.setTargetTableName("biz_1000_house_archives");
    list.add(syncConfig);

    syncConfig = new SyncConfig();
    syncConfig.setTableName("i_m_jghmd");
    selectFields = " `HMDH` as `id`,\n"
        + "`qybh`,\n"
        + "`qymc`,\n"
        + "`ksrq`,\n"
        + "`jsrq`,\n"
        + "`cflx`,\n"
        + "`memo` ";
//    synConfig.setTableName("house_archives");
    syncConfig.setSelectFields(selectFields);
    syncConfig.setTableIdName("id");
    syncConfig.setSelectTableIdRealName("HMDH");
    syncConfig.setTableIdType(TableIdType.NUM);
//    syncConfig.setTableKeyName("CJSJWYH");
//    syncConfig.setTableKeyType(TableIdType.STRING);
//    syncConfig.setTableSubKeyName("WTDBH");
//    syncConfig.setTableSubKeyType(TableIdType.STRING);
    syncConfig.setTargetTableIdName("id");
    syncConfig.setTargetTableIdType(TableIdType.NUM);
    syncConfig.setTargetTableName("biz_1000_i_m_jghmd");
    list.add(syncConfig);



    syncConfig = new SyncConfig();
    syncConfig.setTableName("i_m_qy");
    selectFields = "`qybh`,\n"
        + "`qymc`,\n"
        + "`ZZJGDM` as `ZZJFDM`, -- 需要将biz_1000_i_m_qy1表 ZZJFDM 改成 ZZJGDM\n"
        + "`lxsj`,\n"
        + "`qyfr`";
//    synConfig.setTableName("house_archives");
    syncConfig.setSelectFields(selectFields);
    syncConfig.setTableIdName("qybh");
    syncConfig.setTableIdType(TableIdType.STRING);
//    syncConfig.setTableKeyName("CJSJWYH");
//    syncConfig.setTableKeyType(TableIdType.STRING);
//    syncConfig.setTableSubKeyName("WTDBH");
//    syncConfig.setTableSubKeyType(TableIdType.STRING);
    syncConfig.setTargetTableIdName("qybh");
    syncConfig.setTargetTableIdType(TableIdType.STRING);
    syncConfig.setTargetTableName("biz_1000_i_m_qy1");
    list.add(syncConfig);


    syncConfig = new SyncConfig();
    syncConfig.setTableName("i_m_sb");
    selectFields = "`recid`,\n"
        + "`sbbh`,\n"
        + "`sbmc`,\n"
        + "`sbxh`,\n"
        + "`sccj`,\n"
        + "`bfnx`,\n"
        + "`jdzg`";
//    synConfig.setTableName("house_archives");
    syncConfig.setSelectFields(selectFields);
    syncConfig.setTableIdName("recid");
    syncConfig.setTableIdType(TableIdType.NUM);
//    syncConfig.setTableKeyName("CJSJWYH");
//    syncConfig.setTableKeyType(TableIdType.STRING);
//    syncConfig.setTableSubKeyName("WTDBH");
//    syncConfig.setTableSubKeyType(TableIdType.STRING);
    syncConfig.setTargetTableIdName("recid");
    syncConfig.setTargetTableIdType(TableIdType.NUM);
    syncConfig.setTargetTableName("biz_1000_i_m_sb");
    list.add(syncConfig);


    syncConfig = new SyncConfig();
    syncConfig.setTableName("m_by");
    selectFields = "`recid`,\n"
        + "`ytdwbh`,\n"
        + "`ytdwmc`,\n"
        + "`syxmmc`,\n"
        + "`gcdz`,\n"
        + "`gcmc`,\n"
        + "`jcjgms`";
//    synConfig.setTableName("house_archives");
    syncConfig.setSelectFields(selectFields);
    syncConfig.setTableIdName("recid");
    syncConfig.setTableIdType(TableIdType.STRING);
//    syncConfig.setTableKeyName("CJSJWYH");
//    syncConfig.setTableKeyType(TableIdType.STRING);
//    syncConfig.setTableSubKeyName("WTDBH");
//    syncConfig.setTableSubKeyType(TableIdType.STRING);
    syncConfig.setTargetTableIdName("recid");
    syncConfig.setTargetTableIdType(TableIdType.STRING);
    syncConfig.setTargetTableName("biz_1000_m_by");
    list.add(syncConfig);


    syncConfig = new SyncConfig();
    syncConfig.setTableName("m_random_spot_test");
    selectFields = "`recid`,\n"
        + "`test_name`,\n"
        + "`test_time`,\n"
        + "`create_user_name`";
//    synConfig.setTableName("house_archives");
    syncConfig.setSelectFields(selectFields);
    syncConfig.setTableIdName("recid");
    syncConfig.setTableIdType(TableIdType.STRING);
//    syncConfig.setTableKeyName("CJSJWYH");
//    syncConfig.setTableKeyType(TableIdType.STRING);
//    syncConfig.setTableSubKeyName("WTDBH");
//    syncConfig.setTableSubKeyType(TableIdType.STRING);
    syncConfig.setTargetTableIdName("recid");
    syncConfig.setTargetTableIdType(TableIdType.STRING);
    syncConfig.setTargetTableName("biz_1000_m_random_spot_test");
    list.add(syncConfig);



    syncConfig = new SyncConfig();
    syncConfig.setTableName("UP_CJJL");
    selectFields = " CJSJWYH,WTDBH,SYR,SYRXM,KSSJ";
//    synConfig.setTableName("house_archives");
    syncConfig.setSelectFields(selectFields);
//    syncConfig.setTableIdName("id");
//    syncConfig.setTableIdType(TableIdType.STRING);
    syncConfig.setTableKeyName("CJSJWYH");
    syncConfig.setTableKeyType(TableIdType.STRING);
    syncConfig.setTableSubKeyName("WTDBH");
    syncConfig.setTableSubKeyType(TableIdType.STRING);
    syncConfig.setTargetTableIdName("CJSJWYH");
    syncConfig.setTargetTableIdType(TableIdType.STRING);
    syncConfig.setTargetTableName("biz_1000_up_cjjl");
    list.add(syncConfig);


    syncConfig = new SyncConfig();
    syncConfig.setTableName("view_i_m_ry");
    selectFields = " `qymc`,\n"
        + "`ryxm`,\n"
        + "`sfzhmtm`,\n"
        + "`sjhmtm`,\n"
        + "`zh`,\n"
        + "`rybh`";
//    synConfig.setTableName("house_archives");
    syncConfig.setSelectFields(selectFields);
//    syncConfig.setTableIdName("id");
//    syncConfig.setTableIdType(TableIdType.STRING);
    syncConfig.setTableKeyName("rybh");
    syncConfig.setTableKeyType(TableIdType.STRING);
    syncConfig.setTableSubKeyName("qybh");
    syncConfig.setTableSubKeyType(TableIdType.STRING);
    syncConfig.setTargetTableIdName("rybh");
    syncConfig.setTargetTableIdType(TableIdType.STRING);
    syncConfig.setTargetTableName("biz_1000_view_i_m_ry");
    list.add(syncConfig);


    syncConfig = new SyncConfig();
    syncConfig.setTableName("I_M_RY");
    selectFields = "`ryxm`,\n"
        + "`sfzhm`,\n"
        + "`sjhm`,\n"
        + "`xb`,\n"
        + "`zh`,\n"
        + "`mz`,\n"
        + "`rybh`";
//    synConfig.setTableName("house_archives");
    syncConfig.setSelectFields(selectFields);
    syncConfig.setTableIdName("rybh");
    syncConfig.setTableIdType(TableIdType.STRING);
//    syncConfig.setTableKeyName("CJSJWYH");
//    syncConfig.setTableKeyType(TableIdType.STRING);
//    syncConfig.setTableSubKeyName("WTDBH");
//    syncConfig.setTableSubKeyType(TableIdType.STRING);
    syncConfig.setTargetTableIdName("rybh");
    syncConfig.setTargetTableIdType(TableIdType.STRING);
    syncConfig.setTargetTableName("biz_1000_view_i_m_ry1");
    list.add(syncConfig);



    syncConfig = new SyncConfig();
    syncConfig.setTableName("view_i_s_qy_qyzz");
    selectFields = "`recid`,\n"
        + "`qybh`,\n"
        + "`lxmc`,\n"
        + "`zzx`,\n"
        + "`zzdjmc`,\n"
        + "`zznrbh`,\n"
        + "`zzmc`,\n"
        + "`fzjg`,\n"
        + "`yxqz`";
//    synConfig.setTableName("house_archives");
    syncConfig.setSelectFields(selectFields);
    syncConfig.setTableIdName("recid");
    syncConfig.setTableIdType(TableIdType.NUM);
//    syncConfig.setTableKeyName("CJSJWYH");
//    syncConfig.setTableKeyType(TableIdType.STRING);
//    syncConfig.setTableSubKeyName("WTDBH");
//    syncConfig.setTableSubKeyType(TableIdType.STRING);
    syncConfig.setTargetTableIdName("recid");
    syncConfig.setTargetTableIdType(TableIdType.NUM);
    syncConfig.setTargetTableName("biz_1000_view_i_s_qy_qyzz");
    list.add(syncConfig);



    System.out.println(JSON.toJSONString(list));


  }

}
