import React,{Component} from 'react';
import '../../common.css';
import { makeStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';
import Typography from '@material-ui/core/Typography';
// make sure you include the timeline stylesheet or the timeline will not be styled
import { withStyles } from '@material-ui/core/styles';
import HelpIcon from '@material-ui/icons/Help';
import Tooltip from '@material-ui/core/Tooltip';
import ApiAdd from './ApiAdd'
import ApiUpdate from './ApiUpdate'
import ApiDelete from './ApiDelete'
import axios from 'axios';

const styles = theme => ({
  root:{
    marginTop:10
  },
  object:{
    color:'blue',
    fontSize:'1em'
  },
  bold:{
    fontWeight : 500,
    fontSize: '1.8em',
    color: 'blue'
  },
  hidden: {
      display: 'none'
  },
  api_body: {
    color: "black",
    marginTop:40,
    marginLeft:20,
    marginRight:20
  },
});

const ApiTooltip = withStyles((theme) => ({
  tooltip: {
    backgroundColor: '#f5f5f9',
    color: 'rgba(0, 0, 0, 0.87)',
    maxWidth: 250,
    fontSize: theme.typography.pxToRem(12),
    border: '2px solid #56574fc4',
  },
}))(Tooltip);

const mainHelper = `Topology에서 사용할 API를 등록하는 공간`;
const apiIdHelper = `API고유ID, Topology의 API모듈에 해당 ID로 등록`;

class Api extends Component{

  state = {
    apiList: []
  }

  loadApiList = async() => {
    axios.get(global.config.ajax.backend.common.url+'/readAllApi',{})
    .then((result) => {
      console.log(result)
      this.setState({
        isLoaded: true,
        apiList: result.data
      })
    })
    .catch(e => {
      console.error(e)
      this.setState({
        isLoaded: false,
        apiList: []
      })
    })
  }
  componentDidMount() {
    this.loadApiList()
    setInterval(this.loadApiList, 10000)
  }
  
  render(){
    const { classes } = this.props
    return(
        <div className={classes.api_body}>
          <div className={classes.title}>
          <Typography component="h3" variant="h4" color="primary" className={classes.bold}>
            API Management <ApiTooltip title={mainHelper} arrow><HelpIcon className="helper" fontSize="small"/></ApiTooltip>
          </Typography>
          </div>
          <div align='right'>
            <ApiAdd loadApiList={this.loadApiList}/>
          </div>
          <TableContainer className={classes.root} component={Paper}>
            <Table className={classes.table} aria-label="simple table">
              <TableHead className={classes.object} >
                <TableRow>
                  <TableCell className={classes.object} align="right">API ID <ApiTooltip title={apiIdHelper} arrow><HelpIcon className="helper" fontSize="small"/></ApiTooltip></TableCell>
                  <TableCell className={classes.object}  align="right">API명&nbsp;</TableCell>
                  <TableCell className={classes.object}  align="right">URL&nbsp;</TableCell>
                  <TableCell className={classes.object}  align="right">출력양식&nbsp;</TableCell>
                  <TableCell className={classes.object}  align="right">인증키</TableCell>
                  <TableCell className={classes.object}  align="right">등록ID</TableCell>
                  <TableCell className={classes.object}  align="right">수정시간</TableCell>
                  <TableCell className={classes.object}  align="right"></TableCell>
                  <TableCell className={classes.object}  align="left"></TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {this.state.apiList.map((api) => (
                  <TableRow key={api.apiId}>
                    <TableCell align="right">{api.apiId}</TableCell>
                    <TableCell align="right">{api.apiName}</TableCell>
                    <TableCell align="right">{api.url}</TableCell>
                    <TableCell align="right">{api.output_format}</TableCell>
                    <TableCell align="right">{api.cert[0].certId}</TableCell>
                    <TableCell align="right">{api.chgr_no}</TableCell>
                    <TableCell align="right">{api.chg_dt_tm}</TableCell>
                    <TableCell align="right">
                      <ApiUpdate api={api} loadApiList={this.loadApiList}/>
                    </TableCell>
                    <TableCell align="left">
                      <ApiDelete apiId={api.apiId} loadApiList={this.loadApiList}/>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </div>
    );
  }
}
export default withStyles(styles)(Api)