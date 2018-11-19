<template>
  <section class="content">
    <div class="card">
      <div class="card-body">
        <h4 class='card-title'>{{title}}</h4>
        <action url='/job/add' :actions='actions' ></action>
        <div class="data-table">
          <div class='data-table-item table-fixed-left'>
            <table class='table'>
                <thead>
                  <tr v-if="checkFixed('left') && actions.filter" class="table-inverse">
                    <th v-for='h in column' :class="{'filter-item': ignoreFilter.indexOf(h.name)==-1}" v-if="h.fixed == 'left'" >
                      <input v-if="ignoreFilter.indexOf(h.name) == -1" type="text" class="input-basic" :placeholder="h.title">
                    </th>
                  </tr>
                  <tr>
                    <th v-for='h in column' v-if="h.fixed == 'left'">{{h.title}}</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for='page,index in pageData.result' :class="{'line-hover':index == hoverIndex}" @mouseover='hoverIndex=index'>
                    <td v-for='col in column' v-if="col.fixed == 'left'">
                      {{page[col.name]|doValue(col.name)}}
                    </td>
                  </tr>
                </tbody>
            </table>
          </div>
          <div class='data-table-item table-fixed-center'>
            <table id='data-table' class='table'>
              <thead>
                <tr v-if="actions.filter" class="table-inverse">
                  <th v-for='h in column' :class="{'filter-item': ignoreFilter.indexOf(h.name)==-1}" v-if="!h.fixed">
                    <input v-if="ignoreFilter.indexOf(h.name) == -1" type="text" class="input-basic" :placeholder="h.title">
                  </th>
                </tr>
                <tr>
                  <th v-for='h in column' v-if="!h.fixed">{{h.title}}</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for='page,index in pageData.result' :class="{'line-hover':index == hoverIndex}" @mouseover='hoverIndex=index'>
                  <td v-for='col in column' v-if="!col.fixed">
                    <span v-if="col.name==='jobType'" class="badge btn-dark">
                        {{page[col.name]|doValue(col.name)}}
                    </span>
                    <span v-else-if="col.name==='pause'" class="badge" :class="labelStyle(col.name,page[col.name])" >
                      {{page[col.name]|doValue(col.name)}}
                    </span>
                    <span v-else-if="col.name==='execUser'" class="badge btn-light">{{page[col.name]}}</span>
                    <span v-else-if="col.name==='redo'">
                        {{page[col.name]|doValue(col.name)}}
                    </span>
                    <span v-else :title="col.name === 'command'?page[col.name]:''" class="command">
                      {{page[col.name]|doValue(col.name)}}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="data-table-item table-fixed-right">
            <table class='table'>
              <thead>
                  <tr v-if="actions.filter" class="table-inverse">
                    <th v-for='h in column' :class="{'filter-item': ignoreFilter.indexOf(h.name)==-1}" v-if="h.fixed == 'right'" >
                      <input v-if="ignoreFilter.indexOf(h.name) == -1" type="text" class="input-basic" :placeholder="h.title">
                    </th>
                    <th>
                      <button class="btn btn-light table-search"><i class="zmdi zmdi-search"></i> Search</button>
                    </th>
                  </tr>
                  <tr>
                    <th v-for='h in column' v-if="h.fixed == 'right'">{{h.title}}</th>
                    <th>操作</th>
                  </tr>
              </thead>
              <tbody>
                <tr v-for='page,index in pageData.result' :class="{'line-hover':index == hoverIndex}" @mouseover='hoverIndex=index'>
                  <td v-for='col in column' v-if="col.fixed == 'right'">
                    {{page[col.name]|doValue(col.name)}}
                  </td>
                  <td>
                    <i class="zmdi zmdi-eye"></i>&nbsp;&nbsp;
                    <i class="zmdi zmdi-edit"></i>&nbsp;&nbsp;
                    <i class="zmdi zmdi-play"></i>&nbsp;&nbsp;
                    <i class="zmdi zmdi-delete"></i>&nbsp;&nbsp;
                    <i class="zmdi zmdi-copy"></i>&nbsp;&nbsp;
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        <pager :pageData='pageData' :offset='offset' @goPageNo='goPageNo'></pager>
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
        title:'作业列表',
        url: "/job/view",
        pageData:{},
        offset:5,
        column: [
          {title: '执行器', name: 'agentName',fixed: 'left' },
          {title: '名称', name: 'jobName',fixed: 'left' },
          {title: '作业人', name: 'operateUname',fixed: 'left' },
          {title: '执行身份	', name: 'execUser' },
          {title: '命令', name: 'command' },
          {title: '暂停', name: 'pause'},
          {title: '重跑', name: 'redo'},
          {title: '调度方式', name: 'jobType'},
          {title: 'CRONEXP', name: 'cronExp'}
        ],
        ignoreFilter:['cronExp']
      }
    },
    mounted() {
      this.getPageData()
    },
    methods : {
     getPageData(data) {
        this.$http.post(this.url, data || {}).then(resp => {
          this.pageData = resp.body
          this.$nextTick(()=>{
            let wrapper = document.querySelector('.table-fixed-center')
            let scroll = new BScroll(wrapper,{
              scrollX: true,
              bounce: {
                left: false,
                right: false
              },
              mouseWheel: {
                speed: 20,
                invert: false,
                easeTime: 300
              }
            })
          })
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
<style lang="scss" scoped>
.select2-container--default .select2-selection--single {
    border-radius: 0;
    border: 0;
    background-color: transparent;
    border-bottom: 1px solid rgba(255, 255, 255, .2);
    height: 32.59px !important
}
.data-table {
  clear:both;
  overflow:hidden;
  display:flex;
  width:100%;
  .table thead >tr >th {
    min-width: 100px;
  }
  .data-table-item {
    display:inline;
  }
  .table-fixed-left {
    //min-width:30%
  }
  .table-fixed-center {
    cursor: pointer;
    overflow:hidden;
     .command {
      display: inline-block;
      width: max-content
    }
  }
  .table-fixed-right {
    min-width:180px;
  }
  .line-hover {
      background-color: rgba(255, 255, 255, .04)
  }
  .table-search {
    cursor: pointer;
    padding: .55rem 1rem !important;
  }
  .zmdi {
    cursor: pointer;
  }
 
}



</style>
