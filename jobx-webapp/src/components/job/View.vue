<template>
  <section class="content">
      <header class="content__title">
        <h4 class='card-title'>{{title}}</h4>
      </header>
      <div class="card">
        <div class="card-body">
            <v-data-table
              :headers="headers"
              :loading="true"
              :items="pageData">
              <v-progress-linear slot="progress" color="white" style="height:1px" indeterminate></v-progress-linear>
              <template slot="items" slot-scope="props">
                <td>{{ props.item.agentName }}</td>
                <td class="text-left">{{ props.item.jobName }}</td>
                <td class="text-left">{{ props.item.execUser }}</td>
                <td class="text-left">{{ props.item.command }}</td>
                <td class="text-left">{{ props.item.pause }}</td>
                <td class="text-left">{{ props.item.redo }}</td>
                <td class="text-left">{{ props.item.jobType }}</td>
                <td class="text-left">{{ props.item.cronExp }}</td>
              </template>
          </v-data-table>
        </div>
      </div>
  </section>
</template>
<script type="text/ecmascript-6">
  import pager from '@/components/common/Pager'
  import action from '@/components/common/Action'
  import select2 from '@/components/common/Select2'
  import BScroll from 'better-scroll'
  export default {
    components: {
      pager,action,select2
    },
    data: () => ({
      actions:{
        filter:false
      },
      hoverIndex:0,
      title:'JOB LIST',
      url: "/job/view",
      headers: [
        {
          text: '执行器',
          value: 'agentName',
          align: 'left',
          sortable: true
        },
        { text: '名称', value: '名称' },
        { text: '执行身份', value: '执行身份' },
        { text: '命令', value: '命令' },
        { text: '暂停', value: '暂停' },
        { text: '重跑', value: '重跑', sortable: true },
        { text: '调度方式', value: '调度方式', sortable: true },
        { text: 'CRONEXP', value: 'CRONEXP' }
      ],
      pageData:{}
  }),

   mounted () {
    this.getPageData()
  },

  methods: {
    getPageData (data) {
      this.$http.post(this.url, data || {}).then(resp => {
        this.pageData = resp.body.result
        console.log(this.pageData)
      })
    }
  }
}

</script>

<style lang="scss" scoped>
.theme--light.v-table{
   background-color: rgba(0,0,0,0);
  color:rgba(255,255,255,0.65);
}
</style>
