<template>
  <div class="container" id="crop-avatar">
    <!-- Cropping modal -->
    <div class="modal fade" id="avatar-modal" aria-hidden="true" aria-labelledby="avatar-modal-label" role="dialog" tabindex="-1">
      <div class="modal-dialog modal-md">
        <div class="modal-content">
          <form class="avatar-form" name="picform" action="/headpic/upload.do" enctype="multipart/form-data" method="post">
            <input name="userId" type="hidden" value="${jobx_user.userId}">
            <div class="modal-header">
              <button class="close btn-float" style="margin-top:-7px!important;margin-right:-11px;" data-dismiss="modal" aria-hidden="true"><i class="md md-close"></i></button>
              <h4 class="modal-title" id="avatar-modal-label">更改图像</h4>
            </div>
            <div class="modal-body">
              <div class="avatar-body">
                <!-- Upload image and data -->
                <div class="avatar-upload">
                  <input class="avatar-src" name="src" type="hidden">
                  <input class="avatar-data" name="data" type="hidden">

                  <button class="btn btn-default" onclick="document.picform.file.click()">请选择本地照片</button>
                  <input class="avatar-input" id="avatarInput" name="file" type="file" style="display:none;">
                </div>

                <!-- Crop and preview -->
                <div class="row">
                  <div class="col-md-8">
                    <div class="avatar-wrapper">
                      <span class="upload-txt"><span class="upload-add"></span>点击上传图片并选择需要裁剪的区域</span>
                    </div>
                  </div>

                  <div class="col-md-4">
                    <div class="avatar-preview preview-lg"></div>
                  </div>
                </div>

                <div class="row avatar-btns">
                  <div class="col-md-8">
                    <div class="btn-group">
                      <button type="button" class="btn btn-primary" data-method="rotate" data-option="-90" title="逆时针旋转90度">向左转</button>
                      <button type="button" class="btn btn-primary" data-method="rotate" data-option="-15">-15°</button>
                      <button type="button" class="btn btn-primary" data-method="rotate" data-option="-30">-30°</button>
                      <button type="button" class="btn btn-primary" data-method="rotate" data-option="-45">-45°</button>
                    </div>
                    <div class="btn-group" style="float:right">
                      <button type="button" class="btn btn-primary" data-method="rotate" data-option="90" title="顺时针旋转90度">向右转</button>
                      <button type="button" class="btn btn-primary" data-method="rotate" data-option="15">15°</button>
                      <button type="button" class="btn btn-primary" data-method="rotate" data-option="30">30°</button>
                      <button type="button" class="btn btn-primary" data-method="rotate" data-option="45">45°</button>
                    </div>
                  </div>
                  <div class="col-md-4">
                    <button class="btn btn-primary btn-block avatar-save" type="submit">上传</button>
                  </div>
                </div>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
    <!-- Loading state -->
    <div class="loading" aria-label="Loading" role="img" tabindex="-1"></div>
  </div>
</template>

<script type="text/ecmascript-6">
  export default {}
</script>

