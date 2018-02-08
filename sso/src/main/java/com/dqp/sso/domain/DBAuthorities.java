package com.dqp.sso.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yirendai.dataplatform.enums.DBEngine;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 权限表
 *
 * Created on 2017/8/3.
 * Desc:
 */
@Data
@Entity
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DBAuthorities {

  @Id
  @GeneratedValue
  private Long id;

  @Enumerated(EnumType.STRING)
  @NotNull
  private DBEngine dbEngine;

  @Column(nullable = false)
  @NotNull
  private String db;

  @Column(nullable = false)
  @NotNull
  private String tableName;

  @Temporal(TemporalType.TIMESTAMP)
  private Date createDate;

  //... 其他的以后加
}
