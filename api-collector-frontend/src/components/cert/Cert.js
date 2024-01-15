import React,{Component} from 'react';
import '../../common.css';
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
import CertAdd from './CertAdd'
import CertUpdate from './CertUpdate'
import CertDelete from './CertDelete'
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
  cert_body: {
    color: "black",
    marginTop:40,
    marginLeft:20,
    marginRight:20
  }
});

const CertTooltip = withStyles((theme) => ({
  tooltip: {
    backgroundColor: '#f5f5f9',
    color: 'rgba(0, 0, 0, 0.87)',
    maxWidth: 250,
    fontSize: theme.typography.pxToRem(12),
    border: '2px solid #56574fc4',
  },
}))(Tooltip);

const mainHelper = `API 제공기관으로부터 발급받은 인증키를 등록하는 공간`;
const certIdHelper = `인증키 고유ID, API추가 시 선택`;

class Cert extends Component{

  state = {
    certList: []
  }

  loadCertList = async() => {
    axios.get(global.config.ajax.backend.common.url+'/readAllCert',{})
    .then((result) => {
      console.log(result)
      this.setState({
        isLoaded: true,
        certList: result.data
      })
    })
    .catch(e => {
      console.error(e)
      this.setState({
        isLoaded: false,
        certList: []
      })
    })
  }
  componentDidMount() {
    this.loadCertList()
    setInterval(this.loadCertList, 10000)
  }
  
  render(){
    const { classes } = this.props
    return(
        <div className={classes.cert_body}>
          <div className="title">
          <Typography component="h3" variant="h4" color="primary" className={classes.bold}>
          인증키 관리 <CertTooltip title={mainHelper} arrow><HelpIcon className="helper" fontSize="small"/></CertTooltip>
          </Typography>
          </div>
          <div align='right'>
          <CertAdd loadCertList={this.loadCertList}/>
          </div>
          <TableContainer className={classes.root} component={Paper}>
            <Table className={classes.table} aria-label="simple table">
              <TableHead className={classes.object}>
                <TableRow>
                  <TableCell className={classes.object} align="right">인증키 ID <CertTooltip title={certIdHelper} arrow><HelpIcon className="helper" fontSize="small"/></CertTooltip></TableCell>
                  <TableCell className={classes.object} align="right">사이트명&nbsp;</TableCell>
                  <TableCell className={classes.object} align="right">인증키&nbsp;</TableCell>
                  <TableCell className={classes.object} align="right">조회제한건수(일)&nbsp;</TableCell>
                  <TableCell className={classes.object} align="right">만료일</TableCell>
                  <TableCell className={classes.object} align="right">등록인ID</TableCell>
                  <TableCell className={classes.object} align="right">수정시간</TableCell>
                  <TableCell className={classes.object} align="right"></TableCell>
                  <TableCell className={classes.object} align="left"></TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {this.state.certList.map((cert) => (
                  <TableRow key={cert.certId}>
                    <TableCell align="right">{cert.certId}</TableCell>
                    <TableCell align="right">{cert.siteName}</TableCell>
                    <TableCell align="right">{cert.certKey}</TableCell>
                    <TableCell align="right">{cert.limit}</TableCell>
                    <TableCell align="right">{cert.expired}</TableCell>
                    <TableCell align="right">{cert.chgr_no}</TableCell>
                    <TableCell align="right">{cert.chg_dt_tm}</TableCell>
                    <TableCell align="right">
                      <CertUpdate cert={cert} loadCertList={this.loadCertList}/>
                    </TableCell>
                    <TableCell align="left">
                      {/* <CertDelete certId={cert.certId} loadCertList={this.loadCertList}/> */}
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
export default withStyles(styles)(Cert)