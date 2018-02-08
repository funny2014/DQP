# Executor
 Executor：链接指定数据源，执行数据查询

sqltable表 : 存储查询sql的历史记录  
CREATE TABLE `sqlstable` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `dbEngine` varchar(255) DEFAULT NULL,
  `sqlStr` text,
  `createtime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=94 DEFAULT CHARSET=utf8

