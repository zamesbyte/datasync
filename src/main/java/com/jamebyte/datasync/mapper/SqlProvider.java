package com.jamebyte.datasync.mapper;


import com.jamebyte.datasync.constants.Constants;
import com.jamebyte.datasync.mapper.SqlParam.FieldVo;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: zhanlifeng
 * @description:
 * @date: 2024/7/10 15:01
 * @version: 1.0
 */
public class SqlProvider {

  private static final Logger SQL_LOG = LoggerFactory.getLogger("sql_info");



  public String count(@Param("sqlParam") SqlParam sqlParam) {
    String tableName = sqlParam.getTableName();
    List<FieldVo> whereFieldVos = sqlParam.getWhereFieldVo();
    String countSQL = createCountSQL(tableName, whereFieldVos);
    SQL_LOG.info("sql:{}", countSQL);
    return countSQL;
  }

  public String select(@Param("sqlParam") SqlParam sqlParam) {
    String tableName = sqlParam.getTableName();
    List<FieldVo> whereFieldVos = sqlParam.getWhereFieldVo();
    String selectSQL = createSelectSQL(tableName, sqlParam.getColumns(), whereFieldVos);
    SQL_LOG.info("sql:{}", selectSQL);
    return selectSQL;
  }


  public String selectForUpdate(@Param("sqlParam") SqlParam sqlParam) {
    String tableName = sqlParam.getTableName();
    List<FieldVo> whereFieldVos = sqlParam.getWhereFieldVo();
    String selectForUpdateSQL =
        createSelectSQL(tableName, sqlParam.getColumns(), whereFieldVos) + " for update ";
    SQL_LOG.info("sql:{}", selectForUpdateSQL);
    return selectForUpdateSQL;
  }


  public String insert(@Param("sqlParam") SqlParam sqlParam) {
    String tableName = sqlParam.getTableName();
    List<FieldVo> whereFieldVos = sqlParam.getSetFieldVo();
    String insertSQL = createInsertSQL(tableName, whereFieldVos);
    SQL_LOG.info("sql:{}", insertSQL);
    return insertSQL;
  }

  public String insertAll(@Param("sqlParam") SqlParam sqlParam ,@Param("params") List<Map<String, Object>> params) {
    String tableName = sqlParam.getTableName();
    List<FieldVo> whereFieldVos = sqlParam.getSetFieldVo();
    String insertSQL = createInsertAllSQL(tableName, whereFieldVos,params);
    SQL_LOG.info("sql:{}", insertSQL);
    return insertSQL;
  }



  public String update(@Param("sqlParam") SqlParam sqlParam) {
    String tableName = sqlParam.getTableName();
    List<FieldVo> fieldVos = sqlParam.getSetFieldVo();
    List<FieldVo> whereFieldVos = sqlParam.getWhereFieldVo();
    String updateSQL = createUpdateSQL(tableName, fieldVos, whereFieldVos);
    SQL_LOG.info("sql:{}", updateSQL);
    return updateSQL;
  }

  public String delete(@Param("sqlParam") SqlParam sqlParam) {
    String tableName = sqlParam.getTableName();
    List<FieldVo> whereFieldVos = sqlParam.getWhereFieldVo();
    String deleteSQL = createDeleteSQL(tableName, whereFieldVos);
    SQL_LOG.info("sql:{}", deleteSQL);
    return deleteSQL;
  }


  public String createInsertSQL(String tableName, List<FieldVo> fieldVos) {
    StringBuilder sql = new StringBuilder("INSERT INTO ");
    sql.append(tableName).append(" (");

    StringBuilder columns = new StringBuilder();
    StringBuilder values = new StringBuilder();

    for (FieldVo datum : fieldVos) {
      columns.append(datum.getTargetColumn()).append(", ");
      values.append(modifyParamName(datum)).append(", ");
    }

    // Remove the last comma and space
    columns.setLength(columns.length() - 2);
    values.setLength(values.length() - 2);

    sql.append(columns).append(") VALUES (").append(values).append(")");

    SQL_LOG.info("{}", sql);
    return sql.toString();
  }


