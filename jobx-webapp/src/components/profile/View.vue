<template>

  <section class="content">
    <div class="content__inner content__inner--sm">
      <div class="card">
        <div class="card-body">
          <div class="prefile_header">
            <img src="/static/logo.png" class="prefile" alt="JobX">
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

            <li class="li-ssl">
              <i class="zmdi zmdi-key"
                 title="是否开启SSL"
                 data-toggle="tooltip"
                 data-placement="top">
              </i>
              <div class="is-ssl form-group toggle-switch toggle-switch--green">
                <input type="checkbox" class="toggle-switch__checkbox" :checked=profile.useSsl disabled>
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

            <li>
              <label>清理记录</label>
              <div class="row clean-record">
                <div class="col-sm-6">
                  <div class="form-group">
                    <flatPickr class="form-control" v-model="cleanDate" :config="config"></flatPickr>
                    <i class="form-group__bar"></i>
                  </div>
                </div>
                <button class="btn btn-light btn-clean" @click="clean()">清理</button>
              </div>
            </li>

          </ul>
        </div>
      </div>
    </div>
  </section>

</template>
<script type="application/ecmascript">
  import flatPickr from 'vue-flatpickr-component';
  export default {
    data(){
      return {
        profile: {},
        cleanDate:null,
        config:{
            mode: "range",
            nextArrow: '<i class="zmdi zmdi-long-arrow-right" />',
            prevArrow: '<i class="zmdi zmdi-long-arrow-left" />'
        }
      }
    },
    components: {
      flatPickr
    },
    mounted() {
      this.$http.post('/profile/info', {}).then(response => {
        this.profile = response.body
        this.profile.execUser =  this.profile.execUser.split(",")
      }, error => {
        console.log(error)
      })

    },
    methods: {
      goEdit(){
        this.$router.push('/profile/edit')
      },
      clean() {
        if (this.cleanDate) {
          let dateArr = this.cleanDate.split("to")
          if (dateArr.length == 1) {
            alert("")
            return
          }
          let start = dateArr[0];
          let end = dateArr[1];
          this.$http.post('/profile/clean', {
            start:start,
            end:end
          }).then(response => {
            if(response.code === 200) {
              this.$swal({
                title: 'Successful',
                text: 'clean record successful',
                type: 'success',
                background: 'rgba(0, 0, 0, 0.96)',
                showConfirmButton: false,
                timer:1500
              })
            }
          }, error => {
            console.log(error)
          })

        }
      }
    }
  }
</script>
<!--覆盖组件里的默认样式-->
<style>
  .flatpickr-day {
    width: 40px;
    max-width: 40px;
    height: 40px;
    line-height: 40px;
  }
  span.flatpickr-day {
    border-radius: 50% !important;
  }
  .flatpickr-day.startRange,.flatpickr-day.startRange:hover,
  .flatpickr-day.endRange,.flatpickr-day.endRange:hover {
    background: #569ff7 !important;
    -webkit-box-shadow: none;
    box-shadow: none;
    color:#fff;
    border-color: #569ff7 !important;
  }
  .flatpickr-day.inRange {
    box-shadow:none;
    color:black;
  }
  span.flatpickr-weekday {
    margin: 11px;
    display:inline-flex
  }

</style>

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
      margin-top: 15px;
    }
    .li-ssl{
      margin-top: 5px;
    }
    .execUser {
      margin-right: 5px;
      padding: .20rem .45rem;
    }
    .from-to{
      margin-top: 15px
    }
  }
  .tip{
    color:rgba(225,225,225,.6)
  }
  .is-ssl {
    margin-bottom: 1rem;
    height:0;
  }
  .contact-item {
    .zmdi {
      cursor: pointer;
    }
  }
  .btn-clean{
    height: 33px;
    margin-top:5px;
  }
</style>


