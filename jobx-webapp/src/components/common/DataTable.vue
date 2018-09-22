<template>
  <div>
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
          <th v-for="h in column">{{h.header}}
            <span class="dropdown" v-if="h.filter">
                <i class="zmdi zmdi-cocktail zmdi-hc-fw" data-toggle="dropdown" v-if="h.filter.type=='input'"></i>
                <span class="dropdown-menu select-filter-opt" x-placement="bottom-start" v-if="h.filter.type=='input'">
                 <div class="form-group select-filter-input">
                      <input type="text" class="form-control form-control-sm" :placeholder="h.header">
                      <i class="form-group__bar"></i>
                  </div>
                  <button class="btn btn-light btn-sm select-filter-btn">确定</button>
                  <button class="btn btn-light btn-sm select-filter-btn">清空</button>
                </span>
               <i class="zmdi zmdi-cocktail zmdi-hc-fw" data-toggle="dropdown" v-if="h.filter.type=='select'"></i>
                <span class="dropdown-menu select-filter-opt" x-placement="bottom-start" v-if="h.filter.type=='select'">
                    <a v-for="opt in h.filter.values" class="dropdown-item">{{opt.text}}</a>
                </span>
            </span>
          </th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="page in pager.result">
          <td v-for="col in column">{{page[col.data]}}</td>
        </tr>
        </tbody>
      </table>
      <nav>
        <ul class="pagination justify-content-center">
          <li class="page-item pagination-first"
              :class="pager.pageNo===1?'disabled':''">
            <a class="page-link" @click="gotoPage(1)"></a>
          </li>

          <li class="page-item pagination-prev"
              :class="pager.pageNo===1?'disabled':''">
            <a class="page-link"
               @click="gotoPage(pager.pageNo-1)"></a>
          </li>

          <li class="page-item" v-for="index in preNo">
            <a class="page-link"
               @click="gotoPage(index)">{{index}}
            </a>
          </li>

          <li class="page-item active">
            <a class="page-link">{{pager.pageNo}}</a>
          </li>

          <li class="page-item" v-for="index in nextNo">
            <a class="page-link"
               @click="gotoPage(index)">{{index}}
            </a>
          </li>

          <li class="page-item pagination-next"
              :class="pager.pageNo===pager.pageTotal?'disabled':''">
            <a class="page-link"
               @click="gotoPage(pager.pageNo-1)">
            </a>
          </li>

          <li class="page-item pagination-last"
              :class="pager.pageNo===pager.pageTotal?'disabled':''">
            <a class="page-link"
               @click="gotoPage(pager.pageTotal)">
            </a>
          </li>
        </ul>
      </nav>
    </div>
  </div>
</template>

<script type="text/ecmascript-6">
  export default {
    props: ['title','url','column'],
    data() {
      return {
        pager: {},
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
          this.pager = resp.body
          this.preNo = []
          let preStart = 1
          if (this.pager.pageNo > 1) {
            if (this.pager.pageNo - this.offset > 1) {
              preStart = this.pager.pageNo - this.offset
              if (this.pager.pageTotal - this.pager.pageNo < this.offset) {
                preStart -=
                  this.offset - (this.pager.pageTotal - this.pager.pageNo)
              }
            }
            for (let i = preStart; i < this.pager.pageNo; i++) {
              this.preNo.push(i)
            }
          }
          this.nextNo = []
          if (this.pager.pageNo < this.pager.pageTotal) {
            let nextLen = this.offset * 2 - this.preNo.length
            let nextEnd =
              this.pager.pageNo + nextLen > this.pager.pageTotal
                ? this.pager.pageTotal
                : this.pager.pageNo + nextLen
            for (let i = this.pager.pageNo + 1; i <= nextEnd; i++) {
              this.nextNo.push(i)
            }
          }
        });
      },
      gotoPage(pageNo) {
        this.getPager({
          pageNo: pageNo,
          pageSize: this.pager.pageSize,
          order: this.pager.order,
          orderBy: this.pager.orderBy
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






