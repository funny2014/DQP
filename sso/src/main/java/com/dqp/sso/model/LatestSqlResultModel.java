package com.dqp.sso.model;

import com.dqp.sso.enums.DBEngine;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 查询最新查询返回
 *
 * {
 * "data": [
 * {
 * "id": 59,
 * "username": "hadoop",
 * "dbEngine": "hive",
 * "sqlStr": "select * from test",
 * "createtime": 1503374422000
 * }
 * ],
 * "columns": [
 * "8"
 * ],
 * "success": true,
 * "errorMsg": ""
 * }
 *
 */
@Data
public class LatestSqlResultModel {

  private List<History> data;
  private List<String> columns;
  private boolean success;
  private String errorMsg;

  @Data
  public static class History {

    private Long id;

    @JsonProperty("username")
    private String user;

    private String sqlStr;   //txt类型

    private DBEngine dbEngine;

    @JsonProperty("createtime")
    private Date createDate;
  }

}
