import React , {useState} from 'react'
import { post } from 'axios';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';
import Snackbar from '@material-ui/core/Snackbar';
import MuiAlert from '@material-ui/lab/Alert';
import moment from 'moment'
import HelpIcon from '@material-ui/icons/Help';
import Tooltip from '@material-ui/core/Tooltip';
import { withStyles } from '@material-ui/core/styles';

function Alert(props) {
return <MuiAlert elevation={6} variant="filled" {...props} />;
}

export default function ScheduleJobAdd (props) {

    const [open, enableOpen] = useState(false);
    const [jobName, setJobName] = useState();
    const [scheduleTime, setScheduleTime] = useState();
    const [cronExpression, setCronExpression] = useState();
    const [topicName, setTopicName] = useState();
    const [diogOkOpen, setDiogOkOpen] = useState(false);
    const [diogErrOpen, setDiogErrOpen] = useState(false);

    const topicNameHelper = `실행할 Topology의 start모듈의 consumerTopic항목 참고.`;

    const ScheduleAddTooltip = withStyles((theme) => ({
        tooltip: {
          backgroundColor: '#f5f5f9',
          color: 'rgba(0, 0, 0, 0.87)',
          maxWidth: 220,
          fontSize: theme.typography.pxToRem(12),
          border: '2px solid #56574fc4',
        },
      }))(Tooltip);

    const handleOkClose = (event, reason) => {
        if (reason === 'clickaway') {
        return;
        }
        setDiogOkOpen(false);
    };
    const handleErrClose = (event, reason) => {
        if (reason === 'clickaway') {
        return;
        }
        setDiogErrOpen(false);
    };
    const handleOpen = () => {
        setJobName('')
        setScheduleTime(moment().format("YYYY/MM/DD HH:mm:ss"))
        setCronExpression('')
        setTopicName('')
        enableOpen(true)
    }
    const handleClose = () => {
        setJobName('')
        setScheduleTime('')
        setCronExpression('')
        setTopicName('')
        enableOpen(false)
    }

    function addScheduleJob(e) {
        e.preventDefault()
        const url = global.config.ajax.backend.scheduler.url+'/schedule';
        const request = {
        'jobName': jobName,
        'scheduleTime': scheduleTime,
        'cronExpression': cronExpression,
        'topicName': topicName
        }
        const config = {
            headers: {
                'content-type': 'application/json'
            }
        }
        post(url, request, config)
            .then((response) => {
                console.log(response.data);
                if(response.data.responseCode !== 200) {
                    setDiogErrOpen(true)
                }else{
                    setDiogOkOpen(true)
                }                
                props.loadScheduleJob()
                enableOpen(false)
            })
            .catch(error=>{
                setDiogErrOpen(true)
                console.error(error)
            })
    }
    return (
    <div>
        <Button variant="contained" color="primary" onClick={handleOpen}>
            job 추가
        </Button>
        <Dialog open={open} onClose={handleClose}>
            <DialogTitle>스케줄정보 추가</DialogTitle>
            <DialogContent>
                <TextField label="스케줄 작업명" type="text" name="jobName" value={jobName} onChange={(e)=>{setJobName(e.target.value)}} /><br/>
                <TextField label="스케줄 시작시간" type="text" name="scheduleTime" value={scheduleTime} onChange={(e)=>{setScheduleTime(e.target.value)}} /><br/>
                <TextField label="cronExpression" type="text" name="cronExpression" value={cronExpression} onChange={(e)=>{setCronExpression(e.target.value)}} /><br/>
                <TextField label="topic명" type="text" name="topicName" value={topicName} onChange={(e)=>{setTopicName(e.target.value)}} /><br/>
                <ScheduleAddTooltip title={topicNameHelper} arrow><HelpIcon className="helper" fontSize="small"/></ScheduleAddTooltip><br />
            </DialogContent>
            <DialogActions>
                <Button variant="contained" color="primary" onClick={(e)=>{addScheduleJob(e)}}>추가</Button>
                <Button variant="outlined" color="primary" onClick={handleClose}>닫기</Button>
            </DialogActions>
        </Dialog>
        <Snackbar open={diogOkOpen} autoHideDuration={6000} onClose={handleOkClose}>
            <Alert onClose={handleOkClose} severity="success">
            성공
            </Alert>
        </Snackbar>
        <Snackbar open={diogErrOpen} autoHideDuration={6000} onClose={handleErrClose}>
            <Alert onClose={handleErrClose} severity="error">
            등록 실패
            </Alert>
        </Snackbar>
    </div>
    )
}