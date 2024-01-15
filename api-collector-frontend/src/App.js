import React from 'react';
import './App.css';
import RoutePath from './components/router/RoutePath';
import {BrowserRouter} from 'react-router-dom';

function App(){

  return (
        <div className="App">
          <BrowserRouter>
          <RoutePath/>
          </BrowserRouter>
        </div>
  );
}

export default App;
