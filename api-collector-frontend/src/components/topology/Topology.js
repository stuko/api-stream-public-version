import React from 'react';
import { ReactDiagram } from 'gojs-react';
import GoJsTopologyDiagram from './GoJsTopologyDiagram';
import * as go from 'gojs';
import './Topology.css';

export default function Topology(){
  return (
    <div>
      <GoJsTopologyDiagram/>
    </div>
  );
}
