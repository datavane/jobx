<template>
  <div class="app-container">

    <div class="steps-form">

      <el-form :model="form.job" ref="jobForm" :rules="jobFormRule" label-width="120px" >

        <el-form-item :label="$t('job.jobName')" prop="jobName">
          <el-input :placeholder="$t('job.jobName')" v-model="form.job.jobName" clearable class="input-item" />
        </el-form-item>

        <el-form-item :label="$t('job.jobType')" prop="jobType">
          <el-select v-model="form.job.jobType" :placeholder="$t('job.jobType')" clearable class="input-item">
            <el-option v-for="item in control.jobType" :key="item" :label="item.name" :value="item.id"/>
          </el-select>
        </el-form-item>

        <el-form-item :label="$t('agent.agentName')" v-show="form.job.jobType == 0" prop="agentId">
          <el-select v-model="form.job.agentId" clearable filterable class="input-item"  :placeholder="$t('agent.agentName')" >
            <el-option
              v-for="item in control.agents"
              :key="item.value"
              :label="item.agentName"
              :value="item.agentId">
              <span style="float: left">{{ item.agentName }}</span>
              <span style="float: right;margin-right: 25px; color: #8492a6; font-size: 13px">{{ item.host }}</span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-dialog class="cronExp" :visible.sync="control.showCron" width="560px">
          <cron v-model="form.job.cronExp" url="/verify/recent"></cron>
        </el-dialog>

        <el-form-item :label="$t('job.cronExp')" prop="cronExp">
          <el-input :placeholder="$t('job.cronExp')" v-model="form.job.cronExp" class="input-item" @focus="control.showCron=!control.showCron"/>
        </el-form-item>

        <el-form-item :label="$t('job.execUser')" v-show="form.job.jobType == 0" prop="execUser">
          <el-select v-model="form.job.execUser" clearable filterable class="input-item" :placeholder="$t('job.execUser')">
            <el-option
              v-for="item in control.execUsers"
              :key="item"
              :label="item"
              :value="item">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item :label="$t('job.command')" v-show="form.job.jobType == 0" prop="command">
          <div class="command-input">
            <textarea ref="command" placeholder="请输入内容" v-model="form.job.command"/>
          </div>
        </el-form-item>

        <el-form-item :label="$t('agent.upload')" v-show="form.job.jobType == 0">
          <el-upload drag action="https://jsonplaceholder.typicode.com/posts/">
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">{{$t('agent.uploadText')}}<em>{{$t('agent.clickUpload')}}</em></div>
            <div class="el-upload__tip" slot="tip">{{$t('agent.uploadTip')}}</div>
          </el-upload>
        </el-form-item>

        <!--工作流-->
        <el-form-item :label="$t('job.dependency')" v-if="form.job.jobType == 1">
          <el-table class="input-item dependency" border stripe highlight-current-row :data="form.workFlow.count">
            <el-table-column :label="$t('job.job')" align="center">
              <template slot-scope="scope">
                <el-select v-model="form.workFlow.detail[handleFindDependency(scope.row.id)].jobId" placeholder="请选择">
                  <el-option-group
                    v-for="(group,index) in control.jobs"
                    :key="group.label"
                    :label="group.label">
                    <el-option
                      v-for="item in group.options"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id">
                      <span style="float: left; color: #8492a6; font-size: 13px">
                        <font-awesome-icon icon="list" size="xs" v-if="index == 0"/>
                        <font-awesome-icon icon="sitemap" size="xs" v-if="index == 1"/>
                      </span>
                      <span style="float: left;margin-left:5px">{{ item.name }}</span>
                    </el-option>
                  </el-option-group>
                </el-select>
              </template>
            </el-table-column>

            <el-table-column :label="$t('job.parentDependency')" align="center">
              <template slot-scope="scope">
                <el-select v-model="form.workFlow.detail[handleFindDependency(scope.row.id)].dependency" :placeholder="$t('job.parentDependency')" clearable>
                  <el-option-group
                    v-for="(group,index) in control.jobs"
                    :key="group.label"
                    :label="group.label">
                    <el-option
                      v-for="item in group.options"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id">
                      <span style="float: left; color: #8492a6; font-size: 13px">
                        <font-awesome-icon icon="list" size="xs" v-if="index == 0"/>
                        <font-awesome-icon icon="sitemap" size="xs" v-if="index == 1"/>
                      </span>
                      <span style="float: left;margin-left:5px">{{ item.name }}</span>
                    </el-option>
                  </el-option-group>
                </el-select>
              </template>
            </el-table-column>

            <el-table-column :label="$t('job.trigger.name')" align="center">
              <template slot-scope="scope">
                <el-select v-model="form.workFlow.detail[handleFindDependency(scope.row.id)].trigger" :placeholder="$t('job.trigger.name')" clearable>
                  <el-option v-for="item in control.triggerType" :key="item" :label="item.name" :value="item.id"/>
                </el-select>
                <i class="el-icon-delete" type="danger" style="margin-left: 10px" v-if="form.workFlow.count.length>1" @click="handleDeleteDependency(scope.row.id)"></i>
              </template>
            </el-table-column>

          </el-table>
          <div style="margin-top: 20px">
            <el-button size="mini" type="primary" @click="handleAddJob()">新增作业</el-button>
            <el-button size="mini" type="success" @click="handleAddDependency">增加依赖</el-button>
          </div>
        </el-form-item>

        <el-form-item :label="$t('job.successExit')"  v-if="form.job.jobType == 0" prop="successExit">
          <el-input :placeholder="$t('job.successExit')" v-model.number="form.job.successExit" clearable class="input-item"/>
        </el-form-item>

        <el-form-item :label="$t('job.alarm')">
          <el-switch
            v-model="form.job.alarm"
            active-color="#13ce66"
            inactive-color="#ff4949"
            active-value="1"
            inactive-value="0">
          </el-switch>
        </el-form-item>

        <!--告警方式-->
        <div v-show="form.job.alarm==1">
          <el-form-item :label="$t('job.alarmType')" prop="alarmType">
            <el-select v-model="form.job.alarmType" :placeholder="$t('job.alarmType')" clearable multiple class="input-item">
              <el-option v-for="item in control.alarmType" :key="item" :label="item.name" :value="item.id"/>
            </el-select>
          </el-form-item>

          <el-form-item :label="$t('job.dingTask')" v-show="form.job.alarmType.indexOf(1)>-1" prop="alarmDingDing">
            <el-input :placeholder="$t('job.dingTask')" v-model="form.job.dingTask" clearable class="input-item"/>
          </el-form-item>

          <el-form-item :label="$t('job.atUser')" v-show="form.job.alarmType.indexOf(1)>-1" prop="dingTaskAtUser">
            <el-input :placeholder="$t('job.atUser')" v-model="form.job.dingTaskAtUser" clearable class="input-item"/>
          </el-form-item>

          <el-form-item :label="$t('job.email')" v-show="form.job.alarmType.indexOf(2)>-1" prop="alarmEmail">
            <el-input :placeholder="$t('job.email')" v-model="form.job.email" clearable class="input-item"/>
          </el-form-item>

          <el-form-item :label="$t('job.sms')" v-show="form.job.alarmType.indexOf(3)>-1" prop="alarmSms">
            <el-input :placeholder="$t('job.sms')" v-model="form.job.sms" clearable class="input-item"/>
          </el-form-item>
          <el-form-item :label="$t('job.smsTemplate')" v-show="form.job.alarmType.indexOf(3)>-1" prop="alarmSmsTemplate">
            <el-input :placeholder="$t('job.smsTemplate')" v-model="form.job.smsTemplate" clearable class="input-item"/>
          </el-form-item>
        </div>

        <el-form-item :label="$t('job.runCount')">
          <el-input-number v-model="form.job.runCount" controls-position="right" :min="0" :max="10"></el-input-number>
        </el-form-item>

        <el-form-item :label="$t('job.timeout')">
          <el-input-number v-model="form.job.timeout" controls-position="right" :min="0" :max="10"></el-input-number>
        </el-form-item>

        <el-form-item :label="$t('job.comment')">
          <el-input
            type="textarea"
            :rows="4"
            class="input-item"
            :placeholder="$t('job.comment')"
            v-model="form.job.comment">
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="onSubmit('jobForm')">{{$t('action.create')}}</el-button>
          <el-button @click="onCancel">{{$t('action.cancel')}}</el-button>
        </el-form-item>

      </el-form>

      <!--工作流添加弹窗-->
      <el-dialog :visible.sync="control.showJob" width="700px" class="dependencyForm">
        <el-form ref="job" label-width="100px">

          <el-form-item :label="$t('job.jobName')">
            <el-input :placeholder="$t('job.jobName')" v-model="form.dependency.jobName" clearable class="input-item"/>
          </el-form-item>

          <el-form-item :label="$t('agent.agentName')">
            <el-select v-model="form.dependency.agentId" clearable filterable class="input-item" :placeholder="$t('agent.agentName')">
              <el-option
                v-for="item in control.agents"
                :key="item.value"
                :label="item.agentName"
                :value="item.agentId">
                <span style="float: left">{{ item.agentName }}</span>
                <span  style="float: right;margin-right: 25px; color: #8492a6; font-size: 13px">{{ item.host }}</span>
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item :label="$t('job.execUser')">
            <el-select v-model="form.dependency.execUser" clearable filterable class="input-item" :placeholder="$t('job.execUser')">
              <el-option
                v-for="item in control.execUsers"
                :key="item"
                :label="item"
                :value="item">
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item :label="$t('job.command')">
            <div class="command-input1">
              <textarea ref="command1" placeholder="请输入内容" v-model="form.dependency.command"/>
            </div>
          </el-form-item>

          <el-form-item :label="$t('job.successExit')">
            <el-input :placeholder="$t('job.successExit')" v-model.number="form.dependency.successExit" clearable class="input-item"/>
          </el-form-item>

        </el-form>

        <div slot="footer" class="dialog-footer">
          <el-button @click="control.showJob = false">取 消</el-button>
          <el-button type="primary" @click="submitDependency()">确 定</el-button>
        </div>

      </el-dialog>

    </div>

  </div>

