# SQLE Eclipse Plugin

## 简介

该项目是[SQLE](https://github.com/actiontech/sqle) 的 Eclipse 审核插件 , 该插件满足开发人员在开发阶段进行实时自助式的静态SQL审核，实现开发阶段审核上线等各个SQL开发阶段的SQL开发规范。


## 使用方式

### 下载方式

### 安装插件
将下载的jar包，复制到eclipse编辑器目录中的dropins文件夹中
![安装插件](https://github.com/actiontech/sqle-ee/assets/53266479/c6f1144f-88ad-49a0-961a-309b73d53d3c)
### 配置插件
  - 点击[Window]-[Preferences]-[SQLE]
  - 输入SQLE Addr，用户以及密码
  - 点击Test Connection，确保可以连接到SQLE，并获取Project和DBType列表
  - 依次选取Project，DBType，Data Source和Schema下拉框
  - 点击Apply and Close保存配置并退出配置界面
    ![20240112-173243](https://github.com/actiontech/sqle-eclipse-plugin/assets/53266479/bf182405-5038-4866-8618-6ef0f1569de1)
  - 配置说明

| 配置项             | 配置项说明                                            |
|-----------------|--------------------------------------------------|
| SQLE Addr       | SQLE 服务地址, 格式为 IP:Port                           |
| UserName        | 登录SQLE使用的用户名                                     |
| Password        | 登录SQLE使用的密码                                      |
| Test Connection | 测试连接是否成功, 将会尝试登录                                 |
| Project         | 项目名称                                             |
| DB Type         | 当其他配置正确时此下拉框会自动获取支持审核的实例类型, 选择后会使用此实例类型的审核规则进行审核 |
| Data Source     | 数据源名称                                            |
| Schema          | 数据库名称                                            |

### 使用说明
1. 选中需要审核的SQL, 可以同时选中多条SQL.点击右键, 选中 [sqle audit] ,  插件会以视图的形式将审核结果进行展示
![20240112-174009](https://github.com/actiontech/sqle-eclipse-plugin/assets/53266479/af8d6dc5-43cc-4b34-8626-65d68b6e57af)

2. 审核mybatis xml文件,选中mybatis xml文件,鼠标右击选中 [sqle audit]
![20240112-174318](https://github.com/actiontech/sqle-eclipse-plugin/assets/53266479/494d7d33-eb65-4dea-990c-d99dd3f59f64)

3. 审核sql文件，选中sql文件，鼠标右击选中 [sqle audit]
   ![20240112-174531](https://github.com/actiontech/sqle-eclipse-plugin/assets/53266479/b03ff8a9-d4ce-48fc-9046-27fffacf2e48)



