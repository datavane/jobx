<template>
  <div class="login">
    <!-- Login -->
    <div class="login__block active" id="l-login">
      <div class="login__block__header">
        <h1 class="login_logo">
          <img src="static/logo.png"/>
        </h1>
        {{slogan}}
      </div>
      <div class="login__block__body">
        <span class="error_msg" v-if="info.length>0" :style="{color:status?'green':'red'}">{{info}}</span>
        <div class="form-group user__name">
          <input type="text" class="form-control text-center" v-model="userName" placeholder="Address">
        </div>
        <div class="form-group">
          <input type="password" class="form-control text-center" v-model="password" placeholder="Password">
        </div>
        <span @click="tologin" class="btn btn--icon login__block__btn"><i class="zmdi zmdi-long-arrow-right"></i></span>
      </div>
    </div>
  </div>
</template>

<script type="text/ecmascript-6">
  import md5 from 'md5'
  import {mapActions} from 'vuex'
  export default {
    data() {
      return {
        slogan: "Let's scheduling easy",
        status: true,
        info: "",
        userName: "",
        password: ""
      }
    },
    methods: {
      ...mapActions(['login']),
      tologin() {
        if (this.userName.length == 0) {
          this.info = "请输入用户名"
          this.status = false
          return
        }
        if (this.password.length == 0) {
          this.info = "请输入密码"
          this.status = false
          return
        }
        this.status = true
        this.$http.post('/login.do',{
            userName:this.userName,
            password:md5(this.password)
        }).then(response=> {
          switch(response.code) {
            case 500:
              this.info = "用户名密码错误"
              this.status = false 
            break
            case 201:
              let userId = response.body.userId
              this.info = "请修改默认密码"
              this.status = false
            break
            case 200:
              this.info = "登录成功,正在进入主页..."
              this.status = true
              this.login({user:response.body.user})
              this.$storage.set(this.$const.keys.xsrf,response.body.xsrf)
              setTimeout(()=>{
                this.info=""
                this.$router.push({path:'/dashboard'})
              },1000)
            break
          }
        },error=> {
          console.log(error)
        })
      }
       
    }
  }
</script>

<style lang="scss">
  .login_logo {
    width:250px;
    img {
      width:200px;
    }
  }
  .form-group {
    padding-top:10px;
  }
  .error_msg {
    margin-top:-15px;
    margin-bottom:8px;
  }
</style>
