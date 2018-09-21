<template>
  <section class="content">
    <div class="content__inner content__inner--sm">
      <div class="card new-contact">
        <div class="new-contact__header">
          <a href="" class="zmdi zmdi-camera new-contact__upload"></a>
          <img src="/static/img/profile-pics/profile-pic.jpg" class="new-contact__img" alt="">
        </div>
        <div class="card-body">
          <mu-form ref="form" :model="profile" class="mu-demo-form">
              <mu-form-item label="发件邮箱" help-text="用于发送告警的邮箱" prop="senderEmail"  :rules="rules.mail">
                <mu-text-field v-model="profile.senderEmail" prop="senderEmail"></mu-text-field>
              </mu-form-item>
              <mu-form-item label="邮箱密码" help-text="发件邮箱的密码" prop="emailPassword" :rules="rules.password">
                <mu-text-field v-model="profile.emailPassword" type="password" prop="emailPassword"></mu-text-field>
              </mu-form-item>
              <mu-form-item label="SMTP地址" help-text="发件邮箱的SMTP地址" prop="smtpHost" :rules="rules.smtpHost">
                <mu-text-field v-model="profile.smtpHost" prop="profile.smtpHost"></mu-text-field>
              </mu-form-item>
              <mu-form-item label="SMTP端口" help-text="发件邮箱的SMTP端口" prop="smtpPort" :rules="rules.smtpPort">
                <mu-text-field v-model="profile.smtpPort" prop="profile.smtpPort"></mu-text-field>
              </mu-form-item>
              <mu-form-item label="SSL验证" help-text="发送邮箱是否需要开启SSL验证">
                <mu-switch v-model="profile.useSSL"></mu-switch>
              </mu-form-item>
              <mu-form-item label="告警间隔" help-text="告警间隔,两次发送告警的时间间隔,避免告警太频繁轰炸" prop="spaceTime" :rules="rules.spaceTime">
                <mu-text-field v-model.number="profile.spaceTime" prop="spaceTime" type="number"></mu-text-field>
              </mu-form-item>
              <mu-form-item label="短信URL" help-text="短信通道商提供的发送接口URL" prop="profile.sendUrl"  :rules="rules.sendUrl">
                <mu-text-field v-model="profile.sendUrl" prop="profile.sendUrl"></mu-text-field>
              </mu-form-item>
              <mu-form-item prop="短信模板" label="短信模板" :rules="rules.template">
                <mu-text-field multi-line :rows="2" :rows-max="6" v-model="profile.template"></mu-text-field>
              </mu-form-item>
              <mu-form-item prop="执行用户" label="执行用户"  :rules="rules.execUser">
                <mu-text-field multi-line :rows="2" :rows-max="6" v-model="profile.execUser"></mu-text-field>
              </mu-form-item>
              <mu-form-item>
                <mu-button class="btn btn-light" role="button" @click="submit">提交</mu-button>
                <mu-button class="btn btn-light" role="button" @click="profile=profile1">重置</mu-button>
              </mu-form-item>
          </mu-form>
        </div>
      </div>
    </div>
  </section>
</template>
<script type="application/ecmascript">
  export default {
    data() {
      return {
        profile: {},
        profile1:{},
        rules:{
          mail: [
            { validate: (val) => !!val, message: '发送邮箱不能为空'},
            { validate: (val) => val.length >= 3, message: '用户名长度大于3'}
          ],
          password:[
            { validate: (val) => !!val, message: '必须填写密码'}
          ],
          spaceTime:[
             { validate: (val) => !!val>0, message: '告警间隔必须大于0'}
          ]
        }
      }
    },
    mounted() {
      this.getInfo()
    },
    methods: {
      getInfo() {
        this.$http.post('/profile/info.do', {}).then(response => {
          this.profile = response.body
          this.profile1 = this.profile
        }, error => {
          console.log(error)
        })
      },
      submit () {
        this.$refs.form.validate().then((result) => {
          if(result){
              this.$http.post('/profile/save.do',this.profile).then(response => {
                if(response.code == 200) {
                  this.$swal({
                    title: 'Successful',
                    text: 'update prefile successful',
                    type: 'success',
                    buttonsStyling: false,
                    confirmButtonClass: 'btn btn-sm btn-light',
                    background: 'rgba(0, 0, 0, 0.96)',
                    showConfirmButton: false,
                    timer:1000
                  })
                  setTimeout(() => {
                    this.$router.push('/profile/view')
                  }, 1000)
                }
              }, error => {
                console.log(error)
              })
          }
        })
      }

    }
  }
</script>
