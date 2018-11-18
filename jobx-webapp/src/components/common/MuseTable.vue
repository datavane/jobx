<template>
  <div id="app">
    <h4 class="card-title">{{title}}</h4>
    <div class="actions">
      <i class="actions__item zmdi zmdi-print" data-table-action="print"></i>
      <i class="actions__item zmdi zmdi-fullscreen" data-table-action="fullscreen"></i>
      <i class="actions__item zmdi zmdi-download" data-table-toggle="dropdown"></i>
      <i class="actions__item zmdi zmdi-plus" @click="goAdd()"></i>
    </div>
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
      <mu-paper :z-depth="1">
        <mu-data-table
          :loading="loading"
          :columns="columns"
          :sort.sync="sort"
          @sort-change="sortHandle"
          :data="pager.result">
        </mu-data-table>
      </mu-paper>
      <mu-flex justify-content="center" class="pagination">
        <mu-pagination raised circle :total="pager.totalRecord" :current.sync="pager.pageNo" :size="pager.pageSize" :count="pager.pageTotal" @change="gotoPage"></mu-pagination>
      </mu-flex>
    </div>
  </div>
</template>

<script type="text/ecmascript-6">
  export default {
    props: ['title','url','columns','expand'],
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
                if(column.handler) {

                  alert("5555")
                  this.pager.data.forEach(obj => {
                  let val = obj[column.name]

                  console.log(val)

                  column.handler.forEach(h=>{
                    let hval = h.value
                    if(val == hval){
                      obj[column.name] = h.name
                    }
                  })

                  })
                }
                if(column.filter) {
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
          setTimeout(()=>{
            this.loading = false
          },500)
        })
      },
      gotoPage(pageNo) {
        this.loading = true
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




