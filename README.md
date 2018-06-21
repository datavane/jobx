##升级日志


V1.2.0 by 2018-xx-xx
```
    1)Window端支持强势来袭,实现全平台编译和安装

    2)更换项目rpc调度框架thrift为Netty和Mina
    
    3)引入zookeeper,更改连续心跳检测机制为zookeeper通知的方式
    
    4)基于zookeeper,agent实现自动注册
    
    5)基于zookeeper实现server集群,动态增减server,job自动分配,实现高可用
    
    6)server端支持单机和集群两种部署方式,对用户来说一个参数决定哪种部署方式

    7)server在集群情况下处理分布式session的缓存实现redis和memcached可选
    
    8)可以自由定义任务的执行身份
    
    9)简化安装部署流程,提供三个脚本一键安装启动项目(build.sh,agent.sh,server.sh)
    
    10)server内置tomcat和jetty组件,支持开发者模式,大大减轻运维部署的难度
    
    11)改进首页实时监控,大幅度提升agent端性能
    
    12)webssh新增基于privateKey方式的登录
  
    13)支持上传文件到agent
    
    14)更强大的任务流调度
    
    15)支持对外提供api的方式,允许通过接口调度任务
    
    16)新增一系列的examples调度使用实例
    
    17)修复一系列已知bug,增强稳定性和可用性
    
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
  > git clone https://github.com/wolfboys/jobx.git

2):修改server端的配置信息
   1:创建数据,数据库名字可以是jobx或者其他
   2:进入jobx-server/src/main/resources 修改config.properties里的jdbc连接信息
   
   #jdbc
   jdbc.driver=com.mysql.jdbc.Driver
   jdbc.url=jdbc:mysql://${mysql_host}:3306/jobx?useUnicode=true&characterEncoding=UTF-8
   jdbc.username=${user}
   jdbc.password=${password}
   
   #redis
   redis.host=${redis.host}
   redis.password=${redis.password}
   redis.port=${redis.port}
   
   #zookeeper
   jobx.registry=zookeeper://${zookeeper_host}:2181?bakup=${zookeeper_host1}:2181,${zookeeper_host2}:2181
3):进入源码目录并执行编译:
   cd jobx
   sh build.sh
编译完成的文件在build/dist下


## jobx-agent 部署安装步骤

1) 执行运行agent.sh即可 或者手动部署agent
  
    手动部署agent步骤
    
    将jobx-agent-${version}.tar.gz包拷贝到要管理任务的目标服务器,解包,会看到以下目录
    ---bin/
    |  startup.sh          #agent的启动脚本,调用的是jobx.sh来完成
    |  shutdown.sh         #agent停止脚本，调用的是jobx.sh来完成
    |  jobx.sh         #agent控制启动|停止的脚本
    |  monitor.sh          #实时监控获取数据需要的脚本,由系统调度
    |  kill.sh             #kill任务时需要的脚本,由系统调度
    ---conf/
       conf.properties     #agent配置文件 
    |  log4j.properties    #log4j配置文件
    ---lib/
    | *.jar                #agent运行需要的jar文件
    ---temp/
    | *.sh                 #用于存放项目生成的零时文件的目录
    ---logs
    | jobx.out         #项目启动会产生的Log文件
    
    > tar -xzvf jobx-agent-${version}.tar.gz
    1)修改conf/conf.properties里的配置信息
        #zookepper注册中心
        jobx.registry=zookeeper://${zookeeper_host}:2181
        #agent Ip,确保server可以通过此ip访问到该agent(主要实现agent自动注册)
        jobx.host=127.0.0.1
    2)启动jobx-agent 进入jobx-agent/bin
    > cd jobx-agent/bin
    > sh startup.sh
    这里可以接受两个参数，分别是服务启动的端口和密码(默认端口是:1577,默认密码:jobx)以及agent自动注册的url和密码 
    如要指定参数启动命令如下:
    > sh startup.sh -P10001 -p123456
    参数说明:
    -P (大写的p)为agent启动的端口，选填，如果不输入默认启动端口是1577
    -p (小写的p)为当前agent的连接密码,选填，如果不输入默认连接该机器的密码是jobx
    以下两个参数为agent自动注册需要的两个参数（选填）
    该脚本启动之后agent就自动注册到server端了
    更多详细的启动信息请查看logs/jobx.out
    
    3)停止jobx-agent 进入jobx-agent/bin 执行：
    > cd jobx-agent/bin
    > sh shutdown.sh

```
  
## jobx-server 部署步骤:

