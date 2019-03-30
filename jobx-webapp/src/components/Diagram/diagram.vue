<template>
  <div :class="{'fullScreen':fullScreen}">
    <div class="option">
       <el-dropdown size="small" @command="handleDirection">
         <el-button type="success" size="mini">布局</el-button>
        <el-dropdown-menu slot="dropdown" style="z-index: 9999">
          <el-dropdown-item v-for="item in directions" :key="item.value" :command="item.value"> {{item.label}} </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <el-button type="primary" @click="handleRefresh()" size="mini">重置</el-button>
      <el-button type="primary" @click="handleFullScreen()" size="mini">全屏</el-button>
    </div>
    <div ref="diagram" :style="diagStyle" class="diagram"></div>
  </div>
</template>
<script>
  import go from '../../../static/gojs/go'
  let $ = go.GraphObject.make
  export default {
    name: 'diagram',
    props: ["data"],  // accept model data as a parameter
    data() {
      return {
        diagram: null,
        direction:0,
        fullScreen:false,
        diagStyle:{
          height:"500px"
        },
        borderColor:'green',
        //fillColor:'rgb(27,172,255)',
        fillColor:'#fff',
        linkColor:'rgb(95,95,105)',
        fontColor:'#333',
        directions:[
          {value:0,label:'从左到右'},
          {value:180,label:'从右到左'},
          {value:90,label:'从上到下'},
          {value:270,label:'从下到上'},
        ]
      }
    },
    mounted: function() {
      this.initFullScreen()
      this.initDiagramStyle()
      this.initDiagram()
      this.initNodeTemplate()
      this.initLinkTemplate()
      this.handleUpdateModel(this.data)
    },

    watch: {
      data(val){
        this.handleUpdateModel(val)
      },
      direction() {
        this.diagram.layout.direction = this.direction
      },
      fullScreen() {
        this.initDiagramStyle()
      }
    },
    methods: {

      model(){
        return this.diagram.model
      },

      initDiagram() {
        this.diagram = $(
          go.Diagram,
          this.$refs.diagram,
          {
            //禁止操作
            allowCopy: false,
            allowDelete: false,
            allowMove: true,
            allowLink: false,//是否允许拖拽连线
            allowRelink: false,//是否允许重新连线
            //画布相关
            allowZoom: true,//允许缩放。。。
            initialAutoScale: go.Diagram.Uniform,//自适应
            initialContentAlignment: go.Spot.Center,
            maxScale:1.5,//画布最大比例
            "animationManager.isEnabled": false,//禁止画布初始化动画
            "toolManager.mouseWheelBehavior": go.ToolManager.WheelZoom, //启用视图放大缩小
            "grid.visible": false,//是否显示背景栅格
            "commandHandler.copiesTree": false,  // 禁用复制快捷键
            "commandHandler.deletesTree": false, // 禁用删除快捷键
            "draggingTool.dragsTree": true,
            layout: $(go.LayeredDigraphLayout,{direction:this.direction}),
            "undoManager.isEnabled": true
          }
        )
      },

      initNodeTemplate() {
        this.diagram.nodeTemplate = $(
          go.Node,
          "Auto",
          {isShadowed: false },
          $(go.Shape,
            "RoundedRectangle",
            {
              fill:this.fillColor,
              fromLinkable: true,
              fromLinkableSelfNode: true,
              fromLinkableDuplicates: true,
              toLinkable: true,
              toLinkableSelfNode: true,
              toLinkableDuplicates: true,
              stroke: this.borderColor,//边框颜色
              strokeWidth: 2,
              cursor: "pointer"
            }
          ),
          $(
            go.TextBlock,{
              stroke: this.fontColor,
              font: "700 14px Droid Serif, sans-serif",
              textAlign: "center",
              margin: 8
            },
            new go.Binding("text").makeTwoWay()
          )
        )
      },
      initLinkTemplate() {
        this.diagram.linkTemplate = $(go.Link,
          {
            curve: go.Link.Bezier,
            toShortLength: 5,
            toEndSegmentLength: 35,
            fromEndSegmentLength: 2,
            adjusting: go.Link.Scale,
          },
          $(go.Shape,
            {
              strokeWidth: 1,
              stroke: this.linkColor
            }
          ),
          $(
            go.Shape,
            { toArrow: "Standard", fill: this.linkColor, stroke: null }
          )
        )
      },
      handleDirection(val){
        this.direction = val
      },
      initDiagramStyle() {
        if (this.fullScreen) {
          this.diagStyle.height = window.document.body.offsetHeight + "px"
          if (this.diagram) {
            this.diagram.minScale = 0.4
            this.handleUpdateModel(this.data)
          }
        }else {
          this.diagStyle.height = this.data.nodeDataArray.length * 25+ "px"
          if (this.diagram) {
            this.diagram.minScale = 0.8
            this.handleUpdateModel(this.data)
          }
        }
      },

      handleRefresh(){
        this.handleUpdateModel(this.data)
      },


      handleUpdateModel (val) {
        // No GoJS transaction permitted when replacing Diagram.model.
        if (val instanceof go.Model) {
          this.diagram.model = val
        } else {
          let m = new go.GraphLinksModel()
          if (val) {
            for (let p in val) {
              m[p] = val[p]
            }
          }
          this.diagram.model = m
        }
      },
      handleUpdateDiagram () {
        this.diagram.startTransaction()
        // This is very general but very inefficient.
        // It would be better to modify the diagramData data by calling
        // Model.setDataProperty or Model.addNodeData, et al.
        this.diagram.updateAllRelationshipsFromData()
        this.diagram.updateAllTargetBindings()
        this.diagram.commitTransaction("updated")
      },

      handleFullScreen: function () {
        if (this.fullScreen) {
          if (this.cancelFullScreen()) {
            alert("你的浏览器，不支持哦");
          } else {
            this.fullScreen = false
          }
        } else {
          let view = document.documentElement;
          if (this.launchFullScreen(view)) {
            alert("你的浏览器，不支持哦");
          } else {
            this.fullScreen = true
          }
        }
      },

      initFullScreen: function () {
        document.addEventListener && (document.addEventListener('webkitfullscreenchange', this.escFullScreen(), false) ||
          document.addEventListener('mozfullscreenchange', this.escFullScreen(), false) ||
          document.addEventListener('fullscreenchange', this.escFullScreen(), false) ||
          document.addEventListener('webkitfullscreenchange', this.escFullScreen(), false));
        document.attachEvent && document.attachEvent('msfullscreenchange', this.escFullScreen());
      },

      launchFullScreen: function (element) {
        if (element.requestFullScreen) {
          element.requestFullScreen();
        } else if (element.mozRequestFullScreen) {
          element.mozRequestFullScreen();
        } else if (element.webkitRequestFullScreen) {
          element.webkitRequestFullScreen();
        } else if (element.msRequestFullScreen) {
          element.msRequestFullScreen();
        } else {
          return true;
        }
      },

      cancelFullScreen: function () {
        if (document.exitFullscreen) {
          document.exitFullscreen();
        } else if (document.mozCancelFullScreen) {
          document.mozCancelFullScreen();
        } else if (document.webkitCancelFullScreen) {
          document.webkitCancelFullScreen();
        } else if (document.msExitFullscreen) {
          document.msExitFullscreen();
        } else {
          return true;
        }
      },

      escFullScreen: function () {
        if (this.fullScreen) {
          this.handleFullScreen()
        }
      },

    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .fullScreen {
    background-color: #fff;
    position: fixed;
    width: 100%;
    height: 100%;
    z-index: 9999;
    left: 0px;
    top: 0px;
    padding:0px;
  }
  .diagram {
    min-height: 400px;
    width: 100%;
    clear: both;
  }
  .option{
    margin: 20px;
    float: right;
  }
</style>
