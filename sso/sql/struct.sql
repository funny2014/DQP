DROP TABLE IF EXISTS `dbauthorities`;
CREATE TABLE `dbauthorities` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `db_engine` varchar(20) COLLATE utf8_bin NOT NULL,
  `db` varchar(50) COLLATE utf8_bin NOT NULL,
  `table_name` varchar(100) COLLATE utf8_bin NOT NULL,
  `create_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `auth` (`db_engine`,`db`,`table_name`)
);


DROP TABLE IF EXISTS `dbrole`;
CREATE TABLE `dbrole` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_date` datetime DEFAULT NULL,
  `name` varchar(30) COLLATE utf8_bin NOT NULL,
  `status` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `dbrole_authorities`;
CREATE TABLE `dbrole_authorities` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `authority_id` bigint(20) NOT NULL,
  `create_date` datetime DEFAULT NULL,
  `db_type` varchar(10) COLLATE utf8_bin NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `query_history`;
CREATE TABLE `query_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_date` datetime NOT NULL,
  `sql_str` TEXT COLLATE utf8_bin NOT NULL,
  `db_engine` varchar(20) COLLATE utf8_bin NOT NULL,
  `user` varchar(30) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`)
);