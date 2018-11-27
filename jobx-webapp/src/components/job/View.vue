<template>
  <section class="content">
      <header class="content__title">
        <h4 class='card-title'>{{title}}</h4>
      </header>
      <div class="card">
        <div class="card-body">
          <div class="table-filter">
            <span class="label arrowed-right">筛选： </span>
            <v-text-field
                v-model="name"
                :rules="nameRules"
                label="作业名称"
                required
              ></v-text-field>
              <v-text-field
                v-model="name"
                :rules="nameRules"
                label="作业名称"
                required
              ></v-text-field>
              <v-text-field
                v-model="name"
                :rules="nameRules"
                label="作业名称"
                required
              ></v-text-field>
          </div>
            <v-data-table
              :headers="headers"
              :loading="loading"
              :pagination.sync="pagination"
              :total-items="pagination.totalItems"
              :items="pageData.result"
              hide-actions
             >
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
                <td class="justify-center layout px-0">
                  <v-icon small class="mr-2" @click="editItem(props.item)">play_arrow</v-icon>
                  <v-icon small class="mr-2" @click="editItem(props.item)">pause</v-icon>
                  <v-icon small class="mr-2" @click="editItem(props.item)">edit</v-icon>
                  <v-icon small class="mr-2" @click="deleteItem(props.item)">visibility</v-icon>
                  <v-icon small class="mr-2" @click="deleteItem(props.item)">delete</v-icon>
                </td>
              </template>
              <template slot="no-data">
                <v-alert :value="true" color="error" icon="warning">
                  Sorry, nothing to display here :(
                </v-alert>
              </template>
          </v-data-table>
          <div class="text-xs-center pt-2" v-if="!noData()">
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
  export default {
    components: {
      pager,action,select2
    },
    data: () => ({
      actions:{
        filter:false
      },
      loading:false,
      pagination: {},
      title:'JOB LIST',
      url: "/job/view",
      headers: [
        { text: '执行器',value: 'agent_name'},
        { text: '名称', value: 'job_name'},
        { text: '执行身份', value: 'exec_user'},
        { text: '命令', value: 'command',sortable: false  },
        { text: '暂停', value: 'pause',sortable: false  },
        { text: '重跑', value: 'redo',sortable: false  },
        { text: '调度方式', value: 'job_type',sortable: false  },
        { text: 'CRONEXP', value: 'cron_exp',sortable: false  },
        { text: 'Actions', value: 'name', sortable: false }
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
        this.pageData = resp.body
        this.pagination.page = this.pageData.pageNo
        this.pagination.pages = this.pageData.pageTotal
        this.pagination.totalItems = this.pageData.totalRecord
        this.pagination.rowsPerPage = this.pageData.pageSize
        setTimeout(() => {
          this.loading = false
        },1000)
      },error=>{
        this.loading = false
      })
    },
    noData:function(){
      return this.pageData == {}|!this.pageData.result
    }
  },
  watch: {
    pagination: {
      deep: true,
      handler (data) {
        if(!this.loading) {
          if(this.pagination.descending != null) {
            this.postData.orderBy = this.pagination.sortBy
            this.postData.order = this.pagination.descending?'desc':'asc'
          }
          this.postData.pageNo = this.pagination.page
          this.postData.pageSize = this.pagination.rowsPerPage
          this.getPageData()
        }
      }
    }

  },
}

</script>
