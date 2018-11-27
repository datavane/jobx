<template>

<section class="content">
      <header class="content__title">
          <h4 class='card-title'>{{title}}</h4>
          <action url='/job/add' :actions='actions' theme="input"></action>
      </header>
    <div class="card">
      <div class="card-body">
        <mu-form ref="form" :model="job" class="mu-demo-form">
            <mu-form-item label="作业名称" help-text="作业名称必填项,不能为空" prop="jobName"  :rules="rules.jobName">
              <mu-text-field v-model="job.jobName" prop="jobName"></mu-text-field>
            </mu-form-item>
            <mu-form-item label="规则表达式" help-text="请采用quartz框架的时间格式表达式,不能为空" prop="cronExp"  :rules="rules.cronExp">
              <mu-text-field v-model="job.cronExp" prop="cronExp"></mu-text-field>
            </mu-form-item>
            <mu-form-item label="执行器" help-text="要执行此作业的机器" prop="agentId"  :rules="rules.agentId">
              <mu-text-field v-model="job.agentId" prop="agentId"></mu-text-field>
            </mu-form-item>
            <mu-form-item prop="" label="执行命令" :rules="rules.command">
              <mu-text-field multi-line :rows="2" :rows-max="20" v-model="job.command"></mu-text-field>
            </mu-form-item>
            <mu-form-item label="执行身份" help-text="执行该作业的用户身份" prop="execUser"  :rules="rules.execUser">
              <mu-text-field v-model="job.execUser" prop="execUser"></mu-text-field>
            </mu-form-item>
            <mu-form-item label="成功标识" help-text="自定义作业执行成功的返回标识(默认执行成功是0)" prop="successExit"  :rules="rules.successExit">
              <mu-text-field v-model="job.successExit" prop="successExit"></mu-text-field>
            </mu-form-item>
            <mu-form-item>
              <mu-button color="primary" @click="submit">提交</mu-button>
              <mu-button @click="profile=profile1">重置</mu-button>
            </mu-form-item>
        </mu-form>
      </div>
    </div>
  </section>
</template>
<script type="application/ecmascript">
  import action from '@/components/common/Action'
  import select2 from '@/components/common/Select2'
  export default {
    components: {
      action,select2
    },
    data() {
      return {
        title:'ADD NEW JOB',
        job: {},
        profile1:{},
        rules:{
          jobName: [
            { validate: (val) => !!val, message: '作业名称必填项,不能为空'},
            { validate: (val) => val.length >= 3, message: '用户名长度大于3'}
          ],
          cronExp:[
            { validate: (val) => !!val, message: '请采用quartz框架的时间格式表达式(双击控件输入或手动输入)'}
          ],
          agentId:[
             { validate: (val) => !!val, message: '请选择要执行执行器'}
          ],
          command:[
             { validate: (val) => !!val, message: ' 执行命令不能为空,请填写执行命令'}
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