```
1):编译好项目源码

2):部署启动server
  由两种部署方式,  
  1:自动部署执行server.sh即可,该项目已经内置了Tomcat和Jetty,要实现自动部署很简单,运行项目根路径下的server.sh即可完成启动(默认运行的是Tomcat)
  2:手动发布 tomcat或者其他web服务器 
  tomcat发布项目步骤:
     找到build/dist/jobx-server.war
     tomcat部署有两种部署方式
     1):直接部署到webapps下:
        1:下载tomcat8或者以上版本(http://tomcat.apache.org)
        2:解压tomcat,删除webapps目录下的全部文件 
          >  rm -rf ${tomcat_home}/webapps/*
        3:在webapps下新建ROOT文件夹 
          >  mkdir ${tomcat_home}/webapps/ROOT
        4:将war解包到ROOT下并删除war文件(注意解包完毕一定要删除war包)
          >  mv server.war ${tomcat_home}/webapps/ROOT 
          >  cd ${tomcat_home}/webapps/ROOT 
          >  jar -xvf server.war 
          >  rm -rf server.war
        5:更改jdbc和zookeeper配置信息 
          > vi ${tomcat_home}/webapps/ROOT/WEB-INF/classes/config.properties
        6:完成启动
     2):通过配置server.xml外部指向
        1:将war包解压到指定的路径,如 /data/www/jobx,并删除war包
        2:更改jdbc配置文件
           vi /data/www/jobx/WEB-INF/classes/config.properties
        3:进入tomcat的conf中修改server.xml配置文件
           下面附上我的完整的server.xml配置:
           
           <?xml version='1.0' encoding='utf-8'?>
           <Server port="7000" shutdown="SHUTDOWN">
             <Listener className="org.apache.catalina.startup.VersionLoggerListener" />
             <Listener className="org.apache.catalina.core.AprLifecycleListener" SSLEngine="on" />
             <Listener className="org.apache.catalina.core.JreMemoryLeakPreventionListener" />
             <Listener className="org.apache.catalina.mbeans.GlobalResourcesLifecycleListener" />
             <Listener className="org.apache.catalina.core.ThreadLocalLeakPreventionListener" />
           
             <GlobalNamingResources>
               <Resource name="UserDatabase" auth="Container"
                         type="org.apache.catalina.UserDatabase"
                         description="User database that can be updated and saved"
                         factory="org.apache.catalina.users.MemoryUserDatabaseFactory"
                         pathname="conf/tomcat-users.xml" />
             </GlobalNamingResources>
           
               <Service name="Catalina">
               
               <!--项目的访问端口-->
               <Connector port="8080" protocol="HTTP/1.1"
                           connectionTimeout="20000"
                          maxThreads="550"
                           minSpareThreads="25"
                           maxSpareThreads="75"
                           minProcessors="100"
                           maxProcessors="300"
                          acceptCount="100"
                          enableLookups="false"
                          disableUploadTimeout="true"
                          compression="on"
                          compressionMinSize="2048"
                          compressableMimeType="text/html,text/xml,text/javascript,text/css,text/plain"
                          redirectPort="7970"
                          URIEncoding="UTF-8"/>
                   <Connector port="3007" protocol="AJP/1.3" redirectPort="2007" />
           
                    <Engine name="Catalina" defaultHost="localhost">
                           <Realm className="org.apache.catalina.realm.UserDatabaseRealm" resourceName="UserDatabase"/>
                           <Host name="localhost"
                                   appBase="/data/www/jobx"
                                   unpackWARs="true"
                                   autoDeploy="false"
                                   xmlValidation="false"
                                   xmlNamespaceAware="false"
                                   URIEncoding="UTF-8">
           
                           <Context path="/"
                                   docBase="/data/www/jobx"
                                   debug="0"
                                   reloadable="true"/>
                           </Host>
           
                   </Engine>
             </Service>
           
           </Server>
           
           配置里Host里的appBase和Context的docBase即为外部解压的项目的路径
           推荐第二种外部部署的方式
           
        启动tomcat,打开浏览器以$ip:$port的方式访问,如:  http://192.168.0.188:8080   
        
  不论哪种方式部署,第一次会自动创建表,默认初始用户名jobx,密码jobx,第一次登陆会提示修改密码.
      
3):进入到jobx的管理端,如果agent也启动了,应该可以直接在server的执行器页面看到agent,则添加任务即可...

```  

## 注意事项:
```
1):如果自行编译项目的,有可能agent端的脚本执行失败,这时请更改agent/bin下所有的脚本的字符集
   a) vim *.sh
   b) :set ff=unix 保存退出即可

2):如果脚本字符编码已经是unix,还是启动失败,请尝试给启动脚本添加权限 chmod 777 bin/*
   
3):如果agent已经成功启动server还是连接不上,请检查agent端口是否开放(如很多云服务器得开放端口才能访问)

4):如果server端用nginx做反向代理,配置如下:

   
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

更多问题请加入jobx交流群156429713,欢迎大家加入
    
