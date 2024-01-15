import React, {Component} from 'react';
import axios from "axios";
import SearchBar from 'material-ui-search-bar';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider'
import Grid from '@material-ui/core/Grid';
import {makeStyles} from '@material-ui/core/styles';
import AddCircleOutlineIcon from '@material-ui/icons/AddCircleOutline';
import IconButton from '@material-ui/core/IconButton';
import Button from '@material-ui/core/Button';
import Fade from '@material-ui/core/Fade';
import '../home/ListPage.css';
import TrafficLight from 'react-trafficlight';
import Paper from '@material-ui/core/Paper';
import {Modal,Form,Button as BButton} from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.css';
import TopologySelectBox from '../topology/TopologySelectBox';

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
    flexDirection: 'column',      
    textAlign: 'center',
    alignItems : 'center',  
  },
  paper: {
    padding: theme.spacing(1),
    // color: theme.palette.text.secondary,
    flexDirection: 'column',
    width:'100%',
    textAlign: 'center',
    alignItems : 'center',
    color: '#000000',
  },
  column: {
    marginBottom:5,
    textAlign: 'center',
    alignItems : 'center',

  },
  img: {
    marginBottom:5,
    width : 400,  
    textAlign: 'center',
    alignItems : 'center',
    color: '#000000',
    background:'#FFFFFF',

  },
  title: {
    marginBottom:5,  
    width: 200,
    textAlign: 'center',
    alignItems : 'center',

  },
  subTitle: {
      marginBottom:5,    
      width: 200,
      textAlign: 'center',
      alignItems : 'center',

  },
  subDesc: {
      marginBottom:5,   
      width: 200,
      textAlign: 'center',
      alignItems : 'center',

  },
  button: {
      marginBottom:5,   
      width: 200,
      textAlign: 'center',
      alignItems : 'center',

  }
}));

function BatchModalInput(props) {
  
  function registerBatch(){

  }

  return (
    <Modal
      {...props}
      size="lg"
      aria-labelledby="contained-modal-title-vcenter"
      centered
    >
      <Modal.Header closeButton>
        <Modal.Title id="contained-modal-title-vcenter">
          배치정보등록
        </Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Form>
          <Form.Group controlId="form.BAT_NM">
            <Form.Label>배치 이름</Form.Label>
            <Form.Control type="text" placeholder="배치 이름을 입력해 주세요"/>
            <Form.Text className="text-muted">
              입력된 배치이름은 추후 스케줄 관리에서 사용이 됩니다.
            </Form.Text>
          </Form.Group>
          <Form.Group controlId="form.ST_TPLG_ID">
            <Form.Label>시작 토폴로지 아이디</Form.Label>
            <TopologySelectBox />
          </Form.Group>
          <Form.Group controlId="form.PRCS_EXPLN">
            <Form.Label>배치 프로세스 설명</Form.Label>
            <Form.Control type="text" placeholder="배치 이름을 입력해 주세요" />
            <Form.Text className="text-muted">
              배치 프로세스에 대한 설명을 적어 주세요.
            </Form.Text>
          </Form.Group>
        </Form>
      </Modal.Body>
      <Modal.Footer>
        <BButton variant="primary" onClick={registerBatch}>등록</BButton>
        <BButton variant="secondary" onClick={props.onHide}>닫기</BButton>
      </Modal.Footer>
    </Modal>
  );
}


export default class Batch extends Component {

  state = {
    open: false,
    loading: false,
    transition: Fade,
    modalShow: false,
    list: []
  }

  state={
    open:false,
    loading:false,
    transition: Fade,
    showBatchModalInput : false,
    list:[]
  }

