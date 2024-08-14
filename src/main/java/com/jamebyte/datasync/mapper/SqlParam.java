package com.jamebyte.datasync.mapper;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class SqlParam {

  private String tableName;

  private String keyColumn;

  private List<String> columns;

  private List<FieldVo> setFieldVo;

  private List<FieldVo> whereFieldVo;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class FieldVo {

    private String targetColumn;

    private String paramName;

  }

}