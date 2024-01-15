import React, {Component} from 'react';
import logo from '../../logo.svg';
import axios from "axios"; 
import ListPage from './ListPage';
import CardTile from './CardTile';
import Grid from '@material-ui/core/Grid';
import { makeStyles } from '@material-ui/core/styles';


export default class Home extends Component{

  constructor(props){
    super(props);
  }
  
  state={
    loading:false,
    list:[]
  }

  loadItem = async () => {
    axios
    .get("/data/SearchJson.json")
    .then(({ data }) => {
      this.setState({ 
        loading: true,
        list: data.Item
      });
    })
    .catch(e => {  // API 호출이 실패한 경우
      console.error(e);  // 에러표시
      this.setState({  
        loading: false
      });
    });
  };
  
  componentDidMount() {
    this.loadItem();  // loadItem 호출
  }

  render (){
    const { list } = this.state;
    return(
       <div style={{ padding: 50 }}>
       <Grid 
        direction="row"
        justify="center"
        alignItems="center"
        container spacing={4}>
        <Grid item lg={3} sm={6} xl={3} xs={12}><CardTile style={{flexDirection: 'column',textAlign: 'center',alignItems : 'center',marginBottom : 10,}}/></Grid>
        <Grid item lg={3} sm={6} xl={3} xs={12}><CardTile style={{flexDirection: 'column',textAlign: 'center',alignItems : 'center',marginBottom : 10,}}/></Grid>
        <Grid item lg={3} sm={6} xl={3} xs={12}><CardTile style={{flexDirection: 'column',textAlign: 'center',alignItems : 'center',marginBottom : 10,}}/></Grid>
        <Grid item lg={3} sm={6} xl={3} xs={12}><CardTile style={{flexDirection: 'column',textAlign: 'center',alignItems : 'center',marginBottom : 10,}}/></Grid>
       </Grid>
       <ListPage card={list} cols="12" style={{flexDirection: 'column',textAlign: 'center',alignItems : 'center',marginBottom : 10,}}/>
       </div>
    );
  }
}