<template lang="html">
  <div class="cron" :val="value_">

    <a-tabs defaultActiveKey="1" @change="callback">
      <a-tab-pane tab="秒" name="s" key="s">
        <second-minute v-model="sVal" lable="秒"></second-minute>
      </a-tab-pane>
      <a-tab-pane tab="分" name="m" key="m">
        <second-minute v-model="mVal" lable="分"></second-minute>
      </a-tab-pane>
      <a-tab-pane tab="时" name="h" key="h">
        <second-minute v-model="hVal" lable="时"></second-minute>
      </a-tab-pane>
      <a-tab-pane tab="日" name="d" key="d">
        <second-minute v-model="dVal" lable="日"></second-minute>
      </a-tab-pane>
      <a-tab-pane tab="月" name="month" key="month">
        <second-minute v-model="monthVal" lable="月"></second-minute>
      </a-tab-pane>
      <a-tab-pane tab="周" name="week" key="week">
        <second-minute v-model="weekVal" lable="周"></second-minute>
      </a-tab-pane>
      <a-tab-pane tab="年" name="year" key="year">
        <second-minute v-model="yearVal" lable="年"></second-minute>
      </a-tab-pane>
    </a-tabs>

    <!-- table -->
    <el-table
      :data="tableData"
      size="mini"
      border
      style="width: 100%;">
      <el-table-column
        prop="sVal"
        label="秒"
        width="70">
      </el-table-column>
      <el-table-column
        prop="mVal"
        label="分"
        width="70">
      </el-table-column>
      <el-table-column
        prop="hVal"
        label="时"
        width="70">
      </el-table-column>
      <el-table-column
        prop="dVal"
        label="日"
        width="70">
      </el-table-column>
      <el-table-column
        prop="monthVal"
        label="月"
        width="70">
      </el-table-column>
      <el-table-column
        prop="weekVal"
        label="周"
        width="70">
      </el-table-column>
      <el-table-column
        prop="yearVal"
        label="年">
      </el-table-column>
    </el-table>
    <div class="recent" v-if="recentData.length">
      <span class="recent_lable">最近3次执行</span>
      <span v-for="item in recentData" style="margin-left: 10px"> {{ item }} </span>
    </div>
  </div>
</template>

<script>
import {recent} from '@/api/verify'
import secondMinute from './secondMinute'
import hour from './hour'
import day from './day'
import month from './month'
import week from './week'
import year from './year'

export default {
  props: {
    value : {
      type: String,
      url: String
    }
  },

  components: {
    secondMinute, hour, day, month, week, year
  },

  data () {
    return {
      activeName: 's',
      sVal: '',
      mVal: '',
      hVal: '',
      dVal: '',
      monthVal: '',
      weekVal: '',
      yearVal: '',
      recentData: []
    }
  },
  watch: {
    'value' (a, b) {
      this.updateVal()
    }
  },

  computed: {
    tableData () {
      return [{
        sVal: this.sVal,
        mVal: this.mVal,
        hVal: this.hVal,
        dVal: this.dVal,
        monthVal: this.monthVal,
        weekVal: this.weekVal,
        yearVal: this.yearVal
      }]
    },

    value_ () {
      if (!this.dVal && !this.weekVal) {
        return ''
      }
      if (this.dVal === '?' && this.weekVal === '?') {
        this.$message.error('日期与星期不可以同时为“不指定”')
      }
      if (this.dVal !== '?' && this.weekVal !== '?') {
        this.$message.error('日期与星期必须有一个为“不指定”')
      }
      let v = this.getCronExp()
      if (v !== this.value) {
        this.$emit('input', v)
      }
      return v
    }
  },

  methods: {
    callback (key) {
      console.log(key)
    },
    getCronExp () {
      return `${this.sVal} ${this.mVal} ${this.hVal} ${this.dVal} ${this.monthVal} ${this.weekVal} ${this.yearVal}`
    },
    updateVal () {
      if (!this.value) {
        return
      }
      const array = this.value.split(' ')
      this.sVal = array[0]
      this.mVal = array[1]
      this.hVal = array[2]
      this.dVal = array[3]
      this.monthVal = array[4]
      this.weekVal = array[5]
      this.yearVal = array[6]

      recent(this.getCronExp()).then(response => {
        if (response.code === 200) {
          this.recentData = response.body
        } else {
          this.recentData = []
          /*this.$message({
            message: response.body,
            type: 'error',
            duration: 5 * 1000
          })*/
        }
      })
    }
  },
  created () {
    this.updateVal()
  }

}
</script>

<style rel="stylesheet/scss" lang="less" scoped>
  .cron {
    text-align: left;
    padding: 10px;
    background: #fff;
    //box-shadow: 0 2px 4px 0 rgba(0, 0, 0, .12), 0 0 6px 0 rgba(0, 0, 0, .04);
    .recent {
      margin-top: 20px;
      margin-bottom: 10px;
      font-size: 12px;
      .recent_lable {
        font-weight: bold;
        margin-right: 10px
      }
    }
  }
</style>
