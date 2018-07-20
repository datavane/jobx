<template>
  <aside class="sidebar" v-if="!$route.path.endsWith('/login')">
    <div class="scrollbar-inner">

      <div class="user">
        <div class="user__info" data-toggle="dropdown">
          <img class="user__img" :src="user.headerPath" alt="">
          <div>
            <div class="user__name">{{userName}}</div>
          </div>
        </div>
        <div class="dropdown-menu">
          <span class="dropdown-item" @click="profile">个人信息</span>
          <span class="dropdown-item" data-toggle="modal" data-target="#modal-upload">更换头像</span>
          <span class="dropdown-item" @click="toLogout">退出登录</span>
        </div>
      </div>

      <!--navigation-->
      <ul class="navigation">
        <li v-for="nav in navigation" :class="navStyle(nav)">
          <router-link tag="a" :to="nav.item.length==0?nav.url:''">
            <i class="zmdi" :class="nav.icon"></i>
            {{nav.name}}
          </router-link>
          <ul v-if="nav.item.length>0">
            <li v-for="item in nav.item" :class="itemStyle(item)">
              <router-link tag="a" :to="item.url">
                {{item.name}}
              </router-link>
            </li>
          </ul>
        </li>
      </ul>

    </div>

  </aside>
</template>

<script type="text/ecmascript-6">
  import {mapGetters,mapActions} from 'vuex'
  export default {
    name: "Sidebar",
    computed: {
      ...mapGetters([
          'user'
        ])
    },
    data() {
      return {
        userName:"benjobs",
        navigation: [
          {
            name: "Dashboard",
            className: "",
            icon: "zmdi-view-dashboard",
            url: "/dashboard",
            item: []
          },
          {
            name: "执行器管理",
            className: "@@variantsactive",
            icon: "zmdi-laptop-chromebook",
            url: "",
            item: [
              {
                name: "分组管理",
                className: "@@sidebaractive",
                url: "/group"
              },
              {
                name: "执行器管理",
                className: "@@boxedactive",
                url: "/agent"
              }
            ]
          },
          {
            name: "作业管理",
            className: "@@variantsactive",
            icon: "zmdi-view-list",
            url: "/job",
            item: [
              {
                name: "作业列表",
                className: "@@sidebaractive",
                url: "/job/view"
              },
              {
                name: "现场执行",
                className: "@@boxedactive",
                url: "/job/exec"
              }
            ]
          },
          {
            name: "调度记录",
            className: "@@variantsactive",
            icon: "zmdi-print",
            url: "/record",
            item: [
              {
                name: "正在运行",
                className: "@@sidebaractive",
                url: "/record/running"
              },
              {
                name: "已完成",
                className: "@@boxedactive",
                url: "/record/done"
              }
            ]
          },
          {
            name: "终端",
            className: "",
            icon: "zmdi-chevron-right",
            url: "/terminal",
            item: []
          },
          {
            name: "用户管理",
            className: "",
            icon: "zmdi-account",
            url: "/user",
            item: []
          },
          {
            name: "系统设置",
            className: "",
            icon: "zmdi-settings",
            url: "/config",
            item: []
          }
        ]
      };
    },

    methods: {
      navStyle(nav) {
        let className = nav.className
        if (nav.item.length) {
          nav.item.forEach((_item,index)=>{
            if (this.$route.path.indexOf(_item.url) > -1) {
              className += " navigation__sub navigation__sub--active navigation__sub--toggled"
              return className;
            }
          })
          className += " navigation__sub"
        } else if (this.$route.path.indexOf(nav.url) > -1) {
          className += " navigation__active"
        }
        return className
      },

      itemStyle(item) {
        if (this.$route.path.endsWith(item.url)) {
          return "navigation__active"
        }
        return item.className
      },

      profile() {
        this.$router.push({ path: "/profile" })
      },

      upload() {
        console.log("upload....")
      },
      toLogout() {
        this.logout()
        this.$router.push({ path: "/login" })
      },
      ...mapActions(['logout'])
    }
   
  }
</script>
<style scoped>
</style>
