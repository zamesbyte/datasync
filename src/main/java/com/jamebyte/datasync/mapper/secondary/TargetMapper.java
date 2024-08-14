package com.jamebyte.datasync.mapper.secondary;

import com.jamebyte.datasync.constants.Constants;
import com.jamebyte.datasync.mapper.SqlParam;
import com.jamebyte.datasync.mapper.SqlProvider;
import com.jamebyte.datasync.model.DBEntity;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TargetMapper {

  @InsertProvider(type = SqlProvider.class, method = "insert")
  @Options(useGeneratedKeys = true, keyProperty = "entity.id", keyColumn = "#{keyColumn}")
  int insert(@Param("sqlParam") SqlParam sqlParam,
      @Param(Constants.EXEC_PARAMS) Map<String, Object> params, @Param("entity") DBEntity entity,
      @Param("keyColumn") String keyColumn);


  @InsertProvider(type = SqlProvider.class, method = "insertAll")
  @Options(useGeneratedKeys = true,keyProperty = Constants.EXEC_PARAMS+".id", keyColumn = "#{keyColumn}")
  int insertAll(@Param("sqlParam") SqlParam sqlParam,
      @Param(Constants.EXEC_PARAMS) List<Map<String, Object>> params,@Param("keyColumn") String keyColumn);

  @Select("${sql}")
  List<Map<String, Object>> select(@Param("sql") String sql);

  @Select("${sql}")
  Map<String, Object> selectOne(@Param("sql") String sql);

  @Select("${sql}")
  Map<String, Object> selectId(@Param("sql") String sql);

}
