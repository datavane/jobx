<template>
  <div class="app-container">

      <el-steps :active="control.step" finish-status="success" align-center class="steps">
        <el-step title="基础信息"></el-step>
        <el-step title="调度信息"></el-step>
        <el-step title="告警信息"></el-step>
        <el-step title="作业预览"></el-step>
        <el-step title="提交完成"></el-step>
      </el-steps>


      <el-form :model="form.job" :ref="formName" :rules="stepValidator" label-width="10%" class="steps-form">

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

          <el-dialog class="cronExp" :visible.sync="control.showCron" width="550px">
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
                collapse-tags
                :key="item"
                :label="item"
                :value="item">
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item :label="$t('job.successExit')"  v-if="form.job.jobType == 1" prop="successExit">
            <el-input :placeholder="$t('job.successExit')" v-model.number="form.job.successExit" clearable class="input-item"/>
          </el-form-item>

          <!--工作流-->
          <el-form-item v-if="form.job.jobType == 2" :label="$t('job.dependency')">
            <div v-for="(item,index) in form.workFlow" class="workflow">
              <div :class=" index==0 ?'workRoot':'work-item'">
                <el-form-item>
                  <el-select
                    v-model="item.jobId"
                    clearable
                    placeholder="请选择">
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
                </el-form-item>
              </div>

              <div class="work-item" v-if="index > 0">
                <el-form-item>
                  <el-select
                    v-model="item.parentId"
                    clearable
                    :placeholder="$t('job.parentDependency')">
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
                </el-form-item>
              </div>

              <div class="work-item" v-if="index > 0">
                <el-form-item>
                  <el-select v-model="item.trigger" :placeholder="$t('job.trigger.name')" clearable>
                    <el-option v-for="item in control.triggerType" :key="item.id" :label="item.name" :value="item.id"/>
                  </el-select>
                </el-form-item>
              </div>

              <div class="work-item">
                <el-tooltip class="item" effect="dark" content="添加一个流程作业" placement="top">
                  <el-button type="success" icon="el-icon-plus" v-if="index == 1" circle @click="handleAddNode()"></el-button>
                </el-tooltip>
                <el-button type="danger" icon="el-icon-delete" v-if="index>1" circle @click="handleDeleteNode(index)"></el-button>
              </div>
            </div>

            <div style="margin-top: 20px">
              <el-tooltip class="item" effect="dark" content="新增一个作业节点" placement="top-start">
                <el-button type="primary" icon="el-icon-plus" circle @click="handleAddJob()"></el-button>
              </el-tooltip>
            </div>
          </el-form-item>

        </div>

        <div v-show="control.step == 2">

          <el-form-item :label="$t('job.runCount')" prop="runCount">
            <el-input-number v-model="form.job.runCount" controls-position="right" clearable :min="0" :max="10" class="input-item"></el-input-number>
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
          <div v-if="form.job.alarm ==1 ">
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

        <div v-if="control.step == 3">
          <div class="preview-card">

            <div class="detail_step">
              <div class="title"><i class="el-icon-tickets"></i>&nbsp;基础信息
                <i class="el-icon-edit-outline edit" @click="control.step = 0"></i>
              </div>
              <div class="line"></div>
              <div class="detail_table">
                <table>
                  <tr>
                    <td>{{$t('job.jobName')}}：{{form.job.jobName}}</td>
                    <td>
                      {{$t('job.jobType')}}：
                      <span v-if="form.job.jobType == 1">{{$t('job.simpleJob')}}</span>
                      <span v-else>{{$t('job.workFlow')}}</span>
                    </td>
                  </tr>
                  <tr>
                    <td colspan="2">{{$t('job.comment')}}：{{form.job.comment}}</td>
                  </tr>
                </table>
              </div>
            </div>

            <div class="detail_step">
              <div class="title"><i class="el-icon-tickets"></i>&nbsp;调度信息
                <i class="el-icon-edit-outline edit" @click="control.step = 1"></i>
              </div>
              <div class="line"></div>
              <div class="detail_table">
                <table v-if="form.job.jobType == 1">
                  <tr>
                    <td>{{$t('agent.agentName')}}：{{choose.agentName}}</td>
                    <td>{{$t('job.cronExp')}}：{{form.job.cronExp}}</td>
                  </tr>
                  <tr>
                    <td>{{$t('job.execUser')}}：<el-tag size="small" type="success">{{form.job.execUser}}</el-tag></td>
                    <td>{{$t('job.successExit')}}：{{form.job.successExit}}</td>
                  </tr>
                  <tr>
                    <td colspan="2">{{$t('job.command')}}：{{form.job.command}}</td>
                  </tr>
                </table>
                <table v-else>
                  <tr>
                    <td>作业节点数: {{form.workFlow.length}}</td>
                    <td>{{$t('job.cronExp')}}：{{form.job.cronExp}}</td>
                  </tr>
                </table>
              </div>
            </div>

            <div class="detail_step">
              <div class="title"><i class="el-icon-tickets"></i>&nbsp;告警信息
                <i class="el-icon-edit-outline edit" @click="control.step = 2"></i>
              </div>
              <div class="line"></div>
              <div class="detail_table">
                <table>
                  <tr>
                    <td>{{$t('job.runCount')}}：{{form.job.runCount}}</td>
                    <td>{{$t('job.timeout')}}：{{form.job.timeout}}</td>
                  </tr>
                  <tr>
                    <td :colspan="form.job.alarm ==1?1:2">
                      {{$t('job.alarm')}}：
                      <el-switch
                        disabled
                        v-model="form.job.alarm"
                        active-color="#13ce66"
                        inactive-color="#ff4949"
                        active-value="1"
                        inactive-value="0">
                      </el-switch>
                    </td>
                    <td v-if="form.job.alarm ==1">{{$t('job.alarmType')}}：<el-tag v-for="(item,index) in choose.alarmType" size="small" style="margin-right: 10px">{{item}}</el-tag></td>
                  </tr>

                  <tr v-if="form.job.alarm == 1 && form.job.alarmType.indexOf(1)>-1">
                    <td colspan="2">{{$t('job.dingTask')}}：{{form.job.alarmDingURL}}</td>
                  </tr>

                  <tr v-if="form.job.alarm == 1 && form.job.alarmType.indexOf(1)>-1">
                    <td colspan="2">{{$t('job.atUser')}}：{{form.job.alarmDingAtUser}}</td>
                  </tr>

                  <tr v-if="form.job.alarm == 1 && form.job.alarmType.indexOf(2)>-1">
                    <td colspan="2">{{$t('job.email')}}：{{form.job.alarmEmail}}</td>
                  </tr>

                  <tr v-if="form.job.alarm == 1 && form.job.alarmType.indexOf(3)>-1">
                    <td colspan="2">{{$t('job.sms')}}：{{form.job.alarmSms}}</td>
                  </tr>

                  <tr v-if="form.job.alarm == 1 && form.job.alarmType.indexOf(3)>-1">
                    <td colspan="2">{{$t('job.smsTemplate')}}：{{form.job.alarmSmsTemplate}}</td>
                  </tr>

                </table>
              </div>
            </div>

            <div class="detail_step">
              <div class="title">DAG预览图</div>
              <div class="line"></div>
              <div class="detail_table">
                <diagram ref="diag" :data="diagramData"></diagram>
              </div>
            </div>

          </div>

          <el-form-item>
            <el-button type="primary" @click="handleSubmitJob('jobForm')">{{$t('action.create')}}</el-button>
            <el-button @click="handleResetJob">{{$t('action.cancel')}}</el-button>
          </el-form-item>
        </div>

        <div v-if="control.step == 4">
          <div class="create_success">
            <div class="success">
              <font-awesome-icon icon="check-circle" class="success"></font-awesome-icon>
              <div>操作成功</div>
            </div>
            <div class="box">
              付款账户：ant-design@alipay.com
              收款账户：test@example.com
              收款人姓名：Alex
              转账金额：500 元
            </div>
          </div>
        </div>

        <el-form-item style="margin-top: 30px">
          <el-button @click="handleStepNext(-1)" type="primary" v-if="control.step>0 && control.step<3">上一步</el-button>
          <el-button @click="handleStepNext(1)" type="primary" v-if="control.step<3">下一步</el-button>
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
              <textarea ref="command1" placeholder="请输入内容" v-model="form.dependency.command"></textarea>
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

