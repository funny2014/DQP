package com.dqp.sso.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 执行sql查询返回的结果
 * {
   data=[{name=aa, age=11, sex=1}, {name=bb, age=22, sex=2}, {name=cc, age=33, sex=3}],
   columns=[name, age, sex],
   success=true,
   errorMsg=
   }
 *
 *
 * Created on 2017/8/8.
 * Desc:
 */
@Data
public class ExeQueryResultModel {

  private List<Map<String,String>> data;
  private List<String> columns;
  private boolean success;
  private String errorMsg;
}
