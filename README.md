## opencron

    
一个功能完善真正通用的linux定时任务调度定系统,满足多种场景下各种复杂的定时任务调度,同时集成了linux实时监控,webssh,提供一个方便管理定时任务的平台.

你是否有定时执行任务计划的需求,需要在linux的crontab里一一定义任务?
 -  需要在每台linux服务器的crontab里一一定义任务
 -  任务的执行监控太不方便了
 -  得登录到每台机器查看定时任务的运行结果,机器一多简直是一种灾难
 -  对于多台机器协同处理一个任务很麻烦,如何保证多台机器上的任务按顺序依次执行?
 -  当任务运行失败,要重新执行,还得重新定义下执行时间,让其重跑,重跑完成了还得改回正常时间
 -  正在运行的任务要kill掉很麻烦,查看进程然后才能kill
......

opencron的出现将彻底的解决上面所有问题.功能如下:
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

Browser 
IE10+
   
## 安装步骤

 opencron分为两个opencron-server端和opencron-agent端，opencron-server端即为一个web可视化的中央管理调度平台,opencron-agent为要管理的任务的机器,每个要纳入中央统一管理的机器都必须安装opencron-agent, opencron-agent在要管理的服务器中安装执行完后，可以直接在opencron-server添加当前的机器.


## opencron-agent 安装步骤:
```

1)下载源码: 
> git clone https://github.com/wolfboys/opencron.git

2):修改server端的jdbc连接信息
   1:创建数据,数据库名字可以是opencron或者其他
   2:进入opencron-server/src/main/resources 修改config.properties里的jdbc连接信息
   
   jdbc.driver=com.mysql.jdbc.Driver
   jdbc.url=jdbc:mysql://${you_mysql_host}:3306/opencron?useUnicode=true&characterEncoding=UTF-8
   jdbc.username=${user}
   jdbc.password=${password}

3):进入源码目录并执行编译:
> cd opencron
> sh build.sh
编译完成的文件在build/dist下

4) 部署agent

  执行运行agent.sh即可 或者手动部署agent
  
    手动部署agent步骤
    
    将opencron-agent-${version}.tar.gz包拷贝到要管理任务的目标服务器,解包,会看到以下目录
    ---bin/
    |  startup.sh          #agent的启动脚本,调用的是opencron.sh来完成
    |  shutdown.sh         #agent停止脚本，调用的是opencron.sh来完成
    |  opencron.sh         #agent控制启动|停止的脚本
    |  monitor.sh          #实时监控获取数据需要的脚本,由系统调度
    |  kill.sh             #kill任务时需要的脚本,由系统调度
    ---conf/
    | log4j.properties     #log4j配置文件
    ---lib/
    | *.jar                #agent运行需要的jar文件
    ---temp/
    | *.sh                 #用于存放项目生成的零时文件的目录
    ---logs
    | opencron.out         #项目启动会产生的Log文件
    
    > tar -xzvf opencron-agent-${version}.tar.gz
    1)启动opencron-agent 进入opencron-agent/bin
    > cd opencron-agent/bin
    > sh startup.sh
    这里可以接受四个参数，分别是服务启动的端口和密码(默认端口是:1577,默认密码:opencron)以及agent自动注册的url和密码 
    如要指定参数启动命令如下:
    > sh startup.sh -P10001 -p123456 -shttp://127.0.0.1:8080 -kopencron@2016
    参数说明:
    -P (大写的p)为agent启动的端口，选填，如果不输入默认启动端口是1577
    -p (小写的p)为当前agent的连接密码,选填，如果不输入默认连接该机器的密码是opencron
    以下两个参数为agent自动注册需要的两个参数（选填）
    -s 填写opencron-server部署之后的访问地址 
    -k 填写自动发现的密码,对应 opencron-server/src/main/resources/config.properties 里的opencron.autoRegKey
    更多详细的启动信息请查看logs/opencron.out
    
    
    2)停止opencron-agent 进入opencron-agent/bin 执行：
    > cd opencron-agent/bin
    > sh shutdown.sh

```
  
## opencron-server 部署步骤:

```
1):编译好项目源码找到

2):部署启动server
  由两种部署方式,  
  1:自动部署执行server.sh即可,该项目已经内置了Tomcat和Jetty,要实现自动部署很简单,运行项目根路径下的server.sh即可完成启动(默认运行的是Tomcat)
  2:手动发布 tomcat或者其他web服务器 
  tomcat发布项目步骤:
     找到build/dist/opencron-server.war
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
        5:更改jdbc配置信息 
          > vi ${tomcat_home}/webapps/ROOT/WEB-INF/classes/config.properties
        6:完成启动
     2):通过配置server.xml外部指向
        1:将war包解压到指定的路径,如 /data/www/opencron,并删除war包
        2:更改jdbc配置文件
           vi /data/www/opencron/WEB-INF/classes/config.properties
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
                                   appBase="/data/www/opencron"
                                   unpackWARs="true"
                                   autoDeploy="false"
                                   xmlValidation="false"
                                   xmlNamespaceAware="false"
                                   URIEncoding="UTF-8">
           
                           <Context path="/"
                                   docBase="/data/www/opencron"
                                   debug="0"
                                   reloadable="true"/>
                           </Host>
           
                   </Engine>
             </Service>
           
           </Server>
           
           配置里Host里的appBase和Context的docBase即为外部解压的项目的路径
           推荐第二种外部部署的方式
           
        启动tomcat,打开浏览器以$ip:$port的方式访问,如:  http://192.168.0.188:8080   
        
  不论哪种方式部署,第一次会自动创建表,默认初始用户名opencron,密码opencron,第一次登陆会提示修改密码.
    
      
3):进入到opencron的管理端第一件要做的事情就是添加要管理的执行器.在菜单的第二栏点击"执行器管理"->添加执行器,执行器ip，
就是上面你部署的opencron-agent的机器ip，端口号是要连接的opencron-agent的启动端口，密码也是opencron-agent端的连接密码,
输入ip,端口和密码后点击"检查通信",如果成功则server和agnet端已经成功通信，server可以管理agent了,添加保持即可.
如果连接失败，先检查agent端启动是否成功,查看logs中的详情

```  

## 注意事项:
```
1):如果自行编译项目的,有可能agent端的脚本执行失败,这时请更改agent/bin下所有的脚本的字符集
   a) vim *.sh
   b) :set ff=unix 保存退出即可

2):如果脚本字符编码已经是unix,还是启动失败,请尝试给启动脚本添加权限 chmod 777 bin/*
   
3):如果agent已经成功启动server还是连接不上,请检查agent端口是否开放(如很多云服务器得开放端口才能访问)

4):如果server端用nginx做反向代理,配置如下:

   
upstream opencron {
     server 127.0.0.1:8080;
}

server {
    listen 80;
    server_name www.opencron.org;
    root /data/www/opencron/;

    location / {
        proxy_pass        http://opencron;
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
        proxy_pass http://opencron;
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

更多问题请加入opencron交流群156429713,欢迎大家加入
    
