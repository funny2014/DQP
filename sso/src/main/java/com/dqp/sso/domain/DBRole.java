package com.dqp.sso.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 数据库权限部分的 角色
 *
 * 1. 角色名全部以 DB_ 开头  DB_ROLE_PREFIX
 *
 * Created on 2017/8/3.
 * Desc:
 */
@Data
@Entity
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DBRole {


  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  @NotNull
  private String name;

  @Column(nullable = false)
  private Boolean status;

  @Temporal(TemporalType.TIMESTAMP)
  private Date createDate;

}
