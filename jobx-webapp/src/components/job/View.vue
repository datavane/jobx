<template>
  <section class="content">
      <header class="content__title">
          <h4 class='card-title'>{{title}}</h4>
          <action url='/job/add' :actions='actions' theme="list"></action>
      </header>
    <div class="card">
      <div class="card-body">
        <a-table :columns="columns" :dataSource="pageData.result" :scroll="{ x: 1300 }">
          <a slot="action" slot-scope="text" href="javascript:;">action</a>
        </a-table>
        <pager :pageData='pageData' @goPageNo='goPageNo'></pager>
      </div>
    </div>
  </section>
</template>
<script type="text/ecmascript-6">
  import pager from '@/components/common/Pager'
  import action from '@/components/common/Action'
  import select2 from '@/components/common/Select2'
  import BScroll from 'better-scroll'

  export default {
    components: {
      pager,action,select2
    },
    data() {
      return {
        actions:{
          filter:false
        },
        hoverIndex:0,
        title:'JOB LIST',
        url: "/job/view",
        pageData:{},
        columns: [
          {title: '执行器', key: 'agentName',dataIndex:'agentName' },
          {title: '名称', key: 'jobName',dataIndex:'jobName' },
          {title: '作业人', key: 'operateUname',dataIndex:'operateUname'},
          {title: '执行身份	', key: 'execUser',dataIndex:'execUser' },
          {title: '命令', key: 'command',dataIndex:'command' },
          {title: '暂停', key: 'pause',dataIndex:'pause'},
          {title: '重跑', key: 'redo',dataIndex:'redo'},
          {title: '调度方式', key: 'jobType',dataIndex:'jobType'},
          {title: 'CRONEXP', key: 'cronExp',dataIndex:'cronExp'},
          {
            title: 'Action',
            fixed: 'right',
            width: 100,
            scopedSlots: { customRender: 'action' },
          }
        ],
      }
    },
    mounted() {
      this.getPageData()
    },
    methods : {
     getPageData(data) {
        this.$http.post(this.url, data || {}).then(resp => {
          this.pageData = resp.body
        })
      },
      goPageNo(pageNo) {
        this.getPageData({
          pageNo: pageNo,
          pageSize: this.pageData.pageSize,
          order: this.pageData.order,
          orderBy: this.pageData.orderBy
        })
      },
      showFilder(status) {
        this.showFilder =  status
      },
      labelStyle(field,value){
        if(field === 'pause') {
          return value === 'true'?'btn-primary':'btn-success'
        }
      },
      checkFixed(type) {
        for(let col in this.column) {
          if(this.column[col].fixed === type) {
            return true
          }
        }
        return false
      },
      remove(id) {
        let $this = this
        $this.$swal({
              title: 'Are you sure?',
              text: 'You will not be able to recover this imaginary file!',
              type: 'warning',
              showCancelButton: true,
              buttonsStyling: false,
              confirmButtonClass: 'btn btn-danger',
              confirmButtonText: 'Yes, delete it!',
              cancelButtonClass: 'btn btn-light',
              background: 'rgba(0, 0, 0, 0.96)'
          }).then(function() {
            $this.$http.post(
              '/job/delete',
              {jobId:id}
            ).then(resp => {
              if(resp.status == 200) {
                $this.$swal({
                    title: 'Are you sure?',
                    text: 'You will not be able to recover this imaginary file!',
                    type: 'success',
                    buttonsStyling: false,
                    confirmButtonClass: 'btn btn-light',
                    background: 'rgba(0, 0, 0, 0.96)'
                })
              }else {
                console.log(resp.message)
              }
            })
          })

      }
    },
    filters: {
      doValue(value,field) {
        if(field === 'pause') {
          return value === 'true'?'暂停':'运行'
        }
        if(field === 'jobType') {
          return value == 0 ? '自动':'手动'
        }
        if(field === 'redo') {
          return value == 0 ? '是':'否'
        }
        if(field === 'command') {
          return value.length>80?value.substring(0,80)+'...':value
        }
        return value
      }
    }
  }
</script>
<style lang="css" scoped>
.select2-container--default .select2-selection--single {
    border-radius: 0;
    border: 0;
    background-color: transparent;
    border-bottom: 1px solid rgba(255, 255, 255, .2);
    height: 32.59px !important
}

</style>
