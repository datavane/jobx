--t_agent_group
create table `t_agent_group1` (
  `group_id` bigint(20) not null,
  `agent_id` bigint(20) not null,
  primary key (`group_id`,`agent_id`),
  key qa_agentid (`agent_id`)
) engine=innodb default charset=utf8;
insert into `t_agent_group1`(`group_id`,`agent_id`)
select `groupId`,`agentId`
from `t_agent_group`;
drop table `t_agent_group`;
alter table `t_agent_group1` rename `t_agent_group`;

--t_agent
alter table `t_agent` change column `agentid` `agent_id` bigint(20) not null auto_increment;
alter table `t_agent` change column `comment` `comment` text;
alter table `t_agent` change column `emailAddress` `email` text;
alter table `t_agent` change column `ip` `host` varchar(255);
alter table `t_agent` change column `machineId` `machine_id` varchar(64);
alter table `t_agent` change column `mobiles` `mobile` text;
alter table `t_agent` change column `name` `name` varchar(50);
alter table `t_agent` change column `password` `password` varchar(50);
alter table `t_agent` change column `notifyTime` `notify_time` datetime;
alter table `t_agent` change column `port` `port` int(10);
alter table `t_agent` change column `proxyAgent` `proxy_id` bigint(20);
alter table `t_agent` change column `status` `status` tinyint(1);
alter table `t_agent` change column `updateTime` `update_time` datetime;
alter table `t_agent` add column `platform` tinyint(1);
alter table `t_agent` drop column `proxy`;

--t_config
create table `t_config1` (
  `config_key` varchar(50) not null,
  `config_val` text,
  `comment` varchar(255) default null,
  primary key (`config_key`)
) engine=innodb default charset=utf8;
insert into `t_config1`(`config_key`,`config_val`,`comment`) select "sender_email",`senderEmail`,"发送邮箱的账号" from `t_config`;
insert into `t_config1`(`config_key`,`config_val`,`comment`) select "email_password",`password`,"发送邮箱的密码" from `t_config`;
insert into `t_config1`(`config_key`,`config_val`,`comment`) select "smtp_host",`smtpHost`,"发送邮箱的smtp" from `t_config`;
insert into `t_config1`(`config_key`,`config_val`,`comment`) select "use_ssl",1,"发送邮箱使用使用SSL" from `t_config`;
insert into `t_config1`(`config_key`,`config_val`,`comment`) select "smtp_port",`smtpPort`,"发送邮箱的port" from `t_config`;
insert into `t_config1`(`config_key`,`config_val`,`comment`) select "send_url",`sendUrl`,"短信发送通道商url" from `t_config`;
insert into `t_config1`(`config_key`,`config_val`,`comment`) select "space_time",`spaceTime`,"发送告警时间间隔" from `t_config`;
insert into `t_config1`(`config_key`,`config_val`,`comment`) select "template",`template`,"发送短信的模板" from `t_config`;
insert into `t_config1`(`config_key`,`config_val`,`comment`) select "version","V1.2.0","当前JobX版本号" from `t_config`;
insert into `t_config1`(`config_key`,`config_val`,`comment`) values ("exec_user",null,"该平台执行任务的代理用户,多个用户用\",\"分隔");
drop table `t_config`;
alter table `t_config1` rename `t_config`;

--t_group
alter table `t_group` change column `groupId` `group_id` bigint(20) auto_increment;
alter table `t_group` change column `comment` `comment` text;
alter table `t_group` change column `createTime` `create_time` datetime;
alter table `t_group` change column `groupName` `group_name` varchar(255);
alter table `t_group` change column `userId` `user_id` bigint(20);

--job
alter table `t_job` change column `jobId` `job_id` bigint(20) auto_increment;
alter table `t_job` change column `agentId` `agent_id` bigint(20);
alter table `t_job` change column `userId` `user_id` bigint(20);
alter table `t_job` change column `jobType` `job_type` tinyint(1);
alter table `t_job` change column `jobName` `job_name` varchar(50);
alter table `t_job` change column `cronExp` `cron_exp` varchar(255);
alter table `t_job` change column `emailAddress` `email` text;
alter table `t_job` change column `mobiles` `mobile` text;
alter table `t_job` change column `redo` `redo` bit(1);
alter table `t_job` change column `runCount` `run_count` int(10);
alter table `t_job` change column `successExit` `success_exit` varchar(255);
alter table `t_job` change column `token` `token` varchar(64);
alter table `t_job` change column `updateTime` `update_time` datetime;
alter table `t_job` change column `timeout` `timeout` int(10);
alter table `t_job` change column `createType` `create_type` tinyint(1);
alter table `t_job` add column `create_type` tinyint(1);
alter table `t_job` add column  `token` varchar(64);
alter table `t_job` add column `pause` bit(1);
alter table `t_job` add column `exec_user` varchar(50);
alter table `t_job` add index qa_agent_id (`agent_id`);
update `t_job` set `pause`=0 where `pause` is null ;
update `t_job` set `token`=MD5(RAND()) where `token` is null;
update `t_job` set `create_type`=1 where `job_type`=0 or `flowNum`=0;
alter table `t_job` drop column `flowId`;
alter table `t_job` drop column `flowNum`;
alter table `t_job` drop column `lastChild`;
alter table `t_job` drop column `runEntity`;
alter table `t_job` drop column `cronType`;

