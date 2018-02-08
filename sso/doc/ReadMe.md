# 权限相关
## 初期没有界面只有接口，但是批量操作的话，最好是用sql

- 先添加资源
       INSERT INTO dbauthorities(db_engine,db,table_name,create_date) VALUES ('mysql',"test_db","t_admin",NOW());   

- 添加角色
       INSERT INTO dbrole(name,status,create_date) VALUES ('DB_lender',1,NOW());
        
- 添加关联(注意ID)
        INSERT INTO dbrole_authorities(role_id,authority_id,db_type,create_date) VALUES (3,3,'DM',NOW())
        
        INSERT INTO dbrole_authorities(role_id,authority_id,db_type,create_date) VALUES ((select id from dbrole where name = 'DB_role'),(SELECT id from dbauthorities WHERE db_engine="sparksql" AND db="default" AND table_name="test"),'DM',NOW())
        
        
# 数据查询     
1.1接口地址：
/facade/open/excuteQueryByDbEngine

1.2请求类型：GET

1.3接口参数：
参数              类型              说明
sql             String          Sql语句
jobId           String          任务ID
username        String          当前登录用户
dBEngine        String          数据驱动
startTime       String          起始时间
endTime         String          结束时间


1.4返回参数：
参数              类型              说明
data            List            数据集
columns         List            数据集中包含的字段
success         Boolean         是否成功
errorMsg        String          如果失败，错误信息；否则为空字符串

1.5代码示例：
{
data=[{name=aa, age=11, sex=1}, {name=bb, age=22, sex=2}, {name=cc, age=33, sex=3}], 
columns=[name, age, sex], 
success=true, 
errorMsg=
}

{
data=null, 
columns=null, 
success=false, 
errorMsg=Connection refused: connect
}





















