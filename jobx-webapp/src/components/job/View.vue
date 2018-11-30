<template>
  <section class="content">
    <header class="content__title">
      <h4 class='card-title'>{{title}}</h4>
      <action url='/job/add' :actions='actions' theme="list"></action>
    </header>
    <div class="card">
      <div class="card-body">
        <pagerSearch :search="search" v-if="actions.search"/>
        <v-data-table
          :search="search"
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
            <td class="text-left">
              <span class="badge btn-light">{{ props.item.execUser }}</span>
            </td>
            <td class="text-left">{{ props.item.command }}</td>
            <td class="text-left">
              <span class="badge" :class="{'btn-info':props.item.pause,'btn-primary':!props.item.pause}">
                {{ props.item.pause?'是':'否' }}
              </span>
            </td>
            <td class="text-left">{{ props.item.redo }}</td>
            <td class="text-left">
              <span class="badge btn-dark">
                {{props.item.jobType ==  0 ? '自动':'手动'}}
              </span>
            </td>
            <td class="text-left">{{ props.item.cronExp }}</td>
            <td class="justify-center layout px-0">
              <v-icon small class="mr-2" @click="execute(props.item.jobId)">play_arrow</v-icon>
              <v-icon small class="mr-2" @click="pause(props.item.jobId)">pause</v-icon>
              <v-icon small class="mr-2" @click="edit(props.item)">edit</v-icon>
              <v-icon small class="mr-2" @click="detail(props.item.jobId)">visibility</v-icon>
              <v-icon small class="mr-2" @click="remove(props.item.jobId)">delete</v-icon>
            </td>
          </template>
          <template slot="no-data">
            <v-alert :value="true" color="error" icon="warning">
              Sorry, nothing to display here :(
            </v-alert>
          </template>
        </v-data-table>
        <pager :pager="pagination"></pager>
      </div>
    </div>
  </section>
</template>
<script type="text/ecmascript-6">
  import pager from '@/components/common/Pager'
  import pagerSearch from '@/components/common/PagerSearch'
  import action from '@/components/common/Action'

  export default {
    components: {
      pager, pagerSearch, action
    },
    data: () => ({
      actions: {
        search: true,
        print: false,
        fullscreen: false,
        download: false,
        addURL: '/job/add'//未输入表示无添加按钮
      },
      title: 'JOB LIST',
      url: '/job/view',
      headers: [
        {text: '执行器', value: 'agent_name'},
        {text: '作业名称', value: 'job_name'},
        {text: '执行身份', value: 'exec_user'},
        {text: '执行命令', value: 'command', sortable: false},
        {text: '托管状态', value: 'pause', sortable: false},
        {text: '重跑次数', value: 'redo', sortable: false},
        {text: '调度方式', value: 'job_type', sortable: false},
        {text: 'CRONEXP', value: 'cron_exp', sortable: false},
        {text: 'Actions', value: 'name', sortable: false}
      ],
      search: {},
      pageData: {},
      postData: {},
      loading: false,
      pagination: {}
    }),

    mounted () {
      this.getPageData()
    },

    methods: {
      getPageData () {
        this.loading = true
        this.$http.post(this.url, this.postData).then(resp => {
          this.pageData = resp.body
          this.pagination.page = this.pageData.pageNo
          this.pagination.pages = this.pageData.pageTotal
          this.pagination.totalItems = this.pageData.totalRecord
          this.pagination.rowsPerPage = this.pageData.pageSize
          setTimeout(() => {
            this.loading = false
          }, 1000)
        }, error => {
          console.error(error)
          this.loading = false
        })
      },

      execute (id) {
        let $this = this
        $this.$swal({
          title: '',
          text: '你确定要执行该作业吗?',
          type: 'question',
          showCancelButton: true,
          buttonsStyling: false,
          confirmButtonClass: 'btn btn-success',
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          cancelButtonClass: 'btn btn-light',
          background: 'rgba(0, 0, 0, 0.96)'
        }).then(function () {
          $this.$http.post(
            '/job/execute',
            {jobId: id}
          ).then(resp => {
            if (resp.status == 200) {
              $this.$swal({
                title: '',
                text: '提交成功,该作业正在执行中.',
                type: 'success',
                buttonsStyling: false,
                confirmButtonClass: 'btn btn-light',
                background: 'rgba(0, 0, 0, 0.96)'
              })
            } else {
              console.log(resp.message)
            }
          })
        })
      },

      detail (id) {
        this.$router.push({path: '/job/detail', params: {jobId: id}})
      },

      pause (id) {
        let $this = this
        $this.$swal({
          title: '',
          text: '你确定要将该作业置为托管状态吗?',
          type: 'question',
          showCancelButton: true,
          buttonsStyling: false,
          confirmButtonClass: 'btn btn-danger',
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          cancelButtonClass: 'btn btn-light',
          background: 'rgba(0, 0, 0, 0.96)'
        }).then(function () {
          $this.$http.post(
            '/job/pause',
            {jobId: id}
          ).then(resp => {
            if (resp.status == 200) {
              $this.$swal({
                title: '',
                text: '托管状态设置成功',
                type: 'success',
                buttonsStyling: false,
                confirmButtonClass: 'btn btn-light',
                background: 'rgba(0, 0, 0, 0.96)'
              })
            } else {
              console.log(resp.message)
            }
          })
        })
      },

      edit (id) {
        this.$router.push({path: '/job/edit', params: {jobId: id}})
      },

      remove (id) {
        let $this = this
        $this.$swal({
          title: '',
          text: '你确定要删除该作业吗?',
          type: 'warning',
          showCancelButton: true,
          buttonsStyling: false,
          confirmButtonClass: 'btn btn-danger',
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          cancelButtonClass: 'btn btn-light',
          background: 'rgba(0, 0, 0, 0.96)'
        }).then(function () {
          $this.$http.post(
            '/job/delete',
            {jobId: id}
          ).then(resp => {
            if (resp.status == 200) {
              $this.$swal({
                title: '',
                text: '删除作业成功',
                type: 'success',
                buttonsStyling: false,
                confirmButtonClass: 'btn btn-light',
                background: 'rgba(0, 0, 0, 0.96)'
              })
            } else {
              console.log(resp.message)
            }
          })
        })
      }
    },
    watch: {
      pagination: {
        deep: true,
        handler (data) {
          if (!this.loading) {
            console.log(data.page)
            if (data.descending != null) {
              this.postData.orderBy = data.sortBy
              this.postData.order = data.descending ? 'desc' : 'asc'
            }
            this.postData.pageNo = data.page
            this.postData.pageSize = data.rowsPerPage
            this.getPageData()
          }
        }
      },
      search: {
        deep: true,
        handler (data) {
          if (data.pageSize) {
            this.postData.pageSize = data.pageSize
          }
          if (data.word) {
            this.postData.search = data.word
          }
          this.getPageData()
        }
      },
      actions: {
        deep: true,
        handler (data) {
          if (data.print) {
            console.log('print...')
            data.print = false
          }
          if (data.fullscreen) {
            console.log('fullscreen...')
            data.fullscreen = false
          }
          if (data.download) {
            console.log('download...')
            data.download = false
          }
        }
      }
    },
  }
</script>

