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
        url: "/job/view.do",
        columns: [
          {title: '执行器', name: 'agentName',align: 'center',sortable: true },
          {title: '作业名称', name: 'jobName',align: 'center',sortable: true },
          {title: '作业人	', name: 'operateUname',align: 'center',sortable: true },
          {title: '暂停', name: 'pause',align: 'center',sortable: true,filter:"boolean"},
          {title: '失败重跑', name: 'redo',align: 'center',sortable: true,filter:"boolean"},
          {title: '作业类型', name: 'jobType',align: 'center',sortable: true,filter:{0:'单体作业',1:'流程作业'}},
          {title: '执行规则', name: 'cronExp',align: 'center',sortable: true}
        ]
      }
    }
  }
</script>
<style lang="scss" scoped>
</style>
