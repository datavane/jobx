<template>
  <a-card :bordered="false">
    <a-steps class="steps" :current="stepIndex">
      <a-step v-for="item in stepTitle" :key="item" :title="item"/>
    </a-steps>
    <div class="content">
      <step1 v-show="stepIndex === 0" :job="job1" ref="step1" @nextStep="nextStep"/>
      <step2 v-show="stepIndex === 1" :job="job2" ref="step2" @prevStep="prevStep" @nextStep="nextStep"/>
      <step3 v-show="stepIndex === 2" :job="job3" ref="step3" @prevStep="prevStep" @nextStep="nextStep"/>
      <step4 v-show="stepIndex === 3" :job="job4" ref="step4" @prevStep="prevStep" @nextStep="nextStep"/>
      <step5 v-show="stepIndex === 4" ref="step5" @prevStep="prevStep" @finish="finish"/>
    </div>
  </a-card>
</template>

<script>
import Step1 from './Step1'
import Step2 from './Step2'
import Step3 from './Step3'
import Step4 from './Step4'
import Step5 from './Step5'

export default {
  name: 'StepForm',
  components: {
    Step1,
    Step2,
    Step3,
    Step4,
    Step5
  },
  data () {
    return {
      job1: {},
      job2: {},
      job3: {},
      job4: {},
      description: '将一个冗长或用户不熟悉的表单任务分成多个步骤，指导用户完成。',
      stepTitle: ['基础信息', '调度信息', '告警信息', '作业预览', '提交完成'],
      stepIndex: 0,
      form: null
    }
  },
  methods: {
    nextStep () {
      if (this.stepIndex < 4) {
        this.stepIndex += 1
        if (this.stepIndex === 1) {
          this.$refs.step2.handleInit()
        }
      }
    },
    prevStep () {
      if (this.stepIndex > 0) {
        this.stepIndex -= 1
      }
    },
    finish () {
      this.stepIndex = 0
    }
  }
}
</script>

<style lang="less" scoped>
  .steps {
    max-width: 800px;
    margin: 16px auto;
  }
</style>
