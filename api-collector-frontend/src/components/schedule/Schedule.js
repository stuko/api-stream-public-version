import React,{Component} from 'react';
import './Schedule.css';
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
import 'react-calendar-timeline/lib/Timeline.css'
import HelpIcon from '@material-ui/icons/Help';
import Tooltip from '@material-ui/core/Tooltip';
import { withStyles } from '@material-ui/core/styles';
import ScheduleJobAdd from './ScheduleJobAdd'
import ScheduleJobUpdate from './ScheduleJobUpdate'
import ScheduleJobDelete from './ScheduleJobDelete'
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
  schedule_body: {
    color: "black",
    marginTop:40,
    marginLeft:20,
    marginRight:20
  }
});

const ScheduleTooltip = withStyles((theme) => ({
  tooltip: {
    backgroundColor: '#f5f5f9',
    color: 'rgba(0, 0, 0, 0.87)',
    maxWidth: 250,
    fontSize: theme.typography.pxToRem(12),
    border: '2px solid #56574fc4',
  },
}))(Tooltip);

const mainHelper = `Topoloy에서 생성한 작업을 스케줄에 등록`;

class Schedule extends Component{

  state = {
    scheduleJobs: []
  }

  loadScheduleJob = async() => {
    axios.post(global.config.ajax.backend.scheduler.url+'/jobs',{})
    .then((result) => {
      console.log(result)
      this.setState({
        isLoaded: true,
        scheduleJobs: result.data.data
      })
    })
    .catch(e => {
      console.error(e)
      this.setState({
        isLoaded: false,
        scheduleJobs: []
      })
    })
  }
  componentDidMount() {
    this.loadScheduleJob()
    setInterval(this.loadScheduleJob, 10000)
  }
  
  render(){
    const { classes } = this.props
    return(
        <div className={classes.schedule_body}>
          <div className="title">
          <Typography component="h3" variant="h4" color="primary" className={classes.bold}>
          Scheduler <ScheduleTooltip title={mainHelper} arrow><HelpIcon className="helper" fontSize="small"/></ScheduleTooltip>
          </Typography>
          </div>
          <div align="right">
          <ScheduleJobAdd  className="ne_button" loadScheduleJob={this.loadScheduleJob}/>
          </div>

          <TableContainer className={classes.root} component={Paper}>
            <Table className={classes.table} aria-label="simple table">
              <TableHead className={classes.object} >
                <TableRow  className="ne_thead">
                  <TableCell className={classes.object}  align="right">배치작업명</TableCell>
                  <TableCell className={classes.object}  align="right">스케줄등록시간&nbsp;</TableCell>
                  <TableCell className={classes.object}  align="right">발행Topic명&nbsp;</TableCell>
                  <TableCell className={classes.object}  align="right">스케줄 정보&nbsp;</TableCell>
                  <TableCell className={classes.object}  align="right">작업상태</TableCell>
                  <TableCell className={classes.object}  align="right">마지막수행시간</TableCell>
                  <TableCell className={classes.object}  align="right">다음수행시간</TableCell>
                  <TableCell className={classes.object}  align="right"></TableCell>
                  <TableCell className={classes.object}  align="left"></TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {this.state.scheduleJobs.map((scheduleJob) => (
                  <TableRow key={scheduleJob.jobName}>
                    <TableCell align="right">{scheduleJob.jobName}</TableCell>
                    <TableCell align="right">{scheduleJob.scheduleTime}</TableCell>
                    <TableCell align="right">{scheduleJob.topicName}</TableCell>
                    <TableCell align="right">{scheduleJob.cronExpression}</TableCell>
                    <TableCell align="right">{scheduleJob.jobStatus}</TableCell>
                    <TableCell align="right">{scheduleJob.lastFiredTime}</TableCell>
                    <TableCell align="right">{scheduleJob.nextFireTime}</TableCell>
                    <TableCell align="right">
                      <ScheduleJobUpdate scheduleJob={scheduleJob} loadScheduleJob={this.loadScheduleJob}/>
                    </TableCell>
                    <TableCell align="left">
                      <ScheduleJobDelete jobName={scheduleJob.jobName} loadScheduleJob={this.loadScheduleJob}/>
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
export default withStyles(styles)(Schedule)