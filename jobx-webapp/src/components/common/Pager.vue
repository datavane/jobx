<template>
<nav>
    <ul class="pagination justify-content-center">
          <li class="page-item pagination-first"
              :class="{'disabled':pageData.pageNo===1}">
            <a class="page-link" @click="goPageNo(1)"></a>
          </li>
          <li class="page-item pagination-prev"
              :class="{'disabled':pageData.pageNo===1}">
            <a class="page-link"
               @click="goPageNo(pageData.pageNo-1)"></a>
          </li>
          <li class="page-item" v-for="index in preNo">
            <a class="page-link"
               @click="goPageNo(index)">{{index}}
            </a>
          </li>
          <li class="page-item active">
            <a class="page-link">{{pageData.pageNo}}</a>
          </li>
          <li class="page-item" v-for="index in nextNo">
            <a class="page-link"
               @click="goPageNo(index)">{{index}}
            </a>
          </li>
          <li class="page-item pagination-next"
             :class="{'disabled':pageData.pageNo===pageData.pageTotal}">
            <a class="page-link"
               @click="goPageNo(pageData.pageNo+1)">
            </a>
          </li>
          <li class="page-item pagination-last"
           :class="{'disabled':pageData.pageNo===pageData.pageTotal}">
            <a class="page-link"
               @click="goPageNo(pageData.pageTotal)">
            </a>
          </li>
      </ul>
  </nav>
</template>

<script>
  export default {
    props: ['pageData','offset'],
    data() {
      return {
        preNo: [],
        nextNo: []
      }
    },
    methods:{
       goPageNo(pageNo) {
          this.$emit('goPageNo',pageNo)
       },
    },
    watch:{
      pageData:{
    　　 immediate:true,
        handler:function(){
          this.preNo = []
          let preStart = 1
          if (this.pageData.pageNo > 1) {
            if (this.pageData.pageNo - this.offset > 1) {
              preStart = this.pageData.pageNo - this.offset
              if (this.pageData.pageTotal - this.pageData.pageNo < this.offset) {
                preStart -=
                  this.offset - (this.pageData.pageTotal - this.pageData.pageNo)
              }
            }
            for (let i = preStart; i < this.pageData.pageNo; i++) {
              this.preNo.push(i)
            }
          }
          this.nextNo = []
          if (this.pageData.pageNo < this.pageData.pageTotal) {
            let nextLen = this.offset * 2 - this.preNo.length
            let nextEnd =
              this.pageData.pageNo + nextLen > this.pageData.pageTotal
                ? this.pageData.pageTotal
                : this.pageData.pageNo + nextLen
            for (let i = this.pageData.pageNo + 1; i <= nextEnd; i++) {
              this.nextNo.push(i)
            }
          }
        }
  　  }
    }
  }
</script>

<style scoped>

</style>
