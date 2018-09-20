<template>
  <section class="content">
    <div class="card">
      <div class="card-body">
        <h4 class="card-title">Basic example</h4>
        <div class="actions">
          <i class="actions__item zmdi zmdi-search" @click="where=!where"></i>
          <i class="actions__item zmdi zmdi-plus" @click="goAdd()"></i>
          <div class="dropdown actions__item">
            <i data-toggle="dropdown" class="zmdi zmdi-more-vert"></i>
            <div class="dropdown-menu dropdown-menu-right">
              <a href="" class="dropdown-item">Refresh</a>
              <a href="" class="dropdown-item">Manage Widgets</a>
              <a href="" class="dropdown-item">Settings</a>
            </div>
          </div>
        </div>
        <div class="select-wrap" v-if="where">
          <div class="select-box">
            <label>执行器</label>
            <div><select2 :options="agents" :selected="agentId" v-model="agentId"></select2></div>
          </div>
          <div class="select-box">
            <label>作业类型</label>
            <div><select2 :options="jobTypes" :selected="jobType" v-model="jobType"></select2></div>
          </div>
          <div class="select-box">
            <label>是否重跑</label>
            <div><select2 :options="reRuns" :selected="rerun" v-model="rerun"></select2></div>
          </div>
        </div>
        <dataTable :url="url" :column="column"></dataTable>
      </div>
    </div>
  </section>
</template>
<script type="text/ecmascript-6">
  import dataTable from "@/components/common/DataTable"
  import select2 from '@/components/common/Select2'

  export default {
    components: {
      dataTable,
      select2
    },
    data() {
      return {
        url: "/agent/view.do",
        column: [
          {header: 'Host', data: 'host'},
          {header: 'Name', data: 'name'},
          {header: 'Port', data: 'port'},
          {header: 'Status', data: 'status'},
          {header: 'Warning', data: 'warning'},
          {header: 'Proxy', data: 'proxy'}
        ],
        agentId:undefined,
        agents:[],
        jobTypes:[
          { id:0, text:'单一' },
          { id:1, text:'流程' }
        ],
        reRuns:[
          { id:0, text:'否' },
          { id:1, text:'是' }
        ],
        jobType:undefined,
        rerun:undefined,
        where:false
      }
    },
    mounted(){
      this.getAgent()
    },
    methods:{
      getAgent() {
        this.$http.post('/agent/all.do', {}).then(response => {
          if (response.body) {
            response.body.forEach((agent)=>{
              this.agents.push({
                id:agent.agentId,
                text:agent.name
              })
            })
          }
        }, error => {
          console.log(error)
        })
      },
      goAdd(){
        this.$router.push('/agent/add')
      }
    }

  }
</script>
<style lang="scss" scoped>
  .select-wrap{
    overflow: hidden;
    margin-right: -20px;
    .select-box{
      float: left;
      width: calc(33% - 20px);
      margin-right: 20px;
      padding-bottom: 15px;
      label{
        float: left;
        margin-right: 10px;
      }
      div{
        float: left;
      }
    }
  }

</style>
