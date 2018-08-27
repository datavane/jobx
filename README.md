##升级日志


V1.2.0 by 2018-xx-xx
```
    1)支持Window端,实现全平台编译和安装

    2)简化安装部署流程,提供三个脚本一键安装启动项目(build.sh,agent.sh,server.sh)
    
    3)更换项目rpc调度框架thrift为Netty和Mina
    
    4)引入zookeeper,更改心跳检测机制
    
    5)基于zookeeper,agent实现自动注册
    
    6)基于zookeeper实现server集群,动态增减server,job自动分配,实现高可用
    
    7)server端支持单机和集群两种部署方式,对用户来说一个参数决定哪种部署方式

    8)可以自由定义任务的执行身份
    
    9)server内置tomcat和jetty组件,支持开发者模式,大大减轻运维部署的难度
    
    10)webssh新增基于privateKey方式的登录
  
    11)支持上传文件到agent
    
    12)更强大的任务流调度
    
    13)支持对外提供api的方式,允许通过接口调度任务
    
    14)新增一系列的examples调度使用实例
    
    15)修复一系列已知bug,增强稳定性和可用性
    
    此版本是里程碑版本,从底层架构到项目实现都进行重新规划,大大提高了可用性和稳定性,建议所有人升级``
 ```   
V1.1.0 by 2017-04-20

    1)新增agent和server代理连接方式
    
    2)新增quartz类型的任务调度
    
    3)新增web终端,实现web终端文件上传,主题更换
    
    4)更改agnet和server心跳检查机制
    
    5)新增流程任务调度
    
    6)增加xss和csrf注入攻击的预防
    
    7)server端增加踢人下线单一登录控制

## jobx

    
一个功能完善真正通用的linux定时任务调度定系统,满足多种场景下各种复杂的定时任务调度,同时集成了linux实时监控,webssh,提供一个方便管理定时任务的平台.

你是否有定时执行任务计划的需求,需要在linux的crontab里一一定义任务?
 -  需要在每台linux服务器的crontab里一一定义任务
 -  任务的执行监控太不方便了
 -  得登录到每台机器查看定时任务的运行结果,机器一多简直是一种灾难
 -  对于多台机器协同处理一个任务很麻烦,如何保证多台机器上的任务按顺序依次执行?
 -  当任务运行失败,要重新执行,还得重新定义下执行时间,让其重跑,重跑完成了还得改回正常时间
 -  正在运行的任务要kill掉很麻烦,查看进程然后才能kill
......

jobx的出现将彻底的解决上面所有问题.功能如下:
 -  自动化管理任务,提供可操作的web图形化管理
 -  要当场执行只需点击执行即可,非常方便
 -  时间规则支持quartz和crontab,更强大更灵活
 -  非常方便的修改任务的执行时间
 -  任务的运行状态实时查看
 -  支持任务kill(包括由当前任务调起的其他子任务链,彻底kill)
 -  支持重新执行正在运行的任务
 -  出错后实时通知给任务人(超过重跑次数自动发送邮件,短信)
 -  支持任务超时设置,一旦超过预定运行时长自动kill,任务结束,防止僵尸任务
 -  支持流程任务(多台机器上协同完成一个大的任务,按任务分配的顺序依次执行每台机器上的任务)
 -  记录任务的运行日志,非常方便查看
 -  多用户多角色
 -  现场执行(选择N台机器同时执行一个命令或任务)
 -  webssh,在浏览器一键ssh登录到linux服务器
 -  提供服务器的性能实时监控
 ......
    

## 运行环境

Java JDK 1.7 or greater
http://www.oracle.com/technetwork/java/javase/overview/index.html

Tomcat server 8.0 or greater
https://tomcat.apache.org

Zookeeper
http://zookeeper.apache.org/

Redis
https://redis.io/

Browser IE10+

   
## 安装说明

 jobx分为两个jobx-server端和jobx-agent端,jobx-server端即为一个web可视化的中央管理调度平台,jobx-agent为要管理的任务的机器
 agent和server都依赖zookeeper,安装部署jobx之前必须先安装和启动zookeeper,server和agent必须连接同一个zookeeper,server端依赖redis

## 编译步骤:

```
1)下载源码: 
  > git clone https://github.com/jobxhub/jobx.git

2):编译:
  进入deployment
  > cd deployment
   修改配置信息
    jobx.password=jobx
    jobx.port=1577
    jobx.registry=zookeeper://${zookeeper_host}:2181?bakup=${zookeeper_host1}:2181,${zookeeper_host2}:2181

    #jobx.host=
    jdbc.driver=com.mysql.jdbc.Driver
    jdbc.url=jdbc:mysql://localhost:3306/jobx
    jdbc.username=root
    jdbc.password=${password}

    jobx.cluster=false
    jobx.cached=memcached

    redis.host=${redis.host}
    redis.password=${redis.password}
    redis.port=6379

    memcached.servers=${memcached.servers}
    memcached.protocol=BINARY

    *nix平台执行 sh build.sh
    window平台双击 build.bat
    
3):启动
    1）agent
        *nix平台:  执行 sh agent.sh
        window平台: 需要进入jobx-agent/target下,解包jobx-agent-${version}.tar.gz到指定的位置,进入bin,执行startup.bat
   
   2)  server
        *nix平台:  执行 sh server.sh 8080
        window平台 双击
        ---conf/server.bat

   3):进入到jobx的管理端,如果agent也启动了,应该可以直接在server的执行器页面看到agent,则添加任务即可...

```  

## 注意事项:
```
1):如果自行编译项目的,有可能agent端的脚本执行失败,这时请更改agent/bin下所有的脚本的字符集
   a) vim *.sh
   b) :set ff=unix 保存退出即可
   
2):如果agent已经成功启动server还是连接不上,请检查agent端口是否开放(如很多云服务器得开放端口才能访问)

3):如果server端用nginx做反向代理,配置如下:

   
upstream jobx {
     server 127.0.0.1:8080;
}

server {
    listen 80;
    server_name www.jobx.org;
    root /data/www/jobx/;

    location / {
        proxy_pass        http://jobx;
        proxy_set_header   Host             $host;
        proxy_set_header   X-Real-IP        $remote_addr;
        proxy_set_header   X-Forwarded-For  $proxy_add_x_forwarded_for;
        client_max_body_size  10m;
        client_body_buffer_size 1m;
        proxy_connect_timeout 300;
        proxy_send_timeout    300;
        proxy_read_timeout    300;
        proxy_buffer_size     4k;
        proxy_buffers    4   32k;
        proxy_busy_buffers_size 64k;
        proxy_temp_file_write_size  64k;
    }

    #这里必须这么配置,否则web终端无法使用
    location  ^~  /terminal.ws {
        proxy_pass http://jobx;
        proxy_redirect    off;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header Host $host;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

}


```

## 常见问题:
```
 1)创建作业运行身份无法选择？
   由于考虑到权限的问题,当前登录的用户不能随便指定任务的执行身份,需要超级管理员权限的用户(jobx)登录,在设置页面统一设置运行身份,多个运行身份用","分割,然后超级管理员在编辑用户
   为该用户指定可以执行的身份(可选择多个),这样用户在创建任务的时候就可以选择指定身份去执行了

```

更多问题请加入jobx交流群156429713,欢迎大家加入
    
