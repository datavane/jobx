package com.jobxhub.core.model


class Agent {

   var agentId = 0L

  //执行器机器的唯一id(当前取的是机器的MAC地址)
   var machineId = null

  //代理执行器的Id
   var proxyId = 0L

   var host = null
   var platform = null
   var port = null
   var name = null
   var password = null
   var warning = false

   var email = null
   var mobile = null

   var status = null //1通讯成功,0:失败失联,2:密码错误

   var notifyTime = null //失败后发送通知告警的时间

   var comment = null
   var updateTime = null

  
}
