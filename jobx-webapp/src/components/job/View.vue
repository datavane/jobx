<template>
  <section class="content">
      <header class="content__title">
        <h4 class='card-title'>{{title}}</h4>
      </header>
      <div class="card">
        <div class="card-body">
            <v-data-table
              :headers="headers"
              :loading="loading"
              :pagination.sync="pagination"
              :total-items="pagination.totalItems"
              :items="pageData"
              hide-actions>
              <v-progress-linear slot="progress" color="white" style="height:1px" indeterminate></v-progress-linear>
              <template slot="items" slot-scope="props">
                <td class="text-left">{{ props.item.agentName }}</td>
                <td class="text-left">{{ props.item.jobName }}</td>
                <td class="text-left">{{ props.item.execUser }}</td>
                <td class="text-left">{{ props.item.command }}</td>
                <td class="text-left">{{ props.item.pause }}</td>
                <td class="text-left">{{ props.item.redo }}</td>
                <td class="text-left">{{ props.item.jobType }}</td>
                <td class="text-left">{{ props.item.cronExp }}</td>
              </template>
              <template slot="no-data">
                <v-alert :value="true" color="error" icon="warning">
                  Sorry, nothing to display here :(
                </v-alert>
              </template>
          </v-data-table>
          <div class="text-xs-center pt-2" v-if="pageData">
            <v-pagination v-model="pagination.page" :length="pagination.pages"></v-pagination>
          </div>
        </div>
      </div>
  </section>
</template>
<script type="text/ecmascript-6">
  import pager from '@/components/common/Pager'
  import action from '@/components/common/Action'
  import select2 from '@/components/common/Select2'
  import BScroll from 'better-scroll'
  import { setTimeout } from 'timers'
  export default {
    components: {
      pager,action,select2
    },
    data: () => ({
      actions:{
        filter:false
      },
      loading:true,
      pagination: {},
      title:'JOB LIST',
      url: "/job/view",
      headers: [
        {text: '执行器',value: 'agent_name',sortable: true},
        { text: '名称', value: 'job_name',sortable: true },
        { text: '执行身份', value: 'exec_user',sortable: true },
        { text: '命令', value: 'command' },
        { text: '暂停', value: 'pause' },
        { text: '重跑', value: 'redo' },
        { text: '调度方式', value: 'job_type' },
        { text: 'CRONEXP', value: 'cron_exp' }
      ],
      pageData:{},
      postData:{}
  }),

  mounted () {
    this.getPageData()
  },

  methods: {
    getPageData () {
      this.loading = true
      this.$http.post(this.url,this.postData).then(resp => {
        this.pagination.page = resp.body.pageNo
        this.pagination.pages = resp.body.pageTotal
        this.pagination.totalItems = resp.body.pageSize
        this.pageData = resp.body.result
        setTimeout(() => {
          this.loading = false
        },1000)
      })
    }
  },
  watch: {
      pagination: {
        deep: true,
        handler (data) {
          this.postData.pageNo = this.pagination.page
          if(this.pagination.descending != null) {
            this.postData.orderBy = this.pagination.sortBy
            this.postData.order = this.pagination.descending?'desc':'asc'
          }
          this.getPageData()
        }
      }
  },
}

</script>