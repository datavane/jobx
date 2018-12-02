<template>
  <div class="actions">
   <!-- <i class="actions__item zmdi zmdi-filter-list" data-table-action="search" @click="actions.search = !actions.search"></i>-->
    <i class="actions__item zmdi zmdi-print" data-table-action="print" @click="actions.print = true"></i>
    <i class="actions__item zmdi zmdi-fullscreen" data-table-action="fullscreen" @click="actions.fullscreen=true"></i>
    <i class="actions__item zmdi zmdi-download" data-table-toggle="dropdown" @click="actions.download=true"></i>
    <i v-if="actions.addURL" class="actions__item zmdi zmdi-plus" @click="$router.push(actions.addURL)"></i>
  </div>
<!-- <div class="actions" v-else>
    <i class="actions__item zmdi zmdi-undo" data-table-action="undo" @click="$router.back(-1)"></i>
    <i class="actions__item zmdi zmdi-refresh" data-table-action="refresh"></i>
  </div>-->
</template>

<script>
  export default {
    props: ['actions'],
    data() {
      return {
        show:false
      }
    }
  }
</script>

<style scoped>

</style>
