<template>
  <div class="table-responsive">
    <mu-paper :z-depth="1">
      <mu-data-table stripe :loading="loading" :columns="columns" :sort.sync="sort" @sort-change="sortHandle" :data="pager.data">
        <!--
        <template slot="expand" slot-scope="prop">
          <div style="padding: 24px;" >{{prop.row.command}}</div>
        </template>
        -->
      </mu-data-table>
    </mu-paper>
    <mu-flex justify-content="center" class="pagination">
      <mu-pagination raised circle :total="pager.totalRecord" :current.sync="pager.pageNo" :size="pager.pageSize" :count="pager.pageTotal" @change="gotoPage"></mu-pagination>
    </mu-flex>
  </div>
</template>

<script type="text/ecmascript-6">
  export default {
    props: ["url", "columns","expand"],
    data() {
      return {
        pager: {},
        loading:false,
        sort: {
          name: '',
          order: 'asc'
        }
      }
    },
    mounted() {
      this.getPager()
    },

    methods: {
      sortHandle ({name, order}) {
        this.pager.data =  this.pager.data.sort((a, b) => order === 'asc' ? a[name] - b[name] : b[name] - a[name])
      },
      getPager(data) {
        this.$http.post(this.url, data || {}).then(resp => {
          this.pager = resp.body
          this.current = this.pager.pageNo
          if(this.pager.data) {
              this.columns.forEach( column=> {
                if(column.filter){
                  this.pager.data.forEach(obj => {
                    if(column.filter === "boolean") {
                      let val = obj[column.name]
                      if(val == "true"
                          ||val == "TRUE"
                          ||val == true
                          ||val == "1"
                          ||val == 1
                          ||val == "YES"
                          ||val == "yes"
                          ||val == "OK"
                          ||val == "ok" ) {
                        obj[column.name] = "是"
                      }
                      obj[column.name] = "否"
                    }
                    if(typeof(column.filter) ===  'object') {
                       obj[column.name] = column.filter[obj[column.name]]
                    }
                    if (typeof(column.filter) ===  'function') {
                      obj[column.name] = column.filter(obj[column.name])
                    }
                  })
                }
              })
          }
        })
      },
      gotoPage(pageNo) {
        this.getPager({
          pageNo: pageNo,
          pageSize: this.pager.pageSize,
          order: this.sort.name,
          orderBy: this.sort.order
        })

      }
    }
  }
</script>

<style lang="scss">
.pagination{
  margin-top: 20px !important;
}
.mu-paper{
  background-color:rgba(0, 0, 0, 0) !important;
  .mu-table{
      background-color:rgba(0, 0, 0, 0)  !important;
      th {
        color:rgba(255,255,255,.54)!important;
        padding: 1rem 1.5rem;
        vertical-align: top;
        font-weight:600;
      }
      td{
        border-top: 1px solid rgba(255, 255, 255, .125);
        padding: 1rem 1.5rem;
        vertical-align: top;
      }
      tr{
          color:rgba(255,255,255,.87)!important;
      }
  }
}
</style>






