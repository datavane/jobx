<!-- 秒,分钟 -->
<template lang="html">
  <div :val="value_" class="cron-container">
    <div class="line">
      <a-radio-group v-model="type" size="small" buttonStyle="solid">
        <a-radio-button value="1">每{{lable}}</a-radio-button>
      </a-radio-group>
    </div>
    <div class="line">
      <a-radio-group v-model="type" size="small" buttonStyle="solid">
        <a-radio-button value="2">周期</a-radio-button>
      </a-radio-group>
      <span style="margin-left: 10px; margin-right: 5px;">从</span>
      <a-input-number @change="type = '2'" v-model="cycle.start" :min="1" :max="59" size="small"></a-input-number>
      <span style="margin-left: 5px; margin-right: 5px;">至</span>
      <a-input-number @change="type = '2'" v-model="cycle.end" :min="2" :max="59" size="small"></a-input-number>
      {{lable}}
    </div>
    <div class="line">
      <a-radio-group v-model="type" size="small" buttonStyle="solid">
        <a-radio-button value="3">循环</a-radio-button>
      </a-radio-group>
      <span style="margin-left: 10px; margin-right: 5px;">从</span>
      <a-input-number @change="type = '3'" v-model="loop.start" :min="0" :max="59" size="small"></a-input-number>
      <span style="margin-left: 5px; margin-right: 5px;">{{lable}}开始，每</span>
      <a-input-number @change="type = '3'" v-model="loop.end" :min="1" :max="59" size="small"></a-input-number>
      {{lable}}执行一次
    </div>
    <div class="line">
      <a-radio-group v-model="type" size="small" buttonStyle="solid">
        <a-radio-button value="4">指定</a-radio-button>
      </a-radio-group>
      <a-checkbox-group v-model="appoint" @change="type = '4'"  >
        <div  v-for="i in 6" :key="i" style="margin-left: 2px; line-height: 25px;">
          <a-checkbox @change="type = '4'"  v-for="j in 10" :key="j">{{(i - 1) + '' + (j - 1)}}</a-checkbox>
        </div>
      </a-checkbox-group>

    </div>
  </div>
</template>

<script>
  export default {
    props: {
      value: {
        type: String,
        default: '*'
      },
      lable: {
        type: String
      }
    },
    data () {
      return {
        type: '1', // 类型
        cycle: { // 周期
          start: 0,
          end: 0
        },
        loop: { // 循环
          start: 0,
          end: 0
        },
        week: { // 指定周
          start: 0,
          end: 0
        },
        work: 0,
        last: 0,
        appoint: [] // 指定
      }
    },
    computed: {
      value_ () {
        let result = []
        switch (this.type) {
          case '1': // 每秒
            result.push('*')
            break
          case '2': // 年期
            result.push(`${this.cycle.start}-${this.cycle.end}`)
            break
          case '3': // 循环
            result.push(`${this.loop.start}/${this.loop.end}`)
            break
          case '4': // 指定
            result.push(this.appoint.join(','))
            break
          case '6': // 最后
            result.push(`${this.last === 0 ? '' : this.last}L`)
            break
          default: // 不指定
            result.push('?')
            break
        };
        this.$emit('input', result.join(''))
        return result.join('')
      }
    },
    watch: {
      'value' (a, b) {
        this.updateVal()
      }
    },
    methods: {
      updateVal () {
        if (!this.value) {
          return
        }
        if (this.value === '?') {
          this.type = '5'
        } else if (this.value.indexOf('-') !== -1) { // 2周期
          if (this.value.split('-').length === 2) {
            this.type = '2'
            this.cycle.start = this.value.split('-')[0]
            this.cycle.end = this.value.split('-')[1]
          }
        } else if (this.value.indexOf('/') !== -1) { // 3循环
          if (this.value.split('/').length === 2) {
            this.type = '3'
            this.loop.start = this.value.split('/')[0]
            this.loop.end = this.value.split('/')[1]
          }
        } else if (this.value.indexOf('*') !== -1) { // 1每
          this.type = '1'
        } else if (this.value.indexOf('L') !== -1) { // 6最后
          this.type = '6'
          this.last = this.value.replace('L', '')
        } else if (this.value.indexOf('#') !== -1) { // 7指定周
          if (this.value.split('#').length === 2) {
            this.type = '7'
            this.week.start = this.value.split('#')[0]
            this.week.end = this.value.split('#')[1]
          }
        } else if (this.value.indexOf('W') !== -1) { // 8工作日
          this.type = '8'
          this.work = this.value.replace('W', '')
        } else { // *
          this.type = '4'
          this.appoint = this.value.split(',')
        }
      }
    },
    created () {
      this.updateVal()
    }
  }
</script>
