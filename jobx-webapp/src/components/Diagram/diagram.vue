<template>

</template>
<script>
  import go from '../../../static/gojs/go'
  let $ = go.GraphObject.make
  export default {
    name: 'diagram',
    props: ["modelData"],  // accept model data as a parameter
    data() {
      return {
        diagram: null,
        cxElement:null,
        fillColor:'rgb(27,172,255)'
      }
    },
    mounted: function() {
      this.initDiagram()
      this.initNodeTemplate()
      this.initLinkTemplate()
      this.handleUpdateModel(this.modelData)
    },

    watch: {
      modelData: function(val) { this.handleUpdateModel(val) }
    },

    methods: {
      initDiagram() {
        this.diagram = $(
          go.Diagram,
          this.$el,
          {
            allowCopy: false,
            allowDelete: false,
            allowMove: false,
            allowLink: false,//是否允许拖拽连线
            allowRelink: false,//是否允许重新连线
            allowZoom: true,//允许缩放。。。
            initialAutoScale: go.Diagram.Uniform,
            initialContentAlignment: go.Spot.Center, //设置整个图表在容器中的位置 https://gojs.net/latest/api/symbols/Spot.html
            "grid.visible": false,//是否显示背景栅格
            "grid.gridCellSize": new go.Size(5, 5),//栅格大小
            "commandHandler.copiesTree": false,  // 禁用复制快捷键
            "commandHandler.deletesTree": false, // 禁用删除快捷键
            "draggingTool.dragsTree": true,
            "commandHandler.deletesTree": true,
            //"toolManager.mouseWheelBehavior": go.ToolManager.WheelZoom, //启用视图放大缩小
            layout: $(go.TreeLayout, { angle: 90, arrangement: go.TreeLayout.ArrangementHorizontal }),
            "undoManager.isEnabled": true,
            // Model ChangedEvents get passed up to component users
            // "ModelChanged": function(e) { self.$emit("model-changed", e) },
           // "ChangedSelection": function(e) { self.$emit("changed-selection", e) }
          }
        )
      },

      initNodeTemplate() {
        this.diagram.nodeTemplate = $(
          go.Node,
          "Auto",
          { isShadowed: false },
          // define the node's outer shape
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
              stroke: this.fillColor,//边框颜色
              strokeWidth: 1,
              cursor: "pointer"
            }
          ),
          $(
            go.TextBlock,{
              stroke: "#fff",
              font: "700 12px Droid Serif, sans-serif",
              textAlign: "center",
              margin: 6
            },
            new go.Binding("text").makeTwoWay()
          )
        )
      },

      initLinkTemplate() {
        this.diagram.linkTemplate =
          $(go.Link,
            {
              curve: go.Link.Bezier,
              toShortLength: 5,
              toEndSegmentLength: 45,
              fromEndSegmentLength: 10,
              adjusting: go.Link.Stretch,
            },
            $(go.Shape,
              {
                strokeWidth: 1,
                stroke: 'rgb(125,125,125)'
              }
            ),
            $(
              go.Shape,
              { toArrow: "Standard", fill: 'rgb(125,125,125)', stroke: null }
            )
          )
      },

      model: function() { return this.diagram.model },
      handleUpdateModel: function(val) {
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
      handleUpdateDiagram: function() {
        this.diagram.startTransaction()
        // This is very general but very inefficient.
        // It would be better to modify the diagramData data by calling
        // Model.setDataProperty or Model.addNodeData, et al.
        this.diagram.updateAllRelationshipsFromData()
        this.diagram.updateAllTargetBindings()
        this.diagram.commitTransaction("updated")
      },

      handleShowContextMenu:function (obj, diagram, tool) {
        // Show only the relevant buttons given the current state.
        let cmd = diagram.commandHandler;
        document.getElementById("cut").style.display = cmd.canCutSelection() ? "block" : "none";
        document.getElementById("copy").style.display = cmd.canCopySelection() ? "block" : "none";
        document.getElementById("paste").style.display = cmd.canPasteSelection() ? "block" : "none";
        document.getElementById("delete").style.display = cmd.canDeleteSelection() ? "block" : "none";
        document.getElementById("color").style.display = (obj !== null ? "block" : "none");

        // Now show the whole context menu element
        this.cxElement.style.display = "block";
        // we don't bother overriding positionContextMenu, we just do it here:
        let mousePt = diagram.lastInput.viewPoint;
        this.cxElement.style.left = mousePt.x + "px";
        this.cxElement.style.top = mousePt.y + "px";
      }

    }
    
  }
</script>
