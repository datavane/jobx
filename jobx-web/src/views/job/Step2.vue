<template>
  <div>
    <div v-if="jobType == 1">
      <a-form style="max-width: 600px; margin: 40px auto 0;" :form="form">
        <a-form-item
          label="执行器"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
        >
          <a-select
            placeholder="请选择"
            allowClear
            v-decorator="[
              'agentId',
              {rules: [{ required: true, message: '请选择执行器' }]}
            ]"
          >
            <a-select-option
              v-for="item in agents"
              :key="item.agentId"
              :value="item.agentId">{{ item.agentName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          label="表达式"
          placeholder="表达式"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
        >
          <cron v-model="cronExp" url="/verify/recent"></cron>
          <a-input
            allowClear
            @focus="cronPopover=true"
            @blur="cronPopover=false"
            v-decorator="[
            'cronExp',
            {rules: [{ required: true, message: '请输入表达式' }]}
          ]"
          />
        </a-form-item>
        <a-form-item
          label="执行命令"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol">
          <a-textarea
            class="command"
            placeholder="请输入执行命令"
            v-decorator="[
            'command',
            {rules: [{ required: true, message: '请输入执行命令' }]}
          ]"
          />
        </a-form-item>
        <a-form-item
          label="执行身份"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
        >
          <a-select
            placeholder="请选择"
            v-decorator="[
              'execUser',
              {rules: [{ required: true, message: '请选择执行身份' }]}
            ]"
          >
            <a-select-option
              v-for="item in execUsers"
              :key="item"
              :value="item">{{ item }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          label="成功标识"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
        >
          <a-input-number
            :min="0"
            :max="100000"
            :defaultValue="0"
            name="successExit"
            placeholder="成功标识"
          />
        </a-form-item>
        <a-form-item :wrapperCol="{span: 19, offset: 5}">
          <a-button @click="prevStep">上一步</a-button>
          <a-button style="margin-left: 8px" type="primary" @click="nextStep">下一步</a-button>
        </a-form-item>
      </a-form>
      <a-divider/>
    </div>
    <div v-if="jobType == 2">
      2222222
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import cron from '@/components/Cron'
import { getAgent as fetchAgent } from '@/api/agent'
import { getExecUser as fetchExecUser } from '@/api/user'
import CodeMirror from 'codemirror'
import 'codemirror/theme/darcula.css'
import 'codemirror/lib/codemirror.css'
import 'codemirror/mode/shell/shell'

export default {
  components: { cron },
  name: 'Step2',
  data () {
    return {
      execUsers: [],
      cronPopover: false,
      cronExp: '',
      agents: [],
      labelCol: { lg: { span: 5 }, sm: { span: 5 } },
      wrapperCol: { lg: { span: 19 }, sm: { span: 19 } },
      form: this.$form.createForm(this)
    }
  },
  methods: {
    handleInit () {
      if (this.jobType === '1' && !this.inined) {
        this.$nextTick(function () {
          this.httpGetAgent()
          this.httpGetExecUser()
          this.initCodeMirror()
          this.inined = true
        })
      }
    },
    initCodeMirror () {
      CodeMirror.fromTextArea(document.querySelector('.command'), {
        tabSize: 4,
        styleActiveLine: true,
        lineNumbers: true,
        line: true,
        foldGutter: true,
        styleSelectedText: true,
        matchBrackets: true,
        showCursorWhenSelecting: true,
        extraKeys: { 'Ctrl': 'autocomplete' },
        lint: true,
        autoMatchParens: true,
        mode: 'shell',
        theme: 'darcula',	// 设置主题
        lineWrapping: true, // 代码折叠
        gutters: ['CodeMirror-linenumbers', 'CodeMirror-foldgutter', 'CodeMirror-lint-markers']
      }).on('change', cm => {
        this.form.setFieldsValue({
          command: cm.getValue()
        })
      })
    },
    validateSuccess (rule, value, callback) {
      const regex = /^[0-9]+$/
      if (!regex.test(value)) {
        callback(new Error('成功标识必须为整数'))
      }
      callback()
    },
    prevStep (e) {
      this.$emit('prevStep')
    },
    changeCron (val) {
      console.log(val)
    },
    handleOk(e) {
      console.log(e);
      this.cronPopover = false
    },
    nextStep (e) {
      e.preventDefault()
      this.form.validateFields((err, values) => {
        if (!err) {
          console.log('Received values of form: ', values)
          this.$emit('nextStep')
        }
      })
    },
    httpGetAgent () {
      fetchAgent().then(resp => {
        this.agents = resp.body
      })
    },
    httpGetExecUser () {
      fetchExecUser().then(resp => {
        this.execUsers = resp.body
      })
    }
  },
  computed: {
    ...mapGetters(['jobType'])
  }
}
</script>

<style lang="less" scoped>
  .ant-input-number {
    width: 100%;
  }
</style>
