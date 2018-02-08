package com.dqp.sso.model;

import lombok.Data;

import java.util.Map;

/**
 *
 *
// {
//   "success": false,
//   "msg": {
//     "ErrorMsg": "line 1:0 cannot recognize input near 'selectc' '*' 'from'"
//   }
// }

// {
//   "success": true,
//   "msg": {
//     "a.b": "SELECT"
//   }
// }
 *
 *
 *
 * Created on 2017/8/4.
 * Desc:
 */
@Data
public class HqlParserResultModel {

  private boolean success;

  private Map<String,String> msg;
}
