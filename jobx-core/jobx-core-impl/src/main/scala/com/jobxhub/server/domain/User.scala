package com.jobxhub.core.model


import javax.persistence.Transient

class User {

   var userId = 0L

   var roleId = 0L

   var userName = null

   var password = null

   var salt = null

   var realName = null

   var contact = null

   var email = null

   var qq = null

   var createTime = null

   var modifyTime = null

  @Transient  var roleName = null

   var headerPic = null

  //头像文件的后缀名字
   var picExtName = null

   var execUser = null
}
