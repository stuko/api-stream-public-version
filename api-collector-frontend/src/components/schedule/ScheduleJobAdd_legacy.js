import React, {useState} from 'react'
import { post } from 'axios';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';
import moment from 'moment'
import Snackbar from '@material-ui/core/Snackbar';
import MuiAlert from '@material-ui/lab/Alert';

const styles = theme => ({
    hidden: {
        display: 'none'
    }
});

function Alert(props) {
    return <MuiAlert elevation={6} variant="filled" {...props} />;
}

class ScheduleJobAdd extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            jobName: '',
            jobScheduleTime: '',
            cronExpression: '',
            topicName: '',
            open: false,
            diogOkOpen: true,
            diogErrOpen: false
        }
        this.handleFormSubmit = this.handleFormSubmit.bind(this)
        this.handleValueChange = this.handleValueChange.bind(this)
        this.addScheduleJob = this.addScheduleJob.bind(this)
        this.handleClickOpen = this.handleClickOpen.bind(this)
        this.handleClose = this.handleClose.bind(this);
    }

    handleFormSubmit(e) {
        e.preventDefault()
        this.addScheduleJob()
            .then((response) => {
                console.log(response.data);
                if(response.data.responseCode !== 200){
                    this.setState({diogErrOpen:true})
                }else {
                    this.setState({diogOkOpen:true})
                    this.props.loadScheduleJob()
                }
                
            })
            .catch((error)=>{
                console.error(error)
                this.setState({diogErrOpen:true})
            })
        this.setState({
            jobName: '',
            jobScheduleTime: '',
            cronExpression: '',
            topicName: '',
            open: false
        })

    }
    handleValueChange(e) {
        let nextState = {};
        nextState[e.target.name] = e.target.value;
        this.setState(nextState);
    }
    addScheduleJob() {
        const url = global.config.ajax.backend.scheduler.url+'/schedule';
        const request = {
            'jobName': this.state.jobName,
            'scheduleTime': this.state.scheduleTime,
            'cronExpression': this.state.cronExpression,
            'topicName': this.state.topicName
            }
        const config = {
            headers: {
                'content-type': 'application/json'
            }
        }
        return post(url, request, config)
    }
    handleClickOpen() {
        this.setState({
            scheduleTime: moment().format("YYYY/MM/DD HH:mm:ss"),
            open: true
        });
    }
    handleClose() {
        this.setState({
            jobName: '',
            scheduleTime: '',
            cronExpression: '',
            topicName: '',
            open: false
        })
    }
    handleOkClose( event, reason ) {
        if(reason === 'clickaway') {
            return;
        }
        this.setState({
            diogOkOpen: false
        })
    }
    handleErrClose( event, reason ) {
        if(reason === 'clickaway') {
            return;
        }
        this.setState({
            diogErrOpen: false
        })
    }

    render() {
        const { classes } = this.props;
        return (
            <div>
                <Button variant="contained" color="primary" onClick={this.handleClickOpen}>
                    job 추가
                </Button>
                <Dialog open={this.state.open} onClose={this.handleClose}>

                    <DialogTitle>job 추가</DialogTitle>
                    <DialogContent>
                        <TextField label="jobName" type="text" name="jobName" value={this.state.jobName} onChange={this.handleValueChange} /><br />
                        <TextField label="scheduleTime" type="text" name="scheduleTime" value={this.state.scheduleTime} onChange={this.handleValueChange} /><br />
                        <TextField label="cronExpression" type="text" name="cronExpression" value={this.state.cronExpression} onChange={this.handleValueChange} /><br />
                        <TextField label="topicName" type="text" name="topicName" value={this.state.topicName} onChange={this.handleValueChange} /><br />
                    </DialogContent>
                    <DialogActions>
                        <Button variant="contained" color="primary" onClick={this.handleFormSubmit}>추가</Button>
                        <Button variant="outlined" color="primary" onClick={this.handleClose}>닫기</Button>
                    </DialogActions>
                </Dialog>
                <Snackbar open={this.diogOkOpen} autoHideDuration={6000} onClose={this.handleOkClose}>
                    <Alert onClose={this.handleOkClose} severity="success">
                    성공
                    </Alert>
                </Snackbar>
                <Snackbar open={this.diogErrOpen} autoHideDuration={6000} onClose={this.handleErrClose}>
                    <Alert onClose={this.handleErrClose} severity="error">
                    수정 실패
                    </Alert>
                </Snackbar>
            </div>
        )
    }
}

export default ScheduleJobAdd