</template>

<script>
  import cron from '@/components/Cron'
  import diagram from '@/components/Diagram/diagram'
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
      cron,diagram
    },
    data() {
      return {
        diagramData: {
          nodeDataArray: [
            { key: 1, text: "zhekou_ab_case"},
            { key: 2, text: "hds_mysql_high_flow"},
            { key: 3, text: "member_device"},
            { key: 4, text: "sm_ftp_download"},
            { key: 5, text: "hds_mysql_high_other_flow"},
            { key: 6, text: "hdw_user_new_device"},
            { key: 7, text: "hdw_business_entrance"},
            { key: 8, text: "hds_parser_log_high_flow"},
            { key: 9, text: "hdw_user_product_order_flow"},
            { key: 10, text: "sm_black_device"},
            { key: 11, text: "hdw_view_order_model"},
            { key: 12, text: "hds_parser_log_normal_flow"},
            { key: 13, text: "hdw_user_new_active"},
            { key: 14, text: "hdw_app_base_stat_flow"},
            { key: 15, text: "hdm_server_data_flow"},
            { key: 16, text: "hdm_user_order"},
            { key: 17, text: "real_time_flow"},
            { key: 18, text: "hdw_ab_test_flow"},
            { key: 19, text: "hds_parser_log_low_flow"},
            { key: 20, text: "hdm_analysis_demand_flow"},
            { key: 21, text: "device_push_kafka"},
            { key: 22, text: "hdm_alg_data_flow"},
            { key: 23, text: "tag_flow"},
            { key: 24, text: "analysis_task_order_new"},
            { key: 25, text: "end"},
          ],
          linkDataArray: [
            { from: 1, to: 8 },
            { from: 2, to: 5 },
            { from: 2, to: 9 },
            { from: 3, to: 6 },
            { from: 4, to: 6 },
            { from: 5, to: 7 },
            { from: 5, to: 11 },
            { from: 6, to: 8 },
            { from: 6, to: 9 },
            { from: 6, to: 10 },
            { from: 7, to: 17 },
            { from: 8, to: 12 },
            { from: 8, to: 13 },
            { from: 8, to: 14 },
            { from: 9, to: 11 },
            { from: 9, to: 13 },
            { from: 9, to: 14 },
            { from: 9, to: 15 },
            { from: 9, to: 16 },
            { from: 10, to: 25 },
            { from: 11, to: 18 },
            { from: 12, to: 19 },
            { from: 13, to: 17 },
            { from: 13, to: 18 },
            { from: 13, to: 20 },
            { from: 13, to: 21 },
            { from: 13, to: 22 },
            { from: 13, to: 23 },
            { from: 14, to: 23 },
            { from: 15, to: 25 },
            { from: 15, to: 25 },
            { from: 16, to: 25 },
            { from: 17, to: 25 },
            { from: 18, to: 25 },
            { from: 20, to: 24 },
            { from: 19, to: 25 },
            { from: 21, to: 25 },
            { from: 22, to: 25 },
            { from: 23, to: 25 },
            { from: 24, to: 25 },
          ]
        },
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
          command1: null,
        },
        choose:{
          agentName:null,
          alarmType:[]
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
          workFlow:[{
            jobId:null
          },{
            jobId:null,
            parentId:null,
            trigger:null
          }],
          dependency: {
            jobName: null,
            agentId: null,
            execUser: null,
            command: null,
            successExit: null,
            jobType:0
          }
        },
        formName:'jobForm',
        stepValidator:{},
        validators:[
          {
            jobName: [
              {required: true, message: '请输入AppName', trigger: 'change'},
              {min: 3, max: 20, message: '长度在 3 到 20 个字符'}
            ],
            jobType: [{required: true, message: '请选择作业类型', trigger: 'change'}]
          },
          {
            agentId: [{trigger: 'change', validator: (r, v, c) => this.checkNull(r, v, c, this.$t('agent.agentName'))}],
            cronExp: [{required: true, message: '请输入表达式', trigger: 'change'}],
            command: [{
              trigger: 'change',
              validator: (r, v, c) => this.checkNull(r, this.form.job.command, c, this.$t('job.command'))
            }],
            execUser: [{trigger: 'change', validator: (r, v, c) => this.checkNull(r, v, c, this.$t('job.execUser'))}],
            successExit: [{trigger: 'change', validator: (r, v, c) => this.checkSuccessExit(r, v, c)}],
          },
          {
            alarmType: [{trigger: 'change', validator: this.checkAlarm}],
            alarmDingURL: [{trigger: 'change', validator: this.checkDingURL}],
            alarmDingAtUser: [{trigger: 'change', validator: this.checkDingAtUser}],
            alarmEmail: [{trigger: 'change', validator: this.checkEmail}],
            alarmSms: [{trigger: 'change', validator: this.checkSms}],
            alarmSmsTemplate: [{
              trigger: 'change',
              validator: (r, v, c) => this.checkNull(r, v, c, this.$t('job.smsTemplate'))
            }]
          }
        ]
      }
    },
    created() {
      this.httpGetAgent()
      this.httpGetExecUser()
      this.handleGetJob()
    },
    mounted() {
      this.control.command = this.initCodeMirror(this.$refs.command)
      this.control.command.on('change', cm => {
        this.form.job.command = cm.getValue()
        this.$refs[this.formName].validateField('command')
      })
    },

    methods: {
      initCodeMirror(el) {
        return CodeMirror.fromTextArea(el, {
          lineNumbers: true,
          lint: true,
          autoMatchParens: true,
          mode: 'shell',
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
        this.httpGetJob(0)
        this.httpGetJob(1)
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
      handleStepNext(step) {
        this.$refs[this.formName].clearValidate()
        if (step == -1) {
          this.control.step += step
        } else {
          Object.assign(this.stepValidator,this.validators[this.control.step])
          this.$refs[this.formName].validate((valid) => {
            if (valid) {
              //验证通过
              this.control.step += step
              console.log("check Success...")
            } else {
              console.log('error submit!!')
              return false
            }
          })

          if (this.control.step == 3) {
            this.handleGraph()
          }
        }
      },
      //其他事件相关。。。
      handleSubmitJob(formName) {
        this.control.step += 1

        this.$refs[formName].validate((valid) => {
          if (valid) {
            //提交简单任务
            if (this.form.job.jobType == 1) {
              addJob(this.form.job).then(response =>{
              })
            } else {
              this.submitWorkFlow()
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
        this.form.workFlow.push({
          jobId: null,
          parentId: null,
          trigger: null
        })
      },
      handleDeleteNode(index) {
        this.form.workFlow.splice(index, 1)
      },

      handleGraph() {
        this.$nextTick(()=>{
          this.$refs.diag.handleUpdateDiagram()
        })
      },

      handleShowEdit(name) {

        this.$refs[name].style.display = 'block'
      },

      //check..验证表单相关。。。
      checkNull(rule, value, callback, field) {
        if (this.form.job.jobType == 1) {
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
      'control.step'() {
        for(let key in this.stepValidator) {
          delete this.stepValidator[key]
        }
      },
      'form.job.agentId'(value){
        this.control.agents.forEach((item,index)=>{
          if (item.agentId == value) {
            this.choose.agentName = item.agentName
          }
        })
      },
      'form.job.jobType'(value) {
        this.$refs[this.formName].clearValidate()
      },
      'form.job.alarm'(value) {
        if (value == 0) {
          this.form.alarmType = []
        }
      },
      'form.job.alarmType' (value) {
        this.$refs[this.formName].clearValidate('alarmType')
        this.$refs[this.formName].clearValidate('alarmDingURL')
        this.$refs[this.formName].clearValidate('alarmDingAtUser')
        this.$refs[this.formName].clearValidate('alarmEmail')
        this.$refs[this.formName].clearValidate('alarmSms')
        this.$refs[this.formName].clearValidate('alarmSmsTemplate')
        this.choose.alarmType = []
        this.control.alarmType.forEach((item,index)=>{
          if (value.indexOf(item.id)>-1) {
            this.choose.alarmType.push(item.name)
          }
        })

      },
      'form.job.command' (value) {
        let codeValue = this.control.command.getValue()
        if (value !== codeValue) {
          this.control.command.setValue(value)
        }
      },
      'form.dependency.command' (value) {
        let codeValue = this.control.command1.getValue()
        if (value !== codeValue) {
          this.control.command1.setValue(value)
        }
      },
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .steps {
    width:90%;
    margin: 0 auto;
    margin-top: 50px;
  }

  .steps-form {
    display:block;
    position: static;
    padding-top: 50px;
    width: 75%;
    margin:0 auto;
    .input-item {
      width: 91%;
    }
    .command-input {
      width: 91%;
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
    .workflow {
      margin-bottom:20px;
      overflow: hidden;
      .el-select{
        width: 91%;
      }
      .work-item {
        float: left;
        width: 31.222%;
        &:last-child {
          float: left;
          width: 20px;
        }
      }
      .workRoot {
        width: 100%;
      }
    }
    .preview-card {
      width: 100%;
      padding:0 5%;
      .detail_step {
        margin-bottom: 20px;
        .title{
          color: #2f2f2f;
          font-size: 16px;
          font-weight: 700;
          margin-top: 10px;
          margin-bottom: 15px;
          i{
            color:#909399;
          }
          .edit{
            float: right;
            cursor: pointer;
            padding: 10px;
            &:hover{
              color: #40a9ff;
            }
          }
        }
        .line{
          width: 100%;
          height: 1px;
          background-color:#fff;
        }
        .detail_table {
          border: 1px dashed #d9d9d9;
          background-color: #fff;
          border-radius: 10px;
          margin-bottom: 20px;
          &:hover{
            background-color: #f9fafc;
            border: 1px dashed #40a9ff;
          }
          table {
            width: 90%;
            padding-top: 10px;
            padding-bottom: 10px;
            tr td {
              width: 50%;
              height: 35px;
              font-size: 13px;
              color: #555;
              line-height: 1;
              &:not(:first-child) {
                padding-left: 50px;
              }
              &:first-child {
                padding-left: 20px;
              }
            }
          }

        }
      }

    }

    .create_success{
      width: 500px;
      display:block;
      position: static;
      margin: 10px auto;
      .success{
        width: 100px;
        display:block;
        margin: 10px auto;
        svg{
          font-size: 70px;
          color: #52c41a;
        }
        div {
          margin-top: 20px;
          margin-bottom: 20px;
          font-size: 24px;
          color: #000;
        }
      }
      .box{
        display: none;
        width: 600px;
        height: 400px;
        background: #fafafa;
      }

    }

  }
</style>
