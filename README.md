# datacenter
数据平台

本项目适用于百亿行、TB级日志存储，提供秒级检索和统计能力。可以面向非专业人群开放日志查询和统计功能。

全量数据存储在HBase，采用ElasticSearch维护二级索引，支持userid、手机号、操作类别和操作时间多条件复合查询；

用户界面采用element组件开发，支持动态曲线图、数据表格 两种数据可视化形式；

借助MCC，实现查询参数模板化动态配置，使用者无需关心参数细节；

同时具备日志数据实时分析和报警能力。
