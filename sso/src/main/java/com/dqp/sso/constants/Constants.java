package com.dqp.sso.constants;

/**
 * 部分常量定义
 */
public interface Constants {

  String RESULT_OK = "OK";                //前端接口正常返回code
  String RESULT_FAILURE = "FAILURE";      //前端接口异常返回code
  String RESULT_SYSFAIL = "SYS_FAIL";      //前端接口系统错误code

  /**
   * 数据库权限角色命名前缀
   */
  String DB_ROLE_PREFIX = "DB_";

  /**
   * 数据库权限角色命名前缀
   */
  String GROUP_PREFIX = "Group_";

  /**
   * 按钮角色前缀
   */
  String BUTTON_PREFIX = "BUTTON_";

  /**
   * 查询页面按钮权限角色前缀
   */
  String QUERY_BUTTON_PREFIX =BUTTON_PREFIX+ "QUERY_";
}


