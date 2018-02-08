package com.dqp.sso.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yirendai.dataplatform.enums.DBEngine;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 查询记录
 * Created on 2017/8/4.
 * Desc:
 */
@Data
@Entity
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class QueryHistory {

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  @NotNull
  private String user;

  @Column(nullable = false)
  @NotNull
  private String sqlStr;   //txt类型

  @Enumerated(EnumType.STRING)
  @NotNull
  private DBEngine dbEngine;

  @Temporal(TemporalType.TIMESTAMP)
  @NotNull
  private Date createDate;

  //....其他属性

}
