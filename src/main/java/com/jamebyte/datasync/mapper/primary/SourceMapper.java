package com.jamebyte.datasync.mapper.primary;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SourceMapper {


  @Select("${sql}")
  List<Map<String, Object>> select(@Param("sql") String sql);

  @Select("${sql}")
  Map<String, Object> selectId(@Param("sql") String sql);

}
