<template>
  <div class="app-container">

    <div class="steps-form">

      <el-steps :active="control.step" finish-status="success" align-center style="width:80%;margin-bottom: 50px">
        <el-step title="基础信息"></el-step>
        <el-step title="调度信息"></el-step>
        <el-step title="告警信息"></el-step>
        <el-step title="作业预览"></el-step>
      </el-steps>

      <el-form :model="form.job" ref="jobForm" :rules="jobFormRule" label-width="10%" >

          <div v-show="control.step == 0">
            <el-form-item :label="$t('job.jobName')" prop="jobName">
              <el-input :placeholder="$t('job.jobName')" v-model="form.job.jobName" clearable class="input-item" />
            </el-form-item>

            <el-form-item :label="$t('job.jobType')">
              <el-select v-model="form.job.jobType" :placeholder="$t('job.jobType')" clearable class="input-item">
                <el-option v-for="item in control.jobType" :key="item.id" :label="item.name" :value="item.id"/>
              </el-select>
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
          </div>

          <div v-show="control.step == 1">

            <el-form-item :label="$t('agent.agentName')" v-show="form.job.jobType == 1" prop="agentId">
              <el-select v-model="form.job.agentId" clearable filterable class="input-item"  :placeholder="$t('agent.agentName')" >
                <el-option
                  v-for="item in control.agents"
                  :key="item.agentId"
                  :label="item.agentName"
                  :value="item.agentId">
                  <span style="float: left">{{ item.agentName }}</span>
                  <span class="select-item-right">{{ item.host }}</span>
                </el-option>
              </el-select>
            </el-form-item>

            <el-dialog class="cronExp" :visible.sync="control.showCron" width="560px">
              <cron v-model="form.job.cronExp" url="/verify/recent"></cron>
            </el-dialog>

            <el-form-item :label="$t('job.cronExp')" prop="cronExp">
              <el-input :placeholder="$t('job.cronExp')" v-model="form.job.cronExp" class="input-item" @focus="control.showCron=!control.showCron"/>
            </el-form-item>

            <el-form-item :label="$t('job.command')" v-show="form.job.jobType == 1" prop="command">
              <div class="command-input">
                <textarea ref="command" placeholder="请输入内容" v-model="form.job.command"/>
              </div>
            </el-form-item>

            <el-form-item :label="$t('job.execUser')" v-show="form.job.jobType == 1" prop="execUser">
              <el-select v-model="form.job.execUser" clearable filterable class="input-item" :placeholder="$t('job.execUser')">
                <el-option
                  v-for="item in control.execUsers"
                  :key="item"
                  :label="item"
                  :value="item">
                </el-option>
              </el-select>
            </el-form-item>

            <el-form-item :label="$t('job.successExit')"  v-if="form.job.jobType == 1" prop="successExit">
              <el-input :placeholder="$t('job.successExit')" v-model.number="form.job.successExit" clearable class="input-item"/>
            </el-form-item>

           <!-- <el-form-item :label="$t('agent.upload')" v-show="form.job.jobType == 1">
              <el-upload drag action="https://jsonplaceholder.typicode.com/posts/">
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">{{$t('agent.uploadText')}}<em>{{$t('agent.clickUpload')}}</em></div>
                <div class="el-upload__tip" slot="tip">{{$t('agent.uploadTip')}}</div>
              </el-upload>
            </el-form-item>
            -->

            <!--工作流-->
            <el-form-item :label="$t('job.dependency')" v-if="form.job.jobType == 2">
              <el-table class="input-item dependency" border stripe highlight-current-row :data="form.workFlow.count">
                <el-table-column :label="$t('job.job')" align="center">
                  <template slot-scope="scope">
                    <el-select v-model="form.workFlow.detail[handleFindNode(scope.row.id)].jobId" placeholder="请选择">
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
                    <el-select v-model="form.workFlow.detail[handleFindNode(scope.row.id)].dependency" :placeholder="$t('job.parentDependency')" clearable>
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
                    <el-select v-model="form.workFlow.detail[handleFindNode(scope.row.id)].trigger" :placeholder="$t('job.trigger.name')" clearable>
                      <el-option v-for="item in control.triggerType" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                    <i class="el-icon-delete" type="danger" style="margin-left: 10px" v-if="form.workFlow.count.length>1" @click="handleDeleteNode(scope.row.id)"></i>
                  </template>
                </el-table-column>

              </el-table>

              <div style="margin-top: 20px">
                <el-button size="mini" type="primary" @click="handleAddJob()">新增依赖节点</el-button>
                <el-button size="mini" type="success" @click="handleAddNode">增加依赖作业</el-button>
              </div>
            </el-form-item>

          </div>

          <div v-show="control.step == 2">

            <el-form-item :label="$t('job.runCount')" prop="runCount">
              <el-input v-model="form.job.runCount" controls-position="right" clearable class="input-item"></el-input>
            </el-form-item>

            <el-form-item :label="$t('job.timeout')" prop="timeout">
              <el-input v-model="form.job.timeout" controls-position="right" clearable class="input-item"></el-input>
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
            <div v-if="form.job.alarm==1">
              <el-form-item :label="$t('job.alarmType')" prop="alarmType">
                <el-select v-model="form.job.alarmType" :placeholder="$t('job.alarmType')" clearable multiple class="input-item">
                  <el-option v-for="item in control.alarmType" :key="item.id" :label="item.name" :value="item.id"/>
                </el-select>
              </el-form-item>

              <el-form-item :label="$t('job.dingTask')" v-if="form.job.alarmType.indexOf(1)>-1" prop="alarmDingURL">
                <el-input :placeholder="$t('job.dingTask')" v-model="form.job.alarmDingURL" clearable class="input-item"/>
              </el-form-item>

              <el-form-item :label="$t('job.atUser')" v-if="form.job.alarmType.indexOf(1)>-1" prop="alarmDingAtUser">
                <el-input :placeholder="$t('job.atUser')" v-model="form.job.alarmDingAtUser" clearable class="input-item"/>
              </el-form-item>

              <el-form-item :label="$t('job.email')" v-if="form.job.alarmType.indexOf(2)>-1" prop="alarmEmail">
                <el-input :placeholder="$t('job.email')" v-model="form.job.alarmEmail" clearable class="input-item"/>
              </el-form-item>

              <el-form-item :label="$t('job.sms')" v-if="form.job.alarmType.indexOf(3)>-1" prop="alarmSms">
                <el-input :placeholder="$t('job.sms')" v-model="form.job.alarmSms" clearable class="input-item"/>
              </el-form-item>
              <el-form-item :label="$t('job.smsTemplate')" v-if="form.job.alarmType.indexOf(3)>-1" prop="alarmSmsTemplate">
                <el-input :placeholder="$t('job.smsTemplate')" v-model="form.job.alarmSmsTemplate" clearable class="input-item"/>
              </el-form-item>
            </div>
          </div>

          <div v-show="control.step == 3">
            <el-form-item>
              <el-button type="primary" @click="handleSubmitJob('jobForm')">{{$t('action.create')}}</el-button>
              <el-button @click="handleResetJob">{{$t('action.cancel')}}</el-button>
            </el-form-item>
          </div>

          <el-form-item style="margin-top: 30px">
            <el-button @click="handleStepNext(-1)" v-if="control.step>0 && control.step<3">上一步</el-button>
            <el-button @click="handleStepNext(1)" v-if="control.step<3">下一步</el-button>
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
                :key="item.agentId"
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
          <el-button type="primary" @click="handleSubmitNode()">确 定</el-button>
        </div>

      </el-dialog>

    </div>
  </div>

