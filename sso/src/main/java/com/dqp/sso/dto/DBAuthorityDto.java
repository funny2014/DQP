package com.dqp.sso.dto;

import com.dqp.sso.enums.DBEngine;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * DB资源DTO
 */
@Data
public class DBAuthorityDto {

  @NotNull
  private DBEngine dbEngine;

  @NotNull
  private String db;

  @NotNull
  private String tableName;
}
