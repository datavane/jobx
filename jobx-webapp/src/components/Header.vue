<template>
  <div>
    <div id="header" v-if="!$route.path.endsWith('/login')">
      <div class="page-loader">
        <div class="page-loader__spinner">
          <svg viewBox="25 25 50 50">
            <circle cx="50" cy="50" r="20" fill="none" stroke-width="2" stroke-miterlimit="10"/>
          </svg>
        </div>
      </div>
      <header class="header">
        <div class="navigation-trigger hidden-xl-up" data-sa-action="aside-open" data-sa-target=".sidebar">
          <i class="zmdi zmdi-menu"></i>
        </div>

        <div class="logo hidden-sm-down">
          <img src="static/logo.png"/>
        </div>

        <ul class="top-nav">
          <li class="dropdown top-nav__notifications">
              <span data-toggle="dropdown" class="top-nav__notify">
                <i class="zmdi zmdi-notifications"></i>
              </span>
            <div class="dropdown-menu dropdown-menu-right dropdown-menu--block">
              <div class="dropdown-header">
                Notifications
                <div class="actions">
                  <a href="" class="actions__item zmdi zmdi-check-all" data-sa-action="notifications-clear"></a>
                </div>
              </div>

              <div class="listview listview--hover">
                <div class="listview__scroll scrollbar-inner">
                  <a href="" class="listview__item" v-for="index in 10">
                    <img src="demo/img/profile-pics/1.jpg" class="listview__img" alt="">
                    <div class="listview__content">
                      <div class="listview__heading">David Belle</div>
                      <p>Cum sociis natoque penatibus et magnis dis parturient montes</p>
                    </div>
                  </a>
                </div>

                <div class="p-1"></div>
              </div>
            </div>
          </li>

          <li class="dropdown hidden-xs-down">
            <span data-sa-action="fullscreen"><i class="zmdi zmdi-fullscreen-alt"></i></span>
          </li>

          <li class="hidden-xs-down">
              <span class="top-nav__themes" data-toggle="modal" data-target="#modal-theme">
                <i class="zmdi zmdi-palette"></i>
              </span>
          </li>
        </ul>

        <div class="clock hidden-md-down">
          <div class="time">
            <span class="time__hours"></span>
            <span class="time__min"></span>
            <span class="time__sec"></span>
          </div>
        </div>
      </header>
    </div>

    <div class="modal fade" id="modal-theme" v-if="!$route.path.endsWith('/login')" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h6 class="modal-title" id="theme-modal-label">Theme & Style</h6>
            <button class="btn btn-light btn--icon" data-dismiss="modal"><i class="zmdi zmdi-close"></i></button>
          </div>
          <div class="modal-body">
            <div class="row template-skins">
              <img v-for="index in 15" @click="theme(index)" :src="'static/img/bg/'+index+'.jpg'">
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="modal fade" id="modal-upload" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <form class="avatar-form" name="picform" action="http://jobx.17gwx.com/headpic/upload.do"
                enctype="multipart/form-data" method="post">
            <input name="userId" value="1" type="hidden">
            <div class="modal-header">
              <h5 class="modal-title" id="avatar-modal-label">更改图像</h5>
              <button class="btn btn-light btn--icon" data-dismiss="modal"><i class="zmdi zmdi-close"></i></button>
            </div>
            <div class="modal-body">
              <div class="avatar-body">
                <!-- Upload image and data -->
                <div class="avatar-upload">
                  <input class="avatar-src" name="src" type="hidden">
                  <input class="avatar-data" name="data" type="hidden">
                  <button class="btn btn-light btn--icon-text" onclick="document.picform.file.click()">请选择本地照片</button>
                  <input class="avatar-input" id="avatarInput" name="file" style="display:none;" type="file">
                </div>
                <!-- Crop and preview -->
                <div class="row">
                  <div class="col-md-8">
                    <div class="avatar-wrapper" style="cursor: pointer;">
                      <span class="upload-txt"><span class="upload-add"></span>点击上传图片并选择需要裁剪的区域</span>
                    </div>
                  </div>

                  <div class="col-md-4">
                    <div class="avatar-preview preview-lg"><img
                      src="http://jobx.17gwx.com/upload/1_140.jpg?1530977156713"></div>
                  </div>
                </div>

                <div class="row avatar-btns">
                  <div class="col-md-8">
                    <div class="btn-group">
                      <button class="btn btn-light btn--icon-text" data-method="rotate" data-option="-5" title="-5"><i
                        class="zmdi zmdi-replay-5"></i>-5°
                      </button>
                      <button class="btn btn-light btn--icon-text" data-method="rotate" data-option="-10" title="-10"><i
                        class="zmdi zmdi-replay-10"></i>-10°
                      </button>
                      <button class="btn btn-light btn--icon-text" data-method="rotate" data-option="-30" title="-30"><i
                        class="zmdi zmdi-replay-30"></i>-30°
                      </button>
                    </div>
                    <div class="btn-group" style="float:right">
                      <button class="btn btn-light btn--icon-text" data-method="rotate" data-option="5" title="5"><i
                        class="zmdi zmdi-forward-5"></i>5°
                      </button>
                      <button class="btn btn-light btn--icon-text" data-method="rotate" data-option="10" title="10"><i
                        class="zmdi zmdi-forward-10"></i>10°
                      </button>
                      <button class="btn btn-light btn--icon-text" data-method="rotate" data-option="30" title="30"><i
                        class="zmdi zmdi-forward-30"></i>30°
                      </button>
                    </div>
                  </div>
                  <div class="col-md-4">
                    <button class="btn btn-light btn-block btn--icon-text" type="submit"><i
                      class="zmdi zmdi-upload"></i>上传
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>

  </div>
</template>
<script type="text/ecmascript-6">
  import {mapGetters} from 'vuex'

  export default {
    computed: {
      ...mapGetters([
        'loading'
      ])
    },
    methods: {
      theme(index) {
        this.$storage.set(this.$const.keys.theme, index)
        $("body").attr("data-sa-theme", index)
      }
    },
    watch: {
      loading(val) {
        if (!val) {
          $('.page-loader').fadeOut()
        } else {
          $('.page-loader').fadeIn()
        }
      }
    }

  }
</script>
<style lang="scss" scoped>
  .logo {
    img {
      margin-left: 30px;
      height: 50px;
    }
  }

  .preview-lg {
    height: 195px;
    width: 195px;
    margin: 68px 12px 37px;
    opacity: 0.9;
  }

  .avatar-preview {
    border-radius: 50% !important;
    border: 2px solid rgba(200, 220, 220, 0.35);
    float: left;
    overflow: hidden;
  }

  #modal-theme {
    .modal-dialog {
      background-color: rgba(0, 0, 0, 0.6)
    }
    .modal-header {
      padding: 20px 25px 5px 25px;
      border-bottom: 0 solid #e9ecef;
    }
    .modal-body {
      flex: 1;
      padding: 18px 20px;
    }
    .template-skins {
      img {
        padding-left: 15px;
        padding-bottom: 15px;
        width: 155px;
        height: 100px;
      }
    }
  }
</style>
