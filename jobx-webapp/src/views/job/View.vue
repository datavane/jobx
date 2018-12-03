<template>
  <section class="content">
    <header class="content__title">
      <h4 class='card-title'>{{title}}</h4>
      <action url='/job/add' :actions='actions' theme="list"></action>
    </header>
    <div class="card">
      <div class="card-body">
        <div class="pager-search">
          <div class="data-table_option">
            <button class="btn btn-light btn--icon-text" @click="refresh"><i class="zmdi zmdi-refresh"></i>刷新</button>
            <button class="btn btn--icon-text"
                    :class="{'btn-light cursor':selected.length>0,'btn-dark no_cursor':selected.length == 0}"
                    @click="pause(false)"><i class="zmdi zmdi-play-circle"></i>启用
            </button>
            <button class="btn btn--icon-text"
                    :class="{'btn-light cursor':selected.length>0,'btn-dark no_cursor':selected.length == 0}"
                    @click="pause(true)"><i class="zmdi zmdi-pause-circle"></i>托管
            </button>
          </div>
          <div class="dataTables_length" id="data-table_length">
            <label>
              <select name="data-table_length" aria-controls="data-table" class="" v-model="search.pageSize">
                <option v-for="index in [15,30,50,100,-1]" :value="index">{{index == -1 ? 'All':index}} Rows</option>
              </select>
            </label>
          </div>
          <div id="data-table_filter" class="dataTables_filter">
            <label>Search:<input type="search" class="" v-model="search.word" placeholder="Search"
                                 aria-controls="data-table"></label>
          </div>
        </div>
        <v-data-table
          :search="search"
          :headers="headers"
          :loading="loading"
          :pagination.sync="pagination"
          :total-items="pagination.totalItems"
          v-model="selected"
          select-all
          item-key="jobId"
          :items="pageData.result"
          hide-actions>
          <template slot="headerCell" slot-scope="props">
            {{ props.header.text }}
            <div class="dropdown actions__item hidden-sm-down" v-if="props.header.value ==='agent_name'">
              <i class="zmdi zmdi-filter-list" data-toggle="dropdown" aria-expanded="false"></i>
              <div class="dropdown-menu dropdown-menu-right dropdown-menu--active" x-placement="bottom-end" @click.stop>
                <div class="dropdown-item">
                  <label class="custom-control custom-radio" @click.stop>
                    <input name="issue-severity" type="radio" class="custom-control-input" checked="" @click.stop>
                    <span class="custom-control-indicator" @click.stop></span>
                    <span class="custom-control-description" @click.stop>托管</span>
                  </label>
                </div>
                <div class="dropdown-item">
                  <label class="custom-control custom-radio" @click.stop>
                    <input name="issue-severity" type="radio" class="custom-control-input" checked="" @click.stop>
                    <span class="custom-control-indicator" @click.stop></span>
                    <span class="custom-control-description" @click.stop>就绪</span>
                  </label>
                </div>
              </div>
            </div>

            <div class="dropdown actions__item hidden-sm-down" data-toggle="tooltip"
                 v-if="props.header.value ==='paush'">
              <i class="zmdi zmdi-filter-list" data-toggle="dropdown" aria-expanded="false"></i>
              <div class="dropdown-menu dropdown-menu-right dropdown-menu--active" x-placement="bottom-end">
                <div class="dropdown-item">
                  <label class="custom-control custom-radio">
                    <input name="issue-severity" type="radio" class="custom-control-input" checked="">
                    <span class="custom-control-indicator"></span>
                    <span class="custom-control-description">托管</span>
                  </label>
                </div>
                <div class="dropdown-item">
                  <label class="custom-control custom-radio">
                    <input name="issue-severity" type="radio" class="custom-control-input" checked="">
                    <span class="custom-control-indicator"></span>
                    <span class="custom-control-description">就绪</span>
                  </label>
                </div>
              </div>
            </div>
          </template>
          <v-progress-linear slot="progress" color="white" style="height:1px" indeterminate></v-progress-linear>
          <template slot="items" slot-scope="props">
            <td>
              <v-checkbox
                v-model="props.selected"
                primary
                hide-details
              ></v-checkbox>
            </td>
            <td class="text-left"><i class="zmdi zmdi-windows platform"></i>{{props.item.agentName }}</td>
            <td class="text-left">{{ props.item.jobName }}</td>
            <td class="text-left">{{ props.item.command }}</td>
            <td class="text-left">
              <span class="badge" :class="{'btn-info':props.item.pause,'btn-primary':!props.item.pause}">
                {{ props.item.pause?'就绪':'托管' }}
              </span>
            </td>
            <td class="text-left">{{ props.item.cronExp }}</td>
            <td>
              <div class="dropdown actions__item">
                <i data-toggle="dropdown" class="zmdi zmdi-more-vert" aria-expanded="false"></i>
                <div class="dropdown-menu dropdown-menu-right" x-placement="bottom-end"
                     style="position: absolute; transform: translate3d(35px, 28px, 0px); top: 0px; left: 0px; will-change: transform;">
                  <a class="dropdown-item">Execute</a>
                  <a class="dropdown-item">Edit</a>
                  <a class="dropdown-item">Detail</a>
                  <a class="dropdown-item">Pause</a>
                  <a class="dropdown-item">Copy</a>
                </div>
              </div>
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
  import pager from '@/components/Pager'
  import pagerSearch from '@/components/PagerSearch'
  import action from '@/components/Action'

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
        {text: '执行命令', value: 'command'},
        {text: '托管状态', value: 'pause', sortable: false},
        {text: 'CRONEXP', value: 'cron_exp', sortable: false},
        {text: '操作', value: 'name', sortable: false}
      ],
      search: {},
      pageData: {},
      postData: {},
      loading: false,
      pagination: {},
      selected: [],
    }),

    mounted() {
      this.getPageData()
    },

    methods: {
      getPageData() {
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

      execute(id) {
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

      detail(id) {
        this.$router.push({path: '/job/detail', params: {jobId: id}})
      },

      pause(id) {
        if (this.selected.length == 0) return
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

      edit(id) {
        this.$router.push({path: '/job/edit', params: {jobId: id}})
      },

      remove(id) {
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
      },

      refresh() {
        this.getPageData()
      }

    },
    watch: {
      pagination: {
        deep: true,
        handler(data) {
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
        handler(data) {
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
        handler(data) {
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
      },
      selected: {
        deep: true,
        handler(data) {
          console.log(data)
        }
      }
    },
  }
</script>
<style lang="stylus">


  .data-table_option
    float left
    position relative
    margin-right 10px
    .btn
      margin-right 2px
    .cursor
      cursor pointer
    .no_cursor
      cursor default

  .table-option-icon
    margin-right 10px
    .zmdi
      font-size 15px
      height 48px
      line-height 48px
      padding-left 10px
      color rgba(255, 255, 255, 0.85)

  .platform
    color rgba(255, 255, 255, 0.55)
    margin-right 10px

</style>

