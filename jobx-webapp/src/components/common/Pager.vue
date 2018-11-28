<template>
<nav>
    <ul class="pagination justify-content-center">
          <li class="page-item pagination-first" :class="{'disabled':pager.page===1}">
            <a class="page-link" @click="pager.page=1"></a>
          </li>
          <li class="page-item pagination-prev" :class="{'disabled':pager.page===1}">
            <a class="page-link"  @click="pager.page-=1"></a>
          </li>
          <li class="page-item" v-for="index in preNo">
            <a class="page-link" @click="pager.page=index">{{index}}</a>
          </li>
          <li class="page-item active">
            <a class="page-link">{{pager.page}}</a>
          </li>
          <li class="page-item" v-for="index in nextNo">
            <a class="page-link" @click="pager.page=index">{{index}}</a>
          </li>
          <li class="page-item pagination-next" :class="{'disabled':pager.page===pager.pages}">
            <a class="page-link" @click="pager.page+=1"></a>
          </li>
          <li class="page-item pagination-last" :class="{'disabled':pager.page===pager.pages}">
            <a class="page-link" @click="pager.page=pager.pages"></a>
          </li>
      </ul>
  </nav>
</template>

<script>
  export default {
    props: ['pager'],
    data() {
      return {
        offset: 5,
        preNo: [],
        nextNo: []
      }
    },
    watch: {
      pager: {
        deep: true,
        handler:function(){
           console.log("chind....")
          this.preNo = []
          let preStart = 1
          if (this.pager.page > 1) {
            if (this.pager.page - this.offset > 1) {
              preStart = this.pager.page - this.offset
              if (this.pager.pages - this.pager.page < this.offset) {
                preStart -= this.offset - (this.pager.pages - this.pager.page)
              }
            }
            for (let i = preStart; i < this.pager.page; i++) {
              this.preNo.push(i)
            }
          }
          this.nextNo = []
          if (this.pager.page < this.pager.pages) {
            let nextLen = this.offset * 2 - this.preNo.length
            let nextEnd =
              this.pager.page + nextLen > this.pager.pages
                ? this.pager.pages
                : this.pager.page + nextLen
            for (let i = this.pager.page + 1; i <= nextEnd; i++) {
              this.nextNo.push(i)
            }
          }
        }
  　  }
    }
  }
</script>
