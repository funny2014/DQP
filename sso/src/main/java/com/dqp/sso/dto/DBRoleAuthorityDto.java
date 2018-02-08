package com.dqp.sso.dto;

import com.dqp.sso.enums.DBType;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * DB资源DTO
 * Created on 2017/8/3.
 * Desc:
 */
@Data
public class DBRoleAuthorityDto {

  @NotNull
  private Long roleId;

  @NotNull
  private Long authorityId;

  @NotNull
  private DBType dbType;
}
