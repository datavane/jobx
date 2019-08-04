<template>
  <div>
    <a-form style="max-width: 600px; margin: 40px auto 0;" :form="form">
      <a-form-item
        label="AppName"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-input
          allowClear
          v-decorator="[
            'jobName',
            {rules: [{ required: true, message: '请输入AppName' }]}
          ]"
          placeholder="AppName" />
      </a-form-item>
      <a-form-item
        label="作业类型"
        placeholder="请选择作业类型"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-select
          @change="handleChangeType"
          allowClear
          placeholder="作业类型"
          v-decorator="[
            'jobType',
            {rules: [{ required: true, message: '请选择作业类型' }]}
          ]"
        >
          <a-select-option value="1">简单作业</a-select-option>
          <a-select-option value="2">工作流</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item
        label="描述信息"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol">
        <a-textarea
          rows="4"
          placeholder="请输入描述信息"
          v-decorator="['comment']"
        />
      </a-form-item>
      <a-form-item :wrapperCol="{span: 19, offset: 5}">
        <a-button type="primary" @click="nextStep">下一步</a-button>
      </a-form-item>
    </a-form>
    <a-divider />
  </div>
</template>

<script>
import { mapActions } from 'vuex'
export default {
  name: 'Step1',
  props: ['job'],
  data () {
    return {
      labelCol: { lg: { span: 5 }, sm: { span: 5 } },
      wrapperCol: { lg: { span: 19 }, sm: { span: 19 } },
      form: this.$form.createForm(this)
    }
  },
  methods: {
    ...mapActions(['SetJobType']),
    nextStep (e) {
      e.preventDefault()
      this.form.validateFields((err, values) => {
        if (!err) {
          Object.assign(this.job, values)
          this.$emit('nextStep')
        }
      })
    },
    handleChangeType (jobType) {
      this.SetJobType(jobType)
    }
  }
}
</script>
<style lang="less" scoped>
  .step-form-style-desc {
    padding: 0 56px;
    color: rgba(0,0,0,.45);
    h3 {
      margin: 0 0 12px;
      color: rgba(0,0,0,.45);
      font-size: 16px;
      line-height: 32px;
    }
    h4 {
      margin: 0 0 4px;
      color: rgba(0,0,0,.45);
      font-size: 14px;
      line-height: 22px;
    }
    p {
      margin-top: 0;
      margin-bottom: 12px;
      line-height: 22px;
    }
  }
</style>
