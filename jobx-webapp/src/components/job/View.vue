<template>
  <section class="content">
    <div class="card">
      <div class="card-body">
        <museTable title="作业列表" :url="url" :columns="columns"></museTable>
      </div>
    </div>
  </section>
</template>
<script type="text/ecmascript-6">
  import museTable from "@/components/common/MuseTable"
  export default {
    components: {
      museTable
    },
    data() {
      return {
        url: "/job/view",
        columns: [
          {title: '执行器', name: 'agentName',sortable: true },
          {title: '作业名称', name: 'jobName',sortable: true },
          {title: '作业人	', name: 'operateUname',sortable: true },
          {title: '暂停', name: 'pause',sortable: true,filter:"boolean"},
          {title: '失败重跑', name: 'redo',sortable: true,filter:"boolean"},
          {title: '作业类型', name: 'jobType',sortable: true,filter:{0:'单体作业',1:'流程作业'}},
          {title: '执行规则', name: 'cronExp',sortable: true}
        ]
      }
    }
  }
</script>
<style lang="scss" scoped>
</style>
