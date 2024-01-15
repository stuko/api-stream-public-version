import React from "react";
import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import DialogActions from "@material-ui/core/DialogActions";
import Typography from "@material-ui/core/Typography";
import { post } from 'axios';

class CertDelete extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            open: false,
            certId: props.certId
        };
        this.handleClickOpen = this.handleClickOpen.bind(this);
        this.handleClose = this.handleClose.bind(this);
        this.deleteCertJob = this.deleteCertJob.bind(this);
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
        this.deleteCertJob()
            .then((response) => {
                console.log(response.data)
                this.props.loadCertList()
            })
            .catch((error)=>{
                console.error(error)
            })
        this.setState({
            open: false
        })
    }
    deleteCertJob() {
        const url = global.config.ajax.backend.common.url+'/deleteCert';
        const request = {
            'certId': this.state.certId
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
                <Typography gutterBottom>선택한 인증키 정보가 삭제됩니다.</Typography>
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

export default CertDelete;
