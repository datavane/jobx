<template>
  <div class="app-container">

    <div class="filter-container" style="padding:10px">
      <el-input :placeholder="$t('job.jobName')" v-model="queryData.jobName" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter"/>
      <el-select v-model="queryData.agentId" :placeholder="$t('agent.agentName')" clearable class="filter-item" style="width: 150px">
        <el-option v-for="item in agents" :key="item.agentId" :label="item.agentName" :value="item.agentId"/>
      </el-select>
      <el-select v-model="queryData.jobType" :placeholder="$t('job.jobType')" clearable style="width: 110px" class="filter-item">
        <el-option v-for="item in jobTypes" :key="item.id" :label="item.name" :value="item.id"/>
      </el-select>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">{{ $t('action.search') }}</el-button>
      <el-button class="filter-item" type="primary" icon="el-icon-plus" @click="handleAdd">{{ $t('action.add') }}</el-button>&nbsp;
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">{{ $t('action.export') }}</el-button>
    </div>

    <div class="table-container" style="padding: 10px">
      <el-table v-loading="loading"
                :data="list"
                fit
                stripe
                highlight-current-row
                style="width: 100%"
                @selection-change="handleSelectionChange"
                @sort-change="handleSortChange">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column :label="$t('agent.agentName')" prop="agentName" sortable="custom" align="left" width="150px">
          <template slot-scope="scope">
            <span>{{ scope.row.agentName }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('job.jobName')" width="180px" align="left">
          <template slot-scope="scope">
            <span>{{ scope.row.jobName}}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('job.command')" min-width="250px">
          <template slot-scope="scope">
           <span style="white-space:nowrap;text-overflow:ellipsis;">{{ scope.row.command}}</span >
          </template>
        </el-table-column>
        <el-table-column :label="$t('job.jobType')" min-width="100px" align="center">
          <template slot-scope="scope">
            <el-tag type="primary" v-if="scope.row.jobType == 0">{{$t('job.simpleJob')}}</el-tag>
            <el-tag type="info" v-else>{{$t('job.workFlow')}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('job.pause')" width="100px" align="center">
          <template slot-scope="scope">
            <el-tag type="warning" v-if="scope.row.jobType == 0">{{$t('job.pauseJob')}}</el-tag>
            <el-tag type="success" v-else>{{$t('job.readyJob')}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('job.cronExp')" min-width="100px">
          <template slot-scope="scope">
            <span>{{ scope.row.cronExp}}</span>
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('action.name')"
          align="center"
          width="230"
          class-name="small-padding fixed-width"
          fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="success" @click="handleExecute(scope.row.jobId)" :title="$t('action.execute')">{{$t('action.execute')}}</el-button>
            <el-button size="mini" type="primary" @click="handleDetail(scope.row.jobId)" :title="$t('action.detail')">{{$t('action.detail')}}</el-button>
            <el-button size="mini" type="info"    @click="handleDelete(scope.row.jobId)" title="$t('action.edit')">{{$t('action.edit')}}</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <pagination v-show="total>0" :total="total" :page.sync="queryData.pageNo" :limit.sync="queryData.pageSize" @pagination="getList" />

  </div>
</template>

<script>
  import { getAgent } from '@/api/agent'
  import { view } from '@/api/job'
  import Pagination from '@/components/Pagination'

  export default {
    components: { Pagination },
    data() {
      return {
        list: null,
        total: 0,
        loading: true,
        downloadLoading: false,
        queryData: {
          pageNo: 1,
          pageSize: 20,
          sort: '+id'
        },
        jobId:[],
        agents:[],
        jobTypes:[
          {id:1,name:this.$t('job.simpleJob')},
          {id:2,name:this.$t('job.workFlow')}
        ]
      }
    },
    created() {
      this.httpGetAgent()
      this.httpGetList()
    },
    methods: {

      httpGetAgent() {
        getAgent().then(response => {
          this.agents = response.body
        })
      },
      
      httpGetList() {
        this.loading = true
        view(this.queryData).then(response => {
          this.list = response.body.result
          this.total = response.body.totalRecord
          setTimeout(() => {
            this.loading = false
          }, 500)
        })
      },

      handleSortChange(data) {
        const { prop, order } = data
        if (prop === 'id') {
          this.handleSortByID(order)
        }
      },

      handleSortByID(order) {
        if (order === 'ascending') {
          this.queryData.sort = '+id'
        } else {
          this.queryData.sort = '-id'
        }
        this.handleFilter()
      },

      handleFilter() {
        this.queryData.pageNo=1
        this.getList()
      },

      handleAdd() {
        this.$router.push("/job/add")
      },

      handleDetail(id) {
        this.$router.push({path:"/job/detail",params:{jobId:id}})
      },

      handleDelete(){

      },

      handleExecute() {
        this.$confirm('立即执行该作业,是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$notify({
            title: '成功',
            message: '该作业已经开始调度执行中',
            type: 'success',
            duration: 2000
          })
        }).catch(() => {
        });
      },

      handleSelectionChange(item){
        this.jobId = []
        item.forEach(x=>{
          this.jobId.push(x.jobId)
        })
      },

      handleFormatJson(filterVal, jsonData) {
        return jsonData.map(v => filterVal.map(j => {
          if (j === 'timestamp') {
            return parseTime(v[j])
          } else {
            return v[j]
          }
        }))
      }

    }
  }
</script>
