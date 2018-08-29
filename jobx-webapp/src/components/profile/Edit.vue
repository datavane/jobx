<template>

  <section class="content">
    <div class="content__inner content__inner--sm">

      <div class="card new-contact">
        <div class="new-contact__header">
          <a href="" class="zmdi zmdi-camera new-contact__upload"></a>
          <img src="static/img/profile-pics/profile-pic.jpg" class="new-contact__img" alt="">
        </div>

        <div class="card-body">
          <div class="row">
            <div class="col-md-6">
              <div class="form-group">
                <label>发件邮箱</label>
                <input type="text"
                       class="form-control"
                       placeholder="e.g: notice@jobxhub.com"
                       v-model="profile.senderEmail">
                <i class="form-group__bar"></i>
              </div>
            </div>

            <div class="col-md-6">
              <div class="form-group">
                <label>邮箱密码</label>
                <input type="password"
                       class="form-control"
                       placeholder="******"
                       v-model="profile.emailPassword">
                <i class="form-group__bar"></i>
              </div>
            </div>

            <div class="col-md-6">
              <div class="form-group">
                <label>SMTP地址</label>
                <input type="text"
                       class="form-control"
                       placeholder="e.g: smtp.exmail.qq.com"
                       v-model="profile.smtpHost">
                <i class="form-group__bar"></i>
              </div>
            </div>

            <div class="col-md-6">
              <div class="form-group">
                <label>SMTP端口</label>
                <input type="text"
                       class="form-control"
                       placeholder="e.g: 465"
                       v-model="profile.smtpPort">
                <i class="form-group__bar"></i>
              </div>
            </div>

            <div class="col-md-6">
              <label>SSL验证</label>
              <div class="form-group">
                <div class="toggle-switch toggle-switch--green">
                  <input type="checkbox"
                         class="toggle-switch__checkbox"
                         v-model="profile.isSSL">
                  <i class="toggle-switch__helper"></i>
                </div>
              </div>
            </div>

            <div class="col-md-6">
              <div class="form-group">
                <label>告警间隔</label>
                <input type="text" class="form-control"
                       placeholder="e.g: 30"
                       v-model="profile.spaceTime">
                <i class="form-group__bar"></i>
              </div>
            </div>

          </div>

          <div class="form-group">
            <label>短信URL</label>
            <input type="text" class="form-control"
                   placeholder=""
                   v-model="profile.sendUrl">
            <i class="form-group__bar"></i>
          </div>

          <div class="form-group">
            <label>短信模板</label>
            <textarea class="form-control textarea-autosize"
                      placeholder="e.g: 【%s jobxHub】"
                      v-model="profile.template"></textarea>
            <i class="form-group__bar"></i>
          </div>

          <div class="form-group">
            <label>执行用户</label>
            <textarea class="form-control textarea-autosize"
                      placeholder="e.g: root,hadoop,hdfs"
                      v-model="profile.execUser"></textarea>
            <i class="form-group__bar"></i>
          </div>

          <div class="form-group">
            <label>清理记录</label>
            <div class="row clean-record">
              <div class="col-sm-3">
                <div class="form-group">
                  <input type="text" class="form-control date-picker" placeholder="开始">
                  <i class="form-group__bar"></i>
                </div>
              </div>
              <span class="from-to">至</span>
              <div class="col-sm-3">
                <div class="form-group">
                  <input type="text" class="form-control date-picker" placeholder="结束">
                  <i class="form-group__bar"></i>
                </div>
              </div>
              <a class="btn btn-light clean-btn">清理</a>
              <i class="zmdi zmdi-info zmdi-hc-fw" title="此操作会删除选定时间段内的任务记录，请谨慎执行" data-toggle="tooltip"
                 data-placement="top"></i>
            </div>
          </div>
          <div class="clearfix"></div>
          <div class="mt-5 text-center">
            <a href="" class="btn btn-light">Save new contact</a>
            <a href="" class="btn btn-light"></a>
          </div>
        </div>
      </div>
    </div>
  </section>

</template>
<script type="application/ecmascript">

  import 'flatpickr/dist/flatpickr.min.css'
  import 'flatpickr/dist/flatpickr.min'

  export default {
    methods: {},
    data() {
      return {
        profile: {}
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
  .form-group {
    padding-bottom: 0rem;
    margin-bottom: 1.5rem;
    margin-top: 0px;
    textarea {
      overflow: hidden;
      word-wrap: break-word;
      height: 49px;
    }
    .clean-record {
      margin-top: 10px;
    }
    .clean-btn {
      height: 33.33px;
    }
    .from-to {
      margin-top: 6px;
    }
  }

  .toggle-switch {
    margin-top: 10px;
  }

</style>
