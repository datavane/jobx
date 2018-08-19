<template>

  <section class="content">
    <div class="content__inner content__inner--sm">
      <div class="card">
        <div class="card-body">
          <div class="prefile_header">
            <img src="static/logo.png" class="prefile" alt="JobX">
            <ul class="icon-list contact-item">
              <li>
                <i class="zmdi zmdi-github"></i>
                <i class="zmdi zmdi-whatsapp"></i>
                <i class="zmdi zmdi-facebook"></i>
                <i class="zmdi zmdi-edit" @click="goEdit()"></i>
              </li>
            </ul>
          </div>
          <h4 class="card-body__title mb-4">What's JobX</h4>
          <p>
            &nbsp;&nbsp;&nbsp;&nbsp;一个功能完善真正通用的linux定时任务调度定系统,满足多种场景下各种复杂的定时任务调度,同时集成了linux实时监控,webssh,提供一个方便管理定时任务的平台.</p>
          <br>
          <h4 class="card-body__title mb-4">System Settings</h4>
          <ul class="icon-list">
            <li>
              <i class="zmdi zmdi-email"
                 title="发件邮箱"
                 data-toggle="tooltip"
                 data-placement="top">
              </i>
              {{profile.senderEmail}}
            </li>

            <li>
              <i class="zmdi zmdi-functions"
                 title="SMTP地址"
                 data-toggle="tooltip"
                 data-placement="top">
              </i>
              {{profile.smtpHost}}
            </li>

            <li>
              <i class="zmdi zmdi-parking"
                 title="SMTP端口"
                 data-toggle="tooltip"
                 data-placement="top">
              </i>
              {{profile.smtpPort}}
            </li>

            <li>
              <i class="zmdi zmdi-key"
                 title="是否开启SSL"
                 data-toggle="tooltip"
                 data-placement="top">
              </i>
              <div class="is-ssl form-group toggle-switch toggle-switch--green">
                <input type="checkbox" class="toggle-switch__checkbox" checked disabled>
                <i class="toggle-switch__helper"></i>
              </div>
            </li>

            <li>
              <i class="zmdi zmdi-lock-outline"
                 title="邮箱密码"
                 data-toggle="tooltip"
                 data-placement="top">
              </i>
              ******
            </li>

            <li>
              <i class="zmdi zmdi-http"
                 title="短信地址"
                 data-toggle="tooltip"
                 data-placement="top">
              </i>
              {{profile.sendUrl}}
            </li>

            <li>
              <i class="zmdi zmdi-alarm-check"
                 title="发送间隔"
                 data-toggle="tooltip"
                 data-placement="top">
              </i>
              {{profile.spaceTime}} 分钟
              <span class="tip">（同一执行器失联后告警邮件和短信发送后到下一次发送的时间间隔）</span>
            </li>

            <li>
              <i class="zmdi zmdi-format-align-justify"
                 title="短信模板"
                 data-toggle="tooltip"
                 data-placement="top">
              </i>
              {{profile.template}}
            </li>

            <li>
              <i class="zmdi zmdi-accounts-outline"
                 title="执行用户"
                 data-toggle="tooltip"
                 data-placement="top">
              </i>
              <button type="button" class="execUser btn btn-light btn-sm" v-for="execUser in profile.execUser">{{execUser}}</button>
            </li>

          </ul>
        </div>
      </div>
    </div>
  </section>

</template>
<script type="application/ecmascript">
  import 'flatpickr/dist/flatpickr.min.css'
  import 'flatpickr/dist/flatpickr.min'
  export default {
    data(){
      return {
        profile: {}
      }
    },
    methods: {
      goEdit(){
        this.$router.push({path: '/profile/edit'})
      }
    },
    mounted() {
      this.$http.post('/profile/info.do', {}).then(response => {
        this.profile = response.body
        this.profile.execUser =  this.profile.execUser.split(",")
      }, error => {
        console.log(error)
      })

      $('.date-picker').flatpickr({
        enableTime: !1,
        nextArrow: '<i class=\'zmdi zmdi-long-arrow-right\' />',
        prevArrow: '<i class=\'zmdi zmdi-long-arrow-left\' />'
      })
    }
  }
</script>
<style lang="scss" scoped>
  .prefile_header {
    text-align: center;
    padding: 10px 0;
    border-radius: 2px 2px 0 0;
  }

  .card-body {
    padding-top: 1rem;
  }

  .prefile {
    height: 100px;
  }

  .contact-item {
    .zmdi {
      padding: 5px;
      margin: 10px;
      font-size: 24px;
    }

  }

  .icon-list {
    li {
      margin-top: 6px;
    }

    .execUser {
      margin-right: 5px;
      padding: .20rem .45rem;
    }
  }

  .tip{
    color:rgba(225,225,225,.6)
  }

  .is-ssl {
    margin-bottom: 1rem;
    height:0;
  }

</style>
