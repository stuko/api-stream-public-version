import React , {useState} from 'react'
import axios from 'axios';
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

export default function CertUpdate (props) {

    const [open, enableOpen] = useState(false);
    const [certId, setCertId] = useState(props.cert.certId);
    const [siteName, setSiteName] = useState(props.cert.siteName);
    const [certKey, setCertKey] = useState(props.cert.certKey);
    const [limit, setLimit] = useState(props.cert.limit);
    const [expired, setExpired] = useState(props.cert.expired);
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

    const handleOpen = () => {
        enableOpen(true)
    }

    function updateCert(e) {
        e.preventDefault()
        const requestUrl = global.config.ajax.backend.common.url+'/updateCert';
        const request = {
        'certId': certId,
        'siteName': siteName,
        'certKey': certKey,
        'limit': limit,
        'expired': expired
        }
        const config = {
            headers: {
                'content-type': 'application/json'
            }
        }
        axios.post(requestUrl, request, config)
            .then((response) => {
                console.log(response.data);
                if(response.status !== 200) {
                    setDiogErrOpen(true)
                }else{
                    setDiogOkOpen(true)
                }                
                props.loadCertList()
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
            수정
        </Button>
        <Dialog open={open} onClose={()=>{enableOpen(false)}}>
            <DialogTitle>인증키정보 수정</DialogTitle>
            <DialogContent>
                <TextField label="certId" type="text" name="certId" value={certId} onChange={(e)=>{setCertId(e.target.value)}} disabled/><br />
                <TextField label="사이트명" type="text" name="siteName" value={siteName} onChange={(e)=>{setSiteName(e.target.value)}} /><br />
                <TextField label="인증키" type="text" name="certKey" value={certKey} onChange={(e)=>{setCertKey(e.target.value)}} /><br />
                <TextField label="조회제한건수(일)" type="text" name="limit" value={limit} onChange={(e)=>{setLimit(e.target.value)}} /><br />
                <TextField label="만료일" type="text" name="expired" value={expired} onChange={(e)=>{setExpired(e.target.value)}}/><br />
            </DialogContent>
            <DialogActions>
                <Button variant="contained" color="primary" onClick={(e)=>{updateCert(e)}}>수정</Button>
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

