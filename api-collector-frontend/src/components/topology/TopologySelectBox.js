import React, {Component} from 'react';

export default class TopologySelectBox extends Component {
    state = {
      topologies: []
    };
  
    componentDidMount() {
        fetch(global.config.ajax.backend.common.url + "getTopologyList")
          .then((response) => {
            return response.json();
          })
          .then(data => {
            let listFromApi = data.map(topology => {
              return {value: topology.id, display: topology.name}
            });
            this.setState({
                topologies: [{value: '', display: '(Select topology)'}].concat(listFromApi)
            });
          }).catch(error => {
            console.log(error);
          });
    }
  
    render() {
      return (
        <div>
          <select>
          {this.state.topologies.map((topology) => <option key={topology.value} value={topology.value}>{topology.display}</option>)}
          </select>
        </div>
      );
    }
  }