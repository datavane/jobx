<template>
  <section class="content">
    <div class="card">
      <div class="card-body">
        <h4 class="card-title">{{title}}</h4>
        <div class="actions">
          <i class="actions__item zmdi zmdi-print" data-table-action="print"></i>
          <i class="actions__item zmdi zmdi-fullscreen" data-table-action="fullscreen"></i>
          <i class="actions__item zmdi zmdi-download" data-table-toggle="dropdown"></i>
          <i class="actions__item zmdi zmdi-plus" @click="goAdd()"></i>
        </div>
        <div class="table-responsive">
          <table id="data-table" class="table">
            <thead class="thead-default">
              <tr>
                <th v-for="h in column">{{h.title}}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="page in pager.result">
                <td v-for="col in column">{{page[col.name]}}</td>
              </tr>
            </tbody>
          </table>
          <pager v-if="pageData" :pageData="pageData" :offset="offset" @goPage="goPage" ref="pager"></pager>
        </div>
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
        title:'执行器列表',
        url: "/agent/view",
        column: [
          {title: 'Name', name: 'name'},
          {title: 'Port', name: 'port'},
          {title: 'Status', name: 'status',
            valueAs:[
                {value:0,text:'正常'},
                {value:1,text:'失联'},
                {value:2,text:'密码错误'}
            ]
          },
          {title: 'Warning', name: 'warning',
            valueAs:[
              {value:0,text:'告警'},
              {value:1,text:'不告警'}
            ]
          },
          {title: 'Proxy', name: 'proxy'}
        ],
        agentId:undefined,
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
        where:false,
        pageData:null
      }
    },
    mounted(){
      this.getPageData()
    },
    methods:{
      goAdd(){
        this.$router.push('/agent/add')
      },
      getPageData(data) {
        let $this = this
        this.$http.post(this.url, data || {}).then(resp => {
          $this.pageData = resp.body
          $this.$refs.pager.render()
        })
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
