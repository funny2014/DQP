package com.dqp.sso.model;

import com.dqp.sso.constants.Constants;
import com.dqp.sso.enums.ErrorCodeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResultModel<T> {

  private String code = Constants.RESULT_FAILURE;
  private String msg = null;
  private String errorCode = null;
  private T data;

  public CommonResultModel(T data) {
    this.data = data;
  }

  public CommonResultModel(String msg) {
    this.msg = msg;
  }

  public CommonResultModel(String errorCode, String msg) {
    this.msg = msg;
    this.errorCode = errorCode;
  }

  public CommonResultModel(ErrorCodeEnum errorCodeEnum) {
    this.msg = errorCodeEnum.getMsg();
    this.errorCode = errorCodeEnum.getCode();
  }

  public CommonResultModel(ErrorCodeEnum errorCodeEnum, String code) {
    this(errorCodeEnum);
    this.code = code;
  }


  public void setErrorCodeEnum(ErrorCodeEnum errorCodeEnum) {
    this.msg = errorCodeEnum.getMsg();
    this.errorCode = errorCodeEnum.getCode();
  }

}
