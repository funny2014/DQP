package com.dqp.sso.dto;


import com.dqp.sso.enums.DBEngine;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QueryDto {

  @NotNull
  private String sql;

  @NotNull
  private String timeLiness;

  @NotNull
  private DBEngine dbEngine;

  private String jobId = "";
}
