package com.dqp.sso.model;

import com.yirendai.dataplatform.enums.DBEngine;
import com.yirendai.dataplatform.enums.DBType;
import lombok.Data;

/**
 * Created on 2017/8/10.
 * Desc:
 */
@Data
public class DBRoleAuthoritiesModel {

  // DBType
  private DBType dbType;

  private DBEngine dbEngine;

  private String db;

  private String table;

  private Long authorityId;
}
