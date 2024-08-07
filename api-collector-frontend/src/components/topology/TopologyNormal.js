import * as go from 'gojs';
import React from 'react';
import {ReactDiagram} from 'gojs-react';

const $ = go.GraphObject.make;
go.licenseKey = "2bf843e7b36758c511895a25406c7efb0bab2d67ce864df3595012a0ed587a04249fb87b50d7d8c986aa4df9182ec98ed8976121931c0338e737d48f45e0d5f1b63124e5061841dbf4052691c9fb38b1ff7971fbddbc68a2d2";
var DIAGRAM, PALETTE;

class TopRotatingTool {
    updateAdornments(part) {
        go.RotatingTool.prototype.updateAdornments.call(this, part);
        let adornment = part.findAdornment("Rotating");
        if (adornment !== null) {
            adornment.location = part.rotateObject.getDocumentPoint(new go.Spot(0.5, 0, 0, -30));  // above middle top
        }
    }

    rotate(newangle) {
        go.RotatingTool.prototype.rotate.call(this, newangle + 90);
    }
}


export default function GoJsTopologyDiagram() {

    function getDiagram() {
        let diagram =
            $(go.Diagram, // must name or refer to the DIV HTML element
                {
                    grid: $(go.Panel, "Grid",
                        $(go.Shape, "LineH", {stroke: "lightgray", strokeWidth: 0.5}),
                        $(go.Shape, "LineH", {stroke: "gray", strokeWidth: 0.5, interval: 10}),
                        $(go.Shape, "LineV", {stroke: "lightgray", strokeWidth: 0.5}),
                        $(go.Shape, "LineV", {stroke: "gray", strokeWidth: 0.5, interval: 10})
                    ),
                    allowDrop: true,  // must be true to accept drops from the Palette
                    "draggingTool.dragsLink": true,
                    "draggingTool.isGridSnapEnabled": true,
                    "linkingTool.isUnconnectedLinkValid": true,
                    "linkingTool.portGravity": 20,
                    "relinkingTool.isUnconnectedLinkValid": true,
                    "relinkingTool.portGravity": 20,
                    "relinkingTool.fromHandleArchetype":
                        $(go.Shape, "Diamond", {
                            segmentIndex: 0,
                            cursor: "pointer",
                            desiredSize: new go.Size(8, 8),
                            fill: "tomato",
                            stroke: "darkred"
                        }),
                    "relinkingTool.toHandleArchetype":
                        $(go.Shape, "Diamond", {
                            segmentIndex: -1,
                            cursor: "pointer",
                            desiredSize: new go.Size(8, 8),
                            fill: "darkred",
                            stroke: "tomato"
                        }),
                    "linkReshapingTool.handleArchetype":
                        $(go.Shape, "Diamond", {
                            desiredSize: new go.Size(7, 7),
                            fill: "lightblue",
                            stroke: "deepskyblue"
                        }),
                    // rotatingTool: $(TopRotatingTool),  //###### 20200622 Modified defined below
                    "rotatingTool.snapAngleMultiple": 15,
                    "rotatingTool.snapAngleEpsilon": 15,
                    "undoManager.isEnabled": true
                });

        diagram.addDiagramListener("Modified", function (e) {
            if (e.isTransactionFinished) {
                // this records each Transaction as a JSON-format string
                //  ###### 20200623 Modified
                // console.log(myDiagram.model.toIncrementalJson(e));
                console.log('modified.............');
            }
        });

        init(diagram);
        return diagram;
    }

    function getPalette(d) {
        let palette =
            $(go.Palette, // must name or refer to the DIV HTML element
                {
                    maxSelectionCount: 1,
                    nodeTemplateMap: d.nodeTemplateMap,  // share the templates used by myDiagram
                    linkTemplate: // simplify the link template, just in this Palette
                        $(go.Link,
                            { // because the GridLayout.alignment is Location and the nodes have locationSpot == Spot.Center,
                                // to line up the Link in the same manner we have to pretend the Link has the same location spot
                                locationSpot: go.Spot.Center,
                                selectionAdornmentTemplate:
                                    $(go.Adornment, "Link",
                                        {locationSpot: go.Spot.Center},
                                        $(go.Shape,
                                            {isPanelMain: true, fill: null, stroke: "deepskyblue", strokeWidth: 0}),
                                        $(go.Shape,  // the arrowhead
                                            {toArrow: "Standard", stroke: null})
                                    )
                            },
                            {
                                routing: go.Link.AvoidsNodes,
                                curve: go.Link.JumpOver,
                                corner: 5,
                                toShortLength: 4
                            },
                            new go.Binding("points"),
                            $(go.Shape,  // the link path shape
                                {isPanelMain: true, strokeWidth: 2}),
                            $(go.Shape,  // the arrowhead
                                {toArrow: "Standard", stroke: null})
                        ),
                    model: new go.GraphLinksModel([  // specify the contents of the Palette
                        {text: "Start", figure: "Circle", fill: "#00AD5F"},
                        {text: "Step"},
                        {text: "DB", figure: "Database", fill: "lightgray"},
                        {text: "???", figure: "Diamond", fill: "lightskyblue"},
                        {text: "End", figure: "Circle", fill: "#CE0620"},
                        {text: "Comment", figure: "RoundedRectangle", fill: "lightyellow"}
                    ], [
                        // the Palette also has a disconnected Link, which the user can drag-and-drop
                        {points: new go.List(go.Point).addAll([new go.Point(0, 0), new go.Point(30, 0), new go.Point(30, 40), new go.Point(60, 40)])}
                    ])
                });
        return palette;
    }

    function init(object) {
        setNodeTemplate(object);
        setLinkTemplate(object);
    }

    function nodeStyle() {
        return [
            new go.Binding("location", "loc", go.Point.parse).makeTwoWay(go.Point.stringify),
            {
                locationSpot: go.Spot.Center
            }
        ];
    }

    function makePort(name, spot, output, input) {
        // the port is basically just a small transparent square
        return $(go.Shape, "Circle",
            {
                fill: null,  // not seen, by default; set to a translucent gray by showSmallPorts, defined below
                stroke: null,
                desiredSize: new go.Size(7, 7),
                alignment: spot,  // align the port on the main Shape
                alignmentFocus: spot,  // just inside the Shape
                portId: name,  // declare this object to be a "port"
                fromSpot: spot, toSpot: spot,  // declare where links may connect at this port
                fromLinkable: output, toLinkable: input,  // declare whether the user may draw links to/from here
                cursor: "pointer"  // show a different cursor to indicate potential link point
            });
    }

    function textStyle() {
        return {
            font: "bold 11pt Lato, Helvetica, Arial, sans-serif",
            stroke: "#F8F8F8"
        }
    }

    function setNodeTemplate(diagram) {  // the default category

        let nodeSelectionAdornmentTemplate =
            $(go.Adornment, "Auto",
                $(go.Shape, {fill: null, stroke: "deepskyblue", strokeWidth: 1.5, strokeDashArray: [4, 2]}),
                $(go.Placeholder)
            );
        let nodeResizeAdornmentTemplate =
            $(go.Adornment, "Spot",
                {locationSpot: go.Spot.Right},
                $(go.Placeholder),
                $(go.Shape, {
                    alignment: go.Spot.TopLeft,
                    cursor: "nw-resize",
                    desiredSize: new go.Size(6, 6),
                    fill: "lightblue",
                    stroke: "deepskyblue"
                }),
                $(go.Shape, {
                    alignment: go.Spot.Top,
                    cursor: "n-resize",
                    desiredSize: new go.Size(6, 6),
                    fill: "lightblue",
                    stroke: "deepskyblue"
                }),
                $(go.Shape, {
                    alignment: go.Spot.TopRight,
                    cursor: "ne-resize",
                    desiredSize: new go.Size(6, 6),
                    fill: "lightblue",
                    stroke: "deepskyblue"
                }),

                $(go.Shape, {
                    alignment: go.Spot.Left,
                    cursor: "w-resize",
                    desiredSize: new go.Size(6, 6),
                    fill: "lightblue",
                    stroke: "deepskyblue"
                }),
                $(go.Shape, {
                    alignment: go.Spot.Right,
                    cursor: "e-resize",
                    desiredSize: new go.Size(6, 6),
                    fill: "lightblue",
                    stroke: "deepskyblue"
                }),

                $(go.Shape, {
                    alignment: go.Spot.BottomLeft,
                    cursor: "se-resize",
                    desiredSize: new go.Size(6, 6),
                    fill: "lightblue",
                    stroke: "deepskyblue"
                }),
                $(go.Shape, {
                    alignment: go.Spot.Bottom,
                    cursor: "s-resize",
                    desiredSize: new go.Size(6, 6),
                    fill: "lightblue",
                    stroke: "deepskyblue"
                }),
                $(go.Shape, {
                    alignment: go.Spot.BottomRight,
                    cursor: "sw-resize",
                    desiredSize: new go.Size(6, 6),
                    fill: "lightblue",
                    stroke: "deepskyblue"
                })
            );
        let nodeRotateAdornmentTemplate =
            $(go.Adornment,
                {locationSpot: go.Spot.Center, locationObjectName: "CIRCLE"},
                $(go.Shape, "Circle", {
                    name: "CIRCLE",
                    cursor: "pointer",
                    desiredSize: new go.Size(7, 7),
                    fill: "lightblue",
                    stroke: "deepskyblue"
                }),
                $(go.Shape, {
                    geometryString: "M3.5 7 L3.5 30",
                    isGeometryPositioned: true,
                    stroke: "deepskyblue",
                    strokeWidth: 1.5,
                    strokeDashArray: [4, 2]
                })
            );
        diagram.nodeTemplate =
            $(go.Node, "Spot",
                {locationSpot: go.Spot.Center},
                new go.Binding("location", "loc", go.Point.parse).makeTwoWay(go.Point.stringify),
                {selectable: true, selectionAdornmentTemplate: nodeSelectionAdornmentTemplate},
                {resizable: true, resizeObjectName: "PANEL", resizeAdornmentTemplate: nodeResizeAdornmentTemplate},
                {rotatable: true, rotateAdornmentTemplate: nodeRotateAdornmentTemplate},
                new go.Binding("angle").makeTwoWay(),
                // the main object is a Panel that surrounds a TextBlock with a Shape
                $(go.Panel, "Auto",
                    {name: "PANEL"},
                    new go.Binding("desiredSize", "size", go.Size.parse).makeTwoWay(go.Size.stringify),
                    $(go.Shape, "Rectangle",  // default figure
                        {
                            portId: "", // the default port: if no spot on link data, use closest side
                            fromLinkable: true, toLinkable: true, cursor: "pointer",
                            fill: "white",  // default color
                            strokeWidth: 2
                        },
                        new go.Binding("figure"),
                        new go.Binding("fill")),
                    $(go.TextBlock,
                        {
                            font: "bold 11pt Helvetica, Arial, sans-serif",
                            margin: 8,
                            maxSize: new go.Size(160, NaN),
                            wrap: go.TextBlock.WrapFit,
                            editable: true
                        },
                        new go.Binding("text").makeTwoWay())
                ),
                // four small named ports, one on each side:
                makePort("T", go.Spot.Top, false, true),
                makePort("L", go.Spot.Left, true, true),
                makePort("R", go.Spot.Right, true, true),
                makePort("B", go.Spot.Bottom, true, false),
                { // handle mouse enter/leave events to show/hide the ports
                    mouseEnter: function (e, node) {
                        showSmallPorts(node, true);
                    },
                    mouseLeave: function (e, node) {
                        showSmallPorts(node, false);
                    }
                }
            );
        // ######## 20200623 Modified
        let showSmallPorts = function (node, show) {
            node.ports.each(function (port) {
                if (port.portId !== "") {  // don't change the default port, which is the big shape
                    port.fill = show ? "rgba(0,0,0,.3)" : null;
                }
            });
        }
    }

    // replace the default Link template in the linkTemplateMap
    function setLinkTemplate(diagram) {

        let linkSelectionAdornmentTemplate =
            $(go.Adornment, "Link",
                $(go.Shape,
                    // isPanelMain declares that this Shape shares the Link.geometry
                    {isPanelMain: true, fill: null, stroke: "deepskyblue", strokeWidth: 0})  // use selection object's strokeWidth
            );

        diagram.linkTemplate = $(go.Link,  // the whole link panel
            {selectable: true, selectionAdornmentTemplate: linkSelectionAdornmentTemplate},
            {relinkableFrom: true, relinkableTo: true, reshapable: true},
            {
                routing: go.Link.AvoidsNodes,
                curve: go.Link.JumpOver,
                corner: 5,
                toShortLength: 4
            },
            new go.Binding("points").makeTwoWay(),
            $(go.Shape,  // the link path shape
                {isPanelMain: true, strokeWidth: 2}),
            $(go.Shape,  // the arrowhead
                {toArrow: "Standard", stroke: null}),
            $(go.Panel, "Auto",
                new go.Binding("visible", "isSelected").ofObject(),
                $(go.Shape, "RoundedRectangle",  // the link shape
                    {fill: "#F8F8F8", stroke: null}),
                $(go.TextBlock,
                    {
                        textAlign: "center",
                        font: "10pt helvetica, arial, sans-serif",
                        stroke: "#919191",
                        margin: 2,
                        minSize: new go.Size(10, NaN),
                        editable: true
                    },
                    new go.Binding("text").makeTwoWay())
            )
        );
    }

    go.Diagram.inherit(TopRotatingTool, go.RotatingTool);
    DIAGRAM = getDiagram();
    PALETTE = getPalette(DIAGRAM);

    function initDiagram() {
        let json = '{ "class": "go.GraphLinksModel","linkFromPortIdProperty": "fromPort","linkToPortIdProperty": "toPort", "nodeDataArray": [],"linkDataArray": []}';
        DIAGRAM.model = go.Model.fromJson(json);
        let pos = DIAGRAM.model.modelData.position;
        if (pos) DIAGRAM.initialPosition = go.Point.parse(pos);
        return DIAGRAM;
    }

    function initPalette() {
        return PALETTE;
    }

    function handleModelChange(obj) {
        const insertedNodeKeys = obj.insertedNodeKeys;
        const modifiedNodeData = obj.modifiedNodeData;
        const removedNodeKeys = obj.removedNodeKeys;
        const insertedLinkKeys = obj.insertedLinkKeys;
        const modifiedLinkData = obj.modifiedLinkData;
        const removedLinkKeys = obj.removedLinkKeys;
        const modifiedModelData = obj.modelData;
        console.log(obj);
    }


    function TopRotatingTool() {
        go.RotatingTool.call(this);
    }

    return (
        <div className = "topology-box">
          <ReactDiagram
            className = "topology-palette"
            initDiagram = {initPalette}
            divClassName = 'topology-palette'
            onModelChange = {handleModelChange}>
          </ReactDiagram>
          <ReactDiagram className = "topology-panel"
            initDiagram = {initDiagram}
            divClassName = 'topology-panel'
            onModelChange = {handleModelChange}>
          </ReactDiagram>
        </div>
    );
} 

