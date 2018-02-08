------------------------------------------------------------------------------------------------
--  file name   : TEST_HQL.hql
--  author      : Godlen
--  create date :  
--  fun desc    : 
--  TARGET_TABLE: gld.TEST_TABLE2
--  SOURCE_TABLE :gld.TEST_TABLE1
--
--  tem_table   :
--  modify date :
--  moifier     :
--  modify content :
--
----------------------------------------------------------------------------------------------
 
 
set mapred.job.queue.name=root.mapreduce.hive;
use gld ;
insert overwrite table gld.TEST_TABLE2
SELECT id -- 编号
      , nm -- 名称
FROM gld.TEST_TABLE1
;