  loadItem = async () => {
    axios
      .get("/data/getBatchList.json")
      .then(({data}) => {
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

  clickPlus=()=>{
    console.log("click plus....");
    this.setState({ 
      showBatchModalInput : true
    });
  }
  
  closeModal = () => {
    // setModalVisible(false)
  }

  handleClose = () => {
    this.setOpen(true);
  };

  setModalShow = (st) => {
    this.setState({
      modalShow: st
    });
  }

  render(){
    const { list , modalShow} = this.state;
    return(
      <MuiThemeProvider>
        <div>
          <div className="title">
            <h1>배치 정보 관리</h1>
          </div>
          <div style={{padding:10,}}/>
          <Grid 
            direction="row"
            justify="center"
            alignItems="center"
            container spacing={4}>
            <Grid item lg={3} sm={6} xl={3} xs={12}>
              <SearchBar
                onChange={() => console.log('onChange')}
                onRequestSearch={() => console.log('onRequestSearch')}
                style={{
                  margin: '0 auto',
                  maxWidth: 1000
                }}
              />
            </Grid> 
            <Grid item>
              <IconButton onClick={() => this.setModalShow(true)}><AddCircleOutlineIcon /></IconButton>
            </Grid>
          </Grid>
          <ListPage card={list} cols="1" style={{flexDirection: 'column',textAlign: 'center',alignItems : 'center',marginBottom : 10,}}/>

          <BatchModalInput
            show={modalShow}
            onHide={() => this.setModalShow(false)}
          />

        </div>
      </MuiThemeProvider>
    );
  }
}

class ListPage extends Component {
    id = 1;
    state = {
        card: [],
        cols: 1
    }

    render() {
        const {card, cols} = this.props;
        console.log(card);
        return (
            <div className = "gridList">
              {card && card.map((rowdata, rowIndex) => {
                return ( <Grid key = {rowIndex} className = "gridDiv" >
                          <Grid container item xs = {parseInt(cols)} spacing = {3} >
                            {rowdata && rowdata.map((itemdata, colIndex) => {
                              return (
                                <TrafficCard
                                  key = {colIndex}
                                  id = {itemdata.id}
                                  Align = "Horizontal"
                                  ImageURL = {itemdata.ImageURL}
                                  Title = {itemdata.Title}
                                  SubTitle = {itemdata.SubTitle}
                                  SubDesc = {itemdata.SubDesc}
                                  FirstBtn = {itemdata.FirstBtn}
                                  SecondBtn = {itemdata.SecondBtn}
                                  Value = {itemdata.Value}
                                />
                              );
                            })
                            };
                          </Grid>
                        </Grid>
                      );
              })
              }
            </div>
        );
    }
}


function TrafficCard({ id, Align, ImageURL, Title, SubTitle,SubDesc,FirstBtn,SecondBtn, Value }) {

  const classes = useStyles();
  let showBatchModalInput = false;

  function viewDetail(){

  }

  function modify(){

  }

  let style,row,paperWidth;

  if(Align === 'Vertical'){
     style = { padding: 50 ,
              flexDirection: 'column',
              textAlign: 'center',
              alignItems : 'center',
              marginBottom : 10,};
     row=3;
     paperWidth = {width:400};
  }else{
      style = { padding: 50 ,
                 flexDirection: 'row',
                 textAlign: 'right',
                 alignItems : 'left',
                 marginBottom : 10,
              }
      row=1;
      paperWidth = {width:'85vw'};
  }

  let chart,traffic;
  let color = Value;
  let yellow = <TrafficLight YellowOn />;
  let red = <TrafficLight RedOn />;
  let green = <TrafficLight GreenOn />;
  let normal = <TrafficLight />;
  if(color === 'R') traffic = red;
  else if(color === 'Y') traffic = yellow;
  else if(color === 'G') traffic = green;
  else traffic = normal;

  if(true){
    if(Align === 'Vertical') chart = 
          <div className={classes.img}>
            {traffic}
          </div>;
    else chart = 
          <div className="float-right" style={{flexDirection: 'row', justifyContent: 'flex-end'}}>
            {traffic}
          </div>;            
  }else{
    chart = null;
  }

  let title,subTitle;
  if (true) {
    if(Align === 'Vertical') title = <div style={{flexDirection: 'column',textAlign: 'center',alignItems : 'center',marginBottom : 10,}}>{Title}</div>;
    else title = <div className="float-right" style={{flexDirection: 'row', justifyContent: 'flex-end'}}>{Title}</div>;
  }else{
    title = null;
  }

  if (true) {
    if(Align === 'Vertical') subTitle = <div style={{flexDirection: 'column',textAlign: 'center',alignItems : 'center',marginBottom : 10,}}>{SubTitle}<p>{SubDesc}</p></div>;
    else subTitle = <div className="float-right" style={{flexDirection: 'row', justifyContent: 'flex-end'}}>{SubTitle}<p>{SubDesc}</p></div>;
  }else{
    subTitle = null;
  }

  let firstButton,secondButton;

  if (true) {
    if(Align === 'Vertical') firstButton = <div style={{flexDirection: 'column',textAlign: 'center',alignItems : 'center',marginBottom : 10,}}> 
                   <Button
                    type="button"
                    variant="contained"
                    color="primary"
                    className={classes.submit}
                    onClick={viewDetail}
                    >{FirstBtn}</Button>
                  </div>;
    else firstButton = <div style={{flexDirection: 'column',textAlign: 'center',alignItems : 'center',marginBottom : 10,}}><Button
                        type="button"
                        variant="contained"
                        color="primary"
                        className={classes.submit + " float-right"}
                        onClick={viewDetail}
                        >{FirstBtn}</Button>
                      </div>;                  
  } else {
    firstButton = null;
  }

  if (true) {
    if(Align === 'Vertical') secondButton = <div style={{flexDirection: 'column',textAlign: 'center',alignItems : 'center',marginBottom : 10,}}>
                    <Button 
                      type="button"
                      variant="contained"
                      color="primary"
                      className={classes.submit}
                      onClick={modify}
                      >{SecondBtn}</Button>
                   </div>;
    else secondButton = <div style={{flexDirection: 'column',textAlign: 'center',alignItems : 'center',marginBottom : 10,}}><Button 
                        type="button"
                        variant="contained"
                        color="primary"
                        className={classes.submit + " float-right"}
                        onClick={modify}
                        >{SecondBtn}</Button></div>;                   
  } else {
    secondButton = null;
  }

  return (
    <div style={style}>
        <Grid key={id} item xs={12} className={classes.root}>
        <Paper className={classes.paper} style={paperWidth}>
          <Grid container spacing={row}>
          <Grid item xs>
           {chart}
          </Grid>
          <Grid item xs>
           {title}
          </Grid> 
          <Grid item xs>
           {subTitle}
          </Grid>
          <Grid item xs>
           {firstButton}
           {secondButton}
          </Grid>   
          </Grid>                  
        </Paper>
        </Grid>
        <BatchModalInput visible={showBatchModalInput}>Hello Modify</BatchModalInput>
    </div>
  );
}
