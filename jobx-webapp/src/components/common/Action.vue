<template>
  <div class="actions" v-if="!theme || theme == 'list'">
    <i class="actions__item zmdi zmdi-filter-list" data-table-action="search" @click="actions.filter = !actions.filter"></i>
    <i class="actions__item zmdi zmdi-print" data-table-action="print"></i>
    <i class="actions__item zmdi zmdi-fullscreen" data-table-action="fullscreen"></i>
    <i class="actions__item zmdi zmdi-download" data-table-toggle="dropdown"></i>
    <i v-if="url" class="actions__item zmdi zmdi-plus" @click="goAdd()"></i>
  </div>
 <div class="actions" v-else>
    <i class="actions__item zmdi zmdi-undo" data-table-action="undo" @click="undo"></i>
    <i class="actions__item zmdi zmdi-refresh" data-table-action="refresh"></i>
  </div>
</template>

<script>
  export default {
    props: ['url','actions','theme'],
    data() {
      return {
        show:false
      }
    },
    methods:{
      goAdd(){
        this.$router.push(this.url)
      },
      undo(){
        this.$router.back(-1)
      }
    }
  }
</script>

<style scoped>

</style>
