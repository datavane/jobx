<template>

  <section class="content">
    <div class="content__inner content__inner--sm">

      <div class="card new-contact">
        <div class="new-contact__header">
          <a href="" class="zmdi zmdi-camera new-contact__upload"></a>
          <img src="/static/img/profile-pics/profile-pic.jpg" class="new-contact__img" alt="">
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
              <div class="tip"></div>
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
              <div class="tip"></div>
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
              <div class="tip"></div>
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
              <div class="tip"></div>
            </div>

            <div class="col-md-6">
              <label>SSL验证</label>
              <div class="form-group">
                <div class="toggle-switch toggle-switch--green">
                  <input type="checkbox"
                         class="toggle-switch__checkbox"
                         v-model="profile.useSSL">
                  <i class="toggle-switch__helper"></i>
                </div>
              </div>
              <div class="tip"></div>
            </div>

            <div class="col-md-6">
              <div class="form-group">
                <label>告警间隔</label>
                <input type="text" class="form-control"
                       placeholder="e.g: 30"
                       v-model="profile.spaceTime">
                <i class="form-group__bar"></i>
              </div>
              <div class="tip"></div>
            </div>

          </div>

          <div class="form-group">
            <label>短信URL</label>
            <input type="text" class="form-control"
                   placeholder=""
                   v-model="profile.sendUrl">
            <i class="form-group__bar"></i>
            <div class="tip"></div>
          </div>

          <div class="form-group">
            <label>短信模板</label>
            <textarea class="form-control textarea-autosize"
                      placeholder="e.g: 【%s jobxHub】"
                      v-model="profile.template"></textarea>
            <i class="form-group__bar"></i>
            <div class="tip"></div>
          </div>

          <div class="form-group">
            <label>执行用户</label>
            <textarea class="form-control textarea-autosize"
                      placeholder="e.g: root,hadoop,hdfs"
                      v-model="profile.execUser"></textarea>
            <i class="form-group__bar"></i>
            <div class="tip"></div>
          </div>

          <div class="clearfix"></div>
          <div class="mt-5 text-center">
            <button class="btn btn-light" @click="save()">Save</button>
            <button class="btn btn-light">Cancal</button>
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
    data() {
      return {
        profile: {}
      }
    },
    mounted() {
      this.getInfo()
      $('.date-picker').flatpickr({
        enableTime: !1,
        nextArrow: '<i class=\'zmdi zmdi-long-arrow-right\' />',
        prevArrow: '<i class=\'zmdi zmdi-long-arrow-left\' />'
      })
    },
    methods: {
      getInfo() {
        this.$http.post('/profile/info.do', {}).then(response => {
          this.profile = response.body
        }, error => {
          console.log(error)
        })
      },
      save() {
        this.$http.post('/profile/save.do',this.profile).then(response => {
          if(response.code == 200) {
            this.$swal({
              title: 'Successful',
              text: 'update prefile successful',
              type: 'success',
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
    },
  }
</script>
<style lang="scss" scoped>
  .form-group {
    padding-bottom: 0rem;
    margin-bottom: 0.5rem;
    margin-top: 1.5rem;
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

   .tip {
      height: 20px;
      line-height: 20px;
    }

  .toggle-switch {
    margin-top: 10px;
  }

</style>