  private String createInsertAllSQL(String tableName, List<FieldVo> fieldVos,List<Map<String, Object>> params) {
    StringBuilder sql = new StringBuilder("INSERT INTO ");
    sql.append(tableName).append(" (");

    StringBuilder columns = new StringBuilder();

    for (FieldVo datum : fieldVos) {
      columns.append(datum.getTargetColumn()).append(", ");
    }
    // Remove the last comma and space
    columns.setLength(columns.length() - 2);

    StringBuilder valueSql = new StringBuilder();
    for (int i = 0; i < params.size(); i++) {
      valueSql.append("(");
      for (FieldVo datum : fieldVos) {
        valueSql.append("#{"+Constants.EXEC_PARAMS+"[").append(i).append("]."+datum.getParamName()+"}, ");
      }
      valueSql.setLength(valueSql.length() - 2);
      valueSql.append(")");
      if (i < params.size() - 1) {
        valueSql.append(",");
      }
    }

    sql.append(columns).append(") VALUES ")
        .append(valueSql);

    return sql.toString();



  }


  public String createUpdateSQL(String tableName, List<FieldVo> fieldVos,
      List<FieldVo> whereFieldVos) {
    StringBuilder sql = new StringBuilder("UPDATE ");
    sql.append(tableName).append(" SET ");

    String setClause = genCommonCunlunExpress(fieldVos);
    sql.append(setClause);

    String whereClause = genCommonCunlunExpress(whereFieldVos);

    sql.append(" WHERE ").append(whereClause);
    SQL_LOG.info("{}", sql);
    return sql.toString();
  }


  public String createDeleteSQL(String tableName, List<FieldVo> whereFieldVos) {
    StringBuilder sql = new StringBuilder("DELETE FROM ");

    String whereClause = genCommonCunlunExpress(whereFieldVos);

    sql.append(tableName).append(" WHERE ").append(whereClause);
    SQL_LOG.info("{}", sql);
    return sql.toString();
  }

  public String createSelectSQL(String tableName, List<String> columns,
      List<FieldVo> whereFieldVos) {
    StringBuilder sql = new StringBuilder("SELECT ");

    if (columns == null || columns.isEmpty()) {
      sql.append("*");
    } else {
      for (String column : columns) {
        sql.append(column).append(", ");
      }
      // Remove the last comma and space
      sql.setLength(sql.length() - 2);
    }

    sql.append(" FROM ").append(tableName);

    String whereClause = genCommonCunlunExpress(whereFieldVos);

    sql.append(" WHERE ").append(whereClause);
    SQL_LOG.info("{}", sql);
    return sql.toString();
  }


  public String createCountSQL(String tableName, List<FieldVo> whereFieldVos) {
    StringBuilder sql = new StringBuilder("SELECT ");
    sql.append("count(*)  FROM ").append(tableName);

    String whereClause = genCommonCunlunExpress(whereFieldVos);

    sql.append(" WHERE ").append(whereClause);
    SQL_LOG.info("{}", sql);
    return sql.toString();
  }

  private static String genCommonCunlunExpress(List<FieldVo> fieldVos) {
    StringBuilder whereClause = new StringBuilder();
    for (FieldVo whereFieldVo : fieldVos) {
      whereClause.append(whereFieldVo.getTargetColumn()).append(" = ")
          .append(modifyParamName(whereFieldVo))
          .append(", ");
    }
    whereClause.setLength(whereClause.length() - 2);
    return whereClause.toString();
  }


  private static String modifyParamName(FieldVo datum) {
    String stringBuilder = "#{"
        + Constants.EXEC_PARAMS
        + "."
        + datum.getParamName()
        + "}";
    return stringBuilder;
  }

//
//  public static void main(String[] args) {
//
//    SqlProvider sqlProvider = new SqlProvider();
//    String tableName = "test_biz01";
//
//    List<FieldVo> whereFieldVos = Lists.newArrayList(
//        FieldVo.builder().targetColumn("id").paramName("cond1").type(TargetFieldType.NUMBER_TYPE)
//            .build(),
//        FieldVo.builder().targetColumn("word03").paramName("cond1")
//            .type(TargetFieldType.STRING_TYPE).build());
//    String countSQL = sqlProvider.createCountSQL(tableName, whereFieldVos);
//    System.out.println(countSQL);
//
//    List<FieldVo> fieldVos = Lists.newArrayList(
//        FieldVo.builder().targetColumn("id").paramName("cond1").type(TargetFieldType.NUMBER_TYPE)
//            .build(),
//        FieldVo.builder().targetColumn("word03").paramName("cond1")
//            .type(TargetFieldType.STRING_TYPE).build());
//    System.out.println(sqlProvider.createSelectSQL(tableName, null, whereFieldVos));
//    System.out.println(sqlProvider.createInsertSQL(tableName, fieldVos));
//    System.out.println(sqlProvider.createDeleteSQL(tableName, fieldVos));
//    System.out.println(sqlProvider.createUpdateSQL(tableName, fieldVos, whereFieldVos));
//  }


}
