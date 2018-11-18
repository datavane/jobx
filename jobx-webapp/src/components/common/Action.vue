<template>
  <div class="actions">
      <i class="actions__item zmdi zmdi-filter-list" data-table-action="search" @click="actions.filter = !actions.filter"></i>
      <i class="actions__item zmdi zmdi-print" data-table-action="print"></i>
      <i class="actions__item zmdi zmdi-fullscreen" data-table-action="fullscreen"></i>
      <i class="actions__item zmdi zmdi-download" data-table-toggle="dropdown"></i>
      <i v-if="url" class="actions__item zmdi zmdi-plus" @click="goAdd()"></i>
    </div>
</template>

<script>
  export default {
    props: ['url','actions'],
    data() {
      return {
        show:false
      }
    },
    methods:{
      goAdd(){
        this.$router.push(this.url)
      }
    }
  }
</script>

<style scoped>

</style>
