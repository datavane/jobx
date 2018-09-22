<template>
  <section class="content">
    <div class="card">
      <div class="card-body">
        <dataTable title="Basic example" :url="url" :column="column"></dataTable>
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
          {header: 'Host', data: 'host',filter:{type:'input'}},
          {header: 'Name', data: 'name',filter:{type:'input'}},
          {header: 'Port', data: 'port',filter:{type:'input'}},
          {header: 'Status', data: 'status',
            filter:{
              type:'select',
              values:[
                {id:0,text:'正常'},
                {id:1,text:'失联'},
                {id:2,text:'密码错误'}
              ]
            }
          },
          {header: 'Warning', data: 'warning',
            filter:{
              type:'select',
              values:[
                {id:0,text:'告警'},
                {id:1,text:'不告警'}
              ]
            }
          },
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
            this.agents = response.body
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
