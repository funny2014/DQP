package com.dqp.sso.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DBRoleDto {

  @NotNull
  private String name;
}
