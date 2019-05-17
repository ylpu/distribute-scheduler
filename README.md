# Introduction
distribute-scheduler是一款自主研发的分布式工作流调度系统，系统目前支持命令方式和jar方式提交任务，系统提供了任务dag解析，pool的最优调度，重试，监控，告警等一些列功能。
# Architecture
![image](https://github.com/ylpu/distribute-scheduler/blob/master/files/arch.png)
# Component
* distribute-scheduler-client
client端主要负责任务dag解析与分发，根据任务所属的pool从resource manager中选择最优的机器去提交任务，任务失败后会根据用户配置的重试次数选择其它机器重新提交任务；监听zookeeper中pool下面机器的变化，如果有机器掉线，关闭客户端到该机器的连接池

* distribute-scheduler-executor
executor端在启动的时候首先会把自己注册到zookeeper上，其次会启动一个jetty server,jetty server主要用于用户获取任务的输出信息和错误信息,最后当任务执行完成更新任务状态并调用resourcemanager释放资源

* distribute-scheduler-resoucemanger
resourcemanager主要负责管理机器的资源；暴露jmx信息给外部；监听zookeeper中机器的变化，自动刷新内存中维护的机器列表；为client端提供最优的机器选择策略，目前支持3中策略：1.最优资源策略，提供任务pool中内存最多的一台机器给客户端。2.最小任务策略，提供pool中机器任务数最少的一台机器给客户端。3.随机策略，从pool中随机选择一台机器给客户端。同时支持基于mesos和k8s的资源管理。

* distribute-scheduler-common
common提供了任务调度的bean和utils等相关类

* distribute-scheduler-core
core主要提供了rpc框架，zookeeper,jersey等一些帮助类

* distribute-scheduler-web
web主要负责任务的调度，停止，查看以及更新（目前还在开发中）

# Feature
* 自定义DAG
* 分布式部署
* 基于真实资源，每一次任务提交都会减少机器的真实资源
* 资源隔离，相同类型的任务可以提交到对应的资源池
* 支持大规模任务调度
* 节点掉线自动发现
* 任务失败后自动重试
* 任务失败后自动告警
* 可以通过页面调度，停止，查看任务详情和依赖等等
* 既可以执行普通的工作流又可以调度定时任务
* 可以调度大数据任务
