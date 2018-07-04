<template>
  <div id="app">
    <Header></Header>
    <Avatar></Avatar>
    <section id="main" class="p-relative" role="main">
      <Nav></Nav>
      <keep-alive>
        <router-view></router-view>
      </keep-alive>
    </section>
  </div>
</template>

<script type="text/ecmascript-6">
  import Header from './components/header/Header'
  import Avatar from './components/header/Avatar'
  import Nav from './components/nav/Nav'
  export default {
    components: {
      Header,
      Avatar,
      Nav
    }
  }
</script>

<style scoped >

#app{
  width:100%;
  height: 100%;
}
</style>