</template>

<script>
  import cron from '@/components/Cron'
  import {allAgent} from '@/api/agent'
  import {execUser} from '@/api/user'
  import {addJob,getJob,addDependency,getDependency} from '@/api/job'
  import {validateDingDing,validatePhone,validateEmail,validateURL} from '@/utils/validate'
  import CodeMirror from 'codemirror'
  import 'codemirror/addon/lint/lint.css'
  import 'codemirror/lib/codemirror.css'
  import 'codemirror/theme/darcula.css'
  import 'codemirror/mode/django/django'
  import 'codemirror/mode/php/php'
  import 'codemirror/mode/shell/shell'
  import 'codemirror/mode/cmake/cmake'
  import 'codemirror/mode/python/python'
  import 'codemirror/mode/go/go'
  import 'codemirror/mode/groovy/groovy'
  import 'codemirror/mode/erlang/erlang'
  import 'codemirror/addon/lint/json-lint'
  import 'codemirror/addon/lint/javascript-lint'

  export default {
    components: {
      cron
    },
    data() {

      return {
        control:{//控制页面显示,提供表单数据等...
          agents: [],//已有的执行器
          jobs:[
            {
              params:{ createType:1 },
              label:this.$t('job.simpleJob'),
              options:[]
            },
            {
              params:{ createType:2 },
              label:this.$t('job.dependencyItem'),
              options:[]
            }
          ],//已有的作业
          execUsers: [],//执行身份
          alarmType: [//告警类型
            {id: 1, name: this.$t('job.dingTask')},
            {id: 2, name: this.$t('job.email')},
            {id: 3, name: this.$t('job.sms')}
          ],
          triggerType: [//任务触发方式
            {id: 3, name: this.$t('job.trigger.success')},
            {id: 2, name: this.$t('job.trigger.done')},
            {id: 1, name: this.$t('job.trigger.parallel')},
            {id: 4, name: this.$t('job.trigger.fail')}
          ],
          jobType: [//作业类型
            {id: 0, name: this.$t('job.simpleJob')},
            {id: 1, name: this.$t('job.workFlow')}
          ],
          showCron: false,//是否显示cron控件
          showJob:false,//是否显示添加作业弹窗,
          command:null,
          command1:null
        },
        form: {//绑定form表单的数据
          job:{
            jobType: 0,
            alarmType: [],
            runCount: 0,
            timeout: 0,
            dingTask:null
          },
          workFlow: {
            count:[{}],
            detail:[{}]
          },
          dependency:{
            jobName:null,
            agentId:null,
            execUser:null,
            command:null,
            successExit:null
          }
        },
        jobFormRule:{
          jobName:[
            {required: true,message: '请输入AppName',trigger: 'change'},
            {min: 3,max: 20,message: '长度在 3 到 20 个字符'}
          ],
          jobType:[{required: true,message: '请选择作业类型',trigger: 'change'}],
          cronExp:[{required: true,message: '请输入表达式',trigger: 'change'}],
          agentId:[{trigger:'change',validator:(r, v, c)=>this.checkNull(r, v, c,this.$t('agent.agentName'))}],
          execUser:[{trigger:'change',validator:(r, v, c)=>this.checkNull(r, v, c,this.$t('job.execUser'))}],
          command:[{trigger:'change',validator:(r, v, c)=>this.checkNull(r, this.form.job.command, c,this.$t('job.command'))}],
          successExit:[
            {trigger:'change',required: true, message: this.$t('job.successExit').concat('不能为空')},
            { type:'number',message:this.$t('job.successExit').concat('必须为数字值')}
          ],
          alarmType:[{trigger:'change',validator:this.checkAlarm}],
          alarmDingDing:[{trigger:'change',validator:this.checkDingDing}],
          dingTaskAtUser:[{trigger:'change',validator:this.checkDingTaskAtUser}],
          alarmEmail:[{trigger:'change',validator:this.checkAlarmEmail}],
          alarmSms:[{trigger:'change',validator:this.checkAlarmSms}],
          alarmSmsTemplate:[{trigger:'change',validator:(r, v, c)=>this.checkNull(r, v, c,this.$t('job.smsTemplate'))}],
        }
      }
    },


    created() {
      this.getAgent()
      this.getJob()
      this.getExecUser()
      this.handleInitWorkFlow()
    },

    mounted() {
      this.control.command = this.handleCodeMirror(this.$refs.command)
      this.control.command.on('change', cm => {
        this.form.job.command = cm.getValue()
        this.$refs.jobForm.validateField('command')
      })
    },

    methods: {

      checkNull(rule, value, callback,field) {
        if (this.form.job.jobType === 0) {
          if (!value) {
            callback(new Error('请输入'.concat(field)))
          }else {
            callback()
          }
        }else {
          callback()
        }
      },

      checkAlarm(rule, value, callback) {
        if (this.form.job.alarm == 1) {
          if (!value||value.length == 0) {
            callback(new Error('请至少选择一种报警通知方式'))
          }else {
            callback()
          }
        }else {
          callback()
        }
      },

      checkDingDing(rule, value, callback) {
        if ( this.form.job.alarm == 1 && this.form.job.alarmType.indexOf(1)>-1 ) {
          if (!value) {
            callback(new Error('请输入钉钉机器人URL'))
          }else if (!validateDingDing(value)) {
            callback(new Error('钉钉机器人URL错误,请参考钉钉官网规范'))
          }else {
            callback()
          }
        }else {
          callback()
        }
      },

      checkDingTaskAtUser(rule, value, callback) {
        if ( this.form.job.alarm == 1 && this.form.job.alarmType.indexOf(1)>-1 ) {
          if (value && !validatePhone(value)) {
            callback(new Error('钉钉@通知人,格式有误,请参考钉钉官网规范'));
          }else {
            callback()
          }
        }else {
          callback()
        }
      },

      checkAlarmEmail(rule, value, callback) {
        if ( this.form.job.alarm == 1 && this.form.job.alarmType.indexOf(2)>-1 ) {
          if ( !value ) {
            callback(new Error('请输入正确的邮箱地址'))
          } else if (!validateEmail(value)) {
            callback(new Error('邮箱格式错误'))
          } else {
            callback()
          }
        }else {
          callback()
        }
      },

      checkAlarmSms(rule, value, callback){
        if ( this.form.job.alarm == 1 && this.form.job.alarmType.indexOf(2)>-1 ) {
          if (!value) {
            callback(new Error('请输入正确短信通道商http请求URL'))
          }else if(!validateURL(value)){
            callback(new Error('短信通道商http请求URL错误'))
          }else{
            callback()
          }
        }else {
          callback()
        }
      },

      onSubmit(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            alert('submit!')
          } else {
            console.log('error submit!!')
            return false;
          }
        });
      },

      onReset(formName) {
        this.$refs[formName].resetFields()
      },

      getAgent() {
        allAgent().then(response => {
          this.control.agents = response.body
        })
      },

      getJob() {

        getJob(this.control.jobs[0].params).then(response=> {
          this.control.jobs[0].options = []
          let job = response.body
          job.forEach(x=>{
            this.control.jobs[0].options.push({
              id:x.jobId,
              name:x.jobName
            })
          })

          getJob(this.control.jobs[1].params).then(response=> {
            this.control.jobs[1].options = []
            let job = response.body
            job.forEach(x=>{
              this.control.jobs[1].options.push({
                id:x.jobId,
                name:x.jobName
              })
            })
          })

        })
      },

      getExecUser() {
        execUser().then(response => {
          this.control.execUsers = response.body
        })
      },

      submitDependency() {
        addDependency(this.form.dependency).then(response=>{
          this.control.showJob = false
        })
      },

      handleCodeMirror(el){
        return CodeMirror.fromTextArea(el, {
          lineNumbers: true,
          lint: true,
          autoMatchParens: true,
          mode: 'shell',
          lineNumbers: true,	//显示行号
          theme: "darcula",	//设置主题
          lineWrapping: true,	//代码折叠
          foldGutter: true,
          gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter", 'CodeMirror-lint-markers'],
          matchBrackets: true,
        })
      },

      handleAddJob() {
        this.control.showJob = true
        this.$nextTick(()=>{
          if(!this.control.command1){
            this.control.command1 = this.handleCodeMirror(this.$refs.command1)
            this.control.command1.on('change', cm => {
              this.form.dependency.command = cm.getValue()

            })
          }
        })
      },

      handleAddDependency() {
        let id = new Date().getMilliseconds()
        this.form.workFlow.count.push({"id": id})
        this.form.workFlow.detail.push({
          id: id,
          job: null,
          dependency: null,
          trigger: null
        })
      },

      handleFindDependency(id) {
        for (let index = 0; index < this.form.workFlow.detail.length; index++) {
          let detail = this.form.workFlow.detail[index]
          if (detail.id === id) {
            return index
          }
        }
      },

      handleDeleteDependency(id) {
        this.form.workFlow.count.forEach((item, index) => {
          if (item.id == id) {
            this.form.workFlow.count.splice(index, 1)
            this.form.workFlow.detail.splice(index, 1)
          }
        })
      },

      handleInitWorkFlow() {
        let id = new Date().getTime()
        this.form.workFlow.count[0].id = id;
        this.form.workFlow.detail[0].id = id;
      }

    },

    watch: {
      'form.job.alarm': function (value) {
        if (value === 0) {
          this.form.alarmType = []
        }
        this.$refs.jobForm.clearValidate('alarmType')
      },

      'form.job.alarmType': function (value) {
        this.$refs.jobForm.clearValidate('alarmDingDing')
        this.$refs.jobForm.clearValidate('dingTaskAtUser')
        this.$refs.jobForm.clearValidate('alarmEmail')
        this.$refs.jobForm.clearValidate('alarmSms')
        this.$refs.jobForm.clearValidate('alarmSmsTemplate')
      },

      'form.job.command': function (value) {
        let codeValue = this.control.command.getValue()
        if (value !== codeValue) {
          this.control.command.setValue(value)

        }
      },
      'form.dependency.command': function (value) {
        let codeValue = this.control.command1.getValue()
        if (value !== codeValue) {
          this.control.command1.setValue(value)
        }
      },

      'form.job.jobType': function (value) {
        if (value === 1) {
        }
      }

    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .steps-btn {
    margin-left: 200px;
  }

  .steps-form {
    padding: 20px;
  }

  .input-item {
    width: 70%;
  }

  .command-input {
    width: 70%;
    font-size: 12px;
    position: relative;
  }

  .command-input >> .CodeMirror {
    height: auto;
    min-height: 300px;
  }

  .command-input >>> .CodeMirror-scroll {
    min-height: 300px;
  }

  .command-input >>> .cm-s-rubyblue span.cm-string {
    color: #F08047;
  }

  .dependency {
    .el-table .cell, .el-table th div {
      padding-left:0px;
      padding-right: 0px;
    }
    .el-input__inner{
      height:30px;
    }
    .el-table__header-wrapper {
      line-height:20px;
    }
  }

  .dependencyForm {
    .input-item {
      width: 85%;
    }
    .command-input1 {
      width: 85%;
      font-size: 12px;
      position: relative;
      .CodeMirror{
        height: 150px;
      }
      .CodeMirror-scroll{
        height: 150px;
      }
    }
  }

</style>
