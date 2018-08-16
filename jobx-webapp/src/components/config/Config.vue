<template>

  <section class="content">
    <header class="content__title">
      <h1>DATA TABLES</h1>
    </header>

    <div class="card">
      <div class="card-body">
        <table class="table mb-0">

          <tbody>
          <tr>
            <td class="item"><i class="glyphicon glyphicon-envelope"></i>&nbsp;发件邮箱</td>
            <td>notice@ibantang.com&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
              <a class="green" href="http://jobx.17gwx.com/config/edit.htm" title="编辑"><i class="glyphicon glyphicon-pencil"></i></a>
            </td>
          </tr>

          <tr>
            <td class="item"><i class="glyphicon glyphicon-map-marker"></i>&nbsp;SMTP地址</td>
            <td>smtp.exmail.qq.com
            </td>
          </tr>

          <tr>
            <td class="item"><i class="glyphicon glyphicon-filter"></i>&nbsp;SMTP端口</td>
            <td>465 &nbsp;&nbsp;<span class="tips"></span>
            </td>
          </tr>

          <tr>
            <td class="item"><i class="glyphicon glyphicon-lock"></i>&nbsp;邮箱密码</td>
            <td>******</td>
          </tr>
          <tr>
            <td class="item"><i class="glyphicon glyphicon-font"></i>&nbsp;发信URL</td>
            <td> http://your_url</td>
          </tr>
          <tr>
            <td class="item"><i class="glyphicon glyphicon-time"></i>&nbsp;发送间隔</td>
            <td>
              30 分钟<span class="tips">（同一执行器失联后告警邮件和短信发送后到下一次发送的时间间隔）</span>
            </td>
          </tr>
          <tr>
            <td class="item"><i class="glyphicon glyphicon-list-alt"></i>&nbsp;短信模板</td>
            <td>

            </td>
          </tr>
          <tr>
            <td class="item"><i class="fa fa-users"></i>&nbsp;执行用户</td>
            <td>
              root,hadoop,hive,hdfs,algorithm1,algorithm2,algorithm3
            </td>
          </tr>
          <tr>
            <td class="item"><i class="glyphicon glyphicon-trash"></i>&nbsp;清理记录</td>
            <td>

              <div class="row">
                <div class="col-sm-2">
                  <div class="form-group">
                    <input type="text" class="form-control date-picker" placeholder="开始">
                    <i class="form-group__bar"></i>
                  </div>
                </div>
                至
                <div class="col-sm-2">
                  <div class="form-group">
                    <input type="text" class="form-control date-picker" placeholder="结束">
                    <i class="form-group__bar"></i>
                  </div>
                </div>
                <button onclick="clearRecord()" class="btn btn-default btn-sm" style="vertical-align:top;height: 25px;" type="button"><i class="glyphicon glyphicon-trash"></i>&nbsp;清理</button><span class="tips">&nbsp;&nbsp;&nbsp;（<b>*&nbsp;</b>此操作会删除选定时间段内的任务记录，请谨慎执行）</span>
              </div>

            </td>
          </tr>
          </tbody>

        </table>
      </div>
    </div>
  </section>
</template>
<script type="application/ecmascript">

  import 'flatpickr/dist/flatpickr.css'
  import 'flatpickr/dist/flatpickr.js'

  export default {
    methods: {},
    mounted(){
      $('.date-picker').flatpickr({
        enableTime: !1,
        nextArrow: '<i class=\'zmdi zmdi-long-arrow-right\' />',
        prevArrow: '<i class=\'zmdi zmdi-long-arrow-left\' />'
      })
    }
  }
</script>
<style lang="scss" scoped>

  .card-body{
    padding: 0px;
  }
  .form-group {
    .zmdi{
      font-size: 16px;
      line-height: 15px;
    }
    input {
      margin-top: -5px;
    }
  }
  .item{
    width: 120px;
  }


</style>
