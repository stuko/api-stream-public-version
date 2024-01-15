import React from "react";
import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import DialogActions from "@material-ui/core/DialogActions";
import Typography from "@material-ui/core/Typography";
import { post } from 'axios';

class ApiDelete extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            open: false,
            apiId: props.apiId
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
                this.props.loadApiList()
            })
            .catch((error)=>{
                console.error(error)
            })
        this.setState({
            open: false
        })
    }
    deleteScheduleJob() {
        const url = global.config.ajax.backend.common.url+'/deleteApi';
        const request = {
            'apiId': this.state.apiId
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
                <Typography gutterBottom>선택한 API 정보가 삭제됩니다.</Typography>
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

export default ApiDelete;
