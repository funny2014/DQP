package com.dqp.sso.enums;

/**
 * 通用错误定义
 * Created on 2017/8/1.
 * Desc:
 */
public enum  ErrorCodeEnum {

  SYS_ERROR("001","系统错误"), //001-100 系统级别
  PARAM_ERROR("100","参数错误"),  //101-200 静态验证级别
  AUTH_ERROR("201","权限错误"); //201-300 业务级别


  String code; //错误码
  String msg;  //错误信息


  ErrorCodeEnum(String code,String msg){
    this.code=code;
    this.msg=msg;
  }


  public String getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }
}
