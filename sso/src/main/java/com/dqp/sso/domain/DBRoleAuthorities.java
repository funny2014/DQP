package com.dqp.sso.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yirendai.dataplatform.enums.DBType;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 角色-权限关联
 * Created on 2017/8/3.
 * Desc:
 */
@Data
@Entity
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DBRoleAuthorities {

  @Id
  @GeneratedValue
  private Long id;

  /**
   * 角色Id
   */
  @Column(nullable = false)
  private Long roleId;

  /**
   * 资源Id
   */
  @Column(nullable = false)
  private Long authorityId;

  /**
   * 类型
   */
  @Enumerated(EnumType.STRING)
  @NotNull
  private DBType dbType;

  //TODO 若是需要 可以在这里加个属性 表示读写改权限 (类似 0000 表示增删改查 0无权限 1 有权限)
  //当然涉及地方还比较多 不一定可行

  @Temporal(TemporalType.TIMESTAMP)
  private Date createDate;

}
