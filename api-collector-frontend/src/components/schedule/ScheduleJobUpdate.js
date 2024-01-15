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

function Alert(props) {
return <MuiAlert elevation={6} variant="filled" {...props} />;
}

export default function ScheduleJobUpdate (props) {

    const [open, enableOpen] = useState(false);
    const [jobName, setJobName] = useState(props.scheduleJob.jobName);
    const [scheduleTime, setScheduleTime] = useState(props.scheduleJob.scheduleTime);
    const [cronExpression, setCronExpression] = useState(props.scheduleJob.cronExpression);
    const [topicName, setTopicName] = useState(props.scheduleJob.topicName);

    const [diogOkOpen, setDiogOkOpen] = useState(false);
    const [diogErrOpen, setDiogErrOpen] = useState(false);
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

    function updateScheduleJob(e) {
        e.preventDefault()
        const url = global.config.ajax.backend.scheduler.url+'/update';
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
        <Button variant="contained" color="primary" onClick={()=>{enableOpen(true)}}>
            수정
        </Button>
        <Dialog open={open} onClose={()=>{enableOpen(false)}}>
            <DialogTitle>스케줄정보 수정</DialogTitle>
            <DialogContent>
                <TextField label="jobName" type="text" name="jobName" value={jobName} onChange={(e)=>{setJobName(e.target.value)}} disabled/><br />
                <TextField label="scheduleTime" type="text" name="scheduleTime" value={scheduleTime} onChange={(e)=>{setScheduleTime(e.target.value)}} /><br />
                <TextField label="cronExpression" type="text" name="cronExpression" value={cronExpression} onChange={(e)=>{setCronExpression(e.target.value)}} /><br />
                <TextField label="topicName" type="text" name="topicName" value={topicName} onChange={(e)=>{setTopicName(e.target.value)}} /><br />
            </DialogContent>
            <DialogActions>
                <Button variant="contained" color="primary" onClick={(e)=>{updateScheduleJob(e)}}>수정</Button>
                <Button variant="outlined" color="primary" onClick={()=>{enableOpen(false)}}>닫기</Button>
            </DialogActions>
        </Dialog>
        <Snackbar open={diogOkOpen} autoHideDuration={6000} onClose={handleOkClose}>
            <Alert onClose={handleOkClose} severity="success">
            성공
            </Alert>
        </Snackbar>
        <Snackbar open={diogErrOpen} autoHideDuration={6000} onClose={handleErrClose}>
            <Alert onClose={handleErrClose} severity="error">
            수정 실패
            </Alert>
        </Snackbar>
    </div>
    )
}

