import React from "react";
import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import DialogActions from "@material-ui/core/DialogActions";
import Typography from "@material-ui/core/Typography";
import { post } from 'axios';

class ScheduleJobDelete extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            open: false,
            jobName: props.jobName
        };
        this.handleClickOpen = this.handleClickOpen.bind(this);
        this.handleClose = this.handleClose.bind(this);
        this.deleteScheduleJob = this.deleteScheduleJob.bind(this);
    }
    handleClickOpen() {
        this.setState({
            open: true,
        });
    }
    handleClose() {
        this.setState({
            open: false,
        });
    }
    handleFormSubmit(e){
        e.preventDefault()
        this.deleteScheduleJob()
            .then((response) => {
                console.log(response.data)
                this.props.loadScheduleJob()
            })
            .catch((error)=>{
                console.error(error)
            })
        this.setState({
            open: false
        })
    }
    deleteScheduleJob() {
        const url = global.config.ajax.backend.scheduler.url+'/delete';
        const request = {
            'jobName': this.state.jobName
            }
        const config = {
            headers: {
                'content-type': 'application/json'
            }
        }
        return post(url, request, config)
    }
    render() {
        return (
        <div>
            <Button variant="contained" color="secondary" onClick={this.handleClickOpen}>
            삭제
            </Button>
            <Dialog onClose={this.handleClose} open={this.state.open}>
            <DialogTitle onClose={this.handleClose}>삭제 경고</DialogTitle>

            <DialogContent>
                <Typography gutterBottom>선택한 스케줄 정보가 삭제됩니다.</Typography>
            </DialogContent>

            <DialogActions>
                <Button variant="contained" color="primary" onClick={(e)=>{this.handleFormSubmit(e)}}>삭제</Button>
                <Button variant="outlined" color="primary" onClick={this.handleClose}>닫기</Button>
            </DialogActions>
            </Dialog>
        </div>
        );
    }
}

export default ScheduleJobDelete;
