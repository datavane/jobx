<template>
  <header id="header">
    <a href="" id="menu-toggle" style="background-image: none"><i class="icon">&#61773;</i></a>
    <a id="log1" href="/dashboard.htm" class="logo pull-left"><div style="float: left; width: 110px; margin-top: -10px; margin-left: 40px;">
      <img src="/static/img/jobx.png">
    </div>
    </a>
    <div class="slogan">Let's scheduling easy</div>
    <div class="media-body">
      <div class="media" id="top-menu" style="float:right;margin-right:15px;">
        <div class="pull-left tm-icon" id="full-screen">
          <a  class="drawer-toggle" data-drawer="messages" id="toggle_screen" href="#">
            <i id="fullscreenbtn" open="off" class="icon" title="全屏/退出全屏" style="background-image:none;font-size: 30px; background-size: 25px;" onclick="goF11();">&#61831;</i>
          </a>
        </div>
        <div class="pull-left tm-icon" id="msg-icon" style="display: none;">
          <a  class="drawer-toggle" data-drawer="messages" id="toggle_message" href="#">
            <i class="sa-top-message icon" style="background-image:none;font-size: 30px; background-size: 25px;">&#61710;</i>
            <i class="n-count">5</i>
          </a>
        </div>
        <div id="time" style="float:right;">
          <span id="hours"></span>:<span id="min"></span>:<span id="sec"></span>
        </div>
      </div>
    </div>
  </header>
</template>

<script type="text/ecmascript-6">
  export default {}
</script>
