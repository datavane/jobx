<template>
  <div class="table-responsive">
    <div id="data-table_wrapper" class="dataTables_wrapper no-footer">
      <div class="dataTables_length" id="data-table_length">
        <label>Show
          <select name="data-table_length" aria-controls="data-table" class="">
            <option value="15">15 Rows</option>
            <option value="30">30 Rows</option>
            <option value="45">45 Rows</option>
            <option value="-1">Everything</option>
          </select>
        entries</label>
      </div>
      <div id="data-table_filter" class="dataTables_filter">
        <label>Search:<input type="search" class=""  placeholder="Search for records..." aria-controls="data-table"></label>
      </div>
    </div>

    <table id="data-table" class="table">
      <thead>
      <tr>
        <th v-for="h in column">{{h.header}}</th>
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
</template>

<script type="text/ecmascript-6">
  export default {
    props: ["url", "column"],
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

<style scoped>
  .dataTables_wrapper{
    margin-bottom: 20px;
  }
</style>






