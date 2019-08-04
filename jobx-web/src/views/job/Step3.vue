<template>
  <div>
    <a-form style="max-width: 600px; margin: 40px auto 0;" :form="form">
      <a-form-item
        label="重跑次数"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-input-number
          :min="0"
          :max="100000"
          :defaultValue="0"
          name="runCount"
          placeholder="重跑次数"
        />
      </a-form-item>
      <a-form-item
        label="时长限制"
        placeholder="时长限制"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-input
          allowClear
          v-decorator="[
            'timeOut',
            {rules: [{ required: true, message: '请输入时长限制' }]}
          ]"
        />
      </a-form-item>
      <a-form-item
        label="失败报警"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol">
        <a-switch @change="handleWatchAlarm">
          <a-icon type="check" slot="checkedChildren"/>
          <a-icon type="close" slot="unCheckedChildren"/>
        </a-switch>
      </a-form-item>
      <a-form-item
        label="报警方式"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        v-if="alarm">
        <a-checkbox-group :options="alarmOptions" v-model="alarmType" @change="handleChangeAlarm"/>
      </a-form-item>
      <a-form-item
        label="钉钉"
        placeholder="钉钉URL"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        v-if="alarmType.indexOf(1)>-1"
      >
        <a-input
          allowClear
          v-decorator="[
            'alarmDingURL',
            {rules: [{ required: true, message: '请输入钉钉URL' }]}
          ]"
        />
      </a-form-item>
      <a-form-item
        label="钉钉通知人"
        placeholder="@"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        v-if="alarmType.indexOf(1)>-1"
      >
        <a-input
          allowClear
          v-decorator="[
            'alarmDingAt',
            {rules: [{ required: true, message: '请输入钉钉通知人' }]}
          ]"
        />
      </a-form-item>
      <a-form-item
        label="告警邮箱"
        placeholder="告警邮箱"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        v-if="alarmType.indexOf(2)>-1"
      >
        <a-input
          allowClear
          v-decorator="[
            'alarmEmail',
            {rules: [{ required: true, message: '请输入告警email' }]}
          ]"
        />
      </a-form-item>
      <a-form-item
        label="短信"
        placeholder="短信"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        v-if="alarmType.indexOf(3)>-1"
      >
        <a-input
          allowClear
          v-decorator="[
            'alarmSms',
            {rules: [{ required: true, message: '请输入告警短信URL' }]}
          ]"
        />
      </a-form-item>
      <a-form-item
        label="短信模板"
        placeholder="短信模板"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        v-if="alarmType.indexOf(3)>-1"
      >
        <a-input
          allowClear
          v-decorator="[
            'alarmSmsTemplate',
            {rules: [{ required: true, message: '请输入告警短信模板' }]}
          ]"
        />
      </a-form-item>
      <a-form-item :wrapperCol="{span: 19, offset: 5}">
        <a-button @click="prevStep">上一步</a-button>
        <a-button style="margin-left: 8px" type="primary" @click="nextStep">下一步</a-button>
      </a-form-item>
    </a-form>
    <a-divider/>
  </div>
</template>

<script>
export default {
  name: 'Step3',
  data () {
    return {
      alarm: false,
      alarmType: [],
      alarmOptions: [
        { label: '钉钉', value: 1 },
        { label: '邮件', value: 2 },
        { label: '短信', value: 3 }
      ],
      labelCol: { lg: { span: 5 }, sm: { span: 5 } },
      wrapperCol: { lg: { span: 19 }, sm: { span: 19 } },
      form: this.$form.createForm(this)
    }
  },
  methods: {
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
    nextStep (e) {
      e.preventDefault()
      this.form.validateFields((err, values) => {
        if (!err) {
          console.log('Received values of form: ', values)
          this.$emit('nextStep')
        }
      })
    },
    handleWatchAlarm (checked) {
      this.alarm = checked
      if (!this.alarm) {
        this.alarmType = []
      }
    },
    handleChangeAlarm (checked) {
      console.log(this.alarmType)
    }
  }
}
</script>

<style lang="less" scoped>
  .ant-input-number {
    width: 100%;
  }
</style>