</template>

<script>
  import cron from '@/components/Cron'
  import {getAgent} from '@/api/agent'
  import {getExecUser} from '@/api/user'
  import {addJob, addFlow,getJob, addNode, getNode} from '@/api/job'
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
        control: {//控制页面显示,提供表单数据等...
          step:0,
          agents: [],//已有的执行器
          jobs: [
            {
              params: {jobType: 1},
              label: this.$t('job.simpleJob'),
              options: []
            },
            {
              params: {jobType: 0},
              label: this.$t('job.dependencyItem'),
              options: []
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
            {id: 1, name: this.$t('job.simpleJob')},
            {id: 2, name: this.$t('job.workFlow')}
          ],
          showCron: false,//是否显示cron控件
          showJob: false,//是否显示添加作业弹窗,
          command: null,
          command1: null
        },
        form: {//绑定form表单的数据
          job: {
            jobType: 1,
            alarm:1,
            alarmType: [],
            runCount: 0,
            timeout: 0,
            cronExp: null
          },
          workFlow: {
            count: [{}],
            detail: [{}]
          },
          dependency: {
            jobName: null,
            agentId: null,
            execUser: null,
            command: null,
            successExit: null,
            jobType:0
          }
        },
        jobFormRule: {
          jobName: [
            {required: true, message: '请输入AppName', trigger: 'change'},
            {min: 3, max: 20, message: '长度在 3 到 20 个字符'}
          ],
          jobType: [{required: true, message: '请选择作业类型', trigger: 'change'}],
          cronExp: [{required: true, message: '请输入表达式', trigger: 'change'}],
          agentId: [{trigger: 'change', validator: (r, v, c) => this.checkNull(r, v, c, this.$t('agent.agentName'))}],
          execUser: [{trigger: 'change', validator: (r, v, c) => this.checkNull(r, v, c, this.$t('job.execUser'))}],
          command: [{
            trigger: 'change',
            validator: (r, v, c) => this.checkNull(r, this.form.job.command, c, this.$t('job.command'))
          }],
          successExit: [{trigger: 'change', validator: (r, v, c) => this.checkSuccessExit(r, v, c)}],
          alarmType: [{trigger: 'change', validator: this.checkAlarm}],
          alarmDingURL: [{trigger: 'change', validator: this.checkDingURL}],
          alarmDingAtUser: [{trigger: 'change', validator: this.checkDingAtUser}],
          alarmEmail: [{trigger: 'change', validator: this.checkEmail}],
          alarmSms: [{trigger: 'change', validator: this.checkSms}],
          alarmSmsTemplate: [{
            trigger: 'change',
            validator: (r, v, c) => this.checkNull(r, v, c, this.$t('job.smsTemplate'))
          }],
        }
      }
    },

    created() {
      this.httpGetAgent()
      this.httpGetExecUser()
      this.initWorkFlow()
      this.handleGetJob()
    },

    mounted() {
      this.control.command = this.initCodeMirror(this.$refs.command)
      this.control.command.on('change', cm => {
        this.form.job.command = cm.getValue()
        this.$refs.jobForm.validateField('command')
      })
    },

    methods: {

      //init初始化环境相关。。。
      initWorkFlow() {
        let id = new Date().getTime()
        this.form.workFlow.count[0].id = id
        this.form.workFlow.detail[0].id = id
      },

      initCodeMirror(el) {
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

      //http和后台交互相关...
      httpGetAgent() {
        getAgent().then(response => {
          this.control.agents = response.body
        })
      },

      handleGetJob() {
        this.httpGetJob(0);
        this.httpGetJob(1);
      },

      httpGetJob(index) {
        getJob(this.control.jobs[index].params).then(response => {
          this.control.jobs[index].options = []
          let job = response.body
          job.forEach(x => {
            this.control.jobs[index].options.push({
              id: x.jobId,
              name: x.jobName
            })
          })
        })
      },

      httpGetExecUser() {
        getExecUser().then(response => {
          this.control.execUsers = response.body
        })
      },

      handleStepNext(step){
        this.control.step += step;
      },

      //其他事件相关。。。
      handleSubmitJob(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            //提交简单任务
            if (this.form.job.jobType === 1) {
              addJob(this.form.job).then(response =>{

              })
            } else {
              this.submitWorkFlow
            }
          } else {
            console.log('error submit!!')
            return false
          }
        });
      },

      //提交一个复杂的工作流任务
      submitWorkFlow() {

      },

      handleResetJob(formName) {
        this.$refs[formName].resetFields()
      },

      handleSubmitNode() {
        addNode(this.form.dependency).then(resp => {
          this.control.showJob = false
          this.httpGetJob(1);
        })
      },

      handleAddJob() {
        this.control.showJob = true
        this.$nextTick(() => {
          if (!this.control.command1) {
            this.control.command1 = this.initCodeMirror(this.$refs.command1)
            this.control.command1.on('change', cm => {
              this.form.dependency.command = cm.getValue()
            })
          }
        })
      },

      handleAddNode() {
        let id = new Date().getMilliseconds()
        this.form.workFlow.count.push({"id": id})
        this.form.workFlow.detail.push({
          id: id,
          job: null,
          dependency: null,
          trigger: null
        })
      },

      handleFindNode(id) {
        for (let index = 0; index < this.form.workFlow.detail.length; index++) {
          let detail = this.form.workFlow.detail[index]
          if (detail.id === id) {
            return index
          }
        }
      },

      handleDeleteNode(id) {
        this.form.workFlow.count.forEach((item, index) => {
          if (item.id == id) {
            this.form.workFlow.count.splice(index, 1)
            this.form.workFlow.detail.splice(index, 1)
          }
        })
      },

      //check..验证表单相关。。。
      checkNull(rule, value, callback, field) {
        if (this.form.job.jobType === 1) {
          if (!value) {
            callback(new Error('请输入'.concat(field)))
          } else {
            callback()
          }
        } else {
          callback()
        }
      },

      checkSuccessExit(rule, value, callback) {
        if (this.form.job.alarm == 0) {
          if (value == null || value == undefined || value.length == 0) {
            callback(new Error('请输入成功标志'))
          } else {
            if (!this.$verify.isPositiveNum(value)) {
              callback(new Error('成功标志必须为整数'))
            } else {
              callback()
            }
          }
        } else {
          callback()
        }
      },

      checkAlarm(rule, value, callback) {
        if (this.form.job.alarm == 1) {
          if (!value || value.length == 0) {
            callback(new Error('请至少选择一种报警通知方式'))
          } else {
            callback()
          }
        } else {
          callback()
        }
      },

      checkDingURL(rule, value, callback) {
        if (this.form.job.alarm == 1 && this.form.job.alarmType.indexOf(1) > -1) {
          if (!value) {
            callback(new Error('请输入钉钉机器人URL'))
          } else if (!this.$verify.isDingTaskURL(value)) {
            callback(new Error('钉钉机器人URL错误,请参考钉钉官网规范'))
          } else {
            callback()
          }
        } else {
          callback()
        }
      },

      checkDingAtUser(rule, value, callback) {
        if (this.form.job.alarm == 1 && this.form.job.alarmType.indexOf(1) > -1) {
          if (value && !this.$verify.isPhone(value)) {
            callback(new Error('钉钉@通知人,格式有误,请参考钉钉官网规范'));
          } else {
            callback()
          }
        } else {
          callback()
        }
      },

      checkEmail(rule, value, callback) {
        if (this.form.job.alarm == 1 && this.form.job.alarmType.indexOf(2) > -1) {
          if (!value) {
            callback(new Error('请输入正确的邮箱地址'))
          } else if (!this.$verify.isEmail(value)) {
            callback(new Error('邮箱格式错误'))
          } else {
            callback()
          }
        } else {
          callback()
        }
      },

      checkSms(rule, value, callback) {
        if (this.form.job.alarm == 1 && this.form.job.alarmType.indexOf(2) > -1) {
          if (!value) {
            callback(new Error('请输入正确短信通道商http请求URL'))
          } else if (!this.$verify.isURL(value)) {
            callback(new Error('短信通道商http请求URL错误'))
          } else {
            callback()
          }
        } else {
          callback()
        }
      },
    },

    watch: {
      'form.job.jobType': function (value) {
        this.$refs.jobForm.clearValidate()
      },

      'form.job.alarm': function (value) {
        if (value === 0) {
          this.form.alarmType = []
        }
      },

      'form.job.alarmType': function (value) {
        this.$refs.jobForm.clearValidate('alarmType')
        this.$refs.jobForm.clearValidate('alarmDingURL')
        this.$refs.jobForm.clearValidate('alarmDingAtUser')
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
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>

  .steps-btn {
    margin-left: 200px;
  }

  .steps-form {
    position: static;
    padding-top: 50px;
    padding-left: 20px;
    padding-right: 20px;
  }

  .input-item {
    width: 75%;
  }

  .command-input {
    width: 75%;
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
      padding-left: 0px;
      padding-right: 0px;
    }

    .el-input__inner {
      height: 30px;
    }

    .el-table__header-wrapper {
      line-height: 20px;
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

      .CodeMirror {
        height: 150px;
      }

      .CodeMirror-scroll {
        height: 150px;
      }
    }
  }

  .select-item-right {
    float: right;
    margin-right: 25px;
    color: #8492a6;
    font-size: 13px
  }



</style>
