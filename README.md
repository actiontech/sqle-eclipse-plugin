# SQLE Eclipse Plugin

## 简介

该项目是[SQLE](https://github.com/actiontech/sqle) 的 Eclipse 审核插件 , 该插件满足开发人员在开发阶段进行实时自助式的静态SQL审核，实现开发阶段审核上线等各个SQL开发阶段的SQL开发规范。

## 编译
Java8版本

Eclipse插件最低适配Eclipse 2015 mars版本

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
   ![20240118-133603](https://github.com/actiontech/sqle-eclipse-plugin/assets/53266479/4d113257-0b28-4bc8-81d5-1d955ca95009)

  - 配置说明

| 配置项             | 配置项说明                                            |
|-----------------|--------------------------------------------------|
| SQLE Addr       | SQLE 服务地址, 格式为 IP:Port                           |
| HTTP       | 指定通过http的方式连接SQLE还是通过https的方式连接                           |
| UserName        | 登录SQLE使用的用户名                                     |
| Password        | 登录SQLE使用的密码                                      |
| Test Connection | 测试连接是否成功, 将会尝试登录                                 |
| Project         | 项目名称                                             |
| DB Type         | 当其他配置正确时此下拉框会自动获取支持审核的实例类型, 选择后会使用此实例类型的审核规则进行审核 |
| Data Source     | 数据源名称                                            |
| Schema          | 数据库名称                                            |

### 使用说明
1. 选中需要审核的SQL, 可以同时选中多条SQL.点击右键, 选中 [SQLE SQL Audit] ,  插件会以视图的形式将审核结果进行展示
![20240118-133916](https://github.com/actiontech/sqle-eclipse-plugin/assets/53266479/ca3dd9ae-501e-48e0-867f-5467e4ff61b1)

2. 审核mybatis xml文件,选中mybatis xml文件,鼠标右击选中 [SQLE Mybatis Audit]
![20240118-134103](https://github.com/actiontech/sqle-eclipse-plugin/assets/53266479/6b8cd9a4-4676-415c-9000-0a8407c17efd)

3. 审核sql文件，选中sql文件，鼠标右击选中 [SQLE SQL Audit]
![20240118-134219](https://github.com/actiontech/sqle-eclipse-plugin/assets/53266479/65ea91da-537c-4d01-a912-9d82fb64952c)

4. 审核xml文件夹，选中文件夹，鼠标右击选中 [SQLE Mybatis Folder Audit]
![20240118-134450](https://github.com/actiontech/sqle-eclipse-plugin/assets/53266479/c1ab3c24-8d71-4266-9654-8ef12ae989fe)


