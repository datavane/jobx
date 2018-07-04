<template>

  <!-- Sidebar -->
  <aside id="sidebar">

    <!-- Sidbar Widgets -->
    <div class="side-widgets overflow">
      <!-- Profile Menu -->
      <div class="text-center s-widget m-b-25 dropdown" id="profile-menu">
        <a href="" id="header-img" data-toggle="dropdown" class="animated a-hover">
          <img class="profile-pic" id="profile-pic" width="140px;" height="140px;" :src="avatarUrl"/>
          <div class="change-text" id="change-img" href="javascript:void(0);">更换头像</div>
        </a>
        <h4 class="m-0">${jobx_user.userName}</h4>
        <ul class="dropdown-menu profile-menu">
          <li><a href="${contextPath}/user/detail/${jobx_user.userId}.htm">个人信息</a> <i class="icon left">&#61903;</i><i class="icon right">&#61815;</i></li>
          <li><a href="${contextPath}/notice/view.htm">通知&nbsp;&&nbsp;消息</a> <i class="icon left">&#61903;</i><i class="icon right">&#61815;</i></li>
          <li><a href="${contextPath}/logout.htm">退出登录</a> <i class="icon left">&#61903;</i><i class="icon right">&#61815;</i></li>
        </ul>
      </div>

      <!-- Calendar -->
      <div class="s-widget m-b-25">
        <div id="sidebar-calendar"></div>
      </div>

      <div class="s-widget m-b-25 jobx-progress" style="display: none;" >
        <h2 class="tile-title side-tile-title tile-title-color">
          报告明细
        </h2>

        <div class="s-widget-body">
          <div class="side-border">
            <small>运行模式(自动/手动)</small>
            <div class="progress progress-small">
              <a href="#" data-toggle="tooltip" id="progress_type" class="progress-bar tooltips progress-bar-danger">
                <span class="sr-only" id="progress_type_tip"></span>
              </a>
            </div>
          </div>
          <div class="side-border">
            <small>作业类型(单一/流程)</small>
            <div class="progress progress-small">
              <a href="#" data-toggle="tooltip" id="progress_category" class="tooltips progress-bar progress-bar-info">
                <span class="sr-only"></span>
              </a>
            </div>
          </div>
          <div class="side-border">
            <small>重跑状态(非重跑/重跑)</small>
            <div class="progress progress-small">
              <a href="#" data-toggle="tooltip" id="progress_rerun" class="tooltips progress-bar progress-bar-success">
                <span class="sr-only"></span>
              </a>
            </div>
          </div>
          <div class="side-border">
            <small>执行状态(成功/失败)</small>
            <div class="progress progress-small">
              <a href="#" data-toggle="tooltip" id="progress_status" class="tooltips progress-bar progress-bar-success">
                <span class="sr-only"></span>
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>

    <ul class="list-unstyled side-menu">
      <li v-for="res in resURL" :class="res.dropdown">
        <router-link tag="a" :to="res.url">
          <i aria-hidden="true" class="fa" :class="res.icon"></i><span class="menu-item">{{res.name}}</span>
        </router-link>
        <ul class="list-unstyled menu-item" v-if="res.item.length>0">
          <li v-for="item in res.item">
            <router-link tag="a" :to="item.url">
              <span>{{item.name}}</span>
            </router-link>
          </li>
        </ul>
      </li>
    </ul>

  </aside>

</template>

<script>
export default {
  name: 'Nav',
  data () {
    return {
      resURL:[
        {
          name:"作业报告",
          dropdown:"",
          icon:"fa-tachometer",
          url:"dashboard.htm",
          item:[]
        },
        {
          name:"执行器管理",
          dropdown:"dropdown",
          icon:"fa-desktop",
          url:"",
          item:[
            {
              name:"分组管理",
              url:"/group/view.htm"
            },
            {
              name:"已完成",
              url:"/agent/view.htm"
            }
          ]
        },

        {
          name:"作业管理",
          dropdown:"dropdown",
          icon:"fa-tasks",
          url:"",
          item:[
            {
              name:"作业列表",
              url:"/group/view.htm"
            },
            {
              name:"现场执行",
              url:"/agent/view.htm"
            }
          ]
        },

        {
          name:"调度记录",
          dropdown:"dropdown",
          icon:"fa-print",
          url:"",
          item:[
            {
              name:"正在运行",
              url:"/record/running.htm"
            },
            {
              name:"已完成",
              url:"/record/done.htm"
            }
          ]
        },
        {
          name:"WEB终端",
          dropdown:"",
          icon:"fa-terminal",
          url:"terminal.htm",
          item:[]
        },


        {
          name:"服务监控",
          dropdown:"dropdown",
          icon:"fa-print",
          url:"",
          item:[
            {
              name:"监控任务",
              url:"/record/running.htm"
            },
            {
              name:"监控日志",
              url:"/record/done.htm"
            }
          ]
        },

        {
          name:"用户管理",
          dropdown:"",
          icon:"fa-user",
          url:"terminal.htm",
          item:[]
        },

        {
          name:"系统设置",
          dropdown:"",
          icon:"fa-cog",
          url:"terminal.htm",
          item:[]
        },

      ],
      contextPath:"/",
      avatarUrl:"/static/img/profile-pic.jpg",
      url:""
    }
  }
}
</script>


<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
