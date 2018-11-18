<template>
  <div>
    <h4 class='card-title'>{{title}}</h4>
    <action url='/job/add'></action>
    <div class='table-responsive'>
      <table id='data-table' class='table'>
        <thead class='thead-default'>
        <tr>
          <th v-for='h in column'>{{h.header}}
            <span class='dropdown' v-if='h.filter'>
                <i class='zmdi zmdi-cocktail zmdi-hc-fw' data-toggle='dropdown' v-if='h.filter.type=='input''></i>
                <span class='dropdown-menu select-filter-opt' x-placement='bottom-start' v-if='h.filter.type=='input''>
                 <div class='form-group select-filter-input'>
                      <input type='text' class='form-control form-control-sm' :placeholder='h.header'>
                      <i class='form-group__bar'></i>
                  </div>
                  <button class='btn btn-light btn-sm select-filter-btn'>确定</button>
                  <button class='btn btn-light btn-sm select-filter-btn'>清空</button>
                </span>
               <i class='zmdi zmdi-cocktail zmdi-hc-fw' data-toggle='dropdown' v-if='h.filter.type=='select''></i>
                <span class='dropdown-menu select-filter-opt' x-placement='bottom-start' v-if='h.filter.type=='select''>
                    <a v-for='opt in h.filter.values' class='dropdown-item'>{{opt.text}}</a>
                </span>
            </span>
          </th>
        </tr>
        </thead>
        <tbody>
        <tr v-for='page in pager.result'>
          <td v-for='col in column'>{{page[col.data]}}</td>
        </tr>
        </tbody>
      </table>
       <pager :pageData='pageData' :offset='offset' @goPage='goPage'></pager>
    </div>
  </div>
</template>

<script type='text/ecmascript-6'>
  import pager from '@/components/common/Pager'
  import action from '@/components/common/Action'
  export default {
    props: ['title','url','column'],
    data() {
      return {
        pageData: {},
        offset: 3, //前后取多少页
        preNo: [],
        nextNo: []
      }
    },
    mounted() {
      this.getPager()
    },

    methods: {
      getPager(data) {
        this.$http.post(this.url, data || {}).then(resp => {
          this.pageData = resp.body
        });
      },
      goPage(pageNo) {
        this.getPager({
          pageNo: pageNo,
          pageSize: this.pageData.pageSize,
          order: this.pageData.order,
          orderBy: this.pageData.orderBy
        })
      }
    }
  }
</script>

<style  lang='scss' scoped>
  .dataTables_wrapper{
    margin-bottom: 20px;
  }
  .select-filter-opt{
    position: absolute;
    transform: translate3d(5px, 20px, 0px)!important;
    top: 0px;
    left: 0px;
    will-change: transform;
  }
  .select-filter-input{
    padding-left: 10px;
    padding-right:10px;
    .form-group__bar {
      padding-left: 10px;
      padding-right:10px;
    }
  }
  .select-filter-btn{
    margin-left: 15px;
  }
</style>






