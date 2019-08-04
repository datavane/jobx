<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <a-row >
          <div :class="advanced ? null: 'fold'">
            <a-col :md="12" :sm="24" >
              <a-form-item
                      label="appName"
                      :labelCol="{span: 4}"
                      :wrapperCol="{span: 18, offset: 2}">
                <a-input v-model="queryParam.jobName" placeholder="appName" />
              </a-form-item>
            </a-col>

            <a-col :md="12" :sm="24" >
              <a-form-item
                      label="执行器"
                      :labelCol="{span: 4}"
                      :wrapperCol="{span: 18, offset: 2}">
                <a-select v-model="queryParam.agentId" placeholder="请选择" default-value="0">
                  <a-select-option
                          v-for="(item) in where.agents"
                          :key="item.agentId"
                          :value="item.agentId">{{ item.agentName }}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>

            <template v-if="advanced">
              <a-col :md="12" :sm="24" >
                <a-form-item
                  label="作业类型"
                  :labelCol="{span: 4}"
                  :wrapperCol="{span: 18, offset: 2}">
                  <a-select v-model="queryParam.jobType" placeholder="请选择" default-value="0">
                    <a-select-option value="1">简单作业</a-select-option>
                    <a-select-option value="2">工作流</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :md="12" :sm="24" >
                <a-form-item
                 label="托管状态"
                 :labelCol="{span: 4}"
                 :wrapperCol="{span: 18, offset: 2}">
                  <a-select v-model="queryParam.pause" placeholder="请选择" default-value="0">
                    <a-select-option value="1">托管</a-select-option>
                    <a-select-option value="2">就绪</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
            </template>
          </div>

          <span style="float: right; margin-top: 3px;" :style="advanced && { float: 'right', overflow: 'hidden' } || {} ">
              <a-button type="primary" shape="circle" icon="search" @click="httpPageData"></a-button>
              <a-button type="primary" style="margin-left: 8px;margin-right: 8px" shape="circle" icon="rest" @click="handleRest()"></a-button>
              <a-button type="primary" shape="circle" icon="plus" @click="handleCreate()"></a-button>
              <a @click="advanced = !advanced" style="margin-left: 8px">
                {{ advanced ? '收起' : '展开' }}
                <a-icon :type="advanced ? 'up' : 'down'"/>
              </a>
            </span>

        </a-row>
      </a-form>
    </div>

    <a-table
      :columns="pageData.columns"
      :rowKey="record => record.jobId"
      :dataSource="pageData.data"
      :pagination="pageData.pagination"
      @change="handleTableChange"
      :loading="pageData.loading">
      <span slot="jobType" slot-scope="row">
        <a-tag color="green" v-if="row === 1">简单作业</a-tag>
        <a-tag color="cyan" v-else>工作流</a-tag>
      </span>
      <span slot="pause" slot-scope="row">
        <a-badge status="success" v-if="!row || row === 1" text="就绪"/>
        <a-badge status="error" v-else text="托管"/>
      </span>
    </a-table>

  </a-card>

</template>
<script>
export default {
  name: 'JobView',
  data () {
    return {
      // 高级搜索 展开/关闭
      advanced: false,
      queryParam: {},
      pageData: {
        data: [],
        pagination: {
          hideOnSinglePage: false, // 只有一页隐藏页码条
          defaultCurrent: 1, // 默认取第一页数据
          defaultPageSize: 10, // 默认每页显示10条
          showSizeChanger: true, // 是否可以改变 pageSize
          pageSizeOptions: ['10', '20', '30', '50', '100'],
          showSizeChange: function (current, pageSize) {
            console.log(pageSize)
          }
        },
        loading: false,
        columns: [
          {
            title: '执行器',
            dataIndex: 'agentName',
            sorter: true,
            width: '10%'
          },
          {
            title: 'AppName',
            dataIndex: 'jobName',
            sorter: true,
            width: '10%'
          },
          {
            title: '执行命令',
            dataIndex: 'command',
            width: '20%'
          },
          {
            title: '作业类型',
            dataIndex: 'jobType',
            width: '20%',
            scopedSlots: { customRender: 'jobType' }
          },
          {
            title: '托管',
            dataIndex: 'pause',
            width: '10%',
            scopedSlots: { customRender: 'pause' }
          },
          {
            title: '表达式',
            dataIndex: 'cronExp',
            width: '20%'
          }
        ]
      },
      where: {
        agents: [],
        jobTypes: [
          { id: 1, name: '简单作业' },
          { id: 2, name: '工作流' }
        ],
        jobStatus: []
      }
    }
  },
  mounted () {
    this.httpGetAgent()
    this.httpPageData()
  },
  methods: {
    httpPageData () {
      this.$post('/job/view', {
        ...this.queryParam
      }).then((resp) => {
        const pager = { ...this.pageData.pagination }
        pager.total = resp.body.totalRecord||0
        this.pageData.pagination = pager
        this.pageData.loading = false
        this.pageData.data = resp.body.result
      })
    },

    httpGetAgent () {
      this.$post('/agent/all', {
      }).then((resp) => {
        this.where.agents = resp.body
      })
    },
    handleCreate () {
      this.$router.push({ name: 'JobAdd' })
      console.log('create...')
    },
    handleTableChange (pager, filters, sorter) {
      this.queryParam.pageSize = pager.pageSize
      this.queryParam.pageNo = pager.current
      const pagination = { ...this.pageData.pagination }
      pagination.current = pager.current
      this.pageData.pagination = pagination
      this.httpPageData()
    },
    handleRest () {
      this.queryParam = {}
      this.queryParam.pageNo = 1
      this.queryParam.pageSize = this.pageData.pagination.pageSize
      this.httpPageData()
    }
  }
}
</script>