--log
alter table `t_log` change column `logId` `log_id` bigint(20) auto_increment;
alter table `t_log` change column `agentId` `agent_id` bigint(20);
alter table `t_log` change column `userId` `user_id` bigint(20);
alter table `t_log` change column `isread` `is_read` bit(1);
alter table `t_log` change column `sendTime` `send_time` datetime;
alter table `t_log` change column `type` `type` tinyint(1);

--t_role
alter table `t_role` change column `roleId` `role_id` bigint(20);
alter table `t_role` change column `roleName` `role_name` varchar(255);

--t_terminal
alter table `t_terminal` change column `logintime` `login_time` datetime;
alter table `t_terminal` change column `port` `port` int(10);
alter table `t_terminal` change column `privateKey` `private_key` blob;
alter table `t_terminal` change column `sshType` `ssh_type` tinyint(1);
alter table `t_terminal` change column `status` `status` varchar(20);
alter table `t_terminal` change column `theme` `theme` varchar(20);
alter table `t_terminal` change column `userId` `user_id` bigint(20);
alter table `t_terminal` change column `userName` `user_name` varchar(50);

--t_t_record
--t_record_message
create table `t_record_message` (
  `record_id` bigint(20) not null,
  `message` longtext,
  `start_time` datetime,
  primary key (`record_id`)
) engine=innodb default charset=utf8;

insert into `t_record_message`(`record_id`,`message`,`start_time`) select `recordId`,`message`,`startTime` from `t_record`;
alter table `t_record` change column `recordId` `record_id` bigint(20) auto_increment;
alter table `t_record` change column `agentId` `agent_id` bigint(20);
alter table `t_record` change column `startTime` `start_time` datetime;
alter table `t_record` change column `endTime` `end_time` datetime;
alter table `t_record` change column `execType` `exec_type` tinyint(1);
alter table `t_record` change column `jobId` `job_id` bigint(20);
alter table `t_record` change column `jobType` `job_type` tinyint(1);
alter table `t_record` change column `parentId` `parent_id` bigint(20);
alter table `t_record` change column `userId` `user_id` bigint(20);
alter table `t_record` change column `status` `status` int(10);
alter table `t_record` change column `success` `success` int(10);
alter table `t_record` change column `pid` `pid` varchar(64);
alter table `t_record` change column `groupId` `group_id` bigint(20);
alter table `t_record` change column `redoCount` `redo_num` int(10);
alter table `t_record` change column `returnCode` `return_code` int(10);
alter table `t_record` drop column `flowNum`;
alter table `t_record` drop column `redo`;
alter table `t_record` drop column `runCount`;
alter table `t_record` drop column `message`;
alter table `t_record` add column `exec_user` varchar(50);
alter table `t_record` add column `job_name` varchar(50);
alter table `t_record` add index qa_success(`success`);

update `t_record` as r inner join `t_job` as t set r.job_name=t.job_name where r.job_id = t.job_id;
                     
--t_user
alter table `t_user` change column `userId` `user_id` bigint(20) auto_increment;
alter table `t_user` change column `userName` `user_name` varchar(50);
alter table `t_user` change column `password` `password` varchar(50);
alter table `t_user` change column `contact` `contact` varchar(50);
alter table `t_user` change column `email` `email` varchar(50);
alter table `t_user` change column `headerpic` `header_pic` longblob;
alter table `t_user` change column `createTime` `create_time` datetime;
alter table `t_user` change column `modifyTime` `modify_time` datetime;
alter table `t_user` change column `picExtName` `pic_ext_name` varchar(10);
alter table `t_user` change column `qq` `qq` varchar(20);
alter table `t_user` change column `realName` `real_name` varchar(50);
alter table `t_user` change column `roleId` `role_id` bigint(20);
alter table `t_user` change column `salt` `salt` varchar(64);
alter table `t_user` add column `exec_user` text;

--t_user_agent
create table `t_user_agent` (
  `id` bigint(20) not null auto_increment,
  `user_id` int(11),
  `agent_id` int(11),
  primary key (`id`)
) engine=innodb auto_increment=10000 default charset=utf8;
