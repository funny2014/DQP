功能：加载解析指定路径下的hql文件，生成AST(抽象语法树)；分析遍历AST,获得与指定表，与作业和其他表的关联关系；


 AnalysorInitialService： 初始化类，随项目启动而启动
 HqlFileAnalyserService：获得hql文件并解析文件的各项属性  HqlFileSink  ->  HqlAnalyserService 
 
 
### HqlAnalyserService 
> 利用hive原生的sql parser 解析sql
> 
> 关键技术点「hive sqlparser,antlr」

1. 通过hiveSqlParser解析出AST
2. 深度优先遍历ast
3. 遇到token操作判断其类型 「是否join」「表操作类型」则入栈，保存当前处理操作类型 当前操作表信息入栈
4. 遇到tocken TOK_TABNAME，TOK_TABREF 等解析出table 和cols 信息 以及 当前操作表信息
5. 判断当前子句是否结束
6. 重复3-4-5直到完全遍历完 


表解析：
测试语句：
 CREATE TABLE tmp.user AS SELECT e.id,e.name,d.name FROM tmp.Employee e INNER JOIN e.department d
 
测试 url:
http://localhost:8088/analysor/hql/parser?sql=CREATE%20TABLE%20tmp.user%20AS%20SELECT%20e.id,e.name,d.name%20FROM%20tmp.Employee%20e%20INNER%20JOIN%20e.department%20d

返回结果 ：
 JSON格式：
 {"success":true,"msg":{"e.department":"SELECT","tmp.employee":"SELECT","tmp.user":"CREATETABLE"}}


遍历图：
测试语句：
select substr(b.create_date, 1,7) que_mon,
       ad.activity_name,
       au.user_name,
       sum(c.cash_value) cash_value,
                substr(b.create_date,1,7) calc_dt,
                'M' date_type
  from stg.flow_business b
  left join stg.flow_cash c
    on c.flow_bus_id = b.flow_bus_id and c.dt='$yesterday' and c.sys='p2p'
--left join flow_account a on a.flow_cash_id = c.flow_cash_id
  left join stg.flow_dic_cash_type ct
    on ct.flow_cash_type_id = c.flow_cash_type_uuid and ct.dt='$yesterday' and ct.sys='p2p'
  left join stg.activity_reward_record arr
    on arr.id = b.business_id and arr.dt='$yesterday' and arr.sys='p2p'
  left join stg.activity_dic ad
    on ad.activity_code = arr.activity_type and ad.dt='$yesterday' and ad.sys='p2p'
  left join stg.admin_user au
    on au.user_id = arr.operator and au.dt='$yesterday' and au.sys='p2p'
 where 1 = 1
   and c.flow_cash_type_uuid in ('201801')
   and b.bus_state = '1'
      --and a.flow_acc_type = '2'
   --and b.create_date >= to_date('&startdate', 'yyyy-mm-dd')
   --and b.create_date < to_date('&enddate', 'yyyy-mm-dd')
            and cast(to_date(b.create_date) as date) >= date'$yesterday'
            and cast(to_date(b.create_date) as date) <  date_add(date'$yesterday',1)
   and b.dt='$yesterday' and b.sys='p2p'
 group by substr(b.create_date, 1,7), ad.activity_name, au.user_name;
 
测试 url:
localhost:19091//analysor/alltables?sql=同上SQL

返回结果 ：
前端接口：
AnalysorInitialService.nodeList:
[{"category":2,"name":"stg.flow_business","value":30},  -- InTables
{"category":2,"name":"stg.activity_dic","value":30},
{"category":1,"name":"APP/HQL/ACTI_REWARD_DET_D.hql","value":30},   -- 作业
{"category":2,"name":"acti_reward_det_d","value":30},             
{"category":2,"name":"stg.activity_reward_record","value":30},
{"category":0,"name":"app.acti_reward_det_d","value":30},      -- 分析目标表
{"category":2,"name":"stg.flow_cash","value":30},
{"category":2,"name":"stg.admin_user","value":30},
{"category":2,"name":"stg.flow_dic_cash_type","value":30}]


AnalysorInitialService.linkList:
[{"name":"INPUT","source":"stg.flow_dic_cash_type","target":"APP/HQL/ACTI_REWARD_DET_D.hql"}, -- INPUT  分析目标表 -> 作业
{"name":"INPUT","source":"acti_reward_det_d","target":"APP/HQL/ACTI_REWARD_DET_D.hql"},
{"name":"INPUT","source":"stg.flow_business","target":"APP/HQL/ACTI_REWARD_DET_D.hql"},
{"name":"INPUT","source":"stg.flow_cash","target":"APP/HQL/ACTI_REWARD_DET_D.hql"},
{"name":"INPUT","source":"stg.activity_reward_record","target":"APP/HQL/ACTI_REWARD_DET_D.hql"},
{"name":"OUTPUT","source":"APP/HQL/ACTI_REWARD_DET_D.hql","target":"app.acti_reward_det_d"},  -- OUTPUT  作业 -> 分析目标表
{"name":"INPUT","source":"stg.activity_dic","target":"APP/HQL/ACTI_REWARD_DET_D.hql"},
{"name":"INPUT","source":"stg.admin_user","target":"APP/HQL/ACTI_REWARD_DET_D.hql"}]
