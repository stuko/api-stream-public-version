import React, {Component} from 'react';
import go from 'gojs';
const goObj = go.GraphObject.make;

export default class GoJs extends Component {

    constructor (props) {
      super (props);
      this.renderCanvas = this.renderCanvas.bind (this);
      this.state = {myModel: null, myDiagram: null}
    }

    renderCanvas () {
      let model = goObj (go.TreeModel);
      let diagram = goObj (go.Diagram, this.refs.goJsDiv, {initialContentAlignment: go.Spot.Center});
      this.setState({myModel: model, myDiagram: diagram},
        () => {
          model.nodeDataArray = this.props.data;
          diagram.model = model;
          this.setState({myModel: model, myDiagram: diagram});
        }
      );
    }

    componentDidMount () {
      this.renderCanvas ();
    }

    componentWillUpdate (prevProps) {
      if (this.props.data !== prevProps.data) {
        console.log ('Updating');
        const model = this.state.myModel;
        const diagram = this.state.myDiagram;
        model.nodeDataArray = this.props.data;
        diagram.model = model;
        this.setState({myModel: model, myDiagram: diagram});
      }
    }

    render () {
      return <div ref="goJsDiv" style={{'width': '500px', 'height': '500px', 'backgroundColor': '#DAE4E4'}}></div>;
    }
}

