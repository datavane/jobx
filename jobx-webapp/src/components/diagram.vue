<script>
  import go from '../../static/gojs/go'
  let $ = go.GraphObject.make
  export default {
    name: 'diagram',
    props: ["modelData"],  // accept model data as a parameter
    data() {
      return {
        diagram: null,
      }
    },
    mounted: function() {
      let self = this
      let $ = go.GraphObject.make
      let myDiagram = $(go.Diagram, this.$el,
          {
            layout: $(go.TreeLayout, { angle: 90, arrangement: go.TreeLayout.ArrangementHorizontal }),
            "undoManager.isEnabled": true,
            // Model ChangedEvents get passed up to component users
            "ModelChanged": function(e) { self.$emit("model-changed", e) },
            "ChangedSelection": function(e) { self.$emit("changed-selection", e) }
          })

      myDiagram.nodeTemplate = $(go.Node,
          "Auto",
          // define the node's outer shape
          $(go.Shape,
            "RoundedRectangle",
            {
              fill: "white", strokeWidth: 0,
              portId: "",
              fromLinkable: true,
              toLinkable: true,
              stroke: "#5293c4",//边框颜色
              strokeWidth: 1,
              cursor: "pointer"
            },
            new go.Binding("fill", "color")),
          $(go.TextBlock,
            {
              margin: 5,
              stroke: "#333",
              font: "bold 13px Helvetica, bold Arial, sans-serif"},
            new go.Binding("text").makeTwoWay())
        )

     /* myDiagram.nodeTemplate = $(
          go.Node,
          "Auto",
          $(go.Shape,{
              fill: "white",
              strokeWidth: 1,
              portId: "",
              fromLinkable: true,
              toLinkable: true,
              cursor: "pointer"
            },
            new go.Binding("fill", "color")
          ),
          $(go.TextBlock,
            { margin: 10, stroke: "#333", font: "bold 16px sans-serif"},
            new go.Binding("text").makeTwoWay()
          )
        )*/

      myDiagram.linkTemplate =
        $(go.Link,
          {
            curve: go.Link.Bezier,
            toEndSegmentLength: 30,
            fromEndSegmentLength: 20
          },
          $(go.Shape),
          $(go.Shape, { toArrow: "OpenTriangle" })
        )


      this.diagram = myDiagram

      this.updateModel(this.modelData)
    },
    watch: {
      modelData: function(val) { this.updateModel(val) }
    },
    methods: {
      model: function() { return this.diagram.model },
      updateModel: function(val) {
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
      updateDiagramFromData: function() {
        this.diagram.startTransaction()
        // This is very general but very inefficient.
        // It would be better to modify the diagramData data by calling
        // Model.setDataProperty or Model.addNodeData, et al.
        this.diagram.updateAllRelationshipsFromData()
        this.diagram.updateAllTargetBindings()
        this.diagram.commitTransaction("updated")
      }
    }
    
  }
</script>
