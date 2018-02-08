package com.dqp.sso.enums;

/**
 * Created on 2017/8/3.
 * Desc:
 */
public enum DBEngine {

  mysql,
  hive,
  oracle,
  hbase,
  mycat,
  sparksql,
  presto;

  /**
   * 不同的引擎 对应相同的库表 在这里统一成一个存入资源
   * @param dbEngine
   * @return
   */
  public static DBEngine getEqualsOne(DBEngine dbEngine){
    if(dbEngine==null){
      return null;
    }
    switch (dbEngine){
      case hive:
      case sparksql:
      case presto:
        return hive;
      case mysql:
      case mycat:
        return mysql;
    }
    return dbEngine;
  }
}
